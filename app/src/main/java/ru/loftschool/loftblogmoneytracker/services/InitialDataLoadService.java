package ru.loftschool.loftblogmoneytracker.services;

import android.app.IntentService;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;

import java.util.ArrayList;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.ExpenseDetails;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity;
import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateFormats;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EIntentService
public class InitialDataLoadService extends IntentService implements DateFormats {

    @Bean
    ServerReqUtils serverRequest;

    RestService restService = new RestService();

    public InitialDataLoadService() {
        super(InitialDataLoadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadData();
    }

    public void loadData() {
        startLoadData();

        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            ArrayList<CategoryWithExpensesModel> expensesResp = restService.getAllCategoriesWithExpenses(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if (!expensesResp.isEmpty()) {
                for (CategoryWithExpensesModel category : expensesResp) {
                    Categories newCategory = new Categories(category.getTitle(), Integer.parseInt(category.getId()));
                    newCategory.save();
                    if (!category.getTransactions().isEmpty()) {
                        for (ExpenseDetails expense : category.getTransactions()) {
                            Expenses newExpense = new Expenses(expense.getComment(), Float.parseFloat(expense.getSum()),
                                    DateConvertUtils.stringToDate(expense.getTrDate(), INVERSE_DATE_FORMAT_LOAD), Integer.parseInt(expense.getId()), newCategory);
                            newExpense.save();
                        }
                    }
                }
            } else {
                String[] initCategories = getResources().getStringArray(R.array.initial_categories);
                for (int i = 0; i < initCategories.length; i++) {
                    Categories category = new Categories(initCategories[i]);
                    category.save();
                    serverRequest.addCategoryToServer(category);
                }
            }
        } else {
            serverRequest.noInternetReaction();
        }

        stopLoadData();
    }

    void startLoadData() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.LOAD_START_ACTION);
        sendBroadcast(broadcastIntent);
    }

    void stopLoadData() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.LOAD_STOP_ACTION);
        sendBroadcast(broadcastIntent);
    }
}
