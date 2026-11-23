package com.bluemoon.dao;

import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
import com.bluemoon.model.VaiTro;
import java.util.List;
import java.util.Optional;

/** Ghi và đọc bảng {@code tai_khoan}. */
public interface TaiKhoanDao {

    /**
     * Thêm tài khoản (US-020). Tên đăng nhập là duy nhất, không phân biệt
     * chữ hoa, chữ thường.
     *
     * @param taiKhoan tài khoản mới, mật khẩu đã băm
     * @return {@code false} nếu tên đăng nhập đã có, không thêm gì
     * @throws LoiCsdl khi không ghi được CSDL
     */
    boolean them(TaiKhoanMoi taiKhoan, NguoiDung nguoiThucHien);

    /**
     * Tìm tài khoản theo tên đăng nhập (US-021), không phân biệt chữ hoa,
     * chữ thường.
     *
     * @param tenDangNhap tên đăng nhập đã bỏ khoảng trắng hai đầu
     * @return tài khoản, rỗng nếu không có
     * @throws LoiCsdl khi không đọc được CSDL
     */
    Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap);

    /**
     * Tìm tài khoản theo mã (US-023: người đang đăng nhập).
     *
     * @param id mã tài khoản
     * @return tài khoản, rỗng nếu không có
     * @throws LoiCsdl khi không đọc được CSDL
     */
    Optional<TaiKhoan> timTheoId(int id);

    /**
     * Sửa họ tên và số điện thoại của một tài khoản (US-023, AC-065).
     *
     * @param id mã tài khoản
     * @param hoTen họ tên mới
     * @param soDienThoai số điện thoại mới, {@code null} để xóa
     * @return {@code false} nếu không có tài khoản mã đó
     * @throws LoiCsdl khi không ghi được CSDL
     */
    boolean capNhatThongTin(int id, String hoTen, String soDienThoai);

    /**
     * Ghi mật khẩu mới đã băm (US-023, AC-063).
     *
     * @param id mã tài khoản
     * @param matKhauBam mật khẩu mới đã băm BCrypt
     * @return {@code false} nếu không có tài khoản mã đó
     * @throws LoiCsdl khi không ghi được CSDL
     */
    boolean doiMatKhau(int id, String matKhauBam);

    /**
     * Mọi tài khoản, xếp theo tên đăng nhập (US-022, giả định 18).
     *
     * @return danh sách, rỗng nếu chưa có tài khoản nào
     * @throws LoiCsdl khi không đọc được CSDL
     */
    List<TaiKhoan> danhSach();

    /**
     * Đổi vai trò một tài khoản (US-022, giả định 18). Không đổi nếu việc
     * này làm không còn Quản trị viên nào đang hoạt động; kiểm và ghi trong
     * cùng một giao dịch để hai máy đổi cùng lúc cũng không vượt được.
     *
     * @param id mã tài khoản
     * @param vaiTro vai trò mới
     * @return {@code false} nếu không có tài khoản mã đó hoặc đây là Quản
     *         trị viên đang hoạt động cuối cùng
     * @throws LoiCsdl khi không ghi được CSDL
     */
    boolean doiVaiTro(int id, VaiTro vaiTro,
            NguoiDung nguoiThucHien);

    /**
     * Khóa hoặc mở khóa một tài khoản (US-022, giả định 18). Không khóa
     * Quản trị viên đang hoạt động cuối cùng; kiểm và ghi trong cùng một
     * giao dịch.
     *
     * @param id mã tài khoản
     * @param hoatDong {@code false} để khóa, {@code true} để mở khóa
     * @return {@code false} nếu không có tài khoản mã đó hoặc đây là Quản
     *         trị viên đang hoạt động cuối cùng
     * @throws LoiCsdl khi không ghi được CSDL
     */
    boolean datHoatDong(int id, boolean hoatDong,
            NguoiDung nguoiThucHien);

    /** Đặt lại mật khẩu cho tài khoản khác và ghi nhật ký cùng transaction. */
    boolean datLaiMatKhau(int id, String matKhauBam,
            NguoiDung nguoiThucHien);
}
