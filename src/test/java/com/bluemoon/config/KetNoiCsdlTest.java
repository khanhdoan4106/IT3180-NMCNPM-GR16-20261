package com.bluemoon.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KetNoiCsdlTest {

    @TempDir
    Path thuMuc;

    @Test
    void docDuCauHinh() throws IOException, SQLException {
        Path tep = thuMuc.resolve("db.properties");
        Files.writeString(tep, "url=jdbc:mysql://localhost:3306/bluemoon\n"
                + "user=bluemoon\npassword=mk\n");
        Properties cauHinh = KetNoiCsdl.docCauHinh(tep);
        assertEquals("bluemoon", cauHinh.getProperty("user"));
    }

    @Test
    void thieuTepThiBaoLoi() {
        assertThrows(SQLException.class,
                () -> KetNoiCsdl.docCauHinh(thuMuc.resolve("khong-co")));
    }

    @Test
    void thieuUrlThiBaoLoi() throws IOException {
        Path tep = thuMuc.resolve("db.properties");
        Files.writeString(tep, "user=bluemoon\n");
        assertThrows(SQLException.class, () -> KetNoiCsdl.docCauHinh(tep));
    }
}
