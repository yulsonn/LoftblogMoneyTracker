package ru.loftschool.loftblogmoneytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpensesFragment extends Fragment {

    private ListView listView;
    private TransactionAdapter transactionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expenses_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.main_listview);
        List<Transaction> adapterData = getDataList();
        getActivity().setTitle(R.string.frag_title_expenses);
        transactionAdapter = new TransactionAdapter(getActivity(), adapterData);
        listView.setAdapter(transactionAdapter);
        return view;
    }

    private List<Transaction> getDataList(){
        List<Transaction> data = new ArrayList<>();

        data.add(new Transaction("Telephone", 2000, new Date()));
        data.add(new Transaction("Internet", 3000, new Date()));
        data.add(new Transaction("Food", 4000, new Date()));
        data.add(new Transaction("Transport", 500, new Date()));
        return data;
    }
}