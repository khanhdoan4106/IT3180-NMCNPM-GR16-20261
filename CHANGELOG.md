# Nhật ký thay đổi

Mọi thay đổi đáng chú ý của dự án được ghi ở đây, theo
[Keep a Changelog](https://keepachangelog.com/vi/1.1.0/).

## [Chưa phát hành]

### Thêm

- US-002: form thêm nhân khẩu có loại cư trú. Người tạm trú bắt buộc
  nhập nơi thường trú và thời hạn; người và đợt tạm trú đầu tiên lưu
  trong một giao dịch (giả định 8, 9). Danh sách có cột Loại cư trú và
  Nơi thường trú; cột Quan hệ hiện chữ thay cho mã.
- US-002: lưu được quan hệ "Bố/Mẹ" (thêm `BO_ME` vào `nhan_khau.quan_he`).
  Cần nâng cấp CSDL, xem `docs/setup.md` mục 7.
- US-002: chặn ngày sinh ở tương lai.
- TS-002: nhật ký thao tác. Màn hình Nhật ký chỉ đọc cho Quản trị viên,
  mới nhất ở trên, cột Thao tác hiện nhãn tiếng Việt từ enum `ThaoTac`
  (khớp danh sách mã trong `database/schema.sql`). Ghi nhật ký cùng
  transaction với thao tác: lỗi ghi nhật ký thì thao tác rollback. Đã
  tích hợp thêm hộ, thêm nhân khẩu, ghi tạm trú/tạm vắng và các thay đổi
  tài khoản (tạo, sửa thông tin, đổi mật khẩu, đổi vai trò, khóa/mở khóa,
  đặt lại mật khẩu); nhật ký không lưu mật khẩu rõ hay chuỗi băm. Các
  nghiệp vụ còn lại chưa có API nên chưa tích hợp. Cần nâng cấp CSDL cũ
  bằng `database/migration_ts002_tai_khoan.sql` (thêm hai mã
  `SUA_THONG_TIN_TAI_KHOAN`, `DOI_MAT_KHAU`).

## [v0.1.0] – 23/11/2026 – Sprint 1

Kết thúc Sprint 1. Gồm cả US-022 và US-023 của Sprint 2 đã làm xong sớm.

### Thêm

- US-001: màn hình Hộ gia đình (nhóm menu Hộ gia đình): xem danh sách hộ
  đang ở, tìm theo mã căn hộ hoặc tên chủ hộ (không phân biệt hoa thường
  và dấu), thêm hộ mới kèm chủ hộ trong cùng một giao dịch.
- US-002: màn hình Nhân khẩu: thêm nhân khẩu vào hộ kèm quan hệ với chủ
  hộ; số CCCD 12 chữ số, không trùng với người đang ở, bắt buộc với người
  từ đủ 14 tuổi.
- US-003: ghi nhận tạm trú, tạm vắng của nhân khẩu kèm ngày bắt đầu, ngày
  kết thúc và lý do; có kiểm thử tích hợp trên MySQL.
- TS-001: script PowerShell sao lưu CSDL MySQL `bluemoon` bằng
  `mysqldump`, lưu file có dấu thời gian, log kết quả, khóa chống chạy
  đồng thời, bỏ qua lần chạy tiếp theo trong ngày sau khi đã sao lưu thành
  công và giữ tối đa 7 bản gần nhất. Thông tin đăng nhập lấy từ file
  `backup.cnf` bên ngoài repository; hướng dẫn cài đặt, chạy và khôi phục
  tại `docs/backup-restore.md`. Cơ chế Startup hiện kích hoạt khi đăng nhập
  Windows, chưa bảo đảm chạy đúng giờ khi người dùng chưa đăng nhập.
- US-022: phân quyền hai lớp. Menu chỉ hiện màn hình vai trò được dùng;
  mở màn không có quyền thì về Trang chính kèm câu báo. Mỗi Service kiểm
  lại vai trò qua `PhanQuyen` (bảng `docs/design/phan-quyen.md`). Trước
  mỗi lần mở màn hình, ứng dụng đọc lại tài khoản: vừa bị khóa thì đăng
  xuất kèm câu báo, vừa đổi vai trò thì menu và thanh đầu đổi theo.
  Thủ quỹ tạm không dùng các chức năng chờ chốt họp 22/11 (US-005,
  US-008, US-009, US-018, thẻ tổng thu tháng).
- US-022: tab "Quản lý tài khoản" (chỉ Quản trị viên): danh sách tài
  khoản, đổi vai trò, khóa/mở khóa, đặt lại mật khẩu tạm hiện một lần.
  Không tự khóa, không tự đổi vai trò của mình; luôn còn ít nhất một
  Quản trị viên đang hoạt động, kể cả khi hai máy thao tác cùng lúc.
- US-023: màn Tài khoản chia hai tab. Tab "Tài khoản của tôi" cho mọi
  người dùng xem tên đăng nhập, vai trò; sửa họ tên và số điện thoại
  (10 chữ số, bắt đầu bằng 0, để trống được); đổi mật khẩu khi nhập đúng
  mật khẩu hiện tại, mật khẩu mới ít nhất 8 ký tự, nhập lại khớp và khác
  mật khẩu cũ. Lỗi báo ngay dưới ô; đổi họ tên thì thanh đầu cửa sổ đổi
  theo. Tab "Tạo tài khoản" giữ chức năng của US-020 và chỉ Quản trị viên
  thấy; Service cũng từ chối tạo tài khoản khi người đăng nhập không phải
  Quản trị viên (giả định 2, AC-061).
- US-021: ứng dụng mở lên là màn đăng nhập; đăng nhập đúng thì vào cửa
  sổ chính, thanh đầu hiện họ tên, vai trò và nút Đăng xuất. Sai tên hay
  sai mật khẩu đều báo chung "Tên đăng nhập hoặc mật khẩu không đúng.";
  tài khoản bị khóa không đăng nhập được. Chưa đăng nhập thì không mở được
  màn hình chức năng nào (AC-059). Người đang đăng nhập giữ ở
  `PhienDangNhap` để US-022 phân quyền.
- US-020: màn hình Tài khoản (menu Hệ thống) để Quản trị viên tạo tài
  khoản, chọn vai trò Quản trị viên hoặc Thủ quỹ; báo trùng tên đăng nhập
  (không phân biệt chữ hoa, chữ thường) và mật khẩu dưới 8 ký tự ngay dưới
  ô sai. Mật khẩu băm BCrypt cost 12 (thư viện `at.favre.lib:bcrypt`),
  cùng chuẩn với tài khoản admin trong `seed.sql`.
- TS-003: lớp khởi động `Launcher` và `App`; khi mở, ứng dụng thử kết nối
  MySQL, lỗi thì báo nguyên nhân và cách sửa bằng tiếng Việt rồi thoát
  (AC-070). Lỗi CSDL dùng chung lớp `LoiCsdl`.
- TS-003: cửa sổ chính bố cục trái – đầu – thân, menu trái nhóm theo mảng;
  mục chưa có màn hình mở trang "Chức năng đang được xây dựng". Danh sách
  màn hình đăng ký một chỗ ở `ManHinh`; kiểu dùng chung `giao-dien.css`
  theo `docs/design/ui.md`.
- TS-003: trang chính hiện số hộ và số nhân khẩu đang ở, nút "Tải lại";
  là mẫu View → Controller → Service → DAO → MySQL cho cả nhóm. Kiểm thử
  tích hợp chứng minh dữ liệu một máy ghi thì máy khác thấy (AC-071).
  Tài liệu `docs/design/kien-truc.md`.

### Hạn chế đã biết

- US-002: chọn quan hệ "Bố/Mẹ" lưu bị lỗi, cột Quan hệ hiện mã thay vì
  chữ, chưa có loại cư trú và nơi thường trú (giả định 8), chưa chặn ngày
  sinh ở tương lai. Sửa trong Sprint 2.
- TS-001: sao lưu chạy khi người dùng đăng nhập Windows, chưa chạy đúng
  giờ khi máy chưa có ai đăng nhập.
- US-006 (tạo khoản thu tự nguyện) chưa vào bản này, chuyển sang Sprint 2.

[Chưa phát hành]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/compare/v0.1.0...main
[v0.1.0]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/releases/tag/v0.1.0
