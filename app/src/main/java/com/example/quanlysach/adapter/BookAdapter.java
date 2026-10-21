package com.example.quanlysach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.quanlysach.R;
import com.example.quanlysach.BookCoverHelper;
import com.example.quanlysach.model.Book;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    public interface FavoriteListener { void onFavoriteChanged(Book book); }
    public interface BookClickListener { void onBookClicked(Book book); }
    private final FavoriteListener favoriteListener;
    private final BookClickListener bookClickListener;
    /** Adapter nhận danh sách từ repository để ListView hiển thị dữ liệu thật. */
    public BookAdapter(Context context, List<Book> books, FavoriteListener listener, BookClickListener clickListener) {
        super(context, 0, books); this.favoriteListener = listener; this.bookClickListener = clickListener;
    }
    public void setBooks(List<Book> books) { clear(); addAll(books); notifyDataSetChanged(); }
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        // Tái sử dụng dòng có sẵn khi cuộn để giảm số lần tạo View.
        View row = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.item_book, parent, false) : convertView;
        Book book = getItem(position);
        if (book != null) {
            row.setClickable(true);
            row.setOnClickListener(v -> { if (bookClickListener != null) bookClickListener.onBookClicked(book); });
            BookCoverHelper.bind((TextView) row.findViewById(R.id.bookCover), book);
            ((TextView) row.findViewById(R.id.bookTitle)).setText(book.getTitle());
            ((TextView) row.findViewById(R.id.bookAuthor)).setText(book.getAuthor());
            ((TextView) row.findViewById(R.id.bookMeta)).setText(book.getCategory() + "  ·  " + book.getStatus());
            ((TextView) row.findViewById(R.id.bookRating)).setText(book.getRating() == 0 ? "Chưa đánh giá" : "★ " + (int) book.getRating() + "/5");
            ImageButton favorite = row.findViewById(R.id.favoriteButton);
            favorite.setImageResource(book.isFavorite() ? R.drawable.ic_heart_on : R.drawable.ic_heart_off);
            favorite.setContentDescription(book.isFavorite() ? "Bỏ yêu thích" : "Thêm vào yêu thích");
            favorite.setOnClickListener(v -> { if (favoriteListener != null) favoriteListener.onFavoriteChanged(book); });
        }
        return row;
    }
}
