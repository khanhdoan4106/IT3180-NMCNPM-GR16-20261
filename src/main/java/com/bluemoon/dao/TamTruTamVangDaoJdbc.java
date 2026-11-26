package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauKhaiBao;
import com.bluemoon.util.Ngay;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Triển khai JDBC cho bảng tam_tru_tam_vang. */
public class TamTruTamVangDaoJdbc implements TamTruTamVangDao {

    private static final String SQL_NGUOI =
            "SELECT id, ho_ten, so_cccd FROM nhan_khau "
            + "WHERE ho_id = ? AND trang_thai = 'DANG_O' ORDER BY id";

    private static final String SQL_LAY =
            "SELECT t.id, t.nhan_khau_id, n.ho_ten, n.so_cccd, t.loai, "
            + "t.so_giay, t.tu_ngay, t.den_ngay, t.ly_do, t.dia_chi_tam_vang "
            + "FROM tam_tru_tam_vang t "
            + "JOIN nhan_khau n ON t.nhan_khau_id = n.id "
            + "WHERE n.ho_id = ? ORDER BY t.tu_ngay DESC";

    private static final String SQL_THEM =
            "INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, so_giay, "
            + "tu_ngay, den_ngay, ly_do, dia_chi_tam_vang) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_TEN_NGUOI =
            "SELECT n.ho_ten, h.ma_can_ho FROM nhan_khau n "
            + "JOIN ho_gia_dinh h ON h.id = n.ho_id WHERE n.id = ?";

    private final NguonKetNoi nguon;
    private final NhatKyDao nhatKyDao;

    /** Khởi tạo với nguồn kết nối mặc định. */
    public TamTruTamVangDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /** Khởi tạo với nguồn kết nối tùy chỉnh. */
    public TamTruTamVangDaoJdbc(NguonKetNoi nguon) {
        this(nguon, new NhatKyDaoJdbc(nguon));
    }

    /** Cho phép thay DAO nhật ký để kiểm thử rollback. */
    public TamTruTamVangDaoJdbc(NguonKetNoi nguon, NhatKyDao nhatKyDao) {
        this.nguon = nguon;
        this.nhatKyDao = nhatKyDao;
    }

    @Override
    public List<NguoiTrongHo> layNguoiTrongHo(int hoId) {
        List<NguoiTrongHo> ds = new ArrayList<>();
        try (Connection kn = nguon.moKetNoi();
                PreparedStatement ps = kn.prepareStatement(SQL_NGUOI)) {
            ps.setInt(1, hoId);
            try (ResultSet kq = ps.executeQuery()) {
                while (kq.next()) {
                    ds.add(new NguoiTrongHo(
                            kq.getInt("id"),
                            kq.getString("ho_ten"),
                            kq.getString("so_cccd")));
                }
            }
            return ds;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public List<TamTruTamVang> layDanhSachTheoHo(int hoId) {
        List<TamTruTamVang> ds = new ArrayList<>();
        try (Connection kn = nguon.moKetNoi();
                PreparedStatement ps = kn.prepareStatement(SQL_LAY)) {
            ps.setInt(1, hoId);
            try (ResultSet kq = ps.executeQuery()) {
                while (kq.next()) {
                    ds.add(new TamTruTamVang(
                            kq.getInt("id"),
                            kq.getInt("nhan_khau_id"),
                            kq.getString("ho_ten"),
                            kq.getString("so_cccd"),
                            kq.getString("loai"),
                            kq.getString("so_giay"),
                            kq.getDate("tu_ngay").toLocalDate(),
                            kq.getDate("den_ngay").toLocalDate(),
                            kq.getString("ly_do"),
                            kq.getString("dia_chi_tam_vang")));
                }
            }
            return ds;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public int themKhaiBao(YeuCauKhaiBao yc, NguoiDung nguoiDung) {
        int co = Statement.RETURN_GENERATED_KEYS;
        try (Connection kn = nguon.moKetNoi()) {
            kn.setAutoCommit(false);
            try {
                int khaiBaoId;
                try (PreparedStatement ps = kn.prepareStatement(SQL_THEM, co)) {
                    ps.setInt(1, yc.nhanKhauId());
                    ps.setString(2, yc.loai());
                    ps.setString(3, yc.soGiay());
                    ps.setDate(4, Date.valueOf(yc.tuNgay()));
                    ps.setDate(5, Date.valueOf(yc.denNgay()));
                    ps.setString(6, yc.lyDo());
                    ps.setString(7, yc.diaChiTamVang());
                    ps.executeUpdate();
                    try (ResultSet khoa = ps.getGeneratedKeys()) {
                        if (!khoa.next()) {
                            throw new SQLException(
                                    "Không nhận được ID khai báo vừa thêm.");
                        }
                        khaiBaoId = khoa.getInt(1);
                    }
                }

                String[] nguoi = docTenNguoi(kn, yc.nhanKhauId());
                nhatKyDao.ghi(kn, nguoiDung.id(), ThaoTac.GHI_TAM_TRU_TAM_VANG,
                        "Nhân khẩu " + nguoi[0] + " (hộ " + nguoi[1] + ")",
                        noiDungNhatKy(yc, nguoi[0]));
                kn.commit();
                return khaiBaoId;
            } catch (SQLException | RuntimeException loi) {
                try {
                    kn.rollback();
                } catch (SQLException loiRollback) {
                    loi.addSuppressed(loiRollback);
                }
                throw loi;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    /** Vd "Tạm trú: Nguyễn Văn A, từ 01/11/2026 đến 30/04/2027". */
    private static String noiDungNhatKy(YeuCauKhaiBao yc, String hoTen) {
        boolean tamTru = "TAM_TRU".equals(yc.loai());
        String noiDung = (tamTru ? "Tạm trú: " : "Tạm vắng: ") + hoTen
                + ", từ " + Ngay.dinhDang(yc.tuNgay())
                + " đến " + Ngay.dinhDang(yc.denNgay());
        if (!tamTru && yc.diaChiTamVang() != null) {
            noiDung += ", nơi đến " + yc.diaChiTamVang();
        }
        return noiDung;
    }

    /** Họ tên và mã căn hộ của nhân khẩu, để nhật ký không ghi ID. */
    private static String[] docTenNguoi(Connection kn, int nhanKhauId)
            throws SQLException {
        try (PreparedStatement ps = kn.prepareStatement(SQL_TEN_NGUOI)) {
            ps.setInt(1, nhanKhauId);
            try (ResultSet kq = ps.executeQuery()) {
                if (!kq.next()) {
                    throw new SQLException(
                            "Không tìm thấy nhân khẩu #" + nhanKhauId);
                }
                return new String[] {kq.getString(1), kq.getString(2)};
            }
        }
    }
}
