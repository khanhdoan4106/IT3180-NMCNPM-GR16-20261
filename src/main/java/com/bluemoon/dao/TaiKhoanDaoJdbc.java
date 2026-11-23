package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Bản JDBC của {@link TaiKhoanDao}. */
public class TaiKhoanDaoJdbc implements TaiKhoanDao {

    /** Mã lỗi MySQL: trùng khóa duy nhất ({@code ten_dang_nhap}). */
    static final int MA_TRUNG_KHOA = 1062;

    private static final String SQL_THEM = "INSERT INTO tai_khoan "
            + "(ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_CHON = "SELECT id, ten_dang_nhap, "
            + "ho_ten, vai_tro, mat_khau_bam, hoat_dong, so_dien_thoai "
            + "FROM tai_khoan ";

    private static final String SQL_TIM_THEO_TEN = SQL_CHON
            + "WHERE ten_dang_nhap = ?";

    private static final String SQL_TIM_THEO_ID = SQL_CHON + "WHERE id = ?";

    private static final String SQL_CAP_NHAT_THONG_TIN = "UPDATE tai_khoan "
            + "SET ho_ten = ?, so_dien_thoai = ? WHERE id = ?";

    private static final String SQL_DOI_MAT_KHAU = "UPDATE tai_khoan "
            + "SET mat_khau_bam = ? WHERE id = ?";

    private static final String SQL_DANH_SACH = SQL_CHON
            + "ORDER BY ten_dang_nhap";

    /**
     * Khóa các Quản trị viên đang hoạt động tới hết giao dịch: hai máy cùng
     * hạ quyền hoặc khóa hai Quản trị viên cuối cùng thì máy sau phải chờ
     * và thấy kết quả của máy trước.
     */
    private static final String SQL_KHOA_QUAN_TRI = "SELECT id FROM "
            + "tai_khoan WHERE vai_tro = 'QUAN_TRI' AND hoat_dong = TRUE "
            + "FOR UPDATE";

    private static final String SQL_KHOA_DONG = "SELECT ten_dang_nhap, "
            + "ho_ten, so_dien_thoai, vai_tro, hoat_dong "
            + "FROM tai_khoan WHERE id = ? FOR UPDATE";

    private static final String SQL_DOI_VAI_TRO = "UPDATE tai_khoan "
            + "SET vai_tro = ? WHERE id = ?";

    private static final String SQL_DAT_HOAT_DONG = "UPDATE tai_khoan "
            + "SET hoat_dong = ? WHERE id = ?";

    private final NguonKetNoi nguonKetNoi;
    private final NhatKyDao nhatKyDao;

    /** Dùng CSDL theo {@code config/db.properties}. */
    public TaiKhoanDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Dùng một nguồn kết nối khác, vd CSDL kiểm thử.
     *
     * @param nguonKetNoi nơi lấy kết nối
     */
    public TaiKhoanDaoJdbc(NguonKetNoi nguonKetNoi) {
        this(nguonKetNoi, new NhatKyDaoJdbc(nguonKetNoi));
    }

    /** Cho phép thay DAO nhật ký để kiểm thử rollback. */
    public TaiKhoanDaoJdbc(NguonKetNoi nguonKetNoi, NhatKyDao nhatKyDao) {
        this.nguonKetNoi = nguonKetNoi;
        this.nhatKyDao = nhatKyDao;
    }

    @Override
    public boolean them(TaiKhoanMoi taiKhoan, NguoiDung nguoiThucHien) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement lenh = ketNoi.prepareStatement(
                        SQL_THEM, Statement.RETURN_GENERATED_KEYS)) {
                    lenh.setString(1, taiKhoan.tenDangNhap());
                    lenh.setString(2, taiKhoan.matKhauBam());
                    lenh.setString(3, taiKhoan.hoTen());
                    lenh.setString(4, taiKhoan.vaiTro().name());
                    lenh.executeUpdate();
                    try (ResultSet khoa = lenh.getGeneratedKeys()) {
                        if (!khoa.next()) {
                            throw new SQLException(
                                    "Không nhận được ID tài khoản vừa tạo.");
                        }
                        id = khoa.getInt(1);
                    }
                }
                nhatKyDao.ghi(ketNoi, nguoiThucHien.id(), "TAO_TAI_KHOAN",
                        "Tài khoản #" + id,
                        "Tạo tài khoản " + taiKhoan.tenDangNhap()
                                + ", họ tên " + taiKhoan.hoTen()
                                + ", vai trò " + taiKhoan.vaiTro());
                ketNoi.commit();
                return true;
            } catch (SQLException | RuntimeException loi) {
                rollback(ketNoi, loi);
                if (loi instanceof SQLException sqlLoi
                        && sqlLoi.getErrorCode() == MA_TRUNG_KHOA) {
                    return false;
                }
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_TIM_THEO_TEN)) {
            lenh.setString(1, tenDangNhap);
            return docMotDong(lenh);
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public Optional<TaiKhoan> timTheoId(int id) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_TIM_THEO_ID)) {
            lenh.setInt(1, id);
            return docMotDong(lenh);
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean capNhatThongTin(int id, String hoTen,
            String soDienThoai, NguoiDung nguoiThucHien) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                String tenDangNhap;
                String hoTenCu;
                String soDienThoaiCu;
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_DONG)) {
                    lenh.setInt(1, id);
                    try (ResultSet kq = lenh.executeQuery()) {
                        if (!kq.next()) {
                            ketNoi.rollback();
                            return false;
                        }
                        tenDangNhap = kq.getString("ten_dang_nhap");
                        hoTenCu = kq.getString("ho_ten");
                        soDienThoaiCu = kq.getString("so_dien_thoai");
                    }
                }
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_CAP_NHAT_THONG_TIN)) {
                    lenh.setString(1, hoTen);
                    lenh.setString(2, soDienThoai);
                    lenh.setInt(3, id);
                    lenh.executeUpdate();
                }
                nhatKyDao.ghi(ketNoi, nguoiThucHien.id(),
                        "SUA_THONG_TIN_TAI_KHOAN", "Tài khoản #" + id,
                        "Tài khoản " + tenDangNhap + ": họ tên "
                                + hoTenCu + " -> " + hoTen
                                + "; số điện thoại " + soDienThoaiCu
                                + " -> " + soDienThoai);
                ketNoi.commit();
                return true;
            } catch (SQLException | RuntimeException loi) {
                rollback(ketNoi, loi);
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean doiMatKhau(int id, String matKhauBam,
            NguoiDung nguoiThucHien) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                String tenDangNhap;
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_DONG)) {
                    lenh.setInt(1, id);
                    try (ResultSet kq = lenh.executeQuery()) {
                        if (!kq.next()) {
                            ketNoi.rollback();
                            return false;
                        }
                        tenDangNhap = kq.getString("ten_dang_nhap");
                    }
                }
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DOI_MAT_KHAU)) {
                    lenh.setString(1, matKhauBam);
                    lenh.setInt(2, id);
                    lenh.executeUpdate();
                }
                nhatKyDao.ghi(ketNoi, nguoiThucHien.id(), "DOI_MAT_KHAU",
                        "Tài khoản #" + id,
                        "Người dùng đổi mật khẩu tài khoản " + tenDangNhap);
                ketNoi.commit();
                return true;
            } catch (SQLException | RuntimeException loi) {
                rollback(ketNoi, loi);
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public List<TaiKhoan> danhSach() {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DANH_SACH);
                ResultSet kq = lenh.executeQuery()) {
            List<TaiKhoan> ds = new ArrayList<>();
            while (kq.next()) {
                ds.add(docDong(kq));
            }
            return ds;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean doiVaiTro(int id, VaiTro vaiTro,
            NguoiDung nguoiThucHien) {
        return ghiGiuQuanTri(id, vaiTro != VaiTro.QUAN_TRI,
                SQL_DOI_VAI_TRO, lenh -> {
                    lenh.setString(1, vaiTro.name());
                    lenh.setInt(2, id);
                }, nguoiThucHien, "DOI_VAI_TRO", vaiTro.name());
    }

    @Override
    public boolean datHoatDong(int id, boolean hoatDong,
            NguoiDung nguoiThucHien) {
        String thaoTac = hoatDong ? "MO_KHOA_TAI_KHOAN"
                : "KHOA_TAI_KHOAN";
        return ghiGiuQuanTri(id, !hoatDong, SQL_DAT_HOAT_DONG, lenh -> {
            lenh.setBoolean(1, hoatDong);
            lenh.setInt(2, id);
        }, nguoiThucHien, thaoTac, hoatDong ? "Mở khóa" : "Khóa");
    }

    @Override
    public boolean datLaiMatKhau(int id, String matKhauBam,
            NguoiDung nguoiThucHien) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                String tenDangNhap;
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_DONG)) {
                    lenh.setInt(1, id);
                    try (ResultSet kq = lenh.executeQuery()) {
                        if (!kq.next()) {
                            ketNoi.rollback();
                            return false;
                        }
                        tenDangNhap = kq.getString("ten_dang_nhap");
                    }
                }
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DOI_MAT_KHAU)) {
                    lenh.setString(1, matKhauBam);
                    lenh.setInt(2, id);
                    lenh.executeUpdate();
                }
                nhatKyDao.ghi(ketNoi, nguoiThucHien.id(),
                        "DAT_LAI_MAT_KHAU", "Tài khoản #" + id,
                        "Đặt lại mật khẩu cho tài khoản " + tenDangNhap);
                ketNoi.commit();
                return true;
            } catch (SQLException | RuntimeException loi) {
                rollback(ketNoi, loi);
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    /** Đặt tham số cho câu lệnh ghi. */
    @FunctionalInterface
    private interface DatThamSo {
        void dat(PreparedStatement lenh) throws SQLException;
    }

    /**
     * Ghi một thay đổi trên tài khoản {@code id} trong một giao dịch, giữ
     * lại ít nhất một Quản trị viên đang hoạt động.
     *
     * @param boQuanTri thay đổi này có làm tài khoản thôi là Quản trị viên
     *        đang hoạt động không (hạ vai trò hoặc khóa)
     */
    private boolean ghiGiuQuanTri(int id, boolean boQuanTri, String sql,
            DatThamSo thamSo, NguoiDung nguoiThucHien, String thaoTac,
            String thayDoi) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                List<Integer> quanTri = new ArrayList<>();
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_QUAN_TRI);
                        ResultSet kq = lenh.executeQuery()) {
                    while (kq.next()) {
                        quanTri.add(kq.getInt(1));
                    }
                }
                String tenDangNhap;
                String giaTriCu;
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_DONG)) {
                    lenh.setInt(1, id);
                    try (ResultSet kq = lenh.executeQuery()) {
                        if (!kq.next()) {
                            ketNoi.rollback();
                            return false;
                        }
                        tenDangNhap = kq.getString("ten_dang_nhap");
                        giaTriCu = thaoTac.equals("DOI_VAI_TRO")
                                ? kq.getString("vai_tro")
                                : Boolean.toString(kq.getBoolean("hoat_dong"));
                    }
                }
                if (boQuanTri && quanTri.contains(id) && quanTri.size() == 1) {
                    ketNoi.rollback();
                    return false;
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(sql)) {
                    thamSo.dat(lenh);
                    lenh.executeUpdate();
                }
                nhatKyDao.ghi(ketNoi, nguoiThucHien.id(), thaoTac,
                        "Tài khoản #" + id,
                        "Tài khoản " + tenDangNhap + ": " + giaTriCu
                                + " -> " + thayDoi);
                ketNoi.commit();
                return true;
            } catch (SQLException | RuntimeException loi) {
                rollback(ketNoi, loi);
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    /** Rollback và giữ lại lỗi rollback như suppressed exception. */
    private static void rollback(Connection ketNoi, Throwable loi) {
        try {
            ketNoi.rollback();
        } catch (SQLException loiRollback) {
            loi.addSuppressed(loiRollback);
        }
    }

    private static Optional<TaiKhoan> docMotDong(PreparedStatement lenh)
            throws SQLException {
        try (ResultSet kq = lenh.executeQuery()) {
            if (!kq.next()) {
                return Optional.empty();
            }
            return Optional.of(docDong(kq));
        }
    }

    private static TaiKhoan docDong(ResultSet kq) throws SQLException {
        return new TaiKhoan(kq.getInt("id"),
                kq.getString("ten_dang_nhap"),
                kq.getString("ho_ten"),
                VaiTro.valueOf(kq.getString("vai_tro")),
                kq.getString("mat_khau_bam"),
                kq.getBoolean("hoat_dong"),
                kq.getString("so_dien_thoai"));
    }
}
