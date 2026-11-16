# Nhật ký thay đổi

Mọi thay đổi đáng chú ý của dự án được ghi ở đây, theo
[Keep a Changelog](https://keepachangelog.com/vi/1.1.0/).

## [Chưa phát hành]

### Thêm

- TS-003: lớp khởi động `Launcher` và `App`; khi mở, ứng dụng thử kết nối
  MySQL, lỗi thì báo nguyên nhân và cách sửa bằng tiếng Việt rồi thoát
  (AC-070). Lỗi CSDL dùng chung lớp `LoiCsdl`.
- TS-003: cửa sổ chính bố cục trái – đầu – thân, menu trái nhóm theo mảng;
  mục chưa có màn hình mở trang "Chức năng đang được xây dựng". Danh sách
  màn hình đăng ký một chỗ ở `ManHinh`; kiểu dùng chung `giao-dien.css`
  theo `docs/design/ui.md`.
