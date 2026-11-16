package com.bluemoon.util;

/**
 * Danh sách màn hình của ứng dụng, cũng là danh sách mục trên menu trái
 * (theo đúng thứ tự khai báo, các mục cùng nhóm đứng liền nhau).
 *
 * <p>
 * Thêm màn hình mới: tạo tệp FXML trong
 * {@code src/main/resources/com/bluemoon/view/} rồi ghi tên tệp vào dòng
 * của màn hình đó. Mục còn {@code null} mở trang "Chức năng đang được xây
 * dựng". Xem docs/design/kien-truc.md.
 */
public enum ManHinh {

    /** Trang chính, mở khi vào ứng dụng hoặc bấm tên ứng dụng. */
    TRANG_CHINH("Trang chính", null, "trang-chinh.fxml"),

    // US-001
    HO_GIA_DINH("Hộ gia đình", NhomMenu.HO_GIA_DINH, "ho-gia-dinh.fxml"),
    // US-002
    NHAN_KHAU("Nhân khẩu", NhomMenu.HO_GIA_DINH, null),
    // US-003
    TAM_TRU_TAM_VANG("Tạm trú, tạm vắng", NhomMenu.HO_GIA_DINH, null),
    // US-004
    BAO_CAO_NHAN_KHAU("Báo cáo nhân khẩu", NhomMenu.HO_GIA_DINH, null),

    // US-005, US-006, US-008
    DANH_MUC_KHOAN_THU("Danh mục khoản thu", NhomMenu.KHOAN_THU, null),
    // US-009
    CHOT_CONG_NO("Chốt công nợ", NhomMenu.KHOAN_THU, null),
    // US-015
    THONG_BAO_PHI("Thông báo phí", NhomMenu.KHOAN_THU, null),

    // US-007, US-010, US-011, US-012
    THU_PHI("Thu phí", NhomMenu.THU_PHI, null),
    // US-013, US-014
    PHIEU_THU("Phiếu thu", NhomMenu.THU_PHI, null),

    // US-017
    NO_DONG("Hộ nợ đọng", NhomMenu.THONG_KE, null),
    // US-018, US-019
    THONG_KE_KHOAN_THU("Thống kê theo khoản thu", NhomMenu.THONG_KE, null),

    // US-020, US-022, US-023
    TAI_KHOAN("Tài khoản", NhomMenu.HE_THONG, null),
    // TS-002
    NHAT_KY("Nhật ký thao tác", NhomMenu.HE_THONG, null);

    private final String ten;
    private final NhomMenu nhom;
    private final String tepFxml;

    ManHinh(String ten, NhomMenu nhom, String tepFxml) {
        this.ten = ten;
        this.nhom = nhom;
        this.tepFxml = tepFxml;
    }

    /**
     * Tên trên menu, cũng là tiêu đề trang khi chưa có màn hình.
     *
     * @return tên tiếng Việt có dấu
     */
    public String getTen() {
        return ten;
    }

    /**
     * Nhóm trên menu.
     *
     * @return nhóm, {@code null} với mục đứng đầu menu (Trang chính)
     */
    public NhomMenu getNhom() {
        return nhom;
    }

    /**
     * Tên tệp FXML trong thư mục view.
     *
     * @return tên tệp, {@code null} khi màn hình chưa làm
     */
    public String getTepFxml() {
        return tepFxml;
    }
}
