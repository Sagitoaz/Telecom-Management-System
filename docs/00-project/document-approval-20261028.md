# CR-DOC-004 — Chốt bộ tài liệu và chuẩn bị coding

**Trạng thái: APPROVED / ĐÃ CHỐT. Ngày: 28/10/2026. Người duyệt: Chủ dự án.**

## Căn cứ phê duyệt

Chủ dự án xác nhận trực tiếp: “giờ coi như bộ tài liệu đã chốt rồi, đóng mọi tài liệu là chốt, xóa bớt các tool và các file không cần thiết đi, chuẩn bị để vào giai đoạn coding”. Đây là phê duyệt rõ ràng cho bộ tài liệu hiện có, gồm delta MySQL M4-DB và các đề xuất kỹ thuật hiện hành; không yêu cầu duyệt lại cùng nội dung.

Trạng thái cũ: bộ cập nhật ngày 26/10 là REVIEW, M4-DB chờ nghiệm thu. Trạng thái mới: **baseline tài liệu đã chốt**, M0–M4 và M4-DB đủ điều kiện chuyển sang **P5.1**. Phê duyệt được lưu tại biên bản này và clarification log mục 21.

## Phạm vi và cách áp dụng

- Chốt yêu cầu/SRS, UC01–UC06/jobs, truy vết/ca biên, toàn bộ UML, kiến trúc/ADR hiện hành, MySQL schema/ERD, OpenAPI/API, Design Class, transaction/recovery và cấu hình đề xuất đang dùng.
- Cụ thể gồm MySQL 8.0.46/InnoDB/READ COMMITTED; UUID CHAR(36) ascii_bin; contract/HTTP/scope/ownership; protocol CR-CAN-003; renewal interval 10s/batch 100, lease 30s/retry 1..16s, report retention 24h theo thiết kế. Đây là cấu hình khởi điểm đã duyệt, phải kiểm chứng khi chạy.
- Chốt cả kế hoạch test/tải, deployment guide và báo cáo tổng hợp **ở mức tài liệu**. Toàn bộ ca runtime vẫn NOT_RUN; M5–M8 chưa đạt. P5.1 chỉ hoàn tất khi có build/migration/healthcheck thực tế.
- Các nhãn REVIEW/DRAFT, tên file có DRAFT và câu “chưa duyệt/chờ duyệt” trong bản vẽ, báo cáo hoặc lịch sử trước ngày 28/10 là dấu vết của bản được ký duyệt. Quyết định này xác lập trạng thái APPROVED cho đúng các nguồn/hình đó; không sửa nội dung hình chỉ để thay nhãn và làm mất liên kết hash nguồn–ảnh.
- Giữ quyết định nghiệp vụ có hiệu lực: Hybrid UC01; hủy cuối toàn bộ thời gian đã trả theo CR-CAN-002/003; CR-TIME-001/002; worker gia hạn + JOB-01 00:05 + JOB-02 00:30. Không khôi phục phương án đã bị thay hoặc NOT ADOPTED trong phiếu/CR/ADR cũ.
- Phiếu đã điền và đề bài gốc giữ nguyên byte. Nội dung lịch sử chỉ dùng truy vết; không yêu cầu chọn lại phương án cũ.

## Phần chuyển sang thực thi

Các OQ đã có lựa chọn trong SRS/API/schema/transaction được đóng **phần quyết định tài liệu** theo baseline này. Không còn blocker duyệt tài liệu trước P5.1. Phần chưa có giá trị thực tế vẫn là công việc triển khai, không tự bịa dữ liệu:

| Phần việc còn phải làm | Mốc |
|---|---|
| Java/Maven, chuyển dependency PostgreSQL cũ sang MySQL, Spring Boot entrypoint, cấu hình office/home, Flyway/MySQL, Compose | P5.1 |
| Issuer/audience/JWKS/claim mapping, trusted caller/evidence và secrets của môi trường thật | Trước nghiệm thu REST API UC01 |
| Migration/CHECK/UQ/FK/collation, seed thực, EXPLAIN, race/deadlock/unknown commit/recovery | P5, theo từng UC |
| Seed tài khoản phụ/danh mục gói và dữ liệu đại diện | Trước UC liên quan |
| Đo tải/overlap/đối soát/khả năng phục hồi và chạy trên máy sạch | P6/P7 |
| Tái sử dụng số, archive/purge sau demo, triển khai production ngoài phạm vi hiện tại | Chỉ mở khi có yêu cầu mới |

Nếu kiểm thử làm lộ lỗi hoặc phải đổi quy tắc đã chốt, ghi CR/delta đúng phạm vi. Không mở lại vòng duyệt toàn bộ tài liệu trước mỗi UC.

## Dọn repository

Đã xóa out/target và, theo yêu cầu bổ sung của chủ dự án ngày 28/10/2026, xóa cả ZIP lưu tạm, thư mục script/JSON kiểm tra tài liệu và manifest JSON phụ. Không duy trì công cụ kiểm tra tài liệu riêng trong repository.

Giữ tài liệu chuẩn, nguồn PlantUML, PNG/SVG đang nhúng trong báo cáo, phiếu/DEC/CR, cấu hình IDE và code/pom hiện có. Lịch sử quyết định và tóm tắt kết quả kiểm tra được ghi trong Markdown; không còn phụ thuộc ZIP hoặc script đã xóa. Việc dọn chỉ áp dụng trong repository.

## Bước tiếp theo duy nhất

**P5.1 — Khởi tạo backend MySQL theo [kế hoạch triển khai](../04-design/implementation-plan.md).** Đầu vào tài liệu đã được duyệt; chưa triển khai nghiệp vụ trong lượt chốt/dọn này. Xem [ROADMAP](../ROADMAP.md) và [kiểm chứng](../verification.md).
