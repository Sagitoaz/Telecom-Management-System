# Nhật ký làm rõ yêu cầu – Telecom Subscription Management System

**Mã tài liệu:** DOC-PROJ-003

**Phiên bản:** 1.0

**Ngày xác nhận:** 14/10/2026

**Trạng thái:** Baseline Phase 0 đã duyệt; các chi tiết còn mở có mốc xử lý

**Người xác nhận:** Chủ dự án – người dùng, có quyền quyết định nghiệm thu cuối cùng

**Giai đoạn:** Phase 0 – Project Definition & Scope – hoàn thành

## 1. Căn cứ và thẩm quyền

- [Đề bài gốc](../requirement.md): giữ nguyên nội dung; phân biệt yêu cầu gốc với cách diễn giải được chủ dự án duyệt.
- [Tổng quan](overview.md), [phạm vi và thuật ngữ](scope-and-glossary.md), [roadmap](../ROADMAP.md): được đồng bộ theo các quyết định dưới đây.
- Căn cứ duyệt: 14 lựa chọn trong phiếu Phase 0 và yêu cầu trong chat “đã điền xong, giờ update tài liệu và đẩy lên git”. Q13=C, ghi chú của chủ dự án: “vai trò của tôi là người làm chủ mọi yếu tố trong dự án”. Chủ dự án xác nhận quyền quyết định nghiệm thu cuối cùng cho dự án này; không ghi thay thành đã có xác nhận từ một người ra đề bên ngoài.
- Phiếu tạm đã được chuyển đầy đủ thành bảng quyết định bên dưới và được xóa theo yêu cầu. Bảng này là bản ghi duyệt được lưu lâu dài, không phụ thuộc file tạm.

## 2. Quy ước trạng thái

**Resolved:** Nội dung câu hỏi đã được quyết định đầy đủ. **Open:** Còn chi tiết cần quyết định; được phép chuyển giai đoạn nếu có người phụ trách và hạn chốt. **Superseded:** Quyết định cũ được thay thế, giữ lịch sử để tra cứu.

Một lựa chọn có thể chốt định hướng nhưng chưa đóng toàn bộ OQ. Các đề xuất chưa được chọn ở bản nháp 0.1 không tự trở thành yêu cầu. Mọi quyết định mới cần ghi người duyệt, ngày thực tế, lý do thay đổi và tài liệu chịu ảnh hưởng.

## 3. Bản ghi đầy đủ 14 lựa chọn đã duyệt

Tất cả các dòng được chủ dự án duyệt ngày **14/10/2026**. Mã Q giữ lại để truy vết sau khi xóa phiếu; không phải đường dẫn tới file đã xóa.

| Câu | Đáp án | Nội dung được duyệt | Truy vết |
|---|---|---|---|
| Q01 | A | Bàn giao backend REST API, CSDL quan hệ, job hàng ngày, công cụ sinh dữ liệu/phát tải/đối soát và tài liệu. Không xây giao diện CSKH/mobile, tích hợp mạng thật, ngân hàng, tính cước hay BI hoàn chỉnh. | SC-01–SC-06, EX-01–EX-06 |
| Q02 | A | Một khách hàng có nhiều thuê bao; mỗi thuê bao thuộc một khách hàng tại một thời điểm. | DEC-01, OQ-08 |
| Q03 | A | Cấp số tự động và chọn số khả dụng; xem danh sách không giữ số. Cấp phát phải không trùng; số được chọn đã bị cấp thì trả lỗi để chọn lại. | DEC-02, OQ-05 |
| Q04 | B | Đăng ký gói có thu phí, backend điều phối. Chỉ có hiệu lực khi thu phí thành công; lỗi một bước không để lại gói hoạt động hoặc khoản trừ tiền thành công một phần. Cơ chế transaction chốt ở thiết kế. | DEC-03, OQ-06 |
| Q05 | A | Hủy có hiệu lực cuối chu kỳ hiện tại; khách hàng dùng gói đến hết chu kỳ. | DEC-04, OQ-07, OQ-09 |
| Q06 | A | Soft delete/cập nhật trạng thái lần đăng ký và tài khoản hết hạn; ngừng sử dụng trong nghiệp vụ hiện tại, giữ lịch sử báo cáo/đối soát; không xóa định nghĩa gói chỉ vì một lần đăng ký hết hạn. | DEC-06, OQ-11 |
| Q07 | B | Có job tổng hợp tiêu dùng hàng ngày riêng, ngoài API truy vấn và job hết hạn. Đầu ra, chỉ tiêu và lịch chạy chốt trong SRS. | DEC-07, OQ-13 |
| Q08 | A | Tổng các bảng dữ liệu nghiệp vụ đạt tối thiểu 500.000 bản ghi trước đo tải; phân bố đủ chạy các nghiệp vụ hợp lệ và thống kê từng bảng. | DEC-08, OQ-04 |
| Q09 | B | Đăng ký gói và trừ tiền chạy cùng lúc, mỗi nghiệp vụ ít nhất 30 TPS, tổng ít nhất 60 TPS. TPS đếm nghiệp vụ mới hoàn tất thành công; không đếm retry trả kết quả cũ như nghiệp vụ mới; báo riêng lỗi nghiệp vụ/hệ thống/timeout. | DEC-09, OQ-04 |
| Q10 | B | 50% tổng request trừ tiền chia thành nhiều nhóm cặp thuê bao–tài khoản có tranh chấp; phải có request thực sự chồng lấn. Cách chia nhóm chốt ở Test Plan. Chủ dự án chấp nhận cách diễn giải theo nhóm thay cho một cặp cố định. | DEC-10, OQ-14 |
| Q11 | A | Không cho số dư âm; thiếu tiền thì thất bại, không thay đổi số dư và không ghi giao dịch tài chính thành công. | DEC-05, OQ-10 |
| Q12 | A | Đối chiếu từng mã yêu cầu với giao dịch đã commit, kiểm tra một tác động khi retry và số dư đầu/cuối; có ca đủ tiền, thiếu tiền, timeout; không kết luận đúng/đủ khi còn request chưa xác định kết quả. | DEC-11, OQ-02, OQ-10, OQ-14 |
| Q13 | C | Chủ dự án có quyền quyết định nghiệm thu cuối cùng và xác nhận các lựa chọn; vai trò được ghi tại mục 1. | Thẩm quyền baseline |
| Q14 | A | Chốt Phase 0 khi tài liệu nhất quán; chi tiết chưa đủ đáp án vẫn Open, chủ dự án phụ trách, trợ lý tổng hợp/đề xuất đúng mốc. Không tự chọn thay quy tắc chưa duyệt. | M0, danh sách còn mở |

Ngoài ghi chú vai trò tại Q13, phiếu không có ghi chú nghiệp vụ bổ sung. Vì vậy không suy diễn thêm loại tài khoản, thời hạn, phí, hoàn phí hay công nghệ khóa từ các đáp án.

## 4. Danh mục quyết định baseline

| Mã | Quyết định có hiệu lực | Căn cứ |
|---|---|---|
| DEC-01 | Customer–Subscriber là 1–N; mỗi thuê bao thuộc một khách hàng tại một thời điểm. | Q02=A |
| DEC-02 | Có cấp số tự động/chọn số; tra cứu không giữ số; không cấp trùng; tranh chấp số chọn trả lỗi. | Q03=A |
| DEC-03 | Đăng ký và trừ tiền là các chức năng riêng; backend phối hợp thu phí khi đăng ký, bảo đảm không thành công một phần. | Q04=B; làm rõ DEC-03 cũ |
| DEC-04 | Hủy gói có hiệu lực cuối chu kỳ hiện tại. | Q05=A |
| DEC-05 | Thiếu tiền: thất bại, không thay đổi số dư, không có giao dịch tài chính thành công; không thấu chi. | Q11=A |
| DEC-06 | Hết hạn: soft delete/trạng thái lần đăng ký và tài khoản, giữ lịch sử, giữ danh mục gói. | Q06=A |
| DEC-07 | Có job tổng hợp tiêu dùng hàng ngày bên cạnh API báo cáo và job hết hạn. | Q07=B |
| DEC-08 | Tổng bản ghi nghiệp vụ tối thiểu 500.000, có sẵn trước đo, thống kê từng bảng. | Q08=A |
| DEC-09 | Hai nghiệp vụ cùng chạy, mỗi loại ≥30 TPS thành công mới, tổng ≥60 TPS; retry không tăng số nghiệp vụ mới. | Q09=B |
| DEC-10 | 50% tổng request trừ tiền vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp và chồng lấn thực tế. | Q10=B |
| DEC-11 | Đối soát từng yêu cầu/giao dịch, một tác động khi retry, số dư đầu/cuối; có ca đủ/thiếu tiền/timeout. | Q12=A |

Định nghĩa “xóa”, mô hình 1–N, phạm vi job báo cáo, tổng 500.000 bản ghi, mục tiêu tải hỗn hợp và phân nhóm 50% là các diễn giải đã được chủ dự án có quyền nghiệm thu xác nhận. Không sửa lại đề bài gốc để làm mất dấu các diễn giải này.

## 5. Truy vết yêu cầu gốc

Các mã REQ chỉ dùng trong nhật ký, giữ liên tục từ bản 0.1.

| Mã | Yêu cầu gốc | Quyết định và phần còn mở |
|---|---|---|
| REQ-01 | Mô hình đối tượng và CSDL quan hệ | DEC-01; OQ-01, OQ-09, OQ-12 |
| REQ-02 | API đấu nối thuê bao | DEC-02; OQ-05, OQ-12, OQ-15 |
| REQ-03 | API đăng ký/hủy gói | DEC-03, DEC-04; OQ-06, OQ-07, OQ-09 |
| REQ-04 | API cộng/trừ tiền | DEC-05, DEC-11; OQ-01, OQ-02, OQ-10 |
| REQ-05 | Báo cáo hàng ngày và truy vấn tiêu dùng X–Y trên gói A, B | DEC-07; OQ-03, OQ-13 |
| REQ-06 | Job hàng ngày kiểm tra/xóa gói và tài khoản hết hạn | DEC-06; OQ-07, OQ-11 |
| REQ-07 | CSDL tối thiểu 500.000 bản ghi | DEC-08; OQ-04 |
| REQ-08 | Tool đăng ký/trừ tiền tối thiểu 30 TPS đồng thời | DEC-09; OQ-04 |
| REQ-09 | 50% request đồng thời cùng thuê bao/tài khoản | DEC-10; OQ-14 |
| REQ-10 | Tool/phương pháp kiểm chứng đúng và đủ khi đồng thời | DEC-11; OQ-02, OQ-10, OQ-14 |

## 6. Trạng thái và kế hoạch xử lý OQ

**Người phụ trách tất cả OQ còn mở:** Chủ dự án (người dùng) quyết định; trợ lý tổng hợp và đề xuất. Hạn chốt là mốc công việc, không phải ngày lịch. Các vấn đề đã chốt định hướng nhưng còn chi tiết vẫn giữ Open; không chặn việc đóng Phase 0.

| Mã | Trạng thái | Đã chốt | Còn cần quyết định | Hạn chốt |
|---|---|---|---|---|
| OQ-01 | Open | Một thuê bao có nhiều tài khoản dịch vụ | Loại tài khoản, tạo khi nào, số dư/thời hạn đầu, quan hệ với gói | SRS trước thiết kế tài khoản/gói |
| OQ-02 | Open | Một tác động khi retry và đối soát từng yêu cầu (DEC-11) | Mã chống trùng, phạm vi duy nhất, thời gian lưu, xử lý cùng mã khác nội dung/đồng thời | SRS trước thiết kế đăng ký/giao dịch |
| OQ-03 | Open | Có API lọc X–Y và gói, giữ dữ liệu lịch sử | OR/AND gói, định nghĩa tiêu dùng/phí đăng ký, liên kết gói, thời điểm lọc, phân trang | SRS trước thiết kế báo cáo |
| OQ-04 | Open | Tổng 500K có sẵn, cùng chạy ≥30 TPS mỗi loại, định nghĩa TPS (DEC-08/09) | Máy, CSDL, phân bố dữ liệu, thời lượng đo, độ trễ, tỷ lệ lỗi, ảnh hưởng của phí đăng ký lên cách đếm giao dịch | Mục tiêu ở NFR; phép đo đầy đủ trong Test Plan trước chạy tải |
| OQ-05 | Open | Hai cách cấp số; không giữ khi xem; không trùng; tranh chấp trả lỗi (DEC-02) | Tái sử dụng số, quy tắc trạng thái và thiết kế cấp phát nguyên tử | SRS/thiết kế UC01 trước code đấu nối |
| OQ-06 | Open | Có thu phí, backend điều phối, không thành công một phần (DEC-03) | Tài khoản thu phí, mức phí, lỗi/retry và cơ chế transaction cụ thể | SRS rồi thiết kế trước code đăng ký gói |
| OQ-07 | Open | Hủy cuối chu kỳ (DEC-04), có job hết hạn | Trạng thái, hủy lặp, hiệu lực trước khi job chạy, xung đột đồng thời, lịch/múi giờ/chạy lại | SRS trước thiết kế gói/tài khoản/job |
| OQ-08 | Resolved | Quan hệ 1–N được chủ dự án có quyền nghiệm thu duyệt (DEC-01) | Không còn trong phạm vi câu hỏi này | Đã chốt 14/10/2026 |
| OQ-09 | Open | Thời điểm hủy theo DEC-04 | Cách tính chu kỳ, tự gia hạn, đăng ký trùng/lại, gói loại trừ | SRS trước thiết kế đăng ký gói |
| OQ-10 | Open | Không âm/không đủ tiền không thay đổi số dư (DEC-05); đối soát theo DEC-11 | Đơn vị tiền, số tiền hợp lệ, số dư đầu, tài khoản hết hạn, hạn mức/hoàn tiền | SRS trước thiết kế tài khoản/giao dịch |
| OQ-11 | Open | Soft delete/trạng thái và giữ lịch sử/danh mục đã được chấp nhận (DEC-06) | Giữ lịch sử bao lâu, tài khoản hết hạn còn số dư xử lý thế nào | SRS trước thiết kế tài khoản/job |
| OQ-12 | Open | Đấu nối trong backend, không nối mạng thật | Trường hồ sơ bắt buộc, khóa nhận diện CMT/CCCD, hồ sơ đã tồn tại, tài khoản mặc định | SRS UC01 trước thiết kế API đấu nối |
| OQ-13 | Open | Có job tổng hợp hàng ngày (DEC-07) | Chỉ tiêu, đầu ra bảng/file/API, kỳ dữ liệu, lịch chạy, dữ liệu muộn và chạy lại | SRS trước thiết kế báo cáo |
| OQ-14 | Open | Phân nhóm 50% và tiêu chí đúng/đủ đã duyệt (DEC-10/11) | Số nhóm, phân bố, mẫu tải, bằng chứng chồng lấn, tool, cách xác định kết quả timeout | Tính đúng trước thiết kế giao dịch; kịch bản đầy đủ trước chạy tải |
| OQ-15 | Open | REST API phục vụ CSKH | Đồng bộ/bất đồng bộ, lỗi, xác thực/phân quyền, tra cứu sau timeout | SRS UC01 trước hợp đồng API đầu tiên |

### Các ràng buộc cần giữ khi đặc tả chi tiết

- Đăng ký có thu phí có thể tạo giao dịch trừ tiền nội bộ. Test Plan phải phân biệt nghiệp vụ đăng ký với lệnh trừ tiền được phát độc lập; không đếm hai lần một thao tác để đạt TPS.
- Tỷ lệ 50% dùng tổng request trừ tiền làm mẫu số theo Q10; việc phân loại request trong bài hỗn hợp và cách tách giao dịch thu phí nội bộ phải được ghi rõ trước khi đo.
- Với từng tài khoản, kiểm tra `số dư cuối = số dư đầu + tổng cộng tiền thành công - tổng trừ tiền thành công`, đồng thời đối chiếu từng nghiệp vụ; công thức tổng một mình không đủ.
- Không mặc định OR cho báo cáo, VND nguyên, giờ chạy job, tự gia hạn hay cách xử lý số dư hết hạn khi chủ dự án chưa duyệt.

## 7. Điều kiện đóng Phase 0 và thay đổi tiếp theo

Đã có đủ mục tiêu, ranh giới, thuật ngữ, quyết định được duyệt và danh sách OQ có trách nhiệm/mốc xử lý. Chủ dự án chọn Q14=A cho phép đóng Phase 0; Phase 1 chưa bắt đầu. Các OQ còn mở không phải bằng chứng đã hoàn thành SRS hay kiểm thử.

Khi đóng một OQ sau này, ghi quyết định chi tiết, người/ngày xác nhận, căn cứ, tài liệu thay đổi và tiêu chí kiểm chứng. Giữ mã OQ và DEC; nếu thay quyết định thì dẫn quyết định thay thế và lý do.

## 8. Lịch sử cập nhật

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 0.1 | 14/10/2026 | Bản nháp từ đề bài, DEC-01–DEC-06 và OQ-01–OQ-15; các phương án khi đó chưa duyệt. |
| 1.0 | 14/10/2026 | Lưu đủ 14 đáp án và thẩm quyền; làm rõ DEC-03, xác nhận DEC-01–DEC-06, thêm DEC-07–DEC-11; đóng OQ-08, giữ chi tiết còn mở có người/mốc xử lý; chốt Phase 0 và chuyển thông tin khỏi phiếu tạm. |
