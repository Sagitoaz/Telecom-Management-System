# Phân tích nghiệp vụ và thuyết minh UML

**0.2 — DRAFT, 18/10/2026. P2.1/P2.2/P2.4/P2.6.**
Nguồn: SRS 0.6, UC01 0.5, DEC-01–11, DEC-REQ-01–31, DEC-UC01-01–05, CR-UC01-001.
M1 chưa nghiệm thu; các sơ đồ tạo mới theo CR-DOC-001, không phải sơ đồ cũ được sửa.

| Sản phẩm | Phạm vi | Giới hạn |
|---|---|---|
| [Use Case toàn hệ thống](use-case-diagram.puml) | UC01–06, tra cứu số/trạng thái; hai job nội bộ | Không biến scheduler thành actor |
| [Use Case UC01](uc01-use-case-diagram.puml) | Chọn số/tự cấp là biến thể; chờ có điều kiện | Include là bước luôn thực hiện, extend chỉ nhánh có điều kiện |
| [Domain Model](domain-model.puml) | Khái niệm nghiệp vụ, thuộc tính, multiplicity | Không PK/FK/JPA/SQL |
| [Sequence UC01](sequence-diagrams/uc01-activation.puml) | Hybrid, retry, WAITING, recovery, timeout, tranh chấp | Không đưa chi tiết khóa/HTTP/database transaction vào phân tích |
| [State đăng ký gói](state-diagrams/package-subscription.puml) | Có hiệu lực, chờ hủy, hết hạn | Tên trạng thái phân tích; hướng chu kỳ/gia hạn đã chọn; ca biên kỹ thuật còn Open |

## Ràng buộc mô hình

Customer có 0..N thuê bao, mỗi Subscriber đúng 1 Customer. Cụm “1–N” là quyền sở hữu, không bắt buộc Customer mới phải có thuê bao trước khi đấu nối thành công.
Mỗi Account thuộc 1 Subscriber, mỗi Transaction thuộc 1 Account; PackageSubscription thuộc 1 Subscriber và 1 ServicePackage.
Kho số chứa nhiều PhoneNumber; một số gắn tối đa 1 thuê bao hiện tại. Quan hệ không mô hình hóa lịch sử tái cấp chưa chốt.
ActivationRequest có thể chưa gắn Customer/Subscriber khi chờ hoặc thất bại; thành công có đúng 1 Subscriber và Account chính.
VerifiedIdentityDocument chỉ biểu diễn giấy tờ đã xác minh/liên kết. Không mô hình hóa hệ thống KYC hay hợp nhất hồ sơ tự động.

Trạng thái số AVAILABLE/ASSIGNED khác trạng thái yêu cầu WAITING/SUCCEEDED/FAILED/EXPIRED. WAITING chỉ có ở tự cấp hết số có consent, hạn tối đa 24h. PROCESSING chưa được quyết định là trạng thái API.

## Truy vết và rà soát

| Quy tắc | Vị trí UML | Kiểm chứng dự kiến |
|---|---|---|
| Không cấp trùng, tra cứu không giữ số | Use Case, PhoneNumber, sequence cấp số | AC-SUB-06/08/19 |
| Customer nhiều giấy tờ, không tự ghép | Domain, bước đối chiếu sequence | AC-SUB-02/03/04 |
| Hybrid, WAITING có consent, expiry | UC01 extend, sequence | AC-SUB-07–12 |
| Idempotency, conflict, timeout | ActivationRequest, sequence | AC-SUB-13/14/18 |
| Đăng ký có thu phí, hủy cuối kỳ | Domain, state | AC-PKG-01/02, AC-CAN-01/02 |

## Còn mở và điều kiện M2

OQ-12 đã chọn so toàn bộ trường gửi và chứng cứ ngoài; rà soát cách hiểu R1 ở M1; OQ-02/05/15 kỹ thuật Phase 3–4; OQ-07/09 trước UC02/03. Không bổ sung thuật toán DB ở mô hình phân tích.
M2 chỉ đủ khi M1 được duyệt, chủ dự án rà soát domain/UC01, các nhánh nhất quán và mọi khoảng trống có OQ/mốc xử lý. Có DRAFT không đồng nghĩa M2 đạt.
P2.5 sequence chi tiết UC02/05/job còn hoãn đến vòng UC tương ứng.


**Quy tắc phân tích mới:** canonical business content/caller scope, replay7 ngày+tombstone; WAITING FIFO ưu tiên tự cấp mới khi bổ sung kho, recovery 60s giữ deadline gốc. Domain bổ sung source evidence/caller và price snapshot/ngữ cảnh tiêu dùng; row lock và RabbitMQ/Redis thuộc architecture/design, không làm thành lớp nghiệp vụ.
