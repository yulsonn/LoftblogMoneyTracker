package ru.loftschool.loftblogmoneytracker.rest;

import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;

public class RestService {

    private static final String FLAG = "1";

    public UserRegisterModel register(String login, String password) {
        RestClient restClient = new RestClient();

        return restClient.registerUserAPI().registerUser(login, password, FLAG);
    }
}
