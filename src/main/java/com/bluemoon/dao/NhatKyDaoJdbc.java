package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NhatKy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Bản JDBC của {@link NhatKyDao}. */
public class NhatKyDaoJdbc implements NhatKyDao {

    private static final String SQL_GHI = "INSERT INTO nhat_ky "
            + "(tai_khoan_id, thao_tac, doi_tuong, noi_dung) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_DANH_SACH = "SELECT n.id, n.thoi_diem, "
            + "t.ten_dang_nhap, n.thao_tac, n.doi_tuong, n.noi_dung "
            + "FROM nhat_ky n JOIN tai_khoan t ON t.id = n.tai_khoan_id "
            + "ORDER BY n.thoi_diem DESC, n.id DESC LIMIT ?";

    private final NguonKetNoi nguonKetNoi;

    /** Dùng CSDL theo config/db.properties. */
    public NhatKyDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Dùng nguồn kết nối khác, vd CSDL kiểm thử.
     *
     * @param nguonKetNoi nơi lấy kết nối
     */
    public NhatKyDaoJdbc(NguonKetNoi nguonKetNoi) {
        this.nguonKetNoi = nguonKetNoi;
    }

    @Override
    public void ghi(Connection ketNoi, int taiKhoanId, String thaoTac,
            String doiTuong, String noiDung) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_GHI)) {
            lenh.setInt(1, taiKhoanId);
            lenh.setString(2, thaoTac);
            lenh.setString(3, doiTuong);
            lenh.setString(4, noiDung);
            lenh.executeUpdate();
        }
    }

    @Override
    public List<NhatKy> danhSachMoiNhat(int gioiHan) {
        if (gioiHan < 1 || gioiHan > 1000) {
            throw new IllegalArgumentException(
                    "Số dòng nhật ký phải từ 1 đến 1000.");
        }
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DANH_SACH)) {
            lenh.setInt(1, gioiHan);
            try (ResultSet kq = lenh.executeQuery()) {
                List<NhatKy> danhSach = new ArrayList<>();
                while (kq.next()) {
                    Timestamp thoiDiem = kq.getTimestamp("thoi_diem");
                    danhSach.add(new NhatKy(
                            kq.getInt("id"),
                            thoiDiem.toLocalDateTime(),
                            kq.getString("ten_dang_nhap"),
                            kq.getString("thao_tac"),
                            kq.getString("doi_tuong"),
                            kq.getString("noi_dung")));
                }
                return danhSach;
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
