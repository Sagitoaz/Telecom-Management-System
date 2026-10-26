# Báo cáo nghiệp vụ và thiết kế backend tổng thể

**Cập nhật 26/10/2026 — REVIEW.** Đây là báo cáo mô tả hệ thống và phương án thiết kế theo quyết định đã tiếp nhận, chưa phải báo cáo kết quả triển khai. Tên file DRAFT được giữ để không làm hỏng liên kết cũ. [Phiếu 16](16-PHIEU-DUYET-TONG-HOP.md) giữ nguyên; hiệu lực mới trong [bản tiếp nhận](../00-project/decision-receipt-20261026.md) và các CR.

## 1. Hệ thống giải quyết việc gì?

Ứng dụng CSKH gọi backend để tạo thuê bao cho khách hàng, đăng ký/hủy gói, cộng tiền và ghi nhận tiêu dùng. Backend quản lý số, quyền dùng gói, số dư và lịch sử, đồng thời tổng hợp tiêu dùng để tra cứu/đối soát. Đấu nối ở đây là tạo hồ sơ nghiệp vụ trong hệ thống; không kích hoạt mạng viễn thông thật.

Một Customer có nhiều Subscriber; một Subscriber thuộc một Customer tại một thời điểm. Mỗi thuê bao có MAIN và có thể có tài khoản phụ được cấu hình/seed. Gói là định nghĩa sản phẩm; đăng ký gói là quan hệ của thuê bao với sản phẩm; từng kỳ đã trả giữ giá, thời lượng và khoảng hiệu lực riêng. Không xóa lịch sử khi gói/tài khoản hết hạn.

Phạm vi hiện tại gồm UC01–UC06, worker gia hạn và hai job. Không xây UI CSKH đầy đủ, mobile app, ngân hàng/cổng thanh toán, billing engine hoặc BI hoàn chỉnh. JWT do IdP bên ngoài cấp; backend xác minh và kiểm quyền.

## 2. Các luồng người dùng chính

| Nghiệp vụ | Khi thành công | Nhánh phải xử lý rõ |
|---|---|---|
| UC01 đấu nối | Hồ sơ, số, thuê bao và MAIN được tạo/cấp nhất quán | Hồ sơ không khớp; số chọn đã bị cấp; AUTO hết kho; chờ 24 giờ; retry; crash; mất phản hồi |
| UC02 đăng ký/mua nối | Thu phí MAIN, ghi kỳ và cập nhật thời gian đã trả trong cùng transaction | Thiếu tiền; gói ngừng bán; version cũ; chờ hủy; manual đua worker; lỗi DB |
| UC03 hủy | Nhận yêu cầu hủy hợp lệ; giữ quyền đã trả tới cuối toàn bộ coverage | Receipt đúng/sau hạn; mất kết nối; hủy lặp; version đổi; chặn gia hạn trong lúc ý định chưa kết luận |
| UC04 cộng tiền | Đúng accountId tăng số dư và có một giao dịch nguồn | Hết hạn; sai thuê bao; quá hạn mức; tràn số; hai lệnh cạnh tranh; replay |
| UC05 trừ tiền | Đúng accountId giảm số dư, có attribution tới gói/kỳ và một giao dịch | Thiếu tiền; kỳ không hiệu lực; account hết hạn; xung đột request; timeout/retry |
| UC06 tra cứu | Tập kết quả cố định và phân trang ổn định theo caller/bộ lọc | OR/AND, có/không phí; giao dịch commit muộn; cursor sai/hết hạn; caller khác |

Đặc tả đầy đủ ở [SRS](../01-requirements/SRS.md), [danh mục UC](../01-requirements/use-cases/UC02-UC06.md) và từng file UC được liên kết trong danh mục. Các UC có tiền/hậu điều kiện, đầu vào/đầu ra, luồng chính, luồng thay thế, ngoại lệ và AC.

### Đấu nối Hybrid

Nếu có thể hoàn tất, backend xử lý đồng bộ và trả `SUCCEEDED` hoặc `FAILED`. Chỉ khi **AUTO hết kho và khách đồng ý chờ**, yêu cầu mới vào `WAITING`, tối đa 24 giờ. Chọn một số cụ thể nhưng số đã bị cấp không tự chuyển thành chọn số khác hoặc chờ. `WAITING` chưa có thuê bao đấu nối thành công. Bổ sung kho đánh thức xử lý; recovery khôi phục yêu cầu bị bỏ sót. Hết hạn chờ thành `EXPIRED`.

### Tiền MAIN và tài khoản phụ

Mọi tài khoản có số dư riêng. UC04/UC05 làm đúng accountId caller gửi; không tự gom hoặc đổi nguồn. Ví dụ một khoản tiêu dùng 30.000 đồng chỉ định GAME nhưng GAME có 20.000: lệnh thất bại vì thiếu tiền, dù MAIN có tiền. Không trừ 20.000 GAME cộng 10.000 MAIN, cũng không tự chuyển toàn bộ khoản thu sang MAIN. Phí đăng ký/gia hạn gói có quy tắc riêng: luôn thu MAIN. Caller không cần gọi UC05 thêm để thu phí đăng ký.

Mỗi lệnh tối đa 10 triệu đồng, số dư tối đa 100 triệu đồng, VND nguyên; giới hạn có cấu hình. Thiếu tiền hoặc vi phạm điều kiện thì không tạo giao dịch tài chính thành công hoặc thay đổi số dư. Kiểm hạn tài khoản dùng giờ DB dưới khóa ngay trước cập nhật theo [CR-TIME-002](../00-project/CR-TIME-002-account-expiry.md): còn hạn lúc kiểm có thể commit qua hạn; không cam kết thời điểm hoàn tất commit vật lý.

### Kỳ gói, mua nối và gia hạn muộn

Ngày đăng ký là ngày đầu. Gói 30 ngày đăng ký 10:15 ngày 01/10/2026 có hiệu lực tới đúng 00:00 ngày 31/10/2026 theo giờ Việt Nam. Dùng khoảng `[start,end)`: đúng end đã hết quyền. Lưu UTC, tính ngày theo `Asia/Ho_Chi_Minh`.

Mua nối khi còn thời gian đã trả thì nối từ cuối coverage, không cắt mất kỳ tương lai. Khi đã gián đoạn, kỳ mới bắt đầu lúc xử lý hợp lệ, không hồi tố quyền vào khoảng trống. Worker chạy muộn vẫn có thể thu **một kỳ mới** nếu đủ điều kiện theo Q25=B; không thu bù hàng loạt kỳ đã lỡ. Một lần tự gia hạn đã chốt thất bại do thiếu tiền không tự sống lại sau khi nạp tiền. Đăng ký mới hợp lệ là nghiệp vụ riêng có version/mã yêu cầu riêng.

Manual và worker cùng nhắm một kỳ phải dùng chung điều kiện và khóa nghiệp vụ, chỉ tạo một kỳ/một phí. Gói ngừng bán không nhận khách mới; khách đã đăng ký vẫn có thể gia hạn nếu chính sách hiện hành cho phép, lưu giá/thời lượng snapshot của lần đó.

### Hủy bảo vệ toàn bộ thời gian đã trả

Nếu khách đã trả tới 30/11 dù kỳ hiện tại hết 31/10, nhận hủy ngày 20/10 vẫn giữ quyền tới 30/11, không hoàn phí và không mua nối thêm sau khi nhận hủy. Khi đến cuối coverage chuyển `CANCELLED`, khác `EXPIRED` do hết hạn thông thường. Chi tiết ở [CR-CAN-002](../00-project/CR-CAN-002-prepaid-cancellation.md).

Mốc nhận hủy theo [CR-CAN-003](../00-project/CR-CAN-003-cancellation-receipt.md): lưu ý định `PENDING` và xác nhận commit trước; sau đó khóa lại và kiểm giờ DB. Trong lúc pending phải chặn cả manual và auto. Mốc kiểm trước cuối coverage mới được nhận hủy; đúng/sau hạn bị từ chối, gỡ chặn và không tự đổi sang kỳ khác. Nếu mất phản hồi phải tra lại DB; chưa rõ commit thì không đoán thất bại. Kết quả nhận hủy đã commit được trả lại nguyên vẹn khi gửi cùng mã, kể cả lúc tra lại đã qua hạn.

## 3. Vì sao có worker và hai job?

Worker gia hạn tìm các đăng ký tới hạn qua chỉ mục và xử lý theo lô; không có một thread/timer riêng cho mỗi thuê bao. Chu kỳ chạy còn là tham số thiết kế cần review, không mặc định 60 giây. JOB-01 lúc 00:05 xử lý hết hạn tài khoản/gói, hoàn tất hủy và khôi phục các việc bỏ sót; dùng chung lõi và dấu kỳ với worker để không thu phí trùng. JOB-02 lúc 00:30 tổng hợp tiêu dùng ngày trước, là nghiệp vụ riêng. Recovery chờ cấp số UC01 phục vụ WAITING và không thay worker gia hạn.

API kiểm quyền bằng timestamp, không chờ job đổi nhãn trạng thái mới chặn sử dụng quá hạn. Khi backend dừng, không hứa gia hạn đúng hạn hoặc quyền liên tục. Khi chạy lại áp dụng quy tắc kỳ mới sau khoảng gián đoạn. Job chạy tay/retry/crash phải tiếp tục từ dấu đã lưu; không lấy runId mới để tạo lại phí cùng kỳ.

## 4. Báo cáo và dữ liệu đến muộn

Báo cáo phân loại giao dịch theo `recordedAt`: thời điểm ghi nhận nghiệp vụ trong transaction cuối cùng đã commit. Ví dụ ghi 23:59:59 ngày 20, commit 00:00:01 ngày 21 thì vẫn thuộc ngày 20; chưa commit thì chưa nhìn thấy. Đây là thay đổi đã duyệt [CR-TIME-001](../00-project/CR-TIME-001-reporting-time.md), không cần suy đoán timestamp commit vật lý.

Tra cứu UC06 cố định cả thành viên lẫn số liệu của tập kết quả ngay khi tạo snapshot; phân trang không đọc một tập đang thay đổi. OR nghĩa có ít nhất một gói được lọc; AND nghĩa có đủ mọi gói được lọc theo tiêu chí query. Caller chọn có tính phí đăng ký/gia hạn hay chỉ tiêu dùng. Page mặc định 50, tối đa 200; một thuê bao không lặp trên nhiều dòng vì có nhiều nguồn giao dịch.

JOB-02 giữ cutoff đầu tiên đã lưu bền vững dù lần chạy trước lỗi. Đóng góp nguồn được ghi duy nhất, tổng hợp từ tập nguồn đã cố định. Ngày đã finalized không sửa lại; giao dịch commit muộn được ghi điều chỉnh ở lượt sau với truy vết nguồn/ngày gốc, không tính hai lần và không đổi giờ giao dịch để ép vào ngày mới. Query trực tiếp và bản chốt ngày có thể khác ở thời điểm dữ liệu muộn chưa điều chỉnh; phải giải thích và đối soát được.

## 5. Cơ chế bảo vệ dữ liệu và mất phản hồi

Mã yêu cầu do ứng dụng CSKH quản lý. Cùng caller/loại thao tác/requestId và cùng nội dung trả kết quả cũ; đổi nội dung bị `IDEMPOTENCY_CONFLICT`. Hai requestId khác nhau không tự được coi là retry: version và khóa kỳ bảo vệ tranh chấp nghiệp vụ. Replay giữ 30 ngày với UC02/04/05, 7 ngày với UC03 và UC01 theo baseline; sau đó tombstone vẫn ngăn tác động lại. Không purge request đang xử lý.

Timeout phía caller không chứng minh DB rollback. Caller tra trạng thái/gửi lại cùng mã, backend xác minh kết quả trước khi tạo tác động mới. Transaction phải nguyên tử giữa tiền, ledger, kỳ và kết quả; ràng buộc unique là lớp bảo vệ cuối bên cạnh kiểm tra dưới khóa. READ COMMITTED không tự tránh âm tiền. Các chi tiết lock order, unknown commit, lease/fencing và recovery ở [concurrency](../04-design/concurrency-and-jobs.md).

Dữ liệu demo dùng chung nhưng quyền thao tác theo scope; chỉ caller tạo lệnh được xem kết quả lệnh đó. Bằng chứng giấy tờ chỉ nhận từ caller được cấp quyền xác minh; backend không tự xây hệ thống xác minh giấy tờ. Không ghép Customer chỉ vì trùng tên/ngày sinh. Phải che dữ liệu nhạy cảm và không ghi JWT/secret vào log.

## 6. Thiết kế kỹ thuật và UML

Thiết kế dùng backend phân module, MySQL 8.0.46/InnoDB là nguồn quyết định về tiền/quyền/trạng thái. RabbitMQ hỗ trợ tín hiệu bất đồng bộ có recovery từ DB; Redis hỗ trợ cache phù hợp, không làm nguồn số dư hoặc giữ khóa thay tính đúng của MySQL. IdP ngoài backend cấp JWT. Môi trường công ty không cần chạy service; môi trường nhà chạy các service theo topology.

[UML phân tích](../02-analysis/analysis.md) gồm Use Case, Domain, State và Sequence cho UC01–06, worker và hai job; [kiến trúc](../03-architecture/architecture.md) có C4/module; [schema](../04-design/database-schema.md) có ERD; [Design Class](../04-design/design-class-diagrams/README.md) mô tả trách nhiệm/lời gọi ở mức thiết kế. Nguồn `.puml` là nơi sửa, SVG phải được sinh từ đúng nguồn. Domain không đồng nhất với JPA Entity hoặc ERD.

[OpenAPI](../04-design/openapi.yaml) và [API contract](../04-design/api-contract.md) mô tả endpoint, DTO, scope, HTTP/lỗi, lookup và cursor. Chi tiết UUID CHAR36, chỉ mục, cột, cadence/lô/lease và cấu hình đề xuất còn REVIEW. Việc chọn MySQL không tự duyệt mọi câu SQL hoặc index.

## 7. Chứng minh thiết kế đúng và đáp ứng tải

[27 AC UC01](../01-requirements/use-cases/UC01-Subscriber-Activation.md), AC từng UC/job và [64 ca ngoại lệ](19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md) là đầu vào test. Kiểm thử cần MySQL thật cho khóa/unique/rollback, race cùng số, cùng tiền, cùng kỳ, worker/job/manual và fault injection quanh commit. Kiểm tra từng request/ledger/số dư, không chỉ tổng số thành công.

Trước tải phải có ít nhất 500.000 bản ghi tổng các bảng nghiệp vụ. Theo Q32: warm-up 2 phút, đo 600 giây; cùng cửa sổ phải có ít nhất 18.000 đăng ký gói thành công mới và 18.000 trừ tiền độc lập thành công mới. Retry/phí nội bộ không tăng số nghiệp vụ mới. 50% debit attempts, kể cả retry, chia vào 10 cặp thuê bao–tài khoản có tranh chấp thật. p95 ≤500 ms, p99 ≤1 s, lỗi hệ thống ≤0,1% theo đúng mẫu số; timeout phải báo và đối soát, unresolved phải bằng 0. [Protocol tải](../05-testing/performance-test.md) quy định chi tiết thu bằng chứng.

Tất cả ca runtime/tải hiện **chưa chạy**. Kiểm tra Markdown/OpenAPI/render UML chỉ chứng minh cấu trúc tài liệu, không thay migration/test. [Verification](../verification.md) lưu kết quả thực tế.

## 8. Điều kiện tiến tới triển khai

Nghiệp vụ khảo sát đã được ghi nhận; không yêu cầu điền lại phiếu cũ. M0–M4 UC01 có lịch sử duyệt, còn delta MySQL và thiết kế mới cần review theo [checklist M4](../04-design/m4-review-checklist.md). Sau duyệt delta UC01 mới P5.1 tạo backend/migration/test, rồi triển khai từng UC. Không đánh dấu M5–M8 đạt chỉ vì tài liệu đầy đủ hơn.

Phần còn mở là nghiệm thu kỹ thuật/config, thực thi test, archive/purge sau demo và backup/restore thật. Không có tuyên bố đã xử lý mọi ngoại lệ có thể tồn tại; bộ tài liệu xác định các tình huống đã xét, kết quả mong đợi và bằng chứng cần có để tiếp tục phát hiện thiếu sót.
