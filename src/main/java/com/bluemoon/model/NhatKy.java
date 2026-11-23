package com.bluemoon.model;

import java.time.LocalDateTime;

/**
 * Một dòng nhật ký thao tác (TS-002). Dữ liệu chỉ đọc sau khi được ghi.
 *
 * @param id mã nhật ký
 * @param thoiDiem thời điểm theo giờ máy chủ CSDL
 * @param tenDangNhap người thực hiện
 * @param thaoTac mã thao tác nghiệp vụ
 * @param doiTuong đối tượng bị tác động
 * @param noiDung mô tả nội dung thay đổi
 */
public record NhatKy(int id, LocalDateTime thoiDiem, String tenDangNhap,
        String thaoTac, String doiTuong, String noiDung) {
}
