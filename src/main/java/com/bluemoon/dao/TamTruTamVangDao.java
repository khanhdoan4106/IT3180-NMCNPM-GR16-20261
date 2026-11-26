package com.bluemoon.dao;

import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import java.time.LocalDate;
import java.util.List;

/** Giao diện truy cập dữ liệu tạm trú, tạm vắng. */
public interface TamTruTamVangDao {

    /** Câu báo khi đợt mới chồng ngày với đợt đã có (giả định 11). */
    String LOI_CHONG_NGAY = "Đợt này chồng ngày với một đợt tạm trú hoặc "
            + "tạm vắng đã có của nhân khẩu.";

    /** Lấy danh sách nhân khẩu đang ở trong hộ. */
    List<NguoiTrongHo> layNguoiTrongHo(int hoId);

    /** Lấy danh sách khai báo tạm trú, tạm vắng theo hộ. */
    List<TamTruTamVang> layDanhSachTheoHo(int hoId);

    /**
     * Lấy loại cư trú của nhân khẩu đang ở.
     *
     * @param nhanKhauId định danh nhân khẩu
     * @return loại cư trú, {@code null} nếu không có nhân khẩu đang ở này
     */
    LoaiCuTru layLoaiCuTru(int nhanKhauId);

    /**
     * Kiểm tra nhân khẩu đã có đợt nào chồng lên khoảng ngày cho trước.
     *
     * @param nhanKhauId định danh nhân khẩu
     * @param tuNgay ngày bắt đầu đợt mới
     * @param denNgay ngày kết thúc đợt mới
     * @return {@code true} nếu có đợt chồng ngày
     */
    boolean coDotChongNgay(int nhanKhauId, LocalDate tuNgay,
            LocalDate denNgay);

    /**
     * Thêm khai báo và nhật ký trong cùng một giao dịch. Giao dịch khóa dòng
     * nhân khẩu rồi kiểm tra lại chồng ngày, nên hai máy cùng lưu một lúc
     * cũng không tạo được hai đợt chồng nhau.
     */
    int themKhaiBao(YeuCauKhaiBao yc, NguoiDung nguoiDung);
}
