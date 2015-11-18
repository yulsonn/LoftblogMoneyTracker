package ru.loftschool.loftblogmoneytracker.rest.api;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.SyncWrapper;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDeleteModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;

public interface CategoriesAPI {

    @GET("/categories/add")
    CategoryModel addCategory(@Query("title") String title,
                              @Query("google_token") String gToken,
                              @Query("auth_token") String token);

    @GET("/categories/edit")
    CategoryModel editCategory(@Query("title") String title,
                               @Query("id") Integer id,
                               @Query("google_token") String gToken,
                               @Query("auth_token") String token);

    @GET("/categories/del")
    CategoryDeleteModel deleteCategory(@Query("id") Integer id,
                                 @Query("google_token") String gToken,
                                 @Query("auth_token") String token);

    @GET("/categories")
    AllCategoriesModel getAllCategories(@Query("google_token") String gToken,
                                        @Query("auth_token") String token);

    @GET("/categories/{id}")
    List<CategoryWithExpensesModel> getCategoryWithExpenses(@Path("id") Integer id,
                                                      @Query("google_token") String gToken,
                                                      @Query("auth_token") String token);

    @GET("/categories/synch")
    void categoriesSync(@Query("data") SyncWrapper data,
                        @Query("google_token") String gToken,
                        @Query("auth_token") String token,
                        Callback<AllCategoriesModel> cb);

    @GET("/transcat")
    ArrayList<CategoryWithExpensesModel> getAllCategoriesWithExpenses(@Query("google_token") String gToken,
                                                                      @Query("auth_token") String token)  throws UnauthorizedException;
}
