package com.bluemoon.dao;

/**
 * Các loại lỗi CSDL người dùng cần phân biệt, mỗi loại một câu báo nói rõ
 * cần làm gì. Câu báo hiện thẳng lên màn hình nên viết tiếng Việt có dấu.
 */
public enum LoaiLoiCsdl {

    /** Thiếu hoặc sai tệp config/db.properties. */
    THIEU_CAU_HINH("Chưa có tệp cấu hình kết nối config/db.properties, "
            + "hoặc tệp thiếu url, user. Sao chép "
            + "config/db.properties.example thành config/db.properties, "
            + "điền tên đăng nhập và mật khẩu MySQL, rồi mở lại ứng dụng."),

    /** Không tới được máy chủ MySQL (chưa chạy, sai địa chỉ, mất mạng). */
    KHONG_TOI_MAY_CHU("Không kết nối được máy chủ MySQL. Kiểm tra MySQL "
            + "đã chạy và địa chỉ trong url của config/db.properties đúng, "
            + "rồi mở lại ứng dụng."),

    /** MySQL từ chối tên đăng nhập hoặc mật khẩu. */
    SAI_DANG_NHAP("MySQL từ chối tên đăng nhập hoặc mật khẩu. Kiểm tra "
            + "user và password trong config/db.properties, rồi mở lại "
            + "ứng dụng."),

    /** Tài khoản MySQL chưa được cấp quyền trên CSDL. */
    KHONG_CO_QUYEN("Tài khoản MySQL chưa được cấp quyền trên CSDL "
            + "bluemoon. Chạy lệnh GRANT trong docs/setup.md, mục 3, "
            + "rồi mở lại ứng dụng."),

    /** Chưa tạo CSDL hoặc thiếu bảng. */
    CHUA_DUNG_CSDL("Chưa có CSDL bluemoon hoặc thiếu bảng. Chạy "
            + "database/schema.sql rồi database/seed.sql bằng tài khoản "
            + "root (docs/setup.md, mục 3), rồi mở lại ứng dụng."),

    /** Lỗi khác khi đọc hoặc ghi dữ liệu. */
    KHAC("Không đọc hoặc ghi được dữ liệu. Thử lại; nếu vẫn lỗi, kiểm tra "
            + "MySQL và config/db.properties.");

    private final String thongDiep;

    LoaiLoiCsdl(String thongDiep) {
        this.thongDiep = thongDiep;
    }

    /**
     * Câu báo cho người dùng.
     *
     * @return câu báo tiếng Việt, nói rõ cần làm gì
     */
    public String thongDiep() {
        return thongDiep;
    }
}
