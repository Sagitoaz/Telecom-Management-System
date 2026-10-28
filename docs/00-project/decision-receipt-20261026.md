> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Tiếp nhận quyết định từ phiếu16 và xác nhận ngày26/10/2026

**Mã:** RECEIPT-BE-20261026 · **Người quyết định:** Chủ dự án · **Ngày tiếp nhận:** 26/10/2026. Đây là ghi nhận nội dung đã chọn, không phải nghiệm thu thiết kế/schema/API hay kết quả code/test. MụcD, tên và ngày duyệt toàn tài liệu trong phiếu còn trống.

## 1. Nguồn và bảo toàn lựa chọn

Phiếu nguồn: [16-PHIEU-DUYET-TONG-HOP](../07-business-review/16-PHIEU-DUYET-TONG-HOP.md), SHA-256 lúc tiếp nhận `2e3474b90e37c471510eb2977515d70691bb01dad05409e7940f7e26a07450d5`. Không sửa dấu chọn/ghi chú của người dùng. Q03/Q08/Q17/Q20 chỉ tham chiếu baseline,30 câu khác mỗi câu một đáp án. Nội dung chi tiết dưới lựa chọn (đặc biệt bốn điểm Q32) cũng có hiệu lực theo câu đã chọn, không rút gọn thành chỉ một dòng.

| Câu | Chọn | Nội dung được chọn trong file nguồn | Ghi chú nguyên văn |
|---|---|---|---|
| Q01 | A | **A – Khuyến nghị:** UC02/04/05 giữ full result 30 ngày sau trạng thái cuối, cấu hình được; sau đó giữ tombstone trong demo. UC03 giữ 7 ngày+tombstone như UC01. Không dọn đang xử lý. | Không ghi thêm |
| Q02 | A | **A – Khuyến nghị:** Mỗi lệnh tối đa 10 triệu, số dư tối đa 100 triệu, cấu hình được; ví dụ trên bị từ chối toàn bộ. Giá phí gói cũng phải trong miền tiền hợp lệ. | Không ghi thêm |
| Q03 | Tham chiếu | Mục tham chiếu quyết định đã có, không hỏi chọn lại. | Không ghi thêm |
| Q04 | A | **A – Khuyến nghị:** Một kỳ kế tiếp chỉ thu một lần; nếu thao tác kia đã tạo kỳ đó thì re-evaluate và trả xung đột/trạng thái hiện tại, không tự nối thêm một kỳ nữa. | Không ghi thêm |
| Q05 | A | **A – Khuyến nghị:** Kỳ mới bắt đầu từ lúc đăng ký lại hợp lệ 15/10; không thu ngược thời gian gián đoạn. | Không ghi thêm |
| Q06 | A | **A – Khuyến nghị:** Phân biệt CANCELLED (khách hủy) và EXPIRED (hết hạn); dễ giải thích và thống kê. | Không ghi thêm |
| Q07 | A | **A – Phương án đã chọn:** Worker gia hạn kiểm tra thường xuyên các gói đến hạn tự gia hạn; **JOB-01 vẫn chạy lúc 00:05 hằng ngày**, xử lý hết hạn gói/tài khoản, hoàn tất hủy và rà soát phần bị bỏ sót. Hai đường dùng chung logic kiểm điều kiện/cycle/thu phí nguyên tử, không thu lại kỳ đã thành công. Worker là tác vụ hỗ trợ trong backend, không phải ứng dụng riêng hay một timer cho mỗi thuê bao. JOB-02 tổng hợp báo cáo vẫn 00:30. | A – chủ dự án xác nhận trong chat ngày 25/10/2026; ghi nhận DEC-JOB-001. |
| Q08 | Tham chiếu | Mục tham chiếu quyết định đã có, không hỏi chọn lại. | Không ghi thêm |
| Q09 | A | **A – Khuyến nghị: hỗ trợ cả MAIN và tài khoản phụ, nhưng làm đúng tài khoản được chỉ rõ.** Ví dụ trên **báo GAME không đủ tiền**, cả hai số dư giữ nguyên; không tự lấy MAIN dù MAIN đủ. Lệnh trừ 20.000đ từ GAME được xử lý nếu GAME còn hạn và được phép trừ. Từng loại tài khoản được cấu hình có cho cộng, cho trừ hay cả hai; MAIN không có hạn mặc định. Tài khoản phụ demo được tạo sẵn cùng loại/quyền/hạn dùng rõ ràng, chưa khai báo hạn thì chưa tạo ví đó. Không thêm API mở ví ở câu này. | Không ghi thêm |
| Q10 | A | **A – Khuyến nghị:** Mỗi trang mặc định 50 thuê bao; bên gọi được yêu cầu nhiều hơn nhưng tối đa 200. Dấu chuyển trang được kiểm tra để tránh sửa giả, gắn với người có quyền xem/bộ lọc và tập kết quả ở Q27. | Không ghi thêm |
| Q11 | A | **A – Khuyến nghị: phải có gói đang hiệu lực.** Backend kiểm gói/kỳ sử dụng đúng thuê bao. Chưa đăng ký gói, gói hết hạn hoặc bên gọi chỉ sai gói thì từ chối và không trừ 5.000đ. | Không ghi thêm |
| Q12 | A | **A – Khuyến nghị: giữ lịch 00:30 đã chọn.** Ngay khi mốc/tập dữ liệu để chốt đã được lưu bền vững lần đầu, các lần chạy lại phải dùng đúng nó, kể cả lần trước lỗi và chưa hoàn tất báo cáo. Không lấy một mốc mới mỗi lần thử lại để tránh số liệu thay đổi tùy lúc chạy. Dữ liệu muộn được xử lý riêng theo Q28. | Không ghi thêm |
| Q13 | B | **B:** Dùng MySQL 8.0.46 với InnoDB vì máy nhà đã cài và đang chạy phiên bản này. Khi triển khai/kiểm thử, cần kiểm tra tương thích trên đúng phiên bản đã chọn. | Không ghi thêm |
| Q14 | C | **C:** Tôi giao agent đánh giá và đề xuất cách lưu; phải ghi rõ lựa chọn trong bản thiết kế DB để duyệt, chưa tự coi đã chọn A hoặc B. | Không ghi thêm |
| Q15 | A | **A – Khuyến nghị thử:** Dùng `READ COMMITTED` cho các nghiệp vụ ghi tiền/cấp số có khóa rõ. Hiểu đơn giản: các lần đọc thông thường có thể thấy thay đổi đã được lệnh khác lưu xong giữa các lần đọc. Agent phải thiết kế khóa và kiểm thử tranh chấp; báo cáo nhiều trang cần cơ chế riêng ở Q27. | Không ghi thêm |
| Q16 | A | **A – Khuyến nghị:** Lưu chữ bằng `utf8mb4` để hỗ trợ Unicode; chuẩn hóa đầu vào theo T1 đã duyệt; quy tắc so các mã được ghi rõ. Thời gian lưu dạng `DATETIME(6)` theo UTC, API gửi thời gian kèm múi giờ/độ lệch. Q26/Q34 quyết định lấy mốc thời gian nào, không phải câu này. | Không ghi thêm |
| Q17 | Tham chiếu | Mục tham chiếu quyết định đã có, không hỏi chọn lại. | Không ghi thêm |
| Q18 | A | **A – Khuyến nghị: dựng đủ bộ dịch vụ ở nhà.** MySQL, RabbitMQ, Redis và Keycloak chạy bằng Compose. Backend chạy từ IDE khi phát triển, đóng container lúc bàn giao; kiểm tình trạng từng dịch vụ. | Không ghi thêm |
| Q19 | A | **A – Khuyến nghị:** Đồng ý cách chia dữ liệu ở mức ý tưởng đó. Agent vẫn phải trình bảng/cột/ràng buộc cụ thể cho từng UC; chọn A chưa duyệt sẵn mọi câu SQL hoặc coi migration đã chạy. | Không ghi thêm |
| Q20 | Tham chiếu | Mục tham chiếu quyết định đã có, không hỏi chọn lại. | Không ghi thêm |
| Q21 | B | **B: tính theo ngày trên lịch địa phương.** Bạn cần ghi thêm: ngày đăng ký có tính là ngày đầu không, và hết vào đầu hay cuối ngày nào. Nếu chưa ghi thì agent chưa được tự suy công thức. | ngay dang ky tinh laa ngay dau |
| Q22 | A | **A – Khuyến nghị về độ đơn giản:** Cho dùng tới 30/11 vì đã trả tiền, không hoàn phí; chặn mua thêm và gia hạn tự động. **Đây là đổi quy tắc** “hủy cuối kỳ hiện tại” thành “hủy cuối toàn bộ thời gian đã trả”, cần ghi CR khi bạn chọn. | Không ghi thêm |
| Q23 | A | **A – Khuyến nghị: bảo vệ khi hai lệnh cùng nhắm một hạn cũ.** Bên gọi gửi kèm hạn/phiên bản đang thấy. Một lệnh nối kỳ thành công; lệnh kia báo dữ liệu đã thay đổi và không thu thêm. Muốn mua kỳ nữa phải lấy hạn mới rồi gửi yêu cầu có chủ đích. | Không ghi thêm |
| Q24 | A | **A – Khuyến nghị:** Chỉ tính đã nhận khi backend kiểm quyền/nội dung và **lưu bền vững ý định hủy, lấy giờ từ DB làm mốc**. Nếu ý định được lưu trước 00:00 thì bên gia hạn phải tôn trọng; đúng 00:00 không tính là trước hạn. Chỉ thấy yêu cầu đến sớm trong log chưa đủ để hứa đã nhận hủy hợp lệ. | Không ghi thêm |
| Q25 | B | **B – Khuyến nghị: cho tự mua một kỳ mới từ lúc xử lý hợp lệ.** Ví dụ xử lý 00:00:20 thì kỳ mới bắt đầu từ mốc đó, không tính quyền dùng ngược 20 giây. Chỉ thu khi chưa hủy hợp lệ, còn đủ điều kiện/tiền và chưa có lệnh khác tạo kỳ mới. Áp dụng cả chậm vài giây lẫn ngừng nhiều giờ/**nhiều ngày**; chỉ thu một kỳ mới, không truy thu tất cả các kỳ đã bỏ lỡ. | Không ghi thêm |
| Q26 | B | **B: giữ đúng thời điểm DB hoàn tất lưu toàn bộ.** Theo ví dụ thì tính ngày 21/10. Agent phải thiết kế được nguồn thời gian xác nhận này; không được lấy 23:59:59 ghi trước đó rồi đặt tên là giờ hoàn tất. | Không ghi thêm |
| Q27 | A | **A – Khuyến nghị: giữ nguyên tập kết quả của lượt xem.** Các trang đều lấy từ một bản kết quả đã cố định; muốn thấy dữ liệu mới thì mở lượt báo cáo mới. Dấu chuyển trang gắn đúng người xem/bộ lọc/bản kết quả. Không giữ transaction DB mở trong suốt thời gian người dùng đọc trang. | Không ghi thêm |
| Q28 | A | **A – Khuyến nghị: vẫn dùng mốc thời gian chọn ở Q26.** Giao dịch thực sự thuộc ngày 20 bị phát hiện muộn thì ghi một khoản bổ sung ở kỳ báo cáo sau, chỉ rõ giao dịch/ngày gốc. Không sửa ngầm bản đã chốt và không cộng một nguồn hai lần. Không tự chuyển về ngày 20 chỉ vì bên gọi gửi ngày sử dụng cũ. | Không ghi thêm |
| Q29 | B | **B:** Chỉ ngừng bán cho khách mới. Khách đang có gói vẫn được gia hạn theo giá/chính sách cấu hình; phải lưu rõ điều kiện cho nhóm khách cũ. | Không ghi thêm |
| Q30 | A | **A – Khuyến nghị:** Có một API tra kết quả chung cho đăng ký gói, hủy, cộng và trừ tiền. Bên gọi gửi loại thao tác + mã yêu cầu, chỉ xem được lệnh thuộc quyền mình. Kết quả phân biệt đang xử lý/thành công/thất bại; mã cũ hết hạn lưu phản hồi cũng không được dùng để làm lại. Quản trị có API nội bộ riêng để chạy job bằng tay, CSKH thông thường không có quyền đó. | Không ghi thêm |
| Q31 | A | **A – Khuyến nghị cho demo:** Dùng một tập dữ liệu giả lập chung. Ứng dụng CSKH có đúng quyền được thao tác thuê bao trong tập đó; chỉ ứng dụng được cấp quyền xác minh ngoài mới được gửi tham chiếu chứng cứ xác minh. Backend kiểm nguồn/định dạng và lưu tham chiếu, không tự xây hệ thống xác minh giấy tờ. Kết quả theo mã yêu cầu vẫn chỉ ứng dụng tạo lệnh được tra cứu, không tự cho A xem lệnh của B. | Không ghi thêm |
| Q32 | A | **A – Khuyến nghị: dùng cách tính chi tiết bên dưới.** Giữ ngưỡng đã chốt; tách số nghiệp vụ mới, số lần gửi lại, lỗi và mất phản hồi. Không bỏ mất phản hồi khỏi báo cáo để làm tỷ lệ lỗi nhỏ hơn. | Không ghi thêm |
| Q33 | A | **A – Khuyến nghị:** Chỉ nhận số đã viết theo chuẩn quốc tế, ví dụ`+84901234567`: dấu+ và tối đa 15 chữ số. CSKH/dữ liệu tạo sẵn phải chuẩn hóa trước; gửi 090… thì backend báo cần đổi định dạng, không tự đoán quốc gia. Tên kỹ thuật của chuẩn là E.164. | Không ghi thêm |
| Q34 | B | **B:** Chỉ cho thành công nếu **toàn bộ việc lưu tiền/lịch sử hoàn tất trước 00:00**. Yêu cầu chặt hơn; agent phải thiết kế và kiểm chứng cơ chế đủ bảo đảm, không chỉ kiểm giờ một lần trong Java. | Không ghi thêm |

## 2. Xác nhận bổ sung trực tiếp của chủ dự án trong phiên26/10

| Mã quyết định bổ sung | Nội dung có hiệu lực | Quan hệ với phiếu |
|---|---|---|
| DEC-BE-21 | Ngày đăng ký là ngày đầu; gói30 ngày bắt đầu10:15 ngày01/10 dùng hết30/10 và hết đúng00:00 ngày31/10 theo Asia/Ho_Chi_Minh | Hoàn tất phần mốc cuối ngày Q21=B chưa điền đủ trong file |
| CR-CAN-002 | Hủy có hiệu lực cuối toàn bộ thời gian đã trả, không hoàn phí; chặn mua thêm/tự gia hạn từ khi hủy được nhận hợp lệ | Q22=A thay chi tiết cuối chu kỳ hiện tại của DEC-04; không xóa lịch sử DEC-04 |
| CR-TIME-001 | Báo cáo dùng `recordedAt`, mốc ghi nhận nghiệp vụ trong transaction cuối cùng đã commit; không dùng/không hứa giờ hoàn tất durable commit | Chủ dự án xác nhận đổi Q26=B sang mốc ghi nhận, lập CR; dấuB trong phiếu được giữ làm lịch sử |
| CR-CAN-003 | Sau xác nhận commit ý định hủy, kiểm giờ DB mới để tiếp nhận; đúng/sau hạn từ chối; kết quả đã chốt thì replay khi mất phản hồi | Chủ dự án xác nhận trực tiếp ngày 26/10, hoàn tất biên Q24=A |
| CR-TIME-002 | Kiểm giờDB dưới khóa ngay trước cập nhật tài chính; nếu đã tới hạn thì từ chối, nếu còn hạn lúc kiểm có thể commit sau hạn | Chủ dự án xác nhận đổi Q34=B sang kiểm điều kiện dưới khóa, lập CR; dấuB trong phiếu giữ lịch sử |

Ví dụ Q21: start=`2026-10-01T10:15:00+07:00`, end=`2026-10-31T00:00:00+07:00`. Kỳ mới sau gián đoạn bắt đầu lúc thực hiện hợp lệ, end=đầu ngày địa phương(start)+durationDays. Kỳ nối liền bắt đầu ở end trước và cộng số ngày lịch của kỳ mới. Dùng `[start,end)`, không tạo quyền ngược khoảng gián đoạn (Q25=B).

Ví dụ CR-TIME-001: ghi nhận23:59:59 ngày20 nhưng commit00:00:01 ngày21 thì giao dịch thuộc ngày20; chỉ xuất hiện khi đã commit. Nếu đến sau bản chốt, ghi đóng góp điều chỉnh theoQ28, không đổi timestamp để ép vào ngày21. Ví dụ CR-TIME-002: kiểm dưới khóa23:59:59 còn hạn00:00 có thể commit00:00:01; đây là hành vi đã được chấp nhận, không gọi là bảo đảm commit vật lý trước hạn.

## 3. Tổng hợp quy tắc hiện hành dễ tra cứu

- MAIN/phụ có số dư riêng, lệnh tiêu dùng/cộng trừ làm đúng accountId, không fallback/gộp; phí đăng ký/gia hạn luôn MAIN. Tiêu dùng bắt buộc gói/cycle hợp lệ thuộc thuê bao.
- Số tiền mỗi lệnh≤10 triệu, balance≤100 triệu; integer VND; replay30 ngày UC02/04/05,7 ngày UC03/UC01, sau đó tombstone; failed đã chốt không tự hồi sinh.
- Đăng ký lại nối từ cuối thời gian đã trả nếu còn coverage; expectedVersion bảo vệ hai mã mới cùng nhắm một hạn. Worker/manual cùng kỳ chỉ một fee. Sau ngừng bán vẫn có thể gia hạn cho khách cũ đủ điều kiện, không bán mới.
- Worker gia hạn thường xuyên + JOB-01 00:05 + JOB-02 00:30, cùng lõi/cycle guards. Khoảng chạy worker là thiết kế cần trình; không chốt60s theo recovery UC01.
- MySQL8.0.46/InnoDB, READ COMMITTED writes, utf8mb4, UTC DATETIME(6), E.164 strict input. Q14=C giao agent đề xuất UUID representation, không phải đã duyệt CHAR36/BINARY16.
- Báo cáo page50/max200, OR/AND/includeFee rõ, fixed snapshot, source contributions không đúp; late adjustment truy nguồn. Kế hoạch mixed500K/30+30TPS/10hot pairs50% và công thứcQ32 giữ nguyên.
- Keycloak/IdP ngoài backend, scope theo thao tác và caller sở hữu request; demo chung dữ liệu nhưng không mở quyền đọc kết quả caller khác.

## 4. Phần chưa phải quyết định cuối và điều kiện nghiệm thu

UUID CHAR36 ASCII binary, chi tiết bảng/cột/index/DTO/HTTP/scopes/config defaults trong bộ26/10 là đề xuất thiết kế REVIEW. Các số cấu hình không có trong phiếu phải ghi rõ đề xuất. Dữ liệu seed phụ phải ghi type/quyền/hạn trước tạo; chưa mở API tạo account phụ.

Q21/Q26/Q34 không còn chặn nghiệp vụ sau xác nhận trong phiên; chi tiết kỹ thuật receipt hủy, locking/deadlock/idempotency/report snapshot vẫn cần review và kiểm thử. M1–M4 lịch sử UC01 được giữ; delta MySQL và tài liệu UC02–06 mới không tự đạt checkpoint. Không có evidence Java/MySQL/runtime/TPS mới từ việc tiếp nhận phiếu.

Nguồn quyết định đầy đủ và lịch sử: [clarification log](clarification-log.md). Ba CR chi tiết: [hủy trả trước](CR-CAN-002-prepaid-cancellation.md), [mốc báo cáo](CR-TIME-001-reporting-time.md), [hạn tài khoản](CR-TIME-002-account-expiry.md).

Chi tiết Q24: [CR-CAN-003 — mốc tiếp nhận hủy](CR-CAN-003-cancellation-receipt.md). Không dùng giờ HTTP hay giờ ghi ý định chưa được xác nhận để nhận hủy trước hạn. Kết quả nhận hủy đã commit được giữ nguyên khi retry sau hạn.
