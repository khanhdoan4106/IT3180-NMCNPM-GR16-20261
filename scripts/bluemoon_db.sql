USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'bluemoon_db')
BEGIN
    CREATE DATABASE bluemoon_db;
END
GO

USE bluemoon_db;
GO

-- 1. Bảng tài khoản người dùng (US-020 -> US-023)
IF OBJECT_ID('dbo.users', 'U') IS NULL
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    passwd VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role INT NOT NULL DEFAULT 2,      -- 1: Admin/Tổ trưởng, 2: Kế toán
    status TINYINT NOT NULL DEFAULT 1, -- 1: Hoạt động, 0: Khóa
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- 2. Bảng hộ khẩu / căn hộ (US-001)
IF OBJECT_ID('dbo.ho_khau', 'U') IS NULL
CREATE TABLE ho_khau (
    ma_ho INT IDENTITY(1,1) PRIMARY KEY,
    so_ho_khau VARCHAR(30) NOT NULL UNIQUE,
    ma_can_ho VARCHAR(20) NOT NULL UNIQUE,
    ten_chu_ho NVARCHAR(100) NOT NULL,
    dien_tich FLOAT NOT NULL DEFAULT 0,
    so_thanh_vien INT NOT NULL DEFAULT 1,
    dia_chi NVARCHAR(200) DEFAULT N'Chung cư BlueMoon, Văn Phú',
    ngay_lap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE)
);
GO

-- 3. Bảng nhân khẩu (US-002)
IF OBJECT_ID('dbo.nhan_khau', 'U') IS NULL
CREATE TABLE nhan_khau (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_ho INT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    bi_danh NVARCHAR(100),
    ngay_sinh DATE NOT NULL,
    gioi_tinh NVARCHAR(10) NOT NULL,
    noi_sinh NVARCHAR(150),
    nguyen_quan NVARCHAR(150),
    dan_toc NVARCHAR(50) DEFAULT N'Kinh',
    ton_giao NVARCHAR(50) DEFAULT N'Không',
    nghe_nghiep NVARCHAR(100),
    noi_lam_viec NVARCHAR(150),
    cccd VARCHAR(20) UNIQUE,
    ngay_cap DATE,
    noi_cap NVARCHAR(150),
    quan_he_chu_ho NVARCHAR(50),
    ngay_them DATE DEFAULT CAST(GETDATE() AS DATE),
    trang_thai NVARCHAR(30) DEFAULT N'Thường trú',
    ghi_chu NVARCHAR(MAX),
    CONSTRAINT fk_nhankhau_hokhau FOREIGN KEY (ma_ho) REFERENCES ho_khau(ma_ho) ON DELETE SET NULL
);
GO

-- 4. Bảng lịch sử biến động nhân khẩu (US-002, US-004)
IF OBJECT_ID('dbo.lich_su_nhan_khau', 'U') IS NULL
CREATE TABLE lich_su_nhan_khau (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_ho INT NOT NULL,
    id_nhan_khau INT NOT NULL,
    loai_thay_doi INT NOT NULL, -- 1: Thêm mới, 2: Chuyển đi, 3: Đổi chủ hộ
    thoi_gian DATETIME DEFAULT GETDATE(),
    ghi_chu NVARCHAR(255),
    CONSTRAINT fk_lichsu_hokhau FOREIGN KEY (ma_ho) REFERENCES ho_khau(ma_ho) ON DELETE CASCADE,
    CONSTRAINT fk_lichsu_nhankhau FOREIGN KEY (id_nhan_khau) REFERENCES nhan_khau(id) ON DELETE CASCADE
);
GO

-- 5. Bảng tạm trú tạm vắng (US-003 - Nhiệm vụ của TV2 ở Sprint 1)
IF OBJECT_ID('dbo.tam_tru_tam_vang', 'U') IS NULL
CREATE TABLE tam_tru_tam_vang (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_nhan_khau INT NOT NULL,
    ma_giay VARCHAR(30) UNIQUE,
    trang_thai NVARCHAR(20) NOT NULL, -- N'Tạm trú' hoặc N'Tạm vắng'
    dia_chi_tam_tru_vang NVARCHAR(200) NOT NULL,
    tu_ngay DATE NOT NULL,
    den_ngay DATE NOT NULL,
    noi_dung_de_nghi NVARCHAR(MAX),
    CONSTRAINT fk_tttv_nhankhau FOREIGN KEY (id_nhan_khau) REFERENCES nhan_khau(id) ON DELETE CASCADE
);
GO

-- 6. Bảng khoản thu (US-005, US-006, US-008 - Nhiệm vụ của TV2 ở Sprint 2)
IF OBJECT_ID('dbo.khoan_thu', 'U') IS NULL
CREATE TABLE khoan_thu (
    ma_khoan_thu INT IDENTITY(1,1) PRIMARY KEY,
    ten_khoan_thu NVARCHAR(100) NOT NULL,
    loai_khoan_thu INT NOT NULL, -- 0: Bắt buộc, 1: Tự nguyện (Đóng góp)
    so_tien FLOAT NOT NULL DEFAULT 0,
    don_vi_tinh NVARCHAR(30) DEFAULT N'Hộ', -- N'm2', N'Nhân khẩu', N'Hộ'
    ngay_tao DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    thoi_han DATE,
    ghi_chu NVARCHAR(255)
);
GO

-- 7. Bảng nộp tiền / phiếu thu (US-007, US-009 -> US-014)
IF OBJECT_ID('dbo.nop_tien', 'U') IS NULL
CREATE TABLE nop_tien (
    id_nop_tien INT IDENTITY(1,1) PRIMARY KEY,
    ma_khoan_thu INT NOT NULL,
    ma_ho INT NOT NULL,
    so_tien_phai_thu FLOAT NOT NULL DEFAULT 0,
    so_tien_da_nop FLOAT NOT NULL DEFAULT 0,
    ngay_thu DATETIME DEFAULT GETDATE(),
    nguoi_nop NVARCHAR(100),
    hinh_thuc_thanh_toan NVARCHAR(30) DEFAULT N'Tiền mặt',
    trang_thai INT NOT NULL DEFAULT 1, -- 0: Chưa nộp, 1: Đã nộp, 2: Đã hủy
    ghi_chu NVARCHAR(255),
    CONSTRAINT fk_noptien_khoanthu FOREIGN KEY (ma_khoan_thu) REFERENCES khoan_thu(ma_khoan_thu) ON DELETE CASCADE,
    CONSTRAINT fk_noptien_hokhau FOREIGN KEY (ma_ho) REFERENCES ho_khau(ma_ho) ON DELETE CASCADE
);
GO

-- Thêm dữ liệu mẫu kiểm thử (chỉ thêm khi bảng còn trống)
IF NOT EXISTS (SELECT 1 FROM users)
BEGIN
    INSERT INTO users (username, passwd, full_name, email, role) VALUES
    ('admin', '123456789', N'Đoàn Quốc Khánh', 'khanh.dq@bluemoon.vn', 1),
    ('ketoan', '123456789', N'Nguyễn Đình Dũng', 'dung.nd@bluemoon.vn', 2);

    INSERT INTO ho_khau (so_ho_khau, ma_can_ho, ten_chu_ho, dien_tich, so_thanh_vien, ngay_lap) VALUES
    ('HK001', 'P1001', N'Phạm Tuấn Hiếu', 75.5, 4, '2023-06-01'),
    ('HK002', 'P1002', N'Nguyễn Quang Văn', 60.0, 3, '2023-06-15');

    INSERT INTO nhan_khau (ma_ho, ho_ten, ngay_sinh, gioi_tinh, cccd, quan_he_chu_ho) VALUES
    (1, N'Phạm Tuấn Hiếu', '1985-04-12', N'Nam', '001085012345', N'Chủ hộ'),
    (2, N'Nguyễn Quang Văn', '1990-08-20', N'Nam', '001090054321', N'Chủ hộ');

    INSERT INTO khoan_thu (ten_khoan_thu, loai_khoan_thu, so_tien, don_vi_tinh, ngay_tao, thoi_han) VALUES
    (N'Phí quản lý chung cư tháng 11/2026', 0, 7000, N'm2', '2026-11-01', '2026-11-30'),
    (N'Phí dịch vụ chung cư tháng 11/2026', 0, 10000, N'm2', '2026-11-01', '2026-11-30'),
    (N'Quỹ vì người nghèo năm 2026', 1, 0, N'Hộ', '2026-11-01', '2026-12-31');
END
GO