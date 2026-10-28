> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# UC06 — Tra cứu thuê bao có tiêu dùng

**DOC-UC06 · v0.1 · 26/10/2026 · REVIEW.** Nghiệp vụ theo phiếu16/xác nhận26/10; DTO/thiết kế mới cần rà soát.

## Mục tiêu, tác nhân, tiền điều kiện và dữ liệu

Ứng dụng báo cáo có scope riêng, dữ liệu giả lập chung; snapshot/result vẫn caller-owned. Input lần đầu có queryRequestId (khóa kỹ thuật caller-scoped chống tạo snapshot khác khi mất phản hồi), X,Y timestamps có offset (X<Y), danh sách packageIds, matchMode OR hoặc AND, includeFee bắt buộc, pageSize(default50,max200), cursor nếu tiếp trang. GET /api/v1/reports/consumption/subscribers. Output queryId/snapshot version, normalized filters, danh sách subscriber không trùng, nextCursor/hasMore, createdAt; số tiền/count nếu contract khai báo phải cùng snapshot.

OR: có ít nhất một transaction đủ điều kiện của bất kỳ gói đã chọn. AND: cùng subscriber có transaction đủ điều kiện cho TỪNG gói trong danh sách ở khoảng đó; không yêu cầu một ledger gắn nhiều gói. SERVICE_CONSUMPTION là tiêu dùng; chỉ thêm PACKAGE_FEE/AUTO_RENEWAL_FEE nếu includeFee=true, không CREDIT.

## Luồng chính

1. Kiểm JWT/report scope, caller và queryRequestId/filter X<Y/matchMode/includeFee/pageSize.
2. Với query mới, chọn ledger đã commit có recordedAt trong [X,Y), attribution phù hợp và loại phí theo filter.
3. Resolve caller+queryRequestId và canonical filters: đã có thì dùng query/cutoff/membership gốc, khác filter409; còn materializing trả202 với queryId, retry cùng key/queryId. Tạo tập thành viên snapshot cố định từ một statement snapshot nhất quán; persist query/filter/caller/version và thành viên đủ điều kiện, không chỉ cutoff timestamp.
4. Lấy page ổn định theo subscriberId, mỗi subscriber đúng một lần; cursor chống sửa và bind query/filter/caller/version.
5. Commit dữ liệu snapshot ngắn trước trả; không giữ transaction trong lúc người đọc trang.
6. Trang tiếp dùng tập đã cố định; muốn dữ liệu mới phải query mới.

## Luồng thay thế

A1 Kết quả rỗng:200 danh sách rỗng, không tạo kết quả giả.

A2 Ledger recordedAt23:59:59 cuối20/10 nhưng commit00:00:01 ngày21: thuộc20/10 nếu snapshot đọc sau commit; snapshot cũ giữ nguyên không tự thêm.

A3 Ledger mới xuất hiện giữa page1/page2 không làm tập cũ đổi; query mới mới thấy.

A4 includeFee=false loại phí, true thêm đúng một lần theo attribution.

A5 Snapshot/cursor hết retention cấu hình: trả lỗi yêu cầu query mới, không tiếp tục trên tập khác.

## Ngoại lệ

E1 X>=Y/thiếu OR-AND/includeFee, pageSize vượt200: validation error, không tự default filter.

E2 Cursor sai chữ ký/sai caller/filter/version: từ chối.

E3 Snapshot thiếu/chưa READY/hết hạn: trả trạng thái/lỗi contract, không fallback live query.

E4 Lỗi giữa materialize và publish: không trả snapshot thiếu; retry tạo lại có guard/recovery theo design.

E5 Không đúng report scope:403, không lộ PII không cần thiết.

## Hậu điều kiện

Mỗi lượt xem trả tập cố định, không trùng/bỏ member giữa trang; không thay tiền hay kỳ. Query không là JOB-02 và không sửa báo cáo ngày đã finalized. recordedAt không phải physical commit time hoặc client service time.

## Bảo mật, lưu giữ và kiểm chứng

Snapshot retention/cursor secret/expiry/budget là cấu hình đề xuất kỹ thuật cần review, không phải ngày đã chọn trong phiếu. Lịch sử tài chính5năm cấu hình, demo không purge, khác full result idempotency. Query dùng queryRequestId/queryId riêng để phục hồi snapshot, không dùng registry tác động tiền UC02–05; không thêm tác động tiền. Materialize membership dưới READ COMMITTED phải lấy nguồn cùng một SELECT snapshot, tránh hai lần đọc không hạn chế nhìn hai tập khác nhau.

Race test: commit qua midnight; giao dịch tới muộn; page2 sau thêm/xóa logical eligibility; AND/OR,fee filter/caller forged cursor, restart materialization, snapshot expired. Mọi test dự kiến chưa chạy. [SRS](../SRS.md), [OpenAPI](../../04-design/openapi.yaml), [report design](../../04-design/concurrency-and-jobs.md) REVIEW; toàn bộ bản mới chờ artifact review.

## Tiêu chí nghiệm thu và truy vết

Nguồn: FR-RPT-01..03; BR-21/22; Q10/12/26/27/28. Mỗi AC cần test riêng hoặc tổ hợp có đối soát dữ liệu.

| Mã | Điều kiện cần kiểm chứng | Yêu cầu |
|---|---|---|
| AC-RPT-01 | Chỉ committed ledger trong [X,Y) recordedAt. | FR-RPT-01..03 |
| AC-RPT-02 | RecordedAt trước midnight/commit sau vẫn thuộc ngày ghi nhận. | FR-RPT-01..03 |
| AC-RPT-03 | OR hợp bất kỳ gói; AND đủ mọi gói theo subscriber. | FR-RPT-01..03 |
| AC-RPT-04 | includeFee bắt buộc và đúng loại, không CREDIT. | FR-RPT-01..03 |
| AC-RPT-05 | Mỗi subscriber một lần, không nhân tiền vào hai gói. | FR-RPT-01..03 |
| AC-RPT-06 | Default50,max200 được kiểm. | FR-RPT-01..03 |
| AC-RPT-07 | Page2 cùng tập bất biến dù có dữ liệu mới. | FR-RPT-01..03 |
| AC-RPT-08 | Cursor bind caller/filter/query/version, chống giả. | FR-RPT-01..03 |
| AC-RPT-09 | Caller khác không xem snapshot. | FR-RPT-01..03 |
| AC-RPT-10 | Không giữ transaction suốt đọc nhiều trang. | FR-RPT-01..03 |
| AC-RPT-11 | Snapshot hết hạn yêu cầu query mới, không đổi live tập. | FR-RPT-01..03 |
| AC-RPT-12 | Query không sửa finalized day; late adjustment dành JOB02. | FR-RPT-01..03 |
