package ru.loftschool.loftblogmoneytracker.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class AllCategoriesModel {

    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<AllCategoriesItem> categories = new ArrayList<AllCategoriesItem>();

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
    public List<AllCategoriesItem> getCategories() {
        return categories;
    }

    /**
     *
     * @param categoriesItems
     * The data
     */
    public void setCategories(List<AllCategoriesItem> categoriesItems) {
        this.categories = categoriesItems;
    }

}
