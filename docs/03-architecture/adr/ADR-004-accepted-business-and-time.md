> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# ADR-004 — Ảnh hưởng kiến trúc của quyết định nghiệp vụ và thời gian

**Trạng thái:** Quy tắc đầu vào đã được chủ dự án xác nhận ngày 26/10/2026; cơ chế kỹ thuật bổ sung là REVIEW.  
**Nguồn:** DEC-BE-21, CR-CAN-002, CR-CAN-003, CR-TIME-001, CR-TIME-002 và DEC-JOB-001.  
**Phạm vi:** Kỳ gói, hủy, gia hạn, ghi nhận tài chính và báo cáo.

## 1. Bối cảnh

Phiếu 16 và xác nhận trực tiếp trong phiên 26/10 làm rõ cách tính ngày, thời điểm hủy, nguồn thời gian báo cáo và kiểm hạn tài khoản. Các lựa chọn này tác động xuyên suốt UC, UML, schema, API và kiểm thử. Chấp nhận quy tắc nghiệp vụ không đồng nghĩa chủ dự án đã nghiệm thu thuật toán khóa, mọi cột/index hoặc cấu hình runtime.

## 2. Quy tắc có hiệu lực

| Chủ đề | Quyết định | Hệ quả cần thể hiện trong thiết kế |
|---|---|---|
| Ngày gói | Ngày đăng ký là ngày đầu theo lịch `Asia/Ho_Chi_Minh`; khoảng `[start,end)` | Gói 30 ngày từ 10:15 ngày 01/10 hết lúc 00:00 ngày 31/10; UTC chỉ là cách lưu |
| Hủy trả trước | Hủy tại cuối toàn bộ thời gian đã trả, không hoàn phí; chặn mua thêm/gia hạn từ khi nhận hợp lệ | Tách coverageEnd khỏi cuối kỳ hiện tại; giữ lịch sử cycle và trạng thái PENDING_CANCEL |
| Ghi nhận báo cáo | Dùng `recordedAt` bên trong transaction cuối cùng đã commit | Không coi là giờ hoàn tất commit; commit qua ngày vẫn phân loại theo recordedAt |
| Hạn tài khoản | Kiểm giờ DB dưới khóa ngay trước cập nhật tiền/kết quả | Tới hạn thì từ chối; kiểm trước hạn có thể commit sau hạn |
| Tác vụ nền | Worker gia hạn thường xuyên; JOB-01 lúc 00:05; JOB-02 lúc 00:30 | Dùng chung lõi, khóa kỳ và chống thu trùng; nhịp worker chưa chốt |
| Ngừng bán gói | Ngừng bán mới, khách cũ có thể gia hạn theo chính sách cấu hình | Tách quyền bán mới khỏi điều kiện gia hạn; lưu snapshot giá/chính sách từng kỳ |

Kỳ gia hạn xử lý muộn bắt đầu tại lúc xử lý hợp lệ, không hồi tố quyền sử dụng hoặc truy thu nhiều kỳ bỏ lỡ. Chỉ thu một kỳ mới khi chưa có hủy hợp lệ, chưa có lệnh khác tạo kỳ và còn đủ điều kiện/MAIN.

## 3. Hệ quả kiến trúc

### 3.1. Kỳ gói và cạnh tranh gia hạn

Một đăng ký gói có bản ghi ổn định với version và nhiều cycle bất biến. Giá, số ngày, khoảng hiệu lực và phí của cycle đã mua không đổi theo cập nhật danh mục. Thao tác thủ công dùng phiên bản đang thấy; worker và thủ công cùng nhắm một kỳ phải cùng cơ chế chống trùng. Phát hiện bên kia đã mua kỳ không cho phép tự nối thêm kỳ nữa.

### 3.2. Hủy và bằng chứng tiếp nhận

[CR-CAN-003](../../00-project/CR-CAN-003-cancellation-receipt.md) đã chốt ngữ nghĩa receipt sau commit. TxA lưu ý định PENDING và mục tiêu version/coverageEnd, chặn tạm mua thêm/gia hạn. Sau khi xác nhận TxA đã commit, TxB lấy giờ DB mới dưới khóa. Chỉ receipt nhỏ hơn observedCoverageEnd và version khớp mới chấp nhận hủy; ghi intent/root/outcome nguyên tử. Bằng hoặc sau hạn thì chốt FAILED CANCELLATION_RECEIVED_TOO_LATE, giải phóng chặn tạm và không tự nhắm kỳ mới. Timestamp trước commit hoặc thời gian HTTP đến không tạo quyền ưu tiên.

Mất phản hồi commit phải tra cứu bằng kết nối mới và khóa request; chưa xác định thì không trả accepted hoặc FAILED giả. Kết quả accepted đã commit được replay nguyên trạng dù tra lại sau hạn. TxB có thể commit sau hạn nếu receipt đã chụp trước hạn; không có lời hứa đo thời điểm commit vật lý. Ngữ nghĩa này đã duyệt; thuật toán hai transaction và bằng chứng runtime vẫn cần review/test.

### 3.3. Báo cáo và dữ liệu muộn

Không cần bổ sung CDC/binlog consumer để thu thời điểm commit vật lý vì quy tắc hiện hành dùng `recordedAt`. Báo cáo chỉ đọc giao dịch đã commit. Snapshot truy vấn được lưu tập thành viên/kết quả để phân trang ổn định, không giữ transaction lâu hoặc dựa vào nhiều SELECT độc lập ở READ COMMITTED.

JOB-02 giữ cutoff/tập nguồn đầu tiên qua mọi retry. Ngày đã chốt bất biến; giao dịch thuộc ngày cũ nhưng xuất hiện muộn tạo điều chỉnh trong kỳ sau, có truy vết nguồn/ngày gốc. Ràng buộc đóng góp nguồn duy nhất ngăn một giao dịch được cộng cả vào bản gốc lẫn điều chỉnh.

## 4. Các cách làm không phù hợp quyết định

Không chuyển UC01 thành bất đồng bộ toàn bộ, không dùng Redis làm khóa bảo vệ tiền, không chia thu phí UC02 qua một transaction API UC05 độc lập. Không hứa commit vật lý trước hạn hoặc gọi recordedAt là giờ durable commit. Không thay bộ worker/JOB-01 đã chọn bằng một lịch cron khác mà không có CR được duyệt.

## 5. Điều kiện nghiệm thu phần kỹ thuật

Cần review và kiểm thử transaction/khóa, receipt hủy, version/cycle guards, snapshot báo cáo và điều chỉnh muộn. Kiểm tình huống commit qua ngày/qua hạn, timeout, retry, restart và nhiều worker. Chưa có PASS runtime hoặc checkpoint mới từ ADR này.

Nguồn chi tiết: [biên nhận ngày 26/10](../../00-project/decision-receipt-20261026.md), [CR hủy](../../00-project/CR-CAN-002-prepaid-cancellation.md), [CR thời gian báo cáo](../../00-project/CR-TIME-001-reporting-time.md), [CR hạn tài khoản](../../00-project/CR-TIME-002-account-expiry.md). Lịch sử lựa chọn cũ được giữ trong sổ quyết định và phiếu nguồn.
