package com.bluemoon.model;

/**
 * Loại căn hộ trong tòa nhà BlueMoon theo bảng {@code ho_gia_dinh}.
 */
public enum LoaiCanHo {
    CAN_HO("Căn hộ"),
    KIOT("Kiot"),
    PENTHOUSE("Penthouse");

    private final String nhan;

    LoaiCanHo(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Nhãn tiếng Việt hiển thị trên giao diện.
     *
     * @return tên loại căn hộ có dấu
     */
    public String getNhan() {
        return nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}
