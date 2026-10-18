# Chỉ mục bộ tài liệu

**Cập nhật:** 18/10/2026 — đã đồng bộ DEC-PRE-01–24; Q25 chờ chủ dự án duyệt. Vị trí thực tế: docs/. M0 đã duyệt; M1 chờ duyệt; các phần tiếp theo là DRAFT/kế hoạch theo CR-DOC-001.

Đọc [đề bài](requirement.md) → [roadmap](ROADMAP.md) → [sổ quyết định](00-project/clarification-log.md) → [SRS](01-requirements/SRS.md). SRS/UC01 và sổ quyết định có đủ nội dung áp dụng, không cần mở phiếu review cũ.

| Nhóm | Tài liệu chính | Vai trò / điều kiện |
|---|---|---|
| Quyết định | [Phiếu còn thiếu](00-project/decision-questionnaire.md) | Đầu vào cho chủ dự án; ô trống không có hiệu lực |
| Yêu cầu | [UC01](01-requirements/use-cases/UC01-Subscriber-Activation.md), [UC02–06](01-requirements/use-cases/UC02-UC06.md), [hai job](01-requirements/scheduled-jobs.md) | M1 ưu tiên UC01; phần còn lại chốt trước UC tương ứng |
| Phân tích | [Thuyết minh UML](02-analysis/analysis.md) | P2.1/P2.2/P2.4; mô hình nghiệp vụ, chưa phải DB |
| Kiến trúc | [Kiến trúc](03-architecture/architecture.md), [module](03-architecture/module-structure.md) | Đề xuất Phase 3, chờ quyết định stack/ranh giới |
| Thiết kế | [DB](04-design/database-schema.md), [hợp đồng UC01](04-design/api-contract-uc01.md), [đồng thời](04-design/concurrency-and-jobs.md) | Chưa có schema/OpenAPI triển khai chính thức |
| Triển khai | [Kế hoạch vertical slice](04-design/implementation-plan.md) | Chỉ bắt đầu UC01 sau M4 |
| Kiểm thử | [Test plan](05-testing/test-plan.md), [hiệu năng](05-testing/performance-test.md) | Kế hoạch; chưa có kết quả đo |
| Bàn giao | [Hướng dẫn triển khai](06-deployment/deployment-guide.md) | Các bước nghiệm thu dự kiến; chưa phải runbook đã chạy |

Mỗi tài liệu mới ghi nguồn, phần đã chốt, phần Open và điều kiện hoàn tất. DRAFT không phải baseline được duyệt. Các file .puml là nguồn UML; báo cáo kiểm chứng ghi riêng trong [verification.md](verification.md). Hai phiếu review cũ đã xóa theo yêu cầu; lịch sử/hiệu lực lựa chọn nằm đầy đủ trong clarification log, không sửa ngược quyết định.


**Bắt đầu đọc tại [review-guide.md](00-project/review-guide.md):** SRS0.6 → UC01 0.5 → ý kiến R1/R2/R3/R4 → Q25. Chưa cần duyệt hết UML/DB/API trước M1. Quyết định RabbitMQ/Redis ở [ADR-001](03-architecture/adr/ADR-001-foundation.md), kiểm thử tải dùng k6 theo DEC-PRE-24.
