package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
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
 * Menu chỉ hiện màn vai trò được mở; trước mỗi lần mở màn hình đọc lại
 * tài khoản từ CSDL (US-022, giả định 17).
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
        // US-023: sửa họ tên thì thanh đầu đổi theo ngay; US-022: đổi vai
        // trò thì menu đổi theo ngay.
        PhienDangNhap.khiDoiNguoiDung(nguoiDung -> {
            hienNguoiDung(nguoiDung);
            taoMenu();
        });
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

    /** Dựng menu trái: chỉ các màn vai trò hiện tại được mở (US-022). */
    private void taoMenu() {
        menuTrai.getChildren().clear();
        nutMenu.clear();
        VaiTro vaiTro = PhienDangNhap.nguoiDung().map(NguoiDung::vaiTro)
                .orElse(null);
        NhomMenu nhomTruoc = null;
        for (ManHinh manHinh : ManHinh.values()) {
            if (!manHinh.duocMo(vaiTro)) {
                continue;
            }
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
        // Giả định 17: đọc lại tài khoản trước khi mở. Bị khóa thì đăng
        // xuất; đổi vai trò thì menu, thanh đầu đã đổi qua khiDoiNguoiDung.
        try {
            if (dangNhapService.lamMoiPhien()
                    == DangNhapService.KetQuaPhien.DA_KET_THUC) {
                CuaSoUngDung.hienDangNhap(
                        DangNhapService.BI_KHOA_KHI_DANG_DUNG);
                return;
            }
        } catch (LoiCsdl loi) {
            // Không đọc được CSDL: giữ phiên, màn hình tự báo lỗi CSDL;
            // Service vẫn kiểm quyền theo phiên hiện tại.
        }
        ManHinh daMo = dieuHuong.mo(manHinh);
        if (daMo == null) {
            return;
        }
        nutMenu.values().forEach(n -> n.getStyleClass().remove("dang-mo"));
        Button nut = nutMenu.get(daMo);
        if (nut != null) {
            nut.getStyleClass().add("dang-mo");
        }
    }
}
