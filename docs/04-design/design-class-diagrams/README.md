> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Design Class Diagrams — REVIEW 26/10/2026

Bắt đầu bằng các sơ đồ tập trung dưới đây để đọc signatures và trách nhiệm ở kích thước rõ ràng. Mỗi sơ đồ giữ tên lớp/phương thức từ overview, chỉ lược các trách nhiệm không thuộc luồng đang xem; authorization và operation registry dùng chung vẫn áp dụng. Đây là Design Class, không phải Domain Model/JPA Entity/ERD.

| Góc nhìn | Nguồn sửa | Ảnh SVG sau render |
|---|---|---|
| UC01 — claim, allocation, customer, MAIN, WAITING/recovery | [uc01-design-class.puml](uc01-design-class.puml) | [UC01 SVG](uc01-design-class.svg) |
| UC04/05 — tiền, eligibility dưới khóa, immutable ledger, idempotency | [money-design-class.puml](money-design-class.puml) | [Money SVG](money-design-class.svg) |
| UC02/03 — kỳ/phí nguyên tử, version, CR-CAN-003 hai transaction | [subscription-design-class.puml](subscription-design-class.puml) | [Subscription SVG](subscription-design-class.svg) |
| UC06/JOB-02 — một statement snapshot, DISTINCT subscriber, cursor và contribution | [report-design-class.puml](report-design-class.puml) | [Report SVG](report-design-class.svg) |
| Worker/JOB-01/JOB-02 — phối hợp, renewal core, pending intent và recovery | [jobs-design-class.puml](jobs-design-class.puml) | [Jobs SVG](jobs-design-class.svg) |
| Toàn backend — overview đầy đủ cần zoom SVG | [backend-design-class.puml](backend-design-class.puml) | [Overview SVG](backend-design-class.svg) |

Overview giữ đầy đủ liên hệ API/application/core/ports/jobs để truy vết; không dùng ảnh PNG thu nhỏ của overview thay các hình tập trung. SVG có thể zoom mà không mất chữ. Mỗi nguồn PlantUML tự chứa, smetana, không remote include; SVG phải được render lại sau sửa nguồn.

DbClock lấy giờ sau đủ khóa và ngay trước cập nhật tiền; broker/cache không làm source of truth. Q24 đã chốt CR-CAN-003: receipt sau confirmed intent commit, late receipt thất bại, accepted mất phản hồi replay; thiết kế hai transaction còn REVIEW/test dự kiến. Chi tiết [concurrency-and-jobs](../concurrency-and-jobs.md) và [OpenAPI](../openapi.yaml). Có UML/render không có nghĩa code chạy hay M4-DB được duyệt.
