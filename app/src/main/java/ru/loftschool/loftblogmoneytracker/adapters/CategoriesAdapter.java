package ru.loftschool.loftblogmoneytracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CardViewCategoryHolder> {

    private List<Categories> categories;

    public CategoriesAdapter() {
    }

    public CategoriesAdapter(List<Categories> categories) {
        this.categories = categories;
    }

    @Override
    public CategoriesAdapter.CardViewCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_categories, parent, false);
        return new CardViewCategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewCategoryHolder holder, int position) {
        Categories category = categories.get(position);
        holder.textTitle.setText(category.name);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public class CardViewCategoryHolder extends RecyclerView.ViewHolder{
        protected TextView textTitle;

        public CardViewCategoryHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.category_name_text);
        }
    }
}
