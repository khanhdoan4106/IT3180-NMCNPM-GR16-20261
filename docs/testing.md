# Kiểm thử

Cách nhóm kiểm thử BlueMoon và bảng đối chiếu tiêu chí chấp nhận (AC) với
test. Cài môi trường và CSDL kiểm thử: [setup.md, mục 4](setup.md#4-chạy-test).
Kiến trúc các tầng: [kien-truc.md](design/kien-truc.md).

## 1. Chiến lược

| Loại | Kiểm gì | Ở đâu | Chạy khi |
|---|---|---|---|
| Test đơn vị Service | Quy tắc nghiệp vụ, câu báo lỗi, phân quyền. Service nhận một **DAO giả** giữ dữ liệu trong bộ nhớ, không cần MySQL (vd `CuDanServiceTest`, `TaiKhoanServiceTest`) | `src/test/java/.../service/*Test.java` | `mvn verify` |
| Test đơn vị tiện ích | Lớp trong `util`, `config` không cần CSDL (vd `NgayTest`, `MatKhauTest`, `KetNoiCsdlTest`) | `src/test/java/.../util/`, `config/` | `mvn verify` |
| Kiểm thử tích hợp DAO | Câu SQL thật, ràng buộc CSDL, giao dịch. Lớp tên kết thúc bằng `IT` (vd `TaiKhoanDaoJdbcIT`), chạy trên CSDL MySQL có chữ **`test`** trong tên vì bảng bị xóa và tạo lại trước mỗi test | `src/test/java/.../dao/*IT.java` | `mvn verify -Pmysql` |
| Kiểm thủ công | Màn hình, luồng thao tác, chữ hiển thị (theo [ui.md](design/ui.md)), những AC chưa có test tự động | Ghi trong mô tả Pull Request: các bước đã làm, kết quả, ảnh chụp cửa sổ ứng dụng | Trước khi mở Pull Request |

Quy ước:

- Mỗi AC có ít nhất một test tự động khi kiểm được bằng mã; tên test bắt
  đầu bằng mã AC, vd `ac056BaoMatKhauQuaNgan`. AC chỉ kiểm được bằng tay thì
  ghi bước kiểm thủ công trong Pull Request.
- Service test với DAO giả, không mở kết nối MySQL; câu SQL kiểm riêng ở
  `*IT`.
- Test không phụ thuộc giờ máy hay dữ liệu có sẵn: tự tạo dữ liệu cần dùng.

## 2. Cách chạy

| Lệnh | Chạy gì |
|---|---|
| `mvn verify` | Biên dịch, mọi test đơn vị và Checkstyle (dòng ≤ 80 cột, không tab). Không cần MySQL |
| `mvn verify -Pmysql` | Thêm kiểm thử tích hợp `*IT` trên CSDL kiểm thử; cần các biến môi trường `BLUEMOON_DB_*` theo [setup.md](setup.md#4-chạy-test) |
| CI GitHub Actions | Chạy cho mọi Pull Request vào `main`: job `build` (`mvn -B verify`) và `build-mysql` (MySQL 8, nạp `schema.sql` và `seed.sql`, `mvn -B verify -Pmysql`). Hai job phải xanh mới merge được |

## 3. Bảng AC → test

Mã AC theo [07-user-stories.md](requirements/07-user-stories.md). Người làm
story điền dòng của mình trong cùng Pull Request với code; ô trống là chưa
có test.

### Sprint 1

| Story | AC | Test tự động | Kiểm thủ công |
|---|---|---|---|
| TS-003 | AC-070 | `KetNoiCsdlTest.docDuCauHinh`, `thieuTepThiBaoLoi`, `thieuUrlThiBaoLoi`; `LoiCsdlTest` (câu báo tiếng Việt khi không kết nối được) | Mở ứng dụng khi MySQL tắt: hiện hộp thoại báo lỗi rồi thoát |
| TS-003 | AC-071 | `CuDanDaoJdbcIT.ac071MayKhacThayDuLieuVuaGhi` | |
| US-020 | AC-054 | `TaiKhoanServiceTest.ac054TaoTaiKhoanHopLe`; `TaiKhoanDaoJdbcIT.ac054GhiTaiKhoanMoi` | |
| US-020 | AC-055 | `TaiKhoanServiceTest.ac055BaoTrungTenDangNhap`; `TaiKhoanDaoJdbcIT.ac055TrungTenDangNhapTraFalse` | |
| US-020 | AC-056 | `TaiKhoanServiceTest.ac056BaoMatKhauQuaNgan`, `matKhauDuTamKyTuLaDuoc` | |
| US-021 | AC-057 | `DangNhapServiceTest.ac057DungThongTinThiVaoDuocVaCoPhien` | |
| US-021 | AC-058 | `DangNhapServiceTest.ac058SaiMatKhauBiTuChoiVaKhongCoPhien`, `ac058SaiTenBaoCungMotCauVaVanKiemTraMatKhau` | |
| US-021 | AC-059 | `DieuHuongTest.ac059ChuaDangNhapThiVeManDangNhap` | |
| US-001 | AC-001 | | |
| US-001 | AC-002 | | |
| US-001 | AC-003 | | |
| US-001 | AC-004 | | |
| US-002 | AC-005 | | |
| US-002 | AC-006 | | |
| US-002 | AC-007 | | |
| US-002 | AC-008 | | |
| US-003 | AC-009 | | |
| US-003 | AC-010 | | |
| US-003 | AC-011 | | |
| US-006 | AC-019 | | |
| US-006 | AC-020 | | |
| US-006 | AC-021 | | |
| TS-001 | AC-066 | Chưa có test tự động | Chạy script; xác nhận file SQL không rỗng, log ghi thành công và marker lưu ngày; chạy lại trong cùng ngày phải được bỏ qua. Startup chỉ chạy khi đăng nhập Windows, không bảo đảm chạy đúng giờ nếu chưa đăng nhập. |
| TS-001 | AC-067 | Chưa có test tự động | Khôi phục file SQL vào `bluemoon_restore_test`; xác nhận 12 bảng được tạo và đối chiếu số bản ghi `ho_gia_dinh`, `nhan_khau`, `phieu_thu` với CSDL gốc. Hướng dẫn tại [backup-restore.md](backup-restore.md). |

Các Sprint sau thêm bảng cùng dạng khi bắt đầu Sprint.
