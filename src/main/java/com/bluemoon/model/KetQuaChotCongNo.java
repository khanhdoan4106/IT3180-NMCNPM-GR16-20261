package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Kết quả thực hiện chốt công nợ cho một tháng (US-009).
 *
 * @param dotThuId mã đợt thu được tạo hoặc đã có
 * @param thang tháng chốt công nợ
 * @param soHoDuocChot số hộ gia đình có khoản công nợ
 * @param soKhoanTaoMoi số khoản phải nộp mới được tạo
 * @param soKhoanBoQua số khoản đã tồn tại từ trước (khi chốt lại)
 * @param tongTien tổng số tiền công nợ của đợt thu
 * @param danhSachCanhBao danh sách các cảnh báo (vd bỏ qua hộ thiếu diện tích)
 */
public record KetQuaChotCongNo(
        int dotThuId,
        LocalDate thang,
        int soHoDuocChot,
        int soKhoanTaoMoi,
        int soKhoanBoQua,
        BigDecimal tongTien,
        List<String> danhSachCanhBao) {
}
