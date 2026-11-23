package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.NhatKyDao;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Kiểm thử quyền xem nhật ký, không cần MySQL. */
class NhatKyServiceTest {

    private static final NguoiDung QUAN_TRI = new NguoiDung(1, "admin",
            "Quản trị viên", VaiTro.QUAN_TRI);
    private static final NguoiDung THU_QUY = new NguoiDung(2, "thuquy",
            "Thủ quỹ", VaiTro.THU_QUY);

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    /** DAO giả lưu danh sách và nhớ giới hạn được yêu cầu. */
    private static final class NhatKyDaoGia implements NhatKyDao {
        private final List<NhatKy> danhSach;
        private int gioiHan;

        NhatKyDaoGia(List<NhatKy> danhSach) {
            this.danhSach = danhSach;
        }

        @Override
        public void ghi(Connection ketNoi, int taiKhoanId, String thaoTac,
                String doiTuong, String noiDung) throws SQLException {
            throw new UnsupportedOperationException("Không dùng trong test.");
        }

        @Override
        public List<NhatKy> danhSachMoiNhat(int gioiHan) {
            this.gioiHan = gioiHan;
            return danhSach;
        }
    }

    @Test
    void quanTriVienXemDuocNhatKyMoiNhat() {
        PhienDangNhap.batDau(QUAN_TRI);
        List<NhatKy> duLieu = List.of();
        NhatKyDaoGia dao = new NhatKyDaoGia(duLieu);

        assertEquals(duLieu, new NhatKyService(dao).xemMoiNhat());
        assertEquals(NhatKyService.GIOI_HAN_MAC_DINH, dao.gioiHan);
    }

    @Test
    void thuQuyKhongDuocXemNhatKy() {
        PhienDangNhap.batDau(THU_QUY);
        NhatKyDaoGia dao = new NhatKyDaoGia(List.of());

        assertThrows(LoiKhongCoQuyen.class,
                () -> new NhatKyService(dao).xemMoiNhat());
        assertEquals(0, dao.gioiHan);
    }

    @Test
    void chuaDangNhapKhongDuocXemNhatKy() {
        NhatKyDaoGia dao = new NhatKyDaoGia(List.of());

        assertThrows(LoiKhongCoQuyen.class,
                () -> new NhatKyService(dao).xemMoiNhat());
        assertEquals(0, dao.gioiHan);
    }
}
