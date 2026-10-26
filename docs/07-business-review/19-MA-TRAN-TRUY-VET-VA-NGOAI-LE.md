# Ma trận truy vết và64 ngoại lệ backend

**TRACE-EDGE-001 · v0.3 · 26/10/2026 · REVIEW · TẤT CẢ NOT_RUN.** Giữ nguyên tập64 mã TC-EDGE-01..64; không đổi số27 AC-SUB baseline. Đây là kịch bản dự kiến, chưa có executable tests hoặc kết quả runtime. Các đáp án phiếu16 đã được tiếp nhận; Q21 và CR-CAN-002/003, CR-TIME-001/002 đã rõ, không còn oracle kiểu “đợi chọn Qxx”. Thiết kế/API/implementation/cấu hình vẫn cần review và thực thi kiểm chứng.

Nguồn chuẩn: [decision receipt](../00-project/decision-receipt-20261026.md), [SRS](../01-requirements/SRS.md), [UC01](../01-requirements/use-cases/UC01-Subscriber-Activation.md), [UC02–06](../01-requirements/use-cases/UC02-UC06.md), [jobs](../01-requirements/scheduled-jobs.md), [OpenAPI](../04-design/openapi.yaml), [test plan](../05-testing/test-plan.md), [performance](../05-testing/performance-test.md). Proposal chi tiết không tự thành artifact đã nghiệm thu; NOT_RUN không là PASS.

## 1. Đề bài → yêu cầu → bằng chứng

| Đề bài | Quyết định/yêu cầu | Kịch bản | Bằng chứng cần nộp |
|---|---|---|---|
| REQ-01 domain/CSDL | DEC-01; BR-01; FR-SUB-07/FR-ACC-01; MySQL delta | 02/08/52 | Domain khácERD; migration/PK/FK/unique/mapping thật |
| REQ-02 đấu nối | CR-UC01-001; FR-SUB-01..15; AC-SUB-01..27 | 01..12 | API response, assignment/Subscriber/MAIN, caller/evidence/recovery |
| REQ-03 đăng ký/hủy | FR-PKG-01..07; CR-CAN-002/003 | 13..29 | Atomic fee/cycle, version/day, TxA/TxB receipt và outcome replay |
| REQ-04 cộng/trừ | FR-ACC-01..07; CR-TIME-002 | 30..40 | Per-key ledger/balance, expiry check sau mọi lock, no fallback |
| REQ-05 query/report | FR-RPT-01..04; CR-TIME-001 | 41..51 | Fixed membership, recordedAt/day, source BASE/ADJUSTMENT dedup |
| REQ-06 hết hạn | FR-JOB-01..04; DEC-JOB-001 | 23/24/28/29/37/53..56 | Timestamp quyền, worker/daily/recovery riêng, backlog/rerun |
| REQ-07 tổng500K | NFR-01; DEC-08 | 57 | Seed và DB count từng bảng trước run |
| REQ-08 mixed30+30 | NFR-02/03/11; Q32A | 58/59/62 | Chung600s, committed new logical counts, raw attempt metrics |
| REQ-09 hot50% | NFR-04; Q32A | 60 |10 cặp chia đều50% mọi debit attempt, retry và backend overlap |
| REQ-10 đúng/đủ | NFR-05..07; DEC-11 | 33..40/59..63 | Mỗi operation/ledger/cycle/fee/balance, unresolved0 |

## 2. Cách chạy và đọc oracle

Mỗi dòng cần fixture/seed trước test, bước và barrier/fault hook, response cùng DB/audit evidence, actual và verdict riêng. Schema/endpoint/status theo OpenAPI REVIEW; business error vẫn phải chứng minh không có effect trái phép. Với concurrency phải có dấu vết xử lý/backend lock wait, không chỉ đồng thời ở client. Timestamp trước COMMIT không chứng minh commit-time; recordedAt là giờ nghiệp vụ DB theo CR-TIME-001. Quy tắc receipt hủy là CR-CAN-003, khác mốc recordedAt và money eligibility CR-TIME-002.

Tất cả64 case hiện NOT_RUN. Nếu công cụ/config/schema chưa sẵn sàng khi thực thi, ghi BLOCKED với nguyên nhân cụ thể; không ghi Q24 chưa được quyết định. Có thể tách nhiều test executable cho một ID nhưng giữ reference gốc, không thay mã đã trao đổi.

## 01–12. UC01: hồ sơ, cấp số, WAITING, quyền và retry

| Test ID | Dữ liệu/thao tác | Oracle cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-01 | AUTO có số, hồ sơ mới hợp lệ | Một Subscriber, đúng một MAIN=0 không hạn, số ASSIGNED; phản hồi đồng bộ sau commit | FR-SUB-05/08/09; AC-SUB-05/07 | NOT_RUN |
| TC-EDGE-02 | Cùng giấy tờ đã xác minh, hai request mới; hồ sơ khớp | Một Customer dùng lại, Subscriber/số khác nhau hợp lệ; không customer/doc mồ côi | FR-SUB-07; AC-SUB-02/03/23 | NOT_RUN |
| TC-EDGE-03 | Customer cũ: absent optional; optional khác; null/rỗng; tên Unicode/hoa thường/dấu; số giấy tờ có0đầu | Absent không sửa; mọi trường đã gửi phải khớp sau NFC/trim/collapse/T1; null/rỗng validation error, giữ dấu/số0đầu; không tự ghép/ghi đè. | BR-12/25; AC-SUB-04/22; T1 | NOT_RUN |
| TC-EDGE-04 | Hai SELECTED cùng số với barrier; gửi thêm số090... thay vì E.164 | Tối đa1success, loser không đổi số/WAITING/account dư. Số không đúng dạng+E.164 bị validation error, không đoán quốc gia hoặc tạo hai canonical number. | FR-SUB-05/06; AC-SUB-06/08; Q33A | NOT_RUN |
| TC-EDGE-05 | AUTO kho rỗng, lần lượt consent=false/true | Không consent FAILED; consent WAITING; cả hai không Subscriber/Account | BR-16; AC-SUB-09/10 | NOT_RUN |
| TC-EDGE-06 | Ba WAITING có thứ tự, nạp hai số và AUTO mới | Hai WAITING hợp lệ đầu thành công; AUTO không vượt FIFO; không tự WAITING chỉ vì backlog | BR-24; AC-SUB-25; T4 | NOT_RUN |
| TC-EDGE-07 | Worker ở deadline−1 đơn vị/đúng deadline/+1, retry/restart | Deadline không đổi; đúng hạn trở đi EXPIRED theo mốc finalization T6, không cấp tác động mới | FR-SUB-10/14; AC-SUB-12/26/27 | NOT_RUN |
| TC-EDGE-08 | Crash sau claim, giữa tạo Customer/số/account, sau commit trước reply | Recover cùng ID; rollback không mồ côi; commit rồi không cấp lần hai | NFR-05/07; AC-SUB-13/18/19 | NOT_RUN |
| TC-EDGE-09 | Cùng ID, JSON đổi thứ tự/token/correlation; lần khác đổi evidence/consent | Những metadata được loại không conflict; đổi nội dung canonical conflict; không sửa request gốc | FR-SUB-11; AC-SUB-14/21; T2 | NOT_RUN |
| TC-EDGE-10 | POST/GET UC01 tại trước/đúng/sau replay7ngày; giữ PROCESSING/WAITING qua retention | Replay đầy đủ trong cửa sổ; ngoài cửa sổ POST409 REQUEST_ID_RETIRED/GET410; key/fingerprint/outcome summary giữ, không tái cấp hoặc dọn request đang xử lý. | BR-27; AC-SUB-24; T3 | NOT_RUN |
| TC-EDGE-11 | JWT sai signature/issuer/audience/expiry/scope; callerB GET ID củaA; caller thiếu trusted evidence scope | Fail closed/ẩn tồn tại theo API, không claim hoặc tạo dữ liệu do auth lỗi; scope thao tác/evidence riêng; demo chung không cấp quyền xem operation caller khác. | NFR-09; AC-SUB-15/16/20/23; Q31A | NOT_RUN |
| TC-EDGE-12 | MQ mất/lặp, Redis lỗi, DB restart; nạp kho đã commit | Recovery xử lý một lần/FIFO/trong hạn; Redis không quyết định cấp số; không hứa thành công trong 60s | AC-SUB-11/26; CR-ARCH-001 | NOT_RUN |

## 13–29. UC02/03: phí, ngày lịch, hủy và gia hạn

| Test ID | Dữ liệu/thao tác | Oracle cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-13 | MAIN=100.000; phí gói=30.000 | Balance=70.000; 1 cycle/fee ledger/result đúng nguồn UC02; không thêm UC05 độc lập | FR-PKG-03/04; AC-PKG-01 | NOT_RUN |
| TC-EDGE-14 | MAIN=29.999; phí=30.000 | FAILED; balance giữ 29.999; không cycle hiệu lực mới, không fee ledger thành công | FR-ACC-04; AC-PKG-02 | NOT_RUN |
| TC-EDGE-15 | Inject exception sau UPDATE balance hoặc sau insert fee trước commit | Toàn bộ mutation tiền/gói rollback; request outcome không hứa success | NFR-06; AC-PKG-01 | NOT_RUN |
| TC-EDGE-16 | Hai POST cùng caller/UC02/ID đồng thời rồi replay | Một charge/cycle; các attempt tham chiếu cùng kết quả | FR-ACC-06; AC-PKG-09 | NOT_RUN |
| TC-EDGE-17 | Giá30.000→40.000, duration30→15; seed policy hiện hành, thử cache stale | Kỳ cũ giữ immutable price/duration/catalogVersion; kỳ mới thu đúng DB/current policy. 30ngày01/10/2026 10:15+07 hết31/10 00:00+07; không30x24h. Cache không quyết giá thu. | FR-PKG-01/02; AC-PKG-05/10; Q21 | NOT_RUN |
| TC-EDGE-18 | Root ACTIVE có kỳ đang dùng và kỳ tương lai đã trả; mua thêm với version hiện tại | Kỳ mới nối từ coverageEnd của TẤT CẢ kỳ đã trả, không cuối cycle hiện tại; một fee/cycle, rootversion tăng1, không chồng quyền. | FR-PKG-02; AC-PKG-03 | NOT_RUN |
| TC-EDGE-19 | Hai ID mới cùng subscriber/package/expectedSubscriptionVersion; sau đó đọc version mới và mua có chủ đích | Một request thắng; request stale version conflict không fee/cycle. Chỉ request mới dựa version mới được mua tiếp; không diễn giải click đôi thành hai kỳ tự động. | Q23A; AC-PKG-07 | NOT_RUN |
| TC-EDGE-20 | Manual UC02 và auto worker/JOB-01 cùng target next cycle, barrier sau đọc candidate | Một cycle/fee; loser re-evaluate/conflict/trả trạng thái hiện tại, không tự thu thêm kỳ khác. Dùng target/cycle guard chung, không chỉ riêng requestId. | Q04A; AC-PKG-08; AC-JOB-03 | NOT_RUN |
| TC-EDGE-21 | TxA intent PENDING chưa phân loại; rồi PENDING_CANCEL accepted; manual extension và worker cùng chạy | PENDING tạm ngăn extension/renewal đến phân loại; accepted tiếp tục chặn. Rejected release guard. Retry operation mua cũ trả outcome đã lưu, không tạo mutation mới. | CR-CAN-003; AC-CAN-03/08; AC-PKG-06 | NOT_RUN |
| TC-EDGE-22 | Gói hết5ngày rồi đăng ký mới; account/gói đủ điều kiện, version hiện tại | Kỳ mới bắt đầu lúc xử lý hợp lệ, end theo localDate+duration midnight; không backdate, không thu/cấp quyền cho khoảng trống. | Q05A; AC-PKG-04 | NOT_RUN |
| TC-EDGE-23 | Hủy accepted giữa kỳ có nhiều kỳ trả trước; query trước/đúng coverageEnd, JOB-01 chưa chạy | Trước coverageEnd còn quyền theo cycle bao phủ; đúng end mất quyền và sau đồng bộ CANCELLED. Không dừng tại cuối cycle đang dùng nếu còn paid future cycle; không refund. | CR-CAN-002; AC-CAN-01/02/09 | NOT_RUN |
| TC-EDGE-24 | CR-CAN-003: crash TxA→TxB, TxA commit unknown, receipt trước/đúng/sau observedCoverageEnd, worker/manual đua | Xác nhận TxA commit rồi TxB lấy DB receipt dưới slot/request. Sameversion vàreceipt<end accepted; receipt>=end khi chưa accepted thì FAILED CANCELLATION_RECEIVED_TOO_LATE/release guard. Không suy từ HTTP/precommit; PENDING guard không bị vượt. | CR-CAN-003; AC-CAN-06/07/08 | NOT_RUN |
| TC-EDGE-25 | Hủy sau khi đã trả thêm2kỳ, rồi thử mua/gia hạn mới | cancelEffectiveAt=cuối TOÀN BỘ paid coverage; giữ các kỳ/phí, không refund; sau accepted chặn mọi extension/renewal mới, chưa cắt quyền ngay. | CR-CAN-002; AC-CAN-02/03 | NOT_RUN |
| TC-EDGE-26 | TxB accepted commit nhưng mấtHTTP; retry/GET cùngID sau deadline; gửi ID hủy mới khi pending; qua retention7ngày | SameID replay accepted gốc cả sau hạn; không lấy giờ mới đổi thành TOO_LATE. ID mới CANCELLATION_PENDING; full result7ngày rồi tombstone chống làm lại, không dời effectiveAt. | Q01A; CR-CAN-003; AC-CAN-04/05/08 | NOT_RUN |
| TC-EDGE-27 | TxA commit trước hạn nhưng receipt sau hạn; receipt trước hạn/TxB commit sau; root/version đổi trước TxB | Ca1 TOO_LATE không retarget kỳ mới. Ca2 giữ accepted vì receipt sớm dù TxB commit sau. Versionđổi conflict không charge/refund. Chưa có accepted outcome thì recovery dùng receipt hiện tại, không giả early receipt. | CR-CAN-003; AC-CAN-06/07/08 | NOT_RUN |
| TC-EDGE-28 | Worker trễ20s, ngừng2giờ hoặc nhiều ngày; MAIN đủ tiền; chạy lại cùng candidate/key | Chỉ mua một kỳ từ lúc xử lý hợp lệ, không retroactive quyền/thu mọi kỳ bỏ lỡ. Worker thường xuyên +JOB-01 daily00:05 chung guard; không tự lấy60s recoveryUC01 làm cadence. | Q25B; DEC-JOB-001; AC-JOB-01/04 | NOT_RUN |
| TC-EDGE-29 | Gói ngừng bán: khách mới vs root đủ grandfather; MAIN hết hạn/thiếu tiền; nạp sau rồi retry | Khách mới bị chặn, khách cũ đủ configured policy còn gia hạn theo giá mới snapshot. MAIN invalid/thiếu tiền không fee; thiếu tiền là FAILED cuối, nạp sau không hồi sinh attempt. Lỗi DB không bị coi thiếu tiền. | Q29B; AC-PKG-11; AC-JOB-05 | NOT_RUN |

## 30–40. UC04/05: tiền, expiry và đồng thời

| Test ID | Dữ liệu/thao tác | Oracle cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-30 | Amount=0, −1, 0,5; thiếu hoặc sai kiểu | Validation error, không money mutation/ledger success | BR-20; AC-CRE-04; AC-DEB-01 | NOT_RUN |
| TC-EDGE-31 | Amount10triệu/10triệu+1; balance95triệu credit10triệu; giá phí vượt cap; arithmetic overflow | Boundary hợp lệ xử lý đúng, vượt cap từ chối toàn bộ; balance tối đa100triệu, cấu hình được và kiểm tràn riêng, không wrap/truncate. Giá phí nằm miền tiền hợp lệ. | Q02A; AC-CRE-04/05 | NOT_RUN |
| TC-EDGE-32 | Account khác subscriber/không tồn tại/không quyền CREDIT hoặc DEBIT; GAME20.000/MAIN50.000 debitGAME30.000 | Đúng lớp lỗi API, không lộ dữ liệu. Không fallback/pooling MAIN/phụ; GAME thiếu thì cả hai không đổi, không suy mọi affectedRows0 là thiếu tiền. | Q09A/31A; AC-CRE-02/03; AC-DEB-05 | NOT_RUN |
| TC-EDGE-33 | Balance=100; ba debit khác ID mỗi lệnh=60 chạy chồng lấn | Tối đa 1 success, balance=40 nếu mọi request được xử lý; không ledger thành công cho hai lỗi thiếu tiền | BR-06; AC-DEB-02/03 | NOT_RUN |
| TC-EDGE-34 | Balance=100; 20 CREDIT/DEBIT nhiều giá trị chồng lấn, gồm UC02 fee | Với lịch commit thực tế: không âm, mỗi ledger có before/after liên tục, final đúng tổng | NFR-05/06; AC-DEB-04 | NOT_RUN |
| TC-EDGE-35 | CREDIT hoặc DEBIT cùngID/payload20attempts; replay trước/sau30ngày; PROCESSING qua retention | Đúng một effect/ledger. Replay30ngày giữ historical balance, không hứa current. Sau cửa sổ POST409/GET410 với tombstone; không purge PROCESSING, không thực hiện lại. | Q01A; AC-CRE-08; AC-DEB-10 | NOT_RUN |
| TC-EDGE-36 | D1 FAILED thiếu tiền, sau đó CREDIT rồi retry D1; tạo D2 mới | D1 vẫn FAILED không trừ; D2 được đánh giá mới; không làm FAILED đổi sang SUCCEEDED | FR-ACC-06; AC-CRE-10; AC-JOB-05 | NOT_RUN |
| TC-EDGE-37 | Đợi account/root/cycle lock qua expiresAt; eligibility ngay trước/đúng/sau expiry; MAINexpiryNULL; commit qua hạn | Sample giờDB mới SAU mọi lock ngay trước updates. checkedAt>=expiry từ chối; checkedAt<expiry hợp lệ được commit sauexpiry. Cycle cũng check tại cùng eligibility; MAINNULL không tự expire. | CR-TIME-002; AC-CRE-06; AC-DEB-11 | NOT_RUN |
| TC-EDGE-38 | CùngID đổi amount/account/subscriber/cycle/serviceReference, thứ tựJSON/JWT/correlation thay đổi | Business payload khác IDEMPOTENCY_CONFLICT giữ outcome/ledger cũ; metadata bị loại khỏi canonical không tạo effect mới. UC02 expectedVersion và UC03 target/version cũng tham gia canonical. | FR-ACC-06; AC-DEB-10 | NOT_RUN |
| TC-EDGE-39 | Timeout sau commit, mất kết nối khi COMMIT, crash giữa bước | GET/retry cùng ID xác định outcome; không thêm money effect; unknown còn thì unresolved | NFR-07; AC-CRE-09; AC-DEB-11 | NOT_RUN |
| TC-EDGE-40 | Deadlock victim và lock timeout riêng biệt; cạn retry budget | Rollback transaction ứng dụng, retry ngoài transaction với cùng ID; không insert ledger tiếp sau statement lỗi | NFR-05/07; AC-DEB-11 | NOT_RUN |

## 41–51. UC06/JOB-02: snapshot và ngày báo cáo

| Test ID | Dữ liệu/thao tác | Oracle cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-41 | S1 chỉA, S2 chỉB, S3 A+B, S4 C, S5 A ngoài kỳ; thêm nhiều ledger cùngsubscriber | OR(A,B)=S1/S2/S3; AND=S3; mỗi subscriber một lần, tổng/count không bị nhân do join; một ledger không nguyên vẹn thuộc hai gói. | AC-RPT-03/05 | NOT_RUN |
| TC-EDGE-42 | recordedAt tạiX−1/X/Y−1/Y; ledger trướcmidnight nhưng commit saunửađêm; CREDIT/FAILED/rollback | [X,Y) gồmX khôngY theo recordedAt DB trong transaction cuối cùng commit; midnight case thuộcngày ghi nhận, không physical commit. CREDIT/FAILED/rollback không là tiêu dùng. | CR-TIME-001; AC-RPT-01/02/04 | NOT_RUN |
| TC-EDGE-43 | Chỉ phí đăng ký A; includeFee false/true | False loại, true có trong OR(A); fee không đúp qua ledger/attribution join | AC-RPT-04/05 | NOT_RUN |
| TC-EDGE-44 | X>=Y, thiếuOR/AND/includeFee, packageIds rỗng/trùng/saiUUID, page default/limit>200 | Validation theo OpenAPI, không ngầm chọn OR/includeFee; duplicate packageIds bị reject theo uniqueItems. Trang mặc định50/tối đa200. | Q10A; AC-RPT-03/04/06 | NOT_RUN |
| TC-EDGE-45 | Cursor hỏng/đổi caller/filter/query/version; snapshot hết hạn/chưaREADY | Từ chối cursor/permission hoặc trạng thái tương ứng, không lộ dữ liệu. Snapshot expired yêu cầu query mới; đang materialize dùng queryId/queryRequestId gốc, không live fallback. | Q27A/31A; AC-RPT-08/09/11 | NOT_RUN |
| TC-EDGE-46 | Transaction recordedAt trướccutoff nhưng commit saupage1, làm subscriber mới đủAND; mấtfirstreply rồi retry queryRequestId | Trang2 vẫn đúng persisted members cũ, không thêm/bỏ; query mới mới thấy. Retry cùngkey dùng cùng query/cutoff/membership, khácfilter409. Cutoff timestamp đơn thuần không chứng minh tập bất biến. | Q27A; AC-RPT-07/10; Q12A | NOT_RUN |
| TC-EDGE-47 | Dừng bán gói/xử lý hết hạn account rồi tra lịch sử | Kết quả cũ còn, định nghĩa/snapshot nguồn không bị mất | BR-07/22; AC-RPT-12 | NOT_RUN |
| TC-EDGE-48 | Debit không có gói/sai owner/cycle hết hạn hoặc tương lai; attribution nhiều gói trên một khoản; pending cancel còn quyền | Không có effective cycle thì NO_EFFECTIVE_CYCLE/validation tương ứng và không trừ. Pending cancel có cycle bao phủ vẫn dùng được. Một consumption thuộc một cycle; không UNASSIGNED/split/nhân nguyên tiền vào A+B. | Q11A; AC-DEB-06/07/08/09 | NOT_RUN |
| TC-EDGE-49 | Ngày Việt Nam đổi UTC; hai JOB-02 runner; crash sau cutoff persist/trước source snapshot, sau membership commit/trước aggregate | Đúng ngày [X,Y) theo recordedAt; first durable cutoff không thay khi retry. Persisted source set giữ nguyên; summary chỉ từ contributions persisted. Một finalization, không đọc live hai tập khác nhau. | Q12A; AC-JOB-09/10/14 | NOT_RUN |
| TC-EDGE-50 | Ledger ngày cũ đã finalized mới commit/được phát hiện; baseline và adjustment runner đua, rerun/ngày nguồn chưa chốt | Mỗi source toàn cục chỉ BASE hoặc ADJUSTMENT đúng một lần. Finalized ngày gốc không sửa; nguồn muộn vào kỳ sau, giữ source/ngày gốc. Ngày nguồn chưa chốt phải xử lý BASE đúng thứ tự, không ADJUSTMENT sớm. | Q28A; AC-JOB-11/14 | NOT_RUN |
| TC-EDGE-51 | Summary DB đã chốt nhưng CSV lỗi; retry export | Không tính tổng lại; file/version/checksum đối chiếu DB; không file nửa chừng được công bố | AC-JOB-13 | NOT_RUN |

## 52–64. DB, recovery, vận hành và hiệu năng

| Test ID | Dữ liệu/thao tác | Oracle cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-52 | MySQL8.0.46/InnoDB sạch; migration đã review; duplicate doc/E.164/MAIN/request/cycle/fee/contribution, FK sai/balance âm | Migration/constraints thực thi; READ COMMITTED/write locks, utf8mb4/identifier equality/UTC DATETIME(6) đúng design. UUID physical là proposal Q14C phải review/test roundtrip PK/FK trước claim PASS; không chạy PG SQL. | Q13B/14C/15A/16A/19A; M4-DB | NOT_RUN |
| TC-EDGE-53 | JOB-01 crash sau batch/commit trước checkpoint; TxA cancellation PENDING; lịch/chạy tay song song | Checkpoint chỉ phản ánh effect đã commit; không mất/đúp fee/cancel/expiry; phục hồi PENDING theo receipt CR-CAN-003. Run states PENDING/RUNNING/SUCCEEDED/FAILED; item error counter không bị giấu. | CR-CAN-003; AC-JOB-08 | NOT_RUN |
| TC-EDGE-54 | Worker hết lease trong khi instance khác nhận việc, worker cũ quay lại | Worker cũ không ghi đè/thu phí lần hai; claim + terminal/cycle guard | NFR-05/07; AC-JOB-08 | NOT_RUN |
| TC-EDGE-55 | Ngừng máy nhiều ngày; worker/JOB-01/JOB-02 catch-up, nguồn cũ chưa finalized | Không bỏ backlog/ngày thiếu; renewal một kỳ từ valid processing, không truy thu. Report đóng ngày thiếu theo thứ tự; nguồn muộn của ngày đã chốt ADJUSTMENT ở kỳ sau đúng một lần; không sửa finalized. | Q25B/28A; AC-JOB-04/08/11 | NOT_RUN |
| TC-EDGE-56 | Account expired có balance>0; job chạy lại; số ASSIGNED gắn subscriber | Giữ tiền/history, không CREDIT/DEBIT mới trái hạn, không xóa catalog/tái cấp số | BR-07/22; AC-JOB-07 | NOT_RUN |
| TC-EDGE-57 | Seed tổng count=499.999/500.000 trước run | Run đầu không đủ dữ liệu; run sau đủ ngưỡng nếu phân bố hợp lệ; lưu count từng bảng nghiệp vụ | NFR-01; TC perf dataset | NOT_RUN |
| TC-EDGE-58 | Mixed600s có ít nhất18.000 UC02 và18.000 UC05 mới đã commit; replay/fee/warm-up/in-flight qua biên | N2/600>=30 và N5/600>=30 cùng window, tổng>=60. Không lấy recordedAt làm commit witness. Boundary uncertainty báo riêng; chỉ count chắc chắn/nguồn thời gian đủ chứng minh, không cộng drain ngoài window. | Q32A; NFR-02/03 | NOT_RUN |
| TC-EDGE-59 | Xen replay/PROCESSING/business failure/system error/mất phản hồi; lookup sau drain | Tách attempt/logical/outcome. Replay/fee không tăng TPS; timeout tính system error dù outcome commit. A=mọi sent UC02/05 attempt kể retry; GET status/health ngoài mẫu số. Unknown không tự gán FAILED. | Q32A; NFR-07/11 | NOT_RUN |
| TC-EDGE-60 | Retry làm số attempts đổi; hot10 cặp phân đều50% mọi debit attempt; kiểm backend wait trace | H/D=50%, mỗi cặp H/10; phí UC02/renewal ngoài mẫu số. Có bằng chứng chồng xử lý/đợi cùng account ở backend, không chỉ client overlap/nhãn hot; điều phối retry không đổi payload cùng ID. | Q32A; NFR-04 | NOT_RUN |
| TC-EDGE-61 | Ledger thiếu/thừa/sai amount hoặc ID, tổng tiền vẫn tình cờ cân bằng | Đối soát theo từng key phát hiện sai, không chỉ check tổng; truy vết cycle/fee/account/source | FR-TST-02; NFR-05/07 | NOT_RUN |
| TC-EDGE-62 | Percentile từ mọi attempt có response, kể retry/error; timeout/dropped/unsent; logical end-to-end | Riêng UC02/05:95%<=500ms và99%<=1s. Timeout báo riêng nhưng vào E; E/A<=0,1% trên mọi sent attempt. Health/lookup ngoài A, dropped/unsent không giả sent; báo logical duration có retry/lookup. | Q32A; NFR-11 | NOT_RUN |
| TC-EDGE-63 | Sau run còn 1 unknown outcome hoặc 1 ledger không ánh xạ request | Không kết luận correctness/reconciliation đạt; lưu danh sách unresolved và nguyên nhân | DEC-11; NFR-07 | NOT_RUN |
| TC-EDGE-64 | Backup/restore ở nhà với MySQL/RabbitMQ/Redis/Keycloak; missing secret/expired JWT/key rotation; office không DB | Restore đúng manifest request/tombstone/ledger/cycle/report/pending intent đã backup; fail closed auth, MQ recovery DB, cache fallback DB. Admin job scope/audit riêng; không hứa RPO/RTO/M8 khi chưa đo, không bypass JWT ở office. | Q18A/30A/31A; NFR-08/09/10; M8 | NOT_RUN |

## 3. Bằng chứng và quyết định kết luận

Mỗi case lưu version tài liệu/code/schema, fixture/config/clock, số dư đầu/cuối, expected/actual, request/caller/operation/attempt/logical key, cycle/ledger/contribution linkage, barrier/fault timing và raw evidence đã ẩn secret/PII. Với unknown commit phải lưu các lần lookup/recovery và outcome cuối; không xem timeout là rollback. Với64 lưu backup manifest/checksum/restore queries, không hứa trạng thái saubackup ngoàiRPO đã đo.

Correctness đòi map từng logical key tới đúng tác động, không chỉ tổng cuối tình cờ cân bằng. Một ledger thừa và một ledger thiếu bù nhau vẫn FAIL. Còn một unknown outcome, source ledger không map hoặc pending recovery không giải thích được thì không kết luận đối soát đạt. Business FAILED không tự đổi thành success sau nạp/đổi kho.

Performance chỉ kết luận từ đúng Q32: warm-up120s, mixed600s, >=18.000 success mới mỗi loại đã commit trong cửa sổ, responded-attempt latency gồm retry/error, timeout vào system error/mọi sent attempt, hot50% gồm retry và backend overlap. Phí nội bộ không vào UC05 TPS; drain ngoài window chỉ giúp đối soát, không tăng TPS. Xem protocol chi tiết để xử lý boundary commit chưa đủ bằng chứng.

Kết quả thực thi sau này có thể PASS/FAIL/BLOCKED; trạng thái hiện tại của mọi dòng vẫn NOT_RUN. Không tự đóng M4-DB/P5/P6/M7/M8 từ ma trận hay việc có bản vẽ/API schema.
