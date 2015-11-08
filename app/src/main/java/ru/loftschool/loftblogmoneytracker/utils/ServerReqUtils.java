package ru.loftschool.loftblogmoneytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.BalanceModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.ExpenseDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLogoutModel;
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

    @Background
    public void editCategoryOnServer(Categories category) {
        if (category != null) {
            if (NetworkConnectionChecker.isNetworkConnected(context)) {
                CategoryModel categoryEditResp
                        = restService.editCategory(category.name, category.sId, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
                if (CategoriesStatus.STATUS_OK.equals(categoryEditResp.getStatus())) {
                    Log.e(LOG_TAG, "Category edited name: " + categoryEditResp.getData().getTitle() +
                            ", Category id: " + categoryEditResp.getData().getId());
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

    @Background
    void getAllCategories() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            //time added for test
            AllCategoriesModel categoriesResp = restService.getAllCategories(MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            if (CategoriesStatus.STATUS_OK.equals(categoriesResp.getStatus())) {
                for (CategoryDetails category : categoriesResp.getCategories()) {
                    Log.e(LOG_TAG, "Category name: " + category.getTitle() +
                            ", Category id: " + category.getId());
                }
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void getCategoryInfo() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            List<CategoryWithExpensesModel> expensesResp = restService.getCategoryWithExpenses(1935, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            Log.e(LOG_TAG, " * Category id: " + expensesResp.get(0).getId() +
                    " * Category name: " + expensesResp.get(0).getTitle() +
                    " * Transactions: ");
            for (ExpenseDetails expense : expensesResp.get(0).getTransactions()) {
                Log.e(LOG_TAG, "  **  Expense id: " + expense.getId() +
                        "  **  , Expense category id: " + expense.getCategoryId() +
                        "  **  , Expense comment: " + expense.getComment() +
                        "  **  , Expense summ: " + expense.getSum() +
                        "  **  , Expense date: " + expense.getTrDate());
            }
        }
    }

    @Background
    void getAllCategoriesInfo() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            ArrayList<CategoryWithExpensesModel> expensesResp = restService.getAllCategoriesWithExpenses(MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            Log.e(LOG_TAG, " | Category id: " + expensesResp.get(expensesResp.size()-1).getId() +
                    " | Category name: " + expensesResp.get(expensesResp.size()-1).getTitle() +
                    " | Transactions: ");
            for (ExpenseDetails expense : expensesResp.get(expensesResp.size()-1).getTransactions()) {
                Log.e(LOG_TAG, "  **  Expense id: " + expense.getId() +
                        "  ||  , Expense category id: " + expense.getCategoryId() +
                        "  ||  , Expense comment: " + expense.getComment() +
                        "  ||  , Expense summ: " + expense.getSum() +
                        "  ||  , Expense date: " + expense.getTrDate());
            }
        }
    }

    @Background
    void getAllExpenses() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            AllExpensesModel expensesResp = restService.getAllExpenses(MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            if (CategoriesStatus.STATUS_OK.equals(expensesResp.getStatus())) {
                for (ExpenseDetails expense : expensesResp.getExpenses()) {
                    Log.e(LOG_TAG, "Expense id: " + expense.getId() +
                            ", Expense category id: " + expense.getCategoryId() +
                            ", Expense comment: " + expense.getComment() +
                            ", Expense summ: " + expense.getSum() +
                            ", Expense date: " + expense.getTrDate());
                }
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void deleteCategory() {
        RestService restService = new RestService();
        Categories category = Categories.selectCategoryById(1935);
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            CategoryModel removeCaatResp = restService.deleteCategory(category.sId, MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            if (CategoriesStatus.STATUS_OK.equals(removeCaatResp.getStatus())) {
                Log.e(LOG_TAG, category.sId + " category removed");
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void balanceTest() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            BalanceModel balanceResp = restService.getBalance(MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            if ("success".equalsIgnoreCase(balanceResp.getStatus())) {
                Log.e(LOG_TAG, "Old balance: " + balanceResp.getBalance());
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }

            balanceResp = restService.setBalance("7777", MoneyTrackerApplication.getGoogleToken(context), MoneyTrackerApplication.getToken(context));
            if ("success".equalsIgnoreCase(balanceResp.getStatus())) {
                Log.e(LOG_TAG, "New balance: " + balanceResp.getBalance());
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    public void logout() {
        if (NetworkConnectionChecker.isNetworkConnected(context)) {
            UserLogoutModel logoutResp = restService.logout();
            if (!ExpensesStatus.STATUS_OK.equals(logoutResp.getStatus())) {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        } else {
            noInternetReaction();
            Log.e(LOG_TAG, noInternetError);
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
