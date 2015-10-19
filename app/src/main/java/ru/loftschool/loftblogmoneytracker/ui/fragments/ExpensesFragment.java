package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.adapters.ExpensesAdapter;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.ui.activities.AddExpenseActivity_;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity;

@EFragment(R.layout.fragment_expenses)
public class ExpensesFragment extends Fragment {

    public ActionModeCallback getActionModeCallback() {
        return actionModeCallback;
    }

    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    @ViewById(R.id.recycler_view_content_expenses)
    RecyclerView recyclerView;

    @ViewById(R.id.fab)
    FloatingActionButton floatingActionButton;

    @StringRes(R.string.frag_title_expenses)
    String title;

    private static ExpensesAdapter adapter;

    private Bundle savedSelectedItems;

    public static ExpensesAdapter getAdapter() {
        return adapter;
    }

    @Click
    void fab() {
        MainActivity.destroyActionModeIfNeeded();
        Intent openActivityIntent = new Intent(getActivity(), AddExpenseActivity_.class);
        getActivity().startActivity(openActivityIntent);
    }

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//      to avoid an error "recyclerview No adapter attached; skipping layout" set a blank adapter for the recyclerView
        recyclerView.setAdapter(new ExpensesAdapter());

        Bundle args = getArguments();
        if (args != null){
            Boolean showSnackbar = args.getBoolean("showSnackbar");
            if (showSnackbar){
                Snackbar.make(recyclerView, getActivity().getTitle() + " selected", Snackbar.LENGTH_SHORT).show();
            }
            args.clear();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedSelectedItems = savedInstanceState;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Expenses>>() {
            @Override
            public Loader<List<Expenses>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Expenses>> loader = new AsyncTaskLoader<List<Expenses>>(getActivity()) {

                    @Override
                    public List<Expenses> loadInBackground() {
                        return getDataList();
                    }
                };
                loader.forceLoad();

                return loader;
            }

            @Override
            public void onLoadFinished(Loader<List<Expenses>> loader, List<Expenses> data) {
                SparseBooleanArray savedCurrentSelectedItems = null;
                if (adapter != null) {
                    savedCurrentSelectedItems = adapter.getSparseBooleanSelectedItems();
                }
                    adapter = new ExpensesAdapter(getDataList(), new ExpensesAdapter.CardViewHolder.ClickListener() {
                        @Override
                        public void onItemClicked(int position) {
                            if (MainActivity.getActionMode() != null) {
                                toggleSelection(position);
                            }
                        }

                        @Override
                        public boolean onItemLongClicked(int position) {
                            if (MainActivity.getActionMode() == null) {
                                AppCompatActivity activity = (AppCompatActivity) getActivity();
                                MainActivity.setActionMode(activity.startSupportActionMode(actionModeCallback));
                            }
                            toggleSelection(position);
                            return true;
                        }
                    });
                if (savedCurrentSelectedItems != null) {
                    adapter.setSelectedItems(savedCurrentSelectedItems);
                }

                if (savedSelectedItems != null) {
                    adapter.onRestoreInstanceState(savedSelectedItems);
                    savedSelectedItems = null;
                }
                if (adapter.getSelectedItemsCount() > 0) {
                    if (MainActivity.getActionMode() == null) {
                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        MainActivity.setActionMode(activity.startSupportActionMode(actionModeCallback));
                    }
                    MainActivity.getActionMode().setTitle(String.valueOf(adapter.getSelectedItemsCount()));
                } else if (adapter.getSelectedItemsCount() == 0 && MainActivity.getActionMode() != null) {
                    MainActivity.getActionMode().finish();
                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onLoaderReset(Loader<List<Expenses>> loader) {
            }
        });
    }

    private void toggleSelection(int position){
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemsCount();
        if (count == 0) {
            MainActivity.getActionMode().finish();
        } else {
            MainActivity.getActionMode().setTitle(String.valueOf(count));
            MainActivity.getActionMode().invalidate();
        }
    }

    private List<Expenses> getDataList(){
        return new Select().from(Expenses.class).execute();
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_remove:
                    adapter.removeItems(adapter.getSelectedItems());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            MainActivity.setActionMode(null);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            adapter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.actionMode != null) {
            MainActivity.destroyActionModeIfNeeded();
        }
    }
}
