# Khung triển khai và bàn giao

**DRAFT 0.2, 18/10/2026 — P7.1–P7.6 chưa thực thi.**
Nguồn: pom.xml/README hiện có, ROADMAP M8. Không phải hướng dẫn đã được chạy xác nhận.

## Sản phẩm cần bàn giao

JAR backend thực tế, migration, cấu hình môi trường không chứa secret, dữ liệu demo, công cụ seed/load/đối soát, OpenAPI, báo cáo test và hướng dẫn khôi phục.
Java 21/Maven là cấu hình hiện có; PostgreSQL cùng RabbitMQ/Redis theo ADR-001, M3 tổng thể chưa duyệt. Chưa có Compose/migration/backend nghiệp vụ hoàn chỉnh để công bố lệnh chạy thành công.

| Bước nghiệm thu từ máy sạch | Kết quả mong đợi | Cần bổ sung sau triển khai |
|---|---|---|
| Kiểm tra JDK/Maven/DB | Version tương thích cấu hình đã chốt | Version cụ thể và lệnh kiểm tra |
| Cấu hình DB/JWT/jobs | Tham số có mô tả, secret tách khỏi Git | Tên biến/default được duyệt |
| Build/test | Artifact + báo cáo test | Lệnh chính xác, output thực tế |
| Khởi tạo DB | Migration trên DB sạch, seed tái lập | Lệnh/profile và volume |
| Chạy/health | Backend kết nối DB và sẵn sàng | Port/path health được xác minh |
| Smoke UC01 | Thành công, hết số có/không consent, retry | Request/token mẫu không lộ secret |
| Restart/recovery | WAITING còn hạn khôi phục, expiry đúng | Thao tác và bằng chứng |
| Chạy hai job | Không nhân đôi, kiểm tra dữ liệu kỳ | Lịch/timezone/run thủ công |
| Backup/restore | Dữ liệu/history/requests nhất quán | Lệnh và thử phục hồi thực tế |

## Logging và hạn chế

Log correlationId/requestId, loại nghiệp vụ/kết quả và mốc xử lý; không in giấy tờ/JWT/secret toàn bộ. Lịch sử5 năm cấu hình, demo không purge (DEC-PRE-21); UC01 replay7 ngày+tombstone, tiền còn đề xuất R2.
Chưa triển khai mạng thật, ngân hàng, UI CSKH; không yêu cầu dịch vụ ngoài phạm vi để chạy bài tập.

## Điều kiện M8

Một người khác làm theo bản guide đã hoàn chỉnh trên môi trường sạch, gọi được API và tái lập kiểm thử; ghi ngày, cấu hình, kết quả và hạn chế. Tạo khung hiện tại không đóng M8.


**Topology bàn giao dự kiến:** backend Java21, PostgreSQL, RabbitMQ và Redis; IdP local tách khỏi backend cung cấp JWKS. Chưa có Compose chạy xác nhận. Cần ghi issuer/audience/scope/caller mapping, recovery 60s, lịch00:05/00:30 Asia/Ho_Chi_Minh và TTL cache60s cấu hình. Smoke restart phải chứng minh broker mất tín hiệu vẫn được recovery DB, Redis lỗi không làm sai nghiệp vụ.
