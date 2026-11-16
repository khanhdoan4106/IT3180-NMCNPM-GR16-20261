package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.SoLieuCuDan;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử tích hợp {@link CuDanDaoJdbc} trên MySQL thật (-Pmysql). */
class CuDanDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
    }

    @Test
    void chiDemHoVaNhanKhauDangO() throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi()) {
            int hoA = themHo(ketNoi, "A0101", "DANG_O");
            themHo(ketNoi, "A0102", "DANG_O");
            int hoDi = themHo(ketNoi, "A0103", "DA_CHUYEN_DI");
            themNhanKhau(ketNoi, hoA, "CHU_HO", "DANG_O");
            themNhanKhau(ketNoi, hoA, "CON", "DANG_O");
            themNhanKhau(ketNoi, hoDi, "CHU_HO", "DA_CHUYEN_DI");
        }
        assertEquals(new SoLieuCuDan(2, 2),
                new CuDanDaoJdbc(nguon).demDangO());
    }

    /**
     * AC-071: hai máy dùng chung CSDL. Máy A ghi trong một giao dịch; máy B
     * (kết nối riêng) chưa thấy khi A chưa ghi xong, thấy ngay khi A ghi
     * xong.
     */
    @Test
    void ac071MayKhacThayDuLieuVuaGhi() throws SQLException {
        CuDanDao mayB = new CuDanDaoJdbc(nguon);
        assertEquals(0, mayB.demDangO().soHo());
        try (Connection mayA = nguon.moKetNoi()) {
            mayA.setAutoCommit(false);
            themHo(mayA, "B0201", "DANG_O");
            assertEquals(0, mayB.demDangO().soHo());
            mayA.commit();
            assertEquals(1, mayB.demDangO().soHo());
        }
    }

    private static int themHo(Connection ketNoi, String maCanHo,
            String trangThai) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, tang, "
                        + "dien_tich, ngay_chuyen_den, trang_thai) "
                        + "VALUES (?, 'CAN_HO', 1, 75.50, '2026-01-01', ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            lenh.setString(1, maCanHo);
            lenh.setString(2, trangThai);
            lenh.executeUpdate();
            try (ResultSet khoa = lenh.getGeneratedKeys()) {
                khoa.next();
                return khoa.getInt(1);
            }
        }
    }

    private static void themNhanKhau(Connection ketNoi, int hoId,
            String quanHe, String trangThai) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "INSERT INTO nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, "
                        + "quan_he, trang_thai) "
                        + "VALUES (?, 'Nguyễn Văn An', '2015-05-01', 'NAM', "
                        + "?, ?)")) {
            lenh.setInt(1, hoId);
            lenh.setString(2, quanHe);
            lenh.setString(3, trangThai);
            lenh.executeUpdate();
        }
    }
}
