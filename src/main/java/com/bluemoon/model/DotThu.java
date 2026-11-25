package com.bluemoon.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Đợt chốt công nợ hàng tháng (US-009).
 *
 * @param id định danh đợt thu
 * @param thang tháng chốt công nợ (ngày 1 của tháng)
 * @param nguoiChot ID tài khoản người thực hiện chốt công nợ
 * @param ngayChot thời điểm thực hiện chốt công nợ
 */
public record DotThu(
        int id,
        LocalDate thang,
        int nguoiChot,
        LocalDateTime ngayChot) {
}
