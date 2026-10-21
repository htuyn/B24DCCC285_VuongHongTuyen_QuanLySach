package com.example.quanlysach.data;

import com.example.quanlysach.model.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

/** Kho dữ liệu trong bộ nhớ, dùng chung cho tất cả Activity trong tiến trình ứng dụng. */
public final class BookRepository {
    // Danh sách duy nhất để tránh mỗi màn hình giữ một bản sao dữ liệu riêng.
    private static final ArrayList<Book> BOOKS = new ArrayList<>();
    private static boolean initialized;
    private static Book undoCandidate;
    private static long undoExpiresAt;
    private BookRepository() {}

    /** Thêm dữ liệu mẫu đúng một lần trong vòng đời của tiến trình ứng dụng. */
    public static synchronized void initialize() {
        if (initialized) return;
        BOOKS.add(new Book("Lập trình Android cơ bản", "Nguyễn Văn A", "Công nghệ", "Đang đọc", 4, ""));
        BOOKS.add(new Book("Đắc nhân tâm", "Dale Carnegie", "Kỹ năng sống", "Đã đọc", 5, ""));
        BOOKS.add(new Book("Dế Mèn phiêu lưu ký", "Tô Hoài", "Văn học", "Chưa đọc", 0, ""));
        initialized = true;
    }
    /** Trả về danh sách dùng chung, đồng thời đảm bảo dữ liệu mẫu đã được tạo. */
    public static ArrayList<Book> getBooks() { initialize(); return BOOKS; }
    /** Thêm một sách mới vào danh sách hiện tại. */
    public static void add(Book book) { getBooks().add(book); }
    /** Tìm sách theo vị trí nhận từ Intent; trả về null nếu vị trí không hợp lệ. */
    public static Book get(int index) { return index >= 0 && index < getBooks().size() ? getBooks().get(index) : null; }
    public static Book findById(String id) {
        if (id == null) return null;
        for (Book book : getBooks()) if (id.equals(book.getId())) return book;
        return null;
    }
    public static int indexOf(Book target) { return getBooks().indexOf(target); }
    /** Xóa sách tại vị trí hợp lệ, không làm ứng dụng lỗi nếu vị trí sai. */
    public static void remove(int index) { if (get(index) != null) BOOKS.remove(index); }
    /** Xóa đúng đối tượng sách, kể cả khi danh sách đang được sắp xếp để hiển thị. */
    public static void remove(Book book) { if (book != null) getBooks().remove(book); }
    /** Xóa và tạm giữ đối tượng để màn hình thư viện có thể cho hoàn tác. */
    public static void removeForUndo(Book book) {
        if (book == null || !getBooks().remove(book)) return;
        undoCandidate = book;
        undoExpiresAt = System.currentTimeMillis() + 5000;
    }
    public static Book getUndoCandidate() {
        if (undoCandidate != null && System.currentTimeMillis() > undoExpiresAt) undoCandidate = null;
        return undoCandidate;
    }
    public static boolean undoLastRemoval() {
        Book candidate = getUndoCandidate();
        if (candidate == null) return false;
        getBooks().add(candidate);
        undoCandidate = null;
        undoExpiresAt = 0;
        return true;
    }
    public static void clearUndoCandidate(String id) {
        if (undoCandidate != null && undoCandidate.getId().equals(id)) { undoCandidate = null; undoExpiresAt = 0; }
    }
    /** Đếm sách có trạng thái Đã đọc để hiển thị trên màn hình chính. */
    public static int countRead() {
        int count = 0;
        for (Book book : getBooks()) if ("Đã đọc".equals(book.getStatus())) count++;
        return count;
    }
    /** Các sách chưa hoàn tất gồm cả Chưa đọc và Đang đọc. */
    public static int countInProgressOrUnread() { return getBooks().size() - countRead(); }
    public static int countStatus(String status) {
        int count = 0;
        for (Book book : getBooks()) if (status.equals(book.getStatus())) count++;
        return count;
    }
    public static int countFavorites() {
        int count = 0;
        for (Book book : getBooks()) if (book.isFavorite()) count++;
        return count;
    }
    public static int countRated() {
        int count = 0;
        for (Book book : getBooks()) if (book.getRating() > 0) count++;
        return count;
    }
    public static int countGenre(String genre) {
        int count = 0;
        for (Book book : getBooks()) if (genre.equals(book.getCategory())) count++;
        return count;
    }
    public static Map<String, Integer> genreCounts() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Book book : getBooks()) result.put(book.getCategory(), result.containsKey(book.getCategory()) ? result.get(book.getCategory()) + 1 : 1);
        return result;
    }
    public static boolean isDuplicate(String title, String author, String excludedId) {
        String normalizedTitle = normalize(title), normalizedAuthor = normalize(author);
        for (Book book : getBooks()) {
            if (excludedId != null && excludedId.equals(book.getId())) continue;
            if (normalize(book.getTitle()).equals(normalizedTitle) && normalize(book.getAuthor()).equals(normalizedAuthor)) return true;
        }
        return false;
    }
    private static String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
    /** Trả về bản danh sách hiển thị; không thay đổi thứ tự gốc trong kho. */
    public static ArrayList<Book> query(String query, String filter, String sort) {
        return query(query, filter, "Tất cả thể loại", sort);
    }

    /** Kết hợp độc lập điều kiện trạng thái/yêu thích với thể loại. */
    public static ArrayList<Book> query(String query, String statusFilter, String categoryFilter, String sort) {
        ArrayList<Book> result = new ArrayList<>();
        String term = normalize(query);
        for (Book book : getBooks()) {
            boolean matchesText = term.isEmpty() || normalize(book.getTitle()).contains(term)
                    || normalize(book.getAuthor()).contains(term) || normalize(book.getCategory()).contains(term);
            boolean matchesStatus = "Tất cả".equals(statusFilter)
                    || ("Yêu thích".equals(statusFilter) && book.isFavorite())
                    || statusFilter.equals(book.getStatus());
            boolean matchesCategory = categoryFilter == null || "Tất cả thể loại".equals(categoryFilter)
                    || book.getCategory().equals(categoryFilter);
            if (matchesText && matchesStatus && matchesCategory) result.add(book);
        }
        Comparator<Book> comparator;
        if ("Tên Z–A".equals(sort)) comparator = (a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle());
        else if ("Đánh giá cao".equals(sort)) comparator = (a, b) -> Float.compare(b.getRating(), a.getRating());
        else if ("Mới thêm".equals(sort)) comparator = (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt());
        else comparator = (a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle());
        Collections.sort(result, comparator);
        return result;
    }
    /** Gợi ý minh bạch: ưu tiên sách chưa đọc thuộc thể loại được đánh giá cao. */
    public static Book suggestedNext() {
        String preferredGenre = null;
        float bestRating = 0;
        for (Book book : getBooks()) if (book.getRating() > bestRating) { bestRating = book.getRating(); preferredGenre = book.getCategory(); }
        for (Book book : getBooks()) if ("Chưa đọc".equals(book.getStatus()) && preferredGenre != null && preferredGenre.equals(book.getCategory())) return book;
        for (Book book : getBooks()) if ("Chưa đọc".equals(book.getStatus())) return book;
        for (Book book : getBooks()) if ("Đang đọc".equals(book.getStatus())) return book;
        for (Book book : getBooks()) if (book.getRating() == 0) return book;
        return null;
    }
}
