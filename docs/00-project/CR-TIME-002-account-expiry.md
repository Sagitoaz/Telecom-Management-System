# CR-TIME-002 – Kiểm hạn dưới khóa trước tác động tài chính

**Trạng thái:** Approved, chủ dự án xác nhận26/10/2026: “Đổi sang kiểm giờ DB dưới khóa trước cập nhật; lập CR”. Thay yêu cầu Q34=B commit vật lý trước hạn; giữ dấuB trong phiếu làm lịch sử.

## Quyết định

Trong transaction cập nhật tiền, khóa đúng account trước, lấy giờDB và kiểm ngay trước mutation. Nếu account không ACTIVE hoặc `expires_at IS NOT NULL AND expires_at <= eligibilityAt` thì từ chối, không thay số dư/ledger thành công. Nếu còn hạn tại mốc kiểm được lưu, có thể commit sau expiresAt; không promise physical commit deadline. MAIN không hạn không bị làm hết hạn ngầm.

Ví dụ hết00:00, lệnh chờ khóa đến00:00:01 → reject dù HTTP tới sớm. Nếu kiểm dưới khóa23:59:59 còn hạn, transaction có thể commit00:00:01 và vẫn hợp lệ theo quy tắc mới. `eligibilityAt` và `recordedAt` có thể dùng cùng một mốcDB tại finalization được thiết kế rõ, không lấy từ đồng hồclient.

## Giữ nguyên

Số dư không âm/không tràn/hạn mức, account thuộcsubscriber, loại tài khoản cho phép thao tác, fee chỉ MAIN, cycle/gói hợp lệ cho consumption, request/cycle idempotency, tiền+ledger+outcome nguyên tử. Timeout chưa rõCOMMIT phải tra cứu, không tự rollback giả. Job trễ không kéo dài quyền cho lệnh mới.

## Ảnh hưởng và kiểm chứng

FR-ACC-07, BR-22; UC02/04/05 và auto renewal; API/business error ACCOUNT_EXPIRED; account row lock, ledger eligibility_at/recorded_at; test chờ khóa qua hạn, check trước hạn commit qua hạn, MAIN null expiry, retry/replay. Không thêm compensation/refund. Test/runtime còn NOT_RUN.

Lý do bỏ hứa deadline tuyệt đối: các câu COMMIT không có predicate hoàn tất theo expiresAt; check trướcCOMMIT không đặt được upper bound I/O. [MySQL transaction statements](https://dev.mysql.com/doc/refman/8.0/en/commit.html).
