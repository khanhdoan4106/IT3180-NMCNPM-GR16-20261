package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Thông tin hộ gia đình kèm tên chủ hộ và số thành viên đang ở.
 *
 * @param id            định danh hộ gia đình
 * @param maCanHo       mã căn hộ
 * @param loaiCanHo     loại căn hộ
 * @param tang          số tầng (1..30)
 * @param dienTich      diện tích căn hộ (m²)
 * @param tenChuHo      họ tên chủ hộ hiện tại
 * @param soDienThoai   số điện thoại liên hệ của hộ
 * @param soThanhVien   số nhân khẩu đang ở trong hộ
 * @param ngayChuyenDen ngày hộ chuyển đến
 */
public record HoGiaDinh(
        int id,
        String maCanHo,
        LoaiCanHo loaiCanHo,
        int tang,
        BigDecimal dienTich,
        String tenChuHo,
        String soDienThoai,
        int soThanhVien,
        LocalDate ngayChuyenDen) {
}