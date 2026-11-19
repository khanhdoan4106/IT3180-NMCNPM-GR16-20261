// package com.bluemoon.dao;

// import com.bluemoon.model.TamTruTamVang;
// import com.bluemoon.model.YeuCauKhaiBao;
// import java.util.List;

// /**
//  * Giao diện truy cập dữ liệu bảng tam_tru_tam_vang (US-003).
//  */
// public interface TamTruTamVangDao {

//     /**
//      * Lấy danh sách khai báo của một hộ gia đình.
//      *
//      * @param hoId định danh hộ gia đình
//      * @return danh sách các bản ghi khai báo
//      */
//     List<TamTruTamVang> layDanhSachTheoHo(int hoId);

//     /**
//      * Lưu thông tin khai báo mới vào cơ sở dữ liệu.
//      *
//      * @param yc thông tin khai báo
//      * @return id của bản ghi mới
//      */
//     int themKhaiBao(YeuCauKhaiBao yc);
// }
package com.bluemoon.dao;

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

    /** Thêm đợt khai báo mới. */
    int themKhaiBao(YeuCauKhaiBao yc);
}