package ru.loftschool.loftblogmoneytracker.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertUtils implements DateFormats{


    public static String dateToString(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public static String dateToString(Long date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public static Date stringToDate(String strDate, SimpleDateFormat dateFormat) {
        Date date;

        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            date = new Date();
        }

        return date;
    }
}
