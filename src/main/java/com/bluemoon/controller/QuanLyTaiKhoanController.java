package com.bluemoon.controller;

import com.bluemoon.dao.LoiCsdl;
import com.bluemoon.dao.TaiKhoanDaoJdbc;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.LoiKhongCoQuyen;
import com.bluemoon.service.LoiNghiepVu;
import com.bluemoon.service.PhienDangNhap;
import com.bluemoon.service.TaiKhoanService;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Tab "Quản lý tài khoản" ({@code quan-ly-tai-khoan.fxml}, US-022, giả
 * định 18): Quản trị viên xem mọi tài khoản, đổi vai trò, khóa/mở khóa và
 * đặt lại mật khẩu tạm cho tài khoản khác. Việc đổi vai trò, khóa và đặt
 * lại mật khẩu đều hỏi xác nhận trước.
 */
public class QuanLyTaiKhoanController {

    private static final String KIEU_LOI = "thong-bao-loi";
    private static final String KIEU_THANH_CONG = "thong-bao-thanh-cong";
    private static final String TIEU_DE = "BlueMoon – Quản lý chung cư";
    /** Bề rộng hộp thoại, tính bằng điểm ảnh. */
    private static final double RONG_HOP_THOAI = 520;

    private final TaiKhoanService taiKhoanService =
            new TaiKhoanService(new TaiKhoanDaoJdbc());

    @FXML
    private TableView<TaiKhoan> bangTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, String> cotTenDangNhap;
    @FXML
    private TableColumn<TaiKhoan, String> cotHoTen;
    @FXML
    private TableColumn<TaiKhoan, String> cotVaiTro;
    @FXML
    private TableColumn<TaiKhoan, String> cotTrangThai;
    @FXML
    private Label dangChon;
    @FXML
    private ComboBox<VaiTro> vaiTroMoi;
    @FXML
    private Button nutDoiVaiTro;
    @FXML
    private Button nutKhoa;
    @FXML
    private Button nutDatLaiMatKhau;
    @FXML
    private Label thongBao;

    @FXML
    private void initialize() {
        cot(cotTenDangNhap, TaiKhoan::tenDangNhap);
        cot(cotHoTen, TaiKhoan::hoTen);
        cot(cotVaiTro, tk -> tk.vaiTro().getTen());
        cot(cotTrangThai, QuanLyTaiKhoanController::trangThai);
        bangTaiKhoan.setPlaceholder(new Label("Chưa có tài khoản nào."));
        vaiTroMoi.getItems().setAll(VaiTro.values());
        thongBao.managedProperty().bind(thongBao.visibleProperty());
        bangTaiKhoan.getSelectionModel().selectedItemProperty()
                .addListener((o, cu, moi) -> capNhatThaoTac(moi));
        // Chỉ bật "Đổi vai trò" khi đã chọn vai trò khác vai trò hiện tại.
        vaiTroMoi.valueProperty().addListener((o, cu, moi) -> {
            TaiKhoan tk = chon();
            nutDoiVaiTro.setDisable(vaiTroMoi.isDisabled() || tk == null
                    || moi == null || moi == tk.vaiTro());
        });
        capNhatThaoTac(null);
    }

    /** Đọc lại danh sách từ CSDL, giữ dòng đang chọn nếu còn. */
    void napDanhSach() {
        Integer idDangChon = Optional.ofNullable(chon())
                .map(TaiKhoan::id).orElse(null);
        try {
            bangTaiKhoan.getItems().setAll(
                    taiKhoanService.danhSachTaiKhoan());
        } catch (LoiKhongCoQuyen | LoiCsdl loi) {
            bangTaiKhoan.getItems().clear();
            hienThongBao(loi.getMessage(), KIEU_LOI);
            return;
        }
        if (idDangChon != null) {
            chonTheoId(idDangChon);
        }
    }

    @FXML
    private void doiVaiTro() {
        TaiKhoan tk = chon();
        VaiTro moi = vaiTroMoi.getValue();
        if (tk == null || moi == null || moi == tk.vaiTro()) {
            return;
        }
        if (!xacNhan("Đổi vai trò của “" + tk.tenDangNhap() + "”?",
                tk.hoTen() + " sẽ từ " + tk.vaiTro().getTen() + " thành "
                        + moi.getTen() + ". Menu của người này đổi theo ở "
                        + "lần mở màn hình kế tiếp.", "Đổi vai trò")) {
            return;
        }
        lam(() -> {
            TaiKhoan sau = taiKhoanService.doiVaiTro(tk.id(), moi);
            return "Đã đổi vai trò của “" + sau.tenDangNhap() + "” thành "
                    + sau.vaiTro().getTen() + ".";
        }, tk.id());
    }

    @FXML
    private void khoaHoacMoKhoa() {
        TaiKhoan tk = chon();
        if (tk == null) {
            return;
        }
        boolean khoa = tk.hoatDong();
        if (khoa && !xacNhan("Khóa tài khoản “" + tk.tenDangNhap() + "”?",
                tk.hoTen() + " sẽ không đăng nhập được cho tới khi được mở "
                        + "khóa. Nếu người này đang dùng, họ bị đăng xuất ở "
                        + "lần mở màn hình kế tiếp.", "Khóa tài khoản")) {
            return;
        }
        lam(() -> {
            TaiKhoan sau = taiKhoanService.khoaTaiKhoan(tk.id(), khoa);
            return (khoa ? "Đã khóa" : "Đã mở khóa") + " tài khoản “"
                    + sau.tenDangNhap() + "”.";
        }, tk.id());
    }

    @FXML
    private void datLaiMatKhau() {
        TaiKhoan tk = chon();
        if (tk == null) {
            return;
        }
        if (!xacNhan("Đặt lại mật khẩu của “" + tk.tenDangNhap() + "”?",
                "Mật khẩu hiện tại của " + tk.hoTen() + " sẽ không dùng "
                        + "được nữa. Hệ thống tạo một mật khẩu tạm và chỉ "
                        + "hiện một lần.", "Đặt lại mật khẩu")) {
            return;
        }
        String[] matKhauTam = new String[1];
        lam(() -> {
            matKhauTam[0] = taiKhoanService.datLaiMatKhau(tk.id());
            return "Đã đặt lại mật khẩu của “" + tk.tenDangNhap() + "”.";
        }, tk.id());
        if (matKhauTam[0] != null) {
            hienMatKhauTam(tk, matKhauTam[0]);
        }
    }

    /** Một thao tác trả về câu báo thành công. */
    @FunctionalInterface
    private interface ThaoTac {
        String lam();
    }

    private void lam(ThaoTac thaoTac, int idChon) {
        thongBao.setVisible(false);
        try {
            String cau = thaoTac.lam();
            napDanhSach();
            chonTheoId(idChon);
            hienThongBao(cau, KIEU_THANH_CONG);
        } catch (LoiNghiepVu | LoiKhongCoQuyen | LoiCsdl loi) {
            napDanhSach();
            chonTheoId(idChon);
            hienThongBao(loi.getMessage(), KIEU_LOI);
        }
    }

    /** Bật, tắt và đổi chữ các nút theo tài khoản đang chọn. */
    private void capNhatThaoTac(TaiKhoan tk) {
        int toi = PhienDangNhap.nguoiDung().map(NguoiDung::id).orElse(-1);
        boolean khongChon = tk == null;
        boolean laToi = !khongChon && tk.id() == toi;
        if (khongChon) {
            dangChon.setText("Chọn một tài khoản trong danh sách để đổi vai "
                    + "trò, khóa hoặc đặt lại mật khẩu.");
        } else if (laToi) {
            dangChon.setText("“" + tk.tenDangNhap() + "” là tài khoản bạn "
                    + "đang dùng: không tự đổi vai trò hay tự khóa. Đổi mật "
                    + "khẩu ở tab “Tài khoản của tôi”.");
        } else {
            dangChon.setText("Đang chọn: " + tk.tenDangNhap() + " – "
                    + tk.hoTen() + ".");
        }
        boolean tat = khongChon || laToi;
        vaiTroMoi.setDisable(tat);
        nutKhoa.setDisable(tat);
        nutDatLaiMatKhau.setDisable(tat);
        vaiTroMoi.setValue(khongChon ? null : tk.vaiTro());
        nutDoiVaiTro.setDisable(true);
        boolean daKhoa = !khongChon && !tk.hoatDong();
        nutKhoa.setText(daKhoa ? "Mở khóa tài khoản" : "Khóa tài khoản");
        nutKhoa.getStyleClass().remove("nut-nguy-hiem");
        if (!daKhoa) {
            nutKhoa.getStyleClass().add("nut-nguy-hiem");
        }
    }

    private boolean xacNhan(String tieuDe, String noiDung, String nutLam) {
        ButtonType lam = new ButtonType(nutLam, ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Hủy",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert hopThoai = new Alert(Alert.AlertType.CONFIRMATION, "", lam,
                huy);
        dungKieu(hopThoai, tieuDe, new Label(noiDung));
        return hopThoai.showAndWait().filter(lam::equals).isPresent();
    }

    /**
     * Hiện mật khẩu tạm một lần (giả định 18). Ô chỉ đọc để chọn, chép
     * được; đóng hộp thoại là không xem lại được nữa.
     */
    private void hienMatKhauTam(TaiKhoan tk, String matKhauTam) {
        Alert hopThoai = new Alert(Alert.AlertType.INFORMATION, "",
                new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE));
        TextField o = new TextField(matKhauTam);
        o.setEditable(false);
        o.setId("matKhauTam");
        Label huongDan = new Label("Báo mật khẩu này cho " + tk.hoTen()
                + " và nhắc họ đổi mật khẩu ngay sau khi đăng nhập (tab "
                + "“Tài khoản của tôi”). Mật khẩu chỉ hiện một lần, đóng "
                + "hộp thoại là không xem lại được.");
        VBox noiDung = new VBox(12, o, huongDan);
        dungKieu(hopThoai, "Mật khẩu tạm của “" + tk.tenDangNhap() + "”",
                noiDung);
        hopThoai.setOnShown(e -> o.selectAll());
        hopThoai.showAndWait();
    }

    /** Hộp thoại cùng giao diện với cửa sổ chính, câu dài tự xuống dòng. */
    private void dungKieu(Alert hopThoai, String tieuDe,
            javafx.scene.Node noiDung) {
        hopThoai.setTitle(TIEU_DE);
        hopThoai.setHeaderText(tieuDe);
        if (noiDung instanceof Label nhan) {
            nhan.setWrapText(true);
        } else if (noiDung instanceof VBox hop) {
            hop.getChildren().stream().filter(Label.class::isInstance)
                    .forEach(n -> ((Label) n).setWrapText(true));
        }
        hopThoai.getDialogPane().setContent(noiDung);
        hopThoai.getDialogPane().setPrefWidth(RONG_HOP_THOAI);
        hopThoai.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (bangTaiKhoan.getScene() != null) {
            hopThoai.initOwner(bangTaiKhoan.getScene().getWindow());
            hopThoai.getDialogPane().getStylesheets()
                    .addAll(bangTaiKhoan.getScene().getStylesheets());
        }
    }

    private TaiKhoan chon() {
        return bangTaiKhoan.getSelectionModel().getSelectedItem();
    }

    private void chonTheoId(int id) {
        for (TaiKhoan tk : bangTaiKhoan.getItems()) {
            if (tk.id() == id) {
                bangTaiKhoan.getSelectionModel().select(tk);
                capNhatThaoTac(tk);
                return;
            }
        }
    }

    private static String trangThai(TaiKhoan tk) {
        return tk.hoatDong() ? "Đang hoạt động" : "Đã khóa";
    }

    private static void cot(TableColumn<TaiKhoan, String> cot,
            Function<TaiKhoan, String> giaTri) {
        cot.setCellValueFactory(o -> new ReadOnlyStringWrapper(
                giaTri.apply(o.getValue())));
    }

    private void hienThongBao(String cau, String kieu) {
        thongBao.getStyleClass().removeAll(KIEU_LOI, KIEU_THANH_CONG);
        thongBao.getStyleClass().add(kieu);
        thongBao.setText(cau);
        thongBao.setVisible(true);
    }
}
