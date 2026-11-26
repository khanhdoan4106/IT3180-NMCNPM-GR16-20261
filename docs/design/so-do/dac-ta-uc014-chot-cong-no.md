# Đặc tả UC014 – Chốt công nợ

## Thông tin chung

- **Mã:** UC014
- **Tên:** Chốt công nợ
- **User story:** US-009
- **Tác nhân chính:** Người dùng (Quản trị viên hoặc Thủ quỹ, giả định 51)
- **Tác nhân phụ:** Không
- **Tiền điều kiện:** Người dùng đã đăng nhập; danh mục có khoản thu bắt buộc (phí dịch vụ, phí quản lý).
- **Kích hoạt:** Người dùng mở màn Chốt công nợ; đầu tháng, Trang chính nhắc khi tháng hiện tại chưa chốt (giả định 15).
- **Hậu điều kiện thành công:** Tháng được chọn có đợt thu; mỗi hộ đang ở có một khoản phải nộp cho mỗi khoản bắt buộc có hiệu lực trong tháng, lưu kèm diện tích và đơn giá lúc chốt. Nhật ký có dòng `CHOT_CONG_NO`, ghi cùng giao dịch.
- **Hậu điều kiện thất bại:** Không có đợt thu, khoản phải nộp hay dòng nhật ký nào mới được ghi.

## Quy tắc nghiệp vụ

1. Mỗi tháng một đợt thu. Chốt lại tháng đã chốt chỉ thêm khoản còn thiếu (hộ mới chuyển đến, khoản bắt buộc mới); khoản đã có, kể cả đã sửa, giữ nguyên (AC-028, giả định 15).
2. Số tiền mỗi khoản = diện tích căn hộ × đơn giá đang áp dụng; một đơn giá chung cho mọi loại căn hộ (AC-015, AC-016, giả định 22).
3. Được chốt bù tháng đã qua: tính hộ chuyển đến không sau ngày cuối tháng đó và còn đang ở tại lúc chốt, khoản bắt buộc có hiệu lực trong tháng đó (giả định 32). Hộ chuyển đến giữa tháng vẫn tính đủ tháng (giả định 15).
4. Khoản tự nguyện không tạo khoản phải nộp (giả định 16).
5. Hộ chưa có diện tích thì bỏ qua và cảnh báo (AC-029).
6. Nhật ký `CHOT_CONG_NO` ghi cùng giao dịch; lỗi ghi nhật ký thì cả lần chốt bị hủy (giả định 44, `nhat-ky-thao-tac.md`).

## Luồng chính

| STT | Thực hiện bởi | Hành động |
|---:|---|---|
| 1 | Người dùng | Mở màn "Chốt công nợ". |
| 2 | View | Hiển thị ô chọn tháng (mặc định tháng hiện tại) và cho biết tháng đó đã chốt hay chưa. |
| 3 | Người dùng | Chọn tháng và nhấn "Chốt công nợ". |
| 4 | Controller | Kiểm tra đã chọn tháng, rồi gọi Service. |
| 5 | Service | Kiểm tra quyền; yêu cầu DAO đọc các hộ đang ở đã chuyển đến không sau ngày cuối tháng và các khoản bắt buộc có hiệu lực trong tháng. |
| 6 | DAO | Trả danh sách hộ (kèm diện tích) và khoản bắt buộc (kèm đơn giá). |
| 7 | Service | Tính số tiền từng cặp hộ – khoản bằng diện tích × đơn giá; ghi cảnh báo cho hộ chưa có diện tích. |
| 8 | DAO | Trong một giao dịch: tạo đợt thu của tháng nếu chưa có; thêm các khoản phải nộp chưa có (bỏ qua cặp hộ – khoản đã có); ghi nhật ký `CHOT_CONG_NO`; xác nhận giao dịch. |
| 9 | Service | Trả kết quả: tháng, số hộ được chốt, số khoản tạo mới, số khoản đã có, tổng tiền, danh sách cảnh báo. |
| 10 | Controller | Nhận kết quả và yêu cầu View hiển thị. |
| 11 | View | Hiển thị kết quả chốt và các cảnh báo (nếu có). |

## Luồng thay thế và ngoại lệ

- **4a.** Chưa chọn tháng: Controller không gọi Service; View yêu cầu chọn tháng. Quay lại bước 3.
- **5a.** Người dùng chưa đăng nhập hoặc vai trò không được dùng màn Chốt công nợ: Service từ chối, không đọc, ghi dữ liệu; View hiển thị câu báo. Kết thúc.
- **6a.** Không có khoản bắt buộc nào có hiệu lực trong tháng: Service không tạo đợt thu; View hiển thị "Chưa có khoản phí bắt buộc cho tháng này". Kết thúc.
- **7a.** Hộ chưa có diện tích: bỏ qua hộ đó, thêm vào danh sách cảnh báo; tiếp tục với các hộ còn lại (AC-029).
- **8a.** Tháng đã chốt và không còn khoản nào thiếu: không thêm khoản nào, không ghi nhật ký; kết quả báo "Không có khoản mới" (AC-028). Tiếp tục bước 9.
- **8b.** Lỗi CSDL, kể cả lỗi ghi nhật ký hoặc hai máy cùng chốt một tháng (ràng buộc `uq_dt_thang`, `uq_pn_dot_ho_khoan`): DAO hủy toàn bộ giao dịch; Controller hiển thị câu báo tiếng Việt, người dùng chốt lại được. Kết thúc.

## Dữ liệu đầu vào và đầu ra

- **Đầu vào:** tháng cần chốt.
- **Đầu ra:** kết quả chốt (`KetQuaChotCongNo`): tháng, số hộ được chốt, số khoản tạo mới, số khoản đã có, tổng tiền, cảnh báo.
- **Nguồn dữ liệu:** `ho_gia_dinh`, `khoan_phi`, `dot_thu`, `phai_nop`, `nhat_ky`.

## Liên hệ Acceptance Criteria

- **AC-027:** tạo danh sách khoản phải nộp cho tất cả các hộ đang ở (luồng chính).
- **AC-028:** chốt lại cùng tháng không tạo trùng (bước 8, 8a).
- **AC-029:** hộ chưa có diện tích thì cảnh báo và bỏ qua (7a).
- **AC-015, AC-016:** số tiền = diện tích × đơn giá phí dịch vụ, phí quản lý (bước 7).
- **AC-068:** ghi nhật ký người chốt, nội dung, thời điểm (bước 8).

## Ghi chú thiết kế

- Đi đúng kiến trúc View → Controller → Service → DAO. Tên lớp theo nhánh `feature/US-009-chot-cong-no` và mô tả PR #109: `ChotCongNoController`, `ChotCongNoService`, `DotThuDao`/`DotThuDaoJdbc`, `PhaiNopDao`/`PhaiNopDaoJdbc`, model `DotThu`, `PhaiNop`, `KetQuaChotCongNo`. Service kiểm quyền bằng `PhanQuyen.yeuCau(ManHinh.CHOT_CONG_NO)`.
- Đơn giá đọc từ `khoan_phi` lúc chốt (UC011 đổi đơn giá chỉ ảnh hưởng tháng chốt sau); `phai_nop` lưu `dien_tich`, `don_gia`, `so_tien_goc` = `so_tien` lúc tạo.
- `ho_gia_dinh.dien_tich` trong `schema.sql` bắt buộc và lớn hơn 0 (`chk_ho_dien_tich`), nên nhánh 7a chỉ là kiểm tra phòng thủ.
