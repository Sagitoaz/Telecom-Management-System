# Đặc tả khung hai job nghiệp vụ

**0.2 — DRAFT, 18/10/2026. P1.7.** Nguồn: DEC-06/07, DEC-REQ-27/29/30/31, FR-JOB-01–04, FR-RPT-04.
Scheduler thuộc nội bộ. Hai job có lịch/timezone riêng cấu hình, mặc định hàng ngày tại giờ cố định và cho chạy thủ công; đã chọn Asia/Ho_Chi_Minh, JOB-01 00:05, JOB-02 00:30 (DEC-PRE-19).

| Nội dung | JOB-01 Hết hạn | JOB-02 Tổng hợp tiêu dùng |
|---|---|---|
| Đầu vào | Mốc xử lý, đăng ký gói/tài khoản đến hạn | Kỳ báo cáo, giao dịch và điều chỉnh |
| Kết quả | Trạng thái/soft delete; giữ lịch sử/danh mục gói | Lưu bảng tổng hợp, tùy chọn xuất file |
| Ràng buộc | Tài khoản hết hạn chặn giao dịch mới, giữ số dư | Một kỳ chốt một lần; dữ liệu muộn điều chỉnh kỳ sau |
| Rủi ro | Đua với gia hạn/hủy/cộng/trừ, chạy lại sau lỗi | Đúp tổng hợp/đúp file, cutoff và dữ liệu muộn |
| Còn mở | OQ-07/09/11 | OQ-03/13 |

## Luồng nghiệp vụ dự kiến

JOB-01: xác định đối tượng đến hạn theo quy tắc chu kỳ đã duyệt → áp dụng quy tắc hủy nhận trước cuối kỳ ngăn gia hạn, chờ hủy không nối kỳ, thiếu tiền hết hiệu lực; kỹ thuật thứ tự còn thiết kế → cập nhật trạng thái nhất quán → lưu kết quả để khôi phục. Chưa chọn tự gia hạn tách task hay nằm trong job; không mặc định mọi gói đến hạn đều hết hiệu lực nếu đang đủ điều kiện gia hạn.

JOB-02: xác định kỳ/cutoff → lấy giao dịch đủ điều kiện → tổng hợp → lưu/chốt kỳ → xuất file nếu bật. Khi chạy lại không nhân đôi; dữ liệu muộn không sửa ngầm kỳ đã chốt. Khóa kỳ, định danh điều chỉnh, schema bảng và cơ chế xuất file chờ Phase 4.

## Tiêu chí kiểm chứng đề xuất

- TC-JOB-01: chạy JOB-01 hai lần không mất lịch sử/số dư, không gia hạn/thu phí trùng.
- TC-JOB-02: lỗi giữa batch rồi khôi phục không bỏ sót hoặc tác động hai lần.
- TC-JOB-03: giao dịch đến đúng biên hết hạn có kết quả xác định theo quy tắc thời gian được duyệt.
- TC-JOB-04: JOB-02 chạy lại cùng kỳ không nhân đôi số liệu; file/table có thể đối chiếu.
- TC-JOB-05: giao dịch muộn được điều chỉnh kỳ sau đúng một lần, truy ra giao dịch nguồn.
- TC-JOB-06: chạy tay/lịch cùng lúc không tạo hai kết quả chốt khác nhau.

**Tác vụ UC01 recovery:** hỗ trợ WAITING khi kho có số và định kỳ khôi phục, không phải job nghiệp vụ thứ ba thay thế hai job trên. Quy tắc ở UC01, kỹ thuật ở concurrency-and-jobs.md.

**Để hoàn tất:** DEC-PRE-13/14/19–21 đã chốt định hướng; còn trạng thái/transaction cutoff/key kỳ/batch và phục hồi; chưa đóng P1.7.


**Đầu ra đã chọn DEC-PRE-20/21:** ngày trước theo timezone trên; count giao dịch thành công/tổng VND theo ngày-thuê bao-gói-loại phí; bảng DB và CSV tùy chọn; dữ liệu muộn ID điều chỉnh ở kỳ sau, kỳ đã chốt không ghi đè. Lịch sử5 năm cấu hình, demo không purge. Recovery UC01 mỗi 60 giây; không kéo dài hiệu lực đến giờ job. Cần xử lý biên gia hạn riêng để không tạo gián đoạn vì JOB-01 chạy00:05.
