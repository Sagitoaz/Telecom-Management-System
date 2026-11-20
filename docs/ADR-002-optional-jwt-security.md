# ADR-002 — Xác thực tùy chọn, không phụ thuộc nhà cung cấp JWT khi phát triển

**Ngày:** 18/11/2026  
**Trạng thái:** Quyết định bổ sung từ yêu cầu mentor; chưa triển khai trong source.

## Quyết định

Ứng dụng có hai chế độ xác thực tách biệt:

- `local`: Chỉ để chạy thử ở máy phát triển, không cần authorization server hoặc JWT. Bỏ qua xác thực HTTP cho **những endpoint thử nghiệm được chỉ định**, không mở toàn bộ API. `callerId` được gán từ cấu hình server (ví dụ `LOCAL_CSKH`) ổn định cho các lần retry; không lấy trực tiếp từ header tùy ý của client.
- `jwt`: Bật kiểm tra JWT (chữ ký, issuer/audience và quyền/scope) bằng Spring Security Resource Server. Tách cấu hình issuer/JWKS khỏi cấu hình chung để chế độ local không cần `JWT_ISSUER_URI` và không cố kết nối dịch vụ ngoài.

Chỉ bật chế độ `local` một cách **tường minh** khi phát triển (ví dụ profiles `home,local`). Khi chạy môi trường không có profile hợp lệ, cấu hình phải **fail closed** (không tự permitAll). Trên máy thật dùng ngoài localhost/LAN, không dùng local mode.

## Ranh giới thiết kế

- `MoneyController` không nhận `@AuthenticationPrincipal Jwt` trực tiếp; chỉ gọi `CallerIdentityResolver.resolve()`.
- `CallerIdentityResolver` là abstraction; triển khai `LocalCallerIdentityResolver` và `JwtCallerIdentityResolver`, mỗi implementation chỉ được khởi tạo ở profile tương ứng.
- Cả hai chế độ đều trả về cùng một kiểu `callerId` cho lớp nghiệp vụ. Giữ nguyên khóa idempotency `(callerId, operation, requestId)`; trong local mode cần `callerId` có giá trị ổn định.
- `SecurityConfig`: hai `SecurityFilterChain` theo profile. Ở `local`, chỉ `permitAll()` cho health và endpoint API đã chỉ định; các route khác `denyAll()`. Ở `jwt`, áp dụng `oauth2ResourceServer(...jwt...)` và các authority như hiện tại.
- `application.yaml`: giữ cấu hình chung, **chuyển** `spring.security.oauth2.resourceserver.jwt.issuer-uri` và audiences sang `application-jwt.yaml`; tạo/định nghĩa cấu hình local (nếu cần).
- Chế độ xác thực HTTP là lớp biên, **độc lập** với quyết định ADR-001: nghiệp vụ SQL/transaction vẫn dùng JDBC `PreparedStatement` và `Connection` thủ công.

## Lộ trình thực hiện

1. Tách cấu hình JWT khỏi `application.yaml` sang profile `jwt`; cấu hình profile `local` chạy không cần issuer.
2. Tách `CallerIdentityResolver` thành interface và hai implementation theo profile, sửa Controller để không phụ thuộc class `Jwt`.
3. Đổi `SecurityConfig` thành cấu hình theo profile và chặn mọi request khi không có cấu hình hợp lệ.
4. Kiểm thử startup + POST credit trên `home,local` (không token), và kiểm thử `home,jwt` (không token phải bị từ chối, token đúng scope mới được chấp nhận). Kiểm tra idempotency dùng callerId cố định qua retry.

## Ghi chú

- `local` nghĩa là **bỏ xác thực để phát triển**, không phải cơ chế xác thực an toàn.
- Không nhận `callerId` từ `X-Caller-Id` tùy ý ở môi trường thật vì client có thể giả mạo caller, phá ranh giới khóa idempotency.
- Nếu dùng Spring Boot 4 hoặc phiên bản khác, cần kiểm tra cấu hình `JwtDecoder` và `SecurityFilterChain` theo đúng dependency thực tế của `pom.xml` trước khi build.
