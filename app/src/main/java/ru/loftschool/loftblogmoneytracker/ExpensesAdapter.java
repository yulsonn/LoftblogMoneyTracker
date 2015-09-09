package ru.loftschool.loftblogmoneytracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.loftschool.loftblogmoneytracker.database.models.Expenses;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.CardViewHolder> {

    private List<Expenses> expenses;
    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    public ExpensesAdapter(List<Expenses> expenses) {
        this.expenses = expenses;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Expenses expense = expenses.get(position);
        holder.textTitle.setText(expense.getName());
        holder.dateTitle.setText(dateFormat.format(new Date()));
        holder.sumTitle.setText(expense.getPrice());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        protected TextView textTitle;
        protected TextView sumTitle;
        protected TextView dateTitle;

        public CardViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.name_text);
            dateTitle = (TextView) itemView.findViewById(R.id.date_text);
            sumTitle = (TextView) itemView.findViewById(R.id.sum_text);
        }
    }
}
