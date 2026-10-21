package com.example.quanlysach;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import com.example.quanlysach.adapter.BookAdapter;
import com.example.quanlysach.data.BookRepository;
import com.example.quanlysach.data.CategoryRepository;
import com.example.quanlysach.model.Book;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BookAdapter adapter;
    private TextView total, read, readingStat, unread, empty;
    private TextView progressLabel, suggestion;
    private TextView genreSummary;
    private EditText search;
    private Spinner filter, sort;
    private ArrayAdapter<String> filterAdapter;
    private String statusFilter = "Tất cả";
    private View undoBar;
    private TextView undoMessage;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable undoExpiry;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_main); WindowInsetsHelper.apply(this);
        ListView list = findViewById(R.id.bookList);
        total = findViewById(R.id.totalCount); read = findViewById(R.id.readCount);
        readingStat = findViewById(R.id.readingStat); unread = findViewById(R.id.unreadCount); empty = findViewById(R.id.emptyMessage);
        progressLabel = findViewById(R.id.progressLabel); suggestion = findViewById(R.id.suggestionText);
        genreSummary = findViewById(R.id.genreSummary);
        undoBar = findViewById(R.id.undoBar); undoMessage = findViewById(R.id.undoMessage);
        search = findViewById(R.id.searchBooks); filter = findViewById(R.id.filterSpinner); sort = findViewById(R.id.sortSpinner);
        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        filter.setAdapter(filterAdapter);
        refreshFilterOptions();
        String[] sorts = {"Tên A–Z", "Tên Z–A", "Đánh giá cao", "Mới thêm"};
        sort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sorts));
        adapter = new BookAdapter(this, BookRepository.query("", "Tất cả", "Tất cả thể loại", "Tên A–Z"), book -> {
            book.setFavorite(!book.isFavorite());
            Toast.makeText(this, book.isFavorite() ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            refresh();
        }, selected -> {
            Intent intent = new Intent(this, BookDetailActivity.class).putExtra(BookDetailActivity.EXTRA_BOOK_ID, selected.getId());
            startActivity(intent);
        }); list.setAdapter(adapter);
        ((Button) findViewById(R.id.addBookButton)).setOnClickListener(v -> startActivity(new Intent(this, AddBookActivity.class)));
        ((Button) findViewById(R.id.logoutButton)).setOnClickListener(v -> { startActivity(new Intent(this, LoginActivity.class)); finish(); });
        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setText(ThemeSettings.isDark(this) ? "☀" : "☾");
        themeButton.setOnClickListener(v -> ThemeSettings.toggle(this));
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refresh(); }
            @Override public void afterTextChanged(Editable s) { }
        };
        search.addTextChangedListener(watcher);
        Button clearSearch = findViewById(R.id.searchClearButton);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearch.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }
            @Override public void afterTextChanged(Editable s) { }
        });
        clearSearch.setOnClickListener(v -> search.setText(""));
        android.widget.AdapterView.OnItemSelectedListener selections = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { refresh(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        };
        filter.setOnItemSelectedListener(selections); sort.setOnItemSelectedListener(selections);
        findViewById(R.id.allFilterChip).setOnClickListener(v -> setStatusFilter("Tất cả"));
        findViewById(R.id.unreadFilterChip).setOnClickListener(v -> setStatusFilter("Chưa đọc"));
        findViewById(R.id.readingShortcut).setOnClickListener(v -> setStatusFilter("Đang đọc"));
        findViewById(R.id.readFilterChip).setOnClickListener(v -> setStatusFilter("Đã đọc"));
        findViewById(R.id.favoritesShortcut).setOnClickListener(v -> setStatusFilter("Yêu thích"));
        findViewById(R.id.goalButton).setOnClickListener(v -> editReadingGoal());
        findViewById(R.id.undoButton).setOnClickListener(v -> {
            if (BookRepository.undoLastRemoval()) Toast.makeText(this, "Sách đã được khôi phục", Toast.LENGTH_SHORT).show();
            if (undoExpiry != null) handler.removeCallbacks(undoExpiry);
            undoBar.setVisibility(View.GONE); refresh();
        });
        refresh();
        updateStatusChips();
    }
    // Khi quay về từ màn hình thêm/chi tiết, làm mới danh sách và các số liệu thống kê.
    @Override protected void onResume() { super.onResume(); if (adapter != null) { refreshFilterOptions(); refresh(); showUndoBarIfNeeded(); } }
    private void refreshFilterOptions() {
        if (filterAdapter == null) return;
        String selected = filter.getSelectedItem() == null ? "Tất cả" : String.valueOf(filter.getSelectedItem());
        ArrayList<String> values = new ArrayList<>();
        values.add("Tất cả thể loại");
        values.addAll(CategoryRepository.getFilterCategories(this));
        filterAdapter.clear(); filterAdapter.addAll(values); filterAdapter.notifyDataSetChanged();
        int position = values.indexOf(selected);
        filter.setSelection(position >= 0 ? position : 0);
    }
    private void setStatusFilter(String value) {
        statusFilter = value;
        updateStatusChips();
        refresh();
    }
    private void updateStatusChips() {
        updateChip(findViewById(R.id.allFilterChip), "Tất cả");
        updateChip(findViewById(R.id.unreadFilterChip), "Chưa đọc");
        updateChip(findViewById(R.id.readingShortcut), "Đang đọc");
        updateChip(findViewById(R.id.readFilterChip), "Đã đọc");
        updateChip(findViewById(R.id.favoritesShortcut), "Yêu thích");
    }
    private void updateChip(Button chip, String value) {
        boolean selected = value.equals(statusFilter);
        chip.setBackgroundResource(selected ? R.drawable.bg_filter_selected : R.drawable.bg_chip);
        chip.setTextColor(getResources().getColor(selected ? R.color.ink : R.color.forest_text, getTheme()));
    }
    private void showUndoBarIfNeeded() {
        Book deleted = BookRepository.getUndoCandidate();
        if (deleted == null) { undoBar.setVisibility(View.GONE); return; }
        undoMessage.setText("Đã xóa “" + deleted.getTitle() + "”");
        undoBar.setVisibility(View.VISIBLE);
        if (undoExpiry != null) handler.removeCallbacks(undoExpiry);
        String deletedId = deleted.getId();
        undoExpiry = () -> {
            BookRepository.clearUndoCandidate(deletedId);
            undoBar.setVisibility(View.GONE);
        };
        handler.postDelayed(undoExpiry, Math.max(0, BookRepository.getUndoCandidate() == null ? 0 : 5000));
    }
    private void refresh() {
        ArrayList<Book> books = BookRepository.getBooks();
        String genre = filter.getSelectedItem() == null ? "Tất cả thể loại" : String.valueOf(filter.getSelectedItem());
        String order = sort.getSelectedItem() == null ? "Tên A–Z" : String.valueOf(sort.getSelectedItem());
        adapter.setBooks(BookRepository.query(search.getText().toString(), statusFilter, genre, order));
        int totalCount = books.size(), readCount = BookRepository.countRead();
        total.setText(String.valueOf(totalCount)); read.setText(String.valueOf(readCount));
        readingStat.setText(String.valueOf(BookRepository.countStatus("Đang đọc")));
        unread.setText(String.valueOf(BookRepository.countStatus("Chưa đọc")));
        int percent = totalCount == 0 ? 0 : Math.round(readCount * 100f / totalCount);
        ((ProgressBar) findViewById(R.id.readingProgress)).setProgress(percent);
        progressLabel.setText("Hoàn thành " + percent + "%");
        empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        Book recommended = BookRepository.suggestedNext();
        suggestion.setText(recommended == null ? "Chưa có gợi ý — thêm sách chưa đọc để bắt đầu." : "Gợi ý tiếp theo · " + recommended.getTitle() + " (" + recommended.getCategory() + ")");
        genreSummary.setText(BookRepository.genreCounts().size() + " thể loại · " + BookRepository.countRated() + " đánh giá");
        int goal = getSharedPreferences("library_settings", MODE_PRIVATE).getInt("monthly_goal", 5);
        ((Button) findViewById(R.id.goalButton)).setText(goal == 0 ? "Mục tiêu tắt" : "Mục tiêu " + readCount + "/" + goal);
    }
    private void editReadingGoal() {
        android.content.SharedPreferences prefs = getSharedPreferences("library_settings", MODE_PRIVATE);
        EditText input = new EditText(this); input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(prefs.getInt("monthly_goal", 5)));
        new android.app.AlertDialog.Builder(this).setTitle("Mục tiêu đọc")
                .setMessage("Tiến độ tính từ số sách hiện được đánh dấu Đã đọc (không tạo lịch sử đọc theo tháng).")
                .setView(input).setNegativeButton("Hủy", null).setPositiveButton("Lưu", (dialog, which) -> {
                    try {
                        int goal = Integer.parseInt(input.getText().toString());
                        if (goal < 0) throw new NumberFormatException();
                        prefs.edit().putInt("monthly_goal", goal).apply();
                        int progress = goal == 0 ? 0 : Math.min(100, Math.round(BookRepository.countRead() * 100f / goal));
                        Toast.makeText(this, goal == 0 ? "Đã tắt mục tiêu" : "Tiến độ mục tiêu: " + progress + "%", Toast.LENGTH_LONG).show();
                    } catch (NumberFormatException e) { Toast.makeText(this, "Nhập mục tiêu là số không âm", Toast.LENGTH_SHORT).show(); }
                }).show();
    }
}
