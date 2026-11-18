package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.YeuCauThemHo;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NhanKhauDaoJdbcIT {

    private final NguonKetNoi nguon = CsdlKiemThu.nguonUngDung();
    private final NhanKhauDaoJdbc dao = new NhanKhauDaoJdbc(nguon);
    private final HoGiaDinhDaoJdbc hoDao = new HoGiaDinhDaoJdbc(nguon);

    @BeforeEach
    void datLaiCsdl() throws Exception {
        CsdlKiemThu.dungLaiBang();
    }

    @Test
    void themVaLayNhanKhau_ThanhCong() {
        // 1. Tạo 1 hộ gia đình trước
        YeuCauThemHo ycHo = new YeuCauThemHo("A-101", LoaiCanHo.CAN_HO, 1, 
                new BigDecimal("70.00"), "090", LocalDate.now(), 
                "Chu Ho", LocalDate.of(1980, 1, 1), "NAM", "001");
        int hoId = hoDao.themHoGiaDinh(ycHo);

        // 2. Thêm nhân khẩu vào hộ đó
        YeuCauThemNhanKhau ycNk = new YeuCauThemNhanKhau(hoId, "Vo Chu Ho", 
                LocalDate.of(1982, 1, 1), "NU", "VO_CHONG", "002", 
                null, null, null);
        int nkId = dao.themNhanKhau(ycNk);
        assertTrue(nkId > 0);

        // 3. Kiểm tra danh sách nhân khẩu của hộ
        List<NhanKhau> ds = dao.layDanhSachTheoHo(hoId);
        // Có 2 người: 1 chủ hộ (tự sinh khi thêm hộ) + 1 vợ (vừa thêm)
        assertEquals(2, ds.size());
        
        // Người thứ 2 là vợ (sắp xếp theo ngày sinh tăng dần: 1980 rồi 1982)
        assertEquals("Vo Chu Ho", ds.get(1).hoTen());
        assertTrue(dao.tonTaiCccdDangO("002"));
    }
}