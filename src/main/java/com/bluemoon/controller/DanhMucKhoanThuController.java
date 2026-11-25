package com.bluemoon.controller;

import com.bluemoon.dao.KhoanPhiDaoJdbc;
import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.YeuCauTaoKhoanTuNguyen;
import com.bluemoon.service.KhoanPhiService;
import com.bluemoon.service.LoiKhongCoQuyen;
import com.bluemoon.service.LoiNghiepVu;
import com.bluemoon.service.LoiNhapLieu;
import com.bluemoon.util.Ngay;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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
 * Điều khiển màn hình Danh mục khoản thu, tính phí tự động và tạo khoản thu
 * tự nguyện (US-005, US-006, {@code danh-muc-khoan-thu.fxml}).
 */
public class DanhMucKhoanThuController {

    private final KhoanPhiService khoanPhiService;

    @FXML
    private Label thongBaoLoi;
    @FXML
    private Label thongBaoThanhCong;

    @FXML
    private ComboBox<KhoanPhi> cboKhoanPhiBatBuoc;
    @FXML
    private Label lblDonGiaHienTai;
    @FXML
    private TextField oDonGiaMoi;

    @FXML
    private TextField oDienTichTinhThu;
    @FXML
    private Label lblKetQuaPhiDichVu;
    @FXML
    private Label lblKetQuaPhiQuanLy;
    @FXML
    private Label lblTongCongPhi;

    @FXML
    private TextField oTenKhoanThu;
    @FXML
    private ComboBox<CachTinhPhi> oCachTinh;
    @FXML
    private TextField oDonGia;
    @FXML
    private TextField oGhiChu;
    @FXML
    private DatePicker oTuNgay;
    @FXML
    private DatePicker oDenNgay;

    @FXML
    private TextField oTimKiem;
    @FXML
    private TableView<KhoanPhi> bangKhoanThu;
    @FXML
    private TableColumn<KhoanPhi, String> cotTen;
    @FXML
    private TableColumn<KhoanPhi, String> cotLoai;
    @FXML
    private TableColumn<KhoanPhi, String> cotCachTinh;
    @FXML
    private TableColumn<KhoanPhi, String> cotDonGia;
    @FXML
    private TableColumn<KhoanPhi, String> cotTuNgay;
    @FXML
    private TableColumn<KhoanPhi, String> cotDenNgay;
    @FXML
    private TableColumn<KhoanPhi, String> cotTrangThai;
    @FXML
    private TableColumn<KhoanPhi, String> cotGhiChu;

    /**
     * Khởi tạo controller với service mặc định.
     */
    public DanhMucKhoanThuController() {
        this(new KhoanPhiService(new KhoanPhiDaoJdbc()));
    }

    /**
     * Khởi tạo controller với service được chỉ định (dùng trong kiểm thử).
     *
     * @param khoanPhiService dịch vụ nghiệp vụ khoản phí
     */
    public DanhMucKhoanThuController(KhoanPhiService khoanPhiService) {
        this.khoanPhiService = khoanPhiService;
    }

    @FXML
    private void initialize() {
        thongBaoLoi.managedProperty().bind(thongBaoLoi.visibleProperty());
        thongBaoThanhCong.managedProperty().bind(
                thongBaoThanhCong.visibleProperty());

        // Khoản tự nguyện chỉ áp dụng cố định theo hộ hoặc tùy tâm đóng góp
        oCachTinh.setItems(FXCollections.observableArrayList(
                CachTinhPhi.KHONG_CO_DINH, CachTinhPhi.CO_DINH_THEO_HO));
        oCachTinh.setValue(CachTinhPhi.KHONG_CO_DINH);
        oDonGia.setDisable(true);

        oCachTinh.valueProperty().addListener((obs, cu, moi) -> {
            boolean khongCoDinh = (moi == CachTinhPhi.KHONG_CO_DINH);
            oDonGia.setDisable(khongCoDinh);
            if (khongCoDinh) {
                oDonGia.clear();
            }
        });

        Ngay.ganDinhDang(oTuNgay);
        Ngay.ganDinhDang(oDenNgay);
        oTuNgay.setValue(LocalDate.now());

        cboKhoanPhiBatBuoc.setConverter(new StringConverter<>() {
            @Override
            public String toString(KhoanPhi kp) {
                return kp != null ? kp.ten() : "";
            }

            @Override
            public KhoanPhi fromString(String string) {
                return null;
            }
        });

        cboKhoanPhiBatBuoc.valueProperty().addListener((obs, cu, moi) -> {
            if (moi != null && moi.donGia() != null) {
                lblDonGiaHienTai.setText(String.format(
                        Locale.forLanguageTag("vi-VN"), "%,.0f đ/m²",
                        moi.donGia()));
            } else {
                lblDonGiaHienTai.setText("-");
            }
        });

        bangKhoanThu.getSelectionModel().selectedItemProperty()
                .addListener((obs, cu, moi) -> {
                    if (moi != null
                            && moi.loai() == LoaiKhoanPhi.BAT_BUOC) {
                        for (KhoanPhi item : cboKhoanPhiBatBuoc.getItems()) {
                            if (item.id() == moi.id()) {
                                cboKhoanPhiBatBuoc.setValue(item);
                                break;
                            }
                        }
                    }
                });

        cauHinhBang();
        taiLai();
    }

    private void cauHinhBang() {
        cotTen.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().ten()));
        cotLoai.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().loai().getTen()));
        cotCachTinh.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().cachTinh().getTen()));
        cotDonGia.setCellValueFactory(d ->
                new SimpleStringProperty(dinhDangDonGia(d.getValue())));
        cotTuNgay.setCellValueFactory(d ->
                new SimpleStringProperty(Ngay.dinhDang(d.getValue().tuNgay())));
        cotDenNgay.setCellValueFactory(d -> {
            LocalDate den = d.getValue().denNgay();
            return new SimpleStringProperty(den == null
                    ? "-" : Ngay.dinhDang(den));
        });
        cotTrangThai.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().trangThai().getTen()));
        cotGhiChu.setCellValueFactory(d -> {
            String gc = d.getValue().ghiChu();
            return new SimpleStringProperty(gc == null ? "" : gc);
        });
    }

    private String dinhDangDonGia(KhoanPhi kp) {
        BigDecimal dg = kp.donGia();
        if (dg == null) {
            return "-";
        }
        if (kp.loai() == LoaiKhoanPhi.BAT_BUOC
                && kp.cachTinh() == CachTinhPhi.THEO_DIEN_TICH) {
            return String.format("%,.0f đ/m²", dg);
        }
        return String.format("%,.0f đ/hộ", dg);
    }

    /**
     * Tải lại danh sách khoản thu từ CSDL và hiển thị lên bảng.
     */
    @FXML
    public void taiLai() {
        anThongBao();
        napBang();
    }

    /**
     * Tìm kiếm khoản thu theo từ khóa gõ vào ô tìm kiếm.
     */
    @FXML
    public void timKiem() {
        napBang();
    }

    private void napBang() {
        try {
            String tuKhoa = oTimKiem != null ? oTimKiem.getText() : "";
            List<KhoanPhi> danhSach = khoanPhiService.timKiem(tuKhoa);
            bangKhoanThu.getItems().setAll(danhSach);

            List<KhoanPhi> batBuoc = danhSach.stream()
                    .filter(k -> k.loai() == LoaiKhoanPhi.BAT_BUOC)
                    .toList();
            KhoanPhi dangChon = cboKhoanPhiBatBuoc.getValue();
            cboKhoanPhiBatBuoc.setItems(
                    FXCollections.observableArrayList(batBuoc));
            if (dangChon != null) {
                for (KhoanPhi item : batBuoc) {
                    if (item.id() == dangChon.id()) {
                        cboKhoanPhiBatBuoc.setValue(item);
                        break;
                    }
                }
            } else if (!batBuoc.isEmpty()) {
                cboKhoanPhiBatBuoc.setValue(batBuoc.get(0));
            }
            tinhThuPhi();
        } catch (LoiKhongCoQuyen | LoiCsdl e) {
            bangKhoanThu.getItems().clear();
            hienLoi(e.getMessage());
        }
    }

    /**
     * Cập nhật đơn giá cho khoản phí bắt buộc (US-005, AC-016).
     */
    @FXML
    public void capNhatDonGia() {
        try {
            anThongBao();
            KhoanPhi chon = cboKhoanPhiBatBuoc.getValue();
            if (chon == null) {
                hienLoi("Vui lòng chọn khoản phí cần cập nhật đơn giá.");
                return;
            }

            BigDecimal donGiaMoi = docSoTien(oDonGiaMoi.getText());
            if (donGiaMoi == null) {
                hienLoi("Vui lòng nhập đơn giá mới.");
                return;
            }

            khoanPhiService.capNhatDonGia(chon.id(), donGiaMoi);
            oDonGiaMoi.clear();
            napBang();
            hienThanhCong("Đã cập nhật đơn giá cho " + chon.ten()
                    + " thành công.");
        } catch (LoiNhapLieu | LoiNghiepVu | LoiKhongCoQuyen | LoiCsdl e) {
            hienLoi(e.getMessage());
        }
    }

    /**
     * Tự động tính thử phí theo diện tích nhập vào (AC-015, AC-018).
     */
    @FXML
    public void tinhThuPhi() {
        String dtText = oDienTichTinhThu != null
                ? oDienTichTinhThu.getText() : null;
        if (dtText == null || dtText.isBlank()) {
            datRongKetQuaTinhThu();
            return;
        }

        try {
            BigDecimal dienTich = docSoThapPhan(dtText);
            if (dienTich == null
                    || dienTich.compareTo(BigDecimal.ZERO) <= 0) {
                datRongKetQuaTinhThu();
                return;
            }

            List<KhoanPhi> ds = bangKhoanThu.getItems();
            BigDecimal dgDichVu = BigDecimal.ZERO;
            BigDecimal dgQuanLy = BigDecimal.valueOf(7000);

            for (KhoanPhi kp : ds) {
                String ten = kp.ten().toLowerCase(Locale.ROOT);
                if (ten.contains("dịch vụ") && kp.donGia() != null) {
                    dgDichVu = kp.donGia();
                } else if (ten.contains("quản lý") && kp.donGia() != null) {
                    dgQuanLy = kp.donGia();
                }
            }

            BigDecimal phiDichVu = khoanPhiService.tinhPhi(
                    dienTich, dgDichVu);
            BigDecimal phiQuanLy = khoanPhiService.tinhPhi(
                    dienTich, dgQuanLy);
            BigDecimal tong = phiDichVu.add(phiQuanLy);

            Locale viVn = Locale.forLanguageTag("vi-VN");
            lblKetQuaPhiDichVu.setText(String.format(
                    viVn, "%,.0f đ", phiDichVu));
            lblKetQuaPhiQuanLy.setText(String.format(
                    viVn, "%,.0f đ", phiQuanLy));
            lblTongCongPhi.setText(String.format(
                    viVn, "%,.0f đ", tong));
        } catch (NumberFormatException e) {
            datRongKetQuaTinhThu();
        }
    }

    private void datRongKetQuaTinhThu() {
        if (lblKetQuaPhiDichVu != null) {
            lblKetQuaPhiDichVu.setText("-");
        }
        if (lblKetQuaPhiQuanLy != null) {
            lblKetQuaPhiQuanLy.setText("-");
        }
        if (lblTongCongPhi != null) {
            lblTongCongPhi.setText("-");
        }
    }

    private BigDecimal docSoThapPhan(String chuoi) {
        if (chuoi == null || chuoi.isBlank()) {
            return null;
        }
        String sach = chuoi.trim().replace(",", ".");
        return new BigDecimal(sach);
    }

    /**
     * Xử lý sự kiện bấm nút "Tạo khoản thu" (US-006, AC-019).
     */
    @FXML
    public void taoKhoan() {
        try {
            anThongBao();
            CachTinhPhi cachTinh = oCachTinh.getValue();
            BigDecimal donGia = null;

            if (cachTinh == CachTinhPhi.CO_DINH_THEO_HO) {
                donGia = docSoTien(oDonGia.getText());
            }

            YeuCauTaoKhoanTuNguyen yc = new YeuCauTaoKhoanTuNguyen(
                    oTenKhoanThu.getText(),
                    cachTinh,
                    donGia,
                    oTuNgay.getValue(),
                    oDenNgay.getValue(),
                    oGhiChu.getText());

            khoanPhiService.taoKhoanTuNguyen(yc);
            xoaForm();
            napBang();
            hienThanhCong("Đã tạo khoản thu tự nguyện thành công.");
        } catch (LoiNhapLieu | LoiNghiepVu | LoiKhongCoQuyen | LoiCsdl e) {
            hienLoi(e.getMessage());
        }
    }

    /**
     * Xóa trắng các ô nhập trên form tạo khoản thu.
     */
    @FXML
    public void nhapLai() {
        xoaForm();
        anThongBao();
    }

    private BigDecimal docSoTien(String chuoi) {
        if (chuoi == null || chuoi.isBlank()) {
            return null;
        }
        try {
            String sach = chuoi.trim().replace(".", "").replace(",", ".");
            return new BigDecimal(sach);
        } catch (NumberFormatException e) {
            throw new LoiNghiepVu("Số tiền không hợp lệ.");
        }
    }

    private void xoaForm() {
        oTenKhoanThu.clear();
        oCachTinh.setValue(CachTinhPhi.KHONG_CO_DINH);
        oDonGia.clear();
        oDonGia.setDisable(true);
        oGhiChu.clear();
        oTuNgay.setValue(LocalDate.now());
        oDenNgay.setValue(null);
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
