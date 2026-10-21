package com.example.quanlysach;

import android.widget.TextView;
import com.example.quanlysach.model.Book;

/** Áp dụng bìa minh họa cục bộ theo thể loại, không tải hình từ mạng. */
public final class BookCoverHelper {
    private BookCoverHelper() { }

    public static void bind(TextView cover, Book book) {
        String category = book.getCategory();
        if ("Văn học".equals(category)) cover.setBackgroundResource(R.drawable.bg_cover_literature);
        else if ("Khoa học".equals(category)) cover.setBackgroundResource(R.drawable.bg_cover_science);
        else if ("Công nghệ".equals(category)) cover.setBackgroundResource(R.drawable.bg_cover_technology);
        else if ("Kỹ năng sống".equals(category)) cover.setBackgroundResource(R.drawable.bg_cover_skills);
        else cover.setBackgroundResource(R.drawable.bg_cover_other);

        String[] words = book.getTitle().trim().split("\\s+");
        String mark = words.length > 1 ? words[0].substring(0, 1) + words[1].substring(0, 1) : words[0].substring(0, 1);
        cover.setText(mark.toUpperCase(java.util.Locale.getDefault()));
        cover.setContentDescription("Bìa minh họa cho " + book.getTitle());
    }
}
