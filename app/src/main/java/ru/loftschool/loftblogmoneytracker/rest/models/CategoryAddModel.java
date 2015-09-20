package ru.loftschool.loftblogmoneytracker.rest.models;

import com.google.gson.annotations.Expose;

public class CategoryAddModel {

    @Expose
    private String status;
    @Expose
    private CategoryDetails data;

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

    /**
     *
     * @return
     * The data
     */
    public CategoryDetails getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(CategoryDetails data) {
        this.data = data;
    }

}
