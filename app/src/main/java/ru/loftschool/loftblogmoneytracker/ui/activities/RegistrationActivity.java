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
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.lang.ref.WeakReference;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;
import ru.loftschool.loftblogmoneytracker.rest.status.UserRegisterModelStatus;

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

    @StringRes(R.string.reg_hint_user)
    String hintUser;

    @StringRes(R.string.reg_hint_password)
    String hintPassword;

    @StringRes(R.string.error_null_reg_name)
    String nullNameError;

    @StringRes(R.string.error_null_reg_password)
    String nullPasswordError;

    @StringRes(R.string.error_exists_reg_name)
    String existsNameError;

    @StringRes(R.string.error_unknown)
    String unknownError;

    @StringRes(R.string.reg_success)
    String successMessage;

    @AfterViews
    void ready() {
        usernameWrapper.setHint(hintUser);
        passwordWrapper.setHint(hintPassword);
    }

    @Click
    void btnRegister() {
        hideKeyboard();
        if (inputValidation()) {
            registration();
        }
    }

    @Background
    void registration() {
        RestService restService = new RestService();
        UserRegisterModel response = restService.register(etUser.getText().toString(), etPassword.getText().toString());

        if (UserRegisterModelStatus.STATUS_OK.equals(response.getStatus())) {
            completeRegistration();
        } else if (UserRegisterModelStatus.STATUS_ERROR.equals(response.getStatus())) {
            showErrorRegistrationMessage(true);
        } else {
            showErrorRegistrationMessage(false);
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
    protected void completeRegistration() {
        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity_.class);
        Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_SHORT).show();
        startActivity(mainIntent);
        finish();
    }

    @UiThread
    protected void showErrorRegistrationMessage(boolean flag) {
        if (flag) {
            Message msg = new Message();
            msg.obj = existsNameError;
            handler.sendMessage(msg);
        } else {
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
}
