package com.bluemoon.model;

/**
 * Một dòng của bảng {@code tai_khoan}, dùng khi đăng nhập (US-021) và khi
 * người dùng xem, sửa thông tin của mình (US-023).
 *
 * @param id mã tài khoản
 * @param tenDangNhap tên đăng nhập như đã lưu
 * @param hoTen họ tên người dùng
 * @param vaiTro vai trò
 * @param matKhauBam mật khẩu đã băm BCrypt
 * @param hoatDong {@code false} khi tài khoản đã bị khóa
 * @param soDienThoai số điện thoại liên hệ, {@code null} khi chưa có
 */
public record TaiKhoan(int id, String tenDangNhap, String hoTen,
        VaiTro vaiTro, String matKhauBam, boolean hoatDong,
        String soDienThoai) {

    /**
     * Thông tin người dùng giữ trong phiên, không kèm mật khẩu băm.
     *
     * @return người dùng tương ứng
     */
    public NguoiDung nguoiDung() {
        return new NguoiDung(id, tenDangNhap, hoTen, vaiTro);
    }
}
