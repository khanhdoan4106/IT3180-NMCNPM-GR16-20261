package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String SQL_KHOA_DONG = "SELECT vai_tro, hoat_dong "
            + "FROM tai_khoan WHERE id = ? FOR UPDATE";

    private static final String SQL_DOI_VAI_TRO = "UPDATE tai_khoan "
            + "SET vai_tro = ? WHERE id = ?";

    private static final String SQL_DAT_HOAT_DONG = "UPDATE tai_khoan "
            + "SET hoat_dong = ? WHERE id = ?";

    private final NguonKetNoi nguonKetNoi;

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
        this.nguonKetNoi = nguonKetNoi;
    }

    @Override
    public boolean them(TaiKhoanMoi taiKhoan) {
        // Không đọc trước rồi mới ghi: khóa UNIQUE chặn trùng ngay cả khi
        // hai máy cùng tạo một tên một lúc.
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(SQL_THEM)) {
            lenh.setString(1, taiKhoan.tenDangNhap());
            lenh.setString(2, taiKhoan.matKhauBam());
            lenh.setString(3, taiKhoan.hoTen());
            lenh.setString(4, taiKhoan.vaiTro().name());
            lenh.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == MA_TRUNG_KHOA) {
                return false;
            }
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
            String soDienThoai) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_CAP_NHAT_THONG_TIN)) {
            lenh.setString(1, hoTen);
            lenh.setString(2, soDienThoai);
            lenh.setInt(3, id);
            return lenh.executeUpdate() == 1;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean doiMatKhau(int id, String matKhauBam) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DOI_MAT_KHAU)) {
            lenh.setString(1, matKhauBam);
            lenh.setInt(2, id);
            return lenh.executeUpdate() == 1;
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
    public boolean doiVaiTro(int id, VaiTro vaiTro) {
        return ghiGiuQuanTri(id, vaiTro != VaiTro.QUAN_TRI, SQL_DOI_VAI_TRO,
                lenh -> {
                    lenh.setString(1, vaiTro.name());
                    lenh.setInt(2, id);
                });
    }

    @Override
    public boolean datHoatDong(int id, boolean hoatDong) {
        return ghiGiuQuanTri(id, !hoatDong, SQL_DAT_HOAT_DONG, lenh -> {
            lenh.setBoolean(1, hoatDong);
            lenh.setInt(2, id);
        });
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
            DatThamSo thamSo) {
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
                try (PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_KHOA_DONG)) {
                    lenh.setInt(1, id);
                    try (ResultSet kq = lenh.executeQuery()) {
                        if (!kq.next()) {
                            ketNoi.rollback();
                            return false;
                        }
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
                ketNoi.commit();
                return true;
            } catch (SQLException e) {
                ketNoi.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
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
