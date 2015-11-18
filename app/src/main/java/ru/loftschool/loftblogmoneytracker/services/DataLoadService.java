package ru.loftschool.loftblogmoneytracker.services;

import android.app.IntentService;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.ExpenseDetails;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoriesStatus;
import ru.loftschool.loftblogmoneytracker.rest.status.ExpensesStatus;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity;
import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateFormats;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EIntentService
public class DataLoadService extends IntentService implements DateFormats {

    @Bean
    ServerReqUtils serverRequest;

    RestService restService = new RestService();

    public DataLoadService() {
        super(DataLoadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadData();
    }

    public void loadData() {
        startLoadData();

        if (Categories.selectAll().isEmpty()) {
            initialLoadData();
        } else {
            updateData();
        }

        checkData();

        if (Categories.selectAll().isEmpty()) {
            insertDefaultCategories();
        }

        stopLoadData();
    }

    private void initialLoadData() {
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            try {
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
                    insertDefaultCategories();
                }
            } catch (UnauthorizedException e) {
                serverRequest.unauthorizedErrorReaction();
            } catch (RetrofitError e) {
                serverRequest.retrofitErrorMessageShow(e.getKind(), e);
            }
        } else {
            serverRequest.noInternetReaction();
        }
    }

    private void insertDefaultCategories() {
        String[] initCategories = getResources().getStringArray(R.array.initial_categories);
        for (int i = 0; i < initCategories.length; i++) {
            Categories category = new Categories(initCategories[i]);
            category.save();
            serverRequest.addCategoryToServer(category);
        }
    }

    private void updateData() {
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            try {
                ArrayList<CategoryWithExpensesModel> expensesResp = restService.getAllCategoriesWithExpenses(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
                if (!expensesResp.isEmpty()) {
                    for (CategoryWithExpensesModel category : expensesResp) {
                        Categories localCategory = Categories.selectCategoryById(Integer.parseInt(category.getId()));
                        if (localCategory == null) {
                            Categories newCategory = new Categories(category.getTitle(), Integer.parseInt(category.getId()));
                            newCategory.save();
                            if (!category.getTransactions().isEmpty()) {
                                for (ExpenseDetails expense : category.getTransactions()) {
                                    Expenses newExpense = new Expenses(expense.getComment(), Float.parseFloat(expense.getSum()),
                                            DateConvertUtils.stringToDate(expense.getTrDate(), INVERSE_DATE_FORMAT_LOAD), Integer.parseInt(expense.getId()), newCategory);
                                    newExpense.save();
                                }
                            }
                        } else {
                            localCategory.name = category.getTitle();
                            localCategory.save();
                            if (!category.getTransactions().isEmpty()) {
                                for (ExpenseDetails expense : category.getTransactions()) {
                                    Expenses localExpense = Expenses.selectExpenseById(Integer.parseInt(expense.getId()));
                                    if (localExpense == null) {
                                        Expenses newExpense = new Expenses(expense.getComment(), Float.parseFloat(expense.getSum()),
                                                DateConvertUtils.stringToDate(expense.getTrDate(), INVERSE_DATE_FORMAT_LOAD), Integer.parseInt(expense.getId()), localCategory);
                                        newExpense.save();
                                    } else {
                                        localExpense.name = expense.getComment();
                                        localExpense.price = Float.parseFloat(expense.getSum());
                                        localExpense.date = DateConvertUtils.stringToDate(expense.getTrDate(), INVERSE_DATE_FORMAT_LOAD);
                                        localExpense.category = localCategory;
                                        localExpense.save();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (UnauthorizedException e) {
                serverRequest.unauthorizedErrorReaction();
            } catch (RetrofitError e) {
                serverRequest.retrofitErrorMessageShow(e.getKind(), e);
            }
        } else {
            serverRequest.noInternetReaction();
        }
    }

    private void checkData() {
        checkCategories();
        checkExpenses();
    }

    private void checkCategories() {
        List<Categories> localCategories = Categories.selectAll();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            try {
                AllCategoriesModel categoriesResp = restService.getAllCategories(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
                String status = categoriesResp.getStatus();
                if (CategoriesStatus.STATUS_OK.equals(status)) {
                    List<CategoryDetails> serverCategories = categoriesResp.getCategories();
                    if (localCategories.size() != serverCategories.size()) {
                        for (Categories lCategory : localCategories) {
                            if (lCategory.sId != null) {
                                int lCategoryId = lCategory.sId;
                                boolean hasEqual = false;
                                for (int i = 0; i < serverCategories.size(); i++) {
                                    if (serverCategories.get(i).getId() == lCategoryId) {
                                        hasEqual = true;
                                        break;
                                    }
                                }
                                if (!hasEqual) {
                                    lCategory.delete();
                                }
                            } else {
                                serverRequest.addCategoryToServer(lCategory);
                            }
                        }
                    }
                } else if (CategoriesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || CategoriesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                    serverRequest.unauthorizedErrorReaction();
                } else {
                    serverRequest.unknownErrorReaction();
                }
            } catch (RetrofitError e) {
                serverRequest.retrofitErrorMessageShow(e.getKind(), e);
            }
        } else {
            serverRequest.noInternetReaction();
        }
    }

    private void checkExpenses() {
        List<Expenses> localExpenses = Expenses.selectAll();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            try {
                AllExpensesModel expensesResp = restService.getAllExpenses(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
                String status = expensesResp.getStatus();
                if (ExpensesStatus.STATUS_OK.equals(status)) {
                    List<ExpenseDetails> serverExpenses = expensesResp.getExpenses();
                    if (localExpenses.size() != serverExpenses.size()) {
                        for (Expenses lExpense : localExpenses) {
                            if (lExpense.sId != null) {
                                int lExpenseId = lExpense.sId;
                                boolean hasEqual = false;
                                for (int i = 0; i < serverExpenses.size(); i++) {
                                    if (Integer.parseInt(serverExpenses.get(i).getId()) == lExpenseId) {
                                        hasEqual = true;
                                        break;
                                    }
                                }
                                if (!hasEqual) {
                                    lExpense.delete();
                                }
                            } else {
                                if (lExpense.category.sId != null) {
                                    serverRequest.addExpenseToServer(lExpense);
                                }
                            }
                        }
                    }
                } else if (ExpensesStatus.STATUS_UNAUTHORIZED.equalsIgnoreCase(status) || ExpensesStatus.STATUS_WRONG_TOKEN.equalsIgnoreCase(status)) {
                    serverRequest.unauthorizedErrorReaction();
                } else {
                    serverRequest.unknownErrorReaction();
                }
            } catch (RetrofitError e) {
                serverRequest.retrofitErrorMessageShow(e.getKind(), e);
            }
        } else {
            serverRequest.noInternetReaction();
        }
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
