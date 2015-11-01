package ru.loftschool.loftblogmoneytracker.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtil {

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private final static DateFormat dateFormatFull = new SimpleDateFormat("d MMMM yyyy, EEEE", Locale.ENGLISH);

    public static String getDateFull(Date date) {
        return dateFormatFull.format(date);
    }

    public static Date parseStringToDate(String date) {
        try {
            return dateFormatFull.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
