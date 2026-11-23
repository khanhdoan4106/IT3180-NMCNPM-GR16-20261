package com.bluemoon.dao;

import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import java.util.List;

/** Giao diện truy cập dữ liệu tạm trú, tạm vắng. */
public interface TamTruTamVangDao {

    /** Lấy danh sách nhân khẩu đang ở trong hộ. */
    List<NguoiTrongHo> layNguoiTrongHo(int hoId);

    /** Lấy danh sách khai báo tạm trú, tạm vắng theo hộ. */
    List<TamTruTamVang> layDanhSachTheoHo(int hoId);

    /** Thêm khai báo và nhật ký trong cùng một giao dịch. */
    int themKhaiBao(YeuCauKhaiBao yc, NguoiDung nguoiDung);
}
