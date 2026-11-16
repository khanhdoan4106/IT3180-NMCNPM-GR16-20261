package com.bluemoon.dao;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.SoLieuCuDan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Bản JDBC của {@link CuDanDao}. Mẫu cho mọi DAO: mỗi lần gọi mở một kết
 * nối, dùng {@link PreparedStatement}, đóng tài nguyên bằng
 * try-with-resources và đổi {@link SQLException} thành {@link LoiCsdl}.
 */
public class CuDanDaoJdbc implements CuDanDao {

    private static final String DANG_O = "DANG_O";

    private static final String SQL_DEM_DANG_O = "SELECT "
            + "(SELECT COUNT(*) FROM ho_gia_dinh WHERE trang_thai = ?), "
            + "(SELECT COUNT(*) FROM nhan_khau WHERE trang_thai = ?)";

    private final NguonKetNoi nguonKetNoi;

    /** Dùng CSDL theo {@code config/db.properties}. */
    public CuDanDaoJdbc() {
        this(KetNoiCsdl::moKetNoi);
    }

    /**
     * Dùng một nguồn kết nối khác, vd CSDL kiểm thử.
     *
     * @param nguonKetNoi nơi lấy kết nối
     */
    public CuDanDaoJdbc(NguonKetNoi nguonKetNoi) {
        this.nguonKetNoi = nguonKetNoi;
    }

    @Override
    public SoLieuCuDan demDangO() {
        try (Connection ketNoi = nguonKetNoi.moKetNoi();
                PreparedStatement lenh =
                        ketNoi.prepareStatement(SQL_DEM_DANG_O)) {
            lenh.setString(1, DANG_O);
            lenh.setString(2, DANG_O);
            try (ResultSet ketQua = lenh.executeQuery()) {
                ketQua.next();
                return new SoLieuCuDan(ketQua.getInt(1), ketQua.getInt(2));
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
