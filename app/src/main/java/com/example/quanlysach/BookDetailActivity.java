package com.example.quanlysach;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.quanlysach.data.BookRepository;
import com.example.quanlysach.model.Book;

public class BookDetailActivity extends AppCompatActivity {
    // Khóa extra dùng chung giữa MainActivity và màn hình chi tiết.
    public static final String EXTRA_INDEX = "book_index";
    public static final String EXTRA_BOOK_ID = "book_id";
    private Book book;
    private Button markRead;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_book_detail); WindowInsetsHelper.apply(this);
        // Kiểm tra chỉ số nhận qua Intent trước khi truy cập danh sách.
        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        String id = getIntent().getStringExtra(EXTRA_BOOK_ID);
        book = id == null ? BookRepository.get(index) : BookRepository.findById(id);
        if (book == null) { Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show(); finish(); return; }
        markRead = findViewById(R.id.markReadButton);
        bindBookDetails();
        RatingBar rating = findViewById(R.id.detailRating);
        // Cập nhật trực tiếp đối tượng dùng chung khi người dùng đổi số sao.
        rating.setOnRatingBarChangeListener((bar, value, fromUser) -> { if (fromUser) book.setRating(value); });
        updateReadButton();
        markRead.setOnClickListener(v -> { book.setStatus("Đã đọc"); ((TextView) findViewById(R.id.detailStatus)).setText(book.getStatus()); updateReadButton(); });
        // Chỉ xóa sau khi người dùng xác nhận; Hủy giữ nguyên danh sách.
        ((Button) findViewById(R.id.deleteBookButton)).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Xóa sách?").setMessage("Bạn có chắc muốn xóa cuốn sách này?")
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .setPositiveButton("Xóa", (d, w) -> { BookRepository.removeForUndo(book); finish(); }).show());
        ((Button) findViewById(R.id.backDetailButton)).setOnClickListener(v -> finish());
        Button favorite = findViewById(R.id.favoriteDetailButton);
        updateFavoriteButton(favorite);
        favorite.setOnClickListener(v -> { book.setFavorite(!book.isFavorite()); updateFavoriteButton(favorite); Toast.makeText(this, book.isFavorite() ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show(); });
        ((Button) findViewById(R.id.editBookButton)).setOnClickListener(v -> startActivity(new android.content.Intent(this, AddBookActivity.class).putExtra(AddBookActivity.EXTRA_EDIT_ID, book.getId())));
    }
    @Override protected void onResume() { super.onResume(); if (book != null) bindBookDetails(); }
    private void bindBookDetails() {
        ((TextView) findViewById(R.id.detailTitle)).setText(book.getTitle());
        BookCoverHelper.bind((TextView) findViewById(R.id.detailCover), book);
        ((TextView) findViewById(R.id.detailAuthor)).setText(book.getAuthor());
        ((TextView) findViewById(R.id.detailCategory)).setText(book.getCategory());
        TextView status = findViewById(R.id.detailStatus);
        status.setText(book.getStatus());
        status.setBackgroundResource(R.drawable.bg_chip);
        status.setPadding(12, 7, 12, 7);
        status.setTextColor(getResources().getColor("Đã đọc".equals(book.getStatus()) ? R.color.success : R.color.forest, getTheme()));
        ((TextView) findViewById(R.id.detailNotes)).setText(book.getNotes().isEmpty() ? "Chưa có ghi chú" : book.getNotes());
        ((RatingBar) findViewById(R.id.detailRating)).setRating(book.getRating());
        updateReadButton();
        Button favorite = findViewById(R.id.favoriteDetailButton);
        updateFavoriteButton(favorite);
    }
    /** Đồng bộ trạng thái và khả năng bấm nút theo trạng thái đọc hiện tại. */
    private void updateReadButton() { markRead.setEnabled(!"Đã đọc".equals(book.getStatus())); markRead.setText(markRead.isEnabled() ? "Đánh dấu đã đọc" : "Đã đọc ✓"); }
    private void updateFavoriteButton(Button button) {
        button.setText(book.isFavorite() ? "♥" : "♡");
        button.setContentDescription(book.isFavorite() ? "Bỏ yêu thích" : "Thêm yêu thích");
    }
}
