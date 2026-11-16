package com.bluemoon;

import com.bluemoon.config.KetNoiCsdl;
import com.bluemoon.dao.LoiCsdl;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Ứng dụng JavaFX BlueMoon. Khi mở, thử kết nối CSDL trước (AC-070); không
 * kết nối được thì báo lỗi bằng tiếng Việt rồi thoát.
 */
public class App extends Application {

    /** Tên ứng dụng trên thanh tiêu đề cửa sổ. */
    static final String TEN_UNG_DUNG = "BlueMoon – Quản lý chung cư";

    /** Số giây chờ khi kiểm tra kết nối còn dùng được. */
    private static final int GIAY_CHO_KIEM_TRA = 5;

    /** Bề rộng hộp thoại báo lỗi, tính bằng điểm ảnh. */
    private static final double RONG_HOP_THOAI = 520;

    private static final double RONG_CUA_SO = 1440;
    private static final double CAO_CUA_SO = 1024;
    private static final double RONG_TOI_THIEU = 1120;
    private static final double CAO_TOI_THIEU = 645;
    private static final double CO_CHU_GOC = 16;

    /** Tệp CSS dùng chung cho mọi màn hình. */
    private static final String KIEU_DUNG_CHUNG = App.class
            .getResource("css/giao-dien.css").toExternalForm();

    private static final System.Logger NHAT_KY =
            System.getLogger(App.class.getName());

    @Override
    public void start(Stage cuaSo) throws IOException {
        napPhongChu();
        try {
            kiemTraKetNoi();
        } catch (LoiCsdl loi) {
            baoLoiVaThoat(loi);
            return;
        }
        Parent cuaSoChinh = FXMLLoader.load(
                App.class.getResource("view/cua-so-chinh.fxml"));
        Scene canh = new Scene(cuaSoChinh);
        canh.getStylesheets().add(KIEU_DUNG_CHUNG);
        cuaSo.setTitle(TEN_UNG_DUNG);
        cuaSo.setScene(canh);
        // Theo ui.md: màn hình chính 1440×1024, nhỏ lại cho vừa vùng dùng
        // được của màn hình máy (kể cả khung cửa sổ, không đè thanh tác
        // vụ); mọi màn phải dùng được ở 1120×645.
        Rectangle2D manHinhMay = Screen.getPrimary().getVisualBounds();
        cuaSo.setWidth(Math.min(RONG_CUA_SO, manHinhMay.getWidth()));
        cuaSo.setHeight(Math.min(CAO_CUA_SO, manHinhMay.getHeight()));
        cuaSo.setMinWidth(RONG_TOI_THIEU);
        cuaSo.setMinHeight(CAO_TOI_THIEU);
        cuaSo.centerOnScreen();
        cuaSo.show();
    }

    /** Nạp phông Be Vietnam Pro; CSS chỉ dùng hai độ đậm này (ui.md). */
    private static void napPhongChu() {
        for (String tep : new String[] {"BeVietnamPro-Regular.ttf",
                "BeVietnamPro-Bold.ttf"}) {
            Font.loadFont(App.class.getResource("fonts/" + tep)
                    .toExternalForm(), CO_CHU_GOC);
        }
    }

    /**
     * Mở một kết nối và kiểm tra máy chủ còn trả lời.
     *
     * @throws LoiCsdl khi không kết nối được, kèm câu báo cho người dùng
     */
    static void kiemTraKetNoi() {
        try (Connection ketNoi = KetNoiCsdl.moKetNoi()) {
            if (!ketNoi.isValid(GIAY_CHO_KIEM_TRA)) {
                throw new SQLException("Máy chủ không trả lời", "08000");
            }
        } catch (SQLException e) {
            throw LoiCsdl.tu(e);
        }
    }

    private static void baoLoiVaThoat(LoiCsdl loi) {
        // Lỗi gốc chỉ ghi ở mức DEBUG để gỡ lỗi, không hiện cho người dùng.
        NHAT_KY.log(System.Logger.Level.DEBUG, "Không kết nối được CSDL",
                loi.getCause());
        Alert hopThoai = new Alert(Alert.AlertType.ERROR, "",
                new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE));
        hopThoai.setTitle(TEN_UNG_DUNG);
        hopThoai.setHeaderText("Không kết nối được cơ sở dữ liệu");
        // Câu báo dài: tự xuống dòng thay vì bị cắt "...".
        Label noiDung = new Label(loi.getMessage());
        noiDung.setWrapText(true);
        hopThoai.getDialogPane().setContent(noiDung);
        hopThoai.getDialogPane().setPrefWidth(RONG_HOP_THOAI);
        hopThoai.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        hopThoai.getDialogPane().getStylesheets().add(KIEU_DUNG_CHUNG);
        hopThoai.showAndWait();
        Platform.exit();
    }
}
