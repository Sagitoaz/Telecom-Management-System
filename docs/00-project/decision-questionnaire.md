# Phiếu quyết định còn thiếu trước code

**Mã:** DECISION-PRECODE-001. **0.2 — ĐÃ TIẾP NHẬN Q01–Q24; Q25 CHỜ DUYỆT, 18/10/2026.**
**Nguồn:** clarification-log.md mục 6/12; SRS 0.6, UC01 0.5, các bản DRAFT Phase 2–4.

Mỗi câu đánh dấu một ô [x]; chọn D phải ghi cụ thể. Các ô Q01–Q24 đã chọn được ghi đầy đủ thành DEC-PRE-01–24; các phần thiếu/đề xuất bổ sung vẫn Open. C giữ Open hoặc yêu cầu phân tích thêm, không phải đồng ý mặc định. Bạn có thể chỉ điền Q01–11 trước để ưu tiên UC01; phần sau không chặn UC01 nếu chưa đến phụ thuộc thật.
Không hỏi lại Hybrid, 24h, không âm, thu phí nguyên tử, hủy cuối kỳ, 500K/30+30 TPS hay quyền duyệt đã chốt. Quyết định nhận được sẽ được ghi đầy đủ vào log trước khi áp dụng. Duyệt hướng chưa đồng nghĩa đã hoàn tất schema/API/thuật toán.

## Q01. So khớp hồ sơ đã có

**Liên quan / hạn chốt:** OQ-12; trước M1.
**Ảnh hưởng:** AC-SUB-02–04; Customer/document; canonical payload.

- [ ] **A.** So họ tên (Unicode NFC, bỏ khoảng trắng thừa, giữ dấu, không phân biệt hoa/thường) và ngày sinh; trường tùy chọn khác không tự cập nhật nhưng không chặn đấu nối.
- [x] **B.** So tất cả trường được gửi sau chuẩn hóa; khác bất kỳ trường nào thì từ chối, it nhat 1 truong lien quan den giay to tuy than duoc gui.
- [ ] **C.** Giữ Open; ghi rõ trường nào phải khớp và quy tắc chuẩn hóa trước M1.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:**

## Q02. Giấy tờ mới và xác minh trong bài tập

**Liên quan / hạn chốt:** OQ-12; trước M1.
**Ảnh hưởng:** Precondition UC01; seed/profile resolver; không tự ghép theo tên/ngày sinh.

- [x] **A.** CSKH gửi tham chiếu chứng cứ xác minh từ quy trình ngoài; backend chỉ dùng giấy tờ hợp lệ đã xác minh để tạo/liên kết Customer; liên kết thêm giấy tờ vào khách cũ ngoài UC01.
- [ ] **B.** Demo dùng dữ liệu xác minh được seed/import trước; UC01 không tự xác minh hay tự liên kết giấy tờ mới.
- [ ] **C.** Giữ Open; mô tả cách xác nhận hồ sơ mới và ai cung cấp chứng cứ.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q03. Kiến trúc và stack

**Liên quan / hạn chốt:** Phase 3; trước M3.
**Ảnh hưởng:** architecture/module/C4/ADR; chưa là Accepted trước khi chọn.

- [x] **A.** Một ứng dụng Spring Boot chia module, Java 21, PostgreSQL; JPA cho CRUD, JDBC khi có lý do ở thiết kế transaction; Flyway; broker/cache.
- [ ] **B.** Một ứng dụng cùng stack, dùng JPA làm mặc định toàn bộ; ngoại lệ JDBC phải có ADR.
- [ ] **C.** Giữ Open hoặc nêu stack/topology khác và lý do.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q04. JWT và quyền caller

**Liên quan / hạn chốt:** OQ-15; trước M3/M4.
**Ảnh hưởng:** NFR-09; API security; không xây cấp token nội bộ.

- [x] **A.** JWT ký bất đối xứng, xác minh qua JWKS cấu hình; issuer/audience được kiểm, scope theo thao tác và caller sở hữu request. Điền issuer/audience/scope map hoặc cấu hình demo ở ghi chú.
- [ ] **B.** JWT ngoài với public key cấu hình cục bộ cho demo; vẫn kiểm issuer/expiry/audience/quyền/ownership. Điền cấu hình ở ghi chú.
- [ ] **C.** Giữ Open; cần nguồn key, claims, quyền tạo/tra cứu và rotation trước code.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** he thong xac thuc chay local

## Q05. Phạm vi duy nhất requestId UC01

**Liên quan / hạn chốt:** OQ-02; trước M4.
**Ảnh hưởng:** Unique request registry; API query/conflict.

- [x] **A.** Duy nhất theo caller ứng dụng + UC01 + requestId; caller không xem request bên khác.
- [ ] **B.** Duy nhất requestId toàn hệ thống; vẫn kiểm quyền caller tra cứu.
- [ ] **C.** Giữ Open; mô tả phạm vi ID và ownership.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q06. Lưu idempotency UC01

**Liên quan / hạn chốt:** OQ-02; trước M4.
**Ảnh hưởng:** DB requests; retry/expiry/history.

- [ ] **A.** Giữ kết quả/tombstone ID suốt vòng đời dữ liệu demo; chưa xóa tự động, không cho tái dùng ID đã xử lý.
- [x] **B.** Retention cấu hình được; ghi thời hạn cụ thể, xử lý ID sau hết hạn và rủi ro retry muộn trong ghi chú.
- [ ] **C.** Giữ Open; chưa được thiết kế xóa record hoặc tái dùng ID.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** Giữ kết quả đầy đủ 7 ngày sau trạng thái cuối; không dọn yêu cầu đang xử lý/chờ. Sau đó giữ dấu mã đã sử dụng, không tự xử lý lại mã cũ như yêu cầu mới.

## Q07. Nội dung so sánh khi retry UC01

**Liên quan / hạn chốt:** OQ-02/12; trước M4.
**Ảnh hưởng:** Fingerprint; validation; IDEMPOTENCY_CONFLICT.

- [x] **A.** So canonical business payload đã chuẩn hóa: hồ sơ, mode, số chọn, consent; bỏ JWT/correlationId và thứ tự JSON. Chuẩn hóa theo Q01/Q02.
- [ ] **B.** So payload nghiệp vụ đúng nguyên giá trị, chỉ bỏ khác biệt thứ tự thuộc tính JSON và metadata transport; thay giá trị là conflict.
- [ ] **C.** Giữ Open; liệt kê rõ trường tham gia/loại bỏ, absent/null/default.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q08. Ưu tiên cấp số giữa WAITING và request mới

**Liên quan / hạn chốt:** OQ-05; trước M4.
**Ảnh hưởng:** Worker claim; pool; race/fairness tests.

- [x] **A.** FIFO theo mốc WAITING cho các yêu cầu chờ hợp lệ; ưu tiên xử lý yêu cầu chờ trước tự cấp mới khi có số bổ sung. Chọn số vẫn tuân điều kiện khả dụng.
- [ ] **B.** Không đảm bảo FIFO; mọi lần cấp là nguyên tử, recovery xử lý chờ còn hạn, không cam kết ưu tiên.
- [ ] **C.** Giữ Open; ghi fairness và cách chứng minh không bỏ sót.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q09. Hướng chống cấp số trùng

**Liên quan / hạn chốt:** OQ-05/02; trước M4.
**Ảnh hưởng:** P4.1/2/4/5; không tự coi chọn hướng là xong thuật toán.

- [x] **A.** Giao dịch DB ngắn + row lock/unique constraint, lock order và xử lý retry/deadlock có giới hạn sẽ viết chi tiết.
- [ ] **B.** Conditional update + unique constraint, retry ứng viên có giới hạn; thiết kế nguyên tử liên bảng trước duyệt M4.
- [ ] **C.** Yêu cầu agent so sánh chi tiết bằng chứng/thuật toán trước khi chọn; chưa chốt khóa.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q10. Recovery, nạp số và biên hết hạn

**Liên quan / hạn chốt:** OQ-05/15; trước M4.
**Ảnh hưởng:** WAITING/recovery; cần chốt chuẩn số trước seed/DB.

- [x] **A.** Trigger sau nạp số + recovery mỗi 60 giây (đề xuất); dùng đồng hồ backend thống nhất, kiểm hạn tại hoàn tất, không reset deadline sau retry/restart.
- [ ] **B.** Cùng nguyên tắc nhưng cấu hình interval khác: điền giá trị; worker/claim sẽ thiết kế sau.
- [ ] **C.** Giữ Open; bổ sung chuẩn số khả dụng/quy trình nạp, interval và mốc expiry.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q11. Hợp đồng HTTP UC01

**Liên quan / hạn chốt:** OQ-15; trước M4.
**Ảnh hưởng:** P4.3 OpenAPI; chọn hướng không thay nghiệm thu hợp đồng.

- [x] **A.** Hướng REST: POST /api/v1/activations, GET /api/v1/activations/{requestId}, GET /api/v1/phone-numbers; success mới 201, WAITING 202, conflict 409. Các lỗi/replay/in-progress cần lập bảng và duyệt OpenAPI tiếp.
- [ ] **B.** Muốn agent đề xuất đầy đủ endpoint/HTTP/schema/timeout/polling trước khi chọn; chưa chốt hợp đồng.
- [ ] **C.** Giữ Open; ghi chuẩn API riêng, ngân sách timeout và polling mong muốn.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q12. Giá chu kỳ và thay giá

**Liên quan / hạn chốt:** OQ-06; trước UC02.
**Ảnh hưởng:** ServicePackage/Subscription/Transaction; phí tài khoản chính đã chốt.

- [x] **A.** Chụp giá áp dụng tại từng lần đăng ký/gia hạn, lưu lịch sử; đơn giá định nghĩa gói có hiệu lực lúc thu. Không sửa phí đã commit.
- [ ] **B.** Giá được version theo gói và thời điểm hiệu lực; mỗi chu kỳ giữ version áp dụng.
- [ ] **C.** Giữ Open; ghi giá mẫu/cycle duration và quy tắc thay giá.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q13. Tính chu kỳ, đăng ký lại, hủy/gia hạn

**Liên quan / hạn chốt:** OQ-07/09; trước UC02/03/job.
**Ảnh hưởng:** State/sequence; chống thu phí/cộng kỳ hai lần.

- [x] **A.** Chu kỳ theo số ngày gói, nối từ hạn hiện tại nếu còn hiệu lực; hủy chờ chặn đăng ký nối dài, không tự gia hạn; thiếu tiền ở cuối kỳ thì hết hiệu lực, không grace ngầm.
- [ ] **B.** Chu kỳ theo lịch tháng; ghi quy tắc ngày cuối tháng, đăng ký lại khi chờ hủy, và thiếu tiền ở ghi chú.
- [ ] **C.** Giữ Open; yêu cầu agent làm bảng ca biên trước quyết định.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q14. Tranh chấp hủy, gia hạn và retry

**Liên quan / hạn chốt:** OQ-07/09; trước UC03/job.
**Ảnh hưởng:** Job/transaction/state; không đổi hủy cuối chu kỳ.

- [x] **A.** Theo trạng thái được commit trước, nhưng hủy đã được nhận trước mốc cuối kỳ phải ngăn gia hạn; request hủy mới khi đã chờ hủy trả lỗi, retry cùng ID trả kết quả gốc.
- [ ] **B.** Cần thiết kế thứ tự và bảng kết quả tại biên thời gian trước khi chọn.
- [ ] **C.** Giữ Open; nêu quy tắc ưu tiên khác.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q15. Idempotency cho lệnh tiền

**Liên quan / hạn chốt:** OQ-02/10; trước UC02/04/05.
**Ảnh hưởng:** Ledger uniqueness; DB/API; không tự mở rộng quyết định UC01 sang tiền.

- [ ] **A.** Mỗi caller + loại nghiệp vụ + requestId; cùng payload trả kết quả cũ, khác payload conflict. Giữ ID trong demo; phí nội bộ có liên kết request đăng ký, không tính thành debit API riêng.
- [x] **B.** Cùng chính sách UC01 sau khi UC01 được chốt nhưng retention riêng: ghi rõ thời hạn.
- [ ] **C.** Giữ Open; cần scope, thời hạn, chuẩn payload, trả kết quả concurrent.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** Thoi han chon tuy ban de xuat, nhung ghi ro ra cho toi va cai y co the cau hinh duoc

## Q16. Tài khoản phụ và giới hạn tiền

**Liên quan / hạn chốt:** OQ-01/10; trước UC02/04/05.
**Ảnh hưởng:** OQ-01/10; schema tiền; expired account giữ số dư đã chốt.

- [x] **A.** UC01 chỉ tạo chính; tài khoản phụ theo loại dịch vụ cấu hình, tạo qua seed/quy trình riêng; tiền integer VND trong miền số có giới hạn kiểm tra tràn. Điền max giao dịch/max số dư, hạn tài khoản phụ.
- [ ] **B.** Yêu cầu agent đề xuất danh mục tài khoản, hạn mức/số dư và xử lý hoàn tiền trước duyệt; chưa bổ sung API mở tài khoản/hoàn tiền vào phạm vi.
- [ ] **C.** Giữ Open; ghi cấu hình mong muốn, không giới hạn bằng câu 'tùy' mà chưa có giá trị.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q17. Thời gian và mặc định bộ lọc báo cáo

**Liên quan / hạn chốt:** OQ-03; trước UC06.
**Ảnh hưởng:** UC06; boundary/filter tests.

- [x] **A.** Khoảng [X,Y) theo thời điểm commit giao dịch; OR/AND và includeFee bắt buộc chỉ rõ, không có mặc định ngầm; chỉ transaction commit thành công.
- [ ] **B.** Theo ngày nghiệp vụ và timezone cấu hình, bao gồm hết ngày Y; OR/AND và includeFee vẫn phải rõ.
- [ ] **C.** Giữ Open; ghi timezone, mốc lọc và defaults.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q18. Truy vết gói và kết quả báo cáo

**Liên quan / hạn chốt:** OQ-03/13/15; trước UC06.
**Ảnh hưởng:** Ledger attribution; UC06/OpenAPI; không tự bịa bắt buộc FK.

- [x] **A.** Mỗi debit tiêu dùng có tham chiếu gói/ngữ cảnh dịch vụ xác định; phí liên kết đăng ký. Báo danh sách thuê bao không trùng, phân trang cursor; caller báo cáo có scope riêng.
- [ ] **B.** Cần agent đề xuất quan hệ transaction–package, xử lý debit không gói và phân trang trước khi chọn.
- [ ] **C.** Giữ Open; ghi caller được phép, trường trả về và page size tối đa.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q19. Giờ chạy mặc định và timezone

**Liên quan / hạn chốt:** OQ-07/13; trước job.
**Ảnh hưởng:** FR-JOB-03; không gộp hai job.

- [x] **A.** Đề xuất Asia/Ho_Chi_Minh; hết hạn 00:05, tổng hợp 00:30 mỗi ngày; từng lịch có thể đổi/chạy tay. Quyền dùng gói/tài khoản không được kéo dài chỉ vì job chạy trễ.
- [ ] **B.** Cấu hình khác: điền timezone, giờ từng job và kỳ xử lý.
- [ ] **C.** Giữ Open; chưa áp lịch/giờ nào làm mặc định.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q20. Chỉ tiêu tổng hợp, cutoff và điều chỉnh

**Liên quan / hạn chốt:** OQ-03/13; trước JOB-02.
**Ảnh hưởng:** Chốt kỳ một lần đã duyệt, không ghi đè ngầm lịch sử.

- [x] **A.** Kỳ ngày trước theo timezone Q19; đếm giao dịch thành công và tổng VND theo ngày/thuê bao/gói/loại phí, lưu DB; CSV tùy chọn. Cutoff lúc chốt; dữ liệu muộn có ID điều chỉnh ở kỳ sau.
- [ ] **B.** Chọn chỉ tiêu/cutoff/format khác: điền rõ và cách phân biệt kỳ nguồn với kỳ điều chỉnh.
- [ ] **C.** Giữ Open; yêu cầu mẫu bảng/file và ca late data trước chọn.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q21. Thời hạn lịch sử và tái cấp số

**Liên quan / hạn chốt:** OQ-11; trước job/deployment.
**Ảnh hưởng:** Không coi 5 năm là yêu cầu pháp lý; giữ lịch sử OQ.

- [x] **A.** Đề xuất retention 5 năm cấu hình; bản demo không chạy purge, số chưa có quy trình giải phóng/cách ly không tái cấp. Thời gian cách ly chốt riêng trước nghiệp vụ ngừng thuê bao.
- [ ] **B.** Retention khác: điền số năm; tái cấp tiếp tục Open ngoài UC01 đầu.
- [ ] **C.** Giữ Open; chưa thực thi xóa dữ liệu hoặc tái cấp số.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q22. Cửa sổ đo và latency/lỗi

**Liên quan / hạn chốt:** OQ-04; trước chạy tải.
**Ảnh hưởng:** Test Plan; các ngưỡng A chỉ đề xuất chưa duyệt.

- [x] **A.** Đề xuất warm-up 2 phút, đo 10 phút, p95 ≤500ms/p99 ≤1s, lỗi hệ thống ≤0,1%; báo riêng business failure/timeout. Ngưỡng TPS và correctness giữ nguyên; cần ghi cấu hình máy/DB trước đo.
- [ ] **B.** Giữ chỉ tiêu bắt buộc TPS/tính đúng, latency và lỗi báo cáo thực tế chưa có ngưỡng phụ; ghi warm-up/thời lượng ở ghi chú.
- [ ] **C.** Giữ Open; điền thời lượng/ngưỡng và cấu hình môi trường mong muốn.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q23. Nhóm tranh chấp và mẫu số 50%

**Liên quan / hạn chốt:** OQ-14; trước chạy tải.
**Ảnh hưởng:** Không đổi DEC-10, đếm TPS theo logical success mới.

- [x] **A.** Đề xuất 10 cặp tranh chấp, chia đều 50% tổng request debit phát lên (kể cả retry); 50% còn lại phân rộng, ghi từng attempt + logical ID; phí nội bộ không là request debit độc lập. Chứng minh overlap thực, không chỉ tỷ lệ phân nhóm.
- [ ] **B.** Chia nhóm khác: điền số nhóm/phân bố, cách tính request retry và cách chứng minh overlap.
- [ ] **C.** Giữ Open; không áp mặc định số nhóm/mẫu tải.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q24. Công cụ và xác định kết quả timeout

**Liên quan / hạn chốt:** OQ-04/14; trước chạy tải.
**Ảnh hưởng:** Có thể hoãn đến Phase 6; không làm giả số đo.

- [x] **A.** k6 phát tải, script đối soát riêng đọc log request và DB; tra cứu/retry có giới hạn rồi báo unresolved, unresolved >0 thì chưa kết luận đối soát đạt.
- [ ] **B.** JMeter phát tải, công cụ đối soát riêng cùng nguyên tắc; ghi cách thu log/DB snapshot.
- [ ] **C.** Giữ Open hoặc nêu tool khác.
- [ ] **D.** Phương án khác, ghi cụ thể.

**Tôi chọn / ghi chú:** 

## Q25. Nghiệm thu M1 — tách khỏi lựa chọn quy tắc

Chỉ trả lời sau khi đã đọc SRS 0.6, UC01 0.5 và chốt hoặc xác nhận hoãn hợp lệ các vấn đề chặn M1 (đặc biệt OQ-12/Q01–02). Chọn câu này không duyệt M2/M3/M4.

- [ ] **A.** Tôi nghiệm thu SRS 0.6/UC01 0.5 sau khi đồng bộ đúng câu trả lời trong phiếu; các OQ kỹ thuật hoãn theo mốc được nêu. Nếu đồng bộ làm thay đổi đáng kể flow/AC thì trình lại trước đóng M1.
- [ ] **B.** Tôi muốn nhận tài liệu đã đồng bộ câu trả lời để rà soát lại rồi mới duyệt M1.
- [ ] **C.** Chưa duyệt M1; ghi phần cần sửa.
- [ ] **D.** Ý kiến khác, ghi rõ phạm vi.

**Tôi chọn / ghi chú:** 

**Người trả lời:** 
**Ngày xác nhận:** 

Sau khi trả lời, bước tiếp theo là ghi quyết định/đồng bộ P1.5 và xác minh M1; sau đó rà soát P2.1. M2–M4 vẫn cần bằng chứng riêng, không tự tick toàn Phase.


**Kết quả tiếp nhận:** Không sửa nội dung đáp án đã nhập. Broker/cache làm rõ theo CR-ARCH-001 trong chat. Đọc review-guide.md trước Q25; chưa chọn Q25 thay chủ dự án.
