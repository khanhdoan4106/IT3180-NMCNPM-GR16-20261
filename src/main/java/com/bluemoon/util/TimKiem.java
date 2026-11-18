package com.bluemoon.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Tìm kiếm không phân biệt hoa thường và dấu (giả định 40 trong
 * docs/requirements/11-chot-va-gia-dinh.md): gõ "nguyen" tìm ra "Nguyễn".
 */
public final class TimKiem {

    /** Dấu thanh, dấu mũ... còn lại sau khi tách chữ theo NFD. */
    private static final Pattern DAU = Pattern.compile("\\p{M}+");

    private static final Pattern KHOANG_TRANG = Pattern.compile("\\s+");

    private TimKiem() {
    }

    /**
     * Bỏ dấu tiếng Việt và đưa về chữ thường; "đ", "Đ" thành "d".
     *
     * @param chuoi chữ cần bỏ dấu, có thể {@code null}
     * @return vd "Nguyễn Đức" thành "nguyen duc"; chuỗi rỗng khi
     *         {@code null}
     */
    public static String boDau(String chuoi) {
        if (chuoi == null) {
            return "";
        }
        String tach = Normalizer.normalize(chuoi, Normalizer.Form.NFD);
        return DAU.matcher(tach).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'd')
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Giá trị có chứa từ khóa không, bỏ qua hoa thường, dấu và khoảng trắng
     * thừa.
     *
     * @param giaTri chữ trong dữ liệu (vd họ tên, mã căn hộ)
     * @param tuKhoa chữ người dùng gõ vào ô tìm kiếm
     * @return {@code true} khi từ khóa rỗng (hiện tất cả) hoặc giá trị chứa
     *         từ khóa
     */
    public static boolean khop(String giaTri, String tuKhoa) {
        String khoa = gon(boDau(tuKhoa));
        if (khoa.isEmpty()) {
            return true;
        }
        return gon(boDau(giaTri)).contains(khoa);
    }

    private static String gon(String chuoi) {
        return KHOANG_TRANG.matcher(chuoi.strip()).replaceAll(" ");
    }
}
