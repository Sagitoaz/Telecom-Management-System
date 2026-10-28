# Kế hoạch coding — baseline ĐÃ CHỐT 28/10/2026

**Đầu vào đã duyệt:** [CR-DOC-004](../00-project/document-approval-20261028.md), [M4-DB](m4-review-checklist.md), [schema](database-schema.md), [API](api-contract.md)/[OpenAPI](openapi.yaml), [transaction/jobs](concurrency-and-jobs.md), [Design Class](design-class-diagrams/README.md). Không còn gate chờ duyệt tài liệu hiện hành. **Cập nhật CR-IMPL-001 (28/10): ưu tiên UC04/UC05 tiền trước**, theo [kế hoạch code tại nhà](../KE-HOACH-CODE-TAI-NHA.md) và [học tại công ty](../KE-HOACH-HOC-TAI-CONG-TY.md).

## Việc kế tiếp duy nhất: P5.1 — Khởi tạo backend MySQL

1. Kiểm Java21/Maven và pom hiện có; thay driver PostgreSQL, Flyway PostgreSQL, Testcontainers PostgreSQL bằng bộ MySQL phù hợp BOM đã dùng; giữ các bản vá bảo mật hiện có khi chưa có bằng chứng cần đổi.
2. Thay Main mẫu tại src/main/java/main/java/Main.java bằng Spring Boot entrypoint và cấu trúc package com.telecom theo module đã duyệt; bổ sung cấu hình/validation/error handling nền cần cho UC01.
3. Profile office phục vụ code/unit không DB; home chạy MySQL8.0.46/RabbitMQ/Redis/Keycloak theo Compose. Secrets dùng biến môi trường, không đưa giá trị thật vào Git. Không dùng starter PostgreSQL/Supabase cũ.
4. Thiết lập Flyway MySQL nền cho UC04/UC05 và fixtures đúng quan hệ customer/subscriber/inventory/account; debit có package/root/cycle/fee hợp lệ. Không cần API UC01/UC02 hoàn tất trước; không bỏ điều kiện kỳ gói. Kiểm migration lặp lại an toàn trên MySQL thật.
5. Cấu hình JWT resource server từ IdP ngoài backend, issuer/audience/caller/scope/evidence; cung cấp thông số thực khi chạy API. Backend không phát JWT nội bộ.
6. Chạy Maven verify, boot/healthcheck và kiểm Flyway/MySQL thật; lưu lệnh, môi trường, output và giới hạn tại verification. Chỉ tick P5.1 khi đủ bằng chứng; thiếu Docker/DB thì ghi phần chưa chạy.

Lượt chốt/dọn này chưa sửa pom/Java, chưa chạy migration hoặc bắt đầu UC01.

## Các slice sau P5.1

| Thứ tự | Phần việc | Bằng chứng cần có |
|---|---|---|
|P5.5/P5.6 — ưu tiên đầu|UC04 cộng tiền → UC05 trừ tiền; request registry/security/fixtures|AC-CRE/AC-DEB, transaction, no negative/cap/expiry, same-ID/replay, race, recovery/unknown commit, đối soát; mốc module tiền không là M5|
|P5.2 — sau module tiền|UC01 Hybrid: request/caller/evidence, allocation/FIFO/wait/recovery|API thật; stock race, retry/crosscaller, expiry, timeout, rollback và unknown commit; M5 sau khi đạt|
|P5.3|UC02 thu phí/kỳ, dùng lại lõi tiền|Fee/cycle nguyên tử, account→root→cycle, one fee/version/target, regression tiền; fee không gọi API UC05|
|P5.4/P5.8|UC03/renewal/JOB-01|CR-CAN-003 hai transaction/receipt, prepaid cancel/no refund, durable coordination, late single cycle, crash/boundary tests|
|P5.7/P5.10|UC06/JOB-02|Fixed cutoff/membership/cursor, immutable finalized summary, source uniqueness và late adjustments|
|P6|Kiểm thử hệ thống/tải|Seed≥500K tổng; warmup2min/measure10min; fresh success30+30TPS;10hotpairs=50% debit attempts incl retry; p95≤500ms/p99≤1s/error≤0.1%; unresolved0; đối soát từng request/ledger/balance|
|P7|Bàn giao|Packaging/seed/config/runbook, backup/recovery và chạy tái lập trên máy khác|

P5.9 cập nhật tài liệu theo implementation sau mỗi slice. Mỗi UC dùng thiết kế đã chốt, chỉ bổ sung delta nếu kiểm thử phát hiện vấn đề; không quay lại duyệt toàn bộ. Cùng requestId khác nội dung conflict; committed-but-ACK-lost phải lookup; FAILED thiếu tiền không hồi sinh.
