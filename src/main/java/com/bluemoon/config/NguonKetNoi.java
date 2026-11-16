package com.bluemoon.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Nơi DAO lấy kết nối. Ứng dụng dùng {@link KetNoiCsdl#moKetNoi()};
 * kiểm thử tích hợp truyền nguồn trỏ tới CSDL kiểm thử.
 */
@FunctionalInterface
public interface NguonKetNoi {

    /**
     * Mở một kết nối mới.
     *
     * @return kết nối; người gọi tự đóng
     * @throws SQLException khi không kết nối được
     */
    Connection moKetNoi() throws SQLException;
}
