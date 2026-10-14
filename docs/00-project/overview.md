# Tổng quan dự án – Hệ thống Quản lý thuê bao viễn thông

**Tên dự án:** Telecom Subscription Management System  
**Mã tài liệu:** DOC-PROJ-001  
**Phiên bản:** 1.0  
**Trạng thái:** Baseline ban đầu – còn các vấn đề cần làm rõ  
**Giai đoạn:** Phase 0 – Project Definition & Scope

## 1. Giới thiệu dự án (Project Introduction)

Hệ thống Quản lý thuê bao viễn thông được xây dựng nhằm quản lý thông tin khách hàng, thuê bao, gói cước, tài khoản dịch vụ và các giao dịch phát sinh trong quá trình sử dụng dịch vụ viễn thông.

Hệ thống cung cấp các REST API để những hệ thống bên ngoài, đặc biệt là hệ thống chăm sóc khách hàng (CSKH), có thể thực hiện các nghiệp vụ như đấu nối thuê bao mới, đăng ký hoặc hủy gói cước, cộng tiền, trừ tiền và tra cứu dữ liệu sử dụng dịch vụ.

Một khách hàng có thể sở hữu nhiều thuê bao, mỗi thuê bao có thể đăng ký nhiều gói cước và có các tài khoản phục vụ những dịch vụ khác nhau.

Ngoài các nghiệp vụ trực tuyến, hệ thống còn hỗ trợ truy vấn dữ liệu phục vụ báo cáo, xử lý dữ liệu hết hạn định kỳ và bảo đảm tính chính xác của giao dịch khi nhiều yêu cầu được thực hiện đồng thời.

## 2. Bối cảnh và bài toán (Problem Statement)

Trong hoạt động viễn thông, khách hàng cần đăng ký thông tin cá nhân trước khi được cấp quyền sử dụng dịch vụ thông qua số thuê bao.

Một khách hàng có thể sở hữu nhiều số thuê bao. Khi thực hiện đấu nối mới, số thuê bao có thể được hệ thống cấp tự động hoặc được khách hàng lựa chọn từ danh sách số chưa được sử dụng thông qua hệ thống CSKH.

Mỗi thuê bao có thể đăng ký nhiều gói cước và có các tài khoản phục vụ những dịch vụ khác nhau. Các gói cước và tài khoản có thời hạn sử dụng riêng.

Việc đăng ký gói cước và trừ tiền được thực hiện bởi hai chức năng nghiệp vụ độc lập, nhưng có thể phối hợp trong cùng một luồng nghiệp vụ khi khách hàng đăng ký sử dụng dịch vụ.

Trong quá trình tiêu dùng, hệ thống phải ghi nhận đầy đủ các giao dịch làm tăng hoặc giảm số dư tài khoản, đồng thời bảo đảm tính chính xác khi có nhiều giao dịch tác động đồng thời lên cùng tài khoản.

Việc quản lý này đặt ra những yêu cầu chính:

- Quản lý thống nhất thông tin khách hàng, thuê bao và các dịch vụ liên quan.
- Quản lý việc cấp phát và sử dụng số thuê bao, tránh cấp trùng số.
- Theo dõi chính xác trạng thái đăng ký và thời hạn sử dụng gói cước.
- Hỗ trợ hủy gói cước với hiệu lực tại thời điểm kết thúc chu kỳ hiện tại.
- Ghi nhận đầy đủ và chính xác các giao dịch cộng tiền, trừ tiền.
- Bảo đảm tính nhất quán của số dư tài khoản khi có nhiều yêu cầu đồng thời.
- Từ chối giao dịch trừ tiền khi số dư khả dụng không đủ.
- Hỗ trợ tra cứu dữ liệu tiêu dùng phục vụ báo cáo.
- Tự động xử lý trạng thái dữ liệu hết hạn mà không làm mất thông tin lịch sử cần thiết.
- Đáp ứng các yêu cầu về quy mô dữ liệu và hiệu năng xử lý.

## 3. Mục tiêu dự án (Project Objectives)

### 3.1. Mục tiêu nghiệp vụ

Xây dựng hệ thống backend hỗ trợ các nghiệp vụ quản lý thuê bao viễn thông, bao gồm:

1. Đấu nối thuê bao mới cho khách hàng.
2. Hỗ trợ cấp số thuê bao tự động hoặc lựa chọn số thuê bao còn khả dụng.
3. Đăng ký gói cước cho thuê bao.
4. Hủy gói cước với hiệu lực khi kết thúc chu kỳ sử dụng hiện tại.
5. Cộng tiền vào tài khoản của thuê bao.
6. Trừ tiền từ tài khoản khi sử dụng dịch vụ.
7. Tra cứu danh sách thuê bao có phát sinh tiêu dùng theo khoảng thời gian và gói cước.
8. Kiểm tra và xử lý dữ liệu hết hạn theo lịch trình hàng ngày.

### 3.2. Mục tiêu kỹ thuật

- Xây dựng mô hình dữ liệu và cơ sở dữ liệu quan hệ phù hợp với các đối tượng nghiệp vụ.
- Cung cấp REST API để các hệ thống bên ngoài thực hiện các chức năng được yêu cầu.
- Bảo đảm tính toàn vẹn và nhất quán của dữ liệu trong quá trình xử lý giao dịch.
- Hỗ trợ xử lý các yêu cầu đồng thời trên cùng một thuê bao và tài khoản.
- Bảo đảm việc cập nhật số dư và ghi nhận giao dịch được thực hiện nhất quán trong một transaction.
- Không cho phép giao dịch trừ tiền làm số dư tài khoản xuống dưới mức cho phép.
- Hỗ trợ quản lý vòng đời và trạng thái của gói cước, tài khoản.
- Hỗ trợ xử lý dữ liệu định kỳ theo cơ chế soft delete.
- Xây dựng công cụ kiểm thử hiệu năng và kiểm chứng tính đúng đắn của giao dịch.

## 4. Tổng quan hệ thống (System Overview)

### 4.1. Các nhóm chức năng chính

**Quản lý thuê bao (Subscriber Management)**

Quản lý thông tin thuê bao, quá trình đấu nối thuê bao mới và liên kết thuê bao với khách hàng.

Hỗ trợ hai hình thức xác định số thuê bao khi đấu nối:

- Hệ thống tự động cấp một số thuê bao còn khả dụng.
- Hệ thống CSKH tra cứu danh sách số thuê bao còn khả dụng, cho phép khách hàng lựa chọn và truyền số đã chọn trong yêu cầu đấu nối.

**Quản lý gói cước (Package Management)**

Quản lý việc đăng ký, hủy đăng ký và theo dõi thời gian sử dụng gói cước.

Nghiệp vụ đăng ký gói cước không trực tiếp thực hiện chức năng trừ tiền. Hai chức năng được thiết kế độc lập để có thể phối hợp trong cùng một luồng nghiệp vụ.

Khi khách hàng yêu cầu hủy gói cước, hiệu lực hủy được áp dụng tại thời điểm kết thúc chu kỳ sử dụng hiện tại. Gói cước tiếp tục có hiệu lực đến thời điểm đó.

**Quản lý tài khoản và giao dịch (Account & Transaction Management)**

Quản lý tài khoản dịch vụ, số dư và các giao dịch cộng tiền, trừ tiền.

Khi thực hiện trừ tiền, hệ thống kiểm tra số dư khả dụng. Nếu số dư không đủ, transaction bị hủy bỏ, hệ thống không thay đổi số dư và trả về kết quả thất bại.

Các giao dịch phải được xử lý an toàn và nhất quán khi nhiều yêu cầu đồng thời tác động lên cùng tài khoản.

**Tra cứu và báo cáo (Query & Reporting)**

Cung cấp khả năng tra cứu danh sách thuê bao có phát sinh tiêu dùng trên một hoặc nhiều gói cước trong khoảng thời gian xác định.

Các điều kiện lọc và quy tắc xác định kết quả sẽ được đặc tả chi tiết trong giai đoạn phân tích yêu cầu.

**Xử lý dữ liệu định kỳ (Scheduled Processing)**

Thực hiện job hàng ngày để kiểm tra các gói cước và tài khoản đã hết thời gian sử dụng.

Hệ thống áp dụng cơ chế soft delete hoặc cập nhật trạng thái phù hợp thay vì xóa vật lý dữ liệu nghiệp vụ.

Thông tin lịch sử và giao dịch cần thiết được giữ lại để hỗ trợ truy vấn, kiểm tra và đối soát.

### 4.2. Các đối tượng nghiệp vụ chính

Hệ thống quản lý sáu nhóm đối tượng:

- **Khách hàng (Customer):** Người đăng ký sử dụng dịch vụ viễn thông. Một khách hàng có thể sở hữu nhiều thuê bao.
- **Thuê bao (Subscriber):** Đại diện cho một số thuê bao được đăng ký. Mỗi thuê bao thuộc về một khách hàng tại một thời điểm.
- **Gói cước (Service Package):** Gói dịch vụ mà thuê bao có thể đăng ký sử dụng.
- **Đăng ký gói cước (Package Subscription):** Ghi nhận việc một thuê bao đăng ký một gói cước, thời gian sử dụng và trạng thái đăng ký.
- **Tài khoản (Account):** Quản lý số dư và thông tin sử dụng dịch vụ của thuê bao.
- **Giao dịch (Transaction):** Ghi nhận các nghiệp vụ cộng tiền và trừ tiền phát sinh trên tài khoản.

Danh sách số thuê bao còn khả dụng, cơ chế cấp phát số và các trạng thái liên quan sẽ được phân tích chi tiết ở những giai đoạn tiếp theo.

Cấu trúc thuộc tính và quan hệ chi tiết giữa các đối tượng được xác định tại giai đoạn phân tích miền nghiệp vụ và thiết kế cơ sở dữ liệu.

## 5. Các bên liên quan (Stakeholders)

**Khách hàng viễn thông**

Là người đăng ký và sử dụng dịch vụ. Khách hàng cung cấp thông tin cá nhân, lựa chọn số thuê bao khi có nhu cầu và yêu cầu sử dụng hoặc ngừng sử dụng dịch vụ thông qua hệ thống CSKH.

Khách hàng không trực tiếp gọi REST API của hệ thống quản lý thuê bao.

**Hệ thống chăm sóc khách hàng (CSKH)**

Là hệ thống bên ngoài tương tác với backend thông qua REST API để thực hiện các nghiệp vụ quản lý thuê bao, gói cước và tài khoản.

**Bộ phận hoặc hệ thống báo cáo**

Là bên sử dụng dữ liệu tra cứu về tình hình tiêu dùng dịch vụ của thuê bao. Phương thức tích hợp cụ thể sẽ được xác định trong tài liệu yêu cầu.

**Đội phát triển và vận hành**

Chịu trách nhiệm phân tích, thiết kế, xây dựng, kiểm thử, triển khai và duy trì hệ thống.

## 6. Yêu cầu và ràng buộc tổng quan (High-Level Requirements & Constraints)

### 6.1. Yêu cầu từ đề bài

| Mã | Yêu cầu |
|---|---|
| HLR-01 | Xây dựng cơ sở dữ liệu có tối thiểu 500.000 bản ghi. |
| HLR-02 | Hỗ trợ kiểm thử nghiệp vụ đăng ký gói cước với thông lượng tối thiểu 30 TPS. |
| HLR-03 | Hỗ trợ kiểm thử nghiệp vụ trừ tiền với thông lượng tối thiểu 30 TPS. |
| HLR-04 | Trong quá trình kiểm thử trừ tiền, giả lập 50% request đồng thời tác động lên cùng một thuê bao và cùng một tài khoản. |
| HLR-05 | Có công cụ và phương pháp kiểm chứng các giao dịch trừ tiền được xử lý đúng và đầy đủ khi có tác động đồng thời. |
| HLR-06 | Có job định kỳ hàng ngày để kiểm tra và xử lý dữ liệu gói cước, tài khoản hết hạn. |
| HLR-07 | Có API tra cứu các thuê bao có phát sinh tiêu dùng theo thời gian và gói cước yêu cầu. |

### 6.2. Các quyết định nghiệp vụ đã được thống nhất

| Mã | Quyết định |
|---|---|
| DEC-01 | Một khách hàng có thể sở hữu nhiều thuê bao. |
| DEC-02 | Số thuê bao có thể được cấp tự động hoặc được lựa chọn từ danh sách số khả dụng thông qua hệ thống CSKH. |
| DEC-03 | Đăng ký gói cước và trừ tiền là hai chức năng độc lập nhưng có thể phối hợp trong cùng một luồng nghiệp vụ. |
| DEC-04 | Việc hủy gói cước có hiệu lực khi kết thúc chu kỳ sử dụng hiện tại. |
| DEC-05 | Giao dịch trừ tiền khi số dư không đủ sẽ bị hủy bỏ, không thay đổi số dư và trả lỗi. |
| DEC-06 | Dữ liệu gói cước và tài khoản hết hạn được xử lý theo cơ chế soft delete, không xóa vật lý lịch sử nghiệp vụ cần thiết. |

Các yêu cầu và quyết định này sẽ được chuyển thành yêu cầu chức năng, quy tắc nghiệp vụ, yêu cầu phi chức năng và tiêu chí nghiệm thu tại giai đoạn SRS.

## 7. Các vấn đề cần làm rõ (Open Questions)

Các vấn đề sau chưa được quyết định đầy đủ và sẽ được tiếp tục phân tích tại những giai đoạn phù hợp.

| Mã | Vấn đề | Trạng thái |
|---|---|---|
| OQ-01 | Phân loại các tài khoản dịch vụ và quy tắc khởi tạo tài khoản. | Open |
| OQ-02 | Cơ chế nhận diện và xử lý yêu cầu giao dịch trùng lặp, đặc biệt khi hệ thống bên ngoài gửi lại yêu cầu. | Open |
| OQ-03 | Quy tắc lọc và xác định kết quả của API báo cáo khi truy vấn nhiều gói cước. | Open |
| OQ-04 | Môi trường, thời gian đo, dữ liệu thử nghiệm và tiêu chí đánh giá hiệu năng 30 TPS trên cơ sở dữ liệu tối thiểu 500.000 bản ghi. | Open |
| OQ-05 | Cơ chế bảo đảm không cấp trùng số thuê bao khi nhiều yêu cầu đấu nối đồng thời. | Open |
| OQ-06 | Cách phối hợp đăng ký gói cước và trừ tiền, đặc biệt khi một chức năng thành công nhưng chức năng còn lại thất bại. | Open |
| OQ-07 | Các trạng thái chi tiết và quy tắc chuyển trạng thái khi hủy gói cước hoặc xử lý dữ liệu hết hạn. | Open |

Các vấn đề chưa có quyết định được ghi nhận để tiếp tục phân tích, không được xem là quy tắc nghiệp vụ đã thống nhất.

---

*Tài liệu này cung cấp cái nhìn tổng quan về dự án và ghi nhận những quyết định nghiệp vụ ban đầu. Các yêu cầu chi tiết, mô hình nghiệp vụ, kiến trúc, cơ sở dữ liệu, API và phương án kiểm thử sẽ được mô tả trong những tài liệu tương ứng ở các giai đoạn tiếp theo.*
