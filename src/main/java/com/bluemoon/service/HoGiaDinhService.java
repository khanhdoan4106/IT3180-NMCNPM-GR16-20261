package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.YeuCauThemHo;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Nghiệp vụ quản lý hộ gia đình (US-001).
 */
public class HoGiaDinhService {

    private static final int TANG_TOI_THIEU = 1;
    private static final int TANG_TOI_DA = 30;
    private static final int TUOI_BAT_BUOC_CCCD = 14;

    private final HoGiaDinhDao hoGiaDinhDao;

    /**
     * Tạo service với DAO được cung cấp.
     *
     * @param hoGiaDinhDao DAO truy cập hộ gia đình
     */
    public HoGiaDinhService(HoGiaDinhDao hoGiaDinhDao) {
        this.hoGiaDinhDao = hoGiaDinhDao;
    }

    /**
     * Lấy danh sách các hộ gia đình đang ở.
     *
     * @return danh sách hộ đang ở
     */
    public List<HoGiaDinh> layDanhSachDangO() {
        return hoGiaDinhDao.layDanhSachDangO();
    }

    /**
     * Tìm kiếm hộ đang ở theo mã căn hộ hoặc tên chủ hộ (không phân biệt
     * hoa thường và dấu theo giả định 40).
     *
     * @param tuKhoa từ khóa tìm kiếm
     * @return danh sách hộ khớp từ khóa
     */
    public List<HoGiaDinh> timKiem(String tuKhoa) {
        List<HoGiaDinh> tatCa = hoGiaDinhDao.layDanhSachDangO();
        if (tuKhoa == null || tuKhoa.isBlank()) {
            return tatCa;
        }
        String chuan = boDau(tuKhoa.trim());
        List<HoGiaDinh> ketQua = new ArrayList<>();
        for (HoGiaDinh ho : tatCa) {
            if (boDau(ho.maCanHo()).contains(chuan)
                    || boDau(ho.tenChuHo()).contains(chuan)) {
                ketQua.add(ho);
            }
        }
        return ketQua;
    }

    /**
     * Kiểm tra dữ liệu và thêm mới hộ gia đình (AC-001..AC-004).
     *
     * @param yc thông tin hộ và chủ hộ cần thêm
     * @return định danh hộ mới
     * @throws IllegalArgumentException nếu thiếu hoặc sai dữ liệu nghiệp vụ
     */
    public int themHoGiaDinh(YeuCauThemHo yc) {
        kiemTraTruongBatBuoc(yc);
        kiemTraMienGiaTri(yc);

        String maCanHo = yc.maCanHo().trim();
        if (hoGiaDinhDao.tonTaiMaCanHoDangO(maCanHo)) {
            throw new IllegalArgumentException(
                    "Mã căn hộ " + maCanHo + " đã tồn tại.");
        }

        String cccd = chuanHoaTuyChon(yc.cccdChuHo());
        if (cccd != null && hoGiaDinhDao.tonTaiCccdDangO(cccd)) {
            throw new IllegalArgumentException(
                    "Số CCCD " + cccd + " đã tồn tại.");
        }

        YeuCauThemHo daChuanHoa = new YeuCauThemHo(
                maCanHo,
                yc.loaiCanHo(),
                yc.tang(),
                yc.dienTich(),
                chuanHoaTuyChon(yc.soDienThoai()),
                yc.ngayChuyenDen(),
                yc.tenChuHo().trim(),
                yc.ngaySinhChuHo(),
                yc.gioiTinhChuHo(),
                cccd);
        return hoGiaDinhDao.themHoGiaDinh(daChuanHoa);
    }

    private void kiemTraTruongBatBuoc(YeuCauThemHo yc) {
        if (yc == null) {
            throw new IllegalArgumentException("Dữ liệu hộ không hợp lệ.");
        }
        List<String> thieu = new ArrayList<>();
        if (trong(yc.maCanHo())) {
            thieu.add("mã căn hộ");
        }
        if (yc.loaiCanHo() == null) {
            thieu.add("loại căn hộ");
        }
        if (yc.tang() == null) {
            thieu.add("tầng");
        }
        if (yc.dienTich() == null) {
            thieu.add("diện tích");
        }
        if (yc.ngayChuyenDen() == null) {
            thieu.add("ngày chuyển đến");
        }
        if (trong(yc.tenChuHo())) {
            thieu.add("tên chủ hộ");
        }
        if (yc.ngaySinhChuHo() == null) {
            thieu.add("ngày sinh chủ hộ");
        }
        if (trong(yc.gioiTinhChuHo())) {
            thieu.add("giới tính chủ hộ");
        }
        if (!thieu.isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ: " + String.join(", ", thieu) + ".");
        }
    }

    private void kiemTraMienGiaTri(YeuCauThemHo yc) {
        if (yc.tang() < TANG_TOI_THIEU || yc.tang() > TANG_TOI_DA) {
            throw new IllegalArgumentException(
                    "Tầng phải nằm trong khoảng từ 1 đến 30.");
        }
        if (yc.dienTich().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Diện tích căn hộ phải lớn hơn 0.");
        }
        int tuoi = Period.between(
                yc.ngaySinhChuHo(), LocalDate.now()).getYears();
        String cccd = chuanHoaTuyChon(yc.cccdChuHo());
        if (tuoi >= TUOI_BAT_BUOC_CCCD && cccd == null) {
            throw new IllegalArgumentException(
                    "Chủ hộ từ đủ 14 tuổi bắt buộc nhập số CCCD.");
        }
        if (cccd != null && !cccd.matches("\\d{12}")) {
            throw new IllegalArgumentException(
                    "Số CCCD phải gồm đúng 12 chữ số.");
        }
    }

    private static boolean trong(String chuoi) {
        return chuoi == null || chuoi.isBlank();
    }

    private static String chuanHoaTuyChon(String chuoi) {
        if (chuoi == null || chuoi.isBlank()) {
            return null;
        }
        return chuoi.trim();
    }

    private static String boDau(String chuoi) {
        if (chuoi == null) {
            return "";
        }
        String phanTach = Normalizer.normalize(chuoi, Normalizer.Form.NFD);
        return phanTach.replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }
}