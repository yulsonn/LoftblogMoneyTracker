package ru.loftschool.loftblogmoneytracker.utils;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;

/**
 * Created by Yulia on 21.09.2015.
 */
public class RetrofitErrorHandler implements ErrorHandler {
    @Override public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            return new UnauthorizedException(cause);
        }
        return cause;
    }
}
