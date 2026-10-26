# Phiếu lựa chọn để hoàn thiện tài liệu toàn hệ thống

**Mã:** REV-ALLUC-MYSQL-001 · **Phiên bản:** 0.5 · **Ngày cập nhật:** 25/10/2026 (viết lại Q09–Q34 dễ hiểu, giữ Q07/DEC-JOB-001).  
**Trạng thái:** ĐANG ĐƯỢC CHỦ DỰ ÁN ĐIỀN; giữ nguyên các đáp án đã có. Q07=A phương án ban đầu được chủ dự án xác nhận trong chat, ghi DEC-JOB-001; các bản trước lưu lịch sử; không có đáp án mới nào do agent tự chọn. Giữ mã Q01–Q34.

## Cách điền và dự án đang làm gì

Đây là backend cho CSKH: mở thuê bao → nạp tiền → đăng ký gói có thu phí → trừ tiền tiêu dùng → hủy cuối kỳ → báo cáo/job. Không xây mạng thật, ngân hàng hay giao diện CSKH đầy đủ.

- Mỗi câu cần quyết định chọn **một** ô bằng `[x]`; có thể ghi phương án khác ở ghi chú. “Khuyến nghị” là ý kiến agent, không phải quyết định.
- Nếu chưa biết, để trống và ghi “Open”. Agent tiếp tục phần không phụ thuộc; không xem để trống là đồng ý.
- Q03/Q08/Q17/Q20 chỉ nhắc điều đã có căn cứ, **không cần chọn lại**. MySQL, Hybrid, M1–M4 lịch sử, Q25 cũ và T1–T7 không bị hỏi lại.
- Nhóm A: đọc để chốt nghiệp vụ. Nhóm B: lựa chọn kỹ thuật; có giải thích, có thể yêu cầu agent đề xuất thay vì tự nghiên cứu SQL.
- Trước mắt UC01 cần Q13–Q16, phần liên quan Q18 và Q33; Q31 làm rõ quyền/evidence trước API thật. Các câu UC02–06/job/tải có thể chốt cùng lượt nhưng không tự thành phụ thuộc UC01.
- Đọc [kết quả rà soát](18-RA-SOAT-CHAT-LUONG.md) nếu muốn biết vì sao cần các câu bổ sung. Sau câu trả lời, agent mới ghi DEC/CR và đồng bộ tài liệu chuẩn.

## A. Nghiệp vụ còn cần lựa chọn

### Q01 – Giữ kết quả lệnh bao lâu để gửi lại không bị trừ/cộng lần hai? (R2)
**Tình huống:** Lệnh D1 đã trừ 30.000 VND, CSKH mất phản hồi và hỏi lại sau 10 ngày. “Full result” là đầy đủ phản hồi cũ; “tombstone” là dấu mã+nội dung+kết quả tóm tắt chống thực hiện lại, không phải lịch sử tiền bị xóa.
- [x] **A – Khuyến nghị:** UC02/04/05 giữ full result 30 ngày sau trạng thái cuối, cấu hình được; sau đó giữ tombstone trong demo. UC03 giữ 7 ngày+tombstone như UC01. Không dọn đang xử lý.
- [ ] **B:** UC02–05 cùng giữ 7 ngày+tombstone; ít dữ liệu phản hồi hơn nhưng sau ngày 7 không hứa trả đủ kết quả gốc.
- [ ] **C:** Thời hạn khác: ghi riêng từng UC và cách xử lý ID sau hạn; không cho ID cũ tác động lại.
**Tôi chọn / ghi chú:**

### Q02 – Tiền mỗi lệnh và số dư tối đa là bao nhiêu? (R3)
**Tình huống:** Tài khoản đang có 95 triệu, khách muốn cộng 10 triệu. Giới hạn vận hành khác kiểm tra tràn kiểu số; tiền luôn là VND nguyên dương.
- [x] **A – Khuyến nghị:** Mỗi lệnh tối đa 10 triệu, số dư tối đa 100 triệu, cấu hình được; ví dụ trên bị từ chối toàn bộ. Giá phí gói cũng phải trong miền tiền hợp lệ.
- [ ] **B:** Không giới hạn vận hành ngoài miền BIGINT có dấu; bắt buộc kiểm tràn và dùng biểu diễn API không mất chính xác với số lớn.
- [ ] **C:** Ghi hai mức khác và có áp cùng mức cho nạp, tiêu dùng, phí gói hay không.
**Tôi chọn / ghi chú:**

### Q03 – Tài khoản phụ do ai tạo? (đã có định hướng, không chọn lại)
DEC-PRE-16 đã chọn: UC01 chỉ tạo MAIN; phụ theo loại dịch vụ cấu hình, tạo bằng seed/quy trình riêng. Không tự mở API tạo tài khoản phụ. Phần còn thiếu là danh mục demo, hạn dùng và quyền giao dịch ở Q09; nếu muốn thêm API mới, ghi yêu cầu thay đổi phạm vi tại đây.
**Yêu cầu thay đổi nếu có:**

### Q04 – Đăng ký nối kỳ thủ công và tự gia hạn cùng nhắm một kỳ
**Tình huống:** Gói hết kỳ lúc 10:00. Lệnh nối thêm kỳ của CSKH và worker tự gia hạn cùng chạy. Hai khoản thu cho cùng kỳ là sai; unique cycleKey chỉ là cách nhận diện kỳ.
- [x] **A – Khuyến nghị:** Một kỳ kế tiếp chỉ thu một lần; nếu thao tác kia đã tạo kỳ đó thì re-evaluate và trả xung đột/trạng thái hiện tại, không tự nối thêm một kỳ nữa.
- [ ] **B:** Ưu tiên lệnh thủ công đã được tiếp nhận bền vững trước biên; worker nhường. Nếu worker đã commit trước lúc tiếp nhận, trả kết quả kỳ hiện có, không đảo phí.
- [ ] **C:** Quy tắc khác; ghi thao tác còn lại có được chủ động mua kỳ tiếp theo hay phải báo xung đột.
**Tôi chọn / ghi chú:**

### Q05 – Đăng ký lại sau một thời gian hết gói
**Tình huống:** Gói hết 10/10, khách đăng ký lại 15/10. Từ 10–15/10 chưa có quyền dùng và không có phí thành công.
- [x] **A – Khuyến nghị:** Kỳ mới bắt đầu từ lúc đăng ký lại hợp lệ 15/10; không thu ngược thời gian gián đoạn.
- [ ] **B:** Nối từ 10/10 dù đã gián đoạn; cần ghi rõ khách được gì và có đồng ý trả cho những ngày cũ không.
- [ ] **C:** Quy tắc khác.
**Tôi chọn / ghi chú:**

### Q06 – Tên trạng thái sau khi hủy có hiệu lực
**Tình huống:** Một gói dừng do khách yêu cầu, gói khác dừng do không đủ tiền gia hạn. Cả hai không còn quyền dùng, lịch sử vẫn giữ.
- [x] **A – Khuyến nghị:** Phân biệt CANCELLED (khách hủy) và EXPIRED (hết hạn); dễ giải thích và thống kê.
- [ ] **B:** Cùng EXPIRED, lưu lý do/mốc hủy riêng; ít trạng thái hơn.
- [ ] **C:** Tên/cách lưu khác, vẫn phân biệt được nguyên nhân từ lịch sử.
**Tôi chọn / ghi chú:**

### Q07 – Đã chốt phương án ban đầu: worker gia hạn + JOB-01 hằng ngày
**Đã duyệt ngày 25/10/2026 – DEC-JOB-001:** Chủ dự án xác nhận “nếu thế chốt phương án như ban đầu đi vậy” sau khi phân biệt kiểm tra định kỳ với hẹn lịch theo từng gói. Chốt cách tổ chức dưới đây, không tự chốt chính sách gia hạn muộn Q25.

- [x] **A – Phương án đã chọn:** Worker gia hạn kiểm tra thường xuyên các gói đến hạn tự gia hạn; **JOB-01 vẫn chạy lúc 00:05 hằng ngày**, xử lý hết hạn gói/tài khoản, hoàn tất hủy và rà soát phần bị bỏ sót. Hai đường dùng chung logic kiểm điều kiện/cycle/thu phí nguyên tử, không thu lại kỳ đã thành công. Worker là tác vụ hỗ trợ trong backend, không phải ứng dụng riêng hay một timer cho mỗi thuê bao. JOB-02 tổng hợp báo cáo vẫn 00:30.
- [ ] **B:** Chỉ JOB-01 hằng ngày 00:05, không có worker gia hạn thường xuyên; chưa chọn.
- [ ] **C:** Cách khác, bao gồm hợp nhất thành một JOB-01 thường xuyên hoặc hẹn lần chạy theo deadline gần nhất; chưa chọn.

**Phân vai để tránh xử lý trùng:**

| Thành phần | Khi nào được kích hoạt? | Trách nhiệm |
|---|---|---|
| Worker gia hạn | Kiểm tra định kỳ thường xuyên; khoảng chạy còn cần thiết kế/cấu hình | Tìm gói tới hạn cần gia hạn, kiểm lại điều kiện và thực hiện tối đa một khoản phí cho cùng kỳ |
| JOB-01 | 00:05 hằng ngày, có thể chạy tay có quyền | Xử lý trạng thái hết hạn gói/tài khoản, hủy đến hạn và rà soát/bù việc bị bỏ sót theo cùng quy tắc |
| JOB-02 | 00:30 hằng ngày | Tổng hợp tiêu dùng; nghiệp vụ khác JOB-01 và worker gia hạn |

**Ví dụ giữ mốc 00:00:** Gói hết 00:00; worker là đường chủ động xử lý gia hạn gần thời điểm đó. Đến 00:05, JOB-01 thấy kỳ mới đã được gia hạn thành công thì bỏ qua khoản thu, không thu lần hai. Nếu gặp phần việc bỏ sót, JOB-01 kiểm lại dữ liệu và dùng chung cơ chế xử lý/recovery; có được thu muộn hay không vẫn phụ thuộc **Q25**.

**Các giới hạn cần giữ rõ:**

- “JOB-01 có lịch” nghĩa là lịch cố định 00:05, không phải đặt một lịch riêng cho từng gói. Bản ghi gói không tự gọi worker khi đồng hồ tới hạn.
- Gói/tài khoản mất quyền đúng mốc hết hạn; đường API kiểm timestamp và trạng thái liên quan, không chờ tới 00:05 mới chặn. Gia hạn chỉ cấp quyền kỳ mới sau khi dữ liệu/thu phí đã commit hợp lệ; không hứa không gián đoạn.
- Worker và JOB-01 có thể gặp cùng gói: phải kiểm lại trạng thái/cycle trong transaction, khóa/unique theo thiết kế, dùng cùng khóa kỳ khi retry. Chạy tay, nhiều instance hoặc mất phản hồi sau commit không được tạo phí trùng. JOB-01 không tự mở lại nghiệp vụ đã FAILED thiếu tiền chỉ vì có nạp thêm.
- Worker chỉ truy vấn tập cần gia hạn; JOB-01 tập trung công việc hết hạn/rà soát. Dùng chỉ mục/lô/budget và đo tải trước kết luận chi phí; không có căn cứ tuyên bố hai tác vụ luôn tốn gấp đôi hoặc không đáng kể.
- **Chưa chốt khoảng chạy worker**; không tự lấy 60 giây của đề xuất hợp nhất bị loại làm giá trị đã duyệt. Backlog, retry kỹ thuật và trễ hạn cần cấu hình/test riêng.

**Q25 vẫn mở:** Nếu worker chỉ lấy gói đã đến hạn rồi mới thu, kết quả thường là gia hạn sau biên. Q25=A cấm mọi thu muộn sẽ không đáp ứng mong muốn tự gia hạn theo cách đó; Q25=B hoặc C có chính sách muộn rõ cần được chọn riêng. Q07=A không mặc nhiên duyệt Q25=B, không cho thu bù nhiều kỳ hoặc cấp quyền cho khoảng trống.

**Lịch sử:** A gốc đã được đánh dấu được lưu tại clarification log mục16.1; bản Q07 v0.3 đề xuất hợp nhất được giữ tại mục17.1. **CR-JOB-001 không được chọn**; giữ lịch DEC-PRE-19 hiện hành. Đây là duyệt cách tổ chức tác vụ, không phải nghiệm thu code/test hay toàn bộ OQ-07/09/13.

**Tôi chọn / ghi chú:** A – chủ dự án xác nhận trong chat ngày 25/10/2026; ghi nhận DEC-JOB-001.

### Q08 – Giao dịch tiêu dùng phải truy được gói/ngữ cảnh (đã có định hướng)
DEC-PRE-18 đã chọn: debit tiêu dùng có tham chiếu gói/ngữ cảnh dịch vụ xác định; phí liên kết đăng ký. Không được giả mạo attribution hoặc tự gán vào A/B. Q11 quyết định có nhận tiêu dùng không gói không; bản đề xuất mỗi khoản tiền thuộc một gói hoặc một ngữ cảnh, không cộng nguyên một khoản vào hai gói. Nếu cần chia một khoản cho nhiều gói, ghi yêu cầu và quy tắc chia tại đây.
**Yêu cầu thay đổi nếu có:**

## Giải thích ngắn trước khi đọc Q09–Q34

- **MAIN:** tài khoản tiền chính của một thuê bao. **GAME/DATA:** ví dụ tài khoản tiền phụ phục vụ một dịch vụ. Mỗi tài khoản có số dư riêng; không phải tài khoản đăng nhập.
- **Bên gửi lệnh:** ứng dụng CSKH hoặc hệ thống được cấp quyền gọi backend. Tài liệu cũ gọi là caller/client.
- **Đăng ký gói:** backend lấy giá gói và thu phí từ MAIN theo quy tắc đã chốt. **Lệnh trừ tiền tiêu dùng:** bên gửi lệnh chỉ rõ tài khoản và khoản tiền muốn trừ, backend kiểm điều kiện.
- **Mã yêu cầu:** mã giúp nhận ra lần gửi lại của cùng một lệnh. Gửi lại không được trừ/cộng lần nữa; đổi ý nghĩa lệnh phải dùng quy tắc mã đã chốt.
- **DB/MySQL:** nơi lưu dữ liệu. **Lưu thành công toàn bộ một nghiệp vụ:** tiền, lịch sử và kết quả được xác nhận cùng nhau; tên kỹ thuật là commit transaction. Mốc ghi dữ liệu có thể khác mốc xác nhận lưu xong.
- Các con số về số dư, giá gói, ngày giờ trong ví dụ chỉ giúp hiểu câu hỏi. Chúng không tự trở thành dữ liệu/giá bán đã được duyệt.

### Q09 – Có cho cộng/trừ tiền ở tài khoản phụ, hay chỉ dùng MAIN?
**Bạn đang chọn điều gì?** API cộng/trừ tiền độc lập có được nhận lệnh cho tài khoản GAME/DATA không. Câu này chưa hỏi tự chuyển sang ví khác khi thiếu tiền.

**Ví dụ:** Một thuê bao có GAME=20.000đ và MAIN=50.000đ. Bên gửi lệnh ghi rõ “trừ 30.000đ từ GAME”.

- [x] **A – Khuyến nghị: hỗ trợ cả MAIN và tài khoản phụ, nhưng làm đúng tài khoản được chỉ rõ.** Ví dụ trên **báo GAME không đủ tiền**, cả hai số dư giữ nguyên; không tự lấy MAIN dù MAIN đủ. Lệnh trừ 20.000đ từ GAME được xử lý nếu GAME còn hạn và được phép trừ. Từng loại tài khoản được cấu hình có cho cộng, cho trừ hay cả hai; MAIN không có hạn mặc định. Tài khoản phụ demo được tạo sẵn cùng loại/quyền/hạn dùng rõ ràng, chưa khai báo hạn thì chưa tạo ví đó. Không thêm API mở ví ở câu này.
- [ ] **B: bản đầu chỉ cho cộng/trừ MAIN.** Lệnh chỉ GAME sẽ bị từ chối vì chưa hỗ trợ tài khoản phụ; hệ thống không tự đổi mục tiêu thành MAIN. Dễ làm hơn nhưng chưa kiểm chứng đầy đủ giao dịch trên nhiều tài khoản như đề bài.
- [ ] **C: tôi muốn quy tắc khác.** Ghi cụ thể loại tài khoản nào được cộng/trừ và hạn dùng. Nếu muốn “GAME thiếu thì lấy **toàn bộ** khoản tiền từ MAIN, không gộp hai ví”, hãy ghi rõ chỉ áp dụng cho tiêu dùng hay cả phí đăng ký gói; agent sẽ phân tích và trình thay đổi quy tắc trước khi áp dụng.

**Tách riêng phí đăng ký:** Gói đăng ký giá 30.000đ luôn thu MAIN theo baseline. GAME có 20.000đ không giúp thanh toán phí này. MAIN=50.000đ thì trừ MAIN 30.000đ; MAIN=20.000đ thì đăng ký thất bại và không ví nào bị trừ. Q09=A không thay quy tắc đó.

**Tôi chọn / ghi chú:**

### Q10 – Một trang báo cáo nên trả bao nhiêu thuê bao?
**Ví dụ:** Báo cáo tìm được 10.000 thuê bao. Hệ thống chia kết quả thành nhiều trang để tránh trả một danh sách quá lớn. Dấu chuyển trang là thông tin backend cấp để lấy trang tiếp; không được sửa bộ lọc rồi dùng lại dấu của lần tìm cũ.

- [x] **A – Khuyến nghị:** Mỗi trang mặc định 50 thuê bao; bên gọi được yêu cầu nhiều hơn nhưng tối đa 200. Dấu chuyển trang được kiểm tra để tránh sửa giả, gắn với người có quyền xem/bộ lọc và tập kết quả ở Q27.
- [ ] **B:** Mặc định 100, tối đa 500 thuê bao một trang. Mỗi lần lấy được nhiều hơn nhưng phải kiểm tra thời gian truy vấn/tải khi triển khai; dấu chuyển trang vẫn gắn đúng bộ lọc/tập kết quả.
- [ ] **C:** Tôi muốn số khác. Ghi số mặc định và số tối đa mỗi trang.

**Tôi chọn / ghi chú:**

### Q11 – Chưa có gói dịch vụ đang hiệu lực thì có cho trừ tiền tiêu dùng không?
**Ví dụ:** Có lệnh “thuê bao dùng dịch vụ GAME, trừ 5.000đ ở tài khoản được phép”, nhưng thuê bao không có gói GAME đang còn hiệu lực. Tiền trong ví là một điều kiện; có quyền dùng dịch vụ/gói hay không là điều kiện riêng.

- [x] **A – Khuyến nghị: phải có gói đang hiệu lực.** Backend kiểm gói/kỳ sử dụng đúng thuê bao. Chưa đăng ký gói, gói hết hạn hoặc bên gọi chỉ sai gói thì từ chối và không trừ 5.000đ.
- [ ] **B: cho tiêu dùng dịch vụ không kèm gói nếu dịch vụ đó đã được cấu hình cho phép.** Vẫn kiểm quyền bên gọi, tài khoản, tiền và loại dịch vụ; lưu rõ “tiêu dùng GAME không thuộc gói”. Khoản này không được tự đưa vào báo cáo tiêu dùng gói A/B. Nếu lệnh có chỉ gói thì gói đó vẫn phải hợp lệ.
- [ ] **C:** Quy tắc khác. Ghi khi nào không có/hết gói vẫn được trừ tiền và khoản đó phải xuất hiện thế nào trong báo cáo.

**Lưu ý:** Đây là mô phỏng lệnh do hệ thống có quyền gửi; chưa xây hệ thống đo lưu lượng mạng hoặc tự tính giá dịch vụ thật.

**Tôi chọn / ghi chú:**

### Q12 – Chốt số liệu của ngày hôm trước vào lúc nào?
**Ví dụ:** JOB-02 chạy 00:30 ngày 21/10 để tổng hợp ngày 20/10. “Chốt” nghĩa là giữ một bản kết quả xác định; chạy lại để khôi phục không được cộng lại số tiền vào cùng báo cáo. Cách xử lý giao dịch bị phát hiện muộn ở Q28.

- [x] **A – Khuyến nghị: giữ lịch 00:30 đã chọn.** Ngay khi mốc/tập dữ liệu để chốt đã được lưu bền vững lần đầu, các lần chạy lại phải dùng đúng nó, kể cả lần trước lỗi và chưa hoàn tất báo cáo. Không lấy một mốc mới mỗi lần thử lại để tránh số liệu thay đổi tùy lúc chạy. Dữ liệu muộn được xử lý riêng theo Q28.
- [ ] **B:** Tôi muốn đổi giờ chốt. Ghi giờ mong muốn và lý do; vì 00:30 đã được chọn trước, agent sẽ ghi thay đổi quyết định và đồng bộ lịch.
- [ ] **C:** Tôi chưa quyết định. Giữ câu này mở tới khi thiết kế job tổng hợp; không tự chọn giờ/mốc chốt khác.

**Tôi chọn / ghi chú:**

## B. Các lựa chọn kỹ thuật – giải thích theo tác động tới dự án

Nhóm Q13–Q19 chủ yếu quyết định cách lưu/chạy phần mềm. Nếu không muốn tự chọn kỹ thuật, có thể dùng phương án giao agent đề xuất ở những câu có lựa chọn đó; agent vẫn phải ghi rõ cách làm và kiểm chứng, không tự nhận bạn đã duyệt kết quả chưa có.

### Q13 – Máy nhà sẽ chạy phiên bản MySQL nào?
**Bạn đang chọn điều gì?** Dự án đã chọn MySQL; câu này chỉ chọn phiên bản để lúc lập trình và lúc kiểm thử dùng cùng môi trường. InnoDB là bộ máy lưu dữ liệu hỗ trợ giao dịch cần dùng. Phiên bản cụ thể phải ghi cố định, tránh mỗi máy tự lấy một bản khác.

- [ ] **A – Khuyến nghị:** Dùng MySQL 8.4 LTS với InnoDB. Khi triển khai, agent chọn bản vá/image cụ thể, ghi lại và kiểm thử migration trên bản đó; chưa coi DB đã chạy chỉ vì chọn A.
- [x] **B:** Dùng MySQL 8.0.46 với InnoDB vì máy nhà đã cài và đang chạy phiên bản này. Khi triển khai/kiểm thử, cần kiểm tra tương thích trên đúng phiên bản đã chọn.
- [ ] **C:** Tôi giao agent khảo sát môi trường máy nhà và đề xuất phiên bản phù hợp, rồi trình phần thiết kế DB trước khi chạy migration. Hiện chưa chốt phiên bản.

**Tôi chọn / ghi chú:**

### Q14 – Mã định danh nên dễ đọc trong DB hay lưu gọn hơn?
**Ví dụ:** Một thuê bao có mã dạng `550e8400-e29b-41d4-a716-446655440000`. Đây là UUID, mã định danh chứ không phải số điện thoại. API vẫn dùng cùng mã; câu hỏi chỉ là DB lưu nó như thế nào.

- [ ] **A: lưu dạng 16 byte để gọn hơn** (`BINARY(16)`). Tiết kiệm chỗ so với chuỗi 36 ký tự, nhưng đọc DB trực tiếp khó hơn; agent phải kiểm việc chuyển qua lại với Java để không sai mã.
- [ ] **B – Khuyến nghị cho bài tập: lưu nguyên chuỗi 36 ký tự** (`CHAR(36)`). Dễ nhìn và kiểm tra khi học/debug, tốn thêm chỗ lưu/index. Các bảng liên kết phải dùng cùng cách lưu/so mã.
- [x] **C:** Tôi giao agent đánh giá và đề xuất cách lưu; phải ghi rõ lựa chọn trong bản thiết kế DB để duyệt, chưa tự coi đã chọn A hoặc B.

**Tôi chọn / ghi chú:**

### Q15 – Chọn cách DB nhìn dữ liệu khi nhiều lệnh chạy cùng lúc
**Ví dụ:** Một ví có 100.000đ, hai lệnh cùng muốn trừ 80.000đ. Hệ thống phải chỉ cho số lệnh hợp lệ thành công, không được âm tiền. Câu này chọn một cấu hình nền của DB; **dù chọn A hay B, vẫn phải có khóa/ràng buộc và test**, không chỉ đổi tên cấu hình là hết lỗi đồng thời.

- [x] **A – Khuyến nghị thử:** Dùng `READ COMMITTED` cho các nghiệp vụ ghi tiền/cấp số có khóa rõ. Hiểu đơn giản: các lần đọc thông thường có thể thấy thay đổi đã được lệnh khác lưu xong giữa các lần đọc. Agent phải thiết kế khóa và kiểm thử tranh chấp; báo cáo nhiều trang cần cơ chế riêng ở Q27.
- [ ] **B:** Dùng `REPEATABLE READ`, cách mặc định của MySQL, rồi thiết kế khóa/index theo cách đó. Các lần đọc thông thường trong cùng transaction có ảnh chụp ổn định hơn, nhưng không tự bảo vệ mọi thao tác ghi. Vẫn phải kiểm thử trừ tiền đồng thời và chờ khóa.
- [ ] **C:** Tôi muốn agent thử cả hai trên MySQL rồi trình kết quả trước khi chốt. Chưa có phép đo thì ghi chưa chọn, không tự tuyên bố cách nào nhanh hơn.

**Tôi chọn / ghi chú:**

### Q16 – Lưu tên tiếng Việt, mã và thời gian theo một quy ước thống nhất?
**Ví dụ:** Tên “Nguyễn Văn An” phải giữ dấu. Mã yêu cầu/giấy tờ không được tự coi hai giá trị khác nhau là một chỉ vì cách so chữ của DB. Thời điểm 07:00 ở Việt Nam tương ứng 00:00 UTC; phải thống nhất để không lệch báo cáo.

- [x] **A – Khuyến nghị:** Lưu chữ bằng `utf8mb4` để hỗ trợ Unicode; chuẩn hóa đầu vào theo T1 đã duyệt; quy tắc so các mã được ghi rõ. Thời gian lưu dạng `DATETIME(6)` theo UTC, API gửi thời gian kèm múi giờ/độ lệch. Q26/Q34 quyết định lấy mốc thời gian nào, không phải câu này.
- [ ] **B:** Tôi muốn quy ước khác. Ghi phần muốn đổi, cách giữ tiếng Việt và cách biểu diễn giờ Việt Nam/UTC; nếu đụng T1 đã duyệt thì agent ghi thay đổi trước khi áp dụng.
- [ ] **C:** Tôi chưa chốt chi tiết kỹ thuật này; giữ mở trước khi tạo migration DB đầu tiên.

**Tôi chọn / ghi chú:**

### Q17 – Ở công ty có bắt buộc chạy database không? (đã chốt, không cần chọn)
**Câu trả lời hiện có:** Không. Ở công ty đọc/duyệt tài liệu, viết code và chạy kiểm thử không cần DB nếu đủ công cụ. DB, các dịch vụ và bài test tích hợp/tải chạy ở nhà. Không yêu cầu cài Docker/MySQL tại công ty; không dùng dịch vụ PostgreSQL Supabase để thay MySQL.

Đây là nội dung đã ghi tại CR-DB-001/ADR-003, không hỏi lại. Chỉ ghi thêm nếu điều kiện công ty của bạn đã thay đổi.

**Thay đổi nếu có:**

### Q18 – Ở nhà dựng đủ các dịch vụ ngay, hay dựng từng bước?
**Các thành phần để bạn dễ hình dung:** MySQL lưu dữ liệu; RabbitMQ gửi tín hiệu có số điện thoại mới; Redis giữ bản sao danh mục để đọc nhanh; Keycloak là ứng dụng bên ngoài backend cấp token đăng nhập/quyền. Docker Compose là công cụ khởi động các dịch vụ theo một file cấu hình.

- [x] **A – Khuyến nghị: dựng đủ bộ dịch vụ ở nhà.** MySQL, RabbitMQ, Redis và Keycloak chạy bằng Compose. Backend chạy từ IDE khi phát triển, đóng container lúc bàn giao; kiểm tình trạng từng dịch vụ.
- [ ] **B: dựng theo từng bước.** Ban đầu chỉ MySQL để kiểm schema/migration; trước khi thử API có bảo vệ phải có bên cấp token hợp lệ; trước nghiệm thu các ca RabbitMQ/Redis phải bổ sung đúng dịch vụ. Không bỏ các thành phần đã chọn khỏi kiến trúc.
- [ ] **C:** Tôi muốn nơi/cách chạy khác. Ghi từng dịch vụ sẽ chạy ở máy nào hoặc dịch vụ nào đã có sẵn.

**Tôi chọn / ghi chú:**

### Q19 – Đồng ý hướng chia dữ liệu thành các nhóm lưu trữ này không?
**Ví dụ:** Muốn biết một phí 30.000đ đã trả cho kỳ nào thì phải nối được lịch sử tiền với kỳ sử dụng. Muốn gửi lại cùng lệnh không trừ hai lần thì phải giữ mã và kết quả yêu cầu.

Hướng thiết kế có các nhóm: **kỳ sử dụng/giá đã trả**, **mã yêu cầu và kết quả**, **lịch sử cộng-trừ**, **báo cáo ngày** và **điều chỉnh báo cáo**. Chi tiết bảng ở [bản đề xuất MySQL](11-MYSQL-DATA-DESIGN.md), đọc cùng [quy tắc chung](20-QUY-TAC-CHUNG-VA-BIEN-NGHIEP-VU_DRAFT.md).

- [x] **A – Khuyến nghị:** Đồng ý cách chia dữ liệu ở mức ý tưởng đó. Agent vẫn phải trình bảng/cột/ràng buộc cụ thể cho từng UC; chọn A chưa duyệt sẵn mọi câu SQL hoặc coi migration đã chạy.
- [ ] **B:** Tôi muốn gộp/đổi một số nhóm/bảng. Ghi tên hoặc mục muốn đổi và điều muốn đơn giản hóa; agent kiểm lại để không mất lịch sử/chống thu trùng.
- [ ] **C:** Tôi muốn xem ERD MySQL chi tiết UC01 trước rồi mới duyệt. ERD là sơ đồ bảng và quan hệ; chưa duyệt schema các UC sau.

**Tôi chọn / ghi chú:**

### Q20 – Khi nào có báo cáo tổng thể? (đã có yêu cầu, không cần chọn)
**Đã thống nhất sản phẩm:** Sau khi duyệt đủ nội dung liên quan, agent tổng hợp **báo cáo ý tưởng/nghiệp vụ** trước code. Sau khi có code và test thật mới bổ sung **báo cáo kết quả triển khai/kiểm thử**.

Ví dụ: trước code có thể viết “hệ thống cần đạt 30 TPS mỗi loại”; chỉ sau đo thật mới được viết “đã đạt”. Câu này nhắc yêu cầu tại CR-DB-001; chỉ ghi thêm nếu bạn muốn thay đổi cách bàn giao báo cáo.

**Thay đổi nếu có:**

## C. Các tình huống nghiệp vụ cần chọn rõ

### Q21 – Gói “30 ngày” hết đúng giờ đăng ký hay theo ngày lịch?
**Ví dụ cố định:** Khách đăng ký lúc 10:15 ngày 01/10. Cần quy định chính xác khi nào mất quyền dùng gói để việc thu phí, hủy và kiểm thử đều tính giống nhau.

- [ ] **A – Khuyến nghị: đủ 30×24 giờ.** Gói hết lúc 10:15 ngày 31/10. Từ đúng mốc đó không có quyền kỳ mới nếu chưa được gia hạn hợp lệ.
- [x] **B: tính theo ngày trên lịch địa phương.** Bạn cần ghi thêm: ngày đăng ký có tính là ngày đầu không, và hết vào đầu hay cuối ngày nào. Nếu chưa ghi thì agent chưa được tự suy công thức.
- [ ] **C:** Cách khác. Ghi ví dụ giờ/ngày bắt đầu và giờ/ngày kết thúc bạn muốn.

**Tôi chọn / ghi chú:** 
ngay dang ky tinh laa ngay dau
### Q22 – Đã mua trước kỳ sau, rồi hủy giữa kỳ hiện tại: dùng tới đâu và tiền xử lý thế nào?
**Ví dụ:** Kỳ đang dùng hết 00:00 ngày 31/10. Ngày 15/10 khách đã trả tiền cho kỳ tiếp theo, dùng từ 31/10 tới 00:00 ngày 30/11. Đến 20/10 khách yêu cầu hủy.

- [x] **A – Khuyến nghị về độ đơn giản:** Cho dùng tới 30/11 vì đã trả tiền, không hoàn phí; chặn mua thêm và gia hạn tự động. **Đây là đổi quy tắc** “hủy cuối kỳ hiện tại” thành “hủy cuối toàn bộ thời gian đã trả”, cần ghi CR khi bạn chọn.
- [ ] **B:** Dừng lúc 31/10, đúng cuối kỳ đang dùng; hoàn toàn bộ tiền của kỳ tương lai chưa bắt đầu bằng giao dịch điều chỉnh có lịch sử. Cách này **thêm nghiệp vụ hoàn tiền**, phải thiết kế/duyệt trước khi triển khai hủy.
- [ ] **C:** Tôi chưa quyết định ca này. Giữ mở; chưa triển khai tính năng trả trước nối nhiều kỳ tới khi chốt. Không tự làm mất tiền/kỳ đã trả của khách.

**Tôi chọn / ghi chú:**

### Q23 – Bấm đăng ký thêm hai lần với hai mã yêu cầu khác nhau thì mua một kỳ hay hai kỳ?
**Ví dụ:** Gói đang hết 31/10. Hai lần bấm tạo hai mãR 1/R2 khác nhau, đều nhắm nối từ hạn 31/10. Gửi lại **cùng mã** đã có quy tắc chống trừ lặp; câu này hỏi **hai mã khác nhau**.

- [x] **A – Khuyến nghị: bảo vệ khi hai lệnh cùng nhắm một hạn cũ.** Bên gọi gửi kèm hạn/phiên bản đang thấy. Một lệnh nối kỳ thành công; lệnh kia báo dữ liệu đã thay đổi và không thu thêm. Muốn mua kỳ nữa phải lấy hạn mới rồi gửi yêu cầu có chủ đích.
- [ ] **B: coi hai mã khác nhau là hai lần mua.** Nếu đủ tiền, xử lý nối hai kỳ liên tiếp và thu hai phí. Bên gọi phải tự dùng lại cùng mã khi muốn gửi lại lệnh cũ, tránh bấm đôi thành hai lần mua.
- [ ] **C:** Quy tắc khác. Ghi khi nào hai lần bấm được tính là hai lần mua và khi nào phải chặn.

**Tôi chọn / ghi chú:**

### Q24 – Khi nào được coi là đã nhận yêu cầu hủy trước giờ hết gói?
**Ví dụ:** Gói hết 00:00. Yêu cầu hủy đến backend lúc 23:59:59, nhưng do chờ xử lý, tới 00:00:01 mới ghi nhận xong. Quy tắc đã chốt: hủy được nhận hợp lệ trước hạn phải ngăn tự gia hạn; cần xác định “nhận hợp lệ” là mốc nào.

- [x] **A – Khuyến nghị:** Chỉ tính đã nhận khi backend kiểm quyền/nội dung và **lưu bền vững ý định hủy, lấy giờ từ DB làm mốc**. Nếu ý định được lưu trước 00:00 thì bên gia hạn phải tôn trọng; đúng 00:00 không tính là trước hạn. Chỉ thấy yêu cầu đến sớm trong log chưa đủ để hứa đã nhận hủy hợp lệ.
- [ ] **B:** Tính từ lúc backend nhận được yêu cầu mạng, dù phần lưu DB hoàn tất sau. Muốn làm vậy phải có bằng chứng tiếp nhận đáng tin/bền vững và cơ chế ngăn gia hạn bỏ qua; phức tạp hơn chỉ lấy giờ từ log hoặc giờ client gửi.
- [ ] **C:** Tôi chưa chọn được mốc. Giữ câu này mở trước khi chốt xử lý đồng thời hủy/gia hạn.

**Tôi chọn / ghi chú:**

### Q25 – Đã quá giờ hết gói rồi, hệ thống có được tự thu tiền gia hạn nữa không?
**Ví dụ:** Gói hết 00:00. Ca 1: worker tới 00:00:20 mới xử lý. Ca 2: DB lỗi, tới 02:00 mới hoạt động lại. Cả hai ca chưa có gia hạn lưu thành công lúc hết hạn; trong khoảng trống không được tự cho dùng kỳ mới.

- [ ] **A: quá hạn là không được tự thu nữa, dù chỉ chậm vài giây.** Gói hết hiệu lực; khách muốn dùng tiếp phải đăng ký lại theo Q05. Vì worker Q07 kiểm gói tới hạn rồi mới xử lý, chọn A sẽ khiến các ca đã qua hạn không tự gia hạn được, kể cả ví đủ tiền.
- [x] **B – Khuyến nghị: cho tự mua một kỳ mới từ lúc xử lý hợp lệ.** Ví dụ xử lý 00:00:20 thì kỳ mới bắt đầu từ mốc đó, không tính quyền dùng ngược 20 giây. Chỉ thu khi chưa hủy hợp lệ, còn đủ điều kiện/tiền và chưa có lệnh khác tạo kỳ mới. Áp dụng cả chậm vài giây lẫn ngừng nhiều giờ/**nhiều ngày**; chỉ thu một kỳ mới, không truy thu tất cả các kỳ đã bỏ lỡ.
- [ ] **C:** Tôi muốn giới hạn việc tự thu muộn. Ví dụ chỉ cho phép chậm tối đa một khoảng do tôi chọn, lâu hơn thì khách đăng ký lại; hoặc phân biệt trễ lịch và lỗi hệ thống. Ghi thời gian/điều kiện, lúc bắt đầu kỳ mới và cách thu; chưa ghi rõ thì vẫn để mở.

**Chung:** Nếu phản hồi bị mất nhưng gia hạn thực tế đã lưu thành công, phải tra kết quả cũ và không thu thêm. Q07 chỉ chốt cách tổ chức worker/JOB-01, chưa chọn hộ Q25.

**Tôi chọn / ghi chú:**

### Q26 – Giao dịch qua nửa đêm được tính vào ngày nào?
**Ví dụ:** Backend ghi mốc giao dịch 23:59:59 ngày 20/10, nhưng tới 00:00:01 ngày 21/10 DB mới xác nhận lưu thành công toàn bộ. Hai mốc khác nhau có thể làm báo cáo khác ngày. Chỉ giao dịch cuối cùng thành công mới được đưa vào báo cáo ở cả A/B.

- [ ] **A – Khuyến nghị cho bài tập:** Dùng mốc ghi nhận nghiệp vụ đã lưu trong giao dịch thành công. Theo ví dụ thì tính vào ngày 20/10. Cách này phải ghi rõ tên/ý nghĩa timestamp; **cần CR thay cách hiểu cũ “thời điểm commit”**. Dữ liệu được nhìn thấy muộn và việc giữ nguyên báo cáo xử lý ở Q27/Q28.
- [x] **B: giữ đúng thời điểm DB hoàn tất lưu toàn bộ.** Theo ví dụ thì tính ngày 21/10. Agent phải thiết kế được nguồn thời gian xác nhận này; không được lấy 23:59:59 ghi trước đó rồi đặt tên là giờ hoàn tất.
- [ ] **C:** Chưa chốt. Giữ yêu cầu cũ theo thời điểm hoàn tất lưu, đồng thời ghi phần thiết kế báo cáo đang chờ quyết định/giải pháp.

**Không đổi:** Khoảng báo cáo vẫn gồm mốc bắt đầu, không gồm mốc kết thúc; câu này chỉ chọn loại timestamp dùng so sánh.

**Tôi chọn / ghi chú:**

### Q27 – Đang đọc nhiều trang báo cáo thì kết quả có được thay đổi không?
**Ví dụ:** Lúc mở báo cáo có 100 thuê bao phù hợp; bạn đọc 50 thuê bao trang 1. Trước khi mở trang 2 có thêm giao dịch làm một thuê bao khác đủ điều kiện. Cần quyết định lượt xem này giữ danh sách cũ hay thay theo dữ liệu mới.

- [x] **A – Khuyến nghị: giữ nguyên tập kết quả của lượt xem.** Các trang đều lấy từ một bản kết quả đã cố định; muốn thấy dữ liệu mới thì mở lượt báo cáo mới. Dấu chuyển trang gắn đúng người xem/bộ lọc/bản kết quả. Không giữ transaction DB mở trong suốt thời gian người dùng đọc trang.
- [ ] **B: mỗi trang xem dữ liệu mới nhất lúc gọi.** Chấp nhận danh sách thay đổi giữa các trang và có thể bỏ qua kết quả mới phát sinh trong lúc duyệt. **Cần điều chỉnh tiêu chí ổn định đã nêu**, không vừa chọn B vừa hứa mọi trang là một tập không đổi.
- [ ] **C:** Giữ câu này mở để agent trình cách bảo đảm kết quả tương đương A bằng dấu phiên bản/mốc dữ liệu có thể kiểm chứng. Chưa có giải pháp thì không tuyên bố trang đã ổn định.

**Tôi chọn / ghi chú:**

### Q28 – Phát hiện thiếu giao dịch sau khi chốt báo cáo ngày thì xử lý ra sao?
**Ví dụ:** Báo cáo ngày 20 đã chốt. Sang ngày 21 mới phát hiện một giao dịch vốn thuộc ngày 20 theo mốc đã chọn ở Q26 nhưng chưa được đưa vào bản chốt. Cần phân biệt với một lệnh chỉ do bên gọi tự ghi ngày 20 nhưng thực tế ngày 21 mới được tiếp nhận/lưu.

- [x] **A – Khuyến nghị: vẫn dùng mốc thời gian chọn ở Q26.** Giao dịch thực sự thuộc ngày 20 bị phát hiện muộn thì ghi một khoản bổ sung ở kỳ báo cáo sau, chỉ rõ giao dịch/ngày gốc. Không sửa ngầm bản đã chốt và không cộng một nguồn hai lần. Không tự chuyển về ngày 20 chỉ vì bên gọi gửi ngày sử dụng cũ.
- [ ] **B: muốn tính theo ngày khách thực sự dùng dịch vụ do bên gửi lệnh cung cấp.** Có thể tính ngày 20 dù nhận/lưu ngày 21, nhưng phải có nguồn ngày sử dụng đáng tin. **Cần CR đổi cơ sở thời gian báo cáo**, không phải chỉ sửa câu SQL.
- [ ] **C:** Tôi chưa quyết định. Giữ mở trước khi thiết kế JOB-02; agent chưa tự chọn ngày để tổng hợp.

**Tôi chọn / ghi chú:**

### Q29 – Gói ngừng bán: khách đang dùng có được gia hạn tiếp không?
**Ví dụ:** Khách đã trả kỳ hiện tại; giữa kỳ người quản trị ngừng bán gói. Quyền đã trả và lịch sử vẫn phải giữ; câu này hỏi các lần mua/gia hạn tiếp theo.

- [ ] **A – Khuyến nghị:** Ngừng bán thì không nhận mua mới, mua nối hay tự gia hạn nữa. Khách vẫn dùng hết thời gian đã trả, sau đó dừng.
- [x] **B:** Chỉ ngừng bán cho khách mới. Khách đang có gói vẫn được gia hạn theo giá/chính sách cấu hình; phải lưu rõ điều kiện cho nhóm khách cũ.
- [ ] **C:** Quy tắc khác. Ghi ai còn được mua/gia hạn và thời điểm bắt đầu áp dụng.

**Tôi chọn / ghi chú:**

### Q30 – Khi gửi lệnh mà mất phản hồi, tra kết quả ở đâu?
**Ví dụ:** CSKH gửi “trừ 30.000đ”, hoặc “hủy gói”, nhưng màn hình bị mất kết nối. Backend có thể đã làm xong. Cần có cách hỏi lại bằng mã yêu cầu; không tự gửi mã mới rồi trừ/hủy lần nữa.

- [x] **A – Khuyến nghị:** Có một API tra kết quả chung cho đăng ký gói, hủy, cộng và trừ tiền. Bên gọi gửi loại thao tác + mã yêu cầu, chỉ xem được lệnh thuộc quyền mình. Kết quả phân biệt đang xử lý/thành công/thất bại; mã cũ hết hạn lưu phản hồi cũng không được dùng để làm lại. Quản trị có API nội bộ riêng để chạy job bằng tay, CSKH thông thường không có quyền đó.
- [ ] **B:** Mỗi nghiệp vụ có một API tra kết quả riêng, vẫn cùng nguyên tắc quyền/mã/gửi lại như A. Job chạy tay bằng công cụ/lệnh nội bộ có ghi lịch sử, không thêm API chạy job.
- [ ] **C:** Tôi muốn kết hợp/cách khác. Ghi cách tra kết quả sau mất phản hồi và ai dùng công cụ nào để chạy job; ví dụ tra cứu chung như A nhưng chạy job bằng lệnh nội bộ như B.

**Sau lựa chọn:** Agent viết rõ đường dẫn, dữ liệu vào/ra và mã phản hồi của từng API trước khi code. Đây là chọn cách tổ chức, chưa phải API đã chạy.

**Tôi chọn / ghi chú:**

### Q31 – Ứng dụng CSKH được phép thao tác trên thuê bao nào?
**Ví dụ:** Có ứng dụng CSKH-A và CSKH-B cùng gọi backend. Có token hợp lệ chỉ chứng minh ứng dụng đã được nhận diện; vẫn cần quy định nó được thao tác dữ liệu nào và có quyền gửi bằng chứng giấy tờ đã xác minh không.

- [x] **A – Khuyến nghị cho demo:** Dùng một tập dữ liệu giả lập chung. Ứng dụng CSKH có đúng quyền được thao tác thuê bao trong tập đó; chỉ ứng dụng được cấp quyền xác minh ngoài mới được gửi tham chiếu chứng cứ xác minh. Backend kiểm nguồn/định dạng và lưu tham chiếu, không tự xây hệ thống xác minh giấy tờ. Kết quả theo mã yêu cầu vẫn chỉ ứng dụng tạo lệnh được tra cứu, không tự cho A xem lệnh của B.
- [ ] **B:** Chia quyền theo danh sách thuê bao/đơn vị. Ví dụ A chỉ được thao tác thuê bao đơn vịA, B chỉ đơn vịB. Phải bổ sung cách cấp/kiểm quyền và kiểm nguồn chứng cứ trước khi chạy API thật.
- [ ] **C:** Chính sách khác. Ghi ứng dụng nào được xem/sửa/nạp/trừ cho nhóm thuê bao nào và ai được gửi chứng cứ xác minh. Agent cấu hình bên cấp token/quyền demo rồi trình kiểm thử; không điền mật khẩu hoặc khóa bí mật vào phiếu này.

**Tôi chọn / ghi chú:**

### Q32 – Đếm kết quả kiểm thử tải thế nào để không “đẹp số” do gửi lại lệnh?
**Ví dụ:** Trong 10 phút đo, muốn đạt 30 nghiệp vụ mới thành công/giây thì cần ít nhất 18.000 lần đăng ký gói mới thành công **và**18.000 lệnh trừ tiền độc lập mới thành công, chạy cùng cửa sổ. Gửi lại cùng mã chỉ tác động một lần thì không được đếm thành nhiều nghiệp vụ.

- [x] **A – Khuyến nghị: dùng cách tính chi tiết bên dưới.** Giữ ngưỡng đã chốt; tách số nghiệp vụ mới, số lần gửi lại, lỗi và mất phản hồi. Không bỏ mất phản hồi khỏi báo cáo để làm tỷ lệ lỗi nhỏ hơn.
- [ ] **B:** Tôi muốn cách tính khác. Ghi cụ thể đếm thời gian của lần gửi hay toàn bộ quá trình gửi lại, lấy tổng số lần nào làm mẫu số tỷ lệ lỗi, và mốc nào tính thành công trong cửa sổ đo. Mục tiêu 30+30,500K và đúng tiền vẫn giữ.
- [ ] **C:** Để tới Phase 6 mới chốt công thức/protocol đo. Hiện chưa được công bố đạt chỉ tiêu khi chỉ có kế hoạch.

**Chọn A nghĩa là đồng ý các điểm này:**

1. Bỏ 2 phút khởi động tải; đo chung 600 giây. Mỗi loại lấy số **nghiệp vụ mới đã lưu thành công trong cửa sổ** chia 600. Phí thu nội bộ khi đăng ký không tính thêm thành lệnh trừ độc lập.
2. Độ trễ đo riêng từng loại đăng ký/trừ, trên mỗi lần gọi có phản hồi của bài tải hỗn hợp, gồm cả gửi lại và phản hồi lỗi. Ít nhất 95% các lần đó không quá 500 ms,99% không quá 1 giây. Mất phản hồi được ghi riêng; không làm như lệnh đó chưa từng tồn tại. Ghi thêm tổng thời gian của một nghiệp vụ qua các lần gửi lại/tra cứu.
3. Tỷ lệ lỗi hệ thống tính trên **mọi lần gửi đăng ký/trừ tiền của bài đo**, gồm gửi lại. Mất phản hồi tính lỗi hệ thống trong phép đo này; lỗi nghiệp vụ như thiếu tiền thống kê riêng. Những lần hỏi trạng thái hoặc kiểm tra service còn sống không vào mẫu số đó. Ngưỡng lỗi hệ thống vẫn≤0,1%.
4. 50% **mọi lần gửi lệnh trừ độc lập**, kể cả gửi lại, chia vào 10 cặp thuê bao–tài khoản tranh chấp; phải có bằng chứng xử lý chồng lấn phía backend. Nửa còn lại phân rộng. Đối chiếu từng mã/lịch sử/số dư; còn lệnh chưa rõ kết quả thì chưa kết luận đối soát đạt.

**Tôi chọn / ghi chú:**

### Q33 – Số điện thoại nhận dạng 090… hay+84…?
**Ví dụ:** `0901234567` và `+84901234567` có thể cùng một số Việt Nam. Kho số phải lưu thống nhất để không coi chúng là hai số khác rồi cấp hai lần.

- [x] **A – Khuyến nghị:** Chỉ nhận số đã viết theo chuẩn quốc tế, ví dụ`+84901234567`: dấu+ và tối đa 15 chữ số. CSKH/dữ liệu tạo sẵn phải chuẩn hóa trước; gửi 090… thì backend báo cần đổi định dạng, không tự đoán quốc gia. Tên kỹ thuật của chuẩn là E.164.
- [ ] **B:** Bản này chỉ xử lý số Việt Nam; backend nhận 090… và có quy tắc đổi sang+84… trước khi so/lưu. Phải cấu hình/kiểm rõ độ dài và đầu số; kho vẫn chỉ lưu một dạng+84….
- [ ] **C:** Tôi muốn dạng khác. Ghi ví dụ đầu vào, dạng lưu duy nhất và cách nhận ra hai chuỗi là cùng một số.

**Tôi chọn / ghi chú:**

### Q34 – Lệnh đến trước lúc tài khoản hết hạn, nhưng tới khi xử lý mới qua hạn thì sao?
**Ví dụ:** Tài khoản hết 00:00. Lệnh đến 23:59:58 nhưng phải chờ. Có thể kiểm điều kiện lúc 23:59:59, còn DB tới 00:00:01 mới xác nhận lưu toàn bộ. Phải chọn mốc quyết định, không chỉ dựa việc lệnh đã tới sớm.

- [ ] **A – Khuyến nghị:** Backend **lấy giờ từ DB** để kiểm lại khi đang giữ khóa, ngay trước khi cập nhật tiền/kết quả. Nếu lúc kiểm đã 00:00 hoặc muộn hơn thì từ chối. Nếu kiểm 23:59:59 còn hợp lệ và lưu mốc đó, có thể chấp nhận dù xác nhận lưu xong trễ qua 00:00. Không hứa toàn bộ COMMIT xảy ra trước hạn.
- [x] **B:** Chỉ cho thành công nếu **toàn bộ việc lưu tiền/lịch sử hoàn tất trước 00:00**. Yêu cầu chặt hơn; agent phải thiết kế và kiểm chứng cơ chế đủ bảo đảm, không chỉ kiểm giờ một lần trong Java.
- [ ] **C:** Tôi muốn chính sách khác. Ghi mốc dùng để xét hạn và kết quả cho lệnh đến sớm nhưng chờ xử lý qua hạn.

**Tôi chọn / ghi chú:**

## D. Xác nhận phạm vi thực sự đã đọc

Không cần duyệt trọn mọi tài liệu nếu chỉ trả lời một nhóm câu. Mỗi dòng chọn một trạng thái; “đồng ý” áp dụng nội dung cụ thể đã đọc cùng đáp án phía trên, không tự duyệt giả định mâu thuẫn.
| Nhóm | Đồng ý sau áp đáp án | Cần sửa | Chưa đọc |
|---|---|---|---|
| UC02 – đăng ký/thu phí/nối kỳ | [ ] | [ ] | [ ] |
| UC03 – hủy/gia hạn/kỳ trả trước | [ ] | [ ] | [ ] |
| UC04 – cộng tiền | [ ] | [ ] | [ ] |
| UC05 – trừ tiền/attribution | [ ] | [ ] | [ ] |
| UC06 – báo cáo/thời gian/snapshot | [ ] | [ ] | [ ] |
| Hai job và phục hồi | [ ] | [ ] | [ ] |
| Delta MySQL UC01 | [ ] | [ ] | [ ] |
| Quy tắc chung 20 và ma trận 19 | [ ] | [ ] | [ ] |
| API/kiểm thử/môi trường dự kiến | [ ] | [ ] | [ ] |

**Phần cần sửa (file/mục và ý muốn đổi):**

**Các câu còn Open và mốc muốn giải quyết:**

**Người xác nhận:**  
**Ngày xác nhận thực tế:** ____/____/______

> Lưu file sau khi điền. Agent kiểm mâu thuẫn giữa các câu (đặc biệt Q04/Q07/Q21–Q25 và Q12/Q26–Q28), ghi DEC/CR, rồi cập nhật SRS/UC/UML/design/test. Không coi lời đề nghị “hoàn thiện tài liệu” là duyệt các đáp án chưa chọn; không coi duyệt tài liệu là đã chạy code/test.
