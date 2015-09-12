package ru.loftschool.loftblogmoneytracker.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Categories")
public class Categories extends Model {

    @Column(name = "Name")
    public String name;

    public Categories() {
        super();
    }

    public Categories(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Expenses> expenses(){
        return getMany(Expenses.class, "Categories");
    }
}
