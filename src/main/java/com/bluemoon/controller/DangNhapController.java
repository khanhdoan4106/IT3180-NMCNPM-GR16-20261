package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.service.DangNhapService;
import com.bluemoon.service.DangNhapService.O;
import com.bluemoon.service.LoiDangNhap;
import com.bluemoon.service.LoiNhapLieu;
import com.bluemoon.util.CuaSoUngDung;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Màn đăng nhập ({@code dang-nhap.fxml}, US-021). Đúng thì vào cửa sổ
 * chính; sai thì báo một câu chung, xóa mật khẩu để gõ lại.
 */
public class DangNhapController {

    /** Kiểu viền đỏ cho ô đang sai. */
    private static final String KIEU_O_SAI = "o-sai";

    private final DangNhapService dangNhapService =
            new DangNhapService(new TaiKhoanDaoJdbc());

    @FXML
    private TextField tenDangNhap;
    @FXML
    private PasswordField matKhau;
    @FXML
    private Label loiTenDangNhap;
    @FXML
    private Label loiMatKhau;
    @FXML
    private Label thongBao;

    @FXML
    private void initialize() {
        // Nhãn lỗi chỉ chiếm chỗ khi đang hiện (ui.md).
        for (Label nhan : new Label[] {loiTenDangNhap, loiMatKhau,
                thongBao}) {
            nhan.managedProperty().bind(nhan.visibleProperty());
        }
        // Giả định 17: vừa bị đăng xuất vì tài khoản bị khóa.
        String cauBao = CuaSoUngDung.layCauBaoDangNhap();
        if (cauBao != null) {
            hienThongBao(cauBao);
        }
        Platform.runLater(tenDangNhap::requestFocus);
    }

    @FXML
    private void dangNhap() {
        xoaThongBao();
        try {
            dangNhapService.dangNhap(tenDangNhap.getText(),
                    matKhau.getText());
            CuaSoUngDung.hienCuaSoChinh();
        } catch (LoiNhapLieu loi) {
            hienLoiO(tenDangNhap, loiTenDangNhap,
                    loi.loiCua(O.TEN_DANG_NHAP));
            hienLoiO(matKhau, loiMatKhau, loi.loiCua(O.MAT_KHAU));
            (loi.loiCua(O.TEN_DANG_NHAP) != null ? tenDangNhap : matKhau)
                    .requestFocus();
        } catch (LoiDangNhap loi) {
            // Không nói sai tên hay sai mật khẩu (AC-058); gõ lại mật khẩu.
            hienThongBao(loi.getMessage());
            matKhau.clear();
            matKhau.requestFocus();
        } catch (LoiCsdl loi) {
            hienThongBao(loi.getMessage());
        }
    }

    private static void hienLoiO(TextField o, Label nhan, String cau) {
        if (cau == null) {
            return;
        }
        nhan.setText(cau);
        nhan.setVisible(true);
        o.getStyleClass().add(KIEU_O_SAI);
    }

    private void hienThongBao(String cau) {
        thongBao.setText(cau);
        thongBao.setVisible(true);
    }

    private void xoaThongBao() {
        loiTenDangNhap.setVisible(false);
        loiMatKhau.setVisible(false);
        thongBao.setVisible(false);
        tenDangNhap.getStyleClass().remove(KIEU_O_SAI);
        matKhau.getStyleClass().remove(KIEU_O_SAI);
    }
}
