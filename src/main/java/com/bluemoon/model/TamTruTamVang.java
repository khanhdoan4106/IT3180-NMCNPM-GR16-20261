package com.bluemoon.model;

import java.time.LocalDate;

/** Dữ liệu hiển thị lịch sử tạm trú, tạm vắng. */
public record TamTruTamVang(
        int id,
        int nhanKhauId,
        String hoTen,
        String soCccd,
        String loai,
        String soGiay,
        LocalDate tuNgay,
        LocalDate denNgay,
        String lyDo,
        String diaChiTamVang) {

    /** Thông tin tóm tắt nhân khẩu trong hộ để chọn trên giao diện. */
    public record NguoiTrongHo(int id, String hoTen, String soCccd) {
    }
}