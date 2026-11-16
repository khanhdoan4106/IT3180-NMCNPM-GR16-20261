package com.bluemoon.util;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Nạp màn hình vào vùng thân của cửa sổ chính theo {@link ManHinh}. Màn
 * hình chưa có tệp FXML thì hiện trang "Chức năng đang được xây dựng".
 */
public final class DieuHuong {

    /** Thư mục chứa tệp FXML trong resources. */
    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";

    private final Pane vungThan;

    /**
     * Tạo bộ điều hướng cho một vùng thân.
     *
     * @param vungThan vùng hiện màn hình; nội dung cũ được thay khi mở
     */
    public DieuHuong(Pane vungThan) {
        this.vungThan = vungThan;
    }

    /**
     * Mở một màn hình, thay màn hình đang hiện.
     *
     * @param manHinh màn hình cần mở
     * @throws IllegalStateException khi thiếu hoặc lỗi tệp FXML (lỗi lập
     *         trình, cần sửa mã)
     */
    public void mo(ManHinh manHinh) {
        Node noiDung = manHinh.getTepFxml() == null
                ? trangDangXayDung(manHinh)
                : napFxml(manHinh.getTepFxml());
        vungThan.getChildren().setAll(noiDung);
    }

    private static Node napFxml(String tep) {
        URL viTri = DieuHuong.class.getResource(THU_MUC_VIEW + tep);
        if (viTri == null) {
            throw new IllegalStateException("Không có tệp view/" + tep);
        }
        try {
            return FXMLLoader.load(viTri);
        } catch (IOException e) {
            throw new IllegalStateException("Không nạp được view/" + tep, e);
        }
    }

    private static Node trangDangXayDung(ManHinh manHinh) {
        Label tieuDe = new Label(manHinh.getTen());
        tieuDe.getStyleClass().add("tieu-de-trang");
        Label loiNhan = new Label("Chức năng này đang được xây dựng.");
        Label ghiChu = new Label("Màn hình sẽ có trong các Sprint tới.");
        ghiChu.getStyleClass().add("chu-phu");
        VBox trang = new VBox(tieuDe, loiNhan, ghiChu);
        trang.getStyleClass().add("trang");
        return trang;
    }
}
