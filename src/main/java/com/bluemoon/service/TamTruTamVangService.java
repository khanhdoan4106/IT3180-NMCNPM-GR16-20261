package com.bluemoon.service;

import com.bluemoon.dao.TamTruTamVangDao;
import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.TamTruTamVang;
import com.bluemoon.model.TamTruTamVang.NguoiTrongHo;
import com.bluemoon.model.YeuCauKhaiBao;
import com.bluemoon.util.ManHinh;
import java.util.List;

/** Xử lý nghiệp vụ tạm trú, tạm vắng. */
public class TamTruTamVangService {

    private final TamTruTamVangDao dao;

    /** Khởi tạo dịch vụ. */
    public TamTruTamVangService(TamTruTamVangDao dao) {
        this.dao = dao;
    }

    /** Lấy danh sách nhân khẩu đang ở trong hộ. */
    public List<NguoiTrongHo> layNguoiTrongHo(int hoId) {
        return dao.layNguoiTrongHo(hoId);
    }

    /** Lấy lịch sử khai báo theo hộ. */
    public List<TamTruTamVang> layDanhSachTheoHo(int hoId) {
        return dao.layDanhSachTheoHo(hoId);
    }

    /** Kiểm tra hợp lệ và thêm khai báo mới. */
    public int themKhaiBao(YeuCauKhaiBao yc) {
        NguoiDung nguoiDung = PhanQuyen.yeuCau(ManHinh.TAM_TRU_TAM_VANG);
        if (yc.nhanKhauId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn nhân khẩu.");
        }
        if (yc.tuNgay() == null || yc.denNgay() == null) {
            throw new IllegalArgumentException("Vui lòng nhập đủ ngày tháng.");
        }
        if (yc.tuNgay().isAfter(yc.denNgay())) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
        }
        if (!"TAM_TRU".equals(yc.loai()) && !"TAM_VANG".equals(yc.loai())) {
            throw new IllegalArgumentException("Loại khai báo không hợp lệ.");
        }

        String diaChi = yc.diaChiTamVang();
        if ("TAM_TRU".equals(yc.loai())
                && diaChi != null && !diaChi.isBlank()) {
            diaChi = null;
        }

        YeuCauKhaiBao chuan = new YeuCauKhaiBao(
                yc.nhanKhauId(), yc.loai(), yc.soGiay(), yc.tuNgay(),
                yc.denNgay(), yc.lyDo(), diaChi);

        return dao.themKhaiBao(chuan, nguoiDung);
    }
}
