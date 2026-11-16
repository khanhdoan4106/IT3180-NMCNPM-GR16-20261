package com.bluemoon.model;

/**
 * Một dòng của bảng {@code tai_khoan}, dùng khi đăng nhập (US-021).
 *
 * @param id mã tài khoản
 * @param tenDangNhap tên đăng nhập như đã lưu
 * @param hoTen họ tên người dùng
 * @param vaiTro vai trò
 * @param matKhauBam mật khẩu đã băm BCrypt
 * @param hoatDong {@code false} khi tài khoản đã bị khóa
 */
public record TaiKhoan(int id, String tenDangNhap, String hoTen,
        VaiTro vaiTro, String matKhauBam, boolean hoatDong) {

    /**
     * Thông tin người dùng giữ trong phiên, không kèm mật khẩu băm.
     *
     * @return người dùng tương ứng
     */
    public NguoiDung nguoiDung() {
        return new NguoiDung(id, tenDangNhap, hoTen, vaiTro);
    }
}
