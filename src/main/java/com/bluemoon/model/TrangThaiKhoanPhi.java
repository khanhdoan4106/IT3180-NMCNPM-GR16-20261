package com.bluemoon.model;

/**
 * Trạng thái của khoản thu theo bảng {@code khoan_phi.trang_thai}.
 */
public enum TrangThaiKhoanPhi {

    /** Đang trong thời gian thu. */
    DANG_THU("Đang thu"),
    /** Đã kết thúc đợt thu. */
    DA_DONG("Đã đóng");

    private final String ten;

    TrangThaiKhoanPhi(String ten) {
        this.ten = ten;
    }

    /**
     * Tên hiển thị tiếng Việt có dấu.
     *
     * @return tên trạng thái
     */
    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }
}
