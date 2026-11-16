# Nhật ký thay đổi

Mọi thay đổi đáng chú ý của dự án được ghi ở đây, theo
[Keep a Changelog](https://keepachangelog.com/vi/1.1.0/).

## [Chưa phát hành]

### Thêm

- US-020: màn hình Tài khoản (menu Hệ thống) để Quản trị viên tạo tài
  khoản, chọn vai trò Quản trị viên hoặc Thủ quỹ; báo trùng tên đăng nhập
  (không phân biệt chữ hoa, chữ thường) và mật khẩu dưới 8 ký tự ngay dưới
  ô sai. Mật khẩu băm BCrypt cost 12 (thư viện `at.favre.lib:bcrypt`),
  cùng chuẩn với tài khoản admin trong `seed.sql`.
- TS-003: lớp khởi động `Launcher` và `App`; khi mở, ứng dụng thử kết nối
  MySQL, lỗi thì báo nguyên nhân và cách sửa bằng tiếng Việt rồi thoát
  (AC-070). Lỗi CSDL dùng chung lớp `LoiCsdl`.
- TS-003: cửa sổ chính bố cục trái – đầu – thân, menu trái nhóm theo mảng;
  mục chưa có màn hình mở trang "Chức năng đang được xây dựng". Danh sách
  màn hình đăng ký một chỗ ở `ManHinh`; kiểu dùng chung `giao-dien.css`
  theo `docs/design/ui.md`.
- TS-003: trang chính hiện số hộ và số nhân khẩu đang ở, nút "Tải lại";
  là mẫu View → Controller → Service → DAO → MySQL cho cả nhóm. Kiểm thử
  tích hợp chứng minh dữ liệu một máy ghi thì máy khác thấy (AC-071).
  Tài liệu `docs/design/kien-truc.md`.
