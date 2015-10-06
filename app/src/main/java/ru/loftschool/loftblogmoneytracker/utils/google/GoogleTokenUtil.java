package ru.loftschool.loftblogmoneytracker.utils.google;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.ui.activities.LoginActivity;
import ru.loftschool.loftblogmoneytracker.ui.activities.LoginActivity_;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity_;
import ru.loftschool.loftblogmoneytracker.utils.NetworkConnectionChecker;
import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;

@EBean
public class GoogleTokenUtil {

    @StringRes(R.string.error_no_internet)
    String errorNoInternet;

    private static final String TAG = GoogleTokenUtil.class.getSimpleName();

    private final static String G_PLUS_SCOPE    = "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USERINFO_SCOPE  = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE     = "https://www.googleapis.com/auth/userinfo.email";
    public final static String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;


    @Background
    public void getGoogleToken(Intent data, final AppCompatActivity activity, final Context context, final int reqCode) {
        final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String googleToken = null;

        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            try {
                googleToken = GoogleAuthUtil.getToken(context, accountName, SCOPES);
            } catch (final UserRecoverableAuthException authEx) {
                Log.d(TAG, "UserRecoverableAuthException");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //restart getting token
                        activity.startActivityForResult(authEx.getIntent(), reqCode);
                    }
                });
            } catch (IOException e) {
                Log.d(TAG, "Google auth: IOException");
            } catch (GoogleAuthException e) {
                Log.d(TAG, "Google auth: Fatal Authorization Exception " + e.getLocalizedMessage());
            }

            MoneyTrackerApplication.setGoogleToken(context, googleToken);
            String googleSharedToken = MoneyTrackerApplication.getGoogleToken(context);
            Log.d(TAG, "Google token: " + googleSharedToken);

            if (!TokenKeyStorage.DEFAULT_TOKEN_GOOGLE_KEY.equalsIgnoreCase(googleSharedToken)) {
                Intent intent = new Intent(context, MainActivity_.class);
                activity.startActivity(intent);
                activity.finish();
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, errorNoInternet, Toast.LENGTH_SHORT).show();
                }
            });
            if (!(activity instanceof LoginActivity)) {
                Intent intent = new Intent(context, LoginActivity_.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }
}
