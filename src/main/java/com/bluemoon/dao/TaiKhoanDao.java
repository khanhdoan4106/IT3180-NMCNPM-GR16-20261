package com.bluemoon.dao;

import com.bluemoon.model.TaiKhoanMoi;

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
}
