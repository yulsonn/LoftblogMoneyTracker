package ru.loftschool.loftblogmoneytracker.ui.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.R;
import ru.loftschool.loftblogmoneytracker.database.model.Categories;
import ru.loftschool.loftblogmoneytracker.database.model.Expenses;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {

    private ArrayList<String> xVals;
    private ArrayList<Entry> yVals;

    @ViewById(R.id.piechart)
    PieChart pieChart;

    @StringRes(R.string.frag_title_statistics)
    String title;

    @StringRes(R.string.pie_chart_title)
    String chartTitleText;

    @StringRes(R.string.pie_chart_description)
    String chartDescription;

    @StringRes(R.string.currency_RUB)
    String curRub;

    @AfterViews
    void ready(){
        getActivity().setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initData()) {
            configurePieChart();
        }
    }

    private void configurePieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDescription(chartDescription);
        pieChart.setDescriptionTextSize(20f);
        pieChart.setDescriptionColor(ContextCompat.getColor(getContext(), R.color.primaryDark));

        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        // enable hole
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(80);

        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHighlightEnabled(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);

        // animate appear
        pieChart.animateXY(1500, 1500);

        // set a chart value selected listener
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
                Toast.makeText(getContext(), xVals.get(e.getXIndex()) + ": " +
                                String.format("%.2f",yVals.get(e.getXIndex()).getVal()) + " " +
                                curRub, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        addData();

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setTextSize(13f);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
    }

    private boolean initData() {
        List<Categories> categories = new Select().from(Categories.class).execute();

        if (categories != null) {

            xVals = new ArrayList<>();
            yVals = new ArrayList<>();

            for (Categories category : categories) {
                float sum = 0f;
                for (Expenses expense : category.expenses()) {
                    sum += expense.price;
                }
                if (sum != 0) {
                    xVals.add(category.name);
                    yVals.add(new Entry(sum, xVals.size() - 1));
                }
            }
            if (!xVals.isEmpty()){
                return true;
            }
        }

        return false;
    }

    private void addData() {

        // create pie dataset
        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(8);


        // add many colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.DKGRAY);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString(chartTitleText + '\n' + String.format("%.2f", countExpensesSum()) + " " +curRub);
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.primaryDark)), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, 0);
        s.setSpan(new RelativeSizeSpan(1.5f), 0, s.length(), 0);

        return s;
    }

    private Float countExpensesSum() {
        Float sum = 0f;
        List<Expenses> expenses = Expenses.selectAll();
        if (!expenses.isEmpty()) {
            for (Expenses expense : expenses) {
                sum += expense.price;
            }
        }
        return sum;
    }
}
