package ru.loftschool.loftblogmoneytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private ListView listView;
    private List<Transaction> data = new ArrayList<>();
    private TransactionAdapter transactionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment, container,false);
        listView = (ListView) view.findViewById(R.id.main_listview);
        List<Transaction> adapterData = getDataList();
        getActivity().setTitle("Second fragment");
        transactionAdapter = new TransactionAdapter(getActivity(), adapterData);
        listView.setAdapter(transactionAdapter);
        return view;
    }

    private List<Transaction> getDataList(){
        data.add(new Transaction("Telephone", "2000"));
        data.add(new Transaction("Internet", "3000"));
        data.add(new Transaction("Food", "4000"));
        data.add(new Transaction("Transport", "5000"));
        data.add(new Transaction("Telephone", "2000"));
        data.add(new Transaction("Internet", "3000"));
        data.add(new Transaction("Food", "4000"));
        data.add(new Transaction("Transport", "5000"));
        return data;
    }
}
