package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Một dòng trong bảng {@code khoan_phi} (US-005, US-006).
 *
 * @param id định danh khoản thu
 * @param ten tên khoản thu
 * @param loai loại khoản bắt buộc hoặc tự nguyện
 * @param cachTinh cách tính tiền
 * @param donGia đơn giá hoặc số tiền cố định, {@code null} khi không cố định
 * @param tuNgay ngày bắt đầu đợt thu
 * @param denNgay ngày kết thúc đợt thu
 * @param trangThai trạng thái đang thu hoặc đã đóng
 * @param ghiChu ghi chú khoản thu
 */
public record KhoanPhi(
        int id,
        String ten,
        LoaiKhoanPhi loai,
        CachTinhPhi cachTinh,
        BigDecimal donGia,
        LocalDate tuNgay,
        LocalDate denNgay,
        TrangThaiKhoanPhi trangThai,
        String ghiChu) {
}
