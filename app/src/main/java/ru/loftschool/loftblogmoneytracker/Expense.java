package ru.loftschool.loftblogmoneytracker;

import java.util.Date;

public class Expense {

    private String title;
    private int sum;
    private Date date;

    public Expense(String title, int sum, Date date) {
        this.title = title;
        this.sum = sum;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public int getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

}
