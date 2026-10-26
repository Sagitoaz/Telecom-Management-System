# Báo cáo tổng hợp — Luồng nghiệp vụ, kịch bản và thiết kế backend Telecom

**Mã:** REPORT-ALL-001 · **Phiên bản:** 1.1 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

**Đọc từ chương 1–9 để hiểu hệ thống chạy như thế nào:** sơ đồ hoạt động tổng thể, ví dụ xuyên suốt, kịch bản chuẩn/thay thế/ngoại lệ của từng UC, worker/jobs và Class Diagram. Các chương sau nối về Phase 0–7, yêu cầu, kiến trúc, DB/API/transaction, kiểm thử và bàn giao.

Mỗi luồng có **Activity Diagram** để theo nhánh quyết định; **Sequence Diagram** để xem tương tác; **State Diagram** để hiểu vòng đời. **Class Diagram** diễn tả đối tượng/quan hệ, còn **ERD** diễn tả dữ liệu vật lý. Không cần đọc hết kiến trúc trước mới thấy luồng nghiệp vụ.

Ảnh xem ngay trong Markdown là PNG với đường dẫn tương đối từ file này; bên dưới có SVG để phóng to và PlantUML để sửa nguồn. Chuyển nguyên thư mục repository không phải sửa lại đường dẫn ổ đĩa.

Đây là báo cáo thiết kế/kế hoạch; kịch bản là kết quả mong đợi, không phải log chạy thật. Quyết định nghiệp vụ đã chốt được giữ nguyên. Tự quyết cách trình bày, bố cục và hình theo ủy quyền của chủ dự án không đồng nghĩa tự nghiệm thu MySQL hoặc tuyên bố code/test đã đạt.

## Mục lục

1. [Bắt đầu từ luồng nghiệp vụ: một thuê bao đi qua hệ thống thế nào?](#flow-overview)
2. [Một hành trình nghiệp vụ xuyên suốt](#journey)
3. [UC01 — Đấu nối Hybrid](#uc01)
4. [UC02 — Đăng ký và mua nối gói](#uc02)
5. [UC03 — Hủy cuối toàn bộ thời gian đã trả](#uc03)
6. [UC04/UC05 — Cộng và trừ tiền](#money)
7. [Worker gia hạn, JOB-01 và recovery](#lifecycle)
8. [UC06/JOB-02 — Tra cứu và tổng hợp](#reporting)
9. [Class Diagram — các lớp nghiệp vụ và quan hệ](#business-classes)
10. [Bức tranh toàn dự án và trạng thái](#overview)
11. [Phase 0 — Đề bài, phạm vi và quyết định](#phase0)
12. [Phase 1 — Quy tắc chung và yêu cầu phải đúng](#phase1)
13. [Phase 2 — Cách nhìn mô hình UML](#phase2)
14. [Phase 3 — Kiến trúc, module và bảo mật](#phase3)
15. [Phase 4 — Dữ liệu, API, transaction và Design Class](#phase4)
16. [Phase 5 — Kế hoạch triển khai từng UC](#phase5)
17. [Phase 6 — Kiểm thử, tải và đối soát](#phase6)
18. [Phase 7 — Triển khai và bàn giao](#phase7)
19. [Truy vết, nghiệm thu và phần còn mở](#review)
20. [Phụ lục — Ca biên và toàn bộ hình](#appendix)

[Hướng dẫn đọc và rà soát](HUONG-DAN-DOC-VA-RA-SOAT.md) · [Chỉ mục hình](UML-INDEX.md)

<a id="flow-overview"></a>

## 1. Bắt đầu từ luồng nghiệp vụ: một thuê bao đi qua hệ thống thế nào?

Đọc theo đường đi của khách hàng: **đấu nối → có MAIN → nạp tiền → mua gói → tiêu dùng → mua nối hoặc hủy/hết hạn → báo cáo**. Mỗi UC bên dưới có Activity Diagram với các nút quyết định, một kịch bản chuẩn có dữ liệu cụ thể và bảng nhánh/lỗi. Sequence mô tả lời gọi được giữ sau phần nghiệp vụ để đối chiếu chi tiết.

### 1.1 Sơ đồ hoạt động xuyên suốt

**Hình 1. Luồng nghiệp vụ từ đấu nối tới tiêu dùng, hủy và báo cáo**

![Hình 1 — Luồng nghiệp vụ từ đấu nối tới tiêu dùng, hủy và báo cáo](02-analysis/activity-diagrams/00-end-to-end.png)

[PNG](02-analysis/activity-diagrams/00-end-to-end.png) · [SVG để phóng to](02-analysis/activity-diagrams/00-end-to-end.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/00-end-to-end.puml)

Các hình Activity đọc từ chấm bắt đầu xuống chấm kết thúc: hình chữ nhật là bước xử lý; hình thoi là điều kiện; nhánh Có/Không chỉ kết quả kiểm tra. Ô đỏ chỉ điểm dừng/lỗi nghiệp vụ cần chú ý, không phải cảnh báo đã có lỗi runtime. Một hình có thể kết thúc ở WAITING/PROCESSING vì caller chưa có kết quả cuối, không vì hệ thống đã thành công.

### 1.2 Phân biệt kịch bản chuẩn, nhánh thay thế và ngoại lệ

| Loại | Ví dụ | Kết quả phải được mô tả |
|---|---|---|
| Chuẩn — S | AUTO có số, hồ sơ hợp lệ | Tạo đúng Subscriber/MAIN, có số, outcome bền vững |
| Thay thế — A | AUTO hết số và khách đồng ý chờ | WAITING có deadline; chưa có thuê bao thành công |
| Ngoại lệ nghiệp vụ — E | GAME thiếu tiền, cycle hết hạn, version cũ | Mã/kết quả rõ; không có tác động tài chính thành công trái phép |
| Sự cố kỹ thuật — X | Lỗi ghi DB, deadlock, mất ACK/HTTP | Rollback nếu đã biết hoặc lookup/recovery; không đoán outcome |

Các mã S/A/E/X trong báo cáo là nhãn kịch bản để đọc, không thay AC-SUB, AC từng UC hoặc TC-EDGE. Những mã đã có vẫn là nguồn truy vết nghiệm thu.

### 1.3 Mọi luồng ghi đều phải đi qua xử lý mã yêu cầu và kết quả

**Hình 2. Luồng chung: input, replay, xung đột, thất bại và mất phản hồi**

![Hình 2 — Luồng chung: input, replay, xung đột, thất bại và mất phản hồi](02-analysis/activity-diagrams/10-common-outcomes.png)

[PNG](02-analysis/activity-diagrams/10-common-outcomes.png) · [SVG để phóng to](02-analysis/activity-diagrams/10-common-outcomes.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/10-common-outcomes.puml)

Nguyên tắc xuyên suốt: HTTP timeout chỉ nói caller chưa biết kết quả; token hợp lệ chưa có nghĩa đủ quyền; cùng requestId khác nội dung là conflict; hai requestId mới cùng nhắm một kỳ vẫn phải phân xử bằng version/target; FAILED nghiệp vụ đã chốt không tự đổi thành success khi nạp thêm tiền.

### 1.4 Dữ liệu nào thay đổi khi luồng kết thúc?

| Luồng | Kết thúc thành công | Nếu nghiệp vụ thất bại |
|---|---|---|
| UC01 | Số ASSIGNED + Subscriber + MAIN=0 + outcome | Không có cấp số/thuê bao/account thành công mồ côi |
| UC02 | MAIN giảm + fee + cycle + coverage/version + outcome | Không có quyền hoặc phí thành công một phần |
| UC03 | Accepted intent + chặn nối/gia hạn; quyền dùng tới paid end | Không refund hoặc tự hủy một kỳ khác |
| UC04 | Account chỉ định tăng + ledger + outcome | Không cộng vào account khác hoặc vượt cap |
| UC05 | Account chỉ định giảm + consumption đúng cycle + outcome | Không âm tiền, không pooling/fallback |
| UC06 | Fixed query result/cursor thuộc caller | Không đổi query cũ ngầm hoặc lộ dữ liệu caller khác |
| Renewal/JOB-01 | Một target được xử lý, trạng thái được đồng bộ | Không thu lại target đã chốt hoặc xóa history |
| JOB-02 | Nguồn cố định, contribution duy nhất, finalized | Không cộng lại nguồn hoặc sửa ngầm ngày đã chốt |


<a id="journey"></a>

## 2. Một hành trình nghiệp vụ xuyên suốt

Đây là **ví dụ minh họa quy tắc**, không phải dữ liệu seed đã chạy hoặc giá gói bắt buộc. Giả sử gói GAME30 có giá hiện hành 30.000 và thời lượng 30 ngày; không có giao dịch khác ngoài các bước được nêu.

| Bước | Hành động | Trạng thái và tiền sau bước |
|---|---|---|
| 1 | Đấu nối AUTO có số, hồ sơ hợp lệ | Một Subscriber và MAIN=0, request SUCCEEDED |
| 2 | Credit MAIN 100.000 | MAIN=100.000, một CREDIT_TOPUP |
| 3 | 01/10 lúc 10:15 mua GAME30, expectedVersion ban đầu | MAIN=70.000; kỳ 01/10 10:15 → 31/10 00:00; một PACKAGE_FEE |
| 4 | Có GAME phụ đã seed hợp lệ 20.000; debit GAME 5.000 với cycle đúng | GAME=15.000; MAIN vẫn 70.000; một SERVICE_CONSUMPTION |
| 5 | Caller debit GAME 30.000 dù MAIN đủ | FAILED; GAME=15.000 và MAIN=70.000 không đổi |
| 6 | Ngày 15/10 mua nối thêm kỳ 30 ngày với version hiện hành | MAIN=40.000; kỳ tương lai 31/10 00:00 → 30/11 00:00; coverageEnd=30/11 |
| 7 | Ngày 20/10 gửi hủy, TxA/TxB nhận hợp lệ trước coverageEnd | PENDING_CANCEL; không refund; vẫn dùng đúng các kỳ đã trả tới 30/11 |
| 8 | Thử mua nối khi đang chờ hủy | Bị chặn, MAIN không bị thu thêm |
| 9 | Tới 30/11 00:00 | Mất quyền theo timestamp; JOB-01 hoàn tất nhãn CANCELLED; không AUTO_RENEWAL_FEE |
| 10 | Tra cứu/chốt ngày | Query có thể tính riêng tiêu dùng 5.000 hoặc thêm hai phí 30.000 theo includeFee và khoảng thời gian; credit/seed không là chi tiêu |

Số dư seed GAME ở bước 4 phải có nguồn fixture/đối soát riêng; không coi đó là credit API đã thực hiện. Bảng chỉ minh họa tác động, không cấp thêm API mở ví hoặc một chính sách cộng tiền không ghi nhận.

### 2.1 Nếu phản hồi bước mua gói bị mất

Caller không biết bước 3 đã commit hay chưa thì tra hoặc POST lại cùng requestId/nội dung. Nếu đã SUCCEEDED, trả kỳ và balanceBefore/After cũ; MAIN không bị trừ thêm 30.000. Nếu đổi sang requestId mới, đó có thể là ý định khác và vẫn phải qua expectedVersion; không coi là cách an toàn để retry.

### 2.2 Nếu thay bước hủy bằng gia hạn tự động

Khi không có hủy và coverageEnd tới hạn, worker có thể mua một kỳ mới nếu đủ MAIN/điều kiện và target chưa được xử lý. Nếu tới vài ngày sau mới chạy, kỳ mới bắt đầu lúc xử lý hợp lệ, không thu bù từng kỳ đã lỡ. Nếu cùng target đã FAILED thiếu tiền, nạp sau không tự làm target đó chạy lại.

### 2.3 Nếu hủy sát 00:00 và mất kết nối

Phải phân biệt “ý định pending được ghi” với “accepted outcome đã commit”. Pending chỉ được xác nhận lại và receipt sau 00:00 thì không được coi là nhận trước hạn. Accepted đã commit trước khi mất HTTP thì tra lại trả accepted cũ. Hai tình huống nhìn giống nhau ở phía caller nhưng dữ liệu/receipt quyết định kết quả khác nhau.

<a id="uc01"></a>

## 3. UC01 — Đấu nối Hybrid

### 3.1 Mục tiêu và đầu vào

Ứng dụng CSKH gửi requestId, hồ sơ, loại+số giấy tờ, tham chiếu xác minh, chế độ AUTO hoặc SELECTED và lựa chọn chờ nếu AUTO hết số. Backend kiểm JWT/quyền và nội dung, không tự xác minh giấy tờ ngoài đời. Một Customer có nhiều giấy tờ đã liên kết; trùng tên/ngày sinh không đủ để hợp nhất hai người.

Quy tắc chuẩn hóa/hồ sơ theo T1/T2: chuẩn hóa Unicode/whitespace phù hợp, giữ dấu và số 0 đầu giấy tờ, phân biệt trường không gửi với null/rỗng. Khi dùng Customer đã liên kết giấy tờ, so các trường được gửi; mâu thuẫn thì từ chối và không ghi đè hồ sơ cũ. Thêm một giấy tờ mới vào Customer cũ không tự được ghép trong flow này.

**Hình 3. Use Case UC01 Hybrid**

![Hình 3 — Use Case UC01 Hybrid](02-analysis/uc01-use-case-diagram.png)

[PNG](02-analysis/uc01-use-case-diagram.png) · [SVG để phóng to](02-analysis/uc01-use-case-diagram.svg) · [Nguồn PlantUML](02-analysis/uc01-use-case-diagram.puml)

**Hình 4. Activity Diagram UC01 — có số, hết số, chờ và deadline**

![Hình 4 — Activity Diagram UC01 — có số, hết số, chờ và deadline](02-analysis/activity-diagrams/01-activation.png)

[PNG](02-analysis/activity-diagrams/01-activation.png) · [SVG để phóng to](02-analysis/activity-diagrams/01-activation.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/01-activation.puml)

### 3.2 Các kịch bản UC01 để đọc và rà soát

**S-UC01 — Đấu nối ngay:** kho có một số AVAILABLE; giấy tờ đã xác minh, hồ sơ khớp hoặc tạo Customer mới hợp lệ; caller có scope và gửi requestId mới, AUTO. Backend kiểm hồ sơ, phối hợp cấp số, ghi Subscriber/MAIN/outcome cùng nghiệp vụ rồi xác nhận commit. **Kết quả:** SUCCEEDED, một số ASSIGNED, đúng một Subscriber mới, MAIN=0 không hạn mặc định; retry cùng mã không thêm gì.

| Nhánh | Tình huống và bước chuyển | Kết quả/khôi phục |
|---|---|---|
| A-UC01-01 | SELECTED một số vừa tra được | Kiểm lại lúc cấp; tra cứu trước đó không giữ chỗ |
| A-UC01-02 | AUTO hết kho, consent=true | WAITING tối đa 24 giờ; nạp kho/recovery FIFO; chưa có Subscriber/MAIN |
| A-UC01-03 | Giấy tờ đã liên kết, mọi trường gửi khớp | Dùng đúng Customer cũ, tạo Subscriber mới; không ghép bằng tên/ngày sinh |
| E-UC01-01 | Số chọn bị request khác lấy | NUMBER_NOT_AVAILABLE; không đổi số hoặc WAITING ngầm |
| E-UC01-02 | AUTO hết kho, consent=false | FAILED NO_AVAILABLE_NUMBER; cùng mã sau nạp kho vẫn kết quả cũ |
| E-UC01-03 | Hồ sơ không khớp/null sai/số không E.164 | Từ chối, không ghi đè Customer hoặc tự đoán quốc gia |
| E-UC01-04 | Chưa được phục vụ vì backlog ưu tiên, chưa chứng minh hết kho | ALLOCATION_BACKLOG theo contract; không tự tạo WAITING |
| E-UC01-05 | Đúng hoặc quá deadline chờ | EXPIRED; retry/restart không reset 24 giờ |
| X-UC01-01 | Crash trước commit / sau commit nhưng mất HTTP | Rollback hoặc lookup/recovery cùng mã; không cấp lần hai |


### 3.3 Luồng chuẩn có số

1. Kiểm token/scope, input, quan hệ trường và định dạng số E.164 nếu chọn số.
2. Xác định key/canonical; có outcome thì replay, có nội dung khác thì conflict.
3. Kiểm hồ sơ/chứng cứ và xác định Customer hợp lệ.
4. SELECTED kiểm lại số muốn lấy; AUTO chọn số khả dụng theo thiết kế cấp số, không dùng kết quả tra cứu cũ làm cam kết.
5. Cấp số, ghi Subscriber, MAIN số dư 0 không hạn mặc định và kết quả request một cách nguyên tử.
6. Xác nhận commit rồi trả SUCCEEDED. “Đấu nối” chỉ hoàn tất trong backend, không kích hoạt mạng thật.

### 3.4 Nhánh và ngoại lệ

| Tình huống | Kết quả nghiệp vụ |
|---|---|
| SELECTED vừa bị người khác cấp | NUMBER_NOT_AVAILABLE; không tự đổi số, không WAITING |
| AUTO hết số, không consent | FAILED NO_AVAILABLE_NUMBER; dùng lại cùng key vẫn kết quả cũ |
| AUTO hết số, consent rõ | WAITING tối đa 24 giờ tính từ lúc vào chờ; chưa có Subscriber/MAIN thành công |
| Bổ sung kho | Thử xử lý WAITING hợp lệ theo FIFO/ưu tiên đã chọn; không để AUTO mới vượt hàng đợi theo baseline |
| WAITING tới deadline | EXPIRED; retry/restart không kéo dài 24 giờ; không cấp từ yêu cầu đã hết hạn |
| Sai giấy tờ/hồ sơ/quyền | Từ chối, không để Customer/Subscriber/số/account mồ côi |
| Đứt kết nối quanh commit | Lookup/recovery theo cùng mã; không cấp lần hai hoặc suy rollback từ HTTP |

RabbitMQ hỗ trợ tín hiệu bổ sung kho sau commit. Recovery mỗi 60 giây đọc trạng thái DB để xử lý việc bị bỏ sót. Con số 60 giây này thuộc UC01, không tự áp cho worker gia hạn gói.

**Hình 5. Vòng đời yêu cầu đấu nối**

![Hình 5 — Vòng đời yêu cầu đấu nối](02-analysis/state-diagrams/activation-request.png)

[PNG](02-analysis/state-diagrams/activation-request.png) · [SVG để phóng to](02-analysis/state-diagrams/activation-request.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/activation-request.puml)

Hình state giúp phân biệt PROCESSING/WAITING với SUCCEEDED. EXPIRED của yêu cầu chờ không có nghĩa một tài khoản tiền bị xóa; đây là trạng thái của một đối tượng khác.

**Hình 6. Sequence đấu nối, chờ và khôi phục**

![Hình 6 — Sequence đấu nối, chờ và khôi phục](02-analysis/sequence-diagrams/uc01-activation.png)

[PNG](02-analysis/sequence-diagrams/uc01-activation.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc01-activation.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc01-activation.puml)

Đọc các nhánh theo thứ tự: replay/conflict → hồ sơ → có số/chọn số lỗi/AUTO hết số → chờ và recovery → tra cứu sau timeout. Kiểm tra thành công chỉ công bố sau commit và không có nhánh WAITING ngầm.

**Cần chứng minh khi triển khai:** hai request cùng số chỉ một success; hai hồ sơ cạnh tranh không tạo trùng Customer theo giấy tờ; crash không để orphan; FIFO/deadline đúng; retry không cấp lại. Chi tiết và 27 AC baseline: [UC01](01-requirements/use-cases/UC01-Subscriber-Activation.md).

<a id="uc02"></a>

## 4. UC02 — Đăng ký và mua nối gói

### 4.1 Mục tiêu và luồng chuẩn

Caller gửi subscriberId, packageId, requestId và expectedSubscriptionVersion. Không gửi giá thu, account thu phí hoặc một chính sách autoRenew mới để tự quyết. Backend lấy MAIN và điều kiện hiện hành có thẩm quyền; version 0 biểu diễn chưa có đăng ký đã mua theo contract.

1. Kiểm quyền/input/key và replay/conflict.
2. Phối hợp MAIN, đăng ký/slot và kỳ mục tiêu theo thứ tự khóa của thiết kế.
3. So expectedVersion, trạng thái hủy, điều kiện bán/gia hạn và giá/thời lượng hiện hành.
4. Kiểm giờ DB mới sau các khóa cần thiết, MAIN còn hợp lệ/đủ tiền, giá nằm trong miền tiền.
5. Tính kỳ, trừ MAIN, ghi PACKAGE_FEE, cycle snapshot, coverageEnd/version và outcome trong một transaction.
6. Commit rồi trả thành công; không gọi một API UC05 khác để thu phí.

**Hình 7. Sequence đăng ký và mua nối gói**

![Hình 7 — Sequence đăng ký và mua nối gói](02-analysis/sequence-diagrams/uc02-package-subscription.png)

[PNG](02-analysis/sequence-diagrams/uc02-package-subscription.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc02-package-subscription.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc02-package-subscription.puml)

**Hình 8. Activity Diagram UC02 — kiểm version, điều kiện và thu phí nguyên tử**

![Hình 8 — Activity Diagram UC02 — kiểm version, điều kiện và thu phí nguyên tử](02-analysis/activity-diagrams/02-subscription.png)

[PNG](02-analysis/activity-diagrams/02-subscription.png) · [SVG để phóng to](02-analysis/activity-diagrams/02-subscription.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/02-subscription.puml)

### 4.2 Các kịch bản UC02

**S-UC02 — Mua kỳ đầu:** MAIN=100.000; gói đang bán, giá 30.000, 30 ngày; chưa có đăng ký đã mua, caller gửi expectedVersion=0 và requestId mới. Xử lý hợp lệ lúc 01/10 10:15. **Kết quả:** MAIN=70.000; một PACKAGE_FEE, một cycle từ 01/10 10:15 đến 31/10 00:00; coverage/version và outcome cùng commit. Các số tiền này chỉ là fixture minh họa, không phải giá seed bắt buộc.

| Nhánh | Điều kiện | Kết quả/điều không được làm |
|---|---|---|
| A-UC02-01 | Còn coverage đã trả | Nối từ coverageEnd, không cắt/kéo lại kỳ cũ |
| A-UC02-02 | Đã gián đoạn nhiều ngày | Start lúc xử lý hợp lệ; không thu hoặc cấp quyền ngược gap |
| A-UC02-03 | Gói ngừng bán mới, khách cũ đủ grandfather policy | Có thể gia hạn theo giá/policy hiện hành và snapshot terms |
| E-UC02-01 | MAIN thiếu tiền hoặc hết hạn lúc kiểm | FAILED; không fee/cycle thành công, không fallback tài khoản phụ |
| E-UC02-02 | Hai mã mới cùng expectedVersion / manual đua auto | Một target được tạo; bên còn lại conflict/re-evaluate, không mua thêm kỳ nữa |
| E-UC02-03 | Pending/accepted cancellation | Chặn mua nối/gia hạn, không thu tiền |
| E-UC02-04 | Khách mới mua gói ngừng bán / amount sai miền tiền | Từ chối toàn bộ |
| X-UC02-01 | Insert cycle/ledger/outcome lỗi | Rollback toàn bộ phần chưa commit; không có success nửa chừng |
| X-UC02-02 | Mất phản hồi COMMIT hoặc HTTP | Lookup cùng key; đã success thì trả cycle/fee cũ, không trừ lại |


### 4.3 Cách tính và bảo vệ kỳ

| Trạng thái trước lệnh | Kỳ mới |
|---|---|
| Chưa mua hoặc đã có gap | start là mốc xử lý hợp lệ; end theo số ngày lịch, không hồi tố |
| Còn coverage đã trả | start bằng coverageEnd cũ; thêm durationDays ngày lịch |
| Có pending/accepted cancellation | Không được nối thêm; xử lý theo guard hủy |
| Worker/manual vừa tạo kỳ cùng mục tiêu | Re-evaluate/xung đột, không âm thầm mua kỳ nữa |

Ví dụ 30 ngày bắt đầu 01/10 10:15 → hết 31/10 00:00. Mua thêm một kỳ 30 ngày khi còn quyền thì kỳ mới từ 31/10 00:00 đến 30/11 00:00. Giá/thời lượng của từng kỳ được lưu snapshot; đổi catalog sau đó không sửa kỳ đã trả.

### 4.4 Ngoại lệ phải giữ nguyên tiền/quyền

Thiếu MAIN, sai subscriber/gói, giá không hợp lệ, version cũ, chờ hủy hoặc gói không bán mới thì từ chối phù hợp, không có phí thành công hoặc kỳ hoạt động một phần. Nếu transaction lỗi trước commit thì rollback; nếu commit chưa rõ phải tra request/cycle trước khi tác động lại.

Hai requestId mới cùng expectedVersion không phải retry của nhau. Một lệnh thắng làm đổi version; lệnh kia không được tự cập nhật version rồi thu thêm. Muốn mua thêm có chủ đích, caller đọc version mới và gửi ý định mới.

Gói ngừng bán chỉ chặn khách mới; khách đã đăng ký vẫn có thể gia hạn theo chính sách/giá cấu hình cho nhóm cũ. Backend phải phân biệt hai trường hợp, không dùng một cờ ngừng bán để cấm hết mọi renewal.

**Hình 9. Vòng đời đăng ký gói**

![Hình 9 — Vòng đời đăng ký gói](02-analysis/state-diagrams/package-subscription.png)

[PNG](02-analysis/state-diagrams/package-subscription.png) · [SVG để phóng to](02-analysis/state-diagrams/package-subscription.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/package-subscription.puml)

PENDING_CANCEL vẫn giữ quyền trong các kỳ đã trả trước mốc end. CANCELLED do yêu cầu hủy khác EXPIRED vì không còn quyền/gia hạn. Nhãn trạng thái không thay phép kiểm `[start,end)`.

Nguồn: [UC02](01-requirements/use-cases/UC02-Package-Subscription.md), [concurrency](04-design/concurrency-and-jobs.md), [CR hủy trả trước](00-project/CR-CAN-002-prepaid-cancellation.md).

<a id="uc03"></a>

## 5. UC03 — Hủy cuối toàn bộ thời gian đã trả

### 5.1 Hủy nghĩa là gì?

Nhận hủy không cắt quyền ngay. Nó ngăn mua nối/gia hạn và ấn định hết quyền ở cuối toàn bộ coverage đã thanh toán. Không hoàn tiền. Khách đã trả tới 30/11 thì không bị cắt tại 31/10 chỉ vì đó là cuối kỳ đang dùng.

Hủy có hai mốc: **đã tiếp nhận hợp lệ** và **đã đến thời điểm hủy có hiệu lực**. API xác nhận request thành công ở mốc thứ nhất; đối tượng đăng ký chỉ thành CANCELLED tại mốc thứ hai. Đây là lý do cần tách ý định hủy khỏi trạng thái cuối vòng đời gói.

**Hình 10. Activity Diagram UC03 — TxA, receipt sau commit và kết luận hủy**

![Hình 10 — Activity Diagram UC03 — TxA, receipt sau commit và kết luận hủy](02-analysis/activity-diagrams/03-cancellation.png)

[PNG](02-analysis/activity-diagrams/03-cancellation.png) · [SVG để phóng to](02-analysis/activity-diagrams/03-cancellation.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/03-cancellation.puml)

### 5.2 Các kịch bản UC03

**S-UC03 — Hủy khi có kỳ trả trước:** kỳ đang dùng hết 31/10 nhưng đã trả tới 30/11. Ngày 20/10 caller gửi version hiện hành; TxA commit được xác nhận; TxB lấy receipt trước coverageEnd, chốt ACCEPTED/SUCCEEDED. **Kết quả:** không thu/hoàn tiền; PENDING_CANCEL, cancelEffectiveAt=30/11 00:00; dùng đúng các kỳ đã trả nhưng không mua nối/auto thêm. Tới end quyền mất và JOB-01 đồng bộ CANCELLED.

| Nhánh | Điểm phát sinh | Kết quả/khôi phục |
|---|---|---|
| A-UC03-01 | Retry cùng ID đã accepted | Replay accepted/effectiveAt cũ kể cả sau end |
| E-UC03-01 | ID mới khi đã PENDING_CANCEL | CANCELLATION_PENDING; không tạo một hủy khác |
| E-UC03-02 | Expected version khác sau một mua nối đã thắng | Conflict; caller đọc lại trạng thái và quyết định mới |
| E-UC03-03 | TxA trước hạn nhưng receipt chỉ lấy đúng/sau end, chưa có accepted outcome | FAILED CANCELLATION_RECEIVED_TOO_LATE; gỡ guard; không retarget/refund |
| X-UC03-01 | Không rõ TxA commit | Tra request/intent bằng kết nối mới; chưa rõ thì PROCESSING, không hứa accepted |
| X-UC03-02 | Crash sau TxA PENDING | Resolver kiểm receipt mới khi biết TxA commit; cả manual và auto vẫn bị chặn tới khi kết luận |
| X-UC03-03 | TxB accepted đã commit, HTTP mất | Caller tra cùng ID nhận accepted cũ, không kiểm hạn lại để đảo thành failure |


### 5.3 Giao thức hai bước theo CR-CAN-003

**TxA:** khóa request và slot đăng ký, so version/coverage mục tiêu, lưu PENDING intent rồi commit. Trong thời gian pending, chặn cả worker renewal và manual extension. Ý định được ghi trong transaction chưa đủ để gọi là accepted.

**TxB:** chỉ sau khi backend biết TxA commit thành công, khóa lại request/slot/intent. Nếu đã terminal thì trả outcome cũ. Nếu còn pending, lấy giờ DB mới `receiptCheckedAt` và kiểm cùng version/coverage:

- `receiptCheckedAt < observedCoverageEnd`: nhận hủy, intent ACCEPTED, root PENDING_CANCEL, cancelEffectiveAt bằng cuối coverage, request SUCCEEDED; commit rồi trả xác nhận.
- `receiptCheckedAt >= observedCoverageEnd` và chưa có accepted outcome: intent REJECTED, request FAILED CANCELLATION_RECEIVED_TOO_LATE, gỡ guard pending; không tự hủy một kỳ mới hoặc refund.
- Version mục tiêu đã thay đổi: xung đột, không tự đổi mục tiêu và không thu tiền.

TxB được phép commit sau end nếu receipt đã được kiểm trước end; quy tắc này không đòi đo deadline hoàn tất commit vật lý.

**Hình 11. Vòng đời ý định hủy**

![Hình 11 — Vòng đời ý định hủy](02-analysis/state-diagrams/cancellation-intent.png)

[PNG](02-analysis/state-diagrams/cancellation-intent.png) · [SVG để phóng to](02-analysis/state-diagrams/cancellation-intent.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/cancellation-intent.puml)

### 5.4 Mất phản hồi và biên 00:00

| Ví dụ end = 00:00 | Cách kết luận |
|---|---|
| TxA được xác nhận 23:59:58, TxB receipt 23:59:59 | Có thể ACCEPTED nếu version/điều kiện hợp lệ |
| TxA ghi trước hạn nhưng chỉ xác nhận và lấy receipt 00:00:02, chưa accepted | Bị từ chối do trễ, không dùng giờ HTTP hoặc persistedAt cũ để lùi receipt |
| TxB ACCEPTED đã commit nhưng HTTP bị mất; caller tra lúc 00:01 | Replay accepted cũ; không đổi thành late vì giờ hiện tại |
| COMMIT TxA/TxB chưa rõ kết quả | Kết nối mới tra/khóa outcome; còn chưa biết thì PROCESSING/quan sát unknown, không đoán rollback |
| Pending tồn tại sau crash | Recovery kết luận với receipt mới sau khi biết TxA commit; nếu đã trễ thì reject và giải guard |

Nếu hủy bị reject do receipt muộn, renewal sau đó chỉ được xử lý theo Q25=B cùng các điều kiện hiện hành; không suy ra phải gia hạn thành công. Nếu MAIN thiếu tiền hoặc target đã FAILED thì áp quy tắc tương ứng.

**Hình 12. Sequence nhận hủy, biên hạn và mất phản hồi**

![Hình 12 — Sequence nhận hủy, biên hạn và mất phản hồi](02-analysis/sequence-diagrams/uc03-package-cancellation.png)

[PNG](02-analysis/sequence-diagrams/uc03-package-cancellation.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc03-package-cancellation.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc03-package-cancellation.puml)

**Cần chứng minh khi triển khai:** pending thực sự chặn cả hai đường nối kỳ; không bypass bằng lease hết; accepted replay không bị đổi; resolver không treo khi DB đã biết outcome; đúng hạn là trễ; bảo toàn toàn bộ paid coverage. Nguồn: [UC03](01-requirements/use-cases/UC03-Package-Cancellation.md), [CR-CAN-002](00-project/CR-CAN-002-prepaid-cancellation.md), [CR-CAN-003](00-project/CR-CAN-003-cancellation-receipt.md).

<a id="money"></a>

## 6. UC04/UC05 — Cộng và trừ tiền

### 6.1 Quy tắc tài khoản được chỉ định

Một account là một số dư riêng. Loại tài khoản phụ được seed với quyền CREDIT/DEBIT và expiry rõ ràng; chưa có API tạo ví công khai trong phạm vi này. MAIN mặc định được tạo khi đấu nối, số dư 0, không có hạn mặc định.

Ví dụ GAME có 20.000, MAIN có 50.000, caller debit GAME 30.000: kết quả INSUFFICIENT_BALANCE, cả hai số dư giữ nguyên. Debit GAME 10.000 có thể thành công nếu GAME còn hạn/được phép và cycle hợp lệ; MAIN vẫn không đổi. Phí gói 30.000 lại thu MAIN vì đó là quy tắc của UC02/renewal.

**Hình 13. Vòng đời tài khoản và hết hạn**

![Hình 13 — Vòng đời tài khoản và hết hạn](02-analysis/state-diagrams/account.png)

[PNG](02-analysis/state-diagrams/account.png) · [SVG để phóng to](02-analysis/state-diagrams/account.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/account.puml)

Account hết hạn giữ số dư/lịch sử nhưng chặn thao tác theo điều kiện. Nạp tiền không tự kéo dài expiry hoặc làm account EXPIRED trở lại ACTIVE.

**Hình 14. Activity Diagram UC04 — cộng tiền đúng tài khoản**

![Hình 14 — Activity Diagram UC04 — cộng tiền đúng tài khoản](02-analysis/activity-diagrams/04-credit.png)

[PNG](02-analysis/activity-diagrams/04-credit.png) · [SVG để phóng to](02-analysis/activity-diagrams/04-credit.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/04-credit.puml)

### 6.2 Kịch bản UC04 — Cộng tiền

**S-UC04:** MAIN hợp lệ đang 0; caller có quyền gửi credit 100.000 với mã mới. Sau khóa, kiểm giờ/cap/amount; ghi MAIN=100.000, một CREDIT_TOPUP với before=0/after=100.000 và outcome cùng commit. Replay không tăng thành 200.000.

| Nhánh | Tình huống | Kết quả |
|---|---|---|
| A-UC04-01 | Chỉ định tài khoản phụ còn hạn và được CREDIT | Cộng đúng phụ; MAIN không đổi |
| E-UC04-01 | Sai subscriber, account không cho CREDIT hoặc caller thiếu scope | Từ chối; không thay tiền |
| E-UC04-02 | Amount 0/âm/lẻ/vượt cap hoặc balance mới vượt cap | Từ chối toàn bộ, kiểm overflow |
| E-UC04-03 | expiresAt <= giờ kiểm dưới khóa | ACCOUNT_EXPIRED, giữ số dư/lịch sử |
| X-UC04-01 | Commit không rõ | Tra cùng ID; không gửi mã mới để cộng lại |


### 6.3 UC04 — Cộng tiền

Kiểm caller/account thuộc subscriber và được CREDIT, claim request, khóa account. Lấy giờ DB dưới khóa trước cập nhật; kiểm expiry, amount nguyên dương/giới hạn, balance mới không vượt cap hoặc tràn. Cập nhật account, append CREDIT_TOPUP và terminal outcome cùng transaction rồi mới trả thành công.

Các nhánh lỗi gồm: sai chủ, thiếu quyền, loại không cho credit, account hết hạn, amount sai, vượt cap, lỗi kỹ thuật/unknown commit. Mọi failure không tạo tiền ở account khác. Replay trả balanceBefore/After của giao dịch cũ, không hứa đó là số dư hiện tại sau các giao dịch mới.

**Hình 15. Sequence cộng tiền**

![Hình 15 — Sequence cộng tiền](02-analysis/sequence-diagrams/uc04-account-credit.png)

[PNG](02-analysis/sequence-diagrams/uc04-account-credit.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc04-account-credit.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc04-account-credit.puml)

**Hình 16. Activity Diagram UC05 — kiểm account, cycle và số dư**

![Hình 16 — Activity Diagram UC05 — kiểm account, cycle và số dư](02-analysis/activity-diagrams/05-debit.png)

[PNG](02-analysis/activity-diagrams/05-debit.png) · [SVG để phóng to](02-analysis/activity-diagrams/05-debit.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/05-debit.puml)

### 6.4 Kịch bản UC05 — Tiêu dùng độc lập

**S-UC05:** GAME=20.000, MAIN=50.000; GAME có quyền DEBIT, còn hạn; cycle thuộc thuê bao và hiệu lực. Debit GAME 5.000. **Kết quả:** GAME=15.000, MAIN=50.000; một SERVICE_CONSUMPTION gắn đúng cycle và outcome cùng commit.

| Nhánh | Tình huống | Kết quả |
|---|---|---|
| A-UC05-01 | PENDING_CANCEL nhưng cycle còn quyền | Vẫn tiêu dùng tới end đã trả |
| E-UC05-01 | Debit GAME 30.000 khi GAME chỉ 20.000 dù MAIN đủ | INSUFFICIENT_BALANCE; cả hai dư giữ nguyên; không gộp/fallback |
| E-UC05-02 | Không có gói, cycle sai chủ/chưa start/đã end | NO_EFFECTIVE_CYCLE; không trừ |
| E-UC05-03 | Account hết hạn hoặc không cho DEBIT | Từ chối; không đổi account khác |
| E-UC05-04 | Hai debit 80.000 cùng account 100.000 | Tối đa một success; lệnh còn lại thiếu tiền; dư cuối 20.000 |
| X-UC05-01 | Deadlock/timeout lock/HTTP | Rollback/recovery hữu hạn hoặc lookup; không quy lỗi kỹ thuật thành thiếu tiền |


### 6.5 UC05 — Tiêu dùng độc lập

Ngoài accountId/subscriberId/amount/requestId, lệnh cần subscriptionId, cycleId và serviceReference theo contract. Backend kiểm attribution đúng thuê bao và kỳ thực sự bao phủ thời điểm kiểm, không chỉ tin caller gửi một packageId. Kỳ tương lai chưa bắt đầu hoặc đã hết hạn không cho tiêu dùng; chưa có gói cũng không được trừ như một dịch vụ chưa phân loại.

Sau khi phối hợp mọi khóa cần thiết, lấy giờ DB mới ngay trước mutation; kiểm account expiry, cycle `[start,end)`, quyền và số dư. Ghi một SERVICE_CONSUMPTION, cập nhật balance và outcome nguyên tử. PENDING_CANCEL vẫn được tiêu dùng trên kỳ còn hiệu lực tới cuối thời gian đã trả.

**Hình 17. Sequence trừ tiền có kiểm quyền kỳ**

![Hình 17 — Sequence trừ tiền có kiểm quyền kỳ](02-analysis/sequence-diagrams/uc05-account-debit.png)

[PNG](02-analysis/sequence-diagrams/uc05-account-debit.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc05-account-debit.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc05-account-debit.puml)

### 6.6 Đồng thời và kiểm hạn

Giả sử account có 100.000, hai debit 80.000 cùng đến. Không được cả hai cùng đọc 100.000 rồi cùng trừ. Đường ghi phối hợp khóa trên account và kiểm lại số dư có thẩm quyền: tối đa một debit này thành công, lệnh còn lại thiếu tiền; dư cuối 20.000. Trường hợp hai credit/debit xen kẽ phải đối soát theo ledger đã commit, không theo thứ tự caller gửi hoặc UUID.

CR-TIME-002: account hết 00:00; khi đã lấy mọi khóa cần thiết, kiểm giờ 23:59:59 còn hạn thì có thể commit 00:00:01. Nếu còn phải chờ khóa tới sau hạn thì phải lấy mốc mới trước mutation; không dùng mốc cũ trước lúc chờ để cho qua. Đúng expiresAt phải từ chối. Đây là điều kiện nghiệp vụ đã chấp nhận, không phải cam kết fsync/commit hoàn tất trước hạn.

**Cần chứng minh:** số dư không âm, ledger chain hợp lệ, cap/overflow đúng, cùng mã không trừ hai lần, khác mã có tranh chấp thật, failed không hồi sinh sau nạp, timeout không tạo khoản thứ hai. Nguồn: [UC04](01-requirements/use-cases/UC04-Account-Credit.md), [UC05](01-requirements/use-cases/UC05-Account-Debit.md), [CR-TIME-002](00-project/CR-TIME-002-account-expiry.md).

<a id="lifecycle"></a>

## 7. Worker gia hạn, JOB-01 và recovery

### 7.1 Vì sao có nhiều tác vụ?

| Thành phần | Việc chính | Thời điểm |
|---|---|---|
| Worker gia hạn | Phát hiện đăng ký tới hạn và thử mua một kỳ mới hợp lệ | Chạy thường xuyên; cadence cấu hình còn REVIEW |
| JOB-01 | Hết hạn account/gói, hoàn tất hủy và rà soát phần bỏ sót | 00:05 hằng ngày, giờ Việt Nam |
| JOB-02 | Tổng hợp ngày, khôi phục kỳ thiếu và ghi điều chỉnh dữ liệu muộn | 00:30 hằng ngày, giờ Việt Nam |
| Recovery UC01 | Xử lý WAITING/cấp số bị bỏ sót và deadline | Mỗi 60 giây, kết hợp tín hiệu kho số |

Worker gia hạn và JOB-01 có phần rà soát chồng nhau, nhưng phải dùng cùng lõi/target key/outcome để không làm lại một kỳ. JOB-01 còn xử lý account, hủy và hết hạn. JOB-02 là nghiệp vụ báo cáo riêng. Không có một thread hay timer chạy riêng cho mỗi thuê bao.

Các gói là dữ liệu, không tự thực thi khi thời gian trôi qua. Scheduler/worker là bên đọc candidate đến hạn qua chỉ mục rồi kiểm lại dưới transaction. Không phải quét toàn bảng liên tục một cách mặc định; thiết kế lô/budget/index phải được EXPLAIN và đo. Đề xuất cadence 10 giây/batch 100 trong thiết kế chưa được coi là ngưỡng đã chốt hoặc tối ưu đã chứng minh.

**Hình 18. Activity Diagram worker — phân xử target và tạo đúng một kỳ**

![Hình 18 — Activity Diagram worker — phân xử target và tạo đúng một kỳ](02-analysis/activity-diagrams/06-renewal.png)

[PNG](02-analysis/activity-diagrams/06-renewal.png) · [SVG để phóng to](02-analysis/activity-diagrams/06-renewal.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/06-renewal.puml)

### 7.2 Kịch bản worker gia hạn

**S-REN:** root hết hạn 00:00, chưa hủy, MAIN đủ, target chưa chốt; worker xử lý 00:00:20. Ghi một AUTO_RENEWAL_FEE và một cycle mới start ở mốc xử lý hợp lệ; gap 20 giây không có quyền ngược.

| Nhánh | Điều kiện | Kết quả |
|---|---|---|
| A-REN-01 | Backend ngừng nhiều ngày | Nếu đủ điều kiện chỉ một kỳ mới từ lúc xử lý, không truy thu mọi kỳ lỡ |
| E-REN-01 | Manual vừa tạo target | Skip/re-evaluate; không mua kỳ sau nữa |
| E-REN-02 | Target đã FAILED thiếu tiền | Không hồi sinh sau nạp hoặc bằng runId/now mới |
| E-REN-03 | Intent PENDING/ACCEPTED | Chặn; resolver pending hoặc tôn trọng hủy |
| X-REN-01 | Worker chết/lease hết/ACK mất | Fence/target/outcome trong DB quyết định; lookup/recovery không thu lại |


### 7.3 Luồng gia hạn

1. Đọc candidate tới coverageEnd; xác định root/version/targetCycleKey ổn định.
2. Tìm outcome của đúng target. Nếu SUCCEEDED hoặc FAILED nghiệp vụ đã chốt, không tác động lại; lỗi kỹ thuật có thể recovery theo guard.
3. Phối hợp với manual và pending/accepted cancellation. Nếu manual đã tạo kỳ mục tiêu, không tự mua thêm kỳ sau nữa.
4. Kiểm MAIN, giờ DB, số dư, chính sách khách cũ và giá/thời lượng có thẩm quyền.
5. Thu AUTO_RENEWAL_FEE, tạo một kỳ mới và cập nhật root/outcome nguyên tử.
6. Commit rồi ghi progress; các runner khác nhìn cùng target phải skip/replay thay vì thu lại.

Theo Q25=B, xử lý 00:00:20 thay vì 00:00 thì kỳ mới bắt đầu ở mốc hợp lệ 00:00:20; khoảng trống 20 giây không có quyền hồi tố. Backend ngừng nhiều giờ/nhiều ngày cũng chỉ mua một kỳ mới khi đủ điều kiện, không truy thu toàn bộ kỳ bị lỡ.

Thiếu tiền là kết quả FAILED của lần gia hạn đúng target; nạp tiền sau đó không làm worker tự chạy lại ý định đã thất bại. Muốn đăng ký lại dùng UC02 theo ý định mới. Vì vậy cần renewal_attempt bền vững, không lấy now/runId làm target mới mỗi lần quét.

**Hình 19. Sequence worker gia hạn và guard cùng kỳ**

![Hình 19 — Sequence worker gia hạn và guard cùng kỳ](02-analysis/sequence-diagrams/renewal-worker.png)

[PNG](02-analysis/sequence-diagrams/renewal-worker.png) · [SVG để phóng to](02-analysis/sequence-diagrams/renewal-worker.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/renewal-worker.puml)

**Hình 20. Activity Diagram JOB-01 — account hết hạn, hủy và rà soát renewal**

![Hình 20 — Activity Diagram JOB-01 — account hết hạn, hủy và rà soát renewal](02-analysis/activity-diagrams/07-expiration.png)

[PNG](02-analysis/activity-diagrams/07-expiration.png) · [SVG để phóng to](02-analysis/activity-diagrams/07-expiration.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/07-expiration.puml)

**S-J01:** run hợp lệ đọc account đến hạn và root PENDING_CANCEL tới coverageEnd; kiểm lại, đổi trạng thái đúng, giữ balance/cycle/ledger, ghi counts/checkpoint phần đã commit. Nếu worker đã mua kỳ mới thì bỏ qua phần gia hạn cũ. **Ngoại lệ:** nhiều runner, một item lỗi, crash sau batch hoặc pending hủy chưa resolve phải dùng fence/recovery; không purge history hoặc reset hạn để chữa lỗi.


### 7.4 JOB-01

Account tới hạn được đánh EXPIRED/soft disable, giữ dư/lịch sử. MAIN không hạn không bị suy diễn thành hết hạn. Gói PENDING_CANCEL tới coverageEnd chuyển CANCELLED; gói không còn quyền và không gia hạn được chuyển EXPIRED. Đường API đã chặn quyền theo timestamp ngay tại end, không đợi 00:05.

JOB-01 kiểm công việc worker đã làm và gọi cùng lõi khi cần recovery; không có thuật toán phí riêng. Pending intent hủy được resolver xử lý theo CR-CAN-003; không bypass vì “job đang quét bù”. Chạy lại/chạy tay/nhiều instance phải phối hợp lease/fence/checkpoint và unique business target.

**Hình 21. Sequence JOB-01 hết hạn, hủy và phục hồi**

![Hình 21 — Sequence JOB-01 hết hạn, hủy và phục hồi](02-analysis/sequence-diagrams/job01-expiration.png)

[PNG](02-analysis/sequence-diagrams/job01-expiration.png) · [SVG để phóng to](02-analysis/sequence-diagrams/job01-expiration.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/job01-expiration.puml)

Run có PENDING/RUNNING/SUCCEEDED/FAILED; lỗi từng item được ghi counters/results. Lỗi nghiệp vụ thiếu tiền khác run hạ tầng FAILED. Cursor/checkpoint chỉ tiến theo phần việc đã commit; một item lỗi không được làm các item khác bị bỏ vĩnh viễn. Chưa có kết quả runtime chứng minh recovery này.

Nguồn: [scheduled jobs](01-requirements/scheduled-jobs.md), [concurrency/jobs](04-design/concurrency-and-jobs.md).

<a id="reporting"></a>

## 8. UC06/JOB-02 — Tra cứu và tổng hợp

### 8.1 Nguồn và ngày nghiệp vụ

Nguồn báo cáo chi tiêu là SERVICE_CONSUMPTION và, nếu yêu cầu tính phí, PACKAGE_FEE/AUTO_RENEWAL_FEE. CREDIT_TOPUP không phải tiêu dùng. Phí được attribution qua kỳ/đăng ký phù hợp, không nhân một fee thành nhiều khoản khi join các kỳ.

Theo CR-TIME-001, giao dịch được phân ngày theo recordedAt do DB ghi trong transaction cuối cùng commit. Ví dụ ghi 23:59:59 ngày 20, commit 00:00:01 ngày 21 vẫn thuộc ngày 20. Chưa commit thì chưa xuất hiện; vì thế một mốc thời gian đơn lẻ không chứng minh đã nhìn thấy tất cả nguồn thuộc ngày đó.

Caller không được gửi serviceOccurredAt cũ để ép backdate. recordedAt không phải timestamp hoàn tất commit vật lý; cũng không dùng nó làm bằng chứng trực tiếp cho TPS commit trong cửa sổ đo tải.

**Hình 22. Activity Diagram UC06 — fixed result, OR/AND và cursor**

![Hình 22 — Activity Diagram UC06 — fixed result, OR/AND và cursor](02-analysis/activity-diagrams/08-query.png)

[PNG](02-analysis/activity-diagrams/08-query.png) · [SVG để phóng to](02-analysis/activity-diagrams/08-query.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/08-query.puml)

### 8.2 Kịch bản UC06 — Tra cứu tiêu dùng

**S-UC06:** trong [X,Y), S1 có giao dịch gói A và B, S2 chỉ có A. Với OR chọn A/B thì kết quả có S1/S2; AND thì chỉ S1. Backend cố định nguồn/tổng, trả mỗi thuê bao một dòng; trang sau dùng đúng query/cutoff. includeFee quyết định thêm phí gói, không tính CREDIT.

| Nhánh | Tình huống | Kết quả |
|---|---|---|
| A-UC06-01 | Nguồn mới xuất hiện giữa hai trang | Trang cũ không xô; mở query mới để thấy dữ liệu mới |
| A-UC06-02 | First response mất, cùng queryRequestId/filter | Dùng query/cutoff đầu tiên, không mở snapshot mới âm thầm |
| E-UC06-01 | X>=Y, gói trùng/rỗng, cursor giả/sai filter/caller | Reject theo contract, không lộ hoặc thay tập dữ liệu |
| E-UC06-02 | Snapshot hết retention | SNAPSHOT_EXPIRED; tạo lượt mới, không dùng cursor cũ trên live result |
| X-UC06-01 | Crash trong freeze trước READY | Rollback phần freeze; retry cùng cutoff, không để READY có tập dở |


### 8.3 UC06 — Query và phân trang cố định

Đầu vào có X/Y kèm offset, X < Y, dùng `[X,Y)`; packageIds không rỗng/không trùng; matchMode OR/AND; includeFee; pageSize và queryRequestId. Kết quả theo thuê bao, có tổng tiền/count và breakdown theo gói từ tập nguồn đã cố định.

- **OR:** một thuê bao có giao dịch phù hợp của ít nhất một gói trong tập chọn.
- **AND:** cùng thuê bao có giao dịch phù hợp của từng gói được chọn, không chỉ đang đăng ký các gói đó.
- **includeFee=false:** chỉ tiêu dùng độc lập; true thêm hai loại phí gói. CREDIT luôn bị loại.
- Mặc định 50 thuê bao/trang, tối đa 200. Một thuê bao chỉ một dòng dù có nhiều giao dịch nguồn.

QueryRequestId gắn caller/filter giúp tìm đúng query nếu phản hồi tạo lần đầu bị mất. Tx0 lưu query/cutoff đầu tiên. Tx1 đóng băng source membership bằng một statement nhất quán, rồi derive subscriber DISTINCT/tổng từ persisted members và đánh READY nguyên tử. Không đọc hai tập live ở READ COMMITTED rồi cho rằng chúng là cùng snapshot.

Trang sau dùng đúng READY result; cursor được bảo vệ và gắn query/caller/filter/version/last key/expiry. Query hết hạn thì báo hết snapshot, không thay bằng kết quả live mới mà giữ nguyên cursor. Người đọc muốn xem giao dịch mới thì tạo lượt query mới; không giữ DB transaction mở trong suốt thời gian họ đọc các trang.

**Hình 23. Sequence query, dùng lại snapshot và cursor**

![Hình 23 — Sequence query, dùng lại snapshot và cursor](02-analysis/sequence-diagrams/uc06-consumption-query.png)

[PNG](02-analysis/sequence-diagrams/uc06-consumption-query.png) · [SVG để phóng to](02-analysis/sequence-diagrams/uc06-consumption-query.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/uc06-consumption-query.puml)

Các ngoại lệ: timestamp/range/filter sai; cursor giả/sai caller; cùng queryRequestId đổi filter; query BUILDING sau lỗi; snapshot hết retention; first response mất; nguồn xuất hiện sau khi READY. Các trang cũ vẫn ổn định, không hứa snapshot là bản chốt ngày hoàn chỉnh.

**Hình 24. Activity Diagram JOB-02 — Tx0, nguồn cố định, FINALIZED và ADJUSTMENT**

![Hình 24 — Activity Diagram JOB-02 — Tx0, nguồn cố định, FINALIZED và ADJUSTMENT](02-analysis/activity-diagrams/09-daily-report.png)

[PNG](02-analysis/activity-diagrams/09-daily-report.png) · [SVG để phóng to](02-analysis/activity-diagrams/09-daily-report.svg) · [Nguồn PlantUML](02-analysis/activity-diagrams/09-daily-report.puml)

**S-J02:** run ngày trước, lưu cutoff, chốt contribution set, aggregate và FINALIZED. **A-J02:** nguồn thuộc ngày đã finalized xuất hiện muộn được ghi một ADJUSTMENT kỳ sau, giữ ngày gốc. **X-J02:** chết sau Tx0 giữ cutoff, chết sau Tx1 tiếp aggregate đúng nguồn đã lưu; retry sau FINALIZED không sửa tổng; CSV lỗi chỉ xuất lại, không cộng lại nguồn. Không có nguồn vẫn finalized 0 đúng nghĩa, không coi là lỗi.


### 8.4 JOB-02 — Đóng kỳ ngày

JOB-02 lúc 00:30 mặc định làm ngày trước và xử lý các ngày còn thiếu theo thứ tự. Ngày D là từ D 00:00 đến D+1 00:00 theo giờ Việt Nam, đổi sang UTC khi truy vấn. Summary giữ chiều thuê bao/gói/sourceType để còn phân biệt tiêu dùng với phí.

| Bước thiết kế | Việc được lưu bền vững | Khi chết/chạy lại |
|---|---|---|
| Tx0 | Period/job linkage và cutoff đầu tiên | Dùng lại period/cutoff dù chưa tạo xong tập nguồn |
| Tx1 | Contribution/source membership cố định và SNAPSHOTTED | Nếu đã commit, không quét thêm nguồn vào cùng tập; nếu rollback, thử lại cùng cutoff |
| Tx2 | Tổng/count từ contribution đã lưu và FINALIZED | Finalized bất biến; retry không increment mù |
| Export | CSV từ đúng phiên bản finalized | Lỗi file chỉ retry export, không tổng hợp hoặc thu phí lại |

`UNIQUE(source_transaction_id)` trên contribution bảo vệ một nguồn trên toàn bộ BASE và ADJUSTMENT. Chỉ unique theo `(source,kind)` sẽ vẫn cho phép cùng nguồn vừa BASE vừa ADJUSTMENT và là sai. Source ngày cũ chưa finalized được xử lý trong ngày nguồn theo thứ tự, không tự gán adjustment sai.

### 8.5 Giao dịch muộn và điều chỉnh

Nếu nguồn đã commit và có mặt trong tập chốt ngày gốc thì góp BASE. Nếu thuộc ngày đã finalized nhưng chỉ xuất hiện sau tập chốt, lượt sau ghi ADJUSTMENT vào kỳ sau, giữ transaction nguồn/ngày gốc/amount/count/reason. Không sửa ngầm bản finalized và không đổi recordedAt để ép nguồn thành ngày mới.

Ví dụ ngày 20 đã chốt tổng 100.000. Một khoản 5.000 thuộc ngày 20 xuất hiện muộn. Bản gốc vẫn 100.000; một kỳ sau có adjustment +5.000 có truy vết về ngày 20. Tổng các contribution sau khi bắt kịp bằng 105.000 cho các nguồn đó, mỗi source đúng một lần. Query mới đọc ledger có thể thấy nguồn trước khi job đưa vào adjustment; sự khác nhau phải giải thích được theo thời điểm/snapshot.

**Hình 25. Vòng đời kỳ báo cáo**

![Hình 25 — Vòng đời kỳ báo cáo](02-analysis/state-diagrams/report-period.png)

[PNG](02-analysis/state-diagrams/report-period.png) · [SVG để phóng to](02-analysis/state-diagrams/report-period.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/report-period.puml)

**Hình 26. Sequence chốt kỳ, cutoff và late adjustment**

![Hình 26 — Sequence chốt kỳ, cutoff và late adjustment](02-analysis/sequence-diagrams/job02-daily-aggregation.png)

[PNG](02-analysis/sequence-diagrams/job02-daily-aggregation.png) · [SVG để phóng to](02-analysis/sequence-diagrams/job02-daily-aggregation.svg) · [Nguồn PlantUML](02-analysis/sequence-diagrams/job02-daily-aggregation.puml)

Các ca cần test: không có nguồn thì finalized count/sum 0; chết sau Tx0, sau Tx1, sau Tx2; hai runner cùng period; lost commit ACK; source muộn; unique conflict; dữ liệu sai attribution; CSV lỗi. Nguồn sai không được tự gán gói tùy ý để báo cáo có vẻ đủ.

Nguồn: [UC06](01-requirements/use-cases/UC06-Consumption-Query.md), [jobs](01-requirements/scheduled-jobs.md), [CR-TIME-001](00-project/CR-TIME-001-reporting-time.md), [concurrency](04-design/concurrency-and-jobs.md).

<a id="business-classes"></a>

## 9. Class Diagram — các lớp nghiệp vụ và quan hệ

Phần này là **sơ đồ lớp**, khác ERD. Mỗi hình thể hiện tên lớp, thuộc tính nghiệp vụ, bội số và quan hệ; chưa phải bảng/cột SQL hay JPA Entity. Design Class có service/controller/port nằm ở chương thiết kế chi tiết phía sau.

### 9.1 Khách hàng → thuê bao → tài khoản → giao dịch

**Hình 27. Class Diagram: Customer, Subscriber, Account và Transaction**

![Hình 27 — Class Diagram: Customer, Subscriber, Account và Transaction](02-analysis/class-diagrams/customer-subscriber-account.png)

[PNG](02-analysis/class-diagrams/customer-subscriber-account.png) · [SVG để phóng to](02-analysis/class-diagrams/customer-subscriber-account.svg) · [Nguồn PlantUML](02-analysis/class-diagrams/customer-subscriber-account.puml)

Customer sở hữu nhiều Subscriber; mỗi Subscriber gắn một số được cấp và nhiều Account tách số dư. Giấy tờ đã xác minh liên kết Customer, không dùng tên/ngày sinh làm khóa ghép. FinancialTransaction diễn tả lịch sử của đúng một account; credit/debit khác nhau về chiều nhưng cùng yêu cầu đối soát before/after.

### 9.2 Sản phẩm → đăng ký ổn định → các kỳ đã trả → ý định hủy

**Hình 28. Class Diagram: ServicePackage, PackageSubscription, Cycle và CancellationIntent**

![Hình 28 — Class Diagram: ServicePackage, PackageSubscription, Cycle và CancellationIntent](02-analysis/class-diagrams/package-cycle-cancellation.png)

[PNG](02-analysis/class-diagrams/package-cycle-cancellation.png) · [SVG để phóng to](02-analysis/class-diagrams/package-cycle-cancellation.svg) · [Nguồn PlantUML](02-analysis/class-diagrams/package-cycle-cancellation.puml)

ServicePackage là định nghĩa gói; PackageSubscription là đăng ký theo subscriber+package; SubscriptionCycle là từng kỳ có terms đã trả. Một root có thể có nhiều cycle nên coverageEnd có thể xa hơn end kỳ đang dùng. CancellationIntent giữ ý định/receipt/kết luận, giúp không nhầm PENDING với accepted hoặc CANCELLED.

### 9.3 Request → nguồn tiền → snapshot query và contribution ngày

**Hình 29. Class Diagram: Request, giao dịch nguồn, query và báo cáo ngày**

![Hình 29 — Class Diagram: Request, giao dịch nguồn, query và báo cáo ngày](02-analysis/class-diagrams/requests-and-reporting.png)

[PNG](02-analysis/class-diagrams/requests-and-reporting.png) · [SVG để phóng to](02-analysis/class-diagrams/requests-and-reporting.svg) · [Nguồn PlantUML](02-analysis/class-diagrams/requests-and-reporting.puml)

Một operation thủ công có tối đa một hiệu ứng tiền trong phạm vi đang xét; UC03 không có ledger. Một nguồn có thể thuộc nhiều lượt query, nhưng chỉ đóng góp một lần toàn cục vào báo cáo ngày. Snapshot query cố định kết quả đọc; period/contribution cố định bản tổng hợp. Không dùng một thực thể chung để làm cả ba việc rồi mất dấu ý nghĩa.

### 9.4 Đọc class theo các luồng vừa xem

| Luồng | Các lớp chịu ảnh hưởng |
|---|---|
| Đấu nối | Customer, VerifiedIdentityDocument, Subscriber, PhoneNumber, MAIN Account và ActivationRequest |
| Mua/nối/gia hạn | ServicePackage, PackageSubscription, SubscriptionCycle, Account, Fee Transaction và request/renewal outcome |
| Hủy | OperationRequest, CancellationIntent và PackageSubscription; không tạo refund transaction |
| Tiêu dùng | Account, SubscriptionCycle, FinancialTransaction và OperationRequest |
| Query/tổng hợp | FinancialTransaction, ReportQuery/FixedQuerySource/SubscriberReportRow, DailyReportPeriod/ReportContribution |

Mô hình Domain đầy đủ vẫn có trong phần phân tích; ERD và Design Class ở phần sau giải thích cách lưu và cách tổ chức code cho các khái niệm này.


<a id="overview"></a>

## 10. Bức tranh toàn dự án và trạng thái

### 10.1 Hệ thống làm gì?

Một khách hàng đến cửa hàng muốn sử dụng dịch vụ. CSKH gửi hồ sơ lên backend để tạo thuê bao và cấp số. Sau đó thuê bao có tài khoản tiền, có thể đăng ký gói, tiêu dùng, mua nối hoặc yêu cầu hủy. Backend cần giữ đúng tiền, quyền dùng gói và lịch sử khi nhiều lệnh chạy cùng lúc, khi job chạy lại hoặc khi mạng làm mất phản hồi.

Hai đầu ra cùng quan trọng: **xử lý nghiệp vụ đúng** và **có bằng chứng đối soát được**. Trả HTTP nhanh nhưng trừ hai lần là sai; tổng số dư khớp nhưng một request bị gắn nhầm thuê bao cũng sai. Báo cáo phải giải thích được mỗi khoản tiền đi từ giao dịch nguồn nào.

### 10.2 Các phase gắn với nhau ra sao?

| Phase | Câu hỏi cần giải quyết | Sản phẩm chính | Điều kiện chuyển mốc |
|---|---|---|---|
| 0 — Định nghĩa dự án | Làm gì, không làm gì, từ nào có nghĩa gì? | Overview, scope/glossary, DEC/CR/OQ | Baseline được duyệt M0 |
| 1 — Yêu cầu | Với từng tình huống, cái gì phải đúng? | SRS, UC, jobs, AC và truy vết | UC01 đủ rõ, không còn OQ chặn, được duyệt M1 |
| 2 — Phân tích | Ai tương tác, đối tượng nào, trạng thái và thứ tự xử lý? | Use Case, Domain, State, Sequence | Mô hình nền và UC01 nhất quán, được duyệt M2 |
| 3 — Kiến trúc | Chia trách nhiệm và tích hợp thế nào? | C4, module, bảo mật, runtime, ADR | Ranh giới/stack/transaction đủ để thiết kế UC01, M3 |
| 4 — Thiết kế chi tiết | DB/API/lớp/khóa thực hiện quy tắc bằng cách nào? | Schema, ERD, OpenAPI, Design Class, concurrency | Đủ thiết kế UC01 và chống cấp số trùng, được duyệt M4 |
| 5 — Triển khai | Chạy được một UC hoàn chỉnh và test được chưa? | Java, migration, API, unit/integration tests | M5 cho UC01, M6 cho nghiệp vụ bắt buộc |
| 6 — Kiểm thử hệ thống/tải | Dữ liệu lớn và cạnh tranh có đúng/đủ/đạt ngưỡng? | Seed, workload, traces, đối soát, báo cáo | Bằng chứng tái lập đạt M7 |
| 7 — Bàn giao | Người khác có tự dựng và kiểm hệ thống được không? | Packaging, cấu hình, runbook, backup/restore | Triển khai và kiểm theo tài liệu được, M8 |

Trình tự triển khai là từng lát nghiệp vụ: thiết kế đủ UC01 → code/test UC01 → hoàn thiện thiết kế UC02 khi tới UC02 → code/test UC02… Bộ báo cáo này bao quát cả hệ thống để nhìn được liên hệ, không yêu cầu phải hoàn tất implementation mọi UC trước khi kiểm chứng UC01.

### 10.3 Tình trạng thực tế tại ngày lập báo cáo

- Có bộ yêu cầu, phân tích, kiến trúc và thiết kế REVIEW được đồng bộ theo phiếu 16 và các xác nhận bổ sung.
- M0–M4 lịch sử chỉ có hiệu lực trong phạm vi ghi nhận của chúng; đổi DBMS sang MySQL cần nghiệm thu delta thiết kế.
- Repo có Maven pom/Main mẫu; tài liệu không chứng minh đã có backend nghiệp vụ, migration hoặc Compose được kiểm chứng. Các dependency cũ được xử lý trong bước triển khai thích hợp.
- Có kế hoạch test và 64 ca biên; trạng thái runtime đều NOT_RUN. Không có số đo TPS hoặc tuyên bố đạt M5–M8.

Nguồn trạng thái: [ROADMAP](ROADMAP.md). Bằng chứng tài liệu: [verification](verification.md).

<a id="phase0"></a>

## 11. Phase 0 — Đề bài, phạm vi và quyết định

### 11.1 Từ đề bài tới cách hiểu hiện hành

Đề bài yêu cầu mô hình dữ liệu quan hệ; API đấu nối, đăng ký/hủy gói, cộng/trừ tiền; báo cáo tiêu dùng theo thời gian/gói; job xử lý hết hạn; dữ liệu ít nhất 500.000 bản ghi; tải đồng thời và kiểm chứng trừ tiền đúng khi tranh chấp. [Đề bài gốc](requirement.md) được giữ nguyên, các cách diễn giải nằm trong DEC/CR/SRS.

Ví dụ, mô tả ban đầu về khách hàng và số thuê bao được làm rõ thành Customer–Subscriber 1–N. “Xóa dữ liệu hết hạn” được thực hiện bằng trạng thái/soft delete để giữ tiền và lịch sử, không xóa ledger. Đây là các quyết định đã ghi nhận, không phải tự sửa nguyên văn đề bài.

### 11.2 Người dùng, bên tích hợp và ranh giới

| Bên tham gia | Vai trò |
|---|---|
| Khách hàng | Cung cấp thông tin, chọn số/chờ, mua hoặc hủy gói qua CSKH |
| Ứng dụng CSKH | Gọi API, tự quản lý requestId, gửi lại/tra cứu khi cần; không buộc nhân viên gõ mã yêu cầu thủ công |
| Bên khai thác báo cáo | Gọi query được phân quyền, đọc tập kết quả và trang tiếp theo |
| Quản trị được cấp quyền | Chạy/tra job qua API nội bộ có kiểm quyền |
| IdP/Keycloak | Phát JWT bên ngoài backend; backend xác minh chữ ký và quyền |
| Backend và tác vụ nội bộ | Thực hiện nghiệp vụ, kiểm điều kiện, giữ trạng thái và khôi phục công việc |

Trong phạm vi có quản lý hồ sơ đã xác minh, kho số, thuê bao, gói/kỳ, tài khoản/giao dịch, tra cứu và tổng hợp. Ngoài phạm vi có kích hoạt mạng thật, KYC nội bộ hoàn chỉnh, giao diện CSKH đầy đủ, mobile app, ngân hàng/cổng thanh toán, đo lưu lượng và billing engine, BI hoàn chỉnh, hoàn tiền hoặc quy trình tái sử dụng số chưa được chốt.

### 11.3 Những quyết định mới ảnh hưởng nhiều phần

| Quyết định | Hành vi có hiệu lực | Phần bị ảnh hưởng |
|---|---|---|
| CR-UC01-001 | Đồng bộ là chính; chỉ AUTO hết số và có consent mới WAITING tối đa 24 giờ | UC01, FIFO, recovery, API, cấp số |
| CR-DB-001 + Q13=B | MySQL 8.0.46/InnoDB thay PostgreSQL | Schema, khóa, migration, môi trường, test |
| DEC-JOB-001 | Worker gia hạn thường xuyên + JOB-01 00:05; JOB-02 00:30 | Vòng đời gói/tài khoản, tránh thu trùng |
| DEC-BE-21 | Ngày đăng ký tính ngày đầu; end theo ngày lịch địa phương | Mọi kỳ gói, kiểm quyền, gia hạn, hủy |
| CR-CAN-002 | Hủy cuối toàn bộ thời gian đã trả, không hoàn phí | Coverage, trả trước, hủy/gia hạn |
| CR-CAN-003 | Receipt hủy lấy sau xác nhận commit ý định và kiểm giờ DB mới | Hai bước hủy, sát hạn, recovery, mất phản hồi |
| CR-TIME-001 | Báo cáo theo recordedAt trong transaction cuối cùng đã commit | Query, cutoff, ngày nguồn, adjustment |
| CR-TIME-002 | Kiểm hạn bằng giờ DB dưới khóa trước cập nhật, có thể commit sau hạn | Cộng/trừ/phí và test biên |

Q26/Q34=B vẫn được giữ trong phiếu gốc để lưu lịch sử, nhưng hành vi hiện hành theo CR-TIME-001/002 mới hơn. Chọn nhóm bảng hoặc MySQL chưa đồng nghĩa duyệt mọi index, UUID representation, DTO/HTTP hoặc cấu hình kỹ thuật.

Nguồn: [overview](00-project/overview.md), [scope/glossary](00-project/scope-and-glossary.md), [bản tiếp nhận](00-project/decision-receipt-20261026.md), [clarification log](00-project/clarification-log.md).

<a id="phase1"></a>

## 12. Phase 1 — Quy tắc chung và yêu cầu phải đúng

### 12.1 Các đối tượng dễ nhầm

| Khái niệm | Cách hiểu trong dự án |
|---|---|
| Customer | Hồ sơ khách hàng; có thể sở hữu nhiều Subscriber |
| Subscriber | Một thuê bao có số điện thoại duy nhất được cấp trong backend |
| Verified document | Loại+số giấy tờ và tham chiếu chứng cứ xác minh ngoài; không tự tin một chuỗi nhập tay |
| ServicePackage | Định nghĩa sản phẩm: thời lượng, giá/chính sách hiện hành và trạng thái bán |
| PackageSubscription | Đăng ký ổn định của một thuê bao với một gói; giữ version, coverage và ý định hủy |
| SubscriptionCycle | Một kỳ đã trả có start/end, giá và thời lượng snapshot bất biến |
| coverageEnd | Cuối toàn bộ thời gian liên tiếp đã trả, có thể xa hơn end kỳ đang sử dụng |
| Account | Một tài khoản tiền riêng của thuê bao; MAIN và phụ không trộn số dư |
| Financial transaction | Giao dịch nguồn đã ghi nhận, gắn account, chiều, số tiền và attribution phù hợp |
| Operation/request | Yêu cầu nghiệp vụ có mã, nội dung và kết quả; retry không tạo lại tác động |

Một thuê bao có nhiều gói không làm một account tự chia tiền theo gói. API và attribution xác định khoản tiền đang làm gì. Phí đăng ký khác lệnh tiêu dùng độc lập; cộng tiền không tự đăng ký gói hoặc khôi phục một lần gia hạn đã FAILED.

### 12.2 Bất biến cần giữ trên mọi đường xử lý

1. Một số không được cấp cho hai thuê bao. Tra cứu AVAILABLE không phải giữ chỗ.
2. Đăng ký gói chỉ có quyền khi phí thành công; không có thành công nửa chừng giữa tiền và kỳ.
3. Không âm tiền; thiếu tiền không thay số dư và không tạo ledger tài chính thành công.
4. UC04/UC05 tác động đúng accountId, không fallback MAIN và không gộp ví. Phí gói luôn MAIN.
5. Cùng yêu cầu không tạo hai tác động. Hai mã mới tranh chấp cùng kỳ vẫn cần version/khóa nghiệp vụ.
6. Hủy bảo toàn mọi kỳ đã trả, không refund và không nối thêm sau khi nhận hủy hợp lệ.
7. Quyền dùng gói/account được kiểm bằng mốc thời gian, không chờ job đổi nhãn mới chặn.
8. Ledger, kỳ và history được giữ; mỗi nguồn tiêu dùng/phí chỉ đóng góp một lần vào tổng hợp ngày trên toàn bộ BASE/ADJUSTMENT.

### 12.3 Thời gian có nhiều ý nghĩa khác nhau

| Mốc | Dùng để làm gì? | Ví dụ quan trọng |
|---|---|---|
| start/end kỳ | Xác định quyền dùng theo `[start,end)` | Đúng end đã hết quyền |
| eligibilityCheckedAt | Giờ DB dưới khóa ngay trước mutation | Check trước expiry có thể commit qua expiry |
| recordedAt | Mốc ghi nhận nghiệp vụ của transaction cuối cùng đã commit | Ghi ngày 20, commit ngày 21 vẫn thuộc ngày ghi nhận 20 |
| receiptCheckedAt | Mốc nhận hủy sau khi backend biết TxA đã commit | Phải nhỏ hơn coverageEnd để nhận hủy |
| cutoff | Mốc lọc được lưu đầu tiên của query/period | Retry không đổi sang mốc mới |
| HTTP arrival/timeout | Quan sát của mạng/caller | Không chứng minh đã nhận hủy hoặc DB rollback |

Lưu UTC bằng DATETIME(6), API có offset/múi giờ; tính ngày nghiệp vụ theo `Asia/Ho_Chi_Minh`. Gói 30 ngày từ 10:15 ngày 01/10/2026 kết thúc 00:00 ngày 31/10/2026. Kỳ nối liên tục bắt đầu ở end cũ; sau gián đoạn bắt đầu ở lúc xử lý hợp lệ, không cấp quyền ngược khoảng trống.

### 12.4 Idempotency, version và timeout

`requestId` định danh ý định gửi lệnh của caller trong loại thao tác. Nội dung được chuẩn hóa/canonical để nhận cùng nghiệp vụ dù JSON đổi thứ tự hoặc correlation/token thay đổi. Thay số tiền, đối tượng hay dữ liệu nghiệp vụ đã tham gia fingerprint thì không còn là cùng nội dung.

| Tình huống | Cách xử lý |
|---|---|
| Cùng caller/operation/id, cùng nội dung, đã terminal | Replay outcome cũ, không làm lại |
| Cùng key, khác nội dung | IDEMPOTENCY_CONFLICT, giữ bản gốc |
| Cùng key đang xử lý | Trả/tra PROCESSING; không tạo operation thứ hai |
| Hai key mới cùng expectedVersion | Version/target guard quyết định một người thắng; không tự nối thêm kỳ ngoài ý định |
| POST timeout | Tra cùng key hoặc retry cùng key/nội dung, không mặc định thất bại |
| Hết full-result replay | Tombstone giữ khóa/fingerprint/tóm tắt, không cho tái dùng để làm lại |

Full replay: UC02/04/05 là 30 ngày sau trạng thái cuối; UC03 và UC01 là 7 ngày theo baseline. Không dọn PROCESSING/WAITING/PENDING đang xử lý. Retention phản hồi khác retention lịch sử nghiệp vụ 5 năm có cấu hình; demo không purge lịch sử. Đây không phải tuyên bố chính sách pháp lý production.

Các giới hạn đã chọn: số tiền nguyên VND, mỗi lệnh tối đa 10 triệu, số dư tối đa 100 triệu, có cấu hình. Kiểm tràn và giới hạn vẫn bắt buộc dù DB dùng kiểu số nguyên lớn.

Nguồn chi tiết: [SRS](01-requirements/SRS.md), [API](04-design/api-contract.md), [concurrency](04-design/concurrency-and-jobs.md).

<a id="phase2"></a>

## 13. Phase 2 — Cách nhìn mô hình UML

### 13.1 Các chức năng và tác nhân

Use Case cho biết ai yêu cầu hệ thống làm gì. Hình không thay mô tả lỗi/transaction và không có nghĩa mọi chức năng là một endpoint riêng. Scheduler và worker thuộc bên trong hệ thống; admin bên ngoài có thể yêu cầu chạy job bằng API được phân quyền.

**Hình 30. Use Case toàn hệ thống**

![Hình 30 — Use Case toàn hệ thống](02-analysis/use-case-diagram.png)

[PNG](02-analysis/use-case-diagram.png) · [SVG để phóng to](02-analysis/use-case-diagram.svg) · [Nguồn PlantUML](02-analysis/use-case-diagram.puml)

Khi đọc, tìm sáu UC bắt buộc, các thao tác tra cứu hỗ trợ và ranh giới IdP. Nếu một nhánh như “đồng ý chờ” chỉ áp dụng khi AUTO hết kho thì không diễn giải nó thành tính năng áp dụng cho mọi yêu cầu đấu nối.

### 13.2 Mô hình nghiệp vụ

**Hình 31. Domain Model nghiệp vụ**

![Hình 31 — Domain Model nghiệp vụ](02-analysis/domain-model.png)

[PNG](02-analysis/domain-model.png) · [SVG để phóng to](02-analysis/domain-model.svg) · [Nguồn PlantUML](02-analysis/domain-model.puml)

Đi từ Customer tới Subscriber, từ Subscriber tới Account và PackageSubscription, rồi từ đăng ký tới các SubscriptionCycle. Transaction ghi tiền, OperationRequest giữ ý định/kết quả, CancellationIntent diễn tả hủy đang chờ/đã nhận. ReportPeriod và Contribution phục vụ tổng hợp có truy vết.

Các quan hệ cốt lõi: Customer 1–N Subscriber; mỗi số tối đa một Subscriber; một đăng ký có nhiều kỳ lịch sử; một kỳ đã trả có một phí; một tiêu dùng gắn một kỳ hợp lệ; một nguồn được đưa vào nhiều query snapshot khác nhau nhưng chỉ một contribution tổng hợp ngày toàn cục.

Đây là mô hình khái niệm, không phải danh sách JPA Entity. ERD ở Phase 4 mới thể hiện bảng, khóa và ràng buộc vật lý. Design Class mô tả trách nhiệm/lời gọi ở mức triển khai.

<a id="phase3"></a>

## 14. Phase 3 — Kiến trúc, module và bảo mật

### 14.1 Context: backend đứng ở đâu?

**Hình 32. C4 System Context**

![Hình 32 — C4 System Context](03-architecture/c4-diagrams/context-and-container.png)

[PNG](03-architecture/c4-diagrams/context-and-container.png) · [SVG để phóng to](03-architecture/c4-diagrams/context-and-container.svg) · [Nguồn PlantUML](03-architecture/c4-diagrams/context-and-container.puml)

CSKH và báo cáo gọi backend qua REST có JWT. IdP phát token ở bên ngoài; backend không tự thêm nghiệp vụ cấp JWT nội bộ. Khách hàng thao tác qua CSKH, không yêu cầu dự án phải có mobile/UI hoàn chỉnh.

### 14.2 Container và nguồn có thẩm quyền

**Hình 33. C4 Container và dịch vụ nền**

![Hình 33 — C4 Container và dịch vụ nền](03-architecture/c4-diagrams/context-and-container_001.png)

[PNG](03-architecture/c4-diagrams/context-and-container_001.png) · [SVG để phóng to](03-architecture/c4-diagrams/context-and-container_001.svg) · [Nguồn PlantUML](03-architecture/c4-diagrams/context-and-container.puml)

| Thành phần | Trách nhiệm | Điều không giao cho thành phần này |
|---|---|---|
| Spring Boot/Java 21 | API, module nghiệp vụ, worker, job, recovery | Không trả success trước outcome bền vững |
| MySQL 8.0.46/InnoDB | Hồ sơ/số, request, money/ledger, cycle, intent, snapshot/summary | Không thay bằng cache/queue khi quyết định tính đúng |
| RabbitMQ | Tín hiệu đánh thức khi kho số được bổ sung sau commit | Không là nơi duy nhất giữ yêu cầu chờ hoặc nguồn quyết tiền |
| Redis | Cache catalog theo nguyên tắc phù hợp, có invalidate/TTL/fallback | Không là số dư có thẩm quyền, request registry hay khóa thay DB cho tiền |
| Keycloak/IdP ngoài | JWT, JWKS và quyền theo cấu hình | Không nằm trong transaction thu phí |
| k6 và đối soát | Tạo workload, đo và chứng minh outcome | Không dùng số request/s để thay đối soát nghiệp vụ |

Mất tín hiệu MQ được recovery từ DB bù; duplicate signal không tạo cấp số lần hai. Cache stale không được dùng làm giá thu hoặc quyền gói có thẩm quyền. Khi IdP/JWKS lỗi, xử lý theo chính sách xác minh token đã thiết kế và cấu hình, không bỏ kiểm quyền để giữ TPS.

### 14.3 Module

**Hình 34. Phụ thuộc giữa các module backend**

![Hình 34 — Phụ thuộc giữa các module backend](03-architecture/module-structure.png)

[PNG](03-architecture/module-structure.png) · [SVG để phóng to](03-architecture/module-structure.svg) · [Nguồn PlantUML](03-architecture/module-structure.puml)

Activation phối hợp customer/inventory/account để đấu nối; Subscription/Catalog phối hợp paid cycle và phí; Account/Ledger giữ biến động; Operation Registry giữ claim/outcome; Reporting cố định tập nguồn và summary; Scheduling gọi các năng lực đó thay vì viết lại quy tắc.

Tương tác có tiền đi trong transaction nội bộ; không gọi HTTP ngược UC05 để thu phí đăng ký. Không giữ khóa DB trong lúc gọi mạng/IdP/broker/cache. Domain service/port giúp kiểm thử trách nhiệm nhưng không làm mất ranh giới nguyên tử của một nghiệp vụ.

### 14.4 Bảo mật và truy vết

JWT được xác minh thuật toán/chữ ký/JWKS, issuer, audience, exp/nbf và scope theo cấu hình đáng tin. Caller lấy từ claim đã xác minh như client_id/azp theo mapping, không tin một header tự khai. Thiếu token/đủ token nhưng sai quyền là hai tình huống khác nhau.

Demo dùng chung dữ liệu giả lập nhưng vẫn phân scope thao tác. Kết quả request/query chỉ caller tạo được đọc; lookup của caller khác trả theo contract tránh lộ existence. Evidence reference chỉ caller có quyền xác minh ngoài mới được gửi; backend lưu/kiểm nguồn/định dạng, không tự tạo hệ thống KYC mới.

Log dùng correlationId, request/operation/job key và mã lỗi; không ghi JWT, secret hoặc giấy tờ đầy đủ. Internal job API vẫn kiểm JWT/scope, không vì có chữ internal mà mở bypass. Cấu hình issuer/JWKS/claim/secret thật phải kiểm trước runtime.

### 14.5 Môi trường

Ở công ty đọc/duyệt/code không DB. Ở nhà dự kiến Compose cho MySQL/RabbitMQ/Redis/Keycloak; backend chạy IDE khi phát triển và container lúc bàn giao. Cấu hình tách môi trường, không sửa code theo từng máy. Hiện chưa coi Compose/topology là đã triển khai thành công.

Nguồn: [architecture](03-architecture/architecture.md), [module](03-architecture/module-structure.md), [security](03-architecture/security-and-integration.md), [profiles](03-architecture/runtime-profiles.md), [topology](03-architecture/docker-local-topology.md), [ADR](03-architecture/adr/README.md).

<a id="phase4"></a>

## 15. Phase 4 — Dữ liệu, API, transaction và Design Class

### 15.1 ERD và các nhóm bảng

**Hình 35. ERD MySQL toàn backend**

![Hình 35 — ERD MySQL toàn backend](04-design/erd.png)

[PNG](04-design/erd.png) · [SVG để phóng to](04-design/erd.svg) · [Nguồn PlantUML](04-design/erd.puml)

Thiết kế có 21 bảng/nhóm thực thể vật lý dưới đây. Bảng này giải thích vai trò, không thay danh sách cột/constraint/index trong schema.

| Bảng | Vai trò và ràng buộc đáng chú ý |
|---|---|
| customer | Hồ sơ chủ sở hữu thuê bao; không unique theo tên/ngày sinh để tự ghép người |
| verified_document | Giấy tờ đã xác minh liên kết Customer; loại+số chuẩn hóa duy nhất theo thiết kế |
| phone_number_inventory | Số E.164 và trạng thái kho; số không được cấp trùng |
| subscriber | Thuê bao thuộc Customer; liên kết duy nhất tới số đã cấp |
| service_account | MAIN/phụ, balance/quyền/expiry; account thuộc subscriber và tách số dư |
| activation_request | Claim, fingerprint, lease/fence, deadline và outcome UC01 |
| allocation_gate | Điểm phối hợp cấp số/FIFO theo thiết kế UC01, không phải một số được giữ chỗ bởi tra cứu |
| service_package | Catalog/giá/thời lượng/version, sale status và grandfather policy |
| package_subscription | Root ổn định theo subscriber+package, version/state/coverage và hủy |
| subscription_cycle | Kỳ bất biến, cycle key duy nhất trong root, snapshot terms và fee liên kết |
| operation_request | Claim/outcome UC02–05 theo caller+operation+requestId, replay/tombstone/fence |
| financial_transaction | Ledger có logical key, account/amount/before/after/recordedAt và attribution |
| cancellation_intent | PENDING/ACCEPTED/REJECTED, observed coverage/version, receipt/effective timestamps |
| report_query | Caller/queryRequestId/filterHash/cutoff và trạng thái BUILDING/READY/FAILED |
| report_snapshot_member | Nguồn ledger được copy cố định cho một query; query+source unique |
| report_snapshot_subscriber | Kết quả DISTINCT thuê bao và totals từ persisted members; query+subscriber unique |
| renewal_attempt | Outcome cùng target bền vững, kể cả thiếu tiền FAILED; root+target unique |
| daily_report_period | Ngày/kind unique, cutoff và BUILDING/SNAPSHOTTED/FINALIZED |
| summary_contribution | Nguồn BASE/ADJUSTMENT; source transaction unique toàn cục |
| daily_consumption_summary | Tổng/count theo period/subscriber/package/sourceType từ contribution |
| job_execution | Run key/job code, lease/fence/checkpoint/counters và trạng thái run |

MySQL dùng utf8mb4; mã định danh so sánh theo quy tắc binary rõ ràng; UTC DATETIME(6). UUID CHAR(36) ASCII binary là đề xuất REVIEW theo Q14=C, không coi người dùng đã chọn sẵn biểu diễn này. Số tiền mỗi giao dịch dùng miền integer và cap đã chọn; số tổng phải được kiểm tràn trong triển khai/đối soát, không lấy cap một account áp cho tổng báo cáo toàn hệ thống.

FK/unique/CHECK là lớp bảo vệ cần thiết nhưng không tự chứng minh nguyên tử liên bảng, đúng owner, không overlap kỳ hoặc fee/cycle khớp. Fee liên kết một chiều qua cycle.fee_transaction_id theo schema để tránh vòng NOT NULL FK; consumption có cycle attribution. ERD đơn lẻ không đủ triển khai đúng các bất biến này.

### 15.2 Các điểm vào API

Các endpoint/DTO/HTTP/scopes chi tiết mới là thiết kế REVIEW. OpenAPI là nguồn máy đọc; bảng dưới giúp theo dõi mục đích và kết quả chính.

| UC/nhóm | Method và path | Scope bắt buộc | Kết quả |
|---|---|---|---|
| UC01 | POST /api/v1/activations | telecom:activation:write + telecom:evidence:submit |201 mới thành công;200 replay;202 PROCESSING/WAITING |
| UC01 | GET /api/v1/activations/{requestId} | telecom:activation:read |200 terminal,202 pending,410 tombstone |
| support | GET /api/v1/phone-numbers | telecom:inventory:read | AVAILABLE không giữ số |
| UC02 | POST /api/v1/package-subscriptions | telecom:subscription:write |201 mới,200 replay; phí MAIN và kỳ nguyên tử |
| UC03 | POST /api/v1/package-subscriptions/{subscriptionId}/cancellations | telecom:subscription:cancel |200 accepted sau durable coordination;202 đang xử lý; không tiền hoàn |
| UC04 | POST /api/v1/accounts/{accountId}/credits | telecom:account:credit |201 ledger mới;200 replay; account chính xác |
| UC05 | POST /api/v1/accounts/{accountId}/debits | telecom:account:debit |201 consumption mới;200 replay; cycle bắt buộc |
| lookup | GET /api/v1/operations/{operation}/{requestId} | telecom:operation:read + scope operation gốc |200 terminal,202 processing,410 tombstone |
| support | GET /api/v1/packages | telecom:package:read | giá/duration/version, saleStatus và grandfather policy |
| support | GET /api/v1/subscribers/{subscriberId}/package-subscriptions | telecom:subscription:read | version/coverageEnd/currentCycle; không root là expectedVersion0 |
| UC06 | GET /api/v1/reports/consumption/subscribers | telecom:report:read |200 snapshot members,202 BUILDING+queryId,410 hết snapshot |
| internal | POST /api/v1/internal/jobs/{jobCode}/runs | telecom:job:run |202 pending/running,200 existing/terminal; jobCode JOB-01/JOB-02 |
| internal | GET /api/v1/internal/jobs/{jobCode}/runs/{runId} | telecom:job:read |200 trạng thái run,404 sai job/run |

Caller lấy version qua API hỗ trợ nhưng kết quả đọc có thể cũ ngay sau đó; POST vẫn phải kiểm version dưới phối hợp đúng. Query/report lookup và operation lookup khác nhau; không dùng một trạng thái query BUILDING để suy ra nghiệp vụ tiền đã thành công.

| Nhóm kết quả | Cách hiểu |
|---|---|
| 201 / 200 | Mới thành công hoặc replay/tra kết quả theo từng operation |
| 202 | PROCESSING/WAITING/query BUILDING/run pending; không phải quyền/tiền đã hoàn tất |
| 400 / 401 / 403 / 404 | Input/cursor sai, token sai, thiếu quyền, không thấy resource theo contract |
| 409 | Nội dung cùng key khác, version/target xung đột, điều kiện nghiệp vụ conflict |
| 422 | Thiếu tiền, account/cycle không hợp lệ, receipt hủy muộn và các lỗi nghiệp vụ tương ứng |
| 410 | Full replay/query snapshot hết hạn theo contract; không mở lại tác động cũ |
| 429 / 503 | Backoff/hạ tầng/unknown outcome; lookup trước retry cùng key |

POST FAILED có thể trả HTTP lỗi nghiệp vụ; GET lookup terminal có thể trả HTTP 200 cùng `status=FAILED`. Không lấy riêng HTTP 200 để đếm success. CorrelationId dùng truy vết, requestId dùng identity nghiệp vụ; hai mã không thay nhau.

### 15.3 Transaction, khóa và race

Các đường ghi tiền có lock order theo thiết kế: request → account → subscription slot → cycle; kiểm điều kiện dưới phối hợp và lấy DB clock mới ngay trước writes. Đường hủy request→slot/intent không được lấy account về sau theo chiều ngược để tạo vòng deadlock. Cấp số có thứ tự/gate riêng được đặc tả cho UC01.

Candidate query, cache và lần GET trước đó chỉ là thông tin để thử; quyền/số dư/version phải được kiểm lại ở nơi quyết định. READ COMMITTED cho phép các lần đọc thông thường thấy dữ liệu mới giữa các statement, vì thế dùng khóa/unique/version cho ghi và materialization cho report, không coi mức cô lập tự giải quyết mọi race.

| Race | Guard cuối cùng cần có |
|---|---|
| Hai yêu cầu cấp một số | Phối hợp cấp số + unique assignment |
| Hai debit cùng account | Account lock và kiểm balance mới |
| Hai mua nối/manual-auto cùng kỳ | Expected version + stable target + cycle unique/outcome |
| Hủy pending đua extension | Cùng slot và guard intent; resolver trước quyết định tiếp |
| Hai job cùng period/source | Coordinator/fence + unique period/contribution |
| Runner hết lease vẫn ghi | Fence được kiểm trong transaction, không chỉ tin lease cục bộ |

Deadlock/lock timeout kỹ thuật cần rollback/retry hữu hạn và quan sát; không ghi thành thiếu tiền. Nếu không biết COMMIT có thành công hay chưa, dùng kết nối mới tra/khóa request/outcome để biết transaction trước đã kết thúc; chưa biết thì giữ trạng thái có thể recovery, không chạy thêm tác động bằng mã mới.

### 15.4 Vòng đời request và tombstone

**Hình 36. Vòng đời request nghiệp vụ UC02–05**

![Hình 36 — Vòng đời request nghiệp vụ UC02–05](02-analysis/state-diagrams/operation-request.png)

[PNG](02-analysis/state-diagrams/operation-request.png) · [SVG để phóng to](02-analysis/state-diagrams/operation-request.svg) · [Nguồn PlantUML](02-analysis/state-diagrams/operation-request.puml)

Terminal FAILED là kết quả ổn định của ý định đã xử lý, không phải “thử lại đến khi điều kiện thuận lợi”. Tombstone ngăn mã cũ thành ý định mới sau khi bỏ payload đầy đủ. Infrastructure unknown là quan sát chưa biết outcome, không phải một success hoặc business failure được bịa ra.

### 15.5 Design Class theo trách nhiệm

Các hình sau mô tả controller/application/core/port/job. Tên phương thức là cách chia trách nhiệm đề xuất; không phải code skeleton đã được triển khai. Những đường có tiền phải vẫn nằm trong transaction đúng, dù chia thành nhiều lớp.

**Hình 37. Design Class UC01: policy, claim, allocation và finalizer**

![Hình 37 — Design Class UC01: policy, claim, allocation và finalizer](04-design/design-class-diagrams/uc01-design-class.png)

[PNG](04-design/design-class-diagrams/uc01-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/uc01-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/uc01-design-class.puml)

UC01 tách kiểm caller/evidence, canonical request, cấp số, profile và atomic finalizer. Worker/recovery dùng lại năng lực đó; không có luồng chờ riêng bỏ qua điều kiện hồ sơ/deadline.

**Hình 38. Design Class tiền: service, core và ledger**

![Hình 38 — Design Class tiền: service, core và ledger](04-design/design-class-diagrams/money-design-class.png)

[PNG](04-design/design-class-diagrams/money-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/money-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/money-design-class.puml)

MoneyService phối hợp OperationCoordinator, MoneyCore và các store/clock. Core kiểm điều kiện trên dữ liệu đã khóa; ledger và outcome cùng commit với balance. Access policy và transaction boundary vẫn bắt buộc dù hình tập trung lược bớt các đường chung.

**Hình 39. Design Class đăng ký kỳ và ý định hủy**

![Hình 39 — Design Class đăng ký kỳ và ý định hủy](04-design/design-class-diagrams/subscription-design-class.png)

[PNG](04-design/design-class-diagrams/subscription-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/subscription-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/subscription-design-class.puml)

SubscriptionService/PaidCycleCore và CancellationService chia sẻ root/slot guard. Hai bước hủy phải thể hiện xác nhận commit giữa chúng; không gộp receipt thành một timestamp bất kỳ trong TxA.

**Hình 40. Design Class query snapshot và daily summary**

![Hình 40 — Design Class query snapshot và daily summary](04-design/design-class-diagrams/report-design-class.png)

[PNG](04-design/design-class-diagrams/report-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/report-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/report-design-class.puml)

ReportQueryService/CursorCodec và DailySummaryService dùng snapshot/contribution ports. Aggregate chỉ từ persisted source set; gọi lại không tự thay cutoff hoặc cộng lại tổng đã finalized.

**Hình 41. Design Class điều phối worker và jobs**

![Hình 41 — Design Class điều phối worker và jobs](04-design/design-class-diagrams/jobs-design-class.png)

[PNG](04-design/design-class-diagrams/jobs-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/jobs-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/jobs-design-class.puml)

JobCoordinator giữ run/claim/fence/checkpoint; các job gọi service/core nghiệp vụ. Không copy logic phí sang một nhánh chỉ chạy đêm.

Hình tổng quan đầy đủ ở phụ lục để phóng to. Nguồn: [schema](04-design/database-schema.md), [API](04-design/api-contract.md), [OpenAPI](04-design/openapi.yaml), [concurrency](04-design/concurrency-and-jobs.md), [Design Class index](04-design/design-class-diagrams/README.md), [checklist M4](04-design/m4-review-checklist.md).

<a id="phase5"></a>

## 16. Phase 5 — Kế hoạch triển khai từng UC

### 16.1 Điều kiện bắt đầu

Trước P5.1 cần nghiệm thu delta MySQL UC01 theo checklist M4. Không lấy phê duyệt PostgreSQL lịch sử làm bằng chứng mọi chi tiết InnoDB đã duyệt. Đồng thời không bắt phải đóng mọi UC sau hoặc mọi tham số chưa liên quan mới cho phép bắt đầu UC01.

### 16.2 Thứ tự triển khai mục tiêu

| Vòng | Phạm vi | Sản phẩm phải chạy được và bằng chứng |
|---|---|---|
| 1 — P5.1 | Nền backend/môi trường MySQL | Build thật, cấu hình home/office, kết nối/readiness, migration nền trên DB trống, kiểm constraints |
| 2 — UC01 | JWT/evidence, claim, Customer, cấp số, MAIN, WAITING/recovery | API đồng bộ và chờ có thể tra; test cấp số race/retry/rollback/FIFO/expiry; bằng chứng M5 |
| 3 — UC02 và tiền | Credit/debit, fee/cycle/version, cap/expiry, exact account | Ledger/balance/cycle/outcome đúng và test cạnh tranh trên MySQL thật |
| 4 — UC03/renewal/JOB-01 | Hủy hai bước, trả trước, shared core, late one-cycle | Crash/ACK/boundary/version/worker-manual races và không thu trùng |
| 5 — UC06/JOB-02 | Snapshot/cursor, cutoff, contribution/finalized/adjustment | Query ổn định qua trang; rerun/late commit đối soát được |
| 6 — Hoàn thiện bắt buộc | Contract, integration, vận hành và tài liệu theo code | M6 dựa trên API/migration/test, không dựa skeleton |

### 16.3 Một slice chỉ được coi là xong khi nào?

Input sai và thiếu quyền bị từ chối đúng; luồng thành công có DB outcome; nhánh nghiệp vụ có kết quả xác định; retry/timeout không làm lại; concurrent requests giữ bất biến; có test/log đủ truy vết; tài liệu khớp revision đang chạy. Chỉ tạo controller/service/entity rỗng không đạt điều kiện.

Unit test phù hợp với quy tắc ngày/tiền/canonical; integration test MySQL cho khóa/unique/transaction; API/contract test cho JWT/DTO/status/lookup. Các adapter queue/cache có kiểm recovery khi tín hiệu mất hoặc dữ liệu cache không còn.

Nguồn: [implementation plan](04-design/implementation-plan.md), [checklist M4](04-design/m4-review-checklist.md), [test plan](05-testing/test-plan.md).

<a id="phase6"></a>

## 17. Phase 6 — Kiểm thử, tải và đối soát

### 17.1 Các tầng chứng minh

| Tầng | Điều cần chứng minh | Loại bằng chứng |
|---|---|---|
| Tài liệu | Có mô tả nhất quán, link/hình/API cấu trúc hợp lệ | Static checker, render manifest, review ghi nhận |
| Chức năng | Input/quyền/luồng chính/nhánh có kết quả đúng | AC, API response và DB state |
| Transaction | Tiền/kỳ/request cùng commit hoặc không có success trái phép | Integration test thật, ledger và outcome |
| Đồng thời | Các request chồng lấn không vượt bất biến | Barrier, lock wait/trace, per-request đối soát |
| Khôi phục | Crash/mất ACK/lease hết không mất hoặc nhân tác động | Fault hooks, restart/recovery logs |
| Tải | Đạt chỉ tiêu cùng protocol và vẫn đúng | Raw attempts, commit-window evidence, histograms, reconciliation |

27 AC-SUB của UC01 được giữ nguyên. AC từng UC/job và 64 TC-EDGE bổ sung phủ các nhánh đã xác định; một TC có thể tách thành nhiều executable tests, không vì chỉ có một mã mà chỉ chạy một trường hợp. Tất cả runtime hiện NOT_RUN.

### 17.2 Quy mô và workload đã chốt

| Tiêu chí | Cách tính cần giữ |
|---|---|
| Dữ liệu | Tổng các bảng nghiệp vụ ≥500.000 trước đo; lưu count từng bảng, không phải mỗi bảng 500K |
| Warm-up | 120 giây, tách khỏi measurement |
| Cửa sổ đo | Cùng `[T0,T1)` dài 600 giây cho UC02 và UC05 |
| Đăng ký gói | N2 ≥18.000 nghiệp vụ mới thành công đã commit trong cửa sổ, tức ≥30 TPS |
| Debit độc lập | N5 ≥18.000 mới thành công cùng cửa sổ, tức ≥30 TPS |
| Tổng | ≥60 TPS, đồng thời từng loại vẫn phải đạt riêng |
| Latency từng loại | p95 ≤500 ms, p99 ≤1 s cho responded attempts gồm replay/lỗi theo protocol |
| Lỗi hệ thống | E/A ≤0,001; A là mọi sent UC02/UC05 attempts trong cửa sổ kể cả retry; E gồm lỗi hệ thống và mất phản hồi |
| Tranh chấp | 50% mọi debit attempts độc lập, gồm retry, chia đều 10 cặp subscriber–account nóng; có overlap ở backend |
| Correctness | Đối soát từng logical request/ledger/cycle/account; unresolved=0 |

Không đếm HTTP 2xx thành nghiệp vụ mới nếu đó là replay hoặc PROCESSING. Không đếm fee UC02/renewal thành debit UC05 độc lập lần nữa. Không cộng hai run riêng để nói đạt mixed 30+30. GET lookup/health có log tải riêng, không tự đưa vào mẫu số UC02/05.

### 17.3 Xác định commit trong cửa sổ đo

recordedAt là timestamp trước khi transaction hoàn tất commit nên không chứng minh commit nằm trong `[T0,T1)`. Request gửi trước T1 nhưng chỉ xác nhận sau T1 cũng chưa đủ để kết luận commit trước T1. Thiết kế instrumentation cần log các mốc có độ bất định/đồng bộ clock được kiểm và logical key.

Khi không có nguồn xác định chính xác mọi commit ở biên, công bố tập chắc chắn trong, chắc chắn ngoài và chưa rõ tại biên. Chứng nhận ngưỡng bằng cận dưới chắc chắn, không gọi cận trên là số commit chính xác. Outcome vẫn phải được đối soát sau drain; không giấu thời điểm chưa rõ thành business failure hoặc cộng vào TPS cho đủ.

### 17.4 Latency, timeout và retry

Một lần gửi HTTP là một attempt. Retry cùng mã là attempt mới nhưng không phải logical business mới. Mẫu latency có phản hồi gồm success/replay/PROCESSING và phản hồi lỗi theo protocol, tính UC02/UC05 riêng. Timeout không có sample latency hữu hạn giả tạo; báo riêng và tính vào E/A.

Ngoài attempt latency cần theo dõi logical end-to-end từ lần gửi đầu tới outcome cuối, kể cả retry/lookup. Dropped/unsent iterations phải báo số lượng; chúng không phải request đã gửi để tự tăng mẫu số. Không loại các logical chậm chỉ vì kết quả tới sau T1.

### 17.5 Tranh chấp phải thực sự xảy ra

Gắn nhãn “hot” ở client chưa đủ. Cần trace receive/handler/lock requested/acquired/release hoặc bằng chứng tương đương, chỉ ra nhiều request cùng account chồng xử lý và có chờ/phối hợp. Đúng thiết kế sẽ không có hai holder cùng giữ exclusive lock; waiter chờ holder khác vẫn là tranh chấp thật.

Retry cũng tính trong tỷ lệ 50%. Không chỉ chia 50% lệnh ban đầu rồi để retry làm lệch phân bố; không đổi account trong cùng requestId để điều chỉnh số liệu. Nếu tỷ lệ hoặc overlap không đạt protocol thì báo chưa chứng minh, chạy lại sau khi sửa workload.

### 17.6 Đối soát

Per-account: **dư cuối = dư đầu + tổng credit đã commit − tổng debit đã commit**, trong đó phí nội bộ phải được tính đúng một lần. Nếu có seed/refill/background jobs thì mọi mutation liên quan phải có nguồn và được đưa vào đối soát.

Per-operation: success có đúng một effect; failure không có successful effect trái phép; replay không thêm; conflict giữ bản gốc; unresolved=0. Per-cycle: có đúng fee, terms bất biến, start/end/version/coverage hợp lý. Per-report: mỗi source expense một contribution toàn cục; tổng đã bắt kịp nguồn có thể giải thích qua BASE/ADJUSTMENT.

Chỉ tổng tiền khớp chưa đủ: một request mất và một request thừa cùng amount có thể triệt tiêu. Phải tìm từng caller/operation/requestId → outcome → ledger/cycle; đối chiếu ownership và amount, không chỉ count/sum.

### 17.7 Hồ sơ test phải lưu

Revision/schema hash, config, tool/JVM/DB versions, CPU/RAM/disk/network, seed/count trước đo, số dư mở/đóng, raw attempts và traces đã che secret, histograms, error taxonomy, hot distribution, commit boundary uncertainty, reconciliation diff/unresolved, PASS/FAIL/NOT_PROVEN từng tiêu chí và lệnh tái lập.

Nguồn: [test plan](05-testing/test-plan.md), [protocol tải](05-testing/performance-test.md), [ma trận 64 ca](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md). Chưa có kết quả để nghiệm thu M7.

<a id="phase7"></a>

## 18. Phase 7 — Triển khai và bàn giao

### 18.1 Bộ bàn giao mục tiêu

Source revision và JAR/image có hash; Compose cho các service; migration/seed MySQL; realm/client/scopes demo; cấu hình mẫu không secret; OpenAPI/UML/schema khớp runtime; test/k6/reconciler; log/metrics/runbook; known limitations và backup/restore đã thử.

Không dùng một file ZIP/starter không tồn tại làm căn cứ. Image/version phải cụ thể, không dựa `latest`; máy nhà có MySQL đúng phiên bản chưa chứng minh ứng dụng tương thích.

### 18.2 Trình tự dựng từ môi trường sạch

1. Lấy đúng revision, kiểm Java/Maven/container runtime và cấu hình đích.
2. Chuẩn bị secret ngoài Git; kiểm issuer/audience/JWKS/caller mapping và URL host/container.
3. Khởi động MySQL/RabbitMQ/Redis/Keycloak, kiểm readiness và persistence.
4. Migration trên DB thử đã xác định; seed dữ liệu giả, kiểm constraints/count/source số dư.
5. Build/test, chạy backend và health/readiness bằng lệnh thật từ artifact đã kiểm.
6. Smoke UC01 rồi các UC đã triển khai; có case sai token, thiếu quyền, input sai và replay.
7. Restart và thử mất broker/cache có kiểm soát; request/tiền/số vẫn đúng.
8. Kiểm worker/job theo lịch và chạy tay; không trùng fee/contribution/cutoff.
9. Khi đủ điều kiện mới chạy tải/đối soát theo cùng protocol.
10. Backup rồi restore tới DB thử tách biệt; đối soát lại dữ liệu và tài liệu thao tác.

Đây là trình tự mục tiêu, không phải danh sách lệnh startup đã chạy. Không xóa volume/DB, clean migration hoặc restore đè để sửa lỗi nếu chưa có phạm vi/ủy quyền phù hợp. Không đưa giấy tờ thật/secret vào fixture hoặc log.

### 18.3 Quan sát và xử lý sự cố

| Dấu hiệu | Điều cần quan sát/điều tra |
|---|---|
| PROCESSING/WAITING/PENDING lâu | Deadline, lease/fence, DB outcome, resolver và backlog; không reset key/deadline cho qua |
| Worker/job trễ | Oldest due/lag, last successful run, counts/errors, checkpoint và index/lock waits |
| Commit ACK mất | Lookup request/cycle/intent bằng đường recovery, không blind retry tác động |
| MQ/cache lỗi | DB recovery/fallback và tín hiệu duplicate/lost; không cho cache thành authority |
| Report chênh | Snapshot/cutoff/source set, finalized/adjustment và nguồn chưa đóng góp; không sửa tổng thủ công |
| Tải không đạt | Offered/sent/completed, pool/DB waits/hot distribution/errors; không tắt bảo vệ nghiệp vụ để đạt TPS |

M8 cần người khác triển khai/kiểm theo tài liệu được. Backup/restore, vận hành và archive/purge sau demo còn phải có quyết định/bằng chứng thực tế. Nguồn: [deployment guide](06-deployment/deployment-guide.md), [runtime](03-architecture/runtime-profiles.md), [topology](03-architecture/docker-local-topology.md).

<a id="review"></a>

## 19. Truy vết, nghiệm thu và phần còn mở

### 19.1 Đi từ yêu cầu tới bằng chứng

| Nhóm đề bài | Yêu cầu/đặc tả chính | Thiết kế | Bằng chứng cần có |
|---|---|---|---|
| Mô hình/CSDL | BR về ownership, FR-SUB/ACC, Domain | ERD/schema MySQL | Migration, FK/UQ/CHECK, dữ liệu hợp lệ |
| Đấu nối | FR-SUB-01..15, UC01, 27 AC | Hybrid state/sequence, allocation, request, API | Stock race/retry/WAITING/FIFO/deadline/crash |
| Đăng ký/hủy | FR-PKG-01..07, UC02/03, CR-CAN | Cycle/fee/version/intent/core | Atomic fee, concurrent target, prepaid cancel/receipt |
| Cộng/trừ | FR-ACC-01..07, UC04/05, CR-TIME-002 | Account/ledger/clock/lock | No negative/overflow/fallback; per-key balance |
| Query/tổng hợp | FR-RPT-01..04, UC06, JOB-02 | Snapshot/cursor/contribution, CR-TIME-001 | Fixed membership, late commit/dedup/adjustment |
| Hết hạn/khôi phục | FR-JOB-01..04, DEC-JOB-001 | Worker/JOB-01/leases/fence/checkpoint | Restart/rerun/boundary/no duplicate fee |
| Quy mô/tải/đúng đủ | NFR-01..11, FR-TST, Q32 | Workload và reconciliation protocol | 500K, mixed 30+30, 50% hot attempts, raw logs |

Mã chi tiết còn trong SRS/UC/AC và ma trận nguồn; các nhóm trên giúp không bỏ sót một yêu cầu vì chỉ đọc API hoặc chỉ đọc DB.

### 19.2 Đã kiểm chứng ở mức tài liệu

Bộ nguồn UML hiện có 41 file, xuất 42 SVG do C4 có hai hình; OpenAPI và các local reference có checker; baseline đề bài/phiếu/27 AC UC01 được bảo toàn trong đợt đồng bộ. Số đếm/lệnh/kết quả chính thức ở [verification](verification.md) và [render manifest](checks/render-manifest-20261026.json).

Báo cáo này dùng lại ảnh hiện hành, không tạo quy tắc UML mới. Ảnh nhúng dùng PNG và đường dẫn tương đối từ file Markdown. SVG/PlantUML cạnh mỗi ảnh phục vụ phóng to/sửa nguồn. Checker kiểm cả target ảnh, PNG và hash nguồn; chỉ kiểm file SVG tồn tại chưa đủ để kết luận Markdown hiển thị được.

### 19.3 Còn mở ở mức nào?

| Hạng mục | Phần đã rõ | Phần còn cần làm |
|---|---|---|
| Nghiệp vụ khảo sát | Lựa chọn và các CR bổ sung đã tiếp nhận | Không hỏi lại phương án đã chốt; lập CR nếu muốn đổi |
| MySQL/API/transaction | Có phương án REVIEW cụ thể | Chủ dự án review UUID/index/DTO/HTTP/config và delta UC01 |
| Cadence/lô/lease/retention query | Có đề xuất trong thiết kế | Review cấu hình, EXPLAIN/profile/test; không gọi tối ưu trước đo |
| Runtime/IdP/service config | Có ranh giới và môi trường mục tiêu | Tạo artifact, cấu hình thật, kiểm version/secret/scope/health |
| Correctness/tải | Có oracle và protocol | Executable tests, raw evidence, đối soát và báo phần chưa đạt |
| Lịch sử/vận hành | Demo không purge, có retention cấu hình | Archive/purge sau demo, backup/restore và bàn giao thật |
| Tái sử dụng số | Chưa mặc định tái cấp số cũ | Quy trình cách ly/giải phóng phải được chốt trước khi mở rộng phạm vi |

Không có tuyên bố đã xét mọi ngoại lệ có thể tồn tại. Bộ hồ sơ xác định các tình huống đang xét, kết quả cần giữ và cách kiểm chứng; implementation/test có thể phát hiện thêm vấn đề và phải quay lại cập nhật nguồn liên quan.

**Tiếp theo duy nhất:** nghiệm thu delta MySQL UC01 theo [checklist M4](04-design/m4-review-checklist.md), rồi P5.1. Việc bổ sung báo cáo này không nghiệm thu M4-DB/M5–M8, không thay lịch sử phê duyệt và không yêu cầu điền lại phiếu 16.

<a id="appendix"></a>

## 20. Phụ lục — Ca biên và toàn bộ hình

### 20.1 Danh sách 64 ca biên

Đưa vào báo cáo để người đọc thấy cụ thể ngoài happy path còn phải kiểm những gì. Các dòng dưới là bản tổng hợp từ [ma trận chuẩn](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md), giữ mã, oracle và trạng thái **NOT_RUN** tại ngày 26/10/2026. Nơi cập nhật chính vẫn là ma trận; sửa nghiệp vụ phải đồng bộ cả hai nơi. 27 AC baseline UC01 và AC riêng từng UC/job không bị thay bởi danh sách này.

#### TC-EDGE-01–12: UC01 — Hồ sơ, cấp số và yêu cầu

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-01 | AUTO có số, hồ sơ mới hợp lệ | Một Subscriber, đúng một MAIN=0 không hạn, số ASSIGNED; phản hồi đồng bộ sau commit | FR-SUB-05/08/09; AC-SUB-05/07 | NOT_RUN |
| TC-EDGE-02 | Cùng giấy tờ đã xác minh, hai request mới; hồ sơ khớp | Một Customer dùng lại, Subscriber/số khác nhau hợp lệ; không customer/doc mồ côi | FR-SUB-07; AC-SUB-02/03/23 | NOT_RUN |
| TC-EDGE-03 | Customer cũ: absent optional; optional khác; null/rỗng; tên Unicode/hoa thường/dấu; số giấy tờ có0đầu | Absent không sửa; mọi trường đã gửi phải khớp sau NFC/trim/collapse/T1; null/rỗng validation error, giữ dấu/số0đầu; không tự ghép/ghi đè. | BR-12/25; AC-SUB-04/22; T1 | NOT_RUN |
| TC-EDGE-04 | Hai SELECTED cùng số với barrier; gửi thêm số090... thay vì E.164 | Tối đa1success, loser không đổi số/WAITING/account dư. Số không đúng dạng+E.164 bị validation error, không đoán quốc gia hoặc tạo hai canonical number. | FR-SUB-05/06; AC-SUB-06/08; Q33A | NOT_RUN |
| TC-EDGE-05 | AUTO kho rỗng, lần lượt consent=false/true | Không consent FAILED; consent WAITING; cả hai không Subscriber/Account | BR-16; AC-SUB-09/10 | NOT_RUN |
| TC-EDGE-06 | Ba WAITING có thứ tự, nạp hai số và AUTO mới | Hai WAITING hợp lệ đầu thành công; AUTO không vượt FIFO; không tự WAITING chỉ vì backlog | BR-24; AC-SUB-25; T4 | NOT_RUN |
| TC-EDGE-07 | Worker ở deadline−1 đơn vị/đúng deadline/+1, retry/restart | Deadline không đổi; đúng hạn trở đi EXPIRED theo mốc finalization T6, không cấp tác động mới | FR-SUB-10/14; AC-SUB-12/26/27 | NOT_RUN |
| TC-EDGE-08 | Crash sau claim, giữa tạo Customer/số/account, sau commit trước reply | Recover cùng ID; rollback không mồ côi; commit rồi không cấp lần hai | NFR-05/07; AC-SUB-13/18/19 | NOT_RUN |
| TC-EDGE-09 | Cùng ID, JSON đổi thứ tự/token/correlation; lần khác đổi evidence/consent | Những metadata được loại không conflict; đổi nội dung canonical conflict; không sửa request gốc | FR-SUB-11; AC-SUB-14/21; T2 | NOT_RUN |
| TC-EDGE-10 | POST/GET UC01 tại trước/đúng/sau replay7ngày; giữ PROCESSING/WAITING qua retention | Replay đầy đủ trong cửa sổ; ngoài cửa sổ POST409 REQUEST_ID_RETIRED/GET410; key/fingerprint/outcome summary giữ, không tái cấp hoặc dọn request đang xử lý. | BR-27; AC-SUB-24; T3 | NOT_RUN |
| TC-EDGE-11 | JWT sai signature/issuer/audience/expiry/scope; callerB GET ID củaA; caller thiếu trusted evidence scope | Fail closed/ẩn tồn tại theo API, không claim hoặc tạo dữ liệu do auth lỗi; scope thao tác/evidence riêng; demo chung không cấp quyền xem operation caller khác. | NFR-09; AC-SUB-15/16/20/23; Q31A | NOT_RUN |
| TC-EDGE-12 | MQ mất/lặp, Redis lỗi, DB restart; nạp kho đã commit | Recovery xử lý một lần/FIFO/trong hạn; Redis không quyết định cấp số; không hứa thành công trong 60s | AC-SUB-11/26; CR-ARCH-001 | NOT_RUN |

#### TC-EDGE-13–29: Gói, kỳ, hủy và gia hạn

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-13 | MAIN=100.000; phí gói=30.000 | Balance=70.000; 1 cycle/fee ledger/result đúng nguồn UC02; không thêm UC05 độc lập | FR-PKG-03/04; AC-PKG-01 | NOT_RUN |
| TC-EDGE-14 | MAIN=29.999; phí=30.000 | FAILED; balance giữ 29.999; không cycle hiệu lực mới, không fee ledger thành công | FR-ACC-04; AC-PKG-02 | NOT_RUN |
| TC-EDGE-15 | Inject exception sau UPDATE balance hoặc sau insert fee trước commit | Toàn bộ mutation tiền/gói rollback; request outcome không hứa success | NFR-06; AC-PKG-01 | NOT_RUN |
| TC-EDGE-16 | Hai POST cùng caller/UC02/ID đồng thời rồi replay | Một charge/cycle; các attempt tham chiếu cùng kết quả | FR-ACC-06; AC-PKG-09 | NOT_RUN |
| TC-EDGE-17 | Giá30.000→40.000, duration30→15; seed policy hiện hành, thử cache stale | Kỳ cũ giữ immutable price/duration/catalogVersion; kỳ mới thu đúng DB/current policy. 30ngày01/10/2026 10:15+07 hết31/10 00:00+07; không30x24h. Cache không quyết giá thu. | FR-PKG-01/02; AC-PKG-05/10; Q21 | NOT_RUN |
| TC-EDGE-18 | Root ACTIVE có kỳ đang dùng và kỳ tương lai đã trả; mua thêm với version hiện tại | Kỳ mới nối từ coverageEnd của TẤT CẢ kỳ đã trả, không cuối cycle hiện tại; một fee/cycle, rootversion tăng1, không chồng quyền. | FR-PKG-02; AC-PKG-03 | NOT_RUN |
| TC-EDGE-19 | Hai ID mới cùng subscriber/package/expectedSubscriptionVersion; sau đó đọc version mới và mua có chủ đích | Một request thắng; request stale version conflict không fee/cycle. Chỉ request mới dựa version mới được mua tiếp; không diễn giải click đôi thành hai kỳ tự động. | Q23A; AC-PKG-07 | NOT_RUN |
| TC-EDGE-20 | Manual UC02 và auto worker/JOB-01 cùng target next cycle, barrier sau đọc candidate | Một cycle/fee; loser re-evaluate/conflict/trả trạng thái hiện tại, không tự thu thêm kỳ khác. Dùng target/cycle guard chung, không chỉ riêng requestId. | Q04A; AC-PKG-08; AC-JOB-03 | NOT_RUN |
| TC-EDGE-21 | TxA intent PENDING chưa phân loại; rồi PENDING_CANCEL accepted; manual extension và worker cùng chạy | PENDING tạm ngăn extension/renewal đến phân loại; accepted tiếp tục chặn. Rejected release guard. Retry operation mua cũ trả outcome đã lưu, không tạo mutation mới. | CR-CAN-003; AC-CAN-03/08; AC-PKG-06 | NOT_RUN |
| TC-EDGE-22 | Gói hết5ngày rồi đăng ký mới; account/gói đủ điều kiện, version hiện tại | Kỳ mới bắt đầu lúc xử lý hợp lệ, end theo localDate+duration midnight; không backdate, không thu/cấp quyền cho khoảng trống. | Q05A; AC-PKG-04 | NOT_RUN |
| TC-EDGE-23 | Hủy accepted giữa kỳ có nhiều kỳ trả trước; query trước/đúng coverageEnd, JOB-01 chưa chạy | Trước coverageEnd còn quyền theo cycle bao phủ; đúng end mất quyền và sau đồng bộ CANCELLED. Không dừng tại cuối cycle đang dùng nếu còn paid future cycle; không refund. | CR-CAN-002; AC-CAN-01/02/09 | NOT_RUN |
| TC-EDGE-24 | CR-CAN-003: crash TxA→TxB, TxA commit unknown, receipt trước/đúng/sau observedCoverageEnd, worker/manual đua | Xác nhận TxA commit rồi TxB lấy DB receipt dưới slot/request. Sameversion vàreceipt<end accepted; receipt>=end khi chưa accepted thì FAILED CANCELLATION_RECEIVED_TOO_LATE/release guard. Không suy từ HTTP/precommit; PENDING guard không bị vượt. | CR-CAN-003; AC-CAN-06/07/08 | NOT_RUN |
| TC-EDGE-25 | Hủy sau khi đã trả thêm2kỳ, rồi thử mua/gia hạn mới | cancelEffectiveAt=cuối TOÀN BỘ paid coverage; giữ các kỳ/phí, không refund; sau accepted chặn mọi extension/renewal mới, chưa cắt quyền ngay. | CR-CAN-002; AC-CAN-02/03 | NOT_RUN |
| TC-EDGE-26 | TxB accepted commit nhưng mấtHTTP; retry/GET cùngID sau deadline; gửi ID hủy mới khi pending; qua retention7ngày | SameID replay accepted gốc cả sau hạn; không lấy giờ mới đổi thành TOO_LATE. ID mới CANCELLATION_PENDING; full result7ngày rồi tombstone chống làm lại, không dời effectiveAt. | Q01A; CR-CAN-003; AC-CAN-04/05/08 | NOT_RUN |
| TC-EDGE-27 | TxA commit trước hạn nhưng receipt sau hạn; receipt trước hạn/TxB commit sau; root/version đổi trước TxB | Ca1 TOO_LATE không retarget kỳ mới. Ca2 giữ accepted vì receipt sớm dù TxB commit sau. Versionđổi conflict không charge/refund. Chưa có accepted outcome thì recovery dùng receipt hiện tại, không giả early receipt. | CR-CAN-003; AC-CAN-06/07/08 | NOT_RUN |
| TC-EDGE-28 | Worker trễ20s, ngừng2giờ hoặc nhiều ngày; MAIN đủ tiền; chạy lại cùng candidate/key | Chỉ mua một kỳ từ lúc xử lý hợp lệ, không retroactive quyền/thu mọi kỳ bỏ lỡ. Worker thường xuyên +JOB-01 daily00:05 chung guard; không tự lấy60s recoveryUC01 làm cadence. | Q25B; DEC-JOB-001; AC-JOB-01/04 | NOT_RUN |
| TC-EDGE-29 | Gói ngừng bán: khách mới vs root đủ grandfather; MAIN hết hạn/thiếu tiền; nạp sau rồi retry | Khách mới bị chặn, khách cũ đủ configured policy còn gia hạn theo giá mới snapshot. MAIN invalid/thiếu tiền không fee; thiếu tiền là FAILED cuối, nạp sau không hồi sinh attempt. Lỗi DB không bị coi thiếu tiền. | Q29B; AC-PKG-11; AC-JOB-05 | NOT_RUN |

#### TC-EDGE-30–40: Tài khoản, tiền và cạnh tranh

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-30 | Amount=0, −1, 0,5; thiếu hoặc sai kiểu | Validation error, không money mutation/ledger success | BR-20; AC-CRE-04; AC-DEB-01 | NOT_RUN |
| TC-EDGE-31 | Amount10triệu/10triệu+1; balance95triệu credit10triệu; giá phí vượt cap; arithmetic overflow | Boundary hợp lệ xử lý đúng, vượt cap từ chối toàn bộ; balance tối đa100triệu, cấu hình được và kiểm tràn riêng, không wrap/truncate. Giá phí nằm miền tiền hợp lệ. | Q02A; AC-CRE-04/05 | NOT_RUN |
| TC-EDGE-32 | Account khác subscriber/không tồn tại/không quyền CREDIT hoặc DEBIT; GAME20.000/MAIN50.000 debitGAME30.000 | Đúng lớp lỗi API, không lộ dữ liệu. Không fallback/pooling MAIN/phụ; GAME thiếu thì cả hai không đổi, không suy mọi affectedRows0 là thiếu tiền. | Q09A/31A; AC-CRE-02/03; AC-DEB-05 | NOT_RUN |
| TC-EDGE-33 | Balance=100; ba debit khác ID mỗi lệnh=60 chạy chồng lấn | Tối đa 1 success, balance=40 nếu mọi request được xử lý; không ledger thành công cho hai lỗi thiếu tiền | BR-06; AC-DEB-02/03 | NOT_RUN |
| TC-EDGE-34 | Balance=100; 20 CREDIT/DEBIT nhiều giá trị chồng lấn, gồm UC02 fee | Với lịch commit thực tế: không âm, mỗi ledger có before/after liên tục, final đúng tổng | NFR-05/06; AC-DEB-04 | NOT_RUN |
| TC-EDGE-35 | CREDIT hoặc DEBIT cùngID/payload20attempts; replay trước/sau30ngày; PROCESSING qua retention | Đúng một effect/ledger. Replay30ngày giữ historical balance, không hứa current. Sau cửa sổ POST409/GET410 với tombstone; không purge PROCESSING, không thực hiện lại. | Q01A; AC-CRE-08; AC-DEB-10 | NOT_RUN |
| TC-EDGE-36 | D1 FAILED thiếu tiền, sau đó CREDIT rồi retry D1; tạo D2 mới | D1 vẫn FAILED không trừ; D2 được đánh giá mới; không làm FAILED đổi sang SUCCEEDED | FR-ACC-06; AC-CRE-10; AC-JOB-05 | NOT_RUN |
| TC-EDGE-37 | Đợi account/root/cycle lock qua expiresAt; eligibility ngay trước/đúng/sau expiry; MAINexpiryNULL; commit qua hạn | Sample giờDB mới SAU mọi lock ngay trước updates. checkedAt>=expiry từ chối; checkedAt<expiry hợp lệ được commit sauexpiry. Cycle cũng check tại cùng eligibility; MAINNULL không tự expire. | CR-TIME-002; AC-CRE-06; AC-DEB-11 | NOT_RUN |
| TC-EDGE-38 | CùngID đổi amount/account/subscriber/cycle/serviceReference, thứ tựJSON/JWT/correlation thay đổi | Business payload khác IDEMPOTENCY_CONFLICT giữ outcome/ledger cũ; metadata bị loại khỏi canonical không tạo effect mới. UC02 expectedVersion và UC03 target/version cũng tham gia canonical. | FR-ACC-06; AC-DEB-10 | NOT_RUN |
| TC-EDGE-39 | Timeout sau commit, mất kết nối khi COMMIT, crash giữa bước | GET/retry cùng ID xác định outcome; không thêm money effect; unknown còn thì unresolved | NFR-07; AC-CRE-09; AC-DEB-11 | NOT_RUN |
| TC-EDGE-40 | Deadlock victim và lock timeout riêng biệt; cạn retry budget | Rollback transaction ứng dụng, retry ngoài transaction với cùng ID; không insert ledger tiếp sau statement lỗi | NFR-05/07; AC-DEB-11 | NOT_RUN |

#### TC-EDGE-41–51: Query, snapshot và tổng hợp

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-41 | S1 chỉA, S2 chỉB, S3 A+B, S4 C, S5 A ngoài kỳ; thêm nhiều ledger cùngsubscriber | OR(A,B)=S1/S2/S3; AND=S3; mỗi subscriber một lần, tổng/count không bị nhân do join; một ledger không nguyên vẹn thuộc hai gói. | AC-RPT-03/05 | NOT_RUN |
| TC-EDGE-42 | recordedAt tạiX−1/X/Y−1/Y; ledger trướcmidnight nhưng commit saunửađêm; CREDIT/FAILED/rollback | [X,Y) gồmX khôngY theo recordedAt DB trong transaction cuối cùng commit; midnight case thuộcngày ghi nhận, không physical commit. CREDIT/FAILED/rollback không là tiêu dùng. | CR-TIME-001; AC-RPT-01/02/04 | NOT_RUN |
| TC-EDGE-43 | Chỉ phí đăng ký A; includeFee false/true | False loại, true có trong OR(A); fee không đúp qua ledger/attribution join | AC-RPT-04/05 | NOT_RUN |
| TC-EDGE-44 | X>=Y, thiếuOR/AND/includeFee, packageIds rỗng/trùng/saiUUID, page default/limit>200 | Validation theo OpenAPI, không ngầm chọn OR/includeFee; duplicate packageIds bị reject theo uniqueItems. Trang mặc định50/tối đa200. | Q10A; AC-RPT-03/04/06 | NOT_RUN |
| TC-EDGE-45 | Cursor hỏng/đổi caller/filter/query/version; snapshot hết hạn/chưaREADY | Từ chối cursor/permission hoặc trạng thái tương ứng, không lộ dữ liệu. Snapshot expired yêu cầu query mới; đang materialize dùng queryId/queryRequestId gốc, không live fallback. | Q27A/31A; AC-RPT-08/09/11 | NOT_RUN |
| TC-EDGE-46 | Transaction recordedAt trướccutoff nhưng commit saupage1, làm subscriber mới đủAND; mấtfirstreply rồi retry queryRequestId | Trang2 vẫn đúng persisted members cũ, không thêm/bỏ; query mới mới thấy. Retry cùngkey dùng cùng query/cutoff/membership, khácfilter409. Cutoff timestamp đơn thuần không chứng minh tập bất biến. | Q27A; AC-RPT-07/10; Q12A | NOT_RUN |
| TC-EDGE-47 | Dừng bán gói/xử lý hết hạn account rồi tra lịch sử | Kết quả cũ còn, định nghĩa/snapshot nguồn không bị mất | BR-07/22; AC-RPT-12 | NOT_RUN |
| TC-EDGE-48 | Debit không có gói/sai owner/cycle hết hạn hoặc tương lai; attribution nhiều gói trên một khoản; pending cancel còn quyền | Không có effective cycle thì NO_EFFECTIVE_CYCLE/validation tương ứng và không trừ. Pending cancel có cycle bao phủ vẫn dùng được. Một consumption thuộc một cycle; không UNASSIGNED/split/nhân nguyên tiền vào A+B. | Q11A; AC-DEB-06/07/08/09 | NOT_RUN |
| TC-EDGE-49 | Ngày Việt Nam đổi UTC; hai JOB-02 runner; crash sau cutoff persist/trước source snapshot, sau membership commit/trước aggregate | Đúng ngày [X,Y) theo recordedAt; first durable cutoff không thay khi retry. Persisted source set giữ nguyên; summary chỉ từ contributions persisted. Một finalization, không đọc live hai tập khác nhau. | Q12A; AC-JOB-09/10/14 | NOT_RUN |
| TC-EDGE-50 | Ledger ngày cũ đã finalized mới commit/được phát hiện; baseline và adjustment runner đua, rerun/ngày nguồn chưa chốt | Mỗi source toàn cục chỉ BASE hoặc ADJUSTMENT đúng một lần. Finalized ngày gốc không sửa; nguồn muộn vào kỳ sau, giữ source/ngày gốc. Ngày nguồn chưa chốt phải xử lý BASE đúng thứ tự, không ADJUSTMENT sớm. | Q28A; AC-JOB-11/14 | NOT_RUN |
| TC-EDGE-51 | Summary DB đã chốt nhưng CSV lỗi; retry export | Không tính tổng lại; file/version/checksum đối chiếu DB; không file nửa chừng được công bố | AC-JOB-13 | NOT_RUN |

#### TC-EDGE-52–56: Dữ liệu nền, jobs và recovery

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-52 | MySQL8.0.46/InnoDB sạch; migration đã review; duplicate doc/E.164/MAIN/request/cycle/fee/contribution, FK sai/balance âm | Migration/constraints thực thi; READ COMMITTED/write locks, utf8mb4/identifier equality/UTC DATETIME(6) đúng design. UUID physical là proposal Q14C phải review/test roundtrip PK/FK trước claim PASS; không chạy PG SQL. | Q13B/14C/15A/16A/19A; M4-DB | NOT_RUN |
| TC-EDGE-53 | JOB-01 crash sau batch/commit trước checkpoint; TxA cancellation PENDING; lịch/chạy tay song song | Checkpoint chỉ phản ánh effect đã commit; không mất/đúp fee/cancel/expiry; phục hồi PENDING theo receipt CR-CAN-003. Run states PENDING/RUNNING/SUCCEEDED/FAILED; item error counter không bị giấu. | CR-CAN-003; AC-JOB-08 | NOT_RUN |
| TC-EDGE-54 | Worker hết lease trong khi instance khác nhận việc, worker cũ quay lại | Worker cũ không ghi đè/thu phí lần hai; claim + terminal/cycle guard | NFR-05/07; AC-JOB-08 | NOT_RUN |
| TC-EDGE-55 | Ngừng máy nhiều ngày; worker/JOB-01/JOB-02 catch-up, nguồn cũ chưa finalized | Không bỏ backlog/ngày thiếu; renewal một kỳ từ valid processing, không truy thu. Report đóng ngày thiếu theo thứ tự; nguồn muộn của ngày đã chốt ADJUSTMENT ở kỳ sau đúng một lần; không sửa finalized. | Q25B/28A; AC-JOB-04/08/11 | NOT_RUN |
| TC-EDGE-56 | Account expired có balance>0; job chạy lại; số ASSIGNED gắn subscriber | Giữ tiền/history, không CREDIT/DEBIT mới trái hạn, không xóa catalog/tái cấp số | BR-07/22; AC-JOB-07 | NOT_RUN |

#### TC-EDGE-57–64: Quy mô, tải, đối soát và bảo mật

| Test ID | Dữ liệu/thao tác | Kết quả cần đối chiếu | Truy vết | Trạng thái |
|---|---|---|---|---|
| TC-EDGE-57 | Seed tổng count=499.999/500.000 trước run | Run đầu không đủ dữ liệu; run sau đủ ngưỡng nếu phân bố hợp lệ; lưu count từng bảng nghiệp vụ | NFR-01; TC perf dataset | NOT_RUN |
| TC-EDGE-58 | Mixed600s có ít nhất18.000 UC02 và18.000 UC05 mới đã commit; replay/fee/warm-up/in-flight qua biên | N2/600>=30 và N5/600>=30 cùng window, tổng>=60. Không lấy recordedAt làm commit witness. Boundary uncertainty báo riêng; chỉ count chắc chắn/nguồn thời gian đủ chứng minh, không cộng drain ngoài window. | Q32A; NFR-02/03 | NOT_RUN |
| TC-EDGE-59 | Xen replay/PROCESSING/business failure/system error/mất phản hồi; lookup sau drain | Tách attempt/logical/outcome. Replay/fee không tăng TPS; timeout tính system error dù outcome commit. A=mọi sent UC02/05 attempt kể retry; GET status/health ngoài mẫu số. Unknown không tự gán FAILED. | Q32A; NFR-07/11 | NOT_RUN |
| TC-EDGE-60 | Retry làm số attempts đổi; hot10 cặp phân đều50% mọi debit attempt; kiểm backend wait trace | H/D=50%, mỗi cặp H/10; phí UC02/renewal ngoài mẫu số. Có bằng chứng chồng xử lý/đợi cùng account ở backend, không chỉ client overlap/nhãn hot; điều phối retry không đổi payload cùng ID. | Q32A; NFR-04 | NOT_RUN |
| TC-EDGE-61 | Ledger thiếu/thừa/sai amount hoặc ID, tổng tiền vẫn tình cờ cân bằng | Đối soát theo từng key phát hiện sai, không chỉ check tổng; truy vết cycle/fee/account/source | FR-TST-02; NFR-05/07 | NOT_RUN |
| TC-EDGE-62 | Percentile từ mọi attempt có response, kể retry/error; timeout/dropped/unsent; logical end-to-end | Riêng UC02/05:95%<=500ms và99%<=1s. Timeout báo riêng nhưng vào E; E/A<=0,1% trên mọi sent attempt. Health/lookup ngoài A, dropped/unsent không giả sent; báo logical duration có retry/lookup. | Q32A; NFR-11 | NOT_RUN |
| TC-EDGE-63 | Sau run còn 1 unknown outcome hoặc 1 ledger không ánh xạ request | Không kết luận correctness/reconciliation đạt; lưu danh sách unresolved và nguyên nhân | DEC-11; NFR-07 | NOT_RUN |
| TC-EDGE-64 | Backup/restore ở nhà với MySQL/RabbitMQ/Redis/Keycloak; missing secret/expired JWT/key rotation; office không DB | Restore đúng manifest request/tombstone/ledger/cycle/report/pending intent đã backup; fail closed auth, MQ recovery DB, cache fallback DB. Admin job scope/audit riêng; không hứa RPO/RTO/M8 khi chưa đo, không bypass JWT ở office. | Q18A/30A/31A; NFR-08/09/10; M8 | NOT_RUN |

### 20.2 Design Class tổng quan đầy đủ

**Hình 42. Design Class toàn backend — cần phóng to**

![Hình 42 — Design Class toàn backend — cần phóng to](04-design/design-class-diagrams/backend-design-class.png)

[PNG](04-design/design-class-diagrams/backend-design-class.png) · [SVG để phóng to](04-design/design-class-diagrams/backend-design-class.svg) · [Nguồn PlantUML](04-design/design-class-diagrams/backend-design-class.puml)

Hình này giữ đủ liên hệ giữa các nhóm; đọc các hình tiền/gói/báo cáo/jobs ở chương 15 trước để tránh phải theo tất cả mũi tên cùng lúc.

### 20.3 Tra cứu nguồn hình

| Hình | Nội dung | Xem ảnh | Nguồn |
|---|---|---|---|
| 1 | Luồng nghiệp vụ từ đấu nối tới tiêu dùng, hủy và báo cáo | [PNG](02-analysis/activity-diagrams/00-end-to-end.png) · [SVG](02-analysis/activity-diagrams/00-end-to-end.svg) | [PlantUML](02-analysis/activity-diagrams/00-end-to-end.puml) |
| 2 | Luồng chung: input, replay, xung đột, thất bại và mất phản hồi | [PNG](02-analysis/activity-diagrams/10-common-outcomes.png) · [SVG](02-analysis/activity-diagrams/10-common-outcomes.svg) | [PlantUML](02-analysis/activity-diagrams/10-common-outcomes.puml) |
| 3 | Use Case UC01 Hybrid | [PNG](02-analysis/uc01-use-case-diagram.png) · [SVG](02-analysis/uc01-use-case-diagram.svg) | [PlantUML](02-analysis/uc01-use-case-diagram.puml) |
| 4 | Activity Diagram UC01 — có số, hết số, chờ và deadline | [PNG](02-analysis/activity-diagrams/01-activation.png) · [SVG](02-analysis/activity-diagrams/01-activation.svg) | [PlantUML](02-analysis/activity-diagrams/01-activation.puml) |
| 5 | Vòng đời yêu cầu đấu nối | [PNG](02-analysis/state-diagrams/activation-request.png) · [SVG](02-analysis/state-diagrams/activation-request.svg) | [PlantUML](02-analysis/state-diagrams/activation-request.puml) |
| 6 | Sequence đấu nối, chờ và khôi phục | [PNG](02-analysis/sequence-diagrams/uc01-activation.png) · [SVG](02-analysis/sequence-diagrams/uc01-activation.svg) | [PlantUML](02-analysis/sequence-diagrams/uc01-activation.puml) |
| 7 | Sequence đăng ký và mua nối gói | [PNG](02-analysis/sequence-diagrams/uc02-package-subscription.png) · [SVG](02-analysis/sequence-diagrams/uc02-package-subscription.svg) | [PlantUML](02-analysis/sequence-diagrams/uc02-package-subscription.puml) |
| 8 | Activity Diagram UC02 — kiểm version, điều kiện và thu phí nguyên tử | [PNG](02-analysis/activity-diagrams/02-subscription.png) · [SVG](02-analysis/activity-diagrams/02-subscription.svg) | [PlantUML](02-analysis/activity-diagrams/02-subscription.puml) |
| 9 | Vòng đời đăng ký gói | [PNG](02-analysis/state-diagrams/package-subscription.png) · [SVG](02-analysis/state-diagrams/package-subscription.svg) | [PlantUML](02-analysis/state-diagrams/package-subscription.puml) |
| 10 | Activity Diagram UC03 — TxA, receipt sau commit và kết luận hủy | [PNG](02-analysis/activity-diagrams/03-cancellation.png) · [SVG](02-analysis/activity-diagrams/03-cancellation.svg) | [PlantUML](02-analysis/activity-diagrams/03-cancellation.puml) |
| 11 | Vòng đời ý định hủy | [PNG](02-analysis/state-diagrams/cancellation-intent.png) · [SVG](02-analysis/state-diagrams/cancellation-intent.svg) | [PlantUML](02-analysis/state-diagrams/cancellation-intent.puml) |
| 12 | Sequence nhận hủy, biên hạn và mất phản hồi | [PNG](02-analysis/sequence-diagrams/uc03-package-cancellation.png) · [SVG](02-analysis/sequence-diagrams/uc03-package-cancellation.svg) | [PlantUML](02-analysis/sequence-diagrams/uc03-package-cancellation.puml) |
| 13 | Vòng đời tài khoản và hết hạn | [PNG](02-analysis/state-diagrams/account.png) · [SVG](02-analysis/state-diagrams/account.svg) | [PlantUML](02-analysis/state-diagrams/account.puml) |
| 14 | Activity Diagram UC04 — cộng tiền đúng tài khoản | [PNG](02-analysis/activity-diagrams/04-credit.png) · [SVG](02-analysis/activity-diagrams/04-credit.svg) | [PlantUML](02-analysis/activity-diagrams/04-credit.puml) |
| 15 | Sequence cộng tiền | [PNG](02-analysis/sequence-diagrams/uc04-account-credit.png) · [SVG](02-analysis/sequence-diagrams/uc04-account-credit.svg) | [PlantUML](02-analysis/sequence-diagrams/uc04-account-credit.puml) |
| 16 | Activity Diagram UC05 — kiểm account, cycle và số dư | [PNG](02-analysis/activity-diagrams/05-debit.png) · [SVG](02-analysis/activity-diagrams/05-debit.svg) | [PlantUML](02-analysis/activity-diagrams/05-debit.puml) |
| 17 | Sequence trừ tiền có kiểm quyền kỳ | [PNG](02-analysis/sequence-diagrams/uc05-account-debit.png) · [SVG](02-analysis/sequence-diagrams/uc05-account-debit.svg) | [PlantUML](02-analysis/sequence-diagrams/uc05-account-debit.puml) |
| 18 | Activity Diagram worker — phân xử target và tạo đúng một kỳ | [PNG](02-analysis/activity-diagrams/06-renewal.png) · [SVG](02-analysis/activity-diagrams/06-renewal.svg) | [PlantUML](02-analysis/activity-diagrams/06-renewal.puml) |
| 19 | Sequence worker gia hạn và guard cùng kỳ | [PNG](02-analysis/sequence-diagrams/renewal-worker.png) · [SVG](02-analysis/sequence-diagrams/renewal-worker.svg) | [PlantUML](02-analysis/sequence-diagrams/renewal-worker.puml) |
| 20 | Activity Diagram JOB-01 — account hết hạn, hủy và rà soát renewal | [PNG](02-analysis/activity-diagrams/07-expiration.png) · [SVG](02-analysis/activity-diagrams/07-expiration.svg) | [PlantUML](02-analysis/activity-diagrams/07-expiration.puml) |
| 21 | Sequence JOB-01 hết hạn, hủy và phục hồi | [PNG](02-analysis/sequence-diagrams/job01-expiration.png) · [SVG](02-analysis/sequence-diagrams/job01-expiration.svg) | [PlantUML](02-analysis/sequence-diagrams/job01-expiration.puml) |
| 22 | Activity Diagram UC06 — fixed result, OR/AND và cursor | [PNG](02-analysis/activity-diagrams/08-query.png) · [SVG](02-analysis/activity-diagrams/08-query.svg) | [PlantUML](02-analysis/activity-diagrams/08-query.puml) |
| 23 | Sequence query, dùng lại snapshot và cursor | [PNG](02-analysis/sequence-diagrams/uc06-consumption-query.png) · [SVG](02-analysis/sequence-diagrams/uc06-consumption-query.svg) | [PlantUML](02-analysis/sequence-diagrams/uc06-consumption-query.puml) |
| 24 | Activity Diagram JOB-02 — Tx0, nguồn cố định, FINALIZED và ADJUSTMENT | [PNG](02-analysis/activity-diagrams/09-daily-report.png) · [SVG](02-analysis/activity-diagrams/09-daily-report.svg) | [PlantUML](02-analysis/activity-diagrams/09-daily-report.puml) |
| 25 | Vòng đời kỳ báo cáo | [PNG](02-analysis/state-diagrams/report-period.png) · [SVG](02-analysis/state-diagrams/report-period.svg) | [PlantUML](02-analysis/state-diagrams/report-period.puml) |
| 26 | Sequence chốt kỳ, cutoff và late adjustment | [PNG](02-analysis/sequence-diagrams/job02-daily-aggregation.png) · [SVG](02-analysis/sequence-diagrams/job02-daily-aggregation.svg) | [PlantUML](02-analysis/sequence-diagrams/job02-daily-aggregation.puml) |
| 27 | Class Diagram: Customer, Subscriber, Account và Transaction | [PNG](02-analysis/class-diagrams/customer-subscriber-account.png) · [SVG](02-analysis/class-diagrams/customer-subscriber-account.svg) | [PlantUML](02-analysis/class-diagrams/customer-subscriber-account.puml) |
| 28 | Class Diagram: ServicePackage, PackageSubscription, Cycle và CancellationIntent | [PNG](02-analysis/class-diagrams/package-cycle-cancellation.png) · [SVG](02-analysis/class-diagrams/package-cycle-cancellation.svg) | [PlantUML](02-analysis/class-diagrams/package-cycle-cancellation.puml) |
| 29 | Class Diagram: Request, giao dịch nguồn, query và báo cáo ngày | [PNG](02-analysis/class-diagrams/requests-and-reporting.png) · [SVG](02-analysis/class-diagrams/requests-and-reporting.svg) | [PlantUML](02-analysis/class-diagrams/requests-and-reporting.puml) |
| 30 | Use Case toàn hệ thống | [PNG](02-analysis/use-case-diagram.png) · [SVG](02-analysis/use-case-diagram.svg) | [PlantUML](02-analysis/use-case-diagram.puml) |
| 31 | Domain Model nghiệp vụ | [PNG](02-analysis/domain-model.png) · [SVG](02-analysis/domain-model.svg) | [PlantUML](02-analysis/domain-model.puml) |
| 32 | C4 System Context | [PNG](03-architecture/c4-diagrams/context-and-container.png) · [SVG](03-architecture/c4-diagrams/context-and-container.svg) | [PlantUML](03-architecture/c4-diagrams/context-and-container.puml) |
| 33 | C4 Container và dịch vụ nền | [PNG](03-architecture/c4-diagrams/context-and-container_001.png) · [SVG](03-architecture/c4-diagrams/context-and-container_001.svg) | [PlantUML](03-architecture/c4-diagrams/context-and-container.puml) |
| 34 | Phụ thuộc giữa các module backend | [PNG](03-architecture/module-structure.png) · [SVG](03-architecture/module-structure.svg) | [PlantUML](03-architecture/module-structure.puml) |
| 35 | ERD MySQL toàn backend | [PNG](04-design/erd.png) · [SVG](04-design/erd.svg) | [PlantUML](04-design/erd.puml) |
| 36 | Vòng đời request nghiệp vụ UC02–05 | [PNG](02-analysis/state-diagrams/operation-request.png) · [SVG](02-analysis/state-diagrams/operation-request.svg) | [PlantUML](02-analysis/state-diagrams/operation-request.puml) |
| 37 | Design Class UC01: policy, claim, allocation và finalizer | [PNG](04-design/design-class-diagrams/uc01-design-class.png) · [SVG](04-design/design-class-diagrams/uc01-design-class.svg) | [PlantUML](04-design/design-class-diagrams/uc01-design-class.puml) |
| 38 | Design Class tiền: service, core và ledger | [PNG](04-design/design-class-diagrams/money-design-class.png) · [SVG](04-design/design-class-diagrams/money-design-class.svg) | [PlantUML](04-design/design-class-diagrams/money-design-class.puml) |
| 39 | Design Class đăng ký kỳ và ý định hủy | [PNG](04-design/design-class-diagrams/subscription-design-class.png) · [SVG](04-design/design-class-diagrams/subscription-design-class.svg) | [PlantUML](04-design/design-class-diagrams/subscription-design-class.puml) |
| 40 | Design Class query snapshot và daily summary | [PNG](04-design/design-class-diagrams/report-design-class.png) · [SVG](04-design/design-class-diagrams/report-design-class.svg) | [PlantUML](04-design/design-class-diagrams/report-design-class.puml) |
| 41 | Design Class điều phối worker và jobs | [PNG](04-design/design-class-diagrams/jobs-design-class.png) · [SVG](04-design/design-class-diagrams/jobs-design-class.svg) | [PlantUML](04-design/design-class-diagrams/jobs-design-class.puml) |
| 42 | Design Class toàn backend — cần phóng to | [PNG](04-design/design-class-diagrams/backend-design-class.png) · [SVG](04-design/design-class-diagrams/backend-design-class.svg) | [PlantUML](04-design/design-class-diagrams/backend-design-class.puml) |

### 20.4 Cách duy trì báo cáo

Khi thay đổi quyết định, cập nhật DEC/CR rồi SRS/UC/UML/API/schema/test tương ứng; sau đó đồng bộ chương liên quan trong báo cáo này. Khi thay nguồn PlantUML, xuất lại SVG và kiểm manifest. Khi bắt đầu có code/test/runtime thật, cập nhật trạng thái và dẫn bằng chứng có revision/ngày; không viết lại mô tả kế hoạch thành “đã đạt” khi chưa chạy.

[Hướng dẫn đọc và rà soát](HUONG-DAN-DOC-VA-RA-SOAT.md) cung cấp vòng đọc, checklist và mẫu nhận xét. [Chỉ mục chuẩn](README.md) dẫn đến tài liệu riêng đầy đủ; [ROADMAP](ROADMAP.md) quyết định tiến trình; [clarification log](00-project/clarification-log.md) quyết định hiệu lực; [verification](verification.md) ghi đúng việc đã kiểm chứng.
