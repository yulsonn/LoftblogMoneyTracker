package ru.loftschool.loftblogmoneytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.CategorySyncObject;
import ru.loftschool.loftblogmoneytracker.rest.ExpensesSyncObject;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.SyncWrapper;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDeleteModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLogoutModel;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoriesStatus;
import ru.loftschool.loftblogmoneytracker.rest.status.ExpensesStatus;
import ru.loftschool.loftblogmoneytracker.rest.status.UserStatus;
import ru.loftschool.loftblogmoneytracker.ui.activities.LoginActivity_;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateFormats;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EBean
public class ServerReqUtils implements DateFormats, SyncTypes{

    private static String LOG_TAG = ServerReqUtils.class.getSimpleName();
    private RestService restService = new RestService();
    private Context context;
    private String unknownError;
    private String unauthorizedError;
    private String noInternetError;
    private String syncCategoriesOk;
    private String syncCategoriesFailed;
    private String syncExpensesOk;
    private String syncExpensesFailed;
    private String errorTroubleConnect;
    private String errorTroubleServer;

    public ServerReqUtils(Context context) {
        this.context = context;
        unknownError = context.getResources().getString(R.string.error_unknown);
        unauthorizedError = context.getResources().getString(R.string.error_unauthorized);
        noInternetError = context.getResources().getString(R.string.error_no_internet);
        syncCategoriesOk = context.getResources().getString(R.string.sync_categories_ok);
        syncCategoriesFailed = context.getResources().getString(R.string.sync_categories_failed);
        syncExpensesOk = context.getResources().getString(R.string.sync_expenses_ok);
        syncExpensesFailed = context.getResources().getString(R.string.sync_expenses_failed);
        errorTroubleConnect = context.getResources().getString(R.string.error_troubles_connection);
        errorTroubleServer = context.getResources().getString(R.string.error_troubles_server);
    }

    @Background
    public void addCategoryToServer(Categories category) {
        if (category != null) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                try {
                    CategoryModel categoryAddResp = restService.addCategory(category.name, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                    String status = categoryAddResp.getStatus();
                    if (CategoriesStatus.STATUS_OK.equals(status)) {
                        category.sId = categoryAddResp.getData().getId();
                        category.save();
                        Log.e(LOG_TAG, "Category name: " + category.name + ", id: " + category.getId() + ", server id: " + category.sId);
                    } else if (CategoriesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || CategoriesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                        unauthorizedErrorReaction();
                    } else {
                        unknownErrorReaction();
                        Log.e(LOG_TAG, unknownError);
                    }
                } catch (RetrofitError e) {
                    retrofitErrorMessageShow(e.getKind(), e);
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
                try {
                    AllExpensesModel addExpenseResp = restService.addExpense(String.valueOf(expense.price), expense.name,
                            expense.category.sId, DateConvertUtils.dateToString(expense.date, INVERSE_DATE_FORMAT),
                            MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                    String status = addExpenseResp.getStatus();
                    if (ExpensesStatus.STATUS_OK.equals(status)) {
                        expense.sId = addExpenseResp.getId();
                        expense.save();
                        Log.e(LOG_TAG, "Expense sId: " + expense.sId);
                    } else if (ExpensesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || ExpensesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                        unauthorizedErrorReaction();
                    } else {
                        unknownErrorReaction();
                        Log.e(LOG_TAG, unknownError);
                    }
                } catch (RetrofitError e) {
                    retrofitErrorMessageShow(e.getKind(), e);
                }
            } else {
                noInternetReaction();
                Log.e(LOG_TAG, noInternetError);
            }
        }
    }

    @Background
    public void editCategoryOnServer(Categories category) {
        if (category != null) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                try {
                    CategoryModel categoryEditResp
                            = restService.editCategory(category.name, category.sId, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                    String status = categoryEditResp.getStatus();
                    if (CategoriesStatus.STATUS_OK.equals(categoryEditResp.getStatus())) {
                        Log.e(LOG_TAG, "Category edited name: " + categoryEditResp.getData().getTitle() +
                                ", Category id: " + categoryEditResp.getData().getId());
                    } else if (CategoriesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || CategoriesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                        unauthorizedErrorReaction();
                    } else {
                        unknownErrorReaction();
                        Log.e(LOG_TAG, unknownError);
                    }
                } catch (RetrofitError e) {
                    retrofitErrorMessageShow(e.getKind(), e);
                }
            } else {
                noInternetReaction();
                Log.e(LOG_TAG, noInternetError);
            }
        }
    }

    @Background
    public void deleteCategories(Map<Integer, Categories> categories) {
        if (categories != null && !categories.isEmpty()) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                try {
                    for (Map.Entry<Integer, Categories> pair : categories.entrySet()) {
                        Categories category = pair.getValue();
                        CategoryDeleteModel removeCategoryResp = restService.deleteCategory(category.sId, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                        String status = removeCategoryResp.getStatus();
                        if (CategoriesStatus.STATUS_OK.equals(removeCategoryResp.getStatus())) {
                            Log.e(LOG_TAG, category.sId + " category removed");
                        } else if (CategoriesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || CategoriesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                            unauthorizedErrorReaction();
                        } else {
                            unknownErrorReaction();
                            Log.e(LOG_TAG, unknownError);
                        }
                    }
                } catch (RetrofitError e) {
                    retrofitErrorMessageShow(e.getKind(), e);
                }
            } else {
                noInternetReaction();
                    Log.e(LOG_TAG, noInternetError);
            }
        }
    }

    @Background
    public void logout() {
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            try {
                UserLogoutModel logoutResp = restService.logout();
                if (UserStatus.STATUS_ERROR.equals(logoutResp.getStatus())) {
                    unknownErrorReaction();
                    Log.e(LOG_TAG, unknownError);
                }
            } catch (RetrofitError e) {
                retrofitErrorMessageShow(e.getKind(), e);
            }
        } else {
            noInternetReaction();
            Log.e(LOG_TAG, noInternetError);
        }
    }

    @Background
    public void synchronize(int launchMode) {
        categoriesSync(launchMode);
    }

    @Background
    public void categoriesSync(final int launchMode) {
        List<Categories> categories = new Select().from(Categories.class).orderBy("sId").execute();

        List<CategorySyncObject> params = new ArrayList<>();
        for (Categories category : categories) {
            if (category.sId != null) {
                params.add(new CategorySyncObject(category.sId, category.name));
            }
        }

        SyncWrapper data = new SyncWrapper(params, null);

        if (NetworkConnectionChecker.isNetworkConnected(context)) {
                restService.categoriesSync(
                        data,
                        MoneyTrackerApplication.getGoogleToken(context),
                        MoneyTrackerApplication.getToken(context),
                        new Callback<AllCategoriesModel>() {
                            @Override
                            public void success(AllCategoriesModel allCategoriesModel, Response response) {
                                if (allCategoriesModel.getStatus().equalsIgnoreCase(CategoriesStatus.STATUS_OK)) {
                                    Log.e(LOG_TAG, "OK. Category sync status success");
                                    if (launchMode == SYNC_MANUAL) {
                                        syncFinished(SYNC_CATEGORIES, SYNC_OK);
                                    }
                                } else {
                                    Log.e(LOG_TAG, "BAD. Category sync: something went wrong");
                                    if (launchMode == SYNC_MANUAL) {
                                        syncFinished(SYNC_CATEGORIES, SYNC_FAILED);
                                    }
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(LOG_TAG, "ERROR. Category sync failed");
                                if (launchMode == SYNC_MANUAL) {
                                    if (error.getCause().getMessage().equalsIgnoreCase("retrofit.RetrofitError: 401 Unauthorized")) {
                                        unauthorizedErrorReaction();
                                    } else {
                                        syncFinished(SYNC_CATEGORIES, SYNC_FAILED);
                                    }
                                }
                            }
                        });
        } else {
            noInternetReaction();
            Log.e(LOG_TAG, noInternetError);
        }

        expensesSync(launchMode);
    }

    @Background
    public void expensesSync(final int launchMode) {
        List<Expenses> expenses = new Select().from(Expenses.class).orderBy("sId").execute();

        List<ExpensesSyncObject> params = new ArrayList<>();
        for (Expenses expense : expenses) {
            if (expense.sId != null) {
                params.add(new ExpensesSyncObject(expense.sId, expense.category.sId, expense.name,
                        String.valueOf(expense.price), DateConvertUtils.dateToString(expense.date, DateFormats.INVERSE_DATE_FORMAT)));
            }
        }

        SyncWrapper data = new SyncWrapper(null, params);

        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            restService.expensesSync(
                    data,
                    MoneyTrackerApplication.getGoogleToken(context),
                    MoneyTrackerApplication.getToken(context),
                    new Callback<AllExpensesModel>() {
                        @Override
                        public void success(AllExpensesModel allExpensesModel, Response response) {
                            if (allExpensesModel.getStatus().equalsIgnoreCase("success")) {
                                Log.e(LOG_TAG, "OK. Expenses sync status success");
                                if (launchMode == SYNC_MANUAL) {
                                    syncFinished(SYNC_EXPENSES, SYNC_OK);
                                }
                            } else {
                                Log.e(LOG_TAG, "BAD. Expenses sync: something went wrong");
                                if (launchMode == SYNC_MANUAL || launchMode == SYNC_EXPENSES_LIST_UPDATE) {
                                    syncFinished(SYNC_EXPENSES, SYNC_FAILED);
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(LOG_TAG, "ERROR. Expenses sync failed");
                            if (launchMode == SYNC_MANUAL || launchMode == SYNC_EXPENSES_LIST_UPDATE) {
                                if (error.getCause().getMessage().equalsIgnoreCase("retrofit.RetrofitError: 401 Unauthorized")) {
                                    unauthorizedErrorReaction();
                                } else {
                                    syncFinished(SYNC_EXPENSES, SYNC_FAILED);
                                }
                            }
                        }
                    });
            if (launchMode == SYNC_AUTOMATIC) {
                NotificationUtil.UpdateNotifications(context);
            }
        } else {
            noInternetReaction();
            Log.e(LOG_TAG, noInternetError);
        }
    }

    @UiThread
    public void unauthorizedErrorReaction() {
        Toast.makeText(context, unauthorizedError, Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, CategoriesStatus.STATUS_WRONG_TOKEN);
        goToLogin();
        ((Activity)context).finish();
    }

    @UiThread
    public void unknownErrorReaction() {
        Toast.makeText(context, unknownError, Toast.LENGTH_LONG).show();
    }

    @UiThread
    public void noInternetReaction() {
        Toast.makeText(context, noInternetError, Toast.LENGTH_LONG).show();
    }

    @UiThread
    public void syncFinished(int type, int status) {
        switch (type) {
            case SYNC_CATEGORIES:
                if (status == SYNC_OK) {
                    Toast.makeText(context, syncCategoriesOk, Toast.LENGTH_LONG).show();
                } else if (status == SYNC_FAILED) {
                    Toast.makeText(context, syncCategoriesFailed, Toast.LENGTH_LONG).show();
                }
                break;
            case SYNC_EXPENSES:
                if (status == SYNC_OK) {
                    Toast.makeText(context, syncExpensesOk, Toast.LENGTH_LONG).show();
                } else if (status == SYNC_FAILED) {
                    Toast.makeText(context, syncExpensesFailed, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @UiThread
    public void retrofitErrorMessageShow(RetrofitError.Kind kind, RetrofitError error) {
        if (kind.equals(RetrofitError.Kind.NETWORK)) {
            Toast.makeText(context, errorTroubleConnect, Toast.LENGTH_SHORT).show();
        } else if (kind.equals(RetrofitError.Kind.CONVERSION) || kind.equals(RetrofitError.Kind.HTTP)) {
            Toast.makeText(context, errorTroubleServer, Toast.LENGTH_SHORT).show();
        } else {
            throw error;
        }
    }

    @UiThread
    void goToLogin() {
        Intent intent = new Intent(context, LoginActivity_.class);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
