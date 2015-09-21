package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;

public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_DISPLAY_LENGTH = 1500;
    private final static String DEFAULT_TOKEN_KEY = "1";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (DEFAULT_TOKEN_KEY.equals(MoneyTrackerApplication.getToken(getApplicationContext()))) {
                    intent = new Intent(SplashActivity.this, LoginActivity_.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity_.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
