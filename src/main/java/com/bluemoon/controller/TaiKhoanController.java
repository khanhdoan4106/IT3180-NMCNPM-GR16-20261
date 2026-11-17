package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.LoiKhongCoQuyen;
import com.bluemoon.service.LoiNhapLieu;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.service.TaiKhoanService.O;
import java.util.EnumMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

/**
 * Tab "Tạo tài khoản" của màn Tài khoản ({@code tao-tai-khoan.fxml},
 * US-020). Lỗi của ô nào hiện ngay dưới ô đó; lỗi CSDL và kết quả hiện
 * ở nhãn thông báo trên hàng nút.
 */
public class TaiKhoanController {

    /** Kiểu viền đỏ cho ô đang sai. */
    private static final String KIEU_O_SAI = "o-sai";
    /** Kiểu câu gợi ý dưới ô; ẩn khi ô đó đang báo lỗi cho khỏi lặp ý. */
    private static final String KIEU_GOI_Y = "chu-goi-y";

    private final TaiKhoanService taiKhoanService =
            new TaiKhoanService(new TaiKhoanDaoJdbc());

    @FXML
    private TextField tenDangNhap;
    @FXML
    private TextField hoTen;
    @FXML
    private ComboBox<VaiTro> vaiTro;
    @FXML
    private PasswordField matKhau;
    @FXML
    private PasswordField nhapLai;
    @FXML
    private Label goiYMatKhau;
    @FXML
    private Label loiTenDangNhap;
    @FXML
    private Label loiHoTen;
    @FXML
    private Label loiVaiTro;
    @FXML
    private Label loiMatKhau;
    @FXML
    private Label loiNhapLai;
    @FXML
    private Label thongBao;
    @FXML
    private ScrollPane cuonForm;

    private final Map<O, Control> oNhap = new EnumMap<>(O.class);
    private final Map<O, Label> nhanLoi = new EnumMap<>(O.class);

    @FXML
    private void initialize() {
        vaiTro.getItems().setAll(VaiTro.values());
        goiYMatKhau.setText("Ít nhất " + TaiKhoanService.MAT_KHAU_TOI_THIEU
                + " ký tự.");
        oNhap.put(O.TEN_DANG_NHAP, tenDangNhap);
        oNhap.put(O.HO_TEN, hoTen);
        oNhap.put(O.VAI_TRO, vaiTro);
        oNhap.put(O.MAT_KHAU, matKhau);
        oNhap.put(O.NHAP_LAI_MAT_KHAU, nhapLai);
        nhanLoi.put(O.TEN_DANG_NHAP, loiTenDangNhap);
        nhanLoi.put(O.HO_TEN, loiHoTen);
        nhanLoi.put(O.VAI_TRO, loiVaiTro);
        nhanLoi.put(O.MAT_KHAU, loiMatKhau);
        nhanLoi.put(O.NHAP_LAI_MAT_KHAU, loiNhapLai);
        // Nhãn lỗi, thông báo chỉ chiếm chỗ khi đang hiện (ui.md).
        for (Label nhan : nhanLoi.values()) {
            nhan.managedProperty().bind(nhan.visibleProperty());
        }
        thongBao.managedProperty().bind(thongBao.visibleProperty());
    }

    @FXML
    private void taoTaiKhoan() {
        xoaThongBao();
        try {
            TaiKhoanMoi tk = taiKhoanService.taoTaiKhoan(
                    tenDangNhap.getText(), hoTen.getText(), vaiTro.getValue(),
                    matKhau.getText(), nhapLai.getText());
            hienThongBao("Đã tạo tài khoản “" + tk.tenDangNhap() + "” cho "
                    + tk.hoTen() + ", vai trò " + tk.vaiTro().getTen()
                    + ".", "thong-bao-thanh-cong");
            tenDangNhap.clear();
            hoTen.clear();
            matKhau.clear();
            nhapLai.clear();
            tenDangNhap.requestFocus();
        } catch (LoiNhapLieu loi) {
            hienLoiTheoO(loi);
        } catch (LoiKhongCoQuyen | LoiCsdl loi) {
            hienThongBao(loi.getMessage(), "thong-bao-loi");
        }
    }

    private void hienLoiTheoO(LoiNhapLieu loi) {
        Control oDauTien = null;
        for (O o : O.values()) {
            String cau = loi.loiCua(o);
            if (cau == null) {
                continue;
            }
            Label nhan = nhanLoi.get(o);
            nhan.setText(cau);
            nhan.setVisible(true);
            oNhap.get(o).getStyleClass().add(KIEU_O_SAI);
            hienGoiY(oNhap.get(o), false);
            if (oDauTien == null) {
                oDauTien = oNhap.get(o);
            }
        }
        if (oDauTien != null) {
            oDauTien.requestFocus();
            // Ô nhập, gợi ý và câu báo lỗi nằm chung một khối (o-form).
            cuonToi(oDauTien.getParent());
        }
    }

    /**
     * Cuộn phần ô nhập cho một khối hiện trọn trong khung nhìn (ScrollPane
     * không tự cuộn tới ô có tiêu điểm).
     */
    private void cuonToi(Node khoi) {
        Node noiDung = cuonForm.getContent();
        cuonForm.applyCss();
        cuonForm.layout();
        double caoNoiDung = noiDung.getBoundsInLocal().getHeight();
        double caoKhung = cuonForm.getViewportBounds().getHeight();
        double thua = caoNoiDung - caoKhung;
        if (thua <= 0) {
            return;
        }
        Bounds b = noiDung.sceneToLocal(
                khoi.localToScene(khoi.getBoundsInLocal()));
        double dinh = cuonForm.getVvalue() * thua;
        if (b.getMinY() < dinh) {
            dinh = b.getMinY();
        } else if (b.getMaxY() > dinh + caoKhung) {
            dinh = Math.min(b.getMinY(), b.getMaxY() - caoKhung);
        }
        cuonForm.setVvalue(Math.max(0, Math.min(1, dinh / thua)));
    }

    private void xoaThongBao() {
        for (Label nhan : nhanLoi.values()) {
            nhan.setVisible(false);
        }
        for (Control o : oNhap.values()) {
            o.getStyleClass().remove(KIEU_O_SAI);
            hienGoiY(o, true);
        }
        thongBao.setVisible(false);
    }

    /** Hiện hoặc ẩn câu gợi ý nằm cùng khối với ô nhập. */
    private static void hienGoiY(Control o, boolean hien) {
        for (Node con : o.getParent().getChildrenUnmodifiable()) {
            if (con.getStyleClass().contains(KIEU_GOI_Y)) {
                con.setVisible(hien);
                con.setManaged(hien);
            }
        }
    }

    private void hienThongBao(String cau, String kieu) {
        thongBao.getStyleClass().removeAll("thong-bao-loi",
                "thong-bao-thanh-cong");
        thongBao.getStyleClass().add(kieu);
        thongBao.setText(cau);
        thongBao.setVisible(true);
    }
}
