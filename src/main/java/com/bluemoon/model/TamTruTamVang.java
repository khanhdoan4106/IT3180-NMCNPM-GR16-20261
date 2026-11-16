package com.bluemoon.model;

import java.sql.Date;

/**
 * Một đợt tạm trú hoặc tạm vắng của nhân khẩu (US-003). Ánh xạ bảng
 * {@code tam_tru_tam_vang}.
 */
public class TamTruTamVang {

    /** Giá trị {@link #getTrangThai()} của đợt tạm trú. */
    public static final String TAM_TRU = "Tạm trú";
    /** Giá trị {@link #getTrangThai()} của đợt tạm vắng. */
    public static final String TAM_VANG = "Tạm vắng";

    private int id;
    private int idNhanKhau;
    private String maGiay;
    private String trangThai;
    private String diaChiTamTruVang;
    private Date tuNgay;
    private Date denNgay;
    private String noiDungDeNghi;

    /** Tạo đợt rỗng. */
    public TamTruTamVang() {
    }

    /**
     * Tạo đợt tạm trú hoặc tạm vắng đầy đủ thông tin.
     *
     * @param id mã đợt
     * @param idNhanKhau mã nhân khẩu
     * @param maGiay số giấy tạm trú/tạm vắng
     * @param trangThai {@link #TAM_TRU} hoặc {@link #TAM_VANG}
     * @param diaChiTamTruVang địa chỉ tạm trú hoặc nơi đến khi tạm vắng
     * @param tuNgay ngày bắt đầu
     * @param denNgay ngày kết thúc
     * @param noiDungDeNghi nội dung đề nghị
     */
    public TamTruTamVang(int id, int idNhanKhau, String maGiay,
            String trangThai, String diaChiTamTruVang, Date tuNgay,
            Date denNgay, String noiDungDeNghi) {
        this.id = id;
        this.idNhanKhau = idNhanKhau;
        this.maGiay = maGiay;
        this.trangThai = trangThai;
        this.diaChiTamTruVang = diaChiTamTruVang;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.noiDungDeNghi = noiDungDeNghi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdNhanKhau() {
        return idNhanKhau;
    }

    public void setIdNhanKhau(int idNhanKhau) {
        this.idNhanKhau = idNhanKhau;
    }

    public String getMaGiay() {
        return maGiay;
    }

    public void setMaGiay(String maGiay) {
        this.maGiay = maGiay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getDiaChiTamTruVang() {
        return diaChiTamTruVang;
    }

    public void setDiaChiTamTruVang(String diaChiTamTruVang) {
        this.diaChiTamTruVang = diaChiTamTruVang;
    }

    public Date getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(Date tuNgay) {
        this.tuNgay = tuNgay;
    }

    public Date getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(Date denNgay) {
        this.denNgay = denNgay;
    }

    public String getNoiDungDeNghi() {
        return noiDungDeNghi;
    }

    public void setNoiDungDeNghi(String noiDungDeNghi) {
        this.noiDungDeNghi = noiDungDeNghi;
    }
}
