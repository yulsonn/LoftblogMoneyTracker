package ru.loftschool.loftblogmoneytracker.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;

public class CategoriesAdapter extends SelectableAdapter<CategoriesAdapter.CardViewCategoryHolder> {

    private List<Categories> categories;
    private CardViewCategoryHolder.ClickListener clickListener;
    private Context context;
    private int lastPosition = -1;

    public CategoriesAdapter() {
    }

    public CategoriesAdapter(List<Categories> categories, CardViewCategoryHolder.ClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
    }

    @Override
    public CategoriesAdapter.CardViewCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_categories, parent, false);
        context = parent.getContext();
        return new CardViewCategoryHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CardViewCategoryHolder holder, int position) {
        Categories category = categories.get(position);
        holder.textTitle.setText(category.name);
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

        setAnimation(holder.cardView, position);
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()){
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    count++;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }
                for (int i = 0; i < count; i++) {
                    positions.remove(0);
                }
            }
        }
    }

    public void removeItem(int position) {
        removeCategories(position);
        notifyItemRemoved(position);
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int position = 0; position < itemCount; position++) {
            removeCategories(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    private void removeCategories(int position) {
        if (categories.get(position) != null) {
            categories.get(position).delete();
            categories.remove(position);
        }
    }


    public void addCategory(Categories category) {
        category.save();
        categories.add(category);
        notifyItemInserted(getItemCount()-1);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public static class CardViewCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected TextView textTitle;
        protected View selectedOverlay;
        protected CardView cardView;
        private ClickListener clickListener;

        public CardViewCategoryHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            textTitle = (TextView) itemView.findViewById(R.id.category_name_text);
            selectedOverlay = itemView.findViewById(R.id.categories_selected_overlay);
            cardView = (CardView) itemView.findViewById(R.id.card_view_categories);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (clickListener != null) {
                clickListener.onItemLongClicked(getAdapterPosition());
            }
            return true;
        }

        public interface ClickListener {

            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }
    }
}
