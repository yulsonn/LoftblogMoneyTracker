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

import com.google.android.gms.common.AccountPicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.lang.ref.WeakReference;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLoginModel;
import ru.loftschool.loftblogmoneytracker.rest.status.UserStatus;
import ru.loftschool.loftblogmoneytracker.utils.SignInMessages;
import ru.loftschool.loftblogmoneytracker.utils.TextInputValidator;
import ru.loftschool.loftblogmoneytracker.utils.google.GoogleTokenUtil;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

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

    @StringRes(R.string.error_no_internet)
    String noInternetError;

    @Bean
    TextInputValidator validator;

    @Bean
    SignInMessages message;

    @Bean
    GoogleTokenUtil googleTokenUtil;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQ_CODE = 10;

    private final static String G_PLUS_SCOPE =
            "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USERINFO_SCOPE =
            "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
    public final static String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;

    @AfterViews
    void ready() {
        usernameWrapper.setHint(hintUser);
        passwordWrapper.setHint(hintPassword);

        Intent intent = getIntent();
        if (intent != null) {
            etUser.setText(intent.getStringExtra("etUser"));
            etPassword.setText(intent.getStringExtra("etPassword"));
        }
    }

    @Click
    void linkRegistration() {
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity_.class);
        startActivity(intent);
        finish();
    }

    @Click
    void btnLogin() {
        hideKeyboard();
        if (validator.validateLoginForm(etUser, etPassword)) {
            if (NetworkConnectionChecker.isNetworkConnected(this)) {
                login();
            } else {
                Toast.makeText(getApplicationContext(), noInternetError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Click(R.id.btn_google_login)
    void btnGoogleLogin() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, REQ_CODE);
    }

    @Background
    void login() {
        RestService restService = new RestService();
        UserLoginModel response = restService.login(etUser.getText().toString(), etPassword.getText().toString());
        // save auth_token to SharedPreferences
        MoneyTrackerApplication.setToken(this, response.getAuthToken());
        String status = response.getStatus();

        if (UserStatus.STATUS_OK.equals(status)) {
            completeLogin();
        } else {
            message.showErrorLoginMessage(status, this.getCurrentFocus(), handler);
        }
    }

    @UiThread
    protected void completeLogin() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity_.class);
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
        private final WeakReference<LoginActivity> mActivity;

        public WeakRefHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == SignInMessages.MESSAGE_USER) {
                    activity.etUser.requestFocus();
                    activity.etUser.setError((String) msg.obj);
                } else {
                    activity.etPassword.requestFocus();
                    activity.etPassword.setError((String) msg.obj);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            googleTokenUtil.getGoogleToken(data, LoginActivity.this, this, REQ_CODE);
        }
    }
}
