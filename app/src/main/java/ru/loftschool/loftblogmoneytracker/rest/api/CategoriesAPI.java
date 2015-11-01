package ru.loftschool.loftblogmoneytracker.rest.api;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;

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

    @GET("/categories/del")
    CategoryModel deleteCategory(@Query("id") Integer id,
                                 @Query("google_token") String gToken,
                                 @Query("auth_token") String token) throws UnauthorizedException;

    @GET("/categories")
    AllCategoriesModel getAllCategories(@Query("google_token") String gToken,
                                        @Query("auth_token") String token);

    @GET("/categories/{id}")
    CategoryWithExpensesModel getCategoryWithExpenses(@Path("id") Integer id,
                                                      @Query("google_token") String gToken,
                                                      @Query("auth_token") String token);

    @GET("/categories/synch")
    void categoriesSync(@Query("data{id}") Integer id,
                        @Query("data[title]")String title,
                        @Query("google_token") String gToken,
                        @Query("auth_token") String token,
                        Callback<CategoryDetails> cb);

    @GET("/transcat")
    ArrayList<CategoryWithExpensesModel> getAllCategoriesWithExpenses(@Query("google_token") String gToken,
                                                                      @Query("auth_token") String token);
}
