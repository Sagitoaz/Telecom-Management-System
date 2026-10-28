> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](../00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Cấu trúc module và hợp đồng trách nhiệm

**Mã tài liệu:** DOC-ARCH-MOD-001 · **Cập nhật:** 26/10/2026 · **Trạng thái:** REVIEW.

Kiến trúc một ứng dụng chia module đã được chọn tại M3. Bản này mô tả trách nhiệm và giao tiếp dự kiến cho toàn bộ backend; tên interface và tổ chức Java chi tiết vẫn cần review trước triển khai. Package gốc dự kiến là `com.telecom`; module quản lý gói dùng tên `catalog` và `subscription`, tránh từ khóa Java `package`.

## 1. Phân chia trách nhiệm

| Module | Dữ liệu/trách nhiệm chính | Năng lực cung cấp | Giới hạn |
|---|---|---|---|
| `customer` | Customer và giấy tờ đã xác minh | Đối chiếu hồ sơ chuẩn hóa, phân giải Customer theo giấy tờ/chứng cứ hợp lệ | Không tự merge Customer, sửa hồ sơ khác biệt hoặc thực hiện KYC |
| `inventory` | Kho số và cơ chế điều phối cấp số | Tra số khả dụng, cấp số chọn/tự động, tiếp nhận tín hiệu bổ sung kho | GET không giữ chỗ; không tự tái cấp số từng dùng |
| `activation` | Request và luồng UC01 | Tiếp nhận, tra kết quả, xử lý WAITING, khôi phục request | Giữ Hybrid; không biến chọn số lỗi thành WAITING |
| `operations` | Registry UC02–UC05, fingerprint, kết quả và tombstone | Nhận quyền xử lý theo caller/operation/requestId, replay, chốt và tra kết quả | Không giữ định danh chỉ ở Redis; không coi timeout là thành công |
| `account` | Account, số dư và sổ giao dịch bất biến | Cộng/trừ tiền, kiểm hạn và thu phí gói trong transaction của Use Case | Không fallback/gộp tài khoản, số dư âm hoặc commit phí riêng với UC02 |
| `catalog` | Định nghĩa/phiên bản gói và chính sách khách cũ | Đọc danh mục, cung cấp giá/số ngày/chính sách hợp lệ khi thu phí | Không tin giá cache để thu; không đồng nhất ngừng bán với cấm mọi gia hạn |
| `subscription` | Đăng ký gói ổn định, version, kỳ đã trả và ý định hủy | Đăng ký/mua kỳ, hủy, gia hạn, xử lý hết quyền dùng | Không coi coverageEnd là cuối kỳ hiện tại trong mọi trường hợp |
| `reporting` | Lượt truy vấn, tập kết quả cố định, kỳ báo cáo và đóng góp nguồn | Tra báo cáo, phân trang, chốt ngày và ghi điều chỉnh muộn | Không sửa số dư/ledger hoặc sửa ngầm ngày đã chốt |
| `scheduling` | Lần chạy job, nhận việc và adapter theo lịch/chạy tay | Kích hoạt worker gia hạn, JOB-01, JOB-02 và khôi phục UC01 | Không sao chép quy tắc thu phí hoặc sinh nghiệp vụ mới chỉ vì retry |
| `security` | Caller đã xác minh, scope và ranh giới chứng cứ tin cậy | Kiểm danh tính, quyền thao tác, sở hữu request và snapshot | Không phát JWT nội bộ hoặc tin caller tự khai |
| `integration` | Adapter RabbitMQ, Redis và JWKS | Phát tín hiệu sau commit, cache danh mục, nhận tín hiệu và lấy khóa xác minh | Không gọi mạng trong lúc giữ khóa DB dài hạn; không trở thành nguồn quyết định tiền |

Subscriber được tạo trong luồng activation và được các module khác tham chiếu qua định danh/khả năng đọc đã quy định. Khi triển khai, cần xác định rõ interface đọc Subscriber; không để một module sửa trực tiếp bảng của module khác qua repository công khai tùy ý.

## 2. Hướng phụ thuộc

Hướng gọi chính là: **API hoặc scheduler → dịch vụ ứng dụng → lõi nghiệp vụ và các cổng truy cập → adapter lưu trữ/tích hợp**. Controller kiểm định dạng và lấy caller đã xác minh; dịch vụ ứng dụng kiểm quy tắc nghiệp vụ và điều phối transaction. Repository thực hiện thao tác dữ liệu được yêu cầu; không tự suy ra kết quả nghiệp vụ từ một lỗi SQL không được phân loại.

Các module trong cùng ứng dụng gọi interface Java đã quy định. Đăng ký gói gọi khả năng thu phí của `account` trong cùng transaction, không gọi HTTP sang UC05 rồi commit riêng. Reporting đọc dữ liệu đã commit theo hợp đồng nguồn, không gọi ngược đường thu tiền. Scheduling chỉ kích hoạt các dịch vụ chung, không có bản sao riêng của thuật toán gia hạn cho JOB-01.

Registry request là năng lực dùng chung có phạm vi caller/thao tác, không phải biến trạng thái toàn cục. Registry UC01 tách riêng vì cần `WAITING`/`EXPIRED`; registry UC02–UC05 có `PROCESSING`/`SUCCEEDED`/`FAILED` và tombstone.

## 3. Transaction và thứ tự khóa

Một transaction nghiệp vụ dùng cùng MySQL transaction manager. Phối hợp JPA/JDBC phải bảo đảm cùng connection và đồng bộ trạng thái entity bằng cách flush/refresh/clear phù hợp; không dựa vào object số dư cũ sau cập nhật SQL trực tiếp.

Thứ tự dự kiến cho đường có tiền là request → tài khoản MAIN/tài khoản được chỉ định → bản ghi đăng ký gói/slot → kỳ gói. Slot bảo vệ cặp thuê bao–gói ngay cả trước lần mua đầu tiên, giúp hai request cùng `expectedSubscriptionVersion=0` không thu trùng. Chi tiết khóa/index/retry nằm trong [thiết kế đồng thời](../04-design/concurrency-and-jobs.md), vẫn là phần cần review và test MySQL.

Đường hủy có thể chỉ khóa slot khi không quay lại lấy khóa tài khoản sau đó. Không tạo thứ tự ngược slot → account trong một nhánh khác khiến deadlock với đường thu phí. Worker và JOB-01 cùng dùng dịch vụ gia hạn/hết hạn và khóa theo kỳ nghiệp vụ; `runId` không thay khóa chống thu trùng.

## 4. Trạng thái và hợp đồng trao đổi

| Khái niệm | Ý nghĩa cần giữ |
|---|---|
| Request `PROCESSING` | Đang xử lý hoặc cần xác định lại kết quả; chưa phải thành công |
| Activation `WAITING` | Chờ số tự động có sự đồng ý; chưa tạo thành công thuê bao |
| UC03 `SUCCEEDED` | Đã nhận hợp lệ ý định hủy; quyền dùng có thể còn tới coverageEnd |
| Subscription `PENDING_CANCEL` | Chặn mua thêm/gia hạn, giữ quyền trong thời gian đã trả |
| Subscription `CANCELLED` | Đã hết quyền dùng do yêu cầu hủy có hiệu lực |
| Subscription `EXPIRED` | Hết hạn theo vòng đời, khác lý do khách hủy |
| `UNKNOWN` phía caller | Chưa quan sát được kết quả sau timeout; cần tra cùng mã request |

DTO, lỗi và tên trường công khai lấy từ [OpenAPI](../04-design/openapi.yaml), không tự serialize entity làm hợp đồng. Một thay đổi interface ảnh hưởng nghiệp vụ phải được đồng bộ vào UC, UML, schema/API và kịch bản kiểm thử. Không đưa thông tin bí mật hoặc toàn bộ hồ sơ vào event/log chỉ vì interface nội bộ.

## 5. Kiểm tra kiến trúc khi triển khai

Cần kiểm tra không có phụ thuộc vòng, gọi HTTP nội bộ để chia đôi transaction, repository vượt quyền sở hữu hoặc logic thu phí trùng giữa worker và job. Các ca rollback phải chứng minh số dư, ledger, cycle và outcome không bị chốt riêng. Hợp đồng caller phải được truyền tường minh, tránh ngữ cảnh mutable dùng chung giữa luồng.

[Module UML](module-structure.puml) mô tả hướng cộng tác; [Design Class](../04-design/design-class-diagrams/backend-design-class.puml) mô tả class/interface dự kiến. Hai sơ đồ không phải mã Java đã tồn tại hoặc bằng chứng module đã hoàn thành.
