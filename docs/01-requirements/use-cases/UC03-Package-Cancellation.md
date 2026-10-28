> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC03 — Hủy gói cuối toàn bộ thời gian đã trả

**DOC-UC03 · v0.1 · 26/10/2026 · REVIEW.** Nghiệp vụ theo phiếu16/xác nhận26/10; DTO/thiết kế mới cần rà soát.

## Mục tiêu, tác nhân, tiền điều kiện và dữ liệu

Ứng dụng CSKH có quyền hủy, subscription thuộc thuê bao được thao tác và còn quyền đã trả/chưa PENDING_CANCEL. Input: requestId, subscriptionId; expectedSubscriptionVersion bắt buộc trong hợp đồng REVIEW để bảo vệ trạng thái caller đã đọc. Output: status, requestId, subscriptionId/state/version, acceptance evidence, cancelEffectiveAt=coverageEnd, correlationId. POST /api/v1/package-subscriptions/{subscriptionId}/cancellations. Hủy không thu/hoàn tiền.

Accepted nghĩa là đã nhận bền vững và chặn gia hạn mới, không phải quyền đã hết. Effective nghĩa là tới cancelEffectiveAt và chuyển CANCELLED. Tất cả kỳ đã trả (kể cả kỳ tương lai) được dùng hết.

## Luồng chính — tiếp nhận sau xác nhận commit (CR-CAN-003)

1. Xác thực/quyền/schema và caller+UC03+requestId/canonical. Nếu outcome đã có, replay; không tính lại hạn cho yêu cầu đã accepted.
2. Phối hợp root/slot, kiểm expectedSubscriptionVersion và paid coverage; request hủy mới khi PENDING_CANCEL bị từ chối. Ghi observedVersion/observedCoverageEnd cho ý định này.
3. **TxA:** persist intent PENDING và operation PROCESSING, commit. Intent PENDING tạm chặn manual extension và renewal; không được bỏ qua chỉ vì root vẫn ACTIVE. HTTP arrival/cancelRequestedAt chưa là accepted.
4. Chỉ sau khi backend xác nhận TxA commit bền vững mới bắt đầu **TxB:** lock slot/request, kiểm lại version/observedCoverageEnd, kiểm outcome chưa finalized rồi lấy giờDB `receiptCheckedAt`.
5. Nếu version không đổi và receiptCheckedAt < observedCoverageEnd: accept intent, root PENDING_CANCEL, acceptedAt=receiptCheckedAt, cancelEffectiveAt=coverageEnd, operation SUCCEEDED; commit TxB rồi trả HTTP. TxB commit có thể qua deadline, không hủy receipt hợp lệ đã lấy trước đó.
6. Nếu receiptCheckedAt >= observedCoverageEnd và chưa có accepted outcome persisted: reject intent, operation FAILED với CANCELLATION_RECEIVED_TOO_LATE; gỡ guard PENDING sau commit. Không tự chuyển yêu cầu sang kỳ mới, không hoàn phí. Nếu root/version đổi: conflict, không thu/hoàn tiền.
7. Sau accepted, quyền dùng hết mọi kỳ đã trả; tới cancelEffectiveAt chuyển CANCELLED qua JOB-01. Quyền vẫn mất đúng timestamp dù job chưa đồng bộ state.

## Luồng thay thế

A1 Có nhiều kỳ trả trước: cancelEffectiveAt là cuối mọi kỳ, không cuối kỳ đang dùng. Chặn mua thêm ngay sau tiếp nhận.

A2 Hủy mới khi PENDING_CANCEL: CANCELLATION_PENDING, cùng ID cũ replay kết quả gốc.

A3 Gia hạn đã commit trước khi hủy hợp lệ: đọc lại coverage/version; hủy theo coverage đã trả mới, không đảo phí; version mismatch trả conflict để caller xác nhận lại.

A4 TxA đã commit trước hạn nhưng xác nhận/receipt chỉ sau hạn: FAILED CANCELLATION_RECEIVED_TOO_LATE theo CR-CAN-003. Không suy đoán accepted từ log hoặc precommit time; không tự retarget kỳ mới.

A5 TxB accepted đã commit nhưng mất HTTP: cùng ID luôn replay accepted outcome, kể cả tra sau deadline. TxA commit unknown: lookup/recover trước, không suy rollback; nếu chưa có accepted outcome và receipt sau deadline thì reject theo quy tắc mới. Không gửi ID mới tự động.

## Ngoại lệ

E1 Không đúng chủ/quyền/không tồn tại: từ chối.

E2 Đã CANCELLED/EXPIRED không còn quyền trả trước: không tạo hủy cho kỳ cũ.

E3 TxA/TxB commit unknown: tra operation/intent bằng cùng ID để xác định outcome; chưa có accepted bền vững thì PROCESSING/recovery, không falsely accepted. Recovery receipt sau deadline reject, không hứa ưu tiên từ ý định ban đầu.

E4 Hủy và auto race: phải chứng minh coordination không bỏ qua intent đã hợp lệ; rollback/retry kỹ thuật không đổi accepted thành hiệu lực sớm.

## Hậu điều kiện

Accepted bền vững: PENDING_CANCEL, toàn bộ coverage đã trả giữ nguyên, không mua nối/gia hạn. Effective: CANCELLED ở cuối coverage; không ledger tài chính do hủy. Không accepted nếu chỉ có ý định trong RAM/log.

## Mốc tiếp nhận và mất phản hồi — quyết định đã duyệt

CR-CAN-003 ngày26/10/2026: chủ dự án đồng ý mốc xác nhận sau commit và xử lý mất phản hồi. `receiptCheckedAt` do DB cấp trong TxB sau khi TxA được xác nhận bền vững, dưới điều phối slot/request, là mốc xét trước hạn. Chỉ receipt nhỏ hơn observedCoverageEnd và cùng version mới accepted; bằng deadline là muộn. Timestamp ghi trong TxA/HTTP arrival không thay mốc này. Commit TxB có thể xong sau hạn mà vẫn giữ receipt đã hợp lệ.

TxA PENDING tạm ngăn nối/gia hạn, có recovery hữu hạn và audit, không treo vô thời hạn. TxB accepted/rejected và outcome commit cùng nhau. Nếu mất phản hồi sau TxB commit, lookup/retry cùng ID trả outcome đã lưu; không lấy giờ mới để biến accepted cũ thành FAILED. Nếu chưa có accepted outcome và recovery mới lấy receipt sau deadline, reject CANCELLATION_RECEIVED_TOO_LATE, release guard, giữ lịch sử; không tự retarget kỳ/gói hoặc refund. Root/version thay đổi là conflict không charge. Cơ chế khóa/recovery/claim cụ thể và race tests là REVIEW implementation, không còn là lựa chọn nghiệp vụ Q24 chưa chốt.

## Chính sách chung và bằng chứng

JWT ngoài backend; kiểm issuer/audience/signature/expiry/scope trước truy cập, caller chỉ tra operation mình. Tập demo dùng chung không bỏ kiểm quyền từng nghiệp vụ. Canonical payload có mọi trường ảnh hưởng nghiệp vụ (gồm target/version/amount/context), không gồm JWT/correlationId/thứ tự JSON; null/rỗng không tự tương đương absent khi schema không cho phép. Cùng caller+operation+requestId và cùng nội dung trả outcome cũ, khác nội dung IDEMPOTENCY_CONFLICT. PROCESSING trả đang xử lý; không chạy tác động lần hai.

Full result UC02/04/05 giữ30ngày, UC03 giữ7ngày sau trạng thái cuối, cấu hình được; sau đó tombstone giữ mã/fingerprint/outcome tóm tắt, không cho tái dùng mã trong demo. Không dọn PROCESSING. Response replay mô tả kết quả và số dư tại tác động cũ, không hứa số dư hiện tại. GET /api/v1/operations/{operation}/{requestId} là tra chung sau mất phản hồi, luôn caller-owned. Timeout không là FAILED chắc chắn: có thể đã commit; tra/retry cùng ID. FAILED nghiệp vụ cuối không hồi sinh do nạp thêm/đổi cấu hình.

Tiền integer VND >0, <=10.000.000/lệnh, balance<=100.000.000 (cấu hình); kiểm tràn riêng. Không fallback/pooling tài khoản; phí luôn MAIN. Kiểm account bằng giờDB dưới khóa ngay trước cập nhật tiền/kết quả: checkedAt>=expiresAt từ chối, còn hạn ở check có thể commit sau hạn (CR-TIME-002). recordedAt do DB ghi trong transaction cuối cùng commit, không là physical commit time (CR-TIME-001).

## Kiểm chứng và trạng thái tài liệu

Mọi AC bên dưới là kịch bản dự kiến chưa thực thi; cần integration/race test MySQL8.0.46 InnoDB và kiểm ledger/operation/balance/cycle. Runtime5xx/commit-unknown phải được tra cứu trước retry tác động. Lỗi deadlock/timeout lock phải rollback toàn bộ transaction trước retry hữu hạn theo design; không chuyển lỗi kỹ thuật thành thiếu tiền. Không ghi thành công trước commit. [SRS](../SRS.md), [OpenAPI REVIEW](../../04-design/openapi.yaml), [concurrency REVIEW](../../04-design/concurrency-and-jobs.md) mô tả nguồn liên quan. Bản này là REVIEW; nghiệp vụ đã chọn không có nghĩa DTO/SQL/artifact đã nghiệm thu.
## Tiêu chí nghiệm thu và truy vết

Nguồn: FR-PKG-05/06; BR-05/19; CR-CAN-002/003; Q24 và xác nhận26/10. Mỗi AC cần test riêng hoặc tổ hợp có đối soát dữ liệu.

| Mã | Điều kiện cần kiểm chứng | Yêu cầu |
|---|---|---|
| AC-CAN-01 | Hủy không dừng quyền trước coverageEnd. | FR-PKG-05/06 |
| AC-CAN-02 | Hủy nhiều kỳ trả trước giữ mọi kỳ, không hoàn tiền. | FR-PKG-05/06 |
| AC-CAN-03 | Tiếp nhận bền vững chặn mua/gia hạn mới. | FR-PKG-05/06 |
| AC-CAN-04 | Cùng ID replay7ngày/tombstone, không tạo hủy lần hai. | FR-PKG-05/06 |
| AC-CAN-05 | ID mới khi pending trả lỗi. | FR-PKG-05/06 |
| AC-CAN-06 | Đúng boundary không tính trước hạn. | FR-PKG-05/06 |
| AC-CAN-07 | TxA commit sớm nhưng receipt sau hạn phải FAILED CANCELLATION_RECEIVED_TOO_LATE; TxB commit muộn không hủy receipt sớm. | FR-PKG-05/06 |
| AC-CAN-08 | PENDING tạm chặn renewal/manual; accepted mất phản hồi replay cùngID sau hạn vẫn accepted; root/version đổi conflict không charge. | FR-PKG-05/06 |
| AC-CAN-09 | Đến hạn CANCELLED phân biệt EXPIRED, job trễ không kéo quyền. | FR-PKG-05/06 |
| AC-CAN-10 | Caller khác không xem được operation hủy. | FR-PKG-05/06 |
