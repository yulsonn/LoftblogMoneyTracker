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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.rest.RestService;
import ru.loftschool.loftblogmoneytracker.rest.exception.UnauthorizedException;
import ru.loftschool.loftblogmoneytracker.rest.models.AllCategoriesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.AllExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.BalanceModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryModel;
import ru.loftschool.loftblogmoneytracker.rest.models.CategoryWithExpensesModel;
import ru.loftschool.loftblogmoneytracker.rest.models.ExpenseDetails;
import ru.loftschool.loftblogmoneytracker.rest.models.GoogleAccountDataModel;
import ru.loftschool.loftblogmoneytracker.rest.status.CategoriesStatus;
import ru.loftschool.loftblogmoneytracker.rest.status.ExpensesStatus;
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.SettingsFragment;
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
            for (int i = 0; i < initCategories.length; i++) {
                Categories category = new Categories(initCategories[i]);
                category.save();
                serverRequest.addCategoryToServer(category);
            }
        }
    }

    @Background
    void addCategoriesToServer() {
        RestService restService = new RestService();
        CategoryModel categoryAddResp = null;
        List<Categories> categories = new Select().from(Categories.class).execute();

        if (!categories.isEmpty()) {
            for (Categories category : categories) {
                try {
                    categoryAddResp = restService.addCategory(category.name, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
                    if (CategoriesStatus.STATUS_OK.equals(categoryAddResp.getStatus())) {
                        Log.e(LOG_TAG, "Category name: " + categoryAddResp.getData().getTitle() +
                                ", Category id: " + categoryAddResp.getData().getId());
                        category.sId = categoryAddResp.getData().getId();
                        category.save();
                        Log.e(LOG_TAG, "New category id: " + category.sId);

                    } else {
                        unknownErrorReaction();
                        Log.e(LOG_TAG, unknownError);
                    }
                } catch (UnauthorizedException e) {
                    unauthorizedErrorReaction();
                    break;
                }
            }
        }
    }

    @Background
    void addExpense() {
        RestService restService = new RestService();
        List<Expenses> expenses = Expenses.selectAll();
        Expenses expense = expenses.get(1);
        Log.e(LOG_TAG, "Old expense sId: " + expense.sId);
        // hardcode just for test
        AllExpensesModel addExpenseResp = restService.addExpense(String.valueOf(expense.price), expense.name, expense.category.sId, new SimpleDateFormat("yyyy/MM/dd").format(new Date()), MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
        if (ExpensesStatus.STATUS_OK.equals(addExpenseResp.getStatus())) {
            Log.e(LOG_TAG, "Expense id: " + addExpenseResp.getId());
            expense.sId = addExpenseResp.getId();
            expense.save();
            Log.e(LOG_TAG, "New expense sId: " + expense.sId);
        } else {
            unknownErrorReaction();
            Log.e(LOG_TAG, unknownError);
        }
    }

    @Background
    void editCategoryOnServer() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            //time added for test
            Categories category = Categories.selectCategoryById(1935);

            CategoryModel categoryEditResp
                    = restService.editCategory("Edited category " + new SimpleDateFormat("HH:mm:ss").format(new Date()),
                    category.sId, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if (CategoriesStatus.STATUS_OK.equals(categoryEditResp.getStatus())) {
                Log.e(LOG_TAG, "Category edited name: " + categoryEditResp.getData().getTitle() +
                        ", Category id: " + categoryEditResp.getData().getId());
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void getAllCategories() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            //time added for test
            AllCategoriesModel categoriesResp = restService.getAllCategories(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if (CategoriesStatus.STATUS_OK.equals(categoriesResp.getStatus())) {
                for (CategoryDetails category : categoriesResp.getCategories()) {
                    Log.e(LOG_TAG, "Category name: " + category.getTitle() +
                            ", Category id: " + category.getId());
                }
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void getCategoryInfo() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            List<CategoryWithExpensesModel> expensesResp = restService.getCategoryWithExpenses(1935, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            Log.e(LOG_TAG, " * Category id: " + expensesResp.get(0).getId() +
                            " * Category name: " + expensesResp.get(0).getTitle() +
                            " * Transactions: ");
                for (ExpenseDetails expense : expensesResp.get(0).getTransactions()) {
                    Log.e(LOG_TAG, "  **  Expense id: " + expense.getId() +
                            "  **  , Expense category id: " + expense.getCategoryId() +
                            "  **  , Expense comment: " + expense.getComment() +
                            "  **  , Expense summ: " + expense.getSum() +
                            "  **  , Expense date: " + expense.getTrDate());
                }
        }
    }

    @Background
    void getAllCategoriesInfo() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            ArrayList<CategoryWithExpensesModel> expensesResp = restService.getAllCategoriesWithExpenses(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            Log.e(LOG_TAG, " | Category id: " + expensesResp.get(expensesResp.size()-1).getId() +
                    " | Category name: " + expensesResp.get(expensesResp.size()-1).getTitle() +
                    " | Transactions: ");
            for (ExpenseDetails expense : expensesResp.get(expensesResp.size()-1).getTransactions()) {
                Log.e(LOG_TAG, "  **  Expense id: " + expense.getId() +
                        "  ||  , Expense category id: " + expense.getCategoryId() +
                        "  ||  , Expense comment: " + expense.getComment() +
                        "  ||  , Expense summ: " + expense.getSum() +
                        "  ||  , Expense date: " + expense.getTrDate());
            }
        }
    }

    @Background
    void getAllExpenses() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            AllExpensesModel expensesResp = restService.getAllExpenses(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if (CategoriesStatus.STATUS_OK.equals(expensesResp.getStatus())) {
                for (ExpenseDetails expense : expensesResp.getExpenses()) {
                    Log.e(LOG_TAG, "Expense id: " + expense.getId() +
                                    ", Expense category id: " + expense.getCategoryId() +
                                    ", Expense comment: " + expense.getComment() +
                                    ", Expense summ: " + expense.getSum() +
                                    ", Expense date: " + expense.getTrDate());
                }
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void deleteCategory() {
        RestService restService = new RestService();
        Categories category = Categories.selectCategoryById(1935);
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            CategoryModel removeCaatResp = restService.deleteCategory(category.sId, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if (CategoriesStatus.STATUS_OK.equals(removeCaatResp.getStatus())) {
                Log.e(LOG_TAG, category.sId + " category removed");
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }
        }
    }

    @Background
    void balanceTest() {
        RestService restService = new RestService();
        if (NetworkConnectionChecker.isNetworkConnected(this)) {
            BalanceModel balanceResp = restService.getBalance(MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if ("success".equalsIgnoreCase(balanceResp.getStatus())) {
                Log.e(LOG_TAG, "Old balance: " + balanceResp.getBalance());
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
            }

            balanceResp = restService.setBalance("7777", MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            if ("success".equalsIgnoreCase(balanceResp.getStatus())) {
                Log.e(LOG_TAG, "New balance: " + balanceResp.getBalance());
            } else {
                unknownErrorReaction();
                Log.e(LOG_TAG, unknownError);
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
