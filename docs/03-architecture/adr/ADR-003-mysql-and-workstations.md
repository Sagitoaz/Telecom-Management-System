# ADR-003 — MySQL và môi trường công ty/nhà

**Trạng thái:** Accepted đối với lựa chọn DBMS và môi trường; REVIEW đối với thiết kế vật lý/cấu hình mới.  
**Quyết định gốc:** CR-DB-001 ngày 19/10/2026.  
**Bổ sung:** Q13, Q15, Q16, Q18 ngày 26/10/2026.  
**Người quyết định:** Chủ dự án.

## 1. Bối cảnh

Kiến trúc nền từng dùng PostgreSQL và mô hình công ty kết nối Supabase. Chủ dự án đã chuyển DBMS sang MySQL, phù hợp môi trường chạy tại nhà; công ty dùng để đọc/duyệt, viết mã và unit test không cần DB. MySQL đang có tại nhà là 8.0.46, nên phải kiểm tương thích trên đúng phiên bản thay vì dùng một bản MySQL khác làm bằng chứng thay thế.

## 2. Quyết định có hiệu lực

- Dùng **MySQL 8.0.46, InnoDB** cho dữ liệu nghiệp vụ.
- Dùng `READ COMMITTED` cho các transaction ghi tiền/cấp số, kết hợp khóa tường minh và ràng buộc DB.
- Chuỗi dùng `utf8mb4`; quy tắc so sánh mã được ghi rõ trong schema. Thời gian dùng `DATETIME(6)` theo UTC; ngày nghiệp vụ dùng `Asia/Ho_Chi_Minh`.
- Tại nhà, MySQL, RabbitMQ, Redis và Keycloak chạy bằng Compose; backend phát triển từ IDE và có container khi bàn giao.
- Tại công ty, làm tài liệu/mã/unit test không phụ thuộc service; không yêu cầu Supabase hoặc Docker.

Giữ Java 21, Spring Boot, một ứng dụng chia module, JPA/JDBC, Flyway, RabbitMQ, Redis và IdP ngoài Telecom. ADR này thay phần DBMS/môi trường tương ứng trong [ADR-001](ADR-001-foundation.md) và [ADR-002](ADR-002-runtime-environments.md); không xóa lịch sử hai ADR đó hoặc thay đổi nguyên văn đề bài.

## 3. Hệ quả thiết kế và triển khai

Cần migration MySQL mới, dependency driver/Flyway/Testcontainers phù hợp và kiểm constraint/khóa/race trên InnoDB thực. Không mang nguyên `JSONB`, `TIMESTAMPTZ`, `ON CONFLICT`, partial index hoặc `currentSchema` của PostgreSQL sang MySQL. Việc thay DBMS là thay đổi cần review, không tự chứng minh bản M4 PostgreSQL cũ đã đủ cho implementation MySQL.

Dùng một codebase và cấu hình kết nối phù hợp môi trường. Backend thật không có nhánh bỏ xác thực/DB khi chạy ở công ty; fake chỉ được dùng trong phạm vi unit test. Kiểm khả năng khởi động, migration từ DB trống, rollback, restart và kết nối host/container trước khi coi P5.1 hoàn tất.

## 4. Phần chưa được nghiệm thu

Q14=C giao agent đánh giá và trình cách lưu UUID, chưa chấp nhận trước phương án A/B. Đề xuất hiện tại là API dùng UUID dạng chuỗi chuẩn, DB dùng `CHAR(36)` với so sánh ASCII nhị phân thống nhất PK/FK; đây là phương án REVIEW. Bảng/cột/index, tên biến cấu hình, version/digest ngoài MySQL và các script thực thi cũng cần rà soát/kiểm chứng.

Trước triển khai DB UC01 cần review delta schema, khóa, retry và thời gian trong phạm vi UC01. UC02–UC06 tiếp tục theo từng vòng Use Case; không lấy việc chưa code các UC sau để chặn không cần thiết UC01. Nghiệm thu thiết kế không thay bằng chứng migration/integration test.

## 5. Nguồn và tài liệu bị tác động

[Biên nhận quyết định](../../00-project/decision-receipt-20261026.md), [sổ quyết định](../../00-project/clarification-log.md), [schema](../../04-design/database-schema.md), [runtime](../runtime-profiles.md), [topology](../docker-local-topology.md) và [runbook bàn giao](../../06-deployment/deployment-guide.md).
