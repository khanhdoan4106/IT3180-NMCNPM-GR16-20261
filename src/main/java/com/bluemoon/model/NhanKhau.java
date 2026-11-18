package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Thông tin cơ bản của nhân khẩu để hiển thị trên danh sách (US-002).
 *
 * @param id định danh nhân khẩu trong CSDL
 * @param hoId định danh hộ gia đình mà nhân khẩu này thuộc về
 * @param hoTen họ và tên nhân khẩu
 * @param ngaySinh ngày sinh
 * @param gioiTinh giới tính (NAM/NU)
 * @param soCccd số Căn cước công dân (có thể null nếu dưới 14 tuổi)
 * @param quanHe quan hệ với chủ hộ
 */
public record NhanKhau (
    int id,
        int hoId,
        String hoTen,
        LocalDate ngaySinh,
        String gioiTinh,
        String soCccd,
        String quanHe) {
}
