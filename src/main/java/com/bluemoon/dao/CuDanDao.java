package com.bluemoon.dao;

import com.bluemoon.model.SoLieuCuDan;

/**
 * Đọc số liệu hộ gia đình và nhân khẩu. Service chỉ biết interface này,
 * nên test Service bằng DAO giả mà không cần MySQL.
 */
public interface CuDanDao {

    /**
     * Đếm hộ gia đình và nhân khẩu có trạng thái đang ở.
     *
     * @return số hộ và số nhân khẩu đang ở
     * @throws LoiCsdl khi không đọc được CSDL
     */
    SoLieuCuDan demDangO();
}
