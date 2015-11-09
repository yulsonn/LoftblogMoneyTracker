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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;

public class ExpensesAdapter extends SelectableAdapter<ExpensesAdapter.CardViewHolder> {

    private static final long UNDO_TIMEOUT = 3600L;

    private boolean multipleRemove = false;

    private List<Expenses> expenses;
    private Map<Integer, Expenses> removedExpensesMap;
    private CardViewHolder.ClickListener clickListener;
    private Context context;
    private int lastPosition = -1;
    private Timer undoRemoveTimer;

    public ExpensesAdapter() {
    }

    public ExpensesAdapter(List<Expenses> expenses, CardViewHolder.ClickListener clickListener) {
        this.expenses = expenses;
        this.clickListener = clickListener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_expenses, parent, false);
        context = parent.getContext();
        return new CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Expenses expense = expenses.get(position);
        holder.textTitle.setText(expense.name);
        holder.dateTitle.setText(DateConvertUtils.dateToString(expense.date, DateConvertUtils.DEFAULT_FORMAT));
        holder.sumTitle.setText(String.format("%.2f",expense.price));
        holder.categoryTitle.setText(expense.category.toString());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

        setAnimation(holder.cardView, position);
    }

    public void removeItems(List<Integer> positions) {
        if (positions.size() > 1) {
            multipleRemove = true;
        }
        saveRemovedItems(positions);

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
        multipleRemove = false;
    }

    public void removeItem(int position) {
        if (!multipleRemove) {
            saveRemovedItem(position);
        }
        removeExpenses(position);
        notifyItemRemoved(position);
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int position = 0; position < itemCount; position++) {
            removeExpenses(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    private void removeExpenses(int position) {
        if (expenses.get(position) != null) {
            //expenses.get(position).delete();
            expenses.remove(position);
        }
    }

    private void completelyRemoveExpensesFromDB() {
        if (removedExpensesMap != null) {
            for (Map.Entry<Integer, Expenses> pair : removedExpensesMap.entrySet()) {
                pair.getValue().delete();
            }
            removedExpensesMap = null; 
        }
    }

    public Expenses addExpense(Expenses expense) {
        expense.save();
        expenses.add(expense);
        notifyItemInserted(getItemCount() - 1);
        return  expense;
    }

    public Expenses getExpense(int position) {
        return expenses.get(position);
    }

    public void updateExpense(int position, Expenses expense) {
        expense.save();
        notifyItemChanged(position);
    }

    public void refreshAdapter(List<Expenses> data, int rowCount) {
        if (data != null && !data.isEmpty()){
            for (Expenses expense : data) {
                expenses.add(expense);
            }
        }
        notifyItemRangeInserted(0, rowCount);
    }

    @Override
    public int getItemCount() {
        return expenses == null ? 0 : expenses.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void saveRemovedItems(List<Integer> positions) {
        if (removedExpensesMap != null) {
            completelyRemoveExpensesFromDB();
        }
        removedExpensesMap = new TreeMap<>();
        for (int position : positions) {
            removedExpensesMap.put(position, expenses.get(position));
        }
    }

    private void saveRemovedItem(int position) {
        if (removedExpensesMap != null) {
            completelyRemoveExpensesFromDB();
        }
        ArrayList<Integer> positions = new ArrayList<>(1);
        positions.add(position);
        saveRemovedItems(positions);
    }

    public void restoreRemovedItems() {
        stopUndoTimer();
        for (Map.Entry<Integer, Expenses> pair : removedExpensesMap.entrySet()){
            expenses.add(pair.getKey(), pair.getValue());
            notifyItemInserted(pair.getKey());
        }
        removedExpensesMap = null;
    }

    public void startUndoTimer(long timeout) {
        stopUndoTimer();
        this.undoRemoveTimer = new Timer();
        this.undoRemoveTimer.schedule(new UndoTimer(), timeout > 0 ? timeout : UNDO_TIMEOUT);
    }

    private void stopUndoTimer() {
        if (this.undoRemoveTimer != null) {
            this.undoRemoveTimer.cancel();
            this.undoRemoveTimer = null;
        }
    }

    private class UndoTimer extends TimerTask {
        @Override
        public void run() {
            undoRemoveTimer = null;
            completelyRemoveExpensesFromDB();
            removedExpensesMap = null;
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected TextView textTitle;
        protected TextView sumTitle;
        protected TextView dateTitle;
        protected TextView categoryTitle;   // for testing of Categories-Expenses relation
        protected View selectedOverlay;
        protected CardView cardView;
        private ClickListener clickListener;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            textTitle = (TextView) itemView.findViewById(R.id.expense_name_text);
            dateTitle = (TextView) itemView.findViewById(R.id.expense_date_text);
            sumTitle = (TextView) itemView.findViewById(R.id.expense_sum_text);
            categoryTitle = (TextView) itemView.findViewById(R.id.expense_category_text);   // for testing of Categories-Expenses relation
            selectedOverlay = itemView.findViewById(R.id.expense_selected_overlay);
            cardView = (CardView) itemView.findViewById(R.id.card_view_expenses);


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
