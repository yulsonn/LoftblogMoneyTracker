package ru.loftschool.loftblogmoneytracker.rest;

import retrofit.RestAdapter;
import ru.loftschool.loftblogmoneytracker.rest.api.CategoryAddAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserLoginAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserLogoutAPI;
import ru.loftschool.loftblogmoneytracker.rest.api.UserRegisterAPI;
import ru.loftschool.loftblogmoneytracker.utils.RetrofitErrorHandler;

public class RestClient {

    private static final String BASE_URL = "http://62.109.17.114";

    private UserRegisterAPI userRegisterAPI;
    private UserLoginAPI userLoginAPI;
    private UserLogoutAPI userLogoutAPI;
    private CategoryAddAPI categoryAddAPI;

    RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        userRegisterAPI = restAdapter.create(UserRegisterAPI.class);
        userLoginAPI    = restAdapter.create(UserLoginAPI.class);
        userLogoutAPI   = restAdapter.create(UserLogoutAPI.class);
        categoryAddAPI  = restAdapter.create(CategoryAddAPI.class);
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

    public CategoryAddAPI getCategoryAddAPI() {
        return categoryAddAPI;
    }
}
