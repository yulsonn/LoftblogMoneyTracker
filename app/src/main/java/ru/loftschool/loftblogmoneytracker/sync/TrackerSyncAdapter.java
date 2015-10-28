package ru.loftschool.loftblogmoneytracker.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.query.Select;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;

public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = TrackerSyncAdapter.class.getSimpleName();
    RestService restService;

    public TrackerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() starts");
        restService = new RestService();
        categoriesSync();
        expensesSync();
    }

    private void categoriesSync() {
        Log.d(TAG, "categoriesSync() called with: " + "");
        List<Categories> categories = new Select().from(Categories.class).execute();
        if (!categories.isEmpty()) {
            for (Categories category : categories) {
                restService.categoriesSync(category.getId().intValue(),
                        category.name,
                        MoneyTrackerApplication.getGoogleToken(getContext()),
                        MoneyTrackerApplication.getToken(getContext()),
                        new Callback<CategoryDetails>() {
                            @Override
                            public void success(CategoryDetails categoryDetails, Response response) {
                                Log.e(TAG, "OK. Category sync success");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(TAG, "ERROR. Category sync failed");
                            }
                        });
            }
        }
    }

    private void expensesSync() {
        Log.d(TAG, "expensesSync() called with: " + "");
        List<Expenses> expenses = new Select().from(Expenses.class).execute();
        if (!expenses.isEmpty()){
            for(Expenses expense : expenses)
                restService.expensesSync(expense.getId().intValue(),
                        expense.name,
                        expense.price.toString(),
                        expense.date,
                        MoneyTrackerApplication.getGoogleToken(getContext()),
                        MoneyTrackerApplication.getToken(getContext()),
                        new Callback<CategoryDetails>() {
                            @Override
                            public void success(CategoryDetails categoryDetails, Response response) {
                                Log.e(TAG, "OK. Expenses sync success");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(TAG, "ERROR. Expenses sync failed");
                            }
                        });
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
}
