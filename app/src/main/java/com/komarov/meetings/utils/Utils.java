package com.komarov.meetings.utils;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ilia on 19.11.2017.
 */

public class Utils {

    public static Date fromString(String text, String pattern) {
        try {
            return (new SimpleDateFormat(pattern, Locale.US)).parse(text);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static String join(String separator, Iterable<String> strings) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String s : strings) {
            sb.append(sep).append(s);
            sep = separator;
        }
        return sb.toString();
    }

}
