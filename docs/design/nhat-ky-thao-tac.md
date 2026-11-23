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
| `DOI_VAI_TRO` | Đổi vai trò |
| `KHOA_TAI_KHOAN`, `MO_KHOA_TAI_KHOAN` | Khóa/mở khóa tài khoản |
| `DAT_LAI_MAT_KHAU` | Đặt lại mật khẩu |

## API đã thêm

- Model: `com.bluemoon.model.NhatKy`.
- DAO: `NhatKyDao`, `NhatKyDaoJdbc`.
- Service: `NhatKyService`.
- Màn hình: `nhat-ky.fxml`, `NhatKyController`, đã đăng ký trong
  `ManHinh.NHAT_KY` và chỉ cho `VaiTro.QUAN_TRI`.
- `NhatKyService.ghiTrongGiaoDich(...)` nhận **chính Connection của
  giao dịch nghiệp vụ**. DAO chỉ INSERT trên connection đó; không mở
  connection riêng, không commit và không nuốt lỗi. Lỗi ghi phải được
  truyền lên để giao dịch nghiệp vụ rollback.

Ví dụ tích hợp trong một Service nghiệp vụ:

```java
try (Connection ketNoi = nguonKetNoi.moKetNoi()) {
    ketNoi.setAutoCommit(false);
    try {
        // DAO nghiệp vụ phải dùng cùng ketNoi để ghi thay đổi.
        hoGiaDinhDao.them(ketNoi, hoGiaDinh);
        nhatKyService.ghiTrongGiaoDich(ketNoi, nguoiDung,
                "THEM_HO", "Hộ " + hoGiaDinh.maCanHo(),
                "Thêm hộ gia đình");
        ketNoi.commit();
    } catch (SQLException | RuntimeException loi) {
        ketNoi.rollback();
        throw loi;
    }
}
```

Không gọi API ghi nhật ký sau khi DAO nghiệp vụ đã tự commit hoặc đã
mở một connection khác: khi đó AC-068 không được bảo đảm.

## Kiểm soát truy cập và bất biến

- Service xem nhật ký kiểm tra `ManHinh.NHAT_KY`; vai trò Thủ quỹ và
  phiên chưa đăng nhập bị từ chối trước khi gọi DAO.
- DAO chỉ có hai thao tác: INSERT và SELECT. Không định nghĩa UPDATE
  hoặc DELETE.
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
  gồm thao tác thêm hộ/chủ hộ.
- Nếu INSERT nhật ký lỗi, transaction rollback cả hộ, chủ hộ và nhật ký;
  lỗi CSDL được chuyển thành `LoiCsdl`.
- Không ghi nhật ký cho các truy vấn đọc danh sách hoặc kiểm tra trùng.

## Phần còn phải tích hợp

Các nghiệp vụ còn lại vẫn cần được tích hợp theo cùng nguyên tắc
transaction, gồm sửa/chuyển đi hộ, nhân khẩu, tạm trú/tạm vắng, tài khoản
và các nghiệp vụ thu phí. Mỗi thao tác phải dùng đúng mã trong bảng
danh sách phía trên; không ghi sau khi transaction nghiệp vụ đã commit.

Các test đã thêm/cập nhật:
- Unit test: Quản trị viên được thêm hộ; Thủ quỹ và phiên chưa đăng nhập
  bị từ chối trước khi DAO được gọi.
- Integration test MySQL: thêm hộ tạo đúng một dòng `THEM_HO`; nếu ghi
  nhật ký thất bại thì cả hộ và chủ hộ đều rollback.
- Các integration test nhân khẩu và tạm trú/tạm vắng tạo hộ fixture
  bằng tài khoản Quản trị viên.
- Bộ test nhật ký trước đó vẫn kiểm tra commit/rollback và trigger
  chặn UPDATE/DELETE.

Chạy:
- `mvn verify`
- `mvn verify -Pmysql` với biến môi trường CSDL kiểm thử theo
  `docs/setup.md`.
