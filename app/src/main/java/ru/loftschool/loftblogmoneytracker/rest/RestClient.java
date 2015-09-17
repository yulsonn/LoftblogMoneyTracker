package ru.loftschool.loftblogmoneytracker.rest;

import retrofit.RestAdapter;
import ru.loftschool.loftblogmoneytracker.rest.api.UserRegisterAPI;

public class RestClient {

    private static final String BASE_URL = "http://62.109.17.114";

    private UserRegisterAPI userRegisterAPI;

    RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .build();

        userRegisterAPI = restAdapter.create(UserRegisterAPI.class);
    }

    public UserRegisterAPI registerUserAPI() {
        return userRegisterAPI;
    }
}
