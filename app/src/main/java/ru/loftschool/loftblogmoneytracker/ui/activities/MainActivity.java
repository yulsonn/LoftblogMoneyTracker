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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.MoneyTrackerApplication;
import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
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
import ru.loftschool.loftblogmoneytracker.ui.fragments.CategoriesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.SettingsFragment_;
import ru.loftschool.loftblogmoneytracker.ui.fragments.StatisticsFragment_;
import ru.loftschool.loftblogmoneytracker.utils.NetworkConnectionChecker;
import ru.loftschool.loftblogmoneytracker.utils.TokenKeyStorage;

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

    @OptionsItem(android.R.id.home)
    void settings(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @AfterViews
    void ready(){
        initToolbar();
        setupNavigationDrawer();
        initialCategoriesFill();

        // methods for testing rest-queries:

        //addCategoriesToServer();
        editCategoryOnServer();
        getAllCategories();
        getAllExpenses();
        //addExpense();
        //getCategoryInfo();
        //getAllCategoriesInfo();
        balanceTest();

        initDrawerHeaderWithGoogleAccInfo();
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
            new Categories("Transport").save();

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
        // hardcode just for test
        AllExpensesModel addExpenseResp = restService.addExpense("100", "TestExpense Cafe", 1084, new SimpleDateFormat("yyyy/MM/dd").format(new Date()), MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
        if (ExpensesStatus.STATUS_OK.equals(addExpenseResp.getStatus())) {
            Log.e(LOG_TAG, "Expense id: " + addExpenseResp.getId());
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
            CategoryModel categoryEditResp
                    = restService.editCategory("Edited category " + new SimpleDateFormat("HH:mm:ss").format(new Date()),
                    1354, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
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
            CategoryWithExpensesModel expensesResp = restService.getCategoryWithExpenses(1084, MoneyTrackerApplication.getGoogleToken(this), MoneyTrackerApplication.getToken(this));
            Log.e(LOG_TAG, " * Category id: " + expensesResp.getId() +
                            " * Category name: " + expensesResp.getTitle() +
                            " * Transactions: ");
                for (ExpenseDetails expense : expensesResp.getTransactions()) {
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
            Log.e(LOG_TAG, " | Category id: " + expensesResp.get(1).getId() +
                    " | Category name: " + expensesResp.get(1).getTitle() +
                    " | Transactions: ");
            for (ExpenseDetails expense : expensesResp.get(1).getTransactions()) {
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
}
