package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.TrangThaiKhoanPhi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Bản cài đặt JDBC của {@link KhoanPhiDao}.
 */
public class KhoanPhiDaoJdbc implements KhoanPhiDao {

    private static final String SQL_THEM = "INSERT INTO khoan_phi "
            + "(ten, loai, cach_tinh, don_gia, tu_ngay, den_ngay, "
            + "trang_thai, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_CHON = "SELECT id, ten, loai, "
            + "cach_tinh, don_gia, tu_ngay, den_ngay, trang_thai, "
            + "ghi_chu FROM khoan_phi ";

    private static final String SQL_DANH_SACH = SQL_CHON
            + "ORDER BY id DESC";

    private static final String SQL_TIM_THEO_ID = SQL_CHON
            + "WHERE id = ?";

    private static final String SQL_TIM_THEO_TEN = SQL_CHON
            + "WHERE LOWER(TRIM(ten)) = LOWER(TRIM(?)) LIMIT 1";

    private static final String SQL_CAP_NHAT_DON_GIA = "UPDATE khoan_phi "
            + "SET don_gia = ? WHERE id = ?";

    private static final String SQL_KIEM_TRA_TRUNG = "SELECT 1 FROM "
            + "khoan_phi WHERE LOWER(TRIM(ten)) = LOWER(TRIM(?)) "
            + "AND tu_ngay = ? LIMIT 1";

    private final NguonKetNoi nguonKetNoi;
    private final NhatKyDao nhatKyDao;

    /**
     * Khởi tạo DAO dùng cấu hình mặc định trong {@code config/db.properties}.
     */
    public KhoanPhiDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Khởi tạo DAO với nguồn kết nối chỉ định.
     *
     * @param nguonKetNoi nguồn cấp kết nối CSDL
     */
    public KhoanPhiDaoJdbc(NguonKetNoi nguonKetNoi) {
        this(nguonKetNoi, new NhatKyDaoJdbc(nguonKetNoi));
    }

    /**
     * Khởi tạo DAO với nguồn kết nối và DAO nhật ký chỉ định.
     *
     * @param nguonKetNoi nguồn cấp kết nối CSDL
     * @param nhatKyDao DAO ghi nhật ký thao tác
     */
    public KhoanPhiDaoJdbc(NguonKetNoi nguonKetNoi, NhatKyDao nhatKyDao) {
        this.nguonKetNoi = nguonKetNoi;
        this.nhatKyDao = nhatKyDao;
    }

    @Override
    public int them(KhoanPhi kp) {
        try (Connection conn = nguonKetNoi.moKetNoi();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_THEM, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, kp.ten());
            ps.setString(2, kp.loai().name());
            ps.setString(3, kp.cachTinh().name());
            if (kp.donGia() == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, kp.donGia());
            }
            ps.setDate(5, Date.valueOf(kp.tuNgay()));
            if (kp.denNgay() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(kp.denNgay()));
            }
            ps.setString(7, kp.trangThai().name());
            if (kp.ghiChu() == null || kp.ghiChu().isBlank()) {
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setString(8, kp.ghiChu().trim());
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new LoiCsdl(LoaiLoiCsdl.KHAC, null);
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean tonTaiTenTrongDot(String ten, LocalDate tuNgay) {
        try (Connection conn = nguonKetNoi.moKetNoi();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_KIEM_TRA_TRUNG)) {
            ps.setString(1, ten);
            ps.setDate(2, Date.valueOf(tuNgay));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public List<KhoanPhi> layDanhSach() {
        try (Connection conn = nguonKetNoi.moKetNoi();
                PreparedStatement ps = conn.prepareStatement(SQL_DANH_SACH);
                ResultSet rs = ps.executeQuery()) {
            List<KhoanPhi> ketQua = new ArrayList<>();
            while (rs.next()) {
                ketQua.add(docKhoanPhi(rs));
            }
            return ketQua;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public Optional<KhoanPhi> timTheoId(int id) {
        try (Connection conn = nguonKetNoi.moKetNoi();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(docKhoanPhi(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public Optional<KhoanPhi> timTheoTen(String ten) {
        try (Connection conn = nguonKetNoi.moKetNoi();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_TIM_THEO_TEN)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(docKhoanPhi(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public void capNhatDonGia(int id, BigDecimal donGiaMoi,
            NguoiDung nguoiDung, String noiDungNhatKy) {
        try (Connection conn = nguonKetNoi.moKetNoi()) {
            conn.setAutoCommit(false);
            try {
                String tenKhoan = "";
                try (PreparedStatement psDoc = conn.prepareStatement(
                        "SELECT ten FROM khoan_phi WHERE id = ? FOR UPDATE")) {
                    psDoc.setInt(1, id);
                    try (ResultSet rs = psDoc.executeQuery()) {
                        if (rs.next()) {
                            tenKhoan = rs.getString(1);
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        SQL_CAP_NHAT_DON_GIA)) {
                    ps.setBigDecimal(1, donGiaMoi);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }

                if (nguoiDung != null && nhatKyDao != null) {
                    nhatKyDao.ghi(conn, nguoiDung.id(), ThaoTac.SUA_DON_GIA,
                            "Khoản phí: " + tenKhoan, noiDungNhatKy);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException loiRb) {
                    e.addSuppressed(loiRb);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    private KhoanPhi docKhoanPhi(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String ten = rs.getString("ten");
        LoaiKhoanPhi loai = LoaiKhoanPhi.valueOf(rs.getString("loai"));
        CachTinhPhi cachTinh = CachTinhPhi.valueOf(rs.getString("cach_tinh"));
        BigDecimal donGia = rs.getBigDecimal("don_gia");
        LocalDate tuNgay = rs.getDate("tu_ngay").toLocalDate();
        Date denNgaySql = rs.getDate("den_ngay");
        LocalDate denNgay = denNgaySql == null
                ? null : denNgaySql.toLocalDate();
        TrangThaiKhoanPhi trangThai = TrangThaiKhoanPhi.valueOf(
                rs.getString("trang_thai"));
        String ghiChu = rs.getString("ghi_chu");
        return new KhoanPhi(id, ten, loai, cachTinh, donGia, tuNgay, denNgay,
                trangThai, ghiChu);
    }
}
