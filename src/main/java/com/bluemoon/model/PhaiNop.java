package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Khoản phải nộp của một hộ trong một đợt thu đã chốt (US-009, US-008).
 *
 * @param id định danh khoản phải nộp
 * @param dotThuId mã đợt thu
 * @param hoId mã hộ gia đình
 * @param maCanHo mã căn hộ của hộ
 * @param tenChuHo họ tên chủ hộ
 * @param khoanPhiId mã khoản thu
 * @param tenKhoanPhi tên khoản thu
 * @param dienTich diện tích căn hộ lúc chốt (m²)
 * @param donGia đơn giá lúc chốt (đ/m²)
 * @param soTienGoc số tiền gốc tính tự động lúc chốt
 * @param soTien số tiền phải nộp hiện tại (sau điều chỉnh US-008 nếu có)
 * @param nguoiSua ID người sửa số tiền (nếu có)
 * @param ngaySua thời điểm sửa số tiền (nếu có)
 * @param ghiChu ghi chú lý do điều chỉnh
 */
public record PhaiNop(
        int id,
        int dotThuId,
        int hoId,
        String maCanHo,
        String tenChuHo,
        int khoanPhiId,
        String tenKhoanPhi,
        BigDecimal dienTich,
        BigDecimal donGia,
        BigDecimal soTienGoc,
        BigDecimal soTien,
        Integer nguoiSua,
        LocalDateTime ngaySua,
        String ghiChu) {
}
