package com.bluemoon.dao;

import com.bluemoon.config.LoiCauHinh;
import java.sql.SQLException;

/**
 * Lỗi truy cập CSDL, dùng chung cho mọi DAO. DAO bắt {@link SQLException}
 * và ném lỗi này; {@link #getMessage()} là câu báo tiếng Việt hiện được
 * ngay cho người dùng, lỗi gốc giữ ở {@link #getCause()} để gỡ lỗi.
 */
public class LoiCsdl extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Mã lỗi MySQL: sai tên đăng nhập hoặc mật khẩu. */
    static final int MA_SAI_DANG_NHAP = 1045;
    /** Mã lỗi MySQL: không có quyền trên CSDL. */
    static final int MA_KHONG_QUYEN_CSDL = 1044;
    /** Mã lỗi MySQL: không có quyền trên bảng. */
    static final int MA_KHONG_QUYEN_BANG = 1142;
    /** Mã lỗi MySQL: CSDL không tồn tại. */
    static final int MA_KHONG_CO_CSDL = 1049;
    /** Mã lỗi MySQL: bảng không tồn tại. */
    static final int MA_KHONG_CO_BANG = 1146;

    /**
     * Tạo lỗi với câu báo mặc định của loại lỗi.
     *
     * @param loai loại lỗi
     * @param nguyenNhan lỗi gốc, có thể {@code null}
     */
    public LoiCsdl(LoaiLoiCsdl loai, Throwable nguyenNhan) {
        super(loai.thongDiep(), nguyenNhan);
    }

    /**
     * Đổi một {@link SQLException} thành lỗi có câu báo cho người dùng.
     *
     * @param loi lỗi từ JDBC
     * @return lỗi đã phân loại, giữ {@code loi} làm nguyên nhân
     */
    public static LoiCsdl tu(SQLException loi) {
        return new LoiCsdl(phanLoai(loi), loi);
    }

    /**
     * Phân loại lỗi theo mã lỗi MySQL và SQLState (nhóm {@code 08} là lỗi
     * đường kết nối).
     *
     * @param loi lỗi từ JDBC
     * @return loại lỗi
     */
    static LoaiLoiCsdl phanLoai(SQLException loi) {
        if (loi instanceof LoiCauHinh) {
            return LoaiLoiCsdl.THIEU_CAU_HINH;
        }
        switch (loi.getErrorCode()) {
            case MA_SAI_DANG_NHAP:
                return LoaiLoiCsdl.SAI_DANG_NHAP;
            case MA_KHONG_QUYEN_CSDL:
            case MA_KHONG_QUYEN_BANG:
                return LoaiLoiCsdl.KHONG_CO_QUYEN;
            case MA_KHONG_CO_CSDL:
            case MA_KHONG_CO_BANG:
                return LoaiLoiCsdl.CHUA_DUNG_CSDL;
            default:
                break;
        }
        String trangThai = loi.getSQLState();
        if (trangThai != null && trangThai.startsWith("08")) {
            return LoaiLoiCsdl.KHONG_TOI_MAY_CHU;
        }
        return LoaiLoiCsdl.KHAC;
    }
}
