package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoriesStatus;
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.SettingsFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.StatisticsFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.StatisticsFragment_;
import ru.loftschool.loftblogmoneytracker.utils.ServerReqUtils;
import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;
import ru.loftschool.loftblogmoneytracker.utils.network.NetworkConnectionChecker;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    public static ActionMode actionMode;

    private ActionBarDrawerToggle mDrawerToggle;

    private SparseBooleanArray currentSelectedItems;

    @ViewById(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @ViewById(R.id.frame_container)
    View container;

    @ViewById(R.id.navigation_view)
    NavigationView navView;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.avatar)
    ImageView avatar;

    @ViewById(R.id.drawer_username)
    TextView userName;

    @ViewById(R.id.drawer_email)
    TextView email;

    @StringRes(R.string.error_unknown)
    String unknownError;

    @StringRes(R.string.error_unauthorized)
    String unauthorizedError;

    @StringArrayRes(R.array.initial_categories)
    String[] initCategories;

    @Bean
    ServerReqUtils serverRequest;

    @OptionsItem(android.R.id.home)
    void settings(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @AfterViews
    void ready(){
        initToolbar();
        setupNavigationDrawer();

       /* methods for testing rest-queries: */

        /* 1. add new category - OK */
        //addCategoriesToServer();

        /* 2. edit category - OK */
        //editCategoryOnServer();

        /* 3. get all categories info - OK */
        //getAllCategories();

        /* 4. get one category with expenses info - OK
        * bug: returns List<Object> instead of one Object*/
        //getCategoryInfo();

        /* 5. get all expenses info - OK */
        //getAllExpenses();

        /* 6. get one expense info - OK */
        //addExpense();

        /* 7. get all categories with expenses info - OK*/
        //getAllCategoriesInfo();

        /* 8. get balance / set balance - OK*/
        //balanceTest();

        /* 9. remove category - FAIL */
        //deleteCategory();

        /* 10. categories synch - OK */
        //categoriesSync();

        /* 11. expenses synch - ? */
        //expensesSync();

        initDrawerHeaderWithGoogleAccInfo();
    }

    public static ActionMode getActionMode() {
        return actionMode;
    }

    public static void setActionMode(ActionMode actionMode) {
        MainActivity.actionMode = actionMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() method called");
        initialCategoriesFill();
        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fm.beginTransaction().replace(R.id.frame_container, new ExpensesFragment_(), ExpensesFragment_.class.getSimpleName())
                    .addToBackStack(ExpensesFragment_.class.getSimpleName())
                    .commit();
        }

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
            }
        });
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
                destroyActionModeIfNeeded();
                menuItem.setChecked(true);
                selectDrawerItem(menuItem);
                return true;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if(newState == DrawerLayout.STATE_DRAGGING){
                    if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                        //closing drawer
                        if (currentSelectedItems != null) {
                            startActionMode();
                        }
                    } else {
                        //opening drawer
                        if (actionMode != null) {
                            saveAndStopActionMode();
                        }
                    }
                }
            }
        };

        // set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    // saving selected items and finishing Action Mode
    private void saveAndStopActionMode() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof CategoriesFragment) {
            currentSelectedItems = CategoriesFragment.getAdapter().getSparseBooleanSelectedItems().clone();
        } else if (currentFragment instanceof ExpensesFragment) {
            currentSelectedItems = ExpensesFragment.getAdapter().getSparseBooleanSelectedItems().clone();
        }
        actionMode.finish();
    }

    // starting Action Mode and restoring selected items
    private void startActionMode() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof CategoriesFragment) {
            CategoriesFragment.getAdapter().setSelectedItems(currentSelectedItems.clone());
            CategoriesFragment.getAdapter().notifyDataSetChanged();
            actionMode = startSupportActionMode(((CategoriesFragment) currentFragment).getActionModeCallback());
            actionMode.setTitle(String.valueOf(CategoriesFragment.getAdapter().getSelectedItemsCount()));
        } else if (currentFragment instanceof ExpensesFragment) {
            ExpensesFragment.getAdapter().setSelectedItems(currentSelectedItems.clone());
            ExpensesFragment.getAdapter().notifyDataSetChanged();
            actionMode = startSupportActionMode(((ExpensesFragment) currentFragment).getActionModeCallback());
            actionMode.setTitle(String.valueOf(ExpensesFragment.getAdapter().getSelectedItemsCount()));
        }
        currentSelectedItems = null;
    }

    private void selectDrawerItem(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.drawer_item_expenses:
                replaceFragment(new ExpensesFragment_());
                break;
            case R.id.drawer_item_categories:
                replaceFragment(new CategoriesFragment_());
                break;
            case R.id.drawer_item_statistics:
                replaceFragment(new StatisticsFragment_());
                break;
            case R.id.drawer_item_settings:
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new SettingsFragment(), SettingsFragment.class.getSimpleName())
                        .addToBackStack(SettingsFragment.class.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                break;
            case R.id.drawer_item_logout:
                serverRequest.logout();
                goToLogin();
                MoneyTrackerApplication.setToken(this, TokenKeyStorage.DEFAULT_TOKEN_KEY);
                MoneyTrackerApplication.setGoogleToken(this, TokenKeyStorage.DEFAULT_TOKEN_GOOGLE_KEY);
                break;
        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPooped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPooped && fragmentManager.findFragmentByTag(backStateName) == null) {
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, backStateName)
                    .addToBackStack(backStateName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (currentSelectedItems != null) {
                startActionMode();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                super.onBackPressed();
                navView.setCheckedItem(R.id.drawer_item_expenses);
            } else {
                super.onBackPressed();
                getLastFragmentChecked();
            }
        }
    }

    private void getLastFragmentChecked() {
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
        String str = backEntry.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(str);
        updateSelectedItem(fragment);
    }

    private void updateSelectedItem(Fragment fragment) {
        if (fragment instanceof ExpensesFragment) {
            navView.setCheckedItem(R.id.drawer_item_expenses);
        } else if (fragment instanceof CategoriesFragment) {
            navView.setCheckedItem(R.id.drawer_item_categories);
        } else if (fragment instanceof StatisticsFragment) {
            navView.setCheckedItem(R.id.drawer_item_statistics);
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
            for (int i = 0; i < initCategories.length; i++) {
                Categories category = new Categories(initCategories[i]);
                category.save();
                serverRequest.addCategoryToServer(category);
            }
        }
    }



    @UiThread
    void unauthorizedErrorReaction() {
        Toast.makeText(this, unauthorizedError, Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, CategoriesStatus.STATUS_WRONG_TOKEN);
        goToLogin();
        finish();
    }

    @UiThread
    void unknownErrorReaction() {
        Toast.makeText(this, unknownError, Toast.LENGTH_LONG).show();
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
        MoneyTrackerApplication.setToken(this, TokenKeyStorage.DEFAULT_TOKEN_KEY);
    }

    void initDrawerHeaderWithGoogleAccInfo() {
        if (NetworkConnectionChecker.isNetworkConnected(this)
                && !TokenKeyStorage.DEFAULT_TOKEN_GOOGLE_KEY.equalsIgnoreCase(MoneyTrackerApplication.getGoogleToken(this))) {
            getGoogleAccountData();
        }
    }

    @Background
    void getGoogleAccountData() {
        RestService restService = new RestService();
        GoogleAccountDataModel gAccountData = restService.getGoogleAccountData(MoneyTrackerApplication.getGoogleToken(this));
        if (gAccountData != null) {
            setDrawerGoogleAccountData(gAccountData);
        }
    }

    @UiThread
    void setDrawerGoogleAccountData(GoogleAccountDataModel gAccountData) {
        Picasso.with(this).load(gAccountData.getPicture()).into(avatar);
        userName.setText(gAccountData.getName());
        email.setText(gAccountData.getEmail());
    }

    public static void destroyActionModeIfNeeded() {
        if (actionMode != null) {
            actionMode.finish();
            Log.e("ActionMode", "FINISH");
        }
    }
}
