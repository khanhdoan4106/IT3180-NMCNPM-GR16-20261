package com.bluemoon.util;

/** Các nhóm trên menu trái, theo mảng nghiệp vụ. */
public enum NhomMenu {

    /** Hộ gia đình, nhân khẩu, tạm trú/tạm vắng. */
    HO_GIA_DINH("Hộ gia đình"),
    /** Danh mục khoản thu, công nợ, thông báo phí. */
    KHOAN_THU("Khoản thu"),
    /** Tra cứu và thu tiền, phiếu thu. */
    THU_PHI("Thu phí"),
    /** Báo cáo, thống kê. */
    THONG_KE("Thống kê"),
    /** Tài khoản, nhật ký. */
    HE_THONG("Hệ thống");

    private final String ten;

    NhomMenu(String ten) {
        this.ten = ten;
    }

    /**
     * Tên nhóm hiện trên menu.
     *
     * @return tên tiếng Việt có dấu
     */
    public String getTen() {
        return ten;
    }
}
