> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC05 — Trừ tiền tiêu dùng độc lập

**DOC-UC05 · v0.1 · 26/10/2026 · REVIEW.** Nghiệp vụ theo phiếu16/xác nhận26/10; DTO/thiết kế mới cần rà soát.

## Mục tiêu, tác nhân, tiền điều kiện và dữ liệu

Ứng dụng có scope tiêu dùng gửi lệnh; account đúng subscriber, được DEBIT, ACTIVE còn hạn; package subscription/cycle đúng thuê bao và có quyền dùng tại eligibility check. PENDING_CANCEL còn quyền tới coverageEnd nếu kỳ bao phủ; CANCELLED/EXPIRED hay kỳ tương lai chưa bắt đầu không hợp lệ.

POST /api/v1/accounts/{accountId}/debits. Input requestId, subscriberId, amountVnd, subscriptionId, cycleId và serviceReference bắt buộc theo DTO REVIEW, đủ xác định attribution duy nhất; output status,transactionId,balanceBefore/After,recordedAt,eligibilityCheckedAt,package/cycle attribution,correlationId. Backend mô phỏng lệnh đã định giá, không đo lưu lượng mạng/engine tính cước.

## Luồng chính

1. Kiểm token/scope/schema/canonical caller+UC05+ID và replay/conflict.
2. Đồng bộ đúng account; kiểm chủ/loại/quyền, đọc gói/kỳ có thẩm quyền sau phối hợp.
3. Lấy giờDB dưới khóa account ngay trước cập nhật; kiểm expiry account và kỳ [start,end), subscriber/package policy.
4. Kiểm amount/hạn mức/số dư đủ; không fallback/pooling MAIN/phụ.
5. Trừ account đúng amount, ghi một SERVICE_CONSUMPTION ledger gắn một cycle/package và operation outcome nguyên tử.
6. Sau commit trả SUCCEEDED; chỉ logical debit mới thành công mới vào UC05 TPS.

## Luồng thay thế

A1 GAME20.000 MAIN50.000, debit GAME30.000: thiếu GAME, không trừ ví nào.

A2 PENDING_CANCEL nhưng kỳ hiện tại còn hiệu lực: vẫn tiêu dùng, tới end đúng mốc thì chặn.

A3 Client gửi gói không thuộc thuê bao/không còn quyền: từ chối dù còn tiền.

A4 Replay/commit timeout theo common registry; một logical effect.

A5 Fee nội bộ đăng ký không đi qua UC05 API và không đếm UC05 độc lập.

## Ngoại lệ

E1 Không có gói/không đúng cycle/chưa start/đã end: NO_EFFECTIVE_CYCLE, không trừ.

E2 Không đủ tiền: FAILED, không ledger tài chính thành công; không đổi số dư.

E3 Account hết hạn/không cho debit/sai chủ: từ chối.

E4 Amount invalid hoặc idempotency conflict: giữ dữ liệu gốc.

E5 Deadlock/lock timeout/commit unknown: xử lý kỹ thuật có giới hạn/tra, không hứa rollback chỉ vì HTTP timeout.

## Hậu điều kiện

SUCCEEDED giảm đúng amount và giữ một attribution, ledger nhất quán account/operation. FAILED không đổi tiền/kỳ; không âm. Retry không là tiêu dùng mới; không gán khoản tiền nguyên vẹn đồng thời A và B.

## Chính sách chung và bằng chứng

JWT ngoài backend; kiểm issuer/audience/signature/expiry/scope trước truy cập, caller chỉ tra operation mình. Tập demo dùng chung không bỏ kiểm quyền từng nghiệp vụ. Canonical payload có mọi trường ảnh hưởng nghiệp vụ (gồm target/version/amount/context), không gồm JWT/correlationId/thứ tự JSON; null/rỗng không tự tương đương absent khi schema không cho phép. Cùng caller+operation+requestId và cùng nội dung trả outcome cũ, khác nội dung IDEMPOTENCY_CONFLICT. PROCESSING trả đang xử lý; không chạy tác động lần hai.

Full result UC02/04/05 giữ30ngày, UC03 giữ7ngày sau trạng thái cuối, cấu hình được; sau đó tombstone giữ mã/fingerprint/outcome tóm tắt, không cho tái dùng mã trong demo. Không dọn PROCESSING. Response replay mô tả kết quả và số dư tại tác động cũ, không hứa số dư hiện tại. GET /api/v1/operations/{operation}/{requestId} là tra chung sau mất phản hồi, luôn caller-owned. Timeout không là FAILED chắc chắn: có thể đã commit; tra/retry cùng ID. FAILED nghiệp vụ cuối không hồi sinh do nạp thêm/đổi cấu hình.

Tiền integer VND >0, <=10.000.000/lệnh, balance<=100.000.000 (cấu hình); kiểm tràn riêng. Không fallback/pooling tài khoản; phí luôn MAIN. Kiểm account bằng giờDB dưới khóa ngay trước cập nhật tiền/kết quả: checkedAt>=expiresAt từ chối, còn hạn ở check có thể commit sau hạn (CR-TIME-002). recordedAt do DB ghi trong transaction cuối cùng commit, không là physical commit time (CR-TIME-001).

## Kiểm chứng và trạng thái tài liệu

Mọi AC bên dưới là kịch bản dự kiến chưa thực thi; cần integration/race test MySQL8.0.46 InnoDB và kiểm ledger/operation/balance/cycle. Runtime5xx/commit-unknown phải được tra cứu trước retry tác động. Lỗi deadlock/timeout lock phải rollback toàn bộ transaction trước retry hữu hạn theo design; không chuyển lỗi kỹ thuật thành thiếu tiền. Không ghi thành công trước commit. [SRS](../SRS.md), [OpenAPI REVIEW](../../04-design/openapi.yaml), [concurrency REVIEW](../../04-design/concurrency-and-jobs.md) mô tả nguồn liên quan. Bản này là REVIEW; nghiệp vụ đã chọn không có nghĩa DTO/SQL/artifact đã nghiệm thu.
## Tiêu chí nghiệm thu và truy vết

Nguồn: FR-ACC-03..07; BR-06/20/22; Q09/11/34. Mỗi AC cần test riêng hoặc tổ hợp có đối soát dữ liệu.

| Mã | Điều kiện cần kiểm chứng | Yêu cầu |
|---|---|---|
| AC-DEB-01 | Đủ điều kiện giảm đúng amount, một ledger. | FR-ACC-03..07 |
| AC-DEB-02 | Thiếu tiền không đổi dư/ledger thành công. | FR-ACC-03..07 |
| AC-DEB-03 | Hai debit80.000 trên dư100.000 tối đa một thành công. | FR-ACC-03..07 |
| AC-DEB-04 | Nhiều credit/debit cùng account đúng đối soát. | FR-ACC-03..07 |
| AC-DEB-05 | GAME thiếu không lấy MAIN, không pooling. | FR-ACC-03..07 |
| AC-DEB-06 | Không có gói bị từ chối dù đủ tiền. | FR-ACC-03..07 |
| AC-DEB-07 | Sai chủ/cycle tương lai/hết hạn bị từ chối. | FR-ACC-03..07 |
| AC-DEB-08 | Pending cancel còn kỳ dùng được, hết end chặn. | FR-ACC-03..07 |
| AC-DEB-09 | Attribution đúng một cycle, fee không đếm UC05. | FR-ACC-03..07 |
| AC-DEB-10 | Replay30ngày/tombstone không trừ lại. | FR-ACC-03..07 |
| AC-DEB-11 | Check DB dưới lock/expiry và timeout được xử lý đúng. | FR-ACC-03..07 |
| AC-DEB-12 | 50%attempt hot10cặp có overlap thực, unresolved0 khi đối soát. | FR-ACC-03..07 |
