-- Dữ liệu mẫu để thử ứng dụng. Chạy sau schema.sql, bằng root. Chạy lại
-- được nhiều lần: dòng đã có thì bỏ qua.
-- Mật khẩu tài khoản mẫu đang để dạng chữ thường; đổi sang dạng băm khi làm
-- US-021 (đăng nhập).

USE bluemoon;

INSERT IGNORE INTO users (username, passwd, full_name, email, role) VALUES
    ('admin', '123456789', 'Đoàn Quốc Khánh', 'khanh.dq@bluemoon.vn', 1),
    ('ketoan', '123456789', 'Nguyễn Đình Dũng', 'dung.nd@bluemoon.vn', 2);

INSERT IGNORE INTO ho_khau
    (so_ho_khau, ma_can_ho, ten_chu_ho, dien_tich, so_thanh_vien, ngay_lap)
VALUES
    ('HK001', 'P1001', 'Phạm Tuấn Hiếu', 75.5, 4, '2023-06-01'),
    ('HK002', 'P1002', 'Nguyễn Quang Văn', 60.0, 3, '2023-06-15');

INSERT IGNORE INTO nhan_khau
    (ma_ho, ho_ten, ngay_sinh, gioi_tinh, cccd, quan_he_chu_ho)
SELECT h.ma_ho, v.ho_ten, v.ngay_sinh, 'Nam', v.cccd, 'Chủ hộ'
FROM (
    SELECT 'P1001' AS ma_can_ho, 'Phạm Tuấn Hiếu' AS ho_ten,
           DATE '1985-04-12' AS ngay_sinh, '001085012345' AS cccd
    UNION ALL
    SELECT 'P1002', 'Nguyễn Quang Văn', DATE '1990-08-20', '001090054321'
) AS v
JOIN ho_khau h ON h.ma_can_ho = v.ma_can_ho;

INSERT INTO khoan_thu
    (ten_khoan_thu, loai_khoan_thu, so_tien, don_vi_tinh, ngay_tao, thoi_han)
SELECT v.ten, v.loai, v.so_tien, v.don_vi, DATE '2026-11-01', v.thoi_han
FROM (
    SELECT 'Phí quản lý chung cư tháng 11/2026' AS ten, 0 AS loai,
           7000 AS so_tien, 'm2' AS don_vi, DATE '2026-11-30' AS thoi_han
    UNION ALL
    SELECT 'Phí dịch vụ chung cư tháng 11/2026', 0, 10000, 'm2',
           DATE '2026-11-30'
    UNION ALL
    SELECT 'Quỹ vì người nghèo năm 2026', 1, 0, 'Hộ', DATE '2026-12-31'
) AS v
WHERE NOT EXISTS (
    SELECT 1 FROM khoan_thu k WHERE k.ten_khoan_thu = v.ten
);
