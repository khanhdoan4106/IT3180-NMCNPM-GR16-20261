# Sprint Review 1, Retrospective và Sprint Planning 2

> **Dự thảo**, chuẩn bị trước buổi họp. Số liệu Sprint 1 lấy từ Issue và
> Pull Request trên GitHub ngày 17/11/2026; cập nhật lại trước giờ họp.
> Các ô "…" điền trong và sau buổi họp.

- **Thời gian:** Chủ nhật 22/11/2026, … · **Hình thức:** …
- **Thành phần:** …
- **Vắng:** …
- **Thư ký:** Đoàn Quốc Khánh

## 1. Chương trình họp

Ba phần gộp một buổi khoảng 1,5 giờ, theo quyết định 6 của
[Sprint Planning 1](2026-11-16-sprint-planning-1.md).

| # | Nội dung | Thời lượng | Kết quả cần có |
|---|---|---|---|
| 1 | Sprint Review 1: demo các story đã merge, đối chiếu Sprint Goal | 25 phút | Story đạt / chưa đạt Definition of Done |
| 2 | Retrospective Sprint 1 | 15 phút | Điều làm tốt, cần cải thiện, việc thay đổi ở Sprint 2 |
| 3 | Nội dung cần chốt (mục 4) | 30 phút | Quyết định cho từng mục |
| 4 | Sprint Planning 2 | 20 phút | Sprint Backlog, Sprint Goal Sprint 2 |

## 2. Sprint Review 1

### 2.1. Sprint Goal Sprint 1

> Người dùng có tài khoản đăng nhập được vào ứng dụng, quản lý được hộ gia
> đình, nhân khẩu và tạm trú/tạm vắng, tạo được khoản thu tự nguyện và có
> bản sao lưu CSDL định kỳ; dữ liệu lưu trên MySQL.

Kết luận đạt / chưa đạt: …

### 2.2. Story của Sprint 1

32 SP theo [Sprint Planning 1](2026-11-16-sprint-planning-1.md) mục 3.1.
Trạng thái ngày 17/11/2026.

| ID | Issue | Người phụ trách | SP | Trạng thái | Pull Request |
|---|---|---|---|---|---|
| TS-003 | #34 | TV1 Khánh | 5 | Hoàn thành (đã merge, Issue đã đóng) | #55, #77, #78, #79 |
| US-020 | #28 | TV1 Khánh | 3 | Hoàn thành (đã merge, Issue đã đóng) | #81 |
| US-021 | #29 | TV1 Khánh | 3 | Hoàn thành (đã merge, Issue đã đóng) | #82 |
| US-001 | #7 | TV2 Dũng | 3 | Pull Request đang mở, CI xanh, chưa có lượt duyệt | #85 |
| US-002 | #8 | TV2 Dũng | 5 | Chưa bắt đầu (chưa có nhánh, Pull Request) | – |
| US-003 | #9 | TV2 Dũng | 5 | Chưa bắt đầu (chưa có nhánh, Pull Request) | – |
| US-006 | #14 | TV3 Hùng | 3 | Chưa bắt đầu; Issue chưa gán người vì TV3 chưa là collaborator | – |
| TS-001 | #32 | TV5 Huy | 5 | Chưa bắt đầu (chưa có nhánh, Pull Request) | – |
| **Tổng** | | | **32** | **Hoàn thành 11 SP** | |

Không tính SP: thiết kế Figma cho các story Sprint 1–2 (TV4 Đạt, #70),
Issue đang mở.

Trạng thái lúc họp và kết quả demo:

| ID | Trạng thái lúc họp | Kết quả demo | Nhận xét |
|---|---|---|---|
| TS-003 | … | … | … |
| US-020 | … | … | … |
| US-021 | … | … | … |
| US-001 | … | … | … |
| US-002 | … | … | … |
| US-003 | … | … | … |
| US-006 | … | … | … |
| TS-001 | … | … | … |

### 2.3. Pull Request đã merge trong Sprint 1

Từ 16/11/2026 tới ngày 17/11/2026, tất cả vào `main` qua Pull Request.

| Nhóm việc | Pull Request |
|---|---|
| Nền tảng (TS-003): CI, CSDL MySQL, cấu trúc gói, cấu hình kết nối, khung giao diện | #2, #37, #53, #55, #56, #66, #77, #78, #79 |
| CSDL: các trường bổ sung từ thiết kế của TV2 và dữ liệu mẫu | #57, #58, #59, #60, #61, #62, #63, #64 |
| US-020 tạo tài khoản, US-021 đăng nhập | #81, #82 |
| Tài liệu yêu cầu và giả định | #38, #39, #40, #41, #42, #65 |
| Quy trình, quy ước, hướng dẫn cài đặt, README | #43, #44, #45, #46, #47, #48, #49, #50, #51, #52, #71, #75, #76, #80 |
| Thiết kế và biên bản | #69, #83, #84 |

## 3. Retrospective Sprint 1

| Câu hỏi | Ý kiến |
|---|---|
| Điều làm tốt | … |
| Điều cần cải thiện | … |
| Việc thay đổi ở Sprint 2 | … |

## 4. Nội dung cần chốt

| # | Nội dung | Tài liệu | Lựa chọn | Quyết định |
|---|---|---|---|---|
| 1 | Giả định 1–50 (giả định 50: mật khẩu tối thiểu 8 ký tự) | [11-chot-va-gia-dinh.md](../requirements/11-chot-va-gia-dinh.md) | Giữ / sửa / bỏ từng giả định | … |
| 2 | Bảng chức năng – vai trò | [phan-quyen.md](../design/phan-quyen.md) | Duyệt bảng mục 1 | … |
| 3 | Quyền Thủ quỹ với US-005, US-008, US-009, US-018: story viết "Là Thủ quỹ" nhưng AC-060 chỉ cho Thủ quỹ thu phí và tra cứu | [phan-quyen.md](../design/phan-quyen.md) mục 2 | Theo story (Thủ quỹ được dùng) / theo AC-060 (chỉ Quản trị viên), từng story | … |
| 4 | UC011 "Tính phí theo diện tích" (US-005) | [so-do/README.md](../design/so-do/README.md), mục "Cần chốt ở họp 22/11", ý b | Giữ / đổi thành "Cập nhật đơn giá phí" theo AC-016 / bỏ | … |
| 5 | Ghi lịch sử sao lưu (AC-066, giả định 43): `database/schema.sql` chưa có bảng ghi thời điểm và kết quả từng lần sao lưu | [07-user-stories.md](../requirements/07-user-stories.md) TS-001 | Thêm bảng vào `schema.sql` / ghi ra tệp log ngoài CSDL | … |
| 6 | Hạn sơ đồ chi tiết của thành viên | [so-do/README.md](../design/so-do/README.md) mục 6; Issue #86, #87, #88, #89 | Hạn đề xuất trong Issue: review ERD 22/11; use case, máy trạng thái 29/11; đặc tả, hoạt động, tuần tự 06/12 | … |
| 7 | Story Sprint 1 chưa xong chuyển sang Sprint 2 | Mục 2.2 | Chuyển nguyên story / tách phần còn lại | … |
| 8 | Phát hành `v0.1.0` | [workflow.md](../workflow.md) | Phát hành với các story đã merge / dời tới khi đủ Sprint Goal | … |

## 5. Sprint Planning 2

Sprint 2: Thứ Hai 23/11/2026 – Chủ nhật 29/11/2026, mốc `v0.2.0`.

### 5.1. Story của Sprint 2

Theo [Sprint Planning 1](2026-11-16-sprint-planning-1.md) mục 4.2 và
milestone "Sprint 2". Cột "Cam kết" điền khi họp.

| ID | Issue | Mô tả ngắn | Người làm | SP | Cam kết |
|---|---|---|---|---|---|
| US-022 | #30 | Phân quyền Quản trị viên và Thủ quỹ | TV1 Khánh | 5 | |
| US-023 | #31 | Đổi mật khẩu và cập nhật thông tin cá nhân | TV1 Khánh | 3 | |
| US-026 | #11 | Đổi chủ hộ | TV2 Dũng | 3 | |
| US-027 | #12 | Lịch sử biến động nhân khẩu | TV2 Dũng | 5 | |
| US-005 | #13 | Tự tính phí dịch vụ, phí quản lý theo diện tích × đơn giá | TV3 Hùng | 5 | |
| US-009 | #17 | Tự tạo danh sách công nợ đầu tháng | TV3 Hùng | 5 | |
| TS-002 | #33 | Nhật ký thao tác | TV5 Huy | 5 | |
| **Tổng** | | | | **31** | |

Không tính SP: thiết kế Figma cho các story Sprint 3–4 (TV4 Đạt); sơ đồ mô
hình của thành viên (#86, #87, #88, #89). Story Sprint 1 chuyển sang (mục
4, ý 7): …

### 5.2. Sprint Goal Sprint 2

> …

## 6. Quyết định

| # | Nội dung quyết định | Ghi chú |
|---|---|---|
| 1 | … | |

## 7. Việc cần làm

| # | Việc | Người phụ trách | Hạn |
|---|---|---|---|
| 1 | … | | |
