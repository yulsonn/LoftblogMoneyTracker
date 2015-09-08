package ru.loftschool.loftblogmoneytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpensesFragment extends Fragment {

    private ExpensesAdapter expensesAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.frag_title_expenses);
        final View view = inflater.inflate(R.layout.expenses_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_content);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        List<Expense> adapterData = getDataList();
        expensesAdapter = new ExpensesAdapter(adapterData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(expensesAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openActivityIntent = new Intent(getActivity(),AddExpenseActivity_.class);
                getActivity().startActivity(openActivityIntent);
            }
        });

        Bundle args = getArguments();
        if (args != null){
            Boolean showSnackbar = args.getBoolean("showSnackbar");
            if (showSnackbar){
                Snackbar.make(recyclerView, getActivity().getTitle() + " selected", Snackbar.LENGTH_SHORT).show();
            }
            args.clear();
        }
        return view;
    }

    private List<Expense> getDataList(){
        List<Expense> data = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            data.add(new Expense("Telephone", 2000, new Date()));
            data.add(new Expense("Internet", 3000, new Date()));
            data.add(new Expense("Food", 4000, new Date()));
            data.add(new Expense("Transport", 500, new Date()));
        }
        return data;
    }
}
