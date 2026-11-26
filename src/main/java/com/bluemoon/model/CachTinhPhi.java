package com.bluemoon.model;

/**
 * Cách tính tiền của khoản thu theo bảng {@code khoan_phi.cach_tinh}.
 */
public enum CachTinhPhi {

    /** Tính theo diện tích căn hộ (đ/m²), dùng cho khoản bắt buộc. */
    THEO_DIEN_TICH("Theo diện tích"),
    /** Cố định theo hộ, mỗi hộ đóng cùng một số tiền. */
    CO_DINH_THEO_HO("Cố định theo hộ"),
    /** Không cố định số tiền, tùy ý đóng góp kể cả 0 đ (AC-021). */
    KHONG_CO_DINH("Tùy tâm đóng góp");

    private final String ten;

    CachTinhPhi(String ten) {
        this.ten = ten;
    }

    /**
     * Tên hiển thị tiếng Việt có dấu.
     *
     * @return tên cách tính
     */
    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }
}
