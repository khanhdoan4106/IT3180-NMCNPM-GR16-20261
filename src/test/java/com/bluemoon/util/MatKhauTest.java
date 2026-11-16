package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Băm BCrypt cùng chuẩn với database/seed.sql. */
class MatKhauTest {

    /** Mật khẩu băm của admin trong seed.sql (mật khẩu gốc Admin@123). */
    private static final String BAM_ADMIN_SEED =
            "$2a$12$a/XOhCEY60CX9B/MclBSt.lsuUfKkP5zsjSKMMVOeiEnhNhKQNjCC";

    @Test
    void khopMatKhauAdminTrongSeed() {
        assertTrue(MatKhau.khop("Admin@123", BAM_ADMIN_SEED));
        assertFalse(MatKhau.khop("admin@123", BAM_ADMIN_SEED));
    }

    @Test
    void bamCungChuanVoiSeed() {
        String bam = MatKhau.bam("MatKhau@1");
        assertTrue(bam.startsWith("$2a$12$"), bam);
        assertTrue(bam.length() == BAM_ADMIN_SEED.length(), bam);
        assertTrue(MatKhau.khop("MatKhau@1", bam));
        assertFalse(MatKhau.khop("MatKhau@2", bam));
    }

    @Test
    void moiLanBamMotMuoiKhac() {
        assertNotEquals(MatKhau.bam("MatKhau@1"), MatKhau.bam("MatKhau@1"));
    }

    @Test
    void matKhauCoDauVaMatKhauDai() {
        String coDau = "Mật khẩu có dấu";
        assertTrue(MatKhau.khop(coDau, MatKhau.bam(coDau)));
        String dai = "Đ".repeat(64);
        assertTrue(MatKhau.khop(dai, MatKhau.bam(dai)));
    }
}
