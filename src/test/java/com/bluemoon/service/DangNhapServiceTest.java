package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.bluemoon.service.DangNhapService.O;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test {@link DangNhapService} bằng DAO giả, không cần MySQL. */
class DangNhapServiceTest {

    /** DAO giả: tài khoản cho trước, tìm không phân biệt hoa thường. */
    private static final class TaiKhoanDaoGia implements TaiKhoanDao {
        private final List<TaiKhoan> taiKhoan = new ArrayList<>();
        private LoiCsdl loi;

        @Override
        public boolean them(TaiKhoanMoi moi) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TaiKhoan> timTheoTenDangNhap(String ten) {
            if (loi != null) {
                throw loi;
            }
            return taiKhoan.stream().filter(t -> t.tenDangNhap()
                    .toLowerCase(Locale.ROOT)
                    .equals(ten.toLowerCase(Locale.ROOT))).findFirst();
        }

        @Override
        public Optional<TaiKhoan> timTheoId(int id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean capNhatThongTin(int id, String hoTen, String sdt) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean doiMatKhau(int id, String matKhauBam) {
            throw new UnsupportedOperationException();
        }
    }

    private final TaiKhoanDaoGia dao = new TaiKhoanDaoGia();
    /** Băm giả "bam:" + mật khẩu; BCrypt thật test ở MatKhauTest. */
    private final List<String> daKiemTra = new ArrayList<>();
    private final DangNhapService service = new DangNhapService(dao,
            (mk, bam) -> {
                daKiemTra.add(bam);
                return bam.equals("bam:" + mk);
            });

    @BeforeEach
    void taoTaiKhoan() {
        PhienDangNhap.ketThuc();
        dao.taiKhoan.add(new TaiKhoan(1, "admin", "Nguyễn Thị Thu Hương",
                VaiTro.QUAN_TRI, "bam:Admin@123", true, null));
        dao.taiKhoan.add(new TaiKhoan(2, "thuquy01", "Nguyễn Văn Hùng",
                VaiTro.THU_QUY, "bam:ThuQuy@2026", true, null));
        dao.taiKhoan.add(new TaiKhoan(3, "thuquy09", "Lê Văn Bình",
                VaiTro.THU_QUY, "bam:ThuQuy@2026", false, null));
    }

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    @Test
    void ac057DungThongTinThiVaoDuocVaCoPhien() {
        NguoiDung nd = service.dangNhap(" admin ", "Admin@123");
        assertEquals(new NguoiDung(1, "admin", "Nguyễn Thị Thu Hương",
                VaiTro.QUAN_TRI), nd);
        assertEquals(Optional.of(nd), PhienDangNhap.nguoiDung());
    }

    @Test
    void tenDangNhapKhongPhanBietHoaThuong() {
        assertEquals(VaiTro.THU_QUY,
                service.dangNhap("ThuQuy01", "ThuQuy@2026").vaiTro());
    }

    @Test
    void ac058SaiMatKhauBiTuChoiVaKhongCoPhien() {
        LoiDangNhap loi = assertThrows(LoiDangNhap.class,
                () -> service.dangNhap("admin", "admin@123"));
        assertEquals("Tên đăng nhập hoặc mật khẩu không đúng.",
                loi.getMessage());
        assertFalse(PhienDangNhap.daDangNhap());
    }

    @Test
    void ac058SaiTenBaoCungMotCauVaVanKiemTraMatKhau() {
        LoiDangNhap loi = assertThrows(LoiDangNhap.class,
                () -> service.dangNhap("khongco", "Admin@123"));
        assertEquals(DangNhapService.SAI_THONG_TIN, loi.getMessage());
        // Vẫn kiểm tra với chuỗi băm giả để thời gian như sai mật khẩu.
        assertEquals(1, daKiemTra.size());
        assertFalse(PhienDangNhap.daDangNhap());
    }

    @Test
    void taiKhoanBiKhoaKhongDangNhapDuoc() {
        LoiDangNhap loi = assertThrows(LoiDangNhap.class,
                () -> service.dangNhap("thuquy09", "ThuQuy@2026"));
        assertEquals(DangNhapService.BI_KHOA, loi.getMessage());
        assertFalse(PhienDangNhap.daDangNhap());
    }

    @Test
    void taiKhoanBiKhoaSaiMatKhauChiBaoSaiThongTin() {
        LoiDangNhap loi = assertThrows(LoiDangNhap.class,
                () -> service.dangNhap("thuquy09", "sai-mat-khau"));
        assertEquals(DangNhapService.SAI_THONG_TIN, loi.getMessage());
    }

    @Test
    void deTrongBaoTheoTungO() {
        LoiNhapLieu loi = assertThrows(LoiNhapLieu.class,
                () -> service.dangNhap("  ", ""));
        assertEquals("Nhập tên đăng nhập.", loi.loiCua(O.TEN_DANG_NHAP));
        assertEquals("Nhập mật khẩu.", loi.loiCua(O.MAT_KHAU));
        assertTrue(daKiemTra.isEmpty());
    }

    @Test
    void dangNhapSaiSauKhiDaDangNhapThiMatPhienCu() {
        service.dangNhap("admin", "Admin@123");
        assertThrows(LoiDangNhap.class,
                () -> service.dangNhap("thuquy01", "sai-mat-khau"));
        assertFalse(PhienDangNhap.daDangNhap());
    }

    @Test
    void dangXuatKetThucPhien() {
        service.dangNhap("admin", "Admin@123");
        service.dangXuat();
        assertFalse(PhienDangNhap.daDangNhap());
    }

    @Test
    void loiCsdlChuyenNguyenLenController() {
        dao.loi = new LoiCsdl(LoaiLoiCsdl.KHONG_TOI_MAY_CHU, null);
        assertSame(dao.loi, assertThrows(LoiCsdl.class,
                () -> service.dangNhap("admin", "Admin@123")));
    }
}
