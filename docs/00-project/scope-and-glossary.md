# Phạm vi hệ thống và thuật ngữ – Telecom Subscription Management System

**Tên dự án:** Telecom Subscription Management System

**Mã tài liệu:** DOC-PROJ-002

**Phiên bản:** 1.1

**Trạng thái:** Baseline Phase 0 được chủ dự án có quyền nghiệm thu duyệt ngày 14/10/2026

**Giai đoạn:** Phase 0 – Project Definition & Scope

---

## 1. Mục đích tài liệu (Document Purpose)

Tài liệu này xác định phạm vi, ranh giới và các thuật ngữ nghiệp vụ được sử dụng trong dự án Telecom Subscription Management System.

Tài liệu đóng vai trò làm cơ sở cho việc:

- Xác định những chức năng thuộc trách nhiệm của hệ thống.
- Phân biệt trách nhiệm giữa TelecomManagement và các hệ thống bên ngoài.
- Xác định các chức năng nằm ngoài phạm vi triển khai hiện tại.
- Thống nhất cách hiểu những thuật ngữ nghiệp vụ.
- Hỗ trợ xây dựng Software Requirements Specification (SRS), Use Case Specification, Domain Model và thiết kế hệ thống ở các giai đoạn tiếp theo.

Tài liệu tập trung vào phạm vi nghiệp vụ và ranh giới trách nhiệm, không mô tả chi tiết thuật toán, cấu trúc cơ sở dữ liệu hoặc giao diện REST API.

## 2. Ranh giới hệ thống (System Boundary)

### 2.1. Hệ thống được xem xét (System of Interest)

TelecomManagement là hệ thống backend chịu trách nhiệm quản lý dữ liệu và xử lý các nghiệp vụ liên quan đến khách hàng, thuê bao, gói cước, tài khoản dịch vụ và giao dịch.

Hệ thống cung cấp REST API cho các hệ thống bên ngoài gọi đến và có các tác vụ xử lý nội bộ được kích hoạt tự động theo lịch trình.

TelecomManagement không trực tiếp cung cấp giao diện cho khách hàng cuối mà xử lý những yêu cầu nghiệp vụ được gửi từ các hệ thống tích hợp.

### 2.2. Các thành phần liên quan

| Thành phần | Vai trò | Ranh giới trách nhiệm |
|---|---|---|
| Khách hàng | Người đăng ký và sử dụng dịch vụ viễn thông | Cung cấp thông tin và thực hiện yêu cầu thông qua hệ thống CSKH |
| Hệ thống CSKH | Hệ thống bên ngoài gọi REST API | Tiếp nhận yêu cầu từ khách hàng, gửi dữ liệu nghiệp vụ và nhận kết quả |
| TelecomManagement | Hệ thống backend đang phát triển | Kiểm tra, xử lý nghiệp vụ, quản lý trạng thái và dữ liệu |
| Cơ sở dữ liệu | Thành phần lưu trữ của TelecomManagement | Lưu dữ liệu khách hàng, thuê bao, gói cước, tài khoản và giao dịch |
| Bộ phận/hệ thống báo cáo | Bên sử dụng thông tin báo cáo | Yêu cầu hoặc khai thác kết quả truy vấn theo phương thức tích hợp sẽ được xác định |
| Scheduler | Thành phần xử lý nội bộ | Kích hoạt các công việc xử lý dữ liệu định kỳ |
| Công cụ kiểm thử | Thành phần phục vụ phát triển và đánh giá | Sinh dữ liệu, gửi yêu cầu kiểm thử và kiểm chứng kết quả |

Scheduler và cơ sở dữ liệu thuộc phạm vi kỹ thuật nội bộ của hệ thống, không được xem là những hệ thống nghiệp vụ bên ngoài.

### 2.3. Nguyên tắc phân chia trách nhiệm

**Hệ thống CSKH chịu trách nhiệm:**

- Tiếp nhận thông tin và yêu cầu từ khách hàng.
- Gửi yêu cầu đấu nối thuê bao.
- Hiển thị hoặc cung cấp danh sách số thuê bao còn khả dụng để khách hàng lựa chọn.
- Gửi yêu cầu đăng ký hoặc hủy gói cước.
- Gửi các yêu cầu cộng tiền, trừ tiền theo nghiệp vụ.
- Tiếp nhận và hiển thị kết quả xử lý.

**TelecomManagement chịu trách nhiệm:**

- Tiếp nhận và kiểm tra các yêu cầu nghiệp vụ được gửi đến.
- Quản lý thông tin khách hàng và thuê bao trong phạm vi hệ thống.
- Quản lý tình trạng khả dụng và cấp phát số thuê bao.
- Quản lý thông tin đăng ký gói cước và thời hạn sử dụng.
- Quản lý các tài khoản dịch vụ và số dư.
- Thực hiện các giao dịch cộng tiền, trừ tiền.
- Bảo đảm tính nhất quán dữ liệu và xử lý an toàn các yêu cầu đồng thời.
- Cung cấp dữ liệu phục vụ tra cứu và báo cáo.
- Tự động xử lý dữ liệu hết hạn theo lịch trình.

Các quy tắc xác thực, phân quyền và phương thức tích hợp cụ thể sẽ được đặc tả ở những giai đoạn tiếp theo.

## 3. Phạm vi chức năng (Functional Scope)

### 3.1. Các chức năng nằm trong phạm vi (In Scope)

#### SC-01. Quản lý khách hàng và thuê bao

Hệ thống hỗ trợ tiếp nhận thông tin khách hàng và đấu nối thuê bao mới.

Các khả năng thuộc phạm vi:

- Tiếp nhận thông tin đăng ký khách hàng.
- Quản lý quan hệ giữa khách hàng và thuê bao.
- Cho phép một khách hàng sở hữu nhiều thuê bao.
- Hỗ trợ cấp số thuê bao tự động.
- Cung cấp khả năng tra cứu số thuê bao còn khả dụng để hệ thống CSKH cho khách hàng lựa chọn.
- Tiếp nhận số thuê bao được lựa chọn để thực hiện đấu nối.
- Kiểm tra tính khả dụng của số thuê bao khi cấp phát.
- Lưu thông tin khách hàng và thuê bao.
- Trả kết quả xử lý cho hệ thống CSKH.

Tra cứu không giữ số. Tại lúc đấu nối, cấp phát phải không trùng; số được chọn đã bị cấp thì trả lỗi để chọn lại (DEC-02). Quy tắc tái sử dụng số, trạng thái và cơ chế nguyên tử chi tiết chốt tại OQ-05 trước code UC01.

#### SC-02. Quản lý đăng ký gói cước

Hệ thống hỗ trợ đăng ký gói cước cho thuê bao.

Các khả năng thuộc phạm vi:

- Tiếp nhận yêu cầu đăng ký gói cước.
- Xác định thuê bao và gói cước cần đăng ký.
- Kiểm tra điều kiện đăng ký.
- Ghi nhận thông tin đăng ký gói cước.
- Quản lý thời điểm bắt đầu và kết thúc chu kỳ sử dụng.
- Theo dõi trạng thái đăng ký gói cước.
- Trả kết quả xử lý cho hệ thống CSKH.

Đăng ký gói cước và trừ tiền là các chức năng riêng. Backend điều phối thu phí khi đăng ký; gói chỉ có hiệu lực khi thu phí thành công (DEC-03).

Nếu một bước lỗi, không để lại gói đang hoạt động hoặc khoản trừ tiền thành công một phần. Mức phí, tài khoản thu phí, retry và cơ chế transaction chốt ở SRS/thiết kế trước code đăng ký (OQ-06).

#### SC-03. Hủy đăng ký gói cước

Hệ thống hỗ trợ tiếp nhận và xử lý yêu cầu hủy gói cước của thuê bao.

Các khả năng thuộc phạm vi:

- Tiếp nhận yêu cầu hủy gói cước.
- Xác định thông tin đăng ký cần hủy.
- Ghi nhận yêu cầu hủy.
- Duy trì hiệu lực gói cước đến hết chu kỳ sử dụng hiện tại.
- Cập nhật trạng thái khi đến thời điểm hủy có hiệu lực.
- Trả kết quả xử lý cho hệ thống CSKH.

Việc hủy gói cước không có hiệu lực ngay tại thời điểm gửi yêu cầu mà có hiệu lực khi kết thúc chu kỳ sử dụng hiện tại.

#### SC-04. Quản lý tài khoản và giao dịch

Hệ thống quản lý các tài khoản phục vụ sử dụng dịch vụ và những giao dịch làm thay đổi số dư tài khoản.

Các khả năng thuộc phạm vi:

- Quản lý thông tin tài khoản liên kết với thuê bao.
- Theo dõi số dư và thời hạn sử dụng tài khoản.
- Tiếp nhận và xử lý yêu cầu cộng tiền.
- Tiếp nhận và xử lý yêu cầu trừ tiền.
- Kiểm tra số dư trước khi trừ tiền.
- Hủy bỏ transaction và trả lỗi nếu số dư không đủ.
- Ghi nhận các giao dịch phát sinh.
- Bảo đảm dữ liệu số dư và giao dịch nhất quán.
- Hỗ trợ xử lý các yêu cầu đồng thời trên cùng một tài khoản.

Phân loại tài khoản, quy tắc khởi tạo và cơ chế xử lý yêu cầu giao dịch trùng lặp sẽ được làm rõ tại những giai đoạn tiếp theo.

#### SC-05. Tra cứu và báo cáo

Hệ thống cung cấp khả năng tra cứu dữ liệu sử dụng dịch vụ theo điều kiện được yêu cầu.

Các khả năng thuộc phạm vi:

- Tiếp nhận khoảng thời gian tra cứu từ X đến Y.
- Tiếp nhận danh sách gói cước cần tra cứu.
- Xác định các giao dịch tiêu dùng thuộc phạm vi truy vấn.
- Xác định các thuê bao liên quan.
- Trả danh sách thuê bao đáp ứng điều kiện tra cứu.

Quy tắc kết hợp điều kiện khi truy vấn nhiều gói cước và định nghĩa chi tiết của giao dịch tiêu dùng sẽ được làm rõ trong SRS (OQ-03). Chưa mặc định phí đăng ký là tiêu dùng dịch vụ.

Phạm vi có thêm **job tổng hợp tiêu dùng hàng ngày riêng** (DEC-07), ngoài API truy vấn và job hết hạn. Chỉ tiêu, đầu ra, kỳ dữ liệu, lịch chạy và cách chạy lại chốt trong SRS (OQ-13).

#### SC-06. Xử lý dữ liệu hết hạn định kỳ

Hệ thống thực hiện các tác vụ kiểm tra và xử lý dữ liệu hết hạn theo lịch trình hàng ngày.

Các khả năng thuộc phạm vi:

- Tự động kích hoạt job định kỳ.
- Kiểm tra thời hạn sử dụng của các đăng ký gói cước và tài khoản.
- Xác định dữ liệu đã hết hiệu lực.
- Cập nhật trạng thái hoặc thực hiện soft delete theo quy tắc nghiệp vụ.
- Giữ lại thông tin lịch sử cần thiết cho việc tra cứu và đối soát.
- Ghi nhận kết quả thực hiện job phục vụ kiểm tra và xử lý lỗi.

Việc xử lý dữ liệu hết hạn không đồng nghĩa với xóa định nghĩa gói cước trong danh mục gói cước.

Trạng thái dữ liệu và quy tắc xử lý cụ thể sẽ được đặc tả sau.

### 3.2. Các chức năng nằm ngoài phạm vi (Out of Scope)

Trong phạm vi triển khai ban đầu, hệ thống không bao gồm:

| Mã | Nội dung ngoài phạm vi | Giải thích |
|---|---|---|
| EX-01 | Xây dựng giao diện CSKH hoàn chỉnh | CSKH được xem là hệ thống bên ngoài sử dụng REST API |
| EX-02 | Xây dựng ứng dụng di động cho khách hàng | Không thuộc yêu cầu hiện tại |
| EX-03 | Tích hợp trực tiếp với thiết bị và hạ tầng mạng viễn thông thực tế | Nghiệp vụ đấu nối được mô hình hóa trong backend; việc kích hoạt trên mạng thực chưa nằm trong đề bài |
| EX-04 | Tích hợp cổng thanh toán hoặc ngân hàng thực tế | Phạm vi hiện tại tập trung vào xử lý giao dịch cộng/trừ tiền nội bộ |
| EX-05 | Xây dựng hệ thống tính cước viễn thông hoàn chỉnh | Chưa bao gồm tính giá cước theo lưu lượng, thời lượng cuộc gọi hay dữ liệu mạng thực tế |
| EX-06 | Xây dựng nền tảng Business Intelligence hoàn chỉnh | Chỉ triển khai các khả năng truy vấn và báo cáo được yêu cầu |

Các chức năng ngoài phạm vi có thể được xem xét mở rộng trong tương lai nếu có yêu cầu mới.

Việc chưa xác định chi tiết một chức năng thuộc phạm vi không có nghĩa chức năng đó bị loại bỏ.

## 4. Phạm vi dữ liệu (Data Scope)

### 4.1. Các nhóm dữ liệu được quản lý

| Nhóm dữ liệu | Nội dung chính |
|---|---|
| Customer | Thông tin cá nhân của khách hàng |
| Subscriber | Thông tin số thuê bao và quan hệ sở hữu |
| Service Package | Danh mục và thông tin gói cước |
| Package Subscription | Thông tin đăng ký, chu kỳ và trạng thái sử dụng gói cước |
| Account | Thông tin tài khoản, số dư và thời hạn sử dụng |
| Transaction | Thông tin giao dịch cộng/trừ tiền và lịch sử biến động tài khoản |

Ngoài các nhóm dữ liệu trên, hệ thống cần có khả năng quản lý trạng thái khả dụng của số thuê bao để hỗ trợ cấp số tự động và cho khách hàng lựa chọn số.

Việc mô hình hóa dữ liệu số thuê bao thành bảng độc lập hay kết hợp với cấu trúc khác sẽ được quyết định khi thiết kế cơ sở dữ liệu.

### 4.2. Quan hệ nghiệp vụ cơ bản

- Một khách hàng có thể sở hữu nhiều thuê bao.
- Một thuê bao thuộc về một khách hàng tại một thời điểm.
- Một thuê bao có thể đăng ký nhiều gói cước.
- Một gói cước có thể được nhiều thuê bao đăng ký.
- Quan hệ đăng ký gói cước được quản lý thông qua thông tin đăng ký riêng.
- Một thuê bao có thể có nhiều tài khoản phục vụ các dịch vụ khác nhau.
- Mỗi tài khoản thuộc về một thuê bao.
- Một tài khoản có thể phát sinh nhiều giao dịch.
- Mỗi giao dịch cộng hoặc trừ tiền tác động đến một tài khoản cụ thể.
- Các giao dịch tiêu dùng có thể liên quan đến gói cước hoặc dịch vụ tương ứng.

### 4.3. Lưu trữ và vòng đời dữ liệu

Hệ thống sử dụng cơ sở dữ liệu quan hệ để quản lý các đối tượng nghiệp vụ.

Những dữ liệu hết hạn cần được xử lý bằng cách cập nhật trạng thái hoặc cơ chế soft delete thay vì xóa vật lý những thông tin lịch sử cần thiết.

Hệ thống phải bảo toàn dữ liệu giao dịch phục vụ việc kiểm tra, tra cứu và đối soát.

Các quy tắc chi tiết về thời gian lưu trữ, trạng thái dữ liệu, ràng buộc và chiến lược lưu trữ sẽ được xác định trong SRS và thiết kế cơ sở dữ liệu.

## 5. Phạm vi chất lượng và hiệu năng (Quality & Performance Scope)

Hệ thống phải đáp ứng các yêu cầu phi chức năng trọng tâm sau:

| Mã | Nhóm yêu cầu | Phạm vi |
|---|---|---|
| QS-01 | Quy mô dữ liệu | Tổng các bảng dữ liệu nghiệp vụ có tối thiểu 500.000 bản ghi trước đo tải, thống kê từng bảng (DEC-08) |
| QS-02 | Hiệu năng đăng ký gói cước | Đạt ít nhất 30 nghiệp vụ mới thành công/giây trong cùng bài tải hỗn hợp với nghiệp vụ còn lại; tổng ít nhất 60 TPS (DEC-09) |
| QS-03 | Hiệu năng trừ tiền | Đạt ít nhất 30 nghiệp vụ mới thành công/giây trong cùng bài tải hỗn hợp với nghiệp vụ còn lại; tổng ít nhất 60 TPS (DEC-09) |
| QS-04 | Xử lý đồng thời | 50% tổng request trừ tiền chia vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp và thực sự chồng lấn (DEC-10) |
| QS-05 | Tính chính xác dữ liệu | Tool đối soát từng mã yêu cầu/giao dịch và số dư; một tác động khi retry, đủ/thiếu tiền và timeout phải xác định kết quả (DEC-11) |
| QS-06 | Tính toàn vẹn giao dịch | Ngăn ngừa việc cập nhật sai số dư hoặc ghi nhận thiếu giao dịch thành công |
| QS-07 | Xử lý định kỳ | Job hết hạn hàng ngày theo DEC-06 và job tổng hợp tiêu dùng hàng ngày theo DEC-07 |

Các thông số về môi trường, thời gian kiểm thử, đặc điểm tải và điều kiện nghiệm thu sẽ được đặc tả chi tiết trong tài liệu yêu cầu phi chức năng và kế hoạch kiểm thử.

## 6. Thuật ngữ nghiệp vụ (Business Glossary)

| Thuật ngữ | Tên tiếng Anh | Định nghĩa |
|---|---|---|
| Khách hàng | Customer | Cá nhân đăng ký sử dụng dịch vụ viễn thông, có thể sở hữu nhiều thuê bao |
| Thuê bao | Subscriber | Đối tượng đại diện cho một thuê bao viễn thông đã được đăng ký trong hệ thống |
| Số thuê bao | Subscriber Number / MSISDN | Số định danh dùng để nhận diện thuê bao; phải được kiểm soát để tránh cấp trùng |
| Đấu nối thuê bao | Subscriber Activation / Provisioning | Nghiệp vụ tạo và kích hoạt thông tin thuê bao trong phạm vi hệ thống quản lý |
| Kho số | Number Inventory | Tập hợp các số thuê bao và thông tin trạng thái khả dụng phục vụ cấp phát |
| Số thuê bao khả dụng | Available Number | Số thuê bao đáp ứng điều kiện để được cấp cho thuê bao mới |
| Gói cước | Service Package | Gói dịch vụ có các đặc điểm và thời hạn sử dụng được quy định |
| Đăng ký gói cước | Package Subscription | Việc một thuê bao đăng ký sử dụng một gói cước cụ thể |
| Chu kỳ sử dụng | Subscription Period | Khoảng thời gian một lần đăng ký gói cước có hiệu lực |
| Hủy gói cước | Package Cancellation | Nghiệp vụ yêu cầu kết thúc hiệu lực đăng ký gói cước theo quy tắc đã xác định |
| Tài khoản dịch vụ | Service Account | Đối tượng quản lý tiền và thông tin liên quan đến việc sử dụng dịch vụ của một thuê bao |
| Số dư | Balance | Số tiền được hệ thống ghi nhận trên tài khoản tại một thời điểm |
| Giao dịch | Business Transaction | Bản ghi thể hiện một nghiệp vụ cộng tiền hoặc trừ tiền đối với tài khoản |
| Cộng tiền | Credit | Nghiệp vụ làm tăng số dư tài khoản |
| Trừ tiền | Debit | Nghiệp vụ làm giảm số dư tài khoản |
| Giao dịch cơ sở dữ liệu | Database Transaction | Đơn vị thực thi các thao tác dữ liệu theo cơ chế giao dịch của hệ quản trị CSDL |
| Rollback | Transaction Rollback | Hủy bỏ các thay đổi chưa được commit trong một database transaction |
| Dữ liệu hết hạn | Expired Data | Dữ liệu có thời điểm hết hiệu lực theo quy tắc nghiệp vụ |
| Soft Delete | Logical Deletion | Đánh dấu dữ liệu không còn hiệu lực hoặc bị xóa về mặt logic mà không xóa bản ghi vật lý |
| Scheduler | Task Scheduler | Thành phần kích hoạt các công việc tự động theo lịch |
| Job định kỳ | Scheduled Job | Tác vụ được thực hiện tự động theo thời điểm hoặc chu kỳ quy định |
| Job tổng hợp tiêu dùng | Daily Consumption Aggregation Job | Tác vụ tổng hợp tiêu dùng hàng ngày riêng, khác API truy vấn và job hết hạn; chỉ tiêu/đầu ra chốt trong SRS |
| REST API | REST Application Programming Interface | Giao diện tích hợp cho phép các hệ thống trao đổi yêu cầu và kết quả thông qua HTTP |
| TPS | Transactions Per Second | Số nghiệp vụ mới hoàn tất thành công trong một giây; retry trả lại kết quả cũ không được tính thành nghiệp vụ mới; công bố riêng lỗi và timeout |
| Xử lý đồng thời | Concurrent Processing | Việc nhiều yêu cầu được thực hiện trong các khoảng thời gian chồng lấn nhau |
| Tính nhất quán | Data Consistency | Tính chất bảo đảm dữ liệu tuân thủ các ràng buộc và quy tắc nghiệp vụ sau những thao tác xử lý |
| Đối soát giao dịch | Transaction Reconciliation | Quá trình so sánh giao dịch và số dư để phát hiện chênh lệch hoặc sai sót |

**Lưu ý về thuật ngữ:**

- Tài khoản dịch vụ (Service Account) không đồng nghĩa với tài khoản đăng nhập người dùng.
- Gói cước (Service Package) là định nghĩa một sản phẩm dịch vụ; đăng ký gói cước (Package Subscription) là thông tin sử dụng sản phẩm đó của một thuê bao cụ thể.
- Giao dịch nghiệp vụ (Business Transaction) khác với database transaction dùng để bảo đảm tính nguyên tử và nhất quán dữ liệu.
- Đấu nối thuê bao trong phạm vi dự án hiện tại được hiểu là xử lý và quản lý thuê bao ở cấp ứng dụng, chưa bao gồm thao tác trực tiếp trên hạ tầng mạng viễn thông thực.

## 7. Quyết định và chi tiết được hoãn

### 7.1. Baseline đã được duyệt

Chủ dự án, người dùng có quyền quyết định nghiệm thu cuối cùng, xác nhận ngày 14/10/2026 qua 14 đáp án. Danh mục đầy đủ DEC-01–DEC-11 và nội dung từng đáp án nằm trong [clarification log](clarification-log.md); các quyết định quan trọng:

- Customer–Subscriber là 1–N; cấp số tự động/chọn số, không giữ số khi xem và không cấp trùng.
- Đăng ký có thu phí do backend điều phối, không thành công một phần; hủy cuối chu kỳ.
- Không thấu chi; thiếu tiền không thay đổi số dư hay tạo giao dịch tài chính thành công.
- Soft delete/trạng thái lần đăng ký và tài khoản hết hạn, giữ lịch sử/danh mục; thời gian giữ và số dư còn lại vẫn cần chốt.
- Có API báo cáo, job tổng hợp tiêu dùng hàng ngày và job hết hạn hàng ngày.
- Dữ liệu tối thiểu 500.000 bản ghi tính tổng các bảng nghiệp vụ trước đo; cùng chạy ≥30 TPS mỗi nghiệp vụ, tổng ≥60 TPS. Retry không được đếm như nghiệp vụ mới.
- 50% tổng request trừ tiền chia vào nhiều nhóm tranh chấp đồng thời; có tool đối soát từng yêu cầu/giao dịch và số dư.

Phạm vi loại trừ EX-01–EX-06 giữ nguyên theo Q01=A. Các cách diễn giải quan hệ khách hàng, “xóa”, tải hỗn hợp và nhóm tranh chấp đã được người có quyền nghiệm thu chấp nhận; [đề bài gốc](../requirement.md) giữ nguyên để đối chiếu.

### 7.2. Chi tiết còn mở

OQ-08 đã Resolved; các OQ còn lại vẫn Open ở phần chi tiết. Chủ dự án chịu trách nhiệm chốt, trợ lý tổng hợp/đề xuất; mốc đầy đủ tại [clarification log, mục 6](clarification-log.md#6-trạng-thái-và-kế-hoạch-xử-lý-oq).

| Nhóm | Chi tiết cần chốt | Mốc |
|---|---|---|
| OQ-05, OQ-12, OQ-15 | Tái sử dụng/cấp số nguyên tử, hồ sơ khách hàng, hợp đồng và quyền gọi API | SRS UC01 trước thiết kế/code đấu nối |
| OQ-01, OQ-02, OQ-06, OQ-09, OQ-10 | Tài khoản, chống trùng, mức phí/tài khoản thu, chu kỳ/gia hạn, đơn vị tiền/hạn mức | SRS và thiết kế trước code gói/tài khoản/giao dịch |
| OQ-07, OQ-11 | Trạng thái, hiệu lực, chạy lại job, thời gian giữ lịch sử, số dư hết hạn | SRS trước thiết kế gói/tài khoản/job |
| OQ-03, OQ-13 | OR/AND gói, định nghĩa tiêu dùng/phí, thời gian lọc, đầu ra/chỉ tiêu/lịch báo cáo | SRS trước thiết kế báo cáo |
| OQ-04, OQ-14 | Môi trường, thời lượng, ngưỡng lỗi/độ trễ, phân bố dữ liệu/nhóm, mẫu số request trong tải hỗn hợp và bằng chứng đồng thời | NFR/Test Plan trước chạy tải; tính đúng trước thiết kế giao dịch |

Hoãn chi tiết không có nghĩa bỏ yêu cầu hoặc chấp nhận một đề xuất chưa duyệt. Không đánh dấu đã nghiệm thu hiệu năng ở Phase 0.

## 8. Tài liệu liên quan và trạng thái giai đoạn

- [Overview](overview.md): mục tiêu, bối cảnh và danh mục quyết định.
- [Clarification log](clarification-log.md): bản ghi duyệt, truy vết yêu cầu và danh sách còn mở.
- [Roadmap](../ROADMAP.md): Phase 0 hoàn thành, Phase 1 chưa bắt đầu.
- SRS, Use Case, Domain Model, kiến trúc, CSDL và API sẽ tạo ở các giai đoạn tương ứng; chưa có liên kết tới file chưa tồn tại.

## 9. Lịch sử cập nhật

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 1.0 | Trước duyệt Phase 0 | Phạm vi và thuật ngữ ban đầu. |
| 1.1 | 14/10/2026 | Đồng bộ các lựa chọn đã duyệt, bổ sung thu phí đăng ký/job báo cáo, cập nhật quy mô/tải và trách nhiệm xử lý chi tiết; đóng Phase 0. |


## Bổ sung hiện hành ngày 18/10/2026

Baseline phạm vi Phase0 giữ nguyên; các định hướng chi tiết tại DEC-PRE-01–24 trong [clarification log](clarification-log.md) đã được tiếp nhận. Hồ sơ so mọi trường gửi/chứng cứ ngoài; UC01 caller-scoped ID/replay7 ngày+tombstone/FIFO/recovery 60s. Gói giá snapshot/chu kỳ ngày/hủy theo mốc nhận, lịch job00:05/00:30 Asia/Ho_Chi_Minh, lịch sử 5 năm không purge demo. Báo cáo [X,Y) theo commit/cursor; kiểm thử k6 với các ngưỡng bổ sung. RabbitMQ/Redis theo CR-ARCH-001, không mở rộng mạng thật/ngân hàng/UI. Các trạng thái “còn chờ chọn” trong phần baseline lịch sử phải đối chiếu bảng OQ hiện hành; retention tiền/hạn mức phụ còn đề xuất. M1 vẫn chờ chủ dự án đọc SRS0.6/UC01 0.5 và trả lời Q25. [Thứ tự đọc](review-guide.md).
