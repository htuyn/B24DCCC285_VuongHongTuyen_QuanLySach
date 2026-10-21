package com.example.quanlysach;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;
import com.example.quanlysach.data.BookRepository;
import com.example.quanlysach.data.CategoryRepository;
import com.example.quanlysach.model.Book;
import java.util.ArrayList;

public class AddBookActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_ID = "edit_book_id";
    // Chặn thêm trùng nếu người dùng nhấn nút lưu liên tiếp trước khi Activity đóng.
    private boolean saved;
    private boolean duplicateDialogShown;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_add_book); WindowInsetsHelper.apply(this);
        EditText title = findViewById(R.id.inputTitle), author = findViewById(R.id.inputAuthor), notes = findViewById(R.id.inputNotes);
        EditText customCategory = findViewById(R.id.inputCustomCategory);
        android.widget.LinearLayout customCategoryGroup = findViewById(R.id.customCategoryGroup);
        android.widget.TextView customCategoryError = findViewById(R.id.customCategoryError);
        Spinner category = findViewById(R.id.inputCategory);
        ArrayList<String> categories = CategoryRepository.getEntryCategories(this);
        category.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        category.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                boolean other = "Khác".equals(category.getSelectedItem());
                customCategoryGroup.setVisibility(other ? android.view.View.VISIBLE : android.view.View.GONE);
                if (!other) { customCategory.setText(""); customCategoryError.setText(""); }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { customCategoryGroup.setVisibility(android.view.View.GONE); }
        });
        RadioGroup status = findViewById(R.id.statusGroup); RatingBar rating = findViewById(R.id.inputRating);
        String editId = getIntent().getStringExtra(EXTRA_EDIT_ID);
        Book editing = BookRepository.findById(editId);
        if (editing != null) {
            ((android.widget.TextView) findViewById(R.id.addFormTitle)).setText("Chỉnh sửa sách");
            title.setText(editing.getTitle()); author.setText(editing.getAuthor()); notes.setText(editing.getNotes()); rating.setRating(editing.getRating());
            boolean foundCategory = false;
            for (int i = 0; i < categories.size(); i++) if (categories.get(i).equalsIgnoreCase(editing.getCategory())) { category.setSelection(i); foundCategory = true; break; }
            if (!foundCategory) {
                int otherPosition = categories.indexOf("Khác");
                if (otherPosition >= 0) category.setSelection(otherPosition);
                customCategory.setText(editing.getCategory());
            }
            status.check("Đang đọc".equals(editing.getStatus()) ? R.id.statusReading : "Đã đọc".equals(editing.getStatus()) ? R.id.statusRead : R.id.statusUnread);
            ((Button) findViewById(R.id.saveBookButton)).setText("Lưu thay đổi");
        }
        ((Button) findViewById(R.id.saveBookButton)).setOnClickListener(v -> {
            if (saved) return;
            String name = title.getText().toString().trim(), writer = author.getText().toString().trim();
            // Hai trường này bắt buộc; dừng lưu và đưa con trỏ về ô cần bổ sung.
            if (name.isEmpty()) { title.setError("Vui lòng nhập tên sách"); title.requestFocus(); return; }
            if (writer.isEmpty()) { author.setError("Vui lòng nhập tác giả"); author.requestFocus(); return; }
            int checked = status.getCheckedRadioButtonId();
            String readingStatus = checked == R.id.statusReading ? "Đang đọc" : checked == R.id.statusRead ? "Đã đọc" : "Chưa đọc";
            String noteText = notes.getText().toString().trim();
            String selectedCategory = String.valueOf(category.getSelectedItem());
            String categoryInput = "Khác".equals(selectedCategory) ? customCategory.getText().toString().trim() : selectedCategory;
            if ("Khác".equals(selectedCategory) && categoryInput.isEmpty()) {
                customCategoryError.setText("Vui lòng nhập tên thể loại mới.");
                customCategory.setError("Bắt buộc"); customCategory.requestFocus(); return;
            }
            String resolvedCategory = CategoryRepository.resolve(this, categoryInput);
            if (resolvedCategory == null) {
                customCategoryGroup.setVisibility(android.view.View.VISIBLE);
                customCategoryError.setText("Tên thể loại không hợp lệ hoặc đang dùng từ khóa Khác.");
                customCategory.requestFocus(); return;
            }
            final String genre = resolvedCategory;
            customCategoryError.setText(""); customCategory.setError(null);
            if (BookRepository.isDuplicate(name, writer, editing == null ? null : editing.getId())) {
                if (duplicateDialogShown) return;
                duplicateDialogShown = true;
                new AlertDialog.Builder(this).setTitle("Có thể bị trùng")
                        .setMessage("Đã có sách cùng tên và tác giả. Bạn vẫn muốn " + (editing == null ? "thêm" : "lưu") + " không?")
                        .setNegativeButton("Quay lại", (dialog, which) -> duplicateDialogShown = false)
                        .setOnCancelListener(dialog -> duplicateDialogShown = false)
                        .setPositiveButton("Tiếp tục", (dialog, which) -> saveBook(editing, name, writer, genre, readingStatus, rating.getRating(), noteText)).show();
            } else saveBook(editing, name, writer, genre, readingStatus, rating.getRating(), noteText);
        });
        ((Button) findViewById(R.id.cancelAddButton)).setOnClickListener(v -> finish());
    }
    private void saveBook(Book editing, String name, String author, String category, String status, float rating, String notes) {
        if (saved) return;
        CategoryRepository.remember(this, category);
        if (editing == null) BookRepository.add(new Book(name, author, category, status, rating, notes));
        else {
            editing.setTitle(name); editing.setAuthor(author); editing.setCategory(category);
            editing.setStatus(status); editing.setRating(rating); editing.setNotes(notes);
        }
        saved = true; Toast.makeText(this, editing == null ? "Đã thêm sách" : "Đã cập nhật sách", Toast.LENGTH_SHORT).show(); finish();
    }
}
