package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauThemHo;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử tích hợp {@link HoGiaDinhDaoJdbc} trên MySQL thật (-Pmysql). */
class HoGiaDinhDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();

    // Sửa lỗi 1: Truyền nguồn kiểm thử vào DAO
    private final HoGiaDinhDaoJdbc dao = new HoGiaDinhDaoJdbc(nguon);
    private NguoiDung quanTri;

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
        quanTri = CsdlKiemThu.taoQuanTriVien(nguon);
    }

    private YeuCauThemHo taoYeuCauMau(String maCanHo, String cccd) {
        return new YeuCauThemHo(maCanHo, LoaiCanHo.CAN_HO, 5, 
                new BigDecimal("70.00"), "0912345678", 
                LocalDate.of(2026, 11, 1), "Nguyễn Văn A",
                LocalDate.of(1990, 1, 1), "NAM", cccd);
    }

    @Test
    void a_themHoGiaDinh_TraVeDungDanhSachVaSoThanhVien()
            throws SQLException {
        YeuCauThemHo yc = taoYeuCauMau("A-101", "001090123456");
        dao.themHoGiaDinh(yc, quanTri);

        List<HoGiaDinh> ds = dao.layDanhSachDangO();
        assertEquals(1, ds.size());
        assertEquals(1, demNhatKy());
        
        HoGiaDinh ho = ds.get(0);
        assertEquals("A-101", ho.maCanHo());
        assertEquals("Nguyễn Văn A", ho.tenChuHo());
        assertEquals(1, ho.soThanhVien());
    }

    @Test
    void b_themHoGiaDinh_TraVeIdLonHon0VaLuuDungChuHo() throws Exception {
        YeuCauThemHo yc = taoYeuCauMau("B-202", "001090123457");
        int hoId = dao.themHoGiaDinh(yc, quanTri);
        
        assertTrue(hoId > 0, "ID sinh ra phải lớn hơn 0");

        String sql = "SELECT COUNT(*) FROM nhan_khau "
                   + "WHERE ho_id = ? AND quan_he = 'CHU_HO'";
                   
        // Sửa lỗi 2: Dùng nguồn kiểm thử thay vì DB thật
        try (Connection ketNoi = nguon.moKetNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
            lenh.setInt(1, hoId);
            try (ResultSet kq = lenh.executeQuery()) {
                assertTrue(kq.next());
                assertEquals(1, kq.getInt(1), 
                        "Chỉ có 1 dòng CHU_HO cho hộ này");
            }
        }
    }

    @Test
    void c_tonTaiMaCanHoDangO_KiemTraChinhXac() {
        assertFalse(dao.tonTaiMaCanHoDangO("C-303"));
        
        dao.themHoGiaDinh(taoYeuCauMau("C-303", "001090123458"), quanTri);
        assertTrue(dao.tonTaiMaCanHoDangO("C-303"));
    }

    @Test
    void d_tonTaiCccdDangO_KiemTraChinhXac() {
        assertFalse(dao.tonTaiCccdDangO("001090123459"));
        
        dao.themHoGiaDinh(taoYeuCauMau("D-404", "001090123459"), quanTri);
        assertTrue(dao.tonTaiCccdDangO("001090123459"));
    }

    @Test
    void ghiNhatKyThatBaiRollbackCaHoVaChuHo() throws SQLException {
        NhatKyDao daoLoi = new NhatKyDao() {
            @Override
            public void ghi(Connection ketNoi, int taiKhoanId,
                    ThaoTac thaoTac, String doiTuong, String noiDung)
                    throws SQLException {
                throw new SQLException("Lỗi giả lập khi ghi nhật ký.");
            }

            @Override
            public List<com.bluemoon.model.NhatKy> danhSachMoiNhat(
                    int gioiHan) {
                return List.of();
            }
        };
        HoGiaDinhDaoJdbc daoCoLoi =
                new HoGiaDinhDaoJdbc(nguon, daoLoi);

        assertThrows(LoiCsdl.class, () -> daoCoLoi.themHoGiaDinh(
                taoYeuCauMau("E-505", "001090123460"), quanTri));
        assertEquals(0, demBang("ho_gia_dinh"));
        assertEquals(0, demBang("nhan_khau"));
        assertEquals(0, demNhatKy());
    }

    private int demNhatKy() throws SQLException {
        return demBang("nhat_ky");
    }

    private int demBang(String tenBang) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tenBang;
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            kq.next();
            return kq.getInt(1);
        }
    }
}
