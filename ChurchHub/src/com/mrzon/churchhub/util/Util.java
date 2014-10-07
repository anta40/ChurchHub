package com.mrzon.churchhub.util;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Util {
    public static String LOGIN_EVENT = "LOGIN_EVENT";
    public static String LOGOUT_EVENT = "LOGOUT_EVENT";
    public static String RECEIVE_ACTIVE_WORSHIP_EVENT = "RECEIVE_ACTIVE_WORSHIP_EVENT";
    public static String OBJECT_ADDED_EVENT = "OBJECT_ADDED_EVENT";

    public static double distFrom(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return dist * meterConversion;
    }

    public static String getPreetyDistance(double meters) {
        if (meters > 10000) {
            return ">10km";
        }
        if (meters > 1000) {
            return ((int) meters) / 1000 + "." + (((int) meters) % 1000) / 100 + "km";
        }
        return ((int) meters) + "m";
    }

    public static int getCurrentWeekOfTheYear() {
        GregorianCalendar now = new GregorianCalendar();
        now.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        int week = now.get(GregorianCalendar.WEEK_OF_YEAR);
        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return week - 1;
        } else
            return week;
    }

    public static int getCurrentDayOfTheWeek() {
        Calendar now = GregorianCalendar.getInstance();
        now.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        int day = now.get(GregorianCalendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            return 6;
        } else {
            return day - 2;
        }
    }

    public static int getCurrentYear() {
        Calendar now = GregorianCalendar.getInstance();
        return now.get(GregorianCalendar.YEAR);
    }

    public static Date getMinimumDateBasedOnWeekOfTheYear(int week) {
        Calendar now = GregorianCalendar.getInstance();
        now.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        now.set(GregorianCalendar.WEEK_OF_YEAR, week);
        int i = week;
        while (i == week) {
            now.add(GregorianCalendar.DAY_OF_YEAR, -1);
            i = now.get(GregorianCalendar.WEEK_OF_YEAR);
        }
        now.add(GregorianCalendar.DAY_OF_YEAR, 1);
        return now.getTime();
    }

    public static Date getMaximumDateBasedOnWeekOfTheYear(int week) {
        Calendar now = GregorianCalendar.getInstance();
        now.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        now.set(GregorianCalendar.WEEK_OF_YEAR, week);
        int i = week;
        while (i == week) {
            now.add(GregorianCalendar.DAY_OF_YEAR, 1);
            i = now.get(GregorianCalendar.WEEK_OF_YEAR);
        }
        now.add(GregorianCalendar.DAY_OF_YEAR, -1);
        return now.getTime();
    }

    public static String toDDMMMString(Date date) {
        return DateFormat.format("dd MMM", date).toString();

    }

    public static String toDDMMYYYYString(Date date) {
        return DateFormat.format("dd-MM-yyyy", date).toString();
    }

    public double getLatMin(double lat, int distance) {
        return 0;
    }

    public double getLatMax(double lat, int distance) {
        return 0;
    }

    public double getLonMin(double lon, int distance) {
        return 0;
    }

    public double getLonMax(double lon, int distance) {
        return 0;
    }
}
