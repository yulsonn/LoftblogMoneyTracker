package ru.loftschool.loftblogmoneytracker.rest;

import java.util.List;

public class SyncWrapper {

    private List<CategorySyncObject> categories;

    private List<ExpensesSyncObject> expenses;

    public SyncWrapper(List<CategorySyncObject> categories, List<ExpensesSyncObject> expenses) {
        this.categories = categories;
        this.expenses = expenses;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (categories != null) {
            sb.append("[");
            for (int i = 0; i < categories.size(); i++) {
                sb.append(categories.get(i).toString());
                if (i != categories.size()-1) {
                    sb.append(",");
                }
            }
            sb.append("]");
        } else if (expenses != null) {
            sb.append("[");
            for (int i = 0; i < expenses.size(); i++) {
                sb.append(expenses.get(i).toString());
                if (i != expenses.size()-1) {
                    sb.append(",");
                }
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
