package com.bluemoon.model;

import java.sql.Date;

/**
 * Khoản thu trong danh mục: bắt buộc (phí dịch vụ, phí quản lý) hoặc tự
 * nguyện (các quỹ). Ánh xạ bảng {@code khoan_thu}.
 */
public class KhoanThu {

    /** Khoản bắt buộc, vào danh sách công nợ khi chốt tháng. */
    public static final int BAT_BUOC = 0;
    /** Khoản tự nguyện (quỹ), đóng bao nhiêu ghi bấy nhiêu. */
    public static final int TU_NGUYEN = 1;

    private int maKhoanThu;
    private String tenKhoanThu;
    private int loaiKhoanThu;
    private double soTien;
    private String donViTinh;
    private Date ngayTao;
    private Date thoiHan;
    private String ghiChu;

    /** Tạo khoản thu rỗng. */
    public KhoanThu() {
    }

    /**
     * Tạo khoản thu mới, chưa có mã.
     *
     * @param tenKhoanThu tên khoản thu
     * @param soTien đơn giá hoặc số tiền cố định
     * @param loaiKhoanThu {@link #BAT_BUOC} hoặc {@link #TU_NGUYEN}
     */
    public KhoanThu(String tenKhoanThu, double soTien, int loaiKhoanThu) {
        this.tenKhoanThu = tenKhoanThu;
        this.soTien = soTien;
        this.loaiKhoanThu = loaiKhoanThu;
    }

    /**
     * Tạo khoản thu đã có mã.
     *
     * @param maKhoanThu mã khoản thu
     * @param tenKhoanThu tên khoản thu
     * @param soTien đơn giá hoặc số tiền cố định
     * @param loaiKhoanThu {@link #BAT_BUOC} hoặc {@link #TU_NGUYEN}
     */
    public KhoanThu(int maKhoanThu, String tenKhoanThu, double soTien,
            int loaiKhoanThu) {
        this(tenKhoanThu, soTien, loaiKhoanThu);
        this.maKhoanThu = maKhoanThu;
    }

    public int getMaKhoanThu() {
        return maKhoanThu;
    }

    public void setMaKhoanThu(int maKhoanThu) {
        this.maKhoanThu = maKhoanThu;
    }

    public String getTenKhoanThu() {
        return tenKhoanThu;
    }

    public void setTenKhoanThu(String tenKhoanThu) {
        this.tenKhoanThu = tenKhoanThu;
    }

    public int getLoaiKhoanThu() {
        return loaiKhoanThu;
    }

    public void setLoaiKhoanThu(int loaiKhoanThu) {
        this.loaiKhoanThu = loaiKhoanThu;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getThoiHan() {
        return thoiHan;
    }

    public void setThoiHan(Date thoiHan) {
        this.thoiHan = thoiHan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
