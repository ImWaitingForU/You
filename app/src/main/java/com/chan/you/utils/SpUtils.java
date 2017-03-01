package com.chan.you.utils;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.EditText;

/**
 * Created by chan on 2017/3/1.
 */

public class SpUtils {

    public final static String SP_NAME = "YOU_SP";
    public final static String KEY_PHONE = "phone";
    public final static String KEY_QQ = "qq";
    public final static String KEY_WEIBO = "weibo";
    public final static String KEY_ISFIRST_USE = "isFirstUse";
    public final static String KEY_NAME = "hisName";

    public static boolean checkIsFirstUse (SharedPreferences sp) {
        return sp.getBoolean (KEY_ISFIRST_USE, true);
    }

    public static void putBoolean (SharedPreferences sp, String key, boolean value) {
        sp.edit ().putBoolean (key, value).apply ();
    }

    public static String getString (SharedPreferences sp, String key) {
        return sp.getString (key, null);
    }

    public static String getName (@NonNull SharedPreferences sp) {
        return sp.getString (KEY_NAME, "");
    }

    public static String getPhone (@NonNull SharedPreferences sp) {
        return sp.getString (KEY_PHONE, "");
    }

    public static String getQQ (@NonNull SharedPreferences sp) {
        return sp.getString (KEY_QQ, "");
    }

    public static String getWeibo (@NonNull SharedPreferences sp) {
        return sp.getString (KEY_WEIBO, "");
    }

    public static void putMsg (SharedPreferences sp, String name, String phone, String qq, String weibo) {
        SharedPreferences.Editor editor = sp.edit ();
        editor.putString (KEY_NAME, name);
        editor.putString (KEY_PHONE, phone);
        editor.putString (KEY_QQ, qq);
        editor.putString (KEY_WEIBO, weibo);
        editor.apply ();
    }

    public static String getStringFromET (EditText et) {
        return et.getText ().toString ();
    }
}
