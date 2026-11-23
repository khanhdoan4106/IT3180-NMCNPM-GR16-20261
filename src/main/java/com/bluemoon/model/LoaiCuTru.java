package com.bluemoon.model;

/**
 * Loại đăng ký cư trú, khớp cột {@code nhan_khau.loai_cu_tru} (giả định 8,
 * 13).
 */
public enum LoaiCuTru {
    THUONG_TRU("Thường trú"),
    TAM_TRU("Tạm trú");

    private final String nhan;

    LoaiCuTru(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Nhãn tiếng Việt hiển thị trên giao diện.
     *
     * @return tên loại cư trú có dấu
     */
    public String getNhan() {
        return nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}
