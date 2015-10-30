package ru.loftschool.loftblogmoneytracker.rest;

public class ExpensesSyncObject {

    private int id;
    private int categoryId;
    private String comment;
    private String sum;
    private String date;

    public ExpensesSyncObject(int id, int categoryId, String comment, String sum, String date) {
        this.id = id;
        this.categoryId = categoryId;
        this.comment = comment;
        this.sum = sum;
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
                    "\"id\":" + id +
                    ", \"category_id\":" + categoryId +
                    ", \"comment\":\"" + comment + "\"" +
                    ", \"sum\":\"" + sum + "\"" +
                    ", \"tr_date\":\"" + date + "\"" +
                "}";
    }
}
