package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.models.UserRegisterModel;

public interface UserRegisterAPI {

    @GET("/auth")
    UserRegisterModel registerUser(@Query("login") String login,
                                   @Query("password") String password,
                                   @Query("register") String flag);

}
