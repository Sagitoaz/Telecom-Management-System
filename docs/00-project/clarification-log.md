# Nhật ký làm rõ yêu cầu – Telecom Subscription Management System

**Mã tài liệu:** DOC-PROJ-003  
**Phiên bản:** 0.1  
**Ngày cập nhật:** 14/10/2026  
**Trạng thái:** Bản nháp – chờ người phụ trách dự án duyệt  
**Giai đoạn:** Phase 0 – Project Definition & Scope

## 1. Mục đích và căn cứ

Tài liệu theo dõi các điểm chưa rõ, khác biệt giữa tài liệu, quyết định đã được ghi nhận và đề xuất cần duyệt trước khi đặc tả SRS, mô hình đối tượng, CSDL, API và kế hoạch kiểm thử.

| Tài liệu nguồn | Vai trò |
|---|---|
| [Đề bài gốc](../requirement.md) | Căn cứ đối chiếu yêu cầu bắt buộc và điều kiện nghiệm thu. |
| [Tổng quan dự án](overview.md) | Nguồn các quyết định DEC-01–DEC-06 và câu hỏi OQ-01–OQ-07 hiện có. |
| [Phạm vi và thuật ngữ](scope-and-glossary.md) | Nguồn phạm vi SC-01–SC-06, chất lượng QS-01–QS-07 và thuật ngữ nghiệp vụ. |

Các quyết định trong tài liệu hiện có được ghi lại đúng nguồn; nhật ký này không khẳng định người ra đề đã phê duyệt chúng. Nếu một cách hiểu có thể làm thay đổi yêu cầu gốc, cần xác nhận riêng trước khi dùng làm tiêu chí nghiệm thu. Mọi nội dung có nhãn **Đề xuất** đều chưa được duyệt.

## 2. Quy ước theo dõi

- **Open:** Chưa có câu trả lời hoặc chưa đủ căn cứ để chốt.
- **Proposed:** Có phương án đề xuất, đang chờ duyệt.
- **Resolved:** Đã có quyết định, người xác nhận, ngày xác nhận và căn cứ được lưu lại.
- **Superseded:** Được thay thế bởi quyết định khác; giữ lại để tra cứu lịch sử.
- **P0:** Cần chốt trước khi hoàn tất SRS, thiết kế dữ liệu hoặc tiêu chí nghiệm thu liên quan.
- **P1:** Cần chốt trước khi triển khai và kiểm thử chức năng liên quan.

Người phụ trách dự án tổng hợp câu trả lời; các vấn đề diễn giải đề bài và nghiệm thu cần được người ra đề/người nghiệm thu xác nhận. Vai trò này chưa được gán cho cá nhân cụ thể. Không chuyển trạng thái chỉ vì có đề xuất trong tài liệu.

## 3. Yêu cầu gốc cần bảo toàn

Các mã REQ dưới đây chỉ dùng để truy vết trong nhật ký này, không thay thế mã HLR hoặc QS của tài liệu nguồn.

| Mã | Nội dung từ đề bài | Điểm cần làm rõ |
|---|---|---|
| REQ-01 | Thiết kế mô hình đối tượng và CSDL quan hệ giữa các thực thể. | OQ-01, OQ-08, OQ-09, OQ-12 |
| REQ-02 | API đấu nối mới thuê bao vào mạng. | OQ-05, OQ-08, OQ-12 |
| REQ-03 | API đăng ký và hủy gói cước. | OQ-06, OQ-07, OQ-09 |
| REQ-04 | API cộng tiền và trừ tiền khi sử dụng dịch vụ. | OQ-01, OQ-02, OQ-06, OQ-10 |
| REQ-05 | Luồng báo cáo hàng ngày và API liệt kê thuê bao có tiêu dùng từ X đến Y trên gói A, B. | OQ-03, OQ-13 |
| REQ-06 | Job hàng ngày kiểm tra và xóa dữ liệu gói cước, tài khoản hết hạn. | OQ-07, OQ-11 |
| REQ-07 | CSDL có tối thiểu 500.000 bản ghi. | OQ-04 |
| REQ-08 | Tool kiểm thử đăng ký gói cước và trừ tiền đạt tối thiểu 30 TPS đồng thời. | OQ-04 |
| REQ-09 | Giả lập 50% request trừ tiền đồng thời trên cùng thuê bao và cùng tài khoản. | OQ-04, OQ-14 |
| REQ-10 | Có công cụ và phương pháp kiểm chứng trừ tiền đúng, đủ khi có đồng thời. | OQ-02, OQ-10, OQ-14 |

## 4. Quyết định đã ghi nhận trong tài liệu hiện có

Nguồn: mục 6.2 của `overview.md` và mục 7.1 của `scope-and-glossary.md`. Các mã DEC được giữ nguyên để tránh mất liên kết.

| Mã | Nội dung đã ghi nhận | Tình trạng đối chiếu đề bài |
|---|---|---|
| DEC-01 | Một khách hàng có thể sở hữu nhiều thuê bao. | Cần xác nhận cách hiểu câu “mỗi khách hàng ... với 1 số thuê bao”; theo dõi OQ-08. |
| DEC-02 | Cấp số tự động hoặc chọn số khả dụng qua CSKH. | Chi tiết bổ sung từ tài liệu dự án; đề bài không quy định hai hình thức này. Theo dõi OQ-05. |
| DEC-03 | Đăng ký gói cước và trừ tiền là hai chức năng độc lập, có thể phối hợp. | Chưa quyết định xử lý thành công một phần; theo dõi OQ-06. |
| DEC-04 | Hủy gói có hiệu lực khi kết thúc chu kỳ hiện tại. | Chi tiết bổ sung từ tài liệu dự án; cần chốt chu kỳ và chuyển trạng thái tại OQ-07, OQ-09. |
| DEC-05 | Không đủ số dư thì rollback, không thay đổi số dư và trả lỗi. | Chưa chốt đơn vị tiền, tài khoản hết hạn và các trường hợp biên; theo dõi OQ-10. |
| DEC-06 | Soft delete dữ liệu hết hạn, giữ lịch sử nghiệp vụ cần thiết. | Cần xác nhận có đáp ứng từ “xóa” trong đề bài hay không; theo dõi OQ-11. |

## 5. Danh sách vấn đề cần làm rõ

OQ-01–OQ-07 giữ nguyên chủ đề trong hai tài liệu nguồn. OQ-08 trở đi bổ sung các khoảng trống khi đối chiếu đề bài. Tất cả hiện có trạng thái **Open**; phương án bên dưới chỉ phục vụ duyệt.

| Mã | Chủ đề | Mức | Cần chốt trước |
|---|---|---|---|
| OQ-01 | Loại tài khoản và khởi tạo | P0 | Mô hình dữ liệu và API tài khoản |
| OQ-02 | Nhận diện yêu cầu trùng và retry | P0 | Thiết kế giao dịch và đối soát |
| OQ-03 | Điều kiện báo cáo tiêu dùng | P0 | SRS và truy vấn báo cáo |
| OQ-04 | Quy mô dữ liệu và cách đo hiệu năng | P0 | Chốt kế hoạch kiểm thử hiệu năng |
| OQ-05 | Cấp số và chống cấp trùng | P1 | Triển khai đấu nối |
| OQ-06 | Phối hợp đăng ký gói và trừ tiền | P0 | Luồng đăng ký và giao dịch |
| OQ-07 | Hủy gói, hết hạn và chuyển trạng thái | P0 | Mô hình vòng đời dữ liệu |
| OQ-08 | Quan hệ khách hàng – thuê bao | P0 | Chốt phạm vi và mô hình dữ liệu |
| OQ-09 | Chu kỳ, gia hạn và đăng ký lại | P0 | Thiết kế đăng ký gói |
| OQ-10 | Quy tắc tiền và tính đúng của giao dịch | P0 | Thiết kế tài khoản và kiểm chứng |
| OQ-11 | Ý nghĩa “xóa” và bảo toàn lịch sử | P0 | Chốt nghiệm thu job hết hạn |
| OQ-12 | Thông tin khách hàng khi đấu nối | P1 | Đặc tả API đấu nối |
| OQ-13 | Phạm vi luồng báo cáo hàng ngày | P0 | Chốt đầu ra báo cáo |
| OQ-14 | Tải đồng thời 50% và phương pháp đối soát | P0 | Thiết kế tool kiểm thử |
| OQ-15 | Hợp đồng API và quyền truy cập | P1 | Triển khai tích hợp CSKH |

### OQ-01. Loại tài khoản và quy tắc khởi tạo

**Căn cứ:** Đề bài cho phép nhiều tài khoản phục vụ các dịch vụ khác nhau; SC-04 và OQ-01 hiện có chưa mô tả chi tiết.

**Cần xác nhận:** Có tài khoản chính và tài khoản riêng theo dịch vụ không? Mỗi loại được tạo khi đấu nối, khi đăng ký gói hay qua lệnh riêng? Một tài khoản dùng cho nhiều gói được không? Số dư và thời hạn ban đầu lấy từ đâu? Khi gói kết thúc thì tài khoản liên quan được giữ hay hết hạn theo?

**Đề xuất:** Liệt kê từng loại tài khoản với thời điểm tạo, đơn vị số dư, thời hạn, gói/dịch vụ được phép sử dụng; không mặc định quan hệ một gói – một tài khoản.

**Ảnh hưởng:** Quan hệ Account–Subscriber–Package, API cộng/trừ tiền và job hết hạn.

### OQ-02. Yêu cầu trùng lặp và retry

**Căn cứ:** OQ-02 hiện có và yêu cầu kiểm chứng trừ tiền đúng, đủ khi đồng thời.

**Cần xác nhận:** CSKH có gửi mã yêu cầu duy nhất không? Mã có phạm vi duy nhất nào và được lưu bao lâu? Gửi lại cùng mã, cùng nội dung trả kết quả cũ hay lỗi? Cùng mã nhưng khác nội dung xử lý thế nào? Hai request cùng mã đến đồng thời hoặc retry sau timeout phải trả kết quả gì?

**Đề xuất:** Dùng mã yêu cầu để bảo đảm một nghiệp vụ chỉ tác động số dư một lần; trả lại kết quả đã ghi nhận cho retry hợp lệ và từ chối mã bị tái sử dụng với nội dung khác. Chốt riêng phạm vi áp dụng cho đăng ký, hủy, cộng tiền và trừ tiền.

**Ảnh hưởng:** Ràng buộc duy nhất, lưu kết quả giao dịch, kiểm thử retry và cách tính TPS giao dịch mới.

### OQ-03. Điều kiện báo cáo tiêu dùng

**Căn cứ:** API truy vấn thuê bao có tiêu dùng từ X đến Y trên gói A, B; OQ-03 hiện có.

**Cần xác nhận:** A, B có nghĩa dùng ít nhất một gói (OR) hay phải dùng cả hai (AND)? “Tiêu dùng” chỉ tính trừ tiền thành công cho dịch vụ hay gồm phí đăng ký? Mỗi giao dịch liên kết gói bằng thông tin nào, đặc biệt khi tài khoản dùng chung? Thời gian lọc là thời điểm sử dụng hay thời điểm ghi nhận? Có tính giao dịch của gói/tài khoản đã hết hạn không?

**Đề xuất:** Dùng OR, chỉ tính giao dịch trừ tiền tiêu dùng thành công; trả thuê bao không trùng, có phân trang và thứ tự ổn định. Lưu liên kết gói/dịch vụ tại thời điểm giao dịch để truy vấn được lịch sử sau khi hết hạn. Nếu X, Y là ngày, chuyển thành khoảng từ đầu ngày X đến trước đầu ngày sau Y theo múi giờ thống nhất.

**Ảnh hưởng:** Thuộc tính giao dịch, truy vấn, chỉ mục và dữ liệu kiểm thử báo cáo.

### OQ-04. Dữ liệu 500.000 bản ghi và tiêu chí 30 TPS

**Căn cứ:** Các yêu cầu hiệu năng trong đề bài; HLR-01–HLR-04, QS-01–QS-04 và OQ-04 hiện có.

**Cần xác nhận:**

1. 500.000 bản ghi là tổng các bảng, số thuê bao hay số giao dịch? Phân bố dữ liệu và tỷ lệ bản ghi còn hiệu lực cần như thế nào? Quy mô này phải có sẵn trước khi chạy tải hay được tích lũy trong khi chạy?
2. 30 TPS áp dụng riêng cho đăng ký và trừ tiền, hay tổng hai loại lệnh? Hai nghiệp vụ phải cùng chạy hay được đo riêng? Nếu chạy hỗn hợp, tỷ lệ mỗi loại là bao nhiêu?
3. TPS đếm request gửi, response hoàn tất hay nghiệp vụ mới thành công? Lỗi nghiệp vụ dự kiến, lỗi hệ thống và retry được tính thế nào?
4. Cấu hình CPU/RAM, CSDL, số instance, mạng và máy phát tải là gì? Thời gian khởi động tải, thời gian đo ổn định, số kết nối đồng thời, giới hạn độ trễ p95/p99 và tỷ lệ lỗi chấp nhận là bao nhiêu?

**Đề xuất:** Đo trên dữ liệu được sinh sẵn; công bố số bản ghi từng bảng. Có bài đo riêng từng nghiệp vụ đạt ít nhất 30 nghiệp vụ mới thành công/giây và bài tải hỗn hợp để kiểm tra tác động đồng thời. Tạm đề xuất 2 phút khởi động tải, 10 phút đo ổn định; thời lượng và ngưỡng độ trễ chờ duyệt. Báo cáo riêng số gửi, thành công, từ chối nghiệp vụ, lỗi hệ thống, timeout và retry, kèm cấu hình môi trường.

**Ảnh hưởng:** Điều kiện nghiệm thu, bộ sinh dữ liệu và cách diễn giải kết quả. Chưa kết luận đạt yêu cầu chỉ dựa vào tốc độ gửi 30 request/giây.

### OQ-05. Cấp số và chống cấp trùng

**Căn cứ:** DEC-02, SC-01 và OQ-05 hiện có; kho số là chi tiết bổ sung so với đề bài.

**Cần xác nhận:** Có giữ số tạm khi khách chọn không hay chỉ kiểm tra tại lúc đấu nối? Nếu số vừa được cấp cho yêu cầu khác, trả lỗi hay tự chọn số thay thế? Có cho tái sử dụng số đã ngừng hoạt động không?

**Đề xuất:** Nếu chưa yêu cầu giữ số, chỉ hiển thị khả dụng tại thời điểm tra cứu; cấp phát phải nguyên tử và có ràng buộc không trùng trong CSDL. Tranh chấp số được chọn trả lỗi rõ ràng để chọn lại; cấp tự động chọn một số còn khả dụng.

**Ảnh hưởng:** Kho số, transaction đấu nối và kiểm thử hai yêu cầu cùng chọn một số.

### OQ-06. Phối hợp đăng ký gói cước và trừ tiền

**Căn cứ:** DEC-03, SC-02 và OQ-06 hiện có.

**Cần xác nhận:** Phí đăng ký có thuộc phạm vi không? Nếu có, CSKH hay backend điều phối hai chức năng? Đăng ký thành công nhưng trừ tiền thất bại có được sử dụng gói không? Trừ tiền thành công nhưng đăng ký lỗi thì hoàn tiền hay thử lại? API báo thành công ở thời điểm nào?

**Đề xuất:** Nếu có thu phí, gói chỉ có hiệu lực khi đã thu tiền thành công; cần quyết định rõ cách bảo đảm nguyên tử hoặc cơ chế bù và đối soát phù hợp với ranh giới hệ thống. Nếu đăng ký không thu phí, ghi rõ và giữ hai API độc lập. Chưa lựa chọn phương án kỹ thuật khi chưa chốt nghiệp vụ.

**Ảnh hưởng:** Trạng thái đăng ký, hợp đồng API, giao dịch hoàn tiền nếu có và kiểm thử lỗi từng bước.

### OQ-07. Hủy gói, hết hạn và chuyển trạng thái

**Căn cứ:** DEC-04, SC-03, SC-06 và OQ-07 hiện có.

**Cần xác nhận:** Yêu cầu hủy được lưu dưới trạng thái nào? Gửi hủy nhiều lần xử lý ra sao? Sau thời điểm hết hạn nhưng trước lần chạy job, gói/tài khoản có còn được dùng không? Khi hủy, gia hạn, trừ tiền và job cùng tác động thì nghiệp vụ nào hợp lệ? Job chạy giờ nào, múi giờ nào, xử lý lỗi và chạy lại thế nào?

**Đề xuất:** Dùng thời hạn thực tế để kiểm tra quyền sử dụng tại lúc xử lý request; không kéo dài hiệu lực chỉ vì job chưa chạy. Ghi nhận hủy chờ cuối chu kỳ. Job xử lý theo lô, chạy lại không tạo tác động lặp và ghi nhận số bản ghi thành công/thất bại. Đề xuất múi giờ nghiệp vụ `Asia/Ho_Chi_Minh`; giờ chạy và trạng thái cụ thể chờ duyệt.

**Ảnh hưởng:** Quy tắc vòng đời, xử lý đồng thời, scheduler và trường hợp biên tại thời điểm hết hạn.

### OQ-08. Quan hệ khách hàng – thuê bao

**Căn cứ:** Đề bài viết “mỗi khách hàng thực tế được đại diện với 1 số thuê bao”; DEC-01 và SC-01 cho phép một khách hàng có nhiều thuê bao.

**Cần xác nhận:** Câu này bắt buộc quan hệ 1–1 hay chỉ mô tả cách đại diện một khách hàng qua thuê bao? Có chấp nhận quan hệ 1–N trong phạm vi bài làm không?

**Đề xuất:** Giữ hướng 1–N của tài liệu hiện có nếu được người ra đề chấp nhận. Nếu bắt buộc 1–1, cập nhật DEC-01, phạm vi, thuật ngữ và ràng buộc dữ liệu tương ứng.

**Ảnh hưởng:** Phạm vi, khóa/ràng buộc dữ liệu khách hàng và API đấu nối. Đây là điểm khác biệt cần xác nhận, chưa tự kết luận là mâu thuẫn.

### OQ-09. Chu kỳ, gia hạn và đăng ký lại gói

**Căn cứ:** Đề bài yêu cầu thời gian sử dụng; DEC-04 cần khái niệm kết thúc chu kỳ.

**Cần xác nhận:** Chu kỳ là số giờ/ngày tính từ đăng ký hay theo lịch? Có tự gia hạn không? Có được đăng ký lại cùng gói khi còn hiệu lực hoặc đang chờ hủy không? Hủy rồi đăng ký lại có mở chu kỳ mới không? Có các gói loại trừ lẫn nhau không?

**Đề xuất:** Mỗi gói khai báo rõ cách tính thời hạn; khoảng hiệu lực gồm thời điểm bắt đầu và không gồm thời điểm kết thúc. Chưa đưa tự gia hạn vào phạm vi nếu chưa được yêu cầu. Đề xuất từ chối đăng ký trùng cùng gói khi lần đăng ký trước còn hiệu lực, kể cả đang chờ hủy; đây là quy tắc cần duyệt.

**Ảnh hưởng:** Dữ liệu đăng ký, tính thời hạn, kiểm thử đồng thời và khả năng sinh đủ đăng ký hợp lệ khi đo TPS.

### OQ-10. Quy tắc tiền và tính đúng của giao dịch

**Căn cứ:** API cộng/trừ tiền, DEC-05 và yêu cầu kiểm chứng giao dịch.

**Cần xác nhận:** Đơn vị là VND nguyên hay có phần thập phân? Có số dư ban đầu khác 0 không? Cộng/trừ số tiền bằng 0 hoặc âm, giao dịch trên tài khoản hết hạn và cộng tiền để gia hạn xử lý thế nào? Có hạn mức giao dịch và nghiệp vụ hoàn/đảo tiền không?

**Đề xuất:** Nếu dùng VND nguyên, lưu số tiền bằng kiểu chính xác; nhận số tiền dương và phân biệt chiều cộng/trừ bằng loại nghiệp vụ. Không cho số dư âm theo DEC-05. Cập nhật số dư và ghi giao dịch thành công trong cùng một database transaction. Lỗi nghiệp vụ không làm thay đổi số dư; log yêu cầu thất bại nếu cần phải phân biệt với giao dịch tài chính thành công.

**Ảnh hưởng:** Kiểu dữ liệu tiền, validation, tính nguyên tử và công thức đối soát tại OQ-14.

### OQ-11. Ý nghĩa “xóa” dữ liệu hết hạn

**Căn cứ:** Đề bài yêu cầu “xóa”; DEC-06 và SC-06 chọn soft delete.

**Cần xác nhận:** Người nghiệm thu có chấp nhận soft delete/cập nhật trạng thái không? Đối tượng xử lý là lần đăng ký gói hay cả định nghĩa gói trong danh mục? Tài khoản hết hạn còn số dư xử lý ra sao? Lịch sử giữ bao lâu và có yêu cầu xóa vật lý sau một thời hạn không?

**Đề xuất:** Xử lý lần đăng ký và tài khoản hết hạn bằng trạng thái/soft delete; giữ danh mục gói và lịch sử giao dịch để báo cáo, đối soát. Không tự xóa hoặc chuyển số dư còn lại khi chưa có quy tắc. Nếu bắt buộc xóa vật lý, cần chốt cách lưu trữ lịch sử và bảo toàn tham chiếu trước khi thiết kế job.

**Ảnh hưởng:** Khả năng đáp ứng đề bài, quan hệ CSDL, báo cáo lịch sử và đối soát. Chưa coi DEC-06 là bằng chứng nghiệm thu yêu cầu “xóa”.

### OQ-12. Thông tin khách hàng và phạm vi đấu nối

**Căn cứ:** Đề bài nhắc CMT, ngày sinh, nghề nghiệp và API đấu nối; SC-01 giới hạn ở backend.

**Cần xác nhận:** Trường nào bắt buộc? CMT/CCCD có phải khóa nhận diện khách hàng không? Khi khách hàng đã tồn tại thì dùng lại hồ sơ hay cập nhật? Đấu nối có tạo tài khoản mặc định không? Có yêu cầu xác minh giấy tờ hoặc kích hoạt trên mạng thực không?

**Đề xuất:** Chốt danh sách trường và quy tắc kiểm tra; hỗ trợ nhận diện khách hàng đã tồn tại theo khóa nghiệp vụ đã duyệt. Giữ phạm vi đấu nối ở cấp ứng dụng theo EX-03; chưa thêm tích hợp mạng thực hay xác minh giấy tờ bên ngoài.

**Ảnh hưởng:** API đầu vào, dữ liệu cá nhân, quy tắc tránh trùng hồ sơ và dữ liệu giả lập.

### OQ-13. Phạm vi luồng báo cáo hàng ngày

**Căn cứ:** Đề bài nêu “các luồng báo cáo định kỳ hàng ngày”, sau đó liệt kê API truy vấn và job hết hạn; SC-05 hiện mô tả truy vấn theo yêu cầu.

**Cần xác nhận:** API truy vấn và job hết hạn đã đủ hay cần thêm job tổng hợp báo cáo hàng ngày? Nếu cần, đầu ra là bảng tổng hợp, file hay API; gồm chỉ tiêu nào và ngày nghiệp vụ được chốt lúc nào?

**Đề xuất:** Giữ API truy vấn theo yêu cầu; chỉ bổ sung job tổng hợp khi được xác nhận. Khi đó cần chốt kỳ dữ liệu, cách chạy lại và cập nhật báo cáo nếu giao dịch được ghi nhận muộn.

**Ảnh hưởng:** Phạm vi bàn giao, scheduler, dữ liệu báo cáo và tiêu chí nghiệm thu.

### OQ-14. Tải đồng thời 50% và kiểm chứng đúng, đủ

**Căn cứ:** Hai yêu cầu cuối của đề bài; chi tiết hóa phần tải và đối soát của OQ-04.

**Cần xác nhận:** Mẫu số 50% là tổng request trừ tiền hay tổng mọi request trong bài hỗn hợp? 50% cùng một cặp thuê bao–tài khoản cố định hay chia thành nhiều nhóm tranh chấp? Cần mức chồng lấn xử lý tối thiểu nào? Có yêu cầu tình huống đủ tiền, thiếu tiền, retry, timeout và gián đoạn không?

**Đề xuất kịch bản:** Trong bài trừ tiền riêng, chọn chính xác 50% request gửi đến một cặp thuê bao–tài khoản cố định, 50% còn lại phân tán sang cặp khác. Dùng nhiều worker đồng thời và lưu thời điểm gửi/nhận, số request đang chờ xử lý để chứng minh tải chồng lấn. Nếu cần chứng minh transaction thực sự tranh chấp trong backend, bổ sung dấu vết xử lý phía server. Gửi tuần tự vào một tài khoản không đủ chứng minh yêu cầu đồng thời.

**Đề xuất kiểm chứng:**

1. Chụp số dư đầu, sinh mã yêu cầu duy nhất và lưu dữ liệu đầu vào để có kết quả kỳ vọng độc lập với phản hồi API.
2. Chạy một ca đủ tiền với số request và số tiền biết trước để kiểm tra không mất lần trừ; chạy riêng ca thiếu tiền để kiểm tra số dư không âm và giao dịch bị từ chối không thay đổi tiền. Ca retry xác nhận một mã nghiệp vụ chỉ có một tác động.
3. Sau khi tải kết thúc và các request đã được xác định kết quả, đối chiếu từng mã yêu cầu với kết quả API và giao dịch đã commit. Request timeout phải được tra cứu/đối soát đến khi biết kết quả; chưa coi timeout là chưa trừ tiền.
4. Với từng tài khoản, kiểm tra: `số dư cuối = số dư đầu + tổng cộng tiền thành công - tổng trừ tiền thành công`, mỗi nghiệp vụ thành công có đúng một bản ghi tài chính, không có tác động trùng hoặc giao dịch đã commit nhưng thiếu trong kết quả đối soát.
5. Báo cáo tỷ lệ request nhắm vào cặp cố định, mức tải đồng thời quan sát được, TPS, độ trễ, số thành công/thất bại/chưa xác định và mọi chênh lệch. Chỉ công bố đúng, đủ khi không còn kết quả chưa xác định và không có chênh lệch.

**Ảnh hưởng:** Thiết kế tool phát tải, dữ liệu đầu vào, log giao dịch và bằng chứng nghiệm thu. Công thức số dư một mình không đủ phát hiện các lỗi bù trừ lẫn nhau; phải đối chiếu cả từng nghiệp vụ.

### OQ-15. Hợp đồng API và quyền truy cập

**Căn cứ:** Các API phục vụ CSKH; mục 2.3 của tài liệu phạm vi để mở phương thức tích hợp.

**Cần xác nhận:** API xử lý đồng bộ hay trả mã lệnh để tra cứu sau? Môi trường bài tập có yêu cầu xác thực/phân quyền không? Định dạng lỗi, mã theo dõi yêu cầu và cách trả trạng thái giao dịch sau timeout là gì?

**Đề xuất:** Thống nhất hợp đồng REST API gồm validation, mã lỗi nghiệp vụ, mã yêu cầu và kết quả xử lý trước khi triển khai. Chọn phạm vi xác thực phù hợp yêu cầu bài tập; chưa bổ sung hệ thống quản lý người dùng riêng khi chưa có yêu cầu.

**Ảnh hưởng:** Tích hợp CSKH, retry, tool kiểm thử và phạm vi triển khai.

## 6. Các câu hỏi ưu tiên khi duyệt

Bạn có thể duyệt trước các điểm ảnh hưởng trực tiếp đến cách hiểu đề bài:

1. **OQ-08:** Giữ mô hình một khách hàng có nhiều thuê bao như tài liệu hiện có được không?
2. **OQ-11:** Soft delete có được chấp nhận cho yêu cầu “xóa” dữ liệu hết hạn không?
3. **OQ-04:** 500.000 bản ghi được tính trên đối tượng nào; 30 TPS đo riêng từng nghiệp vụ hay tổng tải hỗn hợp?
4. **OQ-14:** Có chấp nhận cách đo 50% tổng request trừ tiền cùng nhắm vào một cặp thuê bao–tài khoản, với tải thực sự đồng thời không?
5. **OQ-06:** Đăng ký gói có thu phí và cần phối hợp với trừ tiền trong phạm vi bài này không?
6. **OQ-13:** Có cần một job báo cáo hàng ngày riêng ngoài API truy vấn và job xử lý hết hạn không?

Các câu hỏi còn lại tiếp tục được chốt khi xây dựng SRS. Việc duyệt bố cục tài liệu không đồng nghĩa phê duyệt tất cả đề xuất nghiệp vụ trong tài liệu.

## 7. Cách ghi nhận câu trả lời và đóng vấn đề

Với mỗi OQ được trả lời, bổ sung bản ghi theo mẫu dưới đây; giữ nguyên mã OQ khi cập nhật tài liệu liên quan.

| Trường | Nội dung cần ghi |
|---|---|
| Mã OQ | Mã vấn đề được giải quyết. |
| Quyết định cuối cùng | Quy tắc cụ thể, gồm trường hợp biên và ngoại lệ nếu có. |
| Người xác nhận, ngày xác nhận | Cá nhân/vai trò xác nhận và ngày thực tế; không điền thay khi chưa có phản hồi. |
| Căn cứ | Phản hồi người ra đề, biên bản hoặc nội dung duyệt có thể tra cứu. |
| Tài liệu cần cập nhật | Overview, Scope, SRS, mô hình dữ liệu, API hoặc Test Plan tương ứng. |
| Bằng chứng nghiệm thu | Ví dụ đầu vào/đầu ra hoặc cách đo có thể kiểm chứng quyết định. |
| Trạng thái | Chuyển sang Resolved sau khi đủ căn cứ; nếu thay quyết định cũ, dẫn mã quyết định thay thế. |

Hiện chưa có câu trả lời mới được phê duyệt trong nhật ký này. Khi chốt OQ-08 hoặc OQ-11, cần cập nhật nhất quán DEC-01 hoặc DEC-06 và các đoạn liên quan trong hai tài liệu nguồn.

## 8. Lịch sử cập nhật

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 0.1 | 14/10/2026 | Tạo bản nháp từ đề bài và hai tài liệu Phase 0; truy vết yêu cầu, giữ DEC-01–DEC-06 và OQ-01–OQ-07, bổ sung OQ-08–OQ-15 cùng phương án để duyệt. |
