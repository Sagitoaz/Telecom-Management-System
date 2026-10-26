# Kế hoạch kiểm thử backend

**DOC-TST-001 · v0.3 · 26/10/2026 · REVIEW · NOT_RUN.** Tài liệu mô tả kịch bản và oracle cần thực thi, không phải báo cáo test. Hiện chưa có kết quả unit/integration/API/concurrency/tải/restore để tuyên bố backend đạt. M0–M4 UC01 đã duyệt lịch sử không thay kiểm thử deltaMySQL.

Nguồn: [SRS](../01-requirements/SRS.md), [tiếp nhận quyết định](../00-project/decision-receipt-20261026.md), [clarification log](../00-project/clarification-log.md), [64 ngoại lệ](../07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md), [OpenAPI REVIEW](../04-design/openapi.yaml). Nghiệp vụ đã chốt theo phiếu16 và CR-CAN-002/003, CR-TIME-001/002; kỹ thuật/design cần review và bằng chứng thực chạy.

## 1. Phạm vi và tầng kiểm thử

| Tầng | Mục tiêu | Không được thay thế |
|---|---|---|
| Unit | Validation, normalization T1/T2, canonical, ngày lịch, điều kiện hạn/tiền/policy, AND/OR và công thức đo | Không chứng minh row lock/constraint/COMMIT bằng mock |
| Integration DB | Migration/constraint/JDBC mapping, transaction rollback/unknown, khóa/race/claim/recovery trên MySQL8.0.46 InnoDB | Không dùng H2/PG/Supabase thay bằng chứng MySQL |
| API/security | Schema/error/status/replay, JWT/JWKS/issuer/audience/scope/caller, cursor, admin scope | Không coi contract YAML hợp lệ là API đã chạy |
| Component failure | MQ lost/duplicate, cache stale/failure, IdP rotation/unavailable, worker crash/reclaim, export | Không coi happy path thay recovery |
| Concurrency/system | Barrier/fault injection dưới xử lý thật: số/tiền/cycle/cancel/report source | Không chứng minh tranh chấp chỉ bằng client gửi đồng thời |
| Performance | 500K tổng, mixed30+30, latency/error/50%attempt hot10cặp, đối soát | Không dùng replay/fee nội bộ làm TPS mới |
| Handover | Clean setup/seed/config, backup/restore, restart, fail closed | Không đóng M8 từ tài liệu hướng dẫn chưa chạy |

Phạm vi UC01–06, worker gia hạn, JOB-01/JOB-02 và recovery UC01. Không thêm mạng viễn thông thật, cổng ngân hàng, UI CSKH, engine giá/lưu lượng, KYC nội bộ, API refund hoặc ngầm tái cấp số.

## 2. Điều kiện vào và fixture tái lập

Trước integration cần schema/API/transaction deltaMySQL được review; có JDK21, MySQL8.0.46/InnoDB, READ COMMITTED cho write, cùng cấu hình charset/collation/UTC DATETIME(6) đã ghi; UUID physical là proposalQ14C cần review, API UUID string nhất quán. Nhà chạy topology MySQL/RabbitMQ/Redis/Keycloak; office unit/doc không DB không là môi trường chứng minh integration.

Mỗi run ghi source revision hoặc hash tree, document versions/CRs, tool/JDK/MySQL/driver, OS/CPU/RAM/disk/network, pool/timeouts/worker intervals/batch/retry/instance count, timezone/clock sync, seed/runId và reset procedure. Không đưa secret/PII thật vào fixture/log.

Fixture có: Customer nhiều verified documents, hồ sơ mới/khớp/mâu thuẫn; E.164 khả dụng/đã cấp, kho rỗng; callerA/B với scopes đúng/sai và evidence scope riêng; MAIN0/đủ/thiếu/sát cap, phụ có loại/quyền/hạn rõ; package đang bán/ngừng bán với grandfather policy, duration1/15/30 ngày, giá snapshot; root mới/version0, ACTIVE nhiều prepaid cycles, PENDING_CANCEL/CANCELLED/EXPIRED; ledger thuộc ranh giới ngày/late source, snapshot/query chưa READY/finalized. Không tự seed phụ không expiry/quyền cấu hình.

## 3. Quy tắc tạo oracle

Expected tính từ fixture trước run và quy tắc đã chốt; actual thu riêng từ response/DB/audit. Một test phải xác định cả trạng thái lẫn tác động: số lượng subscriber/account/cycle/ledger/contribution, số dư và khóa nghiệp vụ. Tổng tiền đúng không đủ nếu có ledger sai nguồn/amount tình cờ bù nhau.

Giờ kiểm biên dùng clock abstraction cho unit và test hook điều khiển barrier tại thời điểmDB trong integration; không thay đổi đồng hồOS production. Với COMMIT unknown, injection trước COMMIT/đangCOMMIT/sauDBcommit trướcHTTP phải phân biệt; caller timeout không chứng minh rollback. Không dùng sleep tùy ý làm bằng chứng thứ tự; dùng barrier/latch/fault hook và DB evidence.

## 4. Nhóm kiểm thử và oracle cụ thể

### 4.1 UC01 — giữ đủ27AC baseline

[UC01](../01-requirements/use-cases/UC01-Subscriber-Activation.md), AC-SUB-01..27; TC-EDGE-01..12.

- AUTO có số/SELECTED khả dụng: trả cuối đồng bộ sau commit; đúng1Subscriber,1MAIN0 không hạn,1assignment và Customer phù hợp. Hai SELECTED cùng số: tối đa1success, loser không đổi số/WAITING/account dư.
- AUTO hết kho: consentfalse FAILED NO_AVAILABLE_NUMBER, consenttrue WAITING tối đa24h. WAITING không có Subscriber/Account thành công; retry/restart giữ deadline. Tại deadline finalization>=deadline EXPIRED, không cấp số từ yêu cầu cũ.
- Verified doc reuse đúng hồ sơ; mọi optional gửi khác bị từ chối, absent không sửa, null/rỗng validationerror theoT1; giữ dấu/số0đầu. Caller thiếu evidence scope không tạo/liên kết hồ sơ; không ghép theo tên/ngày sinh. Input090 bị từ chối, +E.164 chuẩn mới được nhận.
- FIFO khi thêm kho, tie-break theo thiết kế; MQ mất/lặp và worker crash không cấp trùng; recovery60s không phải promise hoàn tất60s, không là cadence gia hạn. Replay7ngày/tombstone và caller isolation đúngT3.

### 4.2 UC02 — phí nguyên tử/version/ngày lịch

[UC02](../01-requirements/use-cases/UC02-Package-Subscription.md), AC-PKG-01..12; TC-EDGE-13..22/29.

Oracle: successful purchase có đúng1PACKAGE_FEE và1immutable cycle gắn root, MAIN giảm đúngprice, root version tăng1, coverageEnd đúng. MAIN29.999/fee30.000 FAILED không thay dư/cycle. Fault sauUPDATE/sauinsertfee trướccommit rollback mọi effect; chưa có committedsuccess.

Ngày lịch Asia/Ho_Chi_Minh:01/10/2026 10:15+07 mua30ngày hết31/10/2026 00:00+07. Nối trả trước từ coverageEnd midnight; sau gián đoạn startvalidprocessinginstant, không backdate. Đổi giá/duration chỉ ảnh hưởng kỳ mới snapshot; không đọc cache stale để thu.

Hai ID khác cùng expectedSubscriptionVersion chỉ một success; loser409/conflict không charge, không tự mua kỳ sau. Manual/auto/JOB-01 cùng targetcycle cùng oracle mộtfee, không tự nối thêm. Ngừng bán chặn khách mới; root đủ grandfather được gia hạn theo giá/policy hiện hành; thiếu policy cấu hình là lỗi fixture/config cần sửa, không tự phát minh rule.

### 4.3 UC03 — tất cả kỳ trả trước và receipt sau commit

[UC03](../01-requirements/use-cases/UC03-Package-Cancellation.md), AC-CAN-01..10; TC-EDGE-23..27/53/54; CR-CAN-002/003.

TxA commit intent PENDING và operation PROCESSING, giữ observedVersion/observedCoverageEnd. PENDING tạm chặn manual extension và renewal. Sau xác nhận TxA commit, TxB giữ slot/request, kiểm terminal/version rồi lấy receiptCheckedAt từ DB. Cùng version và receipt < observedCoverageEnd: ACCEPTED/PENDING_CANCEL, operation SUCCEEDED; TxB commit có thể qua hạn. Receipt >= end và chưa có accepted outcome: FAILED CANCELLATION_RECEIVED_TOO_LATE, REJECTED intent, release guard; không retarget/refund. Version đổi: conflict không charge.

Bắt buộc kiểm: TxA commit trước hạn nhưng receipt sau hạn bị reject; receipt trước hạn nhưng TxB commit sau vẫn accepted; TxA commit unknown phải lookup/recover; TxB accepted đã commit nhưng mất HTTP rồi retry sau hạn vẫn replay accepted; crash giữa TxA/TxB, recovery sau hạn reject thay vì giả định early receipt; hai classifier và manual/worker cùng root không vượt PENDING guard. Sau accepted dùng hết mọi kỳ đã trả rồi CANCELLED; đúng coverageEnd mất quyền dù job trễ. Full result7 ngày/tombstone giữ outcome; ID hủy mới khi pending trả CANCELLATION_PENDING.

### 4.4 UC04/05 — tiền đúng account và eligibility ngay trước update

[UC04](../01-requirements/use-cases/UC04-Account-Credit.md), AC-CRE-01..10; [UC05](../01-requirements/use-cases/UC05-Account-Debit.md), AC-DEB-01..12; TC-EDGE-30..40.

Amount VND nguyên dương <=10.000.000; balance <=100.000.000, cấu hình được và kiểm tràn riêng. Dư95 triệu cộng10 triệu từ chối toàn bộ. GAME20.000/MAIN50.000, debit GAME30.000 thất bại, không fallback/pooling. Phí UC02 luôn MAIN. Kiểm permission CREDIT/DEBIT theo từng loại account; sai owner từ chối không lộ dữ liệu.

CR-TIME-002: lấy giờ DB sau khi đã giữ mọi account/root/cycle lock cần thiết và ngay trước cập nhật tiền/kết quả. Đợi account hoặc root lock qua expiry phải sample mới: checkedAt >= expiry từ chối; check trước expiry hợp lệ có thể commit sau expiry. Kiểm cycle [start,end) cũng tại eligibility đó. MAIN expiry NULL không tự expire. Không có gói, sai owner, cycle tương lai/hết hạn đều không được debit dù đủ tiền. PENDING_CANCEL còn cycle bao phủ vẫn được tiêu dùng. Consumption ledger thuộc đúng một cycle; fee nội bộ không thành UC05 lần hai.

D1 FAILED thiếu tiền, sau đó CREDIT rồi retry D1 vẫn FAILED; D2 mới được đánh giá mới. Same ID20 attempts chỉ một ledger/effect; khác amount/account/cycle/serviceReference conflict. Mất COMMIT ACK phải tra outcome trước tác động lại. Deadlock1213 và lock timeout1205 kiểm riêng: rollback toàn transaction trước retry, không insert ledger sau statement lỗi. Retry budget cạn không được giả business FAILED.

### 4.5 UC06/JOB-02 — recordedAt, tập cố định, nguồn không đếm trùng

[UC06](../01-requirements/use-cases/UC06-Consumption-Query.md), AC-RPT-01..12; [jobs](../01-requirements/scheduled-jobs.md), AC-JOB-09..14; TC-EDGE-41..51/55.

CR-TIME-001: recordedAt23:59:59 ngàyD trong transaction cuối cùng commit00:00:01 ngàyD+1 vẫn thuộc ngàyD. Chỉ ledger đã commit được đọc; precommit/rollback không là nguồn. [X,Y) gồmX, không gồmY. OR là bất kỳ gói đã chọn; AND phải đủ mọi gói theo subscriber; CREDIT/FAILED loại; includeFee=true mới thêm PACKAGE_FEE/AUTO_RENEWAL_FEE đúng một lần.

Query mới có queryRequestId caller/filter-scoped: mất phản hồi đầu rồi retry cùng key phải dùng lại query/cutoff/tập nguồn; đổi filter409. Membership persist từ một statement snapshot nhất quán; derive AND/OR và page từ tập đã persist. Giao dịch commit giữa các trang không thay tập cũ. Default50/max200; cursor chống sửa, gắn caller/filter/query/version; snapshot hết hạn yêu cầu lượt mới, không live fallback hoặc giữ transaction trong lúc người dùng đọc.

JOB-02: cutoff bền vững lần đầu giữ qua rerun kể cả materialization lỗi. Source set đã persist thì retry aggregate đúng tập đó. Finalized bất biến. Ledger thuộc ngày đã chốt bị phát hiện muộn tạo ADJUSTMENT ở kỳ sau; global unique(sourceTransactionId) ngăn vừa BASE vừa ADJUSTMENT hoặc hai period cùng nhận nguồn. Ngày nguồn cũ chưa finalized phải đóng BASE ngày gốc theo thứ tự, không đẩy adjustment sớm. CSV lỗi sau DB finalized chỉ retry export; kiểm version/count/sum/checksum trước publish.

### 4.6 Worker/JOB-01/recovery/vận hành

AC-JOB-01..08/13; TC-EDGE-28/29/52..56/64. Worker kiểm thường xuyên theo cấu hình cần review; JOB-01 hằng ngày00:05, JOB-02 hằng ngày00:30, recovery UC01 mỗi60s tách biệt. Các đường dùng chung cycle guard. Gia hạn muộn vài giây hoặc nhiều ngày chỉ mua một kỳ từ lúc xử lý hợp lệ, không truy thu hoặc phục quyền khoảng trống. FAILED thiếu tiền không tự thử lại sau nạp.

Test crash sau commit trước checkpoint, trước commit, worker hết lease quay lại sau owner khác; fencing/terminal/cycle guard phải ngăn ghi đè. Account EXPIRED giữ dư/history, catalog/paid cycles không xóa; không tái cấp số. Chạy tay API có scope job:run/job:read riêng và audit; CSKH thường403. Catch-up không bỏ ngày thiếu. IdP/JWKS không hợp lệ phải fail closed; Redis lỗi fallback DB; MQ lỗi recovery DB. Restore phải giữ request/tombstone/ledger/cycle/report/intent trong backup đã xác nhận; không hứa RPO/RTO chưa đo.

## 5. Đối soát độc lập và bằng chứng

1. Chụp số dư/tồn đầu theo seed; ghi mọi mutation, gồm CREDIT, manual fee, renewal fee, debit.
2. Map mỗi logical key caller+operation+requestId tới canonical/outcome và mọi attempt. Monetary SUCCEEDED phải có đúng một ledger khớp account/subscriber/amount/source; FAILED không có ledger thành công trái phép. PROCESSING/UNKNOWN phải tra với budget; còn lại là unresolved.
3. Kiểm chuỗi ledger balanceBefore/After theo thứ tự account có thẩm quyền; không dùng UUID làm commit order. Dư cuối = dư đầu + tổng credit − tổng debit (gồm phí đúng một lần), không âm/vượt cap/orphan.
4. Kiểm cycle/fee/root version/coverage, receipt/cancelEffectiveAt theo CR; activation assignment/Subscriber/MAIN đủ và không duplicate. Mỗi timeout phải có outcome evidence.
5. Kiểm report attribution, query fixed membership, mỗi nguồn expense đủ điều kiện có đúng một BASE hoặc ADJUSTMENT sau catch-up; summary từ contribution, finalized bất biến; CSV khớp phiên bản.
6. Lưu oracle input, raw response/audit/query đã ẩn secret/PII, missing/extra/duplicate/mismatch/unresolved theo từng key; không kết luận từ HTTP2xx hoặc tổng tiền tình cờ cân.

## 6. Mẫu kết quả và điều kiện ra

Mỗi case lưu testId/AC/FR/CR, version/config/seed, prestate, steps/barrier/fault, expected, actual, evidence, status NOT_RUN/PASS/FAIL/BLOCKED, defect và retest reference. BLOCKED ghi rõ thiếu tool/config/implementation; Q24 đã chốt CR-CAN-003, không còn là lựa chọn chưa trả lời. NOT_RUN không là PASS.

Nhóm UC chỉ PASS khi có bằng chứng thực chạy và không còn lỗi chặn; outcome timeout chưa rõ vẫn unresolved, không tự đổi thành FAILED để né đối soát. [Protocol tải](performance-test.md) quy định mẫu số/cửa sổ/bằng chứng tranh chấp. Chỉ sau log đầy đủ mới xét P6/M7. Tài liệu này không tạo test executable/runtime; triển khai tiếp tục vertical slice UC01 trước.
