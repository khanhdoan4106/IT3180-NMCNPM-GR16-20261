package com.bluemoon.model;

/**
 * Tài khoản sắp ghi vào CSDL (US-020). Mật khẩu đã băm, không bao giờ giữ
 * mật khẩu gốc.
 *
 * @param tenDangNhap tên đăng nhập đã bỏ khoảng trắng hai đầu
 * @param hoTen họ tên người dùng
 * @param vaiTro vai trò
 * @param matKhauBam mật khẩu đã băm BCrypt
 */
public record TaiKhoanMoi(String tenDangNhap, String hoTen, VaiTro vaiTro,
        String matKhauBam) {
}
