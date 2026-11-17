package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tìm kiếm không phân biệt hoa thường và dấu (giả định 40). */
class TimKiemTest {

    @Test
    void boDauVaChuThuong() {
        assertEquals("nguyen duc", TimKiem.boDau("Nguyễn Đức"));
        assertEquals("tran thi hue", TimKiem.boDau("TRẦN THỊ HUỆ"));
        assertEquals("dinh phi hung", TimKiem.boDau("đinh phi hùng"));
        assertEquals("a-1203", TimKiem.boDau("A-1203"));
    }

    @Test
    void boDauNullHoacRong() {
        assertEquals("", TimKiem.boDau(null));
        assertEquals("", TimKiem.boDau(""));
    }

    @Test
    void khopKhongPhanBietDauVaHoaThuong() {
        assertTrue(TimKiem.khop("Nguyễn Đức", "nguyen duc"));
        assertTrue(TimKiem.khop("Nguyễn Đức", "NGUYỄN"));
        assertTrue(TimKiem.khop("Nguyễn Đức Anh", "duc"));
        assertTrue(TimKiem.khop("nguyen duc", "Nguyễn Đức"));
        assertTrue(TimKiem.khop("A-1203", "a-12"));
    }

    @Test
    void khopBoQuaKhoangTrangThua() {
        assertTrue(TimKiem.khop("Nguyễn  Đức", " nguyen duc "));
    }

    @Test
    void khongKhop() {
        assertFalse(TimKiem.khop("Nguyễn Đức", "nguyen van"));
        assertFalse(TimKiem.khop(null, "nguyen"));
        assertFalse(TimKiem.khop("", "a"));
    }

    @Test
    void tuKhoaRongHoacNullKhopTatCa() {
        assertTrue(TimKiem.khop("Nguyễn Đức", ""));
        assertTrue(TimKiem.khop("Nguyễn Đức", "   "));
        assertTrue(TimKiem.khop("Nguyễn Đức", null));
        assertTrue(TimKiem.khop(null, null));
    }
}
