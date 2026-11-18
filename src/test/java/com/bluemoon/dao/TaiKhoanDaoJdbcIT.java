package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhau;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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

    /** US-021: tìm theo tên không phân biệt hoa thường, đọc đủ cột. */
    @Test
    void timTheoTenDangNhap() throws SQLException {
        String bam = MatKhau.bam("MatKhau@1");
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai", VaiTro.THU_QUY,
                bam));
        TaiKhoan tk = dao.timTheoTenDangNhap("ThuQuy01").orElseThrow();
        assertEquals("thuquy01", tk.tenDangNhap());
        assertEquals("Trần Thị Mai", tk.hoTen());
        assertEquals(VaiTro.THU_QUY, tk.vaiTro());
        assertEquals(bam, tk.matKhauBam());
        assertTrue(tk.hoatDong());
        assertTrue(dao.timTheoTenDangNhap("khongco").isEmpty());
    }

    /** US-021: đọc đúng tài khoản đã bị khóa (hoat_dong = false). */
    @Test
    void docTaiKhoanBiKhoa() throws SQLException {
        dao.them(new TaiKhoanMoi("thuquy02", "Lê Thị Lan", VaiTro.THU_QUY,
                MatKhau.bam("MatKhau@1")));
        try (Connection ketNoi = nguon.moKetNoi();
                Statement lenh = ketNoi.createStatement()) {
            lenh.executeUpdate("UPDATE tai_khoan SET hoat_dong = FALSE "
                    + "WHERE ten_dang_nhap = 'thuquy02'");
        }
        assertFalse(dao.timTheoTenDangNhap("thuquy02").orElseThrow()
                .hoatDong());
    }

    /** US-023: đọc theo mã, có số điện thoại (mặc định trống). */
    @Test
    void timTheoId() throws SQLException {
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai", VaiTro.THU_QUY,
                MatKhau.bam("MatKhau@1")));
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();
        TaiKhoan tk = dao.timTheoId(id).orElseThrow();
        assertEquals("thuquy01", tk.tenDangNhap());
        assertNull(tk.soDienThoai());
        assertTrue(dao.timTheoId(id + 1000).isEmpty());
    }

    /** AC-065: sửa họ tên, số điện thoại; tên đăng nhập, vai trò giữ. */
    @Test
    void ac065CapNhatThongTin() throws SQLException {
        String bam = MatKhau.bam("MatKhau@1");
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai", VaiTro.THU_QUY,
                bam));
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();

        assertTrue(dao.capNhatThongTin(id, "Trần Thị Mai Anh",
                "0912345678"));
        TaiKhoan tk = dao.timTheoId(id).orElseThrow();
        assertEquals("Trần Thị Mai Anh", tk.hoTen());
        assertEquals("0912345678", tk.soDienThoai());
        assertEquals("thuquy01", tk.tenDangNhap());
        assertEquals(VaiTro.THU_QUY, tk.vaiTro());
        assertEquals(bam, tk.matKhauBam());

        // Lưu lại đúng giá trị cũ vẫn tính là có tài khoản.
        assertTrue(dao.capNhatThongTin(id, "Trần Thị Mai Anh", null));
        assertNull(dao.timTheoId(id).orElseThrow().soDienThoai());
        assertFalse(dao.capNhatThongTin(id + 1000, "Ai đó", null));
    }

    /** AC-063: ghi mật khẩu mới đã băm, chỉ đổi tài khoản đó. */
    @Test
    void ac063DoiMatKhau() throws SQLException {
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai", VaiTro.THU_QUY,
                MatKhau.bam("MatKhau@1")));
        String bamKhac = MatKhau.bam("MatKhau@1");
        dao.them(new TaiKhoanMoi("thuquy02", "Lê Thị Lan", VaiTro.THU_QUY,
                bamKhac));
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();

        assertTrue(dao.doiMatKhau(id, MatKhau.bam("MatKhauMoi@2")));
        String bamMoi = dao.timTheoId(id).orElseThrow().matKhauBam();
        assertTrue(MatKhau.khop("MatKhauMoi@2", bamMoi));
        assertFalse(MatKhau.khop("MatKhau@1", bamMoi));
        assertEquals(bamKhac,
                dao.timTheoTenDangNhap("thuquy02").orElseThrow().matKhauBam());
        assertFalse(dao.doiMatKhau(id + 1000, bamMoi));
    }

    // US-022: quản lý tài khoản (giả định 18)

    private int them(String ten, VaiTro vaiTro) {
        dao.them(new TaiKhoanMoi(ten, "Người thử " + ten, vaiTro, "bam"));
        return dao.timTheoTenDangNhap(ten).orElseThrow().id();
    }

    /** Danh sách đủ cột, xếp theo tên đăng nhập. */
    @Test
    void danhSachXepTheoTen() {
        them("thuquy02", VaiTro.THU_QUY);
        them("admin", VaiTro.QUAN_TRI);
        them("thuquy01", VaiTro.THU_QUY);
        assertEquals(List.of("admin", "thuquy01", "thuquy02"),
                dao.danhSach().stream().map(TaiKhoan::tenDangNhap).toList());
    }

    @Test
    void doiVaiTroGiuMotQuanTriHoatDong() {
        int a = them("admin", VaiTro.QUAN_TRI);
        int b = them("quantri02", VaiTro.QUAN_TRI);
        int c = them("thuquy01", VaiTro.THU_QUY);

        assertTrue(dao.doiVaiTro(c, VaiTro.QUAN_TRI));
        assertEquals(VaiTro.QUAN_TRI, dao.timTheoId(c).orElseThrow().vaiTro());
        assertTrue(dao.doiVaiTro(b, VaiTro.THU_QUY));
        assertTrue(dao.doiVaiTro(c, VaiTro.THU_QUY));
        // admin là Quản trị viên hoạt động cuối cùng.
        assertFalse(dao.doiVaiTro(a, VaiTro.THU_QUY));
        assertEquals(VaiTro.QUAN_TRI, dao.timTheoId(a).orElseThrow().vaiTro());
        assertFalse(dao.doiVaiTro(a + 1000, VaiTro.THU_QUY));
    }

    @Test
    void khoaMoKhoaGiuMotQuanTriHoatDong() {
        int a = them("admin", VaiTro.QUAN_TRI);
        int b = them("quantri02", VaiTro.QUAN_TRI);

        assertTrue(dao.datHoatDong(b, false));
        assertFalse(dao.timTheoId(b).orElseThrow().hoatDong());
        // Quản trị viên bị khóa không tính: không khóa được admin.
        assertFalse(dao.datHoatDong(a, false));
        assertFalse(dao.doiVaiTro(a, VaiTro.THU_QUY));
        assertTrue(dao.timTheoId(a).orElseThrow().hoatDong());
        assertTrue(dao.datHoatDong(b, true));
        assertTrue(dao.datHoatDong(a, false));
        assertFalse(dao.datHoatDong(a + 1000, true));
    }
}
