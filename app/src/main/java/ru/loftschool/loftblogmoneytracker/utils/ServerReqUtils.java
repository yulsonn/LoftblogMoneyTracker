package ru.loftschool.loftblogmoneytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoriesStatus;
import ru.loftschool.loftblogmoneytracker.rest.status.ExpensesStatus;
import ru.loftschool.loftblogmoneytracker.ui.activities.LoginActivity_;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateFormats;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EBean
public class ServerReqUtils implements DateFormats{

    private static String LOG_TAG = ServerReqUtils.class.getSimpleName();
    private RestService restService = new RestService();
    private Context context;
    private String unknownError;
    private String unauthorizedError;
    private String noInternetError;

    public ServerReqUtils(Context context) {
        this.context = context;
        unknownError = context.getResources().getString(R.string.error_unknown);
        unauthorizedError = context.getResources().getString(R.string.error_unauthorized);
        noInternetError = context.getResources().getString(R.string.error_no_internet);
    }

    @Background
    public void addCategoryToServer(Categories category) {
        if (category != null) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                try {
                    CategoryModel categoryAddResp = restService.addCategory(category.name, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                    if (CategoriesStatus.STATUS_OK.equals(categoryAddResp.getStatus())) {
                        Log.e(LOG_TAG, "Category name: " + categoryAddResp.getData().getTitle() +
                                ", Category id: " + categoryAddResp.getData().getId());
                        category.sId = categoryAddResp.getData().getId();
                        category.save();
                        Log.e(LOG_TAG, "id: " + category.getId() + " server id: " + category.sId);
                    } else {
                        unknownErrorReaction();
                        Log.e(LOG_TAG, unknownError);
                    }
                } catch (UnauthorizedException e) {
                    unauthorizedErrorReaction();
                }
            } else {
                noInternetReaction();
                Log.e(LOG_TAG, noInternetError);
            }
        }
    }

    @Background
    public void addExpenseToServer(Expenses expense) {
        if (expense != null) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                AllExpensesModel addExpenseResp = restService.addExpense(String.valueOf(expense.price), expense.name,
                        expense.category.sId, DateConvertUtils.dateToString(expense.date, INVERSE_DATE_FORMAT),
                        MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                if (ExpensesStatus.STATUS_OK.equals(addExpenseResp.getStatus())) {
                    Log.e(LOG_TAG, "Expense id: " + addExpenseResp.getId());
                    expense.sId = addExpenseResp.getId();
                    expense.save();
                    Log.e(LOG_TAG, "New expense sId: " + expense.sId);
                } else {
                    unknownErrorReaction();
                    Log.e(LOG_TAG, unknownError);
                }
            } else {
                noInternetReaction();
                Log.e(LOG_TAG, noInternetError);
            }
        }
    }

    @UiThread
    void unauthorizedErrorReaction() {
        Toast.makeText(context, unauthorizedError, Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, CategoriesStatus.STATUS_WRONG_TOKEN);
        goToLogin();
        ((Activity)context).finish();
    }

    @UiThread
    void unknownErrorReaction() {
        Toast.makeText(context, unknownError, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void noInternetReaction() {
        Toast.makeText(context, noInternetError, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void goToLogin() {
        Intent intent = new Intent(context, LoginActivity_.class);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
