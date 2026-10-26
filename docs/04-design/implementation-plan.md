# Kế hoạch triển khai sau duyệt — tài liệu REVIEW 26/10/2026

Lượt này chỉ tài liệu/UML/OpenAPI; không Java, migration, Compose, runtime, branch/commit/deploy. [Checklist](m4-review-checklist.md) là gate; M4 lịch sử UC01 không tự duyệt delta MySQL. Dùng [schema](database-schema.md), [transaction](concurrency-and-jobs.md), [API](api-contract.md) thay hướng PostgreSQL cũ.

| Thứ tự | Phần việc sau khi được giao | Điều kiện và bằng chứng cần có |
|---|---|---|
|1|Chốt delta MySQL UC01|owner duyệt schema UUID/collation/index và contract/khóa/recovery; không cần đóng toàn bộ UC sau |
|2|P5.1 khởi tạo môi trường home/office|Java21/Maven; home MySQL8.0.46/RabbitMQ/Redis/Keycloak; office đọc/code/unit không DB. build/migration/healthcheck thực, không dùng starterPG |
|3|UC01 vertical slice|migration nền, JWT/caller/evidence, claim/idempotency, allocation/FIFO/wait/recovery; test stock race, retry/crosscaller, timeout and rollback; M5 chỉ bằng API chạy thật |
|4|UC02 + UC04/05|review chi tiết trước code; account→root→cycle, onefee/version/target, immutable ledger; cap/expiry under lock; failure/replay and contention tests |
|5|UC03/renewal/JOB-01|review protocol CR-CAN-003 receipt sau confirmed intent commit và mấtACK trước code liên quan; prepaid cancel/no refund, durable coordination, worker shared core and late singlecycle; crash and boundary test |
|6|UC06/JOB-02|persist first query/period cutoff, one statement membership underRC, cursor and immutable finalized summary/source uniqueness; latecommit adjustment tests |
|7|P6 hệ thống/hiệu năng|seed>=500K tổng trước đo; warmup2min/measure10min, simultaneous fresh success30+30TPS;10hotpairs=50% debit attempts inclretry; p95<=500ms/p99<=1s/error<=0.1%; unresolved0 và đối soát từng request/ledger/balance |
|8|P7 bàn giao|packaging/seed/config/runbook và chạy tái lập trên máy khác; báo cáo runtime thực tách báo cáo nghiệp vụ trước code |

Mỗi slice code chỉ sau đủ gate theo phạm vi; cùng requestID nội dung khác409, committed-but-ACK-lost lookup; FAILED thiếu tiền không hồi sinh. Kiểm thử bắt buộc và expected outcome ở transaction/API/SRS/test matrix; chỉ ghi kết quả khi đã chạy. Không tick migration/test/TPS bằng việc viết file. Cần cập nhật Roadmap/DEC bởi lượt điều phối khi owner xác nhận, không tự thay baseline approved.
