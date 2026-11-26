package com.bluemoon.controller;

import com.bluemoon.dao.HoGiaDinhDaoJdbc;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.YeuCauThemHo;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.util.DienTich;
import com.bluemoon.util.Ngay;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Điều khiển màn hình quản lý hộ gia đình ({@code ho-gia-dinh.fxml}).
 */
public class HoGiaDinhController {

    private static final String NHAN_NAM = "Nam";
    private static final String NHAN_NU = "Nữ";

   private final DateTimeFormatter dinhDangNgay;
    private final HoGiaDinhService hoGiaDinhService;

    /** Khởi tạo controller với dịch vụ hộ gia đình mặc định. */
    public HoGiaDinhController() {
        HoGiaDinhDaoJdbc dao = new HoGiaDinhDaoJdbc();
        this.hoGiaDinhService = new HoGiaDinhService(dao);
        this.dinhDangNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
    @FXML
    private Label thongBaoLoi;
    @FXML
    private Label thongBaoThanhCong;
    @FXML
    private TextField oTimKiem;
    @FXML
    private TextField oMaCanHo;
    @FXML
    private ComboBox<LoaiCanHo> oLoaiCanHo;
    @FXML
    private TextField oTang;
    @FXML
    private TextField oDienTich;
    @FXML
    private TextField oSoDienThoai;
    @FXML
    private DatePicker oNgayChuyenDen;
    @FXML
    private TextField oTenChuHo;
    @FXML
    private DatePicker oNgaySinhChuHo;
    @FXML
    private ComboBox<String> oGioiTinhChuHo;
    @FXML
    private TextField oCccdChuHo;

    @FXML
    private TableView<HoGiaDinh> bangHo;
    @FXML
    private Label bangHoTrong;
    @FXML
    private TableColumn<HoGiaDinh, String> cotMaCanHo;
    @FXML
    private TableColumn<HoGiaDinh, String> cotLoaiCanHo;
    @FXML
    private TableColumn<HoGiaDinh, Integer> cotTang;
    @FXML
    private TableColumn<HoGiaDinh, String> cotDienTich;
    @FXML
    private TableColumn<HoGiaDinh, String> cotChuHo;
    @FXML
    private TableColumn<HoGiaDinh, String> cotSoDienThoai;
    @FXML
    private TableColumn<HoGiaDinh, Integer> cotSoThanhVien;
    @FXML
    private TableColumn<HoGiaDinh, String> cotNgayChuyenDen;

    @FXML
    private void initialize() {
        thongBaoLoi.managedProperty().bind(thongBaoLoi.visibleProperty());
        thongBaoThanhCong.managedProperty()
                .bind(thongBaoThanhCong.visibleProperty());

        oLoaiCanHo.setItems(
                FXCollections.observableArrayList(LoaiCanHo.values()));
        oLoaiCanHo.setValue(LoaiCanHo.CAN_HO);

        oGioiTinhChuHo.setItems(
                FXCollections.observableArrayList(NHAN_NAM, NHAN_NU));
        oGioiTinhChuHo.setValue(NHAN_NAM);
        oNgayChuyenDen.setValue(LocalDate.now());
        Ngay.ganDinhDang(oNgaySinhChuHo);
        Ngay.ganDinhDang(oNgayChuyenDen);

        cauHinhBang();
        taiLai();
    }

    private void cauHinhBang() {
        cotMaCanHo.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().maCanHo()));
        cotLoaiCanHo.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().loaiCanHo().getNhan()));
        cotTang.setCellValueFactory(
                d -> new SimpleObjectProperty<>(d.getValue().tang()));
        cotDienTich.setCellValueFactory(
                d -> new SimpleStringProperty(
                        DienTich.dinhDang(d.getValue().dienTich())));
        cotChuHo.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().tenChuHo()));
        cotSoDienThoai.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().soDienThoai() != null
                                ? d.getValue().soDienThoai()
                                : ""));
        cotSoThanhVien.setCellValueFactory(
                d -> new SimpleObjectProperty<>(
                        d.getValue().soThanhVien()));
        cotNgayChuyenDen.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().ngayChuyenDen()
                                .format(dinhDangNgay)));
    }

    @FXML
    private void taiLai() {
        anThongBao();
        napBang();
    }

    @FXML
    private void timKiem() {
        napBang();
    }

    private void napBang() {
        try {
            String tuKhoa = oTimKiem != null ? oTimKiem.getText() : "";
            List<HoGiaDinh> danhSach = hoGiaDinhService.timKiem(tuKhoa);
            bangHoTrong.setText(tuKhoa == null || tuKhoa.isBlank()
                    ? "Chưa có hộ nào đang ở."
                    : "Không có hộ nào khớp với “" + tuKhoa.trim() + "”.");
            bangHo.getItems().setAll(danhSach);
        } catch (LoiCsdl loi) {
            bangHo.getItems().clear();
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void themHo() {
        try {
            Integer tang = docSoNguyen(oTang.getText(), "Tầng");
            BigDecimal dienTich = docSoThuc(
                    oDienTich.getText(), "Diện tích");

            YeuCauThemHo yc = new YeuCauThemHo(
                    oMaCanHo.getText(),
                    oLoaiCanHo.getValue(),
                    tang,
                    dienTich,
                    oSoDienThoai.getText(),
                    oNgayChuyenDen.getValue(),
                    oTenChuHo.getText(),
                    oNgaySinhChuHo.getValue(),
                    maGioiTinh(oGioiTinhChuHo.getValue()),
                    oCccdChuHo.getText());

            hoGiaDinhService.themHoGiaDinh(yc);
            xoaTrangForm();
            napBang();
            hienThanhCong("Đã thêm hộ gia đình thành công.");
        } catch (IllegalArgumentException | LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void nhapLai() {
        xoaTrangForm();
        anThongBao();
    }

    private String maGioiTinh(String nhan) {
        if (NHAN_NAM.equals(nhan)) {
            return "NAM";
        }
        if (NHAN_NU.equals(nhan)) {
            return "NU";
        }
        return null;
    }

    private Integer docSoNguyen(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(chuoi.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    tenTruong + " phải là số nguyên hợp lệ.");
        }
    }

    private BigDecimal docSoThuc(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(chuoi.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    tenTruong + " phải là số hợp lệ.");
        }
    }

    private void xoaTrangForm() {
        oMaCanHo.clear();
        oLoaiCanHo.setValue(LoaiCanHo.CAN_HO);
        oTang.clear();
        oDienTich.clear();
        oSoDienThoai.clear();
        oNgayChuyenDen.setValue(LocalDate.now());
        oTenChuHo.clear();
        oNgaySinhChuHo.setValue(null);
        oGioiTinhChuHo.setValue(NHAN_NAM);
        oCccdChuHo.clear();
    }

    private void anThongBao() {
        thongBaoLoi.setVisible(false);
        thongBaoThanhCong.setVisible(false);
    }

    private void hienLoi(String thongDiep) {
        thongBaoThanhCong.setVisible(false);
        thongBaoLoi.setText(thongDiep);
        thongBaoLoi.setVisible(true);
    }

    private void hienThanhCong(String thongDiep) {
        thongBaoLoi.setVisible(false);
        thongBaoThanhCong.setText(thongDiep);
        thongBaoThanhCong.setVisible(true);
    }
}