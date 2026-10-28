> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Phân tích nghiệp vụ và chỉ mục UML

**DOC-ANL-001 · v0.3 · 26/10/2026 · REVIEW.** M2 nền/UC01 đã duyệt19/10 được giữ lịch sử; nguồn hiện hành đồng bộ quyết định phiếu16 và xác nhận26/10. Sơ đồ sửa/mới cần review, không tự nghiệm thu M2 mở rộng/M4-DB/P5. UML nguồn là `.puml`; SVG do lượt render kiểm chứng tạo từ đúng nguồn, không dùng ảnh baseline cũ làm bằng chứng.

## 1. Đầu vào và mức trừu tượng

[SRS0.7](../01-requirements/SRS.md), [UC01–06](../01-requirements/use-cases/UC02-UC06.md), [jobs](../01-requirements/scheduled-jobs.md), [DEC/CR](../00-project/clarification-log.md) là nguồn. Domain Model mô tả khái niệm/quan hệ nghiệp vụ, không PK/FK/index/JPA. Sequence dùng các trách nhiệm nghiệp vụ, không chỉ định thuật toán khóaSQL. Khóa/claim/physical schema/API thuộc [thiết kế](../04-design/concurrency-and-jobs.md), [ERD](../04-design/erd.puml), [OpenAPI](../04-design/openapi.yaml) REVIEW.

CR-CAN-002: hủy cuối toàn bộ thời gian đã trả, không hoàn tiền. CR-CAN-003: mốc nhận là receipt giờDB trong TxB sau xác nhận TxA commit, strict trước deadline/cùngversion; receipt muộn reject, accepted outcome cũ replay không xét hạn lại. CR-TIME-001: recordedAt là giờDB ghi trong transaction cuối cùng commit, khác physical commit time. CR-TIME-002: check DB expiry dưới khóa ngay trước update, commit có thể qua hạn. DEC-JOB-001: worker gia hạn thường xuyên vàJOB-01 hằng ngày00:05; không hợp nhất/hẹn một timer cho mỗi thuê bao. Q21: ngày đầu tính ngày đăng ký, 30ngày01/10 10:15 hết31/10 00:00+07.

## 2. Danh mục sơ đồ nguồn

| Nhóm | Sản phẩm | Nội dung/AC |
|---|---|---|
| Use Case | [Toàn hệ thống](use-case-diagram.puml) | Actor CSKH/report/admin/IdP bên ngoài, UC01–06 và read hỗ trợ; scheduler nội bộ |
| Use Case | [UC01 Hybrid](uc01-use-case-diagram.puml) | Chọn/tự cấp là biến thể; WAITING extend chỉ empty+consent |
| Domain | [Mô hình nghiệp vụ](domain-model.puml) | Customer/giấy tờ/Subscriber/kho số; root/cycle/fee/account; request/receipt; period/contribution |
| Sequence | [UC01](sequence-diagrams/uc01-activation.puml) | Đồng bộ, retry/conflict, số chọn/auto, WAITING/FIFO/deadline/recovery; AC-SUB01..27 |
| Sequence | [UC02](sequence-diagrams/uc02-package-subscription.puml) | Read version, thuMAIN+cycle, snapshot terms, stale conflict; AC-PKG01..12 |
| Sequence | [UC03](sequence-diagrams/uc03-package-cancellation.puml) | Durable receipt, coverage đã trả, accepted/effective, receipt sau xác nhận commit theo CR-CAN-003; AC-CAN01..10 |
| Sequence | [UC04](sequence-diagrams/uc04-account-credit.puml) | Specified account/cap/expiry/ledger; AC-CRE01..10 |
| Sequence | [UC05](sequence-diagrams/uc05-account-debit.puml) | Account+effective cycle+funds, no fallback; AC-DEB01..12 |
| Sequence | [UC06](sequence-diagrams/uc06-consumption-query.puml) | Fixed membership, OR/AND, recordedAt, caller-bound pagination; AC-RPT01..12 |
| Sequence | [Renewal worker](sequence-diagrams/renewal-worker.puml) | One late cycle, shared guards, failure finality; AC-JOB03..05 |
| Sequence | [JOB-01](sequence-diagrams/job01-expiration.puml) | Expiry/cancel/backlog, skip successful renewal; AC-JOB01..08 |
| Sequence | [JOB-02](sequence-diagrams/job02-daily-aggregation.puml) | First fixed source snapshot, dedup BASE/ADJUSTMENT, export retry; AC-JOB09..14 |
| State | [Activation request](state-diagrams/activation-request.puml) | PROCESSING/WAITING/SUCCEEDED/FAILED/EXPIRED và tombstone |
| State | [Package subscription](state-diagrams/package-subscription.puml) | ACTIVE/PENDING_CANCEL/CANCELLED/EXPIRED và coverage predicate |
| State | [Cancellation intent](state-diagrams/cancellation-intent.puml) | PENDING/ACCEPTED/REJECTED, post-commit receipt/replay |
| State | [Account](state-diagrams/account.puml) | ACTIVE/EXPIRED, giữ số dư và timestamp checks |
| State | [Operation request](state-diagrams/operation-request.puml) | PROCESSING/SUCCEEDED/FAILED, replay/tombstone, timeout không là terminal |
| State | [Report period](state-diagrams/report-period.puml) | Fixed membership/aggregation/finalized/export, immutable day |

## 3. Ràng buộc domain và truy vết

- Mỗi Subscriber thuộc đúng1Customer; Customer có0..NSubscriber và nhiều giấy tờ đã xác minh. Không tự ghép vì trùng họ tên/ngày sinh; việc thêm giấy tờ khách cũ ngoài UC01. FR-SUB-01/07, BR-01/25, AC-SUB02..04/22/23.
- Một số E.164 cấp tối đa1Subscriber hiện tại; xem AVAILABLE không giữ chỗ. Kho bổ sung/claim/expiry không làm yêu cầu đã hết hạn thành công. FR-SUB-02..06/10/14, BR-02/03/16/24, AC-SUB06..12/25..27.
- ActivationRequest WAITING chưa có Subscriber/MAIN thành công. Thành công có đúng1Subscriber và MAIN0 không hạn mặc định. Caller+requestId/canonical chống lặp; recovery60s riêng. FR-SUB-08..15, AC-SUB13..27.
- PackageSubscription là root ổn định subscriber+package, giữ version/coverageEnd; từng SubscriptionCycle bất biến [start,end), snapshot giá/ngày. coverageEnd không đồng nghĩa end của cycle đang dùng. Root chưa có lần mua thành công có thể0cycle trong lúc giữ chỗ điều phối, không công bố thành subscription có quyền. FR-PKG-01..04, AC-PKG01..12.
- CancellationIntent accepted bền vững chặn mọi nối/gia hạn; PENDING_CANCEL giữ mọi kỳ trả trước; cuối coverage thành CANCELLED. Không có refund transaction. FR-PKG-05/06, BR-05, AC-CAN01..10.
- Mỗi Account thuộc1Subscriber; mỗi Transaction thuộc1Account. Consumption ledger thuộc một cycle/package còn hiệu lực, fee thuộc một paid cycle; CREDIT không là tiêu dùng. Không fallback/pooling. FR-ACC-01..07, BR-06/18/20/22, AC-CRE/DEB.
- OperationRequest là lệnh nghiệp vụ UC02–05, không phải JWT/correlationId; cùng caller+operation+ID có một effect tối đa; PROCESSING không thành công. Replay money30ngày/cancel7ngày rồi tombstone; không dọn đang xử lý. FR-ACC-06, NFR-07.
- DailyReportPeriod giữ tập nguồn cố định và kết quả finalized; ReportContribution ghi nguồn thuộcBASE hoặcADJUSTMENT đúng một lần toàn cục. recordedAt quyết định ngày gốc; snapshot membership quyết định baseline có thấy nguồn không. FR-RPT-01..04, FR-JOB-04, AC-RPT/AC-JOB09..14.

## 4. Các biên cần thiết kế/kiểm chứng

Q24 đã chốt CR-CAN-003: TxA commit PENDING intent tạm chặn mua nối/renewal; TxB sau xác nhận TxA commit lấy receiptCheckedAt DB dưới slot/request. Nếu cùng version và receipt<observedCoverageEnd thì accepted/PENDING_CANCEL/outcomeSUCCEEDED; TxB có thể commit sau deadline. Nếu chưa có accepted outcome và receipt>=end thì FAILED CANCELLATION_RECEIVED_TOO_LATE, release guard, không tự retarget/refund. Root/version đổi conflict không charge. Accepted outcome mất HTTP phải replay cùngID cả sau hạn; TxA commit unknown lookup/recovery, không đoán rollback. Quy tắc nghiệp vụ không còn mở; thiết kế khóa/claim/recovery và test implementation vẫn REVIEW. Input/claims/expiry config, physical UUID, cursor retention/batch/worker interval là technical REVIEW.

State ACTIVE không tự cấp quyền nếu không có cycle bao phủ checkedAt. JOB-01 trễ không kéo quyền qua expiry; delayed auto renewal chỉ một kỳ từ xử lý hợp lệ, không tự hồi sinh FAILED thiếu tiền. Root/version/cycle guard chống manual/auto/chạy tay tạo phí trùng; cơ chế cụ thể thuộc design.

Không remote includes hoặc phụ thuộc font riêng trong nguồn. Mọi sequence/state là mô tả hành vi dự kiến; không thay integration/concurrency/performance tests. Bằng chứng syntax/render/visual QA được ghi ở [verification](../verification.md) sau khi chạy thật. Không có commit/branch/code/migration từ việc biên soạn.

## Activity Diagram và Class Diagram tập trung — bổ sung 26/10/2026

Theo yêu cầu đọc lại báo cáo, bổ sung 11 Activity Diagram thể hiện nhánh chuẩn/thay thế/lỗi và 3 Class Diagram nghiệp vụ dễ đọc. Đây là các góc nhìn REVIEW của quy tắc hiện hành, không đổi DEC/CR hoặc tự nghiệm thu checkpoint.

| Nhóm | Nguồn | Ảnh |
|---|---|---|
| Luồng nghiệp vụ xuyên suốt | [PlantUML](activity-diagrams/00-end-to-end.puml) | [PNG](activity-diagrams/00-end-to-end.png) · [SVG](activity-diagrams/00-end-to-end.svg) |
| UC01 — Cấp số, chờ và thất bại | [PlantUML](activity-diagrams/01-activation.puml) | [PNG](activity-diagrams/01-activation.png) · [SVG](activity-diagrams/01-activation.svg) |
| UC02 — Mua gói và mua nối | [PlantUML](activity-diagrams/02-subscription.puml) | [PNG](activity-diagrams/02-subscription.png) · [SVG](activity-diagrams/02-subscription.svg) |
| UC03 — Nhận hủy, receipt và mất phản hồi | [PlantUML](activity-diagrams/03-cancellation.puml) | [PNG](activity-diagrams/03-cancellation.png) · [SVG](activity-diagrams/03-cancellation.svg) |
| UC04 — Cộng tiền đúng accountId | [PlantUML](activity-diagrams/04-credit.puml) | [PNG](activity-diagrams/04-credit.png) · [SVG](activity-diagrams/04-credit.svg) |
| UC05 — Tiêu dùng, số dư và kỳ hiệu lực | [PlantUML](activity-diagrams/05-debit.puml) | [PNG](activity-diagrams/05-debit.png) · [SVG](activity-diagrams/05-debit.svg) |
| Worker gia hạn — một target, một phí | [PlantUML](activity-diagrams/06-renewal.puml) | [PNG](activity-diagrams/06-renewal.png) · [SVG](activity-diagrams/06-renewal.svg) |
| JOB-01 — Hết hạn, hủy và rà soát | [PlantUML](activity-diagrams/07-expiration.puml) | [PNG](activity-diagrams/07-expiration.png) · [SVG](activity-diagrams/07-expiration.svg) |
| UC06 — Tạo tập kết quả và đọc nhiều trang | [PlantUML](activity-diagrams/08-query.puml) | [PNG](activity-diagrams/08-query.png) · [SVG](activity-diagrams/08-query.svg) |
| JOB-02 — Chốt nguồn và điều chỉnh muộn | [PlantUML](activity-diagrams/09-daily-report.puml) | [PNG](activity-diagrams/09-daily-report.png) · [SVG](activity-diagrams/09-daily-report.svg) |
| Luồng chung — Gửi lại, lỗi nghiệp vụ và mất phản hồi | [PlantUML](activity-diagrams/10-common-outcomes.puml) | [PNG](activity-diagrams/10-common-outcomes.png) · [SVG](activity-diagrams/10-common-outcomes.svg) |
| Class Diagram nghiệp vụ — Khách hàng, thuê bao và tài khoản | [PlantUML](class-diagrams/customer-subscriber-account.puml) | [PNG](class-diagrams/customer-subscriber-account.png) · [SVG](class-diagrams/customer-subscriber-account.svg) |
| Class Diagram nghiệp vụ — Gói, kỳ đã trả và ý định hủy | [PlantUML](class-diagrams/package-cycle-cancellation.puml) | [PNG](class-diagrams/package-cycle-cancellation.png) · [SVG](class-diagrams/package-cycle-cancellation.svg) |
| Class Diagram nghiệp vụ — Yêu cầu và báo cáo | [PlantUML](class-diagrams/requests-and-reporting.puml) | [PNG](class-diagrams/requests-and-reporting.png) · [SVG](class-diagrams/requests-and-reporting.svg) |
