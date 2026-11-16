package com.bluemoon.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Mở kết nối tới CSDL MySQL theo tệp {@code config/db.properties} (khóa
 * {@code url}, {@code user}, {@code password}). Tệp này nằm ngoài git;
 * mẫu ở {@code config/db.properties.example}. Mật khẩu không viết trong mã.
 */
public final class KetNoiCsdl {

    /** Đường dẫn tệp cấu hình, tính từ thư mục chạy ứng dụng. */
    public static final Path TEP_CAU_HINH = Path.of("config", "db.properties");

    private KetNoiCsdl() {
    }

    /**
     * Mở một kết nối mới theo tệp cấu hình mặc định.
     *
     * @return kết nối mới; người gọi tự đóng
     * @throws SQLException khi thiếu cấu hình hoặc không kết nối được
     */
    public static Connection moKetNoi() throws SQLException {
        Properties cauHinh = docCauHinh(TEP_CAU_HINH);
        return DriverManager.getConnection(cauHinh.getProperty("url"),
                cauHinh.getProperty("user"),
                cauHinh.getProperty("password"));
    }

    /**
     * Đọc tệp cấu hình kết nối.
     *
     * @param tep đường dẫn tệp {@code .properties}
     * @return cấu hình có đủ {@code url} và {@code user}
     * @throws SQLException khi thiếu tệp, không đọc được hoặc thiếu khóa
     */
    public static Properties docCauHinh(Path tep) throws SQLException {
        Properties cauHinh = new Properties();
        try (InputStream vao = Files.newInputStream(tep)) {
            cauHinh.load(vao);
        } catch (IOException e) {
            throw new SQLException("Thiếu hoặc không đọc được tệp cấu hình "
                    + tep + "; sao chép từ db.properties.example", e);
        }
        for (String khoa : new String[] {"url", "user"}) {
            String giaTri = cauHinh.getProperty(khoa);
            if (giaTri == null || giaTri.isBlank()) {
                throw new SQLException("Tệp " + tep + " thiếu khóa " + khoa);
            }
        }
        return cauHinh;
    }
}
