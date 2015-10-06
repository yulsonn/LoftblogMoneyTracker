package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;

public interface CategoriesAPI {

    @GET("/categories/add")
    CategoryModel addCategory(@Query("title") String title,
                                 @Query("google_token") String gToken,
                                 @Query("auth_token") String token) throws UnauthorizedException;


    @GET("/categories/edit")
    CategoryModel editCategory(@Query("title") String title,
                                 @Query("id") Integer id,
                                 @Query("google_token") String gToken,
                                 @Query("auth_token") String token);

    @GET("/categories")
    AllCategoriesModel getAllCategories(@Query("google_token") String gToken,
                                        @Query("auth_token") String token);

}
