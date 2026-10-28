> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Protocol đo tải hỗn hợp và đối soát

**DOC-PERF-001 · v0.3 · 26/10/2026 · REVIEW · NOT_RUN.** Chưa có k6 script/run/raw log/số đo hoặc kết quả runtime để công bố đạt. Ngưỡng và công thức Q32=A đã được chọn; cấu hình triển khai/instrumentation cụ thể cần review trước chạy.

Nguồn: DEC-08..11, DEC-PRE-22..24, Q32=A, [SRS NFR-01..11](../01-requirements/SRS.md), [decision receipt](../00-project/decision-receipt-20261026.md), [test plan](test-plan.md), [64 ngoại lệ](../07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md). CR-TIME-001 đổi ngày báo cáo sang recordedAt, không đổi yêu cầu đếm nghiệp vụ mới đã commit trong cửa sổ tải.

## 1. Tiêu chí bắt buộc và công thức

| Tiêu chí | Oracle/ngưỡng đã chọn | Đơn vị và tập đo |
|---|---|---|
| Dữ liệu | Tổng các bảng nghiệp vụ >=500.000 trước đo; thống kê từng bảng | Không phải mỗi bảng500K; không cộng bảng migration/system để đủ số |
| Khởi động | Warm-up120 giây | Loại khỏi mọi chỉ tiêu cửa sổ chính; lưu riêng số liệu |
| Đo | Một cửa sổ chung [T0,T1), T1−T0=600 giây | UC02 và UC05 chạy đồng thời; không dùng hai bài riêng cộng lại |
| UC02 TPS | N2/600 >=30, tức N2>=18.000 | Distinct nghiệp vụ đăng ký mới đã commit trong cửa sổ, không replay |
| UC05 TPS | N5/600 >=30, tức N5>=18.000 | Distinct debit tiêu dùng độc lập mới đã commit trong cùng cửa sổ |
| Tổng | (N2+N5)/600 >=60 | Hai loại đều phải qua ngưỡng riêng; phí UC02/renewal không vào N5 |
| Latency UC02 | Ít nhất95% attempts có phản hồi <=500ms;99% <=1s | Riêng UC02, gồm retry và phản hồi lỗi của bài mixed |
| Latency UC05 | Cùng ngưỡng95%/99% | Riêng UC05; thêm combined để quan sát, không thay kết luận từng loại |
| Error rate | E/A <=0,001 | A=mọi lần gửi UC02/UC05 của bài đo, gồm retry; E=lỗi hệ thống và mất phản hồi |
| Tranh chấp | H/D=50%, hot chia đều10 cặp thuê bao–account | D=mọi debit attempt độc lập, gồm retry; phí nội bộ không phải debit attempt |
| Correctness | Không thiếu/thừa/sai tác động; unresolved=0 | Đối soát mỗi logical key/ledger/cycle/balance, không chỉ tổng tiền |

Chỉ count HTTP2xx hoặc request/s không chứng minh TPS nghiệp vụ mới. UC02 có thể trả202 PROCESSING, replay200 hay conflict409; kết luận phải theo outcome/ledger/cycle đã commit. Một debit trong fee đăng ký không là UC05 độc lập; không tính lặp phí vào tổng60TPS.

## 2. Cửa sổ và bằng chứng commit — tách khỏi recordedAt

Chọn clock đo monotonic cho generator; lưu mapping giờUTC/offset/độ lệch tới backend và DB để đối chiếu. T0/T1 công bố trước run; log timestamp đủ độ phân giải. Không tự đổi cửa sổ khi thiếu TPS hoặc thu thêm thành công ở drain sauT1.

Mỗi nghiệp vụ có caller/operation/requestId mới, không trùng warm-up hoặc run khác. Theo dõi mọi attempt và logical outcome. `recordedAt` trong transaction có thể trước physical commit, nên **không dùng recordedAt trong [T0,T1) làm bằng chứng commit trong cửa sổ**. Thời gian HTTP request đến cũng không đủ; lookup sau run chỉ chứng minh outcome đã tồn tại khi lookup, không tự chứng minh lúc commit.

Instrumentation đề xuất cần review: ghi giới hạn thời gian trước COMMIT và xác nhận sau COMMIT/outcome từ cùng đường nghiệp vụ, kèm run/logical key; chỉ timestamp có đồng bộ clock/độ bất định được kiểm mới quy vào cửa sổ. Không thêm binlog CDC chỉ để đổi nghĩa ngày báo cáo.

Để tránh công bố sai ở hai biên, lưu ba tập: commit chắc chắn trong cửa sổ, chắc chắn ngoài cửa sổ, và outcome thành công nhưng thời điểm qua biên chưa đủ bằng chứng. Ví dụ operation hoàn toàn mới được tiếp nhận sauT0 và backend xác nhận commit trướcT1 có thể đếm chắc chắn; một operation ghi recordedAt trướcT1 nhưng chỉ xác nhận sauT1 chưa đủ bằng chứng cho N. Không đếm warm-up operation commit muộn hoặc replay measurement của warm-up.

Nếu chưa có nguồn xác định chính xác thời điểm commit, công bố khoảng đếm [N_chắc_chắn, N_chắc_chắn+N_biên_chưa_rõ] và dùng cận dưới để chứng nhận ngưỡng; không gọi cận trên là số thành công chính xác. Nếu chỉ cận trên vượt18.000 thì chưa chứng minh đạt. Đây là cách báo cáo bảo thủ, không thay yêu cầu Q32 bằng thời điểm reply. Mọi outcome có thể được đối soát sau drain; sự chưa rõ thời điểm biên phải báo riêng, không giấu vào business failure.

## 3. Mẫu số latency, lỗi và phép đếm attempt

Một attempt là một lần gửi HTTP UC02 hoặc UC05 thực sự ra backend; retry là attempt mới cùng logical key. Log send/start/end/response hoặc deadline timeout. Latency có phản hồi = thời gian monotonic từ gửi tới nhận phản hồi đầy đủ; gồm success/replay/PROCESSING/business error/system error có phản hồi. Tính từng loại riêng, lưu histogram/raw samples và phương pháp percentile rõ; có thể công bố thêm tỷ lệ trực tiếp count(latency<=threshold)/count(respondedAttempts) để đối chiếu p95/p99.

Mất phản hồi không có sample latency hữu hạn giả tạo; ghi timeout riêng và tính vào E/A. Đồng thời đo logical end-to-end từ attempt đầu tới outcome cuối xác định, gồm retry/lookup; censored/unresolved ghi riêng. Không loại các logical chậm khỏi báo cáo vì chúng chỉ hoàn tất sau run.

A bao gồm tất cả attempts đăng ký/debit được gửi trong cửa sổ, kể cả retry, timeout và business failure. E gồm response lỗi hệ thống theo taxonomy đã công bố và mất phản hồi; timeout là lỗi trong protocol dù DB về sau đã commit thành công. Business rejection như thiếu tiền/stale version được thống kê riêng, vẫn thuộc A nhưng không tự là E. Lỗi cấu hình/auth/schema do harness phải báo là lỗi bài đo, không dùng để làm mẫu số lớn cho error rate đẹp. Không đổi taxonomy sau nhìn số liệu.

GET operation status, report query, health và background worker không vào A hoặc latency attempt UC02/05; lưu tải hỗ trợ riêng. Dropped/unsent iterations không phải đã gửi backend, không vào A giả tạo; phải công bố số lượng, nguyên nhân và offered/completed load để thấy generator có phát đủ hay không. Attempts gửi trướcT0 có phản hồi trong cửa sổ tách warm-up/in-flight; không chuyển vào sample measurement chỉ vì response tới muộn.

## 4. Tỷ lệ50% và bằng chứng10 cặp thực sự tranh chấp

D = số attempts UC05 độc lập được gửi trong cửa sổ, gồm retry. H = số attempts đó nhắm10 cặp hot được khai báo trước run. Cần H/D=0,5; mỗi cặp hot nhận H/10. Lưu số nguyên từng nhóm; thiết kế số attempt đủ chia để không lờ sai lệch bằng cách làm tròn50%. 50% còn lại phân rộng sang nhiều account khác. Không tính PACKAGE_FEE/AUTO_RENEWAL_FEE vào D.

Retry cũng phải qua bộ điều phối phân nhóm; chỉ gán50% request ban đầu rồi để retry tự làm lệch tỷ lệ không đạt protocol. Nếu không đạt chính xác tỷ lệ/phân đều, báo sai protocol và điều chỉnh kế hoạch ở run sau, không sửa log hoặc chuyển account của cùng requestId làm conflict.

Bằng chứng tranh chấp: ít nhất có nhiều request xử lý cùng account chồng lấn ở backend, có thời gian chờ/đòi quyền xử lý trên cùng account. Ghi receive/handler start/lock requested/acquired/release hoặc trace DB tương đương, caller/logical/attempt/account/hotGroup. Hai holder không cần cùng giữ lock (đúng thiết kế sẽ serialize); request đợi holder khác là bằng chứng có tranh chấp. Chỉ client overlap hoặc gắn nhãn hot không đủ. Nếu tốc độ thấp làm các hot requests tuần tự không chờ/chồng xử lý, chưa chứng minh điều kiện này.

## 5. Dữ liệu, tải và vận hành trong run

Seed tối thiểu500K tổng với phân bố/đếm từng bảng, không chỉ dữ liệu rỗng hoặc orphan. Có đủ Subscriber/account/gói còn hiệu lực để UC05 không hết cycle giữa đo; đủ MAIN cho18.000 lần đăng ký, đủ tiền và số lượng root/version intents cho UC02 mới. Chính sách Q23 yêu cầu expectedVersion hiện tại: workload phải đọc/điều phối phiên bản hoặc phân phối root hợp lệ, không đếm stale conflicts là30TPS.

Dữ liệu hot accounts phải đủ debit thành công và tránh chạm cap khi refill; mọi CREDIT/seed thay đổi tiền trong run phải ghi ledger/log và vào đối soát. Không sửa SQL số dư âm thầm giữa run. Không disable fee/expiry/canonical/constraints/authorization để đạt TPS. Gia hạn/hủy/worker có cấu hình cố định trước đo; nếu chạy background, mọi fee/cycle mutation phải được tính vào reconciliation nhưng không vào UC05 independent TPS.

Chạy correctness nhỏ trước: cùng ID, khác ID/sameversion, thiếu tiền, account expiry qua lock, CR-CAN-003 TxA/TxB và mất phản hồi, delayed renewal, recordedAt/membership/adjustment, deadlock/lock timeout. Sau đó mixed load ổn định; riêng fault campaigns cho timeout/restart/MQ/cache/IdP với config và thời điểm injection ghi trước. Không lấy kết quả stress hoặc fault run khác cấu hình làm pass cho run chính.

Ghi MySQL8.0.46 InnoDB/READ COMMITTED, schema/source hash, JVM21/GC/pool, CPU/RAM/disk/network, DB settings/durability, số instance, worker interval/batch/retry/backoff, Redis warm/cold/hit/fallback, MQ consumers/recovery, Keycloak token refresh. Home chạy đủ dịch vụ; office khôngDB không là runtime measurement. Không tuyên bố optimal trước EXPLAIN/profile/run evidence.

## 6. Trình tự chạy tái lập

1. Freeze version/config/seed; khởi tạo bằng migration đã kiểm, chụp count từng bảng và số dư đầu; kiểm service health/JWT, quyền thao tác, fixtures và không tồn stale operations.
2. Correctness smoke và load-generator calibration; log counter cross-check. Tách runId/warm-up namespace, bảo đảm warm-up không làm cạn fixture hoặc trộn logical keys.
3. Warm-up120s; ghi metrics riêng. Đảm bảo in-flight warm-up được phân loại/drain trước measurement theo protocol đã công bố, không để nhận nhầm success warm-up.
4. Đo600s [T0,T1) đồng thời UC02/UC05, giữ admission/retry/hot-group accounting; không thay cấu hình trong cửa sổ mà không đánh dấu run không so sánh được.
5. TớiT1 dừng admission measurement; drain/lookup có budget để xác định mọi outcome, lưu late completions. Không cộng commit ngoài cửa sổ vào N2/N5.
6. Chụp số dư/cycle/ledger/outcomes sau drain; đối soát từng logical/attempt và background mutation. Unknown chưa hết thì báo unresolved, không kết luận correctness đạt.
7. Tính công thức từ raw logs bằng công cụ độc lập; xuất bảng từng tiêu chí PASS/FAIL/NOT_PROVEN, danh sách sai lệch và môi trường. Không thay thế bằng ảnh dashboard.

## 7. Đối soát và hồ sơ phải nộp

Per-account: dư cuối = dư đầu + tổng credit đã commit − tổng debit đã commit, gồm mọi fee đúng một lần. Per-operation: SUCCEEDED có đúng effect/cycle/ledger, FAILED không effect thành công trái phép, replay không thêm effect, canonical conflict giữ bản gốc, unresolved=0. Ledger before/after liên tục theo thứ tự account có thẩm quyền; không coi UUID hay auto increment là commit order.

Per-package: cycle [start,end) đúng ngày lịch, immutable price/duration, version/coverageEnd hợp lý; manual/auto một fee cho target; cancellation accepted không còn nối, bảo toàn paid coverage. Report không dùng recordedAt làm actual commit witness; báo cáo kinh doanh vẫn theo recordedAt CR-TIME-001 và finalized/adjustment không đúp nguồn.

Artifact: manifest version/config/seed, count-before, account-opening/closing, raw attempts và backend/DB traces đã ẩn secret/PII, logical outcomes/ledger/cycles, histograms, timeout/system/business errors, D/H/hot10distribution và overlap evidence, dropped/unsent, window commit membership/boundary uncertainty, reconciliation diff/unresolved, run summary. Mẫu tên file/thư mục do implementation chọn, phải có hash và lệnh tái lập.

| Tiêu chí | Trạng thái hiện tại | Bằng chứng còn thiếu |
|---|---|---|
| >=500K dữ liệu hợp lệ | NOT_RUN | Seed + count DB thật |
| 30+30TPS cùng600s | NOT_RUN | Raw log + commit-window classification |
| Latency95%/99% | NOT_RUN | Responded attempts/raw histogram từng loại |
| Error<=0,1% | NOT_RUN | Mọi sent attempt/timeout/taxonomy |
| Hot50%/10 cặp/overlap | NOT_RUN | Actual distribution + backend wait traces |
| Correctness/unresolved0 | NOT_RUN | Per-key/ledger/balance/cycle reconciliation |

Chỉ xét M7 khi toàn bộ điều kiện được chứng minh trên cùng protocol/cấu hình đủ truy vết. Tài liệu này không công bố số đo, không viết code phát tải/đối soát và không tự hoàn tất P6.
