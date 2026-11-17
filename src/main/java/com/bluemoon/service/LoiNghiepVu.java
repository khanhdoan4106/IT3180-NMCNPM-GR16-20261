package com.bluemoon.service;

/**
 * Thao tác bị từ chối vì trái một quy tắc nghiệp vụ, không gắn với ô nhập
 * nào (vd tự khóa tài khoản của mình, giả định 18). Câu báo tiếng Việt hiện
 * được cho người dùng.
 */
public class LoiNghiepVu extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tạo lỗi với câu báo cho người dùng.
     *
     * @param cauBao câu báo tiếng Việt, nói rõ vì sao và cách làm
     */
    public LoiNghiepVu(String cauBao) {
        super(cauBao);
    }
}
