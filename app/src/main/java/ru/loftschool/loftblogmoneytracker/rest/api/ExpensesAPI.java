package ru.loftschool.loftblogmoneytracker.rest.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.SyncWrapper;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;

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

    @GET("/transactions/synch")
    void expensesSync(@Query("data") SyncWrapper data,
                      @Query("google_token") String gToken,
                      @Query("auth_token") String token,
                      Callback<AllExpensesModel> cb);
}
