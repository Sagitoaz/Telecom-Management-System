> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# CR-CAN-002 – Hủy tại cuối toàn bộ thời gian đã trả

**Trạng thái:** Approved về nghiệp vụ, tiếp nhận26/10/2026 từ Q22=A của chủ dự án. **Người duyệt:** Chủ dự án. Không phải duyệt implementation/schema/API/test.

## Cũ → mới

DEC-04/BR-05 trước đây ghi hủy cuối chu kỳ hiện tại. Khi khách đã mua trước kỳ tương lai, cách đó chưa giải quyết quyền và tiền đã trả. Nay hủy có hiệu lực ở `coverageEnd`: cuối tất cả kỳ liên tiếp đã thanh toán thành công. Không hoàn phí; giữ quyền tới mốc này. Khi ý định hủy hợp lệ được nhận bền vững, chặn mua nối/tự gia hạn mới. Không xóa cycle/ledger và không cắt mất kỳ tương lai đã trả.

Ví dụ kỳ hiện tại hết00:00 ngày31/10, kỳ trả trước hết00:00 ngày30/11; yêu cầu hủy ngày20/10 được nhận hợp lệ → `cancelEffectiveAt=coverageEnd=30/11 00:00`. Trước mốc còn quyền theo các cycle đã trả, đúng mốc không còn. Dừng trạng thái CANCELLED để phân biệt EXPIRED. Không có nghiệp vụ refund/undo cancellation mới.

## Cạnh tranh và phạm vi

Manual extension đã commit trước nhận hủy thì coverage mới đã trả được bảo vệ. Hủy được nhận hợp lệ trước deadline phải ngăn renewal; không chỉ nhìn giờ HTTP tới. Two requests khác ID hủy lặp theo quy tắc cũ; cùng ID replay không tác động thêm. Mọi race phải có kiểm chứng receipt/slot/cycle trong thiết kế.

Chỉ thay chi tiết “cuối chu kỳ hiện tại” khi có thời gian trả trước; không thay soft delete, quyền sở hữu, thiếu tiền, nguyên tử thu phí hay idempotency. Lịch sử DEC-04 được giữ trong log/archive.

## Đồng bộ và kiểm chứng

SRS FR-PKG-05/06, BR-05/19; UC02/03 và worker/JOB01; Domain/State/Sequence; schema root.coverage_end + cycle lịch sử; API cancelEffectiveAt/expectedVersion; test hủy trước/sau manual extension, hủy cuối paid coverage, không refund và không extra renewal. Test executable còn NOT_RUN.
