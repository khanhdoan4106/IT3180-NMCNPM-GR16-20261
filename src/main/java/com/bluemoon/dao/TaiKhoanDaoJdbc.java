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
import java.util.Optional;

/** Bản JDBC của {@link TaiKhoanDao}. */
public class TaiKhoanDaoJdbc implements TaiKhoanDao {

    /** Mã lỗi MySQL: trùng khóa duy nhất ({@code ten_dang_nhap}). */
    static final int MA_TRUNG_KHOA = 1062;

    private static final String SQL_THEM = "INSERT INTO tai_khoan "
            + "(ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_TIM_THEO_TEN = "SELECT id, "
            + "ten_dang_nhap, ho_ten, vai_tro, mat_khau_bam, hoat_dong "
            + "FROM tai_khoan WHERE ten_dang_nhap = ?";

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
            try (ResultSet kq = lenh.executeQuery()) {
                if (!kq.next()) {
                    return Optional.empty();
                }
                return Optional.of(new TaiKhoan(kq.getInt("id"),
                        kq.getString("ten_dang_nhap"),
                        kq.getString("ho_ten"),
                        VaiTro.valueOf(kq.getString("vai_tro")),
                        kq.getString("mat_khau_bam"),
                        kq.getBoolean("hoat_dong")));
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
