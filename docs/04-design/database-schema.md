# Thiết kế dữ liệu UC01 — khung

**DRAFT 0.2, 18/10/2026. P4.1/P4.2. M4 chưa đạt.**
Nguồn: domain-model.puml; UC01; DEC-01/02, DEC-REQ-05/16/17; OQ-02/05/12/15.
Đây là danh mục lưu trữ và ràng buộc cần hiện thực; tên bảng/cột, kiểu dữ liệu và FK vật lý chưa được duyệt. Chưa tạo migration hay ERD vật lý.

| Nhóm lưu trữ | Nội dung cần giữ | Bất biến cần bảo đảm | Cần chốt |
|---|---|---|---|
| Customer | Hồ sơ tối thiểu và tùy chọn | Không tự ghép khách qua tên/ngày sinh | Đã chọn so mọi trường gửi/chứng cứ ngoài; còn canonical schema |
| Verified document | Loại/số và liên kết Customer, căn cứ xác minh | Một giấy tờ hợp lệ không trỏ mâu thuẫn nhiều Customer | Unique/canonical form và seed xác minh |
| Subscriber | Chủ sở hữu, số đang được cấp, thời điểm đấu nối | Mỗi thuê bao một Customer, không cấp trùng số hiện tại | Khóa và vòng đời số |
| Number inventory | Số hợp lệ, AVAILABLE/ASSIGNED | Chỉ AVAILABLE được cấp, không tạo số giả khi hết kho | Chuẩn số, nạp/sinh, chiến lược chọn ngẫu nhiên |
| Activation request | requestId, phạm vi caller, nội dung so sánh, consent, trạng thái, kết quả, mốc chờ/hạn | Retry một tác động; WAITING chưa thành công; EXPIRED không cấp số | Đã chọn scope/canonical/replay7 ngày+tombstone/FIFO; còn schema và constraint |
| Main account | Thuê bao, loại chính, số dư VND, hạn tùy chọn | UC01 thành công có một account chính 0, không hạn mặc định | Cách lưu ràng buộc account chính |

## Quan hệ và lịch sử

Customer 1–N Subscriber, Subscriber 1–N Account; giấy tờ đã xác minh thuộc Customer. Kết quả ActivationRequest có tối đa một Subscriber; SUCCEEDED phải có đúng một kết quả và Account chính.
ERD nền sẽ mở rộng ServicePackage/PackageSubscription/Transaction khi UC02/04/05 tới thiết kế; không tự chọn khóa vật lý chỉ từ Domain Model.

## Đơn vị nhất quán phải thiết kế

Cấp số, tạo/liên kết Customer hợp lệ, Subscriber, Account chính và kết quả thành công phải nhất quán. Failure không có số ASSIGNED mồ côi. Claim WAITING/expiry không đua tạo hai kết quả.
Lưu request thất bại và rollback thao tác nghiệp vụ cần thiết kế rõ; không đặt mọi lỗi vào một transaction rollback rồi làm mất idempotency.

## Index / constraints / migration — điều kiện hoàn tất

Theo DEC-PRE-01–09 đã chọn, còn phải: ghi cụ thể khóa chính/FK, unique giấy tờ/số/request/account chính, check số dư và deadline; chỉ rõ nullable/retention, index tra số AVAILABLE và WAITING tới hạn, số liệu explain theo tải.
Migration phải chạy trên DB sạch và nâng cấp có dữ liệu; có test unique/race/rollback. Hiện chưa có bằng chứng nào cho phần này.


**Bổ sung dữ liệu đã chọn:** caller scope, fingerprint canonical, mốc terminal/resultRetentionUntil, tombstone/summary không cho tái dùng; không dọn đang xử lý/WAITING. Kết quả đầy đủ 7 ngày không phải xóa lịch sử Subscriber/Account. Giấy tờ/chứng cứ ngoài được đối chiếu; FIFO waitingSince có tie-break chưa chốt. Thu phí tương lai lưu giá snapshot/attribution, lịch sử 5 năm không purge demo. Mọi dữ liệu có thẩm quyền ở PostgreSQL; Redis không thay unique/lock/request registry.
