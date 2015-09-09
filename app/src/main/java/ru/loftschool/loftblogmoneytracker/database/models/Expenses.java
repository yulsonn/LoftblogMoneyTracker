package ru.loftschool.loftblogmoneytracker.database.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import ru.loftschool.loftblogmoneytracker.database.AppDataBase;

@Table(databaseName = AppDataBase.NAME)
public class Expenses extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String price;

    @Column
    private String name;

    @Column
    @ForeignKey(
            references = {@ForeignKeyReference( columnName = "category_id",
                                                columnType = Integer.class,
                                                foreignColumnName = "id")},
            saveForeignKeyModel = false)
    ForeignKeyContainer<Categories> categoriesModelContainer;

    public Expenses() {
    }

    public void associateCategory(Categories categories){
        categoriesModelContainer = new ForeignKeyContainer<Categories>(Categories.class);
        categoriesModelContainer.setModel(categories);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
