package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauKhaiBao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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

    private static final String SQL_LOAI_CU_TRU =
            "SELECT loai_cu_tru FROM nhan_khau "
            + "WHERE id = ? AND trang_thai = 'DANG_O'";

    private static final String SQL_KHOA_NHAN_KHAU =
            SQL_LOAI_CU_TRU + " FOR UPDATE";

    private static final String SQL_CHONG_NGAY =
            "SELECT 1 FROM tam_tru_tam_vang "
            + "WHERE nhan_khau_id = ? AND tu_ngay <= ? AND den_ngay >= ? "
            + "LIMIT 1";

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
    public LoaiCuTru layLoaiCuTru(int nhanKhauId) {
        try (Connection kn = nguon.moKetNoi()) {
            return docLoaiCuTru(kn, SQL_LOAI_CU_TRU, nhanKhauId);
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    @Override
    public boolean coDotChongNgay(int nhanKhauId, LocalDate tuNgay,
            LocalDate denNgay) {
        try (Connection kn = nguon.moKetNoi()) {
            return coChongNgay(kn, nhanKhauId, tuNgay, denNgay);
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
                docLoaiCuTru(kn, SQL_KHOA_NHAN_KHAU, yc.nhanKhauId());
                if (coChongNgay(kn, yc.nhanKhauId(), yc.tuNgay(),
                        yc.denNgay())) {
                    throw new IllegalArgumentException(LOI_CHONG_NGAY);
                }
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

                nhatKyDao.ghi(kn, nguoiDung.id(), ThaoTac.GHI_TAM_TRU_TAM_VANG,
                        "Khai báo #" + khaiBaoId,
                        yc.loai() + " nhân khẩu #" + yc.nhanKhauId()
                                + " từ " + yc.tuNgay()
                                + " đến " + yc.denNgay());
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

    private static LoaiCuTru docLoaiCuTru(Connection kn, String sql,
            int nhanKhauId) throws SQLException {
        try (PreparedStatement ps = kn.prepareStatement(sql)) {
            ps.setInt(1, nhanKhauId);
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next()
                        ? LoaiCuTru.valueOf(kq.getString(1)) : null;
            }
        }
    }

    private static boolean coChongNgay(Connection kn, int nhanKhauId,
            LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        try (PreparedStatement ps = kn.prepareStatement(SQL_CHONG_NGAY)) {
            ps.setInt(1, nhanKhauId);
            ps.setDate(2, Date.valueOf(denNgay));
            ps.setDate(3, Date.valueOf(tuNgay));
            try (ResultSet kq = ps.executeQuery()) {
                return kq.next();
            }
        }
    }
}
