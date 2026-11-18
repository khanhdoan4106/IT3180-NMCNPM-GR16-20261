package com.bluemoon.service;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.YeuCauTaoKhoanTuNguyen;
import com.bluemoon.util.ManHinh;
import com.bluemoon.util.TimKiem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiệp vụ quản lý danh mục khoản thu và tạo khoản thu tự nguyện
 * (US-006, EPIC-02). Mọi thao tác kiểm quyền bằng {@link PhanQuyen}.
 */
public class KhoanPhiService {

    /** Độ dài tên tối đa cho phép. */
    public static final int TEN_TOI_DA = 100;

    /**
     * Các ô nhập trên form tạo khoản thu tự nguyện, phục vụ báo lỗi từng ô
     * theo quy ước giao diện.
     */
    public enum O {
        /** Tên khoản thu. */
        TEN,
        /** Cách tính đóng góp. */
        CACH_TINH,
        /** Số tiền cố định nếu có. */
        DON_GIA,
        /** Ngày bắt đầu đợt thu. */
        TU_NGAY,
        /** Ngày kết thúc đợt thu. */
        DEN_NGAY,
        /** Ghi chú khoản thu. */
        GHI_CHU
    }

    private final KhoanPhiDao khoanPhiDao;

    /**
     * Khởi tạo service với DAO được chỉ định.
     *
     * @param khoanPhiDao DAO truy cập dữ liệu khoản phí
     */
    public KhoanPhiService(KhoanPhiDao khoanPhiDao) {
        this.khoanPhiDao = khoanPhiDao;
    }

    /**
     * Lấy toàn bộ danh sách các khoản thu trong hệ thống (AC-019).
     *
     * @return danh sách khoản thu
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không đủ quyền
     */
    public List<KhoanPhi> layDanhSach() {
        PhanQuyen.yeuCau(ManHinh.DANH_MUC_KHOAN_THU);
        return khoanPhiDao.layDanhSach();
    }

    /**
     * Tìm kiếm khoản thu theo tên (không phân biệt hoa thường và dấu).
     *
     * @param tuKhoa từ khóa người dùng gõ
     * @return danh sách khoản thu khớp từ khóa
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không đủ quyền
     */
    public List<KhoanPhi> timKiem(String tuKhoa) {
        List<KhoanPhi> danhSach = layDanhSach();
        if (tuKhoa == null || tuKhoa.isBlank()) {
            return danhSach;
        }
        List<KhoanPhi> ketQua = new ArrayList<>();
        for (KhoanPhi kp : danhSach) {
            if (TimKiem.khop(kp.ten(), tuKhoa)) {
                ketQua.add(kp);
            }
        }
        return ketQua;
    }

    /**
     * Tạo một khoản thu tự nguyện mới theo từng đợt (US-006, AC-019..AC-021).
     *
     * @param yc dữ liệu khoản thu cần tạo
     * @return mã định danh của khoản thu vừa tạo
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không phải Quản trị viên
     * @throws LoiNhapLieu khi thông tin nhập vào không hợp lệ hoặc bị trùng
     */
    public int taoKhoanTuNguyen(YeuCauTaoKhoanTuNguyen yc) {
        PhanQuyen.yeuCau(ManHinh.DANH_MUC_KHOAN_THU);

        if (yc == null) {
            throw new LoiNghiepVu("Dữ liệu khoản thu không hợp lệ.");
        }

        Map<O, String> loi = new LinkedHashMap<>();

        String tenChuanHoa = kiemTraTen(yc.ten(), loi);
        kiemTraCachTinhVaDonGia(yc.cachTinh(), yc.donGia(), loi);
        kiemTraThoiGian(yc.tuNgay(), yc.denNgay(), loi);

        if (!loi.isEmpty()) {
            throw new LoiNhapLieu(loi);
        }

        // AC-020: kiểm tra trùng tên trong cùng đợt (cùng ngày bắt đầu)
        if (khoanPhiDao.tonTaiTenTrongDot(tenChuanHoa, yc.tuNgay())) {
            throw new LoiNhapLieu(Collections.singletonMap(
                    O.TEN, "Tên khoản thu “" + tenChuanHoa
                            + "” đã tồn tại trong đợt này."));
        }

        BigDecimal donGiaLuu = (yc.cachTinh() == CachTinhPhi.KHONG_CO_DINH)
                ? null : yc.donGia();
        String ghiChuLuu = (yc.ghiChu() == null || yc.ghiChu().isBlank())
                ? null : yc.ghiChu().trim();

        KhoanPhi moi = new KhoanPhi(
                0,
                tenChuanHoa,
                LoaiKhoanPhi.TU_NGUYEN,
                yc.cachTinh(),
                donGiaLuu,
                yc.tuNgay(),
                yc.denNgay(),
                TrangThaiKhoanPhi.DANG_THU,
                ghiChuLuu);

        return khoanPhiDao.them(moi);
    }

    private String kiemTraTen(String ten, Map<O, String> loi) {
        if (ten == null || ten.isBlank()) {
            loi.put(O.TEN, "Vui lòng nhập tên khoản thu.");
            return "";
        }
        String tenTrim = ten.trim();
        if (tenTrim.length() > TEN_TOI_DA) {
            loi.put(O.TEN, "Tên khoản thu không được dài quá "
                    + TEN_TOI_DA + " ký tự.");
        }
        return tenTrim;
    }

    private void kiemTraCachTinhVaDonGia(
            CachTinhPhi cachTinh, BigDecimal donGia, Map<O, String> loi) {
        if (cachTinh == null) {
            loi.put(O.CACH_TINH, "Vui lòng chọn hình thức đóng góp.");
            return;
        }
        if (cachTinh == CachTinhPhi.THEO_DIEN_TICH) {
            loi.put(O.CACH_TINH,
                    "Khoản tự nguyện không áp dụng tính theo diện tích.");
            return;
        }
        if (cachTinh == CachTinhPhi.CO_DINH_THEO_HO) {
            if (donGia == null || donGia.compareTo(BigDecimal.ZERO) <= 0) {
                loi.put(O.DON_GIA, "Số tiền cố định phải lớn hơn 0 đ.");
            }
        }
    }

    private void kiemTraThoiGian(
            java.time.LocalDate tuNgay,
            java.time.LocalDate denNgay,
            Map<O, String> loi) {
        if (tuNgay == null) {
            loi.put(O.TU_NGAY, "Vui lòng chọn ngày bắt đầu.");
        }
        if (denNgay == null) {
            loi.put(O.DEN_NGAY,
                    "Khoản thu tự nguyện bắt buộc có ngày kết thúc.");
        }
        if (tuNgay != null && denNgay != null && denNgay.isBefore(tuNgay)) {
            loi.put(O.DEN_NGAY,
                    "Ngày kết thúc không được trước ngày bắt đầu.");
        }
    }
}
