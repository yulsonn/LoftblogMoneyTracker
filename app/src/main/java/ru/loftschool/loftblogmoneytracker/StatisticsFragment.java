package ru.loftschool.loftblogmoneytracker;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

@EFragment(R.layout.statistics_fragment)
public class StatisticsFragment extends Fragment {

    @StringRes(R.string.frag_title_statistics)
    String title;

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
    }
}
