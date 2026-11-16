package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhau;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử tích hợp {@link TaiKhoanDaoJdbc} trên MySQL thật (-Pmysql). */
class TaiKhoanDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final TaiKhoanDao dao = new TaiKhoanDaoJdbc(nguon);

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
    }

    /** AC-054: ghi đủ cột, mật khẩu chỉ lưu dạng băm, mặc định hoạt động. */
    @Test
    void ac054GhiTaiKhoanMoi() throws SQLException {
        String bam = MatKhau.bam("MatKhau@1");
        assertTrue(dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, bam)));
        try (Connection ketNoi = nguon.moKetNoi();
                Statement lenh = ketNoi.createStatement();
                ResultSet kq = lenh.executeQuery("SELECT ten_dang_nhap, "
                        + "ho_ten, vai_tro, mat_khau_bam, hoat_dong "
                        + "FROM tai_khoan")) {
            assertTrue(kq.next());
            assertEquals("thuquy01", kq.getString(1));
            assertEquals("Trần Thị Mai", kq.getString(2));
            assertEquals("THU_QUY", kq.getString(3));
            assertEquals(bam, kq.getString(4));
            assertTrue(MatKhau.khop("MatKhau@1", kq.getString(4)));
            assertTrue(kq.getBoolean(5));
            assertFalse(kq.next());
        }
    }

    /** AC-055: trùng tên, kể cả khác chữ hoa thường, không ghi thêm. */
    @Test
    void ac055TrungTenDangNhapTraFalse() throws SQLException {
        assertTrue(dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1"))));
        assertFalse(dao.them(new TaiKhoanMoi("ThuQuy01", "Lê Văn Bình",
                VaiTro.QUAN_TRI, MatKhau.bam("MatKhau@2"))));
        try (Connection ketNoi = nguon.moKetNoi();
                Statement lenh = ketNoi.createStatement();
                ResultSet kq = lenh.executeQuery(
                        "SELECT COUNT(*), MAX(ho_ten) FROM tai_khoan")) {
            kq.next();
            assertEquals(1, kq.getInt(1));
            assertEquals("Trần Thị Mai", kq.getString(2));
        }
    }
}
