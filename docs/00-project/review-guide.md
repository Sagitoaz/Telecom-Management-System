# Hướng dẫn đọc và duyệt bộ tài liệu

**Bản rà soát 18/10/2026 — SRS 0.6 / UC01 0.5. M1 CHƯA DUYỆT.**

Đã tiếp nhận Q01–Q24, giữ nguyên lựa chọn trong phiếu, ghi đầy đủ thành DEC-PRE-01–24 ở clarification log mục 13. Đã làm rõ dùng cả RabbitMQ/Redis theo quyền chọn công cụ chủ dự án giao. Chưa chọn Q25 thay chủ dự án.

## 1. Thứ tự đọc để duyệt M1

| Thứ tự | Tài liệu / phần cần đọc | Bạn cần kiểm tra |
|---|---|---|
| 1 | [Đề bài gốc](../requirement.md) | Nhắc lại yêu cầu gốc; file được giữ nguyên |
| 2 | [SRS 0.6](../01-requirements/SRS.md), mục 2–6 và 9 | Phạm vi, FR/BR/NFR đúng ý; chú ý hồ sơ, giá/chu kỳ/hủy, lịch job và tiêu chí tải mới |
| 3 | [UC01 0.5](../01-requirements/use-cases/UC01-Subscriber-Activation.md), mục 2–8 và 10 | Luồng thành công/lỗi/chờ/retry và AC-SUB-01–27 đúng mong muốn |
| 4 | Các điểm ở mục 2 dưới đây | Xác nhận cách hiểu hoặc ghi sửa; đề xuất chưa được mặc định duyệt |
| 5 | [Phiếu hiện tại](decision-questionnaire.md), Q25 | Chọn A nếu nghiệm thu bản SRS/UC01 hiện tại; chọn B nếu còn muốn sửa/đọc lại. Ghi rõ phần đồng ý/không đồng ý |

Không phải duyệt mọi thiết kế để đóng M1. Nếu sửa đáng kể luồng/AC sau phản hồi thì trình lại phiên bản mới, không dùng Q25 cũ để tự duyệt nội dung đã thay đổi.

## 2. Những chỗ cần bạn chú ý

**R1 — hồ sơ/giấy tờ (cần đọc trước M1):** Q01-B yêu cầu so mọi trường được gửi sau chuẩn hóa; ghi chú thêm “ít nhất 1 trường liên quan giấy tờ tùy thân được gửi”. Bản này vẫn bắt buộc họ tên, ngày sinh, loại+số giấy tờ như baseline. Không hiểu ghi chú là được bỏ loại hoặc số. Khi tra Customer, mọi trường có gửi phải khớp; không gửi trường tùy chọn thì không yêu cầu sửa hồ sơ. Thuật toán normalize/absent/null/default cụ thể chốt ở P4. Nếu bạn muốn giảm trường bắt buộc, cần ghi rõ yêu cầu thay đổi trước M1.

**R2 — retention lệnh tiền (đề xuất, không chặn M1 UC01):** Q15 giao đề xuất một thời hạn cấu hình. Đề xuất 30 ngày sau trạng thái cuối để lưu kết quả đầy đủ; không dọn đang xử lý; sau đó giữ dấu ID/fingerprint/kết quả tóm tắt trong vòng đời lịch sử demo, không tự tái dùng. 30 ngày là đề xuất cho bài tập để có cửa sổ đối soát dài hơn UC01, không phải chuẩn ngành hay yêu cầu pháp lý. Chưa tự ghi thành số ngày đã duyệt. Bạn có thể trả lời “duyệt R2” hoặc ghi thời hạn khác trước UC02/04/05.

**R3 — hạn mức/tài khoản phụ (đề xuất, không chặn M1 UC01):** Q16-A chưa điền số cụ thể. Đề xuất max mỗi lệnh 10.000.000 VND, max số dư 100.000.000 VND, cả hai cấu hình; seed loại dịch vụ phụ theo danh mục được chọn, tài khoản phụ yêu cầu hạn sử dụng nhập rõ khi seed, không tự gán 30 ngày hay tự mở qua API. Không chốt tên dịch vụ/hạn từng loại khi chưa có dữ liệu demo. Kiểm tra tràn số luôn bắt buộc; max tiền chỉ áp dụng khi duyệt. Bạn có thể duyệt/sửa hai con số và danh mục/hạn phụ trước UC02/04/05.

**R4 — hủy/gia hạn (đã chọn hướng, đọc để tránh hiểu nhầm):** Q14-A có cả điều kiện hủy đã được tiếp nhận hợp lệ trước cuối kỳ phải ngăn gia hạn. Không phải “ai commit trước thì thắng” thuần túy. Mốc tiếp nhận bền vững, đúng biên, trường hợp gia hạn xong rồi mới hủy còn thiết kế trước UC03/job; không tự đặt hoàn tiền.

**R5 — đã hết 7 ngày replay UC01:** vẫn giữ dấu ID/fingerprint chống tác động lại. Không cam kết trả lại đầy đủ phản hồi cũ sau cửa sổ; HTTP/kết quả tra cứu sau dọn payload chốt ở P4. Lịch sử Subscriber/Account vẫn giữ, khác retention replay.

## 3. Đọc sau M1, theo đúng thứ tự

1. [Use Case toàn hệ thống](../02-analysis/use-case-diagram.svg) và [UC01](../02-analysis/uc01-use-case-diagram.svg) — rà soát P2.1 trước.
2. [Domain Model](../02-analysis/domain-model.svg), [Sequence UC01](../02-analysis/sequence-diagrams/uc01-activation.svg), [thuyết minh](../02-analysis/analysis.md) — P2.2/P2.4 và M2.
3. [Kiến trúc](../03-architecture/architecture.md), [ADR-001](../03-architecture/adr/ADR-001-foundation.md), C4/module — M3, không tự đạt chỉ vì chọn stack.
4. [DB](../04-design/database-schema.md), [API](../04-design/api-contract-uc01.md), [concurrency](../04-design/concurrency-and-jobs.md), design class — hoàn thiện P4 và M4 rồi mới code UC01.

UC02–06/jobs/test/deployment đã đồng bộ định hướng để đọc sau; chưa cần nghiệm thu mọi UC trước UC01. Xem [roadmap](../ROADMAP.md) về trạng thái và [verification](../verification.md) về kiểm chứng thực tế.

## 4. Việc bạn cần làm ngay

Đọc theo mục 1 → ghi ý kiến R1 và các chỗ muốn sửa → trả lời Q25. R2/R3 có thể trả lời cùng lượt hoặc hoãn đúng mốc. Không cần điền lại 24 câu đã trả lời. Các cấu hình kỹ thuật chưa cụ thể sẽ được trình ở giai đoạn thiết kế, không yêu cầu bạn đoán tên claim/SQL lúc duyệt M1.
