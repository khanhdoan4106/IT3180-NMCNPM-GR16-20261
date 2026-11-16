package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.LoaiLoiCsdl;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.TaiKhoanService.O;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Test {@link TaiKhoanService} bằng DAO giả, không cần MySQL. */
class TaiKhoanServiceTest {

    /** DAO giả: nhớ tài khoản đã thêm, trùng tên không phân biệt hoa thường. */
    private static final class TaiKhoanDaoGia implements TaiKhoanDao {
        private final List<TaiKhoanMoi> daThem = new ArrayList<>();
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
    }

    private final TaiKhoanDaoGia dao = new TaiKhoanDaoGia();
    /** Băm giả cho nhanh; băm BCrypt thật test ở MatKhauTest. */
    private final TaiKhoanService service =
            new TaiKhoanService(dao, mk -> "bam:" + mk);

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
}
