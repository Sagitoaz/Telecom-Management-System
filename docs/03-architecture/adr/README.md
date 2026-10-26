# Danh mục quyết định kiến trúc

**Cập nhật:** 26/10/2026.

ADR lưu lý do và hệ quả của một quyết định kiến trúc. Trạng thái Accepted chỉ áp dụng phần quyết định được xác nhận; không thay việc nghiệm thu artifact chi tiết hoặc kết quả kiểm thử. Đọc [biên nhận quyết định mới nhất](../../00-project/decision-receipt-20261026.md) cùng lịch sử trong [sổ quyết định](../../00-project/clarification-log.md).

| ADR | Phạm vi hiện có hiệu lực | Phần lịch sử hoặc cần review |
|---|---|---|
| [ADR-001 — Kiến trúc nền](ADR-001-foundation.md) | Java/Spring Boot, ứng dụng chia module, JPA/JDBC/Flyway, RabbitMQ và Redis | PostgreSQL đã được ADR-003 thay bằng MySQL; chi tiết cấu hình/triển khai cần kiểm chứng |
| [ADR-002 — Môi trường phát triển](ADR-002-runtime-environments.md) | Giữ để tra lịch sử quyết định | Office → Supabase và home → PostgreSQL không còn là hướng cấu hình hiện hành |
| [ADR-003 — MySQL và môi trường](ADR-003-mysql-and-workstations.md) | MySQL 8.0.46/InnoDB, READ COMMITTED, UTC, bộ dịch vụ tại nhà và công ty không DB | UUID vật lý, schema/index, cấu hình và runtime mới là REVIEW |
| [ADR-004 — Quy tắc thời gian/nghiệp vụ](ADR-004-accepted-business-and-time.md) | Ngày lịch, hủy hết thời gian đã trả, receipt sau commit theo CR-CAN-003, recordedAt, kiểm hạn dưới khóa và phân vai job/worker | Cơ chế thực thi receipt hủy, thuật toán khóa/snapshot và các chi tiết triển khai cần review/test |

Khi thay quyết định đã được chốt, lập CR/ADR có truy vết, giữ nội dung và căn cứ duyệt cũ. Không sửa ngược lịch sử để tạo cảm giác phương án mới đã được chọn từ đầu. Những giá trị chưa được chọn như nhịp worker, batch, retry budget, thời gian giữ snapshot và cách lưu UUID phải được ghi rõ là đề xuất.

M3/M4 UC01 đã duyệt lịch sử không tự nghiệm thu delta MySQL hoặc toàn bộ UC02–UC06. Sự tồn tại của ADR, schema hoặc sơ đồ không chứng minh code, migration, runtime, TPS hay checkpoint mới đã hoàn tất.
