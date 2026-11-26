# Đặc tả UC009 – Đổi chủ hộ

## Thông tin chung

- **Mã:** UC009
- **Tên:** Đổi chủ hộ
- **User story:** US-026
- **Tác nhân chính:** Quản trị viên
- **Tác nhân phụ:** Không
- **Tiền điều kiện:** Người dùng đã đăng nhập với vai trò Quản trị viên; hộ đang ở và có chủ hộ.
- **Kích hoạt:** Quản trị viên chọn "Đổi chủ hộ" ở danh sách thành viên của một hộ trên màn Nhân khẩu.
- **Hậu điều kiện thành công:** Thành viên được chọn là chủ hộ; chủ hộ cũ có quan hệ mới với chủ hộ mới hoặc đã chuyển đi; hộ vẫn có đúng một chủ hộ. Lịch sử biến động và nhật ký thao tác có dòng tương ứng, ghi cùng một giao dịch.
- **Hậu điều kiện thất bại:** Chủ hộ, quan hệ và trạng thái của mọi thành viên giữ nguyên; không có dòng lịch sử biến động hay nhật ký nào được ghi.

## Quy tắc nghiệp vụ

1. Chủ hộ mới phải là thành viên đang ở của chính hộ đó (AC-073, AC-075).
2. Hộ không còn thành viên nào khác đang ở thì không đổi chủ hộ được; nếu cả hộ rời đi thì đánh dấu hộ chuyển đi (AC-074).
3. Chủ hộ cũ đổi sang quan hệ Quản trị viên chọn hoặc rời hộ (đánh dấu chuyển đi); cả việc này và việc đặt chủ hộ mới lưu trong một lần (giả định 26).
4. Lịch sử biến động ghi "Đổi chủ hộ", thêm "Chuyển đi" nếu chủ hộ cũ rời hộ (giả định 26, AC-076).
5. Nhật ký thao tác ghi `DOI_CHU_HO` cùng giao dịch; lỗi ghi nhật ký thì cả thao tác bị hủy (giả định 44, `nhat-ky-thao-tac.md`).

## Luồng chính

| STT | Thực hiện bởi | Hành động |
|---:|---|---|
| 1 | Quản trị viên | Chọn hộ đang ở trên màn Nhân khẩu và nhấn "Đổi chủ hộ". |
| 2 | View | Hiển thị chủ hộ hiện tại và danh sách các thành viên đang ở khác của hộ. |
| 3 | Quản trị viên | Chọn thành viên làm chủ hộ mới; chọn quan hệ mới của chủ hộ cũ hoặc chọn "Rời hộ"; nhấn "Lưu". |
| 4 | Controller | Kiểm tra đã chọn chủ hộ mới và cách xử lý chủ hộ cũ, rồi gọi Service. |
| 5 | Service | Kiểm tra quyền; kiểm tra hộ đang ở, còn thành viên khác đang ở và người được chọn đang ở trong hộ. |
| 6 | DAO | Trong một giao dịch: khóa dòng hộ và các nhân khẩu liên quan; đổi chủ hộ cũ sang quan hệ mới hoặc đánh dấu đã chuyển đi; đặt người được chọn làm chủ hộ; ghi lịch sử biến động; ghi nhật ký `DOI_CHU_HO`; xác nhận giao dịch. |
| 7 | Service | Trả kết quả đổi chủ hộ. |
| 8 | Controller | Nhận kết quả và yêu cầu View cập nhật. |
| 9 | View | Hiển thị "Đã đổi chủ hộ" và danh sách thành viên với chủ hộ mới. |

## Luồng thay thế và ngoại lệ

- **2a.** Hộ không còn thành viên nào khác đang ở: View hiển thị "Hộ không còn thành viên khác đang ở. Nếu cả hộ rời đi, hãy đánh dấu hộ chuyển đi." và không cho lưu (AC-074). Kết thúc.
- **4a.** Chưa chọn chủ hộ mới hoặc chưa chọn cách xử lý chủ hộ cũ: Controller không gọi Service; View báo ở ô còn thiếu. Quay lại bước 3.
- **5a.** Người dùng không phải Quản trị viên: Service từ chối, không đọc, ghi dữ liệu; View hiển thị câu báo không đủ quyền. Kết thúc.
- **5b.** Lúc lưu, hộ không còn thành viên khác đang ở (vừa thay đổi ở máy khác): Service từ chối và hướng dẫn đánh dấu cả hộ chuyển đi (AC-074). Kết thúc.
- **5c.** Người được chọn đã chuyển đi hoặc không thuộc hộ: Service từ chối, giữ nguyên chủ hộ hiện tại (AC-075). Kết thúc.
- **6a.** Lỗi CSDL, kể cả lỗi khi ghi lịch sử biến động hoặc nhật ký: DAO hủy toàn bộ giao dịch, không còn dòng lịch sử nào (AC-078); Controller hiển thị câu báo tiếng Việt. Kết thúc.

## Dữ liệu đầu vào và đầu ra

- **Đầu vào:** mã hộ, mã nhân khẩu được chọn làm chủ hộ mới, quan hệ mới của chủ hộ cũ hoặc lựa chọn "Rời hộ".
- **Đầu ra:** danh sách thành viên đang ở của hộ với chủ hộ mới; câu báo thành công hoặc lỗi.
- **Nguồn dữ liệu:** `ho_gia_dinh`, `nhan_khau` (`quan_he`, `trang_thai`), `bien_dong_nhan_khau`, `nhat_ky`.

## Liên hệ Acceptance Criteria

- **AC-073:** đổi chủ hộ sang thành viên đang ở, chủ hộ cũ đổi quan hệ hoặc chuyển đi, hộ vẫn có đúng một chủ hộ (luồng chính).
- **AC-074:** hộ không còn thành viên khác đang ở thì từ chối và hướng dẫn đánh dấu cả hộ chuyển đi (2a, 5b).
- **AC-075:** thành viên được chọn đã chuyển đi thì từ chối, giữ nguyên chủ hộ (5c).
- **AC-076, AC-078:** ghi lịch sử biến động khi lưu thành công, không ghi khi lỗi (bước 6, 6a).
- **AC-068:** ghi nhật ký người sửa, nội dung, thời điểm (bước 6).

## Ghi chú thiết kế

- Đi đúng kiến trúc View → Controller → Service → DAO: `nhan-khau.fxml`, `NhanKhauController`, `NhanKhauService`, `NhanKhauDao`/`NhanKhauDaoJdbc`; Service kiểm quyền bằng `PhanQuyen.yeuCau(ManHinh.NHAN_KHAU)`.
- Cột sinh `chu_ho_cua` có ràng buộc UNIQUE, nên DAO đổi quan hệ chủ hộ cũ trước rồi mới đặt chủ hộ mới, trong cùng giao dịch.
- Nhật ký và lịch sử biến động ghi bằng chính Connection của giao dịch (`NhatKyDao.ghi(...)`), không mở kết nối riêng.
