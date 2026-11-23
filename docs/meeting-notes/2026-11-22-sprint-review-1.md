# Sprint Review 1, Retrospective và Sprint Planning 2

- **Thời gian:** Chủ nhật 22/11/2026, 20:00 – 21:30 · **Hình thức:** trực
  tuyến
- **Thành phần:** Đoàn Quốc Khánh (TV1, Scrum Master), Nguyễn Đình Dũng
  (TV2), Đinh Phi Hùng (TV3), Cao Thành Đạt (TV4), Vũ Nhật Huy (TV5)
- **Vắng:** không
- **Thư ký:** Đoàn Quốc Khánh

## 1. Chương trình họp

Ba phần gộp một buổi, theo quyết định 6 của
[Sprint Planning 1](2026-11-16-sprint-planning-1.md).

| # | Nội dung | Thời lượng | Kết quả |
|---|---|---|---|
| 1 | Sprint Review 1: demo các story đã làm, đối chiếu Sprint Goal | 25 phút | Mục 2 |
| 2 | Retrospective Sprint 1 | 15 phút | Mục 3 |
| 3 | Các nội dung cần chốt | 30 phút | Mục 4 |
| 4 | Sprint Planning 2 | 20 phút | Mục 5 |

## 2. Sprint Review 1

### 2.1. Sprint Goal Sprint 1

> Người dùng có tài khoản đăng nhập được vào ứng dụng, quản lý được hộ gia
> đình, nhân khẩu và tạm trú/tạm vắng, tạo được khoản thu tự nguyện và có
> bản sao lưu CSDL định kỳ; dữ liệu lưu trên MySQL.

**Kết luận: đạt phần lớn Sprint Goal.** Đăng nhập, quản lý hộ gia đình, nhân
khẩu, tạm trú/tạm vắng đã chạy trên MySQL và được demo. Sao lưu CSDL và
tạo khoản thu tự nguyện đã xong và được duyệt, gộp vào `main` ở đầu
Sprint 2. Ngoài kế hoạch, nhóm đã làm xong sớm hai story của Sprint 2 là
phân quyền (US-022) và quản lý tài khoản (US-023).

### 2.2. Story của Sprint 1

| ID | Issue | Người phụ trách | SP | Kết quả demo | Pull Request |
|---|---|---|---|---|---|
| TS-003 | #34 | TV1 Khánh | 5 | Đạt: ứng dụng kết nối CSDL tập trung, hai máy thấy cùng dữ liệu | #77, #78, #79, #92 |
| US-020 | #28 | TV1 Khánh | 3 | Đạt | #81 |
| US-021 | #29 | TV1 Khánh | 3 | Đạt | #82 |
| US-001 | #7 | TV2 Dũng | 3 | Đạt | #85 |
| US-002 | #8 | TV2 Dũng | 5 | Đạt; nhóm góp ý thêm bốn điểm hoàn thiện (mục 6) | #98 |
| US-003 | #9 | TV2 Dũng | 5 | Đạt | #100 |
| US-006 | #14 | TV3 Hùng | 3 | Hoàn thành và đã được duyệt; merge đầu Sprint 2 | #97 |
| TS-001 | #32 | TV5 Huy | 5 | Hoàn thành và đã được duyệt: script sao lưu, hướng dẫn khôi phục; merge đầu Sprint 2 | #101 |
| **Tổng** | | | **32** | | |

Thiết kế Figma cho các story Sprint 1–2 (TV4 Đạt, #70): liên kết Figma đã
đưa vào repo (#99) để cả nhóm dùng chung khi dựng màn hình và góp ý.

Làm sớm của Sprint 2: US-022 Phân quyền (#94), US-023 Quản lý tài khoản
(#93), 8 SP.

### 2.3. Pull Request đã merge trong Sprint 1

Tất cả thay đổi vào `main` qua Pull Request, có người duyệt và CI xanh cả
hai job `build` và `build-mysql`.

| Nhóm việc | Pull Request |
|---|---|
| Nền tảng (TS-003): CI, CSDL MySQL, cấu trúc gói, cấu hình kết nối, khung giao diện, tiện ích dùng chung | #2, #37, #53, #55, #56, #66, #77, #78, #79, #92, #95 |
| CSDL: các trường bổ sung từ thiết kế của TV2 và dữ liệu mẫu | #57, #58, #59, #60, #61, #62, #63, #64 |
| Tài khoản: US-020, US-021, US-022, US-023 | #81, #82, #93, #94 |
| Hộ gia đình, nhân khẩu, tạm trú/tạm vắng: US-001, US-002, US-003 | #85, #98, #100 |
| Tài liệu yêu cầu và giả định | #38, #39, #40, #41, #42, #65, #90 |
| Quy trình, quy ước, hướng dẫn cài đặt, README | #43, #44, #45, #46, #47, #48, #49, #50, #51, #52, #71, #75, #76, #80 |
| Thiết kế, sơ đồ mô hình và biên bản | #69, #83, #84, #91, #99 |

## 3. Retrospective Sprint 1

| Câu hỏi | Ý kiến của nhóm |
|---|---|
| Điều làm tốt | Mọi thay đổi đi qua Pull Request, có người duyệt và CI; nhóm duyệt chéo cho nhau và góp ý cụ thể trên từng Pull Request. Story của mảng hộ gia đình có kiểm thử tích hợp trên MySQL. Tài liệu yêu cầu, giả định và sơ đồ mô hình được cả nhóm dùng chung khi làm. Thiết kế Figma có sớm để các màn hình làm theo cùng một bố cục. |
| Điều cần cải thiện | Sprint đầu cả nhóm mất thời gian làm quen môi trường (MySQL, Maven, quy trình Git), nên nhiều Pull Request mở vào cuối Sprint và có lúc chờ duyệt lâu. Bảng giao việc chưa được cập nhật đều hằng ngày. Mô tả Pull Request chưa thống nhất cách ghi Issue liên quan. |
| Việc thay đổi ở Sprint 2 | Mở Pull Request nháp ngay khi bắt đầu story để cả nhóm theo dõi; người được nhờ duyệt phản hồi trong 24 giờ. Daily 21:00 nhắn đủ ba câu và tự cập nhật trạng thái trong bảng giao việc. Mỗi Pull Request ghi `Closes #<Issue>` và cập nhật CHANGELOG. Story có phụ thuộc chéo (mục 5.3) merge phần nền trước giữa tuần. |

## 4. Nội dung đã chốt

Cả nhóm thảo luận từng mục và thống nhất như sau.

| # | Nội dung | Quyết định |
|---|---|---|
| 1 | Giả định 1–50 trong [11-chot-va-gia-dinh.md](../requirements/11-chot-va-gia-dinh.md) (giả định 50: mật khẩu tối thiểu 8 ký tự) | Giữ nguyên cả 50 giả định. Thêm giả định 51 về quyền của Thủ quỹ (mục 3 dưới đây). |
| 2 | Bảng chức năng – vai trò ([phan-quyen.md](../design/phan-quyen.md)) | Duyệt bảng mục 1, cập nhật các dòng "Chờ chốt" theo mục 3. Mỗi người gắn kiểm tra quyền vào Service của mình theo bảng. |
| 3 | Quyền Thủ quỹ với US-005, US-008, US-009, US-018 | Theo story, hiểu "thu phí" ở AC-060 gồm cả công việc trước và sau khi thu: Thủ quỹ được **sửa khoản phải nộp của hộ (US-008)**, **chốt công nợ (US-009)**, **xuất báo cáo thu (US-018)** và **xem thẻ tổng thu tháng** trên Trang chính. **Đổi đơn giá (US-005, AC-016)** chỉ Quản trị viên. Các chức năng quản lý hộ, nhân khẩu, thống kê theo khoản thu (US-019) và nợ đọng (US-017) giữ nguyên chỉ Quản trị viên. |
| 4 | UC011 "Tính phí theo diện tích" (US-005) | Đổi thành **"Cập nhật đơn giá phí"** (AC-016), tác nhân Quản trị viên. Việc tính diện tích × đơn giá là bước tự động của chốt công nợ (UC014). |
| 5 | Ghi lịch sử sao lưu (AC-066, giả định 43) | Ghi vào **tệp log** cạnh các bản sao lưu như TS-001 đang làm, không thêm bảng vào CSDL (bản sao lưu phải dùng được cả khi CSDL hỏng). Bổ sung hướng dẫn đặt lịch bằng Windows Task Scheduler để sao lưu chạy đúng giờ, trước bản `v1.0.0`. |
| 6 | Hạn sơ đồ chi tiết của thành viên (#86, #87, #88, #89) | Theo đề xuất trong Issue: use case nhóm và máy trạng thái trước 29/11; đặc tả, biểu đồ hoạt động, tuần tự trước 06/12. |
| 7 | Story Sprint 1 chưa merge | US-006 chuyển nguyên sang Sprint 2, SP vẫn tính cho Sprint 1. Bốn điểm hoàn thiện US-002 làm thành một Pull Request ở đầu Sprint 2. |
| 8 | Phát hành `v0.1.0` | Phát hành với các story đã merge trên `main`, không chờ US-006, theo [workflow.md](../workflow.md) mục 5. |

## 5. Sprint Planning 2

Sprint 2: Thứ Hai 23/11/2026 – Chủ nhật 29/11/2026, mốc `v0.2.0`. Kế hoạch
chi tiết ở Issue #72.

### 5.1. Story của Sprint 2

| ID | Issue | Mô tả ngắn | Người làm | SP | Cam kết |
|---|---|---|---|---|---|
| US-022 | #30 | Phân quyền Quản trị viên và Thủ quỹ | TV1 Khánh | 5 | Đã xong (#94) |
| US-023 | #31 | Đổi mật khẩu và cập nhật thông tin cá nhân | TV1 Khánh | 3 | Đã xong (#93) |
| US-027 | #12 | Lịch sử biến động nhân khẩu | TV2 Dũng | 5 | Có |
| US-026 | #11 | Đổi chủ hộ | TV2 Dũng | 3 | Có |
| US-005 | #13 | Tự tính phí dịch vụ, phí quản lý theo diện tích × đơn giá | TV3 Hùng | 5 | Có |
| US-009 | #17 | Tự tạo danh sách công nợ đầu tháng | TV3 Hùng | 5 | Có |
| TS-002 | #33 | Nhật ký thao tác | TV5 Huy | 5 | Có |
| **Tổng** | | | | **31** | |

Chuyển từ Sprint 1: US-006 (TV3 Hùng), hoàn thiện US-002 (TV2 Dũng). Không
tính SP: thiết kế Figma cho các story Sprint 3–4 (TV4 Đạt); sơ đồ mô hình
của thành viên (#86, #87, #88, #89).

### 5.2. Sprint Goal Sprint 2

> Ban quản trị phân quyền được Quản trị viên và Thủ quỹ, đổi được chủ hộ và
> xem lịch sử biến động của từng hộ. Thủ quỹ có danh sách công nợ hằng
> tháng tự tính theo diện tích, và mọi thay đổi quan trọng đều có nhật ký.

### 5.3. Phụ thuộc chéo

| Ai cần | Của ai | Cần gì | Trước ngày |
|---|---|---|---|
| Cả nhóm | TV1 Khánh | Bảng chức năng – vai trò đã chốt | 24/11 |
| Cả nhóm | TV5 Huy | Danh sách thao tác cần ghi nhật ký và cách ghi | 25/11 |
| TV2 Dũng | TV2 Dũng | US-027 merge trước để US-026 ghi lịch sử "Đổi chủ hộ" | 26/11 |
| TV3 Hùng | TV3 Hùng | US-005 merge trước khi làm US-009 | 26/11 |
| Cả nhóm | TV4 Đạt | Màn hình Figma cho các story Sprint 3 | 26/11 |

## 6. Việc cần làm

Theo dõi trạng thái trong trang "Sprint 2" của bảng giao việc.

| # | Việc | Người phụ trách | Hạn |
|---|---|---|---|
| 1 | Phát hành `v0.1.0`; cập nhật phan-quyen.md, giả định 51 và sơ đồ use case theo mục 4 | TV1 Khánh | 24/11 |
| 2 | Hoàn thiện US-002: lưu đúng quan hệ "Bố/Mẹ", cột Quan hệ hiện chữ, thêm loại cư trú và nơi thường trú (giả định 8), chặn ngày sinh ở tương lai; đóng #8, #9 | TV2 Dũng | 25/11 |
| 3 | Gộp `main` vào nhánh US-006 và merge #97 | TV3 Hùng | 24/11 |
| 4 | Gửi nhóm danh sách thao tác cần ghi nhật ký (TS-002) | TV5 Huy | 25/11 |
| 5 | Màn hình Figma Sprint 3 (tra cứu phí, thu tiền, đóng góp, sửa khoản thu, thông báo phí) và Sprint 4 | TV4 Đạt | 26/11, 29/11 |
| 6 | Hướng dẫn đặt lịch sao lưu bằng Task Scheduler (mục 4, ý 5) | TV5 Huy | trước `v1.0.0` |
| 7 | Sprint Review 2, Retrospective, Sprint Planning 3, phát hành `v0.2.0` | Cả nhóm | 29/11 |
