package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.service.LoiNhapLieu;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.service.TaiKhoanService.O;
import java.util.EnumMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Tab "Tài khoản của tôi" ({@code tai-khoan-cua-toi.fxml}, US-023): người
 * đang đăng nhập sửa họ tên, số điện thoại (AC-065) và đổi mật khẩu
 * (AC-063, AC-064). Lỗi của ô nào hiện ngay dưới ô đó; kết quả hiện một
 * dòng trên nút của khung tương ứng.
 */
public class TaiKhoanCuaToiController {

    /** Kiểu viền đỏ cho ô đang sai. */
    private static final String KIEU_O_SAI = "o-sai";
    private static final String KIEU_LOI = "thong-bao-loi";
    private static final String KIEU_THANH_CONG = "thong-bao-thanh-cong";

    private final TaiKhoanService taiKhoanService =
            new TaiKhoanService(new TaiKhoanDaoJdbc());

    @FXML
    private Label tenDangNhap;
    @FXML
    private Label vaiTro;
    @FXML
    private TextField hoTen;
    @FXML
    private TextField soDienThoai;
    @FXML
    private Label loiHoTen;
    @FXML
    private Label loiSoDienThoai;
    @FXML
    private Label thongBaoThongTin;

    @FXML
    private PasswordField matKhauCu;
    @FXML
    private PasswordField matKhauMoi;
    @FXML
    private PasswordField nhapLai;
    @FXML
    private Label goiYMatKhau;
    @FXML
    private Label loiMatKhauCu;
    @FXML
    private Label loiMatKhauMoi;
    @FXML
    private Label loiNhapLai;
    @FXML
    private Label thongBaoMatKhau;

    /** Ô và nhãn lỗi của khung Thông tin cá nhân. */
    private final Map<O, Control> oThongTin = new EnumMap<>(O.class);
    private final Map<O, Label> loiThongTin = new EnumMap<>(O.class);
    /** Ô và nhãn lỗi của khung Đổi mật khẩu. */
    private final Map<O, Control> oMatKhau = new EnumMap<>(O.class);
    private final Map<O, Label> loiMatKhau = new EnumMap<>(O.class);

    @FXML
    private void initialize() {
        goiYMatKhau.setText("Ít nhất " + TaiKhoanService.MAT_KHAU_TOI_THIEU
                + " ký tự, khác mật khẩu hiện tại.");
        oThongTin.put(O.HO_TEN, hoTen);
        oThongTin.put(O.SO_DIEN_THOAI, soDienThoai);
        loiThongTin.put(O.HO_TEN, loiHoTen);
        loiThongTin.put(O.SO_DIEN_THOAI, loiSoDienThoai);
        oMatKhau.put(O.MAT_KHAU_CU, matKhauCu);
        oMatKhau.put(O.MAT_KHAU, matKhauMoi);
        oMatKhau.put(O.NHAP_LAI_MAT_KHAU, nhapLai);
        loiMatKhau.put(O.MAT_KHAU_CU, loiMatKhauCu);
        loiMatKhau.put(O.MAT_KHAU, loiMatKhauMoi);
        loiMatKhau.put(O.NHAP_LAI_MAT_KHAU, loiNhapLai);
        // Nhãn lỗi, thông báo chỉ chiếm chỗ khi đang hiện (ui.md).
        for (Label nhan : new Label[] {loiHoTen, loiSoDienThoai,
                thongBaoThongTin, loiMatKhauCu, loiMatKhauMoi, loiNhapLai,
                thongBaoMatKhau}) {
            nhan.managedProperty().bind(nhan.visibleProperty());
        }
        napThongTin();
    }

    /** Đọc tài khoản đang đăng nhập từ CSDL và điền vào khung. */
    private void napThongTin() {
        try {
            TaiKhoan tk = taiKhoanService.taiKhoanCuaToi();
            tenDangNhap.setText(tk.tenDangNhap());
            vaiTro.setText(tk.vaiTro().getTen());
            hoTen.setText(tk.hoTen());
            soDienThoai.setText(tk.soDienThoai() == null
                    ? "" : tk.soDienThoai());
        } catch (LoiCsdl loi) {
            hienThongBao(thongBaoThongTin, loi.getMessage(), KIEU_LOI);
        }
    }

    @FXML
    private void luuThongTin() {
        xoaLoi(oThongTin, loiThongTin, thongBaoThongTin);
        try {
            taiKhoanService.capNhatThongTin(hoTen.getText(),
                    soDienThoai.getText());
            napThongTin();
            hienThongBao(thongBaoThongTin, "Đã lưu thông tin cá nhân.",
                    KIEU_THANH_CONG);
        } catch (LoiNhapLieu loi) {
            hienLoiTheoO(loi, oThongTin, loiThongTin);
        } catch (LoiCsdl loi) {
            hienThongBao(thongBaoThongTin, loi.getMessage(), KIEU_LOI);
        }
    }

    @FXML
    private void doiMatKhau() {
        xoaLoi(oMatKhau, loiMatKhau, thongBaoMatKhau);
        try {
            taiKhoanService.doiMatKhau(matKhauCu.getText(),
                    matKhauMoi.getText(), nhapLai.getText());
            matKhauCu.clear();
            matKhauMoi.clear();
            nhapLai.clear();
            hienThongBao(thongBaoMatKhau, "Đã đổi mật khẩu. Lần đăng nhập "
                    + "sau dùng mật khẩu mới.", KIEU_THANH_CONG);
        } catch (LoiNhapLieu loi) {
            hienLoiTheoO(loi, oMatKhau, loiMatKhau);
            // AC-064: mật khẩu hiện tại sai thì xóa để gõ lại.
            if (loi.loiCua(O.MAT_KHAU_CU) != null
                    && !matKhauCu.getText().isEmpty()) {
                matKhauCu.clear();
            }
        } catch (LoiCsdl loi) {
            hienThongBao(thongBaoMatKhau, loi.getMessage(), KIEU_LOI);
        }
    }

    private static void hienLoiTheoO(LoiNhapLieu loi, Map<O, Control> o,
            Map<O, Label> nhanLoi) {
        Control oDauTien = null;
        for (Map.Entry<O, Label> muc : nhanLoi.entrySet()) {
            String cau = loi.loiCua(muc.getKey());
            if (cau == null) {
                continue;
            }
            muc.getValue().setText(cau);
            muc.getValue().setVisible(true);
            Control oSai = o.get(muc.getKey());
            oSai.getStyleClass().add(KIEU_O_SAI);
            if (oDauTien == null) {
                oDauTien = oSai;
            }
        }
        if (oDauTien != null) {
            oDauTien.requestFocus();
        }
    }

    private static void xoaLoi(Map<O, Control> o, Map<O, Label> nhanLoi,
            Label thongBao) {
        for (Label nhan : nhanLoi.values()) {
            nhan.setVisible(false);
        }
        for (Control oNhap : o.values()) {
            oNhap.getStyleClass().remove(KIEU_O_SAI);
        }
        thongBao.setVisible(false);
    }

    private static void hienThongBao(Label nhan, String cau, String kieu) {
        nhan.getStyleClass().removeAll(KIEU_LOI, KIEU_THANH_CONG);
        nhan.getStyleClass().add(kieu);
        nhan.setText(cau);
        nhan.setVisible(true);
    }
}
