# Ứng dụng Quản lý thư viện sách cá nhân

Ứng dụng Android Java/XML quản lý thư viện sách cá nhân trong bộ nhớ. Có dashboard thống kê, tìm kiếm, lọc, sắp xếp, yêu thích, thêm/chỉnh sửa sách, đánh giá, trạng thái đọc và xác nhận xóa.

## Công nghệ
- Java và XML (Android Views)
- `ArrayList<Book>` trong `BookRepository` để lưu dữ liệu trong bộ nhớ tiến trình
- `ListView` với `BookAdapter`; `Intent` để điều hướng giữa các Activity
- Không sử dụng cơ sở dữ liệu, API hoặc dịch vụ bên ngoài

## Giao diện
- Design system theo ảnh tham chiếu: nền xám tím nhạt, bề mặt trắng, turquoise làm màu hành động và card bo góc có elevation nhẹ.
- Bảng màu/tokens tại `res/values/colors.xml`; kiểu chữ và nút dùng chung tại `res/values/themes.xml`.
- Bìa minh họa sinh cục bộ theo thể loại bằng drawable XML, không tải ảnh từ Internet.
- Bố cục xử lý system-bar insets và các form hỗ trợ cuộn trên màn hình nhỏ.
- Có giao diện sáng/tối, nút chuyển trên dashboard và ghi nhớ lựa chọn; bảng màu tối định nghĩa riêng trong `res/values-night`.

## Cấu trúc chính
- `model/Book.java`: thông tin sách
- `data/BookRepository.java`: dữ liệu dùng chung và thống kê
- `data/CategoryRepository.java`: danh mục chuẩn và thể loại tùy chỉnh (lưu cài đặt riêng bằng `SharedPreferences`)
- `adapter/BookAdapter.java`: hiển thị từng dòng sách
- `LoginActivity`, `MainActivity`, `AddBookActivity`, `BookDetailActivity`: bốn màn hình
- `res/layout/`: giao diện XML

## Tính năng dashboard
- Tìm kiếm theo tên sách, tác giả hoặc thể loại; lọc theo trạng thái/thể loại/yêu thích.
- Sắp xếp tên A–Z/Z–A, đánh giá cao hoặc mới thêm; thao tác này không thay đổi thứ tự gốc trong repository.
- Thống kê tổng số, đã đọc, đang đọc, chưa đọc và tiến độ hoàn thành; chip trạng thái kết hợp với lọc thể loại và sắp xếp.
- Gợi ý sách chưa đọc theo quy tắc cục bộ, ưu tiên thể loại có sách được đánh giá cao.
- Mục tiêu đọc được lưu bằng `SharedPreferences`; tiến độ so với số sách hiện đánh dấu đã đọc. Ứng dụng không lưu ngày hoàn thành nên không giả lập lịch sử theo tháng.
- Báo trùng tên/tác giả để người dùng xác nhận; chỉnh sửa cập nhật cùng đối tượng, không thêm bản sao.
- Khi chọn “Khác” lúc thêm/sửa, có thể nhập thể loại mới. Tên được chuẩn hóa, chống trùng không phân biệt hoa thường, dùng lại được trong bộ lọc và giữ qua lần mở ứng dụng.
- Xóa sách có thể hoàn tác trong 5 giây từ banner trên trang thư viện.

## Chạy ứng dụng
1. Mở thư mục dự án bằng Android Studio và chờ Gradle Sync.
2. Chọn emulator hoặc thiết bị Android (minSdk 24).
3. Nhấn Run. Đăng nhập bằng `admin` / `123456`.

Dữ liệu chỉ được giữ trong bộ nhớ khi tiến trình ứng dụng còn hoạt động; đóng hoàn toàn ứng dụng sẽ khởi tạo lại ba sách mẫu.
Dark mode có thể bật/tắt tại dashboard và ghi nhớ lựa chọn. APK build/lint thành công; lần kiểm tra emulator mới nhất bị chặn vì AVD báo thiếu dung lượng đĩa, nên giao diện bản cập nhật cuối chưa được chụp xác minh.
