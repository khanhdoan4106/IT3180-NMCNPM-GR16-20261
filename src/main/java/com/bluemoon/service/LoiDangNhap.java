package com.bluemoon.service;

/**
 * Không đăng nhập được: sai tên đăng nhập hoặc mật khẩu, hoặc tài khoản đã
 * bị khóa. {@link #getMessage()} là câu báo hiện ngay cho người dùng.
 */
public class LoiDangNhap extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tạo lỗi.
     *
     * @param thongDiep câu báo cho người dùng
     */
    public LoiDangNhap(String thongDiep) {
        super(thongDiep);
    }
}
