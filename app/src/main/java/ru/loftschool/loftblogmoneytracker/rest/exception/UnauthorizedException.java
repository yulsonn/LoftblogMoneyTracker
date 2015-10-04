package ru.loftschool.loftblogmoneytracker.rest.exception;

/**
 * Created by Yulia on 21.09.2015.
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException() {
    }

    public UnauthorizedException(String detailMessage) {
        super(detailMessage);
    }

    public UnauthorizedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UnauthorizedException(Throwable throwable) {
        super(throwable);
    }
}
