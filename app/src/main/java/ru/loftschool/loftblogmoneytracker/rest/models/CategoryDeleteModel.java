package ru.loftschool.loftblogmoneytracker.rest.models;

import com.google.gson.annotations.Expose;

/**
 * Created by Yulia on 10.11.2015.
 */
public class CategoryDeleteModel {

    @Expose
    private String status;
    @Expose
    private Integer data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
