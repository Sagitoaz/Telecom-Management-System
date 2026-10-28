# AGENTS.md — Quy tắc làm việc cho AI Agent

**Dự án:** Telecom Subscription Management System (`Telecom-Management-System`)  
**Vị trí:** Thư mục gốc repository, ngang hàng với `pom.xml` và `README.md`  
**Vai trò:** Hướng dẫn bắt buộc để agent làm đúng thứ tự, đúng phạm vi, đúng quyết định đã duyệt.  
**Tài liệu điều hành chính:** `docs/ROADMAP.md`

> **NGUYÊN TẮC CỐT LÕI:** Đọc trạng thái thực tế → xác định công việc kế tiếp có đủ điều kiện → làm đúng phạm vi một công việc → tự kiểm tra → trình chủ dự án duyệt khi cần → cập nhật tài liệu và tiến độ. **Không tự nhảy Phase, không tự suy đoán quyết định nghiệp vụ, không đánh dấu hoàn thành chỉ vì đã tạo file.**

## 1. Nguồn sự thật và thứ tự ưu tiên

Trước **mọi nhiệm vụ có tác động đến dự án**, kiểm tra những tệp đang tồn tại trong repository (không sử dụng bản nhớ hoặc tên file ZIP của cuộc trò chuyện thay cho file thực tế):

1. `docs/ROADMAP.md` — thứ tự công việc, checklist P0–P7, checkpoint M0–M8, tình trạng duyệt.
2. `docs/00-project/clarification-log.md` — quyết định `DEC-*`, thay đổi `CR-*`, vấn đề còn mở `OQ-*` và thẩm quyền duyệt.
3. `docs/01-requirements/SRS.md` — yêu cầu `FR-*`, `BR-*`, `NFR-*`, tiêu chí nghiệm thu và truy vết.
4. `docs/01-requirements/use-cases/` — đặc tả Use Case liên quan, nhất là `UC01-Subscriber-Activation.md`.
5. `docs/00-project/overview.md`, `docs/00-project/scope-and-glossary.md`, `docs/requirement.md` — mục tiêu, ranh giới, thuật ngữ và nguyên văn đề bài.
6. Tệp của Phase đang thực hiện, mã nguồn, test, Git status/log và bằng chứng build/test nếu liên quan.

**Nếu thiếu file:** ghi rõ thiếu file nào và hậu quả; kiểm tra đường dẫn tương ứng hoặc hỏi chủ dự án cung cấp. Không tự sáng tác nội dung hoặc đánh dấu file là đã tồn tại. Nếu tài liệu có vẻ mâu thuẫn, tìm quyết định `CR-*` **đã duyệt** mới nhất liên quan, nêu rõ tài liệu nào cần đồng bộ. Đề bài gốc phải được giữ nguyên; các quyết định được lưu riêng, có truy vết.

**Thứ bậc áp dụng khi mâu thuẫn:** yêu cầu người dùng vừa xác nhận rõ trong phiên làm việc (phải ghi lại thành quyết định) → `CR/DEC` có hiệu lực trong clarification log → SRS/UC đã được nghiệm thu → bản nháp đang rà soát → đề xuất của agent. Không tự áp dụng phương án trong phiếu *chưa được chọn*, hoặc thay đổi quyết định đã chốt mà không xin duyệt.

## 2. Tình trạng hiện hành — 28/10/2026

Chủ dự án đã phê duyệt **toàn bộ bộ tài liệu hiện có**, gồm delta MySQL M4-DB, theo `docs/00-project/document-approval-20261028.md` / **CR-DOC-004** trong clarification log mục21. M0–M4 đã đạt ở mức hồ sơ; các phần bổ sung SRS/UC02–06/jobs/UML/kiến trúc/API/schema/transaction/config và kế hoạch test/guide đã chốt.

- **CR-IMPL-001 — 28/10/2026:** Công ty dành cho học/đọc Spring, Docker và thành phần liên quan; ở nhà tự code/chạy service. Sau **P5.1**, ưu tiên **P5.5 UC04 cộng tiền → P5.6 UC05 trừ tiền**, test/recovery/đối soát, rồi P5.2 UC01 và các UC còn lại. Hai kế hoạch tại `docs/KE-HOACH-HOC-TAI-CONG-TY.md` và `docs/KE-HOACH-CODE-TAI-NHA.md`. Không yêu cầu duyệt lại tài liệu đã chốt.
- Java21/Spring Boot; MySQL8.0.46/InnoDB/READ COMMITTED; RabbitMQ/Redis/IdP ngoài backend theo kiến trúc. Office code/unit không DB; home chạy service. Không dùng starter PostgreSQL/Supabase cũ.
- P5.1 đã bắt đầu: pom chuyển sang MySQL/Flyway MySQL/Testcontainers MySQL; có Spring Boot entrypoint, application YAML, home YAML, .env.example và Compose chỉ MySQL. Chưa có schema nghiệp vụ, JWT, API tiền hoặc kiểm chứng DB/health thực tế; P5.1/M5–M8 vẫn mở. Xem docs/verification.md để biết bằng chứng mới nhất.
- Nhãn REVIEW/DRAFT và tên file có DRAFT trong bản ký duyệt là lịch sử trước phê duyệt. Quyết định bị thay hoặc NOT ADOPTED không được khôi phục; áp dụng CR-CAN-002/003 và CR-TIME-001/002 mới nhất.
- Phần OQ thiếu giá trị môi trường hoặc kiểm chứng thực tế tiếp tục theo P5–P7; không biến chúng thành vòng duyệt lại toàn bộ hồ sơ. Đọc `docs/ROADMAP.md` mỗi lượt để xác định trạng thái mới.

## 3. Thứ tự Phase bắt buộc và điều kiện chuyển

| Phase | Công việc chính | Điều kiện để chuyển tiếp |
|---|---|---|
| **0 — Project Definition & Scope** | Tổng quan, phạm vi, thuật ngữ, DEC/OQ | Baseline nhất quán và được duyệt (`M0`). |
| **1 — Requirements (SRS)** | FR, BR, NFR; đặc tả UC01; khung các UC còn lại; job; truy vết | **UC01 đủ rõ**, điều kiện nghiệm thu/ngoại lệ rõ, không còn OQ *chặn UC01* và được duyệt (`M1`). Không cần đóng hết OQ toàn hệ thống. |
| **2 — Analysis & UML** | P2.1 Use Case; P2.2 Domain Model; P2.3 State; P2.4 Sequence UC01; P2.5 khi cần; P2.6 đồng bộ SRS | **Domain Model nền của toàn hệ thống** + các luồng UC01 nhất quán (`M2`). Không bắt buộc vẽ mọi Sequence của mọi UC. |
| **3 — Architecture** | Tài liệu kiến trúc; C4 Context/Container; module; ADR khi có quyết định thực | Chốt stack, ranh giới module, nguyên tắc transaction và tích hợp đủ để thiết kế UC01 (`M3`). |
| **4 — Detailed Design** | ERD/schema nền; thiết kế DB cho UC01; OpenAPI UC01; Design Class; xử lý cạnh tranh cấp số | **P4.1–P4.4 cho UC01 và phương án P4.5 chống cấp số trùng đủ rõ** (`M4`) → được bắt đầu code UC01. |
| **5 — Implementation & Developer Testing** | Khởi tạo backend; ưu tiên **UC04/UC05 tiền trước** theo CR-IMPL-001, rồi UC01/UC02/UC03/UC06/jobs; đồng bộ tài liệu | Từng UC có API chạy được, migration và unit/integration test đạt; toàn bộ nghiệp vụ bắt buộc có bằng chứng (`M5/M6`). |
| **6 — System, Concurrency & Performance Testing** | Sinh dữ liệu, phát tải, test race/retry, đối soát, báo cáo | Có kết quả đo và đối soát tái lập, nêu rõ đạt/chưa đạt từng tiêu chí (`M7`). |
| **7 — Deployment & Handover** | Packaging, Docker Compose, migration/seed, logging, hướng dẫn chạy và bàn giao | Người khác triển khai và kiểm thử theo tài liệu được (`M8`). |

**Chiến lược triển khai: theo từng Use Case/vertical slice, không theo kiểu vẽ/cài hết toàn hệ thống trước.** Bộ thiết kế đã chốt cho phép bắt đầu nền tảng và UC04/UC05 tiền trước theo CR-IMPL-001; dùng fixtures đúng quan hệ để không phải hoàn tất API UC01/UC02 trước. Không bỏ điều kiện kỳ gói của debit. Các UC còn lại dùng thiết kế hiện hành và bổ sung delta nếu thực sự cần; M5 vẫn là nghiệm thu UC01. Không để các tài liệu chưa cần tới chặn không cần thiết.

## 4. Thứ tự công việc cụ thể cần bám

### Phase 1

1. P1.1: Tạo và thống nhất khung `docs/01-requirements/SRS.md`.
2. P1.2–P1.4: FR, BR, NFR có mã, điều kiện nghiệm thu và liên kết DEC/OQ.
3. P1.5: Đặc tả UC01 đủ basic/alternative/exception flows, pre/postconditions, AC/test scenarios; **phê duyệt M1**.
4. P1.6: Lập danh mục/khung UC02–UC06; chi tiết khi sắp triển khai từng UC.
5. P1.7: Đặc tả hai job theo đúng mốc nghiệp vụ của chúng.
6. P1.8: Truy vết đề bài → DEC/OQ → FR/BR/NFR → UC → AC/test.

Các việc bổ sung của Phase 1 có thể diễn ra song song ở mức cần thiết, **không chặn vòng phát triển UC01 nếu không phải phụ thuộc thực sự**.

### Phase 2 — quy trình tham chiếu (đã chốt bộ hiện hành 28/10/2026)

1. **P2.1:** Rà soát Use Case Diagram **đã tạo mới** (`.puml` là nguồn sửa); kiểm tra actor ngoài hệ thống, UC01–UC06, tra cứu số/trạng thái, hai job, `include/extend/generalization`, nhánh Hybrid. Xuất lại PNG/SVG nếu sửa và trình duyệt.
2. **P2.2:** Tạo `docs/02-analysis/domain-model.puml`: lớp nghiệp vụ, thuộc tính nghiệp vụ, bội số/quan hệ, quy tắc sở hữu; ít nhất xem xét `Customer`, `Subscriber`, `ServicePackage`, `PackageSubscription`, `Account`, `Transaction`, `PhoneNumberInventory`; với UC01 còn xem xét giấy tờ đã xác minh và yêu cầu đấu nối/chờ. **Không viết như JPA Entity/ERD.**
3. **P2.4:** Vẽ Sequence Diagram UC01: cấp số được chọn/tự động, đường thành công/thất bại đồng bộ, `WAITING` được đồng ý, cấp số khi bổ sung kho, retry/requestId, timeout, tranh chấp cấp số. **Không tự quyết thuật toán khóa DB trong bước phân tích.**
4. **P2.3:** State Diagram vòng đời đăng ký gói theo roadmap; thực hiện khi đủ quy tắc hoặc trước thiết kế gói, không biến thành lý do trì hoãn UC01.
5. **P2.5:** Sequence cho đăng ký kèm thu phí, trừ tiền đồng thời và job hết hạn khi tiến đến các UC đó.
6. **P2.6:** Khi UML cho thấy thiếu/mâu thuẫn nghiệp vụ, ghi OQ/CR và đồng bộ SRS theo quyết định thực tế.

Sau `M2`, chuyển Phase 3, rồi Phase 4 cho UC01. **Không chuyển thẳng từ Use Case Diagram sang DB/code.**

## 5. Những quy tắc nghiệp vụ agent KHÔNG được tự thay đổi

- `Customer` – `Subscriber` là **1–N**; mỗi thuê bao thuộc một Customer tại một thời điểm.
- Cấp số **tự động hoặc chọn số khả dụng**; tra cứu số **không giữ chỗ**; số đã bị cấp thì không được cấp trùng. Đấu nối trong backend, **không kích hoạt mạng viễn thông thật**.
- **UC01 Hybrid — `CR-UC01-001` có hiệu lực:**
  - Có thể hoàn tất thì xử lý **đồng bộ** và trả kết quả cuối `SUCCEEDED` hoặc `FAILED`.
  - Chỉ khi **cấp số tự động hết kho** **và khách hàng chọn đồng ý chờ** mới nhận yêu cầu `WAITING` tối đa **24 giờ**; không ngầm đưa mọi yêu cầu hết số vào chờ.
  - Khi kho có số mới, hệ thống thử xử lý lại; có cơ chế khôi phục yêu cầu chờ bị bỏ sót. Hết hạn → `EXPIRED`; **`WAITING` không phải đấu nối thành công**.
  - CSKH tự quản lý `requestId` và việc tra cứu; không buộc nhân viên nhập mã thủ công.
  - Retry **cùng requestId, cùng nội dung** không tạo thuê bao/cấp số lần hai; **cùng mã nhưng nội dung khác** → `IDEMPOTENCY_CONFLICT`.
  - JWT được phát hành bởi hệ thống xác thực bên ngoài; backend kiểm tra token/quyền. Không tự thêm module cấp JWT nội bộ.
- Một Customer có thể có nhiều giấy tờ **đã xác minh**; không tự ghép hai Customer chỉ vì trùng họ tên/ngày sinh. Chuẩn hóa/so khớp hồ sơ và quy trình xác minh giấy tờ mới còn phải tuân theo OQ liên quan.
- Đăng ký gói có **thu phí**; gói có hiệu lực **chỉ khi thu phí thành công**, không có thành công một phần. **Đăng ký gói khác API trừ tiền độc lập**.
- Hủy gói có hiệu lực **cuối chu kỳ hiện tại**; tài khoản/gói hết hạn xử lý bằng trạng thái/soft delete, giữ lịch sử và định nghĩa gói.
- Không cho số dư âm; thiếu tiền thì thất bại, **không thay đổi số dư hoặc tạo giao dịch tài chính thành công**. Cộng/trừ và số dư phải nhất quán khi đồng thời và retry.
- Có **hai job khác nhau**: xử lý hết hạn và tổng hợp tiêu dùng hằng ngày; scheduler là thành phần nội bộ, không tự vẽ là actor bên ngoài.
- Quy mô trước đo tải: **tổng các bảng nghiệp vụ ≥ 500.000 bản ghi** (không phải mỗi bảng 500K).
- Bài tải hỗn hợp: **đăng ký gói ≥ 30 TPS thành công mới** VÀ **trừ tiền độc lập ≥ 30 TPS thành công mới**, chạy đồng thời, tổng ≥ 60 TPS; không đếm retry hoặc khoản thu phí nội bộ hai lần.
- **50% tổng request trừ tiền** được phân vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp thực sự. Đối soát từng request/giao dịch và số dư, bao gồm timeout/retry.
- Trong phạm vi hiện tại **không xây** giao diện CSKH hoàn chỉnh, mobile app, kết nối mạng thật, tích hợp ngân hàng/cổng thanh toán, billing engine hoặc BI hoàn chỉnh.

**Các chi tiết còn `Open` không được bịa thành tiêu chuẩn:** tái sử dụng số, so khớp/chuẩn hóa hồ sơ, time-to-live/khóa idempotency, HTTP status/endpoint, thiết kế khóa/transaction, lưu lịch sử, giờ job, đo latency và nhóm tranh chấp. Tra cứu `clarification-log.md` để xác định *chính xác* chi tiết nào đã chốt, cái nào chưa và mốc phải chốt.

## 6. Quy trình bắt buộc cho TỪNG lần agent nhận việc

**Bước 1 — Kiểm tra:**

- Đọc `AGENTS.md`, `docs/ROADMAP.md`, tài liệu nghiệp vụ áp dụng và các file hiện có; xem Git diff/status nếu có.
- Xác định `Phase`, mã đầu việc cụ thể (ví dụ `P2.2`), checkpoint liên quan, phụ thuộc đã đạt/chưa đạt.
- Nếu người dùng nói “làm tiếp”, chọn **đầu việc sớm nhất còn dang dở/đang chờ duyệt có ảnh hưởng đến tiến trình**, không tự chọn bước hấp dẫn hơn.

**Bước 2 — Công bố ngắn kế hoạch:**

- Nêu: `Đang ở [Phase/Px.y] → sẽ làm [đầu việc] → cần [đầu vào] → sản phẩm [đường dẫn]`.
- Nếu có công việc cần duyệt trước, nói rõ; không coi sự im lặng là đã duyệt.
- Nếu được giao nhảy sang bước sau, giải thích phụ thuộc; chỉ tạo bản **DRAFT** khi phù hợp với yêu cầu rõ ràng, không công bố checkpoint đã đạt.

**Bước 3 — Kiểm tra quyết định:**

- Tách rõ **đã duyệt / giả định tạm / chưa chốt / đề xuất mới**.
- Nếu thiếu quyết định **chặn nhiệm vụ**, đưa ra các lựa chọn cụ thể (ưu tiên phiếu Markdown A/B/C/D khi có nhiều quyết định độc lập); chờ phê duyệt trước khi dùng như quy tắc chính thức.
- Nếu chi tiết được Roadmap cho phép hoãn, đánh dấu `Open` với mã OQ/mốc giải quyết và **tiếp tục phần không bị chặn**.

**Bước 4 — Thực hiện đúng mức trừu tượng:**

- Phase 1: mô tả *cái gì phải đúng*, không thiết kế cụ thể SQL/HTTP/framework trước hạn.
- Phase 2: mô hình **nghiệp vụ**, không lẫn Domain Model với ERD hoặc Design Class.
- Phase 3: ranh giới kiến trúc và quyết định có căn cứ; không mặc định microservices, Kafka, Redis, CQRS nếu không có lý do.
- Phase 4: thiết kế chi tiết DB/API/concurrency **cho UC đang làm**.
- Phase 5: mã nguồn chạy được, migration và kiểm thử; không chỉ viết code skeleton rồi tích hoàn thành.
- Phase 6: kết quả đo tải **thực tế**, kèm công cụ, cấu hình, dữ liệu, log và đối soát; không tự tuyên bố đạt TPS.
- Phase 7: chạy/bàn giao tái lập từ máy mới.

**Bước 5 — Tự kiểm tra và cập nhật:**

- Kiểm tra tính nhất quán giữa tài liệu mới với SRS, DEC/CR và Use Case.
- Nếu tạo `.puml`, bảo đảm cú pháp có thể render; nếu tạo ảnh, chúng phải phản ánh nguồn `.puml` mới nhất.
- Ghi rõ file được tạo/sửa, kết quả kiểm chứng và hạng mục chưa đạt.
- **Chỉ tick `- [x]` khi có sản phẩm đủ chất lượng và bằng chứng đúng điều kiện của mục đó;** không tick khi chỉ có file rỗng/bản nháp.
- Với đầu việc yêu cầu chủ dự án nghiệm thu, giữ **🟡 chờ duyệt** cho đến khi có xác nhận rõ. Ghi ngày, người xác nhận, tài liệu/version liên quan trong roadmap/clarification log khi được duyệt.
- Không được tự commit, push, merge, đổi branch, deploy, phá dữ liệu hoặc sửa tài liệu baseline ngoài phạm vi khi chưa được yêu cầu/cho phép tương ứng.

**Bước 6 — Báo cáo kết quả:**

1. **Đã làm:** file/đường dẫn chính xác và những thay đổi thực sự.
2. **Kiểm tra:** cú pháp/build/test/đối chiếu nào đã chạy và kết quả (không bịa).
3. **Còn mở:** OQ, rủi ro hoặc phần cần chủ dự án duyệt.
4. **Tiếp theo duy nhất:** mã đầu việc cụ thể và điều kiện để bắt đầu.

## 7. Quy tắc phê duyệt và thay đổi

- **Chủ dự án (người dùng)** có quyền duyệt cuối cùng. Agent chỉ đề xuất, phân tích, thực hiện phần đã giao; không tự nhận thay người duyệt.
- Khi thay đổi quyết định đã chốt, lập/ghi `CR-*`: quyết định cũ, quyết định mới, lý do, người/ngày duyệt, ảnh hưởng tới SRS/UC/UML/DB/API/test; **giữ lịch sử phiếu cũ**.
- Khi giải quyết `OQ-*`: cập nhật `clarification-log.md` với trạng thái mới và phần còn Open; không đóng cả OQ nếu chỉ chốt một phần.
- **Checkpoint phải có bằng chứng**. Mốc M1/M2/M3/M4... không tự hoàn thành chỉ vì các file tồn tại hoặc vì người dùng nói “làm tiếp”.
- Các file sinh tạm để rà soát có thể mang hậu tố `DRAFT`/`REVIEW`; khi duyệt, cập nhật file chuẩn trong `docs/` thay vì song song duy trì nhiều bản mâu thuẫn.

## 8. Quy ước cấu trúc tài liệu và mã nguồn

```text
Telecom-Management-System/
├── AGENTS.md                          # Tệp này
├── README.md
├── pom.xml
├── docs/
│   ├── ROADMAP.md                    # Single source of truth cho tiến độ
│   ├── requirement.md                # Nguyên văn đề bài (không tự sửa)
│   ├── 00-project/
│   │   ├── overview.md
│   │   ├── scope-and-glossary.md
│   │   └── clarification-log.md
│   ├── 01-requirements/
│   │   ├── SRS.md
│   │   └── use-cases/
│   ├── 02-analysis/
│   │   ├── use-case-diagram.puml
│   │   ├── uc01-use-case-diagram.puml
│   │   ├── domain-model.puml         # Tạo ở P2.2
│   │   ├── state-diagrams/           # Tạo khi cần
│   │   └── sequence-diagrams/        # Tạo khi cần
│   ├── 03-architecture/              # Tạo ở Phase 3
│   ├── 04-design/                    # Tạo ở Phase 4
│   ├── 05-testing/                   # Tạo ở Phase 6
│   └── 06-deployment/                # Tạo ở Phase 7
└── src/                               # Triển khai backend ở Phase 5
```

Cây trên là **đích cấu trúc**, không khẳng định tất cả file/thư mục hiện đã tồn tại. Không tạo file rỗng chỉ để khớp cây. Tôn trọng tên/mã tài liệu sẵn có; các tệp `.md` viết tiếng Việt rõ ràng, UML bằng PlantUML, thuật ngữ tiếng Anh nhất quán.

## 9. Những điều TUYỆT ĐỐI tránh

- Không “đốt giai đoạn”: xong P2.1 ≠ đã đủ điều kiện lập trình; phải qua M2, M3, M4 theo phạm vi UC01.
- Không ép hoàn thành mọi use case/mọi diagram trước khi code UC01.
- Không đánh đồng sơ đồ Use Case, Domain Class, ERD, Design Class và JPA Entity.
- Không bịa actor, trường dữ liệu, business rule, giới hạn TPS, số dư, chính sách giữ lịch sử hoặc thời gian job.
- Không nhầm `WAITING` với `SUCCEEDED`, không bỏ Hybrid đã duyệt hoặc tự quay lại async hoàn toàn.
- Không đánh dấu một Phase hoàn thành vì đã xuất được ảnh/tạo Markdown; phải qua điều kiện nghiệm thu.
- Không âm thầm chỉnh nội dung đã được người dùng duyệt; luôn cập nhật truy vết và lịch sử quyết định.
- Không khẳng định đã chạy test, render, build, commit hoặc deploy nếu thực tế chưa làm.

**Khi phân vân nên làm gì tiếp: mở `docs/ROADMAP.md`, tìm việc đang dở/chờ duyệt đầu tiên và điều kiện chuyển mốc; báo rõ với người dùng, rồi mới thực hiện.**
