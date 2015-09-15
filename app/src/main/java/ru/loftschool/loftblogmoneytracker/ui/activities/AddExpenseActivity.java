package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;

@EActivity(R.layout.activity_add_expense)
public class AddExpenseActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText etPrice, etName;

    @ViewById
    Spinner spCategories;

    @StringRes(R.string.act_title_add_expense)
    String title;

    @StringRes(R.string.error_null_price)
    String nullPriceError;

    @StringRes(R.string.error_null_name)
    String nullNameError;

    @StringRes(R.string.error_input_message)
    String errorMessage;

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    @OptionsItem(android.R.id.home)
    void back() {
        onBackPressed();
    }

    @AfterViews
    void ready() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);

        ArrayAdapter<Categories> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCategoriesList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(adapter);

//        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Position: " + position, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    @Click(R.id.add_expense_button)
    public void addExpenseButton() {

        if (inputValidation()) {
            new Expenses(etName.getText().toString(), etPrice.getText().toString(), String.valueOf(dateFormat.format(new Date())), (Categories)spCategories.getSelectedItem()).save();
            Toast.makeText(this, "Added: " + etPrice.getText().toString() + ", "
                                        + etName.getText().toString() + ", "
                                        + String.valueOf(dateFormat.format(new Date())) + ", "
                                        + spCategories.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean inputValidation() {

        boolean isValid = true;

        if (etPrice.getText().toString().trim().length() == 0) {
            etPrice.setError(nullPriceError);
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (etName.getText().toString().trim().length() == 0) {
            etName.setError(nullNameError);
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private List<Categories> getCategoriesList(){
        return new Select().from(Categories.class).execute();
    }
}
