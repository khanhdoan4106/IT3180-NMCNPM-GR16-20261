package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class NhanKhauServiceTest {

    // Tạo DAO giả để test logic của Service
    private final NhanKhauDao daoGia = new NhanKhauDao() {
        @Override
        public List<NhanKhau> layDanhSachTheoHo(int hoId) {
            return List.of();
        }

        @Override
        public boolean tonTaiCccdDangO(String soCccd) {
            return "012345678901".equals(soCccd); // Báo trùng nếu nhập số này
        }

        @Override
        public int themNhanKhau(YeuCauThemNhanKhau yc) {
            return 1;
        }
    };

    private final NhanKhauService service = new NhanKhauService(daoGia);

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
}