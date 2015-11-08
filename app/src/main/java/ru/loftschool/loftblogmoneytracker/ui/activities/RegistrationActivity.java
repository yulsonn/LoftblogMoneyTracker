package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.lang.ref.WeakReference;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;
import ru.loftschool.loftblogmoneytracker.rest.status.UserStatus;
import ru.loftschool.loftblogmoneytracker.utils.SignInMessages;
import ru.loftschool.loftblogmoneytracker.utils.TextInputValidator;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EActivity(R.layout.activity_registration)
public class RegistrationActivity extends AppCompatActivity {

    // to get fields in the main UI thread from background thread use Handler
    private final WeakRefHandler handler = new WeakRefHandler(this);

    @ViewById(R.id.btn_register)
    Button btnRegister;

    @ViewById(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;

    @ViewById(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;

    @ViewById(R.id.et_reg_user)
    EditText etUser;

    @ViewById(R.id.et_reg_password)
    EditText etPassword;

    @ViewById(R.id.link_login)
    TextView linkLogin;

    @StringRes(R.string.reg_hint_user)
    String hintUser;

    @StringRes(R.string.reg_hint_password)
    String hintPassword;

    @StringRes(R.string.error_no_internet)
    String noInternetError;

    @StringRes(R.string.reg_success)
    String successMessage;

    @Bean
    TextInputValidator validator;

    @Bean
    SignInMessages message;

    @AfterViews
    void ready() {
        usernameWrapper.setHint(hintUser);
        passwordWrapper.setHint(hintPassword);
    }

    @Click
    void linkLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity_.class);
        startActivity(intent);
        finish();
    }

    @Click
    void btnRegister() {
        hideKeyboard();
        if (validator.validateRegisterForm(etUser, etPassword)) {
            if (NetworkConnectionChecker.isNetworkConnected(this)) {
                registration();
            } else {
                Toast.makeText(getApplicationContext(), noInternetError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Background
    void registration() {
        RestService restService = new RestService();
        UserRegisterModel response = restService.register(etUser.getText().toString(), etPassword.getText().toString());

        if (UserStatus.STATUS_OK.equals(response.getStatus())) {
            completeRegistration();
        } else if (UserStatus.STATUS_ERROR_LOGIN_EXISTS.equals(response.getStatus())) {
            message.showErrorRegistrationMessage(true, getCurrentFocus(), handler);
        } else {
            message.showErrorRegistrationMessage(false, getCurrentFocus(), handler);
        }
    }

    @UiThread
    protected void completeRegistration() {
        Intent mainIntent = new Intent(RegistrationActivity.this, LoginActivity_.class);
        mainIntent.putExtra("etUser", etUser.getText().toString());
        mainIntent.putExtra("etPassword", etPassword.getText().toString());
        Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_LONG).show();
        startActivity(mainIntent);
        finish();
    }

    // Android doesn't hide the virtual keyboard automatically if focus on some editText field
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //to avoid "leak might occur" warning while using handler create custom static class WeakRefHandler
    private static class WeakRefHandler extends Handler {
        private final WeakReference<RegistrationActivity> mActivity;

        public WeakRefHandler(RegistrationActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RegistrationActivity activity = mActivity.get();
            if (activity != null) {
                activity.etUser.requestFocus();
                activity.etUser.setError((String) msg.obj);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
