# Tác vụ định kỳ, gia hạn và khôi phục

**DOC-JOB-001 · v0.3 · 26/10/2026 · REVIEW.** Nguồn DEC-JOB-001, DEC-PRE-19/20, Q12/24/25/28/29/30 và CR-TIME-001/002, CR-CAN-002/003. Giữ hai job nghiệp vụ hằng ngày; scheduler/worker là thành phần nội bộ, không actor ngoài. Chưa có code/test/runtime.

## 1. Phân vai, trigger, input/output

| Tác vụ | Trigger | Input | Output / quyền |
|---|---|---|---|
| Worker gia hạn | Kiểm tra thường xuyên gói tới hạn; khoảng chạy cấu hình cần review | Gói autoRenew tới coverageEnd, business cycle key, giờDB | Một kỳ mới hoặc outcome skip/FAILED/technical retry; run/lag metrics |
| JOB-01 | 00:05 hằng ngày Asia/Ho_Chi_Minh; chạy tay có scope admin | Mốc xử lý, cursor/batch checkpoint, package/account tới hạn và backlog | Trạng thái hết hạn/CANCELLED, catch-up renewal theo cùng lõi, run count/errors |
| JOB-02 | 00:30 hằng ngày Asia/Ho_Chi_Minh; chạy tay có scope admin | Ngày báo cáo trước, cutoff/tập nguồn persisted, các ngày còn thiếu, ledger muộn | Summary DB bất biến, đóng góp BASE/ADJUSTMENT, CSV tùy chọn, run status |
| Recovery UC01 | Mỗi60giây và tín hiệu kho số sau commit | WAITING hợp lệ FIFO/deadline/claim | Activation SUCCEEDED/EXPIRED; không thu phí/gia hạn gói |

Worker gia hạn không có lịch riêng cho từng thuê bao; không mặc định60giây của recovery UC01. Worker/JOB-01 không hợp nhất thành lịch thường xuyên thay00:05; CR-JOB-001 không được chọn. RabbitMQ chỉ đánh thức cấp số, không tự là lịch/nguồn quyết tiền. Redis không là lock tiền/cycle registry.

Admin proposal: POST /api/v1/internal/jobs/{jobCode}/runs; GET /api/v1/internal/jobs/{jobCode}/runs/{runId}. Kiểm scope job:run/job:read và allowlisted jobCode theo OpenAPI; CSKH thường không có quyền. Lưu người gọi, reason, thời gian và outcome; không đưa token vào log. Business period key khác runId: mỗi lần retry có thể là run mới nhưng không được thu/đóng góp lần hai.

## 2. Worker gia hạn — đầy đủ luồng

Tiền điều kiện: root đăng ký ổn định subscriber+package, autoRenew bật, coverage đã hết/cần xử lý, không có hủy hợp lệ/PENDING_CANCEL, MAIN đúng chủ và được thu; khách cũ đáp ứng grandfather policy nếu gói ngừng bán. Đủ tiền/expiry phải kiểm lại tại xử lý, không tin candidate query.

1. Nhận candidate theo hạn, đọc giới hạn lô/budget; query index/EXPLAIN thuộc thiết kế, không mặc định full scan.
2. Xác định root/version/cycleKey mục tiêu; dùng cùng năng lực transaction với manual UC02 và JOB-01; kiểm lại sau phối hợp.
3. Nếu request/cycle đã hoàn tất, trả outcome cũ/skip. Nếu manual tạo kỳ mới kể từ candidate thì re-evaluate, không tự mua kỳ tiếp nữa.
4. Kiểm cancellation receipt có thẩm quyền; intent PENDING của TxA tạm chặn cả worker/manual và phải được recovery TxB theo CR-CAN-003, không bị bỏ qua. Không gia hạn nếu PENDING_CANCEL hay hủy hợp lệ; ghi skip có nguyên nhân.
5. Dưới account lock kiểm giờDB/expiry, MAIN balance, package/group policy và giá hiện hành. Chụp giá/ngày/catalog version, không dùng Redis stale làm giá thu.
6. Xử lý sau hạn vài giây hoặc nhiều ngày đều chỉ mua MỘT kỳ mới từ lúc xử lý hợp lệ; không cấp quyền ngược khoảng trống, không thu nhiều kỳ đã bỏ lỡ. End theo ngày lịch như UC02.
7. Thu AUTO_RENEWAL_FEE từ MAIN, ghi cycle/root/version/outcome nguyên tử. Một kỳ chỉ một phí; commit xong mới có quyền kỳ mới.
8. Ghi progress/checkpoint sau commit, metric lag/skip/error. Backlog lớn không tự thay quy tắc hoặc promise không gián đoạn.

Nhánh: thiếu tiền là FAILED nghiệp vụ cuối của đúng cycle attempt, gói hết hiệu lực/EXPIRED và không tự thử lại sau nạp. Muốn tiếp dùng đăng ký lại UC02 theo ý định mới. Lỗi DB/deadlock/lock timeout là kỹ thuật, rollback toàn bộ và retry hữu hạn; không gán là thiếu tiền. Commit phản hồi mất phải tra cycle/operation trước tác động lại. Không dùng runId mới để lách guard của kỳ đã thành công/FAILED.

## 3. JOB-01 — hết hạn/hủy/rà soát

Tiền điều kiện: lịch/chạy tay hợp lệ, xác định mốcDB, phối hợp nhiều runner; dữ liệu account/package giữ lịch sử. Input không được phép bật purge/tái cấp số.

1. Tạo job execution/audit, nhận phần việc theo cơ chế claim/checkpoint REVIEW.
2. Account hết hạn: kiểm lại expiresAt/state; chuyển EXPIRED/soft disable, giữ số dư và lịch sử. MAIN không có hạn không tự bị đánh hết hạn. API chặn đúng timestamp ngay cả JOB-01 chưa chạy.
3. Subscription PENDING_CANCEL và coverageEnd<=now: hoàn tất CANCELLED; giữ mọi cycle/fee đã trả. Không cắt tại cuối kỳ đang dùng nếu còn kỳ trả trước.
4. Subscription tới hạn không hủy: rà soát worker đã làm chưa; đã có kỳ mới thì skip. Nếu đủ điều kiện auto, gọi cùng lõi ở mục2 và Q25=B; không tạo logic phí khác.
5. Nếu không gia hạn/FAILED thiếu tiền/autoRenew tắt: EXPIRED; quyền đã mất từ end, không phải từ lúc job chạy. Phân biệt CANCELLED do hủy với EXPIRED do hết hạn.
6. Ghi counts processed/skipped/failed và checkpoint theo phần việc thật commit; một bản ghi lỗi được báo riêng, không bỏ vĩnh viễn phần còn lại.
7. Chạy lại/chạy tay/nhiều instance kiểm state/cycle/business key, không tạo thêm fee, không reset expiry/cancel deadline.

Đua hủy/worker theo CR-CAN-003: TxA PENDING intent tạm ngăn renewal; TxB sau xác nhận TxA commit lấy receipt DB dưới slot/request. Receipt<observedCoverageEnd/cùngversion mới accepted; recovery receipt>=end khi chưa có accepted outcome thì reject CANCELLATION_RECEIVED_TOO_LATE/release guard. Accepted TxB đã commit phải replay kể cả lookup sau deadline. Không tự retarget kỳ mới; root/version đổi conflict không charge. Arrival time/log không cho ưu tiên; implementation/recovery phải kiểm chứng, quy tắc nghiệp vụ đã chốt. Không hoàn tiền, không xóa package definition, không phục hồi account hết hạn hoặc giải phóng số thuê bao từ job này.

## 4. JOB-02 — nguồn, snapshot và tổng hợp

Tiền điều kiện: dữ liệu ledger immutable đã commit, recordedAt do DB, attribution đúng một cycle/package; timezone/cutoff/period key có thẩm quyền. NgàyD là [D00:00,D+1 00:00) địa phương đổi UTC; không dùng service time client. Summary theo ngày gốc/thuê bao/gói/sourceType, count và tổng integerVND, không cộng CREDIT vào tiêu dùng.

1. Chọn ngày chưa có finalized/cần recovery theo thứ tự; mặc định ngày hôm trước, catch-up các ngày bỏ lỡ, không chỉ chạy hôm qua rồi bỏ ngày cũ.
2. Tạo hoặc đọc daily_report_period. Cutoff/tập nguồn lần đầu lưu bền vững phải giữ nguyên qua mọi retry, kể cả lần đầu chưa hoàn tất summary.
3. Materialize membership của ledger đủ điều kiện bằng một SELECT statement snapshot nhất quán, lưu source IDs/chiều/amount; không dùng hai truy vấn live khác tập rồi nói snapshot cố định. Timestamp cutoff đơn thuần/auto-increment ID không chứng minh membership bất biến.
4. Từ tập persisted, nhận đóng góp BASE đúng một lần cho mỗi source_transaction_id; unique contribution ngăn vừa BASE vừa ADJUSTMENT, không chỉ unique trong từng bảng riêng.
5. Aggregate count/sum từ đóng góp persisted, reconcile với nguồn/các chiều; publish FINALIZED nguyên tử khi đủ. Batch checkpoint chỉ tăng sau commit. Run retry dùng lại membership/contribution, không increment summary mù lần hai.
6. Đối với ledger committed phát hiện muộn thuộc ngàyD đã finalized theo recordedAt: không sửa baselineD. Tạo ADJUSTMENT ở kỳ báo cáo sau, giữ sourceTransactionId/ngày gốc/ngày ghi nhận/amount/count/reason; một nguồn chỉ đóng góp một lần toàn cục.
7. Nếu ledger commit sau midnight nhưng recordedAt trước midnight, vẫn thuộc ngày gốc; nếu đã lọt snapshot thì BASE, nếu sau snapshot thì later adjustment. Không backdate từ client chỉ vì gửi serviceOccurredAt cũ.
8. CSV tùy chọn xuất từ phiên bản finalized, ghi file hoàn chỉnh rồi publish; lỗi export chỉ retry export đúng phiên bản, không chạy lại aggregation/fee. Kiểm count/sum/checksum theo thiết kế.

Nhánh/lỗi: không nguồn thì finalized rỗng có count0/sum0, không là lỗi; nguồn sai attribution thì báo/quarantine và không tự gán A/B; crash trước snapshot commit rollback, sau snapshot commit recovery đúng snapshot; nhiều runner cùng period chỉ một publisher hợp lệ; lease hết không đủ cho runner cũ tự ghi. Commit unknown phải đọc period/contribution; không cộng lần hai. CSV lỗi không làm summary đã finalized thành rỗng.

## 5. Run lifecycle và vận hành

Đề xuất REVIEW: PENDING/RUNNING/SUCCEEDED/FAILED và checkpoint/lease có fencing; lỗi từng item được ghi trong counters/results, run còn item lỗi được đánh FAILED, không tạo trạng thái PARTIAL_FAILED; trạng thái cụ thể đồng bộ OpenAPI/design. FAILED run kỹ thuật khác FAILED operation thiếu tiền. Mỗi run có jobCode/period/caller/mode/start/end/counts/error summary/retry linkage; không ghi PII/token đầy đủ. Cấu hình interval/batch/backoff/timeout/budget cần review và đo; không gắn nhãn tối ưu hoặc SLA khi chưa đo.

Quan sát backlog, last successful run, oldest waiting/cycle lag, receipt pending, PROCESSING treo, report snapshot lỗi, late adjustment và unresolved commit. Service restart tìm phần việc từ DB, không dựa queue là nguồn duy nhất. Backup/restore cần thực nghiệm P7; demo không purge (retention lịch sử5năm cấu hình khác full-result replay).

## 6. AC và truy vết

| Mã | Kiểm chứng dự kiến | Truy vết |
|---|---|---|
| AC-JOB-01 | Worker thường xuyên riêng, JOB-01 00:05/JOB-02 00:30, recovery UC01 60s tách biệt | FR-JOB-01/03; DEC-JOB-001 |
| AC-JOB-02 | Quyền hết đúng timestamp dù job chưa chạy | FR-JOB-02/03; FR-ACC-07 |
| AC-JOB-03 | Worker+manual+JOB-01 đồng thời một kỳ chỉ một phí | FR-PKG-07; Q04/23 |
| AC-JOB-04 | Downtime nhiều ngày chỉ một kỳ mới, không backdate/multi-fee | FR-PKG-07; Q25B |
| AC-JOB-05 | Thiếu MAIN tiền FAILED cuối, nạp sau không tự hồi sinh attempt | FR-ACC-04; Q07 |
| AC-JOB-06 | Hủy accepted chặn renewal, dùng hết mọi coverage rồi CANCELLED | FR-PKG-05/06; Q22/24 |
| AC-JOB-07 | Account expiry giữ số dư/history, MAIN không hạn giữ đúng | FR-ACC-01/07 |
| AC-JOB-08 | Retry/chạy tay/nhiều runner/restart không thu lại hoặc bỏ backlog | FR-JOB-01..04; NFR-07 |
| AC-JOB-09 | JOB-02 [dayStart,dayEnd) theo recordedAt của committed ledger | FR-RPT-01; CR-TIME-001 |
| AC-JOB-10 | Cutoff/membership persisted lần đầu giữ qua lỗi/retry | FR-JOB-04; Q12A |
| AC-JOB-11 | Finalized bất biến; mỗi source BASE hoặc ADJUSTMENT đúng một lần | FR-JOB-04; Q28A |
| AC-JOB-12 | OR/AND query và daily aggregation không trộn phí/count/credit sai | FR-RPT-01/02/04 |
| AC-JOB-13 | Export retry không aggregate lại; admin scope/audit enforced | Q30/31; NFR-09 |
| AC-JOB-14 | recordedAt trước midnight commit sau xuất BASE hoặc adjustment đúng snapshot, không mất/đúp | CR-TIME-001; Q12/28 |

[UC02](use-cases/UC02-Package-Subscription.md), [UC03](use-cases/UC03-Package-Cancellation.md), [UC06](use-cases/UC06-Consumption-Query.md), [SRS](SRS.md), [concurrency REVIEW](../04-design/concurrency-and-jobs.md) chứa quy tắc chung. Tất cả AC dự kiến chưa chạy; không đóng OQ/runtime/M5–M8 từ file này.
