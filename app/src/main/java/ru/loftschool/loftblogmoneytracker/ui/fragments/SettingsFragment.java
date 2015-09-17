package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

import ru.loftschool.loftblogmoneytracker.R;

@EFragment(R.layout.settings_fragment)
public class SettingsFragment extends Fragment {

    @StringRes(R.string.frag_title_settings)
    String title;

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
    }
}
