package ru.loftschool.loftblogmoneytracker.rest;

import retrofit.RestAdapter;
import ru.loftschool.loftblogmoneytracker.rest.api.BalanceAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.CategoriesAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.ExpensesAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.GoogleAccountDataGetAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserLoginAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserLogoutAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserRegisterAPI;
import ru.loftschool.loftblogmoneytracker.utils.network.RetrofitErrorHandler;

public class RestClient {

    //private static final String BASE_URL = "http://62.109.17.114";
    private static final String BASE_URL = "http://lmt.loftblog.tmweb.ru";

    private UserRegisterAPI userRegisterAPI;
    private UserLoginAPI userLoginAPI;
    private UserLogoutAPI userLogoutAPI;
    private CategoriesAPI categoriesAPI;
    private GoogleAccountDataGetAPI googleAccountDataGetAPI;
    private ExpensesAPI expensesAPI;
    private final BalanceAPI balanceAPI;

    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        userRegisterAPI = restAdapter.create(UserRegisterAPI.class);
        userLoginAPI    = restAdapter.create(UserLoginAPI.class);
        userLogoutAPI   = restAdapter.create(UserLogoutAPI.class);
        categoriesAPI   = restAdapter.create(CategoriesAPI.class);
        googleAccountDataGetAPI = restAdapter.create(GoogleAccountDataGetAPI.class);
        expensesAPI     = restAdapter.create(ExpensesAPI.class);
        balanceAPI      = restAdapter.create(BalanceAPI.class);
    }

    public UserRegisterAPI getUserRegisterAPI() {
        return userRegisterAPI;
    }

    public UserLoginAPI getUserLoginAPI() {
        return userLoginAPI;
    }

    public UserLogoutAPI getUserLogoutAPI() {
        return userLogoutAPI;
    }

    public CategoriesAPI getCategoriesAPI() {
        return categoriesAPI;
    }

    public GoogleAccountDataGetAPI getGoogleAccountDataGetAPI() {
        return googleAccountDataGetAPI;
    }

    public ExpensesAPI getExpensesAPI() {
        return expensesAPI;
    }

    public BalanceAPI getBalanceAPI() {
        return balanceAPI;
    }
}
