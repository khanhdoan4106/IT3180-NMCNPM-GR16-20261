package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dữ liệu yêu cầu tạo một khoản thu tự nguyện mới (US-006, AC-019).
 *
 * @param ten tên khoản thu tự nguyện (VD: Quỹ vì người nghèo)
 * @param cachTinh cách tính (cố định theo hộ hoặc không cố định)
 * @param donGia số tiền nếu cố định, {@code null} khi tùy tâm đóng góp
 * @param tuNgay ngày bắt đầu đợt thu
 * @param denNgay ngày kết thúc đợt thu (bắt buộc theo CSDL)
 * @param ghiChu ghi chú khoản thu, {@code null} khi không có
 */
public record YeuCauTaoKhoanTuNguyen(
        String ten,
        CachTinhPhi cachTinh,
        BigDecimal donGia,
        LocalDate tuNgay,
        LocalDate denNgay,
        String ghiChu) {
}
