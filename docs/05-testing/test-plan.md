# Kế hoạch kiểm thử

**DRAFT 0.2, 18/10/2026 — P6.1 chuẩn bị; chưa thực thi.**
Nguồn: SRS NFR-01–11, UC01 AC-SUB-01..27, DEC-08–11.

## Phạm vi và tầng kiểm thử

- Unit: quy tắc consent/hạn, hồ sơ đã xác minh, điều kiện tiền/chu kỳ; không dùng unit test thay kiểm tra transaction DB.
- Integration trên PostgreSQL thật: ràng buộc cấp số, requestId, atomic commit/rollback, job/restart.
- API: JWT/quyền, input/output/lỗi theo hợp đồng sau M4.
- Concurrency: hai số chọn cùng số, retry/worker đua, tiền đồng thời, job chạm biên chu kỳ.
- System/performance: dữ liệu ≥500K tổng, hỗn hợp ≥30/30 TPS thành công mới, 50% debit tranh chấp, đối soát từng request.

| Nhóm | Nguồn | Dữ liệu / tình huống | Bằng chứng phải lưu |
|---|---|---|---|
| UC01 | AC-SUB-01–27 | Customer cũ/mới, hồ sơ sai, số đủ/hết/chọn trùng, 24h, retry/timeout | Test ID → AC, response, DB outcome |
| Gói/hủy | FR-PKG-01–07 | Đủ/thiếu tiền, hủy/gia hạn, thu phí lỗi | Gói và tiền cùng commit hoặc cùng không thành công |
| Tiền | FR-ACC-01–07 | Số dư biên, tài khoản hết hạn, debit/credit/retry đua | Ledger và số dư trước/sau, request outcome |
| Báo cáo/job | FR-RPT/JOB | OR/AND, phí, cutoff, dữ liệu muộn, chạy lại | Bảng/file/kỳ, giao dịch nguồn |
| Bảo mật | NFR-09 | Token lỗi, sai caller/thiếu quyền tra cứu | Bị từ chối, không có side effect/PII thừa |

## Dữ liệu, môi trường và kết luận

Lưu version nguồn, DB/JDK/tool, CPU/RAM/OS, cấu hình pool/worker/transaction, seed cố định, timezone và thời gian chạy. Bộ dữ liệu có đủ số dư/gói hợp lệ để đo success mới, không làm đẹp TPS bằng replay.
Từng case có ID, precondition, bước, expected, actual, log và trạng thái chưa chạy/đạt/chưa đạt.
Chưa có kết quả. Chỉ kết luận đạt khi có log mới và truy vết tới yêu cầu; request timeout chưa xác định phải để unresolved.

## Đối soát

Với mỗi tài khoản: số dư cuối = đầu + credit đã commit − debit đã commit (gồm phí đã commit đúng một lần). Ngoài tổng tiền, đối chiếu từng requestId → trạng thái/kết quả/giao dịch; success có đúng tác động, failure không có tác động trái phép, retry không thêm tác động.
UC01 kiểm số ASSIGNED không trùng, SUCCEEDED có đủ Customer/Subscriber/Account; WAITING/EXPIRED không có thành công mồ côi.
Xem [kế hoạch tải](performance-test.md); DEC-PRE-22–24 đã chốt ngưỡng/công cụ; protocol môi trường/denominator còn xác định trước thực thi, không chặn tài liệu UC01.


**Protocol đã chọn:** warm-up 2 phút/đo 10, p95≤500ms/p99≤1s/lỗi hệ thống≤0,1%; k6; 10 cặp chia đều50% debit attempts kể cả retry, phí nội bộ ngoài mẫu số. Chỉ logical success mới tính TPS. Bổ sung AC20–27 và broker confirm/redelivery/mất tín hiệu, Redis stale/failure/fallback; không dùng kết quả cache để kết luận số dư/cấp số. Xem performance-test.md; chưa chạy.
