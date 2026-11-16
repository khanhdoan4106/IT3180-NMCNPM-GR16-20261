package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dữ liệu đầu vào khi thêm mới một hộ gia đình kèm chủ hộ (US-001).
 *
 * @param maCanHo       mã căn hộ (bắt buộc)
 * @param loaiCanHo     loại căn hộ (bắt buộc)
 * @param tang          tầng từ 1 đến 30 (bắt buộc)
 * @param dienTich      diện tích m² lớn hơn 0 (bắt buộc)
 * @param soDienThoai   số điện thoại hộ (tùy chọn)
 * @param ngayChuyenDen ngày chuyển đến (bắt buộc)
 * @param tenChuHo      họ tên chủ hộ (bắt buộc)
 * @param ngaySinhChuHo ngày sinh chủ hộ (bắt buộc)
 * @param gioiTinhChuHo giới tính chủ hộ: {@code NAM} hoặc {@code NU}
 * @param cccdChuHo     số CCCD 12 chữ số của chủ hộ (bắt buộc nếu đủ 14 tuổi)
 */
public record YeuCauThemHo(
        String maCanHo,
        LoaiCanHo loaiCanHo,
        Integer tang,
        BigDecimal dienTich,
        String soDienThoai,
        LocalDate ngayChuyenDen,
        String tenChuHo,
        LocalDate ngaySinhChuHo,
        String gioiTinhChuHo,
        String cccdChuHo) {
}