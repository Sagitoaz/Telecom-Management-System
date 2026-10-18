# Đồng thời, transaction và recovery

**DRAFT 0.2, 18/10/2026. P4.5 chưa hoàn tất.**
Nguồn: DEC-PRE-05–10/13–15/19–20, CR-ARCH-001. Đã chọn hướng giao dịch DB ngắn+row lock+unique. Thuật toán SQL/lock order/retry chưa hoàn tất; không gọi đây là thiết kế đủ code.

| Tình huống | Quy tắc đã chọn | Còn đặc tả và test |
|---|---|---|
| Hai request chọn cùng số | Tối đa một thành công, không đổi số/WAITING | Unique+row lock, loser mapping, lock order |
| Tự cấp / WAITING cạnh tranh | Ngẫu nhiên trong AVAILABLE; WAITING FIFO ưu tiên tự cấp mới khi nạp kho | FIFO tie-break, claim và thứ tự khóa, tránh bỏ qua head do SKIP LOCKED |
| Cùng scope ID | caller+nghiệp vụ+ID; canonical conflict | Unique scope, in-progress response, khóa claim và rollback |
| Crash sau commit trước response | Không tác động hai lần; kết quả bền vững | Replay7 ngày; sau đó tombstone/fingerprint/summary, HTTP cụ thể |
| Recovery / broker redelivery | DB authority; recovery 60s không reset deadline | Ack sau outcome, worker crash/claim expiry, retry giới hạn |
| Biên24h | Kiểm hạn tại hoàn tất, đã hết hạn không cấp | Nguồn clock, semantics commit/deadline; không coi check đầu transaction là đủ |
| Gói+thu phí+debit độc lập | Một commit nghiệp vụ, không âm/không partial | Lock account/registration order, ledger request key |
| Hủy/gia hạn/job | Hủy nhận hợp lệ trước cuối kỳ ngăn gia hạn; không grace thiếu tiền | Mốc nhận bền vững, exact boundary, sau gia hạn mới hủy, lỗi kỹ thuật/retry |

## RabbitMQ và Redis

Nạp kho commit DB rồi phát tín hiệu; publisher confirms hỗ trợ biết broker đã nhận, không làm DB và broker nguyên tử. Mất tín hiệu được bù bằng quét DB60s. Message lặp/reorder không thay FIFO nghiệp vụ hoặc tạo thuê bao mới; consumer đọc trạng thái có thẩm quyền.
Redis chỉ cache danh mục gói cho read; thu phí/cấp số/request lấy DB. TTL 60s cấu hình+invalidation+DB fallback; không dùng distributed cache lock thay ràng buộc DB.

## Các điểm trước M4

Viết thuật toán từng bước: scope-ID claim → profile/evidence → số → Subscriber/Account/kết quả; failure persistence khác rollback nghiệp vụ; completion/deadline phải nhất quán. Xác định thứ tự lock toàn bộ resource, deadlock retry/backoff có giới hạn, idempotent finalization/tombstone.
Hai job dùng định danh run/kỳ, 00:05/00:30 Asia/Ho_Chi_Minh. Quyền dùng hết đúng mốc, không chờ job; gia hạn/hủy và việc job chạy trễ phải được thiết kế trước UC02/03/job.
