package com.example.espresio.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportUtils {
    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATETIME_FORMAT =
            new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount).replace("IDR", "Rp");
    }

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return DATETIME_FORMAT.format(new Date(timestamp));
    }

    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static long getStartOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis();
    }

    public static String getPeriodDescription(long startDate, long endDate) {
        return formatDate(startDate) + " - " + formatDate(endDate);
    }
}