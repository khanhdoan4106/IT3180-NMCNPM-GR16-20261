package com.bluemoon.util;

import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Hộp thoại cùng giao diện với cửa sổ chính (docs/design/ui.md): gắn CSS
 * dùng chung, nút "Đóng" tiếng Việt, câu báo dài tự xuống dòng.
 */
public final class HopThoai {

    /** CSS dùng chung, cùng tệp cửa sổ chính đang dùng (xem App). */
    static final String TEP_CSS = "/com/bluemoon/css/giao-dien.css";

    /** Tên trên thanh tiêu đề hộp thoại, trùng tên cửa sổ chính. */
    static final String TEN_UNG_DUNG = "BlueMoon – Quản lý chung cư";

    /** Bề rộng hộp thoại báo, tính bằng điểm ảnh. */
    private static final double RONG = 520;

    private HopThoai() {
    }

    /**
     * Gắn CSS dùng chung cho hộp thoại; gọi trước khi hiện. Gọi nhiều lần
     * không gắn trùng.
     *
     * @param hopThoai hộp thoại hoặc {@link Alert} cần gắn
     */
    public static void ganKieu(Dialog<?> hopThoai) {
        String css = duongDanCss();
        DialogPane khung = hopThoai.getDialogPane();
        if (!khung.getStylesheets().contains(css)) {
            khung.getStylesheets().add(css);
        }
    }

    /**
     * Hiện hộp thoại báo lỗi và chờ người dùng bấm "Đóng".
     *
     * @param tieuDe câu ngắn nói việc gì không làm được
     * @param noiDung câu báo nói rõ sai ở đâu và cách sửa (ui.md)
     */
    public static void baoLoi(String tieuDe, String noiDung) {
        hien(Alert.AlertType.ERROR, tieuDe, noiDung);
    }

    /**
     * Hiện hộp thoại báo đã làm xong và chờ người dùng bấm "Đóng".
     *
     * @param tieuDe câu ngắn nói việc đã xong
     * @param noiDung chi tiết, có thể rỗng
     */
    public static void baoThanhCong(String tieuDe, String noiDung) {
        hien(Alert.AlertType.INFORMATION, tieuDe, noiDung);
    }

    /**
     * Đường dẫn CSS dùng chung dạng URL để gắn vào stylesheet.
     *
     * @return URL của tệp CSS
     * @throws IllegalStateException khi thiếu tệp CSS (lỗi đóng gói)
     */
    static String duongDanCss() {
        URL viTri = HopThoai.class.getResource(TEP_CSS);
        if (viTri == null) {
            throw new IllegalStateException("Không có tệp " + TEP_CSS);
        }
        return viTri.toExternalForm();
    }

    private static void hien(Alert.AlertType loai, String tieuDe,
            String noiDung) {
        Alert hopThoai = new Alert(loai, "",
                new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE));
        hopThoai.setTitle(TEN_UNG_DUNG);
        hopThoai.setHeaderText(tieuDe);
        Label chu = new Label(noiDung == null ? "" : noiDung);
        chu.setWrapText(true);
        hopThoai.getDialogPane().setContent(chu);
        hopThoai.getDialogPane().setPrefWidth(RONG);
        hopThoai.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ganKieu(hopThoai);
        hopThoai.showAndWait();
    }
}
