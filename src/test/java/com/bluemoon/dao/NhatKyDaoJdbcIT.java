package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.config.NguonKetNoi;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử nhật ký trên MySQL thật (mvn verify -Pmysql). */
class NhatKyDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final NhatKyDaoJdbc dao = new NhatKyDaoJdbc(nguon);

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
    }

    @Test
    void ghiCungGiaoDichVaRollbackCungNghiepVu()
            throws SQLException {
        int taiKhoanId = themTaiKhoan();
        try (Connection ketNoi = nguon.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            dao.ghi(ketNoi, taiKhoanId, "THEM_HO", "Hộ A0101",
                    "Thêm hộ mới");
            assertEquals(1, demNhatKy(ketNoi));
            ketNoi.rollback();
            assertEquals(0, demNhatKy(ketNoi));
        }
    }

    @Test
    void ghiVaCommitThiDongThoiTonTai() throws SQLException {
        int taiKhoanId = themTaiKhoan();
        try (Connection ketNoi = nguon.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            dao.ghi(ketNoi, taiKhoanId, "SUA_HO", "Hộ A0101",
                    "Diện tích: 70 -> 75 m2");
            ketNoi.commit();
        }
        try (Connection ketNoi = nguon.moKetNoi()) {
            assertEquals(1, demNhatKy(ketNoi));
        }
        assertEquals(1, dao.danhSachMoiNhat(10).size());
    }

    @Test
    void khongTheSuaHoacXoaNhatKy() throws SQLException {
        int taiKhoanId = themTaiKhoan();
        try (Connection ketNoi = nguon.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            dao.ghi(ketNoi, taiKhoanId, "THEM_HO", "Hộ A0101", "Thêm hộ");
            ketNoi.commit();
        }
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement sua = ketNoi.prepareStatement(
                        "UPDATE nhat_ky SET noi_dung = 'đã sửa' WHERE id = 1")) {
            assertThrows(SQLException.class, sua::executeUpdate);
        }
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement xoa = ketNoi.prepareStatement(
                        "DELETE FROM nhat_ky WHERE id = 1")) {
            assertThrows(SQLException.class, xoa::executeUpdate);
        }
        try (Connection ketNoi = nguon.moKetNoi()) {
            assertEquals(1, demNhatKy(ketNoi));
        }
    }

    private int themTaiKhoan() throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(
                        "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_bam, "
                                + "ho_ten, vai_tro) VALUES "
                                + "('admin', 'hash', 'Quản trị', 'QUAN_TRI')",
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
            lenh.executeUpdate();
            try (ResultSet khoa = lenh.getGeneratedKeys()) {
                khoa.next();
                return khoa.getInt(1);
            }
        }
    }

    private static int demNhatKy(Connection ketNoi) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT COUNT(*) FROM nhat_ky");
                ResultSet kq = lenh.executeQuery()) {
            kq.next();
            return kq.getInt(1);
        }
    }
}
