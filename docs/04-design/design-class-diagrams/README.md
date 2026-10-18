# Khung thiết kế lớp UC01

**DRAFT 0.2, 18/10/2026. P4.4 chưa hoàn tất.**
Domain Model mô tả nghiệp vụ; file này liệt kê trách nhiệm kỹ thuật cần thiết kế sau M3, chưa tạo entity Java.

| Vai trò dự kiến | Trách nhiệm | Hợp đồng phải chốt |
|---|---|---|
| Activation API adapter | Parse/validate request, trả kết quả/lỗi | Hướng API DEC-PRE-11, OpenAPI đầy đủ còn P4 |
| Activation application service | Điều phối profile/cấp số/account/kết quả | Hướng row lock DEC-PRE-09, boundary cụ thể còn P4, failure/commit |
| Profile resolver | Chuẩn hóa và dùng giấy tờ đã xác minh | DEC-PRE-01/02; canonical schema còn P4 |
| Request registry | Claim/read kết quả, conflict | Scope/retention/canonical form DEC-PRE-05–07; HTTP tombstone còn P4 |
| Number allocator | Chọn ngẫu nhiên/chọn số, không trùng | DEC-PRE-08/09; thuật toán khóa còn P4, chuẩn số |
| Account creation capability | Một account chính 0 VND | Ràng buộc DB và retry |
| Waiting recovery | Trigger/khôi phục/expiry | DEC-PRE-08/10; claim còn P4, transaction completion |
| Query service | Tra cứu theo quyền caller | JWT/ownership DEC-PRE-04/11; claims/schema còn P4 |

Hoàn tất P4.4 khi có class/interface Java dự kiến, method và kiểu dữ liệu từ schema/API đã chốt, sequence kỹ thuật gồm rollback/retry, mối phụ thuộc không vòng. Không đánh đồng danh sách trên với Design Class Diagram đã hoàn thành.


Thêm adapter RabbitMQ tín hiệu kho số (sau commit, consumer idempotent) và Redis đọc danh mục gói theo ADR-001; không nhét hai adapter vào Domain Model. 27 AC UC01 cần truy vết đến trách nhiệm/method/test sau khi M4 đủ điều kiện.
