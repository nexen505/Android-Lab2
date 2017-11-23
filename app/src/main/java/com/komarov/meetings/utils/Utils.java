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

}
