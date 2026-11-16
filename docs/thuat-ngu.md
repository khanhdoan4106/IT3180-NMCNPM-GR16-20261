# Thuật ngữ

Một khái niệm chỉ dùng một tên trong tài liệu, giao diện và mã nguồn.

| Thuật ngữ | Nghĩa |
|---|---|
| Ban quản trị (BQT) | Ban do cư dân bầu, điều hành chung cư; dùng phần mềm với vai trò Quản trị viên |
| Thủ quỹ | Người của BQT phụ trách thu phí; tương ứng tác nhân Kế toán trong Bộ bài tập. Tài liệu cũ gọi là Nhân viên thu phí |
| Hộ gia đình | Những người cùng ở một căn hộ, kiot hoặc penthouse; mỗi hộ có đúng một chủ hộ |
| Nhân khẩu | Một người thuộc hộ, kể cả người tạm trú |
| Đang ở / Đã chuyển đi | Trạng thái của hộ và nhân khẩu; không xóa, chỉ đổi trạng thái |
| Đợt tạm trú / tạm vắng | Một khoảng thời gian có ngày bắt đầu và kết thúc |
| Khoản thu | Một mục trong danh mục thu: bắt buộc (phí dịch vụ, phí quản lý) hoặc tự nguyện (các quỹ) |
| Đợt (khoản tự nguyện) | Thời gian thu của một khoản tự nguyện, từ ngày – đến ngày |
| Chốt công nợ | Tạo khoản phải nộp tháng này cho mọi hộ đang ở, theo diện tích × đơn giá |
| Khoản phải nộp | Số tiền một hộ phải nộp cho một khoản bắt buộc trong một tháng |
| Công nợ | Tổng các khoản phải nộp chưa đóng của một hộ |
| Phiếu thu | Một lần thu tiền của hộ, có thể gồm nhiều khoản; có hình thức thanh toán và người thu |
| Mã phòng | Cách gọi khác của mã căn hộ ở quầy thu |
| Số biên lai | Số của phiếu thu, dạng `PT-<năm>-<5 chữ số>`, đánh lại mỗi năm; phiếu hủy giữ số |
| Biên lai | Bản in của phiếu thu đưa cho cư dân |
| Đóng góp tự nguyện | Số tiền hộ đóng cho một quỹ (khoản thu tự nguyện); đóng nhiều lần được, ghi 0 đ là "không đóng" |
| Quỹ | Khoản thu tự nguyện có thời gian thu riêng (vd Quỹ vì người nghèo) |
| Thông báo phí | Giấy báo các khoản hộ cần đóng đến hết một tháng đã chốt, in cho từng hộ |
| Hủy phiếu | Hủy phiếu thu nhầm trong ngày; khoản phải nộp trở lại chưa đóng, phiếu vẫn được lưu |

## Thuật ngữ kỹ thuật

Dùng trong mã nguồn và tài liệu thiết kế; chi tiết ở
[kiến trúc mã nguồn](design/kien-truc.md).

| Thuật ngữ | Nghĩa |
|---|---|
| DAO | Lớp đọc, ghi cơ sở dữ liệu bằng câu lệnh SQL; phần khác của chương trình gọi DAO, không tự viết SQL. Xem [mục 7](design/kien-truc.md#7-đối-chiếu-với-kiến-trúc-mvc-của-môn-học) |
| Service | Lớp chứa quy tắc nghiệp vụ, vd kiểm tra dữ liệu nhập trước khi lưu; lấy và ghi dữ liệu qua DAO. Xem [mục 1](design/kien-truc.md#1-các-tầng) |
| Controller | Lớp gắn với một màn hình: nhận thao tác của người dùng (bấm nút, nhập ô), gọi Service rồi hiện kết quả. Xem [mục 1](design/kien-truc.md#1-các-tầng) |
| DAO giả | DAO viết riêng cho test, giữ dữ liệu trong bộ nhớ thay cho MySQL để test Service chạy nhanh trên máy nào cũng được. Xem [mục 7](design/kien-truc.md#7-đối-chiếu-với-kiến-trúc-mvc-của-môn-học) |
| Kiểm thử tích hợp | Test chạy trên MySQL thật để kiểm câu SQL của DAO; chạy ở CI, không chạy trong `mvn verify` thường. Xem [mục 6](design/kien-truc.md#6-kiểm-thử-tích-hợp) |
