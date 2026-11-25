package com.bluemoon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Kiểm thử enum thao tác nhật ký, không cần MySQL. */
class ThaoTacTest {

    private static final Path SCHEMA = Path.of("database", "schema.sql");
    private static final Pattern KHOI_CHECK = Pattern.compile(
            "chk_nky_thao_tac\\s+CHECK\\s*\\(\\s*thao_tac\\s+IN\\s*"
                    + "\\((.*?)\\)\\)",
            Pattern.DOTALL);
    private static final Pattern MA = Pattern.compile("'([A-Z_]+)'");

    @Test
    void tenHangKhopDanhSachCheckTrongSchema() throws IOException {
        String schema = Files.readString(SCHEMA, StandardCharsets.UTF_8);
        Matcher khoi = KHOI_CHECK.matcher(schema);
        assertTrue(khoi.find(), "Không thấy chk_nky_thao_tac trong schema");

        Set<String> maTrongSchema = new LinkedHashSet<>();
        Matcher ma = MA.matcher(khoi.group(1));
        while (ma.find()) {
            maTrongSchema.add(ma.group(1));
        }
        Set<String> maTrongEnum = new LinkedHashSet<>();
        for (ThaoTac thaoTac : ThaoTac.values()) {
            maTrongEnum.add(thaoTac.name());
        }

        assertEquals(maTrongSchema, maTrongEnum);
    }

    @Test
    void moiThaoTacCoNhanTiengVietKhongTrung() {
        Set<String> nhan = new HashSet<>();
        for (ThaoTac thaoTac : ThaoTac.values()) {
            assertFalse(thaoTac.getNhan().isBlank(), thaoTac.name());
            assertFalse(thaoTac.getNhan().matches("[A-Z_]+"),
                    thaoTac.name() + " đang dùng mã thô làm nhãn");
            assertTrue(nhan.add(thaoTac.getNhan()),
                    "Nhãn trùng: " + thaoTac.getNhan());
        }
    }

    @Test
    void nhanHienThiBangTiengViet() {
        assertEquals("Thêm hộ", ThaoTac.THEM_HO.getNhan());
        assertEquals("Sửa nhân khẩu", ThaoTac.SUA_NHAN_KHAU.getNhan());
        assertEquals("Thêm hộ", ThaoTac.THEM_HO.toString());
    }

    @Test
    void tuMaTraDungHangVaBaoLoiKhiSai() {
        for (ThaoTac thaoTac : ThaoTac.values()) {
            assertSame(thaoTac, ThaoTac.tuMa(thaoTac.name()));
        }
        assertThrows(IllegalArgumentException.class,
                () -> ThaoTac.tuMa("XOA_TAT_CA"));
        assertThrows(IllegalArgumentException.class,
                () -> ThaoTac.tuMa(null));
    }
}
