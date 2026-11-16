package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
