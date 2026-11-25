package com.bluemoon.dao;

import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.YeuCauThemHo;
import java.util.List;

/**
 * Truy cập dữ liệu hộ gia đình trong CSDL
 */
public interface HoGiaDinhDao {

    /**
     * Lấy danh sách các hộ gia đình đang ở kèm tên chủ hộ và số thành viên.
     *
     * @return danh sách hộ đang ở, sắp xếp theo mã căn hộ
     */
    List<HoGiaDinh> layDanhSachDangO();

    /**
     * Kiểm tra mã căn hộ đã có hộ đang ở hay chưa (AC-004).
     *
     * @param maCanHo mã căn hộ cần kiểm tra
     * @return {@code true} nếu đã tồn tại hộ đang ở mang mã này
     */
    boolean tonTaiMaCanHoDangO(String maCanHo);

    /**
     * Kiểm tra số CCCD đã thuộc về nhân khẩu đang ở nào chưa (AC-008).
     *
     * @param soCccd số CCCD cần kiểm tra
     * @return {@code true} nếu số CCCD đang được sử dụng
     */
    boolean tonTaiCccdDangO(String soCccd);

    /**
     * Thêm hộ và chủ hộ, đồng thời ghi nhật ký trong cùng transaction.
     *
     * @param yeuCau thông tin hộ và chủ hộ
     * @param nguoiDung người đang thực hiện thao tác
     * @return định danh ({@code id}) của hộ vừa tạo
     */
    int themHoGiaDinh(YeuCauThemHo yeuCau, NguoiDung nguoiDung);
}
