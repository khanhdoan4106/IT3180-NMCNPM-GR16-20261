# Đặc tả UC024 – Thống kê theo khoản thu

## Thông tin chung

- **Mã:** UC024
- **Tên:** Thống kê theo khoản thu
- **Tác nhân chính:** Quản trị viên
- **Tác nhân phụ:** Không
- **Tiền điều kiện:** Người dùng đã đăng nhập và có vai trò Quản trị viên.
- **Kích hoạt:** Quản trị viên mở chức năng Thống kê theo khoản thu.
- **Hậu điều kiện thành công:** Tổng số tiền đã thu của khoản được chọn trong đợt/khoảng thời gian được hiển thị; dữ liệu không bị thay đổi.
- **Hậu điều kiện thất bại:** Không có dữ liệu bị thay đổi; hệ thống hiển thị thông báo phù hợp.

## Quy tắc nghiệp vụ

1. Quản trị viên chọn một khoản thu và đợt thống kê.
2. Với **khoản bắt buộc**, đợt là tháng đã chốt công nợ.
3. Với **khoản tự nguyện**, đợt là khoảng thời gian thu của khoản (từ ngày – đến ngày).
4. Chỉ cộng các khoản tiền thuộc phiếu thu **chưa bị hủy**.
5. Nếu chưa có giao dịch hợp lệ, tổng thu bằng **0 đ**.
6. Thống kê chỉ đọc dữ liệu, không tạo hoặc sửa phiếu thu.

## Luồng chính

| STT | Thực hiện bởi | Hành động |
|---:|---|---|
| 1 | Quản trị viên | Mở chức năng “Thống kê theo khoản thu”. |
| 2 | View | Hiển thị danh sách khoản thu và vùng chọn đợt thống kê. |
| 3 | Quản trị viên | Chọn một khoản thu. |
| 4 | View | Hiển thị lựa chọn đợt phù hợp với loại khoản: tháng đã chốt đối với khoản bắt buộc; khoảng thời gian thu đối với khoản tự nguyện. |
| 5 | Quản trị viên | Chọn đợt/khoảng thời gian và yêu cầu thống kê. |
| 6 | Controller | Kiểm tra lựa chọn đầu vào và gọi Service. |
| 7 | Service | Kiểm tra quyền, tính hợp lệ của khoản thu và khoảng thống kê; yêu cầu DAO truy vấn tổng tiền. |
| 8 | DAO | Truy vấn các dòng chi tiết thuộc khoản thu đã chọn, nối phiếu thu và loại các phiếu đã hủy; áp dụng điều kiện thời gian tương ứng. |
| 9 | Service | Chuẩn hóa kết quả; nếu không có giao dịch thì trả tổng bằng 0. |
| 10 | Controller | Nhận kết quả và yêu cầu View hiển thị. |
| 11 | View | Hiển thị tên khoản, đợt/khoảng thời gian và tổng tiền đã thu. |

## Luồng thay thế và ngoại lệ

- **3a.** Không có khoản thu: View hiển thị thông báo “Chưa có khoản thu”; không cho gửi yêu cầu thống kê.
- **4a.** Khoản bắt buộc chưa có tháng nào được chốt: hiển thị “Chưa có đợt thu đã chốt”.
- **5a.** Chưa chọn khoản hoặc đợt: Controller không gọi Service truy vấn; View yêu cầu chọn đủ thông tin.
- **8a.** Không có phiếu thu phù hợp hoặc mọi phiếu liên quan đã bị hủy: DAO trả kết quả rỗng/0; tiếp tục luồng chính từ bước 9 và hiển thị **0 đ**.
- **8b.** Lỗi truy cập CSDL: DAO ném lỗi CSDL theo quy ước dự án; Controller hiển thị thông báo tiếng Việt, không hiển thị stack trace.
- **7a.** Người dùng không có vai trò Quản trị viên: Service từ chối thao tác; không truy vấn dữ liệu thống kê.

## Dữ liệu đầu vào và đầu ra

- **Đầu vào:** mã khoản thu; mã tháng đã chốt (khoản bắt buộc) hoặc ngày bắt đầu/ngày kết thúc (khoản tự nguyện).
- **Đầu ra:** tên khoản thu, nhãn đợt/khoảng thời gian, tổng tiền đã thu (VND).
- **Nguồn dữ liệu:** `khoan_phi`, `dot_thu`, `phai_nop`, `phieu_thu`, `phieu_thu_chi_tiet`.

## Liên hệ Acceptance Criteria

- **AC-052:** Chọn khoản thu và đợt, hiển thị tổng tiền đã thu.
- **AC-053:** Khoản chưa có giao dịch phải hiển thị tổng bằng 0 đ.

## Ghi chú thiết kế

- Đi đúng kiến trúc View → Controller → Service → DAO; View không truy vấn CSDL và Service không phụ thuộc JavaFX.
- Truy vấn phải loại phiếu thu đã hủy. Không cộng nhầm các dòng chi tiết của khoản khác.
- Cần đối chiếu tên cột/khóa ngoại thực tế trong `database/schema.sql` khi triển khai SQL.
