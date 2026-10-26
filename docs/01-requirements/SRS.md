# Đặc tả yêu cầu hệ thống (SRS)

**DOC-SRS-001 · v0.7 · 26/10/2026 · REVIEW.** Quyết định nghiệp vụ từ phiếu16 và xác nhận trong chat đã tiếp nhận; bộ tài liệu mới cần chủ dự án rà soát. M1–M4 UC01/T1–T7 được duyệt lịch sử19/10; delta MySQL và artifact mới chưa được tự nghiệm thu. Không có bằng chứng backend/migration/tải chỉ từ tài liệu này.

## 1. Mục tiêu, nguồn và phạm vi

Backend Telecom quản lý Customer, Subscriber, số điện thoại, gói/kỳ trả trước, tài khoản tiền, giao dịch, báo cáo và hai job. Customer–Subscriber1–N; đăng ký gói có thu phí nguyên tử; độc lập với lệnh tiêu dùng. Đấu nối chỉ trong backend. Không xây mạng thật, KYC nội bộ, UI CSKH, mobile, ngân hàng, billing/BI hoàn chỉnh hoặc API hoàn tiền.

Nguồn chuẩn: [đề bài giữ nguyên](../requirement.md), [sổ DEC/CR/OQ](../00-project/clarification-log.md), [phiếu16](../07-business-review/16-PHIEU-DUYET-TONG-HOP.md), [Roadmap](../ROADMAP.md). CR-CAN-002 thay nghĩa hủy cuối kỳ thành cuối mọi thời gian đã trả; CR-TIME-001 dùng recordedAt; CR-TIME-002 dùng eligibility check DB dưới khóa. Quyết định mới thắng bản baseline cũ; lịch sử baseline không bị xóa.

## 2. Mô hình thời gian, quyền và kết quả chung

- Ngày lịch Asia/Ho_Chi_Minh; lưu UTC, API timestamp có offset. Khoảng quyền và báo cáo đều [start,end). Kỳ mới có start thực tế; end=midnight(localDate(start)+durationDays). Gói30ngày 01/10 10:15 hết31/10 00:00; kỳ nối từ midnight end cũ kéo dài durationDays ngày lịch.
- PackageSubscription là đăng ký ổn định; SubscriptionCycle là từng kỳ bất biến. coverageEnd là cuối tất cả kỳ đã trả, khác cuối kỳ đang dùng. ACTIVE/PENDING_CANCEL chỉ có quyền nếu thực sự có kỳ bao phủ thời điểm kiểm.
- recordedAt là giờ DB ghi nghiệp vụ bên trong transaction cuối cùng commit, không phải physical commit completion; eligibilityCheckedAt là giờ DB dưới khóa ngay trước cập nhật tiền. Chỉ ledger đã commit là nguồn báo cáo.
- Hủy chỉ được báo accepted sau khi ý định đã commit bền vững; mốc đến HTTP/precommit không chứng minh nhận trước deadline. CR-CAN-003: TxA ghi PENDING intent, commit được xác nhận; TxB dưới slot/request lấy receiptCheckedAt DB. Cùng version và receipt<observedCoverageEnd mới accepted; receipt>=end chưa có accepted outcome thì FAILED CANCELLATION_RECEIVED_TOO_LATE. PENDING tạm chặn mua/gia hạn. TxB commit qua hạn không hủy receipt sớm; accepted mất phản hồi replay cùngID, không tự retarget/refund.
- Token do Keycloak/IdP ngoài phát; backend kiểm chữ ký/issuer/audience/expiry/scope. Demo chung dữ liệu giả lập, caller có quyền thao tác; kết quả lệnh/snapshot chỉ caller tạo xem. Evidence giấy tờ cần scope tin cậy riêng.
- PROCESSING không là thành công. Chỉ SUCCEEDED sau commit; FAILED nghiệp vụ cuối không hồi sinh sau nạp/đổi kho. Timeout là chưa biết outcome phía client: tra/retry cùng ID. Sau full replay window giữ tombstone; không xóa PROCESSING/WAITING.

## 3. Yêu cầu chức năng

### 3.1 Đấu nối

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-SUB-01 | Bắt buộc họ tên, ngày sinh, loại và số giấy tờ; địa chỉ/nghề nghiệp/liên hệ tùy chọn. Giấy tờ có tham chiếu chứng cứ xác minh từ quy trình ngoài. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-02 | Hệ thống cho tra cứu số AVAILABLE; tra cứu không giữ chỗ. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-03 | Hỗ trợ chọn số AVAILABLE để đấu nối; khi xử lý hoàn tất, API trả ngay thành công hoặc thất bại trong cùng request HTTP. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-04 | Hỗ trợ tự cấp ngẫu nhiên một số AVAILABLE từ kho nạp/sinh bổ sung. Có số và đủ điều kiện thì hoàn tất, phản hồi ngay; hết số áp dụng FR-SUB-10. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-05 | Mỗi số chỉ được cấp thành công cho tối đa một thuê bao ở một thời điểm, kể cả khi yêu cầu chồng lấn. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-06 | Khi số được CSKH chọn đã bị cấp, yêu cầu chọn số trả lỗi nghiệp vụ, không tự đổi sang số khác. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-07 | Dùng lại Customer qua giấy tờ đã xác minh/liên kết; mọi trường đã gửi phải khớp sau chuẩn hóa T1. Khác thì từ chối, không ghi đè/ghép theo tên và ngày sinh. Chứng cứ ngoài chỉ nhận từ caller có quyền. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-SUB-08 | Khi đấu nối thành công, tạo một tài khoản tiền chính với số dư ban đầu bằng 0, không có hạn mặc định. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-09 | **Hybrid:** kết quả đấu nối hoàn tất được trả **đồng bộ** (`SUCCEEDED`/`FAILED`). Chỉ với cấp số tự động hết kho và khách hàng đã đồng ý chờ, backend nhận yêu cầu `WAITING` và xử lý nền. CSKH có thể tự tra cứu theo requestId mà không cần nhập mã thủ công. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-10 | Khi tự cấp hết số: **không đồng ý chờ** → trả `NO_AVAILABLE_NUMBER`, không tạo Subscriber/Account và không lưu WAITING; **đồng ý chờ rõ ràng** → ghi `WAITING` tối đa 24 giờ từ lúc chuyển trạng thái, khi hết hạn `EXPIRED`, có thể gửi yêu cầu mới. Không bắt buộc khách hàng chờ. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-11 | Scope caller+UC01+requestId; canonical payload gồm hồ sơ/mode/số chọn/consent/evidence reference. Cùng nội dung không tác động lại; khác nội dung conflict. Replay 7 ngày từ trạng thái cuối, sau đó tombstone; không dọn PROCESSING/WAITING. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-SUB-12 | Phản hồi nghiệp vụ có mã kết quả/lỗi, thông báo, requestId, dữ liệu kết quả (nếu có) và correlationId phục vụ log. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-13 | Một số từng được cấp có thể được tái sử dụng sau khoảng chờ và quy trình giải phóng hợp lệ; **chưa đủ quy tắc để thiết kế/triển khai tái cấp** cho đến khi bổ sung vòng đời ngừng thuê bao. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-14 | Bổ sung số kích hoạt xử lý WAITING hợp lệ theo FIFO; ưu tiên WAITING trước yêu cầu tự cấp mới khi có số bổ sung. Recovery mỗi 60 giây, không reset hạn 24h; kiểm hạn khi hoàn tất. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-SUB-15 | JWT bất đối xứng từ IdP ngoài backend; kiểm JWKS/chữ ký/issuer/audience/thời hạn/scope/caller ownership. Demo dùng tập dữ liệu giả lập chung; scope evidence riêng. Không phát JWT trong Telecom. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |

### 3.2 Gói và hủy

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-PKG-01 | Giữ giá, số ngày và phiên bản danh mục áp dụng cho từng kỳ đã thu; thay giá không sửa lịch sử. Ngừng bán chỉ chặn khách mới; khách đang có đăng ký được gia hạn theo chính sách nhóm cũ đã cấu hình. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-PKG-02 | Chu kỳ ngày lịch Asia/Ho_Chi_Minh, ngày đăng ký tính ngày1; [start,end). Kỳ mới sau gián đoạn bắt đầu lúc xử lý hợp lệ; end là 00:00 của ngày startDate+durationDays. Còn hạn nối từ coverageEnd. expectedSubscriptionVersion cũ bị conflict, không thu thêm. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-PKG-03 | Thu phí từ tài khoản tiền chính của thuê bao; chỉ xác nhận đăng ký có hiệu lực khi thu phí và đăng ký cùng thành công. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-PKG-04 | Khi lỗi một bước, không còn đăng ký hoạt động hoặc khoản trừ tiền thành công một phần. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-PKG-05 | Hủy được nhận theo receiptCheckedAt DB sau xác nhận TxA commit, trước observedCoverageEnd/cùngversion (CR-CAN-003), chặn mọi nối kỳ/gia hạn mới; hiệu lực cuối TOÀN BỘ thời gian đã trả. Request hủy mới khi PENDING_CANCEL bị từ chối, cùng ID replay. Đúng hạn không là trước hạn. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-PKG-06 | PENDING_CANCEL giữ quyền dùng tới coverageEnd của mọi kỳ đã trả; không hoàn phí. Tới hạn chuyển CANCELLED, không hủy sớm kỳ tương lai đã trả (CR-CAN-002). | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-PKG-07 | Worker và JOB-01 chung quy tắc: tối đa một phí/kỳ; đủ MAIN, không hủy, hợp lệ thì tạo kỳ mới. Xử lý muộn chỉ mua một kỳ từ lúc xử lý hợp lệ, không bù quyền/phí các kỳ bỏ lỡ. FAILED thiếu tiền không tự hồi sinh. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |

### 3.3 Tài khoản và tiền

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-ACC-01 | UC01 tạo MAIN 0 VND không hạn mặc định; phụ được seed/quy trình riêng với loại/quyền cộng-trừ/hạn rõ. Không thêm API tạo phụ. Mỗi lệnh nhắm đúng account thuộc subscriber, không fallback/gộp ví. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-ACC-02 | Cộng tiền VND nguyên dương tối đa10.000.000/lệnh; số dư sau cộng tối đa100.000.000, cấu hình được; vượt giới hạn từ chối toàn bộ. Lưu balanceBefore/After và lịch sử. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-ACC-03 | Trừ tiền từ đúng tài khoản được chỉ định, được phép trừ, còn hạn và đủ số dư; phải có kỳ gói đúng thuê bao đang hiệu lực tại eligibility check. Không có gói thì không trừ. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-ACC-04 | Trừ tiền thiếu tiền không đổi số dư và không ghi giao dịch thành công. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-ACC-05 | Cộng/trừ đồng thời phải giữ đúng số dư và lịch sử đã commit. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-ACC-06 | UC02/04/05 replay30 ngày, UC03 replay7 ngày sau trạng thái cuối; cùng scope caller+operation+requestId/payload không tác động lại, khác payload conflict. Sau cửa sổ giữ tombstone demo. Phí nội bộ không là UC05 độc lập. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-ACC-07 | Dưới khóa tài khoản, lấy giờ DB ngay trước cập nhật để kiểm hạn; checkedAt>=expiresAt từ chối. Check hợp lệ trước hạn được commit sau hạn (CR-TIME-002). Hết hạn giữ số dư/lịch sử; không tự mở lại. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |

### 3.4 Báo cáo

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-RPT-01 | Truy vấn [X,Y) theo recordedAt do DB ghi trong transaction cuối cùng đã commit (CR-TIME-001). OR/AND,includeFee bắt buộc. Tập kết quả cố định/caller/filter; cursor chống sửa, default50 max200, thuê bao không trùng. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-RPT-02 | Tiêu dùng thuộc đúng một kỳ/gói hợp lệ, phí gắn kỳ và loại phí; không nhân nguyên khoản tiền vào nhiều gói. includeFee quyết định có tính PACKAGE_FEE/AUTO_RENEWAL_FEE; CREDIT không là tiêu dùng. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-RPT-03 | Lưu lịch sử nghiệp vụ 5 năm cấu hình; bản demo không chạy purge, giữ dữ liệu phục vụ báo cáo/đối soát. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-RPT-04 | Job tổng hợp tiêu dùng hàng ngày lưu bảng tổng hợp và hỗ trợ xuất file tùy chọn. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |

### 3.5 Tác vụ

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-JOB-01 | JOB-01 00:05 xử lý hết hạn tài khoản/gói, hoàn tất hủy, rà soát bỏ sót; worker gia hạn kiểm tra thường xuyên riêng trong backend. Hai đường dùng chung cycle guard/điều kiện/thu phí nguyên tử, không xử lý trùng. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| FR-JOB-02 | Cập nhật trạng thái/soft delete giữ lịch sử, không xóa danh mục gói. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-JOB-03 | Mặc định Asia/Ho_Chi_Minh: hết hạn 00:05, tổng hợp 00:30 mỗi ngày; từng lịch cấu hình/chạy tay. Không kéo dài quyền dùng chỉ vì job chạy trễ. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-JOB-04 | JOB-02 00:30 tổng hợp ngày trước theo recordedAt; persist tập nguồn/cutoff bền vững lần đầu và dùng lại khi retry. Bản finalized bất biến; nguồn phát hiện muộn bổ sung kỳ sau với ngày gốc, đúng một đóng góp BASE hoặc ADJUSTMENT. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |

### 3.6 Kiểm chứng

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| FR-TST-01 | Công cụ sinh dữ liệu, phát tải, đo yêu cầu đồng thời theo ngưỡng đã chốt. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| FR-TST-02 | Công cụ đối soát từng request/giao dịch đã commit và số dư. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |

### 4. Quy tắc nghiệp vụ

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| BR-01 | Một `Customer` có thể có nhiều `Subscriber`; một `Subscriber` thuộc đúng một `Customer` tại một thời điểm. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-02 | Có cấp số tự động và chọn số khả dụng; chỉ xem số không đồng nghĩa giữ chỗ. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-03 | Một số không được cấp thành công đồng thời cho nhiều thuê bao; yêu cầu chọn số đã bị cấp phải bị từ chối. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-04 | Khi đăng ký gói có thu phí, chỉ có hiệu lực khi cả đăng ký và thu phí thành công; không để lại thành công một phần. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-05 | Hủy có hiệu lực cuối toàn bộ coverage đã trả, không hoàn tiền; ngay sau tiếp nhận bền vững chặn mua thêm/tự gia hạn (CR-CAN-002 thay chi tiết DEC-04). | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-06 | Khi không đủ tiền, trừ tiền thất bại, số dư không thay đổi và không có giao dịch tài chính thành công. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-07 | Lần đăng ký gói và tài khoản hết hạn được vô hiệu hóa bằng trạng thái/soft delete; không xóa vật lý lịch sử cần giữ hoặc định nghĩa gói vì một lần đăng ký hết hạn. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-08 | Ngoài API tra cứu và job hết hạn, phải có job tổng hợp tiêu dùng hàng ngày riêng. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-09 | Khi tính TPS, chỉ đếm nghiệp vụ mới hoàn tất thành công; không đếm retry trả kết quả cũ như nghiệp vụ mới. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-10 | Giao dịch được đối soát theo từng yêu cầu và tác động đã commit; retry không được tạo thêm tác động. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-11 | Bắt buộc họ tên/ngày sinh/loại+số giấy tờ và chứng cứ xác minh ngoài; nhận CCCD/CMND/CMT/hộ chiếu. Caller cần quyền evidence riêng. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-12 | NFC/trim/collapse whitespace; tên so không phân biệt hoa thường, giữ dấu; số giấy tờ giữ số0 đầu. Optional absent khác null/rỗng (null/rỗng lỗi). So mọi trường gửi theo T1 lịch sử. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-13 | Thành công UC01 thì tạo tài khoản chính số dư 0, không hạn mặc định. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-14 | Kho số nhận nạp/sinh số, cấp từ tập xác nhận AVAILABLE; tự động chọn ngẫu nhiên. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-15 | Số từng cấp có thể tái sử dụng sau thời gian chờ; **không coi ASSIGNED đã ngừng là AVAILABLE ngay**. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-16 | Hết kho khi cấp tự động: CSKH **không chọn chờ** thì từ chối ngay; chỉ khi khách hàng đồng ý chờ mới ghi `WAITING` tối đa 24 giờ, không coi WAITING là thành công; quá hạn `EXPIRED`. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-17 | **Hybrid:** trả kết quả cuối trong request HTTP nếu hoàn tất ngay; chỉ nhánh được phép chờ trả `WAITING` rồi xử lý nền. Retry cùng requestId/payload không cấp thêm; payload khác trả `IDEMPOTENCY_CONFLICT`; có API tra cứu và correlationId. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-18 | Mỗi phí đăng ký/gia hạn thu MAIN theo giá/số ngày snapshot của kỳ, nguyên tử với kỳ/kết quả; giá hợp lệ trong hạn mức. Không biến phí thành UC05 lần hai. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-19 | Ngày đầu là ngày đăng ký; 01/10/2026 10:15+07 gói30ngày hết31/10/2026 00:00+07. Nối liên tục từ coverageEnd; sau gián đoạn bắt đầu lúc xử lý hợp lệ; không grace/quyền ngược. Hủy hợp lệ chặn nối/gia hạn. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-20 | VND nguyên dương <=10triệu/lệnh, dư<=100triệu, cấu hình và kiểm tràn. Scope caller+operation+ID/canonical, replay UC02/04/05 30ngày, UC03 7ngày; tombstone chống tác động lại. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-21 | [X,Y) theo recordedAt DB của ledger cuối cùng commit; không theo thời gian client hoặc physical commit. OR/AND/includeFee rõ, snapshot thành viên cố định và phân trang an toàn. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-22 | Hết hạn chặn giao dịch mới, giữ số dư; lịch sử 5 năm cấu hình, demo không purge; kỳ chốt một lần, muộn điều chỉnh kỳ sau. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-23 | Asia/Ho_Chi_Minh; JOB-01 00:05, JOB-02 00:30, worker gia hạn thường xuyên (khoảng chạy thiết kế riêng). Quyền hết theo timestamp, không chờ job. Một phí/kỳ cả chạy tay/retry/nhiều instance. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-24 | WAITING FIFO ưu tiên tự cấp mới khi có số bổ sung; recovery 60s, giữ deadline gốc và kiểm hạn hoàn tất. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-25 | Giấy tờ đã xác minh có chứng cứ ngoài; dùng liên kết cũ hoặc tạo Customer mới hợp lệ, không tự ghép khách; thêm giấy tờ khách cũ ngoài UC01. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| BR-26 | JWT ngoài backend, JWKS và kiểm đầy đủ; tập demo chung nhưng caller chỉ tra request/snapshot mình; scope thao tác/report/evidence/job tách biệt. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |
| BR-27 | requestId có scope caller+UC01; canonical payload khác thì conflict; replay đầy đủ 7 ngày sau trạng thái cuối, sau đó tombstone chống tái dùng. | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |

### 5. Yêu cầu phi chức năng

| Mã | Yêu cầu | Truy vết |
|---|---|---|
| NFR-01 | Quy mô dữ liệu | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-02 | Thông lượng đăng ký | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-03 | Thông lượng trừ tiền | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-04 | Tranh chấp đồng thời | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-05 | Tính toàn vẹn dữ liệu | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-06 | Tính nguyên tử nghiệp vụ | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-07 | Đối soát, retry và timeout | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-08 | Bảo toàn lịch sử | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-09 | Bảo mật tích hợp | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-10 | Khả năng vận hành | Baseline DEC/CR; chi tiết trong UC và kế hoạch test. |
| NFR-11 | Warmup120s, đo600s cùng cửa sổ; TPS mới commit/600; latency từng attempt có phản hồi gồm retry/lỗi: p95<=500ms,p99<=1s. Lỗi hệ thống/mọi attempt đăng ký+debit <=0,1%, mất phản hồi tính lỗi; status/health ngoài mẫu. Báo thêm tổng latency logical; unresolved=0 mới đối soát đạt. | Phiếu16 + xác nhận26/10; xem UC/AC và sổ DEC/CR. |

## 6. Giao diện và môi trường

REST JSON có requestId/correlationId, lỗi có mã ổn định, không lộ PII/token. UC01 giữ ba API đã duyệt; UC02–05 có API tra chung operation+requestId. Read hỗ trợ danh mục gói/đăng ký hiện tại trả version để caller không đoán expectedVersion. Admin job trigger/status có scope riêng; CSKH thường không được chạy job. [OpenAPI REVIEW](../04-design/openapi.yaml) mô tả DTO/HTTP cụ thể, không coi là API đã chạy.

Java21/Spring Boot modular monolith; MySQL8.0.46 InnoDB, READ COMMITTED cho ghi có khóa/ràng buộc, utf8mb4, UTC DATETIME(6). UUID physical representation do agent đề xuất để duyệt, không coi Q14=C duyệt CHAR/BINARY. Nhà Compose MySQL/RabbitMQ/Redis/Keycloak; office đọc/code/unit không DB. RabbitMQ chỉ tín hiệu kho bổ sung; Redis cache danh mục, không quyết tiền/cấp số/idempotency.

## 7. Danh mục đặc tả và truy vết nghiệm thu

| Luồng | FR/BR | Đặc tả/AC |
|---|---|---|
| UC01 đấu nối | FR-SUB-01..15; BR-01..03/11..17/24..27 | [UC01](use-cases/UC01-Subscriber-Activation.md), AC-SUB-01..27 giữ mã |
| UC02 đăng ký có phí | FR-PKG-01..04/07; BR-04/18..20 | [UC02](use-cases/UC02-Package-Subscription.md), AC-PKG-01..12 |
| UC03 hủy | FR-PKG-05/06; BR-05/19 | [UC03](use-cases/UC03-Package-Cancellation.md), AC-CAN-01..10 |
| UC04 cộng | FR-ACC-01/02/05..07; BR-20/22 | [UC04](use-cases/UC04-Account-Credit.md), AC-CRE-01..10 |
| UC05 trừ | FR-ACC-03..07; BR-06/20/22 | [UC05](use-cases/UC05-Account-Debit.md), AC-DEB-01..12 |
| UC06 query | FR-RPT-01..03; BR-21/22 | [UC06](use-cases/UC06-Consumption-Query.md), AC-RPT-01..12 |
| Worker/JOB-01/02 | FR-JOB-01..04; FR-RPT-04; BR-23 | [scheduled-jobs](scheduled-jobs.md), AC-JOB-01..14 |
| Hiệu năng/đối soát | FR-TST-01/02; NFR-01..11 | [test plan](../05-testing/test-plan.md), [performance](../05-testing/performance-test.md) |

Mỗi AC là kiểm chứng cần triển khai; không tuyên bố test pass. Đối soát mỗi operation/ledger và dư cuối=dư đầu+credit−debit, tính phí đúng một lần; unresolved>0 chưa đạt. Kiểm retry, timeout trước/sau commit, deadlock rollback toàn bộ, 2caller, 2requestId cùng version, hủy/gia hạn, trả trước và báo cáo muộn.

## 8. Phần còn mở và quyền phê duyệt

| Mục | Mốc xử lý |
|---|---|
| Delta vật lý MySQL, UUID/collation/khóa/retry, implementation giao thức receipt CR-CAN-003 | Trình M4-DB/thiết kế trước implementation; test MySQL thật trước nghiệm thu |
| Issuer/JWKS/claims/scopes, seed phụ có expiry/quyền, demo catalog/grandfather policy | Cấu hình/test trước API thật từng UC |
| Khoảng chạy worker, batch/budget/retry, snapshot retention/cursor secret | Đề xuất cấu hình kỹ thuật có review; không lấy recovery60s làm worker interval |
| Quy trình ngừng/tái cấp số, archive/purge sau demo | OQ tương ứng trước mở phạm vi/deployment thực; demo không purge |
| Test tích hợp/tải/backup/restore | P5–P7 thực thi, chưa có số đo trong tài liệu |

Không tái hỏi lựa chọn đã chốt, không đóng toàn bộ OQ vì chỉ một phần được giải quyết. SectionD phiếu16 trống không là nghiệm thu artifact mới. Lịch sử M0–M4 UC01 và CR/DEC xem sổ quyết định; bản SRS0.7 hiện REVIEW.
