# BlueMoon – Phần mềm quản lý chung cư (IT3180 – Nhóm 16)

Ứng dụng máy tính để bàn giúp Ban quản trị chung cư BlueMoon quản lý hộ gia
đình, nhân khẩu và thu phí, thay cho sổ sách và Excel.

Bài tập lớn IT3180 Nhập môn Công nghệ phần mềm – Nhóm 16, Đại học Bách khoa
Hà Nội.

## Bối cảnh

Chung cư BlueMoon (ngã tư Văn Phú) có 30 tầng gồm kiot, tầng đế, căn hộ và
penthouse. Ban quản trị (BQT) do cư dân bầu ra điều hành chung cư.

Hiện BQT quản lý hộ khẩu bằng sổ và Excel, thu phí bằng sổ ghi tay
([As-is](docs/requirements/02-as-is-scenario.md)):

- Tính phí theo diện tích bằng tay, mất thời gian, dễ sai.
- Thông báo thu tiền in giấy, dán bảng tin, cư dân dễ bỏ sót.
- Tìm hộ chậm, nhầm số tiền; sổ thu không có ngày thu hay số biên lai.
- Ô trống hoặc "0" trong sổ quỹ không phân biệt được "không đóng" với
  "chưa thu".
- Sửa sổ không biết ai sửa gì; tổng hợp báo cáo thủ công, chậm.

Các khoản thu chính:

| Khoản thu | Cách tính |
|---|---|
| Phí dịch vụ | Diện tích × đơn giá 2.500–16.500 đ/m², bắt buộc hằng tháng |
| Phí quản lý | Diện tích × 7.000 đ/m² (mặc định), bắt buộc hằng tháng |
| Đóng góp tự nguyện (quỹ vì người nghèo, quỹ biển đảo…) | Theo đợt, không bắt buộc, đóng bao nhiêu ghi bấy nhiêu, kể cả 0 đ |

Người dùng: **Quản trị viên** (BQT) và **Thủ quỹ**. Cư dân không dùng trực
tiếp phần mềm và không có tài khoản.

## Kế hoạch và trạng thái

Mỗi Sprint 1 tuần, cuối dự án có 1 tuần chốt
([workflow.md](docs/workflow.md)).

| Vòng | Thời gian | Phạm vi chính | Phiên bản | Trạng thái |
|---|---|---|---|---|
| Sprint 1 | 16/11 – 22/11/2026 | Nền tảng Java + MySQL, đăng ký và đăng nhập, hộ gia đình, nhân khẩu, tạm trú/tạm vắng, tạo khoản thu tự nguyện, sao lưu CSDL | [`v0.1.0`](https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/releases/tag/v0.1.0) | Đã phát hành |
| Sprint 2 | 23/11 – 29/11/2026 | Phân quyền, quản lý tài khoản, đổi chủ hộ, lịch sử biến động nhân khẩu, tính phí tự động, chốt công nợ, nhật ký thao tác | `v0.2.0` | Đang làm |
| Sprint 3 | 30/11 – 06/12/2026 | Tra cứu phí, thu tất cả các khoản, hình thức thanh toán và người thu, đóng góp tự nguyện, sửa khoản thu, thông báo phí, báo cáo nhân khẩu; UAT lần 1 | `v0.3.0` | Chưa bắt đầu |
| Sprint 4 | 07/12 – 13/12/2026 | In biên lai, hủy phiếu thu, tổng thu tháng, lọc nợ đọng, thống kê theo khoản thu, xuất Excel, rà soát giao diện | `v0.4.0` | Chưa bắt đầu |
| Tuần chốt | 14/12 – 20/12/2026 | Không nhận story mới: sửa lỗi UAT, UAT lần 2, tài liệu nộp | `v1.0.0` | Chưa bắt đầu |

- Hạn từng Sprint: [milestone][milestone].
- Kế hoạch chi tiết: Sprint 2 [#72][kh72], Sprint 3 [#73][kh73], Sprint 4
  [#74][kh74].
- Bảng công việc (dành cho thành viên): [Project][project].
- Trạng thái từng story:
  [Product Backlog](docs/requirements/08-product-backlog.md).
  Thay đổi theo bản phát hành: [CHANGELOG.md](CHANGELOG.md).

[milestone]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/milestones
[kh72]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/issues/72
[kh73]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/issues/73
[kh74]: https://github.com/khanhdoan4106/IT3180-NMCNPM-GR16-20261/issues/74
[project]: https://github.com/users/khanhdoan4106/projects/1

## Phạm vi v1.0

- **Hộ gia đình:** hộ, nhân khẩu, tạm trú/tạm vắng; xuất danh sách tạm vắng
  cho công an phường.
- **Thu phí:** tự tính phí dịch vụ (2.500–16.500 đ/m²) và phí quản lý
  (7.000 đ/m²) theo diện tích; tạo các quỹ tự nguyện theo đợt; chốt công nợ
  hằng tháng. Tra cứu theo mã phòng, "Thu tất cả", chọn tiền mặt/chuyển
  khoản, tự lưu người thu, in biên lai, hủy phiếu thu nhầm trong ngày.
- **Người dùng:** đăng nhập, phân quyền Quản trị viên / Thủ quỹ, đổi mật
  khẩu.
- **Báo cáo:** tổng thu tháng trên màn hình chính, lọc hộ nợ quá 3 tháng,
  thống kê theo quỹ, xuất Excel.
- **Vận hành:** sao lưu dữ liệu định kỳ, nhật ký thao tác.

Ngoài phạm vi (v2.0): phí gửi xe, thu hộ điện, nước, internet. Chi tiết yêu
cầu: [docs/requirements](docs/requirements).

## Công nghệ và kiến trúc

- Java 21, JavaFX 21 (`javafx-controls`, `javafx-fxml`), Maven.
- MySQL 8.0 qua `mysql-connector-j`.
- JUnit 5; Checkstyle kiểm tra quy ước mã khi chạy `mvn verify`.

```
View (FXML) → Controller → Service → DAO → MySQL
```

Mỗi tầng chỉ gọi tầng ngay dưới. Quy tắc nghiệp vụ nằm ở Service, nên test
được Service bằng DAO giả, không cần MySQL.

## Chạy thử

1. Cài JDK 21, MySQL 8.0 và IntelliJ IDEA.
2. Bằng tài khoản root, chạy `database/schema.sql` rồi `database/seed.sql`.
   Tạo user MySQL cho ứng dụng (chỉ đọc, thêm, sửa dữ liệu).
3. Sao chép `config/db.properties.example` thành `config/db.properties`,
   điền user và mật khẩu. Tệp này không commit.
4. Tài khoản mẫu trong `seed.sql`: `admin` / `Admin@123` (Quản trị viên).
   Đổi mật khẩu này nếu triển khai thật.
5. Lớp khởi động ứng dụng sẽ có sau TS-003.

Hướng dẫn từng bước và lỗi thường gặp: [docs/setup.md](docs/setup.md).

Chạy test: `mvn verify` (gồm Checkstyle), không cần MySQL. Kiểm thử tích
hợp trên MySQL thật (lớp `*IT`): đặt `BLUEMOON_DB_URL`, `BLUEMOON_DB_USER`,
`BLUEMOON_DB_PASSWORD` trỏ tới một CSDL có chữ `test` trong tên, rồi chạy
`mvn verify -Pmysql`.

[CI](.github/workflows/ci.yml) chạy hai job cho mọi Pull Request vào `main`:

- `build`: `mvn -B verify`.
- `build-mysql`: dựng MySQL 8.0, nạp `schema.sql` và `seed.sql`, chạy
  `mvn -B verify -Pmysql` bằng user ứng dụng quyền tối thiểu.

## Cấu trúc thư mục

```text
.
├── src/main/java/com/bluemoon/
│   ├── config/          đọc config/db.properties, mở kết nối MySQL
│   ├── controller/      (dự kiến) xử lý sự kiện của từng màn hình
│   ├── service/         (dự kiến) quy tắc nghiệp vụ, kiểm tra dữ liệu
│   ├── dao/             (dự kiến) truy cập MySQL
│   └── util/            (dự kiến) tiện ích dùng chung
├── src/main/resources/com/bluemoon/
│   ├── view/            (dự kiến) màn hình FXML
│   ├── css/             (dự kiến) màu và kiểu chữ dùng chung
│   └── fonts/           phông Be Vietnam Pro (giấy phép OFL)
├── src/test/java/com/bluemoon/   test JUnit 5
├── database/
│   ├── schema.sql       tạo CSDL và bảng
│   └── seed.sql         dữ liệu mẫu, tài khoản admin
├── config/
│   ├── db.properties.example   mẫu cấu hình kết nối
│   └── checkstyle.xml   luật quy ước mã: dòng tối đa 80 cột, không tab
├── docs/
│   ├── requirements/    01–11: wish list → user story → backlog
│   ├── design/          quy ước mã, giao diện; figma/ (dự kiến)
│   ├── meeting-notes/   biên bản họp
│   ├── releases/        ghi chú phát hành
│   ├── review/          báo cáo rà soát
│   ├── setup.md         cài đặt và chạy
│   ├── thuat-ngu.md     thuật ngữ dùng thống nhất
│   └── workflow.md      quy trình làm việc của nhóm
├── .github/             CI, mẫu Issue và Pull Request
├── CHANGELOG.md         thay đổi theo từng bản phát hành
└── pom.xml              cấu hình Maven
```

## Quy trình làm việc

Issue `[TVx Tên] [US-xxx] …` → nhánh `feature/US-xxx-…` → commit → Pull
Request → thành viên khác review → merge vào `main` → cuối Sprint cập nhật
`CHANGELOG.md` và tạo tag. Chi tiết: [docs/workflow.md](docs/workflow.md).

## Tài liệu

| Nhóm | Tài liệu |
|---|---|
| Yêu cầu | [Wish list](docs/requirements/01-wish-list.md), [As-is](docs/requirements/02-as-is-scenario.md), [Visionary](docs/requirements/03-visionary-scenario.md), [NFR](docs/requirements/04-nfr.md), [Raw log](docs/requirements/05-raw-log.md), [Epic/Feature](docs/requirements/06-epic-feature-list.md), [User Story](docs/requirements/07-user-stories.md), [Product Backlog](docs/requirements/08-product-backlog.md), [Traceability](docs/requirements/09-traceability-matrix.md), [Definition of Done](docs/requirements/10-definition-of-done.md), [Chốt và giả định](docs/requirements/11-chot-va-gia-dinh.md), [Thuật ngữ](docs/thuat-ngu.md) |
| Thiết kế | [Quy ước mã](docs/design/coding-convention.md), [Giao diện](docs/design/ui.md), [Kiến trúc](docs/design/kien-truc.md) |
| Vận hành | [Cài đặt](docs/setup.md), [Kiểm thử](docs/testing.md), [Quy trình làm việc](docs/workflow.md), [Nhật ký thay đổi](CHANGELOG.md), [Ghi chú phát hành](docs/releases/README.md), [Báo cáo rà soát](docs/review/README.md) |
| Họp nhóm | [Biên bản họp](docs/meeting-notes/README.md) |
| Quản lý dự án | [Bảng giao việc (Google Sheet)](https://docs.google.com/spreadsheets/d/13APZsjZj4Dh5jlbh4GALKAoKI_hEWVHzbrsvdXAR60U/edit): việc theo Sprint, hạn, người review, trạng thái |

## Thành viên

| Thành viên | Họ và tên | MSSV | Vai trò chính | Mảng phụ trách |
| :-- | :-- | :-- | :-- | :-- |
| TV1 | Đoàn Quốc Khánh | 202416243 | Quản lý dự án / Scrum Master | Nền tảng, tài khoản, phân quyền |
| TV2 | Nguyễn Đình Dũng | 202416173 | Phân tích yêu cầu, Thiết kế UI, Dev | Hộ gia đình, nhân khẩu |
| TV3 | Đinh Phi Hùng | 202416215 | Lập trình CSDL và nghiệp vụ | Khoản thu, công nợ |
| TV4 | Cao Thành Đạt | 202416152 | Lập trình giao diện JavaFX | Thu phí, biên lai, thiết kế giao diện |
| TV5 | Vũ Nhật Huy | 202416229 | Kiểm thử và tài liệu | Thống kê, báo cáo, sao lưu, nhật ký |

Mã TV1–TV5 dùng thống nhất trong Issue, kế hoạch Sprint và biên bản họp.
