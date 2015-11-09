package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLoginModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserLogoutModel;
import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;

public interface UserAPI {
    @GET("/auth")
    UserLoginModel loginUser(@Query("login") String login,
                             @Query("password") String password);

    @GET("/logout")
    UserLogoutModel logoutUser();

    @GET("/auth")
    UserRegisterModel registerUser(@Query("login") String login,
                                   @Query("password") String password,
                                   @Query("register") String flag);
}
