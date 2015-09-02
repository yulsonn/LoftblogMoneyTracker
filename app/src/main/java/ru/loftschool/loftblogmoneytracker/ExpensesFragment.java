package ru.loftschool.loftblogmoneytracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
        final View view = inflater.inflate(R.layout.expenses_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_content);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        List<Expense> adapterData = getDataList();
        getActivity().setTitle(R.string.frag_title_expenses);
        expensesAdapter = new ExpensesAdapter(adapterData);
        recyclerView.setAdapter(expensesAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(recyclerView, "pressed", Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private List<Expense> getDataList(){
        List<Expense> data = new ArrayList<>();

        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        data.add(new Expense("Telephone", 2000, new Date()));
        data.add(new Expense("Internet", 3000, new Date()));
        data.add(new Expense("Food", 4000, new Date()));
        data.add(new Expense("Transport", 500, new Date()));
        return data;
    }
}
