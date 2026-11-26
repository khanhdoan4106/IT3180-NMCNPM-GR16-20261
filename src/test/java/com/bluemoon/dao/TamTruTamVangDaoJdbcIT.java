package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.YeuCauKhaiBao;
import com.bluemoon.model.YeuCauThemHo;
import com.bluemoon.util.Ngay;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TamTruTamVangDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final TamTruTamVangDaoJdbc dao =
            new TamTruTamVangDaoJdbc(nguon);
    private final HoGiaDinhDaoJdbc hoDao =
            new HoGiaDinhDaoJdbc(nguon);
    private NguoiDung quanTri;

    @BeforeEach
    void datLaiCsdl() throws Exception {
        CsdlKiemThu.dungLaiBang();
        quanTri = CsdlKiemThu.taoQuanTriVien(nguon);
    }

    @Test
    void themVaLayKhaiBao_ThanhCong() throws SQLException {
        int hoId = taoHo("B-202", "009");
        List<NguoiTrongHo> dsNguoi = dao.layNguoiTrongHo(hoId);
        assertFalse(dsNguoi.isEmpty());
        int nkId = dsNguoi.get(0).id();

        YeuCauKhaiBao ycKb = taoKhaiBao(nkId);
        int idKb = dao.themKhaiBao(ycKb, quanTri);
        assertTrue(idKb > 0);

        List<TamTruTamVang> ds = dao.layDanhSachTheoHo(hoId);
        assertEquals(1, ds.size());
        assertEquals("TAM_TRU", ds.get(0).loai());
        assertEquals(2, demNhatKy()); // THEM_HO + GHI_TAM_TRU_TAM_VANG
        assertEquals("GHI_TAM_TRU_TAM_VANG", thaoTacNhatKyCuoi());
        assertEquals("Nhân khẩu Chu Ho (hộ B-202)",
                cotNhatKyCuoi("doi_tuong"));
        assertEquals("Tạm trú: Chu Ho, từ " + Ngay.dinhDang(ycKb.tuNgay())
                + " đến " + Ngay.dinhDang(ycKb.denNgay()),
                cotNhatKyCuoi("noi_dung"));
    }

    @Test
    void ghiNhatKyThatBaiRollbackKhaiBao() throws SQLException {
        int hoId = taoHo("C-303", "010");
        int nkId = dao.layNguoiTrongHo(hoId).get(0).id();
        int soNhatKyTruoc = demNhatKy();

        NhatKyDao daoNhatKyLoi = new NhatKyDao() {
            @Override
            public void ghi(Connection ketNoi, int taiKhoanId,
                    ThaoTac thaoTac, String doiTuong, String noiDung)
                    throws SQLException {
                throw new SQLException("Lỗi giả lập khi ghi nhật ký.");
            }

            @Override
            public List<com.bluemoon.model.NhatKy> danhSachMoiNhat(
                    int gioiHan) {
                return List.of();
            }
        };
        TamTruTamVangDaoJdbc daoCoLoi =
                new TamTruTamVangDaoJdbc(nguon, daoNhatKyLoi);

        assertThrows(LoiCsdl.class, () -> daoCoLoi.themKhaiBao(
                taoKhaiBao(nkId), quanTri));
        assertEquals(0, dem("SELECT COUNT(*) FROM tam_tru_tam_vang"));
        assertEquals(soNhatKyTruoc, demNhatKy());
    }

    @Test
    void layLoaiCuTruCuaNguoiDangO() {
        int hoId = taoHo("D-404", "011");
        int nkId = dao.layNguoiTrongHo(hoId).get(0).id();
        assertEquals(LoaiCuTru.THUONG_TRU, dao.layLoaiCuTru(nkId));
        assertNull(dao.layLoaiCuTru(nkId + 1000));
    }

    @Test
    void dotChongNgay_DaoTuChoiVaKhongGhiNhatKy() throws SQLException {
        int hoId = taoHo("E-505", "012");
        int nkId = dao.layNguoiTrongHo(hoId).get(0).id();
        LocalDate dau = LocalDate.of(2026, 12, 1);
        dao.themKhaiBao(tamVang(nkId, dau, dau.plusDays(9)), quanTri);
        int soNhatKy = demNhatKy();

        assertTrue(dao.coDotChongNgay(nkId, dau.plusDays(9),
                dau.plusDays(20)));
        assertFalse(dao.coDotChongNgay(nkId, dau.plusDays(10),
                dau.plusDays(20)));
        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> dao.themKhaiBao(tamVang(nkId, dau.minusDays(3),
                        dau), quanTri));
        assertEquals(TamTruTamVangDao.LOI_CHONG_NGAY, loi.getMessage());
        assertEquals(1, dem("SELECT COUNT(*) FROM tam_tru_tam_vang"));
        assertEquals(soNhatKy, demNhatKy());
    }

    @Test
    void haiMayCungLuuDotChongNgay_ChiMotDotDuocLuu() throws Exception {
        int hoId = taoHo("F-606", "013");
        int nkId = dao.layNguoiTrongHo(hoId).get(0).id();
        LocalDate dau = LocalDate.of(2027, 1, 1);
        CountDownLatch batDau = new CountDownLatch(1);
        Callable<Boolean> luu = () -> {
            batDau.await();
            try {
                new TamTruTamVangDaoJdbc(nguon).themKhaiBao(
                        tamVang(nkId, dau, dau.plusDays(5)), quanTri);
                return true;
            } catch (IllegalArgumentException loi) {
                return false;
            }
        };
        ExecutorService may = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> mot = may.submit(luu);
            Future<Boolean> hai = may.submit(luu);
            batDau.countDown();
            boolean motLuu = mot.get(30, TimeUnit.SECONDS);
            boolean haiLuu = hai.get(30, TimeUnit.SECONDS);
            assertTrue(motLuu ^ haiLuu);
        } finally {
            may.shutdownNow();
        }
        assertEquals(1, dem("SELECT COUNT(*) FROM tam_tru_tam_vang"));
    }

    private static YeuCauKhaiBao tamVang(int nhanKhauId, LocalDate tu,
            LocalDate den) {
        return new YeuCauKhaiBao(nhanKhauId, "TAM_VANG", null, tu, den,
                "Công tác", "Đà Nẵng");
    }

    private int taoHo(String maCanHo, String cccd) {
        YeuCauThemHo ycHo = new YeuCauThemHo(
                maCanHo, LoaiCanHo.CAN_HO, 2,
                new BigDecimal("80.00"), "091", LocalDate.now(),
                "Chu Ho", LocalDate.of(1985, 5, 5), "NAM", cccd);
        return hoDao.themHoGiaDinh(ycHo, quanTri);
    }

    private YeuCauKhaiBao taoKhaiBao(int nhanKhauId) {
        return new YeuCauKhaiBao(
                nhanKhauId, "TAM_TRU", "GT-001",
                LocalDate.now(), LocalDate.now().plusDays(10),
                "Đến chơi", null);
    }

    private int demNhatKy() throws SQLException {
        return dem("SELECT COUNT(*) FROM nhat_ky");
    }

    private int dem(String sql) throws SQLException {
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            kq.next();
            return kq.getInt(1);
        }
    }

    private String thaoTacNhatKyCuoi() {
        String sql = "SELECT thao_tac FROM nhat_ky "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            return kq.next() ? kq.getString(1) : null;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    private String cotNhatKyCuoi(String cot) {
        String sql = "SELECT " + cot + " FROM nhat_ky "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(sql);
                ResultSet kq = lenh.executeQuery()) {
            return kq.next() ? kq.getString(1) : null;
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }
}
