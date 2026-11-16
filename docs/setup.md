# Cài đặt và chạy

## 1. Cài phần mềm

| Phần mềm | Ghi chú |
|---|---|
| IntelliJ IDEA (Community hoặc Ultimate) | Có sẵn Maven, không cần cài Maven riêng |
| JDK 21 (Eclipse Temurin) | IntelliJ: File → Project Structure → SDK → Download JDK → 21, Temurin. Dự án biên dịch cho Java 17 trở lên |
| Hệ quản trị CSDL | Theo lựa chọn hiện tại của nhóm, xem [README](../README.md), mục cài CSDL |
| Git | |
| Scene Builder (tùy chọn) | Sửa file `.fxml` bằng giao diện kéo thả |

## 2. Lấy mã nguồn

```bash
git clone https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261.git
cd IT3180-NMCNPM-GR16-20261
```

Đặt tên và email Git trùng tài khoản GitHub: xem
[workflow.md](workflow.md), mục 1.

IntelliJ → Open → chọn thư mục repo → Trust Project, chờ Maven tải thư
viện.

## 3. Cấu hình kết nối CSDL

Thông tin kết nối (địa chỉ, tên đăng nhập, **mật khẩu**) khác nhau trên mỗi
máy nên **không viết vào mã nguồn và không commit**:

1. Repo giữ một tệp mẫu không chứa mật khẩu thật, ví dụ
   `config/db.properties.example`.
2. Mỗi người sao chép thành `config/db.properties` rồi điền thông tin của
   máy mình. Tệp `config/db.properties` nằm trong `.gitignore`.
3. Ứng dụng đọc cấu hình từ tệp này khi khởi động; sửa tệp xong thì tắt và
   mở lại ứng dụng.

Lỡ commit mật khẩu thì báo nhóm và **đổi mật khẩu ngay**: xóa khỏi lịch sử
git không đủ, vì người khác có thể đã clone hoặc xem repo.

## 4. Chạy ứng dụng và test

- IntelliJ → Maven → Lifecycle → `verify`: biên dịch và chạy toàn bộ test.
  Kết quả mong đợi: `BUILD SUCCESS`.
- Dòng lệnh (cần Maven 3.9+ trong PATH): `mvn clean verify`; chạy ứng dụng:
  `mvn javafx:run`.
- CI trên GitHub chạy cùng lệnh `mvn -B verify` cho mọi Pull Request vào
  `main` ([workflow.md](workflow.md)).

## 5. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| `release version 21 not supported` hoặc `invalid target release` | Chọn JDK 21 trong Project Structure |
| `'mvn' is not recognized` | Chạy bằng bảng Maven trong IntelliJ |
| Không thấy cửa sổ ứng dụng | Cửa sổ nằm sau IntelliJ, dùng Alt+Tab |
| Không kết nối được CSDL | Kiểm tra dịch vụ CSDL đang chạy và thông tin trong `config/db.properties` |
