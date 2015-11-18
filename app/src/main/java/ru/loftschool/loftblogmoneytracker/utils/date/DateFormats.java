package ru.loftschool.loftblogmoneytracker.utils.date;

import java.text.SimpleDateFormat;
import java.util.Locale;


public interface DateFormats {

    SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    SimpleDateFormat INVERSE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
    SimpleDateFormat INVERSE_DATE_FORMAT_LOAD = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));

}
