package com.bluemoon.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dữ liệu người dùng nhập chưa đúng. Mỗi ô sai có một câu báo riêng để
 * controller hiện ngay dưới ô đó (ui.md: lỗi nhập liệu hiện dưới ô sai).
 * Tên ô thường là một hằng enum của form.
 */
public class LoiNhapLieu extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient Map<Object, String> loiTheoO;

    /**
     * Tạo lỗi từ các ô sai.
     *
     * @param loiTheoO câu báo theo từng ô, theo thứ tự trên form
     */
    public LoiNhapLieu(Map<?, String> loiTheoO) {
        super(String.join(" ", loiTheoO.values()));
        this.loiTheoO = Collections.unmodifiableMap(
                new LinkedHashMap<>(loiTheoO));
    }

    /**
     * Câu báo của một ô.
     *
     * @param o tên ô
     * @return câu báo, {@code null} nếu ô đó đúng
     */
    public String loiCua(Object o) {
        return loiTheoO.get(o);
    }

    /**
     * Các ô sai và câu báo.
     *
     * @return bản chỉ đọc
     */
    public Map<Object, String> getLoiTheoO() {
        return loiTheoO;
    }
}
