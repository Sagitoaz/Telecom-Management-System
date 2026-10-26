> **⚠️ ADR-002 SUPERSEDED VỀ DATABASE VÀ OFFICE:** Office→Supabase và home→PostgreSQL Docker đã thay đổi theo CR-DB-001/ADR-003; giữ nội dung dưới để tra cứu lịch sử, không làm theo cách kết nối cũ.

---

# ADR-002 – Một codebase, hai môi trường phát triển

**Trạng thái:** Accepted về lựa chọn môi trường theo xác nhận chủ dự án 19/10/2026.
**Người quyết định:** Chủ dự án. **Tác động:** P5.1/P5.2/P7; không thay đổi nghiệp vụ/AC UC01 hoặc ADR-001.

## Vấn đề

Máy công ty chạy Windows không thể cài Docker Desktop theo điều kiện hiện tại. Máy cá nhân có thể chạy Docker. Chủ dự án lựa chọn PostgreSQL Supabase ở công ty và PostgreSQL Docker tại nhà, chuyển bằng cấu hình/.env, không thay code.

## Quyết định

- `office`: Spring Boot/JVM trên Windows; database PostgreSQL do Supabase cung cấp; **không yêu cầu Docker**; RabbitMQ và Redis không có thì adapter dev chỉ dùng DB recovery và fallback đọc DB.
- `home`: Spring Boot/JVM trong IDE, PostgreSQL/RabbitMQ/Redis/Keycloak trong Docker Compose.
- Tất cả CRUD, locking, Flyway và SQL là PostgreSQL chuẩn, source chung. Chuyển môi trường bằng `SPRING_PROFILES_ACTIVE` + biến môi trường; **không** sửa Java/SQL thủ công.
- PostgreSQL là nguồn quyết định nghiệp vụ. Office mode không có broker chỉ kiểm thử luồng recovery định kỳ, **không** tuyên bố kiểm thử end-to-end RabbitMQ/Redis.
- JWT phải được kiểm tra bởi bộ xác minh với JWKS hợp lệ. Keycloak home là IdP local, ở office có thể dùng IdP ngoài máy công ty; nếu chưa có IdP thì chỉ kiểm tra migration/health và integration test có mock JWT (test only), **không được mở API công khai không bảo mật**.
- Không sử dụng Supabase SDK/REST/anon key để thay kết nối PostgreSQL cho Spring JDBC/JPA. Không dùng data thật của doanh nghiệp trong Supabase dev.

## Ràng buộc triển khai

- Supabase backend lâu dài dùng **Session Pooler 5432 qua SSL** nếu mạng công ty chỉ có IPv4, hoặc Direct 5432 nếu có IPv6/IPv4 add-on. Không dùng Transaction Pooler 6543 cho JDBC/JPA/Flyway như mặc định.
- Flyway migrations phải có schema riêng (ví dụ `telecom_dev`), role có quyền DDL. Flyway direct URL ưu tiên khi mạng cho phép; nếu không có IPv6, kiểm chứng migration qua session pooler trên **database dev riêng** trước.
- `office` và `home` có cùng phiên bản migration; dữ liệu giữa hai DB không tự đồng bộ.
- Tuyệt đối không commit `.env.office`, `.env.home`, mật khẩu, JWT private key hoặc file credentials.

## Chi phí/đánh đổi

Office mode không đại diện môi trường hoàn chỉnh (RabbitMQ/Redis/Keycloak); kiểm thử đầy đủ UC01 Hybrid theo AC, restart/recovery, race, audit và tải ở `home`/integration riêng. Supabase internet latency/pooling ảnh hưởng phép đo.

## Tham khảo

- Supabase connect: https://supabase.com/docs/guides/database/connecting-to-postgres
- Supabase Spring Boot: https://supabase.com/docs/guides/getting-started/quickstarts/spring-boot
- Docker Compose profiles: https://docs.docker.com/reference/compose-file/profiles/
