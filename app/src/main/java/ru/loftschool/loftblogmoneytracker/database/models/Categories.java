package ru.loftschool.loftblogmoneytracker.database.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import ru.loftschool.loftblogmoneytracker.database.AppDataBase;

@ModelContainer
@Table(databaseName = AppDataBase.NAME)
public class Categories extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String name;

    List<Expenses> expenses;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "expenses")
    public List<Expenses> getMyExpenses(){
        if(expenses == null) {
            expenses = new Select().from(Expenses.class)
                                    .where(Condition.column(Expenses$Table.CATEGORIESMODELCONTAINER_CATEGORY_ID).is(id))
                                    .queryList();
        }

        return expenses;
    }

    public Categories() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
