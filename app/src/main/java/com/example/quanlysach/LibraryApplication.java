package com.example.quanlysach;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/** Nạp tùy chọn giao diện trước khi tạo Activity đầu tiên. */
public class LibraryApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        int mode = getSharedPreferences(ThemeSettings.PREFERENCES, MODE_PRIVATE)
                .getInt(ThemeSettings.KEY_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
