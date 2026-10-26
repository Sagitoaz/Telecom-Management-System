# Topology local và bộ dịch vụ bàn giao

**Mã tài liệu:** DOC-ARCH-TOPO-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

Bộ dịch vụ tại nhà theo Q18=A gồm MySQL, RabbitMQ, Redis và Keycloak chạy bằng Compose. Backend chạy từ IDE khi phát triển hoặc container khi bàn giao. Đây là topology mục tiêu; chưa có bằng chứng Compose/Dockerfile hoặc dịch vụ đã chạy trong đợt tài liệu này.

## 1. Thành phần và kết nối

| Service dự kiến | Vai trò | Backend từ IDE trên host | Backend trong container |
|---|---|---|---|
| `mysql` | DB nghiệp vụ MySQL 8.0.46/InnoDB, volume bền vững | `localhost` và cổng dev được publish | `mysql:3306` qua mạng nội bộ |
| `rabbitmq` | Tín hiệu kho số được bổ sung | Cổng AMQP được publish cho máy dev | `rabbitmq:5672` |
| `redis` | Cache danh mục đọc | Cổng Redis được publish cho máy dev | `redis:6379` |
| `identity-provider` | Keycloak ngoài ranh giới Telecom, cấp JWT/JWKS | Issuer/JWKS truy cập được từ host | Hostname/issuer nhất quán với token và cấu hình backend |
| `telecom-backend` | REST API, scheduler và worker | JVM chạy từ IDE | Image backend phục vụ bàn giao |
| k6 và reconciler | Phát tải và đối soát | Công cụ kiểm thử có quyền đúng mục đích | Có thể chạy riêng trong mạng test; không là dependency bắt buộc của API |

Trong container, `localhost` trỏ chính container đó, không trỏ sang MySQL hoặc IdP khác. Issuer là giá trị cần khớp khi xác minh token; không sửa chuỗi issuer tùy tiện chỉ để JWKS truy cập được. Cần thiết kế hostname/DNS và URL cho cả bên cấp token, bên gọi và backend.

Dữ liệu Keycloak và schema nghiệp vụ tách trách nhiệm; Flyway của Telecom không quản lý schema nội bộ IdP. Cách bố trí persistence của Keycloak, tên volume, cổng dev và các image patch/digest còn là cấu hình triển khai cần review.

## 2. Mạng, secret và dữ liệu

Cổng dev chỉ bind trong phạm vi cần dùng, ưu tiên máy local; không công khai DB/cache/broker hoặc trang quản trị IdP ngoài nhu cầu đã xác nhận. Secret được cung cấp ngoài Git. Tài khoản DB ứng dụng không mặc nhiên có quyền quản trị schema; quyền migration và đối soát được cấp theo mục đích.

Volume nghiệp vụ phải giữ dữ liệu qua restart. Không xóa volume để né lỗi migration. Thử migration mới dùng DB dev đích rõ ràng; thao tác hủy dữ liệu/restore đè cần phạm vi và ủy quyền riêng. Dataset demo là dữ liệu giả, có nguồn seed và trạng thái ban đầu để đối soát.

## 3. Khởi động và hành vi khi dịch vụ lỗi

Health check từng service và retry kết nối phải phản ánh readiness thực. Phụ thuộc thứ tự startup không đủ bảo đảm schema hoặc JWT dùng được. Backend chỉ báo sẵn sàng theo tiêu chí phục vụ đã định nghĩa và sau khi kiểm schema cần thiết.

| Sự cố | Hành vi cần giữ |
|---|---|
| MySQL ngừng hoạt động | Không báo nghiệp vụ thành công khi chưa xác định được kết quả; tra cứu/retry cùng requestId sau phục hồi |
| RabbitMQ ngừng hoặc mất thông điệp | Quét DB khôi phục UC01 vẫn tìm request chờ; không mất WAITING |
| Redis ngừng hoặc cache cũ | Đọc DB; thu phí luôn lấy dữ liệu có thẩm quyền và lưu snapshot kỳ |
| IdP/JWKS không sẵn sàng | Chỉ chấp nhận token kiểm chứng được đầy đủ; không bỏ xác thực |
| Backend restart | Request, số dư, cycle, cancellation intent và checkpoint không mất; retry không thu/cấp số lại |

## 4. Điều kiện nghiệm thu topology

Cần lưu bằng chứng Compose parse được, service ready, migration MySQL từ DB trống, API/JWT smoke, kiểm kết nối host/container và restart giữ dữ liệu. Thử lỗi broker/cache, xoay khóa IdP và restore DB phải có kết quả thực, không suy từ sơ đồ.

MySQL được pin 8.0.46 theo quyết định; phiên bản RabbitMQ/Redis/Keycloak được pin sau khi lựa chọn và kiểm tương thích. Cấu hình cuối phải được đồng bộ vào [runtime](runtime-profiles.md), [C4](c4-diagrams/context-and-container.puml) và [hướng dẫn bàn giao](../06-deployment/deployment-guide.md).
