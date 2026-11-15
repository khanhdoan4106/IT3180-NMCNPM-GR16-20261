package models;

import java.sql.Date;

public class TamTruTamVangModel {
    private int id;
    private int idNhanKhau;
    private String maGiay;
    private String trangThai; // "Tạm trú" hoặc "Tạm vắng"
    private String diaChiTamTruVang;
    private Date tuNgay;
    private Date denNgay;
    private String noiDungDeNghi;

    public TamTruTamVangModel() {
    }

    public TamTruTamVangModel(int id, int idNhanKhau, String maGiay, String trangThai,
                              String diaChiTamTruVang, Date tuNgay, Date denNgay, String noiDungDeNghi) {
        this.id = id;
        this.idNhanKhau = idNhanKhau;
        this.maGiay = maGiay;
        this.trangThai = trangThai;
        this.diaChiTamTruVang = diaChiTamTruVang;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.noiDungDeNghi = noiDungDeNghi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdNhanKhau() { return idNhanKhau; }
    public void setIdNhanKhau(int idNhanKhau) { this.idNhanKhau = idNhanKhau; }

    public String getMaGiay() { return maGiay; }
    public void setMaGiay(String maGiay) { this.maGiay = maGiay; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getDiaChiTamTruVang() { return diaChiTamTruVang; }
    public void setDiaChiTamTruVang(String diaChiTamTruVang) { this.diaChiTamTruVang = diaChiTamTruVang; }

    public Date getTuNgay() { return tuNgay; }
    public void setTuNgay(Date tuNgay) { this.tuNgay = tuNgay; }

    public Date getDenNgay() { return denNgay; }
    public void setDenNgay(Date denNgay) { this.denNgay = denNgay; }

    public String getNoiDungDeNghi() { return noiDungDeNghi; }
    public void setNoiDungDeNghi(String noiDungDeNghi) { this.noiDungDeNghi = noiDungDeNghi; }
}