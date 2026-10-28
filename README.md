# Telecom Management System

Backend quản lý thuê bao: đấu nối Hybrid, đăng ký/hủy gói có thu phí, cộng/trừ tiền, báo cáo tiêu dùng và tác vụ định kỳ. [Đề bài gốc](docs/requirement.md).

**28/10/2026 — bộ tài liệu đã chốt, gồm M4-DB MySQL.** Chủ dự án phê duyệt theo [CR-DOC-004](docs/00-project/document-approval-20261028.md). Việc kế tiếp: **P5.1 — khởi tạo backend**, sau đó ưu tiên **UC04/UC05 cộng-trừ tiền** theo CR-IMPL-001 và hướng mentor, rồi các UC còn lại. [ROADMAP](docs/ROADMAP.md) · [Kế hoạch coding](docs/04-design/implementation-plan.md).

Nền tảng đã chọn: Java21/Spring Boot, MySQL8.0.46/InnoDB, Flyway, RabbitMQ, Redis và Keycloak/IdP ngoài backend. Công ty code/unit không DB; chạy service tại nhà theo [hướng dẫn](docs/06-deployment/deployment-guide.md).

Repo hiện có pom và Main mẫu; pom còn driver/Flyway/Testcontainers PostgreSQL cần chuyển trong P5.1. Chưa có backend nghiệp vụ/migration/Compose được nghiệm thu. M5–M8 và kết quả runtime/tải chưa đạt.

- [Chỉ mục tài liệu đã chốt](docs/README.md)
- [Báo cáo tổng hợp có UML](docs/BAO-CAO-TONG-HOP-DU-AN.md)
- [Schema](docs/04-design/database-schema.md), [OpenAPI](docs/04-design/openapi.yaml), [transaction/jobs](docs/04-design/concurrency-and-jobs.md)
- [Nhật ký kiểm chứng](docs/verification.md)

Đã dọn out/target, ZIP lưu tạm và bộ script/JSON kiểm tra tài liệu. Giữ tài liệu chuẩn, nguồn UML/hình và lịch sử quyết định trong Markdown. Các nhãn REVIEW/DRAFT cũ trên hình/tên file được đóng bởi CR-DOC-004, không còn là yêu cầu duyệt lại.

Kế hoạch cá nhân: [học tại công ty](docs/KE-HOACH-HOC-TAI-CONG-TY.md) · [tự code tại nhà](docs/KE-HOACH-CODE-TAI-NHA.md). Mỗi chủ đề học gắn bài thực hành, test ngoại lệ và tiêu chí hoàn thành.
