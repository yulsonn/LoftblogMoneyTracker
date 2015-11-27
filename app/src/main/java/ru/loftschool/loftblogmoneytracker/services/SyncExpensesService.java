package ru.loftschool.loftblogmoneytracker.services;

import android.app.IntentService;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;

import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.SyncTypes;

@EIntentService
public class SyncExpensesService extends IntentService {

    @Bean
    ServerReqUtils serverRequest;

    public SyncExpensesService() {
        super(SyncExpensesService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        serverRequest.expensesSync(SyncTypes.SYNC_EXPENSES_LIST_UPDATE);
    }
}
