package com.bluemoon.service;

import com.bluemoon.dao.CuDanDao;
import com.bluemoon.model.SoLieuCuDan;
import com.bluemoon.util.ManHinh;

/**
 * Nghiệp vụ về cư dân dùng cho trang chính. Mẫu cho mọi Service: nhận DAO
 * qua constructor (là interface, không phải bản JDBC) và chỉ gọi DAO;
 * quy tắc nghiệp vụ của story đặt ở tầng này.
 */
public class CuDanService {

    private final CuDanDao cuDanDao;

    /**
     * Tạo Service.
     *
     * @param cuDanDao DAO đọc hộ và nhân khẩu
     */
    public CuDanService(CuDanDao cuDanDao) {
        this.cuDanDao = cuDanDao;
    }

    /**
     * Số hộ và số nhân khẩu đang ở, hiện trên Trang chính (mọi vai trò;
     * chỉ là con số tổng, không có thông tin của hộ nào, AC-062).
     *
     * @return số liệu cư dân
     * @throws LoiKhongCoQuyen khi chưa đăng nhập; không đọc CSDL
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public SoLieuCuDan laySoLieu() {
        PhanQuyen.yeuCau(ManHinh.TRANG_CHINH);
        return cuDanDao.demDangO();
    }
}
