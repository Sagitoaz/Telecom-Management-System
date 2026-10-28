> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Kiến trúc backend

**Mã tài liệu:** DOC-ARCH-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

Kiến trúc nền M3 và thiết kế UC01 M4 đã được duyệt trong phạm vi lịch sử ngày 19/10/2026. Bản này đồng bộ các quyết định mới cho MySQL, UC01–UC06 và tác vụ nền; phần thiết kế bổ sung vẫn chờ rà soát. Tài liệu không chứng minh đã có backend chạy được hoặc đã đạt M5–M8. Nguồn quyết định hiện hành là [sổ quyết định](../00-project/clarification-log.md) và [biên nhận ngày 26/10](../00-project/decision-receipt-20261026.md).

## 1. Mục tiêu và ranh giới

TelecomManagement cung cấp REST API để hệ thống CSKH đấu nối thuê bao, đăng ký hoặc hủy gói, cộng tiền, trừ tiền và tra cứu báo cáo. Khách hàng thực hiện yêu cầu thông qua CSKH. Bên khai thác báo cáo và người vận hành có quyền riêng; scheduler và worker là thành phần nội bộ của backend.

Hệ thống dùng Java 21 và Spring Boot, tổ chức thành một ứng dụng chia module (modular monolith). Các nghiệp vụ cần tính nguyên tử được thực hiện trong cùng cơ sở dữ liệu quan hệ. Cách tổ chức này cho phép thu phí, ghi lịch sử và tạo kỳ gói trong một transaction, đồng thời giữ ranh giới trách nhiệm giữa các module để phát triển theo từng Use Case.

Phạm vi không gồm giao diện CSKH hoàn chỉnh, ứng dụng di động, kích hoạt mạng viễn thông thật, ngân hàng/cổng thanh toán, hệ thống xác minh giấy tờ hoặc billing/BI hoàn chỉnh. IdP cấp JWT nằm ngoài backend Telecom, kể cả khi Keycloak chạy cùng máy phát triển.

## 2. Thành phần và nguồn dữ liệu có thẩm quyền

| Thành phần | Trách nhiệm | Nguyên tắc khi lỗi |
|---|---|---|
| API adapter | Xác thực JWT, kiểm quyền, kiểm định dạng, chuyển DTO và mã HTTP | Không tự thay đổi số dư hoặc phát hành JWT |
| Dịch vụ ứng dụng | Điều phối Use Case, chống xử lý lặp, quản lý ranh giới transaction | Chỉ công bố thành công khi kết quả đã commit; thu phí gói không gọi vòng qua API trừ tiền độc lập |
| Lõi nghiệp vụ | Quy tắc cấp số, tiền, kỳ gói, hủy và báo cáo | Quy tắc không phụ thuộc HTTP, RabbitMQ hoặc Redis |
| MySQL 8.0.46 / InnoDB | Hồ sơ, kho số, request, số dư, sổ giao dịch, kỳ gói, ý định hủy và bản báo cáo | Là nguồn có thẩm quyền; DB lỗi không được trả thành công giả |
| RabbitMQ | Báo có số mới sau khi bổ sung kho đã commit | Thông điệp có thể mất hoặc lặp; worker đọc lại DB và có đường khôi phục định kỳ |
| Redis | Cache danh mục gói cho đường đọc | Cache miss hoặc Redis lỗi thì đọc DB; dữ liệu cache không quyết định số dư, cấp số hoặc giá thu phí |
| IdP / Keycloak | Cấp JWT và công bố khóa xác minh qua JWKS | Backend xác minh token; không bỏ xác thực khi không lấy được khóa hợp lệ |
| Scheduler và worker | Kích hoạt gia hạn, hết hạn, tổng hợp ngày và khôi phục UC01 | Dùng chung lõi nghiệp vụ và cơ chế chống xử lý trùng trong DB |
| k6 và công cụ đối soát | Phát tải, ghi nhận từng lần gọi và kiểm tra dữ liệu | Độc lập với runtime của API; mã 2xx hoặc phản hồi replay chưa đủ để tính một nghiệp vụ mới |

JPA phục vụ truy cập dữ liệu thông thường; JDBC được dùng khi cần kiểm soát rõ câu lệnh, khóa và cập nhật có điều kiện. Khi phối hợp hai cách truy cập, phải dùng cùng transaction manager và connection cho một nghiệp vụ nguyên tử. Cần xử lý trạng thái entity đã cache để tránh đọc số dư cũ sau cập nhật JDBC. Flyway là cơ chế thay đổi schema; không dùng `ddl-auto=update` thay migration.

## 3. Ranh giới nhất quán theo nghiệp vụ

### 3.1. UC01 — Đấu nối thuê bao

UC01 tiếp tục dùng mô hình Hybrid của `CR-UC01-001`. Khi đủ điều kiện, lời gọi trả kết quả cuối `SUCCEEDED` hoặc `FAILED`. Chỉ cấp số tự động hết kho và có sự đồng ý chờ mới được ghi `WAITING`, tối đa 24 giờ. Chọn số thất bại không tự chuyển sang chờ; `WAITING` chưa phải đấu nối thành công.

Bản ghi tiếp nhận request có thể được lưu trong transaction ngắn để hỗ trợ khôi phục. Transaction hoàn tất phải bảo đảm cấp đúng một số, liên kết Customer/giấy tờ hợp lệ, tạo Subscriber, tạo MAIN số dư 0 và chốt kết quả nhất quán. Khóa và ràng buộc duy nhất chống cấp trùng; việc tra cứu số khả dụng không giữ chỗ. Thuật toán FIFO/cấp ngẫu nhiên và thứ tự khóa cụ thể được mô tả trong [thiết kế đồng thời](../04-design/concurrency-and-jobs.md).

Sau khi bổ sung kho đã commit, RabbitMQ phát tín hiệu đánh thức worker. Khôi phục từ DB mỗi 60 giây vẫn hoạt động khi mất thông điệp. Hạn chờ không được đặt lại do retry hoặc restart.

### 3.2. UC02, UC04, UC05 và gia hạn

Đăng ký/gia hạn gói thu tiền từ MAIN. API cộng hoặc trừ tiền độc lập thao tác chính xác tài khoản được chỉ định; không lấy bù từ MAIN hay cộng gộp các tài khoản. Tiêu dùng phải tham chiếu một gói/kỳ hợp lệ thuộc đúng thuê bao. Tài khoản phụ cần có cấu hình loại, quyền cộng/trừ và hạn dùng rõ ràng; không bổ sung API tạo ví trong phạm vi này.

Trong đường ghi tiền, hệ thống kiểm điều kiện bằng giờ DB dưới khóa tài khoản ngay trước cập nhật. Nếu đã tới hạn thì từ chối. Nếu còn hạn tại lúc kiểm, transaction được phép commit sau hạn theo `CR-TIME-002`; không tuyên bố có giới hạn thời điểm commit vật lý. Số dư, giao dịch, kỳ gói/phí và kết quả request phải commit hoặc rollback cùng nhau.

Đăng ký thủ công dùng `expectedSubscriptionVersion`; phiên bản cũ bị từ chối mà không thu tiền. Worker và thao tác thủ công cùng nhắm một kỳ chỉ được thu một phí. Không tự nối thêm kỳ khác sau khi phát hiện bên kia đã mua kỳ đó. Đọc trạng thái gói hiện tại là API hỗ trợ cần thiết để bên gọi lấy đúng phiên bản.

### 3.3. UC03 — Hủy gói

Hủy được nhận hợp lệ khi đạt điều kiện receipt sau commit theo CR-CAN-003; việc lưu intent PENDING đơn thuần chưa phải accepted. Nhận hủy không chấm dứt quyền dùng ngay. Theo `CR-CAN-002`, thời điểm hiệu lực là cuối toàn bộ thời gian đã trả (`coverageEnd`), không hoàn phí. Từ khi nhận hủy hợp lệ, hệ thống chặn mua thêm và tự gia hạn; tới hạn mới chuyển sang `CANCELLED`.

Theo [CR-CAN-003](../00-project/CR-CAN-003-cancellation-receipt.md) đã được chủ dự án duyệt, TxA lưu ý định PENDING cùng version và coverageEnd đang quan sát, đồng thời chặn mua thêm/gia hạn trong lúc phân giải. Chưa trả accepted ở bước này. Sau khi biết chắc TxA đã commit, TxB khóa và lấy giờ DB mới làm receipt. Receipt phải nhỏ hơn observedCoverageEnd và version còn khớp mới nhận hủy, ghi PENDING_CANCEL/kết quả thành công nguyên tử rồi commit trước phản hồi. Bằng hạn hoặc sau hạn thì chốt FAILED với CANCELLATION_RECEIVED_TOO_LATE và giải phóng chặn tạm; không tự chuyển sang hủy kỳ mới. Timestamp trước commit và log HTTP đến không tạo quyền ưu tiên.

Nếu chưa biết TxA/TxB đã commit hay chưa, phải tra bằng kết nối mới và khóa request để phân giải, không đoán thất bại hoặc thực hiện lại hiệu ứng. Accepted đã commit nhưng mất phản hồi được replay kết quả/hiệu lực cũ, kể cả lúc tra đã qua hạn. Receipt đã chụp trước hạn có thể được TxB commit sau hạn; đây không phải hạn commit vật lý. Ngữ nghĩa đã được duyệt; thiết kế hai transaction và kiểm thử crash/receipt vẫn REVIEW. Chi tiết tại [thiết kế đồng thời](../04-design/concurrency-and-jobs.md).

### 3.4. UC06 và JOB-02 — Báo cáo

Mốc phân loại giao dịch là `recordedAt`: thời điểm nghiệp vụ ghi nhận trong transaction cuối cùng đã commit (`CR-TIME-001`). Ví dụ ghi nhận 23:59:59 ngày 20, commit 00:00:01 ngày 21 thì giao dịch thuộc ngày 20 và chỉ xuất hiện sau khi commit. Không đặt tên mốc này là giờ hoàn tất commit.

UC06 trả danh sách thuê bao không trùng trong tập kết quả, mặc định 50 và tối đa 200 thuê bao/trang. Bộ lọc gói OR chọn thuê bao có giao dịch của ít nhất một gói được chọn; AND yêu cầu có giao dịch của từng gói được chọn. includeFee phân biệt chỉ tiêu dùng với có thêm phí đăng ký/gia hạn; CREDIT không phải tiêu dùng. Không đếm một thuê bao thành nhiều dòng chỉ vì có nhiều giao dịch.

Một lượt xem báo cáo lưu tập thành viên/kết quả cố định. Các trang đọc cùng bản kết quả đó, không giữ transaction DB mở trong thời gian người dùng xem. Cursor được bảo vệ toàn vẹn, gắn với caller, bộ lọc và phiên bản bản kết quả.

JOB-02 lưu bền vững cutoff/tập nguồn lần đầu và giữ nguyên qua retry, kể cả khi lần đầu chưa hoàn tất. Tập nguồn phải được vật hóa từ một lần đọc nhất quán; không coi nhiều câu SELECT ở `READ COMMITTED` là một snapshot chung. Ngày đã chốt bất biến. Nguồn phát hiện muộn được ghi điều chỉnh ở kỳ sau, chỉ rõ ngày và giao dịch gốc; ràng buộc duy nhất theo nguồn ngăn đếm cả BASE lẫn ADJUSTMENT.

## 4. Quy tắc thời gian và vòng đời

Dữ liệu thời gian lưu UTC bằng `DATETIME(6)` theo quy ước thống nhất; API gửi mốc có độ lệch/múi giờ. Ngày nghiệp vụ và ngày của gói dùng `Asia/Ho_Chi_Minh`. Khoảng hiệu lực là `[start, end)`: có hiệu lực tại start và hết hiệu lực ngay tại end.

Ngày đăng ký là ngày đầu. Gói 30 ngày bắt đầu `2026-10-01T10:15:00+07:00` hết tại `2026-10-31T00:00:00+07:00`. Kỳ liên tiếp bắt đầu ở cuối kỳ trước và kéo dài số ngày lịch đã mua. Sau gián đoạn, kỳ mới bắt đầu tại lúc xử lý hợp lệ, không cấp quyền ngược hoặc truy thu nhiều kỳ đã bỏ lỡ. Giá, số ngày và phiên bản danh mục được lưu bất biến theo từng kỳ.

`coverageEnd` là cuối toàn bộ các kỳ đã trả; nó có thể khác cuối kỳ đang dùng. Ngừng bán mới không mặc nhiên cấm gia hạn của khách cũ: phải kiểm chính sách dành cho khách đang sử dụng và lưu giá/chính sách áp dụng cho kỳ mới.

## 5. Scheduler, worker và khôi phục

| Tác vụ | Lịch/vai trò đã chọn | Ràng buộc |
|---|---|---|
| Worker gia hạn | Kiểm tra thường xuyên gói đến hạn | Khoảng chạy là đề xuất cấu hình cần duyệt, không kế thừa 60 giây của UC01 |
| JOB-01 | 00:05 mỗi ngày, theo `Asia/Ho_Chi_Minh` | Hết hạn gói/tài khoản, hoàn tất hủy và rà soát phần bị bỏ sót; dùng cùng lõi gia hạn |
| JOB-02 | 00:30 mỗi ngày, theo `Asia/Ho_Chi_Minh` | Chốt tổng hợp ngày trước, giữ tập nguồn qua retry và xử lý điều chỉnh muộn |
| Khôi phục UC01 | Mỗi 60 giây theo quyết định đã có | Đọc request `PROCESSING`/`WAITING`, giữ thứ tự và hạn chờ, không phụ thuộc RabbitMQ |

Một lần chạy có `runId` để theo dõi, còn tính duy nhất nghiệp vụ dựa vào đối tượng/kỳ cần xử lý. Retry không được sinh một định danh nghiệp vụ ngẫu nhiên để thu lại tiền. Chạy tay và chạy theo lịch cùng dùng cơ chế nhận việc và lõi xử lý. API kiểm hạn ngay khi thực hiện nghiệp vụ; không đợi job đổi trạng thái rồi mới từ chối dữ liệu đã hết hạn.

## 6. Chống xử lý lặp và lỗi không rõ kết quả

UC01 có registry riêng vì có trạng thái chờ cấp số. UC02–UC05 dự kiến dùng registry chung, khóa theo `(caller, operation, requestId)`, lưu dấu vân tay nội dung chuẩn hóa và kết quả. Cùng khóa/cùng nội dung trả kết quả hiện có; khác nội dung trả `IDEMPOTENCY_CONFLICT`. UC03 cũng phải lưu kết quả hủy để retry an toàn.

UC02/04/05 giữ đầy đủ phản hồi 30 ngày sau trạng thái cuối; UC01/03 giữ 7 ngày. Sau đó giữ tombstone để không tái sử dụng mã thực hiện lại. Không dọn yêu cầu còn xử lý/chờ. Chính sách này khác thời gian lưu lịch sử nghiệp vụ.

Timeout hoặc mất phản hồi commit chỉ cho biết bên gọi chưa biết kết quả. Bên gọi tra cứu hoặc gửi lại cùng requestId; không tạo mã mới để đoán rằng nghiệp vụ đã thất bại. `UNKNOWN` là nhận định của bên quan sát, không phải trạng thái thành công trong DB.

## 7. Giới hạn, rủi ro và điều kiện kiểm chứng

MySQL 8.0.46/InnoDB, `READ COMMITTED` cho đường ghi, UTC, chuẩn số E.164 và bộ dịch vụ tại nhà là quyết định đã chọn. Cách lưu UUID `CHAR(36)` với so sánh ASCII nhị phân mới là đề xuất kỹ thuật theo Q14=C. DTO, mã HTTP, chỉ mục, batch, retry budget và thời gian lưu snapshot mới vẫn cần review.

Các rủi ro cần kiểm chứng gồm commit không rõ kết quả, hủy sát hạn, tranh chấp thủ công/worker, deadlock khi dùng chung tài khoản, dữ liệu báo cáo phát hiện muộn và cache cũ. Bản thiết kế không tự chứng minh đạt tải. [Kế hoạch kiểm thử](../05-testing/test-plan.md) phải được thực hiện trên runtime thật trước khi kết luận.

## 8. Tài liệu liên quan

- [C4 Context/Container](c4-diagrams/context-and-container.puml) và [cấu trúc module](module-structure.md).
- [Bảo mật và tích hợp](security-and-integration.md), [cấu hình runtime](runtime-profiles.md), [topology local](docker-local-topology.md).
- [Domain Model](../02-analysis/domain-model.puml), [ERD](../04-design/erd.puml), [OpenAPI](../04-design/openapi.yaml) và [thiết kế đồng thời](../04-design/concurrency-and-jobs.md).
- [Danh mục ADR](adr/README.md) và [hướng dẫn bàn giao](../06-deployment/deployment-guide.md).
