> **Hiệu lực 28/10/2026: CR-DOC-004 chốt hồ sơ; CR-IMPL-001 đổi thứ tự triển khai: P5.1 → UC04/UC05 module tiền → UC01 và các UC còn lại. Công ty học, ở nhà code/chạy service.** Các trạng thái trước đó bên dưới là lịch sử; phần kiểm chứng runtime còn phải thực hiện. [Biên bản chốt](document-approval-20261028.md).

# Nhật ký làm rõ yêu cầu – Telecom Subscription Management System

**Mã tài liệu:** DOC-PROJ-003

**Phiên bản:** 1.6 – bổ sung phê duyệt M4 và vận hành hai môi trường

**Ngày xác nhận:** 14/10/2026 (Phase 0); cập nhật lựa chọn SRS 15/10/2026

**Trạng thái:** M0–M4 (riêng UC01) đã được chủ dự án duyệt; OQ các nghiệp vụ sau và kiểm chứng kỹ thuật P5 vẫn theo dõi riêng; CR-UC01-001 Hybrid giữ nguyên.

**Người xác nhận:** Chủ dự án – người dùng, có quyền quyết định nghiệm thu cuối cùng

**Giai đoạn:** M0–M4 duyệt theo phạm vi UC01; bước tiếp theo P5.1 khởi tạo backend, kiểm chứng DB/migration/health; P5.2 code nghiệp vụ UC01.

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

| Mã | Trạng thái | Đã chốt | Còn cần quyết định / thiết kế | Hạn chốt |
|---|---|---|---|---|
| OQ-01 | Open — chốt một phần | Chính0 không hạn; phụ theo cấu hình/seed (DEC-PRE-16). | Hạn mức/hạn phụ và danh mục mẫu: đề xuất R3 chưa duyệt. | Trước UC02/04/05 |
| OQ-02 | Open — chốt một phần | Scope/canonical UC01, replay7 ngày+tombstone; normalization/replay/HTTP T1–T3 đã duyệt M4 lịch sử. | Delta MySQL/cấu hình và kiểm chứng; retention UC02–05/registry UC03 theo Q01/Q30 phiếu 16. | Delta UC01; trước UC liên quan |
| OQ-03 | Open — chốt một phần | [X,Y) commit, OR/AND/includeFee rõ; cursor, scope riêng, attribution gói/ngữ cảnh (17/18). | Schema attribution/page size/claims và dữ liệu không ánh xạ. | Trước UC06 |
| OQ-04 | Open — chốt một phần | warm-up2/đo10, p95≤500ms/p99≤1s, lỗi hệ thống≤0,1%, k6 (22/24). | Máy/config, tập latency/mẫu số lỗi/rate, không có số đo. | Trước đo tải |
| OQ-05 | Open — chốt một phần | FIFO/recovery60s/kiểm hạn; T4–T6 lock order/tie-break/clock đã duyệt ý tưởng M4 lịch sử. | Q33 chuẩn số và delta InnoDB/retry/test thực; nạp kho/cách ly/tái cấp chưa có quy trình đủ. | Delta UC01; tái cấp trước ngừng thuê bao |
| OQ-06 | Open — chốt một phần | Snapshot giá mỗi đăng ký/gia hạn, phí tài khoản chính nguyên tử (12, DEC-03). | Giá mẫu/schema snapshot và lỗi/retry rollback. | Trước UC02 |
| OQ-07 | Open — chốt một phần | Hủy trước cuối kỳ ngăn gia hạn; hủy mới lặp lỗi, retry cùng ID kết quả cũ (13/14). | Mốc nhận hợp lệ bền vững, exact boundary, hủy sau gia hạn, job idempotency. | Trước UC03/job |
| OQ-08 | Resolved | Customer–Subscriber 1–N, DEC-01. | Không còn. | Resolved 14/10/2026 |
| OQ-09 | Open — chốt một phần | Chu kỳ số ngày, nối từ hạn cũ; chờ hủy chặn nối/gia hạn; thiếu tiền hết hiệu lực không grace (13). | Ngày tuyệt đối/lịch, đã hết hạn đăng ký lại, lỗi kỹ thuật cuối kỳ. | Trước UC02/03 |
| OQ-10 | Open — chốt một phần | VND nguyên dương, kiểm tràn, không âm; hết hạn giữ số dư/chặn giao dịch (16). | Hạn mức cụ thể R3, không tự mở rộng API hoàn tiền. | Trước UC04/05 |
| OQ-11 | Open — chốt một phần | 5 năm cấu hình, demo không purge, giữ lịch sử (21). | Archive/purge sau demo; không có vòng đời tái cấp. | Trước job/deployment |
| OQ-12 | Open — chốt một phần | M1 đã duyệt so mọi trường gửi, loại+số/chứng cứ ngoài; normalization/absent/null/evidence fingerprint T1/T2 đã duyệt lịch sử. | Q31 phạm vi tin cậy caller/chứng cứ ngoài, cấu hình và kiểm chứng trước API thật; không hỏi lại R1/M1. | Trước API UC01 thật |
| OQ-13 | Open — chốt một phần | Asia/Ho_Chi_Minh00:05/00:30, riêng cấu hình/chạy tay; ngày trước, count/tổngVND theo chiều, CSV, điều chỉnh kỳ sau (19/20). | Transaction chốt kỳ/cutoff/late adjustment/batch/gia hạn đúng biên. | Trước job |
| OQ-14 | Open — chốt một phần | 10 cặp chia đều50% debit attempt kể cả retry; phí nội bộ ngoài mẫu số; k6/unresolved không đạt (23/24). | Bằng chứng overlap phía xử lý, điều phối retry giữ tỷ lệ; logical success tách attempt. | Trước thiết kế tiền/đo tải |
| OQ-15 | Open — chốt một phần | JWKS/IdP ngoài backend, HTTP/replay/in-progress/tombstone T3/T7 đã duyệt ý tưởng M4 lịch sử. | URL/claims/caller/scope/key rotation/polling/timeout cấu hình thực; Q31 quyền dữ liệu/evidence, Q30 UC02–05. | Trước API thật từng UC |

### Các ràng buộc cần giữ khi đặc tả chi tiết

- Đăng ký có thu phí có thể tạo giao dịch trừ tiền nội bộ. Test Plan phải phân biệt nghiệp vụ đăng ký với lệnh trừ tiền được phát độc lập; không đếm hai lần một thao tác để đạt TPS.
- Tỷ lệ 50% dùng tổng request trừ tiền làm mẫu số theo Q10; việc phân loại request trong bài hỗn hợp và cách tách giao dịch thu phí nội bộ phải được ghi rõ trước khi đo.
- Với từng tài khoản, kiểm tra `số dư cuối = số dư đầu + tổng cộng tiền thành công - tổng trừ tiền thành công`, đồng thời đối chiếu từng nghiệp vụ; công thức tổng một mình không đủ.
- Đã chốt OR/AND, VND nguyên, tự gia hạn đủ tiền và tài khoản hết hạn giữ số dư/chặn giao dịch; chi tiết còn Open theo mục 6 và 12.

## 7. Điều kiện đóng Phase 0 và thay đổi tiếp theo

Tại thời điểm 14/10/2026 đã có đủ mục tiêu, ranh giới, thuật ngữ, quyết định được duyệt và danh sách OQ có trách nhiệm/mốc xử lý. Chủ dự án chọn Q14=A cho phép đóng Phase 0. **Đến 15/10/2026 Phase 1 đang rà soát M1**, chưa xác nhận nghiệm thu SRS/UC01. Các OQ còn mở không phải bằng chứng đã hoàn thành SRS hay kiểm thử.

Khi đóng một OQ sau này, ghi quyết định chi tiết, người/ngày xác nhận, căn cứ, tài liệu thay đổi và tiêu chí kiểm chứng. Giữ mã OQ và DEC; nếu thay quyết định thì dẫn quyết định thay thế và lý do.

> **Cách đọc lịch sử:** Mục 8–12 ghi trạng thái tại thời điểm trước bộ DEC-PRE; dùng mục 6 và 13 cho tình trạng hiện hành. Các câu “chưa chốt” trong lịch sử không mở lại lựa chọn đã được chốt ngày 18/10.

## 8. Bản ghi lựa chọn SRS ngày 15/10/2026 – REV-SRS-001

**Nguồn:** `bản ghi REV-SRS-001, mục 12.2; phiếu đã xóa theo yêu cầu`, do chủ dự án gửi lại với 31/31 câu được đánh dấu; ngày tiếp nhận 15/10/2026. Nội dung này ghi các lựa chọn **đã được người dùng đưa ra**, không ghi sai thành đã nghiệm thu toàn bộ SRS. Baseline DEC-01–DEC-11 ngày 14/10 giữ nguyên.

| Nhóm | Các lựa chọn của chủ dự án | Tham chiếu OQ |
|---|---|---|
| UC01 – hồ sơ | Q01=A, Q02=C, Q03=A, Q04=A, Q05=A | OQ-12/01 |
| UC01 – số | Q06=C, Q07=A, Q08=B, Q09=B, Q10=B | OQ-05 |
| UC01 – tích hợp | Q11=B **(đã được CR-UC01-001 thay cho UC01)**, Q12=B, Q13=A, Q14=A, Q15=C | OQ-15/02 |
| Tài khoản/giao dịch | Q16=B, Q17=A, Q18=A, Q24=A, Q27=A | OQ-01/02/10/11 |
| Gói cước/hủy | Q19=A, Q20=A, Q21=B, Q22=B, Q23=B | OQ-06/07/09 |
| Báo cáo | Q25=C, Q26=C, Q29=C, Q31=B | OQ-03/13 |
| Vận hành/job | Q28=B (ghi: "tùy bạn muốn"), Q30=D (ghi: "mix B và C") | OQ-07/11/13 |

**Diễn giải đúng ghi chú Q30:** Mỗi job có lịch và timezone cấu hình riêng (**B**); có giá trị cấu hình mặc định chạy hàng ngày vào một giờ cố định và có khả năng chạy thủ công (**C**). **Không suy diễn giờ hoặc múi giờ mặc định cụ thể.**

**Ghi chú Q28:** Chủ dự án cho phép đề xuất thời hạn lưu, nhưng chưa có con số xác nhận. Gợi ý để thảo luận: **5 năm cấu hình được** cho phạm vi bài tập/demo (không phải tuyên bố yêu cầu pháp lý, không tự xem là thời hạn duyệt).

**Tính nhất quán:** 31 câu mỗi câu đúng một đáp án; không mâu thuẫn trực tiếp DEC Phase 0. Những phụ thuộc cần xử lý: Q07=A (tối thiểu hai trạng thái số) + Q08=B (đã cấp tái sử dụng sau thời gian chờ) cần quy tắc biểu diễn số đang cách ly; Q10=B + Q11=B từng tạo vòng đời bất đồng bộ toàn phần; **CR-UC01-001 đã thay bằng Hybrid có điều kiện và quyền chọn chờ**; Q21=B + Q22=B cần tránh nhân đôi kỳ/phí.

**OQ cần ưu tiên trước khi khóa UC01 (REV-UC01-002):** OQ-05 (cấp tự động khi hết kho); OQ-12 (ghép hồ sơ đa giấy tờ); OQ-15 (JWT, trạng thái xử lý); OQ-02 (cùng requestId khác payload). Các phần khác được phép giữ Open theo mốc riêng.

## 9. Bản ghi bổ sung UC01 ngày 15/10/2026 – REV-UC01-002

**Nguồn:** `bản ghi REV-UC01-002, mục 12.3; phiếu đã xóa theo yêu cầu`; người dùng đã đánh dấu đúng một lựa chọn mỗi câu, **5/5 câu**. Quyết định của người dùng được ghi nhận ở mức nghiệp vụ, không đồng nghĩa đã duyệt toàn bộ SRS/UC01 hay mã kỹ thuật.

| Câu | Lựa chọn | Quy tắc được chọn | OQ |
|---|---|---|---|
| Q01 | A | Cấp số tự động hết kho → WAITING tối đa 24 giờ; quá hạn FAILED/EXPIRED, CSKH gửi yêu cầu mới | OQ-05 |
| Q02 | A | Thử lại yêu cầu WAITING khi nạp/sinh số, có task khôi phục định kỳ để không bỏ sót | OQ-05 |
| Q03 | B | Customer có thể liên kết nhiều giấy tờ **đã xác minh**; UC01 chỉ dùng liên kết đã xác minh, không tự ghép hồ sơ chưa xác minh | OQ-12 |
| Q04 | A | JWT được bên xác thực ngoài phát; TelecomManagement kiểm tra chữ ký/issuer/expiry/quyền cần thiết | OQ-15 |
| Q05 | A | requestId trùng payload khác → lỗi `IDEMPOTENCY_CONFLICT`, không ghi đè hoặc phát sinh yêu cầu thứ hai | OQ-02 |

**Tách rõ phạm vi chưa chốt:** quy tắc kỹ thuật cấp số/khôi phục, cách chuẩn hóa hồ sơ, cấu hình quyền JWT, phạm vi/thời hạn lưu requestId ở Phase 3–4; thời gian cách ly số cũ thuộc vòng đời ngoài UC01; Q28 về năm retention và Q30 về giờ job chưa chốt. **Tất cả các OQ trên tiếp tục Open ở phần còn lại; OQ-08 vẫn Resolved.**

**Đầu ra hiện hành:** `docs/01-requirements/SRS.md` v0.4 và `docs/01-requirements/use-cases/UC01-Subscriber-Activation.md` v0.3, đã tích hợp CR-UC01-001, để chủ dự án rà soát checkpoint M1.


## 10. Change Request CR-UC01-001 - Chuyển sang Hybrid cho UC01

| Thuộc tính | Nội dung |
|---|---|
| Mã | **CR-UC01-001** |
| Ngày quyết định | **15/10/2026** |
| Người quyết định | Chủ dự án – người dùng có quyền nghiệm thu |
| Xác nhận | Người dùng: “vậy chốt theo hướng hybrid đi” sau khi so sánh các phương án và ví dụ quyền chọn chờ |
| Trạng thái | **Approved – phê duyệt cách xử lý Hybrid cho UC01, không phải duyệt M1** |
| Thay thế | REV-SRS-001 Q11=B (bất đồng bộ toàn phần) được thay bằng Hybrid; Q10=B và REV-UC01-002 Q01/Q02 chỉ còn áp dụng cho **nhánh khách hàng chọn chờ** |
| Không thay đổi | DEC-01–DEC-11 Phase 0; JWT ngoài, idempotency, số không cấp trùng, chờ tối đa 24 giờ khi được phép, task khôi phục |
| Tài liệu ảnh hưởng | SRS v0.4, UC01 v0.3, clarification log v1.3, ROADMAP |

**Quy tắc nghiệp vụ thay đổi:**

1. CSKH gọi REST API đấu nối và nhận kết quả cuối **đồng bộ** (`SUCCEEDED`/`FAILED`) nếu nghiệp vụ hoàn thành ngay.
2. Nếu khách **chọn số cụ thể** nhưng số đã cấp: trả lỗi ngay; không chuyển WAITING.
3. Nếu **tự cấp số** và kho hết AVAILABLE: nếu khách **không đồng ý chờ**, trả `NO_AVAILABLE_NUMBER` ngay. Nếu khách **đồng ý chờ** và CSKH truyền lựa chọn này, ghi WAITING không quá 24 giờ; khi bổ sung số tự xử lý lại và có task khôi phục, quá hạn EXPIRED.
4. `WAITING` nghĩa là **đã tiếp nhận để chờ**, không phải đấu nối thành công. CSKH **tự động giữ requestId và theo dõi** bằng API, không yêu cầu nhân viên nhập requestId thủ công.
5. Giữ chống requestId trùng, JWT ngoài, kết quả sau timeout phải có cách tra cứu; cơ chế cụ thể được thiết kế ở Phase 3–4.

**Lưu ý về phạm vi quyết định:** “Khách hàng chọn chờ, không tự ép chờ” là điều kiện trong phương án Hybrid đã trình bày và được người dùng chốt. Chưa chốt tên trường API, hình thức UI CSKH, lịch polling hay điều kiện ưu tiên giữa các yêu cầu WAITING. Những vấn đề này tiếp tục ở OQ-05/OQ-15.

**Không sửa ngược phiếu cũ:** REV-SRS-001 Q10/Q11 và REV-UC01-002 Q01/Q02 vẫn ghi nguyên như lúc được trả lời; ưu tiên CR-UC01-001 khi diễn giải UC01 hiện hành.

## 11. Lịch sử cập nhật

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 0.1 | 14/10/2026 | Bản nháp từ đề bài, DEC-01–DEC-06 và OQ-01–OQ-15; các phương án khi đó chưa duyệt. |
| 1.0 | 14/10/2026 | Lưu đủ 14 đáp án và thẩm quyền; làm rõ DEC-03, xác nhận DEC-01–DEC-06, thêm DEC-07–DEC-11; đóng OQ-08, giữ chi tiết còn mở có người/mốc xử lý; chốt Phase 0 và chuyển thông tin khỏi phiếu tạm. |
| 1.1 | 15/10/2026 | Tiếp nhận đủ 31 lựa chọn REV-SRS-001, cập nhật OQ và phân tách các phần được chọn với chi tiết Open, không thay đổi DEC Phase 0. |
| 1.2 | 15/10/2026 | Tiếp nhận 5/5 lựa chọn REV-UC01-002, cập nhật OQ-02/05/12/15; giữ các chi tiết còn Open và không sửa DEC Phase 0. |
| 1.3 | 15/10/2026 | CR-UC01-001 approved: chốt Hybrid UC01; đồng bộ luồng hoàn tất ngay, WAITING 24h chỉ khi tự cấp hết số và khách đồng ý chờ; M1 chưa duyệt. |
| 1.4 | 18/10/2026 | CR-DOC-001: sổ quyết định độc lập chứa đầy đủ nội dung 31+5 lựa chọn; thêm khung tài liệu và phiếu còn mở, giữ M1 chờ duyệt. |

## 12. Sổ quyết định độc lập với phiếu review (18/10/2026)

### 12.1. Quy tắc quản lý tài liệu – CR-DOC-001

- **Approved, 18/10/2026, người duyệt: chủ dự án trong yêu cầu phiên này.**
- Cũ: SRS/UC01 dẫn phiếu trắc nghiệm như nguồn cần đọc kèm; chưa có khung các phần tiếp theo.
- Mới: clarification log lưu đầy đủ nội dung quyết định; tài liệu chính dùng DEC/CR/OQ và tự chứa quy tắc áp dụng. Phiếu chỉ là đầu vào thu thập/hồ sơ lịch sử.
- Lý do: chủ dự án yêu cầu bỏ phụ thuộc phiếu review, viết tiếp khung tài liệu trước code, gom câu chưa rõ vào một file Markdown.
- Phạm vi: bổ sung DRAFT Phase 2–4, kế hoạch Phase 5–7; không phê duyệt M1–M4, không tự chọn phương án kỹ thuật, không sửa đề bài.
- Ảnh hưởng: SRS 0.5, UC01 0.4, roadmap, README, UML/kiến trúc/DB/API/test. Không thay đổi code.
- Thư mục tài liệu hiện tại là docs/; đã chuẩn hóa khỏi cấu trúc lồng nhau theo yêu cầu tiếp theo của chủ dự án ngày 18/10/2026.
- Kiểm kê 18/10: chưa có .puml trước lượt này. Nhận định “có sơ đồ cũ” trong roadmap và ảnh chụp AGENTS không phù hợp sản phẩm hiện có. UML lượt này là **tạo mới DRAFT**.

### 12.2. Nội dung đầy đủ quyết định yêu cầu đã nhận 15/10/2026

Người quyết định: chủ dự án. Mã dưới đây là bí danh ổn định cho lựa chọn đã ghi mục 8, không phải quyết định nghiệp vụ mới ngày 18/10. Không cần mở phiếu cũ để sử dụng bảng này. Phần Open tại mục 6 vẫn có hiệu lực; M1 chưa duyệt.

| Mã ổn định | Đáp án lịch sử | Nội dung lựa chọn và hiệu lực |
|---|---|---|
| DEC-REQ-01 | A | Họ tên, ngày sinh, số giấy tờ định danh; địa chỉ, nghề nghiệp và thông tin liên hệ là tùy chọn. |
| DEC-REQ-02 | C | Chấp nhận CCCD, CMND/CMT và hộ chiếu; cần quy tắc phân biệt từng loại. |
| DEC-REQ-03 | A | Tìm theo định danh đã chọn và dùng lại Customer; tạo thêm Subscriber mới. |
| DEC-REQ-04 | A | Từ chối cập nhật/đấu nối, trả thông báo cần xác minh thông tin. |
| DEC-REQ-05 | A | Tự tạo một tài khoản cơ bản (số dư ban đầu và hạn dùng chốt riêng trong Q17). |
| DEC-REQ-06 | C | Cả kho số có sẵn và chức năng bổ sung/sinh số, nhưng chỉ cấp từ tập đã xác nhận khả dụng. |
| DEC-REQ-07 | A | Tối thiểu AVAILABLE (còn cấp) và ASSIGNED (đã cấp). |
| DEC-REQ-08 | B | Có thể tái sử dụng sau một khoảng chờ; khoảng chờ sẽ chốt khi đặc tả vòng đời. |
| DEC-REQ-09 | B | Chọn ngẫu nhiên trong tập số khả dụng. |
| DEC-REQ-10 | B | Ghi yêu cầu ở trạng thái chờ xử lý sau; không báo đấu nối thành công. **Superseded/điều chỉnh cho UC01 bởi CR-UC01-001; dùng Hybrid có consent.** |
| DEC-REQ-11 | B | Bất đồng bộ: nhận yêu cầu và trả mã tra cứu trạng thái xử lý. **Superseded/điều chỉnh cho UC01 bởi CR-UC01-001; dùng Hybrid có consent.** |
| DEC-REQ-12 | B | Bearer token/JWT; cần làm rõ bên phát hành và cách kiểm tra token trước khi thiết kế. |
| DEC-REQ-13 | A | CSKH gửi mã yêu cầu duy nhất; gửi lại cùng mã/nội dung thì trả cùng kết quả, không cấp số/tạo thuê bao lần hai. |
| DEC-REQ-14 | A | Có cách tra cứu trạng thái/kết quả theo mã yêu cầu và retry cùng mã; không tạo kết quả thứ hai. |
| DEC-REQ-15 | C | Chuẩn hóa như A và thêm mã tương quan (correlation ID) để truy vết log. |
| DEC-REQ-16 | B | Có một tài khoản chính và có thể có thêm tài khoản phụ theo dịch vụ. |
| DEC-REQ-17 | A | Số dư 0, chưa đặt hạn mặc định (hạn riêng theo sản phẩm/tài khoản khi cần). |
| DEC-REQ-18 | A | Bên gọi cấp requestId duy nhất; cùng mã và cùng nội dung chỉ thực hiện một lần. |
| DEC-REQ-19 | A | Đơn giá được lưu theo gói; tính phí cho chu kỳ đăng ký tại thời điểm đăng ký. |
| DEC-REQ-20 | A | Tài khoản tiền chính của thuê bao. |
| DEC-REQ-21 | B | Tự động gia hạn nếu đủ tiền và chưa có yêu cầu hủy. |
| DEC-REQ-22 | B | Có thể đăng ký lại cùng gói để nối dài chu kỳ. |
| DEC-REQ-23 | B | Trả lỗi nghiệp vụ gói đã có yêu cầu hủy. |
| DEC-REQ-24 | A | Số nguyên theo đơn vị VND, chỉ chấp nhận số tiền dương (>0). |
| DEC-REQ-25 | C | API hỗ trợ cả OR/AND bằng tham số được khai báo rõ. |
| DEC-REQ-26 | C | Hỗ trợ chọn có/không tính phí đăng ký bằng bộ lọc của API. |
| DEC-REQ-27 | A | Vô hiệu hóa giao dịch mới, giữ số dư và lịch sử để đối soát. |
| DEC-REQ-28 | B | Giữ theo số năm xác định (ghi số năm trong ghi chú). Ghi chú: “tùy bạn muốn”; CHƯA chốt số năm. |
| DEC-REQ-29 | C | Vừa lưu bảng tổng hợp, vừa có tùy chọn xuất file. |
| DEC-REQ-30 | D | Khác (ghi lịch/múi giờ). Ghi chú: “mix B và C”: lịch/timezone riêng cấu hình, mặc định hàng ngày giờ cố định và chạy tay; CHƯA chốt giờ/timezone. |
| DEC-REQ-31 | B | Chốt số liệu một lần; dữ liệu muộn ghi thành điều chỉnh kỳ sau. |

### 12.3. Nội dung đầy đủ bổ sung UC01 đã nhận 15/10/2026

| Mã ổn định | Nội dung có hiệu lực | Giới hạn |
|---|---|---|
| DEC-UC01-01 | Chỉ nhánh tự cấp hết số **và có đồng ý chờ** nhận WAITING tối đa 24h, quá hạn EXPIRED; yêu cầu mới dùng requestId mới. | Q01=A được điều chỉnh bởi CR-UC01-001; không chờ mặc định. |
| DEC-UC01-02 | Khi bổ sung số thử lại WAITING còn hạn; có recovery chống bỏ sót. | Chưa chốt lịch/thứ tự/khóa. |
| DEC-UC01-03 | Một Customer có nhiều giấy tờ đã xác minh; UC01 dùng liên kết có sẵn, không tự ghép theo tên/ngày sinh. | Chuẩn hóa/xác minh mới còn OQ-12. |
| DEC-UC01-04 | JWT do hệ thống ngoài phát; backend kiểm tra chữ ký, issuer, expiry và quyền. | Claims/keys/audience còn OQ-15. |
| DEC-UC01-05 | Cùng requestId khác payload: IDEMPOTENCY_CONFLICT, giữ yêu cầu gốc, không tác động lần hai. | Phạm vi ID, retention, canonical payload còn OQ-02. |

### 12.4. Thu nhận câu trả lời mới

Dùng [phiếu quyết định trước code](decision-questionnaire.md). Tất cả ô ban đầu để trống. Sau khi chủ dự án trả lời rõ, kiểm tra mâu thuẫn, ghi người/ngày/phạm vi/ảnh hưởng tại đây rồi mới đồng bộ SRS/UML/thiết kế. Chọn giữ Open không đóng OQ; duyệt một checkpoint không mặc nhiên duyệt checkpoint khác.

### 12.5. Đính chính phạm vi Open

- VND nguyên, OR/AND báo cáo, tự gia hạn khi đủ tiền và chặn tài khoản hết hạn **đã được chọn**, xem DEC-REQ-21/24/25/27.
- Tái cấp số sau cách ly đã chốt định hướng, chưa có thời gian/quy trình; không triển khai trong UC01 đầu.
- Không tự ấn định số năm retention, giờ job, timeout hay giới hạn đo tải.
- OQ-08 vẫn Resolved; OQ khác chỉ đóng từng phần khi đủ bằng chứng.


### 12.6. Chuẩn hóa vị trí tài liệu — CR-DOC-002

**Approved — 18/10/2026, chủ dự án:** yêu cầu chỉ giữ một thư mục docs. Đưa toàn bộ tài liệu từ thư mục lồng nhau lên docs/, cập nhật đường dẫn README/SRS/UC01/roadmap và hướng dẫn kiểm chứng. Đối chiếu hash từng file khi di chuyển; không thay đổi nghiệp vụ, trạng thái duyệt hoặc nội dung requirement.md.


## 13. Quyết định tiếp nhận ngày 18/10/2026 — bộ trước code

Người lựa chọn: chủ dự án. Q01–Q24 mỗi câu một ô, Q25 chưa chọn. Nguồn: file trong workspace, SHA-256 lúc nhận 384c5f849c655649685d1722f1bd38678ba8ab6d01167aafcd4e4642a7df27c8. Yêu cầu hiện tại là đồng bộ để chủ dự án đọc rồi mới duyệt M1. Mã DEC-PRE dưới đây tự chứa nội dung; không cần mở phiếu để hiểu quyết định.

| Mã | Đáp án | Nội dung nguyên văn được chọn | Ghi chú nguyên văn | Hiệu lực |
|---|---|---|---|---|
| DEC-PRE-01 | B | So tất cả trường được gửi sau chuẩn hóa; khác bất kỳ trường nào thì từ chối, it nhat 1 truong lien quan den giay to tuy than duoc gui. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-02 | A | CSKH gửi tham chiếu chứng cứ xác minh từ quy trình ngoài; backend chỉ dùng giấy tờ hợp lệ đã xác minh để tạo/liên kết Customer; liên kết thêm giấy tờ vào khách cũ ngoài UC01. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-03 | A | Một ứng dụng Spring Boot chia module, Java 21, PostgreSQL; JPA cho CRUD, JDBC khi có lý do ở thiết kế transaction; Flyway; broker/cache. | Không ghi thêm | Stack đã chọn; broker/cache theo CR-ARCH-001 |
| DEC-PRE-04 | A | JWT ký bất đối xứng, xác minh qua JWKS cấu hình; issuer/audience được kiểm, scope theo thao tác và caller sở hữu request. Điền issuer/audience/scope map hoặc cấu hình demo ở ghi chú. | he thong xac thuc chay local | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-05 | A | Duy nhất theo caller ứng dụng + UC01 + requestId; caller không xem request bên khác. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-06 | B | Retention cấu hình được; ghi thời hạn cụ thể, xử lý ID sau hết hạn và rủi ro retry muộn trong ghi chú. | Giữ kết quả đầy đủ 7 ngày sau trạng thái cuối; không dọn yêu cầu đang xử lý/chờ. Sau đó giữ dấu mã đã sử dụng, không tự xử lý lại mã cũ như yêu cầu mới. | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-07 | A | So canonical business payload đã chuẩn hóa: hồ sơ, mode, số chọn, consent; bỏ JWT/correlationId và thứ tự JSON. Chuẩn hóa theo Q01/Q02. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-08 | A | FIFO theo mốc WAITING cho các yêu cầu chờ hợp lệ; ưu tiên xử lý yêu cầu chờ trước tự cấp mới khi có số bổ sung. Chọn số vẫn tuân điều kiện khả dụng. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-09 | A | Giao dịch DB ngắn + row lock/unique constraint, lock order và xử lý retry/deadlock có giới hạn sẽ viết chi tiết. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-10 | A | Trigger sau nạp số + recovery mỗi 60 giây (đề xuất); dùng đồng hồ backend thống nhất, kiểm hạn tại hoàn tất, không reset deadline sau retry/restart. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-11 | A | Hướng REST: POST /api/v1/activations, GET /api/v1/activations/{requestId}, GET /api/v1/phone-numbers; success mới 201, WAITING 202, conflict 409. Các lỗi/replay/in-progress cần lập bảng và duyệt OpenAPI tiếp. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-12 | A | Chụp giá áp dụng tại từng lần đăng ký/gia hạn, lưu lịch sử; đơn giá định nghĩa gói có hiệu lực lúc thu. Không sửa phí đã commit. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-13 | A | Chu kỳ theo số ngày gói, nối từ hạn hiện tại nếu còn hiệu lực; hủy chờ chặn đăng ký nối dài, không tự gia hạn; thiếu tiền ở cuối kỳ thì hết hiệu lực, không grace ngầm. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-14 | A | Theo trạng thái được commit trước, nhưng hủy đã được nhận trước mốc cuối kỳ phải ngăn gia hạn; request hủy mới khi đã chờ hủy trả lỗi, retry cùng ID trả kết quả gốc. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-15 | B | Cùng chính sách UC01 sau khi UC01 được chốt nhưng retention riêng: ghi rõ thời hạn. | Thoi han chon tuy ban de xuat, nhung ghi ro ra cho toi va cai y co the cau hinh duoc | Chính sách đã chọn; số ngày tiền chỉ yêu cầu đề xuất |
| DEC-PRE-16 | A | UC01 chỉ tạo chính; tài khoản phụ theo loại dịch vụ cấu hình, tạo qua seed/quy trình riêng; tiền integer VND trong miền số có giới hạn kiểm tra tràn. Điền max giao dịch/max số dư, hạn tài khoản phụ. | Không ghi thêm | Hướng đã chọn; chưa có hạn mức/hạn phụ |
| DEC-PRE-17 | A | Khoảng [X,Y) theo thời điểm commit giao dịch; OR/AND và includeFee bắt buộc chỉ rõ, không có mặc định ngầm; chỉ transaction commit thành công. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-18 | A | Mỗi debit tiêu dùng có tham chiếu gói/ngữ cảnh dịch vụ xác định; phí liên kết đăng ký. Báo danh sách thuê bao không trùng, phân trang cursor; caller báo cáo có scope riêng. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-19 | A | Đề xuất Asia/Ho_Chi_Minh; hết hạn 00:05, tổng hợp 00:30 mỗi ngày; từng lịch có thể đổi/chạy tay. Quyền dùng gói/tài khoản không được kéo dài chỉ vì job chạy trễ. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-20 | A | Kỳ ngày trước theo timezone Q19; đếm giao dịch thành công và tổng VND theo ngày/thuê bao/gói/loại phí, lưu DB; CSV tùy chọn. Cutoff lúc chốt; dữ liệu muộn có ID điều chỉnh ở kỳ sau. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-21 | A | Đề xuất retention 5 năm cấu hình; bản demo không chạy purge, số chưa có quy trình giải phóng/cách ly không tái cấp. Thời gian cách ly chốt riêng trước nghiệp vụ ngừng thuê bao. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-22 | A | Đề xuất warm-up 2 phút, đo 10 phút, p95 ≤500ms/p99 ≤1s, lỗi hệ thống ≤0,1%; báo riêng business failure/timeout. Ngưỡng TPS và correctness giữ nguyên; cần ghi cấu hình máy/DB trước đo. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-23 | A | Đề xuất 10 cặp tranh chấp, chia đều 50% tổng request debit phát lên (kể cả retry); 50% còn lại phân rộng, ghi từng attempt + logical ID; phí nội bộ không là request debit độc lập. Chứng minh overlap thực, không chỉ tỷ lệ phân nhóm. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |
| DEC-PRE-24 | A | k6 phát tải, script đối soát riêng đọc log request và DB; tra cứu/retry có giới hạn rồi báo unresolved, unresolved >0 thì chưa kết luận đối soát đạt. | Không ghi thêm | Đã chọn; chi tiết còn Open theo bảng OQ |

### 13.1. CR-ARCH-001 — broker và cache

**Approved — 18/10/2026, chủ dự án.** Xác nhận trong phiên: “dùng cả 2, và bạn thấy cái gì tốt nhất thì cứ ghi ra, tôi mặc định duyệt”. Agent được giao chọn công cụ/phạm vi phù hợp; không phải duyệt toàn M3/M4.
- Cũ: khung không mặc định broker/cache. Mới: RabbitMQ gửi tín hiệu kho số bổ sung sau commit; worker đọc PostgreSQL xử lý WAITING FIFO/idempotent. Recovery DB mỗi 60s vẫn khôi phục nếu mất tín hiệu.
- Redis cache-aside danh mục gói cho đường đọc, TTL 60s cấu hình và invalidate sau cập nhật, lỗi cache fallback DB. Giá thu phí phải đọc DB/snapshot hợp lệ.
- Redis không là nguồn quyết định số dư, cấp số, trạng thái request, tombstone hay khóa idempotency. RabbitMQ có publisher confirms, consumer ack sau xử lý và có thể redelivery; không hứa exactly-once delivery.
- Lý do: phân vai rõ cho tín hiệu nạp số/cache danh mục; giữ một backend, không chuyển UC01 sang async toàn phần.
- Ảnh hưởng: ADR-001, architecture/C4/modules, recovery/concurrency/test/deployment; chưa sửa pom.xml/code.

### 13.2. CR-DOC-003 — đồng bộ trước duyệt và xóa phiếu cũ

**Approved về phạm vi thao tác — chủ dự án, 18/10/2026.** Lưu quyết định đầy đủ, xóa hai phiếu REV-SRS-001/REV-UC01-002; giữ lịch sử lựa chọn ở mục 8–12. Giữ phiếu trước code hiện tại để trả lời Q25 sau khi đọc SRS 0.6/UC01 0.5.
Tài liệu cập nhật là DRAFT chờ rà soát; M1–M8 không tự tick. Đề xuất mới trong review-guide không có hiệu lực chỉ vì đã được viết.

### 13.3. Cách đọc các điểm chưa đủ quyết định

Xem [hướng dẫn rà soát](review-guide.md). Q01 ghi ít nhất một trường giấy tờ chưa xác nhận bỏ yêu cầu loại+số; bản cập nhật giữ loại+số và nêu để chủ dự án rà soát. Q14 giữ ưu tiên hủy đã tiếp nhận hợp lệ trước cuối kỳ, không rút gọn thành ai commit trước thắng.
Q15 chỉ giao đề xuất retention tiền; Q16 chưa điền hạn mức/hạn phụ. Tombstone UC01 cần giữ fingerprint và kết quả tóm tắt để không tái xử lý; HTTP sau hết 7 ngày replay còn P4.


## 14. Xóa phiếu cũ theo yêu cầu chủ dự án

Hai phiếu lịch sử REV-SRS-001 và REV-UC01-002 đã xóa ngày18/10/2026 theo CR-DOC-003. Bảng31 quyết định yêu cầu và5 quyết định UC01 ở mục12 được giữ nguyên; mục8–10 còn ghi lịch sử đáp án/thay thế Hybrid. Không yêu cầu mở file đã xóa để hiểu SRS/UC01. Phiếu hiện tại DECISION-PRECODE-001 giữ lại để trả lời Q25.


## 14. Xác nhận nghiệm thu tài liệu ngày 19/10/2026

**Nguồn xác nhận:** Chủ dự án gửi ảnh cho thấy `decision-questionnaire.md` có **Q25=A**, đồng thời nêu rõ “các file khác tôi cũng chốt”. Người duyệt: chủ dự án; ngày: 19/10/2026. Đây là xác nhận từ người dùng, **không** phải bằng chứng chạy code hay commit Git.

| Mốc | Phạm vi được xác nhận | Kết quả | Giới hạn |
|---|---|---|---|
| **M1** | SRS v0.6, UC01 v0.5, 27 AC, DEC-PRE-01–24/Q25=A | **Approved** | Chưa duyệt thiết kế DB/API/SQL của Phase 4 |
| **M2** | Use Case, Domain Model và Sequence UC01 hiện có trong `docs/02-analysis/` cùng `analysis.md` | **Approved** theo xác nhận “các file khác ... chốt” | State và Sequence của UC02–06 cập nhật khi đến lượt |
| **M3** | Architecture v0.2, C4, module structure, ADR-001 (stack/RabbitMQ/Redis/JWKS) | **Approved** theo xác nhận “các file khác ... chốt” | Không tự coi Keycloak hay Docker runtime là đã được cài và kiểm tra |
| **M4** | ERD/OpenAPI/Design Class/concurrency UC01; T1–T7 đã duyệt ngày 19/10 | **Đã duyệt thiết kế UC01** | Mọi bằng chứng build/migration/race test chuyển P5; các UC khác thiết kế theo vòng |

**Nguyên tắc bất biến:** Không thay nguyên văn câu trả lời Q01–Q24, không làm hiệu lực lại DEC-REQ-10/11 đã bị CR-UC01-001 thay thế. `Q25=A` duyệt yêu cầu M1, không duyệt những quyết định SQL/API/P4 chưa tồn tại lúc người dùng chọn Q25. Các OQ liên quan UC02–UC06/lệnh tiền không bị tự đóng.

**Hạng mục kỹ thuật M4 được trình riêng:** canonical normalization (Unicode/absent/null); HTTP status cho replay/in-progress/tombstone; JWT caller mapping và mock IdP local; khóa/cấp phát FIFO+random/expiry/claim/recovery. Nội dung là đề xuất P4, không ghi thành DEC-PRE hoặc quyết định nghiệp vụ mới cho đến khi chủ dự án duyệt.


## Phụ lục quyết định 19/10/2026 – M4 và môi trường triển khai

| Mã | Phạm vi | Nội dung / người duyệt |
|---|---|---|
| DEC-M4-001 | UC01 P4.1–P4.5 | Chủ dự án duyệt các đề xuất T1–T7 tại `04-design/m4-review-checklist.md` ngày 19/10/2026. Không suy diễn là đã build/test hoặc có migration hoạt động. |
| DEC-ENV-001 | Môi trường phát triển | Máy công ty không cài Docker, dùng Spring Boot JVM + PostgreSQL Supabase. Máy nhà có Docker Compose; cùng codebase dùng cấu hình `office`/`home`, không đưa host/secret vào Java. |
| DEC-ENV-002 | Broker/cache | ADR-001 RabbitMQ và Redis vẫn hiệu lực. Ở office không có broker/cache thì recovery bằng DB là đường vận hành dev được phép, Redis fallback DB; **không nghiệm thu full broker/cache từ office mode**. Không tự bỏ MQ/Redis khỏi kiến trúc. |
| DEC-ENV-003 | IdP | JWT issuer ngoài backend vẫn bắt buộc cho các API bảo vệ. Keycloak có thể chạy ở nhà bằng Docker; môi trường office phải cấu hình IdP/JWKS tiếp cận được để test API. Không tắt xác thực tự động vì công ty không cài Docker. |

OQ-04/OQ-14, các nghiệp vụ UC02–06 và công việc xác minh runtime vẫn Open theo mốc cũ. Không ghi credential Supabase vào tài liệu hoặc Git.


## Bổ sung thay đổi 19/10/2026 – CR-DB-001

Chủ dự án xác nhận **chuyển DBMS PostgreSQL sang MySQL** và môi trường công ty không chạy được/cài được dịch vụ database theo giới hạn hiện tại; phần thực thi sẽ chạy ở nhà. Trạng thái: **DBMS MySQL đã chốt**, version/DDL/isolation/UUID/Java-JDBC và các đề xuất nghiệp vụ UC02–06 của bộ `07-business-review/` **chưa duyệt**. Chi tiết tại `CR-DB-001-mysql.md`, ADR-003 và `07-business-review/16-PHIEU-DUYET-TONG-HOP.md`.

Không thay ngược nội dung đã chọn trong DEC-PRE-03; chỉ ghi lựa chọn PostgreSQL **superseded ở thành phần DBMS**. M4 UC01 được duyệt lịch sử nhưng không đồng nghĩa MySQL migration/locking đã được duyệt hoặc test.

## 15. Đợt rà soát tài liệu ngày 24/10/2026

**Nguồn chỉ đạo:** chủ dự án yêu cầu đối chiếu requirement.md, rà soát độ đầy đủ/chi tiết/ngoại lệ, hoàn thiện tài liệu có căn cứ, tạo phiếu trắc nghiệm Markdown dễ hiểu cho các điểm cần quyết định. Đây là **ủy quyền rà soát/biên soạn tài liệu**, không phải phê duyệt đáp án nghiệp vụ mới, không duyệt checkpoint hoặc cho phép commit/push/code.

**Đã làm trong phạm vi tài liệu:** đồng bộ trạng thái hiện hành MySQL/M1–M4 lịch sử, sửa hướng runtime/implementation/deployment bị lệch, ghi [báo cáo chất lượng](../07-business-review/18-RA-SOAT-CHAT-LUONG.md), [64 kịch bản](../07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md), [đặc tả chung DRAFT](../07-business-review/20-QUY-TAC-CHUNG-VA-BIEN-NGHIEP-VU_DRAFT.md). [Phiếu 16 v0.2](../07-business-review/16-PHIEU-DUYET-TONG-HOP.md) bổ sung ví dụ/hệ quả/Q21–34; không chọn đáp án thay người dùng.

### 15.1. Theo dõi phần Open bổ sung – không đóng OQ cũ

| OQ hiện có | Chi tiết phát hiện / câu hỏi phiếu 16 | Mốc cần giải quyết |
|---|---|---|
| OQ-01/02/10 | Q01/Q02/Q09/Q34: registry/replay UC03, tiền phụ, hạn mức và mốc kiểm hạn đang chờ lock | Trước UC02–05 liên quan |
| OQ-06/07/09 | Q04–07/Q21–25/Q29: ngày chu kỳ, click đôi khác ID, kỳ trả trước/hủy, tiếp nhận bền vững và downtime/giá/ngừng bán | Trước UC02/03/JOB-01 |
| OQ-03/13 | Q10–12/Q26–28: attribution, commit timestamp chưa có thiết kế đủ, snapshot/cutoff/late contribution | Trước UC06/JOB-02 |
| OQ-05/12/15 | Q13–16/Q18/Q33: delta MySQL và chuẩn số; Q31 trust evidence/caller; cấu hình/runtime chưa kiểm | Delta M4 UC01, trước API thật |
| OQ-04/14 | Q32: mẫu số latency/lỗi, success window/timeout/attempt contention | Trước thực thi P6 |
| OQ-11 | Archive/purge sau demo và backup/restore thực tế vẫn Open; demo không purge giữ quyết định cũ | Trước deployment thực |

**Các thay đổi có thể cần CR nếu được chọn:** Q22=A thay nghĩa “cuối chu kỳ hiện tại”; Q22=B mở hoàn tiền; Q26=A thay mốc lọc commit; Q27=B giảm đảm bảo snapshot/AC; Q28=B đổi ngày báo cáo theo service time. Hiện chưa có lựa chọn, chưa lập chúng thành CR Approved. Q03/Q08/Q17/Q20 không hỏi lại phần đã có căn cứ.

Không tạo mã CR-ENV-002 riêng vì không có bản ghi phê duyệt độc lập; môi trường đổi cùng CR-DB-001/ADR-003. Giữ nguyên đáp án lịch sử DEC-PRE và phiếu decision-questionnaire, requirement.md cùng mã FR/BR/NFR/27 AC. Trạng thái OQ cũ chỉ đóng phần thật sự có đáp án và bằng chứng sau này.

## 16. Rà soát lại Q07 – 25/10/2026

**Ủy quyền:** Chủ dự án yêu cầu sửa phiếu 16, suy xét lại và giải thích phương án phù hợp nhất sau khi chỉ ra sự chồng lấn hai tác vụ. Đây là duyệt phạm vi biên soạn đề xuất, **không tự coi là duyệt lịch JOB-01 mới**. Q01/Q02/Q04/Q05/Q06 đã được người dùng đánh dấu trong file nhưng chưa tổng hợp/đóng các OQ trong nhiệm vụ riêng này.

### 16.1. Bảo toàn lựa chọn Q07 trước khi sửa

Snapshot file trước sửa cho thấy Q07=A cũ được đánh dấu. Lưu nguyên nội dung và dấu chọn dưới đây; không dùng đáp án này làm xác nhận A mới có ý nghĩa khác. Việc bỏ dấu ở **câu Q07 đang hoạt động** chỉ nhằm chờ xác nhận lại đề xuất đã đổi, không xóa lịch sử đáp án. Các câu khác ngoài Q25 dẫn chiếu giữ nguyên byte nội dung sau giải mã UTF-8.

#### Q07 – Gia hạn lúc nào nếu kỳ kết thúc trước job hàng ngày?
**Tình huống:** Gói hết 00:00, JOB-01 chạy 00:05. Không được tự cho dùng thêm 5 phút khi chưa thu phí; lịch 00:05/00:30 đã chốt, không cần chọn lại giờ.
- [x] **A – Khuyến nghị:** Có worker gia hạn sát thời điểm từng kỳ; JOB-01 vẫn 00:05 để rà soát/khôi phục. Chỉ có quyền kỳ mới sau gia hạn commit; nếu xử lý đã qua hạn, áp đúng Q25, không cam kết tuyệt đối không gián đoạn.
- [ ] **B:** Chỉ gia hạn trong JOB-01; các kỳ hết trước giờ job đều là gia hạn muộn theo Q25. Nếu chọn Q25=A, những kỳ đó sẽ hết hiệu lực và không tự gia hạn, kể cả đủ tiền.
- [ ] **C:** Cách khác; mô tả mốc thu phí và mốc bắt đầu quyền kỳ mới.
**Tôi chọn / ghi chú:**

### 16.2. CR-JOB-001 – Đề xuất hợp nhất tác vụ vòng đời

| Thuộc tính | Nội dung |
|---|---|
| Trạng thái | **NOT ADOPTED – KHÔNG ĐƯỢC CHỌN ngày 25/10/2026** theo DEC-JOB-001 tại mục17; giữ nguyên đề xuất bên dưới để truy vết, không đổi DEC-PRE-19 |
| Quyết định cũ có hiệu lực | DEC-PRE-19: JOB-01 mặc định 00:05 hằng ngày, JOB-02 00:30; cấu hình/chạy tay; không kéo dài quyền vì job trễ |
| Đề xuất Q07 cũ bị rút khuyến nghị | Worker gia hạn thường xuyên + JOB-01 hằng ngày rà soát/bù, chưa tách được trách nhiệm đủ rõ |
| Đề xuất thay thế | Một JOB-01 định kỳ thường xuyên sở hữu gia hạn/hủy/hết hạn gói+tài khoản và khôi phục cùng luồng từ DB. Không thêm worker gia hạn riêng hoặc lượt quét bù 00:05; JOB-02/recovery UC01 giữ nhiệm vụ riêng |
| Khoảng chạy đề xuất | Ban đầu chờ 60 giây sau kết thúc lượt rồi chạy lại, cấu hình được, không phải SLA tối đa60s; kiểm thời gian chạy/backlog/khóa/lỗi trước chỉnh |
| Nguyên tắc tải/đồng thời | Lấy bản ghi đến hạn qua index, lô/budget hữu hạn; kiểm lại trạng thái/cycle, fee nguyên tử, chống retry trùng; phối hợp chạy tay/nhiều instance; không hứa đã tối ưu/đo tải |
| Gia hạn muộn | Q25 phải được chọn riêng; Q07 không tự cho phép thu muộn/thu bù nhiều kỳ, không cấp quyền trong khoảng chưa có kỳ hợp lệ |
| Lý do | Giảm chồng lấn xử lý và số thành phần, giữ mô hình đơn giản phù hợp dự án; đổi tần suất thay vì thêm một tác vụ để giữ nguyên lịch cũ |
| Người đề xuất / ngày | Agent, 25/10/2026 theo yêu cầu rà soát của chủ dự án |
| Người duyệt / ngày duyệt | **Chưa có** cho phương án mới; yêu cầu sửa phiếu không là phê duyệt nghiệp vụ mới |
| Ảnh hưởng sau khi duyệt | SRS FR-JOB-03/BR-23/NFR-10/mục6.3; scheduled-jobs; review09/20; transaction/design/tests/config/roadmap. Đề bài gốc giữ nguyên; xác nhận cách đáp ứng job hằng ngày khi chạy nhiều lượt mỗi ngày |

### 16.3. Kiểm tra cần có khi hiện thực

Không có một timer cho từng thuê bao; không full-scan mặc định; không ghi ledger chỉ vì thấy candidate ở truy vấn trước lock. Kiểm EXPLAIN trên dữ liệu đại diện, số lượt/lô/lag và tác động mixed30+30TPS. Test hết hạn API đúng mốc dù JOB-01 chưa chạy, restart/bù việc quá hạn, một bản ghi lỗi không chặn cả hàng đợi, tranh chấp hủy/manual renewal, chạy tay/nhiều instance và không thu lại cùng cycleKey. Chưa có implementation/runtime test trong lần sửa tài liệu.


## 17. DEC-JOB-001 – Chốt phương án worker gia hạn và JOB-01 hằng ngày

**Trạng thái:** APPROVED về cách tổ chức tác vụ. **Người duyệt:** Chủ dự án. **Ngày:** 25/10/2026. **Nguồn xác nhận trong phiên:** “nếu thế chốt phương án như ban đầu đi vậy”. Ngữ cảnh ngay trước đó đã phân biệt worker kiểm tra gia hạn thường xuyên với JOB-01 lịch cố định00:05 và phương án hẹn deadline riêng.

| Phạm vi | Quyết định có hiệu lực |
|---|---|
| Worker gia hạn | Kiểm tra thường xuyên gói đến hạn cần tự gia hạn, là tác vụ hỗ trợ nội bộ; không đặt một lịch riêng cho từng gói |
| JOB-01 | Giữ lịch00:05 hằng ngày theo DEC-PRE-19; xử lý hết hạn gói/tài khoản, hủy đến hạn và rà soát/bù phần bỏ sót theo cùng quy tắc |
| Phối hợp | Dùng chung logic kiểm điều kiện/cycle và thu phí nguyên tử; cùng kỳ chỉ một phí, retry/chạy tay/nhiều instance không tạo tác động trùng; JOB-01 không thu lại kỳ worker đã hoàn tất |
| JOB-02 / UC01 | JOB-02 vẫn00:30 tổng hợp tiêu dùng; recovery UC01 giữ nhiệm vụ WAITING, không gộp với worker gia hạn gói |
| Không chọn | CR-JOB-001 hợp nhất thành một JOB-01 thường xuyên; phương án hẹn theo deadline từng gói/đầu việc chưa chọn |
| Chưa chốt | Khoảng chạy worker (không mặc định60s), batch/index/retry/concurrency cụ thể; chính sách thu muộn Q25, mốc hủy Q24 và kỳ trả trước Q22 vẫn theo đáp án riêng |
| Giới hạn quyền | Không grace ngầm, không quyền dùng kỳ mới trước commit hợp lệ; không reset/thu lại nghiệp vụ FAILED thiếu tiền khi điều kiện thay đổi |
| Ảnh hưởng | Phiếu16 Q07/Q25 dẫn chiếu; SRS và scheduled-jobs phần tổ chức tác vụ; review09/quy tắc20; roadmap. Không đổi lịch baseline, không tự đóng OQ hoặc checkpoint |

CR-JOB-001 giữ lịch sử với trạng thái NOT ADOPTED. Đây là chốt phương án gốc sau trao đổi, không phê duyệt đề xuất hợp nhất và không duyệt toàn bộ nội dung phiếu16. Các đáp án khác giữ nguyên; Q25 chưa được chọn thay người dùng.

### 17.1. Lưu nguyên bản Q07 v0.3 trước xác nhận

#### Q07 – Chọn một cách duy nhất để xử lý gia hạn và hết hạn
**Cập nhật 25/10/2026:** A cũ gồm worker gia hạn thường xuyên và JOB-01 lúc 00:05 rà soát lại. Cách mô tả đó chồng lấn trách nhiệm, agent rút khuyến nghị cũ. Bản này thay bằng phương án một JOB-01 duy nhất.

**Bảo toàn đáp án của bạn:** Trước lần sửa này, bạn đã đánh dấu **A của nội dung cũ**. Lựa chọn/nội dung gốc được lưu tại [sổ quyết định, mục 16](../00-project/clarification-log.md). A mới có ý nghĩa khác nên để trống chờ bạn xác nhận; không tự chuyển dấu chọn cũ sang phương án mới. Các câu khác giữ nguyên đáp án đã điền.

**Tình huống giữ nguyên một mốc:** Gói hết lúc **00:00**. Nếu chỉ đợi JOB-01 lúc **00:05**, hệ thống xử lý chậm ít nhất tới lượt đó. Mục đích là xử lý gần hạn hơn mà không có hai tác vụ cùng quét và thu phí gia hạn.

- [ ] **A – Khuyến nghị cho dự án này:** Chỉ có **một JOB-01 xử lý vòng đời**, chạy định kỳ thường xuyên ngay trong backend: gia hạn khi hợp lệ, hoàn tất hủy đến hạn, xử lý gói/tài khoản hết hạn. Không thêm worker gia hạn riêng, không thêm lượt rà soát/gia hạn hằng ngày lúc 00:05. Đề xuất khởi điểm chờ **60 giây sau khi lượt trước kết thúc** rồi chạy lượt tiếp, cấu hình được; mỗi lượt lấy các bản ghi đến hạn theo chỉ mục, chia lô và giới hạn công việc. Khôi phục sau lỗi vẫn dùng chính JOB-01 đọc việc còn tồn trong DB. Đây là đề xuất đổi lịch JOB-01 theo **CR-JOB-001**, chưa tự thay DEC-PRE-19.
- [ ] **B – Giữ lịch đã chốt:** Một JOB-01 chỉ chạy hằng ngày lúc **00:05**, xử lý cùng các nghiệp vụ trên; không có worker gia hạn riêng. Ít lượt kiểm tra DB hơn nhưng nhiều gói sẽ được xử lý trễ hơn. Gia hạn sau hạn vẫn theo Q25, không tự cho dùng thêm trong thời gian chờ.
- [ ] **C – Phương án khác:** Ghi yêu cầu về độ trễ/cách chạy. Nếu muốn hẹn riêng đúng deadline từng gói, cần thiết kế lưu lịch bền vững, thay lịch khi nối/hủy và khôi phục khi mất lịch; chưa khuyến nghị độ phức tạp này cho bản đầu.

**A hoạt động như thế nào?**

| Việc JOB-01 tìm được | Xử lý trong cùng tác vụ |
|---|---|
| Đăng ký gói đến hạn, có yêu cầu hủy hợp lệ | Hoàn tất hủy theo Q22/Q24; không tự thu phí gia hạn |
| Đến hạn gia hạn, còn đủ điều kiện | Kiểm lại tiền/quyền/cycle dưới transaction, thu phí + ghi kỳ + kết quả nguyên tử; nếu đã trễ thì theo Q25 |
| Đến hạn nhưng không đủ tiền/không được gia hạn | Hết hiệu lực theo quy tắc; không trừ tiền thành công, không ngầm thử lại nghiệp vụ đã FAILED |
| Tài khoản đến hạn | Chặn giao dịch theo mốc hạn, cập nhật trạng thái và giữ tiền/lịch sử |
| Backend từng ngừng hoặc lượt trước lỗi | Đọc lại bản ghi còn cần xử lý; không cần tạo một tác vụ thứ hai để quét bù |

**Ví dụ về độ trễ:** Gói hết **00:00**, JOB-01 xử lý được lúc **00:00:20**. Trong khoảng trống gói không có quyền dùng kỳ mới. Nếu chọn Q25=B và đủ điều kiện, thu một kỳ mới từ mốc xử lý hợp lệ; nếu chọn Q25=A thì không được tự thu sau hạn. Mốc 00:00:20 chỉ minh họa, không phải lịch cố định. Chờ 60 giây giữa lượt **không bảo đảm** mọi gói được xử lý trong 60 giây: còn thời gian chạy, backlog, khóa DB và sự cố. Nếu bạn đòi không gián đoạn, A chưa đáp ứng và cần thiết kế/chốt nghiệp vụ riêng.

**Kết hợp Q25:** Vì cách A lấy gói **đã đến hạn** rồi mới xử lý, không thể vừa dùng cách này vừa kỳ vọng tự gia hạn thành công khi Q25=A cấm mọi khoản thu sau hạn. Nếu muốn tự gia hạn bằng JOB-01 định kỳ, khuyến nghị đọc/chọn riêng Q25=B hiện có, hoặc ghi cửa sổ cho phép xử lý muộn tại Q25=C. Chọn Q07=A cùng Q25=A vẫn có ý nghĩa xử lý hết hạn/hủy, nhưng không đáp ứng mong muốn tự gia hạn các kỳ đã qua hạn; agent phải báo tổ hợp đó khi tổng hợp đáp án, không tự sửa chính sách.

**Chi phí và cách giới hạn:** A có thêm các lượt truy vấn so với B, không phải miễn phí. Thiết kế phải lấy việc đến hạn qua chỉ mục thay vì mặc định quét cả bảng, giới hạn lô/thời gian mỗi lượt, tránh lỗi một bản ghi chặn các bản ghi khác và phân lượt cho cả gói lẫn tài khoản. Cột/index cụ thể và khoảng chạy phải được kiểm EXPLAIN/test tải trước khi gọi là tối ưu về hiệu năng. Không tạo một thread/timer cho mỗi thuê bao, không đưa việc gia hạn lên RabbitMQ/Redis chỉ để thay bộ lịch đơn giản.

**Chống làm hai lần:** Trong một instance không để các lượt JOB-01 chồng nhau; nếu chạy nhiều instance hoặc có chạy tay, phải phối hợp quyền xử lý và kiểm lại trạng thái/cycle trong DB. Dùng lại khóa nghiệp vụ của cùng kỳ khi retry; mất phản hồi sau commit phải tra kết quả trước thử lại, không thu phí hai lần. Gói/tài khoản đã hết hạn phải bị kiểm quyền bằng timestamp ngay ở đường API, không đợi JOB-01 đổi trạng thái.

**Tại sao khuyến nghị A mới?** Một nơi sở hữu toàn bộ vòng đời và đường khôi phục, phù hợp các gói hết hạn ở nhiều thời điểm, ít thành phần hơn cách cũ. Đây là lựa chọn cân bằng độ đơn giản và độ trễ cho phạm vi hiện tại, chưa phải kết luận đã benchmark tốt nhất. **JOB-02 tổng hợp báo cáo vẫn riêng lúc 00:30; recovery UC01 vẫn phục vụ WAITING, không phải tác vụ gia hạn gói.**

**Phạm vi cần duyệt:** Chọn A mới là đồng ý đề xuất đổi JOB-01 từ một lần/ngày lúc 00:05 sang lịch định kỳ thường xuyên, thực hiện mỗi ngày nhiều lượt; không phải giữ lịch cũ rồi bổ sung một job mới. Sau xác nhận sẽ ghi CR và đồng bộ SRS FR-JOB-03/BR-23/NFR-10, đặc tả job/design/test. Q25 vẫn cần chọn riêng để xác định cách xử lý muộn; không đổi nội dung đề bài gốc.

**Tôi chọn / ghi chú:**


## 18. Viết lại câu hỏi Q09–Q34 dễ hiểu – 25/10/2026

**Yêu cầu chủ dự án:** “viết lại mấy câu từ q09 trở xuống ... khó hiểu cả câu hỏi lẫn các phương án”. Phạm vi là biên tập lại cách diễn đạt, không phải phê duyệt thay đổi nghiệp vụ. Phiếu16 v0.5 thêm giải thích MAIN/tài khoản phụ/bên gửi lệnh, viết lại26 câu theo tình huống → lựa chọn → kết quả; giữ mã câu và ý nghĩa A/B/C, toàn bộ dấu chọn/ghi chú đã có, Q01–Q08 và bảng xác nhận không đổi.

**Điểm giữ rõ:** Q09=A nhận lệnh cộng/trừ trên MAIN hoặc tài khoản phụ hợp lệ nhưng chỉ tác động đúng account được chỉ rõ; không tự chuyển GAME sang MAIN hoặc gộp tiền. Phí đăng ký gói vẫn thu MAIN. Cách “ưu tiên GAME, thiếu thì lấy toàn bộ MAIN” người dùng đã nêu đang là nội dung cần xác định phạm vi/chọn riêng tại Q09=C; chưa có xác nhận áp dụng, không tạo DEC/CR Approved cho cách đó.

Q07 vẫn theo DEC-JOB-001 đã duyệt: worker gia hạn thường xuyên + JOB-01 hằng ngày00:05. Q12 giữ mốc/tập chốt đã lưu bền vững khi retry dù lượt trước lỗi; Q24/Q34 vẫn dùng giờ từ DB. Các phương án làm thay đổi baseline ở Q22/Q26/Q27/Q28 được ghi rõ cần CR nếu chọn. Các câu chưa chọn vẫn Open, không tự đóng OQ/checkpoint và không sửa SRS/đề bài/code trong lượt biên tập này.

## 19. Tiếp nhận phiếu 16 và xác nhận bổ sung — 26/10/2026

**Người quyết định:** Chủ dự án. **Căn cứ:** các dấu chọn/ghi chú trong phiếu 16 và trả lời trực tiếp trong phiên. Phiếu được giữ nguyên, kể cả Q26/Q34=B đã được thay bằng CR trong chat. [Bản tiếp nhận](decision-receipt-20261026.md) lưu từng câu, lựa chọn và hiệu lực; không coi phương án khuyến nghị chưa được chọn là quyết định.

Các lựa chọn có hiệu lực: Q01=A, Q02=A, Q04=A, Q05=A, Q06=A, Q07=A (DEC-JOB-001), Q09=A, Q10=A, Q11=A, Q12=A, Q13=B, Q14=C, Q15=A, Q16=A, Q18=A, Q19=A, Q21=B kèm xác nhận lịch ngày, Q22=A, Q23=A, Q24=A kèm CR-CAN-003, Q25=B, Q27=A, Q28=A, Q29=B, Q30=A, Q31=A, Q32=A, Q33=A. Q03/Q08/Q17/Q20 là mục tham chiếu. Q26/Q34=B trong phiếu là lịch sử, hiệu lực mới theo CR-TIME-001/002.

| Mã | Quyết định mới | Trạng thái / nguồn |
|---|---|---|
| DEC-BE-21 | Ngày đăng ký là ngày đầu; gói 30 ngày từ 10:15 ngày 01/10 hết đúng 00:00 ngày 31/10, giờ Việt Nam | Approved, chủ dự án xác nhận trong chat |
| CR-CAN-002 | Hủy cuối toàn bộ thời gian đã trả, không hoàn phí; thay chi tiết “cuối chu kỳ hiện tại” của DEC-04 | Approved theo Q22=A; [CR chi tiết](CR-CAN-002-prepaid-cancellation.md) |
| CR-CAN-003 | Sau xác nhận commit ý định hủy, kiểm giờ DB; receipt trước hạn mới được nhận hủy; mất phản hồi tra lại, kết quả đã chốt được replay | Approved, trả lời “Đồng ý mốc xác nhận sau commit; ghi rõ quy tắc và xử lý mất phản hồi”; [CR chi tiết](CR-CAN-003-cancellation-receipt.md) |
| CR-TIME-001 | Báo cáo dùng mốc ghi nhận nghiệp vụ trong transaction cuối cùng đã commit; không đòi timestamp hoàn tất commit vật lý | Approved, “Đổi sang mốc ghi nhận nghiệp vụ của transaction đã commit; lập CR”; [CR chi tiết](CR-TIME-001-reporting-time.md) |
| CR-TIME-002 | Kiểm giờ DB dưới khóa ngay trước cập nhật tiền; còn hạn lúc kiểm có thể commit qua hạn | Approved, “Đổi sang kiểm giờ DB dưới khóa trước cập nhật; lập CR”; [CR chi tiết](CR-TIME-002-account-expiry.md) |

### Phần OQ được chốt và phần thực thi còn mở

| OQ | Nội dung đã giải quyết bằng phiếu/CR | Phần còn mở và mốc xử lý |
|---|---|---|
| OQ-01/10 | MAIN/phụ tách biệt, đúng accountId; không fallback/gộp; 10 triệu/lệnh, 100 triệu/số dư; kiểm hạn theo CR-TIME-002 | Danh mục seed phụ, migration và test giới hạn/hạn trước UC tiền |
| OQ-02 | Replay 30 ngày UC02/04/05, 7 ngày UC03; tombstone, registry và caller ownership | Review vật lý MySQL; test crash/retry/purge trước implementation UC tương ứng |
| OQ-03 | Attribution bắt buộc, page 50/max 200, fixed membership, recordedAt, OR/AND/includeFee | Test materialization/cursor/phân quyền và tài nguyên trước UC06 |
| OQ-04/14 | Q32 chốt cửa sổ đo, mẫu số lỗi/latency, logical success, 10 cặp nóng chiếm 50% debit attempts | Cấu hình máy và bằng chứng chạy tải/overlap/đối soát thực tế trước M7 |
| OQ-05 | Số vào chuẩn E.164; delta MySQL 8.0.46/InnoDB và READ COMMITTED được chọn | Review khóa/FIFO/index; test runtime; tái sử dụng số vẫn ngoài phạm vi hiện tại |
| OQ-06/09 | Snapshot giá, version mục tiêu; ngày lịch; nối thời gian đã trả; gia hạn muộn một kỳ, không hồi tố; khách cũ được gia hạn gói ngừng bán | Dữ liệu giá mẫu/cấu hình và test cạnh tranh trước UC02/worker |
| OQ-07 | Hủy cuối thời gian đã trả; receipt sau xác nhận commit; đúng/sau hạn từ chối; accepted mất HTTP replay | Review giao thức hai transaction và fault injection trước UC03; không còn câu nghiệp vụ chặn |
| OQ-11 | Demo không purge; lịch sử tài chính được giữ | Archive/purge sau demo, backup/restore thực tế trước triển khai thật |
| OQ-12/15 | Dữ liệu demo chung; scope theo thao tác; caller chỉ xem request của mình; evidence chỉ từ caller được tin cậy | Issuer/audience/JWKS/claim mapping/secret và rotation cụ thể trước runtime |
| OQ-13 | Worker gia hạn thường xuyên + JOB-01 00:05 + JOB-02 00:30; cutoff bất biến; contribution duy nhất; điều chỉnh muộn | Duyệt cadence/lô/lease đề xuất, EXPLAIN, recovery/đối soát trước jobs |

Không đóng toàn bộ OQ chỉ vì phần lựa chọn đã được giải quyết. UUID representation, cột/index/DTO/mã HTTP, mặc định cấu hình và phương án kỹ thuật mới vẫn REVIEW. M0–M4 UC01 lịch sử giữ nguyên; mục D phiếu chưa nghiệm thu toàn bộ bản mới. Không có bằng chứng runtime hoặc tải phát sinh từ việc viết tài liệu.

## 20. Ủy quyền hoàn thiện báo cáo và sửa ảnh — 26/10/2026

Chủ dự án yêu cầu sửa ảnh không hiện, làm lại báo cáo để thấy luồng/sơ đồ luồng/kịch bản chuẩn và ngoại lệ, class diagram; đồng thời nói “cần thì tự viết lại tài liệu và bạn tự quyết định các yếu tố thay tôi”. Trong phạm vi yêu cầu này, agent quyết định bố cục theo nghiệp vụ trước, PNG/đường dẫn tương đối cho Markdown, 11 Activity Diagram và 3 Class view nghiệp vụ bổ sung. Không thay các chính sách tiền, hủy, ngày lịch, replay hay lịch job đã chốt; không tự nghiệm thu M4-DB/M5–M8. Các hình mới là REVIEW và dùng cùng nguồn quyết định hiện hành.


## 21. CR-DOC-004 — Phê duyệt toàn bộ tài liệu, 28/10/2026

**APPROVED. Người duyệt: Chủ dự án.** Xác nhận trong phiên: “giờ coi như bộ tài liệu đã chốt rồi, đóng mọi tài liệu là chốt, xóa bớt các tool và các file không cần thiết đi, chuẩn bị để vào giai đoạn coding”.

[Biên bản](document-approval-20261028.md) xác lập baseline được duyệt và phạm vi từng nhóm tài liệu. Chốt phần bổ sung P1.7/P1.8/P2.3/P2.5/P2.6/P4.6 và toàn bộ delta M4-DB; kế hoạch P6/P7 được duyệt ở mức tài liệu. Các đề xuất kỹ thuật hiện hành tại schema/API/transaction/config nay có hiệu lực để triển khai. Không mở lại lựa chọn đã bị thay hoặc CR-JOB-001 NOT ADOPTED.

Các phần OQ trước đây ghi “chờ review thiết kế/UUID/HTTP/cadence/lô/lease” nay đã duyệt theo artifact hiện hành. Những thông số môi trường chưa được cung cấp (JWT/secret/seed), kiểm chứng migration/index/recovery, kết quả test/tải và việc ngoài phạm vi vẫn theo bảng phần thực thi trong biên bản; không đánh dấu bằng chứng đã có. M5–M8 chưa đạt, không đổi câu NOT_RUN thành PASS.

Cho phép dọn công cụ/file tạm trong out/target; tài liệu chuẩn và lịch sử quyết định được giữ trong Markdown. **Tiếp theo duy nhất: P5.1**, sau đó code/test UC01 trước. Không commit/push/đổi branch/deploy trong lượt này.

### 21.1. Dọn bổ sung theo phản hồi chủ dự án — 28/10/2026

Chủ dự án phản hồi còn ZIP và nhiều tool Python check sau lượt dọn. Agent xóa toàn bộ ZIP lưu tạm, bộ script/JSON kiểm tra tài liệu và manifest JSON phụ; gỡ liên kết và lệnh phụ thuộc chúng. Giữ phiếu/DEC/CR, đặc tả chuẩn, UML/hình và tóm tắt kiểm chứng trong Markdown. Không tạo bộ tool thay thế hoặc khôi phục archive. Phê duyệt CR-DOC-004 và bước P5.1 giữ nguyên; không thay nghiệp vụ/code/pom.


<a id="cr-impl-001"></a>

## 22. CR-IMPL-001 — Học ở công ty, code ở nhà; ưu tiên module tiền

**Trạng thái: APPROVED về môi trường làm việc và thứ tự triển khai. Người xác nhận: Chủ dự án. Ngày: 28/10/2026.** Căn cứ: chủ dự án cho biết điều kiện công ty không cho phép triển khai, muốn tìm hiểu Spring/Docker/các thành phần ở công ty, tự code ở nhà; mentor giao tập trung module tính tiền/cộng-trừ trước và yêu cầu hai file kế hoạch.

| Phạm vi | Quyết định / ảnh hưởng |
|---|---|
| Thứ tự cũ | P5.1 → P5.2 UC01 trước → các UC tiếp theo, theo CR-DOC-004 và roadmap trước cập nhật này |
| Thứ tự mới | P5.1 nền tảng → P5.5 UC04 cộng tiền → P5.6 UC05 trừ tiền, test/recovery/đối soát module tiền → P5.2 UC01 → P5.3 UC02 → UC03/worker/JOB-01 → UC06/JOB-02 → P6/P7 |
| Công ty | Học/đọc/phân tích và chuẩn bị bài thực hành; không yêu cầu cài hoặc chạy Docker/DB/service, không coi mock/unit là kết quả tích hợp |
| Nhà | Tự code, dựng service, migration/fixtures, unit/integration/API/concurrency tests và đo tải |
| Phụ thuộc module tiền | Fixtures phát triển/test tạo graph Customer/Subscriber/inventory/account; debit có package/root/cycle/fee hợp lệ theo schema. Không yêu cầu xong API UC01/UC02 trước, không bỏ điều kiện kỳ gói hoặc tạo public API bypass |
| Giữ nguyên baseline | Không đổi FR/BR/AC, schema/API/transaction/security và các CR nghiệp vụ đã chốt; phí gói khác debit tiêu dùng; M5 vẫn nghiệm thu UC01, M6 toàn nghiệp vụ, M7 tải, M8 bàn giao |
| Kế hoạch học/thực hành | [Tại công ty](../KE-HOACH-HOC-TAI-CONG-TY.md) và [tại nhà](../KE-HOACH-CODE-TAI-NHA.md). C01–C15/N01–N16, thời lượng và cách chia buổi là đề xuất của agent, có thể điều chỉnh theo tiến độ thực; không phải cam kết thời gian hay bằng chứng hoàn thành |
| Tài liệu đồng bộ | AGENTS, roadmap, implementation plan và chỉ mục/README; bộ SRS/UML/API/schema giữ nội dung nghiệp vụ hiện hành, không vẽ lại chỉ vì đổi thứ tự |

Lượt này chỉ lập hai kế hoạch và đồng bộ điều hành; không viết code, tạo migrations/Compose/test tools, chạy backend hoặc tự tick P5–P7. Không commit/push trong lượt lập kế hoạch này. Các ghi nhận “UC01 trước” trong nhật ký cũ là lịch sử trước CR-IMPL-001.

### 22.1. Làm rõ cách dùng hai file — hướng dẫn từ số 0, 28/10/2026

Chủ dự án làm rõ rằng chưa biết Docker/Docker Desktop/Spring; ở công ty cần tra cú pháp và đọc lại code/công cụ đã dùng tối trước, ở nhà cần chỉ rõ tạo file gì, viết gì, chạy lệnh nào, dựng Docker/Java/thành phần nào và kiểm kết quả thế nào. Đây là điều chỉnh cách hướng dẫn, không thay quyết định ưu tiên tiền hoặc nghiệp vụ.

Hai file được viết lại bản2: file công ty là sổ tra theo file/lệnh/lỗi, giải thích khái niệm/cú pháp và vòng tối làm → sáng đọc; file nhà chia nền tảng N01A cài tools → N01B Compose/MySQL → N01C Spring/Ping → N01D JAR/image/backend container, rồi bản đồ file/method/test/lệnh cho N02–N16. Snippet trong Markdown là mẫu để người dùng tự thực hành, chưa có app/Compose/migrations được tạo hoặc chạy trong lượt biên soạn. Không tick tiến độ, không khôi phục tool Python/ZIP, không commit/push.
