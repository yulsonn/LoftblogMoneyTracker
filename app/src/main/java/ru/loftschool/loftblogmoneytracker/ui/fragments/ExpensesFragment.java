package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.app.Dialog;
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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.adapters.ExpensesAdapter;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.services.DataLoadService_;
import ru.loftschool.loftblogmoneytracker.ui.activities.AddExpenseActivity_;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity;
import ru.loftschool.loftblogmoneytracker.ui.dialogs.DatePickerFragment;
import ru.loftschool.loftblogmoneytracker.utils.TextInputValidator;
import ru.loftschool.loftblogmoneytracker.utils.date.DateConvertUtils;
import ru.loftschool.loftblogmoneytracker.utils.date.DateFormats;

@EFragment(R.layout.fragment_expenses)
@OptionsMenu(R.menu.search_menu)
public class ExpensesFragment extends Fragment implements DateFormats{

    private static final String TAG = ExpensesFragment.class.getSimpleName();
    private static final String FILTER_ID = "filter_id";

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private static ExpensesAdapter adapter;
    private Bundle savedSelectedItems;

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

    @StringRes(R.string.edit_expense_changed)
    String expenseChanged;

    @OptionsMenuItem(R.id.search_action)
    MenuItem menuItem;

    @Bean
    TextInputValidator validator;

    public static ExpensesAdapter getAdapter() {
        return adapter;
    }

    public ActionModeCallback getActionModeCallback() {
        return actionModeCallback;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
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
                DataLoadService_.intent(getContext()).start();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ExpensesAdapter());

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItem.setVisible(true);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_label));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit() called with: " + "query = [" + query + "]");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange() called with: " + "newText = [" + newText + "]");
                BackgroundExecutor.cancelAll(FILTER_ID, true);
                delayedSearch(newText);
                return false;
            }
        });
    }

    @Background(delay = 700, id = FILTER_ID)
    void delayedSearch(String filter) {
        loadData(filter);
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
        loadData("");
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

    private void loadData(final String filter) {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Expenses>>() {
            @Override
            public Loader<List<Expenses>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Expenses>> loader = new AsyncTaskLoader<List<Expenses>>(getActivity()) {

                    @Override
                    public List<Expenses> loadInBackground() {
                        return getDataList(filter);
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
                adapter = new ExpensesAdapter(data, new ExpensesAdapter.CardViewHolder.ClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        if (MainActivity.getActionMode() != null) {
                            toggleSelection(position);
                        } else {
                            editExpenseDialog(position);
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

    private void editExpenseDialog(final int position) {
        final Expenses expense = adapter.getExpense(position);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_expense);

        final EditText etPrice = (EditText) dialog.findViewById(R.id.edit_expense_etPrice);
        final EditText etName = (EditText) dialog.findViewById(R.id.edit_expense_etName);
        final Spinner spCategories = (Spinner) dialog.findViewById(R.id.edit_expense_spCategories);
        final EditText etDate = (EditText) dialog.findViewById(R.id.edit_expense_etDate);

        ArrayAdapter<Categories> categoriesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Categories.selectAll());
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoriesAdapter);
        int spinnerPosition = categoriesAdapter.getPosition(expense.category);

        etPrice.setText(String.valueOf(expense.price));
        etName.setText(expense.name);
        spCategories.setSelection(spinnerPosition);
        etDate.setText(DateConvertUtils.dateToString(expense.date, DEFAULT_FORMAT));

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        etDate.setText(DateConvertUtils.dateToString(dateCalendar.getTimeInMillis(), DEFAULT_FORMAT));
                    }
                };
                datePicker.show(getActivity().getSupportFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        Button okButton = (Button) dialog.findViewById(R.id.edit_expense_btn_ok);
        Button cancelButton = (Button) dialog.findViewById(R.id.edit_expense_btn_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator.validateNewExpense(etPrice, etName, getActivity())) {

                    expense.name = etName.getText().toString();
                    expense.price = Float.parseFloat(etPrice.getText().toString());
                    expense.date = DateConvertUtils.stringToDate(etDate.getText().toString(), DEFAULT_FORMAT);
                    expense.category = (Categories)spCategories.getSelectedItem();

                    adapter.updateExpense(position, expense);

                    Toast.makeText(getActivity(), expenseChanged + expense.price + ", "
                            + expense.name + ", "
                            + DateConvertUtils.dateToString(new Date(), DEFAULT_FORMAT) + ", "
                            + expense.category.toString(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.AddCategoryDialogAnimation;
        dialog.show();
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

    private List<Expenses> getDataList(String filter){
        return new Select()
                .from(Expenses.class)
                .where("Name LIKE ?", new Object[]{'%' + filter + '%'})
                .orderBy("Date DESC")
                .execute();
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
