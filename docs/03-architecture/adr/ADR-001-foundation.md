> **⚠️ CHỈ THAY PHẦN DBMS:** ADR-001 vẫn có hiệu lực về RabbitMQ và Redis, nhưng mọi đề cập PostgreSQL là lựa chọn cũ đã thay bởi CR-DB-001/ADR-003 MySQL.

---

# ADR-001 — Backend chia module, RabbitMQ và Redis

**Trạng thái:** Accepted; M3 nền đã duyệt ngày 19/10/2026. Nội dung quyết định ngày 18/10 dưới đây giữ lịch sử; DBMS PG được CR-DB-001/ADR-003 thay MySQL, không đổi broker/cache.
**Ngày:** 18/10/2026. **Người quyết định:** Chủ dự án, DEC-PRE-03/04 và CR-ARCH-001.
**Căn cứ duyệt:** Chủ dự án yêu cầu dùng cả broker/cache và giao agent chọn công cụ, mặc định duyệt trong phạm vi này.

## Bối cảnh và phương án

Backend Java cần DB quan hệ, xử lý đồng bộ là chính; WAITING khi thiếu số cần tín hiệu và recovery. Danh mục gói là dữ liệu đọc có thể cache. Đã xét không broker/cache, RabbitMQ+Redis, hoặc nền tảng streaming/kiến trúc nhiều dịch vụ. Chủ dự án đã chọn có cả hai; không có yêu cầu event replay/streaming để thêm Kafka hay tách microservices.

## Quyết định

Spring Boot/Java21/PostgreSQL, JPA CRUD/JDBC có lý do, Flyway. RabbitMQ cho tín hiệu bổ sung kho sau commit; Redis cache-aside danh mục gói, TTL 60s cấu hình/invalidate/fallback DB. Không cache số dư/ID/cấp số làm nguồn quyết định.
DB recovery 60s là đường khôi phục bền vững, FIFO thuộc logic DB; RabbitMQ không bảo đảm FIFO nghiệp vụ giữa nhiều worker nếu không có claim/ordering thiết kế ở P4.
IdP local ngoài Telecom dùng JWT bất đối xứng/JWKS; tên sản phẩm IdP và claims cụ thể chưa được chọn.
k6 là công cụ tải đã được chọn DEC-PRE-24; không thay bằng Gatling/JMeter chỉ vì từng thảo luận.

## Hệ quả và phạm vi còn mở

Thêm hai thành phần vận hành/health/recovery; broker message có thể gửi trùng, consumer phải idempotent. Không sử dụng distributed transaction DB–broker; tín hiệu thất lạc được bù bằng recovery DB, không được chỉ dựa vào broker để lưu yêu cầu chờ.
Version, queue type/durability, prefetch/retry/DLQ, client timeout, cache concurrency/invalidation và transaction algorithm sẽ đặc tả/kiểm chứng ở P4–7. Không tự tuyên bố đã đáp ứng tải.
Thay quyết định về sau cần ADR/CR mới, giữ bản này trong lịch sử.

## Nguồn kỹ thuật

- [RabbitMQ confirms và acknowledgements](https://www.rabbitmq.com/docs/confirms).
- [RabbitMQ reliability — duplicate và idempotent consumer](https://www.rabbitmq.com/docs/reliability).
- [Redis cache-aside — TTL và invalidation](https://redis.io/docs/latest/develop/use-cases/cache-aside/).

Phân vai công cụ, TTL 60s và cách dùng recovery là lựa chọn thiết kế của dự án theo quyền được giao; các nguồn trên không phải bằng chứng ứng dụng đã triển khai đúng.
