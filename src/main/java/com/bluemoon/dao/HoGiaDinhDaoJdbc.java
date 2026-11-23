package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.YeuCauThemHo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Bản JDBC của {@link HoGiaDinhDao}.
 */
public class HoGiaDinhDaoJdbc implements HoGiaDinhDao {

    private static final String SQL_DS_DANG_O =
            "SELECT h.id, h.ma_can_ho, h.loai_can_ho, "
            + "h.tang, h.dien_tich, "
            + "COALESCE(ch.ho_ten, '') AS ten_chu_ho, "
            + "h.so_dien_thoai, "
            + "(SELECT COUNT(*) FROM nhan_khau nk "
            + " WHERE nk.ho_id = h.id "
            + " AND nk.trang_thai = 'DANG_O') AS so_thanh_vien, "
            + "h.ngay_chuyen_den "
            + "FROM ho_gia_dinh h "
            + "LEFT JOIN nhan_khau ch ON ch.ho_id = h.id "
            + " AND ch.quan_he = 'CHU_HO' "
            + " AND ch.trang_thai = 'DANG_O' "
            + "WHERE h.trang_thai = 'DANG_O' "
            + "ORDER BY h.ma_can_ho";

    private static final String SQL_CO_MA_HO =
            "SELECT 1 FROM ho_gia_dinh "
            + "WHERE ma_can_ho = ? "
            + "AND trang_thai = 'DANG_O' LIMIT 1";

    private static final String SQL_CO_CCCD =
            "SELECT 1 FROM nhan_khau "
            + "WHERE so_cccd = ? "
            + "AND trang_thai = 'DANG_O' LIMIT 1";

    private static final String SQL_THEM_HO =
            "INSERT INTO ho_gia_dinh "
            + "(ma_can_ho, loai_can_ho, tang, dien_tich, "
            + "so_dien_thoai, ngay_chuyen_den) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_THEM_CHU_HO =
            "INSERT INTO nhan_khau "
            + "(ho_id, ho_ten, ngay_sinh, gioi_tinh, "
            + "so_cccd, quan_he) "
            + "VALUES (?, ?, ?, ?, ?, 'CHU_HO')";

    private final NguonKetNoi nguonKetNoi;
    private final NhatKyDao nhatKyDao;

    /** Dùng CSDL theo {@code config/db.properties}. */
    public HoGiaDinhDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Dùng nguồn kết nối tùy chỉnh (ví dụ CSDL kiểm thử).
     *
     * @param nguonKetNoi nơi lấy kết nối
     */
    public HoGiaDinhDaoJdbc(NguonKetNoi nguonKetNoi) {
        this(nguonKetNoi, new NhatKyDaoJdbc(nguonKetNoi));
    }

    /**
     * Cho phép thay DAO nhật ký khi kiểm thử lỗi ghi nhật ký.
     *
     * @param nguonKetNoi nơi lấy kết nối
     * @param nhatKyDao DAO ghi nhật ký
     */
    public HoGiaDinhDaoJdbc(NguonKetNoi nguonKetNoi,
            NhatKyDao nhatKyDao) {
        this.nguonKetNoi = nguonKetNoi;
        this.nhatKyDao = nhatKyDao;
    }

    @Override
    public List<HoGiaDinh> layDanhSachDangO() {
        List<HoGiaDinh> danhSach = new ArrayList<>();
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement ps = ketNoi.prepareStatement(SQL_DS_DANG_O);
                ResultSet kq = ps.executeQuery()) {
            while (kq.next()) {
                danhSach.add(new HoGiaDinh(
                        kq.getInt("id"),
                        kq.getString("ma_can_ho"),
                        LoaiCanHo.valueOf(kq.getString("loai_can_ho")),
                        kq.getInt("tang"),
                        kq.getBigDecimal("dien_tich"),
                        kq.getString("ten_chu_ho"),
                        kq.getString("so_dien_thoai"),
                        kq.getInt("so_thanh_vien"),
                        kq.getDate("ngay_chuyen_den").toLocalDate()));
            }
            return danhSach;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean tonTaiMaCanHoDangO(String maCanHo) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement ps = ketNoi.prepareStatement(SQL_CO_MA_HO)) {
            ps.setString(1, maCanHo);
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean tonTaiCccdDangO(String soCccd) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement ps = ketNoi.prepareStatement(SQL_CO_CCCD)) {
            ps.setString(1, soCccd);
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public int themHoGiaDinh(YeuCauThemHo yeuCau, NguoiDung nguoiDung) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                int hoId = chenHo(ketNoi, yeuCau);
                chenChuHo(ketNoi, hoId, yeuCau);
                nhatKyDao.ghi(ketNoi, nguoiDung.id(), "THEM_HO",
                        "Hộ " + yeuCau.maCanHo(),
                        "Thêm hộ gia đình và chủ hộ "
                                + yeuCau.tenChuHo());
                ketNoi.commit();
                return hoId;
            } catch (SQLException | RuntimeException e) {
                try {
                    ketNoi.rollback();
                } catch (SQLException loiRollback) {
                    e.addSuppressed(loiRollback);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    private int chenHo(Connection ketNoi, YeuCauThemHo yc)
            throws SQLException {
        int co = Statement.RETURN_GENERATED_KEYS;
        try (PreparedStatement ps = ketNoi.prepareStatement(SQL_THEM_HO, co)) {
            ps.setString(1, yc.maCanHo());
            ps.setString(2, yc.loaiCanHo().name());
            ps.setInt(3, yc.tang());
            ps.setBigDecimal(4, yc.dienTich());
            ps.setString(5, yc.soDienThoai());
            ps.setDate(6, Date.valueOf(yc.ngayChuyenDen()));
            ps.executeUpdate();
            try (ResultSet khoa = ps.getGeneratedKeys()) {
                khoa.next();
                return khoa.getInt(1);
            }
        }
    }

    private void chenChuHo(Connection ketNoi, int hoId, YeuCauThemHo yc)
            throws SQLException {
        try (PreparedStatement ps = ketNoi.prepareStatement(SQL_THEM_CHU_HO)) {
            ps.setInt(1, hoId);
            ps.setString(2, yc.tenChuHo());
            ps.setDate(3, Date.valueOf(yc.ngaySinhChuHo()));
            ps.setString(4, yc.gioiTinhChuHo());
            ps.setString(5, yc.cccdChuHo());
            ps.executeUpdate();
        }
    }
}
