package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.ManHinh;
import com.bluemoon.util.MatKhau;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Nghiệp vụ tài khoản. US-020: Quản trị viên tạo tài khoản cho người khác
 * (giả định 2), không có màn tự đăng ký. US-023: người đang đăng nhập sửa
 * họ tên, số điện thoại và đổi mật khẩu của mình (giả định 19). US-022:
 * Quản trị viên đổi vai trò, khóa/mở khóa, đặt lại mật khẩu tạm cho tài
 * khoản khác (giả định 18). Mọi hàm kiểm quyền bằng {@link PhanQuyen}.
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

    /** Mật khẩu tạm 12 ký tự, dài hơn mức tối thiểu (giả định 50). */
    static final int DO_DAI_MAT_KHAU_TAM = 12;

    private static final SecureRandom NGAU_NHIEN = new SecureRandom();

    private static final String QUAN_LY_CHI_QUAN_TRI =
            "Chỉ Quản trị viên được quản lý tài khoản.";
    private static final String CON_MOT_QUAN_TRI = "Phải còn ít nhất một "
            + "Quản trị viên đang hoạt động. Thêm hoặc mở khóa một Quản "
            + "trị viên khác trước.";
    private static final String KHONG_CON_TAI_KHOAN = "Không tìm thấy tài "
            + "khoản này. Tải lại danh sách rồi thử lại.";

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
     * Tạo tài khoản (AC-054). Chỉ Quản trị viên được tạo (giả định 2,
     * AC-061). Báo mọi ô sai cùng lúc; tên đăng nhập trùng (AC-055) và mật
     * khẩu quá ngắn (AC-056) báo ở ô tương ứng.
     *
     * @param tenDangNhap tên đăng nhập, bỏ khoảng trắng hai đầu
     * @param hoTen họ tên, bỏ khoảng trắng hai đầu
     * @param vaiTro vai trò, bắt buộc chọn
     * @param matKhau mật khẩu, giữ nguyên như đã gõ
     * @param nhapLai mật khẩu nhập lại
     * @return tài khoản đã ghi (mật khẩu đã băm)
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không phải Quản trị
     *         viên; không ghi gì
     * @throws LoiNhapLieu khi có ô sai hoặc tên đăng nhập đã có
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public TaiKhoanMoi taoTaiKhoan(String tenDangNhap, String hoTen,
            VaiTro vaiTro, String matKhau, String nhapLai) {
        NguoiDung nguoiThucHien = PhanQuyen.chiQuanTriVien(
                "Chỉ Quản trị viên được tạo tài khoản.");
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
        if (!taiKhoanDao.them(taiKhoan, nguoiThucHien)) {
            throw new LoiNhapLieu(Map.of(O.TEN_DANG_NHAP, "Tên đăng nhập “"
                    + ten + "” đã có người dùng. Chọn tên khác."));
        }
        return taiKhoan;
    }

    /**
     * Tài khoản của người đang đăng nhập, đọc mới từ CSDL (US-023).
     *
     * @return tài khoản, kèm số điện thoại
     * @throws LoiKhongCoQuyen khi chưa đăng nhập
     * @throws IllegalStateException khi tài khoản không còn trong CSDL
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
     * @throws LoiKhongCoQuyen khi chưa đăng nhập
     * @throws IllegalStateException khi tài khoản không còn trong CSDL
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
                soGon.isEmpty() ? null : soGon, nguoiDung)) {
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
     * @throws LoiKhongCoQuyen khi chưa đăng nhập
     * @throws IllegalStateException khi tài khoản không còn trong CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc, ghi được CSDL
     */
    public void doiMatKhau(String matKhauCu, String matKhauMoi,
            String nhapLai) {
        NguoiDung nguoiDung = nguoiDangNhap();
        int id = nguoiDung.id();
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
        if (!taiKhoanDao.doiMatKhau(id, bamMatKhau.apply(moi), nguoiDung)) {
            throw new IllegalStateException("Không có tài khoản " + id);
        }
    }

    // ---- US-022: Quản trị viên quản lý tài khoản (giả định 18) ----

    /**
     * Mọi tài khoản, xếp theo tên đăng nhập. Chỉ Quản trị viên xem.
     *
     * @return danh sách tài khoản
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không phải Quản trị
     *         viên; không đọc CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public List<TaiKhoan> danhSachTaiKhoan() {
        chiQuanTriVien(QUAN_LY_CHI_QUAN_TRI);
        return taiKhoanDao.danhSach();
    }

    /**
     * Đổi vai trò tài khoản khác. Không tự đổi vai trò của mình; luôn còn
     * ít nhất một Quản trị viên đang hoạt động (giả định 18).
     *
     * @param id mã tài khoản cần đổi
     * @param vaiTro vai trò mới
     * @return tài khoản sau khi đổi
     * @throws LoiKhongCoQuyen khi không phải Quản trị viên
     * @throws LoiNghiepVu khi tự đổi vai trò mình, tài khoản không còn,
     *         hoặc đây là Quản trị viên đang hoạt động cuối cùng
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public TaiKhoan doiVaiTro(int id, VaiTro vaiTro) {
        NguoiDung toi = chiQuanTriVien(QUAN_LY_CHI_QUAN_TRI);
        if (vaiTro == null) {
            throw new LoiNghiepVu("Chọn vai trò mới.");
        }
        if (id == toi.id()) {
            throw new LoiNghiepVu("Không tự đổi vai trò của chính mình. "
                    + "Nhờ một Quản trị viên khác đổi giúp.");
        }
        TaiKhoan taiKhoan = timTaiKhoan(id);
        if (taiKhoan.vaiTro() == vaiTro) {
            return taiKhoan;
        }
        if (!taiKhoanDao.doiVaiTro(id, vaiTro, toi)) {
            throw new LoiNghiepVu(CON_MOT_QUAN_TRI);
        }
        return timTaiKhoan(id);
    }

    /**
     * Khóa hoặc mở khóa tài khoản khác. Không tự khóa mình; không khóa
     * Quản trị viên đang hoạt động cuối cùng (giả định 18). Tài khoản bị
     * khóa bị đăng xuất ở lần mở màn hình kế tiếp (giả định 17).
     *
     * @param id mã tài khoản
     * @param khoa {@code true} để khóa, {@code false} để mở khóa
     * @return tài khoản sau khi đổi
     * @throws LoiKhongCoQuyen khi không phải Quản trị viên
     * @throws LoiNghiepVu khi tự khóa mình, tài khoản không còn, hoặc đây
     *         là Quản trị viên đang hoạt động cuối cùng
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public TaiKhoan khoaTaiKhoan(int id, boolean khoa) {
        NguoiDung toi = chiQuanTriVien(QUAN_LY_CHI_QUAN_TRI);
        if (khoa && id == toi.id()) {
            throw new LoiNghiepVu("Không tự khóa tài khoản đang dùng.");
        }
        TaiKhoan taiKhoan = timTaiKhoan(id);
        if (taiKhoan.hoatDong() != khoa) {
            return taiKhoan;
        }
        if (!taiKhoanDao.datHoatDong(id, !khoa, toi)) {
            throw new LoiNghiepVu(CON_MOT_QUAN_TRI);
        }
        return timTaiKhoan(id);
    }

    /**
     * Đặt lại mật khẩu tạm cho tài khoản khác (giả định 18). Mật khẩu tạm
     * sinh ngẫu nhiên, đủ độ dài (giả định 50), chỉ trả về một lần để Quản
     * trị viên báo lại; CSDL chỉ lưu bản băm, không ghi ra log.
     *
     * @param id mã tài khoản
     * @return mật khẩu tạm (chữ rõ)
     * @throws LoiKhongCoQuyen khi không phải Quản trị viên
     * @throws LoiNghiepVu khi đặt lại cho chính mình hoặc tài khoản không
     *         còn
     * @throws com.bluemoon.dao.LoiCsdl khi không ghi được CSDL
     */
    public String datLaiMatKhau(int id) {
        NguoiDung toi = chiQuanTriVien(QUAN_LY_CHI_QUAN_TRI);
        if (id == toi.id()) {
            throw new LoiNghiepVu("Đổi mật khẩu của mình ở tab “Tài khoản "
                    + "của tôi”.");
        }
        timTaiKhoan(id);
        String matKhauTam = sinhMatKhauTam();
        if (!taiKhoanDao.datLaiMatKhau(id, bamMatKhau.apply(matKhauTam),
                toi)) {
            throw new LoiNghiepVu(KHONG_CON_TAI_KHOAN);
        }
        return matKhauTam;
    }

    /**
     * Sinh mật khẩu tạm: {@link #DO_DAI_MAT_KHAU_TAM} ký tự, có chữ hoa,
     * chữ thường và chữ số; bỏ các ký tự dễ nhầm (0/O, 1/l/I).
     */
    static String sinhMatKhauTam() {
        String[] nhom = {"ABCDEFGHJKLMNPQRSTUVWXYZ",
                "abcdefghijkmnopqrstuvwxyz", "23456789"};
        String tatCa = String.join("", nhom);
        List<Character> kyTu = new ArrayList<>();
        for (String n : nhom) {
            kyTu.add(n.charAt(NGAU_NHIEN.nextInt(n.length())));
        }
        while (kyTu.size() < DO_DAI_MAT_KHAU_TAM) {
            kyTu.add(tatCa.charAt(NGAU_NHIEN.nextInt(tatCa.length())));
        }
        Collections.shuffle(kyTu, NGAU_NHIEN);
        StringBuilder sb = new StringBuilder();
        kyTu.forEach(sb::append);
        return sb.toString();
    }

    private TaiKhoan timTaiKhoan(int id) {
        return taiKhoanDao.timTheoId(id).orElseThrow(
                () -> new LoiNghiepVu(KHONG_CON_TAI_KHOAN));
    }

    private static NguoiDung chiQuanTriVien(String cauBao) {
        return PhanQuyen.chiQuanTriVien(cauBao);
    }

    /** Người đang đăng nhập; mọi vai trò được mở màn Tài khoản. */
    private static NguoiDung nguoiDangNhap() {
        return PhanQuyen.yeuCau(ManHinh.TAI_KHOAN);
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
