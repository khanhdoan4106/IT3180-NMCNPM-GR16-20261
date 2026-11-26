package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauThemHo;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NhanKhauDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final NhanKhauDaoJdbc dao = new NhanKhauDaoJdbc(nguon);
    private final HoGiaDinhDaoJdbc hoDao = new HoGiaDinhDaoJdbc(nguon);
    private NguoiDung quanTri;

    @BeforeEach
    void datLaiCsdl() throws Exception {
        CsdlKiemThu.dungLaiBang();
        quanTri = CsdlKiemThu.taoQuanTriVien(nguon);
    }

    @Test
    void themVaLayNhanKhau_ThanhCong() throws SQLException {
        YeuCauThemHo ycHo = new YeuCauThemHo("A-101", LoaiCanHo.CAN_HO, 1,
                new BigDecimal("70.00"), "090", LocalDate.now(),
                "Chu Ho", LocalDate.of(1980, 1, 1), "NAM", "001");
        int hoId = hoDao.themHoGiaDinh(ycHo, quanTri);

        YeuCauThemNhanKhau ycNk = taoYeuCau(hoId, "Vo Chu Ho", "002");
        int nkId = dao.themNhanKhau(ycNk, quanTri);
        assertTrue(nkId > 0);

        List<NhanKhau> ds = dao.layDanhSachTheoHo(hoId);
        assertEquals(2, ds.size());
        assertEquals("Vo Chu Ho", ds.get(1).hoTen());
        assertTrue(dao.tonTaiCccdDangO("002"));
        assertEquals(2, demNhatKy()); // THEM_HO + THEM_NHAN_KHAU
        assertEquals("THEM_NHAN_KHAU", thaoTacNhatKyCuoi());
        assertEquals("Nhân khẩu Vo Chu Ho (hộ A-101)",
                cotNhatKyCuoi("doi_tuong"));
        assertTrue(cotNhatKyCuoi("noi_dung")
                .startsWith("Thêm Vo Chu Ho ("));
        assertTrue(cotNhatKyCuoi("noi_dung").endsWith(") vào hộ A-101"));
    }

    @Test
    void ghiNhatKyThatBaiRollbackNhanKhau() throws SQLException {
        YeuCauThemHo ycHo = new YeuCauThemHo("B-202", LoaiCanHo.CAN_HO, 2,
                new BigDecimal("68.00"), "091", LocalDate.now(),
                "Chu Ho B", LocalDate.of(1980, 1, 1), "NAM", "003");
        int hoId = hoDao.themHoGiaDinh(ycHo, quanTri);
        int soNhatKyTruoc = demNhatKy();

        NhatKyDao daoNhatKyLoi = new NhatKyDao() {
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
        NhanKhauDaoJdbc daoCoLoi = new NhanKhauDaoJdbc(nguon, daoNhatKyLoi);

        assertThrows(LoiCsdl.class, () -> daoCoLoi.themNhanKhau(
                taoYeuCau(hoId, "Nhan Khau Loi", "004"), quanTri));
        assertEquals(1, demSoNhanKhau(hoId));
        assertEquals(soNhatKyTruoc, demNhatKy());
        assertFalse(dao.tonTaiCccdDangO("004"));
    }

    private YeuCauThemNhanKhau taoYeuCau(
            int hoId, String hoTen, String cccd) {
        return new YeuCauThemNhanKhau(hoId, hoTen,
                LocalDate.of(1982, 1, 1), "NU", "VO_CHONG", cccd,
                null, null, null);
    }

    private int demNhatKy() throws SQLException {
        return dem("SELECT COUNT(*) FROM nhat_ky");
    }

    private int demSoNhanKhau(int hoId) throws SQLException {
        return dem("SELECT COUNT(*) FROM nhan_khau WHERE ho_id = " + hoId);
    }

    private int dem(String sql) throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            kq.next();
            return kq.getInt(1);
        }
    }

    private String thaoTacNhatKyCuoi() {
        String sql = "SELECT thao_tac FROM nhat_ky "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            return kq.next() ? kq.getString(1) : null;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    private String cotNhatKyCuoi(String cot) {
        String sql = "SELECT " + cot + " FROM nhat_ky "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            return kq.next() ? kq.getString(1) : null;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
