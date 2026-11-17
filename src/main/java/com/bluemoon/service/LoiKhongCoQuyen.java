package com.bluemoon.service;

/**
 * Người đang đăng nhập không được làm thao tác này, hoặc chưa đăng nhập
 * (giả định 17: Service kiểm tra lại vai trò ở mỗi thao tác). Câu báo
 * tiếng Việt hiện được cho người dùng.
 */
public class LoiKhongCoQuyen extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tạo lỗi với câu báo cho người dùng.
     *
     * @param cauBao câu báo tiếng Việt, nói rõ ai được làm
     */
    public LoiKhongCoQuyen(String cauBao) {
        super(cauBao);
    }
}
