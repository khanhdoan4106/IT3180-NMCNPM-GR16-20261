package com.bluemoon.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
import java.nio.charset.StandardCharsets;

/**
 * Băm và kiểm tra mật khẩu bằng BCrypt, cùng chuẩn với
 * {@code database/seed.sql}: tiền tố {@code $2a$}, cost 12.
 *
 * <p>BCrypt chỉ dùng 72 byte đầu của mật khẩu; phần dư bị bỏ như các bản
 * BCrypt khác, nên mật khẩu dài vẫn băm được.
 */
public final class MatKhau {

    /** Số vòng băm (2^12), như mật khẩu admin trong seed.sql. */
    public static final int COST = 12;

    private static final BCrypt.Version PHIEN_BAN = BCrypt.Version.VERSION_2A;

    private static final BCrypt.Hasher BAM = BCrypt.with(PHIEN_BAN,
            LongPasswordStrategies.truncate(PHIEN_BAN));

    private static final BCrypt.Verifyer KIEM_TRA = BCrypt.verifyer(
            PHIEN_BAN, LongPasswordStrategies.truncate(PHIEN_BAN));

    private MatKhau() {
    }

    /**
     * Băm mật khẩu, mỗi lần một muối ngẫu nhiên.
     *
     * @param matKhau mật khẩu gốc
     * @return chuỗi băm 60 ký tự, dạng {@code $2a$12$...}
     */
    public static String bam(String matKhau) {
        return BAM.hashToString(COST, matKhau.toCharArray());
    }

    /**
     * Mật khẩu có khớp chuỗi băm đã lưu không (dùng cho US-021).
     *
     * @param matKhau mật khẩu người dùng nhập
     * @param chuoiBam chuỗi băm trong cột {@code mat_khau_bam}
     * @return {@code true} nếu khớp
     */
    public static boolean khop(String matKhau, String chuoiBam) {
        return KIEM_TRA.verify(matKhau.getBytes(StandardCharsets.UTF_8),
                chuoiBam.getBytes(StandardCharsets.UTF_8)).verified;
    }
}
