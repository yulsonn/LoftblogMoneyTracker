package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import ru.loftschool.loftblogmoneytracker.adapters.CategoriesAdapter;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;

@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends Fragment {

    @ViewById(R.id.recycler_view_content_categories)
    RecyclerView recyclerView;

    @StringRes(R.string.frag_title_categories)
    String title;

    @AfterViews
    void ready(){
        getActivity().setTitle(title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//        to avoid an error "recyclerview No adapter attached; skipping layout" set a blank adapter for the recyclerView
        recyclerView.setAdapter(new CategoriesAdapter());

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
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(1, null, new LoaderManager.LoaderCallbacks<List<Categories>>() {
            @Override
            public Loader<List<Categories>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Categories>> loader = new AsyncTaskLoader<List<Categories>>(getActivity()) {
                    @Override
                    public List<Categories> loadInBackground() {
                        return getDataList();
                    }
                };
                loader.forceLoad();

                return loader;
            }

            @Override
            public void onLoadFinished(Loader<List<Categories>> loader, List<Categories> data) {
                recyclerView.setAdapter(new CategoriesAdapter(getDataList()));
            }

            @Override
            public void onLoaderReset(Loader<List<Categories>> loader) {

            }
        });
    }

    private List<Categories> getDataList() {
        return new Select().from(Categories.class).execute();
    }
}