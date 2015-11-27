package ru.loftschool.loftblogmoneytracker.ui.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ru.loftschool.loftblogmoneytracker.R;


public class TrackerPreferenceActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_with_actionbar);
        String title = getResources().getString(R.string.act_title_settings);
        setTitle(title);
        initToolbar();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new TrackerPreferenceFragment()).commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    public static class TrackerPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        private CheckBoxPreference mainChkbox;
        private CheckBoxPreference vibroChkbox;
        private CheckBoxPreference ledChkbox;
        private CheckBoxPreference soundChkbox;

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().setTitle(getResources().getString(R.string.act_title_settings));

            addPreferencesFromResource(R.xml.pref_general);
            mainChkbox = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_notifications_key));
            vibroChkbox = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_vibro_key));
            ledChkbox = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_led_key));
            soundChkbox = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_sound_key));

            vibroChkbox.setEnabled(mainChkbox.isChecked());
            ledChkbox.setEnabled(mainChkbox.isChecked());
            soundChkbox.setEnabled(mainChkbox.isChecked());

            mainChkbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    vibroChkbox.setEnabled(mainChkbox.isChecked());
                    ledChkbox.setEnabled(mainChkbox.isChecked());
                    soundChkbox.setEnabled(mainChkbox.isChecked());
                    return true;
                }
            });
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_frequency_of_updates_key)));
            view.setBackgroundColor(getResources().getColor(R.color.background));
            super.onViewCreated(view, savedInstanceState);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);
            // Trigger the listener immediately with the preference's current value.
            onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }
}
