package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhau;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Nghiệp vụ tài khoản. US-020: Quản trị viên tạo tài khoản cho người khác
 * (giả định 2), không có màn tự đăng ký. US-023: người đang đăng nhập sửa
 * họ tên, số điện thoại và đổi mật khẩu của mình (giả định 19).
 */
public class TaiKhoanService {

    /** Các ô trên màn Tài khoản, theo thứ tự trên màn hình. */
    public enum O {
        /** Tên đăng nhập. */
        TEN_DANG_NHAP,
        /** Họ tên. */
        HO_TEN,
        /** Số điện thoại (US-023). */
        SO_DIEN_THOAI,
        /** Vai trò. */
        VAI_TRO,
        /** Mật khẩu hiện tại, khi đổi mật khẩu (US-023). */
        MAT_KHAU_CU,
        /** Mật khẩu; khi đổi mật khẩu là mật khẩu mới. */
        MAT_KHAU,
        /** Nhập lại mật khẩu. */
        NHAP_LAI_MAT_KHAU
    }

    /** Mật khẩu ít nhất 8 ký tự (AC-056, giả định 50). */
    public static final int MAT_KHAU_TOI_THIEU = 8;
    /** Mật khẩu nhiều nhất 64 ký tự. */
    public static final int MAT_KHAU_TOI_DA = 64;
    /** Họ tên nhiều nhất 100 ký tự (cột {@code ho_ten}). */
    public static final int HO_TEN_TOI_DA = 100;

    /**
     * Tên đăng nhập 3–50 ký tự (cột {@code ten_dang_nhap}): chữ không dấu,
     * chữ số, dấu chấm, gạch dưới; gõ trên bàn phím nào cũng được.
     */
    private static final Pattern MAU_TEN_DANG_NHAP =
            Pattern.compile("[A-Za-z0-9._]{3,50}");

    /** Số điện thoại: 10 chữ số, bắt đầu bằng 0. */
    private static final Pattern MAU_SO_DIEN_THOAI =
            Pattern.compile("0\\d{9}");

    /** Dấu cách, chấm, gạch người dùng hay gõ giữa các nhóm số. */
    private static final Pattern NGAN_CACH_SO = Pattern.compile("[\\s.-]");

    private final TaiKhoanDao taiKhoanDao;
    private final UnaryOperator<String> bamMatKhau;
    private final BiPredicate<String, String> khopMatKhau;

    /**
     * Tạo Service, băm và so mật khẩu bằng {@link MatKhau}.
     *
     * @param taiKhoanDao DAO tài khoản
     */
    public TaiKhoanService(TaiKhoanDao taiKhoanDao) {
        this(taiKhoanDao, MatKhau::bam, MatKhau::khop);
    }

    /**
     * Tạo Service với cách băm khác (test dùng cách băm nhanh).
     *
     * @param taiKhoanDao DAO tài khoản
     * @param bamMatKhau hàm băm mật khẩu
     * @param khopMatKhau so mật khẩu gõ vào với mật khẩu đã băm
     */
    TaiKhoanService(TaiKhoanDao taiKhoanDao,
            UnaryOperator<String> bamMatKhau,
            BiPredicate<String, String> khopMatKhau) {
        this.taiKhoanDao = taiKhoanDao;
        this.bamMatKhau = bamMatKhau;
        this.khopMatKhau = khopMatKhau;
    }

    /**
     * Tạo tài khoản (AC-054). Báo mọi ô sai cùng lúc; tên đăng nhập trùng
     * (AC-055) và mật khẩu quá ngắn (AC-056) báo ở ô tương ứng.
     *
     * @param tenDangNhap tên đăng nhập, bỏ khoảng trắng hai đầu
     * @param hoTen họ tên, bỏ khoảng trắng hai đầu
     * @param vaiTro vai trò, bắt buộc chọn
     * @param matKhau mật khẩu, giữ nguyên như đã gõ
     * @param nhapLai mật khẩu nhập lại
     * @return tài khoản đã ghi (mật khẩu đã băm)
     * @throws LoiNhapLieu khi có ô sai hoặc tên đăng nhập đã có
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public TaiKhoanMoi taoTaiKhoan(String tenDangNhap, String hoTen,
            VaiTro vaiTro, String matKhau, String nhapLai) {
        String ten = gon(tenDangNhap);
        String hoTenGon = gon(hoTen);
        String mk = matKhau == null ? "" : matKhau;
        Map<O, String> loi = new LinkedHashMap<>();

        if (ten.isEmpty()) {
            loi.put(O.TEN_DANG_NHAP, "Nhập tên đăng nhập.");
        } else if (!MAU_TEN_DANG_NHAP.matcher(ten).matches()) {
            loi.put(O.TEN_DANG_NHAP, "Tên đăng nhập dài 3–50 ký tự, chỉ gồm "
                    + "chữ không dấu, chữ số, dấu chấm hoặc gạch dưới.");
        }
        kiemHoTen(hoTenGon, loi);
        if (vaiTro == null) {
            loi.put(O.VAI_TRO, "Chọn vai trò.");
        }
        kiemMatKhau(mk, "Nhập mật khẩu.", loi);
        if (!loi.containsKey(O.MAT_KHAU) && !mk.equals(nhapLai)) {
            loi.put(O.NHAP_LAI_MAT_KHAU, "Mật khẩu nhập lại chưa khớp.");
        }
        if (!loi.isEmpty()) {
            throw new LoiNhapLieu(loi);
        }

        TaiKhoanMoi taiKhoan = new TaiKhoanMoi(ten, hoTenGon, vaiTro,
                bamMatKhau.apply(mk));
        if (!taiKhoanDao.them(taiKhoan)) {
            throw new LoiNhapLieu(Map.of(O.TEN_DANG_NHAP, "Tên đăng nhập “"
                    + ten + "” đã có người dùng. Chọn tên khác."));
        }
        return taiKhoan;
    }

    /**
     * Tài khoản của người đang đăng nhập, đọc mới từ CSDL (US-023).
     *
     * @return tài khoản, kèm số điện thoại
     * @throws IllegalStateException khi chưa đăng nhập hoặc tài khoản không
     *         còn trong CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public TaiKhoan taiKhoanCuaToi() {
        int id = nguoiDangNhap().id();
        return taiKhoanDao.timTheoId(id).orElseThrow(
                () -> new IllegalStateException("Không có tài khoản " + id));
    }

    /**
     * Người đang đăng nhập sửa họ tên, số điện thoại của mình (AC-065, giả
     * định 19). Lưu xong thì phiên đăng nhập mang họ tên mới.
     *
     * @param hoTen họ tên, bắt buộc, bỏ khoảng trắng hai đầu
     * @param soDienThoai 10 chữ số bắt đầu bằng 0; để trống được. Dấu
     *        cách, chấm, gạch giữa các số được bỏ đi
     * @return người dùng sau khi sửa
     * @throws LoiNhapLieu khi có ô sai
     * @throws IllegalStateException khi chưa đăng nhập hoặc tài khoản không
     *         còn trong CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public NguoiDung capNhatThongTin(String hoTen, String soDienThoai) {
        NguoiDung nguoiDung = nguoiDangNhap();
        String hoTenGon = gon(hoTen);
        String soGon = NGAN_CACH_SO.matcher(gon(soDienThoai))
                .replaceAll("");
        Map<O, String> loi = new LinkedHashMap<>();
        kiemHoTen(hoTenGon, loi);
        if (!soGon.isEmpty()
                && !MAU_SO_DIEN_THOAI.matcher(soGon).matches()) {
            loi.put(O.SO_DIEN_THOAI, "Số điện thoại gồm 10 chữ số, bắt đầu "
                    + "bằng số 0, vd 0912345678. Không có thì để trống.");
        }
        if (!loi.isEmpty()) {
            throw new LoiNhapLieu(loi);
        }

        if (!taiKhoanDao.capNhatThongTin(nguoiDung.id(), hoTenGon,
                soGon.isEmpty() ? null : soGon)) {
            throw new IllegalStateException(
                    "Không có tài khoản " + nguoiDung.id());
        }
        NguoiDung moi = new NguoiDung(nguoiDung.id(),
                nguoiDung.tenDangNhap(), hoTenGon, nguoiDung.vaiTro());
        PhienDangNhap.capNhat(moi);
        return moi;
    }

    /**
     * Người đang đăng nhập đổi mật khẩu của mình (AC-063). Mật khẩu hiện
     * tại sai thì từ chối (AC-064); mật khẩu mới phải đủ độ dài (giả định
     * 50), nhập lại khớp và khác mật khẩu hiện tại (giả định 19).
     *
     * @param matKhauCu mật khẩu hiện tại, giữ nguyên như đã gõ
     * @param matKhauMoi mật khẩu mới
     * @param nhapLai mật khẩu mới nhập lại
     * @throws LoiNhapLieu khi có ô sai hoặc mật khẩu hiện tại không đúng
     * @throws IllegalStateException khi chưa đăng nhập hoặc tài khoản không
     *         còn trong CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc, ghi được CSDL
     */
    public void doiMatKhau(String matKhauCu, String matKhauMoi,
            String nhapLai) {
        int id = nguoiDangNhap().id();
        String cu = matKhauCu == null ? "" : matKhauCu;
        String moi = matKhauMoi == null ? "" : matKhauMoi;
        Map<O, String> loi = new LinkedHashMap<>();
        if (cu.isEmpty()) {
            loi.put(O.MAT_KHAU_CU, "Nhập mật khẩu hiện tại.");
        }
        kiemMatKhau(moi, "Nhập mật khẩu mới.", loi);
        if (!loi.containsKey(O.MAT_KHAU) && !cu.isEmpty()
                && moi.equals(cu)) {
            loi.put(O.MAT_KHAU, "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }
        if (!loi.containsKey(O.MAT_KHAU) && !moi.equals(nhapLai)) {
            loi.put(O.NHAP_LAI_MAT_KHAU, "Mật khẩu nhập lại chưa khớp.");
        }
        if (!loi.isEmpty()) {
            throw new LoiNhapLieu(loi);
        }

        TaiKhoan taiKhoan = taiKhoanCuaToi();
        if (!khopMatKhau.test(cu, taiKhoan.matKhauBam())) {
            throw new LoiNhapLieu(Map.of(O.MAT_KHAU_CU,
                    "Mật khẩu hiện tại không đúng."));
        }
        if (!taiKhoanDao.doiMatKhau(id, bamMatKhau.apply(moi))) {
            throw new IllegalStateException("Không có tài khoản " + id);
        }
    }

    private static NguoiDung nguoiDangNhap() {
        return PhienDangNhap.nguoiDung().orElseThrow(
                () -> new IllegalStateException("Chưa đăng nhập"));
    }

    private static void kiemHoTen(String hoTen, Map<O, String> loi) {
        if (hoTen.isEmpty()) {
            loi.put(O.HO_TEN, "Nhập họ tên người dùng.");
        } else if (hoTen.length() > HO_TEN_TOI_DA) {
            loi.put(O.HO_TEN, "Họ tên dài tối đa " + HO_TEN_TOI_DA
                    + " ký tự.");
        }
    }

    private static void kiemMatKhau(String mk, String khiTrong,
            Map<O, String> loi) {
        int doDai = mk.codePointCount(0, mk.length());
        if (mk.isEmpty()) {
            loi.put(O.MAT_KHAU, khiTrong);
        } else if (doDai < MAT_KHAU_TOI_THIEU) {
            loi.put(O.MAT_KHAU, "Mật khẩu cần ít nhất " + MAT_KHAU_TOI_THIEU
                    + " ký tự.");
        } else if (doDai > MAT_KHAU_TOI_DA) {
            loi.put(O.MAT_KHAU, "Mật khẩu dài tối đa " + MAT_KHAU_TOI_DA
                    + " ký tự.");
        }
    }

    private static String gon(String chuoi) {
        return chuoi == null ? "" : chuoi.strip();
    }
}
