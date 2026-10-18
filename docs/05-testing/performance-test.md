# Kế hoạch đo tải và đối soát

**DRAFT 0.2, 18/10/2026 — chưa chạy.** Nguồn: DEC-08–11; DEC-PRE-22–24; SRS NFR-01–11.

## Cấu hình đã được chọn

| Mục | Giá trị |
|---|---|
| Dữ liệu trước đo | Tổng bảng nghiệp vụ ≥500.000; thống kê từng bảng, dữ liệu đủ số dư/gói hợp lệ |
| Phát tải | k6; đối soát riêng từ log request và DB, có thể viết Java |
| Cửa sổ | Warm-up2 phút, đo 10 phút; không tính warm-up vào TPS |
| Success mới | Đăng ký gói ≥30 TPS và debit độc lập ≥30 TPS trong cùng cửa sổ; tổng≥60 |
| Latency | p95≤500ms, p99≤1s theo tập đo phải khai báo |
| Lỗi hệ thống | ≤0,1%; business failure/timeout/replay báo riêng |
| Tranh chấp | 10 cặp thuê bao–account chia đều50% tổng debit attempt kể cả retry;50% còn lại phân rộng |
| Tính đúng | Đối soát từng logical request/transaction/số dư; unresolved>0 chưa đạt |

Số lượng request gửi không tự bằng TPS success mới. Phí đăng ký nội bộ không là debit độc lập, retry không tăng logical success. Cần điều phối cả retry để tỷ lệ attempt thực tế vẫn đúng50%, không chỉ gán50% request ban đầu rồi bỏ qua retry.

## Kịch bản

Correctness nhỏ → contention đủ/thiếu tiền, timeout/retry → mixed workload → broker/cache/recovery failure.
Đối soát mỗi account: cuối=đầu+credit commit−debit commit, gồm phí đúng một lần. Kiểm từng ID để tránh tổng tình cờ cân bằng.
Log attempt/logicalID/caller/loại/nhóm/gửi/hoàn tất/status; overlap phía client không đủ chứng minh DB xử lý tranh chấp thật, cần dấu vết xử lý phía backend/DB phù hợp.
Không bỏ timeout khỏi báo cáo chỉ vì chưa biết kết quả; query/retry có giới hạn rồi unresolved, không tự tính failed/rollback.

## Cần chốt trong protocol thực thi

Máy/DB/network/version, worker/pool, seed/run ID, injection/concurrency/rate; tập request tính p95/p99, mẫu số lỗi hệ thống, retry budget, ranh giới timestamp và logical success commit. Các thông số chưa có không làm mất hiệu lực ngưỡng đã chọn; phải ghi trước đo.
Redis/broker có trong topology đã chọn; ghi trạng thái warm/cold, hit ratio và tác động failure, không tự dùng cache để làm giả số dư/tính đúng.

## Hồ sơ và trạng thái

Lưu config, raw log, count trước đo, histogram, cách tính TPS/50%, lỗi/timeout, reconciliation và kết luận từng tiêu chí. Hiện **chưa có số đo**, không đóng P6/M7.
