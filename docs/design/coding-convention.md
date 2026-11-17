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

## Tiện ích dùng chung

Các lớp trong `com.bluemoon.util` dùng cho mọi màn hình. **Không tự viết lại
định dạng ngày, diện tích hay bỏ dấu trong Controller, Service**; cần thêm
dạng mới thì thêm vào lớp tiện ích kèm test.

| Lớp | Dùng khi | Hàm chính |
|---|---|---|
| `Ngay` | Hiện ngày, đọc ngày người dùng gõ, gắn dạng cho ô ngày (`DatePicker`) | `dinhDang(LocalDate)` → "05/09/2026"; `doc(String)`; `ganDinhDang(DatePicker)` |
| `DienTich` | Hiện diện tích căn hộ trong bảng, form, thông báo phí | `dinhDang(BigDecimal)` → "1.234,50 m²" |
| `TimKiem` | Lọc danh sách theo ô tìm kiếm, không phân biệt hoa thường và dấu (giả định 40) | `boDau(String)`; `khop(giaTri, tuKhoa)` |
| `HopThoai` | Mở hộp thoại, `Alert` cùng giao diện với cửa sổ chính | `ganKieu(Dialog<?>)`; `baoLoi(tieuDe, noiDung)`; `baoThanhCong(tieuDe, noiDung)` |

Dạng hiển thị theo [ui.md](ui.md), mục "Cách viết chữ hiển thị". Lỗi nhập
liệu trong form vẫn báo ngay dưới ô (như màn Tài khoản); `HopThoai` dùng
cho lỗi không gắn với ô nào hoặc khi cần người dùng bấm "Đóng" mới đi tiếp.
