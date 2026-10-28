> **ĐÃ CHỐT — 28/10/2026, Chủ dự án phê duyệt theo [CR-DOC-004](00-project/document-approval-20261028.md).** Nhãn REVIEW/DRAFT/chờ duyệt bên dưới là trạng thái trước phê duyệt; các phương án đã bị thay hoặc không được chọn vẫn giữ hiệu lực lịch sử. Kết quả runtime chưa được nghiệm thu.

# Chỉ mục sơ đồ nghiệp vụ, UML và kiến trúc

**REVIEW — 26/10/2026.** Có 41 nguồn PlantUML, sinh 42 SVG và 42 PNG. Đọc ảnh PNG trong Markdown để tương thích trình xem; mở SVG để phóng to và PlantUML để sửa nguồn. [Báo cáo theo luồng nghiệp vụ](BAO-CAO-TONG-HOP-DU-AN.md) nhúng đầy đủ các hình.

Activity Diagram theo nhánh quyết định khác Sequence theo tương tác; Class Diagram khác ERD. Ba class view nghiệp vụ tách từ Domain để đọc rõ; Design Class mô tả controller/service/core/port ở mức kỹ thuật.

| Nhóm / sơ đồ | Xem nhanh | Phóng to | Nguồn sửa |
|---|---|---|---|
| Activity — Luồng nghiệp vụ xuyên suốt | [PNG](02-analysis/activity-diagrams/00-end-to-end.png) | [SVG](02-analysis/activity-diagrams/00-end-to-end.svg) | [PlantUML](02-analysis/activity-diagrams/00-end-to-end.puml) |
| Activity — UC01 — Cấp số, chờ và thất bại | [PNG](02-analysis/activity-diagrams/01-activation.png) | [SVG](02-analysis/activity-diagrams/01-activation.svg) | [PlantUML](02-analysis/activity-diagrams/01-activation.puml) |
| Activity — UC02 — Mua gói và mua nối | [PNG](02-analysis/activity-diagrams/02-subscription.png) | [SVG](02-analysis/activity-diagrams/02-subscription.svg) | [PlantUML](02-analysis/activity-diagrams/02-subscription.puml) |
| Activity — UC03 — Nhận hủy, receipt và mất phản hồi | [PNG](02-analysis/activity-diagrams/03-cancellation.png) | [SVG](02-analysis/activity-diagrams/03-cancellation.svg) | [PlantUML](02-analysis/activity-diagrams/03-cancellation.puml) |
| Activity — UC04 — Cộng tiền đúng accountId | [PNG](02-analysis/activity-diagrams/04-credit.png) | [SVG](02-analysis/activity-diagrams/04-credit.svg) | [PlantUML](02-analysis/activity-diagrams/04-credit.puml) |
| Activity — UC05 — Tiêu dùng, số dư và kỳ hiệu lực | [PNG](02-analysis/activity-diagrams/05-debit.png) | [SVG](02-analysis/activity-diagrams/05-debit.svg) | [PlantUML](02-analysis/activity-diagrams/05-debit.puml) |
| Activity — Worker gia hạn — một target, một phí | [PNG](02-analysis/activity-diagrams/06-renewal.png) | [SVG](02-analysis/activity-diagrams/06-renewal.svg) | [PlantUML](02-analysis/activity-diagrams/06-renewal.puml) |
| Activity — JOB-01 — Hết hạn, hủy và rà soát | [PNG](02-analysis/activity-diagrams/07-expiration.png) | [SVG](02-analysis/activity-diagrams/07-expiration.svg) | [PlantUML](02-analysis/activity-diagrams/07-expiration.puml) |
| Activity — UC06 — Tạo tập kết quả và đọc nhiều trang | [PNG](02-analysis/activity-diagrams/08-query.png) | [SVG](02-analysis/activity-diagrams/08-query.svg) | [PlantUML](02-analysis/activity-diagrams/08-query.puml) |
| Activity — JOB-02 — Chốt nguồn và điều chỉnh muộn | [PNG](02-analysis/activity-diagrams/09-daily-report.png) | [SVG](02-analysis/activity-diagrams/09-daily-report.svg) | [PlantUML](02-analysis/activity-diagrams/09-daily-report.puml) |
| Activity — Luồng chung — Gửi lại, lỗi nghiệp vụ và mất phản hồi | [PNG](02-analysis/activity-diagrams/10-common-outcomes.png) | [SVG](02-analysis/activity-diagrams/10-common-outcomes.svg) | [PlantUML](02-analysis/activity-diagrams/10-common-outcomes.puml) |
| Class — Class Diagram nghiệp vụ — Khách hàng, thuê bao và tài khoản | [PNG](02-analysis/class-diagrams/customer-subscriber-account.png) | [SVG](02-analysis/class-diagrams/customer-subscriber-account.svg) | [PlantUML](02-analysis/class-diagrams/customer-subscriber-account.puml) |
| Class — Class Diagram nghiệp vụ — Gói, kỳ đã trả và ý định hủy | [PNG](02-analysis/class-diagrams/package-cycle-cancellation.png) | [SVG](02-analysis/class-diagrams/package-cycle-cancellation.svg) | [PlantUML](02-analysis/class-diagrams/package-cycle-cancellation.puml) |
| Class — Class Diagram nghiệp vụ — Yêu cầu và báo cáo | [PNG](02-analysis/class-diagrams/requests-and-reporting.png) | [SVG](02-analysis/class-diagrams/requests-and-reporting.svg) | [PlantUML](02-analysis/class-diagrams/requests-and-reporting.puml) |
| Domain nghiệp vụ | [PNG](02-analysis/domain-model.png) | [SVG](02-analysis/domain-model.svg) | [PlantUML](02-analysis/domain-model.puml) |
| JOB-01 hết hạn/hủy/recovery | [PNG](02-analysis/sequence-diagrams/job01-expiration.png) | [SVG](02-analysis/sequence-diagrams/job01-expiration.svg) | [PlantUML](02-analysis/sequence-diagrams/job01-expiration.puml) |
| JOB-02 tổng hợp | [PNG](02-analysis/sequence-diagrams/job02-daily-aggregation.png) | [SVG](02-analysis/sequence-diagrams/job02-daily-aggregation.svg) | [PlantUML](02-analysis/sequence-diagrams/job02-daily-aggregation.puml) |
| Worker gia hạn | [PNG](02-analysis/sequence-diagrams/renewal-worker.png) | [SVG](02-analysis/sequence-diagrams/renewal-worker.svg) | [PlantUML](02-analysis/sequence-diagrams/renewal-worker.puml) |
| Đấu nối và recovery | [PNG](02-analysis/sequence-diagrams/uc01-activation.png) | [SVG](02-analysis/sequence-diagrams/uc01-activation.svg) | [PlantUML](02-analysis/sequence-diagrams/uc01-activation.puml) |
| Đăng ký và mua nối gói | [PNG](02-analysis/sequence-diagrams/uc02-package-subscription.png) | [SVG](02-analysis/sequence-diagrams/uc02-package-subscription.svg) | [PlantUML](02-analysis/sequence-diagrams/uc02-package-subscription.puml) |
| Tiếp nhận hủy và mất phản hồi | [PNG](02-analysis/sequence-diagrams/uc03-package-cancellation.png) | [SVG](02-analysis/sequence-diagrams/uc03-package-cancellation.svg) | [PlantUML](02-analysis/sequence-diagrams/uc03-package-cancellation.puml) |
| Cộng tiền | [PNG](02-analysis/sequence-diagrams/uc04-account-credit.png) | [SVG](02-analysis/sequence-diagrams/uc04-account-credit.svg) | [PlantUML](02-analysis/sequence-diagrams/uc04-account-credit.puml) |
| Trừ tiền độc lập | [PNG](02-analysis/sequence-diagrams/uc05-account-debit.png) | [SVG](02-analysis/sequence-diagrams/uc05-account-debit.svg) | [PlantUML](02-analysis/sequence-diagrams/uc05-account-debit.puml) |
| Tra cứu snapshot | [PNG](02-analysis/sequence-diagrams/uc06-consumption-query.png) | [SVG](02-analysis/sequence-diagrams/uc06-consumption-query.svg) | [PlantUML](02-analysis/sequence-diagrams/uc06-consumption-query.puml) |
| Vòng đời tài khoản | [PNG](02-analysis/state-diagrams/account.png) | [SVG](02-analysis/state-diagrams/account.svg) | [PlantUML](02-analysis/state-diagrams/account.puml) |
| Yêu cầu đấu nối | [PNG](02-analysis/state-diagrams/activation-request.png) | [SVG](02-analysis/state-diagrams/activation-request.svg) | [PlantUML](02-analysis/state-diagrams/activation-request.puml) |
| Ý định hủy | [PNG](02-analysis/state-diagrams/cancellation-intent.png) | [SVG](02-analysis/state-diagrams/cancellation-intent.svg) | [PlantUML](02-analysis/state-diagrams/cancellation-intent.puml) |
| Yêu cầu thao tác | [PNG](02-analysis/state-diagrams/operation-request.png) | [SVG](02-analysis/state-diagrams/operation-request.svg) | [PlantUML](02-analysis/state-diagrams/operation-request.puml) |
| Đăng ký gói | [PNG](02-analysis/state-diagrams/package-subscription.png) | [SVG](02-analysis/state-diagrams/package-subscription.svg) | [PlantUML](02-analysis/state-diagrams/package-subscription.puml) |
| Kỳ báo cáo | [PNG](02-analysis/state-diagrams/report-period.png) | [SVG](02-analysis/state-diagrams/report-period.svg) | [PlantUML](02-analysis/state-diagrams/report-period.puml) |
| Use Case UC01 Hybrid | [PNG](02-analysis/uc01-use-case-diagram.png) | [SVG](02-analysis/uc01-use-case-diagram.svg) | [PlantUML](02-analysis/uc01-use-case-diagram.puml) |
| Use Case toàn hệ thống | [PNG](02-analysis/use-case-diagram.png) | [SVG](02-analysis/use-case-diagram.svg) | [PlantUML](02-analysis/use-case-diagram.puml) |
| C4 Context và Container | [PNG](03-architecture/c4-diagrams/context-and-container.png) | [SVG](03-architecture/c4-diagrams/context-and-container.svg) | [PlantUML](03-architecture/c4-diagrams/context-and-container.puml) |
| C4 Container | [PNG](03-architecture/c4-diagrams/context-and-container_001.png) | [SVG](03-architecture/c4-diagrams/context-and-container_001.svg) | [PlantUML](03-architecture/c4-diagrams/context-and-container.puml) |
| Phụ thuộc module | [PNG](03-architecture/module-structure.png) | [SVG](03-architecture/module-structure.svg) | [PlantUML](03-architecture/module-structure.puml) |
| Design Class toàn backend — hình tổng quan, cần phóng to | [PNG](04-design/design-class-diagrams/backend-design-class.png) | [SVG](04-design/design-class-diagrams/backend-design-class.svg) | [PlantUML](04-design/design-class-diagrams/backend-design-class.puml) |
| Design Class điều phối job | [PNG](04-design/design-class-diagrams/jobs-design-class.png) | [SVG](04-design/design-class-diagrams/jobs-design-class.svg) | [PlantUML](04-design/design-class-diagrams/jobs-design-class.puml) |
| Design Class tiền và ledger | [PNG](04-design/design-class-diagrams/money-design-class.png) | [SVG](04-design/design-class-diagrams/money-design-class.svg) | [PlantUML](04-design/design-class-diagrams/money-design-class.puml) |
| Design Class snapshot và tổng hợp | [PNG](04-design/design-class-diagrams/report-design-class.png) | [SVG](04-design/design-class-diagrams/report-design-class.svg) | [PlantUML](04-design/design-class-diagrams/report-design-class.puml) |
| Design Class kỳ gói và hủy | [PNG](04-design/design-class-diagrams/subscription-design-class.png) | [SVG](04-design/design-class-diagrams/subscription-design-class.svg) | [PlantUML](04-design/design-class-diagrams/subscription-design-class.puml) |
| Design Class UC01 | [PNG](04-design/design-class-diagrams/uc01-design-class.png) | [SVG](04-design/design-class-diagrams/uc01-design-class.svg) | [PlantUML](04-design/design-class-diagrams/uc01-design-class.puml) |
| ERD toàn backend | [PNG](04-design/erd.png) | [SVG](04-design/erd.svg) | [PlantUML](04-design/erd.puml) |

Nguồn và ảnh đã được đối chiếu trong lượt kiểm ngày 28/10/2026, ghi tại [verification](verification.md). Nguồn thay đổi phải xuất lại cả PNG/SVG; không dùng kiểm tra file tồn tại thay cho kiểm tra hiển thị ảnh.
