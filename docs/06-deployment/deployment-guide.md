# Hướng dẫn triển khai và bàn giao backend

**Mã tài liệu:** DOC-DEP-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW — runbook mục tiêu, chưa thực thi.

## 1. Trạng thái và phạm vi

Hướng dẫn này xác định trình tự và bằng chứng phải bổ sung khi backend được triển khai. Hiện trạng trước đợt đồng bộ tài liệu là có `pom.xml` và `Main` in thông báo; chưa có backend nghiệp vụ, bộ migration MySQL, Compose hoặc test suite đủ để bàn giao. Tài liệu không được dùng để tuyên bố P5/P7 hay M8 đã hoàn tất.

M3/M4 UC01 đã được duyệt trong phạm vi lịch sử. Delta MySQL và các artifact chi tiết mới còn REVIEW. Trước DB implementation cần duyệt delta UC01; triển khai và nghiệm thu theo từng Use Case, không bắt buộc hoàn tất code mọi UC mới được kiểm thử UC01. Không sử dụng starter PostgreSQL hoặc ZIP không tồn tại làm hướng dẫn chạy hiện hành.

## 2. Bộ sản phẩm cần bàn giao

| Nhóm | Sản phẩm phải có | Cách xác nhận |
|---|---|---|
| Mã nguồn và bản chạy | Source revision, cấu hình build, JAR/image backend | Build lại được, ghi version/hash và lệnh thực tế |
| Hạ tầng local | Compose và cấu hình MySQL 8.0.46, RabbitMQ, Redis, Keycloak | Parse được; readiness và kết nối host/container đã thử |
| Dữ liệu | Migration MySQL, seed giả lập, hướng dẫn reset an toàn | Migration từ DB trống đạt; số lượng/ràng buộc và nguồn seed có log |
| Xác thực | Realm/client demo, scope, ánh xạ caller, hướng dẫn cấp token | API có JWT hoạt động; trường hợp sai token/quyền bị từ chối |
| Hợp đồng | OpenAPI, schema, UML và cấu hình mẫu không chứa secret | Khớp hành vi source/runtime của revision bàn giao |
| Kiểm thử | Unit/integration/contract test, k6, công cụ đối soát | Có log kết quả thật, dữ liệu và cách tái lập |
| Vận hành | Lịch job, retry, backup/restore, log/metrics và giới hạn đã biết | Người khác thực hiện được trên máy sạch hoặc target thử độc lập |

Java 21 là mục tiêu của dự án; cần ghi phiên bản Java/Maven/container runtime thực tế khi bàn giao. Image phải có patch/digest cụ thể. Không dùng nhãn `latest` hoặc việc máy nhà có MySQL đúng phiên bản để thay cho bằng chứng ứng dụng tương thích.

## 3. Trình tự triển khai từ máy sạch

Các bước dưới đây là quy trình phải thực hiện sau khi có artifact tương ứng. Chưa ghi lệnh startup/seed giả định như thể đã chạy thành công.

| Bước | Việc cần làm | Bằng chứng hoàn tất |
|---|---|---|
| 1 | Lấy đúng revision; kiểm JDK, Maven, Docker và các image | Output phiên bản, source revision và danh sách artifact |
| 2 | Chuẩn bị secret ngoài Git; xác định DB đích, issuer/JWKS/audience và caller/scope | Mapping cấu hình được kiểm; không log credential |
| 3 | Khởi động MySQL, RabbitMQ, Redis và Keycloak với persistence/health check | Service ready; URL host và container đúng |
| 4 | Chạy migration MySQL trên DB dev trống, sau đó seed dữ liệu giả | Lịch sử Flyway, constraint/FK/unique/index và số lượng seed được kiểm |
| 5 | Build/test ứng dụng, khởi động backend | Kết quả `mvn verify` thực, JAR/image, readiness và log startup |
| 6 | Chạy smoke UC01 | Tra số, chọn/tự cấp, retry, tranh chấp, WAITING có đồng ý và tra đúng caller |
| 7 | Chạy smoke gói/tài khoản/báo cáo theo UC đã triển khai | Balance, ledger, cycle và request outcome khớp hợp đồng |
| 8 | Restart và thử lỗi có kiểm soát | Dữ liệu bền vững; mất broker/cache không làm sai nghiệp vụ |
| 9 | Chạy job/worker theo lịch và bằng tay | Không trùng phí/kỳ; cutoff và đóng góp báo cáo không đổi sai qua retry |
| 10 | Chạy tải và đối soát khi đủ điều kiện P6 | Đạt quy mô/công thức đã duyệt hoặc báo rõ phần chưa đạt |
| 11 | Backup và restore tới DB thử tách biệt | Đối soát lại số dư, lịch sử, request, cycle và báo cáo; ghi cách phục hồi |

Chạy migration hoặc seed chỉ trên DB đích đã xác định. Không xóa volume, dùng Flyway clean hay restore đè dữ liệu để sửa lỗi nếu chưa có phạm vi/ủy quyền phù hợp. Hướng dẫn cuối cần ghi lệnh chính xác từ artifact đã chạy và lưu output đã che secret.

## 4. Dữ liệu smoke và kết quả kỳ vọng

Dữ liệu giả cần có một Customer nhiều Subscriber, giấy tờ xác minh giả với nguồn tham chiếu được phép, số E.164 trạng thái AVAILABLE/ASSIGNED, MAIN số dư 0 sau đấu nối và tài khoản phụ có loại/quyền/hạn rõ ràng. Gói mẫu cần kiểm ngày lịch, kỳ trả trước, ngừng bán mới và chính sách gia hạn khách cũ. Số dư khởi tạo phải có nguồn seed/đối soát; không đưa dữ liệu cá nhân thật vào môi trường demo.

| Nhóm ca | Điều cần quan sát |
|---|---|
| UC01 | Một số không cấp cho hai thuê bao; tra cứu không giữ chỗ; AUTO hết số chỉ WAITING khi đồng ý, hết 24 giờ thành EXPIRED |
| UC02 | MAIN trả phí; số dư, giao dịch và kỳ gói commit cùng nhau; version cũ không bị thu tiền |
| UC03 | Nhận hủy không mất quyền ngay; giữ tới cuối mọi kỳ đã trả, không hoàn phí, chặn mua thêm/gia hạn |
| UC04/UC05 | Đúng accountId, không fallback; thiếu tiền/hết hạn thì không có giao dịch tài chính thành công |
| Thời gian | Kiểm hạn bằng DB clock dưới khóa; có thể commit sau hạn khi kiểm trước hạn; `recordedAt` không bị đổi thành giờ commit |
| Báo cáo | Cursor giữ cùng tập kết quả, ngày đã chốt bất biến, nguồn muộn chỉ điều chỉnh một lần |
| Retry | Cùng mã/cùng nội dung không làm lại; khác nội dung xung đột; timeout tra cùng mã, không tự tạo mã mới |

Các scope chính xác lấy từ [OpenAPI](../04-design/openapi.yaml) và [ma trận bảo mật](../03-architecture/security-and-integration.md). Caller chỉ được tra request/bản báo cáo thuộc quyền mình; quyền thao tác trên tập dữ liệu giả chung không cho phép đọc request của ứng dụng khác.

## 5. Job, worker và xử lý sự cố

JOB-01 chạy 00:05, JOB-02 chạy 00:30 hằng ngày theo `Asia/Ho_Chi_Minh`. Worker gia hạn kiểm tra thường xuyên nhưng khoảng chạy còn là cấu hình REVIEW. Khôi phục UC01 mỗi 60 giây là tác vụ riêng; không dùng con số này làm SLA hoặc nhịp gia hạn đã duyệt.

Chạy tay dùng quyền `telecom:job:run`; tra trạng thái dùng `telecom:job:read`. Operator ghi jobCode, runId, ngày nghiệp vụ và lý do chạy lại theo cơ chế audit. Chạy tay và scheduler cùng sử dụng khóa nghiệp vụ/cơ chế nhận việc, không thu lại kỳ worker đã thành công.

| Sự cố | Hướng xử lý và kiểm chứng |
|---|---|
| Mất phản hồi/không rõ commit | Tra hoặc retry cùng requestId; kiểm outcome và ledger trước khi kết luận thất bại |
| MySQL lỗi | Dừng đường thành công; khôi phục kết nối rồi đối chiếu dữ liệu đã commit, không sửa tay số dư |
| RabbitMQ lỗi hoặc mất tín hiệu | Quét DB tìm WAITING; bổ sung kho đã commit vẫn được phát hiện |
| Redis lỗi/cache cũ | Đọc DB; kiểm thu phí không dùng giá cache cũ |
| IdP/JWKS lỗi | Không bỏ kiểm token; chỉ phục vụ khi xác minh đầy đủ bằng khóa hợp lệ |
| Job chết giữa chừng | Tiếp tục từ checkpoint/tập nguồn đã lưu; không lấy cutoff mới hoặc sinh phí mới cho cùng kỳ |
| Xuất CSV lỗi | Xuất lại bản đã chốt; không tổng hợp/cộng đóng góp nguồn lần nữa chỉ vì file lỗi |
| Backend restart | Khôi phục request/chờ, ý định hủy, cycle và job; đối soát không có hiệu ứng lặp |

CR-CAN-003 đã chốt receipt sau khi biết TxA lưu intent PENDING đã commit. TxB lấy giờ DB mới dưới khóa: trước observedCoverageEnd và version khớp thì accepted; bằng/sau hạn thì FAILED CANCELLATION_RECEIVED_TOO_LATE và giải phóng chặn tạm. Intent PENDING chặn cả worker và mua kỳ thủ công tới khi phân giải. Runbook cần kiểm crash giữa hai transaction, commit không rõ kết quả phải lookup, receipt tới muộn và accepted mất phản hồi được replay nguyên trạng sau hạn. Đây là quy tắc đã duyệt; cơ chế triển khai và bằng chứng test còn REVIEW.

## 6. Logging, metrics và retention

Log có cấu trúc cần liên kết correlationId, requestId, caller, operation, transaction/cycle, jobCode/runId, kết quả và thời gian. Theo dõi thời gian chờ khóa, deadlock/retry, connection pool, độ trễ API, tuổi request PROCESSING, WAITING gần hết hạn, độ trễ gia hạn, backlog job, cache hit và thông điệp giao lại. Không ghi token, mật khẩu, khóa riêng, toàn bộ giấy tờ hoặc SQL có secret.

Giữ đầy đủ phản hồi UC01/UC03 7 ngày và UC02/UC04/UC05 30 ngày sau trạng thái cuối; sau đó giữ tombstone. Không dọn request đang xử lý/chờ. Lịch sử nghiệp vụ có chính sách 5 năm cấu hình được, demo không chạy purge. Thời gian giữ snapshot báo cáo, file/log và cơ chế xoay khóa phải được chốt riêng trước bàn giao; không tự lấy thời gian replay làm retention cho mọi loại dữ liệu.

## 7. Bằng chứng tải và đối soát

Chỉ tiến hành khi tổng bản ghi nghiệp vụ trước đo đạt ít nhất 500.000. Tải hỗn hợp phải đạt đồng thời ít nhất 30 nghiệp vụ đăng ký gói thành công mới/giây và 30 lệnh trừ tiền độc lập thành công mới/giây. Không tính replay thành nghiệp vụ mới hoặc đếm phí gói như một lệnh trừ tiền độc lập.

Theo Q32, warm-up 2 phút, đo 10 phút, p95 ≤ 500 ms, p99 ≤ 1 giây, tỷ lệ lỗi ≤ 0,1%. Có 10 cặp thuê bao–tài khoản nóng, nhận 50% số lần gọi trừ tiền kể cả retry để tạo tranh chấp thực. Đối soát từng request, giao dịch và số dư; số request chưa xác định kết quả cuối phải bằng 0. Công thức mẫu số/latency chi tiết lấy từ [kế hoạch kiểm thử](../05-testing/test-plan.md), không suy từ số 2xx của công cụ tải.

Báo cáo cần đính kèm cấu hình máy/service/pool, seed count, log lần gọi, logical outcomes, truy vấn đối soát và giới hạn đã biết. Không kết luận đạt TPS chỉ từ cấu hình hoặc ước lượng.

## 8. Điều kiện nghiệm thu M8

Người khác phải triển khai được từ hướng dẫn và revision bàn giao, tái hiện smoke/test, xử lý các sự cố đã mô tả và restore thành công trên target thử. Kết quả thực tế và hạn chế phải được ghi lại để chủ dự án nghiệm thu. Tệp Markdown, sơ đồ hoặc checklist chưa chạy không đủ để đánh dấu M8.

Đầu vào liên quan: [runtime](../03-architecture/runtime-profiles.md), [topology](../03-architecture/docker-local-topology.md), [OpenAPI](../04-design/openapi.yaml), [thiết kế đồng thời/job](../04-design/concurrency-and-jobs.md) và [kế hoạch kiểm thử](../05-testing/test-plan.md).
