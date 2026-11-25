package com.bluemoon.dao;

import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.util.List;

/**
 * Giao diện truy cập dữ liệu bảng nhan_khau (US-002).
 */
public interface NhanKhauDao {

    /**
     * Lấy danh sách nhân khẩu đang ở thuộc về một hộ gia đình.
     *
     * @param hoId định danh của hộ gia đình
     * @return danh sách thành viên trong hộ
     */
    List<NhanKhau> layDanhSachTheoHo(int hoId);

    /**
     * Kiểm tra số CCCD đã được người đang ở nào dùng chưa.
     *
     * @param soCccd số CCCD cần kiểm tra
     * @return {@code true} nếu CCCD bị trùng
     */
    boolean tonTaiCccdDangO(String soCccd);

    /**
     * Lưu nhân khẩu mới và nhật ký trong cùng một giao dịch.
     *
     * @param yeuCau thông tin nhân khẩu cần thêm
     * @param nguoiDung người thực hiện
     * @return định danh (id) của dòng dữ liệu mới
     */
    int themNhanKhau(YeuCauThemNhanKhau yeuCau, NguoiDung nguoiDung);
}
