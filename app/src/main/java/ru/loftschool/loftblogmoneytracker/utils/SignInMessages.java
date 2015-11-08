package ru.loftschool.loftblogmoneytracker.utils;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.status.UserStatus;

@EBean
public class SignInMessages {

    public static final int MESSAGE_USER = 0;
    public static final int MESSAGE_PASSWORD = 1;

    @StringRes(R.string.error_login_no_such_name)
    String noSuchNameError;

    @StringRes(R.string.error_login_wrong_password)
    String wrongPasswordError;

    @StringRes(R.string.error_exists_reg_name)
    String existsNameError;

    @StringRes(R.string.error_unknown)
    String unknownError;

    public void showErrorLoginMessage(String status, View view, Handler handler) {
        Message msg = new Message();

        switch (status) {
            case UserStatus.STATUS_WRONG_LOGIN:
                msg.obj = noSuchNameError;
                msg.what = MESSAGE_USER;
                handler.sendMessage(msg);
                break;
            case UserStatus.STATUS_WRONG_PASSWORD:
                msg.obj = wrongPasswordError;
                msg.what = MESSAGE_PASSWORD;
                handler.sendMessage(msg);
                break;
            default:
                Snackbar.make(view, unknownError,Snackbar.LENGTH_LONG).show();
        }
    }

    public void showErrorRegistrationMessage(boolean flag, View view, Handler handler) {
        if (flag) {
            Message msg = new Message();
            msg.obj = existsNameError;
            handler.sendMessage(msg);
        } else {
            Snackbar.make(view, unknownError, Snackbar.LENGTH_LONG).show();

        }
    }
}
