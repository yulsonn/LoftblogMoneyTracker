package ru.loftschool.loftblogmoneytracker.rest.api;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;

public interface GoogleAccountDataGetAPI {

    @GET("/gcheck")
    void tokenStatus (@Query("google_token") String gToken, Callback<GoogleAccountDataModel> gTokenStatusModelCallback);

    @GET("/gjson")
    GoogleAccountDataModel googleJson(@Query("google_token") String gToken);
}
