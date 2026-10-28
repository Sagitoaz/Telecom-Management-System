# M4 / delta MySQL — ĐÃ CHỐT 28/10/2026

**Người duyệt: Chủ dự án. Căn cứ: [CR-DOC-004](../00-project/document-approval-20261028.md).** M4 UC01/T1–T7 lịch sử vẫn giữ; delta MySQL và các thiết kế hiện hành nay đã được duyệt để coding.

- [x] P4.1/2 [schema](database-schema.md) + [ERD](erd.puml): MySQL8.0.46/InnoDB, UUID CHAR36 ascii_bin, constraints/index/FK/CHECK và lịch sử.
- [x] P4.3 [OpenAPI](openapi.yaml), [UC01](api-contract-uc01.md), [toàn backend](api-contract.md): HTTP/errors/scopes/ownership/version/cursor.
- [x] P4.4 [Design Class](design-class-diagrams/README.md): trách nhiệm, signatures và ports.
- [x] P4.5 [transaction/jobs](concurrency-and-jobs.md): claim/fence/recovery, unknown COMMIT, allocation/FIFO, fee atomic, no negative balance, period uniqueness và late adjustments.
- [x] Protocol CR-CAN-003: confirmed TxA → TxB receipt; late/equality rejected, pending guard, lost ACK replay.
- [x] Config đề xuất được duyệt làm khởi điểm: renewal10s/batch100, lease30s/retry1..16s, report retention24h.
- [x] Thiết kế JWT/caller/evidence và cấu hình theo môi trường được duyệt; cần cung cấp giá trị runtime thực trước nghiệm thu API.

## Kiểm chứng chuyển sang P5, chưa thực hiện

- [ ] MySQL migrations và CHECK/UQ/FK/collation/index EXPLAIN trên DB thật.
- [ ] Race/rollback/timeout/deadlock/unknown commit/recovery/fault injection theo từng UC.
- [ ] Issuer/audience/JWKS/claim mapping/trusted evidence/secrets cấu hình thật và test API có xác thực.
- [ ] Build, healthcheck và kết quả migration/test tái lập.

**M4-DB đạt ở mức thiết kế. Bước tiếp theo: P5.1.** M5–M8 còn mở; các ô kiểm chứng không phải điều kiện yêu cầu viết lại/duyệt lại bộ tài liệu trước coding.
