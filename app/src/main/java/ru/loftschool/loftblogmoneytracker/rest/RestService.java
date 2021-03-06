package ru.loftschool.loftblogmoneytracker.rest;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.BalanceModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDeleteModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLoginModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLogoutModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;

public class RestService {

    private static final String FLAG = "1";

    RestClient restClient;

    public RestService() {
        restClient = new RestClient();
    }

    public UserRegisterModel register(String login, String password) {
        return restClient.getUserAPI().registerUser(login, password, FLAG);
    }

    public UserLoginModel login(String login, String password) {
        return restClient.getUserAPI().loginUser(login, password);
    }

    public UserLogoutModel logout() {
        return restClient.getUserAPI().logoutUser();
    }

    public CategoryModel addCategory(String title, String gToken, String token){
        return restClient.getCategoriesAPI().addCategory(title, gToken, token);
    }

    public CategoryModel editCategory(String title, Integer id, String gToken, String token) {
        return restClient.getCategoriesAPI().editCategory(title, id, gToken, token);
    }

    public AllCategoriesModel getAllCategories(String gToken, String token) {
        return restClient.getCategoriesAPI().getAllCategories(gToken, token);
    }

    public GoogleAccountDataModel getGoogleAccountData(String gToken) {
        return restClient.getGoogleAccountDataAPI().googleJson(gToken);
    }

    public AllExpensesModel getAllExpenses(String gToken, String token) {
        return restClient.getExpensesAPI().getAllExpenses(gToken, token);
    }

    public AllExpensesModel addExpense(String sum,  String comment, Integer categoryId, String trDate, String gToken, String token) {
        return restClient.getExpensesAPI().addExpense(sum, comment, categoryId, trDate, gToken, token);
    }

    public List<CategoryWithExpensesModel> getCategoryWithExpenses(Integer id, String gToken, String token) {
        return restClient.getCategoriesAPI().getCategoryWithExpenses(id, gToken, token);
    }

    public ArrayList<CategoryWithExpensesModel> getAllCategoriesWithExpenses(String gToken, String token)  throws UnauthorizedException {
        return restClient.getCategoriesAPI().getAllCategoriesWithExpenses(gToken, token);
    }

    public void deleteCategory(Integer id, String gToken, String token, Callback<CategoryDeleteModel> cb) {
        restClient.getCategoriesAPI().deleteCategory(id, gToken, token, cb);
    }

    public BalanceModel getBalance(String gToken, String token) {
        return restClient.getBalanceAPI().getBalance(gToken, token);
    }

    public BalanceModel setBalance(String sum, String gToken, String token) {
        return restClient.getBalanceAPI().setBalance(sum, gToken, token);
    }

     public void categoriesSync(SyncWrapper data, String gToken, String token, Callback<AllCategoriesModel> cb) {
            restClient.getCategoriesAPI().categoriesSync(data, gToken, token, cb);
        }

    public void expensesSync(SyncWrapper data, String gToken, String token, Callback<AllExpensesModel> cb) {
        restClient.getExpensesAPI().expensesSync(data, gToken, token, cb);
    }

}
