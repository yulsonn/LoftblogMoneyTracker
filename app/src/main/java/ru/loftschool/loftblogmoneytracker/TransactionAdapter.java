package ru.loftschool.loftblogmoneytracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class TransactionAdapter extends ArrayAdapter<Transaction>{

    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, 0, transactions);
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = getItem(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        int[] colors = {Color.parseColor("#81D4FA"), Color.parseColor("#EEFF41"), Color.parseColor("#B2FF59"), Color.parseColor("#FF8A80"), Color.parseColor("#EA80FC")};
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView textTitle = (TextView) convertView.findViewById(R.id.name_text);
        TextView sumTitle = (TextView) convertView.findViewById(R.id.sum_text);
        TextView dateTitle = (TextView) convertView.findViewById(R.id.date_text);
        textTitle.setText(transaction.getTitle());
        sumTitle.setText(Integer.toString(transaction.getSum()));
        dateTitle.setText(dateFormat.format(transaction.getDate()));
        textTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
        sumTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
        dateTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
        return convertView;
    }
}
