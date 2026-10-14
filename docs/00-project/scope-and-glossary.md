# Phạm vi hệ thống và thuật ngữ – Telecom Subscription Management System

**Tên dự án:** Telecom Subscription Management System  
**Mã tài liệu:** DOC-PROJ-002  
**Phiên bản:** 1.0  
**Trạng thái:** Baseline ban đầu – còn các vấn đề cần làm rõ  
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

Cơ chế quản lý kho số, giữ số tạm thời và ngăn cấp trùng trong trường hợp đồng thời sẽ được xác định trong giai đoạn phân tích và thiết kế.

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

Đăng ký gói cước và trừ tiền là hai chức năng nghiệp vụ độc lập. Hai chức năng có thể phối hợp trong một luồng nghiệp vụ tổng thể.

Phương án phối hợp và xử lý khi một chức năng thành công nhưng chức năng còn lại thất bại chưa được quyết định.

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

Quy tắc kết hợp điều kiện khi truy vấn nhiều gói cước và định nghĩa chi tiết của giao dịch tiêu dùng sẽ được làm rõ trong giai đoạn đặc tả yêu cầu.

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
| QS-01 | Quy mô dữ liệu | Cơ sở dữ liệu có tối thiểu 500.000 bản ghi |
| QS-02 | Hiệu năng đăng ký gói cước | Đạt thông lượng tối thiểu 30 TPS trong bài kiểm thử |
| QS-03 | Hiệu năng trừ tiền | Đạt thông lượng tối thiểu 30 TPS trong bài kiểm thử |
| QS-04 | Xử lý đồng thời | Giả lập tỷ lệ 50% request trừ tiền đồng thời trên cùng một thuê bao và tài khoản |
| QS-05 | Tính chính xác dữ liệu | Có phương pháp kiểm chứng việc trừ tiền được xử lý đúng và đầy đủ |
| QS-06 | Tính toàn vẹn giao dịch | Ngăn ngừa việc cập nhật sai số dư hoặc ghi nhận thiếu giao dịch thành công |
| QS-07 | Xử lý định kỳ | Hỗ trợ chạy job kiểm tra dữ liệu hết hạn hàng ngày |

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
| REST API | REST Application Programming Interface | Giao diện tích hợp cho phép các hệ thống trao đổi yêu cầu và kết quả thông qua HTTP |
| TPS | Transactions Per Second | Số giao dịch được xử lý trong một giây theo tiêu chí đo được xác định trong kế hoạch kiểm thử |
| Xử lý đồng thời | Concurrent Processing | Việc nhiều yêu cầu được thực hiện trong các khoảng thời gian chồng lấn nhau |
| Tính nhất quán | Data Consistency | Tính chất bảo đảm dữ liệu tuân thủ các ràng buộc và quy tắc nghiệp vụ sau những thao tác xử lý |
| Đối soát giao dịch | Transaction Reconciliation | Quá trình so sánh giao dịch và số dư để phát hiện chênh lệch hoặc sai sót |

**Lưu ý về thuật ngữ:**

- Tài khoản dịch vụ (Service Account) không đồng nghĩa với tài khoản đăng nhập người dùng.
- Gói cước (Service Package) là định nghĩa một sản phẩm dịch vụ; đăng ký gói cước (Package Subscription) là thông tin sử dụng sản phẩm đó của một thuê bao cụ thể.
- Giao dịch nghiệp vụ (Business Transaction) khác với database transaction dùng để bảo đảm tính nguyên tử và nhất quán dữ liệu.
- Đấu nối thuê bao trong phạm vi dự án hiện tại được hiểu là xử lý và quản lý thuê bao ở cấp ứng dụng, chưa bao gồm thao tác trực tiếp trên hạ tầng mạng viễn thông thực.

## 7. Giả định, giới hạn và vấn đề chưa xác định

### 7.1. Các quyết định đã thống nhất

- Một khách hàng có thể sở hữu nhiều thuê bao.
- Hỗ trợ cấp số thuê bao tự động hoặc để khách hàng lựa chọn số khả dụng thông qua CSKH.
- Đăng ký gói cước và trừ tiền là hai chức năng độc lập, có thể phối hợp trong một luồng nghiệp vụ.
- Hủy gói cước có hiệu lực khi kết thúc chu kỳ sử dụng hiện tại.
- Giao dịch trừ tiền không đủ số dư bị hủy bỏ và hệ thống trả lỗi.
- Dữ liệu hết hạn được xử lý theo cơ chế soft delete, bảo toàn lịch sử cần thiết.

### 7.2. Các vấn đề cần làm rõ

| Mã | Vấn đề | Dự kiến xử lý |
|---|---|---|
| OQ-01 | Phân loại và khởi tạo tài khoản dịch vụ | Phân tích nghiệp vụ / SRS |
| OQ-02 | Nhận diện và xử lý yêu cầu giao dịch trùng lặp | SRS và thiết kế transaction |
| OQ-03 | Quy tắc lọc của API báo cáo | SRS |
| OQ-04 | Điều kiện đo và đánh giá hiệu năng | NFR và Test Plan |
| OQ-05 | Chống cấp trùng số thuê bao khi xử lý đồng thời | Phân tích / Thiết kế |
| OQ-06 | Phối hợp đăng ký gói cước và trừ tiền khi có lỗi | SRS và thiết kế nghiệp vụ |
| OQ-07 | Quy tắc chuyển trạng thái và thời điểm xử lý dữ liệu hết hạn | SRS / State Diagram |

Các vấn đề chưa được quyết định sẽ được theo dõi trong `clarification-log.md` và không được mặc nhiên xem là đã có phương án thiết kế cuối cùng.

## 8. Liên kết với các tài liệu khác (Related Documents)

- `overview.md`: Tổng quan, mục tiêu và bối cảnh dự án.
- `clarification-log.md`: Các quyết định, giả định và vấn đề chưa được giải quyết.
- `SRS.md`: Đặc tả yêu cầu chức năng và phi chức năng.
- `use-cases/`: Đặc tả chi tiết từng Use Case.
- Các tài liệu Domain Model, kiến trúc và thiết kế CSDL: Được xây dựng ở những giai đoạn tiếp theo.

---

*Tài liệu này xác lập phạm vi ban đầu của TelecomManagement. Mọi thay đổi về chức năng, ranh giới trách nhiệm hoặc thuật ngữ nghiệp vụ phải được xem xét và cập nhật nhất quán với các tài liệu liên quan.*
