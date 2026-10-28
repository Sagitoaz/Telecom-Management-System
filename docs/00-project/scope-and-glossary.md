> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Phạm vi hệ thống và thuật ngữ

**Dự án:** Telecom Subscription Management System
**Mã tài liệu:** DOC-PROJ-002 · **Phiên bản:** 1.2 · **Cập nhật:** 26/10/2026.

Tài liệu xác định phạm vi và cách hiểu thuật ngữ hiện hành sau các quyết định của chủ dự án ngày 26/10. Quyết định nghiệp vụ được ghi tại [sổ quyết định](clarification-log.md), [biên nhận](decision-receipt-20261026.md) và các CR; thiết kế kỹ thuật mới vẫn REVIEW. Trạng thái checkpoint lấy từ [ROADMAP](../ROADMAP.md), không suy từ việc đã có tài liệu.

## 1. Ranh giới và trách nhiệm

TelecomManagement là backend xử lý và lưu dữ liệu khách hàng, thuê bao, kho số, gói cước, tài khoản, giao dịch và báo cáo. CSKH là ứng dụng bên ngoài, nhận yêu cầu của khách hàng và gọi REST API; backend không cung cấp giao diện khách hàng/CSKH hoàn chỉnh.

| Thành phần | Trách nhiệm trong phạm vi |
|---|---|
| Khách hàng | Cung cấp hồ sơ, chọn số/chờ khi thiếu số, yêu cầu đăng ký/hủy dịch vụ qua CSKH |
| CSKH | Gửi dữ liệu và lựa chọn rõ ràng, quản lý requestId, lấy version hiện hành, tra và hiển thị kết quả |
| Bên khai thác báo cáo | Gửi bộ lọc, dùng cursor đúng lượt xem, khai thác dữ liệu theo quyền |
| IdP / Keycloak | Cấp JWT, quản lý khóa xác minh; nằm ngoài backend Telecom |
| Backend Telecom | Kiểm token/quyền/dữ liệu, điều phối nghiệp vụ nguyên tử, chống lặp và lưu lịch sử |
| Scheduler và worker | Thành phần nội bộ kích hoạt xử lý định kỳ/khôi phục, không phải actor ngoài |
| Người vận hành | Chạy/tra job bằng quyền riêng, theo dõi, phục hồi và đối soát |

MySQL, RabbitMQ và Redis là hạ tầng của giải pháp. MySQL giữ dữ liệu có thẩm quyền; RabbitMQ cung cấp tín hiệu bổ sung kho; Redis cache danh mục đọc. Chúng không thay đổi ranh giới trách nhiệm nghiệp vụ hoặc tạo thêm API quản trị chưa được duyệt.

## 2. Phạm vi chức năng

### SC-01 — Khách hàng, thuê bao và cấp số (UC01)

Một Customer có nhiều Subscriber; mỗi Subscriber thuộc một Customer tại một thời điểm. Customer có thể có nhiều giấy tờ đã xác minh. Backend đối chiếu loại/số giấy tờ và hồ sơ chuẩn hóa theo quyết định đã duyệt; không tự merge theo tên/ngày sinh, không tự sửa hồ sơ khác biệt hoặc thêm giấy tờ vào Customer cũ ngoài quy trình được xác nhận.

Hệ thống nhận số E.164 đã chuẩn hóa, hỗ trợ chọn số khả dụng hoặc tự cấp. Tra cứu không giữ chỗ; lúc cấp phải chống cấp trùng. UC01 trả kết quả cuối đồng bộ khi có thể. Chỉ AUTO hết kho và có sự đồng ý chờ mới vào WAITING tối đa 24 giờ, sau đó EXPIRED nếu chưa hoàn tất. Cơ chế thử lại khi bổ sung kho và quét khôi phục DB mỗi 60 giây không biến WAITING thành thành công.

Thành công tạo Subscriber, cấp một số và MAIN số dư 0 nhất quán. Việc này chỉ diễn ra trong backend, không kích hoạt mạng viễn thông thật. Tái cấp số đã từng sử dụng chưa có quy trình giải phóng/cách ly hoàn chỉnh, không được mặc định thực hiện.

### SC-02 — Đăng ký và gia hạn gói (UC02)

Backend kiểm điều kiện, thu phí MAIN và tạo kỳ gói trong cùng nghiệp vụ nguyên tử. Phí, số dư, kỳ và kết quả request cùng thành công hoặc cùng rollback. API đăng ký không phải một lời gọi API trừ tiền độc lập có commit riêng.

Đăng ký gói là bản ghi ổn định của thuê bao–gói; mỗi lần mua hợp lệ tạo kỳ với giá/số ngày/phiên bản đã chốt cho kỳ đó. Thủ công dùng `expectedSubscriptionVersion`, tránh hai mã mới cùng kéo dài trên phiên bản cũ. Gia hạn thủ công và worker cùng kỳ chỉ được thu một phí; phát hiện bên kia đã tạo kỳ không tự nối thêm kỳ khác.

Khi còn thời gian đã trả, kỳ mới nối từ coverageEnd. Sau gián đoạn, kỳ mới bắt đầu tại lúc xử lý hợp lệ, chỉ một kỳ, không truy thu nhiều kỳ hoặc hồi tố quyền dùng. Ngừng bán mới không tự cấm gia hạn khách cũ; cần kiểm chính sách cấu hình và giá áp dụng cho kỳ mới.

### SC-03 — Hủy đăng ký gói (UC03)

Hủy có hiệu lực cuối tất cả thời gian đã trả, không hoàn phí ([CR-CAN-002](CR-CAN-002-prepaid-cancellation.md)). Nhận hủy hợp lệ chặn mua thêm/tự gia hạn, giữ quyền tới coverageEnd; tới hạn chuyển CANCELLED. Đây khác hết hạn thông thường EXPIRED.

Mốc nhận hủy theo [CR-CAN-003](CR-CAN-003-cancellation-receipt.md): sau khi biết ý định hủy đã commit, backend kiểm giờ DB mới. Mốc này phải nhỏ hơn coverageEnd đang nhắm tới và version vẫn hợp lệ. Đúng/sau hạn thì FAILED `CANCELLATION_RECEIVED_TOO_LATE`, giải phóng chặn tạm, không tự hủy kỳ mới. Intent PENDING chưa phải accepted nhưng chặn cả mua thêm và gia hạn trong lúc phân giải.

Commit chưa rõ kết quả phải lookup; không dùng giờ HTTP hoặc timestamp trước commit để lùi mốc nhận. Accepted đã được chốt nhưng mất phản hồi được replay kết quả cũ dù đã qua hạn. Hai transaction là thiết kế thực hiện cần review/test; quy tắc receipt đã được chủ dự án duyệt.

### SC-04 — Tài khoản và giao dịch (UC04/UC05)

Các tài khoản có số dư riêng. Cộng/trừ tiền thao tác đúng accountId, đúng quyền của loại tài khoản và hạn dùng; không gộp hoặc lấy bù tài khoản khác. Phí gói luôn MAIN. MAIN không có hạn mặc định; tài khoản phụ demo chỉ được tạo khi loại/quyền/hạn đã khai báo rõ. Không có API mở tài khoản phụ trong phạm vi này.

Tiền là số nguyên VND; lệnh phải dương, tối đa 10 triệu, số dư tối đa 100 triệu và cấu hình được. Không số dư âm. Thiếu tiền không đổi số dư và không tạo giao dịch tài chính thành công. Tiêu dùng cần kỳ gói đang hiệu lực thuộc đúng thuê bao; không có gói, hết hạn hoặc sai gói thì từ chối.

Kiểm hạn bằng giờ DB dưới khóa tài khoản ngay trước cập nhật theo [CR-TIME-002](CR-TIME-002-account-expiry.md). Đã tới hạn thì từ chối; kiểm trước hạn có thể commit sau hạn. Phạm vi không yêu cầu đo hoặc bảo đảm commit vật lý trước thời điểm hết hạn.

### SC-05 — Tra cứu và báo cáo (UC06/JOB-02)

API trả danh sách thuê bao không trùng có tiêu dùng trong `[X,Y)`, theo bộ gói. OR chọn ít nhất một gói, AND yêu cầu từng gói được chọn đều có giao dịch. `includeFee` phân biệt chỉ tiêu dùng với có thêm phí đăng ký/gia hạn; CREDIT bị loại khỏi tiêu dùng. Trang mặc định 50, tối đa 200 thuê bao.

Các trang của cùng lượt xem dựa trên tập kết quả cố định, cursor gắn caller/bộ lọc/snapshot và được bảo vệ toàn vẹn. Không giữ transaction DB mở trong lúc bên gọi đọc các trang. Muốn dữ liệu mới phải mở lượt xem mới.

Theo [CR-TIME-001](CR-TIME-001-reporting-time.md), nguồn thời gian là recordedAt của giao dịch cuối cùng đã commit. Ngày đã chốt bất biến; giao dịch phát hiện muộn thuộc ngày cũ tạo một điều chỉnh ở kỳ sau, truy nguồn và không cộng hai lần. JOB-02 giữ cutoff/tập nguồn bền vững đầu tiên qua retry dù lần trước chưa hoàn tất; không lấy mốc mới làm đổi kết quả tùy lần chạy.

### SC-06 — Vòng đời và tác vụ nền

Worker gia hạn kiểm tra thường xuyên. JOB-01 lúc 00:05 hằng ngày xử lý hết hạn tài khoản/gói, hoàn tất hủy và rà soát phần bỏ sót; JOB-02 riêng lúc 00:30 tổng hợp tiêu dùng. Lịch theo Asia/Ho_Chi_Minh. Worker và JOB-01 dùng chung lõi/khóa kỳ, không thu lại phí. Khoảng chạy worker chưa được chốt; 60 giây chỉ là nhịp khôi phục UC01 đã chọn.

Hết hạn dùng trạng thái/soft delete và giữ lịch sử, không xóa định nghĩa gói. API vẫn kiểm thời hạn ngay khi xử lý, không đợi job cập nhật trạng thái mới mất quyền. Chạy tay có quyền vận hành riêng và cùng cơ chế chống trùng như lịch tự động.

## 3. Ngoài phạm vi

| Mã | Nội dung | Ranh giới |
|---|---|---|
| EX-01 | Giao diện CSKH hoàn chỉnh | CSKH là hệ thống ngoài gọi API |
| EX-02 | Ứng dụng di động khách hàng | Không thuộc sản phẩm backend hiện tại |
| EX-03 | Tích hợp thiết bị/mạng viễn thông thật | Đấu nối là nghiệp vụ dữ liệu nội bộ |
| EX-04 | Ngân hàng/cổng thanh toán thật | Cộng/trừ là giao dịch nội bộ theo yêu cầu đã được cấp quyền |
| EX-05 | Billing engine viễn thông hoàn chỉnh | Không tính cước từ lưu lượng/cuộc gọi/dữ liệu mạng thật |
| EX-06 | Nền tảng BI hoàn chỉnh | Chỉ truy vấn/tổng hợp đã được yêu cầu |

Cũng không tự thêm KYC nội bộ, hệ thống đa tenant/grant, API mở ví phụ, quy trình merge Customer, hoàn tiền hoặc tái cấp số đã dùng. Muốn mở rộng phải có quyết định phạm vi và truy vết riêng. Một chi tiết kỹ thuật đang REVIEW không có nghĩa bỏ chức năng nghiệp vụ thuộc phạm vi.

## 4. Quyền gọi, chống lặp và lưu lịch sử

JWT do IdP ngoài Telecom cấp. Backend kiểm danh tính ứng dụng và scope theo thao tác; chỉ caller tin cậy có quyền chứng cứ mới gửi tham chiếu xác minh. Demo dùng dữ liệu giả chung nhưng kết quả request và snapshot vẫn thuộc caller tạo; ứng dụng khác không được đọc theo cùng mã.

Cùng caller/thao tác/requestId và cùng nội dung trả trạng thái/kết quả cũ, không tác động lại. Cùng mã khác nội dung là `IDEMPOTENCY_CONFLICT`. Timeout là chưa biết kết quả: tra hoặc retry cùng mã, không đoán thất bại rồi tạo mã mới. FAILED đã chốt không tự hồi sinh chỉ vì tài khoản được nạp thêm tiền.

Giữ đầy đủ phản hồi UC01/03 7 ngày, UC02/04/05 30 ngày sau trạng thái cuối, rồi giữ tombstone. Không dọn PROCESSING/WAITING. Lịch sử nghiệp vụ có thời gian giữ 5 năm cấu hình được và demo không purge. Thời gian giữ phản hồi, tombstone, snapshot và lịch sử là các chính sách riêng.

## 5. Thuật ngữ thống nhất

| Thuật ngữ | Định nghĩa hiện hành |
|---|---|
| Customer | Hồ sơ khách hàng, có thể sở hữu nhiều thuê bao và nhiều giấy tờ đã xác minh |
| Subscriber | Thuê bao thuộc một Customer tại một thời điểm; được đấu nối trong backend |
| Verified Document / Evidence Reference | Giấy tờ đã xác minh và tham chiếu chứng cứ từ nguồn/caller tin cậy; không phải lời tự khai tùy ý |
| PhoneNumberInventory | Kho số E.164, quản lý khả dụng và cấp phát; danh sách đọc không giữ chỗ |
| ServicePackage | Định nghĩa gói; khác một lần đăng ký và khác kỳ đã trả |
| PackageSubscription / root | Đăng ký ổn định của thuê bao–gói, giữ version, trạng thái, coverage và ý định hủy |
| SubscriptionCycle | Một kỳ đã mua, có start/end, giá/số ngày/phiên bản và phí được giữ bất biến |
| Current cycle | Kỳ có hiệu lực tại thời điểm kiểm; không đồng nhất với mọi kỳ tương lai đã trả |
| coverageEnd | Cuối toàn bộ thời gian đã trả; là mốc hiệu lực hủy theo CR-CAN-002 |
| expectedSubscriptionVersion | Phiên bản bên gọi đã đọc, dùng phát hiện lệnh dựa trên dữ liệu cũ; không tự đoán version |
| MAIN | Tài khoản chính, được tạo số dư 0 sau đấu nối, không hạn mặc định, dùng thu phí gói |
| Tài khoản phụ | Tài khoản có loại, quyền cộng/trừ và hạn được cấu hình rõ; không dùng tiền MAIN để bù |
| FinancialTransaction / ledger | Lịch sử tài chính đã commit, ghi số tiền/tài khoản/nguồn và dữ liệu phục vụ đối soát |
| recordedAt | Mốc ghi nhận nghiệp vụ trong transaction cuối cùng đã commit; không phải giờ hoàn tất commit |
| Eligibility check time | Giờ DB kiểm điều kiện dưới khóa trước cập nhật tiền; có thể trước lúc commit |
| receiptCheckedAt | Giờ DB kiểm sau khi đã xác nhận commit ý định hủy; chỉ trước observedCoverageEnd mới đủ điều kiện nhận |
| PENDING intent | Ý định hủy đang phân giải; chặn mua thêm/gia hạn nhưng chưa phải accepted |
| PENDING_CANCEL | Hủy đã được nhận hợp lệ; còn quyền trong thời gian đã trả và chặn mở rộng |
| CANCELLED / EXPIRED | Hết quyền vì hủy có hiệu lực / hết hạn theo vòng đời; giữ hai lý do riêng |
| WAITING | Chỉ UC01 AUTO hết kho, có đồng ý chờ, tối đa 24 giờ; chưa thành công |
| PROCESSING | Request chưa có kết quả cuối đã xác định; không tương đương WAITING hoặc thành công |
| SUCCEEDED / FAILED | Kết quả cuối của request; UC03 SUCCEEDED là nhận hủy, không nhất thiết đã mất quyền dùng |
| Caller | Ứng dụng đã xác minh từ JWT; không phải dữ liệu callerId tự khai hoặc JWT token cụ thể |
| Idempotency / replay | Một mã nội dung không tạo lại hiệu ứng; trả kết quả/trạng thái đã lưu |
| Tombstone | Dữ liệu tối thiểu giữ khóa/fingerprint/tóm tắt sau khi hết thời gian phản hồi đầy đủ, chặn tái sử dụng mã |
| UNKNOWN | Nhận định phía quan sát chưa biết kết quả, không phải trạng thái thành công DB |
| Report snapshot / cursor | Tập kết quả cố định và dấu chuyển trang được bảo vệ, gắn caller/bộ lọc/lượt xem |
| BASE / ADJUSTMENT | Đóng góp vào ngày nguồn chưa chốt / điều chỉnh kỳ sau cho nguồn muộn; một nguồn không được cộng hai lần |
| Worker / job run | Thành phần xử lý nền / một lần thử chạy có runId; retry run không tạo nghiệp vụ mới |
| Soft delete | Giữ bản ghi/lịch sử và đổi trạng thái, không xóa vật lý hoặc xóa định nghĩa gói do một đăng ký hết hạn |
| TPS thành công mới | Số nghiệp vụ mới thực sự thành công mỗi giây, không tính replay và không tính phí nội bộ thành UC05 độc lập |

## 6. Ba mốc thời gian cần phân biệt

Dữ liệu lưu UTC; ngày lịch dùng Asia/Ho_Chi_Minh. Ngày đăng ký là ngày đầu: gói 30 ngày từ `2026-10-01T10:15:00+07:00` kết thúc `2026-10-31T00:00:00+07:00`. Khoảng `[start,end)` gồm start, không gồm end. Kỳ mới sau gián đoạn bắt đầu tại lúc xử lý hợp lệ, không tạo quyền ngược khoảng gián đoạn.

| Mốc | Ví dụ | Kết luận |
|---|---|---|
| recordedAt cho báo cáo | Ghi nhận 23:59:59 ngày 20, commit 00:00:01 ngày 21 | Thuộc ngày 20, chỉ đọc được sau commit; muộn sau bản chốt thì điều chỉnh |
| Kiểm hạn tài khoản | Dưới khóa kiểm 23:59:59, hạn 00:00, commit 00:00:01 | Có thể thành công theo CR-TIME-002 vì kiểm khi còn hạn |
| Receipt hủy | Đã biết intent commit, kiểm giờ mới lúc 00:00:02 trong khi coverageEnd 00:00 | Từ chối trễ nếu chưa có kết quả accepted đã chốt; không dùng giờ cũ của intent |

Nếu accepted đã commit nhưng chỉ mất HTTP, replay trả accepted cũ dù tra sau hạn. Không dùng một cột timestamp thay cả ba ý nghĩa trên hoặc gọi mọi mốc là committedAt.

## 7. Quy mô, kỹ thuật và trạng thái tài liệu

Tổng dữ liệu nghiệp vụ trước đo ≥ 500.000 bản ghi. Bài tải hỗn hợp yêu cầu đồng thời ≥ 30 TPS đăng ký gói và ≥ 30 TPS trừ tiền độc lập thành công mới; 50% số lần gọi trừ tiền kể cả retry vào 10 cặp nóng. Q32 quy định warm-up 2 phút, đo 10 phút, p95 ≤ 500 ms, p99 ≤ 1 giây, lỗi ≤ 0,1% và đối soát không còn request chưa rõ kết quả. Xem [kế hoạch kiểm thử](../05-testing/test-plan.md) để áp dụng đúng công thức, không suy đạt từ mục tiêu.

MySQL 8.0.46/InnoDB, READ COMMITTED cho đường ghi, utf8mb4, UTC DATETIME(6), bộ service tại nhà và IdP ngoài backend đã được chọn. Cách lưu UUID CHAR(36) là đề xuất theo Q14=C; schema/index/DTO/HTTP/scope/cấu hình và nhịp worker mới vẫn REVIEW. M1–M4 UC01 lịch sử không tự nghiệm thu delta MySQL hoặc UC02–UC06. Chưa có runtime/test/tải mới từ việc hợp nhất tài liệu.

## 8. Truy vết và lịch sử

- [Đề bài gốc](../requirement.md): giữ nguyên văn.
- [Overview](overview.md): mục tiêu, nghiệp vụ và tiêu chí tổng quan.
- [Sổ quyết định](clarification-log.md) và [biên nhận ngày 26/10](decision-receipt-20261026.md): lựa chọn, CR và phần còn mở.
- [SRS](../01-requirements/SRS.md), [UC01](../01-requirements/use-cases/UC01-Subscriber-Activation.md), [kiến trúc](../03-architecture/architecture.md): yêu cầu và mô hình chi tiết hơn.

Phiên bản 1.2 ngày 26/10 hợp nhất các quy tắc có hiệu lực, thay các đoạn cũ “M1 chờ duyệt”, “hủy cuối kỳ hiện tại” hoặc “báo cáo theo giờ commit” đã lỗi thời. Việc hợp nhất không xóa lịch sử quyết định và không tự nâng checkpoint.
