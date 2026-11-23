package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhau;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử tích hợp {@link TaiKhoanDaoJdbc} trên MySQL thật (-Pmysql). */
class TaiKhoanDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final TaiKhoanDaoJdbc dao = new TaiKhoanDaoJdbc(nguon);
    private NguoiDung quanTri;

    @BeforeEach
    void datLaiCsdl() throws SQLException, IOException {
        CsdlKiemThu.dungLaiBang();
        quanTri = CsdlKiemThu.taoQuanTriVien(nguon);
    }

    /** AC-054: tài khoản mới và nhật ký được commit cùng nhau. */
    @Test
    void ac054GhiTaiKhoanMoi() throws SQLException {
        String bam = MatKhau.bam("MatKhau@1");
        assertTrue(dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, bam), quanTri));
        TaiKhoan tk = dao.timTheoTenDangNhap("thuquy01").orElseThrow();
        assertEquals("Trần Thị Mai", tk.hoTen());
        assertEquals(VaiTro.THU_QUY, tk.vaiTro());
        assertTrue(MatKhau.khop("MatKhau@1", tk.matKhauBam()));
        assertTrue(tk.hoatDong());
        assertEquals(1, demNhatKy());
        assertEquals("TAO_TAI_KHOAN", thaoTacNhatKyCuoi());
    }

    /** AC-055: trùng tên, kể cả khác chữ hoa thường, không ghi thêm. */
    @Test
    void ac055TrungTenDangNhapTraFalse() throws SQLException {
        assertTrue(dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1")), quanTri));
        assertFalse(dao.them(new TaiKhoanMoi("ThuQuy01", "Lê Văn Bình",
                VaiTro.QUAN_TRI, MatKhau.bam("MatKhau@2")), quanTri));
        assertEquals(1, dem("SELECT COUNT(*) FROM tai_khoan "
                + "WHERE ten_dang_nhap = 'thuquy01'"));
        assertEquals(1, demNhatKy());
    }

    /** US-021: tìm theo tên không phân biệt hoa thường, đọc đủ cột. */
    @Test
    void timTheoTenDangNhap() {
        String bam = MatKhau.bam("MatKhau@1");
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, bam), quanTri);
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
        dao.them(new TaiKhoanMoi("thuquy02", "Lê Thị Lan",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1")), quanTri);
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
    void timTheoId() {
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1")), quanTri);
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();
        TaiKhoan tk = dao.timTheoId(id).orElseThrow();
        assertEquals("thuquy01", tk.tenDangNhap());
        assertNull(tk.soDienThoai());
        assertTrue(dao.timTheoId(id + 1000).isEmpty());
    }

    /** AC-065: sửa thông tin cá nhân, không đổi tên đăng nhập/vai trò. */
    @Test
    void ac065CapNhatThongTin() throws SQLException {
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1")), quanTri);
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();
        assertTrue(dao.capNhatThongTin(id, "Trần Thị Mai Anh",
                "0912345678", quanTri));
        TaiKhoan tk = dao.timTheoId(id).orElseThrow();
        assertEquals("Trần Thị Mai Anh", tk.hoTen());
        assertEquals("0912345678", tk.soDienThoai());
        assertEquals("thuquy01", tk.tenDangNhap());
        assertEquals(VaiTro.THU_QUY, tk.vaiTro());
        assertTrue(dao.capNhatThongTin(id, "Trần Thị Mai Anh", null, quanTri));
        assertNull(dao.timTheoId(id).orElseThrow().soDienThoai());
        assertFalse(dao.capNhatThongTin(id + 1000, "Ai đó", null, quanTri));
        assertEquals("SUA_THONG_TIN_TAI_KHOAN", thaoTacNhatKyCuoi());
    }

    /** AC-063: đổi mật khẩu cá nhân chỉ lưu bản băm. */
    @Test
    void ac063DoiMatKhau() throws SQLException {
        dao.them(new TaiKhoanMoi("thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY, MatKhau.bam("MatKhau@1")), quanTri);
        String bamKhac = MatKhau.bam("MatKhau@1");
        dao.them(new TaiKhoanMoi("thuquy02", "Lê Thị Lan",
                VaiTro.THU_QUY, bamKhac), quanTri);
        int id = dao.timTheoTenDangNhap("thuquy01").orElseThrow().id();
        assertTrue(dao.doiMatKhau(id, MatKhau.bam("MatKhauMoi@2"),
                dao.timTheoId(id).orElseThrow().nguoiDung()));
        String bamMoi = dao.timTheoId(id).orElseThrow().matKhauBam();
        assertTrue(MatKhau.khop("MatKhauMoi@2", bamMoi));
        assertFalse(MatKhau.khop("MatKhau@1", bamMoi));
        assertEquals("DOI_MAT_KHAU", thaoTacNhatKyCuoi());
        assertEquals(bamKhac,
                dao.timTheoTenDangNhap("thuquy02").orElseThrow().matKhauBam());
        assertFalse(dao.doiMatKhau(id + 1000, bamMoi, quanTri));
    }

    // US-022: quản lý tài khoản (giả định 18)

    private int them(String ten, VaiTro vaiTro) {
        dao.them(new TaiKhoanMoi(ten, "Người thử " + ten, vaiTro, "bam"),
                quanTri);
        return dao.timTheoTenDangNhap(ten).orElseThrow().id();
    }

    @Test
    void danhSachXepTheoTen() {
        them("thuquy02", VaiTro.THU_QUY);
        them("admin", VaiTro.QUAN_TRI);
        them("thuquy01", VaiTro.THU_QUY);
        assertEquals(List.of("admin", "test-admin", "thuquy01", "thuquy02"),
                dao.danhSach().stream().map(TaiKhoan::tenDangNhap).toList());
    }

    @Test
    void doiVaiTroGiuMotQuanTriHoatDong() throws SQLException {
        int a = quanTri.id();
        int b = them("quantri02", VaiTro.QUAN_TRI);
        int c = them("thuquy01", VaiTro.THU_QUY);
        assertTrue(dao.doiVaiTro(c, VaiTro.QUAN_TRI, quanTri));
        assertEquals(VaiTro.QUAN_TRI, dao.timTheoId(c).orElseThrow().vaiTro());
        assertTrue(dao.doiVaiTro(b, VaiTro.THU_QUY, quanTri));
        assertTrue(dao.doiVaiTro(c, VaiTro.THU_QUY, quanTri));
        assertFalse(dao.doiVaiTro(a, VaiTro.THU_QUY, quanTri));
        assertEquals(VaiTro.QUAN_TRI, dao.timTheoId(a).orElseThrow().vaiTro());
        assertFalse(dao.doiVaiTro(a + 1000, VaiTro.THU_QUY, quanTri));
        assertEquals("DOI_VAI_TRO", thaoTacNhatKyCuoi());
        assertEquals(5, demNhatKy()); // 2 lần tạo + 3 lần đổi vai trò
    }

    @Test
    void khoaMoKhoaGiuMotQuanTriHoatDong() throws SQLException {
        int a = quanTri.id();
        int b = them("quantri02", VaiTro.QUAN_TRI);
        assertTrue(dao.datHoatDong(b, false, quanTri));
        assertFalse(dao.timTheoId(b).orElseThrow().hoatDong());
        assertFalse(dao.datHoatDong(a, false, quanTri));
        assertFalse(dao.doiVaiTro(a, VaiTro.THU_QUY, quanTri));
        assertTrue(dao.timTheoId(a).orElseThrow().hoatDong());
        assertTrue(dao.datHoatDong(b, true, quanTri));
        assertTrue(dao.datHoatDong(a, false, quanTri));
        assertFalse(dao.datHoatDong(a + 1000, true, quanTri));
        assertEquals("KHOA_TAI_KHOAN", thaoTacNhatKyCuoi());
        assertEquals(4, demNhatKy()); // 1 tạo + khóa/mở/khóa thành công
    }

    @Test
    void datLaiMatKhauGhiNhatKyKhongLoMatKhau() throws SQLException {
        int id = them("thuquy01", VaiTro.THU_QUY);
        assertTrue(dao.datLaiMatKhau(id, MatKhau.bam("MatKhauTam@1"),
                quanTri));
        assertEquals("DAT_LAI_MAT_KHAU", thaoTacNhatKyCuoi());
        String noiDung = noiDungNhatKyCuoi();
        assertFalse(noiDung.contains("MatKhauTam@1"));
        assertTrue(MatKhau.khop("MatKhauTam@1",
                dao.timTheoId(id).orElseThrow().matKhauBam()));
    }

    @Test
    void ghiNhatKyThatBaiRollbackTaoTaiKhoan() throws SQLException {
        NhatKyDao loiNhatKy = new NhatKyDao() {
            @Override
            public void ghi(Connection ketNoi, int taiKhoanId,
                    String thaoTac, String doiTuong, String noiDung)
                    throws SQLException {
                throw new SQLException("Lỗi giả lập khi ghi nhật ký.");
            }

            @Override
            public List<NhatKy> danhSachMoiNhat(int gioiHan) {
                return List.of();
            }
        };
        TaiKhoanDaoJdbc daoCoLoi = new TaiKhoanDaoJdbc(nguon, loiNhatKy);
        assertThrows(LoiCsdl.class, () -> daoCoLoi.them(
                new TaiKhoanMoi("rollback01", "Người thử",
                        VaiTro.THU_QUY, "bam"), quanTri));
        assertEquals(0, dem("SELECT COUNT(*) FROM tai_khoan "
                + "WHERE ten_dang_nhap = 'rollback01'"));
        assertEquals(0, demNhatKy());
    }

    private int demNhatKy() throws SQLException {
        return dem("SELECT COUNT(*) FROM nhat_ky");
    }

    private int dem(String sql) throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            kq.next();
            return kq.getInt(1);
        }
    }

    private String thaoTacNhatKyCuoi() throws SQLException {
        return docCotNhatKyCuoi("thao_tac");
    }

    private String noiDungNhatKyCuoi() throws SQLException {
        return docCotNhatKyCuoi("noi_dung");
    }

    private String docCotNhatKyCuoi(String cot) throws SQLException {
        String sql = "SELECT " + cot + " FROM nhat_ky "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            return kq.next() ? kq.getString(1) : null;
        }
    }
}
