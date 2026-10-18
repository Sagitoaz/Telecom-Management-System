# Đặc tả Use Case – UC01: Đấu nối thuê bao mới

**Dự án:** Telecom Subscription Management System  
**Mã tài liệu:** DOC-UC-001  
**Phiên bản:** 0.5 – Đồng bộ quyết định trước code; chờ duyệt M1  
**Ngày:** 15/10/2026; cập nhật 18/10/2026  
**Trạng thái:** 🟡 Chủ dự án đã **chốt riêng mô hình Hybrid**; toàn bộ UC01 và checkpoint M1 **chưa nghiệm thu**  
**Đường dẫn thực tế:** `docs/01-requirements/use-cases/UC01-Subscriber-Activation.md`

> **Lịch sử quyết định:** DEC-REQ-11=B từng chọn bất đồng bộ cho mọi đấu nối; CR-UC01-001 ngày 15/10/2026 **thay thế** lựa chọn đó bằng Hybrid. DEC-REQ-10=B về chờ khi hết số được **điều chỉnh**: khách hàng có quyền chọn chờ hoặc không. Quyết định nằm đầy đủ trong [clarification log](../../00-project/clarification-log.md), mục 12; không cần đọc phiếu cũ.

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

1. CSKH có Bearer JWT do hệ thống xác thực ngoài cấp; TelecomManagement kiểm tra chữ ký, issuer, thời hạn và quyền tương ứng. Đã chọn JWT bất đối xứng qua JWKS, IdP chạy local nhưng ngoài Telecom; kiểm issuer/audience/scope và ownership theo caller (DEC-PRE-04). URL/claim map cụ thể chốt Phase 3–4.
2. CSKH cung cấp `requestId`, họ tên, ngày sinh, loại giấy tờ, số giấy tờ; các trường khác tùy chọn. Nhận CCCD, CMND/CMT hoặc hộ chiếu. CSKH cung cấp tham chiếu chứng cứ xác minh từ quy trình ngoài. Ghi chú Q01 về ít nhất một trường giấy tờ không được tự hiểu là bỏ cặp loại+số đang bắt buộc.
3. Đã có kho số AVAILABLE quản lý nội bộ, hoặc có cơ chế nạp/sinh số đã được xác nhận khả dụng.
4. Chế độ **chọn số** phải có số muốn chọn; chế độ **tự cấp** không có số chỉ định.
5. Khi cho phép chờ do kho tự cấp hết số, **khách hàng đã chọn đồng ý chờ** và CSKH chuyển lựa chọn ấy qua API (ký hiệu nghiệp vụ `allowWaitForNumber=true`; tên trường thực tế để Phase 4).

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

**Đặc điểm:** Đồng bộ chỉ nghĩa là *lời gọi này trả kết quả cuối khi xử lý hoàn tất*; backend vẫn có thể phục vụ nhiều lời gọi đồng thời. Cơ chế khóa/cấp số nguyên tử, transaction và mã HTTP sẽ thiết kế ở Phase 4.

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

**A4 – Retry cùng scope requestId/canonical payload:** Nếu yêu cầu gốc đã `SUCCEEDED`/`FAILED`/`EXPIRED`, trả lại kết quả đó; nếu `WAITING`, trả trạng thái hiện tại, không cấp số hoặc tạo hồ sơ lần nữa. Quyền tra cứu theo caller sở hữu request. Trong 7 ngày sau trạng thái cuối giữ đầy đủ kết quả; đang xử lý/WAITING không bị dọn. Sau 7 ngày giữ tombstone/fingerprint/kết quả tóm tắt để chống tái dùng; không hứa replay đầy đủ payload cũ. HTTP/kết quả tra cứu tombstone sẽ chốt tại P4.

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
| `PROCESSING` | Có thể cần biểu diễn nội bộ khi job đang thực hiện; **không tự coi là kết quả thành công** và chưa chốt là trạng thái API công khai |
| `EXPIRED` | Đã quá 24h chờ, không được thực hiện lại cùng yêu cầu |

`AVAILABLE/ASSIGNED` là trạng thái số thuê bao trong kho, khác `WAITING/SUCCEEDED/FAILED/EXPIRED` của yêu cầu đấu nối. Tên trạng thái API/HTTP code cuối cùng chốt ở Phase 4.

### 7.2. Dữ liệu request/response ở mức nghiệp vụ

| Nhóm | Nội dung |
|---|---|
| Request | requestId; chế độ chọn số/tự cấp; họ tên/ngày sinh/loại + số giấy tờ; trường tùy chọn |
| Request (chọn số) | số thuê bao muốn chọn |
| Request (tự cấp) | lựa chọn đồng ý chờ khi kho hết số (ký hiệu `allowWaitForNumber`, **mặc định không chờ**, tên và schema cụ thể chốt ở Phase 4) |
| Header | Bearer JWT; correlationId phục vụ truy vết (cách cấp/gửi chốt Phase 4) |
| Response đồng bộ hoàn tất | `SUCCEEDED` hoặc `FAILED`, requestId, correlationId, mã/thông báo, dữ liệu thuê bao khi thành công |
| Response chờ | `WAITING`, requestId, correlationId, thời điểm/hạn chờ có thể tra cứu; không chứa số đã cấp |
| Tra cứu trạng thái | Theo requestId, trả trạng thái hiện tại/kết quả cuối, yêu cầu JWT/quyền thích hợp; CSKH lưu mã và xử lý tự động |

**Hướng kỹ thuật đã chọn:** scope caller+UC01+ID, canonical payload, replay 7 ngày+tombstone, FIFO, recovery 60s, DB transaction ngắn+row lock+unique. POST /api/v1/activations, GET /api/v1/activations/{requestId}, GET /api/v1/phone-numbers; success mới 201, WAITING 202, conflict 409. **Còn P4:** normalize/absent/null/default, OpenAPI đầy đủ, HTTP replay/in-progress/tombstone, lock order/retry/tie-break FIFO/timeout. Không đưa thuật toán SQL vào mô hình phân tích.

## 8. Acceptance Criteria (mức nghiệp vụ)

| Mã | Điều kiện nghiệm thu | FR/BR liên quan |
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


## 9. Test Scenario (đề xuất cho kiểm thử P5)

| ID | Bối cảnh | Kết quả dự kiến |
|---|---|---|
| TC-UC01-01 | Tự cấp có số AVAILABLE | Đồng bộ SUCCEEDED; số, Subscriber, Account chính hợp lệ |
| TC-UC01-02 | Khách chọn số AVAILABLE | Đồng bộ SUCCEEDED |
| TC-UC01-03 | Hai request cùng chọn một số đồng thời | Tối đa một SUCCEEDED, request kia FAILED |
| TC-UC01-04 | Tự cấp hết số, khách **không** chọn chờ | Đồng bộ FAILED (`NO_AVAILABLE_NUMBER`), không WAITING |
| TC-UC01-05 | Tự cấp hết số, khách **chọn** chờ | WAITING, có requestId; không báo thành công |
| TC-UC01-06 | Bổ sung số trước hạn, chạy trigger/recovery | Từ WAITING sang SUCCEEDED chính xác một lần |
| TC-UC01-07 | Quá 24 giờ khi đang WAITING | EXPIRED, không được cấp số sau hạn |
| TC-UC01-08 | Cùng requestId + payload retry | Cùng trạng thái/kết quả, không tác động lần 2 |
| TC-UC01-09 | Cùng requestId khác payload | `IDEMPOTENCY_CONFLICT` |
| TC-UC01-10 | CSKH timeout sau lời gọi có thể đã commit | Tra cứu bằng requestId cho kết quả đúng, không cấp trùng |
| TC-UC01-11 | Một Customer đã liên kết hai giấy tờ hợp lệ | Reuse Customer khi giấy tờ xác minh; không tự ghép khi chưa xác minh |
| TC-UC01-12 | Token lỗi/sai issuer/hết hạn/thiếu quyền | Từ chối, không có tác động nghiệp vụ |

Các ca bổ sung cần triển khai theo AC-SUB-20–27: scope caller/ownership, canonical fields, hồ sơ tùy chọn không khớp, chứng cứ ngoài, replay/tombstone, FIFO/recovery/expiry race. Tất cả **chưa chạy**.

## 10. Phần còn mở và phạm vi nghiệm thu

| Mốc | Nội dung |
|---|---|
| M1 | Chủ dự án rà soát loại+số giấy tờ vẫn bắt buộc, so mọi trường đã gửi, chứng cứ xác minh ngoài và 27 AC. Chưa chọn Q25. |
| P3–4 UC01 | Chuẩn hóa từng trường, absent/null/default; issuer/JWKS/claim map; chuẩn số/nạp kho; tombstone response; in-progress/replay/lỗi API; lock order/retry/claim/FIFO tie-break; đồng hồ/deadline. |
| Ngoài UC01 đầu | Quy trình thêm giấy tờ khách cũ/giải phóng/tái cấp số; retention và hạn mức lệnh tiền trước UC liên quan. |

Canonical payload giữ hồ sơ/mode/số chọn/consent; JWT/correlationId/thứ tự JSON không tham gia. Q01 đã chọn hướng “sau chuẩn hóa” nhưng chưa chọn thuật toán chuẩn hóa cụ thể; không tự áp chính sách bỏ dấu/hoa thường từ phương án chưa chọn. Các chi tiết kỹ thuật được hoãn đến P4, không phải bỏ qua.
M1 chỉ đóng sau khi chủ dự án đọc SRS 0.6/UC01 0.5 và trả lời Q25 rõ ràng.

## 11. Phê duyệt và lịch sử

| Vai trò | Trạng thái |
|---|---|
| Người soạn | Bản v0.5 đề nghị rà soát |
| Chủ dự án | **Đã chốt Hybrid**, chưa nghiệm thu trọn UC01 và M1 |

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 0.1 | 15/10/2026 | Bản khung theo clarification log mục 12.2. |
| 0.2 | 15/10/2026 | Ghi nhận 5 lựa chọn clarification log mục 12.3. |
| 0.3 | 15/10/2026 | CR-UC01-001: thay bất đồng bộ toàn phần bằng **Hybrid**; đồng bộ nếu xử lý xong ngay, chờ chỉ khi tự cấp hết số và khách đồng ý, chờ tối đa 24 giờ; thêm AC/test tương ứng. |


## Cập nhật quản lý tài liệu ngày 18/10/2026

Phiên bản 0.4: bỏ phụ thuộc phiếu review theo CR-DOC-001. Nguồn quyết định đầy đủ là clarification log mục 12; không thay đổi nghiệp vụ, chưa nghiệm thu M1.


| 0.5 | 18/10/2026 | Đồng bộ DEC-PRE-01–11 và CR-ARCH-001; giữ AC-SUB-01–19, bổ sung AC-SUB-20–27; M1 chờ chủ dự án duyệt. |
