package ru.loftschool.loftblogmoneytracker.rest;

import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
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

    public CategoryModel addCategory(String title, String gToken, String token) throws UnauthorizedException{
        return restClient.getCategoriesAPI().addCategory(title, gToken, token);
    }

    public CategoryModel editCategory(String title, Integer id, String gToken, String token) {
        return restClient.getCategoriesAPI().editCategory(title, id, gToken, token);
    }

    public AllCategoriesModel getAllCategories(String gToken, String token) {
        return restClient.getCategoriesAPI().getAllCategories(gToken, token);
    }

    public GoogleAccountDataModel getGoogleAccountData(String gToken) {
        return restClient.getGoogleAccountDataGetAPI().googleJson(gToken);
    }
}
