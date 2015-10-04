package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryAddModel;

public interface CategoryAddAPI {

    @GET("/categories/add")
    CategoryAddModel addCategory(@Query("title") String title,
                                 @Query("auth_token") String token) throws UnauthorizedException;
}
