
package com.lesports.bike.settings.utils;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;

import com.lesports.bike.settings.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    public static String getCorrentDate(Context context) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String currentDate = mYear + context.getResources().getString(R.string.year) +
                mMonth + context.getResources().getString(R.string.month) +
                mDay + context.getResources().getString(R.string.day);
        return currentDate;
    }

    public static String getDate(Context context, int year, int month, int day) {
        String currentDate = year + context.getResources().getString(R.string.year) +
                month + context.getResources().getString(R.string.month) +
                day + context.getResources().getString(R.string.day);
        return currentDate;
    }

    public static String getCorrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curTime = new Date(System.currentTimeMillis());// 获取当前时间
        String currentTime = formatter.format(curTime);
        return currentTime;
    }

    public static String getCorrentTimezone() {
        TimeZone currentTimeZone = TimeZone.getDefault();
        String displayPlace = currentTimeZone.getDisplayName();
        String gmtString = createGmtOffsetString(true, true, currentTimeZone.getRawOffset());
        return gmtString + " " + displayPlace;
    }

    private static String createGmtOffsetString(boolean includeGmt,
            boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    public static void setSystemDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }

    public static void setSystemTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }

    public static void set24HourFormat(Context context) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24, "24");
    }

    public static String dateToWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        return week;
    }
}
