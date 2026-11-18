package com.bluemoon.util;

import com.bluemoon.model.VaiTro;
import com.bluemoon.service.PhienDangNhap;
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
 * Mọi màn hình đều mở qua đây nên chỉ cần chặn chưa đăng nhập (AC-059) và
 * chặn màn vai trò không được mở (US-022, {@link ManHinh#duocMo}) ở một
 * chỗ.
 */
public final class DieuHuong {

    /** Thư mục chứa tệp FXML trong resources. */
    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";

    private final Pane vungThan;
    private final Runnable khiChuaDangNhap;

    /**
     * Tạo bộ điều hướng cho một vùng thân.
     *
     * @param vungThan vùng hiện màn hình; nội dung cũ được thay khi mở
     * @param khiChuaDangNhap việc làm khi mở màn hình mà chưa đăng nhập,
     *        thường là về màn đăng nhập (AC-059)
     */
    public DieuHuong(Pane vungThan, Runnable khiChuaDangNhap) {
        this.vungThan = vungThan;
        this.khiChuaDangNhap = khiChuaDangNhap;
    }

    /**
     * Mở một màn hình, thay màn hình đang hiện. Chưa đăng nhập thì không
     * mở mà chạy việc {@code khiChuaDangNhap}. Vai trò không được mở màn
     * đó (US-022) thì mở Trang chính kèm một dòng báo.
     *
     * @param manHinh màn hình cần mở
     * @return màn hình thật sự đang hiện; {@code null} khi chưa đăng nhập
     * @throws IllegalStateException khi thiếu hoặc lỗi tệp FXML (lỗi lập
     *         trình, cần sửa mã)
     */
    public ManHinh mo(ManHinh manHinh) {
        // AC-059: chưa đăng nhập thì không mở màn hình chức năng nào.
        if (!PhienDangNhap.daDangNhap()) {
            vungThan.getChildren().clear();
            khiChuaDangNhap.run();
            return null;
        }
        VaiTro vaiTro = PhienDangNhap.nguoiDung().orElseThrow().vaiTro();
        if (!manHinh.duocMo(vaiTro)) {
            Node trangChinh = noiDung(ManHinh.TRANG_CHINH);
            if (trangChinh instanceof Pane trang) {
                trang.getChildren().add(0, baoKhongCoQuyen(manHinh, vaiTro));
            }
            vungThan.getChildren().setAll(trangChinh);
            return ManHinh.TRANG_CHINH;
        }
        vungThan.getChildren().setAll(noiDung(manHinh));
        return manHinh;
    }

    /**
     * Câu báo khi vai trò không được mở màn hình (US-022).
     *
     * @param manHinh màn hình bị từ chối
     * @param vaiTro vai trò người đang đăng nhập
     * @return câu báo tiếng Việt
     */
    static String cauKhongCoQuyen(ManHinh manHinh, VaiTro vaiTro) {
        return "Tài khoản " + vaiTro.getTen() + " không được mở “"
                + manHinh.getTen() + "”. Đã về Trang chính.";
    }

    private static Label baoKhongCoQuyen(ManHinh manHinh, VaiTro vaiTro) {
        Label bao = new Label(cauKhongCoQuyen(manHinh, vaiTro));
        bao.getStyleClass().add("thong-bao-loi");
        bao.setWrapText(true);
        return bao;
    }

    private static Node noiDung(ManHinh manHinh) {
        return manHinh.getTepFxml() == null
                ? trangDangXayDung(manHinh)
                : napFxml(manHinh.getTepFxml());
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
