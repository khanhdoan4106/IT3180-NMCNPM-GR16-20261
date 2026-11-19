package com.bluemoon.service;

import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.YeuCauThemNhanKhau;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý nghiệp vụ nhân khẩu: kiểm tra độ tuổi cấp CCCD, chặn trùng lặp.
 */
public class NhanKhauService {

    private static final int TUOI_CAP_CCCD = 14;

    private final NhanKhauDao nhanKhauDao;

    /** Khởi tạo Service với DAO tiêm vào. */
    public NhanKhauService(NhanKhauDao nhanKhauDao) {
        this.nhanKhauDao = nhanKhauDao;
    }

    /**
     * Lấy danh sách thành viên đang ở của một hộ.
     *
     * @param hoId mã ID của hộ gia đình
     * @return danh sách nhân khẩu
     */
    public List<NhanKhau> layDanhSachTheoHo(int hoId) {
        return nhanKhauDao.layDanhSachTheoHo(hoId);
    }

    /**
     * Kiểm tra dữ liệu và thêm mới nhân khẩu vào hộ.
     *
     * @param yc thông tin nhân khẩu cần lưu
     * @return định danh (id) của nhân khẩu vừa tạo
     */
    public int themNhanKhau(YeuCauThemNhanKhau yc) {
        kiemTraTruongBatBuoc(yc);
        
        String cccd = chuanHoa(yc.soCccd());
        if (cccd != null) {
            if (!cccd.matches("\\d{12}")) {
                throw new IllegalArgumentException(
                        "Số CCCD phải gồm đúng 12 chữ số.");
            }
            if (nhanKhauDao.tonTaiCccdDangO(cccd)) {
                throw new IllegalArgumentException(
                        "Số CCCD " + cccd + " đã tồn tại trong hệ thống.");
            }
        } else {
            int tuoi = Period.between(
                    yc.ngaySinh(), LocalDate.now()).getYears();
            if (tuoi >= TUOI_CAP_CCCD) {
                throw new IllegalArgumentException(
                        "Công dân từ đủ 14 tuổi bắt buộc nhập số CCCD.");
            }
        }

        YeuCauThemNhanKhau daChuanHoa = new YeuCauThemNhanKhau(
                yc.hoId(),
                yc.hoTen().trim(),
                yc.ngaySinh(),
                yc.gioiTinh(),
                yc.quanHe(),
                cccd,
                chuanHoa(yc.danToc()),
                chuanHoa(yc.tonGiao()),
                chuanHoa(yc.ngheNghiep()));

        return nhanKhauDao.themNhanKhau(daChuanHoa);
    }

    private void kiemTraTruongBatBuoc(YeuCauThemNhanKhau yc) {
        List<String> thieu = new ArrayList<>();
        if (yc.hoId() <= 0) {
            thieu.add("hộ gia đình");
        }
        if (yc.hoTen() == null || yc.hoTen().isBlank()) {
            thieu.add("họ tên");
        }
        if (yc.ngaySinh() == null) {
            thieu.add("ngày sinh");
        }
        if (yc.gioiTinh() == null || yc.gioiTinh().isBlank()) {
            thieu.add("giới tính");
        }
        if (yc.quanHe() == null || yc.quanHe().isBlank()) {
            thieu.add("quan hệ");
        }
        if (!thieu.isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ: " + String.join(", ", thieu));
        }
    }

    private static String chuanHoa(String chuoi) {
        return (chuoi == null || chuoi.isBlank()) ? null : chuoi.trim();
    }
}