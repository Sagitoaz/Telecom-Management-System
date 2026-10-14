# Tổng quan dự án – Hệ thống Quản lý thuê bao viễn thông

**Tên dự án:** Telecom Subscription Management System

**Mã tài liệu:** DOC-PROJ-001

**Phiên bản:** 1.1

**Trạng thái:** Baseline Phase 0 đã được chủ dự án duyệt ngày 14/10/2026; chi tiết còn mở được theo dõi riêng

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

Đăng ký gói cước và trừ tiền là các chức năng riêng. Khi đăng ký gói có thu phí, backend điều phối hai bước; gói chỉ có hiệu lực khi thu phí thành công. Nếu một bước lỗi, không để lại gói hoạt động hoặc khoản trừ tiền thành công một phần (DEC-03).

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
9. Tổng hợp tiêu dùng bằng một job báo cáo hàng ngày riêng (DEC-07).

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

Đăng ký gói cước có thu phí do backend điều phối. Chỉ trả kết quả đăng ký thành công khi gói và việc thu phí cùng thành công; không để lại trạng thái thành công một phần. Các chức năng đăng ký và trừ tiền vẫn có trách nhiệm riêng; cơ chế transaction được chốt ở thiết kế (OQ-06).

Khi khách hàng yêu cầu hủy gói cước, hiệu lực hủy được áp dụng tại thời điểm kết thúc chu kỳ sử dụng hiện tại. Gói cước tiếp tục có hiệu lực đến thời điểm đó.

**Quản lý tài khoản và giao dịch (Account & Transaction Management)**

Quản lý tài khoản dịch vụ, số dư và các giao dịch cộng tiền, trừ tiền.

Khi thực hiện trừ tiền, hệ thống kiểm tra số dư khả dụng. Nếu số dư không đủ, transaction bị hủy bỏ, hệ thống không thay đổi số dư và trả về kết quả thất bại.

Các giao dịch phải được xử lý an toàn và nhất quán khi nhiều yêu cầu đồng thời tác động lên cùng tài khoản.

**Tra cứu và báo cáo (Query & Reporting)**

Cung cấp khả năng tra cứu danh sách thuê bao có phát sinh tiêu dùng trên một hoặc nhiều gói cước trong khoảng thời gian xác định.

Có thêm job tổng hợp tiêu dùng hàng ngày riêng, ngoài API truy vấn và job hết hạn. Điều kiện lọc, chỉ tiêu, đầu ra và lịch báo cáo sẽ được đặc tả trong SRS (OQ-03, OQ-13).

**Xử lý dữ liệu định kỳ (Scheduled Processing)**

Thực hiện job hàng ngày để kiểm tra các gói cước và tài khoản đã hết thời gian sử dụng.

Hệ thống áp dụng cơ chế soft delete hoặc cập nhật trạng thái phù hợp thay vì xóa vật lý dữ liệu nghiệp vụ.

Soft delete/trạng thái áp dụng cho lần đăng ký gói và tài khoản hết hạn; không xóa định nghĩa gói chỉ vì một lần đăng ký hết hạn. Thông tin lịch sử và giao dịch cần thiết được giữ để truy vấn và đối soát (DEC-06).

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

### 6.1. Yêu cầu gốc và cách áp dụng đã duyệt

| Mã | Yêu cầu / cách áp dụng baseline |
|---|---|
| HLR-01 | Tổng các bảng dữ liệu nghiệp vụ có tối thiểu 500.000 bản ghi trước đo tải; thống kê từng bảng (DEC-08). |
| HLR-02 | Trong cùng bài tải hỗn hợp, đăng ký gói đạt ít nhất 30 nghiệp vụ mới thành công/giây (DEC-09). |
| HLR-03 | Đồng thời với HLR-02, trừ tiền đạt ít nhất 30 nghiệp vụ mới thành công/giây; tổng hai loại ít nhất 60 TPS. Không đếm retry như nghiệp vụ mới (DEC-09). |
| HLR-04 | 50% tổng request trừ tiền chia thành nhiều nhóm cặp thuê bao–tài khoản có tranh chấp và thực sự chồng lấn; phân bố chi tiết chốt trong Test Plan (DEC-10). |
| HLR-05 | Tool đối chiếu từng mã yêu cầu/giao dịch đã commit, một tác động khi retry và số dư đầu/cuối; ca đủ tiền, thiếu tiền, timeout phải rõ kết quả (DEC-11). |
| HLR-06 | Job hết hạn hàng ngày dùng soft delete/trạng thái lần đăng ký và tài khoản; giữ lịch sử/danh mục gói (DEC-06). |
| HLR-07 | API truy vấn thuê bao có tiêu dùng theo X–Y và gói; điều kiện lọc chi tiết còn mở. |
| HLR-08 | Job tổng hợp tiêu dùng hàng ngày riêng; chỉ tiêu, đầu ra và lịch chạy chốt trong SRS (DEC-07). |

Các dòng trên phân biệt cách áp dụng đã duyệt với [nguyên văn đề bài](../requirement.md), đặc biệt quan hệ khách hàng, “xóa” và cách đo tải. Không coi mục tiêu là kết quả kiểm thử đã đạt.

### 6.2. Quyết định baseline

Chủ dự án xác nhận ngày 14/10/2026 với quyền quyết định nghiệm thu cuối cùng (Q13=C). Toàn bộ 14 đáp án, căn cứ và lịch sử được lưu trong [clarification log](clarification-log.md).

| Mã | Quyết định |
|---|---|
| DEC-01 | Một khách hàng có nhiều thuê bao; mỗi thuê bao thuộc một khách hàng tại một thời điểm. |
| DEC-02 | Cấp số tự động/chọn số; xem danh sách không giữ số; cấp phát không trùng; số đã bị cấp thì trả lỗi để chọn lại. |
| DEC-03 | Đăng ký có thu phí do backend điều phối; gói và thu phí cùng thành công, không để lại thành công một phần. |
| DEC-04 | Hủy gói có hiệu lực cuối chu kỳ hiện tại. |
| DEC-05 | Không thấu chi; thiếu tiền không đổi số dư, không ghi giao dịch tài chính thành công và trả lỗi. |
| DEC-06 | Soft delete/trạng thái lần đăng ký và tài khoản hết hạn; giữ lịch sử và định nghĩa gói. |
| DEC-07 | Thêm job tổng hợp tiêu dùng hàng ngày riêng. |
| DEC-08 | Tổng bản ghi nghiệp vụ tối thiểu 500.000, chuẩn bị trước đo tải. |
| DEC-09 | Cùng chạy ≥30 TPS đăng ký và ≥30 TPS trừ tiền, tổng ≥60 TPS thành công mới; báo riêng lỗi/timeout/retry. |
| DEC-10 | 50% tổng request trừ tiền vào nhiều nhóm cặp thuê bao–tài khoản có tranh chấp đồng thời. |
| DEC-11 | Đối soát từng yêu cầu/giao dịch và số dư; kiểm tra retry, đủ tiền, thiếu tiền và timeout. |

## 7. Các vấn đề còn mở và bước tiếp theo

OQ-08 đã Resolved. OQ-01–OQ-07 và OQ-09–OQ-15 vẫn Open ở phần chi tiết, dù một số định hướng đã được duyệt. [Clarification log, mục 6](clarification-log.md#6-trạng-thái-và-kế-hoạch-xử-lý-oq) là danh sách chính về phần còn thiếu, người phụ trách và hạn chốt; không tự xem các đề xuất cũ là quyết định.

Chủ dự án phụ trách quyết định; trợ lý tổng hợp và đề xuất. Ưu tiên ở SRS UC01 là OQ-05, OQ-12, OQ-15. Các quy tắc gói, tiền, báo cáo và tải được chốt trước khi thiết kế/triển khai thành phần tương ứng. Việc chọn nhóm tranh chấp không tự quyết định số nhóm; việc có thu phí không tự quyết định mức phí/tài khoản thu.

Phase 0 hoàn thành theo Q14=A: ba tài liệu nhất quán, quyết định rõ và các chi tiết còn mở có kế hoạch. Phase 1 chưa bắt đầu. Xem [roadmap](../ROADMAP.md) để theo dõi bước tiếp theo.

## 8. Lịch sử cập nhật

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 1.0 | Trước duyệt Phase 0 | Tổng quan ban đầu, còn các vấn đề cần làm rõ. |
| 1.1 | 14/10/2026 | Đồng bộ 14 lựa chọn: thu phí đăng ký, job báo cáo, 500K tổng, tải hỗn hợp 60 TPS, phân nhóm 50%; xác nhận thẩm quyền và đóng Phase 0. |
