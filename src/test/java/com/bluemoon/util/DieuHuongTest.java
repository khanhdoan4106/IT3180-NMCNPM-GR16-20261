package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.VaiTro;
import com.bluemoon.service.PhienDangNhap;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

/** AC-059: chưa đăng nhập thì không mở được màn hình chức năng nào. */
class DieuHuongTest {

    @Test
    void ac059ChuaDangNhapThiVeManDangNhap() {
        PhienDangNhap.ketThuc();
        Pane vungThan = new Pane();
        AtomicInteger soLanVeDangNhap = new AtomicInteger();
        DieuHuong dieuHuong =
                new DieuHuong(vungThan, soLanVeDangNhap::incrementAndGet);
        for (ManHinh manHinh : ManHinh.values()) {
            dieuHuong.mo(manHinh);
        }
        assertEquals(ManHinh.values().length, soLanVeDangNhap.get());
        assertTrue(vungThan.getChildren().isEmpty());
    }

    /** US-022: câu báo khi vai trò không được mở màn hình. */
    @Test
    void cauBaoKhiKhongCoQuyenMoMan() {
        assertEquals("Tài khoản Thủ quỹ không được mở “Hộ gia đình”. "
                + "Đã về Trang chính.", DieuHuong.cauKhongCoQuyen(
                        ManHinh.HO_GIA_DINH, VaiTro.THU_QUY));
    }
}
