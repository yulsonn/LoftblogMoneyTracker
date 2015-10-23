package ru.loftschool.loftblogmoneytracker.rest.models;

import com.google.gson.annotations.Expose;


public class BalanceModel {

    @Expose
    private String status;
    @Expose
    private String balance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
