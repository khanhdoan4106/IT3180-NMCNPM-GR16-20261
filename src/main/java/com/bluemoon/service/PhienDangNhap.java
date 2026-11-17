package com.bluemoon.service;

import com.bluemoon.model.NguoiDung;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Người đang đăng nhập, dùng chung cho cả ứng dụng (US-021). Màn hình và
 * Service lấy người dùng ở đây; US-022 dựa vào vai trò để phân quyền.
 *
 * <p>Ứng dụng máy tính để bàn một người dùng một lúc, nên giữ một phiên
 * duy nhất. Chỉ dùng trên luồng giao diện JavaFX.
 */
public final class PhienDangNhap {

    private static NguoiDung hienTai;
    private static Consumer<NguoiDung> khiDoi = nguoiDung -> { };

    private PhienDangNhap() {
    }

    /**
     * Bắt đầu phiên sau khi đăng nhập đúng.
     *
     * @param nguoiDung người vừa đăng nhập
     */
    static void batDau(NguoiDung nguoiDung) {
        hienTai = nguoiDung;
    }

    /**
     * Cập nhật thông tin người đang đăng nhập (US-023: vừa sửa họ tên) và
     * báo cho nơi đang theo dõi, vd thanh đầu cửa sổ chính.
     *
     * @param nguoiDung thông tin mới, cùng mã tài khoản
     */
    static void capNhat(NguoiDung nguoiDung) {
        hienTai = nguoiDung;
        khiDoi.accept(nguoiDung);
    }

    /**
     * Đặt việc làm khi thông tin người đang đăng nhập thay đổi. Chỉ giữ một
     * nơi theo dõi (cửa sổ chính); gọi lại thì thay nơi cũ.
     *
     * @param xuLy việc làm, nhận thông tin mới
     */
    public static void khiDoiNguoiDung(Consumer<NguoiDung> xuLy) {
        khiDoi = xuLy == null ? nguoiDung -> { } : xuLy;
    }

    /** Kết thúc phiên (Đăng xuất). */
    public static void ketThuc() {
        hienTai = null;
    }

    /**
     * Người đang đăng nhập.
     *
     * @return rỗng khi chưa đăng nhập
     */
    public static Optional<NguoiDung> nguoiDung() {
        return Optional.ofNullable(hienTai);
    }

    /**
     * Đã có người đăng nhập chưa.
     *
     * @return {@code true} nếu đã đăng nhập
     */
    public static boolean daDangNhap() {
        return hienTai != null;
    }
}
