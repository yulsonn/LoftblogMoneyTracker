package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.lang.ref.WeakReference;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryAddModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLoginModel;
import ru.loftschool.loftblogmoneytracker.rest.status.UserLoginModelStatus;
import ru.loftschool.loftblogmoneytracker.utils.NetworkConnectionChecker;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    // to get fields in the main UI thread from background thread use Handler
    private final WeakRefHandler handler = new WeakRefHandler(this);

    @ViewById(R.id.btn_login)
    Button btnLogin;

    @ViewById(R.id.login_usernameWrapper)
    TextInputLayout usernameWrapper;

    @ViewById(R.id.login_passwordWrapper)
    TextInputLayout passwordWrapper;

    @ViewById(R.id.et_log_user)
    EditText etUser;

    @ViewById(R.id.et_log_password)
    EditText etPassword;

    @ViewById(R.id.link_registration)
    TextView linkRegistration;

    @StringRes(R.string.reg_hint_user)
    String hintUser;

    @StringRes(R.string.reg_hint_password)
    String hintPassword;

    @StringRes(R.string.error_null_reg_name)
    String nullNameError;

    @StringRes(R.string.error_null_reg_password)
    String nullPasswordError;

    @StringRes(R.string.error_login_no_such_name)
    String noSuchNameError;

    @StringRes(R.string.error_login_wrong_password)
    String wrongPasswordError;

    @StringRes(R.string.error_unknown)
    String unknownError;

    @StringRes(R.string.error_no_internet)
    String noInternetError;


    @AfterViews
    void ready() {
        usernameWrapper.setHint(hintUser);
        passwordWrapper.setHint(hintPassword);

        linkRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity_.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            etUser.setText(intent.getStringExtra("etUser"));
            etPassword.setText(intent.getStringExtra("etPassword"));
        }
    }


    @Click
    void btnLogin() {
        hideKeyboard();
        if (inputValidation()) {
            if (NetworkConnectionChecker.isNetworkConnected(this)) {
                login();
            } else {
                Toast.makeText(getApplicationContext(), noInternetError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Background
    void login() {
        RestService restService = new RestService();
        UserLoginModel response = restService.login(etUser.getText().toString(), etPassword.getText().toString());
        MoneyTrackerApplication.setToken(this, response.getAuthToken());
        String status = response.getStatus();

//        CategoryAddModel category = restService.addCategory("Clothes", response.getAuthToken());
//
//        Log.e("LoginActivity", "Category name: " + category.getData().getTitle() +
//                "Category id: " + category.getData().getId());

        if (UserLoginModelStatus.STATUS_OK.equals(status)) {
            completeLogin();
        } else {
            showErrorLoginMessage(status);
        }
    }

    private boolean inputValidation() {

        boolean isValid = true;

        if (etUser.getText().toString().trim().length() == 0) {
            etUser.setError(nullNameError);
            isValid = false;
        }
        if (etPassword.getText().toString().trim().length() == 0) {
            etPassword.setError(nullPasswordError);
            isValid = false;
        }

        return isValid;
    }

    @UiThread
    protected void completeLogin() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity_.class);
        startActivity(mainIntent);
        finish();
    }

    @UiThread
    protected void showErrorLoginMessage(String status) {
        Message msg = new Message();

        switch (status) {
            case UserLoginModelStatus.STATUS_WRONG_LOGIN:
                msg.obj = noSuchNameError;
                msg.what = 0;
                handler.sendMessage(msg);
                break;
            case UserLoginModelStatus.STATUS_WRONG_PASSWORD:
                msg.obj = wrongPasswordError;
                msg.what = 1;
                handler.sendMessage(msg);
                break;
            default:
                Toast.makeText(getApplicationContext(), unknownError, Toast.LENGTH_SHORT).show();
        }
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
        private final WeakReference<LoginActivity> mActivity;

        public WeakRefHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 0) {
                    activity.etUser.requestFocus();
                    activity.etUser.setError((String) msg.obj);
                } else {
                    activity.etPassword.requestFocus();
                    activity.etPassword.setError((String) msg.obj);
                }
            }
        }
    }
}
