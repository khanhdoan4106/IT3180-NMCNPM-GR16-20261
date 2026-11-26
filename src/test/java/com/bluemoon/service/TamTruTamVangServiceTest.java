package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.TamTruTamVangDao;
import com.bluemoon.model.LoaiCuTru;
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

    private LoaiCuTru loaiCuTru = LoaiCuTru.TAM_TRU;
    private boolean chongNgay;
    private YeuCauKhaiBao daLuu;

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
        public LoaiCuTru layLoaiCuTru(int nhanKhauId) {
            return loaiCuTru;
        }

        @Override
        public boolean coDotChongNgay(int nhanKhauId, LocalDate tuNgay,
                LocalDate denNgay) {
            return chongNgay;
        }

        @Override
        public int themKhaiBao(YeuCauKhaiBao yc, NguoiDung nguoiDung) {
            daLuu = yc;
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

    private void baoLoi(YeuCauKhaiBao yc, String cauBao) {
        Exception loi = assertThrows(IllegalArgumentException.class,
                () -> service.themKhaiBao(yc));
        assertEquals(cauBao, loi.getMessage());
        assertNull(daLuu);
    }

    private static YeuCauKhaiBao tamVang(LocalDate tu, LocalDate den) {
        return new YeuCauKhaiBao(1, "TAM_VANG", null, tu, den,
                "Công tác", "Đà Nẵng");
    }

    @Test
    void thieuNgayBatDau_BaoLoi() {
        baoLoi(new YeuCauKhaiBao(1, "TAM_TRU", null, null,
                LocalDate.now(), null, null), "Vui lòng nhập đủ ngày tháng.");
    }

    @Test
    void thieuNgayKetThuc_BaoLoi() {
        baoLoi(new YeuCauKhaiBao(1, "TAM_TRU", null, LocalDate.now(),
                null, null, null), "Vui lòng nhập đủ ngày tháng.");
    }

    @Test
    void tamTruChoNguoiThuongTru_BaoLoi() {
        loaiCuTru = LoaiCuTru.THUONG_TRU;
        baoLoi(taoYeuCauHopLe(),
                "Người thường trú không ghi tạm trú, chỉ ghi tạm vắng.");
    }

    @Test
    void tamVangChoNguoiTamTru_BaoLoi() {
        baoLoi(tamVang(LocalDate.now(), LocalDate.now().plusDays(3)),
                "Người tạm trú không ghi tạm vắng, chỉ ghi thêm đợt "
                + "tạm trú.");
    }

    @Test
    void tamVangChoNguoiThuongTru_DuocLuu() {
        loaiCuTru = LoaiCuTru.THUONG_TRU;
        service.themKhaiBao(
                tamVang(LocalDate.now(), LocalDate.now().plusDays(3)));
        assertEquals("TAM_VANG", daLuu.loai());
        assertEquals("Đà Nẵng", daLuu.diaChiTamVang());
    }

    @Test
    void nhanKhauKhongConO_BaoLoi() {
        loaiCuTru = null;
        baoLoi(taoYeuCauHopLe(),
                "Không tìm thấy nhân khẩu đang ở trong hộ.");
    }

    @Test
    void dotChongNgay_BaoLoi() {
        chongNgay = true;
        baoLoi(taoYeuCauHopLe(), TamTruTamVangDao.LOI_CHONG_NGAY);
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
