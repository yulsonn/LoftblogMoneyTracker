package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;
import ru.loftschool.loftblogmoneytracker.ui.dialogs.DatePickerFragment;
import ru.loftschool.loftblogmoneytracker.ui.fragments.ExpensesFragment;
import ru.loftschool.loftblogmoneytracker.utils.TextInputValidator;

@EActivity(R.layout.activity_add_expense)
public class AddExpenseActivity extends AppCompatActivity {

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText etPrice, etName, etDate;

    @ViewById
    Spinner spCategories;

    @StringRes(R.string.act_title_add_expense)
    String title;

    @StringRes(R.string.error_null_price)
    String nullPriceError;

    @StringRes(R.string.expense_added_text)
    String expenseAdded;

    @Bean
    TextInputValidator validator;

    @Click(R.id.etDate)
    void dateChoose() {
        DatePickerFragment datePicker = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, monthOfYear, dayOfMonth);
                etDate.setText(dateFormat.format(dateCalendar.getTimeInMillis()));
            }
        };
        datePicker.show(getSupportFragmentManager(), DatePickerFragment.class.getSimpleName());
    }


    @OptionsItem(android.R.id.home)
    void back() {
        onBackPressed();
    }

    @AfterViews
    void ready() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        etDate.setText(dateFormat.format(new Date()));

        ArrayAdapter<Categories> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCategoriesList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(adapter);

    }

    @Click(R.id.add_expense_button)
    public void addExpenseButton() {

        if (validator.validateNewExpense(etPrice, etName, getBaseContext())) {
            Expenses newExpense = new Expenses(etName.getText().toString(), etPrice.getText().toString(), etDate.getText().toString(), (Categories)spCategories.getSelectedItem());
            ExpensesFragment.getAdapter().addExpense(newExpense);
            Toast.makeText(this, expenseAdded + newExpense.price + ", "
                    + newExpense.name + ", "
                    + String.valueOf(dateFormat.format(new Date())) + ", "
                    + newExpense.category.toString(), Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        }
    }

    private List<Categories> getCategoriesList(){
        return new Select().from(Categories.class).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
