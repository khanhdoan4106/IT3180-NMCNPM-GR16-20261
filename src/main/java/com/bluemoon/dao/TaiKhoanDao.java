package com.bluemoon.dao;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TaiKhoanMoi;
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
    boolean them(TaiKhoanMoi taiKhoan);

    /**
     * Tìm tài khoản theo tên đăng nhập (US-021), không phân biệt chữ hoa,
     * chữ thường.
     *
     * @param tenDangNhap tên đăng nhập đã bỏ khoảng trắng hai đầu
     * @return tài khoản, rỗng nếu không có
     * @throws LoiCsdl khi không đọc được CSDL
     */
    Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap);
}
