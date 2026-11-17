package com.bluemoon.controller;

import com.bluemoon.model.VaiTro;
import com.bluemoon.service.PhienDangNhap;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Màn Tài khoản ({@code tai-khoan.fxml}): chứa các tab. Tab "Tạo tài
 * khoản" chỉ Quản trị viên thấy (giả định 2, AC-061); TaiKhoanService vẫn
 * kiểm lại vai trò khi tạo.
 */
public class ManTaiKhoanController {

    @FXML
    private TabPane cacTab;
    @FXML
    private Tab tabTaoTaiKhoan;

    @FXML
    private void initialize() {
        boolean laQuanTri = PhienDangNhap.nguoiDung()
                .map(n -> n.vaiTro() == VaiTro.QUAN_TRI).orElse(false);
        if (!laQuanTri) {
            cacTab.getTabs().remove(tabTaoTaiKhoan);
        }
    }
}
