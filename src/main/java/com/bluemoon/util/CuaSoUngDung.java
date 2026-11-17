package com.bluemoon.util;

import com.bluemoon.service.PhienDangNhap;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Cửa sổ duy nhất của ứng dụng, chuyển giữa màn đăng nhập và cửa sổ chính
 * (US-021). Ứng dụng mở lên là màn đăng nhập; chỉ sau khi đăng nhập đúng
 * mới vào cửa sổ chính.
 */
public final class CuaSoUngDung {

    /** Kích thước theo ui.md: đăng nhập 1120×645, màn hình chính 1440×1024. */
    private static final double RONG_DANG_NHAP = 1120;
    private static final double CAO_DANG_NHAP = 645;
    private static final double RONG_CHINH = 1440;
    private static final double CAO_CHINH = 1024;

    private static Stage cuaSo;
    private static String kieuDungChung;
    /** Câu báo chờ hiện trên màn đăng nhập (giả định 17: bị khóa). */
    private static String cauBaoDangNhap;

    private CuaSoUngDung() {
    }

    /**
     * Gắn cửa sổ của ứng dụng, gọi một lần khi khởi động.
     *
     * @param stage cửa sổ JavaFX
     * @param tepCss đường dẫn CSS dùng chung
     */
    public static void gan(Stage stage, String tepCss) {
        cuaSo = stage;
        kieuDungChung = tepCss;
        cuaSo.setMinWidth(RONG_DANG_NHAP);
        cuaSo.setMinHeight(CAO_DANG_NHAP);
    }

    /** Hiện màn đăng nhập (khi mở ứng dụng, khi đăng xuất). */
    public static void hienDangNhap() {
        hien("dang-nhap.fxml", RONG_DANG_NHAP, CAO_DANG_NHAP);
    }

    /**
     * Hiện màn đăng nhập kèm một câu báo, vd tài khoản vừa bị khóa nên bị
     * đăng xuất (giả định 17).
     *
     * @param cauBao câu báo tiếng Việt
     */
    public static void hienDangNhap(String cauBao) {
        cauBaoDangNhap = cauBao;
        hienDangNhap();
    }

    /**
     * Lấy câu báo chờ cho màn đăng nhập rồi xóa đi (chỉ hiện một lần).
     *
     * @return câu báo, {@code null} nếu không có
     */
    public static String layCauBaoDangNhap() {
        String cau = cauBaoDangNhap;
        cauBaoDangNhap = null;
        return cau;
    }

    /** Hiện cửa sổ chính sau khi đăng nhập đúng (AC-057, AC-059). */
    public static void hienCuaSoChinh() {
        if (!PhienDangNhap.daDangNhap()) {
            hienDangNhap();
            return;
        }
        hien("cua-so-chinh.fxml", RONG_CHINH, CAO_CHINH);
    }

    private static void hien(String tep, double rong, double cao) {
        URL viTri = CuaSoUngDung.class.getResource(
                "/com/bluemoon/view/" + tep);
        Parent goc;
        try {
            goc = FXMLLoader.load(viTri);
        } catch (IOException e) {
            throw new IllegalStateException("Không nạp được view/" + tep, e);
        }
        Scene canh = cuaSo.getScene();
        if (canh == null) {
            canh = new Scene(goc);
            canh.getStylesheets().add(kieuDungChung);
            cuaSo.setScene(canh);
        } else {
            canh.setRoot(goc);
        }
        // Kích thước kể cả khung cửa sổ, nhỏ lại cho vừa vùng dùng được của
        // màn hình máy (không đè thanh tác vụ).
        Rectangle2D manHinhMay = Screen.getPrimary().getVisualBounds();
        cuaSo.setWidth(Math.min(rong, manHinhMay.getWidth()));
        cuaSo.setHeight(Math.min(cao, manHinhMay.getHeight()));
        cuaSo.centerOnScreen();
        cuaSo.show();
    }
}
