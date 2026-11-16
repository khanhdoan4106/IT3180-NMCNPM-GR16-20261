package com.bluemoon.model;

/**
 * Vai trò của tài khoản (giả định 2 trong 11-chot-va-gia-dinh.md). Tên
 * hằng trùng giá trị cột {@code tai_khoan.vai_tro}.
 */
public enum VaiTro {

    /** Ban quản trị: dùng mọi chức năng, tạo tài khoản. */
    QUAN_TRI("Quản trị viên"),
    /** Người thu phí. */
    THU_QUY("Thủ quỹ");

    private final String ten;

    VaiTro(String ten) {
        this.ten = ten;
    }

    /**
     * Tên hiện cho người dùng.
     *
     * @return tên tiếng Việt có dấu
     */
    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }
}
