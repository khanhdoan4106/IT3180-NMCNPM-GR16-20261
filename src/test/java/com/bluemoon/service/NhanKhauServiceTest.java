package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHe;
import com.bluemoon.model.VaiTro;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NhanKhauServiceTest {

    private static final NguoiDung QUAN_TRI = new NguoiDung(
            1, "admin", "Quản trị viên", VaiTro.QUAN_TRI);
    private static final LocalDate TU = LocalDate.of(2026, 11, 1);
    private static final LocalDate DEN = LocalDate.of(2027, 4, 30);

    private YeuCauThemNhanKhau daLuu;

    // Tạo DAO giả để test logic của Service
    private final NhanKhauDao daoGia = new NhanKhauDao() {
        @Override
        public List<NhanKhau> layDanhSachTheoHo(int hoId) {
            return List.of();
        }

        @Override
        public boolean tonTaiCccdDangO(String soCccd) {
            return "012345678901".equals(soCccd);
        }

        @Override
        public int themNhanKhau(YeuCauThemNhanKhau yc, NguoiDung nguoiDung) {
            daLuu = yc;
            return 1;
        }
    };

    private final NhanKhauService service = new NhanKhauService(daoGia);

    @BeforeEach
    void dangNhapQuanTriVien() {
        PhienDangNhap.batDau(QUAN_TRI);
    }

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    private static YeuCauThemNhanKhau tamTru(String noiThuongTru,
            LocalDate tu, LocalDate den) {
        return new YeuCauThemNhanKhau(1, "Nguyễn D",
                LocalDate.now().minusYears(5), "NAM", "KHAC", "", null, null,
                null, LoaiCuTru.TAM_TRU, noiThuongTru, tu, den);
    }

    private void baoLoi(YeuCauThemNhanKhau yc, String cauBao) {
        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> service.themNhanKhau(yc));
        assertEquals(cauBao, loi.getMessage());
        assertNull(daLuu);
    }

    @Test
    void themNhanKhau_Du14TuoiKhongCoCccd_BaoLoi() {
        YeuCauThemNhanKhau yc = new YeuCauThemNhanKhau(1, "Nguyễn B",
                LocalDate.now().minusYears(15), "NAM", "CON",
                "", null, null, null);

        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> service.themNhanKhau(yc));
        assertEquals("Công dân từ đủ 14 tuổi bắt buộc nhập số CCCD.",
                loi.getMessage());
    }

    @Test
    void themNhanKhau_TrungCccd_BaoLoi() {
        YeuCauThemNhanKhau yc = new YeuCauThemNhanKhau(1, "Nguyễn C",
                LocalDate.now().minusYears(20), "NAM", "CON",
                "012345678901", null, null, null);

        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> service.themNhanKhau(yc));
        assertEquals("Số CCCD 012345678901 đã tồn tại trong hệ thống.",
                loi.getMessage());
    }

    @Test
    void thuQuyKhongDuocThemNhanKhau() {
        PhienDangNhap.batDau(new NguoiDung(
                2, "thuquy", "Thủ quỹ", VaiTro.THU_QUY));
        YeuCauThemNhanKhau yc = taoYeuCauHopLe();

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themNhanKhau(yc));
    }

    @Test
    void chuaDangNhapKhongDuocThemNhanKhau() {
        PhienDangNhap.ketThuc();

        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themNhanKhau(taoYeuCauHopLe()));
    }

    @Test
    void quanHeBoMe_LuuDungMa() {
        service.themNhanKhau(new YeuCauThemNhanKhau(1, "Nguyễn E",
                LocalDate.now().minusYears(5), "NU", "BO_ME", "", null, null,
                null));
        assertEquals("BO_ME", daLuu.quanHe());
    }

    @Test
    void quanHeKhongHopLe_BaoLoi() {
        baoLoi(new YeuCauThemNhanKhau(1, "Nguyễn E",
                LocalDate.now().minusYears(5), "NU", "ONG_BA", "", null,
                null, null), "Quan hệ không hợp lệ.");
    }

    @Test
    void nhanQuanHe_HienChu() {
        assertEquals("Chủ hộ", QuanHe.tuMa("CHU_HO").getNhan());
        assertEquals("Vợ/Chồng", QuanHe.tuMa("VO_CHONG").getNhan());
        assertEquals("Con", QuanHe.tuMa("CON").getNhan());
        assertEquals("Bố/Mẹ", QuanHe.tuMa("BO_ME").getNhan());
        assertEquals("Khác", QuanHe.tuMa("KHAC").getNhan());
    }

    @Test
    void ngaySinhTuongLai_BaoLoi() {
        baoLoi(new YeuCauThemNhanKhau(1, "Nguyễn F",
                LocalDate.now().plusDays(1), "NAM", "CON", "", null, null,
                null), "Ngày sinh không được sau ngày hôm nay.");
    }

    @Test
    void ngaySinhHomNay_DuocLuu() {
        LocalDate homNay = LocalDate.now();
        service.themNhanKhau(new YeuCauThemNhanKhau(1, "Nguyễn F", homNay,
                "NAM", "CON", "", null, null, null));
        assertEquals(homNay, daLuu.ngaySinh());
    }

    @Test
    void thieuLoaiCuTru_BaoLoi() {
        baoLoi(new YeuCauThemNhanKhau(1, "Nguyễn G",
                LocalDate.now().minusYears(5), "NAM", "CON", "", null, null,
                null, null, null, null, null),
                "Vui lòng nhập đầy đủ: loại cư trú");
    }

    @Test
    void tamTruThieuNoiThuongTru_BaoLoi() {
        baoLoi(tamTru("   ", TU, DEN),
                "Người tạm trú bắt buộc nhập nơi thường trú.");
    }

    @Test
    void tamTruThieuThoiHan_BaoLoi() {
        baoLoi(tamTru("Nam Định", TU, null),
                "Người tạm trú bắt buộc nhập thời hạn tạm trú.");
    }

    @Test
    void tamTruDenTruocTu_BaoLoi() {
        baoLoi(tamTru("Nam Định", DEN, TU),
                "Ngày kết thúc tạm trú không được trước ngày bắt đầu.");
    }

    @Test
    void tamTruHopLe_LuuNoiThuongTruVaThoiHan() {
        service.themNhanKhau(tamTru("  Nam Định ", TU, DEN));
        assertEquals(LoaiCuTru.TAM_TRU, daLuu.loaiCuTru());
        assertEquals("Nam Định", daLuu.noiThuongTru());
        assertEquals(TU, daLuu.tamTruTu());
        assertEquals(DEN, daLuu.tamTruDen());
    }

    @Test
    void thuongTru_BoNoiThuongTruVaThoiHan() {
        service.themNhanKhau(new YeuCauThemNhanKhau(1, "Nguyễn H",
                LocalDate.now().minusYears(5), "NAM", "CON", "", null, null,
                null, LoaiCuTru.THUONG_TRU, "Nam Định", TU, DEN));
        assertNull(daLuu.noiThuongTru());
        assertNull(daLuu.tamTruTu());
        assertNull(daLuu.tamTruDen());
    }

    private YeuCauThemNhanKhau taoYeuCauHopLe() {
        return new YeuCauThemNhanKhau(1, "Nguyễn D",
                LocalDate.now().minusYears(20), "NAM", "CON",
                "123456789012", null, null, null);
    }
}
