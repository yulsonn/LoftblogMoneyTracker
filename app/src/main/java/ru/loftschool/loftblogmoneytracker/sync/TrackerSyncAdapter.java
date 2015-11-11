package ru.loftschool.loftblogmoneytracker.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.rest.RestClient;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;
import ru.loftschool.loftblogmoneytracker.rest.status.GoogleAccountDataStatus;
import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.SyncTypes;
import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;
import ru.loftschool.loftblogmoneytracker.utils.google.GoogleScopes;

public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter implements TokenKeyStorage, GoogleScopes, SyncTypes {

    private static final String TAG = TrackerSyncAdapter.class.getSimpleName();
    private String googleToken;

    public TrackerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        googleToken = MoneyTrackerApplication.getGoogleToken(getContext());
        if(DEFAULT_TOKEN_GOOGLE_KEY.equalsIgnoreCase(googleToken)) {
            if (DEFAULT_TOKEN_KEY.equals(MoneyTrackerApplication.getToken(getContext()))) {
                Log.e(TAG, "Wrong token. Sync failed.");
            } else {
                new ServerReqUtils(getContext()).synchronize(SYNC_AUTOMATIC);
            }
        } else {
            checkTokenValid();
        }
    }

    void checkTokenValid() {
        RestClient restClient = new RestClient();
        restClient.getGoogleAccountDataAPI().tokenStatus(googleToken, new Callback<GoogleAccountDataModel>() {
            @Override
            public void success(GoogleAccountDataModel googleAccountDataModel, Response response) {
                Log.e(TAG, "Google token status: " + googleAccountDataModel.getStatus());
                if (GoogleAccountDataStatus.STATUS_OK.equalsIgnoreCase(googleAccountDataModel.getStatus())) {
                    new ServerReqUtils(getContext()).synchronize(SYNC_AUTOMATIC);
                } else {
                    Log.e(TAG, "Google token is not valid. Sync failed.");
                    new GetGoogleToken().execute();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Google token is not valid. Sync failed.");
            }
        });
    }

    void doubleCheck() {
        Log.e(TAG, "new Google token: " + MoneyTrackerApplication.getGoogleToken(getContext()));

        if (!TokenKeyStorage.DEFAULT_TOKEN_GOOGLE_KEY.equalsIgnoreCase(MoneyTrackerApplication.getGoogleToken(getContext()))) {
            new ServerReqUtils(getContext()).synchronize(SYNC_AUTOMATIC);
        } else {
            Log.e(TAG, "Google token is not valid. Sync failed.");
        }
    }

    public static void syncImmediately (Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);

    }

    // check if account already created on the device, if no - it will be created -> onAccountCreated()
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    // this method response for creating an account
    private static void onAccountCreated(Account newAccount, Context context) {
        final int SYNC_INTERVAL = 60 * 60 * 24;     //sync once per day
        final int SYNC_FLEXTIME = SYNC_INTERVAL/3;  // if usual sync failed try again in a day/3

        TrackerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        ContentResolver.addPeriodicSync(newAccount, context.getString(R.string.content_authority), Bundle.EMPTY, SYNC_INTERVAL);
        syncImmediately(context);
    }

    // configure periodical synchronization
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        // since KITKAT version uses new sync realization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private class GetGoogleToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            AccountManager accountManager = AccountManager.get(getContext());
            Account[] accounts = accountManager.getAccountsByType("com.google");
            String accountName = accounts[0].name;

            try {
                googleToken = GoogleAuthUtil.getToken(getContext(), accountName, SCOPES);
            } catch (final UserRecoverableAuthException authEx) {
                Log.d(TAG, "UserRecoverableAuthException");
                doubleCheck();
            } catch (IOException e) {
                Log.d(TAG, "Google auth: IOException");
            } catch (GoogleAuthException e) {
                Log.d(TAG, "Google auth: Fatal Authorization Exception " + e.getLocalizedMessage());
            }

            MoneyTrackerApplication.setGoogleToken(getContext(), googleToken);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            doubleCheck();
        }
    }
}
