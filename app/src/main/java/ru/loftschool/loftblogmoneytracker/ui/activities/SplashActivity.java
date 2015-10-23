package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.StringRes;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestClient;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;
import ru.loftschool.loftblogmoneytracker.rest.status.GoogleAccountDataStatus;
import ru.loftschool.loftblogmoneytracker.utils.NetworkConnectionChecker;
import ru.loftschool.loftblogmoneytracker.utils.google.GoogleTokenUtil;
import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends AppCompatActivity implements TokenKeyStorage {

    private static final int SPLASH_DISPLAY_LENGTH = 1500;
    private static final int REQ_CODE = 20;
    private static final String TAG = SplashActivity.class.getSimpleName();

    private RestClient restClient;
    private String googleToken;

    @StringRes(R.string.error_no_internet)
    String errorNoInternet;

    @Bean
    GoogleTokenUtil googleTokenUtil;

    @AfterViews
    void ready() {
        restClient = new RestClient();
        googleToken = MoneyTrackerApplication.getGoogleToken(this);
        Log.e(TAG, googleToken != null ? googleToken : "no google token");

        //if we don't have google token -> we'll check for our server token
        if(DEFAULT_TOKEN_GOOGLE_KEY.equalsIgnoreCase(googleToken)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    //if we also don't have server token -> go to LoginScreen, else go to MainActivity
                    if (DEFAULT_TOKEN_KEY.equals(MoneyTrackerApplication.getToken(getApplicationContext()))) {
                        intent = new Intent(SplashActivity.this, LoginActivity_.class);
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity_.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            //check if token is valid or not
            if (NetworkConnectionChecker.isNetworkConnected(this)) {
                checkTokenValid();
            } else {
                Toast.makeText(SplashActivity.this, errorNoInternet, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, LoginActivity_.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Background
    void checkTokenValid() {
        restClient.getGoogleAccountDataGetAPI().tokenStatus(googleToken, new Callback<GoogleAccountDataModel>() {
            @Override
            public void success(GoogleAccountDataModel googleAccountDataModel, Response response) {
                Log.e(TAG, "STATUS: " + googleAccountDataModel.getStatus());
                //if token is valid -> go to MainActivity
                if (GoogleAccountDataStatus.STATUS_OK.equalsIgnoreCase(googleAccountDataModel.getStatus())) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity_.class);
                    startActivity(intent);
                    finish();
                } else {
                //if token is not valid -> get a new google token
                    doubleTokenCheck();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                doubleTokenCheck();
            }
        });
    }

    private void doubleTokenCheck() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            googleTokenUtil.getGoogleToken(data, SplashActivity.this, this, REQ_CODE);
        }
    }
}
