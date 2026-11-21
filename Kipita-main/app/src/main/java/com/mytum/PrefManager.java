package com.mytum;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String FCM_TOKEN = "regId";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String PREF_NAME = "MyPreference";
    private static final String PRODUCT_LIST = "IsFirstTimeLaunch";
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public PrefManager(Context context) {
        this._context = context;
        this.pref = this._context.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        this.editor.putBoolean("IsFirstTimeLaunch", isFirstTime);
        this.editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return this.pref.getBoolean("IsFirstTimeLaunch", true);
    }

    public static void setProductList(Context context, String editable) {
        context.getSharedPreferences("IsFirstTimeLaunch", 0).edit().putString("IsFirstTimeLaunch", editable).commit();
    }

    public static String getProductList(Context context) {
        return context.getSharedPreferences("IsFirstTimeLaunch", 0).getString("IsFirstTimeLaunch", "");
    }

    public static void setFCMID(Context context, String editable) {
        context.getSharedPreferences(FCM_TOKEN, 0).edit().putString(FCM_TOKEN, editable).commit();
    }

    public static String getFCMID(Context context) {
        return context.getSharedPreferences(FCM_TOKEN, 0).getString(FCM_TOKEN, "");
    }
}
