package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.bluemoon.config.LoiCauHinh;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class LoiCsdlTest {

    @Test
    void thieuTepCauHinh() {
        assertEquals(LoaiLoiCsdl.THIEU_CAU_HINH,
                LoiCsdl.phanLoai(new LoiCauHinh("thiếu tệp", null)));
    }

    @Test
    void mayChuChuaChay() {
        // Connector/J báo "Communications link failure" với SQLState 08S01
        SQLException loi = new SQLException("link failure", "08S01", 0);
        assertEquals(LoaiLoiCsdl.KHONG_TOI_MAY_CHU, LoiCsdl.phanLoai(loi));
    }

    @Test
    void saiMatKhau() {
        SQLException loi = new SQLException("Access denied", "28000",
                LoiCsdl.MA_SAI_DANG_NHAP);
        assertEquals(LoaiLoiCsdl.SAI_DANG_NHAP, LoiCsdl.phanLoai(loi));
    }

    @Test
    void chuaCapQuyen() {
        assertEquals(LoaiLoiCsdl.KHONG_CO_QUYEN, LoiCsdl.phanLoai(
                new SQLException("denied", "42000",
                        LoiCsdl.MA_KHONG_QUYEN_CSDL)));
        assertEquals(LoaiLoiCsdl.KHONG_CO_QUYEN, LoiCsdl.phanLoai(
                new SQLException("denied", "42000",
                        LoiCsdl.MA_KHONG_QUYEN_BANG)));
    }

    @Test
    void chuaCoCsdlHoacBang() {
        assertEquals(LoaiLoiCsdl.CHUA_DUNG_CSDL, LoiCsdl.phanLoai(
                new SQLException("Unknown database", "42000",
                        LoiCsdl.MA_KHONG_CO_CSDL)));
        assertEquals(LoaiLoiCsdl.CHUA_DUNG_CSDL, LoiCsdl.phanLoai(
                new SQLException("doesn't exist", "42S02",
                        LoiCsdl.MA_KHONG_CO_BANG)));
    }

    @Test
    void loiKhac() {
        assertEquals(LoaiLoiCsdl.KHAC, LoiCsdl.phanLoai(
                new SQLException("Duplicate entry", "23000", 1062)));
        assertEquals(LoaiLoiCsdl.KHAC,
                LoiCsdl.phanLoai(new SQLException("không rõ")));
    }

    @Test
    void cauBaoTiengVietVaGiuLoiGoc() {
        SQLException goc = new SQLException("Access denied", "28000",
                LoiCsdl.MA_SAI_DANG_NHAP);
        LoiCsdl loi = LoiCsdl.tu(goc);
        assertEquals(LoaiLoiCsdl.SAI_DANG_NHAP.thongDiep(), loi.getMessage());
        assertSame(goc, loi.getCause());
    }
}
