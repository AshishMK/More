package com.example.more.data.local.pref;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefStorage implements PreferencesStorage {

    public static final String PREF_NAME = "preferencesDB";
    public static final String REWARD_AD_OFFSET_COUNT = "reward_ad_download_offset_count";
    public static final String REWARD_AD_LAST_SHOWN = "reward_ad_last_shown";
    public static final int REWARD_AD_DISPLAY_COUNT_LIMIT = 1;
    static  final long timeDiff = 60 * 1000 * 5; //5 min
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
        }
        else if (defaultValue instanceof Long) {
            return pref.getLong(key, (long) defaultValue);
        }
        else if (defaultValue instanceof Boolean) {
            return pref.getBoolean(key, (Boolean) defaultValue);
        }
        return null;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }

    public static boolean showAD(SharedPrefStorage sharedPrefStorage) {
        return ((int) sharedPrefStorage.readValue(REWARD_AD_OFFSET_COUNT, REWARD_AD_DISPLAY_COUNT_LIMIT)) == 0 && (System.currentTimeMillis() - ((long) sharedPrefStorage.readValue(REWARD_AD_LAST_SHOWN, REWARD_AD_DISPLAY_COUNT_LIMIT)) > timeDiff );
    }
    public static int readShowAD(SharedPrefStorage sharedPrefStorage) {
        return (int) sharedPrefStorage.readValue(REWARD_AD_OFFSET_COUNT, REWARD_AD_DISPLAY_COUNT_LIMIT);
    }
    public static void setShowAD(SharedPrefStorage sharedPrefStorage,int count) {
       sharedPrefStorage.writeValue(REWARD_AD_OFFSET_COUNT, count);
    }

    public static void setRewardedTime(SharedPrefStorage preferencesStorage, long time) {
         preferencesStorage.writeValue(REWARD_AD_LAST_SHOWN, time);
    }

    public static long getRewardedTime(SharedPrefStorage preferencesStorage) {
        return (long) preferencesStorage.readValue(REWARD_AD_LAST_SHOWN, 0L);
    }
}