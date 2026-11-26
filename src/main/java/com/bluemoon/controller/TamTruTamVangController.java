package com.bluemoon.controller;

import com.bluemoon.dao.HoGiaDinhDaoJdbc;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TamTruTamVangDaoJdbc;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.TamTruTamVangService;
import com.bluemoon.util.Ngay;
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

/** Điều khiển màn hình quản lý tạm trú, tạm vắng. */
public class TamTruTamVangController {

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String CHUA_CHON_HO =
            "Vui lòng chọn một hộ gia đình ở trên.";

    private final HoGiaDinhService hoService;
    private final TamTruTamVangService ttvService;

    @FXML private Label thongBaoLoi;
    @FXML private Label thongBaoThanhCong;
    @FXML private ComboBox<HoGiaDinh> oHoGiaDinh;
    @FXML private ComboBox<NguoiTrongHo> oNhanKhau;
    @FXML private ComboBox<String> oLoaiKhaiBao;
    @FXML private TextField oSoGiay;
    @FXML private TextField oDiaChiVang;
    @FXML private DatePicker oTuNgay;
    @FXML private DatePicker oDenNgay;
    @FXML private TextField oLyDo;

    @FXML private TableView<TamTruTamVang> bang;
    @FXML private Label bangTrong;
    @FXML private TableColumn<TamTruTamVang, String> cotHoTen;
    @FXML private TableColumn<TamTruTamVang, String> cotCccd;
    @FXML private TableColumn<TamTruTamVang, String> cotLoai;
    @FXML private TableColumn<TamTruTamVang, String> cotTuNgay;
    @FXML private TableColumn<TamTruTamVang, String> cotDenNgay;
    @FXML private TableColumn<TamTruTamVang, String> cotDiaChi;
    @FXML private TableColumn<TamTruTamVang, String> cotLyDo;

    /** Khởi tạo bộ điều khiển. */
    public TamTruTamVangController() {
        this.hoService = new HoGiaDinhService(new HoGiaDinhDaoJdbc());
        this.ttvService = new TamTruTamVangService(
                new TamTruTamVangDaoJdbc());
    }

    @FXML
    private void initialize() {
        thongBaoLoi.managedProperty().bind(thongBaoLoi.visibleProperty());
        thongBaoThanhCong.managedProperty().bind(
                thongBaoThanhCong.visibleProperty());

        oLoaiKhaiBao.setItems(FXCollections.observableArrayList(
                "TAM_TRU", "TAM_VANG"));
        oLoaiKhaiBao.setConverter(new StringConverter<>() {
            @Override
            public String toString(String loai) {
                return nhanLoai(loai);
            }

            @Override
            public String fromString(String s) {
                return null;
            }
        });
        oLoaiKhaiBao.setValue("TAM_TRU");
        oLoaiKhaiBao.getSelectionModel().selectedItemProperty()
                .addListener((ob, cu, moi) -> capNhatTrangThaiForm(moi));
        capNhatTrangThaiForm("TAM_TRU");
        Ngay.ganDinhDang(oTuNgay);
        Ngay.ganDinhDang(oDenNgay);

        cauHinhBang();
        cauHinhChonHo();
        cauHinhChonNhanKhau();
        taiLai();
    }

    private void capNhatTrangThaiForm(String loai) {
        if ("TAM_TRU".equals(loai)) {
            oDiaChiVang.clear();
            oDiaChiVang.setDisable(true);
        } else {
            oDiaChiVang.setDisable(false);
        }
    }

    private void cauHinhBang() {
        cotHoTen.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().hoTen()));
        cotCccd.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().soCccd()));
        cotLoai.setCellValueFactory(
                d -> new SimpleStringProperty(
                        nhanLoai(d.getValue().loai())));
        cotTuNgay.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().tuNgay().format(NGAY)));
        cotDenNgay.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().denNgay().format(NGAY)));
        cotDiaChi.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().diaChiTamVang()));
        cotLyDo.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().lyDo()));
    }

    private static String nhanLoai(String loai) {
        if ("TAM_TRU".equals(loai)) {
            return "Tạm trú";
        }
        return "TAM_VANG".equals(loai) ? "Tạm vắng" : "";
    }

    private void cauHinhChonHo() {
        oHoGiaDinh.setConverter(new StringConverter<>() {
            @Override
            public String toString(HoGiaDinh ho) {
                return ho == null ? ""
                        : ho.maCanHo() + " - " + ho.tenChuHo();
            }

            @Override
            public HoGiaDinh fromString(String s) {
                return null;
            }
        });
        oHoGiaDinh.getSelectionModel().selectedItemProperty()
                .addListener((ob, cu, moi) -> {
                    napBangKhaiBao(moi);
                    taiDanhSachNhanKhau(moi);
                });
    }

    private void cauHinhChonNhanKhau() {
        oNhanKhau.setConverter(new StringConverter<>() {
            @Override
            public String toString(NguoiTrongHo nk) {
                if (nk == null) {
                    return "";
                }
                String cccd = nk.soCccd() == null
                        ? "Không CCCD" : nk.soCccd();
                return nk.hoTen() + " (" + cccd + ")";
            }

            @Override
            public NguoiTrongHo fromString(String s) {
                return null;
            }
        });
    }

    @FXML
    private void taiLai() {
        anThongBao();
        try {
            List<HoGiaDinh> dsHo = hoService.layDanhSachDangO();
            oHoGiaDinh.getItems().setAll(dsHo);
            oHoGiaDinh.setValue(null);
            oNhanKhau.getItems().clear();
        } catch (LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    private void taiDanhSachNhanKhau(HoGiaDinh ho) {
        if (ho == null) {
            oNhanKhau.getItems().clear();
            return;
        }
        try {
            List<NguoiTrongHo> dsNk = ttvService.layNguoiTrongHo(ho.id());
            oNhanKhau.getItems().setAll(dsNk);
        } catch (LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    private void napBangKhaiBao(HoGiaDinh ho) {
        if (ho == null) {
            bang.getItems().clear();
            bangTrong.setText(CHUA_CHON_HO);
            return;
        }
        try {
            List<TamTruTamVang> ds = ttvService.layDanhSachTheoHo(ho.id());
            bang.getItems().setAll(ds);
            bangTrong.setText(
                    "Hộ này chưa có đợt tạm trú, tạm vắng nào.");
        } catch (LoiCsdl loi) {
            bang.getItems().clear();
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void themKhaiBao() {
        NguoiTrongHo nk = oNhanKhau.getValue();
        if (nk == null) {
            hienLoi("Vui lòng chọn nhân khẩu trước.");
            return;
        }
        try {
            String soGiay = oSoGiay.getText().isBlank()
                    ? null : oSoGiay.getText().trim();
            String lyDo = oLyDo.getText().isBlank()
                    ? null : oLyDo.getText().trim();
            String diaChi = oDiaChiVang.getText().isBlank()
                    ? null : oDiaChiVang.getText().trim();

            YeuCauKhaiBao yc = new YeuCauKhaiBao(
                    nk.id(),
                    oLoaiKhaiBao.getValue(),
                    soGiay,
                    oTuNgay.getValue(),
                    oDenNgay.getValue(),
                    lyDo,
                    diaChi);

            ttvService.themKhaiBao(yc);
            xoaTrangForm();
            napBangKhaiBao(oHoGiaDinh.getValue());
            hienThanhCong("Đã thêm khai báo thành công.");
        } catch (IllegalArgumentException | LoiCsdl loi) {
            hienLoi(loi.getMessage());
        }
    }

    @FXML
    private void nhapLai() {
        xoaTrangForm();
        anThongBao();
    }

    private void xoaTrangForm() {
        oNhanKhau.setValue(null);
        oLoaiKhaiBao.setValue("TAM_TRU");
        oSoGiay.clear();
        oTuNgay.setValue(null);
        oDenNgay.setValue(null);
        oLyDo.clear();
        oDiaChiVang.clear();
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