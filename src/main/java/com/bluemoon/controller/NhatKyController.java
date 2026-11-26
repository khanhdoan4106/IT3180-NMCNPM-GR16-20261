package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.NhatKyDaoJdbc;
import com.bluemoon.model.NhatKy;
import com.bluemoon.service.LoiKhongCoQuyen;
import com.bluemoon.service.NhatKyService;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Màn hình xem nhật ký thao tác, chỉ Quản trị viên (TS-002). */
public class NhatKyController {

    private static final DateTimeFormatter DINH_DANG_THOI_DIEM =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final NhatKyService nhatKyService =
            new NhatKyService(new NhatKyDaoJdbc());

    @FXML
    private TableView<NhatKy> bangNhatKy;
    @FXML
    private TableColumn<NhatKy, String> cotThoiDiem;
    @FXML
    private TableColumn<NhatKy, String> cotNguoiDung;
    @FXML
    private TableColumn<NhatKy, String> cotThaoTac;
    @FXML
    private TableColumn<NhatKy, String> cotDoiTuong;
    @FXML
    private TableColumn<NhatKy, String> cotNoiDung;
    @FXML
    private Label thongBaoLoi;

    @FXML
    private void initialize() {
        cotThoiDiem.setCellValueFactory(
                cell -> javafx.beans.binding.Bindings.createStringBinding(
                        () -> cell.getValue().thoiDiem()
                                .format(DINH_DANG_THOI_DIEM)));
        cotNguoiDung.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().tenDangNhap()));
        cotThaoTac.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().thaoTac().getNhan()));
        cotDoiTuong.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().doiTuong()));
        cotNoiDung.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().noiDung()));
        thongBaoLoi.managedProperty().bind(
                thongBaoLoi.visibleProperty());
        taiLai();
    }

    @FXML
    private void taiLai() {
        try {
            bangNhatKy.setItems(FXCollections.observableArrayList(
                    nhatKyService.xemMoiNhat()));
            thongBaoLoi.setVisible(false);
        } catch (LoiKhongCoQuyen | LoiCsdl loi) {
            bangNhatKy.getItems().clear();
            thongBaoLoi.setText(loi.getMessage());
            thongBaoLoi.setVisible(true);
        }
    }
}
