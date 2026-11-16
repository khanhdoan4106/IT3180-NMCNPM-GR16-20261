package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.CuDanDao;
import com.bluemoon.dao.LoaiLoiCsdl;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.model.SoLieuCuDan;
import org.junit.jupiter.api.Test;

/** Test Service bằng DAO giả, không cần MySQL. */
class CuDanServiceTest {

    /** DAO giả: trả số liệu cho trước, hoặc ném lỗi cho trước. */
    private static final class CuDanDaoGia implements CuDanDao {
        private final SoLieuCuDan soLieu;
        private final LoiCsdl loi;

        CuDanDaoGia(SoLieuCuDan soLieu, LoiCsdl loi) {
            this.soLieu = soLieu;
            this.loi = loi;
        }

        @Override
        public SoLieuCuDan demDangO() {
            if (loi != null) {
                throw loi;
            }
            return soLieu;
        }
    }

    @Test
    void traSoLieuTuDao() {
        CuDanService service = new CuDanService(
                new CuDanDaoGia(new SoLieuCuDan(12, 34), null));
        assertEquals(new SoLieuCuDan(12, 34), service.laySoLieu());
    }

    @Test
    void loiCsdlChuyenNguyenLenController() {
        LoiCsdl loi = new LoiCsdl(LoaiLoiCsdl.KHONG_TOI_MAY_CHU, null);
        CuDanService service = new CuDanService(new CuDanDaoGia(null, loi));
        assertSame(loi, assertThrows(LoiCsdl.class, service::laySoLieu));
    }
}
