package com.bluemoon.controller;

import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.service.DangNhapService;
import com.bluemoon.service.PhienDangNhap;
import com.bluemoon.util.CuaSoUngDung;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.ManHinh;
import com.bluemoon.util.NhomMenu;
import java.util.EnumMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Cửa sổ chính ({@code cua-so-chinh.fxml}): dựng menu trái từ
 * {@link ManHinh}, mở màn hình được chọn vào vùng thân và tô đậm mục
 * đang mở. Thanh đầu hiện người đang đăng nhập và nút Đăng xuất (US-021).
 */
public class CuaSoChinhController {

    @FXML
    private VBox menuTrai;
    @FXML
    private StackPane vungThan;
    @FXML
    private Label nguoiDangNhap;
    @FXML
    private Label vaiTroDangNhap;

    private final DangNhapService dangNhapService =
            new DangNhapService(new TaiKhoanDaoJdbc());

    private DieuHuong dieuHuong;
    private final Map<ManHinh, Button> nutMenu =
            new EnumMap<>(ManHinh.class);

    @FXML
    private void initialize() {
        dieuHuong = new DieuHuong(vungThan, CuaSoUngDung::hienDangNhap);
        PhienDangNhap.nguoiDung().ifPresent(this::hienNguoiDung);
        taoMenu();
        mo(ManHinh.TRANG_CHINH);
        // Tiêu điểm đầu tiên ở menu, không rơi vào nút Đăng xuất.
        Platform.runLater(nutMenu.get(ManHinh.TRANG_CHINH)::requestFocus);
    }

    @FXML
    private void moTrangChinh() {
        mo(ManHinh.TRANG_CHINH);
    }

    private void hienNguoiDung(NguoiDung nguoiDung) {
        nguoiDangNhap.setText(nguoiDung.hoTen());
        vaiTroDangNhap.setText(nguoiDung.vaiTro().getTen());
    }

    @FXML
    private void dangXuat() {
        dangNhapService.dangXuat();
        CuaSoUngDung.hienDangNhap();
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
        if (!PhienDangNhap.daDangNhap()) {
            return;
        }
        nutMenu.values().forEach(n -> n.getStyleClass().remove("dang-mo"));
        nutMenu.get(manHinh).getStyleClass().add("dang-mo");
    }
}
