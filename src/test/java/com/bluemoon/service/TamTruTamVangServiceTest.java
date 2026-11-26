package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.TamTruTamVangDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.VaiTro;
import com.bluemoon.model.YeuCauKhaiBao;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TamTruTamVangServiceTest {

    private static final NguoiDung QUAN_TRI = new NguoiDung(
            1, "admin", "Quản trị viên", VaiTro.QUAN_TRI);

    private final TamTruTamVangDao dao = new TamTruTamVangDao() {
        @Override
        public List<NguoiTrongHo> layNguoiTrongHo(int hoId) {
            return List.of();
        }

        @Override
        public List<TamTruTamVang> layDanhSachTheoHo(int id) {
            return List.of();
        }

        @Override
        public int themKhaiBao(YeuCauKhaiBao yc, NguoiDung nguoiDung) {
            return 1;
        }
    };

    private final TamTruTamVangService service =
            new TamTruTamVangService(dao);

    @BeforeEach
    void dangNhapQuanTriVien() {
        PhienDangNhap.batDau(QUAN_TRI);
    }

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    @Test
    void themKhaiBao_NgayBatDauLonHonNgayKetThuc_BaoLoi() {
        YeuCauKhaiBao yc = new YeuCauKhaiBao(
                1, "TAM_TRU", null,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2),
                null, null);
        Exception loi = assertThrows(
                IllegalArgumentException.class,
                () -> service.themKhaiBao(yc));
        assertEquals(
                "Ngày bắt đầu không được lớn hơn ngày kết thúc.",
                loi.getMessage());
    }

    @Test
    void thuQuyKhongDuocThemKhaiBao() {
        PhienDangNhap.batDau(new NguoiDung(
                2, "thuquy", "Thủ quỹ", VaiTro.THU_QUY));
        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themKhaiBao(taoYeuCauHopLe()));
    }

    @Test
    void chuaDangNhapKhongDuocThemKhaiBao() {
        PhienDangNhap.ketThuc();
        assertThrows(LoiKhongCoQuyen.class,
                () -> service.themKhaiBao(taoYeuCauHopLe()));
    }

    private YeuCauKhaiBao taoYeuCauHopLe() {
        return new YeuCauKhaiBao(1, "TAM_TRU", null,
                LocalDate.now(), LocalDate.now().plusDays(5),
                "Test", null);
    }

    @Test
    void thuQuyKhongDuocXemTamTruTamVang() {
        PhienDangNhap.batDau(new NguoiDung(
                2, "thuquy", "Thủ quỹ", VaiTro.THU_QUY));
        assertThrows(LoiKhongCoQuyen.class,
                () -> service.layNguoiTrongHo(1));
        assertThrows(LoiKhongCoQuyen.class,
                () -> service.layDanhSachTheoHo(1));
    }
}
