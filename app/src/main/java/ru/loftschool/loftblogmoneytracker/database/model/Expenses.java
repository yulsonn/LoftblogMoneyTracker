package ru.loftschool.loftblogmoneytracker.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Expenses")
public class Expenses extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "Price")
    public String price;

    @Column(name = "Date")
    public String date;

    @Column(name = "Category")
    public Categories category;

    public Expenses() {
        super();
    }

    public Expenses(String name, String price, String date, Categories category) {
        super();
        this.name = name;
        this.price = price;
        this.date = date;
        this.category = category;
    }
}
