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

    private static final class KhoanPhiDaoGia implements KhoanPhiDao {
        private final List<KhoanPhi> danhSach = new ArrayList<>();
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
    }
}
