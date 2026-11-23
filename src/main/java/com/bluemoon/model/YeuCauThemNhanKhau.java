package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Dữ liệu đầu vào khi thêm mới một nhân khẩu vào hộ (US-002).
 * Các trường tùy chọn bám sát Giả định 47.
 *
 * @param hoId định danh hộ gia đình (bắt buộc)
 * @param hoTen họ tên nhân khẩu (bắt buộc)
 * @param ngaySinh ngày sinh (bắt buộc, không sau hôm nay)
 * @param gioiTinh giới tính (bắt buộc)
 * @param quanHe mã quan hệ với chủ hộ (bắt buộc)
 * @param soCccd số CCCD (bắt buộc nếu từ đủ 14 tuổi)
 * @param danToc dân tộc (tùy chọn - dữ liệu nhạy cảm)
 * @param tonGiao tôn giáo (tùy chọn - dữ liệu nhạy cảm)
 * @param ngheNghiep nghề nghiệp (tùy chọn)
 * @param loaiCuTru loại cư trú (bắt buộc)
 * @param noiThuongTru nơi thường trú (bắt buộc với người tạm trú)
 * @param tamTruTu ngày bắt đầu đợt tạm trú đầu tiên (giả định 9)
 * @param tamTruDen ngày kết thúc đợt tạm trú đầu tiên (giả định 9)
 */
public record YeuCauThemNhanKhau(
        int hoId,
        String hoTen,
        LocalDate ngaySinh,
        String gioiTinh,
        String quanHe,
        String soCccd,
        String danToc,
        String tonGiao,
        String ngheNghiep,
        LoaiCuTru loaiCuTru,
        String noiThuongTru,
        LocalDate tamTruTu,
        LocalDate tamTruDen) {

    /** Yêu cầu cho người thường trú. */
    public YeuCauThemNhanKhau(int hoId, String hoTen, LocalDate ngaySinh,
            String gioiTinh, String quanHe, String soCccd, String danToc,
            String tonGiao, String ngheNghiep) {
        this(hoId, hoTen, ngaySinh, gioiTinh, quanHe, soCccd, danToc,
                tonGiao, ngheNghiep, LoaiCuTru.THUONG_TRU, null, null, null);
    }
}
