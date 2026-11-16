-- Lược đồ CSDL BlueMoon cho MySQL 8.0 (utf8mb4).
-- Chạy bằng tài khoản quản trị (root). Chạy lại được nhiều lần: bảng đã có
-- thì giữ nguyên.

CREATE DATABASE IF NOT EXISTS bluemoon
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon;

-- 1. Tài khoản người dùng (US-020 -> US-023)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    passwd VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role INT NOT NULL DEFAULT 2,         -- 1: Quản trị viên, 2: Thủ quỹ
    status TINYINT NOT NULL DEFAULT 1,   -- 1: Hoạt động, 0: Khóa
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- 2. Hộ khẩu / căn hộ (US-001)
CREATE TABLE IF NOT EXISTS ho_khau (
    ma_ho INT AUTO_INCREMENT PRIMARY KEY,
    so_ho_khau VARCHAR(30) NOT NULL UNIQUE,
    ma_can_ho VARCHAR(20) NOT NULL UNIQUE,
    ten_chu_ho VARCHAR(100) NOT NULL,
    dien_tich DOUBLE NOT NULL DEFAULT 0,
    so_thanh_vien INT NOT NULL DEFAULT 1,
    dia_chi VARCHAR(200) DEFAULT 'Chung cư BlueMoon, Văn Phú',
    ngay_lap DATE NOT NULL DEFAULT (CURRENT_DATE)
) ENGINE = InnoDB;

-- 3. Nhân khẩu (US-002)
CREATE TABLE IF NOT EXISTS nhan_khau (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_ho INT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    bi_danh VARCHAR(100),
    ngay_sinh DATE NOT NULL,
    gioi_tinh VARCHAR(10) NOT NULL,
    noi_sinh VARCHAR(150),
    nguyen_quan VARCHAR(150),
    dan_toc VARCHAR(50) DEFAULT 'Kinh',
    ton_giao VARCHAR(50) DEFAULT 'Không',
    nghe_nghiep VARCHAR(100),
    noi_lam_viec VARCHAR(150),
    cccd VARCHAR(20) UNIQUE,
    ngay_cap DATE,
    noi_cap VARCHAR(150),
    quan_he_chu_ho VARCHAR(50),
    ngay_them DATE DEFAULT (CURRENT_DATE),
    trang_thai VARCHAR(30) DEFAULT 'Thường trú',
    ghi_chu TEXT,
    CONSTRAINT fk_nhankhau_hokhau FOREIGN KEY (ma_ho)
        REFERENCES ho_khau (ma_ho) ON DELETE SET NULL
) ENGINE = InnoDB;

-- 4. Lịch sử biến động nhân khẩu (US-027)
CREATE TABLE IF NOT EXISTS lich_su_nhan_khau (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_ho INT NOT NULL,
    id_nhan_khau INT NOT NULL,
    loai_thay_doi INT NOT NULL,  -- 1: Thêm mới, 2: Chuyển đi, 3: Đổi chủ hộ
    thoi_gian DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ghi_chu VARCHAR(255),
    CONSTRAINT fk_lichsu_hokhau FOREIGN KEY (ma_ho)
        REFERENCES ho_khau (ma_ho) ON DELETE CASCADE,
    CONSTRAINT fk_lichsu_nhankhau FOREIGN KEY (id_nhan_khau)
        REFERENCES nhan_khau (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 5. Tạm trú, tạm vắng (US-003)
CREATE TABLE IF NOT EXISTS tam_tru_tam_vang (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_nhan_khau INT NOT NULL,
    ma_giay VARCHAR(30) UNIQUE,
    trang_thai VARCHAR(20) NOT NULL,  -- 'Tạm trú' hoặc 'Tạm vắng'
    dia_chi_tam_tru_vang VARCHAR(200) NOT NULL,
    tu_ngay DATE NOT NULL,
    den_ngay DATE NOT NULL,
    noi_dung_de_nghi TEXT,
    CONSTRAINT fk_tttv_nhankhau FOREIGN KEY (id_nhan_khau)
        REFERENCES nhan_khau (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 6. Khoản thu (US-005, US-006, US-008)
CREATE TABLE IF NOT EXISTS khoan_thu (
    ma_khoan_thu INT AUTO_INCREMENT PRIMARY KEY,
    ten_khoan_thu VARCHAR(100) NOT NULL,
    loai_khoan_thu INT NOT NULL,  -- 0: Bắt buộc, 1: Tự nguyện (đóng góp)
    so_tien DOUBLE NOT NULL DEFAULT 0,
    don_vi_tinh VARCHAR(30) DEFAULT 'Hộ',  -- 'm2', 'Nhân khẩu', 'Hộ'
    ngay_tao DATE NOT NULL DEFAULT (CURRENT_DATE),
    thoi_han DATE,
    ghi_chu VARCHAR(255)
) ENGINE = InnoDB;

-- 7. Nộp tiền / phiếu thu (US-007, US-009 -> US-014)
CREATE TABLE IF NOT EXISTS nop_tien (
    id_nop_tien INT AUTO_INCREMENT PRIMARY KEY,
    ma_khoan_thu INT NOT NULL,
    ma_ho INT NOT NULL,
    so_tien_phai_thu DOUBLE NOT NULL DEFAULT 0,
    so_tien_da_nop DOUBLE NOT NULL DEFAULT 0,
    ngay_thu DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_nop VARCHAR(100),
    hinh_thuc_thanh_toan VARCHAR(30) DEFAULT 'Tiền mặt',
    trang_thai INT NOT NULL DEFAULT 1,  -- 0: Chưa nộp, 1: Đã nộp, 2: Đã hủy
    ghi_chu VARCHAR(255),
    CONSTRAINT fk_noptien_khoanthu FOREIGN KEY (ma_khoan_thu)
        REFERENCES khoan_thu (ma_khoan_thu) ON DELETE CASCADE,
    CONSTRAINT fk_noptien_hokhau FOREIGN KEY (ma_ho)
        REFERENCES ho_khau (ma_ho) ON DELETE CASCADE
) ENGINE = InnoDB;
