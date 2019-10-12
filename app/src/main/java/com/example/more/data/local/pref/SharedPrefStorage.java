package com.example.more.data.local.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefStorage implements PreferencesStorage {

    public static String PREF_NAME = "preferencesDB";
    private Context context;

    @Inject
    public SharedPrefStorage(Context context) {
        this.context = context;
    }


    @Override
    public void writeValue(String key, Object value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        if (value instanceof String) {

            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (int) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (boolean) value);
        }
        editor.commit();

    }

    @Override
    public Object readValue(String key, Object defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (defaultValue instanceof String) {
            return pref.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return pref.getInt(key, (int) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return pref.getBoolean(key, (Boolean) defaultValue);
        }
        return null;
    }
}