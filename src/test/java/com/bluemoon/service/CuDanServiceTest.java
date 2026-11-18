package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.CuDanDao;
import com.bluemoon.dao.LoaiLoiCsdl;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.SoLieuCuDan;
import com.bluemoon.model.VaiTro;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test Service bằng DAO giả, không cần MySQL. */
class CuDanServiceTest {

    /** Trang chính mở cho mọi vai trò; đăng nhập Thủ quỹ cho chắc. */
    @BeforeEach
    void dangNhap() {
        PhienDangNhap.batDau(new NguoiDung(2, "thuquy01", "Trần Thị Mai",
                VaiTro.THU_QUY));
    }

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

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

    /** US-022: chưa đăng nhập thì không đọc CSDL, không trả số liệu. */
    @Test
    void chuaDangNhapBiTuChoi() {
        PhienDangNhap.ketThuc();
        CuDanService service = new CuDanService(new CuDanDaoGia(null,
                new LoiCsdl(LoaiLoiCsdl.KHAC, null)));
        assertThrows(LoiKhongCoQuyen.class, service::laySoLieu);
    }
}
