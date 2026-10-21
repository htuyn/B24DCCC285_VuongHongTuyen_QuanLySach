package com.example.quanlysach.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Quản lý thể loại tùy chỉnh; danh sách sách vẫn chỉ nằm trong BookRepository. */
public final class CategoryRepository {
    private static final String PREFS = "library_categories";
    private static final String KEY_CUSTOM = "custom_categories_json";
    private static final List<String> FIXED = Arrays.asList("Văn học", "Khoa học", "Công nghệ", "Kỹ năng sống");
    private CategoryRepository() { }

    /** Danh sách chọn khi nhập sách: thể loại chuẩn, thể loại đã tạo và lựa chọn Khác. */
    public static ArrayList<String> getEntryCategories(Context context) {
        ArrayList<String> result = new ArrayList<>(FIXED);
        result.addAll(getCustomCategories(context));
        result.add("Khác");
        return result;
    }

    /** Bộ lọc chỉ liệt kê các thể loại thực sự có thể gắn cho sách. */
    public static ArrayList<String> getFilterCategories(Context context) {
        ArrayList<String> result = new ArrayList<>(FIXED);
        result.addAll(getCustomCategories(context));
        return result;
    }

    public static ArrayList<String> getCustomCategories(Context context) {
        ArrayList<String> result = new ArrayList<>();
        JSONArray data;
        try { data = new JSONArray(preferences(context).getString(KEY_CUSTOM, "[]")); }
        catch (Exception e) { data = new JSONArray(); }
        for (int i = 0; i < data.length(); i++) {
            String value = normalize(data.optString(i, ""));
            if (!value.isEmpty() && !isReserved(value) && !containsIgnoreCase(FIXED, value) && !containsIgnoreCase(result, value)) result.add(value);
        }
        Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    /** Trả thể loại chuẩn hóa/canonical; null nếu chuỗi trống hoặc dùng từ khóa Khác. */
    public static String resolve(Context context, String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty() || "khác".equals(normalized.toLowerCase(Locale.ROOT))) return null;
        for (String fixed : FIXED) if (fixed.equalsIgnoreCase(normalized)) return fixed;
        for (String custom : getCustomCategories(context)) if (custom.equalsIgnoreCase(normalized)) return custom;
        return normalized;
    }

    /** Ghi nhớ thể loại mới chỉ khi thao tác thêm/sửa sách đã được xác nhận. */
    public static void remember(Context context, String value) {
        String canonical = resolve(context, value);
        if (canonical == null || containsIgnoreCase(FIXED, canonical) || containsIgnoreCase(getCustomCategories(context), canonical)) return;
        ArrayList<String> custom = getCustomCategories(context);
        custom.add(canonical);
        Collections.sort(custom, String.CASE_INSENSITIVE_ORDER);
        JSONArray data = new JSONArray();
        for (String item : custom) data.put(item);
        preferences(context).edit().putString(KEY_CUSTOM, data.toString()).apply();
    }

    private static SharedPreferences preferences(Context context) { return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    private static String normalize(String value) { return value == null ? "" : value.trim().replaceAll("\\s+", " "); }
    private static boolean isReserved(String value) { return "khác".equals(value.toLowerCase(Locale.ROOT)); }
    private static boolean containsIgnoreCase(List<String> values, String candidate) {
        for (String value : values) if (value.equalsIgnoreCase(candidate)) return true;
        return false;
    }
}
