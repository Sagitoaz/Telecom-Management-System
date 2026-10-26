# UC01 — Đấu nối thuê bao

**DOC-UC-001 · v0.6 · 26/10/2026 · REVIEW delta.** Baseline UC01v0.5 và 27AC được duyệt19/10; giữ nguyên các AC bên dưới. CR-UC01-001 Hybrid vẫn hiệu lực. MySQL8.0.46/InnoDB, E.164 và caller/evidence quyền theo phiếu16 đã chọn; artifact mới chưa tự nghiệm thu.

## 1. Thông tin chung

| Thuộc tính | Nội dung |
|---|---|
| Use Case ID | UC01 |
| Tên | Đấu nối thuê bao mới (Subscriber Activation) |
| Mục tiêu | Tạo Subscriber với số duy nhất, gắn Customer hợp lệ và tài khoản chính; cho kết quả ngay khi có thể |
| Tác nhân chính | Hệ thống CSKH gọi REST API; khách hàng đưa ra lựa chọn chờ khi thiếu số |
| Trigger | CSKH gửi hồ sơ, phương thức cấp số, `requestId` và tùy chọn chờ đối với cấp tự động |
| Kiểu xử lý | **Hybrid: đồng bộ là luồng chính; bất đồng bộ chỉ khi cấp số tự động hết kho và đã chọn chờ** |
| Nguồn | DEC-01/02; clarification log mục 12.2; clarification log mục 12.3; **CR-UC01-001** |
| Truy vết | REQ-01/02; FR-SUB-01..15; BR-01..03/11..17/24..27; AC-SUB-01..27 |

**Giới hạn nghiệp vụ:** `SUCCEEDED` nghĩa là hoàn tất đấu nối trong backend/cơ sở dữ liệu, **không phải** đã kích hoạt SIM trên mạng thật (EX-03).

## 2. Tiền điều kiện

1. CSKH có Bearer JWT do hệ thống xác thực ngoài cấp; TelecomManagement kiểm tra chữ ký, issuer, thời hạn và quyền tương ứng. Đã chọn JWT bất đối xứng qua JWKS, IdP chạy local nhưng ngoài Telecom; kiểm issuer/audience/scope và ownership theo caller (DEC-PRE-04). Claim/issuer/scope cụ thể là cấu hình phải kiểm chứng trước API thật; Q31=A dùng dữ liệu demo chung, evidence scope riêng.
2. CSKH cung cấp `requestId`, họ tên, ngày sinh, loại giấy tờ, số giấy tờ; các trường khác tùy chọn. Nhận CCCD, CMND/CMT hoặc hộ chiếu. CSKH cung cấp tham chiếu chứng cứ xác minh từ quy trình ngoài. Ghi chú Q01 về ít nhất một trường giấy tờ không được tự hiểu là bỏ cặp loại+số đang bắt buộc.
3. Đã có kho số AVAILABLE quản lý nội bộ, hoặc có cơ chế nạp/sinh số đã được xác nhận khả dụng.
4. Chế độ **chọn số** phải có số muốn chọn; chế độ **tự cấp** không có số chỉ định.
5. Khi cho phép chờ do kho tự cấp hết số, **khách hàng đã chọn đồng ý chờ** và CSKH chuyển lựa chọn ấy qua API (ký hiệu nghiệp vụ `allowWaitForNumber=true`; schema hiện hành xem OpenAPI REVIEW).

## 3. Hậu điều kiện

| Kết quả | Hậu điều kiện |
|---|---|
| `SUCCEEDED` | Trong một nghiệp vụ hoàn tất nhất quán: một số chuyển ASSIGNED và gắn một Subscriber mới với Customer phù hợp; tạo một Account tiền chính số dư 0 VND, không đặt hạn mặc định; có thể tra cứu lại theo `requestId` |
| `FAILED` | Hồ sơ/token không hợp lệ, số chọn đã cấp, hoặc cấp tự động hết số mà **không chọn chờ**: trả lỗi trong cùng lời gọi; không tạo Subscriber/Account hoặc số ASSIGNED mồ côi |
| `WAITING` | Chỉ khi **tự cấp hết số và đã đồng ý chờ**; ghi nhận yêu cầu chờ, chưa tạo Subscriber/Account thành công, không báo đấu nối thành công; CSKH tự theo dõi qua requestId |
| `EXPIRED` | WAITING quá 24 giờ mà chưa hoàn tất hợp lệ: chuyển kết quả cuối, không được cấp số từ yêu cầu đã hết hạn; muốn thử lại cần requestId mới |
| Retry | Cùng requestId + cùng payload trả trạng thái/kết quả hiện có mà không tác động lại; cùng requestId + payload khác trả `IDEMPOTENCY_CONFLICT` |

Quy tắc tái cấp số từng dùng sau thời gian cách ly thuộc vòng đời thuê bao sau, **không** mặc định số ngừng sử dụng được cấp lại ngay.

## 4. Basic Flow – hoàn tất đồng bộ

| Bước | Xử lý |
|---|---|
| 1 | Khách hàng đăng ký qua CSKH; CSKH gửi yêu cầu đấu nối mới có requestId, hồ sơ và chế độ chọn số hoặc tự cấp. |
| 2 | Backend kiểm tra JWT và quyền, kiểm tra dữ liệu đầu vào; không hợp lệ thì trả lỗi ngay. |
| 3 | Backend kiểm tra scope caller ứng dụng + UC01 + requestId và canonical payload; nếu đã có: cùng nội dung chuyển A4, khác nội dung chuyển E5. |
| 4 | Backend đối chiếu giấy tờ/chứng cứ xác minh ngoài; dùng Customer đã liên kết hoặc tạo Customer mới hợp lệ. So mọi trường được gửi sau chuẩn hóa: khác bất kỳ trường nào chuyển E2, không tự cập nhật; không tự ghép chỉ bằng tên/ngày sinh. Thêm giấy tờ vào Customer cũ ngoài UC01. |
| 5 | Với chọn số: kiểm tra lại số AVAILABLE tại thời điểm cấp. Với tự cấp: chọn ngẫu nhiên một số AVAILABLE; nếu hết số chuyển A2. |
| 6 | Backend cấp số không trùng, lưu Subscriber và tài khoản tiền chính số dư 0 trong một nghiệp vụ có tính nhất quán; chỉ công bố thành công sau khi dữ liệu đã commit nhất quán. |
| 7 | API trả ngay kết quả cuối `SUCCEEDED`, requestId, correlationId, số thuê bao/thông tin kết quả. CSKH hiển thị thành công **không cần polling**. |

**Đặc điểm:** Đồng bộ chỉ nghĩa là *lời gọi này trả kết quả cuối khi xử lý hoàn tất*; backend vẫn có thể phục vụ nhiều lời gọi đồng thời. Cơ chế khóa/cấp số nguyên tử và transaction xem thiết kế REVIEW delta MySQL; HTTP T3 có phê duyệt lịch sử.

## 5. Alternative Flows

**A1 – Khách hàng chọn số:** CSKH truyền số đã chọn; backend kiểm tra khả dụng lần cuối. Nếu số đã được cấp hoặc có tranh chấp thì trả lỗi để chọn lại, không tự chuyển sang số khác và không đưa vào WAITING.

**A2 – Hết kho khi cấp số tự động (Hybrid):**

1. Backend xác nhận không còn số AVAILABLE hợp lệ.
2. **Không có lựa chọn chờ rõ ràng:** trả `FAILED` với mã `NO_AVAILABLE_NUMBER` ngay trong request; không lưu yêu cầu WAITING. Kết quả FAILED này vẫn gắn với requestId để retry cùng mã không âm thầm thử lại sau khi kho số thay đổi; muốn yêu cầu mới thì dùng requestId mới.
3. **Có lựa chọn chờ rõ ràng:** tiếp nhận yêu cầu `WAITING` và trả ngay thông báo `WAITING` kèm requestId/correlationId; **không ghi SUCCEEDED**.
4. Hạn chờ tối đa **24 giờ tính từ lúc yêu cầu chuyển WAITING**. Khi nạp/sinh bổ sung số, backend tự thử xử lý lại; WAITING theo FIFO và ưu tiên trước tự cấp mới khi có số bổ sung. Recovery mỗi 60 giây kiểm tra bỏ sót; RabbitMQ hỗ trợ tín hiệu sau commit, DB vẫn là nguồn quyết định. Kiểm tra hạn/điều kiện còn hợp lệ, và kiểm lại hạn khi hoàn tất. Retry/restart không đặt lại mốc 24 giờ.
5. Khi xử lý lại thành công: cấp một số duy nhất, hoàn tất Subscriber/Account và chuyển `SUCCEEDED`. Khi quá hạn chưa thành công: `EXPIRED`; không tác động sau khi hết hạn.
6. Phần mềm CSKH **tự lưu requestId và tự gọi API tra cứu** (polling, hoặc cơ chế thông báo khác nếu sau này thiết kế) để hiển thị kết quả. Nhân viên không bắt buộc nhập requestId thủ công.

**A3 – Customer đã có:** Chỉ sử dụng lại Customer khi giấy tờ đã được xác minh/liên kết và hồ sơ phù hợp; nếu giấy tờ chưa liên kết thì không tự hợp nhất hai hồ sơ theo tên/ngày sinh.

**A4 – Retry cùng scope requestId/canonical payload:** Nếu yêu cầu gốc đã `SUCCEEDED`/`FAILED`/`EXPIRED`, trả lại kết quả đó; nếu `WAITING`, trả trạng thái hiện tại, không cấp số hoặc tạo hồ sơ lần nữa. Quyền tra cứu theo caller sở hữu request. Trong 7 ngày sau trạng thái cuối giữ đầy đủ kết quả; đang xử lý/WAITING không bị dọn. Sau 7 ngày giữ tombstone/fingerprint/kết quả tóm tắt để chống tái dùng; không hứa replay đầy đủ payload cũ. T1–T7 đã duyệt lịch sử: replay POST thành công200; GET tombstone410, POST mã cũ409; PROCESSING/WAITING202. Delta MySQL/DTO mới REVIEW.

**A5 – HTTP timeout:** Sau khi mất kết nối, kết quả có thể đã commit hoặc vẫn đang xử lý; CSKH tra cứu theo requestId hoặc retry cùng requestId/payload, **không** tự hiểu là đã rollback.

**A6 – Tái sử dụng số từng cấp:** Chỉ có thể bổ sung số trở lại AVAILABLE sau khi đáp ứng quy trình giải phóng và thời gian cách ly còn Open tại OQ-05; UC01 phiên bản đầu **chưa triển khai** logic giải phóng/tái cấp số.

## 6. Exception Flows

| Mã | Điều kiện | Kết quả |
|---|---|---|
| E1 | Thiếu/sai thông tin bắt buộc | Trả lỗi phù hợp; không tạo thuê bao |
| E2 | Customer đã liên kết giấy tờ nhưng dữ liệu mâu thuẫn | Từ chối, yêu cầu xác minh; không tự ghi đè |
| E3 | Số chọn không AVAILABLE/cấp trùng | Trả lỗi ngay; không tự thay số/không WAITING |
| E4 | JWT thiếu/sai chữ ký/hết hạn/sai issuer/thiếu quyền | Từ chối; không tác động dữ liệu |
| E5 | Trùng requestId nhưng payload khác | `IDEMPOTENCY_CONFLICT`; giữ nguyên yêu cầu gốc |
| E6 | Lỗi hệ thống/timeout trong khi xử lý | Không công bố SUCCEEDED trước khi commit; tra cứu/retry để xác định kết quả |
| E7 | Yêu cầu WAITING đã qua 24h | `EXPIRED`, không cấp số từ yêu cầu cũ; yêu cầu mới cần requestId mới |
| E8 | Kho số rỗng, khách hàng không chọn chờ | `NO_AVAILABLE_NUMBER` và `FAILED` ngay; không ghi WAITING |

## 7. Trạng thái yêu cầu và phản hồi

### 7.1. Vòng đời yêu cầu (không đồng nhất trạng thái số)

| Trạng thái | Diễn giải |
|---|---|
| `SUCCEEDED` | Đấu nối đã thành công và dữ liệu nhất quán |
| `FAILED` | Có kết quả lỗi cuối cùng; ví dụ hồ sơ sai/số không khả dụng/hết số không chờ |
| `WAITING` | Chỉ nhánh cấp tự động hết kho **có xác nhận đồng ý chờ**; không phải đấu nối thành công |
| `PROCESSING` | Có thể cần biểu diễn nội bộ khi job đang thực hiện; **không tự coi là kết quả thành công**; API phản hồi202 theo T3 đã duyệt lịch sử |
| `EXPIRED` | Đã quá 24h chờ, không được thực hiện lại cùng yêu cầu |

`AVAILABLE/ASSIGNED` là trạng thái số thuê bao trong kho, khác `WAITING/SUCCEEDED/FAILED/EXPIRED` của yêu cầu đấu nối. HTTP T3 được duyệt lịch sử; schema cụ thể ở OpenAPI REVIEW.

### 7.2. Dữ liệu request/response ở mức nghiệp vụ

| Nhóm | Nội dung |
|---|---|
| Request | requestId; chế độ chọn số/tự cấp; họ tên/ngày sinh/loại + số giấy tờ; trường tùy chọn |
| Request (chọn số) | số thuê bao muốn chọn |
| Request (tự cấp) | lựa chọn đồng ý chờ khi kho hết số (ký hiệu `allowWaitForNumber`, **mặc định không chờ**, schema hiện hành xem OpenAPI REVIEW) |
| Header | Bearer JWT; correlationId phục vụ truy vết (theo OpenAPI REVIEW) |
| Response đồng bộ hoàn tất | `SUCCEEDED` hoặc `FAILED`, requestId, correlationId, mã/thông báo, dữ liệu thuê bao khi thành công |
| Response chờ | `WAITING`, requestId, correlationId, thời điểm/hạn chờ có thể tra cứu; không chứa số đã cấp |
| Tra cứu trạng thái | Theo requestId, trả trạng thái hiện tại/kết quả cuối, yêu cầu JWT/quyền thích hợp; CSKH lưu mã và xử lý tự động |

**Hướng kỹ thuật đã chọn:** scope caller+UC01+ID, canonical payload, replay 7 ngày+tombstone, FIFO, recovery 60s, DB transaction ngắn+row lock+unique. POST /api/v1/activations, GET /api/v1/activations/{requestId}, GET /api/v1/phone-numbers; success mới 201, WAITING 202, conflict 409. **T1–T7 lịch sử:** NFC/trim/collapse whitespace, tên không phân biệt hoa thường nhưng giữ dấu, giấy tờ giữ0đầu; optional null/rỗng lỗi; evidence reference trong fingerprint. Tie-break/clock/claim xem thiết kế; delta MySQL và kiểm chứng còn REVIEW. Không đưa thuật toán SQL vào mô hình phân tích.

## 8. Tiêu chí nghiệm thu giữ mã baseline

| Mã | Điều kiện nghiệm thu | Truy vết |
|---|---|---|
| AC-SUB-01 | Đầu vào thiếu trường bắt buộc bị từ chối; không tạo Subscriber. | FR-SUB-01 |
| AC-SUB-02 | Chấp nhận CCCD/CMND/hộ chiếu và Customer có nhiều giấy tờ đã xác minh/liên kết; không tự ghép hồ sơ; liên kết thêm giấy tờ cho Customer cũ là quy trình ngoài UC01. | FR-SUB-01/07; BR-25 |
| AC-SUB-03 | Customer cũ phù hợp được dùng lại, không tạo Customer thứ hai. | FR-SUB-07 |
| AC-SUB-04 | Thông tin mâu thuẫn với hồ sơ đã liên kết bị từ chối, không tự cập nhật. | FR-SUB-07 |
| AC-SUB-05 | Thành công có đúng một số ASSIGNED, một Subscriber gắn Customer phù hợp và một Account chính 0 VND. | FR-SUB-05/08 |
| AC-SUB-06 | Hai yêu cầu chọn cùng số chạy đồng thời: tối đa một thành công, không cấp trùng số. | FR-SUB-05/06 |
| AC-SUB-07 | Tự cấp có số AVAILABLE: hoàn tất và **trả SUCCEEDED ngay trong request**; không bắt buộc polling. | FR-SUB-04/09; BR-17 |
| AC-SUB-08 | Chọn số còn AVAILABLE và đủ điều kiện: trả kết quả cuối trong request; số đã cấp trả lỗi ngay. | FR-SUB-03/06/09 |
| AC-SUB-09 | Tự cấp hết số và **không có lựa chọn đồng ý chờ**: trả `NO_AVAILABLE_NUMBER` ngay, không WAITING hoặc tạo thuê bao. | FR-SUB-10; BR-16 |
| AC-SUB-10 | Tự cấp hết số và **có đồng ý chờ**: trả WAITING cùng requestId, không báo SUCCEEDED; hạn tối đa 24 giờ. | FR-SUB-09/10; BR-16 |
| AC-SUB-11 | Bổ sung số trước hạn có thể xử lý lại WAITING và tạo đúng một thuê bao; job khôi phục không tạo trùng. | FR-SUB-14; BR-24 |
| AC-SUB-12 | Quá hạn WAITING thì EXPIRED; không cấp số cho yêu cầu cũ; CSKH được gửi yêu cầu mới với requestId mới. | FR-SUB-10; BR-16 |
| AC-SUB-13 | Cùng requestId/payload dù retry trong bất cứ trạng thái nào cũng không có tác động thứ hai. | FR-SUB-11; BR-17 |
| AC-SUB-14 | requestId cũ nhưng payload khác trả `IDEMPOTENCY_CONFLICT` và giữ nguyên yêu cầu gốc. | FR-SUB-11; BR-27 |
| AC-SUB-15 | JWT không hợp lệ/hết hạn/sai issuer/thiếu quyền bị từ chối; backend không phát token. | FR-SUB-15; BR-26 |
| AC-SUB-16 | Trạng thái/kết quả có requestId, correlationId, mã/thông điệp phù hợp và không lộ thông tin giấy tờ không cần thiết. | FR-SUB-12; NFR-09 |
| AC-SUB-17 | CSKH có thể tự tra cứu yêu cầu WAITING bằng requestId mà nhân viên không nhập thủ công; kết quả cuối hiển thị đúng. | FR-SUB-09/11 |
| AC-SUB-18 | Timeout được giải quyết bằng tra cứu/retry cùng requestId, không được tự kết luận rollback hoặc tạo tác động trùng. | FR-SUB-11; NFR-05/07 |
| AC-SUB-19 | Trạng thái cuối/commit nhất quán: không có SUCCEEDED khi chưa tạo đủ số + Subscriber + Account hoặc khi đã EXPIRED. | FR-SUB-05/08/10; NFR-05/07 |
| AC-SUB-20 | Hai caller dùng cùng requestId là hai scope độc lập; caller khác không được xem kết quả không thuộc mình. | FR-SUB-11/15; DEC-PRE-04/05 |
| AC-SUB-21 | Retry chỉ khác thứ tự JSON/JWT/correlationId không tạo tác động mới; thay số chọn/consent/hồ sơ theo canonical policy gây conflict. | FR-SUB-11; DEC-PRE-07 |
| AC-SUB-22 | Mọi trường hồ sơ được gửi phải khớp sau chuẩn hóa; thay trường tùy chọn đã gửi cũng bị từ chối, không tự ghi đè. | FR-SUB-07; DEC-PRE-01 |
| AC-SUB-23 | Customer mới có tham chiếu chứng cứ ngoài hợp lệ; không tự liên kết thêm giấy tờ khách cũ trong UC01. | FR-SUB-01/07; DEC-PRE-02 |
| AC-SUB-24 | Replay kết quả đầy đủ trong 7 ngày sau trạng thái cuối; không dọn đang xử lý/WAITING; tombstone chống tái tác động sau cửa sổ replay. | FR-SUB-11; DEC-PRE-06 |
| AC-SUB-25 | Khi bổ sung kho, xử lý WAITING hợp lệ theo FIFO ưu tiên tự cấp mới; tie-break phải được đặc tả và test ở P4. | FR-SUB-14; DEC-PRE-08 |
| AC-SUB-26 | Recovery theo chu kỳ 60s khôi phục request bỏ sót; deadline không reset sau restart/retry; không cam kết mọi request hoàn tất trong 60s. | FR-SUB-14; DEC-PRE-10 |
| AC-SUB-27 | Không hoàn tất cấp số khi đã tới hạn WAITING; đua expiry/worker/nạp kho giữ một kết quả duy nhất. | FR-SUB-05/14; DEC-PRE-09/10 |

## 9. Bổ sung input/output và chính sách hiện hành

Chỉ nhận E.164 dạng dấu+ và tối đa15 chữ số, không tự đoán090 thành+84. Danh sách AVAILABLE không reservation. Lookup GET /api/v1/activations/{requestId} luôn caller-owned; POST replay trong7ngày không cấp thêm. Sau cửa sổ GET410/tombstone và POST409; khác fingerprint luôn conflict, không trở thành yêu cầu mới. CSKH tự lưu mã/polling, không bắt người nhập.

GET /api/v1/phone-numbers là read hỗ trợ, không cấp số. POST /api/v1/activations input hồ sơ/mode/selectedPhoneNumber tùy mode/allowWaitForNumber/evidence reference/requestId; output status/requestId/correlationId, deadline nếu WAITING, subscriberId/phoneNumber nếu SUCCEEDED; lỗi không lộ giấy tờ. canonical loại JWT/correlationId/thứ tự JSON; absent optional khác null/rỗng theo T1. MAIN0VND không hạn mặc định; không tạo phụ tại UC01.

Quyền activate và evidence trusted phải đủ trước tạo/liên kết Customer. Không có issuer/JWKS hợp lệ thì từ chối, không bật bypass. Kiểm so Customer concurrent theo giấy tờ và không cập nhật hồ sơ cũ. Nạp kho được xác nhận hợp lệ từ quy trình nội bộ; vòng đời tái cấp/chờ cách ly chưa triển khai.

## 10. Kiểm chứng và ranh giới

Dự kiến test AC-SUB-01..27 trên đúng MySQL: chọn số race, random allocation, FIFO cùng waitingSince tie-break, expiry đúng deadline, recovery restart/redelivery/claim cũ, transaction rollback, timeout commit unknown, caller isolation, evidence denied, normalization và7ngày/tombstone. UC01 recovery60s chỉ là chu kỳ khôi phục, không SLA hoàn tất và không là worker gia hạn gói. Không gọi broker/IdP khi giữ transaction lâu; MQ trùng/mất không là nguồn quyết định.

[SRS](../SRS.md), [sequence](../../02-analysis/sequence-diagrams/uc01-activation.puml), [OpenAPI](../../04-design/openapi.yaml), [design](../../04-design/concurrency-and-jobs.md) là tài liệu liên quan. Các test trên là kế hoạch chưa chạy; khóa/isolation/claim/retry cụ thể thuộc design REVIEW. AC27 yêu cầu kiểm deadline lúc hoàn tất nghiệp vụ theo T5/T6; không tự đổi nó thành physical commit deadline của lệnh tiền.
