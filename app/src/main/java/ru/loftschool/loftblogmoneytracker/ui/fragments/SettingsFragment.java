package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

import ru.loftschool.loftblogmoneytracker.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

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
        getActivity().setTitle(getResources().getString(R.string.frag_title_settings));

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
        //Тут можно подключать наши списки настроек, например:
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().findViewById(R.id.search_action) != null) {
            getActivity().findViewById(R.id.search_action).setVisibility(View.INVISIBLE);
        }
    }
}
