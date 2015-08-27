package ru.loftschool.loftblogmoneytracker;

import java.util.Date;

public class Transaction {

    private String title;
    private int sum;
    private Date date;

    public Transaction(String title, int sum, Date date) {
        this.title = title;
        this.sum = sum;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
