package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    @ViewById(R.id.recycler_view_content_expenses)
    RecyclerView recyclerView;

    @ViewById(R.id.fab)
    FloatingActionButton floatingActionButton;

    @ViewById(R.id.swipe_refresh_expenses)
    SwipeRefreshLayout swipeRefreshLayout;

    @StringRes(R.string.frag_title_expenses)
    String title;

    @StringRes(R.string.snackbar_undo)
    String undoText;

    @StringRes(R.string.snackbar_removed_expense)
    String removedExpense;

    @StringRes(R.string.snackbar_removed_expenses)
    String removedExpenses;

    private static ExpensesAdapter adapter;

    private Bundle savedSelectedItems;

    public static ExpensesAdapter getAdapter() {
        return adapter;
    }

    public ActionModeCallback getActionModeCallback() {
        return actionModeCallback;
    }

    @Click
    void fab() {
        MainActivity.destroyActionModeIfNeeded();
        Intent openActivityIntent = new Intent(getActivity(), AddExpenseActivity_.class);
        getActivity().startActivity(openActivityIntent);
        getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primaryDark, R.color.buttonAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//      to avoid an error "recyclerview No adapter attached; skipping layout" set a blank adapter for the recyclerView
        recyclerView.setAdapter(new ExpensesAdapter());

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
        loadData();
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                MainActivity.destroyActionModeIfNeeded();
                adapter.removeItem(viewHolder.getAdapterPosition());
                undoSnackbarShow();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadData() {
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
                swipeRefreshLayout.setRefreshing(false);
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

    private void undoSnackbarShow() {
        Snackbar.make(recyclerView, adapter.getSelectedItemsCount() <= 1 ? removedExpense : removedExpenses, Snackbar.LENGTH_LONG)
                .setAction(undoText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.restoreRemovedItems();
                    }
                })
                .show();
        adapter.startUndoTimer(3500);
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
                    undoSnackbarShow();
                    mode.finish();
                    return true;
                case R.id.menu_select_all:
                    adapter.selectAll((adapter.getItemCount()));
                    MainActivity.getActionMode().setTitle(String.valueOf(adapter.getSelectedItemsCount()));
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
