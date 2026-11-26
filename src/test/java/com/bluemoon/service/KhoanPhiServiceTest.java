package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.model.YeuCauTaoKhoanTuNguyen;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử nghiệp vụ quản lý khoản thu tự nguyện (US-006). Sử dụng DAO giả,
 * độc lập với MySQL.
 */
class KhoanPhiServiceTest {

    private KhoanPhiDaoGia daoGia;
    private KhoanPhiService service;

    @BeforeEach
    void setUp() {
        daoGia = new KhoanPhiDaoGia();
        service = new KhoanPhiService(daoGia);
        PhienDangNhap.batDau(new NguoiDung(1, "admin", "Ban Quản Trị",
                VaiTro.QUAN_TRI));
    }

    @AfterEach
    void tearDown() {
        PhienDangNhap.ketThuc();
    }

    @Test
    void chuaDangNhapBiTuChoi() {
        PhienDangNhap.ketThuc();
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo", CachTinhPhi.KHONG_CO_DINH, null,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), null);

        assertThrows(LoiKhongCoQuyen.class, () -> service.layDanhSach());
        assertThrows(LoiKhongCoQuyen.class,
                () -> service.taoKhoanTuNguyen(yc));
    }

    @Test
    void thuQuyKhongCoQuyenTaoKhoanThu() {
        PhienDangNhap.batDau(new NguoiDung(2, "thuquy", "Thủ Quỹ",
                VaiTro.THU_QUY));
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo", CachTinhPhi.KHONG_CO_DINH, null,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), null);

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.taoKhoanTuNguyen(yc));
    }

    @Test
    void ac019_taoKhoanTuNguyenThanhCong() {
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo 2026",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 12, 31),
                "Ủng hộ bà con nghèo");

        int id = service.taoKhoanTuNguyen(yc);
        assertTrue(id > 0);

        List<KhoanPhi> danhSach = service.layDanhSach();
        assertEquals(1, danhSach.size());
        KhoanPhi kp = danhSach.get(0);
        assertEquals("Quỹ vì người nghèo 2026", kp.ten());
        assertEquals(LoaiKhoanPhi.TU_NGUYEN, kp.loai());
        assertEquals(CachTinhPhi.KHONG_CO_DINH, kp.cachTinh());
        assertNull(kp.donGia());
        assertEquals(TrangThaiKhoanPhi.DANG_THU, kp.trangThai());
        assertEquals("Ủng hộ bà con nghèo", kp.ghiChu());
    }

    @Test
    void ac020_trungTenTrongCungDotBiChan() {
        YeuCauTaoKhoanTuNguyen yc1 = new YeuCauTaoKhoanTuNguyen(
                "Quỹ biển đảo",
                CachTinhPhi.CO_DINH_THEO_HO,
                BigDecimal.valueOf(50000),
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30),
                null);
        service.taoKhoanTuNguyen(yc1);

        YeuCauTaoKhoanTuNguyen yc2 = new YeuCauTaoKhoanTuNguyen(
                "Quỹ biển đảo",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 12, 31),
                null);

        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(yc2));
        assertNotNull(ex.loiCua(KhoanPhiService.O.TEN));
        assertTrue(ex.loiCua(KhoanPhiService.O.TEN).contains("đã tồn tại"));
    }

    @Test
    void trungTenKhacDotChoPhepLuu() {
        YeuCauTaoKhoanTuNguyen dot1 = new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                null);
        service.taoKhoanTuNguyen(dot1);

        YeuCauTaoKhoanTuNguyen dot2 = new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30),
                null);

        int id2 = service.taoKhoanTuNguyen(dot2);
        assertTrue(id2 > 0);
        assertEquals(2, service.layDanhSach().size());
    }

    @Test
    void ac021_khoanTuNguyenKhongNhapSoTienCoDinhVanLuuDuoc() {
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                "Quỹ khuyến học",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                null);

        int id = service.taoKhoanTuNguyen(yc);
        assertTrue(id > 0);

        KhoanPhi kp = service.layDanhSach().get(0);
        assertNull(kp.donGia());
        assertEquals(CachTinhPhi.KHONG_CO_DINH, kp.cachTinh());
    }

    @Test
    void coDinhTheoHoYeuCauDonGiaLonHonKhong() {
        YeuCauTaoKhoanTuNguyen ycThieuTien = new YeuCauTaoKhoanTuNguyen(
                "Quỹ ủng hộ bão lũ",
                CachTinhPhi.CO_DINH_THEO_HO,
                null,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null);
        LoiNhapLieu ex1 = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(ycThieuTien));
        assertNotNull(ex1.loiCua(KhoanPhiService.O.DON_GIA));

        YeuCauTaoKhoanTuNguyen ycTienAm = new YeuCauTaoKhoanTuNguyen(
                "Quỹ ủng hộ bão lũ",
                CachTinhPhi.CO_DINH_THEO_HO,
                BigDecimal.valueOf(-1000),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null);
        LoiNhapLieu ex2 = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(ycTienAm));
        assertNotNull(ex2.loiCua(KhoanPhiService.O.DON_GIA));
    }

    @Test
    void kiemTraCacTruongBatBuoc() {
        YeuCauTaoKhoanTuNguyen ycTrong = new YeuCauTaoKhoanTuNguyen(
                "", null, null, null, null, null);
        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(ycTrong));
        assertNotNull(ex.loiCua(KhoanPhiService.O.TEN));
        assertNotNull(ex.loiCua(KhoanPhiService.O.CACH_TINH));
        assertNotNull(ex.loiCua(KhoanPhiService.O.TU_NGAY));
        assertNotNull(ex.loiCua(KhoanPhiService.O.DEN_NGAY));
    }

    @Test
    void ngayKetThucTruocNgayBatDauBiBaoLoi() {
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                "Quỹ trung thu",
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 10),
                null);
        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(yc));
        assertNotNull(ex.loiCua(KhoanPhiService.O.DEN_NGAY));
        assertTrue(ex.loiCua(KhoanPhiService.O.DEN_NGAY)
                .contains("không được trước"));
    }

    @Test
    void tenDaiQuaGioiHanBiBaoLoi() {
        String tenDai = "A".repeat(101);
        YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                tenDai,
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31),
                null);
        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.taoKhoanTuNguyen(yc));
        assertNotNull(ex.loiCua(KhoanPhiService.O.TEN));
    }

    @Test
    void timKiemTheoTenKhongPhanBietDauVaHoaThuong() {
        service.taoKhoanTuNguyen(new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo",
                CachTinhPhi.KHONG_CO_DINH, null,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null));
        service.taoKhoanTuNguyen(new YeuCauTaoKhoanTuNguyen(
                "Quỹ biển đảo Việt Nam",
                CachTinhPhi.CO_DINH_THEO_HO, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), null));

        assertEquals(2, service.timKiem("").size());
        assertEquals(1, service.timKiem("ngheo").size());
        assertEquals(1, service.timKiem("NGHÈO").size());
        assertEquals(1, service.timKiem("bien dao").size());
        assertEquals(0, service.timKiem("khong ton tai").size());
    }

    @Test
    void ac015_tinhPhiDichVuTheoDienTichVaDonGia() {
        BigDecimal dienTich = new BigDecimal("72.5");
        BigDecimal donGia = BigDecimal.valueOf(5000);

        BigDecimal ketQua = service.tinhPhi(dienTich, donGia);

        assertEquals(0, new BigDecimal("362500").compareTo(ketQua));
    }

    @Test
    void ac016_quanTriVienCapNhatDonGiaPhiQuanLy() {
        int id = daoGia.them(new KhoanPhi(0, "Phí quản lý chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(7000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        service.capNhatDonGia(id, BigDecimal.valueOf(8000));

        KhoanPhi capNhat = service.timTheoId(id).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(8000).compareTo(capNhat.donGia()));
        assertFalse(daoGia.layNhatKy().isEmpty());
        assertTrue(daoGia.layNhatKy().get(0).contains("8.000"));
    }

    @Test
    void quanTriVienCapNhatDonGiaPhiDichVu() {
        int id = daoGia.them(new KhoanPhi(0, "Phí dịch vụ chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(5000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        service.capNhatDonGia(id, BigDecimal.valueOf(10000));

        KhoanPhi capNhat = service.timTheoId(id).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(capNhat.donGia()));
    }

    @Test
    void ac017_donGiaPhiDichVuDuoi2500BiChan() {
        int id = daoGia.them(new KhoanPhi(0, "Phí dịch vụ chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(5000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(2000)));
        assertNotNull(ex.loiCua(KhoanPhiService.O.DON_GIA));
        assertTrue(ex.loiCua(KhoanPhiService.O.DON_GIA).contains("2.500"));
    }

    @Test
    void ac017_donGiaPhiDichVuTren16500BiChan() {
        int id = daoGia.them(new KhoanPhi(0, "Phí dịch vụ chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(5000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(17000)));
        assertNotNull(ex.loiCua(KhoanPhiService.O.DON_GIA));
        assertTrue(ex.loiCua(KhoanPhiService.O.DON_GIA).contains("16.500"));
    }

    @Test
    void donGiaPhiQuanLyDuoi7000BiChan() {
        int id = daoGia.them(new KhoanPhi(0, "Phí quản lý chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(7000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        LoiNhapLieu ex = assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(6500)));
        assertNotNull(ex.loiCua(KhoanPhiService.O.DON_GIA));
        assertTrue(ex.loiCua(KhoanPhiService.O.DON_GIA).contains("7.000"));
    }

    @Test
    void ac018_tinhPhiQuanLyLamTronChinhXac() {
        BigDecimal dienTich = new BigDecimal("72.345");
        BigDecimal donGia = new BigDecimal("5555");

        // 72.345 * 5555 = 401876.475 -> làm tròn đến đơn vị: 401876
        BigDecimal ketQua = service.tinhPhi(dienTich, donGia);

        assertEquals(0, new BigDecimal("401876").compareTo(ketQua));
    }

    @Test
    void tinhPhiVoiDauVaoKhongHopLeTraVeKhong() {
        assertEquals(BigDecimal.ZERO,
                service.tinhPhi(null, BigDecimal.valueOf(5000)));
        assertEquals(BigDecimal.ZERO,
                service.tinhPhi(BigDecimal.valueOf(-10),
                        BigDecimal.valueOf(5000)));
        assertEquals(BigDecimal.ZERO,
                service.tinhPhi(new BigDecimal("70"), null));
        assertEquals(BigDecimal.ZERO,
                service.tinhPhi(new BigDecimal("70"),
                        BigDecimal.valueOf(-100)));
    }

    @Test
    void thuQuyKhongDuocDoiDonGia() {
        int id = daoGia.them(new KhoanPhi(0, "Phí quản lý chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(7000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        PhienDangNhap.batDau(new NguoiDung(2, "thuquy", "Thủ Quỹ",
                VaiTro.THU_QUY));

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(8000)));
    }

    @Test
    void khongChoDoiDonGiaKhoanTuNguyen() {
        int id = service.taoKhoanTuNguyen(new YeuCauTaoKhoanTuNguyen(
                "Quỹ vì người nghèo", CachTinhPhi.CO_DINH_THEO_HO,
                BigDecimal.valueOf(50000), LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30), null));

        assertThrows(LoiNghiepVu.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(60000)));
    }

    @Test
    void capNhatDonGiaKhoanKhongTonTai() {
        assertThrows(LoiNghiepVu.class,
                () -> service.capNhatDonGia(999, BigDecimal.valueOf(8000)));
    }

    @Test
    void capNhatDonGiaAmHoacBangKhong() {
        int id = daoGia.them(new KhoanPhi(0, "Phí quản lý chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(7000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, null));
        assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, BigDecimal.ZERO));
        assertThrows(LoiNhapLieu.class,
                () -> service.capNhatDonGia(id, BigDecimal.valueOf(-5000)));
    }

    @Test
    void timTheoTenVaTimTheoId() {
        int id = daoGia.them(new KhoanPhi(0, "Phí dịch vụ chung cư",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(5000), LocalDate.of(2026, 1, 1),
                null, TrangThaiKhoanPhi.DANG_THU, null));

        Optional<KhoanPhi> theoId = service.timTheoId(id);
        assertTrue(theoId.isPresent());
        assertEquals("Phí dịch vụ chung cư", theoId.get().ten());

        Optional<KhoanPhi> theoTen = service.timTheoTen(
                "Phí dịch vụ chung cư");
        assertTrue(theoTen.isPresent());
        assertEquals(id, theoTen.get().id());

        assertFalse(service.timTheoId(999).isPresent());
        assertFalse(service.timTheoTen("Không có").isPresent());
    }

    private static final class KhoanPhiDaoGia implements KhoanPhiDao {
        private final List<KhoanPhi> danhSach = new ArrayList<>();
        private final List<String> nhatKy = new ArrayList<>();
        private int demId = 1;

        @Override
        public int them(KhoanPhi kp) {
            int id = demId++;
            KhoanPhi moi = new KhoanPhi(id, kp.ten(), kp.loai(), kp.cachTinh(),
                    kp.donGia(), kp.tuNgay(), kp.denNgay(), kp.trangThai(),
                    kp.ghiChu());
            danhSach.add(0, moi);
            return id;
        }

        @Override
        public boolean tonTaiTenTrongDot(String ten, LocalDate tuNgay) {
            return danhSach.stream().anyMatch(k ->
                    k.ten().equalsIgnoreCase(ten.trim())
                    && k.tuNgay().equals(tuNgay));
        }

        @Override
        public List<KhoanPhi> layDanhSach() {
            return new ArrayList<>(danhSach);
        }

        @Override
        public Optional<KhoanPhi> timTheoId(int id) {
            return danhSach.stream().filter(k -> k.id() == id).findFirst();
        }

        @Override
        public Optional<KhoanPhi> timTheoTen(String ten) {
            return danhSach.stream().filter(k ->
                    k.ten().equalsIgnoreCase(ten.trim())).findFirst();
        }

        @Override
        public void capNhatDonGia(int id, BigDecimal donGiaMoi,
                NguoiDung nguoiDung, String noiDungNhatKy) {
            for (int i = 0; i < danhSach.size(); i++) {
                KhoanPhi kp = danhSach.get(i);
                if (kp.id() == id) {
                    danhSach.set(i, new KhoanPhi(
                            kp.id(), kp.ten(), kp.loai(), kp.cachTinh(),
                            donGiaMoi, kp.tuNgay(), kp.denNgay(),
                            kp.trangThai(), kp.ghiChu()));
                    nhatKy.add(noiDungNhatKy);
                    return;
                }
            }
        }

        List<String> layNhatKy() {
            return new ArrayList<>(nhatKy);
        }
    }
}
