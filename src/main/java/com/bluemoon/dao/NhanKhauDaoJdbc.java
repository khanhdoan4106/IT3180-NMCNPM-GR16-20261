package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Bản JDBC của {@link NhanKhauDao}. */
public class NhanKhauDaoJdbc implements NhanKhauDao {

    private static final String SQL_LAY_THEO_HO =
            "SELECT id, ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, "
            + "quan_he FROM nhan_khau "
            + "WHERE ho_id = ? AND trang_thai = 'DANG_O' "
            + "ORDER BY ngay_sinh ASC";

    private static final String SQL_CO_CCCD =
            "SELECT 1 FROM nhan_khau "
            + "WHERE so_cccd = ? AND trang_thai = 'DANG_O' LIMIT 1";

    private static final String SQL_THEM =
            "INSERT INTO nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, "
            + "quan_he, so_cccd, dan_toc, ton_giao, nghe_nghiep) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final NguonKetNoi nguonKetNoi;
    private final NhatKyDao nhatKyDao;

    /** Khởi tạo DAO với nguồn CSDL mặc định. */
    public NhanKhauDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /** Khởi tạo DAO với nguồn kết nối tùy chỉnh (ví dụ: DB test). */
    public NhanKhauDaoJdbc(NguonKetNoi nguonKetNoi) {
        this(nguonKetNoi, new NhatKyDaoJdbc(nguonKetNoi));
    }

    /** Cho phép thay DAO nhật ký để kiểm thử tình huống ghi nhật ký lỗi. */
    public NhanKhauDaoJdbc(NguonKetNoi nguonKetNoi, NhatKyDao nhatKyDao) {
        this.nguonKetNoi = nguonKetNoi;
        this.nhatKyDao = nhatKyDao;
    }

    @Override
    public List<NhanKhau> layDanhSachTheoHo(int hoId) {
        List<NhanKhau> danhSach = new ArrayList<>();
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement ps = ketNoi.prepareStatement(
                        SQL_LAY_THEO_HO)) {
            ps.setInt(1, hoId);
            try (ResultSet kq = ps.executeQuery()) {
                while (kq.next()) {
                    danhSach.add(new NhanKhau(
                            kq.getInt("id"),
                            kq.getInt("ho_id"),
                            kq.getString("ho_ten"),
                            kq.getDate("ngay_sinh").toLocalDate(),
                            kq.getString("gioi_tinh"),
                            kq.getString("so_cccd"),
                            kq.getString("quan_he")));
                }
            }
            return danhSach;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean tonTaiCccdDangO(String soCccd) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement ps = ketNoi.prepareStatement(
                        SQL_CO_CCCD)) {
            ps.setString(1, soCccd);
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next();
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public int themNhanKhau(YeuCauThemNhanKhau yc, NguoiDung nguoiDung) {
        int co = Statement.RETURN_GENERATED_KEYS;
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                int nhanKhauId;
                try (PreparedStatement ps =
                        ketNoi.prepareStatement(SQL_THEM, co)) {
                    ps.setInt(1, yc.hoId());
                    ps.setString(2, yc.hoTen());
                    ps.setDate(3, Date.valueOf(yc.ngaySinh()));
                    ps.setString(4, yc.gioiTinh());
                    ps.setString(5, yc.quanHe());
                    ps.setString(6, yc.soCccd());
                    ps.setString(7, yc.danToc());
                    ps.setString(8, yc.tonGiao());
                    ps.setString(9, yc.ngheNghiep());
                    ps.executeUpdate();
                    try (ResultSet khoa = ps.getGeneratedKeys()) {
                        if (!khoa.next()) {
                            throw new SQLException(
                                    "Không nhận được ID nhân khẩu vừa thêm.");
                        }
                        nhanKhauId = khoa.getInt(1);
                    }
                }

                nhatKyDao.ghi(ketNoi, nguoiDung.id(), "THEM_NHAN_KHAU",
                        "Nhân khẩu #" + nhanKhauId,
                        "Thêm " + yc.hoTen() + " vào hộ #" + yc.hoId());
                ketNoi.commit();
                return nhanKhauId;
            } catch (SQLException | RuntimeException loi) {
                try {
                    ketNoi.rollback();
                } catch (SQLException loiRollback) {
                    loi.addSuppressed(loiRollback);
                }
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
