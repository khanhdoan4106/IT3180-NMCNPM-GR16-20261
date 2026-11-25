package com.bluemoon.model;

/**
 * Thao tác thay đổi dữ liệu được ghi vào nhật ký (TS-002). Tên hằng khớp
 * từng mã trong ràng buộc {@code chk_nky_thao_tac} của bảng
 * {@code nhat_ky} ({@code database/schema.sql}); nhãn tiếng Việt dùng để
 * hiển thị trên màn hình Nhật ký.
 */
public enum ThaoTac {
    LAP_PHIEU_THU("Lập phiếu thu"),
    HUY_PHIEU_THU("Hủy phiếu thu"),
    SUA_SO_TIEN_PHAI_NOP("Sửa số tiền phải nộp"),
    TAO_KHOAN_TU_NGUYEN("Tạo khoản thu tự nguyện"),
    SUA_DON_GIA("Sửa đơn giá"),
    CHOT_CONG_NO("Chốt công nợ"),
    THEM_HO("Thêm hộ"),
    SUA_HO("Sửa hộ"),
    HO_CHUYEN_DI("Hộ chuyển đi"),
    THEM_NHAN_KHAU("Thêm nhân khẩu"),
    SUA_NHAN_KHAU("Sửa nhân khẩu"),
    NHAN_KHAU_CHUYEN_DI("Nhân khẩu chuyển đi"),
    DOI_CHU_HO("Đổi chủ hộ"),
    GHI_TAM_TRU_TAM_VANG("Ghi tạm trú, tạm vắng"),
    TAO_TAI_KHOAN("Tạo tài khoản"),
    DOI_VAI_TRO("Đổi vai trò"),
    KHOA_TAI_KHOAN("Khóa tài khoản"),
    MO_KHOA_TAI_KHOAN("Mở khóa tài khoản"),
    DAT_LAI_MAT_KHAU("Đặt lại mật khẩu"),
    SUA_THONG_TIN_TAI_KHOAN("Sửa thông tin tài khoản"),
    DOI_MAT_KHAU("Đổi mật khẩu");

    private final String nhan;

    ThaoTac(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Nhãn tiếng Việt hiển thị trên giao diện.
     *
     * @return tên thao tác có dấu
     */
    public String getNhan() {
        return nhan;
    }

    /**
     * Tìm thao tác theo mã lưu trong cột {@code nhat_ky.thao_tac}.
     *
     * @param ma mã thao tác, ví dụ {@code THEM_HO}
     * @return thao tác tương ứng
     * @throws IllegalArgumentException khi mã không có trong danh sách
     */
    public static ThaoTac tuMa(String ma) {
        for (ThaoTac thaoTac : values()) {
            if (thaoTac.name().equals(ma)) {
                return thaoTac;
            }
        }
        throw new IllegalArgumentException(
                "Mã thao tác nhật ký không hợp lệ: " + ma);
    }

    @Override
    public String toString() {
        return nhan;
    }
}
