package com.bluemoon.dao;

import com.bluemoon.model.NhatKy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** Truy cập nhật ký thao tác (TS-002). */
public interface NhatKyDao {

    /**
     * Ghi nhật ký bằng chính kết nối của giao dịch nghiệp vụ.
     *
     * @param ketNoi kết nối đang ở trong giao dịch nghiệp vụ
     * @param taiKhoanId mã người thực hiện
     * @param thaoTac mã thao tác được schema cho phép
     * @param doiTuong đối tượng bị tác động
     * @param noiDung nội dung thay đổi, có thể null
     * @throws SQLException khi không ghi được; bên gọi phải rollback
     */
    void ghi(Connection ketNoi, int taiKhoanId, String thaoTac,
            String doiTuong, String noiDung) throws SQLException;

    /**
     * Lấy nhật ký mới nhất trước, không cung cấp API sửa hoặc xóa.
     *
     * @param gioiHan số dòng tối đa
     * @return danh sách nhật ký
     */
    List<NhatKy> danhSachMoiNhat(int gioiHan);
}
