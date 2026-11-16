# Quy tắc giao diện chung

Quy tắc áp dụng cho mọi màn hình của BlueMoon. Mục tiêu chính là NFR-008:
người lớn tuổi đọc rõ và thao tác được (TS-004). Thiết kế từng màn hình do
nhóm vẽ trên Figma và lưu ở [docs/design](.).

## Bố cục

- Toàn ứng dụng dùng bố cục **trái – đầu – thân**: menu bên trái chia nhóm
  theo mảng (Hộ gia đình, Khoản thu, Thu phí, Thống kê, Hệ thống); thanh
  đầu có tên ứng dụng, người đăng nhập và Đăng xuất; màn hình chức năng ở
  phần thân. Menu trái thay cho thanh menu trên cùng vì 13 mục xếp dọc,
  mỗi mục một dòng, dễ đọc hơn với người lớn tuổi. Bố cục sẽ chỉnh theo
  thiết kế Figma (#70).
- Kích thước theo Bộ bài tập: màn hình chính 1440×1024; đăng nhập và các
  màn hình chức năng 1120×645. Mọi màn phải dùng được ở 1120×645, không mất
  nút hay bị cắt chữ.
- Menu chỉ hiện mục mà vai trò được dùng (US-022).
- Nút menu của màn đang mở có nền đậm; bấm logo để về màn hình chính.
- Hộp thoại mở đè lên màn hình hiện tại, nền phía sau làm tối, cùng kiểu
  với màn hình.
- Phím Enter trong ô nhập chỉ chạy nút của khung chứa ô đó, không chạy nút
  của khung khác trên cùng màn.
- Nhãn thông báo chỉ chiếm chỗ khi có chữ.
- Lỗi nhập liệu hiện ngay dưới ô sai, không dồn vào hộp cảnh báo chung.
- Form dài: phần ô nhập cuộn được, thông báo và hàng nút luôn nhìn thấy ở
  cuối khung.

## Bảng màu

Màu khai báo thành biến trong CSS (một bộ biến, mỗi phương án là một bộ
giá trị); không viết thẳng mã màu vào từng màn.

| Biến | A – Xanh dương | B – Xanh ngọc | C – Tương phản cao |
|---|---|---|---|
| màu chính | `#2563EB` | `#0F766E` | `#1E3A8A` |
| màu chính đậm | `#1D4ED8` | `#134E4A` | `#172554` |
| nền | `#F1F5F9` | `#F0FDFA` | `#F8FAFC` |
| bề mặt | `#FFFFFF` | `#FFFFFF` | `#FFFFFF` |
| chữ | `#0F172A` | `#134E4A` | `#020617` |
| chữ phụ | `#526077` | `#475569` | `#334155` |
| viền | `#CBD5E1` | `#99D5CC` | `#64748B` |
| lỗi | `#B91C1C` | `#B91C1C` | `#B91C1C` |
| thành công | `#166534` | `#15803D` | `#166534` |

Phương án C ưu tiên người lớn tuổi: chữ gần đen trên nền trắng, viền đậm.
Nhóm chọn một phương án ở Sprint Review.

Màu riêng cho biểu đồ công nợ (giống nhau ở cả ba phương án):

| Phần | Màu |
|---|---|
| Thu đúng hạn | màu chính đậm của phương án |
| Thu trễ hạn | `#D97706` |
| Còn nợ | nền `#E2E8F0`, viền màu chữ phụ |

## Tương phản

- Mọi cặp chữ/nền đạt tối thiểu **4,5:1** (WCAG AA): chữ chính, chữ phụ,
  chữ trắng trên nút chính và thanh menu, chữ lỗi, chữ thành công, chữ trên
  dòng chọn và dòng xen kẽ.
- Vì vậy chữ phụ dùng `#526077` (5,8:1 trên nền `#F1F5F9`), không dùng
  `#64748B` (4,3:1); lỗi dùng `#B91C1C`, không dùng `#DC2626` (4,1:1);
  thành công dùng `#166534`, không dùng `#15803D` trên nền xám nhạt.
- Thêm màu mới thì đo tương phản trước khi dùng.

## Chữ

Phông **Be Vietnam Pro** (thiết kế cho tiếng Việt, dấu rõ ở cỡ nhỏ), nhúng
từ `src/main/resources/com/bluemoon/fonts/` (giấy phép OFL đi kèm). Chữ gốc
16 px; **không chữ nào trên màn hình dưới 14 px**, kể cả tiêu đề cột bảng,
nhãn trục biểu đồ, dòng phụ, thông báo lỗi. Trang in giữ chữ 14 px.

| Kiểu | Cỡ / độ đậm | Dùng cho |
|---|---|---|
| Tên ứng dụng | 32 / Bold | "BlueMoon" ở màn đăng nhập |
| Tiêu đề trang | 24 / Bold | Tiêu đề màn hình |
| Tiêu đề khung | 20 / Bold | Tiêu đề khung, form, hộp thoại |
| Số lớn | 34 / Bold | Thẻ số liệu |
| Nội dung | 16 / Regular | Dữ liệu trong bảng, ô nhập, nhãn |
| Nhãn bảng | 15 / Bold | Tiêu đề cột |
| Chú thích | 14–16 / Regular | Ghi chú, gợi ý dưới ô, trục biểu đồ |
| Nút | 16 / Bold | Chữ trên nút |

JavaFX coi Medium và SemiBold là họ phông riêng, nên trong code dùng Bold
cùng họ "Be Vietnam Pro" ở chỗ Figma dùng Medium/SemiBold.

## Nút và ô nhập

| Thành phần | Quy tắc |
|---|---|
| Nút chính | Nền màu chính, chữ trắng đậm, bo 8, cao ≥ 44; rê chuột nền màu chính đậm. Nút mặc định của hộp thoại cùng kiểu |
| Nút phụ | Nền trắng, viền màu viền, chữ màu chữ đậm, bo 8, cao ≥ 44; nút không có kiểu riêng là nút phụ |
| Nút nguy hiểm | Nền trắng, viền và chữ màu lỗi (vd "Hủy phiếu", "Đánh dấu chuyển đi") |
| Khoảng cách | Hai nút cạnh nhau cách ≥ 8 px |
| Ô nhập, danh sách chọn, ô ngày | Nền trắng, viền 1 px, bo 8, cao ≥ 40, chữ 16; chữ gợi ý màu chữ phụ |
| Tiêu điểm | Luôn nhìn thấy: viền 2 px màu chính (ô nhập, bảng, nút phụ), vòng ngoài cách 2 px (nút chính, nút nguy hiểm). Ô không đổi kích thước khi có tiêu điểm |
| Bảng | Hàng tiêu đề nền màu nền, chữ phụ 15 đậm; dòng cao 40, xen kẽ trắng / xám nhạt; dòng chọn có vạch màu chính bên trái; bảng trống có câu báo tiếng Việt |
| Cột bảng | Cột nội dung cố định (mã, số biên lai, ngày giờ, số tiền, số điện thoại, trạng thái) đủ rộng để không bị rút gọn "…" ở 1120×645; cột số và tiền căn phải |
| Tab | Tab đang chọn chữ màu chính đậm, gạch dưới 3 px; tab thường chữ phụ |

## Quy tắc biểu đồ

- **Trục X:** có tên trục (vd "Tháng (năm 2026)") và nhãn ở từng mốc.
- **Trục Y:** có đơn vị (vd "Triệu đồng") đặt nằm ngang ở đầu trục, không
  xoay chữ (JavaFX vẽ sai dấu tiếng Việt khi xoay); biểu đồ cột có thang
  chia đều, bắt đầu từ 0, có vạch chia và lưới ngang mờ.
- **Cột đúng tỷ lệ** với thang Y, không cắt gốc, không phóng đại; cột chồng
  thì các phần cùng đơn vị, cộng lại đúng chiều cao cột. Ghi số liệu trên
  đầu cột khi cần.
- **Bề rộng cột** khoảng 45% khoảng cách giữa hai mốc, tối đa 72 px.
- **Chú giải** khi có từ hai chuỗi dữ liệu, đặt dưới biểu đồ. Phần "còn
  thiếu" dùng màu trung tính nhạt có viền, không dùng đỏ đặc.
- **Kỳ chưa kết thúc** ghi rõ bằng chú thích (vd "Tháng 11 đang thu, tính
  đến hôm nay"); kỳ chưa có số liệu ghi chữ tại vị trí của kỳ (vd "Chưa
  chốt"), không vẽ cột 0.
- **Di chuột** lên cột hiện tooltip có đủ số liệu của cột.
- **Không dùng hai trục Y.**

## Cách viết chữ hiển thị

- Mọi chữ người dùng thấy (nhãn, nút, thông báo, tiêu đề cột) bằng **tiếng
  Việt có dấu**; dùng đúng tên trong [thuật ngữ](../thuat-ngu.md) (vd "Thủ
  quỹ", không viết "Nhân viên thu phí").
- Nút hộp thoại: "Đồng ý", "Hủy", "Đóng"; nút làm việc nguy hiểm ghi rõ
  việc đó ("Hủy phiếu", không chỉ "Đồng ý").
- Thông báo lỗi nói rõ sai ở đâu và cách sửa, không hiện mã lỗi hay chữ
  tiếng Anh của hệ thống (vd "Ngày kết thúc không được trước ngày bắt
  đầu").
- Số tiền có dấu chấm ngăn cách hàng nghìn và đơn vị: "1.250.000 đ".
- Diện tích luôn hai chữ số lẻ, dấu phẩy thập phân, kèm đơn vị: "75,50 m²";
  ô nhập nhận cả dấu phẩy lẫn dấu chấm.
- Ngày dạng `dd/MM/yyyy`; ô ngày trống có gợi ý "ngày/tháng/năm".
- Bảng trống ghi câu báo cụ thể (vd "Chưa có phiếu thu hôm nay"), không để
  trống trơn.
