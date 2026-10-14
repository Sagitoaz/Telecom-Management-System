# Lộ trình phát triển dự án – Telecom Subscription Management System

> **File theo dõi chính:** `docs/ROADMAP.md`
> **Dự án:** `Telecom-Management-System`
> **Ngày cập nhật:** 14/10/2026
> **Trạng thái tổng thể:** ✅ Phase 0 – Đã chốt baseline ngày 14/10/2026; Phase 1 chưa bắt đầu
> **Bước tiếp theo:** Khi bắt đầu Phase 1: đặc tả SRS/UC01 và chốt OQ-05, OQ-12, OQ-15 trước thiết kế đấu nối.

## 1. Quy ước trạng thái và cách cập nhật

| Trạng thái | Ý nghĩa |
|---|---|
| ✅ Hoàn thành | Có sản phẩm và đã đáp ứng điều kiện hoàn thành của đầu việc. |
| 🟡 Đang thực hiện / cần duyệt | Đã có bản nháp hoặc đang xử lý, **chưa** đủ điều kiện đóng. |
| ⬜ Chưa bắt đầu | Chưa triển khai chính thức. |
| ⛔ Bị chặn | Thiếu quyết định/đầu vào bắt buộc; ghi rõ nguyên nhân trong `clarification-log.md`. |

- Tick `- [x]` khi **đầu việc đã hoàn tất**, không chỉ vì đã tạo file rỗng. Dùng `- [ ]` cho việc đang làm hoặc chưa làm, kèm trạng thái trong ngoặc nếu cần.
- Một Phase chỉ đổi sang **✅ Hoàn thành** khi đạt *điều kiện chuyển Phase* của phần đó; không bắt buộc giải quyết mọi vấn đề được phép hoãn.
- Nếu một chức năng đã được code, test xong, cập nhật cả checkbox tương ứng **và** tài liệu liên quan. Ghi ngày và commit ở nhật ký cuối file.
- **Không viết đủ bộ tài liệu rồi mới code**: phân tích, thiết kế đủ cho UC01 để triển khai UC01; sau đó lặp theo từng chức năng.

## 2. Dashboard – Tiến độ theo Phase

| Phase | Trọng tâm | Trạng thái hiện tại | Sản phẩm / mốc chính |
|---|---|---|---|
| **0 – Project Definition & Scope** | Mục tiêu, phạm vi, thuật ngữ, câu hỏi mở | ✅ Baseline được chủ dự án có quyền nghiệm thu duyệt 14/10/2026 | M0 – Baseline phạm vi ban đầu |
| **1 – Requirements (SRS)** | FR, NFR, quy tắc, kịch bản Use Case | ⬜ Chưa bắt đầu | M1 – Yêu cầu đủ rõ cho UC01 và kiến trúc nền |
| **2 – Analysis & UML** | Use Case, Domain Class, State, Sequence | ⬜ Chưa bắt đầu; **có sơ đồ Use Case cũ cần rà soát** | M2 – Mô hình nghiệp vụ ban đầu |
| **3 – Architecture** | C4, module, quyết định kiến trúc | ⬜ Chưa bắt đầu | M3 – Kiến trúc nền được chọn |
| **4 – Detailed Design** | ERD, schema, Design Class, API, transaction | ⬜ Chưa bắt đầu | M4 – Thiết kế UC01 đủ để code |
| **5 – Implementation** | Spring Boot, JPA/JDBC, nghiệp vụ, test theo chức năng | ⬜ Chưa bắt đầu; `pom.xml` đang chuẩn bị | M5 – Các chức năng hoạt động |
| **6 – System & Performance Testing** | Dữ liệu lớn, đồng thời, đo TPS, đối soát | ⬜ Chưa bắt đầu | M6 – Bằng chứng đáp ứng đề bài |
| **7 – Deployment & Handover** | Đóng gói, triển khai, vận hành | ⬜ Chưa bắt đầu | M7 – Có thể chạy và bàn giao |

**Mốc cần nhớ:** Sau khi có kiến trúc nền + mô hình dữ liệu + hợp đồng API + kiểm thử dự kiến cho **UC01**, **chuyển sang code UC01**, không chờ hoàn thành mọi sơ đồ/API của toàn hệ thống.

---

## 3. Checklist chi tiết

### Phase 0 – Project Definition & Scope ✅

**Mục tiêu:** Thống nhất bức tranh ban đầu và xác định vấn đề chưa rõ; không đi vào chi tiết kỹ thuật.

- [x] **P0.1 – Project Overview:** Tạo [`00-project/overview.md`](00-project/overview.md) (đã có nội dung, baseline ban đầu).
- [x] **P0.2 – Scope & Glossary:** Tạo [`00-project/scope-and-glossary.md`](00-project/scope-and-glossary.md) (đã có nội dung, baseline ban đầu).
- [x] **P0.3 – Requirement Clarification Log:** Tạo [`00-project/clarification-log.md`](00-project/clarification-log.md) (baseline v1.0; lưu đủ 14 đáp án, DEC-01–DEC-11 và trạng thái OQ-01–OQ-15).
- [x] **P0.4 – Rà soát và chốt baseline:** Chủ dự án có quyền quyết định nghiệm thu xác nhận đủ 14 đáp án ngày 14/10/2026 (Q13=C, Q14=A). Đã lưu quyết định, đồng bộ tài liệu và phân công/mốc xử lý chi tiết; bản baseline nằm trong commit `docs: finalize phase 0 baseline` (tra cứu bằng Git log).

**Kết quả duyệt:** Quan hệ 1–N, soft delete, đăng ký có thu phí và job tổng hợp hàng ngày đã được chấp nhận. Dữ liệu tổng ≥500K trước đo; cùng chạy ≥30 TPS mỗi loại, tổng ≥60 TPS; 50% request trừ tiền chia vào nhiều nhóm tranh chấp. OQ-08 Resolved; các OQ còn lại giữ Open ở phần chi tiết, có người phụ trách và mốc chốt trong clarification log.

**Điều kiện chuyển Phase:** Ba tài liệu liên kết nhất quán, quyết định nội bộ rõ ràng, vấn đề chưa giải quyết có mã/trách nhiệm/thời điểm cần chốt. **Không yêu cầu đóng hết 15 OQ.**

### Phase 1 – Software Requirements Specification (SRS) ⬜

**Mục tiêu:** Chuyển đề bài thành yêu cầu kiểm chứng được; ưu tiên đủ chi tiết cho UC01 trước.

- [ ] **P1.1 – SRS khung toàn hệ thống:** Tạo `01-requirements/SRS.md`, gồm mục tiêu, tác nhân/hệ thống tích hợp, các nhóm FR, NFR, giả định và tham chiếu.
- [ ] **P1.2 – Functional Requirements:** Gắn mã `FR-...`, đầu vào/đầu ra và điều kiện nghiệm thu cho đấu nối, gói cước, tiền, báo cáo, job.
- [ ] **P1.3 – Business Rules:** Gắn mã `BR-...` cho quyền sở hữu, cấp số, hủy cuối chu kỳ, số dư, vòng đời và trường hợp biên; phần chưa chốt dẫn mã OQ.
- [ ] **P1.4 – Non-Functional Requirements:** Gắn mã `NFR-...` cho tính đúng giao dịch, 500.000 bản ghi, tối thiểu 30 TPS, 50% request cùng thuê bao/tài khoản, bảo mật và vận hành. Giữ rõ các chỉ tiêu còn cần chốt cách đo.
- [ ] **P1.5 – Use Case Specification cho UC01:** Trigger, tiền/hậu điều kiện, Basic Flow, Alternative Flow, Exception Flow, liên kết FR/BR và tiêu chí kiểm thử.
- [ ] **P1.6 – Use Case Specification cho UC02–UC06:** Xây dựng khung/danh sách đầy đủ; đặc tả chi tiết **khi sắp triển khai từng UC**, không chặn việc code UC01.
- [ ] **P1.7 – Đặc tả các job hàng ngày:** Job hết hạn và job tổng hợp tiêu dùng riêng; chốt trạng thái, đầu ra/chỉ tiêu/lịch và chạy lại theo OQ-07/OQ-11/OQ-13. Không bắt buộc tạo actor bên ngoài chỉ để vẽ Use Case.
- [ ] **P1.8 – Rà soát tính truy vết:** Đối chiếu yêu cầu gốc ↔ FR/NFR/BR ↔ Use Case ↔ tiêu chí kiểm thử.

**Điều kiện chuyển:** Các yêu cầu lõi không mâu thuẫn; **UC01 đủ rõ để vẽ mô hình và lập API**, những yêu cầu chưa cần ngay được ghi rõ trạng thái. SRS tiếp tục cập nhật theo từng vòng phát triển.

### Phase 2 – Analysis & UML ⬜

**Mục tiêu:** Hiểu mô hình nghiệp vụ, quan hệ và vòng đời trước khi hiện thực CSDL.

- [ ] **P2.1 – Rà soát Use Case Diagram:** Dựa trên sơ đồ đã vẽ trong báo cáo cũ; cập nhật cấp số/tra cứu số khả dụng nếu cần, kiểm tra lại actor và ranh giới hệ thống.
- [ ] **P2.2 – Domain Class Diagram:** Vẽ **các lớp nghiệp vụ và quan hệ**, tối thiểu xét `Customer`, `Subscriber`, `ServicePackage`, `PackageSubscription`, `Account`, `Transaction`; cân nhắc `PhoneNumberInventory`.
- [ ] **P2.3 – State Diagram:** Vẽ vòng đời **đăng ký gói cước** (đang hiệu lực, chờ hủy, hết hạn...), thêm thuê bao/tài khoản khi có nhu cầu thật.
- [ ] **P2.4 – Sequence Diagram nghiệp vụ UC01:** Luồng chọn/cấp số, đấu nối thành công và tranh chấp cấp số.
- [ ] **P2.5 – Sequence Diagram các luồng rủi ro cao:** Đăng ký + trừ tiền, giao dịch đồng thời, job hết hạn; thực hiện sát thời điểm thiết kế module tương ứng.
- [ ] **P2.6 – Rà soát và đồng bộ SRS:** Nếu UML làm lộ khoảng trống nghiệp vụ thì cập nhật yêu cầu và `clarification-log.md`.

**Điều kiện chuyển:** Có **Domain Model ban đầu cho toàn hệ thống** và luồng UC01 đủ rõ; không phải vẽ xong mọi Sequence Diagram trước khi thiết kế UC01.

### Phase 3 – Software Architecture ⬜

**Mục tiêu:** Chọn kiến trúc phù hợp một backend Spring Boot quản lý giao dịch và dữ liệu quan hệ.

- [ ] **P3.1 – Architecture Document:** Tạo `03-architecture/architecture.md`, nêu kiến trúc nền, các module và trách nhiệm.
- [ ] **P3.2 – C4 System Context:** Thể hiện TelecomManagement, hệ thống CSKH, bên dùng báo cáo và ranh giới.
- [ ] **P3.3 – C4 Container:** Thể hiện API/backend, database, job nội bộ và công cụ kiểm thử (không tự mặc định microservices).
- [ ] **P3.4 – Package/Module Structure:** Quy định các module như subscriber, package, account, transaction, reporting, scheduling; tránh phụ thuộc vòng.
- [ ] **P3.5 – Architecture Decision Records (ADR):** Ghi quyết định quan trọng **khi thực sự phát sinh**, chẳng hạn PostgreSQL, cơ chế giao dịch, JPA/JDBC, chiến lược khóa.

**Điều kiện chuyển:** Xác định công nghệ, ranh giới module và nguyên tắc transaction đủ rõ để thiết kế UC01.

### Phase 4 – Detailed Design ⬜

**Mục tiêu:** Biến mô hình nghiệp vụ thành thiết kế kỹ thuật; triển khai dần theo UC.

- [ ] **P4.1 – ERD bản đầu:** Vẽ mô hình dữ liệu tổng thể ở mức quan hệ; làm chi tiết bảng phục vụ **UC01** trước.
- [ ] **P4.2 – Database Schema:** Tạo `04-design/database-schema.md`; chốt khóa, FK, unique, index, trạng thái và ràng buộc cho UC01; mở rộng khi triển khai module khác.
- [ ] **P4.3 – API Contract UC01:** Khai báo tra cứu số khả dụng, cấp số tự động/đấu nối, request/response, lỗi và các trường hợp đồng thời trong `04-design/openapi.yaml`.
- [ ] **P4.4 – Design Class Diagram UC01:** Thiết kế class Java thực sự cần (Entity, Repository, Service, Controller, DTO), không lặp lại toàn bộ Domain Diagram.
- [ ] **P4.5 – Concurrency & Transaction Design:** Phác thảo cách chống cấp trùng cho UC01. Trước UC04/UC05, **bắt buộc** chốt xử lý đồng thời trên cùng tài khoản, chống xử lý trùng và đối soát; bổ sung `04-design/concurrency-and-jobs.md`.
- [ ] **P4.6 – Thiết kế module tiếp theo:** Mở rộng ERD, API, Design Class và Sequence kỹ thuật khi chuyển sang từng UC.

**🚩 MỐC BẮT ĐẦU CODE (M4):** Khi **P4.1–P4.4 cho UC01** và xử lý tranh chấp cấp số ở P4.5 đủ rõ → **dừng thiết kế tổng thể quá mức, bắt đầu triển khai UC01**.

### Phase 5 – Implementation & Developer Testing ⬜

**Mục tiêu:** Code theo từng chức năng, chạy được và có bằng chứng kiểm thử.

- [ ] **P5.1 – Khởi tạo backend:** Kiểm tra Maven import/build, Java 21, Spring Boot, PostgreSQL, cấu hình môi trường, Flyway và cấu trúc package. **Đã chuẩn bị dependencies và có kết quả `mvn verify` thành công ngày 14/10/2026 trong phiên làm việc; chưa có backend nghiệp vụ, cấu hình PostgreSQL/migration hay kiểm thử nghiệp vụ nên đầu việc vẫn chưa hoàn tất.**
- [ ] **P5.2 – UC01 – Đấu nối thuê bao:** Entity, migration, Repository, Service, Controller, validation, API và test cấp số/đấu nối.
- [ ] **P5.3 – UC02 – Đăng ký gói cước:** Thiết kế bổ sung → code → Unit/Integration Test.
- [ ] **P5.4 – UC03 – Hủy gói cước:** Xử lý yêu cầu hủy vào cuối chu kỳ → test trạng thái và thời gian.
- [ ] **P5.5 – UC04 – Cộng tiền:** Transaction nhất quán, lưu lịch sử → test.
- [ ] **P5.6 – UC05 – Trừ tiền:** Kiểm tra số dư, atomicity, đồng thời, request trùng/retry → test nhiều luồng trên PostgreSQL thật.
- [ ] **P5.7 – UC06 – API tra cứu báo cáo:** Lọc theo thời gian/gói, phân trang nếu được chốt → test.
- [ ] **P5.8 – Job xử lý hết hạn:** Soft delete/trạng thái theo quyết định nghiệm thu, job hàng ngày, xử lý lại an toàn → test.
- [ ] **P5.9 – Đồng bộ tài liệu:** Cập nhật OpenAPI, schema, sơ đồ và hướng dẫn chạy theo những gì thực tế đã triển khai.
- [ ] **P5.10 – Job tổng hợp tiêu dùng hàng ngày:** Chỉ tiêu/đầu ra/lịch theo SRS; chạy lại an toàn và kiểm thử dữ liệu tổng hợp (DEC-07).

**Nguyên tắc thực hiện cho từng UC:** `Làm rõ yêu cầu → Thiết kế vừa đủ → Code → Unit/Integration Test → Review → Cập nhật docs`.

**Điều kiện chuyển:** Các chức năng bắt buộc chạy được, vượt qua kiểm thử nghiệp vụ và có API/DB phù hợp thiết kế đã cập nhật.

### Phase 6 – System, Concurrency & Performance Testing ⬜

**Mục tiêu:** Chứng minh hệ thống đáp ứng các yêu cầu bắt buộc, không chỉ “API chạy được”.

- [ ] **P6.1 – Test Plan:** Tạo `05-testing/test-plan.md`, xác định môi trường, dữ liệu, tiêu chí thành công, loại lỗi và phương pháp ghi kết quả.
- [ ] **P6.2 – Functional/Integration Regression:** Kiểm tra luồng chính, nhánh thay thế và ngoại lệ giữa các module.
- [ ] **P6.3 – Data Generator:** Tổng các bảng nghiệp vụ có **tối thiểu 500.000 bản ghi trước đo tải**, phân bố đủ chạy nghiệp vụ hợp lệ; lưu thống kê từng bảng (DEC-08).
- [ ] **P6.4 – Performance Tool:** Xây tool hoặc kịch bản k6/JMeter phát tải và lưu kết quả (không nhất thiết là Maven dependency của backend).
- [ ] **P6.5 – Throughput Test:** Cùng chạy đăng ký gói **≥30 TPS** và trừ tiền **≥30 TPS**, tổng **≥60 TPS thành công mới**; không đếm retry trả kết quả cũ và không đếm hai lần phí đăng ký như một lệnh trừ tiền độc lập. Điều kiện đo chi tiết chốt tại OQ-04.
- [ ] **P6.6 – Contention Test:** **50% tổng request trừ tiền** chia vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp; chứng minh chồng lấn thực tế; chốt số nhóm/phân bố/mẫu số trong bài hỗn hợp tại OQ-14 (DEC-10).
- [ ] **P6.7 – Transaction Reconciliation:** Đối chiếu **từng request/giao dịch**, số dư đầu/cuối, số nghiệp vụ thành công/thất bại, retry và timeout; không chỉ cộng trừ tổng số tiền.
- [ ] **P6.8 – Performance Test Report:** Tạo `05-testing/performance-test.md` (kế hoạch/kịch bản) và báo cáo kết quả thực đo, thông số máy, truy vấn, biểu đồ, lỗi và giới hạn.

**Điều kiện chuyển:** Có kết quả đo tái lập được, chứng minh tính đúng/đủ và mức đạt hoặc chưa đạt của từng chỉ tiêu; nếu chưa đạt thì tối ưu và chạy lại.

### Phase 7 – Deployment & Handover ⬜

**Mục tiêu:** Người khác có thể dựng và kiểm tra hệ thống theo hướng dẫn.

- [ ] **P7.1 – Build & Packaging:** Đóng gói executable JAR, cấu hình profile, biến môi trường.
- [ ] **P7.2 – Docker/Compose:** Chạy backend và PostgreSQL theo cấu hình triển khai được kiểm tra.
- [ ] **P7.3 – Database Migration & Seed:** Khởi tạo schema và dữ liệu mẫu/kiểm thử có hướng dẫn.
- [ ] **P7.4 – Logging/Health/Recovery:** Kiểm tra health, log giao dịch phù hợp, cách khởi động lại job và sao lưu/phục hồi cơ bản.
- [ ] **P7.5 – Deployment Guide:** Viết `06-deployment/deployment-guide.md` với hướng dẫn từ máy mới đến khi gọi thử API.
- [ ] **P7.6 – README & Handover:** Cập nhật `README.md`, link tài liệu, lệnh build/test, các hạn chế còn lại và báo cáo nghiệm thu.

**Điều kiện hoàn tất:** Có thể cài đặt, chạy, kiểm thử và đọc tài liệu mà không cần phụ thuộc vào thao tác chưa được ghi lại.

---

## 4. Danh mục tài liệu và sơ đồ (tạo khi đến bước)

| Đường dẫn dự kiến (tương đối từ `docs/`) | Loại | Phase | Trạng thái |
|---|---|---|---|
| [`00-project/overview.md`](00-project/overview.md) | Markdown | 0 | ✅ Baseline v1.1 đã duyệt |
| [`00-project/scope-and-glossary.md`](00-project/scope-and-glossary.md) | Markdown | 0 | ✅ Baseline v1.1 đã duyệt |
| [`00-project/clarification-log.md`](00-project/clarification-log.md) | Markdown | 0 | ✅ Baseline v1.0; chi tiết còn mở có kế hoạch |
| `01-requirements/SRS.md` | Markdown | 1 | ⬜ |
| `01-requirements/use-cases/UC01-UC06.md` hoặc 6 file | Markdown | 1 | ⬜ |
| `02-analysis/use-case-diagram.puml` | UML | 2 | 🟡 Có bản sơ đồ cũ; chưa rà soát |
| `02-analysis/domain-model.puml` | UML Class mức phân tích | 2 | ⬜ |
| `02-analysis/state-diagrams/` | UML State | 2 | ⬜ |
| `02-analysis/sequence-diagrams/` | UML Sequence | 2 | ⬜ |
| `03-architecture/architecture.md` | Markdown | 3 | ⬜ |
| `03-architecture/c4-diagrams/` | C4 | 3 | ⬜ |
| `03-architecture/adr/` | Quyết định kiến trúc (khi cần) | 3 | ⬜ |
| `04-design/erd.puml` hoặc `.drawio` | ERD | 4 | ⬜ |
| `04-design/database-schema.md` | Markdown | 4 | ⬜ |
| `04-design/design-class-diagrams/` | UML Class mức thiết kế | 4 | ⬜ |
| `04-design/openapi.yaml` | OpenAPI | 4–5 | ⬜ |
| `04-design/concurrency-and-jobs.md` | Markdown | 4–5 | ⬜ |
| `05-testing/test-plan.md` | Markdown | 6 | ⬜ |
| `05-testing/performance-test.md` | Markdown | 6 | ⬜ |
| `06-deployment/deployment-guide.md` | Markdown | 7 | ⬜ |
| `../README.md` | Markdown | Xuyên suốt | 🟡 Đã có file, nội dung cần tiếp tục cập nhật |

> **Không cần tạo trước mọi thư mục/file ở bảng trên.** `Domain Class Diagram` ≠ `ERD` ≠ `Design Class Diagram` ≠ Java `@Entity`: phân biệt mô hình nghiệp vụ, cấu trúc lưu trữ và thiết kế lớp trong code.

## 5. Các checkpoint quan trọng

- [x] **M0 – Baseline Phase 0:** Ba tài liệu đã đồng bộ, đủ 14 lựa chọn và thẩm quyền duyệt, DEC/OQ có trạng thái/người/mốc xử lý; lưu trong commit `docs: finalize phase 0 baseline`.
- [ ] **M1 – Yêu cầu UC01 sẵn sàng:** FR/BR/AC và Use Case UC01 đủ rõ; không có OQ chặn UC01.
- [ ] **M2 – Mô hình nghiệp vụ sẵn sàng:** Domain Model nền và luồng đấu nối thống nhất.
- [ ] **M3 – Kiến trúc nền sẵn sàng:** Module, công nghệ và trách nhiệm đã rõ.
- [ ] **M4 – Thiết kế UC01 sẵn sàng:** ERD/schema, API và xử lý cấp số đồng thời → **BẮT ĐẦU CODE**.
- [ ] **M5 – UC01 hoàn thiện:** API hoạt động, migration và test đạt → chuyển UC02.
- [ ] **M6 – Hoàn thiện nghiệp vụ:** UC02–UC06, job hết hạn và job tổng hợp tiêu dùng hàng ngày có test.
- [ ] **M7 – Kiểm thử yêu cầu gốc và baseline:** Tổng ≥500K trước đo, ≥30 TPS mỗi loại đồng thời (tổng ≥60), phân nhóm 50% request trừ tiền tranh chấp, giao dịch đúng/đủ có bằng chứng.
- [ ] **M8 – Bàn giao:** Chạy được theo README/Deployment Guide.

## 6. Nhật ký cập nhật roadmap

| Ngày | Nội dung thay đổi | Phase / checkpoint |
|---|---|---|
| 14/10/2026 | Khởi tạo roadmap từ 3 tài liệu Phase 0, đánh dấu tình trạng bản nháp và mốc bắt đầu code UC01. | Phase 0 – M0 chưa chốt |
| 14/10/2026 | Chốt đủ 14 đáp án; cập nhật 3 tài liệu Phase 0 và README, lưu DEC-01–DEC-11; OQ-08 Resolved, các phần chi tiết Open có trách nhiệm/mốc; xóa phiếu tạm. Commit: `docs: finalize phase 0 baseline` (dùng Git log để lấy hash). | Phase 0 – M0 hoàn thành; Phase 1 chưa bắt đầu |

**Ghi chú:** Các trạng thái ở đây phản ánh tài liệu và thông tin đã cung cấp, **không phải xác nhận đã chạy/build/test thành công trên máy cá nhân**. Khi hoàn thành một đầu việc, cập nhật cả trạng thái, bằng chứng (file/commit/test) và các phụ thuộc liên quan.
