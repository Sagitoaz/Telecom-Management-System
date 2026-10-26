# Kế hoạch đồng bộ bộ thiết kế backend ngày 26/10/2026

**Phạm vi được giao:** Chủ dự án đã điền phiếu16 và yêu cầu rà soát, cập nhật, mô tả chi tiết kèm UML đầy đủ cho backend. Chỉ biên soạn tài liệu/PlantUML/SVG/OpenAPI, không triển khai Java/migration/runtime và không tự nghiệm thu checkpoint.

## Đầu vào và phân biệt hiệu lực

Phiếu16 v0.5 đã được người dùng điền; sổ quyết định, SRS/UC01 đã duyệt và các xác nhận mới trong chat là nguồn nghiệp vụ. MụcD xác nhận toàn tài liệu vẫn để trống. Ghi quyết định đã chọn và CR thay baseline; phần thiết kế vật lý/API mới là REVIEW, không mặc nhiên được duyệt chỉ vì phiếu đã trả lời.

Ba xác nhận ngày26/10: ngày đầu tính ngày đăng ký, gói30 ngày đăng ký01/10 hết00:00 ngày31/10; mốc báo cáo là thời điểm ghi nhận nghiệp vụ trong transaction cuối cùng đã commit; kiểm hạn tài khoản bằng giờDB dưới khóa trước cập nhật, có thể commit sau hạn. Hai nội dung sau thay Q26B/Q34B gốc bằng CR riêng, giữ lịch sử.

## Các gói sản phẩm cần kiểm tra được

1. Lưu snapshot đầu lượt; trích nguyên lựa chọn/ghi chú, lập bảng tiếp nhận và CR có phạm vi/ngày/người, không sửa đề bài.
2. Đồng bộ SRS và đặc tả UC01–06/jobs: input/output, tiền/hậu điều kiện, luồng chính/nhánh/lỗi, quy tắc thời gian, AC và truy vết; phân biệt đăng ký với tiêu dùng, kỳ hiện tại với thời gian trả trước.
3. Hoàn thiện UML phân tích: Use Case, Domain, State, Sequence cho UC01–06 và tác vụ; không đưa khóa/SQL vào Domain Model. Render nguồn thành SVG tương ứng.
4. Đồng bộ kiến trúc MySQL8.0.46/Java21/modular monolith, module/IdP/broker/cache, C4 và failure boundaries; không thêm CDC vì yêu cầu mốc thời gian đã được làm rõ.
5. Viết thiết kế DB/ERD MySQL, Design Class, OpenAPI toàn hệ thống, ma trận lỗi/security, transaction/idempotency/khóa/race/job/report snapshot. Q14 chỉ giao đề xuất UUID, trình cách cụ thể ở bản REVIEW.
6. Đồng bộ kế hoạch test/hiệu năng/đối soát/vận hành, chỉ mục và Roadmap. Giữ rõ test dự kiến với kết quả thực chạy.
7. Kiểm tra link/ref/schema/UML, hash đề bài và lựa chọn; review độc lập các biên thu phí/hủy/renewal/late reporting/commit timeout. Ghi lỗi, sửa và chạy lại kiểm tra bị ảnh hưởng; bàn giao danh mục phần còn chờ duyệt.

## Các điểm bắt buộc kiểm tra chéo

- Cùng requestId không tác động hai lần; timeout không tự là rollback; FAILED thiếu tiền không hồi sinh sau nạp.
- Một phí cho một kỳ; expectedVersion chặn hai mã mới cùng nối một hạn, không thu thêm kỳ ngầm.
- Hủy tới cuối tất cả kỳ đã trả, không hoàn tiền; ngăn gia hạn mới; không mất quyền đã trả.
- Worker gia hạn và JOB-01 phân vai nhưng chung lõi/cycle guard; không chốt khoảng chạy worker thành60s của recovery UC01.
- Ngày lịch và `[start,end)` thống nhất ở SRS/API/UML/test; nhận hủy bền vững không đánh đồng với hết quyền ngay.
- `recordedAt` không mang nghĩa physical commit; chỉ dữ liệu đã commit được báo cáo; snapshot cố định và một nguồn chỉ đóng góp BASE hoặc ADJUSTMENT một lần.
- Cấu hình kỹ thuật chưa được duyệt giữ nhãn đề xuất; không tick M4-DB/M5–M8 hoặc số đo từ việc tạo file.

**Điều kiện bàn giao lượt tài liệu:** Có mô tả và sơ đồ/contract nhất quán, render/validation thật hoặc nêu rõ giới hạn công cụ, danh mục tài liệu và quyết định chờ review; không có kết luận backend đã chạy.

## Xác nhận bổ sung Q24

Chủ dự án đã duyệt [CR-CAN-003](CR-CAN-003-cancellation-receipt.md): sau xác nhận commit ý định hủy, kiểm giờ DB mới; chỉ receipt trước hạn được nhận hủy. Đúng/sau hạn từ chối, kết quả đã nhận hủy commit rồi thì replay khi mất phản hồi. Nghiệp vụ đã chốt, implementation/test còn REVIEW.
