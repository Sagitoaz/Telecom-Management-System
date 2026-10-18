# Đặc tả yêu cầu phần mềm (Software Requirements Specification – SRS)

**Dự án:** Telecom Subscription Management System  
**Mã tài liệu:** DOC-REQ-001  
**Phiên bản:** 0.6 – Đồng bộ DEC-PRE-01–24, chờ nghiệm thu M1  
**Ngày lập:** 15/10/2026; cập nhật 18/10/2026  
**Trạng thái:** 🟡 Đã được chủ dự án duyệt **riêng phương án Hybrid cho UC01** (CR-UC01-001, 15/10/2026); toàn bộ SRS/UC01 và checkpoint M1 **chưa được nghiệm thu**  
**Giai đoạn:** Phase 1 – Software Requirements Specification  
**Vị trí thực tế:** `docs/01-requirements/SRS.md`  
**Phạm vi phiên bản:** Khung SRS toàn hệ thống theo quyết định lịch sử mục12 và24 lựa chọn mới DEC-PRE-01–24 tại clarification log mục13 ngày18/10/2026. Có bản UC01 v0.5 kèm theo để rà soát M1; **chưa** phải tài liệu được nghiệm thu, hợp đồng API hoặc thiết kế CSDL.

> **Thay đổi được duyệt – CR-UC01-001 (15/10/2026):** Chủ dự án chọn **Hybrid cho UC01** sau khi thảo luận các phương án. Đây là quyết định **thay thế DEC-REQ-11=B (bất đồng bộ cho mọi yêu cầu)** và **điều chỉnh DEC-REQ-10=B (không mặc định chờ số)**: xử lý và trả kết quả cuối đồng bộ khi có thể; chỉ tiếp nhận WAITING tối đa 24 giờ cho **cấp số tự động hết kho khi CSKH chuyển lựa chọn đồng ý chờ của khách hàng**. Mọi yêu cầu khác trả kết quả cuối khi xử lý xong; CSKH tự lưu/truy vấn requestId, nhân viên không cần nhập thủ công. Sổ quyết định chứa đầy đủ nội dung; phiếu cũ chỉ lưu lịch sử. Điều kiện chờ tự chọn và khi không chọn chờ trả lỗi ngay là cách cụ thể hóa hướng Hybrid vừa thống nhất, cần được rà soát tại M1.

> **Quy tắc sử dụng:** **Baseline Phase 0** là nội dung có căn cứ từ đề bài/DEC-01–DEC-11. **Lựa chọn SRS** là nội dung chủ dự án đã đánh dấu trong clarification log mục 12.2; cần phân biệt với mốc nghiệm thu toàn bộ SRS. **Open / Cần chốt** là chi tiết chưa đủ quyết định. Các phương án kỹ thuật chỉ được quyết định ở giai đoạn thiết kế tương ứng. Không đổi trạng thái P1.2–P1.8 thành hoàn thành chỉ vì chúng xuất hiện dưới dạng danh mục sơ bộ trong tài liệu này.

---

## 1. Giới thiệu (Introduction)

### 1.1. Mục đích (Purpose)

Tài liệu SRS xác định những yêu cầu mà hệ thống **TelecomManagement** cần đáp ứng, làm căn cứ cho phân tích nghiệp vụ, mô hình UML, thiết kế kiến trúc, thiết kế cơ sở dữ liệu, xây dựng REST API, kiểm thử và nghiệm thu.

SRS chuyển các yêu cầu đã thống nhất trong Phase 0 thành những phát biểu có mã và có thể truy vết. Chi tiết về đầu vào, đầu ra, luồng ngoại lệ và tiêu chí nghiệm thu được bổ sung theo từng Use Case; ưu tiên **UC01 – Đấu nối thuê bao**.

### 1.2. Phạm vi sản phẩm (Product Scope)

Hệ thống là một **backend** quản lý khách hàng, thuê bao, gói cước, lần đăng ký gói, tài khoản dịch vụ, giao dịch cộng/trừ tiền và các xử lý định kỳ. Các hệ thống bên ngoài, đặc biệt là hệ thống chăm sóc khách hàng (CSKH), sử dụng REST API để gửi yêu cầu và nhận kết quả.

Hệ thống phải bảo đảm sự nhất quán của dữ liệu trong những tình huống có yêu cầu đồng thời, đồng thời cung cấp khả năng truy vấn/báo cáo và công cụ kiểm thử, đối soát theo phạm vi đã thống nhất.

**Ngoài phạm vi của bản triển khai ban đầu:** giao diện CSKH hoàn chỉnh, ứng dụng khách hàng, tích hợp hạ tầng mạng viễn thông thật, cổng thanh toán/ngân hàng, hệ thống tính cước hoàn chỉnh và nền tảng BI hoàn chỉnh (EX-01–EX-06).

### 1.3. Đối tượng đọc tài liệu (Intended Audience)

| Đối tượng | Cách sử dụng tài liệu |
|---|---|
| Chủ dự án / người nghiệm thu | Rà soát yêu cầu, chốt các câu hỏi còn mở, xác nhận tiêu chí nghiệm thu |
| Người phân tích và thiết kế | Lập Use Case Specification, UML, kiến trúc, mô hình dữ liệu và hợp đồng API |
| Người phát triển | Triển khai đúng quy tắc nghiệp vụ và các yêu cầu phi chức năng |
| Người kiểm thử | Xây dựng test case, dữ liệu kiểm thử, kiểm thử tải và đối soát |

### 1.4. Thuật ngữ và quy ước (Definitions & Conventions)

- **Customer:** khách hàng; một khách hàng có thể sở hữu nhiều thuê bao.
- **Subscriber:** thuê bao đã được đấu nối; tại một thời điểm thuộc về một khách hàng.
- **MSISDN / Subscriber Number:** số thuê bao cần quản lý tình trạng khả dụng và chống cấp trùng.
- **Service Package:** định nghĩa sản phẩm gói cước.
- **Package Subscription:** một lần đăng ký sử dụng gói cước, có chu kỳ và trạng thái.
- **Account:** tài khoản dịch vụ có số dư; không phải tài khoản đăng nhập.
- **Business Transaction:** bản ghi nghiệp vụ cộng/trừ tiền; khác với database transaction.
- **Soft Delete:** vô hiệu hóa bằng trạng thái/đánh dấu thay vì xóa vật lý lịch sử cần giữ.
- **FR / BR / NFR:** Functional Requirement / Business Rule / Non-Functional Requirement.
- **REQ / DEC / OQ:** mã yêu cầu gốc / quyết định baseline / câu hỏi chưa chốt.
- **AC:** Acceptance Criteria – điều kiện nghiệm thu.
- **Baseline:** nội dung đã có căn cứ được duyệt; **Open:** còn chi tiết chưa được quyết định.

Thuật ngữ chi tiết và phân biệt các khái niệm tham chiếu tại [`scope-and-glossary.md`](../00-project/scope-and-glossary.md).

### 1.5. Tài liệu tham chiếu (References)

| Mã | Tài liệu | Vai trò |
|---|---|---|
| REF-01 | [`requirement.md`](../requirement.md) | Đề bài gốc; giữ nguyên để đối chiếu |
| REF-02 | [`overview.md`](../00-project/overview.md) | Mục tiêu, bối cảnh và định hướng hệ thống đã duyệt |
| REF-03 | [`scope-and-glossary.md`](../00-project/scope-and-glossary.md) | Phạm vi, ranh giới và thuật ngữ |
| REF-04 | [`clarification-log.md`](../00-project/clarification-log.md) | Nguồn chính của các quyết định DEC và trạng thái OQ |
| REF-05 | [`ROADMAP.md`](../ROADMAP.md) | Trình tự công việc, mốc kiểm tra và điều kiện chuyển Phase |
| REF-06 | Clarification log, mục 12.2 | Đầy đủ 31 quyết định yêu cầu, mã DEC-REQ-01–31 |
| REF-07 | Clarification log, mục 12.3 | Đầy đủ 5 quyết định UC01, mã DEC-UC01-01–05 |
| REF-08 | `CR-UC01-001` trong [`clarification-log.md`](../00-project/clarification-log.md) | Quyết định chọn Hybrid, có hiệu lực thay thế một phần các lựa chọn clarification log mục 12.2 |

Khi phát hiện mâu thuẫn giữa bản SRS nháp và baseline Phase 0, cần ghi nhận để rà soát; **không tự sửa nội dung đã được duyệt**. Quyết định bổ sung phải được lưu vào clarification log, ghi rõ ngày và người duyệt trước khi xem là có hiệu lực.

---

## 2. Mô tả tổng quan (Overall Description)

### 2.1. Bối cảnh và ranh giới hệ thống (Product Perspective)

TelecomManagement là backend xử lý nghiệp vụ và lưu trữ dữ liệu của hệ thống quản lý thuê bao. Hệ thống CSKH là thành phần bên ngoài tiếp nhận yêu cầu của khách hàng và gọi REST API. Thành phần lập lịch (scheduler) và cơ sở dữ liệu thuộc phạm vi nội bộ; không xem scheduler là actor nghiệp vụ bên ngoài chỉ để mô tả các job.

**Đầu vào chính:** yêu cầu đấu nối, đăng ký/hủy gói, cộng/trừ tiền, tham số truy vấn báo cáo và kích hoạt các job theo lịch.  
**Đầu ra chính:** kết quả xử lý nghiệp vụ, dữ liệu tra cứu, bản ghi giao dịch, trạng thái và kết quả chạy job, bằng chứng kiểm thử/đối soát.

### 2.2. Các nhóm người dùng và hệ thống liên quan (User Classes & External Systems)

| Tác nhân / thành phần | Vai trò và tương tác |
|---|---|
| Khách hàng viễn thông | Cung cấp hồ sơ và yêu cầu sử dụng dịch vụ thông qua CSKH; không trực tiếp gọi backend |
| Hệ thống CSKH | Gọi các REST API nghiệp vụ; nhận kết quả và thông tin lỗi |
| Bộ phận / hệ thống báo cáo | Khai thác kết quả truy vấn, báo cáo theo phương thức tích hợp sẽ được chốt |
| Nhân sự phát triển / kiểm thử / vận hành | Cấu hình, kiểm thử, kiểm tra hoạt động và kết quả xử lý định kỳ |

### 2.3. Các nhóm chức năng (Product Functions)

| Nhóm | Nội dung chính | Liên hệ phạm vi |
|---|---|---|
| Quản lý thuê bao | Hồ sơ khách hàng, tra cứu/cấp số, đấu nối | SC-01 |
| Đăng ký gói | Đăng ký, thu phí đồng nhất, quản lý thời hạn | SC-02 |
| Hủy gói | Ghi nhận yêu cầu hủy có hiệu lực cuối chu kỳ | SC-03 |
| Tài khoản và giao dịch | Cộng tiền, trừ tiền, cập nhật số dư và lịch sử | SC-04 |
| Tra cứu và báo cáo | Tra cứu tiêu dùng trong khoảng X–Y theo gói; tổng hợp hàng ngày | SC-05 |
| Xử lý định kỳ | Cập nhật trạng thái đăng ký gói và tài khoản hết hạn | SC-06 |
| Kiểm thử và đối soát | Dữ liệu lớn, tải đồng thời, xác minh từng request/giao dịch | REQ-07–REQ-10 |

### 2.4. Các nhóm dữ liệu nghiệp vụ (Business Data)

Tối thiểu xét các khái niệm nghiệp vụ: `Customer`, `Subscriber`, `ServicePackage`, `PackageSubscription`, `Account`, `Transaction`. Hệ thống còn cần quản lý tập số thuê bao khả dụng; **chưa** quyết định biểu diễn bằng bảng riêng `PhoneNumberInventory` hay cấu trúc khác.

Quan hệ đã duyệt: `Customer` 1–N `Subscriber`; mỗi `Subscriber` thuộc một `Customer` tại một thời điểm; thuê bao có nhiều đăng ký gói và nhiều tài khoản; mỗi tài khoản có nhiều giao dịch. Chi tiết thuộc tính, khóa và ràng buộc CSDL để dành cho mô hình phân tích và thiết kế.

### 2.5. Ràng buộc, phụ thuộc và giả định (Constraints, Dependencies & Assumptions)

- Dữ liệu được quản lý bằng cơ sở dữ liệu quan hệ; các hoạt động nghiệp vụ được cung cấp qua REST API.
- Định hướng triển khai hiện tại theo Roadmap: Java 21, Spring Boot, PostgreSQL và migration; lựa chọn kiến trúc/kỹ thuật cụ thể phải được xác nhận ở Phase 3–4.
- Đấu nối trong phạm vi **backend**, không bao gồm kích hoạt số trên hạ tầng mạng thực.
- Các lựa chọn về tài khoản, phí đăng ký, JWT, tái sử dụng số và job được ghi theo clarification log mục 12.2; 5 quy tắc ưu tiên của UC01 được bổ sung theo clarification log mục 12.3. **Chi tiết chưa có quyết định vẫn Open**, như thời gian cách ly số cũ, cách so sánh/chuẩn hóa hồ sơ, cấu hình scope/quyền JWT và giờ chạy job.
- Các yêu cầu chưa rõ được quản lý bằng OQ tại mục 9 và bản gốc `clarification-log.md`.

---

## 3. Yêu cầu chức năng (Functional Requirements – FR)

> **Tình trạng mục này:** Danh mục khởi tạo phục vụ P1.1/P1.2. Mỗi dòng mô tả hành vi theo baseline; **chưa phải** hợp đồng API hoàn chỉnh. Với các yêu cầu phụ thuộc OQ, cần bổ sung trường dữ liệu, tiền/hậu điều kiện, thông báo lỗi và AC chi tiết ở Use Case tương ứng.

### 3.1. Quản lý khách hàng và đấu nối thuê bao (SC-01 / UC01)

| Mã | Yêu cầu | Đầu vào / đầu ra ở mức nghiệp vụ | Nguồn / trạng thái |
|---|---|---|---|
| FR-SUB-01 | Bắt buộc họ tên, ngày sinh, loại và số giấy tờ; địa chỉ/nghề nghiệp/liên hệ tùy chọn. Giấy tờ có tham chiếu chứng cứ xác minh từ quy trình ngoài. | Hồ sơ → kiểm tra hợp lệ | DEC-REQ-01/02; DEC-PRE-01/02. Ghi chú ít nhất một trường giấy tờ không tự bỏ loại+số. |
| FR-SUB-02 | Hệ thống cho tra cứu số AVAILABLE; tra cứu không giữ chỗ. | Bộ lọc → danh sách số | DEC-02; DEC-REQ-07=A |
| FR-SUB-03 | Hỗ trợ chọn số AVAILABLE để đấu nối; khi xử lý hoàn tất, API trả ngay thành công hoặc thất bại trong cùng request HTTP. | Hồ sơ, số chọn, requestId → kết quả cuối | DEC-02; CR-UC01-001 (thay DEC-REQ-11=B cho UC01) |
| FR-SUB-04 | Hỗ trợ tự cấp ngẫu nhiên một số AVAILABLE từ kho nạp/sinh bổ sung. Có số và đủ điều kiện thì hoàn tất, phản hồi ngay; hết số áp dụng FR-SUB-10. | Hồ sơ, requestId, lựa chọn chờ → SUCCEEDED / FAILED / WAITING hợp lệ | DEC-02; DEC-REQ-06=C/DEC-REQ-09=B; CR-UC01-001 |
| FR-SUB-05 | Mỗi số chỉ được cấp thành công cho tối đa một thuê bao ở một thời điểm, kể cả khi yêu cầu chồng lấn. | Tranh chấp → một kết quả thành công tối đa | DEC-02; xử lý nguyên tử ở Phase 4 |
| FR-SUB-06 | Khi số được CSKH chọn đã bị cấp, yêu cầu chọn số trả lỗi nghiệp vụ, không tự đổi sang số khác. | Số chọn không còn AVAILABLE → FAILED | DEC-02 |
| FR-SUB-07 | Dùng lại Customer theo giấy tờ đã xác minh/liên kết khi mọi trường được gửi khớp sau chuẩn hóa; khác bất kỳ trường đã gửi thì từ chối, không ghi đè. Customer mới cần chứng cứ xác minh ngoài; liên kết thêm giấy tờ vào Customer cũ ngoài UC01. Không tự ghép theo tên/ngày sinh. | Hồ sơ/chứng cứ → Customer hợp lệ hoặc từ chối | DEC-UC01-03; DEC-PRE-01/02. Normalize/absent/null cụ thể còn P4; cần rà soát cách hiểu ở M1. |
| FR-SUB-08 | Khi đấu nối thành công, tạo một tài khoản tiền chính với số dư ban đầu bằng 0, không có hạn mặc định. | Subscriber thành công → Account mặc định | DEC-REQ-05=A, DEC-REQ-16=B, DEC-REQ-17=A |
| FR-SUB-09 | **Hybrid:** kết quả đấu nối hoàn tất được trả **đồng bộ** (`SUCCEEDED`/`FAILED`). Chỉ với cấp số tự động hết kho và khách hàng đã đồng ý chờ, backend nhận yêu cầu `WAITING` và xử lý nền. CSKH có thể tự tra cứu theo requestId mà không cần nhập mã thủ công. | Gọi API → kết quả cuối hoặc WAITING (không phải thành công); tra cứu → trạng thái cuối | CR-UC01-001; **supersedes DEC-REQ-11=B** |
| FR-SUB-10 | Khi tự cấp hết số: **không đồng ý chờ** → trả `NO_AVAILABLE_NUMBER`, không tạo Subscriber/Account và không lưu WAITING; **đồng ý chờ rõ ràng** → ghi `WAITING` tối đa 24 giờ từ lúc chuyển trạng thái, khi hết hạn `EXPIRED`, có thể gửi yêu cầu mới. Không bắt buộc khách hàng chờ. | Hết kho + lựa chọn chờ → FAILED ngay hoặc WAITING → SUCCEEDED/EXPIRED | CR-UC01-001 điều chỉnh DEC-REQ-10=B; DEC-UC01-01 cho nhánh chờ |
| FR-SUB-11 | Duy nhất theo caller ứng dụng + UC01 + requestId; cùng canonical payload không tác động lần hai, khác payload trả IDEMPOTENCY_CONFLICT. So hồ sơ/mode/số chọn/consent đã chuẩn hóa, bỏ JWT/correlationId/thứ tự JSON. Kết quả đầy đủ giữ 7 ngày sau trạng thái cuối; không dọn đang xử lý/WAITING; sau đó giữ tombstone/fingerprint, không tái dùng ID. | Retry → replay trong hạn hoặc kết quả theo chính sách tombstone, không tác động mới | DEC-PRE-05–07. Schema canonical và HTTP sau replay window còn P4. |
| FR-SUB-12 | Phản hồi nghiệp vụ có mã kết quả/lỗi, thông báo, requestId, dữ liệu kết quả (nếu có) và correlationId phục vụ log. | API response → dữ liệu chuẩn hóa | DEC-REQ-15=C; schema/HTTP status ở Phase 4 |
| FR-SUB-13 | Một số từng được cấp có thể được tái sử dụng sau khoảng chờ và quy trình giải phóng hợp lệ; **chưa đủ quy tắc để thiết kế/triển khai tái cấp** cho đến khi bổ sung vòng đời ngừng thuê bao. | Số từng dùng → chỉ AVAILABLE khi đủ điều kiện | DEC-REQ-08=B; **Open** thời hạn và mô hình giải phóng |
| FR-SUB-14 | Bổ sung số kích hoạt xử lý WAITING hợp lệ theo FIFO; ưu tiên WAITING trước yêu cầu tự cấp mới khi có số bổ sung. Recovery mỗi 60 giây, không reset hạn 24h; kiểm hạn khi hoàn tất. | Bổ sung kho/recovery → SUCCEEDED hoặc EXPIRED; không trùng | DEC-PRE-08/10. Tie-break FIFO/claim tại P4. RabbitMQ hỗ trợ tín hiệu theo CR-ARCH-001. |
| FR-SUB-15 | JWT ký bất đối xứng từ hệ thống xác thực local ngoài Telecom; backend lấy public key qua JWKS cấu hình, kiểm chữ ký/issuer/audience/expiry/scope và quyền caller với request. Không cấp JWT trong Telecom. | JWT → cho phép/từ chối | DEC-PRE-04; issuer/JWKS/claim map cụ thể P3–4. |

**Kiểm chứng UC01:** AC-SUB-01..27 trong bản đặc tả UC01 v0.5; các quyết định nghiệp vụ tương ứng đã có lựa chọn. **Cần rà soát/duyệt tiêu chí nghiệm thu** trước khi đánh dấu P1.5/M1; chi tiết kỹ thuật thuộc Phase 3–4.

### 3.2. Đăng ký và hủy gói cước (SC-02, SC-03 / UC02, UC03)

| Mã | Yêu cầu | Đầu vào / đầu ra ở mức nghiệp vụ | Căn cứ / còn mở |
|---|---|---|---|
| FR-PKG-01 | Đơn giá gói tại lúc đăng ký/gia hạn được chụp và giữ trong lịch sử lần thu; không sửa khoản phí đã commit khi danh mục đổi giá. | Thuê bao/gói → giá snapshot/kết quả | DEC-PRE-12; giá mẫu/biểu diễn snapshot trước UC02. |
| FR-PKG-02 | Chu kỳ theo số ngày gói; đăng ký lại khi còn hiệu lực nối từ hạn hiện tại; chờ hủy không được nối dài. Không tạo hai đăng ký song song để diễn giải nối kỳ. | Đăng ký lại → hạn mới hoặc lỗi | DEC-PRE-13; định nghĩa ngày và ca đã hết hạn trước UC02. |
| FR-PKG-03 | Thu phí từ tài khoản tiền chính của thuê bao; chỉ xác nhận đăng ký có hiệu lực khi thu phí và đăng ký cùng thành công. | Gói, tài khoản chính → kết quả nhất quán | DEC-03; DEC-REQ-20=A |
| FR-PKG-04 | Khi lỗi một bước, không còn đăng ký hoạt động hoặc khoản trừ tiền thành công một phần. | Lỗi → rollback/không tác động một phần | DEC-03 |
| FR-PKG-05 | Hủy có hiệu lực cuối chu kỳ; yêu cầu mới khi đã chờ hủy trả lỗi, retry cùng ID trả kết quả gốc. Hủy đã tiếp nhận hợp lệ trước cuối kỳ phải ngăn gia hạn. | Yêu cầu hủy → chờ hủy/lỗi | DEC-PRE-14; mốc tiếp nhận bền vững và ca đúng biên trước UC03/job. |
| FR-PKG-06 | Hủy gói không làm dừng quyền dùng gói trước khi kết thúc chu kỳ hiện hành. | Yêu cầu hủy → tiếp tục hiệu lực đến cuối chu kỳ | DEC-04 |
| FR-PKG-07 | Tự gia hạn chỉ khi đủ tiền và chưa chờ hủy. Thiếu tiền khi hết kỳ thì hết hiệu lực, không grace ngầm; không cộng kỳ/thu phí hai lần khi cạnh tranh đăng ký lại. | Cuối kỳ → gia hạn nguyên tử hoặc hết hiệu lực | DEC-PRE-12–14; retry lỗi kỹ thuật và thứ tự job còn thiết kế. |

**Lưu ý:** DEC-REQ-21=B và DEC-REQ-22=B được ghi là hai tình huống khác nhau (gia hạn tự động và chủ động đăng ký lại). Đã chọn hướng ở DEC-PRE-13/14; cách thực hiện không cộng trùng còn thiết kế trước UC02/UC03.

### 3.3. Tài khoản và giao dịch (SC-04 / UC04, UC05)

| Mã | Yêu cầu | Đầu vào / đầu ra nghiệp vụ | Căn cứ / còn mở |
|---|---|---|---|
| FR-ACC-01 | Một tài khoản chính khi UC01 thành công: 0 VND, không hạn mặc định. Phụ theo loại dịch vụ cấu hình, tạo bằng seed/quy trình riêng; không tự thêm API mở tài khoản. | Subscriber → account chính/phụ | DEC-PRE-16; loại/hạn phụ chưa điền. |
| FR-ACC-02 | Cộng tiền hợp lệ làm tăng số dư và lưu lịch sử; số tiền theo đơn vị VND nguyên và >0. | Tài khoản, tiền → số dư/giao dịch | DEC-REQ-24=A; OQ-10 còn mở về hạn mức |
| FR-ACC-03 | Trừ tiền chỉ thành công nếu đủ số dư, không để số dư âm. | Tài khoản, tiền → kết quả | DEC-05; DEC-REQ-24=A |
| FR-ACC-04 | Trừ tiền thiếu tiền không đổi số dư và không ghi giao dịch thành công. | Thiếu tiền → lỗi nghiệp vụ | DEC-05 |
| FR-ACC-05 | Cộng/trừ đồng thời phải giữ đúng số dư và lịch sử đã commit. | Nhiều yêu cầu → dữ liệu nhất quán | DEC-11 |
| FR-ACC-06 | Lệnh tiền áp dụng cùng nguyên tắc UC01: caller+nghiệp vụ+ID, canonical payload, một tác động, khác nội dung conflict. Phí nội bộ liên kết đăng ký, không là debit API độc lập. | Retry → kết quả gốc, không cộng/trừ lại | DEC-PRE-15; retention tiền riêng được yêu cầu đề xuất, chưa duyệt số ngày. Xem review-guide. |
| FR-ACC-07 | Tài khoản hết hạn bị vô hiệu hóa giao dịch mới; giữ nguyên số dư và lịch sử để đối soát. | Account hết hạn → chặn giao dịch mới | DEC-REQ-27=A; Open xử lý số dư về sau |

### 3.4. Tra cứu và báo cáo (SC-05 / UC06)

| Mã | Yêu cầu | Đầu vào / đầu ra nghiệp vụ | Căn cứ / còn mở |
|---|---|---|---|
| FR-RPT-01 | Truy vấn [X,Y) theo thời điểm commit; OR/AND và includeFee phải chỉ rõ; chỉ giao dịch thành công; danh sách thuê bao không trùng, cursor pagination, scope báo cáo riêng. | X,Y,gói,filters,cursor → danh sách | DEC-PRE-17/18; page size/claim cụ thể P4 UC06. |
| FR-RPT-02 | Bộ lọc includeFee bắt buộc quyết định có tính phí đăng ký; mỗi debit tiêu dùng có gói/ngữ cảnh dịch vụ xác định, phí liên kết lần đăng ký; chưa mặc định mọi debit phải có FK gói trực tiếp. | Bộ lọc và ngữ cảnh → tập tiêu dùng | DEC-PRE-17/18; xử lý ngữ cảnh không ánh xạ được gói cần thiết kế trước UC06. |
| FR-RPT-03 | Lưu lịch sử nghiệp vụ 5 năm cấu hình; bản demo không chạy purge, giữ dữ liệu phục vụ báo cáo/đối soát. | Truy vấn → lịch sử | DEC-PRE-21. Khác retention kết quả idempotency 7 ngày. |
| FR-RPT-04 | Job tổng hợp tiêu dùng hàng ngày lưu bảng tổng hợp và hỗ trợ xuất file tùy chọn. | Dữ liệu kỳ → bảng/file | DEC-07; DEC-REQ-29=C |

### 3.5. Xử lý định kỳ và hỗ trợ kiểm chứng (SC-06 / REQ-07–REQ-10)

| Mã | Yêu cầu | Đầu vào / đầu ra ở mức nghiệp vụ | Nguồn / còn mở |
|---|---|---|---|
| FR-JOB-01 | Job định kỳ kiểm tra lần đăng ký gói và tài khoản hết hạn. | Thời điểm xử lý → đối tượng hết hạn | DEC-06; OQ-07/11 |
| FR-JOB-02 | Cập nhật trạng thái/soft delete giữ lịch sử, không xóa danh mục gói. | Hết hạn → không thể dùng/giữ lịch sử | DEC-06 |
| FR-JOB-03 | Mặc định Asia/Ho_Chi_Minh: hết hạn 00:05, tổng hợp 00:30 mỗi ngày; từng lịch cấu hình/chạy tay. Không kéo dài quyền dùng chỉ vì job chạy trễ. | Lịch/kích hoạt → run log | DEC-PRE-19; cách gia hạn kịp biên cần thiết kế riêng. |
| FR-JOB-04 | Tổng hợp ngày trước: count giao dịch thành công và tổng VND theo ngày/thuê bao/gói/loại phí; lưu DB, CSV tùy chọn. Cutoff lúc chốt; dữ liệu muộn có ID điều chỉnh kỳ sau, không đúp. | Kỳ/điều chỉnh → bảng/file | DEC-PRE-20; transaction chốt kỳ/batch còn P4. |
| FR-TST-01 | Công cụ sinh dữ liệu, phát tải, đo yêu cầu đồng thời theo ngưỡng đã chốt. | Kịch bản tải → log/kết quả | DEC-08–10 |
| FR-TST-02 | Công cụ đối soát từng request/giao dịch đã commit và số dư. | Log + DB → sai lệch xác định | DEC-11 |

## 4. Quy tắc nghiệp vụ (Business Rules – BR)

> Bảng dưới ghi các quy tắc đã được quyết định. Các chỗ chưa có quy tắc cụ thể tiếp tục tham chiếu OQ, không xem phương án được gợi ý là quyết định mặc định.

| Mã | Quy tắc | Căn cứ | Chi tiết còn mở |
|---|---|---|---|
| BR-01 | Một `Customer` có thể có nhiều `Subscriber`; một `Subscriber` thuộc đúng một `Customer` tại một thời điểm. | DEC-01 | OQ-08 Resolved |
| BR-02 | Có cấp số tự động và chọn số khả dụng; chỉ xem số không đồng nghĩa giữ chỗ. | DEC-02 | OQ-05: tái sử dụng/trạng thái |
| BR-03 | Một số không được cấp thành công đồng thời cho nhiều thuê bao; yêu cầu chọn số đã bị cấp phải bị từ chối. | DEC-02 | OQ-05: chi tiết nguyên tử ở thiết kế |
| BR-04 | Khi đăng ký gói có thu phí, chỉ có hiệu lực khi cả đăng ký và thu phí thành công; không để lại thành công một phần. | DEC-03 | OQ-06: tài khoản, mức phí, retry |
| BR-05 | Hủy gói có hiệu lực ở cuối chu kỳ hiện tại. | DEC-04 | OQ-07/09: trạng thái, tính chu kỳ, đăng ký lại |
| BR-06 | Khi không đủ tiền, trừ tiền thất bại, số dư không thay đổi và không có giao dịch tài chính thành công. | DEC-05 | OQ-10: số tiền hợp lệ, đơn vị, tài khoản hết hạn |
| BR-07 | Lần đăng ký gói và tài khoản hết hạn được vô hiệu hóa bằng trạng thái/soft delete; không xóa vật lý lịch sử cần giữ hoặc định nghĩa gói vì một lần đăng ký hết hạn. | DEC-06 | OQ-11: lưu trữ/số dư còn lại |
| BR-08 | Ngoài API tra cứu và job hết hạn, phải có job tổng hợp tiêu dùng hàng ngày riêng. | DEC-07 | OQ-13: chỉ tiêu, đầu ra, chạy lại |
| BR-09 | Khi tính TPS, chỉ đếm nghiệp vụ mới hoàn tất thành công; không đếm retry trả kết quả cũ như nghiệp vụ mới. | DEC-09 | OQ-04: phép đo chi tiết |
| BR-10 | Giao dịch được đối soát theo từng yêu cầu và tác động đã commit; retry không được tạo thêm tác động. | DEC-11 | OQ-02/10/14: định danh, timeout, đối soát |

### 4.1. Business Rules bổ sung từ clarification log mục 12.2

Các BR dưới đây **bổ sung** DEC-01–11, không thay thế baseline Phase 0.

| Mã | Quy tắc đã chọn | Nguồn | Giới hạn cần chốt |
|---|---|---|---|
| BR-11 | Bắt buộc tên/ngày sinh/loại+số giấy tờ; CCCD, CMND/CMT, hộ chiếu; CSKH cung cấp tham chiếu chứng cứ ngoài. | DEC-PRE-01/02 | Không tự hạ yêu cầu thành chỉ một trường giấy tờ; rà soát M1. |
| BR-12 | Mọi trường hồ sơ được gửi phải khớp sau chuẩn hóa, khác trường nào thì từ chối, không tự ghi đè. | DEC-PRE-01 | Normalize/absent/null cụ thể P4; không tự áp chuẩn hóa của phương án Q01-A chưa chọn. |
| BR-13 | Thành công UC01 thì tạo tài khoản chính số dư 0, không hạn mặc định. | DEC-REQ-05=A, DEC-REQ-16=B, DEC-REQ-17=A | Loại tài khoản phụ về sau |
| BR-14 | Kho số nhận nạp/sinh số, cấp từ tập xác nhận AVAILABLE; tự động chọn ngẫu nhiên. | DEC-REQ-06=C, DEC-REQ-07=A, DEC-REQ-09=B | Quy tắc sinh/số hợp lệ ở thiết kế |
| BR-15 | Số từng cấp có thể tái sử dụng sau thời gian chờ; **không coi ASSIGNED đã ngừng là AVAILABLE ngay**. | DEC-REQ-08=B | Chưa có thời gian chờ và quy trình giải phóng |
| BR-16 | Hết kho khi cấp tự động: CSKH **không chọn chờ** thì từ chối ngay; chỉ khi khách hàng đồng ý chờ mới ghi `WAITING` tối đa 24 giờ, không coi WAITING là thành công; quá hạn `EXPIRED`. | CR-UC01-001 điều chỉnh DEC-REQ-10=B; DEC-UC01-01 | Truyền và lưu lựa chọn chờ, mốc tính hạn; API schema ở Phase 4 |
| BR-17 | **Hybrid:** trả kết quả cuối trong request HTTP nếu hoàn tất ngay; chỉ nhánh được phép chờ trả `WAITING` rồi xử lý nền. Retry cùng requestId/payload không cấp thêm; payload khác trả `IDEMPOTENCY_CONFLICT`; có API tra cứu và correlationId. | CR-UC01-001 thay DEC-REQ-11=B; DEC-REQ-13=A/DEC-REQ-14=A/DEC-REQ-15=C; DEC-UC01-05 | Phạm vi idempotency, status/HTTP schema Phase 4 |
| BR-18 | Thu từ chính theo giá snapshot của từng lần đăng ký/gia hạn, không sửa phí đã commit. | DEC-PRE-12; DEC-03 | Giá mẫu/schema và rollback/retry trước UC02. |
| BR-19 | Chu kỳ số ngày; còn hiệu lực nối từ hạn cũ; chờ hủy chặn nối/gia hạn; thiếu tiền cuối kỳ hết hiệu lực không grace. Hủy nhận trước cuối kỳ ngăn gia hạn. | DEC-PRE-13/14 | Mốc tiếp nhận/ca biên và lỗi kỹ thuật trước UC02/03. |
| BR-20 | Tiền VND nguyên dương, kiểm tràn; ID lệnh tiền theo cùng chính sách UC01, khác payload conflict. | DEC-PRE-15/16 | Hạn mức và retention tiền chưa chốt số cụ thể. |
| BR-21 | Báo cáo [X,Y) theo commit, OR/AND và includeFee bắt buộc; cursor/danh sách thuê bao không trùng, scope riêng. | DEC-PRE-17/18 | Schema attribution và phân trang trước UC06. |
| BR-22 | Hết hạn chặn giao dịch mới, giữ số dư; lịch sử 5 năm cấu hình, demo không purge; kỳ chốt một lần, muộn điều chỉnh kỳ sau. | DEC-PRE-20/21 | Cơ chế archive/chốt kỳ. |
| BR-23 | Timezone Asia/Ho_Chi_Minh; JOB-01 00:05, JOB-02 00:30, lịch riêng cấu hình/chạy tay; không gia hạn quyền dùng vì job trễ. | DEC-PRE-19 | Thiết kế gia hạn kịp biên. |
| BR-24 | WAITING FIFO ưu tiên tự cấp mới khi có số bổ sung; recovery 60s, giữ deadline gốc và kiểm hạn hoàn tất. | DEC-PRE-08/10 | Tie-break FIFO, claim/race. |
| BR-25 | Giấy tờ đã xác minh có chứng cứ ngoài; dùng liên kết cũ hoặc tạo Customer mới hợp lệ, không tự ghép khách; thêm giấy tờ khách cũ ngoài UC01. | DEC-PRE-02 | Kiểm chứng chứng cứ/schema P4. |
| BR-26 | JWT bất đối xứng từ IdP local ngoài Telecom, JWKS cấu hình; kiểm issuer/audience/expiry/scope/ownership. | DEC-PRE-04 | URL/key rotation/claims cụ thể. |
| BR-27 | requestId có scope caller+UC01; canonical payload khác thì conflict; replay đầy đủ 7 ngày sau trạng thái cuối, sau đó tombstone chống tái dùng. | DEC-PRE-05–07 | Schema fingerprint và HTTP tombstone. |

---

## 5. Yêu cầu phi chức năng (Non-Functional Requirements – NFR)

| Mã | Nhóm | Yêu cầu / tiêu chí kiểm chứng ở mức hiện tại | Trạng thái / phần cần chốt |
|---|---|---|---|
| NFR-01 | Quy mô dữ liệu | **Tối thiểu 500.000 bản ghi tính tổng các bảng nghiệp vụ trước khi đo tải**; có thống kê từng bảng. | DEC-08; Baseline |
| NFR-02 | Thông lượng đăng ký | Trong cùng kịch bản tải hỗn hợp, đạt **≥30 nghiệp vụ đăng ký gói mới thành công/giây**. | DEC-09; Baseline về ngưỡng; OQ-04 Open về cách đo |
| NFR-03 | Thông lượng trừ tiền | Đồng thời với đăng ký, đạt **≥30 nghiệp vụ trừ tiền độc lập mới thành công/giây**; tổng hai loại **≥60 TPS**. | DEC-09; Baseline; OQ-04 Open về phép đo |
| NFR-04 | Tranh chấp đồng thời | 10 cặp chia đều 50% tổng debit attempt kể cả retry; phần còn lại phân rộng; phải chứng minh overlap thực; phí nội bộ không vào mẫu số. | DEC-PRE-23; DEC-10. Mẫu tải/bằng chứng P6. |
| NFR-05 | Tính toàn vẹn dữ liệu | Không cấp trùng số, không trừ vượt số dư và không để sai lệch giữa số dư với giao dịch đã commit khi chạy đồng thời. | DEC-02/05/11; Baseline về kết quả đúng |
| NFR-06 | Tính nguyên tử nghiệp vụ | Đăng ký gói có thu phí không được xác nhận thành công một phần; cộng/trừ tiền và ghi nhận giao dịch phải nhất quán. | DEC-03/11; Baseline; kỹ thuật xử lý ở Phase 4 |
| NFR-07 | Đối soát, retry và timeout | Kiểm tra từng yêu cầu/giao dịch, mỗi yêu cầu tác động tối đa một lần khi retry và xác định rõ trạng thái trường hợp timeout; không khẳng định đúng/đủ nếu còn request chưa rõ kết quả. | DEC-11; Baseline; OQ-02/14 Open về cơ chế |
| NFR-08 | Bảo toàn lịch sử | Xử lý hết hạn không làm mất dữ liệu lịch sử cần thiết cho báo cáo và đối soát. | DEC-06; Baseline; OQ-11 Open về thời gian lưu |
| NFR-09 | Bảo mật tích hợp | JWKS bất đối xứng từ IdP local ngoài Telecom; kiểm token và quyền caller/request; không log JWT/giấy tờ đầy đủ. | DEC-PRE-04; cấu hình cụ thể P3–4. |
| NFR-10 | Khả năng vận hành | JOB-01 00:05, JOB-02 00:30 Asia/Ho_Chi_Minh; riêng cấu hình/chạy tay; UC01 recovery 60s; job trễ không kéo dài quyền dùng. | DEC-PRE-10/19/20; recovery và run identity còn thiết kế. |
| NFR-11 | Phép đo | k6; warm-up 2 phút, cửa sổ 10 phút; p95≤500ms/p99≤1s; lỗi hệ thống≤0,1%, business failure/timeout báo riêng; unresolved>0 chưa đạt đối soát. | DEC-PRE-22/24; tập đo latency/lỗi/máy/cấu hình phải ghi trước chạy. |

**Lưu ý đo tải:** Khi đăng ký gói gây ra lệnh thu phí nội bộ, không đếm lệnh thu phí đó như một nghiệp vụ trừ tiền **độc lập** để đạt chỉ tiêu 30 TPS. Cần phân biệt request mới thành công, retry trả kết quả cũ, lỗi nghiệp vụ, lỗi hệ thống và timeout.

---

## 6. Yêu cầu giao diện và tích hợp (External Interface Requirements)

### 6.1. REST API cho hệ thống CSKH

Backend cung cấp khả năng nghiệp vụ đấu nối; đăng ký/hủy gói; cộng/trừ tiền; tra cứu theo khoảng thời gian/gói. Đây là khả năng API toàn hệ thống; riêng UC01 đã chọn hướng endpoint và 201/202/409 ở DEC-PRE-11, chưa có OpenAPI/schema/lỗi đầy đủ.

Theo **CR-UC01-001**, UC01 áp dụng **Hybrid**: đường chính đồng bộ, trả ngay `SUCCEEDED` hoặc `FAILED` sau khi xử lý hoàn tất. Riêng **cấp số tự động hết kho**, nếu khách hàng đã đồng ý chờ và CSKH truyền lựa chọn đó, backend trả `WAITING` kèm requestId; nếu không đồng ý chờ, trả `NO_AVAILABLE_NUMBER` ngay. Yêu cầu WAITING có thể được giải quyết nền trong tối đa 24 giờ nhờ nạp số và tác vụ khôi phục. CSKH tự lưu mã và tra cứu trạng thái; **không bắt buộc nhân viên nhập requestId thủ công**. JWT do hệ thống ngoài cấp, backend kiểm tra chữ ký/issuer/hạn/quyền. Có **requestId**, **correlationId**, mã lỗi, thông điệp và dữ liệu kết quả. Retry cùng mã/payload không tác động thêm; cùng mã/payload khác trả `IDEMPOTENCY_CONFLICT`. Timeout HTTP không mặc định là thất bại; tra cứu/retry cùng requestId để xác định kết quả. Đã chọn scope caller+UC01, canonical payload và replay 7 ngày sau trạng thái cuối rồi tombstone; JWT/JWKS từ IdP local ngoài Telecom. Hướng POST activations/GET request/GET phone-numbers và 201/202/409 đã chọn. Schema/HTTP còn lại, normalize, claims và transaction cụ thể thuộc Phase3–4.

### 6.2. Tra cứu và báo cáo

DEC-PRE-17/18: [X,Y) theo commit; OR/AND và includeFee bắt buộc; giao dịch thành công có attribution gói/ngữ cảnh, phí gắn đăng ký. Danh sách thuê bao không trùng, cursor pagination, caller có scope báo cáo. Cấu trúc cụ thể thiết kế trước UC06.

### 6.3. Tác vụ định kỳ

DEC-PRE-19/20: JOB-01 00:05 và JOB-02 00:30 theo Asia/Ho_Chi_Minh; mỗi lịch cấu hình/chạy tay. JOB-02 lấy ngày trước, lưu count/tổng VND theo chiều nghiệp vụ, CSV tùy chọn, muộn điều chỉnh kỳ sau. Recovery UC01 mỗi 60s là tác vụ hỗ trợ riêng.
Không trì hoãn hiệu lực hết hạn đến lần job chạy; thứ tự xử lý gia hạn/hủy và mốc tiếp nhận phải thiết kế trước UC02/03/job.

### 6.4. Cơ sở dữ liệu và công cụ kiểm thử

CSDL quan hệ lưu các dữ liệu nghiệp vụ; ràng buộc vật lý, schema và chiến lược khóa thuộc Phase 4. Công cụ tạo dữ liệu/phát tải/đối soát nằm trong sản phẩm bàn giao theo DEC-REQ-01, nhưng không bắt buộc là một module API cho hệ thống CSKH.

---

## 7. Danh mục Use Case (Use Case Catalog)

| Mã | Tên Use Case | Tác nhân chính | Liên quan FR/BR | Trạng thái đặc tả |
|---|---|---|---|---|
| UC01 | Đấu nối thuê bao mới (gồm chọn/cấp số) | Hệ thống CSKH | FR-SUB-01–15; BR-01–03/11–17/24–27 | **UC01 v0.5: đã cập nhật Hybrid theo CR-UC01-001; vẫn chờ duyệt M1** |
| UC02 | Đăng ký gói cước | Hệ thống CSKH | FR-PKG-01–04/07; BR-04/18/19 | Khung; chi tiết trước khi triển khai UC02 |
| UC03 | Hủy gói cước | Hệ thống CSKH | FR-PKG-05–06; BR-05/19 | Khung; chi tiết trước khi triển khai UC03 |
| UC04 | Cộng tiền | Hệ thống CSKH | FR-ACC-01–02, 05–07; BR-10/20/22 | Khung; chi tiết trước khi triển khai UC04 |
| UC05 | Trừ tiền | Hệ thống CSKH | FR-ACC-01, 03–07; BR-06/10/20/22 | Khung; chi tiết trước khi triển khai UC05 |
| UC06 | Tra cứu tiêu dùng theo thời gian/gói | Hệ thống/bộ phận báo cáo hoặc hệ thống tích hợp phù hợp | FR-RPT-01–03 | Khung; đối tượng gọi API cần làm rõ ở OQ-03/15 |

**Job không bắt buộc được xem là Use Case với actor bên ngoài.** Đặc tả riêng theo mục 8 và tài liệu thực hiện khi đến P1.7.

### 7.1. Tóm tắt UC01 phục vụ nghiệm thu M1 (bản dự thảo đã cập nhật)

| Thuộc tính | Nội dung hiện có |
|---|---|
| Mục tiêu | Tạo Subscriber với số được cấp duy nhất, gắn Customer hợp lệ và Account chính số dư 0 |
| Tác nhân khởi tạo | Hệ thống CSKH có JWT hợp lệ do bên phát hành ngoài cung cấp |
| Trigger | CSKH gửi requestId cùng hồ sơ và chế độ chọn số/cấp tự động |
| Basic Flow | Xác thực/kiểm tra requestId và Customer → cấp số → tạo Subscriber/Account trong transaction → trả SUCCEEDED hoặc FAILED **ngay trong request HTTP** |
| Khi hết số tự động | Không chọn chờ → `NO_AVAILABLE_NUMBER` ngay. Có lựa chọn **đồng ý chờ** → WAITING tối đa 24 giờ, thử lại khi bổ sung số và qua task khôi phục; quá hạn EXPIRED |
| Retry | Cùng requestId và payload trả trạng thái/kết quả cũ; cùng mã nhưng payload khác trả `IDEMPOTENCY_CONFLICT` |
| Giấy tờ | Cho phép nhiều giấy tờ đã xác minh cùng Customer; không tự ghép khách hàng qua tên/ngày sinh |
| Bảo mật | JWT do hệ thống ngoài cấp; kiểm tra chữ ký, issuer, expiry và quyền |
| Chi tiết ở giai đoạn thiết kế | Schema và HTTP đầy đủ, normalize/absent/null, scopes/keys, lock order/claim/FIFO tie-break và expiry; recovery 60s/hướng endpoint đã chọn |

**Đặc tả chính:** [`UC01-Subscriber-Activation.md`](use-cases/UC01-Subscriber-Activation.md), v0.5; gồm luồng đồng bộ chính, nhánh WAITING có điều kiện, AC-SUB-01..27 và ma trận ca kiểm thử. **Người dùng đã chọn quy tắc nhưng chưa xác nhận duyệt chính tài liệu và checkpoint M1.**

---

## 8. Yêu cầu xử lý định kỳ (Scheduled Jobs – bản khung)

| Mã | Job | Hành vi đã được duyệt | Cần đặc tả ở P1.7 |
|---|---|---|---|
| JOB-01 | Xử lý hết hạn hàng ngày | Kiểm tra lần đăng ký gói/tài khoản hết hạn; cập nhật trạng thái/soft delete; giữ lịch sử và danh mục gói. | Đã chọn: 00:05 Asia/Ho_Chi_Minh, cấu hình/chạy tay, không kéo dài hiệu lực vì job trễ; còn trạng thái/race/chạy lại; OQ-07/11 |
| JOB-02 | Tổng hợp tiêu dùng hàng ngày | Job riêng, không thay thế API truy vấn và không gộp vào job hết hạn. | Đã chọn: 00:30 Asia/Ho_Chi_Minh, ngày trước, count/tổng VND theo chiều, CSV tùy chọn, muộn điều chỉnh kỳ sau; còn cơ chế chốt/cutoff; OQ-03/13 |

Hai job nghiệp vụ hàng ngày có thể cùng tồn tại với các luồng API thời gian thực. **Tác vụ khôi phục các yêu cầu đấu nối WAITING theo DEC-UC01-02 là một tác vụ kỹ thuật hỗ trợ UC01**, không thay thế hoặc gộp hai job nghiệp vụ bắt buộc nêu trên. Recovery 60 giây đã chọn theo DEC-PRE-10. Thiết kế xử lý trùng/transaction chốt kỳ còn Phase 4.

---

## 9. Phần còn mở sau khi nhận Q01–Q24

Bản ghi đầy đủ: [clarification log](../00-project/clarification-log.md), mục 13. Phiếu đã điền chỉ là đầu vào, không phải nguồn phải đọc kèm.

| Nhóm | Đã chốt | Còn thiếu / hạn |
|---|---|---|
| OQ-12 | So tất cả trường đã gửi; chứng cứ xác minh ngoài | Rà soát cách hiểu Q01 ở M1; chuẩn hóa từng trường/absent/null và verify evidence tại P4 |
| OQ-02/05/15 UC01 | Scope/canonical/7 ngày+tombstone, FIFO, recovery 60s, row lock+unique, JWT JWKS local, hướng API | M3–M4: schema/claims, số hợp lệ, FIFO tie-break, deadline/lock order/retry, HTTP replay/tombstone/in-progress |
| OQ-01/10/02 tiền | Phụ theo cấu hình, ID như UC01 | Hạn mức/hạn phụ và retention tiền cụ thể: đề xuất cần duyệt ở review-guide; trước UC02/04/05 |
| OQ-06/07/09 | Giá snapshot, chu kỳ ngày, chờ hủy chặn nối/gia hạn, thiếu tiền hết hiệu lực | Ca biên hủy/gia hạn, mốc tiếp nhận, ngày lịch/thời lượng, lỗi kỹ thuật; trước UC02/03/job |
| OQ-03/13/11 | Bộ lọc, attribution, cursor; hai lịch/chỉ tiêu; 5 năm không purge demo | Schema kỳ/cutoff/điều chỉnh/page size và archive; trước UC06/job/deployment |
| OQ-04/14 | warm-up 2/đo 10, p95/p99/error budget; 10 cặp/50% attempt kể cả retry; k6 | Máy, cấu hình, denominator metrics/overlap thực và đối soát; trước chạy tải |
| OQ-08 | Resolved | Không mở lại |
| Tái cấp số | Hoãn UC01 đầu | Quy trình giải phóng/cách ly trước nghiệp vụ ngừng thuê bao |

## 10. Ma trận truy vết yêu cầu (Requirements Traceability – bản đầu)

| Yêu cầu gốc | Nội dung | Yêu cầu liên kết trong SRS | Dự kiến kiểm chứng |
|---|---|---|---|
| REQ-01 | Mô hình đối tượng và quan hệ CSDL | BR-01; FR-SUB-07; FR-ACC-01; mục 2.4 | Domain Model, ERD, FK và test quan hệ |
| REQ-02 | Đấu nối thuê bao | FR-SUB-01–15; BR-01–03/11–17/24–27; UC01 | AC-SUB-01..27: test kết quả đồng bộ, chờ có lựa chọn 24h, không chọn chờ trả lỗi, JWT, requestId, đa giấy tờ |
| REQ-03 | Đăng ký/hủy gói | FR-PKG-01–07; BR-04/05/18/19; UC02/UC03 | Test đăng ký có phí, rollback, hủy cuối chu kỳ |
| REQ-04 | Cộng/trừ tiền | FR-ACC-01–07; BR-06/10/20/22; UC04/UC05 | Test đủ/thiếu tiền, đồng thời, retry, số dư |
| REQ-05 | Tra cứu tiêu dùng X–Y trên gói A, B | FR-RPT-01–04; UC06; JOB-02 | Test điều kiện lọc và kết quả tổng hợp khi đã chốt OQ |
| REQ-06 | Job xử lý hết hạn hàng ngày | FR-JOB-01–03; BR-07; JOB-01 | Test dữ liệu hết hạn, giữ lịch sử và chạy lại |
| REQ-07 | Dữ liệu tối thiểu 500K | NFR-01; FR-TST-01 | Báo cáo số bản ghi theo bảng trước đo tải |
| REQ-08 | Công cụ tải đăng ký và trừ tiền ≥30 TPS | NFR-02/03; FR-TST-01 | Một bài tải hỗn hợp với ≥30 TPS thành công mới mỗi loại |
| REQ-09 | 50% request trừ tiền tranh chấp | NFR-04; FR-TST-01 | Báo cáo phân bố nhóm và chồng lấn thực tế |
| REQ-10 | Kiểm chứng trừ tiền đúng, đủ khi đồng thời | NFR-05/06/07; FR-TST-02 | Đối soát từng request/giao dịch và số dư; ca timeout/retry |

Ma trận cần được bổ sung **AC/test case ID, kết quả kiểm thử, phiên bản thiết kế và trạng thái nghiệm thu** khi triển khai P1.8/P5/P6.

---

## 11. Nguyên tắc nghiệm thu và kiểm thử (Verification & Acceptance Approach)

### 11.1. Yêu cầu nghiệp vụ

- Mỗi FR phải có tiêu chí quan sát được qua API, bản ghi nghiệp vụ hoặc bằng chứng test.
- UC01 phải kiểm tra tối thiểu: cấp số tự động; chọn số khả dụng; số đã được cấp trước khi đấu nối; nhiều yêu cầu tranh chấp cùng một số; tính đúng quan hệ khách hàng–thuê bao.
- UC02/UC03 cần chứng minh đăng ký và thu phí không thành công một phần, hủy chỉ có hiệu lực cuối chu kỳ.
- UC04/UC05 cần chứng minh đủ/thiếu tiền, không số dư âm, bảo toàn số dư và giao dịch khi yêu cầu đồng thời/retry.
- API báo cáo và hai job phải có ca kiểm thử dữ liệu lịch sử, kỳ dữ liệu và chạy lại khi các quy tắc OQ liên quan được chốt.

### 11.2. Nghiệm thu quy mô, hiệu năng và tính đúng

- Dữ liệu **≥500.000 bản ghi nghiệp vụ trước đo**, có thống kê theo bảng.
- Trong **cùng** kịch bản hỗn hợp: đăng ký gói **≥30 TPS mới thành công**, trừ tiền độc lập **≥30 TPS mới thành công**, tổng **≥60 TPS**; không tính retry/cùng nghiệp vụ hai lần.
- **50% tổng request trừ tiền** được phân vào nhiều nhóm tranh chấp trên cặp thuê bao–tài khoản, chứng minh có chồng lấn thực tế.
- Đối chiếu từng request với kết quả đã commit; với mỗi tài khoản, đối chiếu `số dư cuối = số dư đầu + tổng tiền cộng thành công − tổng tiền trừ thành công` **và** kiểm tra số lượng/tính duy nhất của từng tác động nghiệp vụ.
- Không kết luận hệ thống đúng/đủ nếu còn request timeout mà chưa xác định được kết quả cuối cùng.
- Thời lượng đo, cấu hình máy, ngưỡng lỗi/độ trễ và cấu trúc dữ liệu tải sẽ được chốt trong OQ-04/14 và `05-testing/test-plan.md`.

### 11.3. Điều kiện đạt mốc M1 (Phase 1)

- [ ] Danh mục FR, BR, NFR không còn mâu thuẫn với các DEC đã duyệt.
- [ ] OQ-05, OQ-12, OQ-15 và phần requestId của OQ-02 được chốt đủ mức UC01; các nội dung có thể hoãn được nêu rành mạch. **Đã ghi nhận đủ 5 đáp án clarification log mục 12.3 và CR-UC01-001 Hybrid; còn chờ phê duyệt bộ tài liệu.**
- [ ] UC01 có bản Use Case Specification được chủ dự án rà soát, AC và tình huống lỗi/tranh chấp kiểm chứng được (**UC01 v0.5 Hybrid đã sẵn sàng để rà soát, chưa duyệt**).
- [ ] Các UC còn lại có khung và điểm chưa rõ đã được theo dõi.
- [ ] Ma trận REQ ↔ FR/BR/NFR ↔ UC ↔ AC được rà soát cho phạm vi UC01.

---

## 12. Trạng thái biên soạn, phê duyệt và lịch sử thay đổi

### 12.1. Kiểm tra phạm vi phiên bản 0.6

| Đầu việc Roadmap | Tình trạng trong bản này |
|---|---|
| **P1.1 – Khung SRS** | 🟡 Có khung, 31 câu clarification log mục 12.2, 5 câu clarification log mục 12.3 và quyết định CR-UC01-001 Hybrid; chờ duyệt cấu trúc và nội dung |
| P1.2 – FR chi tiết | 🟡 Đã bổ sung FR-SUB-01..15/AC cho UC01; các UC khác chi tiết theo vòng phát triển |
| P1.3 – BR chi tiết | 🟡 Có BR-01..27; Open chỉ còn các quy tắc chưa đến mốc hoặc thuộc thiết kế |
| P1.4 – NFR chi tiết | 🟡 Có NFR mức yêu cầu; JWT do ngoài phát hành; keys/scopes và cách đo chi tiết còn ở thiết kế/Test Plan |
| P1.5 – UC01 chi tiết | 🟡 UC01 v0.5 Hybrid có flows, 27 AC; **chờ người dùng duyệt toàn bộ UC01** |
| P1.6 – UC02–UC06 | 🟡 Có [khung riêng](use-cases/UC02-UC06.md); chưa đặc tả chi tiết |
| P1.7 – Hai job | 🟡 Có định hướng lịch cấu hình, dữ liệu muộn, bảng/file; thiếu chi tiết chỉ tiêu/cutoff |
| P1.8 – Truy vết | 🟡 Đã liên kết REV/FR/BR/AC UC01; yêu cầu duyệt nội dung và cập nhật các UC khác khi tới mốc |

**Trạng thái nghiệm thu:** Đã nhận các lựa chọn lịch sử và 24/24 lựa chọn mới, Q25 chưa chọn nhưng **chưa có xác nhận duyệt chính tài liệu SRS và UC01**. M1 còn chờ rà soát. Sau khi chủ dự án chấp thuận SRS v0.6 và UC01 v0.5, cập nhật trạng thái roadmap theo bằng chứng đã có; tiếp tục Phase 2 P2.1, không chờ hoàn tất UC02–UC06 chi tiết.

### 12.2. Phê duyệt

| Vai trò | Người phụ trách | Trạng thái |
|---|---|---|
| Người lập bản nháp | Trợ lý hỗ trợ phân tích | Đã cập nhật v0.6 từ clarification log mục 12.2, clarification log mục 12.3 và CR-UC01-001 |
| Người rà soát và duyệt | Chủ dự án | **Chưa duyệt** |

### 12.3. Lịch sử thay đổi (Revision History)

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 0.1 | 15/10/2026 | Khởi tạo SRS theo cấu trúc thống nhất Phase 0; chuyển DEC-01–11 vào FR/BR/NFR sơ bộ; catalog UC, khung job, OQ, truy vết. |
| 0.2 | 15/10/2026 | Tiếp nhận 31/31 lựa chọn clarification log mục 12.2; cập nhật FR-SUB-01..13, quy tắc BR-11..23, yêu cầu gói/tiền/báo cáo/job; giữ nguyên điểm Open và lập dự thảo UC01. |
| 0.3 | 15/10/2026 | Tiếp nhận 5/5 lựa chọn clarification log mục 12.3; cập nhật thời hạn chờ 24h, khôi phục WAITING, hồ sơ đa giấy tờ đã xác minh, JWT bên ngoài và `IDEMPOTENCY_CONFLICT`; rà soát M1, chưa được duyệt. |
| 0.4 | 15/10/2026 | CR-UC01-001: Chủ dự án chốt **Hybrid**; đồng bộ khi hoàn tất ngay; chỉ WAITING tối đa 24h khi tự cấp hết số và khách hàng đồng ý chờ; CSKH tự tra cứu requestId; cập nhật AC/flow, giữ M1 chờ duyệt. |


## Cập nhật quản lý tài liệu ngày 18/10/2026

Phiên bản 0.5: bỏ phụ thuộc phiếu review theo CR-DOC-001. Nguồn quyết định đầy đủ là clarification log mục 12; không thay đổi nghiệp vụ, chưa nghiệm thu M1.

Khung bổ trợ hiện có: [UC02–UC06](use-cases/UC02-UC06.md), [hai job](scheduled-jobs.md). Đây là bản DRAFT theo CR-DOC-001, không phải đặc tả được nghiệm thu.


### Cập nhật 0.6 — 18/10/2026

Đồng bộ DEC-PRE-01–24 và CR-ARCH-001; mở rộng AC UC01, bổ sung rõ phần đề xuất/chưa chốt. Xóa phiếu cũ sau khi lưu lịch sử. M1 chờ chủ dự án đọc bản 0.6 và UC01 0.5 rồi trả lời Q25; chưa coi Q01–24 là duyệt trọn tài liệu.
