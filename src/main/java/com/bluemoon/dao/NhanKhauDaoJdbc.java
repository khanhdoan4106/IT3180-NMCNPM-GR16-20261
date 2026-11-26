package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHe;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauThemNhanKhau;
import com.bluemoon.util.Ngay;
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
            + "quan_he, loai_cu_tru, noi_thuong_tru FROM nhan_khau "
            + "WHERE ho_id = ? AND trang_thai = 'DANG_O' "
            + "ORDER BY ngay_sinh ASC";

    private static final String SQL_CO_CCCD =
            "SELECT 1 FROM nhan_khau "
            + "WHERE so_cccd = ? AND trang_thai = 'DANG_O' LIMIT 1";

    private static final String SQL_THEM =
            "INSERT INTO nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, "
            + "quan_he, so_cccd, dan_toc, ton_giao, nghe_nghiep, "
            + "loai_cu_tru, noi_thuong_tru) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_THEM_DOT_TAM_TRU =
            "INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, "
            + "den_ngay) VALUES (?, 'TAM_TRU', ?, ?)";

    private static final String SQL_MA_CAN_HO =
            "SELECT ma_can_ho FROM ho_gia_dinh WHERE id = ?";

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
                            kq.getString("quan_he"),
                            LoaiCuTru.valueOf(kq.getString("loai_cu_tru")),
                            kq.getString("noi_thuong_tru")));
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

    /** Người tạm trú và đợt đầu tiên lưu trong một giao dịch (giả định 9). */
    @Override
    public int themNhanKhau(YeuCauThemNhanKhau yc, NguoiDung nguoiDung) {
        try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
            ketNoi.setAutoCommit(false);
            try {
                int id = chenNhanKhau(ketNoi, yc);
                if (yc.loaiCuTru() == LoaiCuTru.TAM_TRU) {
                    chenDotTamTru(ketNoi, id, yc);
                }
                String maCanHo = docMaCanHo(ketNoi, yc.hoId());
                nhatKyDao.ghi(ketNoi, nguoiDung.id(), ThaoTac.THEM_NHAN_KHAU,
                        "Nhân khẩu " + yc.hoTen() + " (hộ " + maCanHo + ")",
                        noiDungNhatKy(yc, maCanHo));
                ketNoi.commit();
                return id;
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

    private static int chenNhanKhau(Connection ketNoi, YeuCauThemNhanKhau yc)
            throws SQLException {
        int co = Statement.RETURN_GENERATED_KEYS;
        try (PreparedStatement ps = ketNoi.prepareStatement(SQL_THEM, co)) {
            ps.setInt(1, yc.hoId());
            ps.setString(2, yc.hoTen());
            ps.setDate(3, Date.valueOf(yc.ngaySinh()));
            ps.setString(4, yc.gioiTinh());
            ps.setString(5, yc.quanHe());
            ps.setString(6, yc.soCccd());
            ps.setString(7, yc.danToc());
            ps.setString(8, yc.tonGiao());
            ps.setString(9, yc.ngheNghiep());
            ps.setString(10, yc.loaiCuTru().name());
            ps.setString(11, yc.noiThuongTru());
            ps.executeUpdate();
            try (ResultSet khoa = ps.getGeneratedKeys()) {
                if (!khoa.next()) {
                    throw new SQLException(
                            "Không nhận được ID nhân khẩu vừa thêm.");
                }
                return khoa.getInt(1);
            }
        }
    }

    private static void chenDotTamTru(Connection ketNoi, int nhanKhauId,
            YeuCauThemNhanKhau yc) throws SQLException {
        try (PreparedStatement ps =
                ketNoi.prepareStatement(SQL_THEM_DOT_TAM_TRU)) {
            ps.setInt(1, nhanKhauId);
            ps.setDate(2, Date.valueOf(yc.tamTruTu()));
            ps.setDate(3, Date.valueOf(yc.tamTruDen()));
            ps.executeUpdate();
        }
    }

    /** Vd "Thêm Nguyễn Văn B (Con) vào hộ A-101, tạm trú từ 01/11/2026…". */
    private static String noiDungNhatKy(YeuCauThemNhanKhau yc,
            String maCanHo) {
        String noiDung = "Thêm " + yc.hoTen() + " ("
                + QuanHe.tuMa(yc.quanHe()).getNhan() + ") vào hộ " + maCanHo;
        if (yc.loaiCuTru() == LoaiCuTru.TAM_TRU) {
            noiDung += ", tạm trú từ " + Ngay.dinhDang(yc.tamTruTu())
                    + " đến " + Ngay.dinhDang(yc.tamTruDen());
        }
        return noiDung;
    }

    private static String docMaCanHo(Connection ketNoi, int hoId)
            throws SQLException {
        try (PreparedStatement ps = ketNoi.prepareStatement(SQL_MA_CAN_HO)) {
            ps.setInt(1, hoId);
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next() ? kq.getString(1) : "#" + hoId;
            }
        }
    }
}
