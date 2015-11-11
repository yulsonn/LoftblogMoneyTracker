package ru.loftschool.loftblogmoneytracker.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.List;

@Table(name = "Categories")
public class Categories extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "sId")
    public Integer sId;

    public Categories() {
        super();
    }

    public Categories(String name, Integer sId) {
        super();
        this.name = name;
        this.sId = sId;
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
        return getMany(Expenses.class, "Category");
    }

    public static List<Categories> selectByNameCaseInsensitive(String name) {
        return SQLiteUtils.rawQuery(Categories.class, "SELECT * from Categories where lower(Name) = ?", new String[]{name.toLowerCase()});
    }

    public static Categories selectCategoryById(int serverId){
        return new Select().from(Categories.class).where("sId = ?", serverId).executeSingle();
    }

    public static List<Categories> selectAll() {
        return new Select().from(Categories.class).orderBy("Id").execute();
    }
}
