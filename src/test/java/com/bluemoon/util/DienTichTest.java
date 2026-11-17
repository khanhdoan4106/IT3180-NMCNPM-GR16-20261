package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Hiện diện tích "75,50 m²" (ui.md). */
class DienTichTest {

    @Test
    void haiChuSoLeDauPhayThapPhan() {
        assertEquals("75,50 m²", DienTich.dinhDang(new BigDecimal("75.5")));
        assertEquals("80,00 m²", DienTich.dinhDang(new BigDecimal("80")));
        assertEquals("0,00 m²", DienTich.dinhDang(BigDecimal.ZERO));
    }

    @Test
    void dauChamNganCachHangNghin() {
        assertEquals("1.234,50 m²",
                DienTich.dinhDang(new BigDecimal("1234.5")));
        assertEquals("1.234.567,00 m²",
                DienTich.dinhDang(new BigDecimal("1234567")));
    }

    @Test
    void lamTronNuaLen() {
        assertEquals("75,56 m²", DienTich.dinhDang(new BigDecimal("75.555")));
        assertEquals("75,55 m²", DienTich.dinhDang(new BigDecimal("75.554")));
    }

    @Test
    void nullLaChuoiRong() {
        assertEquals("", DienTich.dinhDang(null));
    }
}
