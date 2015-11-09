package ru.loftschool.loftblogmoneytracker.rest;

import retrofit.RestAdapter;
import ru.loftschool.loftblogmoneytracker.rest.api.BalanceAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.CategoriesAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.ExpensesAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.GoogleAccountDataAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserAPI;
import ru.loftschool.loftblogmoneytracker.utils.network.RetrofitErrorHandler;

public class RestClient {

    private static final String BASE_URL = "http://lmt.loftblog.tmweb.ru";

    private UserAPI userAPI;
    private CategoriesAPI categoriesAPI;
    private GoogleAccountDataAPI googleAccountDataAPI;
    private ExpensesAPI expensesAPI;
    private final BalanceAPI balanceAPI;

    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        userAPI         = restAdapter.create(UserAPI.class);
        categoriesAPI   = restAdapter.create(CategoriesAPI.class);
        googleAccountDataAPI = restAdapter.create(GoogleAccountDataAPI.class);
        expensesAPI     = restAdapter.create(ExpensesAPI.class);
        balanceAPI      = restAdapter.create(BalanceAPI.class);
    }

    public UserAPI getUserAPI() {
        return userAPI;
    }

    public CategoriesAPI getCategoriesAPI() {
        return categoriesAPI;
    }

    public GoogleAccountDataAPI getGoogleAccountDataAPI() {
        return googleAccountDataAPI;
    }

    public ExpensesAPI getExpensesAPI() {
        return expensesAPI;
    }

    public BalanceAPI getBalanceAPI() {
        return balanceAPI;
    }
}
