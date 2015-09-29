package ru.loftschool.loftblogmoneytracker.utils;

import android.widget.EditText;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.StringRes;

import ru.loftschool.loftblogmoneytracker.R;

@EBean
public class TextInputValidator {

    @StringRes(R.string.error_null_reg_name)
    String nullNameError;

    @StringRes(R.string.error_null_reg_password)
    String nullPasswordError;

    @StringRes(R.string.error_login_no_such_name)
    String noSuchNameError;

    @StringRes(R.string.error_login_wrong_password)
    String wrongPasswordError;

    @StringRes(R.string.error_min_length)
    String minLengthError;

    @IntegerRes(R.integer.min_field_password_length)
    Integer minPasswordLength;

    @IntegerRes(R.integer.min_field_username_length)
    Integer minNameLength;


    public boolean validateLoginForm(EditText login, EditText password) {
        boolean isValid = true;

        if (login.getText().toString().trim().length() == 0) {
            login.setError(nullNameError);
            isValid = false;
        }
        if (password.getText().toString().trim().length() == 0) {
            password.setError(nullPasswordError);
            isValid = false;
        }

        return isValid;
    }

    public boolean validateRegisterForm(EditText login, EditText password) {
        boolean isValid = true;

        if (login.getText().toString().trim().length() == 0) {
            login.setError(nullNameError);
            isValid = false;
        } else if (login.getText().toString().trim().length() < minNameLength) {
            login.setError(minLengthError + minNameLength);
            isValid = false;
        }
        if (password.getText().toString().trim().length() == 0) {
            password.setError(nullPasswordError);
            isValid = false;
        } else if (password.getText().toString().trim().length() < minPasswordLength) {
            password.setError(minLengthError + minPasswordLength);
            isValid = false;
        }

        return isValid;
    }
}
