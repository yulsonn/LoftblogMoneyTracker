package ru.loftschool.loftblogmoneytracker.rest.models;

import com.google.gson.annotations.Expose;

public class UserLogoutModel {
    @Expose
    private String status;

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
