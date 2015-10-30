package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
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

import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.adapters.CategoriesAdapter;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.ui.activities.MainActivity;
import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.TextInputValidator;

@EFragment(R.layout.fragment_categories)
@OptionsMenu(R.menu.search_menu)
public class CategoriesFragment extends Fragment {

    private static final String TAG = CategoriesFragment.class.getSimpleName();
    private static final String FILTER_ID = "filter_id";

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private static CategoriesAdapter adapter;
    private Bundle savedSelectedItems;

    @ViewById(R.id.recycler_view_content_categories)
    RecyclerView recyclerView;

    @ViewById(R.id.categories_fab)
    FloatingActionButton fab;

    @ViewById(R.id.swipe_refresh_expenses)
    SwipeRefreshLayout swipeRefreshLayout;

    @StringRes(R.string.frag_title_categories)
    String title;

    @StringRes(R.string.category_add_enter_category)
    String categoryEnter;

    @StringRes(R.string.category_add_added_text)
    String categoryAdded;

    @StringRes(R.string.category_remove_accept)
    String categoryRemoveAccept;

    @StringRes(R.string.category_remove_cancel)
    String categoryRemoveCancel;

    @StringRes(R.string.category_remove_dialog_title)
    String categoryRemoveTitle;

    @StringRes(R.string.category_remove_dialog_text)
    String categoryRemoveText;

    @StringRes(R.string.snackbar_undo)
    String undoText;

    @StringRes(R.string.snackbar_removed_category)
    String removedCategory;

    @StringRes(R.string.snackbar_removed_categories)
    String removedCategories;

    @Bean
    TextInputValidator validator;

    @Bean()
    ServerReqUtils serverRequest;

    @OptionsMenuItem(R.id.search_action)
    MenuItem menuItem;

    public static CategoriesAdapter getAdapter() {
        return adapter;
    }

    public ActionModeCallback getActionModeCallback() {
        return actionModeCallback;
    }

    @AfterViews
    void ready(){
        getActivity().setTitle(title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primaryDark, R.color.buttonAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData("");
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//        to avoid an error "recyclerview No adapter attached; skipping layout" set a blank adapter for the recyclerView
        recyclerView.setAdapter(new CategoriesAdapter());
    }

    @Click(R.id.categories_fab)
    void fab() {
        MainActivity.destroyActionModeIfNeeded();
        alertDialog();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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
        getLoaderManager().restartLoader(1, null, new LoaderManager.LoaderCallbacks<List<Categories>>() {
            @Override
            public Loader<List<Categories>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Categories>> loader = new AsyncTaskLoader<List<Categories>>(getActivity()) {
                    @Override
                    public List<Categories> loadInBackground() {
                        return getDataList(filter);
                    }
                };
                loader.forceLoad();

                return loader;
            }

            @Override
            public void onLoadFinished(Loader<List<Categories>> loader, List<Categories> data) {
                swipeRefreshLayout.setRefreshing(false);
                SparseBooleanArray savedCurrentSelectedItems = null;
                if (adapter != null) {
                    savedCurrentSelectedItems = adapter.getSparseBooleanSelectedItems();
                }
                adapter = new CategoriesAdapter(data, new CategoriesAdapter.CardViewCategoryHolder.ClickListener() {
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
            public void onLoaderReset(Loader<List<Categories>> loader) {

            }
        });
    }

    private void alertDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_category);
        final EditText editText = (EditText) dialog.findViewById(R.id.new_category_name);
        final TextInputLayout categoryWrapper = (TextInputLayout) dialog.findViewById(R.id.categoryWrapper);
        final Button okButton = (Button) dialog.findViewById(R.id.btn_ok);
        final Editable text = editText.getText();

        categoryWrapper.setHint(categoryEnter);
        Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator.validateCategoryName(text.toString(), categoryWrapper, getContext())) {
                    Categories newCategory = new Categories(text.toString());
                    Categories addedCategory = adapter.addCategory(newCategory);
                    Toast.makeText(getActivity(), categoryAdded + newCategory.name, Toast.LENGTH_SHORT).show();
                    serverRequest.addCategoryToServer(addedCategory);
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

    private List<Categories> getDataList(String filter) {
        return new Select()
                .from(Categories.class)
                .where("Name LIKE ?", new Object[]{'%' + filter + '%'})
                .execute();
    }


    private void undoSnackbarShow() {
        Snackbar snackbar = Snackbar.make(recyclerView, adapter.getSelectedItemsCount() <= 1 ? removedCategory : removedCategories, Snackbar.LENGTH_LONG)
                .setAction(undoText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.restoreRemovedItems();
                    }
                });
                snackbar.show();
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
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_remove:
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
                            .setPositiveButton(categoryRemoveAccept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Accept action
                                    adapter.removeItems(adapter.getSelectedItems());
                                    undoSnackbarShow();
                                    mode.finish();
                                }
                            })
                            .setNegativeButton(categoryRemoveCancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Cancel action
                                    dialogInterface.dismiss();
                                }
                            })
                            .setTitle(categoryRemoveTitle)
                            .setMessage(categoryRemoveText)
                            .create();
                    alertDialog.getWindow().setWindowAnimations(R.style.AlertDialogAnimation);
                    alertDialog.show();

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