> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Bảo mật API và tích hợp

**Mã tài liệu:** DOC-ARCH-SEC-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

Tài liệu cụ thể hóa ranh giới tin cậy của Q30/Q31, DEC-PRE-04 và các quyết định T1/T2/T7. Tên scope dưới đây đồng bộ với [OpenAPI](../04-design/openapi.yaml). Tên realm, audience, ánh xạ claim và cấu hình tích hợp là đề xuất cần review/kiểm thử, không phải cấu hình đã triển khai.

## 1. Xác thực và danh tính ứng dụng gọi

IdP/Keycloak cấp JWT bên ngoài Telecom. Backend là resource server: kiểm chữ ký, thuật toán được cho phép, issuer, audience, `exp`, `nbf` và quyền thao tác. Chỉ chấp nhận khóa từ JWKS đã cấu hình cho issuer tin cậy; không dùng URL khóa do token tùy ý chỉ định.

`callerId` phải được suy ra từ danh tính ứng dụng đã xác minh. Đề xuất dùng claim `client_id`, hoặc ánh xạ `azp` theo cấu hình rõ ràng của IdP; không tự đổi qua lại giữa các claim khi thiếu dữ liệu. Giá trị caller trong body/header không tạo quyền và không thay thế danh tính đã xác minh. Caller ổn định qua các lần cấp token; không dùng toàn bộ JWT hoặc correlationId làm khóa sở hữu request.

Đề xuất realm demo là `telecom-demo`, audience là `telecom-api`. Issuer phải khớp khi CSKH, backend và Keycloak chạy từ host hoặc container. Không hardcode issuer/secret trong mã Java. Tập dữ liệu demo dùng chung không yêu cầu hệ thống đa tenant, nhưng vẫn phải kiểm quyền từng thao tác và quyền sở hữu request/bản báo cáo.

## 2. Ma trận quyền API đề xuất

| Năng lực | Scope trong hợp đồng | Điều kiện bổ sung |
|---|---|---|
| Gửi UC01 | `telecom:activation:write` và `telecom:evidence:submit` | Caller được tin cậy để gửi tham chiếu chứng cứ; hồ sơ hợp lệ |
| Tra trạng thái UC01 | `telecom:activation:read` | Chỉ request thuộc caller hiện tại |
| Tra số khả dụng | `telecom:inventory:read` | Không tạo giữ chỗ hoặc quyền ưu tiên cấp số |
| Đăng ký/mua kỳ gói | `telecom:subscription:write` | Phiên bản đang thấy, quyền dữ liệu và điều kiện nghiệp vụ hợp lệ |
| Yêu cầu hủy gói | `telecom:subscription:cancel` | Không tự chuyển quyền đăng ký thành quyền hủy |
| Đọc đăng ký gói hiện tại | `telecom:subscription:read` | Dữ liệu hỗ trợ lấy version/coverage/kỳ hiện tại |
| Cộng tiền | `telecom:account:credit` | Đúng tài khoản và quyền cộng theo loại tài khoản |
| Trừ tiền | `telecom:account:debit` | Đúng tài khoản, quyền trừ, hạn dùng và kỳ gói hợp lệ |
| Tra kết quả UC02–UC05 | `telecom:operation:read` và scope của thao tác gốc | Cùng caller đã tạo request; hủy dùng scope cancel, không dùng scope write thay thế |
| Đọc danh mục gói | `telecom:package:read` | Chỉ đọc, không cấp quyền chỉnh giá hoặc thay đổi chính sách |
| Tra báo cáo UC06 | `telecom:report:read` | Snapshot và cursor gắn caller/bộ lọc |
| Chạy job bằng tay | `telecom:job:run` | Vai trò vận hành; kiểm jobCode, ngày nghiệp vụ và chống chạy trùng |
| Tra trạng thái job | `telecom:job:read` | API nội bộ, không mặc nhiên cấp cho CSKH |

OpenAPI dùng HTTP Bearer nên danh sách `x-required-scopes` mô tả yêu cầu kiểm quyền phía server; khai báo scheme không tự thực thi các quyền này. Tên scope là thiết kế hợp đồng REVIEW, không phải chứng cứ IdP đã cấu hình.

Token sai/không hợp lệ trả 401; token hợp lệ nhưng thiếu quyền trả 403 theo hợp đồng. Tra request ngoài caller trả 404 để tránh tiết lộ sự tồn tại. Lỗi không trả dư số dư, hồ sơ, SQL hoặc stack trace. Đọc lại kết quả bằng requestId vẫn cần quyền đọc tương ứng, kể cả khi bên gọi có quyền tạo lệnh.

## 3. Chứng cứ xác minh và dữ liệu cá nhân

Có JWT hợp lệ không đồng nghĩa được xác nhận giấy tờ đã xác minh. Caller gửi tham chiếu chứng cứ phải có quyền `telecom:evidence:submit` và thuộc cấu hình nguồn tin cậy. Backend kiểm định dạng, nguồn tham chiếu và cặp loại/số giấy tờ theo quy tắc chuẩn hóa đã duyệt. Không coi một chuỗi evidence bất kỳ là bằng chứng đáng tin.

UC01 đối chiếu giấy tờ đã liên kết; không tự ghép hai Customer bằng tên/ngày sinh, không tự sửa hồ sơ đang khác và không tự thêm giấy tờ mới vào Customer cũ ngoài quy trình đã duyệt. Telecom không xây dịch vụ KYC hoặc gọi một hệ thống xác minh mới trong transaction đang giữ khóa.

Log chỉ ghi thông tin cần cho truy vết: operation, caller, requestId, correlationId, kết quả, thời gian và mã lỗi được kiểm soát. Không ghi JWT, mật khẩu, khóa riêng, toàn bộ số giấy tờ hoặc payload hồ sơ chuẩn hóa. Fingerprint giúp so nội dung, không thay kiểm quyền. Dữ liệu phục hồi request vẫn là dữ liệu nhạy cảm; thời gian lưu đầy đủ và tombstone phải được áp dụng riêng theo chính sách.

## 4. Quản lý khóa và sự cố IdP

Backend lấy khóa công khai qua JWKS; khóa riêng nằm tại IdP. Khi xoay khóa, môi trường thử phải chứng minh token với khóa đang hợp lệ vẫn xác minh được và token không hợp lệ không được chấp nhận. Cấu hình thời gian cache khóa, độ lệch đồng hồ và cách refresh khi gặp `kid` mới cần được ghi rõ trong runtime, không tự phát sinh con số mặc định từ tài liệu này.

Nếu IdP tạm lỗi, chỉ có thể dùng khóa đã cache khi token vẫn kiểm chứng đầy đủ theo chính sách đã cấu hình. Không tìm được khóa hợp lệ thì từ chối yêu cầu được bảo vệ; không chuyển sang bỏ xác thực hoặc chỉ giải mã payload JWT. Khóa bảo vệ cursor cũng là secret độc lập: kế hoạch xoay khóa phải nêu tác động tới cursor cũ và thời gian dùng snapshot, không ghi khóa trong Git/log.

## 5. RabbitMQ — tín hiệu bổ sung kho số

RabbitMQ chỉ báo rằng kho có thể đã có số mới. Đề xuất thông điệp nhỏ gồm `eventId`, `poolKey`, `recordedAt`, không chứa hồ sơ khách hàng. Chỉ publish sau khi transaction bổ sung kho commit. Publisher confirm và consumer acknowledgment không tạo transaction nguyên tử với MySQL.

Consumer gọi worker đọc DB, thực hiện cơ chế nhận việc/cấp số đã thiết kế rồi mới xác nhận thông điệp hoặc xác định không còn việc. Thông điệp trùng, đến lại hoặc khác thứ tự không được phá FIFO nghiệp vụ. Retry có giới hạn và cơ chế xử lý thông điệp lỗi cần được cấu hình ở P5; tên exchange/queue, durability, prefetch và retry budget chưa được coi là đã nghiệm thu.

Thông điệp bị mất được bù bằng quét DB mỗi 60 giây của UC01. Không hứa giao đúng một lần hoặc hoàn tất trong 60 giây khi còn backlog/khóa/lỗi. Nếu cần bảo đảm phát sự kiện mạnh hơn trong tương lai, phải trình thiết kế và phạm vi riêng; bản hiện tại không ngầm bắt buộc outbox.

## 6. Redis — cache danh mục

Redis dùng cache-aside cho đọc danh mục, TTL 60 giây cấu hình được và invalidate sau cập nhật danh mục đã commit. Cache miss hoặc Redis không sẵn sàng thì đọc MySQL. Đường thu phí phải kiểm giá, số ngày, phiên bản và chính sách gia hạn khách cũ tại DB, sau đó lưu snapshot vào kỳ gói.

Không dùng Redis làm nguồn quyết định số dư, cấp số hoặc quyền tái xử lý request. Không dùng khóa phân tán Redis để bảo vệ tiền. Cache cũ không được ghi đè giá/kỳ lịch sử hoặc làm gói đã ngừng bán mất quyền gia hạn hợp lệ theo Q29=B.

## 7. Mạng, vận hành và kiểm chứng

Môi trường local giới hạn cổng cần dùng trên máy phát triển; thông tin kết nối/secret được cung cấp ngoài Git. Khi bàn giao cần cấu hình TLS và ranh giới mạng phù hợp môi trường triển khai thực. Health/readiness chỉ phản ánh khả năng phục vụ đã định nghĩa; service khởi động không đồng nghĩa Use Case đã đúng.

API chạy job phải lưu người gọi, jobCode, runId, ngày nghiệp vụ và kết quả kiểm quyền/chống trùng. Quyền chạy job không bỏ qua kiểm điều kiện tiền/gói hoặc cơ chế claim trong DB.

Các nhóm ca cần thực hiện trước nghiệm thu bảo mật:

- JWT sai chữ ký/issuer/audience/thuật toán, hết hạn, chưa tới `nbf`; refresh khóa, xoay khóa và IdP lỗi.
- Giả caller qua body/header; thiếu evidence scope; evidence sai nguồn hoặc định dạng.
- Cùng requestId ở hai caller; truy cập request hoặc snapshot của caller khác; cursor bị sửa hoặc đổi bộ lọc.
- Thiếu quyền chạy/đọc job; chạy tay trùng lịch; kiểm log không lộ dữ liệu nhạy cảm.
- Mất/lặp thông điệp, Redis lỗi/cache cũ và restart; tính đúng vẫn dựa vào MySQL.

Đây là danh sách kiểm thử dự kiến. Chưa có kết quả thực thi được tạo từ việc soạn tài liệu.
