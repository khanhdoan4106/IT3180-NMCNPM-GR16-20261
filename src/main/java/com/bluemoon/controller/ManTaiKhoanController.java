package com.bluemoon.controller;

import com.bluemoon.model.VaiTro;
import com.bluemoon.service.PhienDangNhap;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Màn Tài khoản ({@code tai-khoan.fxml}): chứa các tab. Tab "Quản lý tài
 * khoản" (US-022) và "Tạo tài khoản" (US-020) chỉ Quản trị viên thấy (giả
 * định 2, 18, AC-061); TaiKhoanService vẫn kiểm lại vai trò ở mỗi thao tác.
 */
public class ManTaiKhoanController {

    @FXML
    private TabPane cacTab;
    @FXML
    private Tab tabQuanLy;
    @FXML
    private Tab tabTaoTaiKhoan;
    @FXML
    private QuanLyTaiKhoanController quanLyController;

    @FXML
    private void initialize() {
        boolean laQuanTri = PhienDangNhap.nguoiDung()
                .map(n -> n.vaiTro() == VaiTro.QUAN_TRI).orElse(false);
        if (!laQuanTri) {
            cacTab.getTabs().removeAll(tabQuanLy, tabTaoTaiKhoan);
            return;
        }
        // Mở tab Quản lý thì đọc lại danh sách (vd vừa tạo tài khoản ở
        // tab Tạo tài khoản).
        tabQuanLy.setOnSelectionChanged(e -> {
            if (tabQuanLy.isSelected()) {
                quanLyController.napDanhSach();
            }
        });
    }
}
