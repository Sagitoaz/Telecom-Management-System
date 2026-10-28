> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# CR-TIME-001 – Báo cáo theo mốc ghi nhận của giao dịch đã commit

**Trạng thái:** Approved, chủ dự án xác nhận trực tiếp26/10/2026: “Đổi sang mốc ghi nhận nghiệp vụ của transaction đã commit; lập CR”. Thay Q26=B trong file nguồn ở phạm vi mốc lọc, không sửa dấu chọn lịch sử.

## Lý do và quyết định

Yêu cầu cũ “thời điểm DB hoàn tất durable commit” không có sẵn một cột SQL có thể chứng minh chính xác trên stack hiện hành. Timestamp viết trướcCOMMIT không phải thời điểm hoàn tất; timestamp binlog cũng không tự chứng minh fsync/InnoDB completion. Không thêm CDC chỉ để đổi tên một timestamp không đáp ứng nghĩa cũ.

Ledger lưu `recorded_at DATETIME(6) UTC` lấy từ giờDB trong transaction nghiệp vụ sau khi có khóa cần thiết, trước mutation/final result; chỉ các dòng transaction cuối cùng commit mới được đọc. Không đặt tên field này là physical `committed_at`. Mốc này xác định thuộc `[X,Y)` và ngày nghiệp vụ Asia/Ho_Chi_Minh. Client không tự backdate mốc bằng serviceOccurredAt.

Ví dụ recordedAt23:59:59 ngày20, commit00:00:01 ngày21 → thuộc ngày20. Lượt báo cáo đã cố định membership không bị thay đổi; lần xem mới có thể thấy dòng đã commit muộn. JOB02 chốt một tập nguồn bền vững, ledger đến muộn của ngày đã chốt trở thành adjustment kỳ sau đúng một lần, không ghi đè kỳ cũ.

## Không thay đổi

Chỉ transaction thành công, không CREDIT trong tiêu dùng, includeFee rõ, OR/AND rõ, snapshot/cursor per caller, nguồn đóng góp không đúp, giữ lịch sử. Đếm TPS vẫn phân biệt success đã commit với recordedAt: không dùng CR này để đếm một transaction commit ngoài cửa sổ thành success trong cửa sổ.

## Ảnh hưởng

FR-RPT-01/02, BR-21/22, UC06/JOB02; ledger/schema/indexes; API tên from/to/recordedAt; report query/membership/contribution; test ngày qua UTC, transaction ghi trước ranh giới commit sau ranh giới, late adjustment và snapshot. Các kiểm chứng runtime còn NOT_RUN.

Nguồn kỹ thuật về giới hạn suy luận: [MySQL binary log](https://dev.mysql.com/doc/refman/8.0/en/binary-log.html). Đây là quyết định nghiệp vụ mới của chủ dự án, không phải chứng minh cột recordedAt bằng durable completion.
