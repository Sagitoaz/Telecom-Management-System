# Hợp đồng UC01 — bản rà soát trước OpenAPI

**DRAFT 0.2, 18/10/2026 — P4.3 chưa hoàn tất.** Nguồn: SRS0.6/UC01 0.5, DEC-PRE-04–07/11. Hướng endpoint/status đã chọn; schema/HTTP đầy đủ còn P4.

| Endpoint đã chọn | Nội dung | Trạng thái |
|---|---|---|
| POST /api/v1/activations | Hồ sơ, chứng cứ ngoài, requestId, mode, số chọn khi cần, consent | Success mới 201; WAITING 202; conflict 409 |
| GET /api/v1/activations/{requestId} | Tra cứu trong scope caller+UC01 | HTTP success/not-found/in-progress/tombstone còn chốt |
| GET /api/v1/phone-numbers | Tra số AVAILABLE, không giữ chỗ | Phân trang/filter/status/schema còn chốt |

JWT bất đối xứng/JWKS, IdP local ngoài Telecom; kiểm issuer/audience/expiry/scope và ownership. Không cho caller khác tra request bằng cách biết ID.
Canonical payload gồm hồ sơ/mode/số chọn/consent, bỏ JWT/correlationId/thứ tự JSON; normalize/absent/null/default cụ thể chưa chốt.
Replay đầy đủ 7 ngày sau trạng thái cuối, không dọn đang xử lý/WAITING. Sau đó tombstone/fingerprint chống tái dùng; không hứa phản hồi đầy đủ cũ.

## Ma trận nghiệp vụ phải được ánh xạ HTTP

SUCCEEDED chỉ sau commit nhất quán; selected unavailable không đổi số/chờ; tự cấp rỗng không consent trả NO_AVAILABLE_NUMBER; có consent WAITING≤24h; quá hạn EXPIRED. Cùng scope ID khác canonical payload conflict. Retry cùng nội dung không tác động thêm; timeout không ngầm WAITING hay rollback.

## Phần còn thiếu trước OpenAPI/M4

Các lỗi validation/security/business/system, replay status, in-progress, payload purged/tombstone, not-found và ownership; schema oneOf selected/automatic, consent mặc định không chờ; kích thước/format dữ liệu, polling/timeout, correlationId và ví dụ.
Q11 chỉ duyệt 201/202/409 theo hướng đã nêu, không tự áp 201 cho mọi replay hay 409 cho mọi failure.
Chưa tạo openapi.yaml hoặc migration/API runtime chỉ để khớp danh mục. Validate OpenAPI và truy vết 27 AC trước đóng P4.3.
