# Chỉ mục bộ thiết kế backend

**28/10/2026 — ĐÃ CHỐT.** Chủ dự án duyệt toàn bộ tài liệu hiện hành theo [CR-DOC-004](00-project/document-approval-20261028.md), gồm M4-DB MySQL. Bước kế tiếp **P5.1**, theo [kế hoạch coding](04-design/implementation-plan.md). Chưa có backend nghiệp vụ chạy được hoặc kết quả đo tải.

## Hai tài liệu để bắt đầu đọc và rà soát

- [Hướng dẫn đọc và rà soát](HUONG-DAN-DOC-VA-RA-SOAT.md): thứ tự theo phase, vòng đọc theo UC, checklist và cách ghi nhận xét.
- [Báo cáo tổng hợp dự án có UML](BAO-CAO-TONG-HOP-DU-AN.md): bắt đầu bằng Activity, kịch bản chuẩn/ngoại lệ và Class Diagram, rồi toàn bộ phase, 42 hình, API/DB và 64 ca biên.

## Thứ tự đọc đề xuất

1. [Báo cáo nghiệp vụ tổng thể](07-business-review/17-BAO-CAO-NGHIEP-VU-TONG-THE_DRAFT.md): hệ thống làm gì, luồng chính và các biên quan trọng.
2. [Bản tiếp nhận quyết định](00-project/decision-receipt-20261026.md) và [clarification log](00-project/clarification-log.md): lựa chọn của chủ dự án, CR và phần còn mở.
3. [SRS](01-requirements/SRS.md), [UC01–UC06](01-requirements/use-cases/UC02-UC06.md), [jobs](01-requirements/scheduled-jobs.md): tiền/hậu điều kiện, dữ liệu, luồng chính/nhánh/lỗi và tiêu chí nghiệm thu.
4. [Toàn bộ hình UML/SVG](UML-INDEX.md) và [UML phân tích](02-analysis/analysis.md): Use Case, Domain, State, Sequence; nguồn PlantUML đi kèm SVG.
5. [Kiến trúc](03-architecture/architecture.md), [module](03-architecture/module-structure.md), [bảo mật](03-architecture/security-and-integration.md), [runtime](03-architecture/runtime-profiles.md).
6. [Schema/ERD](04-design/database-schema.md), [API](04-design/api-contract.md), [OpenAPI](04-design/openapi.yaml), [transaction/jobs](04-design/concurrency-and-jobs.md), [Design Class](04-design/design-class-diagrams/README.md).
7. [Kế hoạch test](05-testing/test-plan.md), [protocol tải](05-testing/performance-test.md), [64 kịch bản ngoại lệ](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md), [bằng chứng kiểm tra tài liệu](verification.md).

## Trạng thái và bước tiếp theo

M0–M4 và delta MySQL **đã duyệt ngày 28/10/2026**. DB hiện hành **MySQL 8.0.46/InnoDB**. [Checklist M4](04-design/m4-review-checklist.md) tách phần thiết kế đã chốt và kiểm chứng runtime còn phải chạy. [ROADMAP](ROADMAP.md) là nguồn trạng thái; [implementation plan](04-design/implementation-plan.md) triển khai từng UC, bắt đầu UC01.

[Phiếu 16](07-business-review/16-PHIEU-DUYET-TONG-HOP.md) đã điền và được giữ nguyên để lưu lịch sử; không yêu cầu điền lại. Các trang review 01–15/20 hiện đóng vai trò chỉ dẫn đến tài liệu chuẩn để tránh duy trì hai bộ quy tắc mâu thuẫn. Lịch sử quyết định được tra tại clarification log; bản ZIP phụ đã dọn theo yêu cầu chủ dự án.

[Đề bài gốc](requirement.md) giữ nguyên byte. Tài liệu thiết kế, kết quả kiểm tra tĩnh và kết quả runtime là ba loại bằng chứng khác nhau. Chưa có bằng chứng M5–M8.

Các nhãn REVIEW/DRAFT trước 28/10 trong hình/tên file/nội dung lịch sử đã được đóng bằng CR-DOC-004; giữ nguyên nguồn–hình và phiếu để truy vết. Kế hoạch test và guide đã chốt ở mức tài liệu, chưa là bằng chứng đạt M5–M8.
