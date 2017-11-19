package com.komarov.meetings.model;

import com.komarov.meetings.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ilia on 19.11.2017.
 */

public class StringDateTime {
    private String date, time;

    public StringDateTime() {
    }

    public StringDateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public StringDateTime(Date date) {
        final String s = new SimpleDateFormat(Meeting.DATE_TIME_PATTERN, Locale.US).format(date);
        final String[] strings = s.split(" ");
        this.date = strings[0];
        this.time = strings[1];
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Date toDate() {
        return Utils.fromString(String.format("%s %s", date, time), Meeting.DATE_TIME_PATTERN);
    }
}
