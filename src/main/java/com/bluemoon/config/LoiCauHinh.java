package com.bluemoon.config;

import java.sql.SQLException;

/**
 * Thiếu tệp {@code config/db.properties}, không đọc được tệp hoặc tệp thiếu
 * khóa bắt buộc. Tách riêng để màn hình báo lỗi nói đúng cách sửa.
 */
public class LoiCauHinh extends SQLException {

    private static final long serialVersionUID = 1L;

    /**
     * Tạo lỗi cấu hình.
     *
     * @param thongDiep mô tả cho người lập trình
     * @param nguyenNhan lỗi gốc, có thể {@code null}
     */
    public LoiCauHinh(String thongDiep, Throwable nguyenNhan) {
        super(thongDiep, nguyenNhan);
    }
}
