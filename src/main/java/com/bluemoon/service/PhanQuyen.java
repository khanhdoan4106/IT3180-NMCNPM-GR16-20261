package com.bluemoon.service;

import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.ManHinh;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lớp 2 của phân quyền (giả định 17, US-022): mỗi hàm Service đọc, ghi
 * dữ liệu nghiệp vụ gọi một hàm ở đây trước khi gọi DAO. Quyền mở màn
 * hình lấy từ {@link ManHinh} (một chỗ, theo docs/design/phan-quyen.md);
 * thao tác riêng của một phần màn hình ghi thành hằng ở lớp này.
 *
 * <p>Người đang đăng nhập lấy ở {@link PhienDangNhap}. Không đủ quyền thì
 * ném {@link LoiKhongCoQuyen}, không trả dữ liệu nào.
 */
public final class PhanQuyen {

    /**
     * Xem thẻ tổng thu tháng trên Trang chính (US-016). Chờ chốt họp 22/11
     * (phan-quyen.md mục 2); tạm chỉ Quản trị viên theo AC-060.
     */
    public static final Set<VaiTro> XEM_TONG_THU_THANG =
            Collections.unmodifiableSet(EnumSet.of(VaiTro.QUAN_TRI));

    /** Câu báo khi chưa đăng nhập. */
    static final String CHUA_DANG_NHAP = "Cần đăng nhập để làm việc này.";

    private PhanQuyen() {
    }

    /**
     * Người đang đăng nhập, bắt buộc đã đăng nhập.
     *
     * @return người đang đăng nhập
     * @throws LoiKhongCoQuyen khi chưa đăng nhập
     */
    public static NguoiDung daDangNhap() {
        return PhienDangNhap.nguoiDung().orElseThrow(
                () -> new LoiKhongCoQuyen(CHUA_DANG_NHAP));
    }

    /**
     * Người đang đăng nhập phải được mở màn hình chứa chức năng này.
     *
     * @param manHinh màn hình chứa chức năng
     * @return người đang đăng nhập
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc vai trò không được
     *         mở màn hình đó
     */
    public static NguoiDung yeuCau(ManHinh manHinh) {
        return yeuCau(manHinh.getVaiTro(), "Chức năng “" + manHinh.getTen()
                + "” chỉ dành cho " + tenVaiTro(manHinh.getVaiTro()) + ".");
    }

    /**
     * Người đang đăng nhập phải có một trong các vai trò cho trước.
     *
     * @param duocPhep các vai trò được làm
     * @param cauBao câu báo khi không đủ quyền, nói rõ ai được làm
     * @return người đang đăng nhập
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không đủ quyền
     */
    public static NguoiDung yeuCau(Set<VaiTro> duocPhep, String cauBao) {
        NguoiDung nguoiDung = daDangNhap();
        if (!duocPhep.contains(nguoiDung.vaiTro())) {
            throw new LoiKhongCoQuyen(cauBao);
        }
        return nguoiDung;
    }

    /**
     * Người đang đăng nhập phải là Quản trị viên (quản lý tài khoản, xem
     * nhật ký: phan-quyen.md mục 1.2).
     *
     * @param cauBao câu báo khi không phải Quản trị viên
     * @return người đang đăng nhập
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không phải Quản trị
     *         viên
     */
    public static NguoiDung chiQuanTriVien(String cauBao) {
        return yeuCau(EnumSet.of(VaiTro.QUAN_TRI), cauBao);
    }

    private static String tenVaiTro(Set<VaiTro> vaiTro) {
        return vaiTro.stream().map(VaiTro::getTen)
                .collect(Collectors.joining(" và "));
    }
}
