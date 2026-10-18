# Kiến trúc nền

**0.2 — DRAFT bộ kiến trúc, 18/10/2026. M3 chưa đạt.**
Nguồn: SRS 0.6, UC01 0.5, DEC-PRE-03/04/09 và CR-ARCH-001. Những lựa chọn stack/công cụ dưới đây đã được chủ dự án chọn; duyệt tài liệu M3 vẫn riêng.

## Quyết định nền

Một backend Spring Boot chia module, Java 21, PostgreSQL; JPA cho CRUD, JDBC khi cần kiểm soát transaction/SQL; Flyway quản lý migration.
Dùng cả RabbitMQ và Redis theo quyền lựa chọn công cụ chủ dự án giao. So với tách nhiều dịch vụ, ứng dụng đơn chia module giữ việc triển khai bài tập và giao dịch DB dễ kiểm chứng; chưa có nhu cầu microservices được duyệt.

| Thành phần | Vai trò | Giới hạn consistency |
|---|---|---|
| API Spring Boot | Xác thực, validation, đồng bộ các UC | Không phát JWT; không báo thành công trước commit |
| PostgreSQL | Customer/subscriber/account/request/kết quả/lịch sử | Nguồn quyết định cấp số, tiền, idempotency và deadline |
| JPA/JDBC + Flyway | Persistence và migration | Thiết kế transaction cụ thể sau M3; không tự trộn persistence context/SQL khi chưa đồng bộ trạng thái |
| RabbitMQ | Tín hiệu kho số đã bổ sung sau DB commit; consumer nội bộ kích hoạt xử lý WAITING | Tín hiệu không phải bằng chứng đã đấu nối; redelivery được xử lý idempotent; recovery DB mỗi60s không phụ thuộc tín hiệu |
| Redis | Cache-aside danh mục gói cho đường đọc; TTL 60s cấu hình, invalidate sau cập nhật | Lỗi cache đọc DB; giá thu phí đọc DB/snapshot, không dùng giá cache cũ. Không giữ nguồn số dư/cấp số/ID/tombstone |
| Scheduler nội bộ | JOB-01/JOB-02 và recovery UC01 | Không là actor ngoài; job trễ không gia hạn quyền dùng |
| IdP local ngoài Telecom | Phát JWT bất đối xứng, JWKS | Backend kiểm issuer/audience/expiry/scope/ownership; không thêm module login nội bộ |
| k6 và đối soát | Gửi tải API, đối chiếu request/DB | Không là dependency runtime backend; không tự tính HTTP request là success mới |

## Luồng UC01 và mất kết nối

Caller có quyền → claim ID theo caller+UC01 → kiểm hồ sơ/chứng cứ ngoài → cấp số chọn hoặc ngẫu nhiên → kết quả nhất quán Subscriber/Account chính.
Chỉ tự cấp hết kho có consent nhận WAITING≤24h. Bổ sung kho commit xong mới phát tín hiệu RabbitMQ; consumer đọc lại DB, chọn WAITING FIFO ưu tiên tự cấp mới, kiểm hạn lúc hoàn tất. Recovery60s bù trường hợp crash trước publish hoặc mất tín hiệu.
Publisher confirm và consumer ack không là một DB transaction; không hứa exactly-once message. Duplicate không tạo kết quả mới. Redis không tham gia đường quyết định idempotency/cấp số.
API timeout không chứng minh rollback; query/retry cùng scope ID. Replay đầy đủ 7 ngày sau final, giữ tombstone sau đó.

## Bảo mật và vận hành

Issuer/JWKS URL, audience, mapping scope/caller, key rotation, quyền endpoint và worker phải được thiết kế cụ thể trước code. “Local” là vị trí triển khai IdP, vẫn ngoài ranh giới backend.
Không log token/giấy tờ đầy đủ. Broker/Redis chạy local trong topology bàn giao sau này, version và cấu hình phải xác minh ở Phase 5/7; chưa sửa pom.xml hay tạo Compose trong lượt tài liệu.

## Điều kiện M3 còn lại

Sau M2: rà soát module interfaces, trust boundary, thông số runtime/claims, nguyên tắc transaction/failure của DB-broker-cache và C4. [ADR-001](adr/ADR-001-foundation.md) Accepted về lựa chọn đã giao không phải checkpoint M3.
