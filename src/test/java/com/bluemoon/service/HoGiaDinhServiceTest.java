package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
import com.bluemoon.model.YeuCauThemHo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kiểm thử đơn vị cho {@link HoGiaDinhService} (AC-001..AC-004).
 */
class HoGiaDinhServiceTest {

    private static final NguoiDung QUAN_TRI = new NguoiDung(
            1, "admin", "Quản trị viên", VaiTro.QUAN_TRI);

    @BeforeEach
    void dangNhapQuanTriVien() {
        PhienDangNhap.batDau(QUAN_TRI);
    }

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    @Test
    void themHoHopLeLuuDungDienTichVaHienTrongDanhSach() {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        HoGiaDinhService service = new HoGiaDinhService(dao);

        YeuCauThemHo yc = new YeuCauThemHo(
                "A-101",
                LoaiCanHo.CAN_HO,
                1,
                new BigDecimal("75.50"),
                "0912345678",
                LocalDate.of(2026, 11, 1),
                "Nguyễn Văn An",
                LocalDate.of(1990, 5, 10),
                "NAM",
                "001090012345");

        int id = service.themHoGiaDinh(yc);
        assertEquals(1, id);
        assertEquals(1, service.layDanhSachDangO().size());
        assertEquals(
                new BigDecimal("75.50"),
                service.layDanhSachDangO().get(0).dienTich());
    }

    @Test
    void thieuThongTinBatBuocBaoTruongConThieuVaKhongLuu() {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        HoGiaDinhService service = new HoGiaDinhService(dao);

        YeuCauThemHo yc = new YeuCauThemHo(
                " ",
                LoaiCanHo.CAN_HO,
                1,
                null,
                null,
                LocalDate.of(2026, 11, 1),
                "",
                LocalDate.of(1990, 5, 10),
                "NAM",
                "001090012345");

        IllegalArgumentException loi = assertThrows(
                IllegalArgumentException.class,
                () -> service.themHoGiaDinh(yc));
        assertTrue(loi.getMessage().contains("mã căn hộ"));
        assertTrue(loi.getMessage().contains("diện tích"));
        assertTrue(loi.getMessage().contains("tên chủ hộ"));
        assertTrue(dao.danhSach.isEmpty());
    }

    @Test
    void trungMaCanHoDangOBaoTrungVaKhongLuu() {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        HoGiaDinhService service = new HoGiaDinhService(dao);

        YeuCauThemHo yc = new YeuCauThemHo(
                "B-202",
                LoaiCanHo.CAN_HO,
                2,
                new BigDecimal("68.00"),
                null,
                LocalDate.of(2026, 11, 1),
                "Trần Thị Bình",
                LocalDate.of(1985, 8, 20),
                "NU",
                "001185000111");

        service.themHoGiaDinh(yc);

        IllegalArgumentException loi = assertThrows(
                IllegalArgumentException.class,
                () -> service.themHoGiaDinh(yc));
        assertTrue(loi.getMessage().contains("đã tồn tại"));
        assertEquals(1, dao.danhSach.size());
    }

    @Test
    void thuQuyKhongDuocThemHo() {
        PhienDangNhap.batDau(new NguoiDung(
                2, "thuquy", "Thủ quỹ", VaiTro.THU_QUY));
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        HoGiaDinhService service = new HoGiaDinhService(dao);

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themHoGiaDinh(taoYeuCauHopLe()));
        assertTrue(dao.danhSach.isEmpty());
    }

    @Test
    void chuaDangNhapKhongDuocThemHo() {
        PhienDangNhap.ketThuc();
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        HoGiaDinhService service = new HoGiaDinhService(dao);

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themHoGiaDinh(taoYeuCauHopLe()));
        assertTrue(dao.danhSach.isEmpty());
    }

    private static YeuCauThemHo chuHoTamTru(String noiThuongTru,
            LocalDate tu, LocalDate den) {
        return new YeuCauThemHo(
                "T-501", LoaiCanHo.CAN_HO, 5,
                new BigDecimal("60.00"), null,
                LocalDate.of(2026, 11, 1), "Trần Thị Thuê",
                LocalDate.of(1995, 3, 3), "NU", "001195012345",
                LoaiCuTru.TAM_TRU, noiThuongTru, tu, den);
    }

    private static void baoLoi(YeuCauThemHo yc, String cauBao) {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> new HoGiaDinhService(dao).themHoGiaDinh(yc));
        assertEquals(cauBao, loi.getMessage());
        assertTrue(dao.danhSach.isEmpty());
    }

    @Test
    void chuHoTamTruThieuNoiThuongTru_BaoLoi() {
        baoLoi(chuHoTamTru(" ", LocalDate.of(2026, 11, 1),
                LocalDate.of(2027, 4, 30)),
                "Chủ hộ tạm trú bắt buộc nhập nơi thường trú.");
    }

    @Test
    void chuHoTamTruThieuThoiHan_BaoLoi() {
        baoLoi(chuHoTamTru("Nam Định", LocalDate.of(2026, 11, 1), null),
                "Chủ hộ tạm trú bắt buộc nhập thời hạn tạm trú.");
    }

    @Test
    void chuHoTamTruDenTruocTu_BaoLoi() {
        baoLoi(chuHoTamTru("Nam Định", LocalDate.of(2027, 4, 30),
                LocalDate.of(2026, 11, 1)),
                "Ngày kết thúc tạm trú không được trước ngày bắt đầu.");
    }

    @Test
    void chuHoTamTruHopLe_LuuNoiThuongTruVaDotDau() {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        new HoGiaDinhService(dao).themHoGiaDinh(chuHoTamTru(
                "  Nam Định ", LocalDate.of(2026, 11, 1),
                LocalDate.of(2027, 4, 30)));
        assertEquals(LoaiCuTru.TAM_TRU, dao.daLuu.loaiCuTruChuHo());
        assertEquals("Nam Định", dao.daLuu.noiThuongTruChuHo());
        assertEquals(LocalDate.of(2026, 11, 1), dao.daLuu.tamTruTu());
        assertEquals(LocalDate.of(2027, 4, 30), dao.daLuu.tamTruDen());
    }

    @Test
    void chuHoThuongTru_BoThongTinTamTru() {
        HoGiaDinhDaoGia dao = new HoGiaDinhDaoGia();
        YeuCauThemHo mau = chuHoTamTru("Nam Định",
                LocalDate.of(2026, 11, 1), LocalDate.of(2027, 4, 30));
        new HoGiaDinhService(dao).themHoGiaDinh(new YeuCauThemHo(
                mau.maCanHo(), mau.loaiCanHo(), mau.tang(), mau.dienTich(),
                null, mau.ngayChuyenDen(), mau.tenChuHo(),
                mau.ngaySinhChuHo(), mau.gioiTinhChuHo(), mau.cccdChuHo(),
                LoaiCuTru.THUONG_TRU, "Nam Định", mau.tamTruTu(),
                mau.tamTruDen()));
        assertEquals(LoaiCuTru.THUONG_TRU, dao.daLuu.loaiCuTruChuHo());
        assertNull(dao.daLuu.noiThuongTruChuHo());
        assertNull(dao.daLuu.tamTruTu());
        assertNull(dao.daLuu.tamTruDen());
    }

    private YeuCauThemHo taoYeuCauHopLe() {
        return new YeuCauThemHo(
                "Z-999", LoaiCanHo.CAN_HO, 9,
                new BigDecimal("80.00"), null,
                LocalDate.of(2026, 11, 1), "Nguyễn Văn Test",
                LocalDate.of(1990, 5, 10), "NAM", "001090012346");
    }

    private static final class HoGiaDinhDaoGia implements HoGiaDinhDao {

        private final List<HoGiaDinh> danhSach = new ArrayList<>();
        private final List<String> dsCccd = new ArrayList<>();
        private YeuCauThemHo daLuu;

        @Override
        public List<HoGiaDinh> layDanhSachDangO() {
            return List.copyOf(danhSach);
        }

        @Override
        public boolean tonTaiMaCanHoDangO(String maCanHo) {
            return danhSach.stream()
                    .anyMatch(h -> h.maCanHo().equals(maCanHo));
        }

        @Override
        public boolean tonTaiCccdDangO(String soCccd) {
            return dsCccd.contains(soCccd);
        }

        @Override
        public int themHoGiaDinh(YeuCauThemHo yc, NguoiDung nguoiDung) {
            daLuu = yc;
            int id = danhSach.size() + 1;
            danhSach.add(new HoGiaDinh(
                    id,
                    yc.maCanHo(),
                    yc.loaiCanHo(),
                    yc.tang(),
                    yc.dienTich(),
                    yc.tenChuHo(),
                    yc.soDienThoai(),
                    1,
                    yc.ngayChuyenDen()));
            if (yc.cccdChuHo() != null) {
                dsCccd.add(yc.cccdChuHo());
            }
            return id;
        }
    }
}