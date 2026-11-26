# TS-002 – Nhật ký thao tác

## Phạm vi

Theo AC-068, AC-069 và giả định 44 trong
`docs/requirements/11-chot-va-gia-dinh.md`, nhật ký ghi cùng giao dịch
với thao tác nghiệp vụ, chỉ Quản trị viên được xem, mới nhất ở trên,
không có chức năng sửa hoặc xóa.

Bảng `nhat_ky`, khóa ngoại, danh sách mã thao tác và hai trigger chặn
UPDATE/DELETE đã có trong `database/schema.sql`.

## Danh sách thao tác phải ghi

| Mã trong CSDL | Sự kiện |
|---|---|
| `LAP_PHIEU_THU` | Lập phiếu thu |
| `HUY_PHIEU_THU` | Hủy phiếu thu |
| `SUA_SO_TIEN_PHAI_NOP` | Sửa số tiền phải nộp |
| `TAO_KHOAN_TU_NGUYEN` | Tạo khoản thu tự nguyện |
| `SUA_DON_GIA` | Sửa đơn giá |
| `CHOT_CONG_NO` | Chốt công nợ |
| `THEM_HO`, `SUA_HO`, `HO_CHUYEN_DI` | Thêm/sửa/chuyển đi hộ |
| `THEM_NHAN_KHAU`, `SUA_NHAN_KHAU`, `NHAN_KHAU_CHUYEN_DI` | Thêm/sửa/chuyển đi nhân khẩu |
| `DOI_CHU_HO` | Đổi chủ hộ |
| `GHI_TAM_TRU_TAM_VANG` | Ghi đợt tạm trú/tạm vắng |
| `TAO_TAI_KHOAN` | Tạo tài khoản |
| `SUA_THONG_TIN_TAI_KHOAN` | Sửa họ tên, số điện thoại tài khoản |
| `DOI_MAT_KHAU` | Người dùng tự đổi mật khẩu |
| `DOI_VAI_TRO` | Đổi vai trò |
| `KHOA_TAI_KHOAN`, `MO_KHOA_TAI_KHOAN` | Khóa/mở khóa tài khoản |
| `DAT_LAI_MAT_KHAU` | Đặt lại mật khẩu |

## API đã thêm

- Model: `com.bluemoon.model.NhatKy` và enum `com.bluemoon.model.ThaoTac`.
- `ThaoTac` liệt kê đủ 21 mã trong bảng trên, tên hằng giống hệt mã
  trong `chk_nky_thao_tac` của `database/schema.sql`; mỗi hằng có nhãn
  tiếng Việt (`getNhan()`, ví dụ `THEM_HO` là "Thêm hộ"). DAO và Service
  nhận `ThaoTac` thay cho `String`, DAO lưu `name()` vào cột `thao_tac`
  và đọc lại bằng `ThaoTac.tuMa(...)`. Màn hình Nhật ký hiển thị nhãn,
  không hiển thị mã thô. Khi thêm mã mới vào CHECK phải thêm hằng vào
  enum; `ThaoTacTest` đối chiếu hai danh sách và sẽ báo lỗi nếu lệch.
- DAO: `NhatKyDao`, `NhatKyDaoJdbc`.
- Service: `NhatKyService`.
- Màn hình: `nhat-ky.fxml`, `NhatKyController`, đã đăng ký trong
  `ManHinh.NHAT_KY` và chỉ cho `VaiTro.QUAN_TRI`.
- DAO nghiệp vụ ghi nhật ký bằng `NhatKyDao.ghi(ketNoi, ...)` với
  **chính Connection của giao dịch nghiệp vụ**, trước khi commit.
  `NhatKyDao.ghi` chỉ INSERT trên connection đó; không mở connection
  riêng, không commit và không nuốt lỗi. Lỗi ghi được truyền lên để giao
  dịch nghiệp vụ rollback. `NhatKyService.ghiTrongGiaoDich(...)` theo
  cùng quy tắc (kiểm tra đủ thông tin rồi gọi `NhatKyDao.ghi`), hiện
  chưa DAO nào dùng.

## Cách viết đối tượng và nội dung

- Không ghi mã thô hay `#id`: đối tượng ghi tên để người đọc nhận ra,
  vd "Hộ A-101", "Nhân khẩu Nguyễn Văn A (hộ A-101)", "Tài khoản
  thuquy01".
- Nội dung dùng nhãn tiếng Việt (loại khai báo, quan hệ, vai trò, trạng
  thái) và ngày dạng dd/MM/yyyy (`Ngay.dinhDang`), vd "Tạm trú: Nguyễn
  Văn A, từ 01/11/2026 đến 30/04/2027", "Vai trò: Thủ quỹ → Quản trị
  viên". Giá trị trống ghi "(trống)".

## Kiểm soát truy cập và bất biến

- Service nghiệp vụ kiểm tra màn hình tương ứng qua
  `PhanQuyen.yeuCau(ManHinh)`; phiên chưa đăng nhập và vai trò không
  được phép bị từ chối trước khi DAO ghi dữ liệu.
- Service xem nhật ký kiểm tra `ManHinh.NHAT_KY`; chỉ Quản trị viên
  được xem.
- DAO nhật ký chỉ có hai thao tác: INSERT và SELECT. Không định nghĩa
  UPDATE hoặc DELETE.
- Trigger trong schema chặn UPDATE/DELETE ở CSDL, kể cả khi có lời gọi
  SQL ngoài giao diện.
- Thời điểm do MySQL tự điền bằng `CURRENT_TIMESTAMP`; không lấy giờ
  trên máy người dùng.
- Màn hình hiển thị tối đa 200 dòng mới nhất, sắp xếp theo
  `thoi_diem DESC, id DESC`, không có nút sửa/xóa.

## Tích hợp đã hoàn thành: tạo hộ gia đình (THEM_HO)

- `HoGiaDinhService` kiểm tra phiên và quyền qua
  `PhanQuyen.yeuCau(ManHinh.HO_GIA_DINH)`, sau đó truyền người thực hiện
  xuống DAO.
- `HoGiaDinhDaoJdbc` mở một Connection, tắt auto-commit, thêm hộ,
  thêm nhân khẩu chủ hộ, rồi gọi `NhatKyDao.ghi(...)` với **chính
  Connection đó** trước khi commit.
- Nhật ký lưu mã thao tác `THEM_HO`, đối tượng là mã căn hộ và nội dung
  ghi tên chủ hộ, kèm thời hạn tạm trú khi chủ hộ tạm trú (đợt tạm trú
  đầu tiên lưu cùng giao dịch, không ghi thêm dòng nhật ký riêng).
- Nếu INSERT nhật ký lỗi, transaction rollback cả hộ, chủ hộ và nhật ký;
  lỗi CSDL được chuyển thành `LoiCsdl`.

## Tích hợp đã hoàn thành: thêm nhân khẩu (THEM_NHAN_KHAU)

- `NhanKhauService` kiểm tra quyền qua
  `PhanQuyen.yeuCau(ManHinh.NHAN_KHAU)`, chuẩn hóa và kiểm tra dữ liệu,
  sau đó truyền người đang đăng nhập xuống DAO.
- `NhanKhauDaoJdbc` thêm nhân khẩu, ghi `THEM_NHAN_KHAU` bằng cùng
  `Connection`, rồi mới commit. Đối tượng nhật ký là tên nhân khẩu
  kèm mã căn hộ; nội dung ghi tên, quan hệ, mã căn hộ và thời hạn tạm
  trú nếu có.
- Nếu ghi nhật ký lỗi, thao tác thêm nhân khẩu rollback; không để lại
  nhân khẩu không có nhật ký tương ứng.
- Test đơn vị kiểm tra Thủ quỹ và phiên chưa đăng nhập bị từ chối.
  Test tích hợp MySQL kiểm tra dòng nhật ký được tạo và rollback khi
  DAO nhật ký phát sinh lỗi.

## Tích hợp đã hoàn thành: ghi tạm trú, tạm vắng (GHI_TAM_TRU_TAM_VANG)

- `TamTruTamVangService` kiểm tra quyền qua
  `PhanQuyen.yeuCau(ManHinh.TAM_TRU_TAM_VANG)`, kiểm tra dữ liệu và
  truyền người đang đăng nhập xuống DAO.
- `TamTruTamVangDaoJdbc` thêm đợt khai báo và ghi
  `GHI_TAM_TRU_TAM_VANG` trên cùng Connection trước khi commit.
- Đối tượng nhật ký là tên nhân khẩu kèm mã căn hộ; nội dung ghi loại
  khai báo, tên, khoảng ngày và nơi đến (với tạm vắng).
- Nếu ghi nhật ký lỗi, bản ghi khai báo rollback. Integration test MySQL
  kiểm tra cả trường hợp thành công và rollback.

## Tích hợp đã hoàn thành: tài khoản

- Tạo tài khoản (`TAO_TAI_KHOAN`), đổi vai trò (`DOI_VAI_TRO`),
  khóa/mở khóa (`KHOA_TAI_KHOAN`, `MO_KHOA_TAI_KHOAN`), đặt lại mật
  khẩu (`DAT_LAI_MAT_KHAU`), sửa thông tin cá nhân
  (`SUA_THONG_TIN_TAI_KHOAN`) và tự đổi mật khẩu (`DOI_MAT_KHAU`)
  đều ghi nhật ký trong cùng transaction với cập nhật tài khoản.
- Nội dung ghi nhận tên đăng nhập, trường thay đổi và giá trị trước/sau
  đối với thông tin/vai trò/trạng thái. Không bao giờ ghi mật khẩu rõ
  hoặc chuỗi băm vào nhật ký.
- Khi ghi nhật ký thất bại, thay đổi tài khoản rollback; nếu thao tác
  không thay đổi dữ liệu (tên đăng nhập trùng, tài khoản không tồn tại,
  hoặc bị chặn vì phải giữ Quản trị viên cuối cùng) thì không tạo nhật ký.
- Bổ sung hai mã thao tác cho thay đổi thông tin cá nhân và tự đổi mật
  khẩu vào CHECK constraint của `database/schema.sql`. CSDL đã được
  khởi tạo trước đó cần chạy một lần
  `database/migration_ts002_tai_khoan.sql`.

## Phần còn phải tích hợp

Các nghiệp vụ còn lại vẫn cần được tích hợp theo cùng nguyên tắc
transaction, gồm sửa/chuyển đi hộ, sửa/chuyển đi nhân khẩu, đổi chủ hộ và các nghiệp vụ thu phí. Chỉ ghi các sự kiện khi nghiệp vụ
tương ứng thực sự tồn tại; không ghi sau khi transaction nghiệp vụ đã
commit.
