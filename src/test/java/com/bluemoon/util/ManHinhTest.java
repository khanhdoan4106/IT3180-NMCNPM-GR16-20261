package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.VaiTro;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Giữ danh sách màn hình đúng khi mỗi người thêm dòng của mình. */
class ManHinhTest {

    @Test
    void tepFxmlKhaiBaoDeuCo() {
        for (ManHinh manHinh : ManHinh.values()) {
            String tep = manHinh.getTepFxml();
            if (tep != null) {
                assertNotNull(ManHinh.class.getResource(
                        "/com/bluemoon/view/" + tep),
                        manHinh + ": không có tệp view/" + tep);
            }
        }
    }

    @Test
    void mucCungNhomDungLienNhau() {
        Set<NhomMenu> daQua = EnumSet.noneOf(NhomMenu.class);
        NhomMenu nhomTruoc = null;
        for (ManHinh manHinh : ManHinh.values()) {
            NhomMenu nhom = manHinh.getNhom();
            if (nhom != nhomTruoc && nhom != null) {
                assertFalse(daQua.contains(nhom),
                        manHinh + ": đặt cạnh các mục khác của nhóm " + nhom);
                daQua.add(nhom);
            }
            nhomTruoc = nhom;
        }
    }

    /**
     * US-022: bảng quyền khớp docs/design/phan-quyen.md mục 1.1. Các dòng
     * chờ chốt họp 22/11 (US-005, US-008, US-009, US-018) tạm không cho
     * Thủ quỹ theo AC-060.
     */
    @Test
    void bangQuyenKhopPhanQuyenMd() {
        Set<ManHinh> thuQuy = EnumSet.of(ManHinh.TRANG_CHINH,
                ManHinh.THU_PHI, ManHinh.PHIEU_THU, ManHinh.TAI_KHOAN);
        for (ManHinh manHinh : ManHinh.values()) {
            assertTrue(manHinh.duocMo(VaiTro.QUAN_TRI), manHinh.name());
            assertEquals(thuQuy.contains(manHinh),
                    manHinh.duocMo(VaiTro.THU_QUY), manHinh.name());
            assertFalse(manHinh.duocMo(null), manHinh.name());
        }
    }
}
