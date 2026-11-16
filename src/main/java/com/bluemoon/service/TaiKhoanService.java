package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhau;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Nghiệp vụ tài khoản. US-020: Quản trị viên tạo tài khoản cho người khác
 * (giả định 2), không có màn tự đăng ký.
 */
public class TaiKhoanService {

    /** Các ô của form tạo tài khoản, theo thứ tự trên màn hình. */
    public enum O {
        /** Tên đăng nhập. */
        TEN_DANG_NHAP,
        /** Họ tên. */
        HO_TEN,
        /** Vai trò. */
        VAI_TRO,
        /** Mật khẩu. */
        MAT_KHAU,
        /** Nhập lại mật khẩu. */
        NHAP_LAI_MAT_KHAU
    }

    /** Mật khẩu ít nhất 8 ký tự (AC-056; tệp 11 chưa có con số). */
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

    private final TaiKhoanDao taiKhoanDao;
    private final UnaryOperator<String> bamMatKhau;

    /**
     * Tạo Service, băm mật khẩu bằng {@link MatKhau#bam(String)}.
     *
     * @param taiKhoanDao DAO tài khoản
     */
    public TaiKhoanService(TaiKhoanDao taiKhoanDao) {
        this(taiKhoanDao, MatKhau::bam);
    }

    /**
     * Tạo Service với cách băm khác (test dùng cách băm nhanh).
     *
     * @param taiKhoanDao DAO tài khoản
     * @param bamMatKhau hàm băm mật khẩu
     */
    TaiKhoanService(TaiKhoanDao taiKhoanDao,
            UnaryOperator<String> bamMatKhau) {
        this.taiKhoanDao = taiKhoanDao;
        this.bamMatKhau = bamMatKhau;
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
        if (hoTenGon.isEmpty()) {
            loi.put(O.HO_TEN, "Nhập họ tên người dùng.");
        } else if (hoTenGon.length() > HO_TEN_TOI_DA) {
            loi.put(O.HO_TEN, "Họ tên dài tối đa " + HO_TEN_TOI_DA
                    + " ký tự.");
        }
        if (vaiTro == null) {
            loi.put(O.VAI_TRO, "Chọn vai trò.");
        }
        int doDai = mk.codePointCount(0, mk.length());
        if (mk.isEmpty()) {
            loi.put(O.MAT_KHAU, "Nhập mật khẩu.");
        } else if (doDai < MAT_KHAU_TOI_THIEU) {
            loi.put(O.MAT_KHAU, "Mật khẩu cần ít nhất " + MAT_KHAU_TOI_THIEU
                    + " ký tự.");
        } else if (doDai > MAT_KHAU_TOI_DA) {
            loi.put(O.MAT_KHAU, "Mật khẩu dài tối đa " + MAT_KHAU_TOI_DA
                    + " ký tự.");
        }
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

    private static String gon(String chuoi) {
        return chuoi == null ? "" : chuoi.strip();
    }
}
