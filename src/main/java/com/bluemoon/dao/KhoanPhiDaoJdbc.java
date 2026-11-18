package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
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

    private static final String SQL_KIEM_TRA_TRUNG = "SELECT 1 FROM "
            + "khoan_phi WHERE LOWER(TRIM(ten)) = LOWER(TRIM(?)) "
            + "AND tu_ngay = ? LIMIT 1";

    private final NguonKetNoi nguonKetNoi;

    /**
     * Khởi tạo DAO dùng cấu hình mặc định trong {@code config/db.properties}.
     */
    public KhoanPhiDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Khởi tạo DAO với nguồn kết nối chỉ định (dùng trong kiểm thử tích hợp).
     *
     * @param nguonKetNoi nguồn cấp kết nối CSDL
     */
    public KhoanPhiDaoJdbc(NguonKetNoi nguonKetNoi) {
        this.nguonKetNoi = nguonKetNoi;
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
