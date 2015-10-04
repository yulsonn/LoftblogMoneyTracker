package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryAddModel;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoryAddModelStatus;
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.SettingsFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.StatisticsFragment_;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @ViewById(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @ViewById(R.id.frame_container)
    View container;

    @ViewById(R.id.navigation_view)
    NavigationView navView;

    @ViewById
    Toolbar toolbar;

    @StringRes(R.string.error_unknown)
    String unknownError;

    @StringRes(R.string.error_unauthorized)
    String unauthorizedError;

    @OptionsItem(android.R.id.home)
    void settings(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @AfterViews
    void ready(){
        initToolbar();
        setupNavigationDrawer();
        initialCategoriesFill();
        addCategoriesToServer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() method called");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new ExpensesFragment_()).commit();
        }
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationDrawer(){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem){
        Fragment fragment;
        Bundle bundle = null;

        switch (menuItem.getItemId()){
            case R.id.drawer_item_expenses:
                fragment = new ExpensesFragment_();
                bundle = new Bundle();
                bundle.putBoolean("showSnackbar",true);
                break;
            case R.id.drawer_item_categories:
                fragment = new CategoriesFragment_();
                bundle = new Bundle();
                bundle.putBoolean("showSnackbar",true);
                break;
            case R.id.drawer_item_statistics:
                fragment = new StatisticsFragment_();
                break;
            case R.id.drawer_item_settings:
                fragment = new SettingsFragment_();
                break;
            default:
                fragment = new ExpensesFragment_();
                bundle = new Bundle();
                bundle.putBoolean("showSnackbar",true);
        }

        if(bundle != null){
            fragment.setArguments(bundle);
        } else {
            Snackbar.make(container, menuItem.getTitle() + " selected", Snackbar.LENGTH_SHORT).show();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume() method called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy() method called");
    }

    private void initialCategoriesFill() {
        if (new Select().from(Categories.class).execute().size() == 0) {
            new Categories("Social").save();
            new Categories("Fun").save();
            new Categories("Clothes").save();
            new Categories("Food").save();
        }
    }

    @Background
    void addCategoriesToServer() {
        RestService restService = new RestService();
        CategoryAddModel categoryAddResp = null;
        List<Categories> categories = new Select().from(Categories.class).execute();

        for (Categories category : categories) {
            try {
                categoryAddResp = restService.addCategory(category.name, MoneyTrackerApplication.getToken(this));
                if (CategoryAddModelStatus.STATUS_OK.equals(categoryAddResp.getStatus())) {
                    Log.e(LOG_TAG, "Category name: " + categoryAddResp.getData().getTitle() +
                                    ", Category id: " + categoryAddResp.getData().getId());
                } else {
                    Toast.makeText(this, unknownError, Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, unknownError);
                }
            } catch (UnauthorizedException e) {
                unauthorizedErrorReaction();
                break;
            }
        }
    }

    @UiThread
    void unauthorizedErrorReaction() {
        Toast.makeText(this, unauthorizedError, Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, CategoryAddModelStatus.STATUS_WRONG_TOKEN);
        goToLogin();
        finish();
    }

    @UiThread
    void goToLogin() {
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();
    }

    @Background
    public void logout(MenuItem item){
        goToLogin();
        MoneyTrackerApplication.setToken(this, MoneyTrackerApplication.DEFAULT_TOKEN_KEY);
    }
}
