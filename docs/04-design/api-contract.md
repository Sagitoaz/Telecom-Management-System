# Hợp đồng API toàn backend — REVIEW 26/10/2026

Nguồn máy đọc: [OpenAPI3.0.3](openapi.yaml). MySQL8.0.46 hiện hành; lựa chọn nghiệp vụ đã ghi DEC/CR, HTTP DTO/status/scope chi tiết là đề xuất kỹ thuật cần duyệt. Chưa endpoint/runtime test. UC01 giữ ba path/field/hành vi lịch sử; Q10 nâng phân trang50/200. [UC01 riêng](api-contract-uc01.md), [transaction](concurrency-and-jobs.md).

## Ma trận operation / scope / kết quả

| UC | Method và path | Scope bắt buộc | Kết quả |
|---|---|---|---|
| UC01 | POST /api/v1/activations | telecom:activation:write + telecom:evidence:submit |201 mới thành công;200 replay;202 PROCESSING/WAITING |
| UC01 | GET /api/v1/activations/{requestId} | telecom:activation:read |200 terminal,202 pending,410 tombstone |
| support | GET /api/v1/phone-numbers | telecom:inventory:read | AVAILABLE không giữ số |
| UC02 | POST /api/v1/package-subscriptions | telecom:subscription:write |201 mới,200 replay; phí MAIN và kỳ nguyên tử |
| UC03 | POST /api/v1/package-subscriptions/{subscriptionId}/cancellations | telecom:subscription:cancel |200 accepted sau durable coordination;202 đang xử lý; không tiền hoàn |
| UC04 | POST /api/v1/accounts/{accountId}/credits | telecom:account:credit |201 ledger mới;200 replay; account chính xác |
| UC05 | POST /api/v1/accounts/{accountId}/debits | telecom:account:debit |201 consumption mới;200 replay; cycle bắt buộc |
| lookup | GET /api/v1/operations/{operation}/{requestId} | telecom:operation:read + scope operation gốc |200 terminal,202 processing,410 tombstone |
| support | GET /api/v1/packages | telecom:package:read | giá/duration/version, saleStatus và grandfather policy |
| support | GET /api/v1/subscribers/{subscriberId}/package-subscriptions | telecom:subscription:read | version/coverageEnd/currentCycle; không root là expectedVersion0 |
| UC06 | GET /api/v1/reports/consumption/subscribers | telecom:report:read |200 snapshot members,202 BUILDING+queryId,410 hết snapshot |
| internal | POST /api/v1/internal/jobs/{jobCode}/runs | telecom:job:run |202 pending/running,200 existing/terminal; jobCode JOB-01/JOB-02 |
| internal | GET /api/v1/internal/jobs/{jobCode}/runs/{runId} | telecom:job:read |200 trạng thái run,404 sai job/run |

Security scheme HTTP bearer không mã hóa OAuth scope trong YAML; `x-required-scopes` + bảng này là contract enforce. JWT từ IdP ngoài, allowlist alg/JWKS/issuer/audience/exp/nbf, caller client_id/azp map cấu hình đáng tin. Dataset hư cấu dùng chung có per-operation scopes, không tự xây tenant/grant subsystem. Request/result/report query chỉ caller tạo được đọc; caller khác404 tránh lộ existence. Package/subscriber support reads được per-operation scope truy cập dataset chung. Evidence chỉ scope tin cậy mới gửi reference, không coi chuỗi tự nhập là chứng cứ xác minh.

## Input và quan hệ trường

- requestId1..128 binary/case-sensitive; scope(caller,operation,id). UUID canonical string, integer VND1..10.000.000 (cap cấu hình); balance tối đa100.000.000. Unknown property/null/rỗng không hợp lệ. Correlation quan sát không canonical.
- REGISTER_PACKAGE gồm subscriberId/packageId/expectedSubscriptionVersion>=0; không gửi price/account/autoRenew; auto-renew theo baseline đủ tiền/không hủy, không thêm public opt-in policy. Hệ thống MAIN+catalog snapshot. Dùng GET support để biết version; read có thể stale nên vẫn409, không tự retry với version mới và thu thêm.
- CANCEL_PACKAGE gồm requestId và expectedVersion, path subscription. Hủy tới hết all paid coverage, accepted chặn renew; CR-CAN-003: receipt giờDB sau backend xác nhận intentTxA commit, chỉ receipt<coverageEnd và version khớp được accepted. receipt>=end chưa có accepted outcome thì422 CANCELLATION_RECEIVED_TOO_LATE, không đổi sang kỳ mới/hoàn tiền. Mất reply sau accepted commit thì cùngID replay accepted cũ; unknown commit lookup trước, chưa biết trả202/503. Xem concurrency.
- CREDIT gồm subscriberId,amountVnd; DEBIT thêm subscriptionId/cycleId/serviceReference. Account path phải thuộc subscriber và type cho phép; consumption phải cycle thuộc gói/subscriber hiệu lực tại DB check dưới account lock. Không dùng fee nội bộ gọi API DEBIT.
- CR-TIME-002 equality expiresAt reject, earlier checkedAt có thể commit sau expiry. recordedAt DB event time trong committed transaction, không client backdate và không physical commit time.
- Query report X/Y timestamps có offset, X<Y, [X,Y) theo recordedAt; packageIds không rỗng/không trùng, matchMode OR/AND và includeFee bắt buộc. OR có giao dịch bất kỳ gói chọn, AND mỗi gói chọn đều có giao dịch ở cùng subscriber. includeFee=false chỉ SERVICE_CONSUMPTION, true thêm PACKAGE_FEE/AUTO_RENEWAL_FEE; CREDIT luôn loại. First call queryRequestId giữ cutoff/query qua retry kể cả mất response, khác filter409; queryId retry BUILDING. Cursor default50/max200 HMAC bind caller/filter/query/version/last subscriberId; expired410. Một subscriber đúng một dòng, amount/count/package breakdown từ fixed persisted source set. Không giữ DB transaction qua pages; query không sửa ngày finalized.

- Job trigger runKey1..128, JOB-02 businessDate bắt buộc; JOB-01 optional date mặc định ngày server. runKey cùng job/date replay, đổi date409. Trigger không vượt quy tắc tiền/quyền/core, không nhận SQL hay tùy chọn bypass. Nội bộ không đồng nghĩa bỏ JWT/scope.

## Ma trận lỗi

| HTTP | Code | Persist/retry |
|---|---|---|
|400|VALIDATION_ERROR, INVALID_CURSOR, INVALID_DATE_RANGE|trước claim, sửa nội dung dùng ID mới nếu đã có claim |
|401|UNAUTHORIZED|JWT thiếu/sai/hết hạn |
|403|FORBIDDEN, UNTRUSTED_EVIDENCE|scope/type permission thiếu, không tác động |
|404|NOT_FOUND|resource không tồn tại hoặc lookup caller khác |
|409|IDEMPOTENCY_CONFLICT, REQUEST_ID_RETIRED|không chạy lại; tombstone giữ khóa |
|409|NUMBER_NOT_AVAILABLE, NO_AVAILABLE_NUMBER, STALE_SUBSCRIPTION_VERSION, CYCLE_ALREADY_CREATED, CANCELLATION_PENDING, PACKAGE_NOT_FOR_SALE|business terminal nếu đã claim; không thay tiền/quyền |
|422|INSUFFICIENT_BALANCE, BALANCE_LIMIT_EXCEEDED, ACCOUNT_EXPIRED, ACCOUNT_NOT_ELIGIBLE, NO_EFFECTIVE_CYCLE, CANCELLATION_RECEIVED_TOO_LATE, CUSTOMER_PROFILE_MISMATCH, VERIFICATION_REQUIRED|business FAILED ổn định khi claim hợp lệ; no partial money |
|410|REPLAY_EXPIRED, SNAPSHOT_EXPIRED|lookup tombstone hoặc hết snapshot; không cho key reuse |
|429|RATE_LIMITED|backoff, cùng ID |
|503|TEMPORARILY_UNAVAILABLE, ALLOCATION_BACKLOG, OUTCOME_UNKNOWN|tra cứu trước retry cùng ID; không suy rollback |

POST business terminal FAILED trả lỗi HTTP tương ứng kèm ErrorResponse(code/status/requestId); GET lookup terminal200 OperationResult(statusFAILED,code) để nhìn outcome bền vững. Original success replay200, đang claim202, không gán HTTP201 cho trạng thái WAITING. Hạ tầng timeout không là FAILED nghiệp vụ. UC01 EXPIRED lookup200 và code WAITING_EXPIRED, replay không tạo request mới. Có `X-Correlation-Id` trong lỗi; không log token/giấy tờ đầy đủ.

## Truy vết và giới hạn nghiệm thu

UC01 AC-SUB-01..27; UC02 fee/cycle/version Q04/Q05/Q21/Q23/Q29; UC03 Q06/Q22/Q24; UC04/05 Q01/Q02/Q09/Q11/Q34; UC06/jobs Q07/Q12/Q26/Q27/Q28/Q30; security Q31, pagination Q10. ID FR/BR/AC chi tiết theo SRS/use-case hiện hành; không tự tạo mã FR mới từ thiết kế. [Checklist](m4-review-checklist.md) giữ chưa duyệt. Trước implementation cần OpenAPI/schema/link validation và MySQL transaction tests, đặc biệt CR-CAN-003 receipt/lostACK; tài liệu không chứng minh TPS/latency.

Truy vết cụ thể: FR-SUB-01..15/AC-SUB-01..27; FR-PKG-01..07/AC-PKG-01..12/AC-CAN-01..10; FR-ACC-01..07/AC-CRE-01..10/AC-DEB-01..12; FR-RPT-01..04/AC-RPT-01..12; FR-JOB-01..04/AC-JOB-01..14; NFR-01..11.

CR-CAN-003 đã duyệt26/10: PENDING intent tạm chặn renewal và manual extension; recovery lấy receipt mới sau khi biết TxA commit. Late receipt terminal FAILED và release guard để Q25B tiếp tục, không treo PROCESSING khi DB đã xác định. Accepted/effective timestamps khác nhau; TxB được commit sau coverageEnd nếu receipt trướcend. Thiết kế/API artifacts còn REVIEW, implementation tests chưa chạy.
