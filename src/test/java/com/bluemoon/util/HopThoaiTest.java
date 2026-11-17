package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Hộp thoại dùng cùng CSS với cửa sổ chính. Không mở hộp thoại thật vì
 * test chạy không có màn hình (CI).
 */
class HopThoaiTest {

    @Test
    void tepCssCoTrongResources() {
        assertNotNull(HopThoai.class.getResource(HopThoai.TEP_CSS));
        assertTrue(HopThoai.duongDanCss().endsWith("css/giao-dien.css"),
                HopThoai.duongDanCss());
    }

    @Test
    void cungTepCssVoiCuaSoChinh() {
        // App lấy CSS bằng App.class.getResource("css/giao-dien.css").
        String cuaSoChinh = com.bluemoon.App.class
                .getResource("css/giao-dien.css").toExternalForm();
        assertEquals(cuaSoChinh, HopThoai.duongDanCss());
    }
}
