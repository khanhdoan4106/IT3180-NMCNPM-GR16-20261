package com.bluemoon.controller;

import com.bluemoon.dao.HoGiaDinhDaoJdbc;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.NhanKhauDaoJdbc;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.YeuCauThemNhanKhau;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NhanKhauService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Điều khiển màn hình quản lý nhân khẩu ({@code nhan-khau.fxml}).
 */
public class NhanKhauController {

    private static final String NAM = "Nam";
    private static final String NU = "Nữ";
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final HoGiaDinhService hoService;
    private final NhanKhauService nkService;

    @FXML private Label thongBaoLoi;
    @FXML private Label thongBaoThanhCong;
    @FXML private ComboBox<HoGiaDinh> oHoGiaDinh;

    @FXML private TextField oHoTen;
    @FXML private DatePicker oNgaySinh;
    @FXML private ComboBox<String> oGioiTinh;
    @FXML private ComboBox<String> oQuanHe;
    @FXML private TextField oCccd;
    @FXML private TextField oDanToc;
    @FXML private TextField oTonGiao;
    @FXML private TextField oNgheNghiep;

    @FXML private TableView<NhanKhau> bang;
    @FXML private TableColumn<NhanKhau, String> cotHoTen;
    @FXML private TableColumn<NhanKhau, String> cotNgaySinh;
    @FXML private TableColumn<NhanKhau, String> cotGioiTinh;
    @FXML private TableColumn<NhanKhau, String> cotQuanHe;
    @FXML private TableColumn<NhanKhau, String> cotCccd;

    /** Khởi tạo controller. */
    public NhanKhauController() {
        this.hoService = new HoGiaDinhService(new HoGiaDinhDaoJdbc());
        this.nkService = new NhanKhauService(new NhanKhauDaoJdbc());
    }

    @FXML
    private void initialize() {
        thongBaoLoi.managedProperty().bind(
                thongBaoLoi.visibleProperty());
        thongBaoThanhCong.managedProperty().bind(
                thongBaoThanhCong.visibleProperty());

        oGioiTinh.setItems(FXCollections.observableArrayList(NAM, NU));
        oGioiTinh.setValue(NAM);

        oQuanHe.setItems(FXCollections.observableArrayList(
                "Vợ/Chồng", "Con", "Bố/Mẹ", "Khác"));
        oQuanHe.setValue("Con");

        cauHinhBang();
        cauHinhChonHo();
        taiLai();
    }

    private void cauHinhBang() {
        cotHoTen.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().hoTen()));
        cotNgaySinh.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().ngaySinh().format(NGAY)));
        cotGioiTinh.setCellValueFactory(
                d -> new SimpleStringProperty(
                        "NAM".equals(d.getValue().gioiTinh())
                                ? NAM : NU));
        cotQuanHe.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().quanHe()));
        cotCccd.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().soCccd() != null
                                ? d.getValue().soCccd() : ""));
    }

    private void cauHinhChonHo() {
        oHoGiaDinh.setConverter(new StringConverter<HoGiaDinh>() {
            @Override
            public String toString(HoGiaDinh ho) {
                if (ho == null) {
                    return "";
                }
                return ho.maCanHo() + " - " + ho.tenChuHo();
            }

            @Override
            public HoGiaDinh fromString(String s) {
                return null;
            }
        });

        oHoGiaDinh.getSelectionModel().selectedItemProperty()
                .addListener((ob, cu, moi) -> napBangNhanKhau(moi));
    }

    @FXML
    private void taiLai() {
        anThongBao();
        try {
            List<HoGiaDinh> dsHo = hoService.layDanhSachDangO();
            oHoGiaDinh.getItems().setAll(dsHo);
            HoGiaDinh dangChon = oHoGiaDinh.getValue();
            if (dangChon != null && !dsHo.contains(dangChon)) {
                oHoGiaDinh.setValue(null);
            } else {
                napBangNhanKhau(oHoGiaDinh.getValue());
            }
        } catch (LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    private void napBangNhanKhau(HoGiaDinh ho) {
        if (ho == null) {
            bang.getItems().clear();
            return;
        }
        try {
            List<NhanKhau> ds = nkService.layDanhSachTheoHo(ho.id());
            bang.getItems().setAll(ds);
        } catch (LoiCsdl loi) {
            bang.getItems().clear();
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void themNhanKhau() {
        HoGiaDinh ho = oHoGiaDinh.getValue();
        if (ho == null) {
            hienLoi("Vui lòng chọn một hộ gia đình.");
            return;
        }
        try {
            YeuCauThemNhanKhau yc = new YeuCauThemNhanKhau(
                    ho.id(),
                    oHoTen.getText(),
                    oNgaySinh.getValue(),
                    NAM.equals(oGioiTinh.getValue()) ? "NAM" : "NU",
                    maQuanHe(oQuanHe.getValue()),
                    oCccd.getText(),
                    oDanToc.getText(),
                    oTonGiao.getText(),
                    oNgheNghiep.getText());

            nkService.themNhanKhau(yc);
            xoaTrangForm();
            napBangNhanKhau(ho);
            hienThanhCong("Đã thêm nhân khẩu thành công.");
        } catch (IllegalArgumentException | LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void nhapLai() {
        xoaTrangForm();
        anThongBao();
    }

    private String maQuanHe(String nhan) {
        if ("Vợ/Chồng".equals(nhan)) {
            return "VO_CHONG";
        }
        if ("Con".equals(nhan)) {
            return "CON";
        }
        if ("Bố/Mẹ".equals(nhan)) {
            return "BO_ME";
        }
        return "KHAC";
    }

    private void xoaTrangForm() {
        oHoTen.clear();
        oNgaySinh.setValue(null);
        oGioiTinh.setValue(NAM);
        oQuanHe.setValue("Con");
        oCccd.clear();
        oDanToc.clear();
        oTonGiao.clear();
        oNgheNghiep.clear();
    }

    private void anThongBao() {
        thongBaoLoi.setVisible(false);
        thongBaoThanhCong.setVisible(false);
    }

    private void hienLoi(String tb) {
        thongBaoThanhCong.setVisible(false);
        thongBaoLoi.setText(tb);
        thongBaoLoi.setVisible(true);
    }

    private void hienThanhCong(String tb) {
        thongBaoLoi.setVisible(false);
        thongBaoThanhCong.setText(tb);
        thongBaoThanhCong.setVisible(true);
    }
}