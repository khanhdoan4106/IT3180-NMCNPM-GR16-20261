package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String HOST_NAME = "localhost";
    private static final String PORT = "1433";
    private static final String DB_NAME = "bluemoon_db";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "team16chanhet"; 

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return getConnection(HOST_NAME, PORT, DB_NAME, USER_NAME, PASSWORD);
    }

    public static Connection getConnection(String hostName, String port, String dbName, String userName, String password)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionUrl = "jdbc:sqlserver://" + hostName + ":" + port + ";"
                + "databaseName=" + dbName + ";"
                + "encrypt=true;trustServerCertificate=true;";
        return DriverManager.getConnection(connectionUrl, userName, password);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT username, full_name, role FROM users")) {
            System.out.println("=== KẾT NỐI SQL SERVER THÀNH CÔNG! ===");
            while (rs.next()) {
                System.out.println("- Tài khoản: " + rs.getString("username")
                        + " | Họ tên: " + rs.getString("full_name")
                        + " | Vai trò: " + rs.getInt("role"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối SQL Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}