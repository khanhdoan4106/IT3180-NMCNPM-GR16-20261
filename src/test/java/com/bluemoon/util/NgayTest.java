package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javafx.util.StringConverter;
import org.junit.jupiter.api.Test;

/** Hiện và đọc ngày dạng dd/MM/yyyy (ui.md). */
class NgayTest {

    @Test
    void dinhDangCoSoKhongODau() {
        assertEquals("05/09/2026", Ngay.dinhDang(LocalDate.of(2026, 9, 5)));
        assertEquals("31/12/2026",
                Ngay.dinhDang(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void dinhDangNullLaChuoiRong() {
        assertEquals("", Ngay.dinhDang(null));
    }

    @Test
    void docNgayDungDang() {
        assertEquals(LocalDate.of(2026, 9, 5), Ngay.doc("05/09/2026"));
        assertEquals(LocalDate.of(2026, 9, 5), Ngay.doc(" 5/9/2026 "));
    }

    @Test
    void docNullHoacRongBaoNhapNgay() {
        for (String chuoi : new String[] {null, "", "   "}) {
            IllegalArgumentException loi = assertThrows(
                    IllegalArgumentException.class, () -> Ngay.doc(chuoi));
            assertTrue(loi.getMessage().startsWith("Nhập ngày"),
                    loi.getMessage());
        }
    }

    @Test
    void docSaiDangBaoLoiTiengViet() {
        for (String chuoi : new String[] {"2026-09-05", "05.09.2026",
                "ngay mai", "05/09/26"}) {
            IllegalArgumentException loi = assertThrows(
                    IllegalArgumentException.class, () -> Ngay.doc(chuoi));
            assertTrue(loi.getMessage().contains("ngày/tháng/năm"),
                    loi.getMessage());
            assertTrue(loi.getMessage().contains(chuoi), loi.getMessage());
        }
    }

    @Test
    void docNgayKhongCoThatBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> Ngay.doc("31/02/2026"));
        assertThrows(IllegalArgumentException.class,
                () -> Ngay.doc("29/02/2025"));
        assertEquals(LocalDate.of(2024, 2, 29), Ngay.doc("29/02/2024"));
    }

    @Test
    void boChuyenDoiChoDatePicker() {
        StringConverter<LocalDate> chuyen = Ngay.boChuyenDoi();
        assertEquals("05/09/2026", chuyen.toString(LocalDate.of(2026, 9, 5)));
        assertEquals("", chuyen.toString(null));
        assertEquals(LocalDate.of(2026, 9, 5),
                chuyen.fromString("05/09/2026"));
        assertNull(chuyen.fromString(""));
        assertNull(chuyen.fromString(null));
        // DatePicker chỉ bắt DateTimeParseException để trả lại ngày cũ.
        assertThrows(DateTimeParseException.class,
                () -> chuyen.fromString("31/02/2026"));
    }
}
