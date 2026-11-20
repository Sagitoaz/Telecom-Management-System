# ADR-001 — Raw JDBC và transaction thủ công cho Telecom Management System

**Trạng thái:** Quyết định kiến trúc theo yêu cầu mentor, 18/11/2026

## Quyết định

- Các truy vấn SQL trong pipeline nghiệp vụ viết bằng JDBC `Connection`, `PreparedStatement`, `ResultSet`; không dùng Spring Data JPA `JpaRepository`, `@Query`/`@NativeQuery`, `JdbcTemplate` hoặc ORM để thực thi SQL nghiệp vụ.
- Transaction trong pipeline nghiệp vụ được quản lý thủ công bằng **cùng một JDBC `Connection`**: `setAutoCommit(false)`, `commit()`, `rollback()` và đóng/trả kết nối trong `finally`/try-with-resources. Không dùng `@Transactional` hay `TransactionTemplate` để quản lý những transaction đó.
- Lớp DAO chỉ chuẩn bị và chạy SQL trên **Connection được truyền vào**; DAO không tự `commit`, `rollback`, hoặc đóng Connection của caller. Lớp điều phối transaction là chủ sở hữu Connection.
- Tiếp tục sử dụng MySQL/InnoDB và schema do Flyway tạo. Có thể dùng `javax.sql.DataSource` với connection pooling; `DataSource` là API JDBC, không đồng nghĩa dùng Spring transaction. Chỉ loại bỏ JPA/JdbcTemplate sau khi chuyển đổi hết các call site liên quan.
- Spring tiếp tục làm lớp HTTP/API, xác thực và request/response; các bước pipeline nghiệp vụ và data access dùng Java/JDBC trực tiếp. Nếu Spring vẫn dùng để tạo/inject DataSource, đó chỉ là bootstrap/configuration, không phải cơ chế xử lý SQL/transaction.
- Các yêu cầu nghiên cứu tiếp theo từ Notes.txt: xử lý transaction qua queue, phân tích cấu hình số luồng/request của Spring; chưa coi là đã triển khai.

## Luồng UC04 cần giữ

1. Tạo `RequestKey`, canonical JSON, SHA-256 fingerprint.
2. **Transaction A (claim)**: trên một Connection độc lập, thử `INSERT operation_request` trạng thái `PROCESSING`, commit; nếu trùng khóa nghiệp vụ, rollback xong mới đọc request cũ để xử lý idempotency.
3. `NEW` mới được phép tiếp tục nghiệp vụ. `PROCESSING` không chạy lại; `REPLAY` trả kết quả đã lưu; cùng key khác fingerprint báo conflict.
4. **Transaction B (finalizer)**: trên một Connection, khóa `operation_request` trước `service_account`, kiểm tra điều kiện, cập nhật balance, ghi `financial_transaction`, ghi `operation_request` thành kết quả cuối; tất cả commit hoặc rollback cùng nhau.
5. Khi chuyển từ Spring Data JPA sang JDBC, lỗi SQL là `SQLException` và các subtype; không tiếp tục chỉ bắt `DataIntegrityViolationException`.

## Bước triển khai đầu tiên

Tạo `OperationRequestJdbcDao` (Java thuần), chuyển lệnh `INSERT operation_request` từ `@Modifying @NativeQuery` sang `PreparedStatement`. Hàm DAO nhận `Connection` từ bên gọi, nhận entity hoặc các giá trị đã chuẩn bị, và trả về số hàng ảnh hưởng. **Không** tự mở transaction hoặc đóng Connection ở DAO. Sau đó mới chuyển `OperationClaimWriter` sang sở hữu Connection và thực hiện commit/rollback thủ công, rồi điều chỉnh `OperationClaimService` xử lý `SQLException`.

## Nguyên tắc chuyển đổi

Chuyển từng luồng rồi kiểm thử: Claim insert → Claim lookup → Credit finalizer → Debit; không xóa dependency JPA/JdbcTemplate khi còn code sử dụng chúng. Tách riêng kiểm thử offline tại máy có Maven và database theo tiến độ thực tế.
