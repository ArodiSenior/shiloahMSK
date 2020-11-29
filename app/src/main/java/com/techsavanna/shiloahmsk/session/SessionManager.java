package com.techsavanna.shiloahmsk.session;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

public class SessionManager {
    public static final String KEY_USER_ID = "user_id";
    private static final String PREF_NAME = "EDSPref";
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public SessionManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        pref = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String str) {
        editor.putString(KEY_USER_ID, str);
        editor.apply();
    }

    public HashMap<String, String> getLoginDetails() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_USER_ID, this.pref.getString(KEY_USER_ID, ""));
        return hashMap;
    }
}
