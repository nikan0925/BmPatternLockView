package com.bmqb.bmpatternlockview;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static String getPattern(Context context) {
        SharedPreferences store = context.getSharedPreferences("pattern_xml", 0);
        return store.getString("pattern_key", "");
    }

    public static void savePattern(Context context, String pattern) {
        SharedPreferences store = context.getSharedPreferences("pattern_xml", 0);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("pattern_key", pattern);
        editor.apply();
    }
}
