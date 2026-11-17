package com.bluemoon.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Hiện diện tích căn hộ theo docs/design/ui.md: hai chữ số lẻ, dấu phẩy
 * thập phân, dấu chấm ngăn cách hàng nghìn, kèm đơn vị "m²".
 */
public final class DienTich {

    /** Đơn vị đi sau số, cách một dấu cách. */
    public static final String DON_VI = "m²";

    private DienTich() {
    }

    /**
     * Hiện diện tích, làm tròn hai chữ số lẻ (nửa lên).
     *
     * @param dienTich diện tích tính bằng m², có thể {@code null}
     * @return vd "75,50 m²", "1.234,50 m²"; chuỗi rỗng khi {@code null}
     */
    public static String dinhDang(BigDecimal dienTich) {
        if (dienTich == null) {
            return "";
        }
        return mauSo().format(dienTich.setScale(2, RoundingMode.HALF_UP))
                + " " + DON_VI;
    }

    /** DecimalFormat không an toàn đa luồng nên tạo mới mỗi lần dùng. */
    private static DecimalFormat mauSo() {
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols();
        kyHieu.setDecimalSeparator(',');
        kyHieu.setGroupingSeparator('.');
        kyHieu.setMinusSign('-');
        DecimalFormat mau = new DecimalFormat("#,##0.00", kyHieu);
        mau.setRoundingMode(RoundingMode.HALF_UP);
        return mau;
    }
}
