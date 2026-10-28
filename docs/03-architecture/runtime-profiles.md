> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Môi trường runtime và hợp đồng cấu hình

**Mã tài liệu:** DOC-ARCH-RUN-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

Q13=B chọn MySQL 8.0.46/InnoDB; Q18=A chọn bộ dịch vụ chạy tại nhà. Tài liệu này quy định ý nghĩa cấu hình và những gì phải kiểm chứng khi triển khai. Tên biến bên dưới là hợp đồng đề xuất, chưa chứng minh đã có profile Spring, Compose, migration hoặc backend tiêu thụ các biến đó.

## 1. Môi trường làm việc

| Môi trường | Công việc phù hợp | Điều kiện và giới hạn |
|---|---|---|
| Công ty (`office`) | Đọc/duyệt tài liệu, viết mã, unit test bằng fake/mock | Không yêu cầu Docker, DB hoặc Supabase; không suy ra đã có integration test |
| Nhà (`home`) | Chạy MySQL, RabbitMQ, Redis, Keycloak bằng Compose; backend từ IDE hoặc container | Ghi phiên bản thực, thực hiện migration và kiểm thử tích hợp |
| CI dự kiến | Build/unit test; integration test khi runner có Docker và MySQL đúng phiên bản | Cấu hình pipeline và kết quả CI chưa tồn tại chỉ vì đã mô tả ở đây |

Một codebase dùng chung; không thêm nhánh nghiệp vụ `if office` hoặc chế độ chạy backend thật bỏ qua DB/xác thực. Fake adapter chỉ dùng trong phạm vi test đã công bố. Hướng office → Supabase/PostgreSQL trong ADR-002 là lịch sử đã được ADR-003 thay thế.

## 2. Hợp đồng cấu hình dự kiến

Tên biến cần được ánh xạ tường minh vào cấu hình ứng dụng ở P5. Tệp `.env` không tự được JVM đọc; IDE, Compose hoặc launcher phải cung cấp đúng biến. Ví dụ URL dưới đây không chứa thông tin đăng nhập.

| Cấu hình đề xuất | Ý nghĩa | Giá trị/quyết định |
|---|---|---|
| `DB_JDBC_URL` | URL MySQL nghiệp vụ | Ví dụ IDE: `jdbc:mysql://localhost:3306/telecom_dev?connectionTimeZone=UTC`; trong container dùng hostname service `mysql` |
| `DB_USERNAME`, `DB_PASSWORD` | Tài khoản và mật khẩu DB | Cấp ngoài Git; phân biệt quyền ứng dụng và migration theo nhu cầu |
| `SPRING_PROFILES_ACTIVE` | Chọn profile ứng dụng | Dự kiến `home`; chỉ dùng sau khi profile thật được tạo và kiểm tra |
| `FLYWAY_ENABLED` | Cho phép migration tại DB đích | Chỉ bật trên đúng môi trường; JPA kiểm schema bằng `ddl-auto=validate` |
| `DB_WRITE_ISOLATION` | Isolation của transaction ghi | `READ_COMMITTED` đã chọn; kiểm cấu hình và việc reset connection pool |
| `DB_TIMEZONE` | Quy ước session/ứng dụng | UTC; `DATETIME(6)` được ghi/đọc theo UTC |
| `BUSINESS_ZONE` | Ngày lịch nghiệp vụ | `Asia/Ho_Chi_Minh` |
| `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, `JWT_AUDIENCE` | Ranh giới xác thực | Khớp realm, audience và hostname từ cả host/container; không chứa khóa riêng |
| `UC_FINANCIAL_REPLAY_DAYS` | Giữ đầy đủ kết quả UC02/04/05 | 30 ngày sau trạng thái cuối, cấu hình được |
| `UC_NONFINANCIAL_REPLAY_DAYS` | Giữ đầy đủ kết quả UC01/03 | 7 ngày; sau đó giữ tombstone; không dọn `PROCESSING`/`WAITING` |
| `MAX_AMOUNT_VND`, `MAX_BALANCE_VND` | Giới hạn tiền | 10.000.000 / 100.000.000 VND, cấu hình được; số nguyên và kiểm tràn số |
| `JOB01_CRON`, `JOB02_CRON` | Lịch tác vụ ngày | 00:05 / 00:30 hằng ngày theo múi giờ nghiệp vụ |
| `ACTIVATION_RECOVERY_INTERVAL` | Khôi phục request UC01 | 60 giây đã chọn; không suy ra SLA hoàn tất trong 60 giây |
| `RENEWAL_POLL_DELAY`, `BATCH_SIZE`, `RETRY_BUDGET` | Nhịp và giới hạn xử lý nền | Đề xuất cần review/đo; chưa lấy 60 giây UC01 làm nhịp gia hạn |
| `PACKAGE_CACHE_TTL` | Cache danh mục đọc | 60 giây cấu hình được; invalidate sau commit, lỗi thì đọc DB |
| `REPORT_SNAPSHOT_TTL` | Thời gian giữ bản kết quả phân trang | Chưa chốt; cần nêu hành vi khi hết hạn và tác động tới bên gọi |
| `CURSOR_SIGNING_KEY` | Bảo vệ toàn vẹn cursor | Secret ngoài Git; gắn caller, bộ lọc, queryId và phiên bản snapshot |

Tên biến có thể được điều chỉnh trong triển khai, nhưng không thay đổi âm thầm ý nghĩa nghiệp vụ. Cần tách thời gian giữ phản hồi đầy đủ, tombstone, lịch sử nghiệp vụ và snapshot báo cáo. Retention lịch sử 5 năm cấu hình được không có nghĩa xóa dữ liệu trong demo; chính sách demo không chạy purge.

## 3. Database, thời gian và phiên bản

MySQL phải đúng 8.0.46 với InnoDB. Chuỗi dùng `utf8mb4`; quy tắc so mã phải được ghi rõ trong schema. Cách lưu UUID `CHAR(36)` với so sánh ASCII nhị phân là đề xuất REVIEW theo Q14=C, không phải lựa chọn đã được chủ dự án duyệt.

Mọi môi trường phải kiểm UTC round-trip tới `DATETIME(6)`, DB clock tại kiểm điều kiện và việc chuyển ngày `Asia/Ho_Chi_Minh`. Không dùng giờ máy phát tải hoặc timestamp do client gửi làm mốc sổ giao dịch. Quy tắc `recordedAt` và kiểm hạn dưới khóa vẫn có hiệu lực khi thời điểm commit sang ngày khác.

RabbitMQ, Redis, Keycloak và các công cụ test phải được chọn patch/digest cụ thể khi triển khai. Không dùng nhãn `latest` như bằng chứng tương thích. Driver MySQL, Flyway và Testcontainers phải được kiểm tra đúng phiên bản thư viện thực tế; dependency PostgreSQL đang có trong khung dự án không tự tương thích MySQL.

## 4. Khởi động, readiness và phục hồi

Trình tự dự kiến: kiểm cấu hình/secret → khởi động hạ tầng → chờ readiness → migration trên DB đích → seed demo có truy vết → backend → smoke API có JWT. Container đã start chưa chứng minh MySQL nhận kết nối, schema đúng hoặc API phục vụ được.

MySQL không sẵn sàng thì không nhận thành công giả. RabbitMQ lỗi vẫn có khôi phục UC01 từ DB; Redis lỗi đọc danh mục từ DB. IdP lỗi không được làm API bỏ xác thực. Restart phải giữ request/tombstone, kỳ đã thu, WAITING và checkpoint báo cáo. Bản backup chỉ được coi là dùng được khi đã restore và đối soát trên DB tách biệt.

## 5. Bằng chứng cần có cho P5.1

Sau khi delta MySQL UC01 được review, cần chạy build/test thực, migration từ DB trống, kiểm constraint/index, readiness, JWT smoke và restart giữ dữ liệu. Ghi phiên bản Java/Maven/MySQL, cấu hình không chứa secret, source revision, lệnh đã chạy và kết quả. Việc soạn tài liệu hiện tại không tạo ra các bằng chứng này.

[Topology local](docker-local-topology.md), [runbook bàn giao](../06-deployment/deployment-guide.md) và [kế hoạch kiểm thử](../05-testing/test-plan.md) là đầu vào cho bước triển khai.
