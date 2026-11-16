package com.bluemoon.controller;

import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.ManHinh;
import com.bluemoon.util.NhomMenu;
import java.util.EnumMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Cửa sổ chính ({@code cua-so-chinh.fxml}): dựng menu trái từ
 * {@link ManHinh}, mở màn hình được chọn vào vùng thân và tô đậm mục
 * đang mở.
 */
public class CuaSoChinhController {

    @FXML
    private VBox menuTrai;
    @FXML
    private StackPane vungThan;

    private DieuHuong dieuHuong;
    private final Map<ManHinh, Button> nutMenu =
            new EnumMap<>(ManHinh.class);

    @FXML
    private void initialize() {
        dieuHuong = new DieuHuong(vungThan);
        taoMenu();
        mo(ManHinh.TRANG_CHINH);
    }

    @FXML
    private void moTrangChinh() {
        mo(ManHinh.TRANG_CHINH);
    }

    private void taoMenu() {
        NhomMenu nhomTruoc = null;
        for (ManHinh manHinh : ManHinh.values()) {
            NhomMenu nhom = manHinh.getNhom();
            if (nhom != null && nhom != nhomTruoc) {
                Label tenNhom = new Label(nhom.getTen());
                tenNhom.getStyleClass().add("nhom-menu");
                menuTrai.getChildren().add(tenNhom);
                nhomTruoc = nhom;
            }
            Button nut = new Button(manHinh.getTen());
            nut.getStyleClass().add("nut-menu");
            nut.setMaxWidth(Double.MAX_VALUE);
            nut.setOnAction(e -> mo(manHinh));
            menuTrai.getChildren().add(nut);
            nutMenu.put(manHinh, nut);
        }
    }

    private void mo(ManHinh manHinh) {
        dieuHuong.mo(manHinh);
        nutMenu.values().forEach(n -> n.getStyleClass().remove("dang-mo"));
        nutMenu.get(manHinh).getStyleClass().add("dang-mo");
    }
}
