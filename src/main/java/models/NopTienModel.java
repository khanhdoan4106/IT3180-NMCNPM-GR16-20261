package models;

import java.sql.Timestamp;

public class NopTienModel {
    private int idNopTien;
    private int maKhoanThu;
    private int maHo;
    private double soTienPhaiThu;
    private double soTienDaNop;
    private Timestamp ngayThu;
    private String nguoiNop;
    private String hinhThucThanhToan;
    private int trangThai; // 0: Chưa nộp, 1: Đã nộp, 2: Đã hủy
    private String ghiChu;

    public NopTienModel() {
    }

    public NopTienModel(int maKhoanThu, int maHo, double soTienDaNop, String nguoiNop, String hinhThucThanhToan) {
        this.maKhoanThu = maKhoanThu;
        this.maHo = maHo;
        this.soTienDaNop = soTienDaNop;
        this.nguoiNop = nguoiNop;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.trangThai = 1;
    }

    public int getIdNopTien() { return idNopTien; }
    public void setIdNopTien(int idNopTien) { this.idNopTien = idNopTien; }

    public int getMaKhoanThu() { return maKhoanThu; }
    public void setMaKhoanThu(int maKhoanThu) { this.maKhoanThu = maKhoanThu; }

    public int getMaHo() { return maHo; }
    public void setMaHo(int maHo) { this.maHo = maHo; }

    public double getSoTienPhaiThu() { return soTienPhaiThu; }
    public void setSoTienPhaiThu(double soTienPhaiThu) { this.soTienPhaiThu = soTienPhaiThu; }

    public double getSoTienDaNop() { return soTienDaNop; }
    public void setSoTienDaNop(double soTienDaNop) { this.soTienDaNop = soTienDaNop; }

    public Timestamp getNgayThu() { return ngayThu; }
    public void setNgayThu(Timestamp ngayThu) { this.ngayThu = ngayThu; }

    public String getNguoiNop() { return nguoiNop; }
    public void setNguoiNop(String nguoiNop) { this.nguoiNop = nguoiNop; }

    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}