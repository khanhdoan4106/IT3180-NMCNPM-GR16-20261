package com.bluemoon.service;

import com.bluemoon.dao.NhatKyDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.util.ManHinh;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** Nghiệp vụ ghi và xem nhật ký thao tác (TS-002). */
public class NhatKyService {

    /** Số dòng tối đa hiển thị trên màn hình. */
    public static final int GIOI_HAN_MAC_DINH = 200;

    private final NhatKyDao nhatKyDao;

    /**
     * Tạo Service.
     *
     * @param nhatKyDao DAO nhật ký
     */
    public NhatKyService(NhatKyDao nhatKyDao) {
        this.nhatKyDao = nhatKyDao;
    }

    /**
     * Ghi nhật ký trong cùng giao dịch với thay đổi dữ liệu.
     * Phương thức không tự mở kết nối, không commit và không nuốt lỗi.
     * Nếu ghi thất bại, ngoại lệ phải làm giao dịch nghiệp vụ rollback.
     *
     * @param ketNoi kết nối đang ở trong giao dịch nghiệp vụ
     * @param nguoiDung người thực hiện đã được xác thực
     * @param thaoTac thao tác nghiệp vụ; tên khớp CHECK của schema
     * @param doiTuong đối tượng bị tác động
     * @param noiDung nội dung thay đổi
     * @throws SQLException khi không ghi được nhật ký
     */
    public void ghiTrongGiaoDich(Connection ketNoi, NguoiDung nguoiDung,
            ThaoTac thaoTac, String doiTuong, String noiDung)
            throws SQLException {
        if (ketNoi == null || nguoiDung == null
                || thaoTac == null
                || doiTuong == null || doiTuong.isBlank()) {
            throw new IllegalArgumentException(
                    "Thiếu thông tin để ghi nhật ký.");
        }
        nhatKyDao.ghi(ketNoi, nguoiDung.id(), thaoTac, doiTuong, noiDung);
    }

    /**
     * Quản trị viên xem nhật ký mới nhất trước.
     *
     * @return tối đa 200 dòng nhật ký
     * @throws LoiKhongCoQuyen khi chưa đăng nhập hoặc không phải Quản trị
     *         viên
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public List<NhatKy> xemMoiNhat() {
        PhanQuyen.yeuCau(ManHinh.NHAT_KY);
        return nhatKyDao.danhSachMoiNhat(GIOI_HAN_MAC_DINH);
    }
}
