package ru.loftschool.loftblogmoneytracker.rest;

import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryAddModel;
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
        return restClient.getUserRegisterAPI().registerUser(login, password, FLAG);
    }

    public UserLoginModel login(String login, String password) {
        return restClient.getUserLoginAPI().loginUser(login, password);
    }

    public UserLogoutModel logout() {
        return restClient.getUserLogoutAPI().logoutUser();
    }

    public CategoryAddModel addCategory(String title, String gToken, String token) throws UnauthorizedException{
        return restClient.getCategoryAddAPI().addCategory(title, gToken, token);
    }

    public GoogleAccountDataModel getGoogleAccountData(String gToken) {
        return restClient.getGoogleAccountDataGetAPI().googleJson(gToken);
    }
}
