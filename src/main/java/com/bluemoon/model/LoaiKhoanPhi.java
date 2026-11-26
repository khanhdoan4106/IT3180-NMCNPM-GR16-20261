package com.bluemoon.model;

/**
 * Loại khoản thu theo bảng {@code khoan_phi.loai}.
 */
public enum LoaiKhoanPhi {

    /** Khoản bắt buộc hằng tháng (phí dịch vụ, phí quản lý). */
    BAT_BUOC("Bắt buộc"),
    /** Khoản đóng góp tự nguyện theo từng đợt (US-006). */
    TU_NGUYEN("Tự nguyện");

    private final String ten;

    LoaiKhoanPhi(String ten) {
        this.ten = ten;
    }

    /**
     * Tên hiển thị tiếng Việt có dấu.
     *
     * @return tên loại khoản phí
     */
    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }
}
