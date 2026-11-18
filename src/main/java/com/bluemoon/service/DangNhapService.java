package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.util.MatKhau;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * Đăng nhập, đăng xuất (US-021) và đọc lại phiên trước mỗi lần mở màn hình
 * (US-022, giả định 17).
 */
public class DangNhapService {

    /** Kết quả đọc lại phiên đăng nhập từ CSDL (giả định 17). */
    public enum KetQuaPhien {
        /** Tài khoản không đổi gì. */
        KHONG_DOI,
        /** Họ tên hoặc vai trò vừa đổi; phiên đã cập nhật. */
        DA_DOI,
        /** Tài khoản vừa bị khóa hoặc không còn; phiên đã kết thúc. */
        DA_KET_THUC
    }

    /** Câu báo trên màn đăng nhập khi đang dùng thì tài khoản bị khóa. */
    public static final String BI_KHOA_KHI_DANG_DUNG = "Tài khoản của bạn "
            + "vừa bị khóa nên đã được đăng xuất. Liên hệ Quản trị viên để "
            + "được mở lại.";

    /** Các ô của màn đăng nhập. */
    public enum O {
        /** Tên đăng nhập. */
        TEN_DANG_NHAP,
        /** Mật khẩu. */
        MAT_KHAU
    }

    /**
     * Sai tên hay sai mật khẩu đều báo chung một câu, không cho biết sai
     * phần nào (AC-058).
     */
    public static final String SAI_THONG_TIN =
            "Tên đăng nhập hoặc mật khẩu không đúng.";

    /** Tài khoản bị khóa (hoat_dong = false, giả định 18). */
    public static final String BI_KHOA = "Tài khoản này đã bị khóa. "
            + "Liên hệ Quản trị viên để được mở lại.";

    /**
     * Chuỗi băm giả: khi không có tên đăng nhập vẫn kiểm tra mật khẩu với
     * chuỗi này, để thời gian trả lời như khi sai mật khẩu, không đoán
     * được tên nào có thật.
     */
    private static final String BAM_GIA =
            MatKhau.bam(UUID.randomUUID().toString());

    private final TaiKhoanDao taiKhoanDao;
    private final BiPredicate<String, String> khopMatKhau;

    /**
     * Tạo Service, kiểm tra mật khẩu bằng {@link MatKhau#khop}.
     *
     * @param taiKhoanDao DAO tài khoản
     */
    public DangNhapService(TaiKhoanDao taiKhoanDao) {
        this(taiKhoanDao, MatKhau::khop);
    }

    /**
     * Tạo Service với cách kiểm tra mật khẩu khác (test).
     *
     * @param taiKhoanDao DAO tài khoản
     * @param khopMatKhau (mật khẩu, chuỗi băm) → có khớp không
     */
    DangNhapService(TaiKhoanDao taiKhoanDao,
            BiPredicate<String, String> khopMatKhau) {
        this.taiKhoanDao = taiKhoanDao;
        this.khopMatKhau = khopMatKhau;
    }

    /**
     * Đăng nhập (AC-057). Đúng thì bắt đầu {@link PhienDangNhap}; sai thì
     * phiên giữ nguyên là chưa đăng nhập (AC-058).
     *
     * @param tenDangNhap tên đăng nhập, bỏ khoảng trắng hai đầu
     * @param matKhau mật khẩu, giữ nguyên như đã gõ
     * @return người vừa đăng nhập
     * @throws LoiNhapLieu khi để trống ô
     * @throws LoiDangNhap khi sai thông tin hoặc tài khoản bị khóa
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public NguoiDung dangNhap(String tenDangNhap, String matKhau) {
        PhienDangNhap.ketThuc();
        String ten = tenDangNhap == null ? "" : tenDangNhap.strip();
        String mk = matKhau == null ? "" : matKhau;
        Map<O, String> loi = new LinkedHashMap<>();
        if (ten.isEmpty()) {
            loi.put(O.TEN_DANG_NHAP, "Nhập tên đăng nhập.");
        }
        if (mk.isEmpty()) {
            loi.put(O.MAT_KHAU, "Nhập mật khẩu.");
        }
        if (!loi.isEmpty()) {
            throw new LoiNhapLieu(loi);
        }

        Optional<TaiKhoan> timThay = taiKhoanDao.timTheoTenDangNhap(ten);
        boolean khop = khopMatKhau.test(mk,
                timThay.map(TaiKhoan::matKhauBam).orElse(BAM_GIA));
        if (timThay.isEmpty() || !khop) {
            throw new LoiDangNhap(SAI_THONG_TIN);
        }
        // Chỉ báo bị khóa khi đã đúng mật khẩu, để người ngoài không dò
        // được tài khoản nào bị khóa.
        TaiKhoan taiKhoan = timThay.get();
        if (!taiKhoan.hoatDong()) {
            throw new LoiDangNhap(BI_KHOA);
        }
        NguoiDung nguoiDung = taiKhoan.nguoiDung();
        PhienDangNhap.batDau(nguoiDung);
        return nguoiDung;
    }

    /** Đăng xuất: kết thúc phiên. */
    public void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    /**
     * Đọc lại tài khoản đang đăng nhập từ CSDL (giả định 17), gọi trước
     * mỗi lần mở màn hình. Bị khóa hoặc không còn thì kết thúc phiên; đổi
     * họ tên, vai trò thì cập nhật phiên (thanh đầu, menu đổi theo).
     *
     * @return kết quả; {@link KetQuaPhien#DA_KET_THUC} cả khi chưa đăng
     *         nhập
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL; phiên giữ
     *         nguyên
     */
    public KetQuaPhien lamMoiPhien() {
        Optional<NguoiDung> hienTai = PhienDangNhap.nguoiDung();
        if (hienTai.isEmpty()) {
            return KetQuaPhien.DA_KET_THUC;
        }
        Optional<TaiKhoan> taiKhoan =
                taiKhoanDao.timTheoId(hienTai.get().id());
        if (taiKhoan.isEmpty() || !taiKhoan.get().hoatDong()) {
            PhienDangNhap.ketThuc();
            return KetQuaPhien.DA_KET_THUC;
        }
        NguoiDung moi = taiKhoan.get().nguoiDung();
        if (moi.equals(hienTai.get())) {
            return KetQuaPhien.KHONG_DOI;
        }
        PhienDangNhap.capNhat(moi);
        return KetQuaPhien.DA_DOI;
    }
}
