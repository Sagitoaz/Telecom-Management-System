> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC02 — Đăng ký gói có thu phí

**DOC-UC02 · v0.1 · 26/10/2026 · REVIEW.** Nghiệp vụ theo phiếu16/xác nhận26/10; DTO/thiết kế mới cần rà soát.

## Mục tiêu, tác nhân, tiền điều kiện và dữ liệu

Ứng dụng CSKH có quyền đăng ký gửi lệnh mua một kỳ. Thuê bao tồn tại/được thao tác; MAIN thuộc thuê bao, ACTIVE còn hạn, đủ tiền. Gói đang bán cho khách mới hoặc có quyền grandfather cho đăng ký cũ. Danh mục/đăng ký hiện tại có read hỗ trợ trả version và coverageEnd; caller lấy version, không tự đoán. Đăng ký đang PENDING_CANCEL không được mua nối.

Input: requestId, subscriberId, packageId, expectedSubscriptionVersion (0 cho lần đầu), không nhận override autoRenew/amount/account do client tự quyết; gia hạn theo baseline đủ tiền/chưa hủy. Output: status, operation, requestId, subscriptionId/version, cycleId/start/end/paidPrice, coverageEnd, feeTransactionId, balance sau phí, correlationId. POST /api/v1/package-subscriptions; đọc hỗ trợ GET /api/v1/packages và GET /api/v1/subscribers/{subscriberId}/package-subscriptions.

## Luồng chính

1. Kiểm token/scope, schema, quyền thuê bao, requestId và canonical; replay/conflict xử lý trước tác động.
2. Lấy giá/số ngày/policy hiện hành có thẩm quyền; nhận dạng đăng ký ổn định của subscriber+package; kiểm expectedVersion sau đồng bộ tranh chấp.
3. Kiểm MAIN đúng chủ, quyền/expiry bằng giờDB dưới khóa, số dư và hạn mức. Kiểm trạng thái đăng ký/khả năng bán hoặc gia hạn nhóm cũ.
4. Nếu còn coverage hợp lệ, kỳ mới start=coverageEnd; nếu chưa có/đã gián đoạn, start=lúc xử lý hợp lệ. End=00:00 localDate(start)+durationDays; ngày đầu tính ngày1.
5. Thu MAIN đúng một phí, ghi ledger PACKAGE_FEE, cycle bất biến giá/ngày/catalog version/cycleKey, cập nhật root coverageEnd/version và outcome SUCCEEDED nguyên tử.
6. Sau commit trả kết quả; từ đó kỳ có thể bắt đầu ngay hoặc là quyền tương lai đã trả. Không có tác động thành công một phần.

## Luồng thay thế

A1 Còn hạn: thêm đúng một kỳ từ coverageEnd, không chồng quyền; không dùng cuối kỳ hiện tại khi còn kỳ trả trước.

A2 Đã hết hạn: bắt đầu mới từ mốc xử lý, không thu/cấp quyền khoảng trống. Root ổn định giữ lịch sử; version không reset.

A3 Hai requestId khác cùng expectedVersion: một thành công, request kia STALE_SUBSCRIPTION_VERSION không thu tiền; muốn mua thêm có chủ đích phải đọc version mới.

A4 Manual cạnh tranh worker cùng kỳ: một phí; bên còn lại trả conflict/trạng thái hiện tại, không lặng lẽ nối kỳ nữa.

A5 Gói ngừng bán: từ chối khách mới, cho khách cũ đủ grandfather policy gia hạn theo cấu hình/giá hiện hành snapshot; không sửa giá kỳ cũ.

A6 Retry/mất phản hồi theo chính sách chung; cùng ID đã FAILED thiếu tiền vẫn FAILED sau nạp.

## Ngoại lệ

E1 Gói/thuê bao/tài khoản không tồn tại hoặc không thuộc quyền: từ chối, không lộ dữ liệu ngoài quyền.

E2 MAIN hết hạn/không đủ tiền/vượt giới hạn/gói không hợp lệ: FAILED, không có cycle hoặc ledger thành công.

E3 PENDING_CANCEL/stale version: conflict, không thu thêm.

E4 Lỗi ghi cycle/ledger/outcome: rollback toàn bộ; timeout commit unknown phải tra, không đoán.

## Hậu điều kiện

SUCCEEDED có đúng một cycle/fee và dư khớp, root version tăng một lần; mọi kỳ trước bất biến. FAILED nghiệp vụ không đổi balance/coverage; PROCESSING chưa là có quyền. Thuê bao có thể có nhiều package khác nhau; không cấm chồng giữa hai loại gói bằng quy tắc chưa duyệt.

## Chính sách chung và bằng chứng

JWT ngoài backend; kiểm issuer/audience/signature/expiry/scope trước truy cập, caller chỉ tra operation mình. Tập demo dùng chung không bỏ kiểm quyền từng nghiệp vụ. Canonical payload có mọi trường ảnh hưởng nghiệp vụ (gồm target/version/amount/context), không gồm JWT/correlationId/thứ tự JSON; null/rỗng không tự tương đương absent khi schema không cho phép. Cùng caller+operation+requestId và cùng nội dung trả outcome cũ, khác nội dung IDEMPOTENCY_CONFLICT. PROCESSING trả đang xử lý; không chạy tác động lần hai.

Full result UC02/04/05 giữ30ngày, UC03 giữ7ngày sau trạng thái cuối, cấu hình được; sau đó tombstone giữ mã/fingerprint/outcome tóm tắt, không cho tái dùng mã trong demo. Không dọn PROCESSING. Response replay mô tả kết quả và số dư tại tác động cũ, không hứa số dư hiện tại. GET /api/v1/operations/{operation}/{requestId} là tra chung sau mất phản hồi, luôn caller-owned. Timeout không là FAILED chắc chắn: có thể đã commit; tra/retry cùng ID. FAILED nghiệp vụ cuối không hồi sinh do nạp thêm/đổi cấu hình.

Tiền integer VND >0, <=10.000.000/lệnh, balance<=100.000.000 (cấu hình); kiểm tràn riêng. Không fallback/pooling tài khoản; phí luôn MAIN. Kiểm account bằng giờDB dưới khóa ngay trước cập nhật tiền/kết quả: checkedAt>=expiresAt từ chối, còn hạn ở check có thể commit sau hạn (CR-TIME-002). recordedAt do DB ghi trong transaction cuối cùng commit, không là physical commit time (CR-TIME-001).

## Kiểm chứng và trạng thái tài liệu

Mọi AC bên dưới là kịch bản dự kiến chưa thực thi; cần integration/race test MySQL8.0.46 InnoDB và kiểm ledger/operation/balance/cycle. Runtime5xx/commit-unknown phải được tra cứu trước retry tác động. Lỗi deadlock/timeout lock phải rollback toàn bộ transaction trước retry hữu hạn theo design; không chuyển lỗi kỹ thuật thành thiếu tiền. Không ghi thành công trước commit. [SRS](../SRS.md), [OpenAPI REVIEW](../../04-design/openapi.yaml), [concurrency REVIEW](../../04-design/concurrency-and-jobs.md) mô tả nguồn liên quan. Bản này là REVIEW; nghiệp vụ đã chọn không có nghĩa DTO/SQL/artifact đã nghiệm thu.
## Tiêu chí nghiệm thu và truy vết

Nguồn: FR-PKG-01..04/07; BR-04/18..20; Q04/05/21/23/25/29. Mỗi AC cần test riêng hoặc tổ hợp có đối soát dữ liệu.

| Mã | Điều kiện cần kiểm chứng | Yêu cầu |
|---|---|---|
| AC-PKG-01 | Phí và cycle cùng commit hoặc cùng rollback. | FR-PKG-01..04/07 |
| AC-PKG-02 | MAIN thiếu tiền: không đổi dư, không tạo phí/cycle. | FR-PKG-01..04/07 |
| AC-PKG-03 | Mua nối từ coverageEnd, không trùng quyền. | FR-PKG-01..04/07 |
| AC-PKG-04 | Sau gián đoạn bắt đầu hiện tại, không backdate. | FR-PKG-01..04/07 |
| AC-PKG-05 | Gói30ngày01/10 10:15 hết31/10 00:00+07. | FR-PKG-01..04/07 |
| AC-PKG-06 | PENDING_CANCEL từ chối nối. | FR-PKG-01..04/07 |
| AC-PKG-07 | Hai mã cùng version chỉ một phí; stale không thu. | FR-PKG-01..04/07 |
| AC-PKG-08 | Manual/auto cùng kỳ tối đa một phí, không tự thêm kỳ. | FR-PKG-01..04/07 |
| AC-PKG-09 | Replay30ngày/tombstone không thu lại. | FR-PKG-01..04/07 |
| AC-PKG-10 | Giá đổi không đổi snapshot kỳ cũ. | FR-PKG-01..04/07 |
| AC-PKG-11 | Ngừng bán cho khách mới; grandfather hợp lệ còn được gia hạn. | FR-PKG-01..04/07 |
| AC-PKG-12 | MAIN expiry check DB dưới khóa; commit qua hạn không đổi check hợp lệ. | FR-PKG-01..04/07 |
