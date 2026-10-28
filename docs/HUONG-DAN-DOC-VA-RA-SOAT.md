> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Hướng dẫn đọc và rà soát bộ tài liệu dự án

**Mã:** GUIDE-REVIEW-001 · **Phiên bản:** 1.0 · **Ngày:** 26/10/2026.  
**Đối tượng:** chủ dự án, người rà soát nghiệp vụ, người thiết kế và người chuẩn bị triển khai backend.

Tài liệu này trả lời hai câu hỏi: **nên đọc file nào trước** và **đọc đến đâu thì phải kiểm tra điều gì**. [Báo cáo tổng hợp có hình UML](BAO-CAO-TONG-HOP-DU-AN.md) giúp hình dung toàn dự án trong một file. Các đặc tả riêng vẫn là nơi tra chi tiết và cập nhật quy tắc.

## 1. Bắt đầu từ đâu?

Báo cáo v1.1 đưa luồng nghiệp vụ lên trước: chương 1 có Activity tổng thể và xử lý lỗi/retry; chương 2 là ví dụ tiền/thời gian; chương 3–8 có kịch bản chuẩn, nhánh và ngoại lệ từng UC/job; chương 9 là Class Diagram. Đọc phần đó trước rồi mới đi vào yêu cầu/kiến trúc/DB ở các chương sau. Ảnh PNG dùng đường dẫn tương đối, có link SVG và PlantUML cạnh hình.

Nếu mới quay lại dự án, đọc theo thứ tự dưới đây. Không cần đọc toàn bộ clarification log từ đầu ngay lần đầu tiên vì file đó chứa cả quyết định lịch sử đã được thay thế.

1. Đọc phần trạng thái mới nhất đầu [ROADMAP](ROADMAP.md): biết đang ở mốc nào, mốc nào đã duyệt và việc nào chưa được thực hiện.
2. Đọc [báo cáo tổng hợp](BAO-CAO-TONG-HOP-DU-AN.md): từ mục tiêu đến các luồng, kiến trúc, dữ liệu, kiểm thử và bàn giao. Dừng ở mỗi hình để đối chiếu với lời giải thích ngay bên cạnh.
3. Đọc [bản tiếp nhận phiếu](00-project/decision-receipt-20261026.md): biết lựa chọn nào đang có hiệu lực. Khi cần giải thích vì sao khác phiếu gốc, mở CR được dẫn kèm.
4. Quay lại các tài liệu chi tiết theo lộ trình tại mục 3 của hướng dẫn này. Không cố nhớ mọi cột DB hoặc mọi mã lỗi API ở lần đọc đầu.
5. Dùng checklist ở mục 5–8 để rà soát chéo. Chỗ chưa khớp phải được ghi thành nhận xét có nguồn, tình huống và kết quả mong muốn cụ thể.

**Kết quả cần có sau lượt đọc đầu:** bạn giải thích được một khách hàng đi từ đấu nối → có tài khoản → nạp tiền → đăng ký gói → tiêu dùng → mua nối/hủy/hết hạn → xuất hiện trên báo cáo; đồng thời biết backend làm gì khi thiếu số, thiếu tiền hoặc mất phản hồi.

## 2. Hiểu đúng hiệu lực của tài liệu

| Nhãn/nội dung | Có nghĩa gì? | Không được suy ra điều gì? |
|---|---|---|
| Quyết định nghiệp vụ đã duyệt | Chủ dự án đã chọn cách xử lý, được ghi trong DEC/CR/bản tiếp nhận | Mọi bảng/cột/index hoặc cách cài đặt đã được duyệt |
| REVIEW / DRAFT | Có phương án cụ thể để đọc, phản biện và nghiệm thu | Đã được quyền bỏ qua review hoặc đã chạy đúng |
| M0–M4 UC01 lịch sử | Phê duyệt có phạm vi và thời điểm cụ thể, được giữ trong roadmap/log | Delta MySQL và mọi UC sau đã tự được nghiệm thu |
| Kiểm tra tài liệu đạt | Link, OpenAPI, render/hashes hoặc cấu trúc được kiểm thành công | API, transaction, hiệu năng hoặc triển khai thực tế đã đạt |
| NOT_RUN | Có ca kiểm thử dự kiến nhưng chưa thực thi | Đã PASS hoặc đang có lỗi thực tế |

Thứ tự áp dụng khi hai đoạn có vẻ mâu thuẫn: xác nhận mới rõ ràng của chủ dự án đã được ghi nhận → DEC/CR có hiệu lực → SRS/UC đã nghiệm thu → thiết kế đang REVIEW → đề xuất chưa được chọn. Với thay đổi đã duyệt, xem phần mới nhất của [clarification log](00-project/clarification-log.md), không chỉ nhìn một bảng lịch sử phía trên.

Phiếu 16 đã điền được giữ nguyên. Q26/Q34=B trong phiếu là lịch sử; hai nội dung đã đổi theo CR-TIME-001/002. Q24 được bổ sung CR-CAN-003. Không cần điền lại phiếu để phản ánh những xác nhận đã ghi nhận này.

## 3. Lộ trình đọc chi tiết theo phase

### Lượt A — Hiểu đề bài và ranh giới, Phase 0

**Thứ tự:** [đề bài gốc](requirement.md) → [overview](00-project/overview.md) → [scope và glossary](00-project/scope-and-glossary.md) → [bản tiếp nhận](00-project/decision-receipt-20261026.md) → phần DEC/CR/OQ liên quan trong [log](00-project/clarification-log.md).

**Cần trả lời được:** khách hàng khác thuê bao thế nào; gói khác kỳ gói thế nào; MAIN khác tài khoản phụ thế nào; “đấu nối” trong dự án có phải kích hoạt mạng thật không; “xóa dữ liệu hết hạn” được hiểu ra sao; những hệ thống nào nằm ngoài phạm vi.

**Điểm rà soát:** Customer–Subscriber là 1–N; không ghép hồ sơ bằng tên/ngày sinh; soft delete giữ lịch sử; không thêm ngân hàng, tính cước mạng, UI hoặc module cấp JWT nội bộ ngoài phạm vi. Đề bài phải giữ nguyên; cách diễn giải được ghi riêng.

### Lượt B — Biết hệ thống phải làm đúng điều gì, Phase 1

**Thứ tự:** [SRS](01-requirements/SRS.md) → [UC01](01-requirements/use-cases/UC01-Subscriber-Activation.md) → [UC02](01-requirements/use-cases/UC02-Package-Subscription.md) → [UC03](01-requirements/use-cases/UC03-Package-Cancellation.md) → [UC04](01-requirements/use-cases/UC04-Account-Credit.md) → [UC05](01-requirements/use-cases/UC05-Account-Debit.md) → [UC06](01-requirements/use-cases/UC06-Consumption-Query.md) → [jobs](01-requirements/scheduled-jobs.md).

Đọc từng UC theo thứ tự: mục tiêu → tiền điều kiện → dữ liệu → luồng chính → nhánh thay thế → ngoại lệ → hậu điều kiện → AC. Mỗi lần chỉ theo một tình huống từ đầu tới cuối; sau đó đổi một điều kiện để xem nhánh lỗi.

**Điểm rà soát:** có kết quả rõ khi đầu vào sai, hết số, thiếu tiền, gói/account hết hạn, version cũ, mã yêu cầu trùng, backend lỗi và HTTP mất phản hồi. “Đang xử lý” không được đồng nghĩa “thành công”. Kết quả thất bại phải nói rõ tiền/quyền/số có thay đổi không.

### Lượt C — Nhìn mô hình và thứ tự xử lý, Phase 2

**Thứ tự:** [analysis](02-analysis/analysis.md) → Use Case toàn hệ thống → Domain → State của đối tượng đang xét → Sequence UC tương ứng. Dùng [chỉ mục UML](UML-INDEX.md) để mở nguồn và ảnh hoặc xem trực tiếp trong báo cáo tổng hợp.

| Loại hình | Đọc để biết | Khi rà soát chú ý |
|---|---|---|
| Use Case | Ai dùng chức năng nào, ranh giới hệ thống | Scheduler/worker nội bộ không bị biến thành actor bên ngoài |
| Domain | Những khái niệm nghiệp vụ và quan hệ của chúng | Customer 1–N Subscriber; đăng ký ổn định khác kỳ trả trước |
| State | Trạng thái nào chuyển được sang trạng thái nào | WAITING ≠ SUCCEEDED; PENDING_CANCEL ≠ CANCELLED; EXPIRED ≠ CANCELLED |
| Activity | Luồng đi qua bước nào và rẽ nhánh theo điều kiện nào | Nhánh chuẩn, WAITING/PROCESSING, lỗi và khôi phục có kết quả rõ |
| Class | Các lớp, thuộc tính và bội số nghiệp vụ | Phân biệt Domain/Class view với bảng và Design Class |
| Sequence | Ai gọi ai, kiểm điều kiện lúc nào, khi nào trả kết quả | Nhánh lỗi, retry, timeout, commit và biên thời gian được thể hiện |

Domain không phải sơ đồ bảng; chưa cần dò kiểu SQL ở đây. Nếu hình cho kết quả khác UC, ghi nhận mâu thuẫn để sửa nguồn; không tự chọn hình làm quy tắc mới.

### Lượt D — Hiểu phân chia trách nhiệm, Phase 3

**Thứ tự:** [architecture](03-architecture/architecture.md) → C4 Context/Container → [module](03-architecture/module-structure.md) → [security](03-architecture/security-and-integration.md) → [runtime profiles](03-architecture/runtime-profiles.md) → [topology](03-architecture/docker-local-topology.md) → [ADR hiện hành](03-architecture/adr/README.md).

**Cần trả lời được:** MySQL, RabbitMQ, Redis và IdP mỗi thành phần chịu trách nhiệm gì; thiếu queue/cache thì có làm sai tiền/cấp số không; worker gia hạn khác JOB-01 và recovery UC01 ở đâu; công ty/nhà chạy những gì.

**Điểm rà soát:** MySQL giữ nguồn quyết định về tiền/quyền/request; Redis không giữ số dư có thẩm quyền; RabbitMQ không là nguồn duy nhất của yêu cầu chờ; JWT do IdP ngoài cấp. Thu phí đăng ký dùng năng lực nội bộ trong cùng transaction, không gọi HTTP vòng lại UC05.

### Lượt E — Kiểm cách hiện thực quy tắc, Phase 4

**Thứ tự:** [schema](04-design/database-schema.md) → ERD → [API contract](04-design/api-contract.md) và [UC01 API riêng](04-design/api-contract-uc01.md) → [OpenAPI](04-design/openapi.yaml) → [concurrency/jobs](04-design/concurrency-and-jobs.md) → [Design Class](04-design/design-class-diagrams/README.md) → [checklist M4](04-design/m4-review-checklist.md).

Đọc schema theo từng nhóm đối tượng thay vì thuộc lòng 21 bảng. Sau đó chọn một endpoint và tìm đầy đủ: scope → input → requestId/version → khóa và điều kiện thời gian → thay đổi bảng → outcome → mã lỗi → cách tra sau timeout.

**Điểm rà soát:** unique nào chống cấp số/trùng kỳ/trùng nguồn; khóa nào chống âm tiền; lúc kiểm giờ có đang chờ khóa khác không; nếu chết giữa chừng thì dữ liệu nào đã commit và ai khôi phục; cursor có buộc đúng caller/filter/snapshot không. READ COMMITTED là mức cô lập được chọn, không tự chứng minh tính đúng.

### Lượt F — Biết code theo thứ tự nào, Phase 5

**Thứ tự:** [implementation plan](04-design/implementation-plan.md) → phần UC01 trong checklist M4 → đặc tả/sequence/API/schema UC01 → các AC UC01 → bộ test dự kiến.

Thiết kế đủ và được duyệt cho UC01 thì triển khai UC01 trước: migration, API, xử lý thành công/lỗi/retry/recovery và test. Sau đó lặp cho UC02, UC03… Không đợi code hết toàn hệ thống rồi mới kiểm một luồng từ đầu tới cuối.

Hiện tại báo cáo chỉ mô tả kế hoạch này. Không được suy ra đã có Spring Boot nghiệp vụ, migration, Compose hoặc test đang chạy chỉ vì đã có tài liệu.

### Lượt G — Biết cần bằng chứng gì, Phase 6

**Thứ tự:** [test plan](05-testing/test-plan.md) → AC của UC đang xét → [64 ca ngoại lệ](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md) → [protocol tải](05-testing/performance-test.md) → [verification](verification.md).

Tách ba việc: test chức năng/đồng thời có đúng không; dữ liệu seed có đủ không; TPS/latency/lỗi có đạt cùng protocol không. Test bằng MySQL thật cần thiết cho khóa, unique, deadlock và unknown commit; mock/unit test không thay các bằng chứng đó.

Đếm logical success khác attempt. Phí đăng ký không là debit UC05 độc lập. 50% request tranh chấp phải thể hiện trong attempts thực tế, kể cả retry, và có overlap ở backend. Chỉ đồ thị request/s không đủ kết luận đạt.

### Lượt H — Biết bàn giao cần gì, Phase 7

**Thứ tự:** [deployment guide](06-deployment/deployment-guide.md) → runtime/topology → test smoke/migration/seed → log, backup/restore và hướng dẫn vận hành → bằng chứng triển khai thực tế.

**Điểm rà soát:** người khác có thể cấu hình, khởi động, migrate/seed, lấy JWT và gọi một luồng thành công/lỗi theo hướng dẫn không; có cách biết job chết hoặc request treo không; restart/restore có làm trùng phí/cấp số không. Hướng dẫn dự kiến chưa đồng nghĩa bàn giao đã được kiểm chứng.

## 4. Rà soát sâu theo từng nghiệp vụ

Một vòng rà soát đầy đủ đi theo chuỗi **quyết định → SRS → UC/AC → State/Sequence → API/schema/transaction → test → bằng chứng**. Không nên chỉ đọc lần lượt các file trong một thư mục rồi kết luận hệ thống nhất quán.

| Vòng | Câu chuyện cần đi hết | Hồ sơ cần đặt cạnh nhau |
|---|---|---|
| 1. Đấu nối | Có số → hết số không chờ → hết số đồng ý chờ → retry/expiry | UC01, Use Case Hybrid, activation state/sequence, UC01 Design Class/API, allocation/schema, 27 AC |
| 2. Thu phí gói | Nạp MAIN → mua kỳ → mua nối → hai lệnh cùng version → thiếu tiền | UC02/UC04, subscription state, sequence đăng ký/credit, schema cycle/fee/request, concurrency |
| 3. Tiêu dùng | Đúng account và cycle → sai account/cycle → thiếu tiền → đua debit | UC05, account state, debit sequence, Money Design Class, account/ledger, race tests |
| 4. Hủy và gia hạn | Có trả trước → pending intent → receipt trước/đúng/sau hạn → mất HTTP | CR-CAN-002/003, UC03, cancellation state/sequence, worker/JOB-01, concurrency, boundary/fault tests |
| 5. Báo cáo | Query OR/AND → trang sau → có commit muộn → chốt ngày → adjustment | CR-TIME-001, UC06/JOB-02, report state/sequence, snapshot/contribution/schema, report tests |
| 6. Tải và bàn giao | Seed → mixed load → timeout/retry → đối soát → restart | Performance protocol, implementation/deployment, raw evidence khi đã chạy |

## 5. Mười tình huống để tự kiểm tra bạn đã hiểu đúng

1. **AUTO hết số, khách chưa đồng ý chờ:** phải FAILED, không WAITING ngầm; có đồng ý mới WAITING tối đa 24 giờ.
2. **Số đã xem AVAILABLE nhưng người khác vừa lấy:** tra cứu không giữ chỗ; tối đa một người cấp thành công.
3. **GAME có 20.000, MAIN có 50.000, debit GAME 30.000:** thiếu GAME, không trừ cả hai và không tự chuyển MAIN.
4. **Đăng ký gói 30.000:** phí thu MAIN cùng transaction tạo kỳ; không gọi thêm UC05 để thu lại.
5. **Hai requestId khác nhau cùng expectedVersion:** không coi là replay; tối đa một lệnh nối kỳ, lệnh còn lại xung đột và không thu thêm.
6. **Gói 30 ngày đăng ký 10:15 ngày 01/10:** hết đúng 00:00 ngày 31/10, không phải 10:15 ngày 31/10.
7. **Hủy khi đã trả tới 30/11:** giữ quyền tới 30/11, không cắt ở cuối kỳ gần nhất 31/10 và không refund.
8. **Ý định hủy ghi trước hạn nhưng chỉ xác nhận/kiểm DB sau hạn, chưa có kết quả accepted:** từ chối do trễ. Nếu accepted đã commit nhưng chỉ mất HTTP thì replay thành công cũ.
9. **Kiểm account dưới khóa trước hạn nhưng commit sau hạn:** được phép theo CR-TIME-002; không áp lại yêu cầu commit vật lý trước hạn.
10. **recordedAt ngày 20, commit ngày 21 sau bản chốt ngày 20:** vẫn thuộc ngày ghi nhận 20; điều chỉnh kỳ sau khi phù hợp, không sửa ngầm bản đã finalized hoặc cộng hai lần.

Nếu kết luận của một tài liệu khác các kết quả này, kiểm nguồn quyết định và ghi nhận sai lệch; đừng sửa lựa chọn của người dùng để làm các file trông khớp nhau.

## 6. Checklist rà soát từng luồng

Các ô dưới đây dành cho người đọc tự dùng; để trống không có nghĩa hệ thống đang lỗi hoặc chưa có tài liệu.

- [ ] Xác định đúng actor, quyền caller và dữ liệu đầu vào bắt buộc.
- [ ] Có đường thành công, nhánh nghiệp vụ hợp lệ khác và lỗi đầu vào/quyền/tài nguyên.
- [ ] Có thời điểm kiểm điều kiện rõ; đúng mốc end/expiry phải có kết quả xác định.
- [ ] Hậu điều kiện nói rõ tiền, số, kỳ, request outcome và lịch sử thay đổi thế nào.
- [ ] Retry cùng mã/cùng nội dung không thêm tác động; khác nội dung có conflict.
- [ ] Hai mã khác nhau tranh chấp có guard nghiệp vụ, không dựa riêng requestId.
- [ ] Timeout/commit không rõ có lookup/recovery; không tự coi đã rollback.
- [ ] State và Sequence khớp UC; API và schema có chỗ biểu diễn đúng kết quả đó.
- [ ] Kịch bản test chỉ rõ expected và bằng chứng; NOT_RUN/PASS/FAIL không bị trộn.
- [ ] Phần chưa chốt được đánh dấu REVIEW/Open, có mốc xử lý; không bịa thành yêu cầu đã duyệt.

## 7. Cách ghi nhận xét để sửa được ngay

Mỗi nhận xét nên có **một vấn đề**, gắn với **một tình huống cụ thể**. Mẫu:

```text
Mã nhận xét: RV-...
Tài liệu / mục / mã UC-AC-FR:
Loại: diễn đạt khó hiểu / mâu thuẫn / thiếu nhánh / lỗi thiết kế / cần bằng chứng
Tình huống: đầu vào, số dư/trạng thái, thời điểm, bên nào chạy đồng thời
Hiện tài liệu đang nói:
Tôi hiểu kết quả đúng phải là:
Nguồn căn cứ: DEC/CR/UC/AC hoặc xác nhận mới
Đề nghị: sửa lời giải thích / bổ sung hình / đồng bộ file / lập CR nếu đổi quy tắc
Kết quả xử lý và tài liệu bị ảnh hưởng:
```

Ví dụ nhận xét hữu ích: “Trong sequence UC05, nếu phải đợi khóa cycle tới sau expiresAt thì mốc kiểm giờ account có được lấy lại sau khóa không? CR-TIME-002 yêu cầu kiểm ngay trước cập nhật. Cần hình và concurrency cùng nói rõ thứ tự.” Cách ghi “luồng tiền chưa ổn” chưa đủ để xác định phải sửa gì.

Nếu chỉ làm rõ cách diễn đạt mà không đổi hành vi, cập nhật tài liệu. Nếu muốn đổi hành vi đã được duyệt, cần CR có cũ/mới/lý do/ảnh hưởng và người duyệt. Không dùng nhận xét mới để âm thầm sửa baseline.

## 8. Điều kiện kết thúc một vòng rà soát

Một vòng đọc hoàn tất khi người đọc có thể kể lại luồng và ngoại lệ, các nhận xét có nguồn đã được xử lý hoặc được ghi Open rõ ràng. Một checkpoint chỉ được nghiệm thu khi có đúng loại bằng chứng mà checkpoint đó yêu cầu.

**Trạng thái lúc lập hướng dẫn:** quyết định khảo sát đã được tiếp nhận; bộ thiết kế mới REVIEW; delta MySQL UC01 chưa nghiệm thu; chưa có bằng chứng backend/runtime/tải. [Checklist M4](04-design/m4-review-checklist.md) là nơi rà soát cụ thể trước bước triển khai UC01. Tạo hướng dẫn và báo cáo tổng hợp không làm thay đổi các mốc đó.

**Bước tiếp theo duy nhất của tiến trình phát triển:** nghiệm thu delta MySQL UC01, sau đó P5.1. Các tài liệu UC sau tiếp tục được rà soát khi tới vòng triển khai tương ứng, không trở thành rào cản không cần thiết cho UC01.
