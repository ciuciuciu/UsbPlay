package com.ingen.usbapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static String date2String(Date date, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static Date string2Date(String date, String pattern) throws Exception {
        return new SimpleDateFormat(pattern).parse(date);
    }

    public static String getDisplayTimeFromSecond(int second) {
        int minute = second / 60;
        second = second % 60;

        String sec = second < 10 ? "0" + String.valueOf(second) : String.valueOf(second);
        String min = "" + minute;

        if (minute < 10) {
            return "0" + min + ":" + sec;
        } else if (minute < 60) {
            return min + ":" + sec;
        } else {
            int hours = minute / 60;
            minute = minute % 60;

            min = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            String hour = hours < 10 ? "0" + String.valueOf(hours) : String.valueOf(hours);

            return hour + ":" + min + ":" + sec;
        }
    }

    public static String getCurrentDate(String pattern) {
        return date2String(new Date(), pattern);
    }

    public static int getCurrentHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }


    public static Date getDateAfterDay(int dayAfter) {
        Date currentDate = new Date();
        // convert date to calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, dayAfter);

        return calendar.getTime();
    }

}
