package com.bluemoon.model;

/**
 * Quan hệ với chủ hộ, khớp cột {@code nhan_khau.quan_he} (AC-006).
 */
public enum QuanHe {
    CHU_HO("Chủ hộ"),
    VO_CHONG("Vợ/Chồng"),
    CON("Con"),
    BO_ME("Bố/Mẹ"),
    KHAC("Khác");

    private final String nhan;

    QuanHe(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Nhãn tiếng Việt hiển thị trên giao diện.
     *
     * @return tên quan hệ có dấu
     */
    public String getNhan() {
        return nhan;
    }

    /**
     * Tìm quan hệ theo mã lưu trong CSDL.
     *
     * @param ma mã quan hệ, ví dụ {@code BO_ME}
     * @return quan hệ tương ứng
     * @throws IllegalArgumentException khi mã không hợp lệ
     */
    public static QuanHe tuMa(String ma) {
        for (QuanHe quanHe : values()) {
            if (quanHe.name().equals(ma)) {
                return quanHe;
            }
        }
        throw new IllegalArgumentException("Quan hệ không hợp lệ.");
    }

    @Override
    public String toString() {
        return nhan;
    }
}
