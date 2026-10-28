# Telecom Management System

Backend quản lý thuê bao: đấu nối Hybrid, đăng ký/hủy gói có thu phí, cộng/trừ tiền, báo cáo tiêu dùng và tác vụ định kỳ. [Đề bài gốc](docs/requirement.md).

**28/10/2026 — bộ tài liệu đã chốt, gồm M4-DB MySQL.** Chủ dự án phê duyệt theo [CR-DOC-004](docs/00-project/document-approval-20261028.md). Việc kế tiếp: **P5.1 — khởi tạo backend**, sau đó ưu tiên **UC04/UC05 cộng-trừ tiền** theo CR-IMPL-001 và hướng mentor, rồi các UC còn lại. [ROADMAP](docs/ROADMAP.md) · [Kế hoạch coding](docs/04-design/implementation-plan.md).

Nền tảng đã chọn: Java21/Spring Boot, MySQL8.0.46/InnoDB, Flyway, RabbitMQ, Redis và Keycloak/IdP ngoài backend. Công ty code/unit không DB; chạy service tại nhà theo [hướng dẫn](docs/06-deployment/deployment-guide.md).

P5.1 đã có bộ nền Spring Boot + cấu hình MySQL; `mvn verify` build được executable JAR. Chưa có migration nghiệp vụ/JWT/API tiền hoặc kiểm chứng DB/health thực tế; M5–M8 chưa đạt.

- [Chỉ mục tài liệu đã chốt](docs/README.md)
- [Báo cáo tổng hợp có UML](docs/BAO-CAO-TONG-HOP-DU-AN.md)
- [Schema](docs/04-design/database-schema.md), [OpenAPI](docs/04-design/openapi.yaml), [transaction/jobs](docs/04-design/concurrency-and-jobs.md)
- [Nhật ký kiểm chứng](docs/verification.md)

Đã dọn out/target, ZIP lưu tạm và bộ script/JSON kiểm tra tài liệu. Giữ tài liệu chuẩn, nguồn UML/hình và lịch sử quyết định trong Markdown. Các nhãn REVIEW/DRAFT cũ trên hình/tên file được đóng bởi CR-DOC-004, không còn là yêu cầu duyệt lại.

Kế hoạch cá nhân: [học tại công ty](docs/KE-HOACH-HOC-TAI-CONG-TY.md) · [tự code tại nhà](docs/KE-HOACH-CODE-TAI-NHA.md). Mỗi chủ đề học gắn bài thực hành, test ngoại lệ và tiêu chí hoàn thành.

## Bộ file nền đầu tiên để đọc vào ngày mai

Đọc lần lượt: [.env.example](.env.example) → [compose.yaml](compose.yaml) → [application.yaml](src/main/resources/application.yaml) → [application-home.yaml](src/main/resources/application-home.yaml) → [Java main](src/main/java/com/telecom/TelecomManagementApplication.java) → [pom.xml](pom.xml). Các file có chú thích ngắn để bạn nối cú pháp với vai trò thực tế.

Compose hiện chỉ chạy MySQL; Java chạy từ IDE/terminal. Tại máy nhà, copy .env.example thành .env, tự điền mật khẩu, mở Docker Desktop chờ Engine sẵn sàng rồi chạy `docker compose up -d mysql`. Khi chạy Java, cấp DB_PASSWORD/DB_USERNAME và profile home qua IDE/shell; JVM không tự đọc .env của Compose. Hướng dẫn từng bước nằm ở [N01B/N01C](docs/KE-HOACH-CODE-TAI-NHA.md).

Chưa tạo Dockerfile/backend container hoặc endpoint ping trong lượt làm bộ nền. Health chỉ kiểm sau khi DB thực chạy; chưa coi các file khởi tạo là backend nghiệp vụ hoàn chỉnh.
