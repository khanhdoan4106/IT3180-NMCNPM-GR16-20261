package models;

import java.sql.Date;


public class KhoanThuModel {
    private int maKhoanThu;
    private String tenKhoanThu;
    private int loaiKhoanThu; // 0: Bắt buộc, 1: Tự nguyện
    private double soTien;
    private String donViTinh;
    private Date ngayTao;
    private Date thoiHan;
    private String ghiChu;

    public KhoanThuModel() {
    }

    public KhoanThuModel(String tenKhoanThu, double soTien, int loaiKhoanThu) {
        this.tenKhoanThu = tenKhoanThu;
        this.soTien = soTien;
        this.loaiKhoanThu = loaiKhoanThu;
    }

    public KhoanThuModel(int maKhoanThu, String tenKhoanThu, double soTien, int loaiKhoanThu) {
        this.maKhoanThu = maKhoanThu;
        this.tenKhoanThu = tenKhoanThu;
        this.soTien = soTien;
        this.loaiKhoanThu = loaiKhoanThu;
    }

    public KhoanThuModel(int maKhoanThu, String tenKhoanThu, int loaiKhoanThu, double soTien,
                         String donViTinh, Date ngayTao, Date thoiHan, String ghiChu) {
        this.maKhoanThu = maKhoanThu;
        this.tenKhoanThu = tenKhoanThu;
        this.loaiKhoanThu = loaiKhoanThu;
        this.soTien = soTien;
        this.donViTinh = donViTinh;
        this.ngayTao = ngayTao;
        this.thoiHan = thoiHan;
        this.ghiChu = ghiChu;
    }

    public int getMaKhoanThu() { return maKhoanThu; }
    public void setMaKhoanThu(int maKhoanThu) { this.maKhoanThu = maKhoanThu; }

    public String getTenKhoanThu() { return tenKhoanThu; }
    public void setTenKhoanThu(String tenKhoanThu) { this.tenKhoanThu = tenKhoanThu; }

    public int getLoaiKhoanThu() { return loaiKhoanThu; }
    public void setLoaiKhoanThu(int loaiKhoanThu) { this.loaiKhoanThu = loaiKhoanThu; }

    public double getSoTien() { return soTien; }
    public void setSoTien(double soTien) { this.soTien = soTien; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getThoiHan() { return thoiHan; }
    public void setThoiHan(Date thoiHan) { this.thoiHan = thoiHan; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

}
