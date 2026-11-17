package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.LoaiLoiCsdl;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.TaiKhoanService.O;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Test {@link TaiKhoanService} bằng DAO giả, không cần MySQL. */
class TaiKhoanServiceTest {

    /**
     * DAO giả: nhớ tài khoản đã thêm (trùng tên không phân biệt hoa
     * thường) và các tài khoản có sẵn theo mã cho US-023.
     */
    private static final class TaiKhoanDaoGia implements TaiKhoanDao {
        private final List<TaiKhoanMoi> daThem = new ArrayList<>();
        private final Map<Integer, TaiKhoan> theoId = new HashMap<>();
        private int soLanGhi;
        private LoiCsdl loi;

        @Override
        public boolean them(TaiKhoanMoi taiKhoan) {
            if (loi != null) {
                throw loi;
            }
            String ten = taiKhoan.tenDangNhap().toLowerCase(Locale.ROOT);
            boolean trung = daThem.stream().anyMatch(t -> t.tenDangNhap()
                    .toLowerCase(Locale.ROOT).equals(ten));
            if (trung) {
                return false;
            }
            daThem.add(taiKhoan);
            return true;
        }

        @Override
        public Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TaiKhoan> timTheoId(int id) {
            if (loi != null) {
                throw loi;
            }
            return Optional.ofNullable(theoId.get(id));
        }

        @Override
        public boolean capNhatThongTin(int id, String hoTen, String sdt) {
            if (loi != null) {
                throw loi;
            }
            TaiKhoan tk = theoId.get(id);
            if (tk == null) {
                return false;
            }
            soLanGhi++;
            theoId.put(id, new TaiKhoan(id, tk.tenDangNhap(), hoTen,
                    tk.vaiTro(), tk.matKhauBam(), tk.hoatDong(), sdt));
            return true;
        }

        @Override
        public boolean doiMatKhau(int id, String matKhauBam) {
            TaiKhoan tk = theoId.get(id);
            if (tk == null) {
                return false;
            }
            soLanGhi++;
            theoId.put(id, new TaiKhoan(id, tk.tenDangNhap(), tk.hoTen(),
                    tk.vaiTro(), matKhauBam, tk.hoatDong(),
                    tk.soDienThoai()));
            return true;
        }
    }

    private final TaiKhoanDaoGia dao = new TaiKhoanDaoGia();
    /** Băm giả "bam:" + mật khẩu cho nhanh; BCrypt thật ở MatKhauTest. */
    private final TaiKhoanService service = new TaiKhoanService(dao,
            mk -> "bam:" + mk, (mk, bam) -> bam.equals("bam:" + mk));

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
        PhienDangNhap.khiDoiNguoiDung(null);
    }

    /** Thủ quỹ thuquy01 đang đăng nhập, mật khẩu "MatKhau@1". */
    private void dangNhapThuQuy() {
        TaiKhoan tk = new TaiKhoan(2, "thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, "bam:MatKhau@1", true, null);
        dao.theoId.put(2, tk);
        PhienDangNhap.batDau(tk.nguoiDung());
    }

    @Test
    void ac054TaoTaiKhoanHopLe() {
        TaiKhoanMoi tk = service.taoTaiKhoan("  thuquy01 ",
                " Trần Thị Mai ", VaiTro.THU_QUY, "MatKhau@1", "MatKhau@1");
        assertEquals(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, "bam:MatKhau@1"), tk);
        assertEquals(List.of(tk), dao.daThem);
    }

    @Test
    void ac055BaoTrungTenDangNhap() {
        service.taoTaiKhoan("thuquy01", "Trần Thị Mai", VaiTro.THU_QUY,
                "MatKhau@1", "MatKhau@1");
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.taoTaiKhoan("ThuQuy01", "Lê Văn Bình",
                        VaiTro.QUAN_TRI, "MatKhau@2", "MatKhau@2"));
        assertEquals("Tên đăng nhập “ThuQuy01” đã có người dùng. "
                + "Chọn tên khác.", loi.loiCua(O.TEN_DANG_NHAP));
        assertEquals(1, dao.daThem.size());
    }

    @Test
    void ac056BaoMatKhauQuaNgan() {
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.taoTaiKhoan("thuquy01", "Trần Thị Mai",
                        VaiTro.THU_QUY, "1234567", "1234567"));
        assertEquals("Mật khẩu cần ít nhất 8 ký tự.",
                loi.loiCua(O.MAT_KHAU));
        assertTrue(dao.daThem.isEmpty());
    }

    @Test
    void matKhauDuTamKyTuLaDuoc() {
        assertNotNull(service.taoTaiKhoan("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, "12345678", "12345678"));
    }

    @Test
    void baoMoiOTrongCungLuc() {
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.taoTaiKhoan(" ", "", null, "", ""));
        assertEquals(List.of(O.TEN_DANG_NHAP, O.HO_TEN, O.VAI_TRO,
                O.MAT_KHAU), List.copyOf(loi.getLoiTheoO().keySet()));
        assertTrue(dao.daThem.isEmpty());
    }

    @Test
    void tenDangNhapCoDauHoacKhoangTrangBiChan() {
        for (String ten : new String[] {"hương", "thu quy", "ab",
                "a".repeat(51)}) {
            LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                    () -> service.taoTaiKhoan(ten, "Trần Thị Mai",
                            VaiTro.THU_QUY, "MatKhau@1", "MatKhau@1"));
            assertNotNull(loi.loiCua(O.TEN_DANG_NHAP), ten);
        }
    }

    @Test
    void nhapLaiKhongKhop() {
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.taoTaiKhoan("thuquy01", "Trần Thị Mai",
                        VaiTro.THU_QUY, "MatKhau@1", "MatKhau@2"));
        assertEquals("Mật khẩu nhập lại chưa khớp.",
                loi.loiCua(O.NHAP_LAI_MAT_KHAU));
        assertNull(loi.loiCua(O.MAT_KHAU));
    }

    @Test
    void matKhauQuaDaiVaHoTenQuaDai() {
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.taoTaiKhoan("thuquy01", "A".repeat(101),
                        VaiTro.THU_QUY, "x".repeat(65), "x".repeat(65)));
        assertNotNull(loi.loiCua(O.HO_TEN));
        assertEquals("Mật khẩu dài tối đa 64 ký tự.", loi.loiCua(O.MAT_KHAU));
    }

    @Test
    void loiCsdlChuyenNguyenLenController() {
        dao.loi = new LoiCsdl(LoaiLoiCsdl.KHONG_TOI_MAY_CHU, null);
        assertSame(dao.loi, assertThrows(LoiCsdl.class,
                () -> service.taoTaiKhoan("thuquy01", "Trần Thị Mai",
                        VaiTro.THU_QUY, "MatKhau@1", "MatKhau@1")));
    }

    // US-023: tài khoản của người đang đăng nhập

    @Test
    void docTaiKhoanCuaNguoiDangNhap() {
        dangNhapThuQuy();
        assertEquals("thuquy01", service.taiKhoanCuaToi().tenDangNhap());
    }

    @Test
    void chuaDangNhapThiKhongDungDuoc() {
        assertThrows(IllegalStateException.class,
                () -> service.taiKhoanCuaToi());
        assertThrows(IllegalStateException.class,
                () -> service.capNhatThongTin("Trần Thị Mai", ""));
        assertThrows(IllegalStateException.class,
                () -> service.doiMatKhau("MatKhau@1", "MatKhau@2",
                        "MatKhau@2"));
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void ac065SuaHoTenVaSoDienThoai() {
        dangNhapThuQuy();
        AtomicReference<NguoiDung> thanhDau = new AtomicReference<>();
        PhienDangNhap.khiDoiNguoiDung(thanhDau::set);

        NguoiDung moi = service.capNhatThongTin("  Trần Thị Mai Anh ",
                "0912 345 678");

        TaiKhoan daLuu = dao.theoId.get(2);
        assertEquals("Trần Thị Mai Anh", daLuu.hoTen());
        assertEquals("0912345678", daLuu.soDienThoai());
        // Tên đăng nhập, vai trò không đổi (giả định 19).
        assertEquals("thuquy01", daLuu.tenDangNhap());
        assertEquals(VaiTro.THU_QUY, daLuu.vaiTro());
        assertEquals("Trần Thị Mai Anh", moi.hoTen());
        assertEquals(moi, PhienDangNhap.nguoiDung().orElseThrow());
        assertEquals(moi, thanhDau.get());
    }

    @Test
    void ac065DeTrongSoDienThoaiThiXoaSo() {
        dangNhapThuQuy();
        service.capNhatThongTin("Trần Thị Mai", "0912345678");
        service.capNhatThongTin("Trần Thị Mai", "  ");
        assertNull(dao.theoId.get(2).soDienThoai());
    }

    @Test
    void soDienThoaiSaiDang() {
        dangNhapThuQuy();
        for (String so : new String[] {"912345678", "09123456789",
                "1912345678", "0912abc678", "+84912345678"}) {
            LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                    () -> service.capNhatThongTin("Trần Thị Mai", so));
            assertNotNull(loi.loiCua(O.SO_DIEN_THOAI), so);
        }
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void hoTenTrongHoacQuaDaiKhongLuu() {
        dangNhapThuQuy();
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.capNhatThongTin("  ", "abc"));
        assertEquals(List.of(O.HO_TEN, O.SO_DIEN_THOAI),
                List.copyOf(loi.getLoiTheoO().keySet()));
        assertNotNull(assertThrows(LoiNhapLieu.class,
                () -> service.capNhatThongTin("A".repeat(101), ""))
                .loiCua(O.HO_TEN));
        assertEquals("Trần Thị Mai", dao.theoId.get(2).hoTen());
        assertEquals("Trần Thị Mai",
                PhienDangNhap.nguoiDung().orElseThrow().hoTen());
    }

    @Test
    void ac063DoiMatKhauKhiMatKhauCuDung() {
        dangNhapThuQuy();
        service.doiMatKhau("MatKhau@1", "MatKhauMoi@2", "MatKhauMoi@2");
        assertEquals("bam:MatKhauMoi@2", dao.theoId.get(2).matKhauBam());
    }

    @Test
    void ac064MatKhauCuSaiThiTuChoi() {
        dangNhapThuQuy();
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("SaiMatKhau", "MatKhauMoi@2",
                        "MatKhauMoi@2"));
        assertEquals("Mật khẩu hiện tại không đúng.",
                loi.loiCua(O.MAT_KHAU_CU));
        assertEquals("bam:MatKhau@1", dao.theoId.get(2).matKhauBam());
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void matKhauMoiQuaNganHoacQuaDai() {
        dangNhapThuQuy();
        LoiNhapLieu ngan = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("MatKhau@1", "1234567", "1234567"));
        assertEquals("Mật khẩu cần ít nhất 8 ký tự.",
                ngan.loiCua(O.MAT_KHAU));
        LoiNhapLieu dai = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("MatKhau@1", "x".repeat(65),
                        "x".repeat(65)));
        assertEquals("Mật khẩu dài tối đa 64 ký tự.", dai.loiCua(O.MAT_KHAU));
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void matKhauMoiPhaiKhacMatKhauCu() {
        dangNhapThuQuy();
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("MatKhau@1", "MatKhau@1",
                        "MatKhau@1"));
        assertEquals("Mật khẩu mới phải khác mật khẩu hiện tại.",
                loi.loiCua(O.MAT_KHAU));
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void matKhauMoiNhapLaiKhongKhop() {
        dangNhapThuQuy();
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("MatKhau@1", "MatKhauMoi@2",
                        "MatKhauMoi@3"));
        assertEquals("Mật khẩu nhập lại chưa khớp.",
                loi.loiCua(O.NHAP_LAI_MAT_KHAU));
        assertFalse(loi.getLoiTheoO().containsKey(O.MAT_KHAU_CU));
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void doiMatKhauDeTrongBaoMoiO() {
        dangNhapThuQuy();
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.doiMatKhau("", null, ""));
        assertEquals("Nhập mật khẩu hiện tại.", loi.loiCua(O.MAT_KHAU_CU));
        assertEquals("Nhập mật khẩu mới.", loi.loiCua(O.MAT_KHAU));
        assertEquals(0, dao.soLanGhi);
    }

    @Test
    void loiCsdlKhiSuaThongTinChuyenNguyenLenController() {
        dangNhapThuQuy();
        dao.loi = new LoiCsdl(LoaiLoiCsdl.KHONG_TOI_MAY_CHU, null);
        assertSame(dao.loi, assertThrows(LoiCsdl.class,
                () -> service.capNhatThongTin("Trần Thị Mai", "")));
        assertEquals("Trần Thị Mai",
                PhienDangNhap.nguoiDung().orElseThrow().hoTen());
    }
}
