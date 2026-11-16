package com.bluemoon.controller;

import com.bluemoon.dao.CuDanDaoJdbc;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.model.SoLieuCuDan;
import com.bluemoon.service.CuDanService;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Trang chính ({@code trang-chinh.fxml}): số hộ và số nhân khẩu đang ở,
 * nút "Tải lại". Mẫu cho mọi controller: chỉ gọi Service, bắt
 * {@link LoiCsdl} và hiện câu báo của nó ở nhãn lỗi.
 */
public class TrangChinhController {

    /** Thay chữ số khi chưa đọc được dữ liệu. */
    private static final String CHUA_CO_SO = "–";

    private final CuDanService cuDanService =
            new CuDanService(new CuDanDaoJdbc());
    private final NumberFormat dinhDangSo =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    @FXML
    private Label soHo;
    @FXML
    private Label soNhanKhau;
    @FXML
    private Label thongBaoLoi;

    @FXML
    private void initialize() {
        // Nhãn lỗi chỉ chiếm chỗ khi đang hiện (ui.md).
        thongBaoLoi.managedProperty().bind(thongBaoLoi.visibleProperty());
        taiLai();
    }

    @FXML
    private void taiLai() {
        try {
            SoLieuCuDan soLieu = cuDanService.laySoLieu();
            soHo.setText(dinhDangSo.format(soLieu.soHo()));
            soNhanKhau.setText(dinhDangSo.format(soLieu.soNhanKhau()));
            thongBaoLoi.setVisible(false);
        } catch (LoiCsdl loi) {
            soHo.setText(CHUA_CO_SO);
            soNhanKhau.setText(CHUA_CO_SO);
            thongBaoLoi.setText(loi.getMessage());
            thongBaoLoi.setVisible(true);
        }
    }
}
