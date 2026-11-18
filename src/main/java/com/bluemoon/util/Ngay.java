package com.bluemoon.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/**
 * Hiện và đọc ngày theo dạng {@code dd/MM/yyyy} (docs/design/ui.md, mục
 * "Cách viết chữ hiển thị"). Dùng chung cho mọi màn hình, không tự viết lại
 * định dạng ngày trong Controller hay Service.
 */
public final class Ngay {

    /** Dạng ngày hiện cho người dùng. */
    public static final String MAU = "dd/MM/yyyy";

    /** Chữ gợi ý của ô ngày còn trống (ui.md). */
    public static final String GOI_Y = "ngày/tháng/năm";

    private static final DateTimeFormatter HIEN = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Đọc ngày: nhận cả "5/9/2026" lẫn "05/09/2026"; ngày không có thật
     * (vd 31/02/2026) bị từ chối.
     */
    private static final DateTimeFormatter DOC = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Ngay() {
    }

    /**
     * Hiện ngày dạng {@code dd/MM/yyyy}.
     *
     * @param ngay ngày cần hiện, có thể {@code null}
     * @return vd "05/09/2026"; chuỗi rỗng khi {@code ngay} là {@code null}
     */
    public static String dinhDang(LocalDate ngay) {
        return ngay == null ? "" : HIEN.format(ngay);
    }

    /**
     * Đọc ngày người dùng gõ, bỏ khoảng trắng hai đầu.
     *
     * @param chuoi chữ người dùng gõ, dạng {@code dd/MM/yyyy}
     * @return ngày đọc được
     * @throws IllegalArgumentException khi trống, sai dạng hoặc ngày không
     *         có thật; câu báo tiếng Việt hiện được cho người dùng
     */
    public static LocalDate doc(String chuoi) {
        String gon = chuoi == null ? "" : chuoi.strip();
        if (gon.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nhập ngày theo dạng ngày/tháng/năm, vd 05/09/2026.");
        }
        try {
            return LocalDate.parse(gon, DOC);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày “" + gon
                    + "” không đúng. Nhập theo dạng ngày/tháng/năm, "
                    + "vd 05/09/2026.", e);
        }
    }

    /**
     * Bộ chuyển đổi ngày cho {@link DatePicker}. Ô trống cho ngày
     * {@code null}; chữ sai dạng ném {@link DateTimeParseException} để
     * {@link DatePicker} trả lại ngày cũ thay vì báo lỗi luồng giao diện.
     *
     * @return bộ chuyển đổi dạng {@code dd/MM/yyyy}
     */
    public static StringConverter<LocalDate> boChuyenDoi() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate ngay) {
                return dinhDang(ngay);
            }

            @Override
            public LocalDate fromString(String chuoi) {
                if (chuoi == null || chuoi.isBlank()) {
                    return null;
                }
                return LocalDate.parse(chuoi.strip(), DOC);
            }
        };
    }

    /**
     * Gắn dạng {@code dd/MM/yyyy} cho ô ngày, kèm chữ gợi ý
     * "ngày/tháng/năm" nếu ô chưa có gợi ý.
     *
     * @param oNgay ô ngày trên màn hình
     */
    public static void ganDinhDang(DatePicker oNgay) {
        oNgay.setConverter(boChuyenDoi());
        if (oNgay.getPromptText() == null
                || oNgay.getPromptText().isEmpty()) {
            oNgay.setPromptText(GOI_Y);
        }
    }
}
