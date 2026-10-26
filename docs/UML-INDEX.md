# Chỉ mục sơ đồ UML và kiến trúc

**REVIEW — 26/10/2026.** Mở SVG để xem hình và phóng to; mở PlantUML để sửa nguồn. Có 27 nguồn, sinh 28 SVG do C4 chứa hai hình. Trạng thái render/hash xem [verification](verification.md). Các sơ đồ mô tả thiết kế, chưa chứng minh runtime đúng.

| Sơ đồ | Nguồn sửa | Hình xem |
|---|---|---|
| Domain nghiệp vụ | [PlantUML](02-analysis/domain-model.puml) | [SVG](02-analysis/domain-model.svg) |
| JOB-01 hết hạn/hủy/recovery | [PlantUML](02-analysis/sequence-diagrams/job01-expiration.puml) | [SVG](02-analysis/sequence-diagrams/job01-expiration.svg) |
| JOB-02 tổng hợp | [PlantUML](02-analysis/sequence-diagrams/job02-daily-aggregation.puml) | [SVG](02-analysis/sequence-diagrams/job02-daily-aggregation.svg) |
| Worker gia hạn | [PlantUML](02-analysis/sequence-diagrams/renewal-worker.puml) | [SVG](02-analysis/sequence-diagrams/renewal-worker.svg) |
| Đấu nối và recovery | [PlantUML](02-analysis/sequence-diagrams/uc01-activation.puml) | [SVG](02-analysis/sequence-diagrams/uc01-activation.svg) |
| Đăng ký và mua nối gói | [PlantUML](02-analysis/sequence-diagrams/uc02-package-subscription.puml) | [SVG](02-analysis/sequence-diagrams/uc02-package-subscription.svg) |
| Tiếp nhận hủy và mất phản hồi | [PlantUML](02-analysis/sequence-diagrams/uc03-package-cancellation.puml) | [SVG](02-analysis/sequence-diagrams/uc03-package-cancellation.svg) |
| Cộng tiền | [PlantUML](02-analysis/sequence-diagrams/uc04-account-credit.puml) | [SVG](02-analysis/sequence-diagrams/uc04-account-credit.svg) |
| Trừ tiền độc lập | [PlantUML](02-analysis/sequence-diagrams/uc05-account-debit.puml) | [SVG](02-analysis/sequence-diagrams/uc05-account-debit.svg) |
| Tra cứu snapshot | [PlantUML](02-analysis/sequence-diagrams/uc06-consumption-query.puml) | [SVG](02-analysis/sequence-diagrams/uc06-consumption-query.svg) |
| Vòng đời tài khoản | [PlantUML](02-analysis/state-diagrams/account.puml) | [SVG](02-analysis/state-diagrams/account.svg) |
| Yêu cầu đấu nối | [PlantUML](02-analysis/state-diagrams/activation-request.puml) | [SVG](02-analysis/state-diagrams/activation-request.svg) |
| Ý định hủy | [PlantUML](02-analysis/state-diagrams/cancellation-intent.puml) | [SVG](02-analysis/state-diagrams/cancellation-intent.svg) |
| Yêu cầu thao tác | [PlantUML](02-analysis/state-diagrams/operation-request.puml) | [SVG](02-analysis/state-diagrams/operation-request.svg) |
| Đăng ký gói | [PlantUML](02-analysis/state-diagrams/package-subscription.puml) | [SVG](02-analysis/state-diagrams/package-subscription.svg) |
| Kỳ báo cáo | [PlantUML](02-analysis/state-diagrams/report-period.puml) | [SVG](02-analysis/state-diagrams/report-period.svg) |
| Use Case UC01 Hybrid | [PlantUML](02-analysis/uc01-use-case-diagram.puml) | [SVG](02-analysis/uc01-use-case-diagram.svg) |
| Use Case toàn hệ thống | [PlantUML](02-analysis/use-case-diagram.puml) | [SVG](02-analysis/use-case-diagram.svg) |
| C4 Context và Container | [PlantUML](03-architecture/c4-diagrams/context-and-container.puml) | [Context](03-architecture/c4-diagrams/context-and-container.svg) · [Container](03-architecture/c4-diagrams/context-and-container_001.svg) |
| Phụ thuộc module | [PlantUML](03-architecture/module-structure.puml) | [SVG](03-architecture/module-structure.svg) |
| Design Class toàn backend — hình tổng quan, cần phóng to | [PlantUML](04-design/design-class-diagrams/backend-design-class.puml) | [SVG](04-design/design-class-diagrams/backend-design-class.svg) |
| Design Class điều phối job | [PlantUML](04-design/design-class-diagrams/jobs-design-class.puml) | [SVG](04-design/design-class-diagrams/jobs-design-class.svg) |
| Design Class tiền và ledger | [PlantUML](04-design/design-class-diagrams/money-design-class.puml) | [SVG](04-design/design-class-diagrams/money-design-class.svg) |
| Design Class snapshot và tổng hợp | [PlantUML](04-design/design-class-diagrams/report-design-class.puml) | [SVG](04-design/design-class-diagrams/report-design-class.svg) |
| Design Class kỳ gói và hủy | [PlantUML](04-design/design-class-diagrams/subscription-design-class.puml) | [SVG](04-design/design-class-diagrams/subscription-design-class.svg) |
| Design Class UC01 | [PlantUML](04-design/design-class-diagrams/uc01-design-class.puml) | [SVG](04-design/design-class-diagrams/uc01-design-class.svg) |
| ERD toàn backend | [PlantUML](04-design/erd.puml) | [SVG](04-design/erd.svg) |

Domain mô tả khái niệm nghiệp vụ; ERD mô tả dữ liệu vật lý; Design Class mô tả trách nhiệm và lời gọi. Chúng phục vụ mục đích khác nhau. Design Class tiền/gói/báo cáo/jobs được tách để đọc rõ hơn hình tổng quan lớn.
