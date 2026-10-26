# CR-DB-001 – Thay PostgreSQL bằng MySQL và thay môi trường làm việc

**Ngày quyết định:** 19/10/2026 · **Người quyết định:** Chủ dự án (trong cuộc trò chuyện).  
**Trạng thái:** ĐÃ CHỐT thay DBMS; **chưa duyệt delta DDL, transaction, MySQL version và schema toàn hệ thống**.

## Lý do

Chủ dự án chuyển từ PostgreSQL sang **MySQL**; mạng/chính sách máy công ty hạn chế cài/chạy dịch vụ DB, nên công việc ở công ty tập trung vào tài liệu, ý tưởng, thiết kế và có thể code offline; các dịch vụ sẽ chạy tại nhà. Chủ dự án yêu cầu soạn toàn bộ tài liệu UC02–06 trước để duyệt cùng lượt, rồi tổng hợp thành báo cáo nghiệp vụ cuối cùng.

## Phạm vi ảnh hưởng

- Thay phần DBMS của DEC-PRE-03, M3/ADR, M4 vật lý UC01 và mọi profile Supabase cũ; **Supabase là PostgreSQL và không phải endpoint MySQL**.
- Không đổi baseline nghiệp vụ DEC-01..11, DEC-REQ, DEC-UC01, quy tắc Hybrid và T1–T7 ở cấp ý tưởng trừ khi kỹ thuật MySQL buộc sửa có biên bản.
- Không tự nhận M4-MySQL đã được duyệt; lập riêng **M4-DB delta review**, sau đó viết Flyway **MySQL** mới.
- Giữ Java21/Spring Boot, modular monolith, JPA/JDBC, Flyway, RabbitMQ, Redis và IdP ngoài backend trừ khi có CR riêng.

## Hồ sơ review

`docs/07-business-review/README-START-HERE.md` → 16-PHIEU-DUYET-TONG-HOP.md → 17-BAO-CAO-NGHIEP-VU-TONG-THE_DRAFT.md.
