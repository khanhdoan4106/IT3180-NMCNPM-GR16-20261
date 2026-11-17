# Cài đặt và chạy

## 1. Cài phần mềm

| Phần mềm | Ghi chú |
|---|---|
| IntelliJ IDEA (Community hoặc Ultimate) | Có sẵn Maven, không cần cài Maven riêng |
| JDK 21 (Eclipse Temurin) | IntelliJ: File → Project Structure → SDK → Download JDK → 21, Temurin |
| MySQL 8.0 + MySQL Workbench | MySQL Installer cho Windows, chọn Custom |
| Git | |
| Scene Builder (tùy chọn) | Sửa file `.fxml` bằng giao diện kéo thả |

## 2. Lấy mã nguồn

```bash
git clone https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261.git
```

IntelliJ → Open → chọn thư mục repo → Trust Project, chờ tải thư viện Maven.
Đặt tên và email Git: [workflow.md](workflow.md), mục 1.

## 3. Dựng cơ sở dữ liệu và cấu hình kết nối

1. Mở MySQL Workbench, kết nối bằng tài khoản root; chạy
   `database/schema.sql`, rồi `database/seed.sql` (biểu tượng tia sét).
2. Tạo user cho ứng dụng, mỗi người tự đặt mật khẩu. User này chỉ được đọc,
   thêm và sửa dữ liệu; tạo bảng là việc của root:
   ```sql
   CREATE USER 'bluemoon'@'localhost' IDENTIFIED BY 'mat_khau_cua_ban';
   GRANT SELECT, INSERT, UPDATE ON bluemoon.* TO 'bluemoon'@'localhost';
   ```
3. Sao chép `config/db.properties.example` thành `config/db.properties`,
   điền `user` và `password`. File này nằm trong `.gitignore`, **không
   commit**; không viết mật khẩu vào mã nguồn.
4. Chạy ứng dụng: mở lớp `com.bluemoon.Launcher` → Run, hoặc Maven →
   Plugins → javafx → `javafx:run` (dòng lệnh: `mvn javafx:run`). Thư mục
   chạy là thư mục gốc repo để ứng dụng tìm thấy `config/db.properties`.
   Không kết nối được CSDL thì ứng dụng báo nguyên nhân, cách sửa rồi thoát.
   Kết nối được thì hiện màn đăng nhập: dùng tài khoản mẫu `admin` /
   `Admin@123` trong `seed.sql` (Quản trị viên).

## 4. Chạy test

IntelliJ → Maven → bluemoon → Lifecycle → `verify`. Kết quả mong đợi:
`BUILD SUCCESS`. Dòng lệnh (cần Maven 3.9+ trong PATH): `mvn clean verify`.
`mvn verify` gồm cả kiểm tra quy ước mã bằng Checkstyle.

Kiểm thử tích hợp với MySQL thật (lớp `*IT`): tạo một CSDL **riêng có chữ
`test` trong tên**, vì test được phép xóa và tạo lại bảng của CSDL đó.

```sql
CREATE DATABASE bluemoon_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT SELECT, INSERT, UPDATE ON bluemoon_test.* TO 'bluemoon'@'localhost';
```

Đặt biến môi trường rồi chạy `mvn verify -Pmysql` (PowerShell):

```powershell
$env:BLUEMOON_DB_URL = "jdbc:mysql://localhost:3306/bluemoon_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
$env:BLUEMOON_DB_USER = "bluemoon"
$env:BLUEMOON_DB_PASSWORD = "mat_khau_cua_ban"
# User được tạo bảng (thường là root): test xóa và tạo lại bảng
$env:BLUEMOON_DB_ADMIN_USER = "root"
$env:BLUEMOON_DB_ADMIN_PASSWORD = "mat_khau_root"
mvn verify -Pmysql
```

Trước mỗi test, bảng của `bluemoon_test` được xóa và tạo lại theo
`database/schema.sql` bằng user quản trị; test đọc, ghi bằng user ứng
dụng như khi chạy thật.

`mvn verify` không có `-Pmysql` không chạy kiểm thử tích hợp. Trên GitHub,
workflow `CI` có hai job chạy cho mọi Pull Request: `build` (`mvn -B
verify`) và `build-mysql` (MySQL 8.0, `mvn -B verify -Pmysql`).

## 5. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| Không kết nối được cơ sở dữ liệu | Sai `user`/`password` trong `config/db.properties`, hoặc MySQL chưa chạy. Sửa xong thì tắt và mở lại ứng dụng |
| Thiếu cấu hình kết nối | Chưa có `config/db.properties` (mục 3, bước 3) |
| `release version 21 not supported` | Chọn JDK 21 trong Project Structure |
| `'mvn' is not recognized` | Chạy bằng bảng Maven trong IntelliJ |
| `mvn verify` báo lỗi Checkstyle | Dòng quá 80 cột hoặc có tab: [quy ước mã](design/coding-convention.md) |
| `CREATE USER` báo user đã tồn tại | Bỏ qua, chạy tiếp `GRANT` |
| `command denied to user 'bluemoon'@'localhost'` khi chạy `schema.sql` | Đang chạy bằng user ứng dụng; các script trong `database/` chạy bằng root |

## 6. Cài đặt IDE

Để Pull Request chỉ gồm phần mình sửa (không đổi định dạng cả tệp), mọi
người dùng chung tệp [`.editorconfig`](../.editorconfig) ở gốc repo: UTF-8,
xuống dòng LF, thụt 4 dấu cách (YAML, PlantUML, Markdown thụt 2), Java tối
đa 80 cột, bỏ khoảng trắng cuối dòng, có dòng trống cuối tệp.

**IntelliJ IDEA**

1. Settings → Editor → Code Style: đánh dấu **Enable EditorConfig
   support** (thường đã bật sẵn).
2. Settings → Tools → **Actions on Save**: bỏ chọn **Reformat code** và
   **Optimize imports**; nếu muốn giữ Reformat code thì chọn phạm vi
   **Changed lines** (chỉ dòng mình sửa), không chọn Whole file.
3. Tự định dạng khi cần: chọn đoạn mình vừa viết rồi Ctrl+Alt+L, không bấm
   trên cả tệp.

**VS Code**

1. Cài tiện ích **EditorConfig for VS Code** (`EditorConfig.EditorConfig`).
2. Trong Settings (JSON) đặt:

   ```json
   "editor.formatOnSave": true,
   "editor.formatOnSaveMode": "modifications"
   ```

   hoặc tắt hẳn `"editor.formatOnSave": false`. Chế độ `modifications` chỉ
   định dạng phần đã sửa so với Git.

Trước khi commit, xem lại `git diff`: nếu thấy cả tệp đổi (thường do đổi
xuống dòng hoặc thụt lề) thì hoàn tác phần thừa rồi mới commit.
