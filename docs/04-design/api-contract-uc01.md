> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC01 API — hợp đồng hiện hành, REVIEW delta MySQL 26/10/2026

[OpenAPI](openapi.yaml) là nguồn schema. Ba path giữ nguyên: POST `/api/v1/activations`, GET `/api/v1/activations/{requestId}`, GET `/api/v1/phone-numbers`. UC01/T1–T7 M4 lịch sử19/10 đã duyệt; delta MySQL8.0.46 chưa nghiệm thu/runtime test. [Ma trận API toàn backend](api-contract.md).

POST requestId/mode/customer; AUTO cấm selectedNumber, chỉ hết kho+allowWaitForNumber=true mới WAITING24h. SELECTED yêu cầu E164 selectedNumber, cấm consent=true; thất bại không chuyển WAITING. Hồ sơ fullName,birthDate,documentType,documentNumber,verificationReference bắt buộc; address/occupation/contact optional nhưng gửi null/rỗng lỗi. Không tự ghép customer bằng tên/ngày sinh, không tự phát chứng cứ KYC.

JWT scope activation:write/read/inventory:read theo endpoint; submit chứng cứ thêm telecom:evidence:submit. Caller từ token đáng tin; requestId do CSKH tự quản lý, không buộc nhân viên nhập. Canonical nghiệp vụ gồm reference chứng cứ và field optional có mặt; bỏ token/correlation/JSON order.

POST201 mới SUCCEEDED sau commit số+subscriber+MAIN0+request;200 replay thành công;202 PROCESSING/WAITING chưa thành công;400 validation;401/403 auth;409 key conflict/number unavailable/no stock;422 hồ sơ/chứng cứ;503 transient/backlog/unknown outcome. GET200 terminal kể cả FAILED/EXPIRED,202 pending,404 không caller-owned,410 chỉ tombstone. Replay7ngày sau terminal; sau đó POST409 retired, không reuse key; WAITING/PROCESSING không purge. Timeout tra GET/retry cùng ID, không suy FAILED.

GET kho số default50/max200 theo Q10, cursor integrity protected, không giữ chỗ. Chuẩn số E164 strict theo Q33. FIFO WAITING/gate, deadline/finalization nghiệp vụ và recovery60s xem [transaction](concurrency-and-jobs.md); không tuyên bố physical commit deadline. Mỗi ảnh UML phải render từ nguồn mới. Truy vết AC-SUB-01..27 ở UC01/SRS; test runtime chưa chạy.
