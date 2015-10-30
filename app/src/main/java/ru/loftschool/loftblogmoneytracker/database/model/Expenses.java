package ru.loftschool.loftblogmoneytracker.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "Expenses")
public class Expenses extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "Price")
    //public String price;
    public Float price;

    @Column(name = "Date")
    //public String date;
    public Date date;

    @Column(name = "sId")
    public Integer sId;

    @Column(name = "Category", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public Categories category;

    public Expenses() {
        super();
    }

    public Expenses(String name, Float price, Date date, Integer sId, Categories category) {
        this.name = name;
        this.price = price;
        this.date = date;
        this.sId = sId;
        this.category = category;
    }

    public Expenses(String name, Float price, Date date, Categories category) {
        super();
        this.name = name;
        this.price = price;
        this.date = date;
        this.category = category;
    }

    public static List<Expenses> selectAll() {
        return new Select().from(Expenses.class).execute();
    }
}
