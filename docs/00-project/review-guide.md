# Hướng dẫn rà soát thiết kế backend

Đọc [chỉ mục tài liệu](../README.md), sau đó [bản tiếp nhận](decision-receipt-20261026.md) để phân biệt quyết định nghiệp vụ đã chọn với thiết kế kỹ thuật còn REVIEW. Không cần điền lại phiếu 16.

Để nghiệm thu phần triển khai UC01, dùng [checklist M4](../04-design/m4-review-checklist.md): delta schema MySQL, hợp đồng API, design class, cơ chế khóa/recovery và cấu hình. Có thể duyệt riêng phạm vi UC01, không cần chờ mọi UC sau.

Đối chiếu từng tình huống theo thứ tự: yêu cầu SRS → UC và AC → sequence/state → schema/API/transaction → kịch bản test. Chú ý mốc thời gian, tác động tài chính khi rollback/retry, quyền caller và kết quả khi mất phản hồi. [64 ca biên](../07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md) là kế hoạch cần chạy, không phải báo cáo đã đạt.

Chỉ nghiệm thu checkpoint khi có xác nhận rõ và bằng chứng phù hợp. Kiểm tra link/UML/OpenAPI chỉ chứng minh cấu trúc tài liệu; M5–M8 cần build, migration, API, race, tải và bàn giao thực tế.
