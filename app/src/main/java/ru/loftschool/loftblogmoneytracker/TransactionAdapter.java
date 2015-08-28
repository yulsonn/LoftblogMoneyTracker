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

    //applying ViewHolder Design Pattern to cache data into the holder and reduce number of findViewById() calling
    static class ViewHolderItem {
        TextView textTitle;
        TextView sumTitle;
        TextView dateTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = getItem(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        int[] colors = {Color.parseColor("#81D4FA"), Color.parseColor("#EEFF41"), Color.parseColor("#B2FF59"), Color.parseColor("#FF8A80"), Color.parseColor("#EA80FC")};
        ViewHolderItem viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            // cache view fields into the holder
            viewHolder = new ViewHolderItem();
            viewHolder.textTitle = (TextView) convertView.findViewById(R.id.name_text);
            viewHolder.dateTitle = (TextView) convertView.findViewById(R.id.date_text);
            viewHolder.sumTitle = (TextView) convertView.findViewById(R.id.sum_text);
            // store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        if (transaction != null) {
            viewHolder.textTitle.setText(transaction.getTitle());
            viewHolder.dateTitle.setText(dateFormat.format(transaction.getDate()));
            viewHolder.sumTitle.setText(Integer.toString(transaction.getSum()));
            viewHolder.textTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
            viewHolder.dateTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
            viewHolder.sumTitle.setBackgroundColor(colors[new Random(System.currentTimeMillis()).nextInt(colors.length)]);
        }
        return convertView;
    }
}
