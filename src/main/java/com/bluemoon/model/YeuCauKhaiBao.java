package com.bluemoon.model;

import java.time.LocalDate;

/** Dữ liệu đầu vào khi thêm khai báo tạm trú, tạm vắng mới. */
public record YeuCauKhaiBao(
        int nhanKhauId,
        String loai,
        String soGiay,
        LocalDate tuNgay,
        LocalDate denNgay,
        String lyDo,
        String diaChiTamVang) {
}