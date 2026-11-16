# Quy ước viết mã Java

| Mục | Quy ước |
|---|---|
| Định dạng | Thụt lề 4 dấu cách, không dùng tab; mỗi dòng tối đa 80 cột; luôn dùng `{}` cho `if/for/while` |
| Đặt tên | Lớp `PascalCase`, phương thức và biến `camelCase`, hằng `UPPER_SNAKE` |
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
