package com.bluemoon.service;

import com.bluemoon.dao.CuDanDao;
import com.bluemoon.model.SoLieuCuDan;

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
     * Số hộ và số nhân khẩu đang ở.
     *
     * @return số liệu cư dân
     * @throws com.bluemoon.dao.LoiCsdl khi không đọc được CSDL
     */
    public SoLieuCuDan laySoLieu() {
        return cuDanDao.demDangO();
    }
}
