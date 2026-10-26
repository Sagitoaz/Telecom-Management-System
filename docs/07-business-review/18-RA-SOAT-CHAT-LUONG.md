# Kết quả rà soát và các giới hạn còn lại

**Cập nhật 26/10/2026 — rà soát tài liệu, không phải báo cáo test backend.** Bộ tài liệu đã được đồng bộ theo phiếu 16 và các xác nhận bổ sung của chủ dự án. [Chỉ mục chuẩn](../README.md) là điểm đọc chính; chi tiết kiểm chứng trong [verification](../verification.md).

## Những khoảng trống đã bổ sung

- Thay khung UC02–06 bằng đặc tả riêng có input/output, tiền/hậu điều kiện, luồng chính/nhánh/lỗi và AC; làm rõ exact-account, phí MAIN, attribution, version và replay.
- Đồng bộ quy tắc ngày lịch; cuối toàn bộ thời gian đã trả khi hủy; receipt sau xác nhận commit; mốc recordedAt; kiểm hạn dưới khóa. Bốn CR giữ truy vết thay đổi và không sửa đáp án phiếu gốc.
- Bổ sung state/sequence cho tài khoản, đăng ký, yêu cầu, ý định hủy, báo cáo; các UC, worker gia hạn và hai job. ERD, C4, module và Design Class đi kèm nguồn PlantUML/ảnh.
- Viết thiết kế MySQL 8.0.46/InnoDB hiện hành thay hướng PostgreSQL trong tài liệu chuẩn: ràng buộc số/cycle/request/contribution, registry, tombstone, unknown commit và recovery.
- Hoàn thiện OpenAPI toàn backend, lookup request/cycle version, scope/caller ownership và fixed snapshot/cursor. Chuẩn hóa mã lỗi UC với enum API.
- Cụ thể hóa 64 ca biên và protocol tải: logical success khác attempt/retry, mẫu số lỗi/latency, hot pairs và bằng chứng thời điểm commit trong cửa sổ đo.
- Các trang review trùng nội dung đã chuyển thành chỉ dẫn đến nguồn chuẩn. Archive giữ bản trước đồng bộ, tránh âm thầm mất lịch sử.

## Sai lệch phát hiện trong kiểm tra chéo đã sửa

| Sai lệch | Cách xử lý |
|---|---|
| Sequence thu tiền kiểm giờ quá sớm, có thể chờ khóa tới sau hạn | Kiểm DB clock mới sau phối hợp mọi khóa cần thiết và ngay trước mutation |
| Mã lỗi UC03/UC05 khác enum API | Dùng thống nhất CANCELLATION_PENDING và NO_EFFECTIVE_CYCLE |
| Run lifecycle trong job có trạng thái ngoài schema/API | Dùng PENDING/RUNNING/SUCCEEDED/FAILED; lỗi từng item ghi counters/results |
| Gia hạn thiếu tiền có thể bị quét lại sau nạp tiền | Lưu terminal renewal_attempt theo target ổn định, không tạo target từ now/runId |
| READ COMMITTED có thể cho hai lần đọc ra hai tập báo cáo khác nhau | Đóng băng thành viên bằng một statement, tổng hợp từ thành viên đã lưu |
| Phân trang ledger có thể lặp thuê bao | Materialize tập subscriber DISTINCT và số liệu từ nguồn đã cố định |
| Ý định hủy sát hạn có thể hứa sai khi mất kết nối | CR-CAN-003 xác định receipt sau xác nhận commit; accepted replay, chưa kết luận phải kiểm giờ mới |

## Phần chưa thể tuyên bố đạt

Các thiết kế mới vẫn REVIEW: physical UUID, cột/index/FK/CHECK, DTO/HTTP/scopes/config, interval/batch/lease và giao thức transaction phải được chủ dự án review và kiểm chứng trên MySQL thật. Không còn câu nghiệp vụ khảo sát chưa trả lời cản đồng bộ tài liệu; OQ về cấu hình thực thi, archive/purge sau demo, backup/restore và chứng minh tải vẫn theo log.

Chưa có migration, API nghiệp vụ, fault injection, race test, EXPLAIN hoặc số đo tải. 64 ca biên đều NOT_RUN; không có tuyên bố đã loại mọi ngoại lệ có thể phát sinh. Chỉ có thể đánh giá thiết kế đã mô tả các tình huống hiện xét và chỉ rõ kết quả cần chứng minh.

**Tiếp theo duy nhất:** nghiệm thu delta MySQL UC01 theo [checklist M4](../04-design/m4-review-checklist.md), sau đó P5.1. Không tự hoàn tất M4-DB hoặc M5–M8 từ kết quả kiểm tra tài liệu.
