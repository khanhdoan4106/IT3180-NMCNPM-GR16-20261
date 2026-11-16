package com.bluemoon;

import javafx.application.Application;

/**
 * Điểm vào của ứng dụng. Lớp này không kế thừa {@link Application}, nên
 * chạy được bằng nút Run của IntelliJ trên classpath thường mà không gặp
 * lỗi "JavaFX runtime components are missing".
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Khởi động giao diện JavaFX.
     *
     * @param args tham số dòng lệnh, chuyển nguyên cho JavaFX
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
