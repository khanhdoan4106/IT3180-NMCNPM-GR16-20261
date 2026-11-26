package com.bluemoon.dao;

import com.bluemoon.model.KhoanPhi;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Truy cập và thao tác dữ liệu bảng {@code khoan_phi} (US-005, US-006).
 */
public interface KhoanPhiDao {

    /**
     * Thêm một khoản thu mới vào CSDL (AC-019).
     *
     * @param khoanPhi thông tin khoản thu cần thêm
     * @return mã định danh vừa sinh của khoản thu
     * @throws LoiCsdl khi không ghi được dữ liệu vào CSDL
     */
    int them(KhoanPhi khoanPhi);

    /**
     * Kiểm tra xem tên khoản thu đã tồn tại trong cùng đợt thu (cùng ngày bắt
     * đầu) hay chưa (AC-020, ràng buộc {@code uq_kp_ten_dot}).
     *
     * @param ten tên khoản thu cần kiểm tra
     * @param tuNgay ngày bắt đầu đợt thu
     * @return {@code true} nếu đã có khoản thu cùng tên trong cùng đợt
     * @throws LoiCsdl khi không đọc được CSDL
     */
    boolean tonTaiTenTrongDot(String ten, LocalDate tuNgay);

    /**
     * Lấy danh sách tất cả các khoản thu trong hệ thống, sắp xếp giảm dần
     * theo mã định danh (mới nhất lên trên).
     *
     * @return danh sách các khoản thu
     * @throws LoiCsdl khi không đọc được CSDL
     */
    List<KhoanPhi> layDanhSach();

    /**
     * Tìm kiếm khoản thu theo mã định danh.
     *
     * @param id mã khoản thu
     * @return thông tin khoản thu, rỗng nếu không tìm thấy
     * @throws LoiCsdl khi không đọc được CSDL
     */
    Optional<KhoanPhi> timTheoId(int id);

    /**
     * Tìm kiếm khoản thu theo tên chính xác.
     *
     * @param ten tên khoản thu
     * @return thông tin khoản thu, rỗng nếu không tìm thấy
     * @throws LoiCsdl khi không đọc được CSDL
     */
    Optional<KhoanPhi> timTheoTen(String ten);

    /**
     * Cập nhật đơn giá cho khoản thu và ghi nhật ký thao tác trong cùng giao
     * dịch (US-005, TS-002).
     *
     * @param id mã khoản thu
     * @param donGiaMoi đơn giá mới
     * @param nguoiDung người thực hiện thao tác
     * @param noiDungNhatKy nội dung chi tiết thay đổi để ghi nhật ký
     * @throws LoiCsdl khi không cập nhật được CSDL
     */
    void capNhatDonGia(int id, java.math.BigDecimal donGiaMoi,
            com.bluemoon.model.NguoiDung nguoiDung, String noiDungNhatKy);
}
