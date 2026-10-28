> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC04 — Cộng tiền vào tài khoản chỉ định

**DOC-UC04 · v0.1 · 26/10/2026 · REVIEW.** Nghiệp vụ theo phiếu16/xác nhận26/10; DTO/thiết kế mới cần rà soát.

## Mục tiêu, tác nhân, tiền điều kiện và dữ liệu

Ứng dụng CSKH có quyền cộng tiền. Subscriber/account tồn tại, account thuộc subscriber, loại cho phép CREDIT và còn hạn. Tài khoản phụ đã được seed với quyền/expiry rõ; không mở ví mới trong flow này.

POST /api/v1/accounts/{accountId}/credits. Input requestId, subscriberId, amountVnd và tham chiếu nghiệp vụ nếu schema có; output operation/requestId/status, transactionId, balanceBefore/After, recordedAt, eligibilityCheckedAt, correlationId. Không tích hợp cổng ngân hàng; đây là lệnh backend được cấp quyền mô phỏng.

## Luồng chính

1. Kiểm quyền/schema/canonical caller+UC04+ID.
2. Đọc và đồng bộ account chỉ định, kiểm chủ/loại/quyền ACTIVE.
3. Lấy giờDB dưới khóa ngay trước cập nhật; account có expiresAt<=checkedAt thì từ chối.
4. Kiểm amount integer>0<=10triệu, dư mới<=100triệu và không tràn.
5. Cập nhật đúng account, ghi một CREDIT_TOPUP ledger balanceBefore/After/recordedAt và outcome nguyên tử.
6. Commit rồi trả SUCCEEDED; không cộng account khác.

## Luồng thay thế

A1 MAIN không có expiresAt mặc định: không hết hạn do job tự suy ra.

A2 Account phụ có quyền CREDIT và còn hạn: cộng đúng phụ; MAIN giữ nguyên.

A3 Replay/timeout tra ID: kết quả số dư lịch sử cũ, không cộng thêm.

A4 Check trước expiry, commit qua expiry được phép theo CR-TIME-002.

## Ngoại lệ

E1 Thiếu quyền/account khác subscriber/loại không cho credit: từ chối không rò dữ liệu.

E2 Amount0/âm/lẻ/vượt10triệu hoặc dư vượt100triệu: từ chối toàn bộ.

E3 Account hết hạn/không ACTIVE: không thay số dư, không ledger thành công.

E4 DB lỗi/commit unknown: rollback nếu xác định hoặc tra outcome, không blind retry với ID mới.

## Hậu điều kiện

Một CREDIT thành công tăng đúng amount và có một ledger gắn account/operation; account khác không đổi. FAILED không có biến động. Cộng tiền không tự tạo package/khôi phục FAILED renewal/hết hạn account.

## Chính sách chung và bằng chứng

JWT ngoài backend; kiểm issuer/audience/signature/expiry/scope trước truy cập, caller chỉ tra operation mình. Tập demo dùng chung không bỏ kiểm quyền từng nghiệp vụ. Canonical payload có mọi trường ảnh hưởng nghiệp vụ (gồm target/version/amount/context), không gồm JWT/correlationId/thứ tự JSON; null/rỗng không tự tương đương absent khi schema không cho phép. Cùng caller+operation+requestId và cùng nội dung trả outcome cũ, khác nội dung IDEMPOTENCY_CONFLICT. PROCESSING trả đang xử lý; không chạy tác động lần hai.

Full result UC02/04/05 giữ30ngày, UC03 giữ7ngày sau trạng thái cuối, cấu hình được; sau đó tombstone giữ mã/fingerprint/outcome tóm tắt, không cho tái dùng mã trong demo. Không dọn PROCESSING. Response replay mô tả kết quả và số dư tại tác động cũ, không hứa số dư hiện tại. GET /api/v1/operations/{operation}/{requestId} là tra chung sau mất phản hồi, luôn caller-owned. Timeout không là FAILED chắc chắn: có thể đã commit; tra/retry cùng ID. FAILED nghiệp vụ cuối không hồi sinh do nạp thêm/đổi cấu hình.

Tiền integer VND >0, <=10.000.000/lệnh, balance<=100.000.000 (cấu hình); kiểm tràn riêng. Không fallback/pooling tài khoản; phí luôn MAIN. Kiểm account bằng giờDB dưới khóa ngay trước cập nhật tiền/kết quả: checkedAt>=expiresAt từ chối, còn hạn ở check có thể commit sau hạn (CR-TIME-002). recordedAt do DB ghi trong transaction cuối cùng commit, không là physical commit time (CR-TIME-001).

## Kiểm chứng và trạng thái tài liệu

Mọi AC bên dưới là kịch bản dự kiến chưa thực thi; cần integration/race test MySQL8.0.46 InnoDB và kiểm ledger/operation/balance/cycle. Runtime5xx/commit-unknown phải được tra cứu trước retry tác động. Lỗi deadlock/timeout lock phải rollback toàn bộ transaction trước retry hữu hạn theo design; không chuyển lỗi kỹ thuật thành thiếu tiền. Không ghi thành công trước commit. [SRS](../SRS.md), [OpenAPI REVIEW](../../04-design/openapi.yaml), [concurrency REVIEW](../../04-design/concurrency-and-jobs.md) mô tả nguồn liên quan. Bản này là REVIEW; nghiệp vụ đã chọn không có nghĩa DTO/SQL/artifact đã nghiệm thu.
## Tiêu chí nghiệm thu và truy vết

Nguồn: FR-ACC-01/02/05..07; BR-20/22; Q02/09/34. Mỗi AC cần test riêng hoặc tổ hợp có đối soát dữ liệu.

| Mã | Điều kiện cần kiểm chứng | Yêu cầu |
|---|---|---|
| AC-CRE-01 | Cộng hợp lệ tăng đúng VND và ledger. | FR-ACC-01/02/05..07 |
| AC-CRE-02 | Không tự chuyển cộng MAIN khi chỉ phụ. | FR-ACC-01/02/05..07 |
| AC-CRE-03 | Loại account không cho CREDIT bị từ chối. | FR-ACC-01/02/05..07 |
| AC-CRE-04 | Amount integer dương và max10triệu được kiểm. | FR-ACC-01/02/05..07 |
| AC-CRE-05 | Dư95triệu cộng10triệu thất bại toàn bộ. | FR-ACC-01/02/05..07 |
| AC-CRE-06 | Đúng expiry từ chối; check trước expiry có thể commit sau. | FR-ACC-01/02/05..07 |
| AC-CRE-07 | Hai credit concurrent không mất cập nhật, đúng cap. | FR-ACC-01/02/05..07 |
| AC-CRE-08 | Replay30ngày/tombstone không cộng lại. | FR-ACC-01/02/05..07 |
| AC-CRE-09 | Commit timeout tra ID xác định outcome. | FR-ACC-01/02/05..07 |
| AC-CRE-10 | Không tự khôi phục expired/renewal FAILED. | FR-ACC-01/02/05..07 |
