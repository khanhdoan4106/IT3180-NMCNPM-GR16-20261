# Đặc tả UC017 – Thu phí

## Thông tin chung

- **Mã:** UC017
- **Tên:** Thu phí
- **User story:** US-011, US-012 (chọn hình thức thanh toán và lưu người thu là một bước của lần thu)
- **Tác nhân chính:** Người dùng (Thủ quỹ hoặc Quản trị viên, AC-060)
- **Tác nhân phụ:** Không (cư dân đến nộp tiền không dùng phần mềm, giả định 2)
- **Tiền điều kiện:** Người dùng đã đăng nhập; đã tra cứu hộ bằng mã căn hộ (UC016) và màn Thu phí đang hiện các khoản chưa đóng của hộ.
- **Kích hoạt:** Người dùng chọn khoản cần thu hoặc nhấn "Thu tất cả".
- **Hậu điều kiện thành công:** Có một phiếu thu mới với số biên lai, hộ, các khoản đã thu, tổng tiền, hình thức thanh toán, người thu và thời điểm; các khoản đó chuyển sang đã đóng. Nhật ký có dòng `LAP_PHIEU_THU`, ghi cùng giao dịch.
- **Hậu điều kiện thất bại:** Không có phiếu thu nào được lưu, không cấp số biên lai, công nợ của hộ giữ nguyên.

## Quy tắc nghiệp vụ

1. Khoản bắt buộc phải đóng đủ số tiền của khoản; thu tất cả thì công nợ của hộ về 0 (AC-033, giả định 23).
2. Chỉ ghi nhận các khoản được chọn; tổng tiền tự cộng từ các khoản đó (AC-034, AC-035).
3. Bắt buộc chọn hình thức thanh toán: tiền mặt hoặc chuyển khoản (AC-036, AC-038).
4. Người thu là tài khoản đang đăng nhập, thời điểm lấy theo giờ máy chủ dữ liệu (AC-037, giả định 38). Họ tên người nộp và ghi chú là tùy chọn (giả định 49).
5. Số biên lai dạng `PT-<năm>-<5 chữ số>`, tăng liên tục theo năm, kể cả khi hai máy cùng lập phiếu (giả định 31).
6. Một khoản trong phiếu vừa được thu hoặc vừa bị sửa ở máy khác thì từ chối cả phiếu (giả định 34).
7. Hộ đã chuyển đi chỉ thu các khoản còn nợ (giả định 21, 39).
8. Nhật ký `LAP_PHIEU_THU` ghi cùng giao dịch; lỗi ghi nhật ký thì không lưu phiếu (giả định 44, `nhat-ky-thao-tac.md`).

## Luồng chính

| STT | Thực hiện bởi | Hành động |
|---:|---|---|
| 1 | Người dùng | Chọn các khoản cần thu trong danh sách khoản chưa đóng, hoặc nhấn "Thu tất cả". |
| 2 | View | Đánh dấu các khoản được chọn và hiển thị tổng tiền tự cộng. |
| 3 | Người dùng | Chọn hình thức thanh toán, nhập họ tên người nộp và ghi chú nếu có, nhấn "Xác nhận thu". |
| 4 | Controller | Kiểm tra đã chọn ít nhất một khoản và hình thức thanh toán, rồi gọi Service. |
| 5 | Service | Kiểm tra quyền; lấy người thu từ phiên đăng nhập; yêu cầu DAO lập phiếu. |
| 6 | DAO | Trong một giao dịch: khóa các khoản phải nộp được chọn và kiểm tra chưa được thu, số tiền chưa đổi; lấy thời điểm từ máy chủ; cấp số biên lai tiếp theo của năm; lưu phiếu thu và từng khoản; ghi nhật ký `LAP_PHIEU_THU`; xác nhận giao dịch. |
| 7 | Service | Trả phiếu thu vừa lập. |
| 8 | Controller | Nhận phiếu thu và yêu cầu View hiển thị. |
| 9 | View | Hiển thị số biên lai, tổng tiền, ngày thu, người thu; cập nhật danh sách khoản chưa đóng; cho chọn "In biên lai" (UC019). |

## Luồng thay thế và ngoại lệ

- **1a.** Hộ không còn khoản nào chưa đóng: View hiển thị "Hộ không còn khoản cần đóng" và không cho xác nhận. Kết thúc.
- **4a.** Chưa chọn khoản nào: Controller không gọi Service; View yêu cầu chọn khoản. Quay lại bước 1.
- **4b.** Chưa chọn hình thức thanh toán: Controller không gọi Service; View báo lỗi ở ô hình thức thanh toán (AC-038). Quay lại bước 3.
- **5a.** Người dùng chưa đăng nhập hoặc vai trò không được dùng màn Thu phí: Service từ chối, không ghi dữ liệu; View hiển thị câu báo. Kết thúc.
- **6a.** Một khoản vừa được thu hoặc vừa bị sửa ở máy khác: DAO hủy giao dịch, không lưu phiếu nào; View báo và nhắc tra cứu lại (giả định 34). Kết thúc.
- **6b.** Lỗi CSDL, kể cả lỗi ghi nhật ký: DAO hủy toàn bộ giao dịch, số biên lai không bị dùng; Controller hiển thị câu báo tiếng Việt. Kết thúc.

## Dữ liệu đầu vào và đầu ra

- **Đầu vào:** mã hộ, danh sách khoản phải nộp được chọn, hình thức thanh toán, họ tên người nộp (tùy chọn), ghi chú (tùy chọn).
- **Đầu ra:** phiếu thu: số biên lai, hộ, các khoản và số tiền, tổng tiền, hình thức thanh toán, người thu, thời điểm.
- **Nguồn dữ liệu:** `phai_nop`, `phieu_thu`, `phieu_thu_chi_tiet`, `bo_dem_bien_lai`, `nhat_ky`.

## Liên hệ Acceptance Criteria

- **AC-033:** "Thu tất cả" ghi nhận toàn bộ khoản, công nợ về 0 (bước 1, 6).
- **AC-034:** chỉ ghi nhận các khoản được chọn (bước 1, 6).
- **AC-035:** tổng tự cộng chính xác và có ngày thu (bước 2, 9).
- **AC-036:** lưu hình thức thanh toán (bước 3, 6).
- **AC-037:** tự lưu tài khoản người thu và thời điểm (bước 5, 6).
- **AC-038:** chưa chọn hình thức thanh toán thì báo lỗi (4b).
- **AC-068:** ghi nhật ký người lập, nội dung, thời điểm (bước 6).

## Ghi chú thiết kế

- Đi đúng kiến trúc View → Controller → Service → DAO: `thu-phi.fxml`, `ThuPhiController`, `ThuPhiService`, `PhieuThuDao`/`PhieuThuDaoJdbc`; Service kiểm quyền bằng `PhanQuyen.yeuCau(ManHinh.THU_PHI)`. Các lớp này chưa có trong mã nguồn, tên đặt theo quy ước ở `kien-truc.md` mục 3 và tên `PhieuThuDao` đã dùng ở biểu đồ tuần tự UC023.
- Chặn thu trùng ở CSDL bằng cột sinh `phai_nop_da_thu` (UNIQUE); số biên lai lấy từ `bo_dem_bien_lai` khóa theo năm trong cùng giao dịch, nên hủy giao dịch thì số không nhảy.
- Đóng góp tự nguyện trên cùng màn hình thuộc UC018; khi lập phiếu chung, các dòng đóng góp lưu cùng phiếu với `phai_nop_id` để trống.
