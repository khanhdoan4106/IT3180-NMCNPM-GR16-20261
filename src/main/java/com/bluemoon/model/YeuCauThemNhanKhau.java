package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Dữ liệu đầu vào khi thêm mới một nhân khẩu vào hộ (US-002).
 * Các trường tùy chọn bám sát Giả định 47.
 *
 * @param hoId định danh hộ gia đình (bắt buộc)
 * @param hoTen họ tên nhân khẩu (bắt buộc)
 * @param ngaySinh ngày sinh (bắt buộc)
 * @param gioiTinh giới tính (bắt buộc)
 * @param quanHe quan hệ với chủ hộ (bắt buộc)
 * @param soCccd số CCCD (bắt buộc nếu từ đủ 14 tuổi)
 * @param danToc dân tộc (tùy chọn - dữ liệu nhạy cảm)
 * @param tonGiao tôn giáo (tùy chọn - dữ liệu nhạy cảm)
 * @param ngheNghiep nghề nghiệp (tùy chọn)
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
        String ngheNghiep) {
}