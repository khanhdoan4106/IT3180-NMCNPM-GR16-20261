package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.model.NguoiDung;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.ManHinh;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Lớp 2 của phân quyền (giả định 17, AC-060, AC-061). */
class PhanQuyenTest {

    private static final NguoiDung QUAN_TRI = new NguoiDung(1, "admin",
            "Nguyễn Thị Hương", VaiTro.QUAN_TRI);
    private static final NguoiDung THU_QUY = new NguoiDung(2, "thuquy01",
            "Trần Thị Mai", VaiTro.THU_QUY);

    @AfterEach
    void dangXuat() {
        PhienDangNhap.ketThuc();
    }

    @Test
    void chuaDangNhapBiTuChoiMoiChucNang() {
        for (ManHinh manHinh : ManHinh.values()) {
            LoiKhongCoQuyen loi = assertThrows(LoiKhongCoQuyen.class,
                    () -> PhanQuyen.yeuCau(manHinh));
            assertEquals("Cần đăng nhập để làm việc này.", loi.getMessage());
        }
    }

    @Test
    void ac061QuanTriVienDungMoiChucNang() {
        PhienDangNhap.batDau(QUAN_TRI);
        for (ManHinh manHinh : ManHinh.values()) {
            assertSame(QUAN_TRI, PhanQuyen.yeuCau(manHinh));
        }
        assertSame(QUAN_TRI, PhanQuyen.chiQuanTriVien("x"));
    }

    @Test
    void ac060ThuQuyChiThuPhiVaTraCuu() {
        PhienDangNhap.batDau(THU_QUY);
        for (ManHinh manHinh : ManHinh.values()) {
            boolean duoc = manHinh == ManHinh.TRANG_CHINH
                    || manHinh == ManHinh.CHOT_CONG_NO
                    || manHinh == ManHinh.THU_PHI
                    || manHinh == ManHinh.PHIEU_THU
                    || manHinh == ManHinh.BAO_CAO_THU
                    || manHinh == ManHinh.TAI_KHOAN;
            if (duoc) {
                assertSame(THU_QUY, PhanQuyen.yeuCau(manHinh));
            } else {
                assertThrows(LoiKhongCoQuyen.class,
                        () -> PhanQuyen.yeuCau(manHinh), manHinh.name());
            }
        }
    }

    @Test
    void cauBaoNoiRoAiDuocDung() {
        PhienDangNhap.batDau(THU_QUY);
        LoiKhongCoQuyen loi = assertThrows(LoiKhongCoQuyen.class,
                () -> PhanQuyen.yeuCau(ManHinh.HO_GIA_DINH));
        assertEquals("Chức năng “Hộ gia đình” chỉ dành cho Quản trị viên.",
                loi.getMessage());
        assertEquals("Chỉ Quản trị viên xem nhật ký.", assertThrows(
                LoiKhongCoQuyen.class,
                () -> PhanQuyen.chiQuanTriVien(
                        "Chỉ Quản trị viên xem nhật ký.")).getMessage());
    }

    /** Thẻ tổng thu tháng (US-016): cả hai vai trò xem được (giả định 51). */
    @Test
    void theTongThuThangCaHaiVaiTro() {
        PhienDangNhap.batDau(THU_QUY);
        assertSame(THU_QUY, PhanQuyen.yeuCau(
                PhanQuyen.XEM_TONG_THU_THANG, "x"));
        PhienDangNhap.batDau(QUAN_TRI);
        assertSame(QUAN_TRI, PhanQuyen.yeuCau(
                PhanQuyen.XEM_TONG_THU_THANG, "x"));
    }
}
