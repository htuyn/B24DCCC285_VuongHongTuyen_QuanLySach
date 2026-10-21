package com.example.quanlysach.model;

/** Mô hình dữ liệu đại diện cho một cuốn sách trong thư viện cá nhân. */
public class Book {
    private final String id;
    private final long createdAt;
    // Thông tin mô tả cơ bản; các giá trị này không đổi sau khi tạo sách.
    private String title;
    private String author;
    private String category;
    // Trạng thái đọc và đánh giá có thể được cập nhật từ màn hình chi tiết.
    private String status;
    private float rating;
    private String notes;
    private boolean favorite;

    public Book(String title, String author, String category, String status, float rating, String notes) {
        this(title, author, category, status, rating, notes, false);
    }

    public Book(String title, String author, String category, String status, float rating, String notes, boolean favorite) {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
        this.favorite = favorite;
    }

    // Getter/setter giúp Activity và Adapter đọc hoặc cập nhật dữ liệu sách.
    public String getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
