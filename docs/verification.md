# Nhật ký kiểm chứng và cleanup — 28/10/2026

Bộ tài liệu được Chủ dự án duyệt theo [CR-DOC-004](00-project/document-approval-20261028.md), gồm M4-DB MySQL. Tiếp theo **P5.1** theo [kế hoạch coding](04-design/implementation-plan.md). Chưa chạy backend/MySQL/migration/API/race/tải trong các lượt chốt và dọn tài liệu.

## Kết quả đã ghi nhận trước khi xóa công cụ phụ

| Ngày/phạm vi | Kết quả đã thực chạy |
|---|---|
| 24/10 — cấu trúc tài liệu | 259 liên kết, 34 câu khảo sát, 64 ca biên, 27 AC UC01; không chạy runtime |
| 25/10 — chỉnh Q07/Q09–34 | Kiểm cấu trúc, bảo toàn lựa chọn/đề bài, đồng bộ DEC-JOB-001; không chạy runtime |
| 26/10 — đồng bộ toàn backend | OpenAPI standards validation đạt; 13 operations/190 local refs; giữ 77 mã FR/BR/NFR và 27 dòng AC UC01 |
| 26/10 — UML/báo cáo | PlantUML render 41 nguồn thành 42 SVG và 42 PNG; báo cáo có 11 Activity, 3 business Class, 20 neo mục lục, 64 ca biên |
| 26/10 — browser preview | Edge headless tải 42/42 ảnh của revision ngày 26/10; không xác nhận native IDE preview |
| 28/10 — chốt tài liệu | 1.016 liên kết không thiếu đích; OpenAPI và hash 41 nguồn/42 SVG/42 PNG đạt; đề bài/phiếu/27 AC/64 oracle không đổi |
| 28/10 — browser thử lại | Không xuất DOM, Crashpad báo Access denied; không nhận bằng chứng cũ là kết quả cho revision mới |
| 28/10 — cleanup đầu tiên | Xóa 1.793 file/96.808.461 byte trong out/target; Git diff cấu trúc đạt, code/pom và nguồn/hình không đổi |

Các con số trên là tóm tắt kết quả đã ghi nhận trong phiên tương ứng. ZIP, script và JSON bằng chứng phụ đã được xóa theo phản hồi của chủ dự án; repository không còn chứa bộ công cụ đó để chạy lại. Đây không phải kết quả kiểm thử backend hoặc đạt M5–M8.

## Dọn bổ sung theo yêu cầu chủ dự án

Xóa ZIP lưu tạm, thư mục bộ kiểm tra tài liệu (script Python, hướng dẫn chạy và JSON kết quả), cùng manifest JSON phụ của lượt chốt. Gỡ liên kết/lệnh phụ thuộc các file đã xóa khỏi tài liệu hiện hành. Giữ đặc tả, lịch sử DEC/CR/OQ và phiếu trong Markdown, nguồn UML và PNG/SVG đang dùng.

Lượt dọn bổ sung kiểm trực tiếp sự tồn tại của file/thư mục, liên kết Markdown/ảnh và Git diff; không tạo script hay JSON kiểm tra mới trong repository. Đề bài, phiếu đã điền, code/pom, nguồn UML/ảnh không sửa. Không build, commit, push, đổi branch hoặc deploy.

**Phần còn phải chứng minh:** Maven verify, boot/healthcheck, migrations MySQL, ràng buộc/index, xác thực API, concurrency/recovery, đo TPS/latency/đối soát và chạy từ máy sạch. Các phần này thuộc P5–P7, không yêu cầu duyệt lại bộ tài liệu.

## Lập kế hoạch học/thực hành — 28/10/2026

Theo CR-IMPL-001, bổ sung đúng hai file kế hoạch: [học tại công ty](KE-HOACH-HOC-TAI-CONG-TY.md) và [code tại nhà](KE-HOACH-CODE-TAI-NHA.md). Đồng bộ thứ tự UC04/UC05 trước UC01 trong AGENTS/roadmap/implementation plan/README; giữ mã task, ý nghĩa M5–M8 và nội dung SRS/UC/API/schema. C01–C15 ghép N01–N16, có ma trận M01–M22, điều kiện fixture debit và các chỉ tiêu của requirement. Thời lượng học là đề xuất, mọi ô tiến độ mới còn trống.

Nguồn học chính thức Spring/Docker/MySQL/Maven/Flyway/JUnit/Testcontainers/RabbitMQ/k6 được mở để đối chiếu khi biên soạn; không đổi dependency theo version mới trên website. Lượt này chỉ kiểm tài liệu bằng lệnh dùng một lần trong terminal, không tạo bộ script/ZIP/JSON phụ. Không code, build, chạy DB/test/tải, commit hoặc push trong lượt lập kế hoạch.

**Bản2 theo phản hồi:** viết lại file công ty thành hướng dẫn tra syntax/đọc code tối trước; file nhà có mẫu Compose/XML/Java/YAML/Dockerfile và lệnh cho N01A–D, bản đồ file/method/test cho phần tiền và các UC tiếp. Chỉ kiểm cấu trúc snippet/liên kết và bảo toàn baseline; chưa build/run các snippet hoặc xác nhận image pull/migration/API thành công.
