package com.example.quanlysach;

import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

/** Lưu và đổi lựa chọn giao diện sáng/tối. */
public final class ThemeSettings {
    public static final String PREFERENCES = "library_settings";
    public static final String KEY_MODE = "theme_mode";
    private ThemeSettings() { }

    public static boolean isDark(Context context) {
        int mode = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getInt(KEY_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) return true;
        if (mode == AppCompatDelegate.MODE_NIGHT_NO) return false;
        return (context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    public static void toggle(Context context) {
        int mode = isDark(context) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putInt(KEY_MODE, mode).apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
