# M4 / delta MySQL — checklist REVIEW 26/10/2026

M4 UC01/T1–T7 đã duyệt19/10 lịch sử; **M4-DB MySQL8.0.46 chưa duyệt**. Phiếu16 mụcD chưa có nghiệm thu toàn bộ thiết kế. Chọn nghiệp vụ không tự nghiệm thu schema/API mới. Sản phẩm cần duyệt:

- [ ] P4.1/2 [schema](database-schema.md) + [ERD](erd.puml): UUID CHAR36 ascii_bin là đề xuất Q14C; constraints/index/FK, vòng đời/historical money,500K tổng.
- [ ] P4.3 [OpenAPI](openapi.yaml), [UC01](api-contract-uc01.md), [toàn backend](api-contract.md): scopes/caller ownership, validation/error/lookup/version/cursor và internal jobs.
- [ ] P4.4 [Design Class](design-class-diagrams/README.md): trách nhiệm/signatures/ports, không JPA domain lẫn ERD.
- [ ] P4.5 [transaction/jobs](concurrency-and-jobs.md): claim/fence/recovery, unknown COMMIT, fee atomic, manual-auto race, no negative balance, period uniqueness, late adjustments.
- [ ] Kiểm thiết kế/implementation CR-CAN-003 đã duyệt: TxAconfirmed→TxB DBreceipt, equalitylate→FAILED/releaseguard; accepted lostACK replay; pending blocks manual/auto, unknowncommit lookup. Không còn blocker quyết định Q24, artifact/test vẫn chưa nghiệm thu.
- [ ] Duyệt config đề xuất: renewal interval10s/batch100, lease30s/retry1..16s, report retention24h; không gán60s UC01 cho renewal.
- [ ] Kiểm chứng MySQL8.0.46 migration/CHECK/UQ/FK/collation/index EXPLAIN, race/rollback/timeout/deadlock/unknown commit thật. Chưa chạy.
- [ ] Duyệt lại các tham số environment JWT claim/JWKS/issuer/audience/permissions và trusted evidence mapping trước runtime.

Điểm đã có thiết kế để review: CR-TIME-001 recordedAt transaction ultimately committed; CR-TIME-002 check dưới account lock trước mutation; CR-CAN-002 all prepaid coverage/no refund; Q21 ngày1/30day example; Q29B grandfather renewal; RC single INSERT SELECT snapshot và unique contribution source toàn cục. Có tài liệu không bằng đã nghiệm thu. Render/YAML validation chỉ xác nhận cấu trúc, không thay owner review hay DB/runtime/performance tests.

Người duyệt/ngày/kết quả: **chưa có**. Không tự tick checkpoint/M5–M8. Điều kiện tiếp theo: chủ dự án review delta MySQL UC01; CR-CAN-003 đã giải semantics Q24; cần review protocol/test UC03/renewal liên quan, không bắt phần chưa phụ thuộc chặn UC01.
