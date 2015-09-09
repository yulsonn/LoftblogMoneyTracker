package ru.loftschool.loftblogmoneytracker;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

@EFragment(R.layout.categories_fragment)
public class CategoriesFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    @ViewById(R.id.categories_listview)
    ListView listView;

    @StringRes(R.string.frag_title_categories)
    String title;

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
        ArrayList<String> adapterData = getDataList();
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
    }

    private ArrayList<String> getDataList(){
        ArrayList<String> data = new ArrayList<>();
        data.add("Telephone");
        data.add("Transport");
        return data;
    }
}