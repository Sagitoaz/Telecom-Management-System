# Ranh giới module dự kiến

**DRAFT 0.2, 18/10/2026 — P3.4.** Chưa tạo Java package. Nguồn: SRS UC01–06, FR-JOB, stack đã chọn DEC-PRE-03 và ADR-001.

| Module đề xuất | Sở hữu / trách nhiệm | Phụ thuộc cần kiểm soát |
|---|---|---|
| customer | Hồ sơ và giấy tờ đã xác minh | Không tự hợp nhất khách |
| subscriber | Subscriber, điều phối ActivationRequest | Gọi customer, inventory, mở Account chính |
| inventory | Kho số, tính khả dụng, cấp duy nhất | Không gọi ngược subscriber để tạo vòng phụ thuộc |
| account | Tài khoản/số dư, thao tác cộng trừ nhất quán | Cung cấp năng lực thu phí cho đăng ký |
| transaction | Lịch sử tài chính/truy vết request | Cùng ranh giới commit với số dư |
| package | Định nghĩa gói, đăng ký, chu kỳ/hủy/gia hạn | Điều phối thu phí qua năng lực account |
| reporting | Truy vấn lịch sử/tổng hợp/điều chỉnh | Không sửa ngược số dư/gói |
| scheduling | Kích hoạt job/recovery | Gọi ứng dụng nghiệp vụ, không nhân bản BR |
| security/api | JWT, quyền, validation, ánh xạ giao thức | Không chứa thuật toán tiền/cấp số |

Hướng gọi: API/scheduling → application use case → domain/persistence; không cho controller sửa trực tiếp số dư.
account/transaction có thể là một module nếu cùng consistency boundary; quyết định tách package Java không buộc tách transaction/DB.
Thiết kế giao diện liên module, nơi giữ requestId và boundary giao dịch cụ thể còn Phase4; hướng scope/canonical/row-lock đã chọn DEC-PRE-05–09. Không đặt entity dùng chung toàn hệ thống rồi phụ thuộc vòng.


Adapter broker/cache thuộc infrastructure: RabbitMQ kích hoạt application worker; Redis đọc danh mục qua cache-aside, không là shared domain state. Khác Java package name với nghiệp vụ: nhãn package module là khái niệm, tên package Java thực tế tránh từ khóa và chốt ở P5.
