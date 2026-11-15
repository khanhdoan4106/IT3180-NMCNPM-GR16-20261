# Phần mềm Quản lý và Thu phí Chung cư BlueMoon v1.0 (IT3180 - Nhóm 16)

Dự án phát triển ứng dụng Desktop quản lý hộ khẩu, nhân khẩu và thu phí tại chung cư BlueMoon theo mô hình kiến trúc MVC.

## 1. Phân công thành viên và nhánh làm việc

| Thành viên | Họ và tên        | MSSV      | Vai trò chính                       | Nhánh Git |
| :--------- | :--------------- | :-------- | :---------------------------------- | :-------- |
| TV1        | Đoàn Quốc Khánh  | 202416243 | Quản lý dự án / Scrum Master        | tv1       |
| TV2        | Nguyễn Đình Dũng | 202416173 | Phân tích yêu cầu, Thiết kế UI, Dev | tv2       |
| TV3        | Đinh Phi Hùng    | 202416215 | Lập trình CSDL và nghiệp vụ         | tv3       |
| TV4        | Cao Thành Đạt    | 202416152 | Lập trình giao diện JavaFX          | tv4       |
| TV5        | Vũ Nhật Huy      | 202416229 | Kiểm thử và tài liệu                | tv5       |

## 2. Công nghệ sử dụng

- **Ngôn ngữ:** Java 17 trở lên, Maven, JavaFX 21.0.2 (Mô hình MVC)
- **Hệ quản trị CSDL:** Microsoft SQL Server và SQL Server Management Studio 22 (SSMS 22)
- **Quản lý mã nguồn:** Git và GitHub

## 3. Cấu trúc thư mục dự án

- docs/requirements/: Tài liệu Project Charter, SRS, Product Backlog
- docs/design/figma/: Sơ đồ ERD, biểu đồ lớp, thiết kế giao diện Figma
- scripts/bluemoon_db.sql: Kịch bản khởi tạo 7 bảng CSDL và dữ liệu mẫu trên SQL Server
- src/main/java/application/: Chứa lớp khởi chạy ứng dụng JavaFX (Main.java)
- src/main/java/models/: Các lớp thực thể ánh xạ bảng dữ liệu CSDL
- src/main/java/services/: Lớp kết nối DatabaseConnection.java và các lớp truy vấn SQL
- src/main/java/controller/: Các lớp điều khiển nghiệp vụ theo từng phân hệ (admin, hokhau, nhankhau, khoanthu, noptien)
- src/main/resources/views/: Các tệp giao diện .fxml và .css tương ứng với từng phân hệ
- test/: Kịch bản và dữ liệu kiểm thử hộp trắng, hộp đen

## 4. Hướng dẫn cài đặt SQL Server cho thành viên mới (Làm 1 lần duy nhất)

Khi kéo dự án về máy, mỗi thành viên cần thực hiện 4 bước sau để kết nối Java với SQL Server ở cổng 1433:

### Bước 1: Bật chế độ Mixed Mode và tài khoản sa trên SSMS 22

1. Mở SQL Server Management Studio 22 (SSMS 22), kết nối bằng Windows Authentication.
2. Nhấn Ctrl + N để mở cửa sổ New Query, dán đoạn lệnh dưới đây và nhấn F5:

```sql
USE [master];
GO
EXEC xp_instance_regwrite N'HKEY_LOCAL_MACHINE',
     N'Software\Microsoft\MSSQLServer\MSSQLServer',
     N'LoginMode', REG_DWORD, 2;
GO
ALTER LOGIN [sa] WITH PASSWORD = N'123456', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;
ALTER LOGIN [sa] ENABLE;
GO
```

### Bước 2: Mở cổng TCP/IP 1433 cho SQL Server

Nhấn tổ hợp phím Windows + R, gõ "compmgmt.msc" và nhấn Enter.

Ở cột bên trái, mở rộng mục Services and Applications -> chọn SQL Server Configuration Manager.

Mở rộng SQL Server Network Configuration -> chọn Protocols for MSSQLSERVER (hoặc SQLEXPRESS).

Nhấp đúp vào TCP/IP ở khung bên phải:

Tab Protocol: Đổi mục Enabled thành Yes.

Tab IP Addresses: Kéo xuống cuối cùng mục IPAll, xóa trống ô TCP Dynamic Ports và điền 1433 vào ô TCP Port -> nhấn OK.

Chọn mục SQL Server Services ở cột bên trái -> chuột phải vào SQL Server (MSSQLSERVER) -> chọn Restart.

### Bước 3: Khởi tạo cơ sở dữ liệu bluemoon_db

Trong SSMS 22, chọn File -> Open -> File... và mở tệp scripts/bluemoon_db.sql trong thư mục dự án.

Nhấn F5 (Execute) để tạo tự động CSDL bluemoon_db cùng 7 bảng và dữ liệu mẫu.

### Bước 4: Kiểm tra kết nối trên VS Code hoặc IntelliJ

Mở thư mục dự án bằng IDE, đợi Maven tải xong thư viện trong tệp pom.xml.

Mở tệp src/main/java/services/DatabaseConnection.java và chạy hàm main. Nếu cửa sổ Console hiện thông báo kết nối thành công và in ra danh sách tài khoản mẫu là hoàn tất.
