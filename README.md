# BlueMoon – Phần mềm quản lý chung cư (IT3180 – Nhóm 16)

Đồ án môn Nhập môn Công nghệ phần mềm (IT3180), Đại học Bách khoa Hà Nội.

## Bài toán (v1.0)

Ban quản trị chung cư BlueMoon đang quản lý hộ khẩu bằng sổ và Excel, thu
phí bằng sổ ghi tay: tính phí theo diện tích dễ nhầm, tìm hộ chậm, cộng tổng
sai, không biết ai đã sửa gì. Phần mềm v1.0 cần:

- Quản lý hộ gia đình, nhân khẩu, tạm trú/tạm vắng; xuất danh sách tạm vắng
  cho công an phường.
- Tự tính phí dịch vụ (2.500–16.500 đ/m²) và phí quản lý (7.000 đ/m²) theo
  diện tích; tạo các quỹ tự nguyện theo đợt; chốt công nợ hằng tháng.
- Tra cứu theo mã phòng, "Thu tất cả", chọn tiền mặt/chuyển khoản, tự lưu
  người thu, in biên lai, hủy phiếu thu nhầm trong ngày.
- Tổng thu tháng trên màn hình chính, lọc hộ nợ quá 3 tháng, thống kê theo
  quỹ, xuất Excel.
- Đăng nhập, phân quyền Quản trị viên / Thủ quỹ, đổi mật khẩu.

Phí gửi xe và thu hộ điện, nước, internet để v2.0. Chi tiết yêu cầu:
[docs/requirements](docs/requirements).

## Công nghệ

- Java 21, JavaFX 21 (ứng dụng máy tính để bàn), Maven
- MySQL 8.0 (CSDL tập trung)
- JUnit 5, Checkstyle; GitHub Actions chạy `mvn verify` cho mọi Pull Request

## Tài liệu

| Tài liệu | Nội dung |
|---|---|
| [docs/requirements](docs/requirements) | Wish list, As-Is, Visionary, NFR, raw log, epic/feature, user story và AC, product backlog, ma trận truy vết, Definition of Done, giả định |
| [docs/setup.md](docs/setup.md) | Cài môi trường, dựng CSDL, chạy test |
| [docs/workflow.md](docs/workflow.md) | Quy trình Scrum, nhánh, commit, Pull Request, phát hành |
| [docs/design/coding-convention.md](docs/design/coding-convention.md) | Quy ước viết mã |
| [docs/design/ui.md](docs/design/ui.md) | Quy tắc giao diện chung |
| [docs/thuat-ngu.md](docs/thuat-ngu.md) | Thuật ngữ thống nhất |
| [CHANGELOG.md](CHANGELOG.md) | Nhật ký thay đổi |

## Bắt đầu

1. Cài môi trường và dựng CSDL theo [setup.md](docs/setup.md).
2. Đọc [workflow.md](docs/workflow.md) trước khi nhận Issue đầu tiên.

## Thành viên

| Thành viên | Họ và tên | MSSV | Vai trò chính |
| :-- | :-- | :-- | :-- |
| TV1 | Đoàn Quốc Khánh | 202416243 | Quản lý dự án / Scrum Master |
| TV2 | Nguyễn Đình Dũng | 202416173 | Phân tích yêu cầu, Thiết kế UI, Dev |
| TV3 | Đinh Phi Hùng | 202416215 | Lập trình CSDL và nghiệp vụ |
| TV4 | Cao Thành Đạt | 202416152 | Lập trình giao diện JavaFX |
| TV5 | Vũ Nhật Huy | 202416229 | Kiểm thử và tài liệu |
