> **Trạng thái điều hành 26/10/2026:** Hoàn tất tiếp nhận phiếu 16; CR-CAN-002/003 và CR-TIME-001/002 có hiệu lực nghiệp vụ. Đã soạn SRS/UC01–06/jobs, UML, kiến trúc, MySQL/OpenAPI/transaction và kế hoạch test. **Các sản phẩm mới là REVIEW**, không tự nghiệm thu M4-DB hoặc M5–M8. Các mục bên dưới ghi tình trạng lịch sử cần đọc cùng cập nhật này; “chờ đáp án phiếu” trước ngày 26/10 đã được thay bằng [bản tiếp nhận](00-project/decision-receipt-20261026.md).

> **CR-DB-001 / 19-10-2026:** DBMS được chủ dự án chuyển **PostgreSQL → MySQL**; môi trường công ty dùng đọc/duyệt/code không DB, chạy service ở nhà. **M4-DB delta trên MySQL chưa duyệt** dù M4 UC01 cũ đã được duyệt. Khung chi tiết UC02–UC06 và jobs mới trong [bộ rà soát](07-business-review/README-START-HERE.md) là DRAFT để chủ dự án duyệt một lần. Không báo Phase 5 DB migration/test hoàn thành khi chưa chạy.

# Lộ trình phát triển dự án – Telecom Subscription Management System

> **File theo dõi chính (vị trí thực tế):** `docs/ROADMAP.md`
> **Dự án:** `Telecom-Management-System`
> **Ngày cập nhật:** 26/10/2026
> **Trạng thái tổng thể (26/10/2026):** M0–M4 UC01 có phê duyệt lịch sử; delta MySQL chưa nghiệm thu. P1.8/P2.6 đã đồng bộ quyết định khảo sát và CR mới thành tài liệu REVIEW cho toàn backend. Chưa có backend nghiệp vụ/runtime/tải.
> **Bước tiếp theo duy nhất:** **P4.1–P4.5 / nghiệm thu delta MySQL cho UC01** theo [checklist M4](04-design/m4-review-checklist.md), sau đó mới P5.1. Phiếu 16 đã được tiếp nhận; không chờ trả lời lại các câu nghiệp vụ đã chọn.

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
- **Không bắt buộc hoàn tất chi tiết toàn hệ thống trước code** (khung DRAFT được chuẩn bị theo CR-DOC-001): phân tích, thiết kế đủ cho UC01 để triển khai UC01; sau đó lặp theo từng chức năng.

## 2. Dashboard – Tiến độ theo Phase

| Phase | Trọng tâm | Trạng thái hiện tại | Sản phẩm / mốc chính |
|---|---|---|---|
| **0 – Project Definition & Scope** | Mục tiêu, phạm vi, thuật ngữ, câu hỏi mở | ✅ Baseline được chủ dự án có quyền nghiệm thu duyệt 14/10/2026 | M0 – Baseline phạm vi ban đầu |
| **1 – Requirements (SRS)** | FR, NFR, quy tắc, kịch bản Use Case | ✅ SRS 0.6/UC01 0.5; chủ dự án Q25=A, M1 duyệt 19/10 | M1 – Yêu cầu đủ rõ cho UC01 và kiến trúc nền |
| **2 – Analysis & UML** | Use Case, Domain Class, State, Sequence | ✅ M2 duyệt mô hình nền/UC01 theo xác nhận 19/10; mô hình UC sau làm theo vòng | M2 – Mô hình nghiệp vụ ban đầu |
| **3 – Architecture** | C4, module, quyết định kiến trúc | ✅ M3 duyệt kiến trúc nền 19/10; runtime/config ở P4/P5 | M3 – Kiến trúc nền được chọn |
| **4 – Detailed Design** | ERD, schema, Design Class, API, transaction | 🟡 M4 UC01/T1–T7 đã duyệt lịch sử; delta MySQL chưa duyệt | Chốt delta trước DB implementation UC01 |
| **5 – Implementation** | Spring Boot, JPA/JDBC, nghiệp vụ, test theo chức năng | ⬜ P5.1 sau duyệt delta MySQL; P5.2 chưa bắt đầu | M5 – UC01; M6 – đủ nghiệp vụ |
| **6 – System & Performance Testing** | Dữ liệu lớn, đồng thời, đo TPS, đối soát | 🟡 Có kế hoạch DRAFT, chưa đo | M7 – Bằng chứng đáp ứng đề bài |
| **7 – Deployment & Handover** | Đóng gói, triển khai, vận hành | 🟡 Có guide khung, chưa chạy | M8 – Có thể chạy và bàn giao |

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

### Phase 1 – Software Requirements Specification (SRS) ✅ M1 UC01; 🟡 bổ sung toàn hệ thống

**Mục tiêu:** Chuyển đề bài thành yêu cầu kiểm chứng được; ưu tiên đủ chi tiết cho UC01 trước.

- [x] **P1.1 – SRS khung toàn hệ thống (v0.6 duyệt qua Q25=A ngày 19/10):** Tạo `01-requirements/SRS.md`, gồm mục tiêu, tác nhân/hệ thống tích hợp, các nhóm FR, NFR, giả định và tham chiếu.
- [x] **P1.2 – Functional Requirements:** Gắn mã `FR-...`, đầu vào/đầu ra và điều kiện nghiệm thu cho đấu nối, gói cước, tiền, báo cáo, job.
- [x] **P1.3 – Business Rules:** Gắn mã `BR-...` cho quyền sở hữu, cấp số, hủy cuối chu kỳ, số dư, vòng đời và trường hợp biên; phần chưa chốt dẫn mã OQ.
- [x] **P1.4 – Non-Functional Requirements:** Gắn mã `NFR-...` cho tính đúng giao dịch, 500.000 bản ghi, tối thiểu 30 TPS, 50% request cùng thuê bao/tài khoản, bảo mật và vận hành. Giữ rõ các chỉ tiêu còn cần chốt cách đo.
- [x] **P1.5 – Use Case Specification cho UC01 (v0.5 Hybrid, 27 AC đã duyệt Q25=A ngày 19/10):** Trigger, tiền/hậu điều kiện, Basic Flow, Alternative Flow, Exception Flow, liên kết FR/BR và tiêu chí kiểm thử.
- [x] **P1.6 – Use Case Specification cho UC02–UC06:** Xây dựng khung/danh sách đầy đủ; đặc tả chi tiết **khi sắp triển khai từng UC**, không chặn việc code UC01. **Cập nhật 19/10:** Khung UC02–UC06.md đã có; chi tiết từng UC khi sắp code.
- [ ] **P1.7 – Đặc tả các job hàng ngày:** Job hết hạn và job tổng hợp tiêu dùng riêng; chốt trạng thái, đầu ra/chỉ tiêu/lịch và chạy lại theo OQ-07/OQ-11/OQ-13. Không bắt buộc tạo actor bên ngoài chỉ để vẽ Use Case. **Tình trạng 18/10:** Có scheduled-jobs.md; lịch/cutoff/chi tiết vẫn Open.
- [ ] **P1.8 – Rà soát tính truy vết:** Đối chiếu yêu cầu gốc ↔ FR/NFR/BR ↔ Use Case ↔ tiêu chí kiểm thử. **Tình trạng 18/10:** Có truy vết SRS và analysis.md; chưa nghiệm thu đầy đủ.

**Điều kiện chuyển:** Các yêu cầu lõi không mâu thuẫn; **UC01 đủ rõ để vẽ mô hình và lập API**, những yêu cầu chưa cần ngay được ghi rõ trạng thái. SRS tiếp tục cập nhật theo từng vòng phát triển.

### Phase 2 – Analysis & UML ✅ M2 nền UC01 đã duyệt

**Mục tiêu:** Hiểu mô hình nghiệp vụ, quan hệ và vòng đời trước khi hiện thực CSDL.

- [x] **P2.1 – Rà soát Use Case Diagram:** Tạo mới use-case-diagram.puml và uc01-use-case-diagram.puml từ tài liệu hiện có; kiểm tra actor, Hybrid, ranh giới; đã duyệt 19/10. Không tìm thấy sơ đồ cũ trong repository.
- [x] **P2.2 – Domain Class Diagram:** Vẽ **các lớp nghiệp vụ và quan hệ**, tối thiểu xét `Customer`, `Subscriber`, `ServicePackage`, `PackageSubscription`, `Account`, `Transaction`; cân nhắc `PhoneNumberInventory`. **Cập nhật 19/10:** Domain Model hiện có đã duyệt M2.
- [ ] **P2.3 – State Diagram:** Vẽ vòng đời **đăng ký gói cước** (đang hiệu lực, chờ hủy, hết hạn...), thêm thuê bao/tài khoản khi có nhu cầu thật. **Tình trạng 18/10:** Có state package-subscription.puml DRAFT, còn OQ-07/09.
- [x] **P2.4 – Sequence Diagram nghiệp vụ UC01:** Luồng chọn/cấp số, đấu nối thành công và tranh chấp cấp số. **Cập nhật 19/10:** Sequence UC01 hiện có đã duyệt M2.
- [ ] **P2.5 – Sequence Diagram các luồng rủi ro cao:** Đăng ký + trừ tiền, giao dịch đồng thời, job hết hạn; thực hiện sát thời điểm thiết kế module tương ứng.
- [ ] **P2.6 – Rà soát và đồng bộ SRS:** Nếu UML làm lộ khoảng trống nghiệp vụ thì cập nhật yêu cầu và `clarification-log.md`. **Tình trạng 18/10:** Đã ghi sai lệch sơ đồ/đường dẫn và phần Open trong log; chưa đóng.

**Điều kiện chuyển:** Có **Domain Model ban đầu cho toàn hệ thống** và luồng UC01 đủ rõ; không phải vẽ xong mọi Sequence Diagram trước khi thiết kế UC01.

### Phase 3 – Software Architecture ✅ M3 đã duyệt

**Mục tiêu:** Chọn kiến trúc phù hợp một backend Spring Boot quản lý giao dịch và dữ liệu quan hệ.

- [x] **P3.1 – Architecture Document:** Tạo `03-architecture/architecture.md`, nêu kiến trúc nền, các module và trách nhiệm. **Cập nhật 19/10:** Kiến trúc nền đã duyệt M3; chi tiết runtime chuyển P4/P5.
- [x] **P3.2 – C4 System Context:** Thể hiện TelecomManagement, hệ thống CSKH, bên dùng báo cáo và ranh giới. **Cập nhật 19/10:** C4 context đã duyệt M3.
- [x] **P3.3 – C4 Container:** Thể hiện API/backend, database, job nội bộ và công cụ kiểm thử (không tự mặc định microservices). **Cập nhật 19/10:** C4 container đã duyệt M3; Docker local topology mới là đề xuất P5/P7.
- [x] **P3.4 – Package/Module Structure:** Quy định các module như subscriber, package, account, transaction, reporting, scheduling; tránh phụ thuộc vòng. **Cập nhật 19/10:** Module structure đã duyệt M3.
- [x] **P3.5 – Architecture Decision Records (ADR):** Ghi quyết định quan trọng **khi thực sự phát sinh**, chẳng hạn PostgreSQL, cơ chế giao dịch, JPA/JDBC, chiến lược khóa. **Cập nhật 19/10:** ADR-001 đã Accepted, M3 được duyệt riêng theo xác nhận chủ dự án.

**Điều kiện chuyển:** Xác định công nghệ, ranh giới module và nguyên tắc transaction đủ rõ để thiết kế UC01.

### Phase 4 – Detailed Design ✅ M4 UC01 lịch sử; 🟡 delta MySQL chờ duyệt

**Mục tiêu:** Biến mô hình nghiệp vụ thành thiết kế kỹ thuật; triển khai dần theo UC.

- [x] **P4.1 – ERD bản đầu:** Vẽ mô hình dữ liệu tổng thể ở mức quan hệ; làm chi tiết bảng phục vụ **UC01** trước. **Lịch sử 18/10:** chưa có ERD vật lý. **Cập nhật 19/10:** Bản thiết kế UC01 đã được chủ dự án duyệt M4 (T1–T7). Kiểm chứng migration/test thuộc P5.
- [x] **P4.2 – Database Schema:** Tạo `04-design/database-schema.md`; chốt khóa, FK, unique, index, trạng thái và ràng buộc cho UC01; mở rộng khi triển khai module khác. **Lịch sử 18/10:** chưa có schema chi tiết. **Cập nhật 19/10:** Bản thiết kế UC01 đã được chủ dự án duyệt M4 (T1–T7). Kiểm chứng migration/test thuộc P5.
- [x] **P4.3 – API Contract UC01:** Khai báo tra cứu số khả dụng, cấp số tự động/đấu nối, request/response, lỗi và các trường hợp đồng thời trong `04-design/openapi.yaml`. **Lịch sử 18/10:** chưa có OpenAPI. **Cập nhật 19/10:** Bản thiết kế UC01 đã được chủ dự án duyệt M4 (T1–T7). Kiểm chứng migration/test thuộc P5.
- [x] **P4.4 – Design Class Diagram UC01:** Thiết kế class Java thực sự cần (Entity, Repository, Service, Controller, DTO), không lặp lại toàn bộ Domain Diagram. **Lịch sử 18/10:** chưa có Design Class Diagram. **Cập nhật 19/10:** Bản thiết kế UC01 đã được chủ dự án duyệt M4 (T1–T7). Kiểm chứng migration/test thuộc P5.
- [x] **P4.5 – Concurrency & Transaction Design:** Phác thảo cách chống cấp trùng cho UC01. Trước UC04/UC05, **bắt buộc** chốt xử lý đồng thời trên cùng tài khoản, chống xử lý trùng và đối soát; bổ sung `04-design/concurrency-and-jobs.md`. **Lịch sử 18/10:** chưa có thuật toán cụ thể. **Cập nhật 19/10:** Bản thiết kế UC01 đã được chủ dự án duyệt M4 (T1–T7). Kiểm chứng migration/test thuộc P5.
- [ ] **P4.6 – Thiết kế module tiếp theo:** Mở rộng ERD, API, Design Class và Sequence kỹ thuật khi chuyển sang từng UC.

**🚩 MỐC BẮT ĐẦU CODE (M4):** Khi **P4.1–P4.4 cho UC01** và xử lý tranh chấp cấp số ở P4.5 đủ rõ → **dừng thiết kế tổng thể quá mức, bắt đầu triển khai UC01**.

### Phase 5 – Implementation & Developer Testing ⬜

**Mục tiêu:** Code theo từng chức năng, chạy được và có bằng chứng kiểm thử.

- [ ] **P5.1 – Khởi tạo backend:** Kiểm tra Maven import/build, Java 21, Spring Boot, MySQL theo CR-DB-001, cấu hình môi trường, Flyway và cấu trúc package. **Repo thực có pom.xml và Main in thông báo; pom vẫn dùng PG driver/Flyway/Testcontainers, chưa có backend nghiệp vụ/config/migration/Compose/test suite. Lịch sử thử build/ZIP không thay kiểm chứng mới; P5.1 chưa hoàn tất.**
- [ ] **P5.2 – UC01 – Đấu nối thuê bao:** Entity, migration, Repository, Service, Controller, validation, API và test cấp số/đấu nối.
- [ ] **P5.3 – UC02 – Đăng ký gói cước:** Thiết kế bổ sung → code → Unit/Integration Test.
- [ ] **P5.4 – UC03 – Hủy gói cước:** Xử lý yêu cầu hủy vào cuối chu kỳ → test trạng thái và thời gian.
- [ ] **P5.5 – UC04 – Cộng tiền:** Transaction nhất quán, lưu lịch sử → test.
- [ ] **P5.6 – UC05 – Trừ tiền:** Kiểm tra số dư, atomicity, đồng thời, request trùng/retry → test nhiều luồng trên MySQL InnoDB thật.
- [ ] **P5.7 – UC06 – API tra cứu báo cáo:** Lọc theo thời gian/gói, phân trang nếu được chốt → test.
- [ ] **P5.8 – Job xử lý hết hạn:** Soft delete/trạng thái theo quyết định nghiệm thu, job hàng ngày, xử lý lại an toàn → test.
- [ ] **P5.9 – Đồng bộ tài liệu:** Cập nhật OpenAPI, schema, sơ đồ và hướng dẫn chạy theo những gì thực tế đã triển khai.
- [ ] **P5.10 – Job tổng hợp tiêu dùng hàng ngày:** Chỉ tiêu/đầu ra/lịch theo SRS; chạy lại an toàn và kiểm thử dữ liệu tổng hợp (DEC-07).

**Nguyên tắc thực hiện cho từng UC:** `Làm rõ yêu cầu → Thiết kế vừa đủ → Code → Unit/Integration Test → Review → Cập nhật docs`.

**Điều kiện chuyển:** Các chức năng bắt buộc chạy được, vượt qua kiểm thử nghiệp vụ và có API/DB phù hợp thiết kế đã cập nhật.

### Phase 6 – System, Concurrency & Performance Testing 🟡 Kế hoạch

**Mục tiêu:** Chứng minh hệ thống đáp ứng các yêu cầu bắt buộc, không chỉ “API chạy được”.

- [ ] **P6.1 – Test Plan:** Tạo `05-testing/test-plan.md`, xác định môi trường, dữ liệu, tiêu chí thành công, loại lỗi và phương pháp ghi kết quả. **Tình trạng 18/10:** Có test-plan.md DRAFT; chưa thực thi.
- [ ] **P6.2 – Functional/Integration Regression:** Kiểm tra luồng chính, nhánh thay thế và ngoại lệ giữa các module.
- [ ] **P6.3 – Data Generator:** Tổng các bảng nghiệp vụ có **tối thiểu 500.000 bản ghi trước đo tải**, phân bố đủ chạy nghiệp vụ hợp lệ; lưu thống kê từng bảng (DEC-08).
- [ ] **P6.4 – Performance Tool:** Xây tool hoặc kịch bản k6 phát tải đã chọn; đối soát riêng và lưu kết quả (không nhất thiết là Maven dependency của backend).
- [ ] **P6.5 – Throughput Test:** Cùng chạy đăng ký gói **≥30 TPS** và trừ tiền **≥30 TPS**, tổng **≥60 TPS thành công mới**; không đếm retry trả kết quả cũ và không đếm hai lần phí đăng ký như một lệnh trừ tiền độc lập. Điều kiện đo chi tiết chốt tại OQ-04.
- [ ] **P6.6 – Contention Test:** **50% tổng request trừ tiền** chia vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp; chứng minh chồng lấn thực tế; đã chọn 10 cặp/50% debit attempts kể cả retry theo DEC-PRE-23; còn protocol và bằng chứng overlap (DEC-10).
- [ ] **P6.7 – Transaction Reconciliation:** Đối chiếu **từng request/giao dịch**, số dư đầu/cuối, số nghiệp vụ thành công/thất bại, retry và timeout; không chỉ cộng trừ tổng số tiền.
- [ ] **P6.8 – Performance Test Report:** Tạo `05-testing/performance-test.md` (kế hoạch/kịch bản) và báo cáo kết quả thực đo, thông số máy, truy vấn, biểu đồ, lỗi và giới hạn. **Tình trạng 18/10:** Có performance-test.md là kế hoạch, chưa có kết quả thực đo.

**Điều kiện chuyển:** Có kết quả đo tái lập được, chứng minh tính đúng/đủ và mức đạt hoặc chưa đạt của từng chỉ tiêu; nếu chưa đạt thì tối ưu và chạy lại.

### Phase 7 – Deployment & Handover 🟡 Khung hướng dẫn

**Mục tiêu:** Người khác có thể dựng và kiểm tra hệ thống theo hướng dẫn.

- [ ] **P7.1 – Build & Packaging:** Đóng gói executable JAR, cấu hình profile, biến môi trường.
- [ ] **P7.2 – Docker/Compose:** Chạy backend và MySQL theo cấu hình triển khai được kiểm tra.
- [ ] **P7.3 – Database Migration & Seed:** Khởi tạo schema và dữ liệu mẫu/kiểm thử có hướng dẫn.
- [ ] **P7.4 – Logging/Health/Recovery:** Kiểm tra health, log giao dịch phù hợp, cách khởi động lại job và sao lưu/phục hồi cơ bản.
- [ ] **P7.5 – Deployment Guide:** Viết `06-deployment/deployment-guide.md` với hướng dẫn từ máy mới đến khi gọi thử API. **Tình trạng 18/10:** Có deployment-guide.md dạng khung; chưa được chạy từ máy sạch.
- [ ] **P7.6 – README & Handover:** Cập nhật `README.md`, link tài liệu, lệnh build/test, các hạn chế còn lại và báo cáo nghiệm thu.

**Điều kiện hoàn tất:** Có thể cài đặt, chạy, kiểm thử và đọc tài liệu mà không cần phụ thuộc vào thao tác chưa được ghi lại.

---

## 4. Danh mục tài liệu và sơ đồ (tạo khi đến bước)

| Đường dẫn dự kiến (tương đối từ `docs/`) | Loại | Phase | Trạng thái |
|---|---|---|---|
| [`00-project/overview.md`](00-project/overview.md) | Markdown | 0 | ✅ Baseline v1.1 đã duyệt |
| [`00-project/scope-and-glossary.md`](00-project/scope-and-glossary.md) | Markdown | 0 | ✅ Baseline v1.1 đã duyệt |
| [`00-project/clarification-log.md`](00-project/clarification-log.md) | Markdown | 0 | ✅ Baseline v1.0; chi tiết còn mở có kế hoạch |
| `01-requirements/SRS.md` | Markdown | 1 | ✅ v0.6 đã duyệt M1 ngày 19/10 |
| `01-requirements/use-cases/UC01-Subscriber-Activation.md` | Markdown | 1 | ✅ v0.5/27 AC đã duyệt M1; UC02–UC06 có khung riêng |
| `02-analysis/use-case-diagram.puml` | UML | 2 | ✅ Đã duyệt M2 theo xác nhận 19/10 |
| `02-analysis/domain-model.puml` | UML Class mức phân tích | 2 | ✅ Đã duyệt M2 |
| `02-analysis/state-diagrams/` | UML State | 2 | 🟡 State đăng ký gói DRAFT |
| `02-analysis/sequence-diagrams/` | UML Sequence | 2 | ✅ UC01 đã duyệt M2; UC sau bổ sung theo vòng |
| `03-architecture/architecture.md` | Markdown | 3 | ✅ Đã duyệt M3 |
| `03-architecture/c4-diagrams/` | C4 | 3 | ✅ C4 đã duyệt M3 |
| `03-architecture/adr/` | Quyết định kiến trúc (khi cần) | 3 | 🟡 ADR-001 Accepted trong phạm vi stack/công cụ |
| `04-design/erd.puml` | ERD | 4 | ✅ Thiết kế UC01 được duyệt M4 19/10 |
| `04-design/database-schema.md` | Markdown | 4 | ✅ Thiết kế UC01 được duyệt M4 19/10 |
| `04-design/design-class-diagrams/` | UML Class mức thiết kế | 4 | ✅ Thiết kế UC01 được duyệt M4 19/10 |
| `04-design/openapi.yaml` | OpenAPI | 4–5 | ✅ Hợp đồng UC01 được duyệt M4; API runtime chưa có |
| `04-design/concurrency-and-jobs.md` | Markdown | 4–5 | 🟡 Có thuật toán đề xuất UC01, chờ review/test thực tế |
| `05-testing/test-plan.md` | Markdown | 6 | 🟡 Kế hoạch DRAFT |
| `05-testing/performance-test.md` | Markdown | 6 | 🟡 Kế hoạch, chưa đo |
| `06-deployment/deployment-guide.md` | Markdown | 7 | 🟡 Khung, chưa chạy |
| `../README.md` | Markdown | Xuyên suốt | 🟡 Đã có file, nội dung cần tiếp tục cập nhật |

> **Không cần tạo trước mọi thư mục/file ở bảng trên.** `Domain Class Diagram` ≠ `ERD` ≠ `Design Class Diagram` ≠ Java `@Entity`: phân biệt mô hình nghiệp vụ, cấu trúc lưu trữ và thiết kế lớp trong code.

## 5. Các checkpoint quan trọng

- [x] **M0 – Baseline Phase 0:** Ba tài liệu đã đồng bộ, đủ 14 lựa chọn và thẩm quyền duyệt, DEC/OQ có trạng thái/người/mốc xử lý; lưu trong commit `docs: finalize phase 0 baseline`.
- [x] **M1 – Yêu cầu UC01 sẵn sàng:** Có SRS v0.6 + UC01 v0.5, đủ 5 quyết định DEC-UC01-01–05 và đã chốt CR-UC01-001 Hybrid; **Q25=A được chủ dự án xác nhận ngày 19/10/2026**; các OQ kỹ thuật được hoãn theo P4.
- [x] **M2 – Mô hình nghiệp vụ sẵn sàng:** Domain Model nền và luồng đấu nối thống nhất.
- [x] **M3 – Kiến trúc nền sẵn sàng:** Module, công nghệ và trách nhiệm đã rõ.
- [x] **M4 – Thiết kế UC01 sẵn sàng:** Chủ dự án duyệt T1–T7 ngày 19/10/2026, chốt ERD/schema/API/lock/recovery UC01 lịch sử. **Delta MySQL chưa duyệt** theo CR-DB-001; không suy ra phần DB P5 đã sẵn sàng từ M4 PG cũ; triển khai/test chưa có bằng chứng.
- [ ] **M5 – UC01 hoàn thiện:** API hoạt động, migration và test đạt → chuyển UC02.
- [ ] **M6 – Hoàn thiện nghiệp vụ:** UC02–UC06, job hết hạn và job tổng hợp tiêu dùng hàng ngày có test.
- [ ] **M7 – Kiểm thử yêu cầu gốc và baseline:** Tổng ≥500K trước đo, ≥30 TPS mỗi loại đồng thời (tổng ≥60), phân nhóm 50% request trừ tiền tranh chấp, giao dịch đúng/đủ có bằng chứng.
- [ ] **M8 – Bàn giao:** Chạy được theo README/Deployment Guide.

## 6. Nhật ký cập nhật roadmap

| Ngày | Nội dung thay đổi | Phase / checkpoint |
|---|---|---|
| 14/10/2026 | Khởi tạo roadmap từ 3 tài liệu Phase 0, đánh dấu tình trạng bản nháp và mốc bắt đầu code UC01. | Phase 0 – M0 chưa chốt |
| 14/10/2026 | Chốt đủ 14 đáp án; cập nhật 3 tài liệu Phase 0 và README, lưu DEC-01–DEC-11; OQ-08 Resolved, các phần chi tiết Open có trách nhiệm/mốc; xóa phiếu tạm. Commit: `docs: finalize phase 0 baseline` (dùng Git log để lấy hash). | Phase 0 – M0 hoàn thành; Phase 1 chưa bắt đầu |
| 15/10/2026 | Tiếp nhận phiếu REV-SRS-001 31/31 đáp án; tổng hợp SRS v0.2, log v1.1, UC01 draft và các câu hỏi bổ sung; Phase 1 đang thực hiện, M1 chưa đạt. | Phase 1 – P1.1–P1.5 đang rà soát |
| 15/10/2026 | Tiếp nhận 5/5 đáp án REV-UC01-002; SRS v0.3, log v1.2, UC01 v0.2 (16 AC); chờ chủ dự án duyệt mốc M1. Không chuyển các checklist sang hoàn thành khi chưa có phê duyệt. | Phase 1 – M1 đang chờ duyệt |
| 15/10/2026 | **CR-UC01-001 được chủ dự án duyệt riêng**: chuyển UC01 sang Hybrid; luồng đồng bộ chính, chỉ WAITING 24h khi cấp tự động hết kho và khách đồng ý chờ. SRS v0.4, UC01 v0.3 (19 AC), log v1.3; M1 chưa nghiệm thu. | Phase 1 – P1.1–P1.5 đang rà soát; M1 pending |

| 18/10/2026 | CR-DOC-001: sổ quyết định độc lập lưu đầy đủ 31+5 lựa chọn; SRS 0.5/UC01 0.4 bỏ phụ thuộc review. Ban đầu giữ thư mục lồng nhau theo working tree; sau đó chuẩn hóa về docs/ theo yêu cầu chủ dự án. Tạo mới UML DRAFT; khung kiến trúc/thiết kế/kế hoạch test/bàn giao; chưa có ERD/OpenAPI/Design Class hoàn chỉnh. Xem chỉ mục và verification.md. | M1 chờ duyệt; M2–M4 chưa đạt, không code |


**Ghi chú:** Trạng thái dựa trên tài liệu và bằng chứng được ghi; không phải xác nhận chạy nghiệp vụ/build/test trong lượt tài liệu.

## 7. Kiểm kê lịch sử và phạm vi DRAFT 18/10/2026

Đường dẫn thực tế: docs/. Đã đưa toàn bộ tài liệu khỏi thư mục docs lồng nhau theo yêu cầu chủ dự án ngày 18/10/2026; đường dẫn hiện khớp cấu trúc AGENTS.md.

- [Chỉ mục bộ tài liệu](README.md): liệt kê sản phẩm hiện có.
- [Phiếu quyết định còn thiếu](00-project/decision-questionnaire.md): 25 câu; đã nhận Q01–Q24, Q25 còn trống. Nội dung quyết định đầy đủ trong log mục13.
- [Thuyết minh UML](02-analysis/analysis.md): UML nghiệp vụ tạo mới, chưa nghiệm thu.
- [Kế hoạch triển khai](04-design/implementation-plan.md): UC01 trước, chỉ bắt đầu sau M4.
- [Kiểm chứng tài liệu](verification.md): kết quả kiểm tra thực tế, không thay phê duyệt nghiệp vụ.

Bảng danh mục mục 4 là đích sản phẩm, không chứng minh file ERD/OpenAPI đã tồn tại. Các mục có khung được nêu rõ trong checklist và chỉ mục, vẫn để unchecked cho đến khi đáp ứng điều kiện hoàn tất.


**Cập nhật cấu trúc 18/10/2026 — CR-DOC-002:** Đã chuẩn hóa về một thư mục docs/, di chuyển 39 file và cập nhật tham chiếu. M1–M8 giữ nguyên trạng thái; bước tiếp theo vẫn P1.5: đọc bộ đồng bộ rồi trả lời Q25.


## 8. Kết quả đồng bộ trước chủ dự án duyệt — 18/10/2026

- Đã nhận 24/24 lựa chọn Q01–Q24, giữ Q25 chưa chọn; DEC-PRE-01–24 chứa nội dung đầy đủ trong clarification log1.5.
- SRS0.6, UC01 0.5 có27 AC; khung UC02–06/job, UML, kiến trúc/design/test/deployment đã đồng bộ phần đã chọn.
- CR-ARCH-001: RabbitMQ/Redis theo quyền chủ dự án giao; ADR-001 Accepted về công cụ, không đánh dấu M3/M4.
- Xóa hai phiếu review cũ theo CR-DOC-003; lịch sử quyết định giữ tại log mục8–12. Không tạo phụ thuộc đọc vào phiếu đã xóa.
- Còn đề xuất cần duyệt: retention lệnh tiền 30 ngày (R2), max lệnh 10 triệu/max số dư 100 triệu và hạn/danh mục phụ (R3). Đây chưa là tiêu chuẩn đã chốt, không chặn UC01 nếu hoãn đúng mốc.
- [Hướng dẫn đọc/duyệt](00-project/review-guide.md) nêu thứ tự đọc, R1 hồ sơ và R4 hủy/gia hạn, phần hoãn và việc cần làm.
- M1–M8 vẫn unchecked, không sửa code/pom.xml, không commit/push. Bằng chứng kiểm tra ở verification.md.


## 9. Lịch sử đồng bộ checkpoint 19/10/2026 – trước CR-DB-001

**Nội dung môi trường/P5 ở mục 9 là lịch sử trước đổi DB. Hướng hiện hành xem mục 10 và phần cập nhật 24/10; không dùng Supabase/PG để cấu hình mới.**

- **M1 đã duyệt**: người dùng chọn Q25=A trong ảnh `decision-questionnaire.md` (không yêu cầu trả lời lại). Phiên bản được duyệt SRS 0.6 / UC01 0.5; 27 AC; các OQ hoãn đúng mốc.
- **M2/M3 đã duyệt**: người dùng xác nhận “các file khác tôi cũng chốt” với bộ tài liệu UML/kiến trúc hiện có. Đây là phê duyệt tài liệu, không phải bằng chứng triển khai kỹ thuật.
- **M4 đã được chủ dự án duyệt ngày 19/10/2026** cho UC01, gồm `erd.puml`, `openapi.yaml`, `database-schema.md`, `design-class-diagrams/uc01-design-class.puml` và `concurrency-and-jobs.md`. Các T1–T7 có hiệu lực; kiểm chứng runtime/migration thuộc P5.
- **Tiếp tục P5.1 ngay:** Maven backend, cấu hình profile office/home; Compose chỉ ở máy nhà. M4 đã duyệt nên được phép code UC01 khi xong khởi tạo. Không tự tick P5.1 nếu thiếu build/migration/healthcheck thực tế.
- Các trạng thái “Q25 chưa chọn” trong lịch sử cũ chỉ phản ánh ảnh chụp 18/10/2026; trạng thái có hiệu lực là bảng này và `clarification-log.md` mục 14.


### Cập nhật bổ sung ngày 19/10/2026 – bước sang P5.1

- Chủ dự án đã **duyệt T1–T7 và M4 của UC01**; không mở lại các quyết định đó khi bắt đầu code.
- Máy công ty không có Docker: `office` chạy JVM, dùng Supabase PostgreSQL qua SSL và session pooler; RabbitMQ/Redis được phép không có trong profile dev giới hạn; cần IdP/JWKS hợp lệ trước khi nghiệm thu REST API có xác thực.
- Máy nhà có Docker: `home` chạy cùng Java code và Flyway migrations trên PostgreSQL/RabbitMQ/Redis/Keycloak qua Compose.
- Không so sánh số TPS với môi trường Supabase chia sẻ/truy cập qua Internet để kết luận đã đạt M7; test concurrency/latency phải tái lập trên môi trường riêng.
- **P5.1 chưa tick:** File khởi tạo và Compose là điểm bắt đầu, chỉ tick sau `mvn verify`, migration và healthcheck thực tế đạt.


## 10. CR-DB-001 – Luồng công việc mới ngày 19/10/2026

- [x] Chủ dự án quyết định chuyển DBMS sang MySQL; làm tài liệu/ý tưởng ở công ty, chạy service ở nhà.
- [x] Có bộ hồ sơ review chi tiết UC02–UC06/jobs + MySQL design DRAFT: `07-business-review/`.
- [ ] Chủ dự án đọc và lựa chọn phiếu `07-business-review/16-PHIEU-DUYET-TONG-HOP.md`.
- [ ] Đồng bộ SRS/UC02–06/Job/UML/ERD/Schema/API theo các quyết định mới, xử lý OQ.
- [ ] Chốt delta DB/MySQL (M4-DB) và migration plan; **không sử dụng `starter/` PostgreSQL**.
- [ ] Code P5.1 ở nhà: Spring Boot + MySQL Compose/Flyway; kiểm chứng runtime.
- [ ] Triển khai/test mỗi UC theo thứ tự trong Roadmap; nghiệm thu bằng bằng chứng thực tế.
- [ ] Sau duyệt nội dung toàn hệ thống, xuất **Báo cáo nghiệp vụ tổng thể**; sau code/test thêm phần kết quả triển khai.

## Rà soát theo yêu cầu chủ dự án – 24/10/2026

- **Yêu cầu:** kiểm tra toàn bộ tài liệu theo đề bài, bổ sung trường hợp ngoại lệ, tạo phiếu trắc nghiệm cụ thể dễ hiểu cho phần cần quyết định.
- **P1.8/P2.6:** có [báo cáo chất lượng](07-business-review/18-RA-SOAT-CHAT-LUONG.md), [64 kịch bản truy vết/ngoại lệ](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md), [quy tắc chung DRAFT](07-business-review/20-QUY-TAC-CHUNG-VA-BIEN-NGHIEP-VU_DRAFT.md); phiếu 16 v0.2 bổ sung ví dụ/hệ quả/Q21–34. Chưa nghiệm thu các đề xuất.
- **Sai lệch đã đồng bộ:** chỉ mục/trạng thái M1–M4, MySQL thay hướng Supabase/PG trong tài liệu vận hành; nội dung PG chi tiết và hình cũ là lịch sử không dùng làm migration MySQL.
- **Còn thiếu:** đáp án nghiệp vụ; delta MySQL UC01; sequence/ERD/API cho các UC sau sau khi chốt; code/migration/test/runtime/đo tải. Không tick P1.7/P1.8/P2.3/P2.5/P2.6/P4.6/P5–P7 chỉ vì đã tạo draft.
- **Điều kiện tiếp tục:** thu đáp án và ghi DEC/CR trước khi dùng như quy tắc; ưu tiên phần phụ thuộc UC01, không bắt toàn bộ UC sau đóng cùng lúc.

## Chỉnh lý đề xuất Q07 – 25/10/2026 (P1.7/P1.8)

- Chủ dự án yêu cầu suy xét và sửa cách tổ chức tác vụ gia hạn trong phiếu16. Q07 v0.3 đề xuất **một JOB-01 định kỳ xử lý vòng đời**, không worker gia hạn riêng + một lượt quét bù00:05.
- **CR-JOB-001 còn PROPOSED** tại clarification log mục16; A cũ có dấu chọn được lưu nguyên lịch sử, A mới chờ xác nhận. Lịch baseline JOB-01 00:05, JOB-02 00:30 trong SRS chưa bị tự thay; Q25 vẫn chờ quyết định chính sách thu muộn.
- Đã đồng bộ cách đọc đề xuất ở phiếu16/review09/quy tắc20; không đổi Phase/checkpoint, không code. Sau xác nhận A mới và Q25 mới cập nhật baseline/lịch/test tương ứng; không để câu hỏi UC sau chặn UC01.


## Chốt Q07 phương án ban đầu – 25/10/2026

- **DEC-JOB-001 Approved**, người duyệt: chủ dự án, xác nhận “nếu thế chốt phương án như ban đầu đi vậy”. Worker gia hạn kiểm tra thường xuyên + JOB-01 vẫn00:05 hằng ngày xử lý hết hạn/hủy/rà soát, dùng chung lõi xử lý chống phí trùng. JOB-02 giữ00:30.
- **CR-JOB-001 NOT ADOPTED**: không áp đề xuất hợp nhất thành một JOB-01 thường xuyên; không chọn hẹn deadline riêng. Phiếu16 v0.4 đánh dấu Q07=A theo xác nhận, giữ lịch sử các bản cũ.
- Đồng bộ SRS/scheduled-jobs/review09/quy tắc20 và log mục17. Khoảng chạy worker chưa chốt; Q25 thu muộn vẫn mở. Không tick thêm P1.7/OQ/Phase/checkpoint, không triển khai code/test.


## Làm rõ phiếu Q09–Q34 – 25/10/2026 (P1.8)

Theo yêu cầu chủ dự án, phiếu16 v0.5 viết lại26 câu từ Q09 tới Q34 bằng ví dụ và kết quả cụ thể của từng lựa chọn, giữ ý nghĩa/đáp án đã điền. Q07/DEC-JOB-001 không đổi; chưa chọn chính sách chuyển GAME→MAIN và chưa duyệt thêm quy tắc. Sổ quyết định mục18 ghi phạm vi biên tập. Tiếp theo chủ dự án đọc/điền phần còn mở; không tick thêm Phase/checkpoint vì đổi cách diễn đạt.

## Cập nhật điều hành — 26/10/2026

| Đầu việc | Sản phẩm hiện có | Trạng thái đúng phạm vi |
|---|---|---|
| P1.8/P2.6 đồng bộ khảo sát | Bản tiếp nhận, 4 CR, SRS, UC01–06, jobs và truy vết | Đã biên soạn và đối chiếu; nội dung thiết kế mới chờ nghiệm thu |
| P2.1–P2.5 bổ sung UML | Use Case, Domain, State, Sequence toàn backend | REVIEW; nguồn và ảnh phải được kiểm cùng manifest trong verification |
| P3 kiến trúc | C4/module/security/runtime và ADR hiện hành MySQL | REVIEW phần cập nhật; giữ M3 UC01 lịch sử |
| P4 thiết kế chi tiết | Schema/ERD, Design Class, OpenAPI, transaction/jobs | M4-DB chưa duyệt; dùng checklist hiện hành |
| P6/P7 chuẩn bị | Kế hoạch test/tải và deployment guide | Kế hoạch, chưa có kết quả đo hoặc triển khai |

**Bước tiếp theo duy nhất:** chủ dự án review/duyệt delta MySQL UC01 theo checklist M4; khi đủ điều kiện mới P5.1 khởi tạo backend/migration/test trên MySQL thật. Không yêu cầu duyệt toàn bộ UC sau để triển khai UC01. Người duyệt/ngày cho bản delta: chưa có. Không commit/push/code nghiệp vụ trong lượt biên soạn này.
