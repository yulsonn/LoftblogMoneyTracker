package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.http.GET;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLogoutModel;

public interface UserLogoutAPI {

    @GET("/logout")
    UserLogoutModel logoutUser();
}
