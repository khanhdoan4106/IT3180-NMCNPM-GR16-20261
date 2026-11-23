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

## Phần còn phải tích hợp

Các DAO và Service nghiệp vụ hiện có cần được chuyển sang nhận chung
Connection/giao dịch rồi gọi `ghiTrongGiaoDich` cho từng mã sự kiện
ở bảng trên. Việc này phải thực hiện cùng người phụ trách từng story
để không tạo transaction lồng nhau hoặc commit giữa chừng.

Các test đã thêm:
- Unit test quyền xem nhật ký: Quản trị viên được xem; Thủ quỹ và
  người chưa đăng nhập bị từ chối.
- Integration test MySQL: INSERT rollback cùng giao dịch; INSERT
  commit được lưu; UPDATE/DELETE bị trigger từ chối.

Chạy:
- `mvn verify`
- `mvn verify -Pmysql` với biến môi trường CSDL kiểm thử theo
  `docs/setup.md`.
