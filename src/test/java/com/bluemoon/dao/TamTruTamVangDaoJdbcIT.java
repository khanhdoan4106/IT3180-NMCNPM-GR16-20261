package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import com.bluemoon.model.YeuCauThemHo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
    void themVaLayKhaiBao_ThanhCong() {
        YeuCauThemHo ycHo = new YeuCauThemHo(
                "B-202", LoaiCanHo.CAN_HO, 2,
                new BigDecimal("80.00"), "091", LocalDate.now(),
                "Chu Ho", LocalDate.of(1985, 5, 5), "NAM", "009");
        int hoId = hoDao.themHoGiaDinh(ycHo, quanTri);

        List<NguoiTrongHo> dsNguoi = dao.layNguoiTrongHo(hoId);
        assertFalse(dsNguoi.isEmpty());
        int nkId = dsNguoi.get(0).id();

        YeuCauKhaiBao ycKb = new YeuCauKhaiBao(
                nkId, "TAM_TRU", "GT-001",
                LocalDate.now(), LocalDate.now().plusDays(10),
                "Đến chơi", null);

        int idKb = dao.themKhaiBao(ycKb);
        assertTrue(idKb > 0);

        List<TamTruTamVang> ds = dao.layDanhSachTheoHo(hoId);
        assertEquals(1, ds.size());
        assertEquals("TAM_TRU", ds.get(0).loai());
    }
}