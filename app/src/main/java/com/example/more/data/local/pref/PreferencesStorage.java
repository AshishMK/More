package com.example.more.data.local.pref;

public interface PreferencesStorage<T> {
    void writeValue(String key, T value);

    T readValue(String key,T defaultValue);
}