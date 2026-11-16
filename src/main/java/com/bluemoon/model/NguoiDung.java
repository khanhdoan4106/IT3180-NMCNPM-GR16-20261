package com.bluemoon.model;

/**
 * Người đang đăng nhập (US-021). Không giữ mật khẩu hay chuỗi băm.
 *
 * @param id mã tài khoản
 * @param tenDangNhap tên đăng nhập
 * @param hoTen họ tên, hiện ở thanh đầu
 * @param vaiTro vai trò, US-022 dùng để phân quyền
 */
public record NguoiDung(int id, String tenDangNhap, String hoTen,
        VaiTro vaiTro) {
}
