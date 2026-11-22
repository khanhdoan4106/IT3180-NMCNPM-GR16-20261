package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.TamTruTamVangDao;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TamTruTamVangServiceTest {

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
        public int themKhaiBao(YeuCauKhaiBao yc) {
            return 1;
        }
    };

    private final TamTruTamVangService service =
            new TamTruTamVangService(dao);

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
}