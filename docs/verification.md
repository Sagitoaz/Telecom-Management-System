# Kiểm chứng bộ thiết kế backend — 26/10/2026

## Báo cáo v1.1 — sửa ảnh và bổ sung luồng/Class Diagram, 26/10/2026

**Lỗi đã xác định:** ảnh nhúng của báo cáo dùng đường dẫn ổ đĩa `E:/...` thay vì URL tương đối từ Markdown. Checker cũ kiểm file tồn tại/XML nên không phát hiện lỗi hiển thị. Nội dung có sequence/state nhưng thiếu Activity Diagram và Class Diagram tập trung ở phần đầu nên khó theo dõi nghiệp vụ.

**Thay đổi:** viết lại bố cục báo cáo theo luồng trước kỹ thuật; chương 1–9 gồm Activity tổng thể, xử lý lỗi/retry chung, hành trình ví dụ, các UC/jobs và Class Diagram. Mỗi UC có standard scenario với dữ liệu/kết quả cụ thể và bảng nhánh A/E/X. Thêm 11 Activity Diagram và 3 focused business Class Diagram; giữ các sequence/state/domain/ERD/design class có sẵn. Toàn bộ 42 ảnh nhúng dùng PNG tương đối; có SVG/PlantUML cạnh hình. Report 1.368 dòng/20 chương; 64 oracle giữ nguyên từ ma trận nguồn.

**Bằng chứng hiển thị:** chuyển đúng Markdown thành HTML bằng Python Markdown 3.11.2, phục vụ local loopback, mở Microsoft Edge headless. JavaScript sau load ghi complete/naturalWidth/naturalHeight của từng ảnh: **42/42 ảnh tải thành công, 0 ảnh lỗi**. Focus riêng chương class: **3/3 ảnh tải thành công**; đã xem screenshot phần class và hình Activity tổng thể. [JSON bằng chứng](checks/report-preview-20261026.json) gắn hash báo cáo và render manifest. Công cụ browser CUA không khởi động được do lỗi volume D; dùng Edge CLI headless thay thế. Không trực tiếp điều khiển hoặc xác nhận native IDE preview của người dùng.

**Kiểm chứng nguồn/hình:** PlantUML 1.2026.9 render 41 nguồn thành 42 SVG + 42 PNG, exit 0. Đã sửa cú pháp màu activity deprecated để hình không chứa banner cảnh báo; PNG overview lớn dùng giới hạn render đủ để không cắt tại 4.096 px. Checker hiện hành kiểm cả image target tương đối và hash PNG/SVG, không chỉ XML. `verify_report.py` kiểm 42 PNG hiện hành, 11 Activity, 3 business Class, 20 neo mục lục, kịch bản S-UC01..06/ngoại lệ và nguyên 64 ca biên; đối chiếu bằng chứng browser đạt.

Lệnh đã chạy:

```powershell
python docs/checks/render_uml.py --jar out/docs-sync-20261026/tools/plantuml-1.2026.9.jar --png
python docs/checks/verify_backend_docs.py --deps out/docs-sync-20261026/pydeps
python docs/checks/verify_report.py --browser-evidence docs/checks/report-preview-20261026.json
```

Không đổi quy tắc đã chốt trong SRS/UC/API/schema, đề bài hoặc phiếu. Chỉ bổ sung view và cách đọc/trình bày theo ủy quyền của chủ dự án; thiết kế mới REVIEW, không tự nghiệm thu M4-DB/M5–M8. Không có backend/MySQL/tải được chạy từ việc sửa báo cáo.


## Bổ sung hướng dẫn đọc và báo cáo tổng hợp — 26/10/2026

Theo yêu cầu chủ dự án, đã tạo [hướng dẫn đọc/rà soát](HUONG-DAN-DOC-VA-RA-SOAT.md) (172 dòng) và [báo cáo tổng hợp](BAO-CAO-TONG-HOP-DU-AN.md) (1.087 dòng). Báo cáo có 18 mục lục neo rõ ràng, bao quát Phase 0–7, nhúng đủ 28 SVG hiện hành từ 27 nguồn, kèm diễn giải luồng chuẩn/nhánh/lỗi, 13 API và nguyên 64 oracle TC-EDGE theo ma trận chuẩn. Các hình có link nguồn và link SVG tương đối cạnh ảnh nhúng local.

Kiểm tra riêng hai tài liệu: không còn placeholder; toàn bộ ảnh tồn tại và XML parse được; tập ảnh đúng bằng tập SVG hiện hành; 18 mục lục trỏ đúng neo; 64 mã/thứ tự/nội dung oracle khớp ma trận; 13 operation khớp bảng API. Checker toàn bộ `verify_backend_docs.py` với validator OpenAPI chạy exit 0 sau khi khôi phục archive bị thiếu. Hash nguồn UML/ảnh, đề bài, phiếu và các đặc tả nghiệp vụ/API/schema không đổi trong lượt tổng hợp này. `git -c core.whitespace=-blank-at-eol diff --check` và `git diff --exit-code -- pom.xml src` đều exit 0.

**Khôi phục hồ sơ thiếu:** đầu lượt phát hiện `docs/00-project/history/2026-10-26-before-sync.zip` không còn dù nhiều tài liệu/checker đang tham chiếu. Đã khôi phục 65 tệp nguồn từ snapshot đầu lượt đồng bộ còn tại `out/docs-sync-20261026/before-texts.json`, chọn byte UTF-8/BOM/line-ending bằng đối chiếu SHA-256 của từng tệp với manifest gốc; tất cả 65 tệp khôi phục khớp hash gốc. Archive giữ thêm manifest 73 mục, không chứa byte 8 ảnh SVG cũ. Checker so nội dung AC đã được sửa để chuẩn hóa CRLF giống `Path.read_text`, tránh báo sai do newline khác giữa ZIP và file local; không đổi nội dung AC hoặc hash baseline để cho qua.

Không có backend/MySQL/race/tải được chạy trong lượt này. Tạo hai báo cáo không đổi DEC/CR hoặc nghiệm thu checkpoint; delta MySQL UC01 vẫn REVIEW trước P5.1.


**Phạm vi:** kiểm tra tài liệu và nguồn hình sau tiếp nhận phiếu 16; không chạy backend, migration, MySQL, API, race hoặc tải. Nội dung các ngày trước bên dưới được giữ như nhật ký lịch sử, không dùng để phủ nhận hoặc thay bằng chứng hiện hành.

## Kết quả đã thực hiện

| Kiểm tra | Kết quả thực tế |
|---|---|
| Bảo toàn đề bài hiện có đầu lượt | SHA-256 không đổi: `613fd0210a576c72cf013dbf76218e503205b09818bf99f71ab9f020d404d13a` |
| Bảo toàn phiếu 16 đã điền | SHA-256 không đổi: `2e3474b90e37c471510eb2977515d70691bb01dad05409e7940f7e26a07450d5`; không sửa dấu chọn/ghi chú |
| Baseline SRS và UC01 | Giữ 77 mã FR/BR/NFR; giữ nguyên văn 27 dòng AC-SUB theo archive đầu lượt |
| Khảo sát và ca biên | 34 mục, 4 mục tham chiếu, 30 lựa chọn đã điền; đủ 64 TC-EDGE theo đúng thứ tự, tất cả runtime NOT_RUN |
| OpenAPI | `openapi-spec-validator 0.9.0` xác nhận cấu trúc chuẩn; 13 operations, 190 local references được resolve |
| PlantUML | PlantUML 1.2026.9 render **27 nguồn thành 28 SVG**, exit 0 toàn bộ; manifest lưu hash từng nguồn/ảnh |
| Liên kết và UTF-8 | Checker kiểm toàn bộ Markdown trong repo hiện hành, không thiếu target file hoặc ký tự thay thế lỗi; số đếm chính xác trong JSON kết quả |
| Diff phạm vi mã nguồn | `git diff --exit-code -- pom.xml src` exit 0; lượt này không sửa pom/Java |
| Whitespace cấu trúc | `git -c core.whitespace=-blank-at-eol diff --check` exit 0; bỏ qua hai khoảng trắng cuối dòng dùng xuống dòng Markdown. Git có cảnh báo chuyển LF/CRLF ở một số file |

Đã kiểm bằng mắt PNG xuất cục bộ của Domain, ERD, UC01, UC03, JOB-02, C4 Container và Design Class. Hình Design Class tổng quan toàn backend quá rộng để đọc trong preview nhỏ nên bổ sung bốn hình tập trung tiền/gói/báo cáo/jobs; giữ hình đầy đủ để phóng to. Không tuyên bố đã kiểm bằng mắt từng hình trong 28 SVG.

## Kiểm tra chéo và sửa sau review

Đã sửa thời điểm kiểm DB clock trong sequence UC02/UC05 về sau mọi phối hợp khóa cần thiết; thống nhất mã lỗi UC03/UC05 với enum API; thống nhất lifecycle job run với schema/API. Bổ sung cutoff bền vững trước công việc tổng hợp và nhánh dùng lại snapshot READY trong sequence báo cáo. Đã kiểm các quy tắc CR-CAN-003, terminal failed renewal target, snapshot nguồn cố định và contribution unique giữa BASE/ADJUSTMENT.

[Manifest render](checks/render-manifest-20261026.json) nối hash nguồn với ảnh. [Kết quả checker](checks/verification-result-20261026.json) chứa thống kê và errors; [hướng dẫn tái lập](checks/README.md) nêu dependency/lệnh. [Chỉ mục hình](UML-INDEX.md) liên kết nguồn và SVG. [Archive trước đồng bộ](00-project/history/2026-10-26-before-sync.zip) giữ baseline đầu lượt; hash đề bài/phiếu so với trạng thái đầu lượt, không giả định chúng giống Git HEAD vốn đã có thay đổi trước đó.

Lệnh đã chạy thành công:

```powershell
python docs/checks/render_uml.py --jar out/docs-sync-20261026/tools/plantuml-1.2026.9.jar
python docs/checks/verify_backend_docs.py --deps out/docs-sync-20261026/pydeps
git -c core.whitespace=-blank-at-eol diff --check
git diff --exit-code -- pom.xml src
```

## Giới hạn và điều kiện tiếp theo

Kiểm tra tĩnh không chứng minh transaction/khóa/index chạy đúng hoặc đạt TPS. Chưa có bằng chứng build/test backend, DDL migration, CHECK/FK/index EXPLAIN, race, crash/unknown commit, load, backup/restore. Mọi ca runtime trong kế hoạch vẫn NOT_RUN. M4-DB/thiết kế mới vẫn REVIEW; M5–M8 chưa đạt. Không commit, push, merge, deploy hoặc sửa đề bài trong lượt này.

Bước tiếp theo duy nhất: nghiệm thu delta MySQL UC01 theo [checklist M4](04-design/m4-review-checklist.md), sau đó P5.1. Không bắt UC sau chưa phụ thuộc chặn triển khai UC01.

---

## Nhật ký kiểm chứng trước ngày 26/10/2026

# Kiểm chứng viết lại Q09–Q34 – 25/10/2026

**Phạm vi:** phiếu16 v0.5 sửa cách diễn đạt26 câu Q09–Q34; thêm ví dụ/giải nghĩa, không chọn thay người dùng, không thay baseline. Log mục18 và Roadmap ghi nhận công việc biên tập.

So với snapshot trước sửa: Q01–Q08 không đổi, toàn bộ dấu chọn và phần ghi chú Q09–Q34 giữ nguyên, bảng xác nhận cuối file giữ nguyên, hash đề bài không đổi. Rà soát độc lập phát hiện hai chi tiết cần giữ chính xác trong bản dễ đọc: mốc chốt đã lưu bền vững ở Q12 vẫn giữ khi lượt trước lỗi; Q24/Q34 lấy giờ từ DB. Đã bổ sung đúng các điểm đó. Q09 không tự chuyển GAME→MAIN; phí đăng ký vẫn MAIN; Q07 vẫn DEC-JOB-001.

Kiểm tra tĩnh bằng script tài liệu, so snapshot và Git diff cấu trúc; không build/test runtime hay đo tải. Kết quả thực chạy cuối ghi cùng output; các báo cáo bên dưới là lịch sử theo thời điểm của chúng.

**Kết quả thực chạy:** exit0; 260 link nội bộ không thiếu đích, 36 ref OpenAPI/3 operations hợp lệ ở mức kiểm tra tham chiếu; đủ34 câu/64 kịch bản/27 AC. So snapshot giữ nguyên Q01–Q08, toàn bộ dấu chọn/ghi chú Q09–Q34 và bảng xác nhận. Kiểm cấu trúc Git bỏ qua blank-at-eol và kiểm pom/src không đổi đều exit0; requirement hash giữ nguyên. Review độc lập xác nhận đã xử lý các điểm giữ mốc chốt Q12 và đồng hồ DB Q24/Q34. Chưa có kết quả runtime mới.

---

# Ghi nhận chốt Q07 phương án ban đầu – 25/10/2026

Đồng bộ phiếu16 v0.4/Q07=A, DEC-JOB-001, trạng thái CR-JOB-001 không được chọn, SRS/scheduled-jobs/review09/quy tắc20/Roadmap. Giữ worker gia hạn thường xuyên và JOB-01 hằng ngày00:05 dùng chung lõi xử lý, JOB-02 giữ00:30. Khoảng chạy worker và Q25 chưa được tự chốt. Các kiểm chứng lịch sử bên dưới giữ nguyên theo thời điểm ghi.

Kiểm tra lượt này: script kiểm tài liệu và so sánh các câu ngoài Q07/Q25; dấu chọn Q25 không đổi; đề bài/code không sửa; không build/runtime/đo tải. Kết quả cụ thể lấy từ output chạy cuối, không dùng xác nhận nghiệp vụ làm bằng chứng test.

**Kết quả thực chạy:** script kiểm tài liệu exit0, 260 link nội bộ không thiếu đích, 36 ref OpenAPI/3 operations, đủ34 câu/64 kịch bản/27 AC. Q07=A được đánh dấu theo xác nhận; 32 câu ngoài Q07/Q25 và các dấu chọn Q25 không đổi. Hash requirement không đổi. Kiểm cấu trúc Git bỏ qua blank-at-eol và kiểm pom/src không đổi đều đạt. Các phần PROPOSED/chưa chọn ở lịch sử phía dưới phản ánh thời điểm trước DEC-JOB-001.

---

# Kiểm chứng chỉnh Q07 ngày 25/10/2026

**Phạm vi:** phiếu16 Q07 v0.3 và dẫn chiếu Q25; đồng bộ cách đọc ở review09/quy tắc20, lưu lịch sử ở clarification log mục16 và Roadmap. Đây là sửa đề xuất tài liệu theo yêu cầu người dùng, chưa duyệt CR-JOB-001 hoặc triển khai JOB-01.

- Snapshot trước sửa lưu nguyên A cũ Q07 đã được đánh dấu vào clarification log mục16.1. A mới chưa được chọn; các câu Q01/Q02/Q04/Q05/Q06 giữ nguyên dấu chọn, nội dung các câu ngoài Q07/Q25 không đổi so snapshot.
- Q07 mới đề xuất một JOB-01 định kỳ, không worker gia hạn + lượt quét bù; ghi rõ dùng bản ghi đến hạn, lô/budget/index, restart và guards khi nhiều instance, không cam kết độ trễ60s hoặc hiệu năng đã đo. Q07A+Q25A không đáp ứng tự gia hạn sau hạn, đề xuất đọc/chọn riêng Q25B/C có chính sách muộn.
- Công cụ kiểm tra tài liệu trước đây coi mọi ô được điền là lỗi; đã sửa để chấp nhận tối đa một đáp án mỗi câu và thống kê câu đang chờ. Không tự chuyển những dấu chọn thành DEC/CR Approved.
- Chạy `python docs/checks/verify_review_20261024.py`: kiểm link/YAML/ref/AC/câu hỏi/kịch bản/hash; kiểm bổ sung so sánh snapshot cho các câu không liên quan và lịch sử A cũ. Kết quả thực tế ghi từ output chạy cuối, không thay kiểm thử nghiệp vụ.
- **Kết quả chạy 25/10:** exit0, 261 liên kết nội bộ/55 file Markdown, 36 ref OpenAPI và 3 operations; đủ34 câu/64 kịch bản/27 AC. Năm câu Q01/Q02/Q04/Q05/Q06 vẫn đánh dấu A, 25 câu còn chờ (gồm Q07 mới). So snapshot: 32 câu ngoài Q07/Q25 giữ nguyên; A cũ Q07 còn nguyên trong lịch sử; Q25 vẫn chưa chọn; hash đề bài không đổi. Kiểm cấu trúc Git bỏ qua blank-at-eol và kiểm không đổi pom/src đều exit0.
- `git -c core.whitespace=-blank-at-eol diff --check` kiểm cấu trúc, bỏ qua hard line break Markdown. Không đổi `requirement.md`, SRS baseline, code/pom, sơ đồ; không build/test runtime, không commit/push/đổi branch.

---

# Kiểm chứng rà soát tài liệu 24/10/2026

**Phạm vi:** kiểm tra tĩnh tài liệu trên workspace thực tại `E:\WINDOW\Project\Telecom-Management-System`; không dùng ZIP làm nguồn thay thế. M1–M4 UC01 lịch sử đã duyệt; delta MySQL/đáp án mới còn chờ. Không chạy Maven/backend/DB/container/kiểm thử nghiệp vụ/đo tải trong lượt này.

## Kết quả kiểm tra của lượt này

- [Script kiểm tra có thể chạy lại](checks/verify_review_20261024.py): `python docs/checks/verify_review_20261024.py`, Python 3.12/PyYAML đã có trong môi trường. Kết quả được xác nhận bằng output thực; script không gọi mạng/ghi file.
- Đủ Q01–Q34 theo thứ tự, 30 câu lựa chọn A/B/C trống và 4 câu chỉ tham chiếu quyết định cũ; không tự chọn đáp án. Đủ TC-EDGE-01–64 duy nhất và AC-SUB-01–27 duy nhất.
- OpenAPI YAML parse, kiểm toàn bộ 36 `$ref` nội bộ và 3 operationId; chưa có validator OpenAPI chuẩn cài sẵn, không gọi đây là validation tiêu chuẩn hoặc contract test.
- Kiểm 259 liên kết nội bộ trong 55 file Markdown: không thiếu đích file. 7 SVG cũ parse XML; **không sửa .puml/.svg trong lượt này**, không claim đã render lại hoặc ảnh cũ chắc chắn khớp source. C4/ERD cũ giữ nhãn PG lịch sử, cần delta hình MySQL khi đủ quyết định.
- SHA-256 `docs/requirement.md` đầu/cuối lượt: `613fd0210a576c72cf013dbf76218e503205b09818bf99f71ab9f020d404d13a`. Hash khác log năm trước do working tree trước lượt đã khác; không sửa file đề bài trong lượt này, không hoàn tác thay đổi sẵn có của người dùng.
- Kiểm tra độc lập các tài liệu bổ sung phát hiện hai vấn đề: điều kiện duyệt delta đòi test P5 trước implementation; và trễ gia hạn Q07/Q25 chưa rõ phạm vi. Đã tách duyệt thiết kế/test thực thi, định nghĩa Q25 cho mọi độ trễ và tái rà soát không còn lỗi quan trọng trong hai mục đó. Đây là review tài liệu, không phải người dùng đã duyệt nghiệp vụ.
- `git diff --check` trên toàn working tree còn báo trailing whitespace trong phiếu lịch sử đã có sẵn và các hard line break Markdown. Không sửa đáp án lịch sử chỉ để xóa whitespace. Không tuyên bố lệnh mặc định sạch; kiểm cấu trúc riêng bỏ qua blank-at-eol được ghi nhận cùng kết quả chạy cuối.

**Lệnh xác nhận cuối:** script kiểm tra exit 0; `git -c core.whitespace=-blank-at-eol diff --check` không báo lỗi cấu trúc; `git diff --exit-code -- pom.xml src` không có thay đổi; HEAD vẫn `362d1c3`. Các cảnh báo LF/CRLF là cấu hình Git hiện có, không phải lỗi nghiệp vụ.

## Giới hạn và bước tiếp theo

Ma trận 64 ca là kế hoạch NOT_RUN, ca theo đáp án chưa chọn chưa có oracle cuối. Không có test/data/TPS/backend runtime mới. P1.8/P2.6 chờ chủ dự án điền [phiếu 16](07-business-review/16-PHIEU-DUYET-TONG-HOP.md); sau đó mới ghi DEC/CR và đồng bộ baseline/phần delta liên quan. Không tick thêm checkpoint. Không sửa pom/src, không commit/push/đổi branch.

---

# Lịch sử kiểm chứng trước 24/10/2026

Nội dung dưới ghi kết quả và nhận xét ở những lần trước; các trạng thái M1/M4 chờ duyệt hoặc nguồn ZIP/starter không phải trạng thái hiện hành. Kết quả năm trước không thay bằng chứng trên workspace lượt này.

## Kiểm tra cập nhật tài liệu 19/10/2026

**Tình trạng:** M1–M3 đã được chủ dự án duyệt qua Q25=A và lời xác nhận. Đây là **kiểm tra tĩnh** trên bản `docs(1).zip` (18/10) và các file mới; **không** chạy Spring Boot/PostgreSQL/RabbitMQ/Redis/Keycloak và **không** có số liệu benchmark thực.

- Phiếu Q25 đã cập nhật A; Q01–Q24 giữ nguyên.
- SRS v0.6 / UC01 v0.5 là baseline M1, không đổi FR/BR/AC; UML/C4 nguồn và SVG cũ giữ nguyên vì được duyệt theo bộ trước.
- M4 mới có ERD, schema, OpenAPI, Design Class, thiết kế concurrency nhưng **chờ người dùng duyệt** và cần kiểm chứng render UML/OpenAPI bằng công cụ dự án ở P4/P5.
- Kiểm tra có thể được chạy trong gói hiện tại: parse YAML và kiểm tra tham chiếu `#/components` bằng script/kiểm tra tĩnh; ghi kết quả cụ thể trong manifest. Không coi chúng thay thế validation OpenAPI chuẩn hay test tích hợp.

---

# Kiểm chứng bộ tài liệu sau nhận Q01–Q24

**Ngày:** 18/10/2026. **Bản:** log1.5, SRS0.6, UC01 0.5. Tài liệu chờ chủ dự án đọc rồi trả lời Q25.

## Phạm vi kiểm tra

Kiểm tra file/Markdown, quyết định được lưu, tính đầy đủ mã FR/BR/NFR/AC, giữ nguyên lựa chọn người dùng, Q25/M1–M8, nguồn UML và SVG render.
Không sửa src/pom.xml; không chạy build/test nghiệp vụ hoặc đo TPS trong lượt này. Kiến trúc/công cụ được duyệt không phải bằng chứng M3/M4 đạt.

## Nguồn quyết định và xóa phiếu cũ

Đã nhận24 câu, mỗi câu một ô; log mục13 lưu nguyên văn đáp án/ghi chú. Q25 vẫn chưa chọn.
Hai phiếu lịch sử REV-SRS-001/REV-UC01-002 đã xóa theo yêu cầu; log mục8–12 còn lịch sử và31+5 quyết định đầy đủ. Không có link hoạt động cần mở phiếu đã xóa.
Broker/cache đã được làm rõ bằng xác nhận trong phiên; ADR-001 ghi phạm vi chủ dự án giao chọn RabbitMQ/Redis.

## Kiểm tra UML

Dùng PlantUML1.2026.9: -checkonly và -tsvg cho docs/02-analysis, sequence-diagrams, state-diagrams và docs/03-architecture/c4-diagrams. Nguồn có6 file .puml, C4 có2 diagram nên có7 SVG. SVG phải parse XML và không chứa Syntax Error. Mỗi lần sửa nguồn phải render lại.

## Bảo toàn dữ liệu

requirement.md SHA-256 đầu lượt: f6667c88e9c403146d2dd2b31df75143af6a1cbb5876849fe896e28151cae532. Không sửa byte nào. Sai khác đã có với Git HEAD là chữ “dịch vụ t=>” trong HEAD và “dịch vụ =>” trong file người dùng; không tự sửa lại.
Không stage/commit/push hoặc đổi branch. Working tree có thay đổi người dùng từ trước, không hoàn tác.

## Giới hạn và phần chờ duyệt

SRS0.6/UC01 0.5 có27 AC, Q25/M1 chưa duyệt; P2–4 còn DRAFT, chưa có schema/OpenAPI/Design Class và thuật toán giao dịch hoàn chỉnh. Retention tiền 30 ngày và hạn mức10triệu/100triệu chỉ là đề xuất R2/R3, chưa áp dụng như chuẩn đã duyệt.
Xem [review-guide](00-project/review-guide.md) để đọc theo thứ tự, [roadmap](ROADMAP.md) để xác định bước tiếp theo.


## Kết quả thực chạy trong lượt này

- PlantUML1.2026.9: checkonly và render SVG exit0; 6 nguồn/7 SVG, XML hợp lệ.
- Đối chiếu100 liên kết Markdown:0 link hỏng.
- Giữ nguyên24 đáp án và ghi chú người dùng sau chuẩn hóa line ending; Q25 trống; đủ31+5 quyết định lịch sử và24 quyết định mới.
- SRS giữ đầy đủ mã FR/BR/NFR; UC01 đủ AC-SUB-01–27, không trùng mã.
- Requirement hash giữ nguyên; thư mục phiếu cũ đã xóa; M1–M8 vẫn unchecked.
- git diff --check không báo lỗi; không có thay đổi src/pom.xml trong diff. Chưa có build/test nghiệp vụ hoặc đo tải mới.


## Kiểm tra static ngày 19/10/2026 – chuyển M4/P5.1

- Chủ dự án xác nhận duyệt T1–T7/M4 trong chat; cập nhật checklist và Roadmap; đây là phê duyệt thiết kế, không phải kết quả kiểm thử code.
- Kiểm tra công cụ đóng gói: YAML `openapi.yaml`, `starter/compose.yaml`, 3 Spring `application*.yml` parse; XML `pom.xml` parse; các `$ref` schema OpenAPI được so khớp tên; ZIP có thể giải nén.
- ERD UC01 bổ sung `service_account.created_at` cho khớp `database-schema.md`.
- Chưa có Maven, PostgreSQL, Docker Engine hay PlantUML executable trong môi trường đóng gói: **chưa chứng minh build, migration, Docker Compose, render sơ đồ, integration test**. Thực hiện khi chạy trên máy người dùng.
