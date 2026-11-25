package com.bluemoon.util;

import com.bluemoon.model.VaiTro;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Danh sách màn hình của ứng dụng, cũng là danh sách mục trên menu trái
 * (theo đúng thứ tự khai báo, các mục cùng nhóm đứng liền nhau).
 *
 * <p>Mỗi màn hình ghi luôn vai trò được mở (US-022, bảng mục 1.1 của
 * docs/design/phan-quyen.md). Đây là chỗ duy nhất ghi quyền mở màn hình:
 * menu chỉ hiện mục vai trò được mở, {@link DieuHuong} từ chối mở màn
 * không có quyền, và Service kiểm lại bằng
 * {@code PhanQuyen.yeuCau(ManHinh)}.
 *
 * <p>Thêm màn hình mới: tạo tệp FXML trong
 * {@code src/main/resources/com/bluemoon/view/} rồi ghi tên tệp vào dòng
 * của màn hình đó. Mục còn {@code null} mở trang "Chức năng đang được xây
 * dựng". Xem docs/design/kien-truc.md.
 */
public enum ManHinh {

    /**
     * Trang chính, mở khi vào ứng dụng hoặc bấm tên ứng dụng. Thẻ tổng thu
     * tháng (US-016) trên trang này cả hai vai trò xem được (giả định 51),
     * xem {@code PhanQuyen.XEM_TONG_THU_THANG}.
     */
    TRANG_CHINH("Trang chính", null, "trang-chinh.fxml",
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),

    // US-001
    HO_GIA_DINH("Hộ gia đình", NhomMenu.HO_GIA_DINH, 
    "ho-gia-dinh.fxml", VaiTro.QUAN_TRI),
    // US-002
    NHAN_KHAU("Nhân khẩu", NhomMenu.HO_GIA_DINH, 
    "nhan-khau.fxml", VaiTro.QUAN_TRI),
    // US-003
    TAM_TRU_TAM_VANG("Tạm trú, tạm vắng", NhomMenu.HO_GIA_DINH,
     "tam-tru-tam-vang.fxml",
            VaiTro.QUAN_TRI),
    // US-004
    BAO_CAO_NHAN_KHAU("Báo cáo nhân khẩu", NhomMenu.HO_GIA_DINH, null,
            VaiTro.QUAN_TRI),

    // US-005 (đổi đơn giá), US-006. Chỉ Quản trị viên (giả định 51).
    DANH_MUC_KHOAN_THU("Danh mục khoản thu", NhomMenu.KHOAN_THU,
            "danh-muc-khoan-thu.fxml", VaiTro.QUAN_TRI),
    // US-009 chốt công nợ, US-008 sửa khoản phải nộp của hộ trước khi hộ
    // đóng. Thủ quỹ được dùng (giả định 51).
    CHOT_CONG_NO("Chốt công nợ", NhomMenu.KHOAN_THU, null,
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),
    // US-015
    THONG_BAO_PHI("Thông báo phí", NhomMenu.KHOAN_THU, null,
            VaiTro.QUAN_TRI),

    // US-007, US-010, US-011, US-012 (AC-060: thu phí và tra cứu)
    THU_PHI("Thu phí", NhomMenu.THU_PHI, null,
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),
    // US-013, US-014 (Thủ quỹ chỉ hủy phiếu mình lập, giả định 28)
    PHIEU_THU("Phiếu thu", NhomMenu.THU_PHI, null,
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),

    // US-017
    NO_DONG("Hộ nợ đọng", NhomMenu.THONG_KE, null, VaiTro.QUAN_TRI),
    // US-019
    THONG_KE_KHOAN_THU("Thống kê theo khoản thu", NhomMenu.THONG_KE, null,
            VaiTro.QUAN_TRI),
    // US-018. Thủ quỹ được xuất báo cáo thu (giả định 51).
    BAO_CAO_THU("Báo cáo thu", NhomMenu.THONG_KE, null,
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),

    // US-020, US-022, US-023. Thủ quỹ chỉ thấy tab "Tài khoản của tôi".
    TAI_KHOAN("Tài khoản", NhomMenu.HE_THONG, "tai-khoan.fxml",
            VaiTro.QUAN_TRI, VaiTro.THU_QUY),
    // TS-002
    NHAT_KY("Nhật ký thao tác", NhomMenu.HE_THONG, "nhat-ky.fxml",
            VaiTro.QUAN_TRI);

    private final String ten;
    private final NhomMenu nhom;
    private final String tepFxml;
    private final Set<VaiTro> vaiTro;

    ManHinh(String ten, NhomMenu nhom, String tepFxml,
            VaiTro... vaiTro) {
        this.ten = ten;
        this.nhom = nhom;
        this.tepFxml = tepFxml;
        EnumSet<VaiTro> tap = EnumSet.noneOf(VaiTro.class);
        Collections.addAll(tap, vaiTro);
        this.vaiTro = Collections.unmodifiableSet(tap);
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

    /**
     * Các vai trò được mở màn hình này.
     *
     * @return tập chỉ đọc
     */
    public Set<VaiTro> getVaiTro() {
        return vaiTro;
    }

    /**
     * Vai trò này có được mở màn hình không.
     *
     * @param vt vai trò người đang đăng nhập, có thể {@code null}
     * @return {@code true} nếu được mở
     */
    public boolean duocMo(VaiTro vt) {
        return vt != null && vaiTro.contains(vt);
    }
}
