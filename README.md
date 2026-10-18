# Telecom Management System

Project Java su dung Maven, dong goi thanh JAR.

## Yeu cau

- JDK 21 tro len (may hien tai su dung JDK 25).
- Maven 3.9 tro len.

## Build va chay

Chay tu thu muc goc cua project:

```powershell
mvn clean package
java -jar target/telecom-management-system-1.0-SNAPSHOT.jar
```

Chay kiem thu khi da them test:

```powershell
mvn test
```

## Cau truc

```text
src/main/java/       Ma nguon Java (entry point: com.telecom.Main)
src/main/resources/  Tai nguyen va cau hinh ung dung
src/test/java/       Ma nguon kiem thu
src/test/resources/  Tai nguyen kiem thu
pom.xml              Cau hinh Maven va dependencies
```

## IntelliJ IDEA

Mo project hoac mo `pom.xml` bang **Open as Project**. Neu project da mo,
nhan chuot phai vao `pom.xml`, chon **Add as Maven Project**, roi reload Maven.
Dat Project SDK thanh JDK 21 tro len. Co the chay truc tiep lop `com.telecom.Main`.

Them thu vien can dung vao muc `<dependencies>` trong `pom.xml`.
Project da khai bao Spring Boot, JPA/JDBC, Flyway, PostgreSQL, Actuator, Springdoc, Lombok va cac thu vien kiem thu/Testcontainers trong pom.xml. Nghiep vu backend va cac bai test chua duoc trien khai.

## Tài liệu và tiến độ

**Phase 0 đã hoàn thành ngày 14/10/2026**, được chủ dự án có quyền nghiệm thu duyệt; Phase 1 đang rà soát SRS/UC01, M1 chờ duyệt.

- [Lộ trình dự án](docs/ROADMAP.md)
- [Tổng quan](docs/00-project/overview.md)
- [Phạm vi và thuật ngữ](docs/00-project/scope-and-glossary.md)
- [Quyết định đã duyệt và câu hỏi còn mở](docs/00-project/clarification-log.md)
- [Đề bài gốc](docs/requirement.md)

Baseline gồm đăng ký gói có thu phí do backend điều phối, hủy cuối chu kỳ, soft delete dữ liệu hết hạn, API báo cáo và job tổng hợp tiêu dùng hàng ngày riêng. Mục tiêu dữ liệu/tải: tổng ≥500.000 bản ghi nghiệp vụ trước đo; đăng ký và trừ tiền cùng đạt ≥30 TPS mỗi loại (tổng ≥60 TPS); 50% request trừ tiền chia vào nhiều nhóm tài khoản có tranh chấp đồng thời và có tool kiểm chứng đúng/đủ. Đây là mục tiêu đã duyệt, chưa phải kết quả triển khai/kiểm thử.

Chi tiết còn mở có người phụ trách và mốc chốt trong clarification log. Bước tiếp theo là chốt yêu cầu còn mở và nghiệm thu M1; Phase 2–4 là DRAFT, chưa đủ điều kiện code.


Tài liệu nằm trong thư mục docs/ duy nhất. Xem [chỉ mục](docs/README.md) và [phiếu quyết định còn thiếu](docs/00-project/decision-questionnaire.md). Hai phiếu review cũ đã xóa; nội dung quyết định và lịch sử nằm đầy đủ trong clarification log.


**Bản chờ duyệt hiện tại:** [Hướng dẫn đọc](docs/00-project/review-guide.md), SRS0.6 và UC01 0.5; đã tiếp nhận Q01–Q24, Q25 chưa chọn. Stack đã chọn gồm RabbitMQ/Redis theo ADR-001; chưa triển khai thêm dependency hay code.
