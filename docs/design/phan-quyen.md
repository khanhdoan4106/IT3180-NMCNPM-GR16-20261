# Phân quyền theo vai trò (US-022)

Bảng chức năng – vai trò cho cả nhóm, dùng khi làm US-022 và khi mỗi người
viết Service của mình. Hai vai trò theo `VaiTro`: **Quản trị viên**
(`QUAN_TRI`) và **Thủ quỹ** (`THU_QUY`), giả định 2 trong
[11-chot-va-gia-dinh.md](../requirements/11-chot-va-gia-dinh.md).

Căn cứ chính:

- **AC-060:** Thủ quỹ "chỉ dùng được chức năng thu phí và tra cứu".
- **AC-061:** Quản trị viên dùng được mọi chức năng, gồm quản lý tài khoản.
- **AC-062:** người không có quyền thì không thấy thông tin nhân khẩu và
  công nợ của hộ.
- **Giả định 17:** phân quyền hai lớp, menu ẩn chức năng không được dùng
  và Service kiểm tra lại vai trò ở mỗi thao tác.
- **Giả định 18:** chỉ Quản trị viên quản lý tài khoản, không tự khóa hay tự
  đổi vai trò của mình.
- **Giả định 28:** Thủ quỹ chỉ hủy phiếu thu do chính mình lập.

Bảng này là đề xuất, **chốt ở họp 22/11** cùng các dòng "Chờ chốt" (mục 2).

## 1. Ma trận chức năng – vai trò

Ký hiệu: **Có** là dùng được; **Không** là không thấy trên menu và Service
từ chối; **Chờ chốt** xem mục 2.

### 1.1. Màn hình

Theo đúng thứ tự trong `ManHinh` (cũng là thứ tự trên menu trái).

| Màn hình (`ManHinh`) | Nhóm menu | US | Quản trị viên | Thủ quỹ | Căn cứ |
|---|---|---|---|---|---|
| Trang chính (`TRANG_CHINH`) | – | US-016 | Có | Có (trang mở đầu sau đăng nhập); thẻ tổng thu tháng xem mục 2 | AC-061; US-016 "Là Ban quản trị" |
| Hộ gia đình (`HO_GIA_DINH`) | Hộ gia đình | US-001 | Có | Không | AC-060, AC-062 |
| Nhân khẩu (`NHAN_KHAU`) | Hộ gia đình | US-002 | Có | Không | AC-060, AC-062; giả định 48 |
| Tạm trú, tạm vắng (`TAM_TRU_TAM_VANG`) | Hộ gia đình | US-003 | Có | Không | AC-060, AC-062 |
| Báo cáo nhân khẩu (`BAO_CAO_NHAN_KHAU`) | Hộ gia đình | US-004 | Có | Không | AC-060, AC-062 |
| Danh mục khoản thu (`DANH_MUC_KHOAN_THU`) | Khoản thu | US-005, US-006, US-008 | Có | US-006: Không. US-005, US-008: **Chờ chốt** | AC-060; US-006 "Là Ban quản trị" |
| Chốt công nợ (`CHOT_CONG_NO`) | Khoản thu | US-009 | Có | **Chờ chốt** | AC-060; giả định 15, 32 |
| Thông báo phí (`THONG_BAO_PHI`) | Khoản thu | US-015 | Có | Không | AC-060; US-015 "Là Ban quản trị" |
| Thu phí (`THU_PHI`) | Thu phí | US-007, US-010, US-011, US-012 | Có | Có | AC-060 (thu phí và tra cứu) |
| Phiếu thu (`PHIEU_THU`) | Thu phí | US-013, US-014 | Có | Có; hủy phiếu theo mục 1.2 | AC-060; giả định 28 |
| Hộ nợ đọng (`NO_DONG`) | Thống kê | US-017 | Có | Không | AC-060, AC-062; US-017 "Là Ban quản trị" |
| Thống kê theo khoản thu (`THONG_KE_KHOAN_THU`) | Thống kê | US-018, US-019 | Có | US-019: Không. US-018: **Chờ chốt** | AC-060; US-019 "Là Ban quản trị" |
| Tài khoản (`TAI_KHOAN`) | Hệ thống | US-020, US-022, US-023 | Có | Chỉ phần tài khoản của mình (US-023); không thấy phần quản lý tài khoản | AC-061; giả định 18, 19 |
| Nhật ký thao tác (`NHAT_KY`) | Hệ thống | TS-002 | Có | Không | AC-069; giả định 44 |

Ghi chú:

- Thủ quỹ không mở màn Hộ gia đình, Nhân khẩu, nhưng màn Thu phí vẫn hiện
  những gì cần để thu tiền: mã căn hộ, tên chủ hộ, các khoản chưa đóng
  của hộ đang tra cứu (AC-060 "tra cứu").
- Dân tộc, tôn giáo chỉ Quản trị viên xem và sửa (giả định 48). Màn hình
  nào có hiện nhân khẩu thì không đưa hai trường này cho Thủ quỹ.
- Một màn hình gồm nhiều story mà Thủ quỹ chỉ được một phần (vd Danh mục
  khoản thu, Thống kê theo khoản thu) thì ẩn phần không được dùng bên trong
  màn hình, và Service vẫn kiểm tra như mọi thao tác khác.

### 1.2. Thao tác đặc biệt

| Thao tác | US | Quản trị viên | Thủ quỹ | Căn cứ |
|---|---|---|---|---|
| Quản lý tài khoản: tạo tài khoản, đổi vai trò, khóa/mở khóa, đặt lại mật khẩu | US-020, US-022 | Có, trừ tự khóa hay tự đổi vai trò của mình | Không | AC-061; giả định 2, 18 |
| Hủy phiếu thu | US-014 | Có, mọi phiếu, trong ngày lập phiếu | Chỉ phiếu do mình lập, trong ngày lập phiếu | AC-041, AC-042; giả định 28, 38 |
| Xem nhật ký thao tác | TS-002 | Có | Không | AC-069; giả định 44 |

## 2. Cần chốt ở họp 22/11

Các dòng dưới đây **không tự quyết**; ghi "Chờ chốt" trong bảng mục 1 cho
tới khi nhóm chốt, rồi sửa bảng này trong một Pull Request riêng.

**Mâu thuẫn:** bốn story dưới viết "Là Thủ quỹ", nhưng AC-060 chỉ cho Thủ
quỹ "thu phí và tra cứu". Sơ đồ use case
([so-do/README.md](so-do/README.md), mục "Cần chốt ở họp 22/11", ý a) đang
theo story: các UC này nối với "Người dùng".

| Story | Nội dung | Màn hình | UC | Thủ quỹ |
|---|---|---|---|---|
| US-005 | Tự tính phí dịch vụ, phí quản lý theo diện tích × đơn giá | Danh mục khoản thu | UC011 | Chờ chốt |
| US-008 | Sửa khoản phải nộp của một hộ trước khi hộ đóng | Danh mục khoản thu | UC013 | Chờ chốt |
| US-009 | Chốt công nợ đầu tháng | Chốt công nợ | UC014 | Chờ chốt |
| US-018 | Xuất báo cáo thu tiền ra Excel | Thống kê theo khoản thu | UC023 | Chờ chốt |

Cần chốt thêm một điểm nhỏ: Thủ quỹ có thấy thẻ tổng thu tháng (US-016,
"Là Ban quản trị") trên Trang chính không.

Kết quả chốt ảnh hưởng menu (mục 3.1), Service của TV3 (US-005, US-008,
US-009) và TV5 (US-018).

## 3. Cài đặt trong mã

Hai lớp kiểm tra theo giả định 17. Hiện trên `main` (sau US-021) chưa có
kiểm tra vai trò nào: menu hiện mọi `ManHinh`, `DieuHuong` chỉ chặn khi
chưa đăng nhập (AC-059), Service chưa đọc vai trò.

### 3.1. Lớp 1: menu và điều hướng

- **`ManHinh`** (`util`): mỗi màn hình ghi thêm vai trò được mở, theo cột
  Quản trị viên, Thủ quỹ ở mục 1.1. Đây là chỗ duy nhất ghi quyền mở màn
  hình.
- **`CuaSoChinhController.taoMenu()`**: đang duyệt `ManHinh.values()` và
  tạo nút cho mọi màn hình. Bỏ qua màn hình vai trò hiện tại không được
  mở; `NhomMenu` không còn mục nào thì không hiện tên nhóm.
- **`DieuHuong.mo()`**: đang chặn chưa đăng nhập. Thêm chặn khi vai trò
  không được mở màn hình đó (không mở, về Trang chính).
- **Giả định 17, đọc lại tài khoản:** trước mỗi lần mở màn hình, đọc lại
  tài khoản từ CSDL theo `id` của người trong `PhienDangNhap`. Tài khoản đã
  khóa (`tai_khoan.hoat_dong = FALSE`) thì `PhienDangNhap.ketThuc()` và về
  màn đăng nhập; vai trò đã đổi thì cập nhật phiên và tạo lại menu.

### 3.2. Lớp 2: Service

- Mỗi hàm `public` của Service thay đổi hoặc đọc dữ liệu lấy người đang
  đăng nhập bằng `PhienDangNhap.nguoiDung()` rồi kiểm tra
  `NguoiDung.vaiTro()` theo bảng mục 1, trước khi gọi DAO. Chưa đăng nhập
  hoặc sai vai trò thì báo lỗi, không gọi DAO.
- Ví dụ trên `main`: `TaiKhoanService.taoTaiKhoan()` chưa kiểm tra vai trò;
  US-022 thêm kiểm tra chỉ `QUAN_TRI` (AC-061).
- Quy tắc theo người làm, không chỉ theo vai trò, cũng nằm ở Service: hủy
  phiếu thu so người lập phiếu với `NguoiDung.id()` (giả định 28); không
  tự khóa, tự đổi vai trò (giả định 18).
- Không dựa vào việc menu đã ẩn: Service vẫn kiểm tra, vì Controller khác
  hoặc test có thể gọi thẳng Service.

### 3.3. Kiểm thử

- Test Service đặt người đăng nhập bằng `PhienDangNhap.batDau()` (hàm
  `package-private`, test cùng gói `com.bluemoon.service` gọi được) và
  `PhienDangNhap.ketThuc()` sau mỗi test.
- Mỗi Service có ít nhất một test Thủ quỹ bị từ chối ở thao tác chỉ dành
  cho Quản trị viên, và một test khi chưa đăng nhập.
- Thêm test cho menu: với từng vai trò, danh sách `ManHinh` hiện ra khớp
  bảng mục 1.1 (đặt cạnh các test của `ManHinhTest`).
