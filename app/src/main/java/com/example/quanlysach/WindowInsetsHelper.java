package com.example.quanlysach;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/** Applies system-bar insets while preserving each layout's own padding. */
public final class WindowInsetsHelper {
    private WindowInsetsHelper() { }

    public static void apply(Activity activity) {
        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) return;
        View root = content.getChildAt(0);
        int left = root.getPaddingLeft(), top = root.getPaddingTop();
        int right = root.getPaddingRight(), bottom = root.getPaddingBottom();
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(left + insets.getSystemWindowInsetLeft(), top + insets.getSystemWindowInsetTop(),
                    right + insets.getSystemWindowInsetRight(), bottom + insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        root.requestApplyInsets();
    }
}
