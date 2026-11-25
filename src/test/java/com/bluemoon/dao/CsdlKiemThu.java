package com.bluemoon.dao;

import com.bluemoon.config.NguonKetNoi;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSDL cho kiểm thử tích hợp ({@code *IT}, chạy bằng
 * {@code mvn verify -Pmysql}). Đọc biến môi trường:
 * {@code BLUEMOON_DB_URL}, {@code BLUEMOON_DB_USER},
 * {@code BLUEMOON_DB_PASSWORD} (user ứng dụng) và
 * {@code BLUEMOON_DB_ADMIN_USER}, {@code BLUEMOON_DB_ADMIN_PASSWORD} (user
 * được tạo bảng; thiếu thì dùng user ứng dụng).
 */
final class CsdlKiemThu {

    private static final Path LUOC_DO = Path.of("database", "schema.sql");

    private CsdlKiemThu() {
    }

    /**
     * Nguồn kết nối bằng user ứng dụng, như khi chạy thật.
     *
     * @return nguồn kết nối tới CSDL kiểm thử
     */
    static NguonKetNoi nguonUngDung() {
        return () -> DriverManager.getConnection(url(),
                bien("BLUEMOON_DB_USER"), bienTuyChon("BLUEMOON_DB_PASSWORD"));
    }

    /**
     * Tạo tài khoản Quản trị viên dùng làm người thực hiện trong test.
     *
     * @param nguon nguồn CSDL kiểm thử
     * @return người dùng có khóa ngoại hợp lệ trong bảng tai_khoan
     * @throws SQLException khi không tạo được tài khoản
     */
    static NguoiDung taoQuanTriVien(NguonKetNoi nguon)
            throws SQLException {
        String sql = "INSERT INTO tai_khoan "
                + "(ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro) "
                + "VALUES ('test-admin', 'test-hash', 'Test Admin', "
                + "'QUAN_TRI')";
        try (Connection ketNoi = nguon.moKetNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            lenh.executeUpdate();
            try (ResultSet khoa = lenh.getGeneratedKeys()) {
                khoa.next();
                return new NguoiDung(khoa.getInt(1), "test-admin",
                        "Test Admin", VaiTro.QUAN_TRI);
            }
        }
    }

    /**
     * Xóa mọi bảng của CSDL kiểm thử rồi tạo lại theo
     * {@code database/schema.sql}.
     *
     * @throws SQLException khi lỗi CSDL
     * @throws IOException khi không đọc được schema.sql
     */
    static void dungLaiBang() throws SQLException, IOException {
        String admin = System.getenv("BLUEMOON_DB_ADMIN_USER");
        String user = admin != null ? admin : bien("BLUEMOON_DB_USER");
        String matKhau = bienTuyChon(admin != null
                ? "BLUEMOON_DB_ADMIN_PASSWORD" : "BLUEMOON_DB_PASSWORD");
        try (Connection ketNoi =
                     DriverManager.getConnection(url(), user, matKhau);
                Statement lenh = ketNoi.createStatement()) {
            List<String> bang = new ArrayList<>();
            try (ResultSet ketQua = lenh.executeQuery("SELECT table_name "
                    + "FROM information_schema.tables "
                    + "WHERE table_schema = DATABASE()")) {
                while (ketQua.next()) {
                    bang.add(ketQua.getString(1));
                }
            }
            lenh.execute("SET FOREIGN_KEY_CHECKS = 0");
            for (String ten : bang) {
                lenh.execute("DROP TABLE `" + ten + "`");
            }
            lenh.execute("SET FOREIGN_KEY_CHECKS = 1");
            for (String cau : cacCauLenh()) {
                lenh.execute(cau);
            }
        }
    }

    /**
     * Các câu lệnh của schema.sql, bỏ dòng chú thích và hai câu tạo, chọn
     * CSDL {@code bluemoon} (kiểm thử dùng CSDL trong url).
     */
    private static List<String> cacCauLenh() throws IOException {
        String noiDung = Files.readAllLines(LUOC_DO, StandardCharsets.UTF_8)
                .stream()
                .filter(dong -> !dong.trim().startsWith("--"))
                .collect(Collectors.joining("\n"));
        List<String> cauLenh = new ArrayList<>();
        for (String cau : noiDung.split(";")) {
            String gon = cau.trim();
            String hoa = gon.toUpperCase();
            if (!gon.isEmpty() && !hoa.startsWith("CREATE DATABASE")
                    && !hoa.startsWith("USE ")) {
                cauLenh.add(gon);
            }
        }
        return cauLenh;
    }

    /** Url CSDL kiểm thử; bắt buộc có chữ "test" để không xóa nhầm. */
    private static String url() {
        String url = bien("BLUEMOON_DB_URL");
        String tenCsdl = url.replaceFirst("^[^/]*//[^/]*/", "")
                .replaceFirst("\\?.*$", "");
        if (!tenCsdl.toLowerCase().contains("test")) {
            throw new IllegalStateException("BLUEMOON_DB_URL phải trỏ tới "
                    + "CSDL có chữ test trong tên, đang là: " + tenCsdl);
        }
        return url;
    }

    private static String bien(String ten) {
        String giaTri = System.getenv(ten);
        if (giaTri == null || giaTri.isBlank()) {
            throw new IllegalStateException("Thiếu biến môi trường " + ten
                    + "; xem docs/setup.md, mục 4");
        }
        return giaTri;
    }

    private static String bienTuyChon(String ten) {
        String giaTri = System.getenv(ten);
        return giaTri != null ? giaTri : "";
    }
}
