# Telecom Management System

Backend quản lý thuê bao: đấu nối Hybrid, đăng ký/hủy gói có thu phí, cộng/trừ tiền, báo cáo tiêu dùng và tác vụ định kỳ. Xem [đề bài gốc](docs/requirement.md).

**26/10/2026:** đã đồng bộ bộ thiết kế theo phiếu chủ dự án và CR mới; đọc [chỉ mục tài liệu](docs/README.md) hoặc [báo cáo nghiệp vụ tổng thể](docs/07-business-review/17-BAO-CAO-NGHIEP-VU-TONG-THE_DRAFT.md). M0–M4 UC01 có phê duyệt lịch sử; delta MySQL và thiết kế mới còn REVIEW theo [checklist M4](docs/04-design/m4-review-checklist.md).

Repo hiện có Maven pom và Main mẫu, chưa có backend nghiệp vụ, migration hoặc Compose được kiểm chứng. pom còn dependency PostgreSQL cũ; chuyển sang MySQL tại P5.1 sau duyệt delta, không xem tài liệu là bằng chứng triển khai xong.

Mục tiêu runtime Java 21/Spring Boot, MySQL 8.0.46/InnoDB; RabbitMQ, Redis và Keycloak theo [kiến trúc](docs/03-architecture/architecture.md). Công ty đọc/duyệt/code không DB; chạy service ở nhà theo [hướng dẫn triển khai](docs/06-deployment/deployment-guide.md).

Tiến độ và bước kế tiếp: [ROADMAP](docs/ROADMAP.md). Kiểm chứng tài liệu: [verification](docs/verification.md). Không có kết quả build/backend/MySQL/tải mới trong lượt biên soạn này.

Hướng dẫn đọc toàn bộ hồ sơ: [thứ tự đọc và rà soát](docs/HUONG-DAN-DOC-VA-RA-SOAT.md). Báo cáo một file kèm hình: [tổng hợp dự án](docs/BAO-CAO-TONG-HOP-DU-AN.md).
