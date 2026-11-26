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
 * @param loaiCuTruChuHo loại cư trú của chủ hộ (bắt buộc, giả định 10)
 * @param noiThuongTruChuHo nơi thường trú (bắt buộc khi chủ hộ tạm trú)
 * @param tamTruTu      ngày bắt đầu đợt tạm trú đầu tiên của chủ hộ
 * @param tamTruDen     ngày kết thúc đợt tạm trú đầu tiên của chủ hộ
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
        String cccdChuHo,
        LoaiCuTru loaiCuTruChuHo,
        String noiThuongTruChuHo,
        LocalDate tamTruTu,
        LocalDate tamTruDen) {

    /** Yêu cầu có chủ hộ thường trú. */
    public YeuCauThemHo(String maCanHo, LoaiCanHo loaiCanHo, Integer tang,
            BigDecimal dienTich, String soDienThoai, LocalDate ngayChuyenDen,
            String tenChuHo, LocalDate ngaySinhChuHo, String gioiTinhChuHo,
            String cccdChuHo) {
        this(maCanHo, loaiCanHo, tang, dienTich, soDienThoai, ngayChuyenDen,
                tenChuHo, ngaySinhChuHo, gioiTinhChuHo, cccdChuHo,
                LoaiCuTru.THUONG_TRU, null, null, null);
    }
}
