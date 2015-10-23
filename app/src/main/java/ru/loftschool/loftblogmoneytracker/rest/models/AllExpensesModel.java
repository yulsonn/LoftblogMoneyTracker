package ru.loftschool.loftblogmoneytracker.rest.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AllExpensesModel {

    @Expose
    private String status;
    @Expose
    private Integer id;
    @SerializedName("data")
    @Expose
    private List<ExpenseDetails> expenses = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
    public List<ExpenseDetails> getExpenses() {
        return expenses;
    }

    /**
     *
     * @param expenses
     * The data
     */
    public void setExpenses(List<ExpenseDetails> expenses) {
        this.expenses = expenses;
    }
}
