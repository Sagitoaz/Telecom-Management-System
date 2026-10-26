# Transaction và cạnh tranh

**Cập nhật 26/10/2026 — trang chỉ dẫn.** READ COMMITTED không tự bảo đảm chống âm tiền: phải phối hợp khóa, version, unique và kiểm giờ DB sau các khóa cần thiết.

Đọc [đặc tả chuẩn đầy đủ](../04-design/concurrency-and-jobs.md). Nội dung chi tiết được duy trì ở nguồn chuẩn này để mọi thay đổi có một nơi cập nhật. Bản review cũ trước đồng bộ được giữ trong [archive](../00-project/history/2026-10-26-before-sync.zip).

Nguồn quyết định: [bản tiếp nhận phiếu](../00-project/decision-receipt-20261026.md), [clarification log](../00-project/clarification-log.md). Thiết kế mới là REVIEW; lựa chọn nghiệp vụ không tự nghiệm thu schema/API hoặc checkpoint.
