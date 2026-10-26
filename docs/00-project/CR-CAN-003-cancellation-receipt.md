# CR-CAN-003 — Mốc tiếp nhận hủy và khôi phục khi mất phản hồi

**Trạng thái:** Approved về nghiệp vụ ngày 26/10/2026. **Người duyệt:** chủ dự án, xác nhận trực tiếp: “Đồng ý mốc xác nhận sau commit; ghi rõ quy tắc và xử lý mất phản hồi”. Giao thức transaction bên dưới là thiết kế REVIEW để thực hiện quyết định, chưa phải kết quả chạy thử.

## Thay đổi và lý do

Q24=A yêu cầu lưu bền vững trước khi xác nhận hủy. Điều đó chưa xác định cách phân xử khi ghi DB sát cuối thời gian đã trả nhưng kết nối mất, chỉ xác nhận lại được sau hạn. Không được suy ra một yêu cầu đến HTTP trước hạn đã được nhận hủy trước hạn.

Quy tắc mới: backend phải xác nhận DB đã lưu ý định hủy thành công, sau đó kiểm lại giờ DB. Mốc kiểm tra sau xác nhận này là mốc tiếp nhận nghiệp vụ. Chỉ mốc **nhỏ hơn** cuối toàn bộ thời gian đã trả mới được nhận hủy để ngăn gia hạn. Đúng hạn hoặc sau hạn bị từ chối, không tự chuyển yêu cầu sang kỳ mới. Phạm vi thời gian đã trả theo [CR-CAN-002](CR-CAN-002-prepaid-cancellation.md).

## Giao thức đề xuất và kết quả quan sát

1. TxA khóa request và bản ghi đăng ký, kiểm phiên bản rồi lưu ý định `PENDING`. Commit của TxA phải được xác nhận. Trong lúc ý định đang chờ kết luận, cả gia hạn tự động và mua nối thêm đều phải dừng trên đăng ký đó.
2. TxB khóa lại request/đăng ký và lấy giờ DB mới `receiptCheckedAt`. Nếu phiên bản vẫn hợp lệ và `receiptCheckedAt < observedCoverageEnd`, ghi `ACCEPTED`, kết quả request `SUCCEEDED`, đăng ký `PENDING_CANCEL`, hiệu lực hủy tại cuối thời gian đã trả. Trả thành công sau khi TxB commit được xác nhận. TxB có thể commit sau hạn; quy tắc dùng mốc kiểm tra, không hứa deadline commit vật lý.
3. Nếu chưa có kết quả được chốt và mốc kiểm tra đã đúng hoặc quá hạn, ghi `REJECTED`, request `FAILED` với `CANCELLATION_RECEIVED_TOO_LATE`, gỡ chặn chờ hủy. Không hoàn tiền, không hủy kỳ mới ngầm. Gia hạn sau đó theo Q25=B và các điều kiện hiện hành. Phiên bản thay đổi thì trả xung đột, không tự đổi mục tiêu.
4. Nếu mất phản hồi commit, dùng kết nối mới để tra cứu và khóa lại bản ghi. Chưa biết commit thành công hay chưa thì giữ quan sát `PROCESSING`, không đoán rollback, không tạo tác động mới bằng mã khác. Khôi phục chưa có kết quả nhận hủy phải kiểm giờ mới; không dùng giờ HTTP hoặc timestamp ghi ý định cũ để lùi mốc tiếp nhận.
5. Nếu TxB đã chốt `ACCEPTED` nhưng HTTP bị mất, gửi lại **cùng requestId và nội dung** trả đúng kết quả đã lưu, dù lúc tra lại đã qua hạn. Không kiểm thời hạn lại để biến thành công cũ thành thất bại. Mã giống nhưng nội dung khác vẫn `IDEMPOTENCY_CONFLICT`.

Ví dụ: thời gian đã trả kết thúc 00:00. TxA được xác nhận lúc 23:59:58, kiểm giờ DB trong TxB lúc 23:59:59 thì đủ điều kiện nhận hủy. Nếu mất kết nối và chỉ có thể xác nhận/kiểm giờ lúc 00:00:02, chưa có kết quả nhận hủy được chốt trước đó, yêu cầu bị từ chối do trễ. Nếu kết quả nhận hủy đã commit rồi nhưng chỉ mất HTTP, tra lại trả thành công cũ.

## Ảnh hưởng và bằng chứng phải có

Đồng bộ SRS, UC03, state/sequence hủy, worker/JOB-01, schema ý định hủy, OpenAPI, concurrency và test. Kiểm thử bắt buộc: sát hạn/đúng hạn/sau hạn; crash sau TxA; commit không rõ; TxB thành công mất HTTP; chặn cả manual/auto khi pending; giải phóng chặn khi từ chối; phiên bản đổi và hai request cạnh tranh. Chưa có kết quả runtime trong đợt biên soạn này.
