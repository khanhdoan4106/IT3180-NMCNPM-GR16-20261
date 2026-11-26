package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.TrangThaiKhoanPhi;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử tích hợp {@link KhoanPhiDaoJdbc} trên MySQL thật (-Pmysql).
 */
class KhoanPhiDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final KhoanPhiDao dao = new KhoanPhiDaoJdbc(nguon);

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
    }

    @Test
    void themVaLayDanhSachKhoanTuNguyen() {
        KhoanPhi moi = new KhoanPhi(
                0,
                "Quỹ vì người nghèo",
                LoaiKhoanPhi.TU_NGUYEN,
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30),
                TrangThaiKhoanPhi.DANG_THU,
                "Ủng hộ hộ nghèo");

        int id = dao.them(moi);
        assertTrue(id > 0);

        Optional<KhoanPhi> timKiem = dao.timTheoId(id);
        assertTrue(timKiem.isPresent());
        KhoanPhi kp = timKiem.get();
        assertEquals("Quỹ vì người nghèo", kp.ten());
        assertEquals(LoaiKhoanPhi.TU_NGUYEN, kp.loai());
        assertEquals(CachTinhPhi.KHONG_CO_DINH, kp.cachTinh());
        assertNull(kp.donGia());
        assertEquals(LocalDate.of(2026, 11, 1), kp.tuNgay());
        assertEquals(LocalDate.of(2026, 11, 30), kp.denNgay());
        assertEquals(TrangThaiKhoanPhi.DANG_THU, kp.trangThai());
        assertEquals("Ủng hộ hộ nghèo", kp.ghiChu());

        List<KhoanPhi> ds = dao.layDanhSach();
        assertFalse(ds.isEmpty());
        assertEquals(id, ds.get(0).id());
    }

    @Test
    void themKhoanTuNguyenCoDinhTheoHo() {
        KhoanPhi moi = new KhoanPhi(
                0,
                "Quỹ biển đảo",
                LoaiKhoanPhi.TU_NGUYEN,
                CachTinhPhi.CO_DINH_THEO_HO,
                BigDecimal.valueOf(50000),
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30),
                TrangThaiKhoanPhi.DANG_THU,
                null);

        int id = dao.them(moi);
        assertTrue(id > 0);

        KhoanPhi kp = dao.timTheoId(id).orElseThrow();
        assertNotNull(kp.donGia());
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(kp.donGia()));
    }

    @Test
    void tonTaiTenTrongDot() {
        KhoanPhi moi = new KhoanPhi(
                0,
                "Quỹ khuyến học",
                LoaiKhoanPhi.TU_NGUYEN,
                CachTinhPhi.KHONG_CO_DINH,
                null,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                TrangThaiKhoanPhi.DANG_THU,
                null);
        dao.them(moi);

        assertTrue(dao.tonTaiTenTrongDot(
                "Quỹ khuyến học", LocalDate.of(2026, 9, 1)));
        assertTrue(dao.tonTaiTenTrongDot(
                "quỹ khuyến học", LocalDate.of(2026, 9, 1)));
        assertFalse(dao.tonTaiTenTrongDot(
                "Quỹ khuyến học", LocalDate.of(2026, 10, 1)));
        assertFalse(dao.tonTaiTenTrongDot(
                "Quỹ khác", LocalDate.of(2026, 9, 1)));
    }

    @Test
    void timTheoTenChinhXacVaKhongPhanBietHoaThuong() {
        KhoanPhi moi = new KhoanPhi(
                0,
                "Phí dịch vụ chung cư",
                LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(5000),
                LocalDate.of(2026, 1, 1),
                null,
                TrangThaiKhoanPhi.DANG_THU,
                null);
        int id = dao.them(moi);

        Optional<KhoanPhi> kq1 = dao.timTheoTen("Phí dịch vụ chung cư");
        assertTrue(kq1.isPresent());
        assertEquals(id, kq1.get().id());

        Optional<KhoanPhi> kq2 = dao.timTheoTen("  phí dịch vụ chung cư  ");
        assertTrue(kq2.isPresent());
        assertEquals(id, kq2.get().id());

        Optional<KhoanPhi> kq3 = dao.timTheoTen("Khoản không tồn tại");
        assertTrue(kq3.isEmpty());
    }

    @Test
    void capNhatDonGiaVaGhiNhatKy() throws SQLException {
        NguoiDung admin = CsdlKiemThu.taoQuanTriVien(nguon);
        KhoanPhi moi = new KhoanPhi(
                0,
                "Phí quản lý chung cư",
                LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH,
                BigDecimal.valueOf(7000),
                LocalDate.of(2026, 1, 1),
                null,
                TrangThaiKhoanPhi.DANG_THU,
                null);
        int id = dao.them(moi);

        dao.capNhatDonGia(id, BigDecimal.valueOf(8000), admin,
                "Đổi đơn giá từ 7.000 đ/m² thành 8.000 đ/m²");

        KhoanPhi sauCapNhat = dao.timTheoId(id).orElseThrow();
        assertEquals(0,
                BigDecimal.valueOf(8000).compareTo(sauCapNhat.donGia()));

        NhatKyDao nhatKyDao = new NhatKyDaoJdbc(nguon);
        List<NhatKy> danhSach = nhatKyDao.danhSachMoiNhat(5);
        assertFalse(danhSach.isEmpty());
        NhatKy nk = danhSach.get(0);
        assertEquals(ThaoTac.SUA_DON_GIA, nk.thaoTac());
        assertEquals("Khoản phí: Phí quản lý chung cư", nk.doiTuong());
        assertEquals("Đổi đơn giá từ 7.000 đ/m² thành 8.000 đ/m²",
                nk.noiDung());
    }
}
