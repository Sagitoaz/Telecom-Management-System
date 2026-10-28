> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Tổng quan dự án — Hệ thống Quản lý thuê bao viễn thông

**Tên dự án:** Telecom Subscription Management System (`Telecom-Management-System`)
**Mã tài liệu:** DOC-PROJ-001 · **Phiên bản:** 1.3 · **Cập nhật:** 26/10/2026.

Tài liệu trình bày phạm vi và quy tắc hiện hành sau khi tiếp nhận các quyết định của chủ dự án. Baseline Phase 0 được duyệt ngày 14/10/2026; M1–M4 có phê duyệt lịch sử trong phạm vi UC01. Các thiết kế bổ sung ngày 26/10 và delta MySQL vẫn là REVIEW, chưa phải bằng chứng triển khai/kiểm thử. Trạng thái công việc được quản lý tại [ROADMAP](../ROADMAP.md).

## 1. Bài toán và mục tiêu

TelecomManagement là backend quản lý khách hàng, thuê bao, kho số, gói cước, tài khoản dịch vụ và giao dịch tài chính nội bộ. Hệ thống cung cấp REST API để hệ thống CSKH đấu nối thuê bao, đăng ký hoặc hủy gói, cộng/trừ tiền và tra cứu thuê bao có phát sinh tiêu dùng. Scheduler và worker nội bộ thực hiện gia hạn, xử lý hết hạn, khôi phục yêu cầu và tổng hợp báo cáo ngày.

Mục tiêu chính là xử lý đúng khi nhiều lời gọi cùng tác động lên một số thuê bao, tài khoản hoặc đăng ký gói. Một số không được cấp hai lần; số dư không âm; phí và quyền dùng gói không được thành công riêng lẻ; retry không tạo lại tác động. Kết quả phải truy vết được theo mã yêu cầu và đối soát với lịch sử giao dịch.

[Nguyên văn đề bài](../requirement.md) được giữ nguyên. Các cách diễn giải và thay đổi đã được chủ dự án xác nhận được lưu riêng trong [sổ quyết định](clarification-log.md), [biên nhận ngày 26/10](decision-receipt-20261026.md) và các CR liên quan. Tài liệu hiện hành áp dụng quyết định mới nhất, không dùng lựa chọn cũ đã bị thay làm quy tắc thực thi.

## 2. Các bên liên quan và ranh giới

| Bên/thành phần | Vai trò |
|---|---|
| Khách hàng | Cung cấp hồ sơ, chọn số, đồng ý chờ hoặc yêu cầu sử dụng/hủy dịch vụ thông qua CSKH |
| Hệ thống CSKH | Gọi API, quản lý requestId, gửi lựa chọn của khách hàng, hiển thị và tra kết quả |
| Bên khai thác báo cáo | Truy vấn dữ liệu tiêu dùng theo quyền, bộ lọc và bản kết quả cố định |
| IdP / Keycloak | Cấp JWT và công bố khóa xác minh; nằm ngoài backend Telecom |
| TelecomManagement | Kiểm quyền/dữ liệu, thực hiện nghiệp vụ, lưu kết quả, cung cấp báo cáo và tác vụ nền |
| Người vận hành | Theo dõi dịch vụ, chạy/tra job bằng quyền riêng, phục hồi và đối soát |
| Công cụ kiểm thử | Tạo dữ liệu, phát tải và kiểm chứng từng yêu cầu, giao dịch, số dư |

Khách hàng không trực tiếp gọi REST API. Scheduler, worker, MySQL, RabbitMQ và Redis thuộc ranh giới kỹ thuật của giải pháp; scheduler không được vẽ như actor nghiệp vụ bên ngoài. Backend không cấp JWT nội bộ và không tự xác minh giấy tờ như một hệ thống KYC.

## 3. Đối tượng nghiệp vụ

| Đối tượng | Vai trò và quan hệ |
|---|---|
| Customer | Hồ sơ khách hàng; một Customer có nhiều Subscriber, mỗi Subscriber thuộc một Customer tại một thời điểm |
| Giấy tờ đã xác minh | Customer có thể có nhiều giấy tờ; liên kết dựa trên cặp loại/số và chứng cứ tin cậy, không tự ghép theo tên/ngày sinh |
| Subscriber | Thuê bao được đấu nối trong backend, liên kết Customer, số đã cấp, các tài khoản và đăng ký gói |
| PhoneNumberInventory | Kho số với trạng thái khả dụng/đã cấp; tra cứu không giữ chỗ |
| ServicePackage | Định nghĩa gói, giá, số ngày và chính sách; ngừng bán mới không tự cấm gia hạn khách cũ đủ điều kiện |
| PackageSubscription | Đăng ký ổn định của cặp thuê bao–gói, có version, trạng thái, ý định hủy và cuối thời gian đã trả |
| SubscriptionCycle | Một kỳ đã mua, lưu khoảng hiệu lực, giá/số ngày/phiên bản áp dụng và phí; lịch sử kỳ bất biến |
| Account | Số dư theo từng tài khoản của thuê bao; MAIN dùng thu phí gói, tài khoản phụ có loại/quyền/hạn rõ ràng |
| FinancialTransaction | Giao dịch đã ghi nhận, liên kết đúng tài khoản/nguồn nghiệp vụ và kỳ gói khi cần; phục vụ đối soát |
| Request và kết quả | Theo dõi thao tác của một caller, chống xử lý lặp và hỗ trợ tra cứu khi mất phản hồi |
| Snapshot/đóng góp báo cáo | Giữ tập kết quả ổn định và truy vết mỗi nguồn được tính đúng một lần |

Đăng ký gói và kỳ gói là hai khái niệm khác nhau. Một đăng ký có thể có kỳ đang dùng và các kỳ tương lai đã trả. `coverageEnd` là cuối toàn bộ thời gian đã trả, có thể muộn hơn cuối kỳ hiện tại; việc hủy phải tôn trọng phần đã trả đó.

## 4. Các nhóm nghiệp vụ

### 4.1. UC01 — Đấu nối thuê bao

Hệ thống hỗ trợ chọn một số khả dụng hoặc tự cấp số. Số đầu vào phải đúng E.164, ví dụ `+84901234567`; backend không tự đoán quốc gia từ `090…`. Tra cứu số chỉ cung cấp thông tin tại thời điểm đọc; khi cấp vẫn phải kiểm và chống tranh chấp.

UC01 dùng mô hình Hybrid: có thể hoàn tất thì trả kết quả cuối đồng bộ. Chỉ khi tự cấp hết kho và khách đồng ý chờ mới ghi `WAITING`, tối đa 24 giờ. Bổ sung kho kích hoạt thử lại; khôi phục DB mỗi 60 giây giúp tìm yêu cầu bị bỏ sót. Hết hạn chuyển `EXPIRED`; WAITING không phải thành công.

Thành công phải tạo thuê bao, liên kết Customer hợp lệ, cấp đúng một số và tạo MAIN số dư 0 nhất quán. Hồ sơ/chứng cứ được đối chiếu theo quy tắc đã duyệt; không tự merge Customer hoặc sửa hồ sơ khác biệt. Đấu nối chỉ hoàn tất trong backend, không kích hoạt mạng thật.

### 4.2. UC02 — Đăng ký và gia hạn gói

Gói chỉ có hiệu lực khi thu phí MAIN thành công. Số dư, giao dịch phí, kỳ gói và kết quả request phải nhất quán; không để có tiền đã trừ nhưng không có kỳ hoặc ngược lại. API đăng ký gói khác API trừ tiền độc lập.

Thao tác thủ công gửi phiên bản đăng ký đang thấy để tránh hai lệnh mới cùng mua trên một hạn cũ. Nếu worker/thao tác khác đã tạo kỳ đó, phải đánh giá lại và trả xung đột/trạng thái hiện tại, không âm thầm nối thêm kỳ nữa. Muốn mua kỳ tiếp phải lấy phiên bản mới và gửi yêu cầu có chủ đích.

Nếu còn thời gian đã trả, kỳ mua tiếp nối từ coverageEnd. Sau gián đoạn, kỳ mới bắt đầu tại lúc xử lý hợp lệ, không hồi tố quyền hoặc truy thu nhiều kỳ đã bỏ lỡ. Gia hạn tự động xử lý muộn chỉ mua một kỳ khi còn đủ điều kiện/MAIN, chưa hủy hợp lệ và chưa có lệnh khác tạo kỳ. Gói ngừng bán mới vẫn có thể gia hạn cho khách cũ theo chính sách được cấu hình.

### 4.3. UC03 — Hủy gói

Theo [CR-CAN-002](CR-CAN-002-prepaid-cancellation.md), hủy có hiệu lực ở cuối **toàn bộ thời gian đã trả**, không hoàn phí. Nhận hủy hợp lệ chặn mua thêm/gia hạn nhưng giữ quyền dùng trong các kỳ đã trả; tới hạn chuyển `CANCELLED`, khác `EXPIRED` do hết hạn thông thường.

Theo [CR-CAN-003](CR-CAN-003-cancellation-receipt.md), mốc tiếp nhận là giờ DB được kiểm sau khi backend đã xác nhận ý định hủy được lưu thành công. Chỉ mốc nhỏ hơn coverageEnd đang nhắm tới mới đủ điều kiện; bằng/sau hạn bị từ chối với `CANCELLATION_RECEIVED_TOO_LATE`, không tự chuyển sang kỳ mới. Intent đang chờ phân giải chặn tạm cả mua thêm và gia hạn. Timestamp trước commit hoặc log HTTP đến sớm không tạo ưu tiên.

Mất phản hồi commit phải tra kết quả, không đoán rollback. Nếu kết quả nhận hủy đã commit nhưng mất HTTP, cùng requestId/nội dung trả lại kết quả cũ dù tra sau hạn. Ngữ nghĩa này đã được duyệt; thiết kế hai transaction và kiểm thử lỗi vẫn là REVIEW.

### 4.4. UC04/UC05 — Cộng và trừ tiền

Lệnh thao tác đúng accountId được chỉ định. Tài khoản phụ thiếu tiền thì thất bại dù MAIN đủ tiền; không fallback hoặc cộng gộp số dư. MAIN không có hạn mặc định; tài khoản phụ demo phải có loại, quyền cộng/trừ và hạn dùng rõ trước khi tạo. Phạm vi không thêm API mở tài khoản phụ.

Tiền dùng số nguyên VND; mỗi lệnh tối đa 10.000.000 VND, số dư tối đa 100.000.000 VND, cấu hình được. Không cho số dư âm. Trừ tiêu dùng phải chỉ đúng kỳ gói đang có hiệu lực thuộc thuê bao; chưa có gói/đã hết hạn/chỉ sai gói thì không trừ tiền.

Theo [CR-TIME-002](CR-TIME-002-account-expiry.md), kiểm giờ DB dưới khóa tài khoản ngay trước cập nhật. Đã tới hạn thì từ chối; kiểm khi còn hạn có thể commit sau hạn. Thiếu tiền hoặc không đủ điều kiện không tạo giao dịch tài chính thành công và không đổi số dư.

### 4.5. UC06 và tổng hợp tiêu dùng

API trả thuê bao có phát sinh tiêu dùng trong khoảng `[X,Y)` và theo gói. OR là có giao dịch của ít nhất một gói được chọn; AND là có giao dịch của từng gói được chọn. `includeFee` phân biệt chỉ tiêu dùng với tính thêm phí đăng ký/gia hạn; CREDIT không phải tiêu dùng. Mỗi thuê bao xuất hiện một lần, mặc định 50 và tối đa 200 thuê bao/trang.

Một lượt xem cố định tập kết quả; cursor được bảo vệ và gắn caller/bộ lọc/snapshot. Muốn thấy dữ liệu mới phải mở lượt xem mới, không giữ transaction DB trong lúc xem các trang.

Theo [CR-TIME-001](CR-TIME-001-reporting-time.md), phân loại bằng `recordedAt` trong transaction cuối cùng đã commit. Ghi nhận 23:59:59 ngày 20 nhưng commit 00:00:01 ngày 21 vẫn thuộc ngày 20, chỉ xuất hiện sau commit. Ngày đã chốt bất biến; nguồn thuộc ngày cũ được phát hiện muộn tạo điều chỉnh ở kỳ sau, có truy vết và không đếm hai lần. Cutoff/tập nguồn đã lưu lần đầu được giữ qua retry dù lần trước chưa hoàn tất.

## 5. Thời gian, tác vụ nền và lịch sử

Dữ liệu thời gian lưu UTC, ngày nghiệp vụ dùng `Asia/Ho_Chi_Minh`. Ngày đăng ký là ngày đầu: gói 30 ngày từ `2026-10-01T10:15:00+07:00` hết tại `2026-10-31T00:00:00+07:00`. Khoảng hiệu lực `[start,end)` không gồm end. Kỳ tiếp nối bắt đầu tại end trước; kỳ sau gián đoạn bắt đầu tại lúc xử lý hợp lệ.

Worker gia hạn chạy thường xuyên, khoảng chạy cụ thể còn là đề xuất kỹ thuật. JOB-01 chạy 00:05 hằng ngày, xử lý hết hạn gói/tài khoản, hoàn tất hủy và rà soát phần bị bỏ sót. JOB-02 riêng chạy 00:30 để tổng hợp ngày. Các đường gia hạn dùng chung lõi và chống trùng phí/kỳ. Không nhầm nhịp khôi phục UC01 60 giây với nhịp worker gia hạn.

Hết hạn dùng trạng thái/soft delete, giữ lịch sử và định nghĩa gói. UC01/03 giữ đầy đủ phản hồi 7 ngày; UC02/04/05 giữ 30 ngày sau trạng thái cuối, sau đó giữ tombstone để mã cũ không thực hiện lại. Không dọn request đang xử lý/chờ. Lịch sử nghiệp vụ có thời gian giữ 5 năm cấu hình được; demo không chạy purge.

## 6. Bảo mật và tích hợp

JWT do IdP ngoài backend cấp. Backend kiểm chữ ký, issuer, audience, thời hạn và scope thao tác. Caller lấy từ danh tính ứng dụng đã xác minh, không từ body tự khai. Chỉ ứng dụng được cấp quyền chứng cứ tin cậy được gửi tham chiếu xác minh giấy tờ; Telecom kiểm nguồn/định dạng và lưu tham chiếu, không xây quy trình KYC.

Demo dùng chung tập dữ liệu giả, không bắt buộc hệ thống đa tenant. Tuy nhiên request/kết quả và snapshot báo cáo thuộc caller; ứng dụng A không được tra kết quả của B. Cùng mã/cùng nội dung không gây tác động lại; khác nội dung là xung đột. Timeout là chưa rõ kết quả, không cho phép tạo mã mới để thử lại một cách mù quáng.

## 7. Ràng buộc quy mô và nghiệm thu

| Mã | Yêu cầu hiện hành |
|---|---|
| HLR-01 | Tổng các bảng nghiệp vụ ≥ 500.000 bản ghi trước đo tải, có thống kê từng bảng |
| HLR-02 | Đăng ký gói ≥ 30 nghiệp vụ thành công mới/giây trong bài tải hỗn hợp |
| HLR-03 | Đồng thời trừ tiền độc lập ≥ 30 nghiệp vụ thành công mới/giây; tổng ≥ 60 TPS; không đếm replay hoặc phí gói hai lần |
| HLR-04 | 50% số lần gọi trừ tiền, kể cả retry, phân vào 10 cặp thuê bao–tài khoản nóng có tranh chấp thực |
| HLR-05 | Đối soát từng request/giao dịch/số dư, kể cả đủ tiền, thiếu tiền, timeout; kết quả cuối chưa xác định phải bằng 0 |
| HLR-06 | JOB-01 hằng ngày dùng trạng thái/soft delete, giữ lịch sử và danh mục gói |
| HLR-07 | UC06 lọc thời gian/gói, OR/AND/includeFee, thuê bao không trùng và phân trang cố định |
| HLR-08 | JOB-02 tổng hợp ngày riêng, giữ tập nguồn qua retry và điều chỉnh nguồn muộn một lần |

Q32 quy định warm-up 2 phút, đo 10 phút, p95 ≤ 500 ms, p99 ≤ 1 giây và lỗi ≤ 0,1%, với cách tính chi tiết tại [kế hoạch kiểm thử](../05-testing/test-plan.md). Đây là tiêu chí phải đo, chưa phải kết quả đã đạt.

## 8. Hướng kỹ thuật và trạng thái tiếp theo

Nền tảng được chọn là Java 21/Spring Boot, một ứng dụng chia module, MySQL 8.0.46/InnoDB, `READ COMMITTED` cho đường ghi, JPA/JDBC, Flyway, RabbitMQ, Redis và IdP/Keycloak ngoài Telecom. Tại nhà chạy bộ service bằng Compose; tại công ty làm tài liệu/mã/unit test không cần DB. RabbitMQ báo bổ sung kho, Redis cache danh mục; MySQL vẫn là nguồn quyết định nghiệp vụ.

Cách lưu UUID, chi tiết schema/index/DTO/HTTP/scope, nhịp worker và cấu hình runtime mới vẫn REVIEW theo quyền được giao đề xuất. Không có migration/runtime/TPS mới được chứng minh bằng việc soạn tài liệu. Bước tiếp theo là nghiệm thu delta MySQL UC01 theo [checklist M4](../04-design/m4-review-checklist.md), sau đó thực hiện P5.1; các UC sau tiếp tục theo từng vòng triển khai.

Chi tiết phạm vi và định nghĩa nằm tại [Scope & Glossary](scope-and-glossary.md); yêu cầu tại [SRS](../01-requirements/SRS.md). Lịch sử quyết định được tra trong sổ DEC/CR/OQ; bản ZIP phụ đã dọn theo yêu cầu chủ dự án ngày 28/10/2026.
