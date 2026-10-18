# Kế hoạch triển khai theo vertical slice

**DRAFT 0.2, 18/10/2026 — kế hoạch trước code; không cấp phép vượt checkpoint.**
Nguồn: ROADMAP M0–M8, SRS, UC01.

| Thứ tự | Công việc | Bằng chứng hoàn tất |
|---|---|---|
| 1 | Chốt phần yêu cầu UC01, duyệt M1 | Quyết định người/ngày, SRS/UC01 version, OQ được hoãn |
| 2 | Rà soát P2.1 → P2.2 → P2.4, M2 | UML render + domain/flows được duyệt |
| 3 | Chốt stack/module/JWT, M3 | ADR được duyệt, C4 nhất quán |
| 4 | Hoàn tất schema/API/design class/concurrency UC01, M4 | Migration design, OpenAPI validate, thuật toán race/expiry/retry và test matrix |
| 5 | P5.1 nền backend, P5.2 UC01 | Build + DB migration + API/test nghiệp vụ trên PostgreSQL |
| 6 | M5 UC01 hoàn thiện | Toàn bộ AC-SUB được test, lỗi/timeout/recovery/race có bằng chứng |
| 7 | UC02 → UC03 → UC04 → UC05 → UC06 và hai job | Mỗi vòng bổ sung yêu cầu/thiết kế → code/test → docs |
| 8 | M6 đầy đủ nghiệp vụ → M7 test hệ thống/tải → M8 bàn giao | Log đo, đối soát, chạy từ môi trường sạch |

Các lần kiểm thử UC01 phải gồm: số chọn đua, tự cấp, consent/no-consent, deadline, retry cùng/khác payload, crash/timeout, JWT, hồ sơ và recovery.
Không tạo skeleton rồi coi P5.x xong; không yêu cầu hoàn tất API/sequence mọi UC trước UC01.
Chưa viết lệnh chạy backend/test mới vì phần triển khai chưa tồn tại.


Stack/công cụ đã chọn: Spring Boot/Java21/PostgreSQL/JPA-JDBC/Flyway/RabbitMQ/Redis; IdP local JWKS bên ngoài. P5/P7 cần dependency/config thực tế và failure tests broker/cache, không tự coi tài liệu là đã code. Kịch bản tải k6 và đối soát riêng theo DEC-PRE-24.
