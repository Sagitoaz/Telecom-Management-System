# Kiểm chứng bộ tài liệu sau nhận Q01–Q24

**Ngày:** 18/10/2026. **Bản:** log1.5, SRS0.6, UC01 0.5. Tài liệu chờ chủ dự án đọc rồi trả lời Q25.

## Phạm vi kiểm tra

Kiểm tra file/Markdown, quyết định được lưu, tính đầy đủ mã FR/BR/NFR/AC, giữ nguyên lựa chọn người dùng, Q25/M1–M8, nguồn UML và SVG render.
Không sửa src/pom.xml; không chạy build/test nghiệp vụ hoặc đo TPS trong lượt này. Kiến trúc/công cụ được duyệt không phải bằng chứng M3/M4 đạt.

## Nguồn quyết định và xóa phiếu cũ

Đã nhận24 câu, mỗi câu một ô; log mục13 lưu nguyên văn đáp án/ghi chú. Q25 vẫn chưa chọn.
Hai phiếu lịch sử REV-SRS-001/REV-UC01-002 đã xóa theo yêu cầu; log mục8–12 còn lịch sử và31+5 quyết định đầy đủ. Không có link hoạt động cần mở phiếu đã xóa.
Broker/cache đã được làm rõ bằng xác nhận trong phiên; ADR-001 ghi phạm vi chủ dự án giao chọn RabbitMQ/Redis.

## Kiểm tra UML

Dùng PlantUML1.2026.9: -checkonly và -tsvg cho docs/02-analysis, sequence-diagrams, state-diagrams và docs/03-architecture/c4-diagrams. Nguồn có6 file .puml, C4 có2 diagram nên có7 SVG. SVG phải parse XML và không chứa Syntax Error. Mỗi lần sửa nguồn phải render lại.

## Bảo toàn dữ liệu

requirement.md SHA-256 đầu lượt: f6667c88e9c403146d2dd2b31df75143af6a1cbb5876849fe896e28151cae532. Không sửa byte nào. Sai khác đã có với Git HEAD là chữ “dịch vụ t=>” trong HEAD và “dịch vụ =>” trong file người dùng; không tự sửa lại.
Không stage/commit/push hoặc đổi branch. Working tree có thay đổi người dùng từ trước, không hoàn tác.

## Giới hạn và phần chờ duyệt

SRS0.6/UC01 0.5 có27 AC, Q25/M1 chưa duyệt; P2–4 còn DRAFT, chưa có schema/OpenAPI/Design Class và thuật toán giao dịch hoàn chỉnh. Retention tiền 30 ngày và hạn mức10triệu/100triệu chỉ là đề xuất R2/R3, chưa áp dụng như chuẩn đã duyệt.
Xem [review-guide](00-project/review-guide.md) để đọc theo thứ tự, [roadmap](ROADMAP.md) để xác định bước tiếp theo.


## Kết quả thực chạy trong lượt này

- PlantUML1.2026.9: checkonly và render SVG exit0; 6 nguồn/7 SVG, XML hợp lệ.
- Đối chiếu100 liên kết Markdown:0 link hỏng.
- Giữ nguyên24 đáp án và ghi chú người dùng sau chuẩn hóa line ending; Q25 trống; đủ31+5 quyết định lịch sử và24 quyết định mới.
- SRS giữ đầy đủ mã FR/BR/NFR; UC01 đủ AC-SUB-01–27, không trùng mã.
- Requirement hash giữ nguyên; thư mục phiếu cũ đã xóa; M1–M8 vẫn unchecked.
- git diff --check không báo lỗi; không có thay đổi src/pom.xml trong diff. Chưa có build/test nghiệp vụ hoặc đo tải mới.
