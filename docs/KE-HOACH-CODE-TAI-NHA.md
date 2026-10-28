# Ở nhà: từng bước cài công cụ, tạo file, viết Java và chạy Docker

**Bản 2 · 28/10/2026.** Hướng dẫn thực hành cho bạn chưa biết Docker/Docker Desktop/Spring. Các đoạn mã là mẫu để bạn tự tạo và chạy ở nhà; chưa được chạy xác nhận trong lượt viết tài liệu. Không có file ứng dụng/Compose/migration nào được tạo thật bởi tài liệu này.

**Thứ tự hiện hành:** P5.1 nền tảng → **P5.5 UC04 cộng tiền → P5.6 UC05 trừ tiền** và kiểm thử/đối soát → P5.2 UC01 → P5.3 UC02 → P5.4/5.8 hủy/gia hạn/hết hạn → P5.7/5.10 báo cáo/tổng hợp → P6/P7. Giữ nguyên mã task/UC, thay thứ tự theo [CR-IMPL-001, mục 22](00-project/clarification-log.md#cr-impl-001).

Đọc kèm [kế hoạch học tại công ty](KE-HOACH-HOC-TAI-CONG-TY.md). Không chờ hoàn tất UC01 hoặc API đăng ký gói mới được làm tiền: dùng fixtures phát triển/test có đủ quan hệ hợp lệ. **Không bỏ điều kiện kỳ gói của debit để làm shortcut.** M5 vẫn là nghiệm thu UC01; hoàn thành tiền trước không tự tick M5/M6/M7.

## 1. Đọc phần này trước khi làm

Bạn không cần hiểu toàn bộ framework trước. Làm một buổi nhỏ, giữ file/lệnh/log đã dùng, sáng hôm sau mở [file ở công ty](KE-HOACH-HOC-TAI-CONG-TY.md) đọc lại từng dòng. Không hiểu chỗ nào thì ghi đúng từ/annotation/property đó, không chỉ ghi “chưa hiểu Spring”.

**Trạng thái lúc viết:** repo mới có pom và Main mẫu, chưa có các file được hướng dẫn tạo bên dưới. Bắt đầu N01A; các N sau là bước tiếp nối, không phải mỗi N buộc xong trong một tối. Một buổi có thể chỉ xử lý lỗi cài đặt.

**Cách đọc hướng dẫn:** “Tạo” là file bạn cần tự tạo; “Sửa” là file đã có; tên trong dấu backtick là đường dẫn tính từ gốc repo. Chạy lệnh bằng PowerShell ở gốc repo trừ khi ghi khác. Thay placeholder mật khẩu bằng giá trị local của bạn; không đưa secret thật vào Git.

**Vị trí nhà có thể khác máy công ty.** Ví dụ lệnh đổi thư mục, thay bằng đường dẫn checkout trên máy nhà:

```powershell
Set-Location 'E:\WINDOW\Project\Telecom-Management-System'
Get-Location
```

Nguồn nghiệp vụ: [UC04](01-requirements/use-cases/UC04-Account-Credit.md), [UC05](01-requirements/use-cases/UC05-Account-Debit.md), [schema](04-design/database-schema.md), [API](04-design/openapi.yaml), [transaction](04-design/concurrency-and-jobs.md). Các ví dụ khởi động không thay contract tiền. Chưa mở API tiền trong các buổi ping đầu tiên.

## 2. N01–N09: từ máy chưa có công cụ tới module tiền

### N01 — Làm bốn buổi nhỏ A → B → C → D, không gộp một tối

#### N01A — Cài và kiểm công cụ: hôm nay chưa code Java

**Cần tạo/sửa:** chưa cần file dự án nào.

1. Kiểm Java/Maven hiện có bằng các lệnh dưới. Nếu thiếu thì cài JDK21 và Maven, đặt JDK của IDE/JAVA_HOME đúng; mở terminal mới sau cài. Không tải lại nếu đã có và đúng bản.
2. Ở máy nhà, theo [hướng dẫn cài Docker Desktop Windows](https://docs.docker.com/desktop/setup/install/windows-install/): kiểm yêu cầu Windows/WSL/virtualization phù hợp, cài Desktop, khởi động lại nếu installer yêu cầu, mở Desktop tới khi Engine sẵn sàng. Dùng Linux containers cho stack này.
3. Nếu chọn backend WSL2, kiểm WSL theo hướng dẫn chính thức; không tự yêu cầu phải cài một bản Ubuntu riêng nếu cấu hình Desktop hiện tại không cần nó.

```powershell
java -version
mvn -version
docker --version
docker version
docker compose version
```

**Được sang B khi:** Java là21; Maven nhận đúng JDK; `docker version` có Client và Server, Compose trả version. Chỉ có `docker --version` mà không nối được Server thì chưa đạt.

**Gặp lỗi:** CLI không tìm thấy → PATH/terminal; không nối daemon → Desktop/Engine/WSL; xem lỗi trước, không chỉnh pom để chữa Docker. **Sáng công ty:** đọc C01/C02 với chính các lệnh và lỗi này.

#### N01B — Tạo Compose và chạy MySQL trước, chưa có backend container

**Tạo tại gốc repo:** `.env.example`, bản local `.env`, `compose.yaml`. `.env` đã nằm trong .gitignore hiện có; kiểm lại trước khi lưu secret.

Nội dung `.env.example` để mô tả biến:

```dotenv
MYSQL_ROOT_PASSWORD=DIEN_MAT_KHAU_ROOT_LOCAL
DB_USERNAME=telecom
DB_PASSWORD=DIEN_MAT_KHAU_APP_LOCAL
```

Copy thành `.env` và **đổi hai placeholder mật khẩu**, không dùng root để backend truy cập. Những biến này được image MySQL dùng để khởi tạo DB/user lần đầu trên volume mới; sửa chúng không tự đổi credentials trong volume cũ. Đọc [MySQL official image](https://hub.docker.com/_/mysql) khi gặp khác biệt khởi tạo.

Nội dung `compose.yaml` ban đầu:

```yaml
services:
  mysql:
    image: mysql:8.0.46
    environment:
      MYSQL_DATABASE: telecom_dev
      MYSQL_USER: ${DB_USERNAME:?Dien DB_USERNAME trong .env}
      MYSQL_PASSWORD: ${DB_PASSWORD:?Dien DB_PASSWORD trong .env}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:?Dien MYSQL_ROOT_PASSWORD trong .env}
    command:
      - --transaction-isolation=READ-COMMITTED
      - --default-time-zone=+00:00
    ports:
      - "127.0.0.1:3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "--silent"]
      interval: 5s
      timeout: 3s
      retries: 20
      start_period: 30s

volumes:
  mysql_data:
```

**Chạy lần lượt:**

```powershell
docker compose config --quiet
docker compose pull mysql
docker compose up -d mysql
docker compose ps
docker compose logs --tail 100 mysql
```

Lệnh pull xác minh registry của máy nhà lấy được tag MySQL8.0.46. Nếu `manifest unknown` hoặc thiếu quyền mạng, ghi blocker và kiểm tag/registry, không tự đổi sang latest/8.4 vì baseline đã chốt8.0.46. Chưa có bằng chứng tag này kéo được trong lượt viết hướng dẫn.

**Kiểm bằng đăng nhập thật** — gõ password app local khi MySQL hỏi:

```powershell
docker compose exec mysql mysql -u telecom -p telecom_dev
```

Trong MySQL prompt:

```sql
SELECT VERSION(), DATABASE(), @@session.time_zone, @@transaction_isolation;
SHOW TABLES;
exit;
```

**Kết quả cần thấy:** đúng version8.0.46, DBtelecom_dev, timezone+00:00, isolationREAD-COMMITTED; đăng nhập được. `SHOW TABLES` có thể chưa có bảng nghiệp vụ. Healthcheck ping chỉ giúp thấy DB đang phản hồi, không thay bài kiểm username/password này.

**Dừng và mở lại không xóa dữ liệu:**

```powershell
docker compose stop mysql
docker compose start mysql
```

Không dùng `down -v` để dừng hằng ngày: đó là yêu cầu xóa volume. Nếu port3306 đã dùng, tìm service chiếm cổng hoặc đổi **cổng host** có ghi nhận và đồng bộ JDBC ở bước sau; không đổi bừa cổng bên trong MySQL.

**Sáng công ty:** mở compose/.env đọc C05, chỉ từng khóa YAML và giải thích volume/port/env. B chỉ xong khi bạn tự đăng nhập được, không chỉ nhìn container màu xanh.

#### N01C — Tạo ứng dụng Spring Boot và API ping, chạy Java từ terminal/IDE

**1. Sửa `pom.xml`, không tạo project thứ hai.** Giữ parent Boot4.1.1/Java21 và security overrides hiện có. Thay `flyway-database-postgresql`, driver `org.postgresql:postgresql`, `testcontainers-postgresql` bằng ba dependency sau, không để cả hai bộ DB cùng phục vụ MySQL:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-mysql</artifactId>
    <scope>test</scope>
</dependency>
```

Giữ starter Flyway/JPA/JDBC/webmvc/test đang có. Property `postgresql.version` và comment riêng driver PG có thể bỏ khi không còn dùng. Nếu Maven báo version không được quản lý, kiểm effective pom/BOM và coordinates trước; không thêm version mới tùy tiện. Tham khảo [Flyway MySQL](https://documentation.red-gate.com/flyway/reference/database-driver-reference/mysql), [Testcontainers MySQL](https://java.testcontainers.org/modules/databases/mysql/).

Trong build/plugins, thay plugin `maven-jar-plugin` đang trỏ `com.telecom.Main` bằng:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.telecom.TelecomManagementApplication</mainClass>
    </configuration>
</plugin>
```

Giữ compiler plugin hiện có. Boot parent chuẩn bị repackage execution; plugin đóng executable JAR. [Tài liệu packaging](https://docs.spring.io/spring-boot/maven-plugin/packaging.html) giải thích vì sao không chỉ đổi mainClass ở JAR thường.

**2. Tạo `src/main/java/com/telecom/TelecomManagementApplication.java`:**

```java
package com.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelecomManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelecomManagementApplication.class, args);
    }
}
```

Sau khi entrypoint mới compile/run đúng, xóa Main mẫu cũ tại `src/main/java/main/java/Main.java`. Không xóa class khác do bạn đã tự viết ở nhà nếu repo đã phát triển thêm.

**3. Tạo `src/main/resources/application.yaml`:**

```yaml
server:
  port: 8080
spring:
  application:
    name: telecom-management
management:
  endpoints:
    web:
      exposure:
        include: health,info
```

**Tạo `src/main/resources/application-home.yaml`:**

```yaml
spring:
  datasource:
    url: ${DB_JDBC_URL:jdbc:mysql://localhost:3306/telecom_dev?connectionTimeZone=UTC}
    username: ${DB_USERNAME:telecom}
    password: ${DB_PASSWORD}
    hikari:
      transaction-isolation: TRANSACTION_READ_COMMITTED
      connection-init-sql: SET time_zone = '+00:00'
  flyway:
    enabled: true
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
```

Chưa có entity/migration ở buổi này; chưa được bật `ddl-auto=update` để framework tự thiết kế bảng. Nếu Flyway báo chưa có migration thì đọc log, chưa coi đó là bằng chứng đã dựng schema nghiệp vụ.

**4. Tạo `src/main/java/com/telecom/dev/PingController.java`:**

```java
package com.telecom.dev;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev")
public class PingController {
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "pong");
    }
}
```

Đây là endpoint học kết nối, không phải API tiền/acceptance requirement. Khi thêm security ở N03 phải xác định lại quyền/loại endpoint dev này; không mở nghiệp vụ tiền vì ping ban đầu chạy được.

**5. Chạy bằng PowerShell ở gốc repo.** Điền password app đúng với .env máy nhà; chuỗi dưới đây là placeholder, không phải mật khẩu có sẵn:

```powershell
$env:DB_USERNAME = 'telecom'
$env:DB_PASSWORD = 'THAY_BANG_MAT_KHAU_APP_LOCAL'
mvn compile
mvn spring-boot:run "-Dspring-boot.run.profiles=home"
```

Hoặc IDE: chọn JDK21, chạy class main mới, thêm `SPRING_PROFILES_ACTIVE=home`, `DB_USERNAME`, `DB_PASSWORD` vào Run Configuration. File.env của Compose không tự được IDE đọc.

Mở terminal thứ hai:

```powershell
Invoke-RestMethod 'http://localhost:8080/api/v1/dev/ping'
Invoke-RestMethod 'http://localhost:8080/actuator/health'
```

**Cần thấy:** messagepong và healthUP khi đúng cấu hình/DB; log không lỗi datasource. Nếu không đạt, đọc log thật và C12, không chuyển tiếp sang xử lý tiền. Dừng Java bằng Ctrl+C trong terminal đang chạy; MySQL container vẫn chạy.

**Sáng công ty:** đọc C02–C04/C06 với đúng pom/main/controller/YAML vừa tạo; tự giải thích annotation, XML, placeholder và JSON.

#### N01D — Đóng JAR, build image backend và đưa vào Compose

Chỉ làm khi N01C chạy từ IDE/terminal đúng. Đích buổi này là **cùng ứng dụng** chạy bằng container, không viết lại code Java.

**1. Dừng backend IDE/terminal** để nhả cổng8080, giữ MySQL. Build executable JAR:

```powershell
mvn clean package
java -jar target/telecom-management-system-1.0-SNAPSHOT.jar --spring.profiles.active=home
```

Dùng lại biến môi trường password của N01C. Gọi ping để xác nhận JAR chạy rồi Ctrl+C dừng nó. Nếu JAR không chạy được, sửa packaging trước khi đổ lỗi Docker. Ở các buổi sau test suite thật phải được cấu hình/chạy; buổi này chưa có test tiền.

**2. Tạo `Dockerfile` tại gốc repo:**

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/telecom-management-system-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Tạo `.dockerignore`:**

```text
.git
.idea
.vscode
docs
.env
*.iml
```

Không ignore `target/` vì mẫu Dockerfile này COPY JAR từ đó. Đây là cách học build JAR ở host rồi build image, chưa dùng multi-stage. Tag nền JRE cần lấy được ở máy nhà; trước phép đo/bàn giao sẽ cố định version/digest thực tế theo cấu hình run, không gọi mẫu này là image đã kiểm chứng.

**3. Thêm service `backend` vào dưới `services:` của compose.yaml, ngang cấp `mysql`, trước `volumes:` cấp gốc:**

```yaml
  backend:
    build: .
    ports:
      - "127.0.0.1:8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: home
      DB_JDBC_URL: jdbc:mysql://mysql:3306/telecom_dev?connectionTimeZone=UTC
      DB_USERNAME: ${DB_USERNAME:?Dien DB_USERNAME trong .env}
      DB_PASSWORD: ${DB_PASSWORD:?Dien DB_PASSWORD trong .env}
    depends_on:
      mysql:
        condition: service_healthy
```

Chú ý trong backend container DB hostname là **mysql**, không phải localhost. Compose cấp env cho container, không trông chờ biến trong terminal host tự đi vào Java container.

**4. Build/chạy/kiểm:**

```powershell
docker compose config --quiet
docker compose build backend
docker compose up -d backend
docker compose ps
docker compose logs --tail 100 backend
Invoke-RestMethod 'http://localhost:8080/api/v1/dev/ping'
```

Sau sửa Java, chuỗi thao tác của mẫu này là **Maven package lại → Compose build lại backend → up lại backend**. Chỉ restart container không copy code/JAR mới vào image cũ.

**Xong buổi:** ghi mình đang chạy backend IDE hay container; đừng để hai bản cùng chiếm8080. Dừng stack bằng `docker compose stop`; giữ volume. **Sáng công ty:** mở C05 và đọc từng dòng Dockerfile/.dockerignore/Compose, đặc biệt build context, COPY, port và hostname.

Sau A–D bạn mới có nền chạy, chưa có API cộng tiền và chưa nghiệm thu P5.1 đầy đủ. Các thành phần security/migration/test và MQ/Redis còn làm tiếp đúng lúc; không học hết chúng mới bắt đầu phần tiền.

### N02 — Migration và fixtures cho tiền, chưa cần API UC01/UC02 · P5.1 + phụ thuộc UC04/05

**Học trước:** C01/C06. **Tự làm:**

1. Tạo Flyway migrations nền theo schema đã chốt; dùng MySQL InnoDB/READ COMMITTED, UTC `DATETIME(6)`, integer VND, UUID/collation và UQ/FK/CHECK đúng thiết kế. Để JPA validate thay vì tự sinh schema làm lệch Flyway.
2. Chuẩn bị graph dữ liệu cho credit: Customer → inventory/Subscriber → MAIN và tài khoản phụ có quyền/hạn rõ; thêm operation_request và financial_transaction cho đường tiền.
3. Chuẩn bị graph debit: service_package → package_subscription → subscription_cycle đúng thuê bao và còn hiệu lực; fee transaction và FK liên quan phải hợp lệ. Không chỉ insert một cycle mồ côi. Cố định mốc thời gian/seed sao cho test không vô tình hết hạn.
4. Seed tiền qua lịch sử/fixture có nguồn rõ: ghi số dư đầu và ledger setup được đối soát; không sửa SQL balance giữa bài test/tải mà bỏ dấu vết. Tách dữ liệu setup khỏi thao tác đang được test.
5. Đặt fixture/seed trong test hoặc profile dev có kiểm soát; không tạo public endpoint cộng tiền/bỏ kiểm gói để cấp dữ liệu nhanh. Tạo đủ ca MAIN, GAME được/không được CREDIT/DEBIT, expired, account sai subscriber, cycle tương lai/hết hạn.

**Chia N02 thành các tối nhỏ, tạo đúng các file sau:**

| Tối | Tạo/sửa | Nội dung bạn tự viết | Cách nhìn kết quả |
|---|---|---|---|
| N02A | `src/main/resources/db/migration/V1__money_foundation.sql` | Lấy cột/ràng buộc từ schema đã chốt. Tạo customer → phone_number_inventory → subscriber → service_account; service_package → package_subscription; operation_request → financial_transaction → subscription_cycle. Thêm FK từ consumption ledger tới cycle sau khi hai bảng có mặt; fee được cycle tham chiếu tới ledger, không tạo vòng insert NOT NULL | Khởi động app chạy Flyway; vào MySQL `SHOW TABLES; SELECT * FROM flyway_schema_history;` |
| N02B | `src/test/resources/sql/money-fixture.sql` | Dữ liệu giả UUID cố định, một Subscriber/MAIN/GAME, gói/root/cycle/fee hợp lệ; có cả ca sai hạn/quyền để test. Giá trị/ledger setup phải cân bằng, không insert cycle thiếu fee | Integration test dùng `@Sql` hoặc helper setup đọc file rồi assertion các quan hệ/số dư |
| N02C | `src/test/java/com/telecom/account/MoneySchemaIT.java` | Chạy MySQL đúng phiên bản bằng Testcontainers; thử seed đúng và insert vi phạm FK/UQ/CHECK phải bị từ chối | Cấu hình Failsafe theo N07, chạy `mvn verify`, kiểm report có chính test này |

Đừng chép bảng giả có ba cột thay schema dự án. Một file fixture trong `src/test/resources` **không tự chạy** khi bật app home: để demo API, tạo `src/main/java/com/telecom/dev/MoneyDemoDataLoader.java` chạy chỉ ở profile dev chuyên biệt (`home,demo`) và đọc bộ seed dev có kiểm soát, hoặc nhập fixture vào DB dev qua công cụ SQL với log rõ ràng. Không chạy loader ở home bình thường/test tải, không tạo endpoint public bypass. Loader chỉ seed khi đủ guard để không nạp lại tiền khi restart; việc này cần test riêng.

Sau N02A, mở MySQL bằng lệnh N01B và dùng:

```sql
SHOW TABLES;
SELECT * FROM flyway_schema_history;
SELECT id, subscriber_id, balance_vnd, status FROM service_account;
```

Nếu câu cuối chưa có dòng sau N02A thì bình thường: chưa chạy fixture/seed. Sau N02B phải thấy đúng dữ liệu setup; đừng debug controller khi DB chưa có account/cycle cần cho test.

**Tự kiểm:** migrate từ DB rỗng, khởi động lại không áp migration lần hai; thử FK/UQ/CHECK bằng test cố ý sai. Lưu được snapshot số dư/ledger đầu để N07–N08 đối soát. Không báo UC01 hoặc UC02 đã hoàn thành từ việc seed graph.

### N03 — Khung API, validation và JWT/caller · P5.1 + nền UC04/UC05

**Học trước:** C04/C10. **Tự làm:** tạo DTO/input validation, error mapping và resource server theo OpenAPI; caller lấy từ JWT đã xác minh với issuer/audience/JWKS/expiry/scope đúng môi trường. Cấu hình scope credit/debit/operation lookup, không tự phát JWT trong Telecom.

Ba API của slice tiền:

| API | Vai trò |
|---|---|
| `POST /api/v1/accounts/{accountId}/credits` | Cộng đúng tài khoản được chỉ định |
| `POST /api/v1/accounts/{accountId}/debits` | Trừ tiêu dùng độc lập, có subscriptionId/cycleId/serviceReference |
| `GET /api/v1/operations/{operation}/{requestId}` | Tra outcome sau retry/mất phản hồi, thuộc caller |

**Không làm cả auth trong một tối. Tạo từng phần:**

| Phần | File/cấu hình | Việc phải làm |
|---|---|---|
| IdP | Thêm service `keycloak` trong `compose.yaml`, thêm biến admin local vào .env.example/.env | Theo [Keycloak Docker guide](https://www.keycloak.org/getting-started/getting-started-docker), dùng tag cố định đã kiểm ở máy nhà, chế độ dev chỉ cho học local; khởi động và mở Admin Console |
| Realm/client | Cấu hình trong Admin Console hoặc realm export cho demo | Realm `telecom-demo`, audience `telecom-api`; confidential client gọi API bằng service account/client credentials; cấu hình scope credit/debit/operation-read vào claim token theo security spec. Client khác để test ownership |
| Dependency | `pom.xml` | Thêm `spring-boot-starter-oauth2-resource-server`, dùng version do parent/BOM quản lý |
| Properties | `application-home.yaml`, env | Issuer, JWKS, audience, mapping caller; thử backend chạy IDE trước cho đơn giản. Sau đó kiểm lại hostname/JWKS khi backend chạy container; issuer token phải nhất quán, không dùng localhost trong container để gọi nhầm chính nó |
| Security | `src/main/java/com/telecom/security/SecurityConfig.java` | Cấu hình filter chain, JWT validators, scope rules theo endpoint; kiểm audience và caller mapping rõ ràng, không coi chỉ có issuer-uri là xong mọi rule |
| Caller | `src/main/java/com/telecom/security/Caller.java`, `CallerResolver.java` | Lấy caller ổn định từ claim đã xác minh, không body; truyền caller tới coordinator |
| Lỗi API | `src/main/java/com/telecom/shared/api/ApiExceptionHandler.java` | Map lỗi đã chốt, không lộ stack/SQL/secret; response có requestId/correlationId đúng contract |
| Test | `src/test/java/com/telecom/security/MoneySecurityIT.java` | 401/403/caller ownership và token thực từ IdP cho smoke; mock JWT chỉ ở test riêng |

**Thử trước nghiệp vụ tiền:** lấy token từ IdP bằng client credentials, cấu hình Bearer token trong HTTP client ở máy nhà, gọi endpoint theo rule bạn đã đặt; một token sai phải bị từ chối. Keycloak chỉ cần làm tới mức cấp/kiểm token của dự án, chưa học hết sản phẩm. Sáng sau mang chính SecurityConfig/token claims đã che secrets lên đọc C10.

**Tự kiểm:** token thiếu/sai/hết hạn → 401; thiếu scope → 403; caller khác tra request → 404 theo contract. Amount/null/field lạ sai được từ chối đúng trước claim. Mock JWT dùng trong test riêng không thay smoke test token thực từ IdP. Không tắt auth của API để demo tiền.

### N04 — Request registry, canonical và idempotency dùng chung

**Học trước:** C07/C09. **Tự làm:**

1. Triển khai canonical payload đúng mọi trường nghiệp vụ; hash, scope `(caller, operation, requestId)` và unique constraint. JWT/correlation/thứ tự JSON không tham gia như nội dung tiền mới.
2. Claim transaction ngắn lưu PROCESSING; duplicate-key rollback claim rồi đọc bản gốc, không dùng `INSERT IGNORE` để nuốt lỗi khác.
3. Tách finalizer transaction: khóa request, kiểm terminal/fence rồi mới xử lý tiền. Cùng ID/cùng payload đang PROCESSING trả trạng thái xử lý; terminal replay; khác payload conflict, không ghi đè.
4. Lookup caller-owned, lưu response lịch sử và kết quả lỗi nghiệp vụ cuối. Replay30ngày sau terminal UC04/05; tombstone không cho dùng lại ID; không purge PROCESSING/demo.

**File bạn cần tạo và làm lần lượt** (tên không ghi adapter cụ thể thì chọn một JDBC/JPA adapter phù hợp thiết kế, không viết cả hai chỉ để đủ file):

| File dưới `src/main/java/com/telecom/` | Bạn viết gì? |
|---|---|
| `operations/Operation.java`, `OperationStatus.java` | Enum operation và PROCESSING/SUCCEEDED/FAILED theo schema/contract |
| `operations/RequestKey.java` | Kiểu dữ liệu cho caller + operation + requestId, không chỉ một String ID |
| `operations/CanonicalPayload.java` | Chuẩn hóa/serialize nội dung nghiệp vụ ổn định và tính SHA-256; viết test trước khi dùng hash vào DB |
| `operations/OperationRequestStore.java` + adapter JDBC/JPA | Insert claim, đọc outcome, khóa request với fence, ghi terminal result theo schema |
| `operations/OperationCoordinator.java` | `claim`, `lookup`, `recoverProcessing`; phân duplicate/cùng payload/khác payload và không tự tác động balance |
| `operations/api/OperationController.java` | GET lookup theo đúng operation/requestId và caller hiện tại |

Chia làm ba tối: **canonical+unit test → DB claim/unique+integration test → lookup/replay**. Tạo `CanonicalPayloadTest`, `OperationClaimIT`, `OperationLookupIT` dưới `src/test/java/com/telecom/operations/`; N07 hướng dẫn Maven chạy chúng. Khi chưa có API tiền, integration test gọi coordinator trực tiếp để chứng minh claim, không thêm public API claim phụ.

**Tự kiểm:** unit canonical + integration hai caller/hai operation, cùng ID khác nội dung, nhiều attempt claim đồng thời. Giai đoạn registry chưa có money effect không được gọi là credit/debit hoàn chỉnh.

### N05 — UC04 cộng tiền đủ transaction · P5.5

**Học trước:** C07/C08/C11. **Tự viết test trước:** dư100.000, credit30.000 → dư130.000, đúng một CREDIT_TOPUP với before/after và outcome SUCCEEDED.

**Tự code theo thiết kế:** finalizer khóa operation_request → đúng service_account; kiểm subscriber/type/permission/status; lấy giờ DB mới sau khóa ngay trước mutation, kiểm hạn; amount nguyên dương/cap và chống tràn, số dư mới không vượt cap. Balance + ledger + outcome trong cùng transaction; chỉ trả thành công sau commit. Lỗi nghiệp vụ không tạo ledger thành công; ghi outcome FAILED bền vững mà không giữ tiền đã rollback.

**Bản đồ file để tự code, theo thứ tự:**

| Thứ tự | File dưới `src/main/java/com/telecom/` | Code cần viết |
|---|---|---|
| 1 | `account/MoneyLimits.java`, `account/MoneyCore.java` | Limits từ cấu hình; hàm kiểm và tính số dư mới/BalanceChange, integer VND và kiểm tràn. Unit test trước, chưa gọi DB/HTTP |
| 2 | `account/AccountStore.java`, adapter persistence; `account/LedgerStore.java`, adapter | SQL/mapping lấy đúng account với row lock, update, append ledger; tham số bound, không tự commit lẻ trong repository |
| 3 | `account/MoneyService.java` | `credit` phối hợp claim/finalizer, account lock, giờDB, MoneyCore, ledger/outcome cùng transaction |
| 4 | `account/api/CreditRequest.java`, `operations/api/OperationResult.java` | DTO có requestId/subscriberId/amountVnd và response theo OpenAPI; không serialize entity thẳng |
| 5 | `account/api/MoneyController.java` | POST credits nhận path accountId, body đã validate, caller đã xác minh; gọi service, trả HTTP201 mới/200 replay/202 đang xử lý hoặc lỗi contract |
| 6 | `src/test/java/com/telecom/account/MoneyCoreTest.java`, `CreditApiIT.java` | Assertion số dư/ledger/request, cap, expiry, replay và rollback |

Số tiền dùng `Long` trong DTO để phân biệt null với0; kiểm nguyên/lẻ theo JSON contract, **không để JSON số lẻ bị ép kiểu âm thầm sang long**. Giới hạn10triệu/100triệu lấy từ cấu hình theo baseline, không hardcode annotation gây lệch khi thay config.

**Tự chạy khi đã viết các test:**

```powershell
mvn "-Dtest=MoneyCoreTest" test
mvn verify
```

Sau đó chạy app với dữ liệu dev đã seed, token hợp lệ, gửi POST credits bằng HTTP client. Điền accountId/subscriberId đúng fixture; body ví dụ:

```json
{
  "requestId": "learn-credit-001",
  "subscriberId": "THAY_BANG_UUID_SUBSCRIBER_CUA_FIXTURE",
  "amountVnd": 30000
}
```

Đây là **mẫu cấu trúc**: chuỗi placeholder phải thay thành UUID thật trước gửi. Dùng lại cùng ID/payload để replay, không để auto-generated ID của HTTP client che mất bài thử. Gọi GET operation và đọc account/ledger để đối chiếu, không chỉ nhìn response màu xanh.

**Tự kiểm:** toàn bộ AC-CRE-01..10, đặc biệt phụ không fallback MAIN; hai credit tranh cap; error sau update trước insert ledger phải rollback. Không gọi HTTP/broker bên ngoài trong lúc giữ khóa tiền.

### N06 — UC05 trừ tiền tiêu dùng đủ điều kiện · P5.6

**Học trước:** C01/C07–C11. **Tự viết test trước:** cùng graph hợp lệ, debit80.000 từ100.000 → dư20.000 và một SERVICE_CONSUMPTION gắn đúng cycle/subscription/package.

**Tự code:** request → account → subscription root → cycles theo thiết kế; sau tất cả thời gian chờ khóa lấy lại giờ DB ngay trước cập nhật. Cùng mốc eligibility kiểm account chưa hết hạn và cycle `[start,end)` đúng subscriber/còn quyền dùng. PENDING_CANCEL vẫn được tiêu dùng trong kỳ còn hiệu lực; không có kỳ hoặc kỳ tương lai/hết hạn thì không trừ.

Kiểm amount/cap/đủ tiền trên đúng account. Không đủ GAME thì không lấy MAIN; không gộp tài khoản. Debit + ledger + outcome commit nguyên tử; FAILED không có money effect. Đây là lệnh tiêu dùng đã có amount, chưa xây định giá lưu lượng. Phí đăng ký/gia hạn sau này không đi qua API UC05.

**Bạn bổ sung vào code đã có, không tạo một app khác:**

- Tạo `account/api/DebitRequest.java`: thêm subscriptionId/cycleId/serviceReference theo OpenAPI bên cạnh requestId/subscriberId/amountVnd.
- Thêm method debit vào MoneyController và `consume` vào MoneyService theo Design Class; dùng lại MoneyCore/AccountStore/LedgerStore/OperationCoordinator, không copy registry riêng cho debit.
- Qua cổng `subscription/SubscriptionStore.java`/adapter, khóa/đọc root/cycle theo trách nhiệm module đã duyệt; kiểm đúng chủ và khoảng hiệu lực, rồi lấy DBclock mới sau các khóa.
- Tạo `src/test/java/com/telecom/account/DebitApiIT.java`. Viết lần lượt: debit hợp lệ → thiếu tiền → GAME không fallback → sai/hết cycle → replay → rollback.

**Bài thực hành nhỏ:** account100.000, cycle còn hạn; HTTP POST debits amount80.000. Đọc DB thấy20.000 và một consumption; replay cùng ID vẫn20.000. Đổi ID mới amount80.000 thì thiếu tiền. Sau đó mới chạy test hai request đồng thời N07.

Tối nào gặp annotation/method SQL mới, ghi nguyên tên và file để sáng đọc C04/C06/C07/C08; chưa cần nhớ hết chúng trước khi gõ code.

**Tự kiểm:** AC-DEB-01..11 trước; AC-DEB-12 thuộc bài tranh chấp/tải sau N07/N15. Không chỉ test debit trên account đủ tiền mà bỏ ràng buộc kỳ gói.

### N07 — Integration/API/concurrency suite module tiền

**Học trước:** C08/C10/C11. Chạy MySQL đúng bản bằng Testcontainers hoặc môi trường test Compose tách biệt, không dùng H2/fake làm bằng chứng InnoDB.

**Cách làm:** mỗi worker có transaction/kết nối độc lập, đủ pool cho kịch bản; seed đã commit trước khi worker đọc. Barrier đặt trước vùng tranh khóa, không đợi mọi thread cùng sở hữu một khóa. Đọc DB sau khi mọi transaction kết thúc; dọn test xác định, không để test này phụ thuộc thứ tự test kia.

**Tạo/sửa để lệnh test thực sự chạy:**

1. Tạo `src/test/java/com/telecom/support/MySqlIntegrationSupport.java` hoặc cấu hình test dùng MySQLContainer8.0.46, credentials/URL cấp cho Spring test context; không trỏ test vào DB dev đang dùng demo. Testcontainers cần Docker Engine đang chạy ở nhà.
2. Tạo `MoneyConcurrencyIT.java` trong package account. Dùng ExecutorService + latch/barrier điều phối nhiều worker, mỗi worker gọi service qua đúng transaction boundary; seed phải commit trước. Chưa cần k6 để chứng minh hai debit tranh tiền.
3. Trong `pom.xml`, thêm Failsafe plugin vào plugins đang có:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Kiểm version plugin được quản lý bởi parent/effective pom; [Failsafe usage](https://maven.apache.org/surefire/maven-failsafe-plugin/usage.html) giải thích lifecycle. Dùng tên `*Test` cho unit và `*IT` cho integration theo cấu hình trên.

```powershell
mvn test
mvn verify
Get-ChildItem target/surefire-reports
Get-ChildItem target/failsafe-reports
```

Đọc báo cáo thấy **đúng tên test và số ca đã chạy**, không chỉ dòng BUILD SUCCESS. Nếu Docker lỗi thì integration tests chưa có bằng chứng, không đổi sang H2 để báo đạt khóa MySQL.

**Bằng chứng:** HTTP outcome + operation row + account + ledger + attribution; quan sát backend/DB lock wait cho ca tranh chấp. Tên test gợi ý: `CreditServiceTest`, `CreditApiIT`, `DebitApiIT`, `MoneyConcurrencyIT`; đặt tên/plugin sao cho chúng thật sự được chạy.

Dùng ma trận mục 3 làm checklist và gắn từng test với AC/TC nguồn. Build xanh nhưng test concurrency chưa chạy thì ghi chưa chạy, không tick thay.

### N08 — Lỗi hạ tầng, recovery và đối soát từng request

**Học trước:** C09/C12. **Tự làm:**

1. Fault injection vào sau claim, sau balance update, trước/đang commit và sau commit trước HTTP response. Tách trường hợp chắc chắn rollback với commit outcome chưa rõ.
2. Deadlock1213/lock timeout1205 phải rollback toàn bộ transaction ứng dụng trước retry transaction mới hữu hạn; không tiếp tục insert ledger sau statement lỗi. Retry cùng logical ID, không biến lỗi hạ tầng thành INSUFFICIENT_BALANCE.
3. Áp lease/fence/reclaim và recovery theo thiết kế; crash để lại PROCESSING phải có đường xử lý lại bền vững. Worker cũ không commit money effect sau khi đã mất quyền. Cạn budget còn cần recovery, không tự đóng FAILED tiền giả.
4. Mất phản hồi commit: dùng kết nối/transaction mới đọc và phối hợp khóa request để xác định transaction cũ; trả outcome hiện có hoặc trạng thái chưa rõ, không cấp ID mới mù quáng.
5. Đối soát từng account và logical request: `balance cuối = balance đầu + CREDIT đã commit − DEBIT đã commit`. Bao gồm fee/setup/background nào thực sự thay tiền trong phạm vi run, mỗi khoản đúng một lần. Kiểm ledger before/after liên tục bằng thứ tự tác động có thẩm quyền; không coi thứ tự UUID là commit order.
6. Mỗi request SUCCEEDED có đúng tác động/ledger mong đợi; FAILED không có effect thành công; replay/conflict không thêm effect; mọi timeout được tra tới outcome cuối, chưa rõ thì ghi unresolved. Hai sai số triệt tiêu tổng tiền vẫn phải bị phát hiện.

**Các file/bài thực hành cần bổ sung:** `operations/OperationRecoveryWorker.java` (hoặc adapter recovery theo design), test `OperationRecoveryIT.java`, `CommitOutcomeIT.java`, `MoneyReconciliationIT.java`. Tạo hook lỗi chỉ trong test để dừng ở các điểm N08 đã liệt kê; không thêm HTTP endpoint lỗi/skip-auth trong app thật. Reconciliation test phải query account/operation/ledger và kiểm từng key, không chỉ gọi lại service tính tiền rồi tin kết quả service đó.

Làm theo ba buổi: **rollback có chủ đích → mất response/lookup → crash/reclaim/reconciliation**. Tới đây mới nghiên cứu sâu lease/fence, vì đã có transaction thật để quan sát. Sáng sau mở chính test thất bại, đọc C09/C11/C12.

**Tự kiểm:** TC-EDGE-35..40 và các ca rollback/recovery; restart rồi cùng ID không tác động lại. Đối soát độc lập với response tổng hợp của API, có thể dùng SQL và test Java/công cụ sau này phù hợp; không khôi phục bộ Python kiểm tài liệu đã xóa.

### N09 — Mốc module tiền để trình bày với mentor

Đây là mốc học/thực hành tiền, không đổi định nghĩa checkpoint M5 UC01.

- [ ] Chạy từ DB rỗng bằng migrations và fixtures có hướng dẫn.
- [ ] Credit/debit/lookup có auth, contract và business errors đúng.
- [ ] Bao phủ AC-CRE-01..10, AC-DEB-01..11; tình trạng AC-DEB-12 ghi rõ theo mức đo đã làm, chưa nhận đủ bài M7.
- [ ] Chứng minh hai debit80.000/dư100.000, credit tranh cap, mixed credit/debit và same-ID đồng thời.
- [ ] Rollback/lost ACK/crash/retry/recovery được kiểm, không cộng/trừ trùng.
- [ ] Bảng đối soát từng request/account/ledger không sai lệch; mọi outcome của run demo xác định.
- [ ] Lưu lệnh chạy, cấu hình, số test, response và truy vấn kiểm DB trong nhật ký ở mục 6; số chưa đo ghi NOT_RUN/NOT_PROVEN.

**Kịch bản demo gợi ý:** giải thích mô hình/transaction → credit thành công → debit thành công → thiếu tiền → gửi lặp → cùng ID đổi amount → hai debit tranh tiền → mất phản hồi/lookup → đối soát DB. Nhờ mentor góp ý bằng test/bằng chứng cụ thể, không chỉ cho xem Swagger.

## 3. Ma trận ngoại lệ tối thiểu của module tiền

Các mã M dưới đây chỉ là mã bài thực hành trong kế hoạch, không phải yêu cầu FR mới. Chi tiết oracle theo [UC04](01-requirements/use-cases/UC04-Account-Credit.md), [UC05](01-requirements/use-cases/UC05-Account-Debit.md) và [ma trận chuẩn](07-business-review/19-MA-TRAN-TRUY-VET-VA-NGOAI-LE.md).

| Bài | Dữ liệu/thao tác | Kết quả phải kiểm trong API và DB | Truy vết chính |
|---|---|---|---|
| M01 | Credit30.000 vào100.000 | Dư130.000, một ledger CREDIT, đúng before/after/outcome | AC-CRE-01 |
| M02 | Debit80.000 từ100.000, cycle hợp lệ | Dư20.000, một consumption gắn đúng cycle | AC-DEB-01/09 |
| M03 | Amount0/âm/lẻ/null/quá lớn/tràn | Validation/giới hạn đúng, không money effect, không truncate | TC-EDGE-30/31 |
| M04 | Biên10triệu/lệnh,100triệu/dư;95triệu+10triệu | Đúng biên được xét bình thường; vượt cap thất bại toàn bộ | AC-CRE-04/05 |
| M05 | Sai account/subscriber/type/quyền; account không tồn tại | Đúng lỗi contract, không thay tiền/không lộ dữ liệu | AC-CRE-02/03; TC-EDGE-32 |
| M06 | GAME20.000/MAIN50.000, debit GAME30.000 | Thất bại; cả hai tài khoản không đổi | AC-DEB-05 |
| M07 | Hai debit khác ID mỗi80.000, dư100.000 | Nếu xử lý đầy đủ và DB khỏe: đúng một success, dư20.000; không ledger cho thiếu tiền | AC-DEB-02/03 |
| M08 | Hai credit cùng account tranh cap | Không mất cập nhật, không vượt100triệu; outcome phản ánh lần nào hợp lệ | AC-CRE-07 |
| M09 | Nhiều credit/debit khác ID trộn cùng account | Không âm, ledger liên tục, đối soát đúng từng request | AC-DEB-04; TC-EDGE-34 |
| M10 | Cùng ID/payload gửi đồng thời nhiều lần | Một effect; PROCESSING/replay theo contract, không ledger lặp | AC-CRE-08; AC-DEB-10 |
| M11 | Cùng ID đổi amount/account/cycle/reference | IDEMPOTENCY_CONFLICT, giữ outcome/tiền cũ | TC-EDGE-38 |
| M12 | Đổi thứ tự JSON/JWT/correlation, cùng caller/payload nghiệp vụ | Không thành lệnh tiền mới; token vẫn phải hợp lệ | TC-EDGE-38 |
| M13 | FAILED thiếu tiền, nạp thêm rồi replay ID cũ | Vẫn FAILED; ID mới mới được xét lại | AC-CRE-10; TC-EDGE-36 |
| M14 | Không gói/sai chủ/sai kỳ/kỳ tương lai/đúng mốc end | Không debit dù account đủ tiền | AC-DEB-06/07 |
| M15 | PENDING_CANCEL đang trong kỳ rồi tới end | Trước end còn quyền; tới end chặn đúng | AC-DEB-08 |
| M16 | Đợi lock qua expiry; check trước/bằng/sau hạn; MAIN expiryNULL | GiờDB mới sau khóa; bằng/sau hạn từ chối; trước hạn có thể commit sau; MAINNULL không tự hết hạn | AC-CRE-06; AC-DEB-11; CR-TIME-002 |
| M17 | Lỗi sau update trước ledger/outcome | Không thành công một phần; money rollback, outcome xử lý bền vững đúng thiết kế | NFR-05/06 |
| M18 | Deadlock/lock timeout/cạn retry | Rollback cả transaction, retry hữu hạn cùng ID, không giả FAILED nghiệp vụ | TC-EDGE-40 |
| M19 | Crash sau claim; mất ACK sau commit; worker cũ bị reclaim | Recovery/lookup xác định, fence đúng, không double effect | AC-CRE-09; TC-EDGE-39 |
| M20 | Replay trong/sau30ngày; PROCESSING quá mốc retention | Trả số dư lịch sử; tombstone POST409/GET410; không tái dùng/purge pending | TC-EDGE-35 |
| M21 | Token/caller/scope sai; callerB lookup A | 401/403/404 đúng contract, không lấy caller từ body, không money effect trái phép | NFR-09; security |
| M22 | Sửa giả kết quả/thiếu ledger nhưng tổng tiền vẫn khớp | Bộ đối soát phải phát hiện sai theo logical key, không chỉ tổng balance | TC-EDGE-61/63 |

Các test lỗi có kiểm thời gian dùng fixture/fault hook chủ động, không chỉ `sleep` rồi cầu đúng biên. Không thay DBclock production bằng giờ client để làm test dễ hơn.

## 4. N10–N16: từ module tiền tới đủ yêu cầu dự án

### N10 — UC01 đấu nối Hybrid · P5.2 / M5

**Học:** C13. Reuse nền request/security nhưng giữ contract UC01 riêng. Làm chọn số/tự cấp, Customer/giấy tờ đã xác minh, tạo Subscriber+MAIN0 nguyên tử; tra cứu không giữ số. AUTO thực sự hết kho và đồng ý chờ mới WAITING24h, FIFO và recovery60s; không coi WAITING là thành công. Test tranh số/hồ sơ, retry/conflict, timeout/restart/hết hạn chờ; đủ 27 AC-SUB và API/migration/test mới nhận M5. Giữ fixtures tiền để regression, không coi chúng thay UC01 thật.

### N11 — UC02 đăng ký/mua nối gói có thu phí · P5.3

**Học:** C13. Dùng lõi giao dịch tiền để thu MAIN + ghi fee + kỳ + outcome cùng transaction; API UC02 khác API debit tiêu dùng. Kiểm version/root/target, snapshot giá/duration/catalogVersion và cycle uniqueness; request version cũ không âm thầm thu/nối thêm. Ngày lịch, kỳ trả trước/gap, gói ngừng bán và khách cũ theo baseline. Test rollback fee/cycle, manual-auto race, insufficient MAIN và idempotency; chạy regression tiền sau tích hợp.

### N12 — UC03, worker gia hạn, JOB-01 · P5.4/P5.8

**Học:** C13/C14. Hủy cuối toàn bộ thời gian đã trả, không hoàn phí. Triển khai CR-CAN-003: intent TxA được xác nhận commit rồi mới DB receipt TxB; pending intent chặn nối/gia hạn; lost ACK replay; receipt bằng/sau hạn từ chối đúng. Worker gia hạn và JOB-01 00:05 dùng chung lõi, cùng target một phí, muộn chỉ một kỳ không hồi tố; FAILED thiếu tiền không tự hồi sinh. Test biên end/version, worker/manual/job đồng thời, restart và nhiều instance; expired giữ lịch sử/soft delete theo baseline, không hard-delete ledger/gói.

### N13 — UC06 và JOB-02 báo cáo tiêu dùng · P5.7/P5.10

**Học:** C15. Attribution tiền đã làm ở UC05 là đầu vào. Làm query `[X,Y)`, gói A/B OR/AND và includeFee, loại CREDIT khỏi tiêu dùng; pagination50/max200, fixed membership/snapshot/cursor caller-owned. Dùng recordedAt của transaction cuối cùng commit, không giả physical commit timestamp; retry giữ cutoff/tập nguồn. JOB-02 00:30 tổng hợp ngày, finalized bất biến, nguồn muộn tạo adjustment một lần. Test ledger qua midnight/late commit, không đếm trùng BASE/ADJUSTMENT, cursor sửa/hết hạn và rebuild sau crash.

### N14 — Đủ tích hợp và vận hành home · P5.1 phần còn lại/P5.9

**Học:** C05/C14. Hoàn thiện Compose MySQL/RabbitMQ/Redis/Keycloak và backend IDE/container theo kiến trúc; kiểm issuer/hostnames/secrets/health đúng từ từng network. RabbitMQ chỉ báo kho bổ sung sau commit; duplicate/lost event không cấp số hai lần nhờ DB/recovery. Redis cache danh mục, lỗi/cache cũ phải đọc/kiểm lại MySQL cho quyết định giá; không dùng Redis lock giữ tiền. Test mất dịch vụ/restart/redelivery/cache stale/IdP key rotation; dọn cấu hình thử và đồng bộ docs/API/schema theo code. Chỉ lúc đáp ứng toàn checklist nền tảng mới đóng phần còn lại P5.1.

### N15 — Kiểm chứng hệ thống và tải theo đề bài · P6/M7

**Học:** C12/C15. Chỉ đo mixed khi UC02+UC05 thật đã đạt correctness. Smoke debit sớm ở N09 phục vụ mentor không thay phép nghiệm thu này.

1. Sinh dữ liệu hợp lệ **tổng bảng nghiệp vụ ≥500.000 trước đo**, thống kê từng bảng; không phải mỗi bảng500K, không cộng bảng hệ thống để đủ số.
2. Chuẩn bị đủ tiền/cycle/version để nghiệp vụ mới thành công; không disable fee/JWT/expiry/UQ để lấy TPS. Mọi refill/background fee đưa vào đối soát.
3. Viết k6/harness và công cụ đối soát phục vụ **runtime**; không phải bộ check tài liệu đã xóa. Lưu logical key, attempt, caller, account/hotGroup, thời gian, response và outcome cuối.
4. Warm-up120giây, đo600giây cùng cửa sổ: **UC02 ≥30 TPS mới thành công và UC05 độc lập ≥30 TPS mới thành công**, tổng≥60. Mỗi loại ít nhất18.000 nghiệp vụ được chứng minh commit trong cửa sổ; replay và fee nội bộ không vào UC05 TPS.
5. Điều phối **50% toàn bộ debit attempts kể retry** vào **10 cặp Subscriber–Account nóng phân đều**, phần còn lại phân rộng; giữ cùng payload khi retry. Chứng minh overlap/chờ khóa ở backend/DB, không chỉ client gọi đồng thời. Lưu số đếm nguyên, không sửa số liệu sau run để vừa tỷ lệ.
6. Từng loại UC02/05 đạt p95≤500ms/p99≤1giây theo protocol, error≤0,1%; timeout tính lỗi dù DB có thể commit. Báo dropped/unsent, logical end-to-end và hỗ trợ lookup riêng, không trộn làm đẹp mẫu số.
7. Phân loại biên commit bằng instrumentation có căn cứ, không dùng recordedAt trong transaction như bằng chứng đã commit trong cửa sổ. Đếm bảo thủ; chưa đủ bằng chứng thì NOT_PROVEN. Dừng admission ở T1 rồi drain/lookup nhưng không cộng success ngoài cửa sổ vào TPS.
8. Đối soát từng logical request/ledger/cycle/balance và background mutation, **unresolved=0**; báo PASS/FAIL/NOT_PROVEN từng tiêu chí kèm raw evidence, cấu hình máy/DB/pool/worker/code version. Fail thì tìm bottleneck/EXPLAIN/profile, đổi một yếu tố có căn cứ rồi chạy lại.

Nguồn công thức và taxonomy đầy đủ: [protocol tải](05-testing/performance-test.md). Không suy ra đạt M7 chỉ từ số HTTP2xx/s hoặc một ảnh dashboard.

### N16 — Đóng gói và bàn giao tái lập · P7/M8

**Học:** C05/C15. Build JAR/image, Compose/config example, migrations/seed, health/log/runbook; hướng dẫn lấy token và gọi API. Từ checkout mới trên môi trường sạch chạy theo README, kiểm sample flows và restart/recovery; kiểm backup/restore giữ balance/ledger/requests/cycles/report đúng. Giữ secrets ngoài Git, không xóa dữ liệu thật để “thử lại”. Lưu kết quả thực; còn bước làm tay không được ghi hoặc chưa restore được thì chưa đạt M8.

### Những file sẽ thêm ở các bước N10–N16

Đây là danh sách **việc sẽ tự tạo khi tới bước**, không phải file đang có. Chỉ thêm migration cho delta còn thiếu; không tạo lại bảng nền N02 hay sửa migration đã chạy.

| Bước | Tạo/sửa cụ thể | Chạy/kiểm sau khi làm |
|---|---|---|
| N10 | `activation/ActivationController`, `ActivationService`; inventory/customer adapters, `ActivationRequestStore`; migration tiếp theo cho phần UC01 còn thiếu; `ActivationApiIT`, `NumberAllocationIT` | `mvn verify`, demo chọn số/AUTO/WAITING/hết hạn/retry; DB chỉ một số/Subscriber/MAIN |
| N11 | `subscription/SubscriptionController`, `SubscriptionService`; catalog read, cycle/fee stores và DTO theo API; `PackageRegistrationIT` | POST đăng ký, kiểm MAIN/fee/cycle/outcome cùng commit và rollback/version conflict |
| N12 | `CancellationService`, `CancellationIntentStore`, `RenewalWorker`, `scheduling/ExpirationJob`; migration intent/renewal/job delta; `CancellationBoundaryIT`, `RenewalRaceIT` | Hủy trước/bằng/sau hạn, lost ACK, worker/job trùng; kiểm history không bị xóa cứng |
| N13 | `reporting/ConsumptionQueryController`, `ReportQueryService`, snapshot/summary stores; `scheduling/DailySummaryJob`; migration report delta; `ConsumptionReportIT`, `LateAdjustmentIT` | Query X/Y/A/B, phân trang/OR/AND/includeFee, rerun/late commit qua midnight |
| N14 | `integration/messaging/InventoryWakeupConsumer`, cache adapter, config properties; thêm service RabbitMQ/Redis/Keycloak vào Compose đã dùng | `docker compose up -d`, logs/health; mất/lặp event, cache stale/outage và restart; core DB vẫn đúng |
| N15 | `tools/load/mixed.js`, fixture/data generator runtime và bộ đối soát Java/SQL độc lập; cập nhật performance report bằng số đo thật | K6 theo protocol bên trên, lưu runId/config/raw metrics và reconcile từng request; không phải tool Python kiểm tài liệu cũ |
| N16 | Dockerfile/Compose cuối, `.env.example`, README/runbook và hướng dẫn seed/backup/restore theo đúng app đã chạy | Checkout sạch, build/run/token/API, restart/restore; ghi các bước còn phải làm tay |

Không cần tạo hết các file ở bảng ngay lúc này. Mỗi bước thêm controller/service/store/test đủ cho flow tương ứng, chạy được rồi mới sang phần tiếp. Với tên lớp hoặc chữ ký đã có trong code lúc đó, cập nhật lớp hiện có thay vì tạo một bản song song.

## 5. Truy vết yêu cầu → phần code → bằng chứng

| Yêu cầu cần hoàn thành | Bước | Bằng chứng cần có |
|---|---|---|
| Mô hình đối tượng và CSDL quan hệ | N02, bổ sung N10–N13 | Migration/FK/UQ/CHECK/index và fixture hợp lệ, diagram/schema khớp code |
| API cộng/trừ tiền | N03–N09 | AC-CRE01..10/AC-DEB01..12, matrix M01–M22, ledger/outcome/balance đúng |
| Đấu nối thuê bao | N10 | AC-SUB01..27, tranh số/retry/WAITING/expiry và API thật |
| Đăng ký/hủy gói | N11–N12 | Fee+kỳ nguyên tử, version/race, paid coverage và receipt/lostACK |
| Xử lý tài khoản/gói hết hạn, giữ lịch sử | N12 | Timestamp eligibility, soft-delete/status, job rerun/restart và audit |
| Báo cáo tiêu dùng X–Y theo gói A/B và tổng hợp ngày | N13 | OR/AND/includeFee/cursor/snapshot, attribution/late adjustments/rerun |
| ≥500K và mixed30+30TPS,50% tranh chấp | N15 | Count trước run, raw logs, bộ đếm mới commit, hot-group/lock overlap và số đo |
| Đúng/đủ khi retry/timeout/đồng thời | N07–N08, regression N11–N15 | Từng request đối chiếu ledger/effect/số dư; unresolved0 |
| Vận hành và bàn giao | N14/N16 | Môi trường/chạy sạch, log/health, secrets/backup/restore được kiểm |

SRS/UC/test matrix là oracle, không chỉ bảng trên. Với ngoại lệ ngoài ma trận, ghi tình huống và phân loại: thuộc quy tắc đã chốt thì thêm test; mâu thuẫn cần đổi rule thì ghi CR; thiếu môi trường thì ghi blocker. Kế hoạch không hứa xử lý mọi lỗi chưa được mô tả bằng suy đoán.

## 6. Ghi tiến độ và giữ vòng học → code

Chỉ tick N khi đã tự chạy tiêu chí của N, không vì đã tạo class/file. Có thể hoàn thành từng phần N01/P5.1 nhưng giữ phần chưa kiểm chứng mở.

- [ ] N01–N04: nền tảng, fixtures, auth/API và request registry.
- [ ] N05–N06: cộng và trừ tiền.
- [ ] N07–N09: test đồng thời, lỗi/recovery/đối soát và demo tiền.
- [ ] N10–N12: đấu nối, thu phí/kỳ, hủy/gia hạn/hết hạn.
- [ ] N13–N14: báo cáo/tổng hợp và tích hợp home đầy đủ.
- [ ] N15: tải/đối soát thực sự đạt các tiêu chí.
- [ ] N16: bàn giao chạy tái lập.

Ghi ngay trong bảng này hoặc nhật ký kiểm thử hiện có, không cần tạo bộ file phụ mới:

| Ngày | Bước/AC/test | Đã code và lệnh thực chạy | Kết quả/bằng chứng | Chưa đạt/câu hỏi mang lên công ty |
|---|---|---|---|---|
| Chưa ghi | N01A | Chưa chạy | NOT_RUN | Chưa cài/kiểm công cụ; sáng đọc C01/C02 |

Sau mỗi buổi, tự trả lời: “Em đã thay đổi gì? Quy tắc nào đang bảo vệ tiền? Test nào cố phá quy tắc đó? Nếu backend chết ở đây thì tra request ra sao?”. Khi đã có thay đổi đúng và test phù hợp mới tạo commit nhỏ theo sự cho phép/quy trình Git của bạn; không tự commit thay trong lượt lập kế hoạch này.

**Việc đầu tiên tối nay:** chỉ làm N01A đến lúc Java/Maven/Docker Engine dùng được. Sau đó mới N01B/MySQL, N01C/Spring, N01D/backend container; có thể một phần kéo dài nhiều buổi. Sáng sau đọc lại đúng file/lệnh của phần vừa làm.
