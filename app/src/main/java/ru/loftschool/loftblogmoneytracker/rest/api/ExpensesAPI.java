package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;

public interface ExpensesAPI {

    @GET("/transactions")
    AllExpensesModel getAllExpenses(@Query("google_token") String gToken,
                                    @Query("auth_token") String token);

    @GET("/transactions/add")
    AllExpensesModel addExpense(@Query("sum") String sum,
                                @Query("comment") String comment,
                                @Query("category_id") Integer categoryId,
                                @Query("tr_date") String trDate,
                                @Query("google_token") String gToken,
                                @Query("auth_token") String token);

    @GET("transactions/synch")
    void expensesSync(@Query("data{id}") Integer id,
                      @Query("data[comment]")String comment,
                      @Query("data[sum]")String sum,
                      @Query("data[tr_date]")String date,
                      @Query("google_token") String gToken,
                      @Query("auth_token") String token,
                      Callback<CategoryDetails> cb);
}
