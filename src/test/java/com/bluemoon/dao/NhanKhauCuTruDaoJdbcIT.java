package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** US-002: lưu quan hệ Bố/Mẹ, loại cư trú và đợt tạm trú đầu tiên. */
class NhanKhauCuTruDaoJdbcIT {

    private static final LocalDate SINH = LocalDate.of(2020, 1, 1);
    private static final LocalDate TU = LocalDate.of(2026, 11, 1);
    private static final LocalDate DEN = LocalDate.of(2027, 4, 30);

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final NhanKhauDaoJdbc dao = new NhanKhauDaoJdbc(nguon);
    private int hoId;
    private NguoiDung quanTri;

    @BeforeEach
    void datLaiCsdl() throws Exception {
        CsdlKiemThu.dungLaiBang();
        quanTri = CsdlKiemThu.taoQuanTriVien(nguon);
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(
                        "INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, "
                                + "tang, dien_tich, ngay_chuyen_den) VALUES "
                                + "('A-101', 'CAN_HO', 1, 70, '2026-01-01')",
                        Statement.RETURN_GENERATED_KEYS)) {
            lenh.executeUpdate();
            try (ResultSet khoa = lenh.getGeneratedKeys()) {
                khoa.next();
                hoId = khoa.getInt(1);
            }
        }
    }

    private YeuCauThemNhanKhau tamTru(LocalDate tu, LocalDate den) {
        return new YeuCauThemNhanKhau(hoId, "Người Tạm Trú", SINH, "NAM",
                "KHAC", null, null, null, null, LoaiCuTru.TAM_TRU,
                "Nam Định", tu, den);
    }

    private int dem(String bang) throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi();
                Statement lenh = ketNoi.createStatement();
                ResultSet kq = lenh.executeQuery(
                        "SELECT COUNT(*) FROM " + bang)) {
            kq.next();
            return kq.getInt(1);
        }
    }

    @Test
    void luuQuanHeBoMe() {
        dao.themNhanKhau(new YeuCauThemNhanKhau(hoId, "Bố Chủ Hộ", SINH,
                "NAM", "BO_ME", null, null, null, null), quanTri);

        NhanKhau nguoi = dao.layDanhSachTheoHo(hoId).get(0);
        assertEquals("BO_ME", nguoi.quanHe());
        assertEquals(LoaiCuTru.THUONG_TRU, nguoi.loaiCuTru());
        assertNull(nguoi.noiThuongTru());
    }

    @Test
    void tamTruLuuCungDotDauTien() throws Exception {
        int id = dao.themNhanKhau(tamTru(TU, DEN), quanTri);

        List<NhanKhau> ds = dao.layDanhSachTheoHo(hoId);
        assertEquals(LoaiCuTru.TAM_TRU, ds.get(0).loaiCuTru());
        assertEquals("Nam Định", ds.get(0).noiThuongTru());
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(
                        "SELECT loai, tu_ngay, den_ngay "
                                + "FROM tam_tru_tam_vang "
                                + "WHERE nhan_khau_id = ?")) {
            lenh.setInt(1, id);
            try (ResultSet kq = lenh.executeQuery()) {
                kq.next();
                assertEquals("TAM_TRU", kq.getString("loai"));
                assertEquals(TU, kq.getDate("tu_ngay").toLocalDate());
                assertEquals(DEN, kq.getDate("den_ngay").toLocalDate());
            }
        }
        assertEquals(1, dem("tam_tru_tam_vang"));
    }

    /** Đợt tạm trú lỗi (vi phạm CHECK ngày) thì không lưu người. */
    @Test
    void dotTamTruLoi_KhongLuuNguoi() throws Exception {
        assertThrows(LoiCsdl.class,
                () -> dao.themNhanKhau(tamTru(DEN, TU), quanTri));

        assertEquals(0, dem("nhan_khau"));
        assertEquals(0, dem("tam_tru_tam_vang"));
    }
}
