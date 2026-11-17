# Quy ước viết mã Java

| Mục | Quy ước |
|---|---|
| Định dạng | Thụt lề 4 dấu cách, không dùng tab; mỗi dòng tối đa 80 cột; luôn dùng `{}` cho `if/for/while` |
| Đặt tên | Lớp `PascalCase`, phương thức và biến `camelCase`, hằng `UPPER_SNAKE` |
| Tên lớp theo tầng | Thêm hậu tố theo tầng: `Controller`, `Service`, `Dao` (interface), `DaoJdbc` (bản JDBC), `DaoGia` (DAO giả trong test), `IT` (kiểm thử tích hợp); xem [kien-truc.md, mục 3](kien-truc.md#3-đặt-tên) |
| Ngôn ngữ trong mã | Tên lớp, biến, phương thức nghiệp vụ bằng tiếng Việt không dấu (`HoGiaDinh`, `tinhTongTien`), nhất quán toàn dự án |
| Chữ hiển thị cho người dùng | Tiếng Việt có dấu (nhãn, nút, thông báo lỗi) |
| Javadoc | Bắt buộc cho lớp và phương thức public; không bắt buộc cho getter và phương thức `@Override` đã có Javadoc ở interface |
| SQL | Luôn dùng `PreparedStatement`, không nối chuỗi dữ liệu người dùng vào câu lệnh |
| Dữ liệu nghiệp vụ | Không xóa cứng; đổi trạng thái để giữ lịch sử |
| Gỡ lỗi | Không để lại mã bị comment-out hay `System.out.println` |

## Kiểm tự động

Hai mục định dạng được Checkstyle kiểm trong `mvn verify` (cấu hình:
[`config/checkstyle.xml`](../../config/checkstyle.xml)), cho cả `src/main`
và `src/test`; CI trượt nếu vi phạm:

| Luật | Nội dung |
|---|---|
| `LineLength` | Mỗi dòng tối đa 80 cột, đếm theo **ký tự** (chữ có dấu tính một cột, không đếm theo byte). Dòng `package` và `import` không ngắt được nên được bỏ qua |
| `FileTabCharacter` | Không có ký tự tab |

Chỉ kiểm định dạng mà không chạy test: `mvn checkstyle:check@quy-uoc-ma`.
Dòng dài thì ngắt trước toán tử `+` hoặc sau dấu `(`, thụt thêm 8 dấu cách.
Các mục còn lại của bảng trên (đặt tên, Javadoc, SQL) vẫn kiểm khi review.

Quy trình nhánh, commit và review: [workflow.md](../workflow.md).

## Kiểm quyền

Phân quyền hai lớp (US-022, giả định 17): menu ẩn màn hình vai trò không
được dùng, và **mỗi hàm Service đọc hoặc ghi dữ liệu nghiệp vụ gọi
`PhanQuyen` trước khi gọi DAO**, theo bảng
[phan-quyen.md](phan-quyen.md). Không dựa vào việc menu đã ẩn.

| Trường hợp | Gọi |
|---|---|
| Chức năng nằm trong một màn hình | `PhanQuyen.yeuCau(ManHinh.X)`: vai trò lấy từ `ManHinh` |
| Thao tác chỉ Quản trị viên (quản lý tài khoản, xem nhật ký) | `PhanQuyen.chiQuanTriVien("Chỉ Quản trị viên được ...")` |
| Một phần của màn hình có quyền riêng | `PhanQuyen.yeuCau(tậpVaiTrò, câuBáo)`, tập vai trò ghi thành hằng trong `PhanQuyen` (vd `XEM_TONG_THU_THANG`) |

Ví dụ trong `CuDanService`:

```java
public SoLieuCuDan laySoLieu() {
    PhanQuyen.yeuCau(ManHinh.TRANG_CHINH);
    return cuDanDao.demDangO();
}
```

Không đủ quyền thì `PhanQuyen` ném `LoiKhongCoQuyen` (câu báo tiếng Việt),
Service không đọc, không ghi gì. Controller bắt lỗi này và hiện câu báo như
lỗi CSDL.

**Màn hình mới** phải khai báo vai trò được mở ngay trên dòng của nó trong
`ManHinh` (vd `HO_GIA_DINH(..., VaiTro.QUAN_TRI)`), đúng bảng
`phan-quyen.md`; `ManHinhTest.bangQuyenKhopPhanQuyenMd` kiểm bảng này.
Test Service đặt người đăng nhập bằng `PhienDangNhap.batDau(...)` và có ít
nhất một test vai trò không được phép bị từ chối.
