package ru.loftschool.loftblogmoneytracker;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.categories_listview);
        ArrayList<String> adapterData = getDataList();
        getActivity().setTitle(R.string.frag_title_categories);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.categories_list_item, adapterData);
        listView.setAdapter(adapter);


        Bundle args = getArguments();
        if (args != null){
            Boolean showSnackbar = args.getBoolean("showSnackbar");
            if (showSnackbar){
                Snackbar.make(listView, getActivity().getTitle() + " selected", Snackbar.LENGTH_SHORT).show();
            }
            args.clear();
        }
        return view;
    }

    private ArrayList<String> getDataList(){
        ArrayList<String> data = new ArrayList<>();
        data.add("Telephone");
        data.add("Transport");
        return data;
    }
}