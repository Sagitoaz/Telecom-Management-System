# Ở công ty: học từ số 0 và đọc hiểu những gì tối trước đã làm

**Bản 2 · 28/10/2026.** Bạn chưa biết Docker/Docker Desktop/Spring. File này là sổ tra và hướng dẫn học lại từ chính file, lệnh, lỗi của buổi code ở nhà; không đòi bạn biết sẵn các thuật ngữ.

**Ở nhà làm theo [file thực hành](KE-HOACH-CODE-TAI-NHA.md). Ở công ty mở lại thay đổi tối trước, rồi dùng file này để hiểu từng dòng.** Không phải học hết một giáo trình trước khi được code. Module cộng/trừ tiền vẫn được làm trước theo [CR-IMPL-001](00-project/clarification-log.md#cr-impl-001).

## 1. Ngày đầu chưa có code tối trước thì làm gì?

Chỉ đọc mục 2–3 để biết các thành phần là gì, sau đó đọc C01/C02/C05. Tối đầu làm **N01A**, tối sau làm **N01B** trong file ở nhà. Không phải cài/chạy gì trên máy công ty.

Từ ngày có code, mỗi buổi ở công ty bắt đầu bằng 4 việc:

1. Xem danh sách file đã sửa tối trước; nếu Git dùng được, đọc `git diff --stat` rồi `git diff -- đường/dẫn/file`. Nếu đã commit, đọc diff của commit đó. Chỉ đọc, không phải chạy dịch vụ.
2. Chọn **một file hoặc 10–30 dòng** đang chưa hiểu. Không đọc hết framework cùng lúc.
3. Tách các từ chưa biết thành **cú pháp ngôn ngữ**, **API/annotation của thư viện**, **cấu hình** và **nghiệp vụ dự án**; dùng bảng mục 3 tìm nơi tra.
4. Tự giải thích từng dòng, dự đoán nếu đổi/xóa nó chuyện gì xảy ra; ghi một thay đổi hoặc một test sẽ thử tối nay.

Nếu tối trước gặp lỗi, học từ log lỗi đó trước. Ví dụ `Connection refused` dẫn tới host/port/container, còn `Access denied for user` dẫn tới credentials; không tìm chung “Spring lỗi” rồi thay cả dự án.

## 2. Các tên bạn sẽ gặp, giải thích bằng việc mình đang làm

| Tên | Hiểu đơn giản | Trong dự án này dùng khi nào? |
|---|---|---|
| JDK/Java | Bộ công cụ biên dịch và chạy Java | Viết/chạy backend Java21 |
| Spring | Framework tạo, nối các đối tượng và hỗ trợ web/giao dịch/bảo mật | Controller gọi service/repository và thực hiện nghiệp vụ |
| Spring Boot | Cách khởi tạo/cấu hình ứng dụng Spring với starter và auto-configuration | Chạy một backend có HTTP server từ hàm main |
| Maven | Công cụ đọc pom, tải thư viện, build và chạy test | Các lệnh `mvn ...` |
| JAR | File đóng gói chương trình Java | Sau build chạy bằng `java -jar ...` |
| Docker Desktop | Ứng dụng trên Windows để quản lý/chạy môi trường Docker | Cài/mở ở máy nhà, xem containers/images/logs |
| Docker Engine | Thành phần thực sự quản lý image/container | Docker Desktop cung cấp môi trường cho Engine chạy |
| Docker CLI | Lệnh `docker` trong terminal | Gửi yêu cầu tới Engine; có CLI không đồng nghĩa Engine đang chạy |
| Image | Mẫu ứng dụng/môi trường đã đóng gói | Lấy image MySQL có sẵn hoặc build image backend |
| Container | Một lần chạy từ image | Một MySQL đang chạy, một backend đang chạy |
| Dockerfile | Công thức build **image** | Đóng JAR của mình vào image Java |
| Compose | Cách khai báo/quản lý nhiều service trong một file | Cho MySQL/backend cùng chạy và liên lạc |
| Volume | Nơi giữ dữ liệu riêng với container | DB còn dữ liệu khi container được tạo lại |
| Port/hostname | Cổng và tên máy/service để kết nối | IDE dùng localhost; container backend gọi service mysql |
| MySQL | Cơ sở dữ liệu quan hệ | Giữ tài khoản/số dư/giao dịch/request |
| JDBC/driver | Cầu nối Java tới DB và thư viện thực hiện kết nối | Java dùng MySQL Connector/J để nói chuyện với MySQL |
| JPA/Hibernate | API/công cụ ánh xạ đối tượng Java và dữ liệu quan hệ | Một cách đọc/ghi bảng, vẫn phải hiểu SQL thật |
| Flyway | Công cụ áp thay đổi schema theo thứ tự file SQL | Dựng bảng từ DB rỗng và nâng schema |
| JWT/Keycloak | Token xác thực và IdP bên ngoài cấp token | Backend kiểm ai gọi/có quyền gì; không tự phát JWT |
| RabbitMQ/Redis | Broker thông điệp/cache | Học khi thật sự thêm chúng; không cần dùng để cộng/trừ đúng tiền |

**Hai hoạt động khác nhau:** `docker compose up mysql` chạy MySQL từ image có sẵn; `docker build` build image backend từ Dockerfile. Viết Java không tự tạo container, mở Docker Desktop cũng không tự build backend.

## 3. Cầm file nào lên thì tra cái gì?

| File/lệnh tối trước | Trước tiên tra | Phần dưới đây |
|---|---|---|
| `docker version`, `docker ps` | CLI, Engine, container status | C01 |
| `pom.xml`, `mvn ...` | XML, dependency/plugin, lifecycle | C02 |
| `TelecomManagementApplication.java` | package/import/class/main, annotation | C03 |
| `PingController.java`, DTO | annotation MVC, method, record, JSON | C04 |
| `compose.yaml`, `.env`, `Dockerfile`, `.dockerignore` | YAML, biến môi trường, build context, image/container | C05 |
| `application.yaml`, `application-home.yaml` | YAML và property của Spring Boot | C03/C06 |
| `V1__money_foundation.sql`, repository | SQL, FK/UQ/CHECK, JDBC/JPA | C06 |
| `MoneyService`, `@Transactional` | transaction/proxy/rollback | C07 |
| `FOR UPDATE`, lỗi deadlock | InnoDB, thứ tự khóa | C08 |
| Request store/fingerprint/replay | Java/SQL dùng để chống xử lý lặp | C09 |
| `SecurityConfig`/token lỗi | resource server, JWT validation/scope/caller | C10 |
| File `*Test.java`/`*IT.java` | assertion, test lifecycle, MySQL container | C11 |
| Log, metrics, lỗi timeout | stack trace, root cause, recovery | C12 |
| UC01/gói/job/report bổ sung | Chính sách đặc thù của dự án | C13–C15 |

Đừng tìm “cú pháp Spring” cho mọi thứ. Ví dụ `public` là Java; `@RestController` là Spring MVC; `spring.datasource.url` là cấu hình Boot; “GAME thiếu tiền không lấy MAIN” là quy tắc dự án.

## 4. C01 — Đọc lại buổi cài và mở Docker Desktop

**Sau N01A, mở lại:** các lệnh đã chạy và ảnh/log lỗi của máy nhà.

| Lệnh | Bạn đang hỏi điều gì? |
|---|---|
| `docker --version` | CLI có cài và version gì? |
| `docker version` | CLI nói chuyện được với Engine chưa? Có phần Client và Server không? |
| `docker compose version` | Compose plugin có dùng được không? |
| `docker ps` | Container nào đang chạy? |
| `docker ps -a` | Cả các container đã dừng là gì? |
| `docker images` | Máy đang có image nào? |

Đọc [hướng dẫn Docker Desktop trên Windows](https://docs.docker.com/desktop/setup/install/windows-install/) theo mục System requirements/WSL/Install/Start. Ghi lại trên máy nhà đã chọn Linux containers và backend WSL2 như thế nào; không cần hiểu sâu ảo hóa ngay buổi đầu.

**Tra theo lỗi đã thấy:** “docker client server connection error”, “Docker Desktop WSL 2 backend”; đối chiếu docs chính thức với lỗi thật. Không suy Docker hỏng chỉ vì `docker ps` đang trống.

**Tự giải thích:** Desktop/Engine/CLI khác nhau ở đâu? Tại sao đóng terminal không đồng nghĩa dừng container? Tối tiếp theo sang N01B nếu Client/Server đã nói chuyện được.

## 5. C02 — Đọc pom và các lệnh Maven

**Sau N01C, mở:** [pom hiện tại](../pom.xml), so với phần bạn đã sửa ở nhà.

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

Đọc từ ngoài vào: `dependency` khai báo một thư viện; `groupId`/`artifactId` xác định nó; `scope` cho biết phạm vi sử dụng. `plugin` là công cụ tham gia build, khác thư viện chương trình dùng. Parent/BOM có thể quản lý version, không phải dependency nào cũng tự thêm version.

| Lệnh | Ý nghĩa để bạn tra tiếp |
|---|---|
| `mvn compile` | Biên dịch code chính |
| `mvn test` | Chạy unit tests theo cấu hình |
| `mvn package` | Đi qua các bước trước rồi đóng gói |
| `mvn verify` | Đến pha kiểm chứng; integration tests chỉ chạy nếu plugin/test đã cấu hình |
| `mvn dependency:tree` | Nhìn cây thư viện thực tế |
| `mvn spring-boot:run` | Dùng Boot plugin chạy ứng dụng |

**Nguồn:** [Maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html), [Boot đóng gói executable JAR](https://docs.spring.io/spring-boot/maven-plugin/packaging.html).

**Bài đọc code:** tìm đúng dependency PostgreSQL đã bỏ và MySQL đã thêm; giải thích vì sao cần cả driver và Flyway module MySQL. Tìm mainClass trong plugin cũ/mới. Không nâng Boot chỉ vì tài liệu online hiển thị version khác.

## 6. C03 — Đọc main, bean và cấu hình Spring

**Sau N01C, mở:** `TelecomManagementApplication.java`, `application.yaml`, `application-home.yaml`.

```java
@SpringBootApplication
public class TelecomManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelecomManagementApplication.class, args);
    }
}
```

| Cú pháp/tên | Cần hiểu |
|---|---|
| `package`, `import` | Java đặt lớp ở namespace nào, dùng tên từ thư viện nào |
| `class`, `{...}`, `;` | Khai báo lớp, khối lệnh, kết thúc câu lệnh Java |
| `public static void main(String[] args)` | Điểm vào chương trình; lần lượt tra public/static/void/array |
| `@SpringBootApplication` | Annotation của Boot, không phải từ khóa Java |
| `SpringApplication.run(...)` | Khởi động Spring context/ứng dụng |
| `@Service`, `@Repository`, `@Bean` | Cách khai báo đối tượng do Spring quản lý; học khi chúng xuất hiện |
| Constructor injection | Nhận dependency qua constructor thay vì tự new tất cả trong controller |

**Đọc YAML:** `server:` là nhóm khóa; `port: 8080` là giá trị; thụt lề xác định cấp. `${DB_PASSWORD}` là placeholder Spring sẽ tìm ở môi trường/cấu hình. Profile `home` lựa chọn cấu hình, không được đổi quy tắc tiền.

**Nguồn:** [Spring IoC/bean](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html), [Boot profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html).

**Tra tiếp bằng đúng từ:** “Spring Boot component scan base package”, “constructor dependency injection”, “Spring Boot external configuration environment variables”. Vẽ ai tạo controller/service/repository. Không lưu số dư đang xử lý trong field mutable của singleton service.

## 7. C04 — Đọc controller và request HTTP đầu tiên

**Sau N01C/N03, mở:** PingController, rồi CreditRequest/DebitRequest/MoneyController.

```java
@RestController
@RequestMapping("/api/v1/dev")
public class PingController {
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "pong");
    }
}
```

**Đọc từng phần:** annotation cấp class đặt đường dẫn chung; annotation method ghép tiếp thành `/api/v1/dev/ping`; `Map<String,String>` là Java generic; `Map.of` tạo dữ liệu; Spring chuyển return value thành JSON. Sau này `@PostMapping`, `@RequestBody`, `@PathVariable`, `@Valid` đọc từ chính controller tiền của bạn.

**Nguồn:** [Spring REST guide](https://spring.io/guides/gs/rest-service/).

**Tự tra và ghi ví dụ:** GET khác POST; path khác JSON body/header; HTTP400/401/403/404/409/422/503 nghĩa gì theo API dự án. Một DTO `record CreditRequest(...)` khác entity lưu DB. Đừng thêm trường callerId/password vào body để tự xác nhận quyền.

**Tối thử:** đổi message của ping, build/run lại rồi gọi URL. Quan sát thay Java có đòi build lại image không nếu đang chạy container từ JAR cũ.

## 8. C05 — Đọc Compose, .env và Dockerfile từng dòng

### 8.1 Sau N01B: đọc compose.yaml

```yaml
services:
  mysql:
    image: mysql:8.0.46
    ports:
      - "127.0.0.1:3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
volumes:
  mysql_data:
```

Đây là **một phần** của file đầy đủ bên hướng dẫn ở nhà. `services` nhóm ứng dụng; `mysql` là tên service; `image` chọn mẫu sẵn có; dấu `-` tạo phần tử danh sách; `ports` nối cổng host/container; named volume giữ dữ liệu tại thư mục DB trong container. `volumes` cuối file khai báo tên volume. Hai khóa cùng tên ở cấp khác nhau có vai trò khác nhau.

Tra thêm `environment`, `healthcheck`, `depends_on`, `condition: service_healthy`, `build`, `restart` **khi chúng xuất hiện** trong file. Đọc [Compose quickstart](https://docs.docker.com/compose/gettingstarted/) và [startup/health](https://docs.docker.com/compose/how-tos/startup-order/).

### 8.2 Biến môi trường có hai nơi giải thích

- Trong Compose, `${DB_PASSWORD}` được Compose thay giá trị từ shell/.env theo cơ chế của Compose.
- Trong application YAML, placeholder được Spring giải quyết lúc Java chạy. Java từ IDE không tự đọc file .env của Compose.
- `$$` trong Compose dùng khi muốn giữ một dấu `$` cho bước xử lý sau. Chỉ học nó khi gặp ví dụ cần dùng, không chép vào mọi chỗ.

Tra [Compose interpolation](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/). `.env` là dữ liệu local, không phải Java; `.env.example` chỉ mô tả tên biến và chỗ điền.

### 8.3 Sau N01D: đọc Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/telecom-management-system-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

`FROM` chọn nền Java; `WORKDIR` đặt thư mục; `COPY` lấy JAR từ build context; `EXPOSE` mô tả cổng, không tự publish ra host; `ENTRYPOINT` chạy Java khi container bắt đầu. Đây là mẫu học một stage, chưa phải kết luận image tối ưu cho bàn giao.

Trong `docker build -t telecom-backend:dev .`, `-t` đặt tên/tag; dấu `.` chọn thư mục hiện tại làm build context. `.dockerignore` loại nội dung khỏi context. Nếu ignore `target/` mà Dockerfile cần COPY JAR từ đó, build sẽ lỗi. Tra [Dockerfile reference](https://docs.docker.com/reference/dockerfile/).

**Câu hỏi phải trả lời từ file của mình:** image backend được build từ đâu? Chỗ nào vẫn là image MySQL tải sẵn? IDE kết nối localhost, backend container kết nối `mysql` vì sao? Sau sửa code tại sao container vẫn chạy bản cũ?

## 9. C06 — Đọc SQL/Flyway/repository từ buổi dựng bảng

**Sau N02, mở:** migration SQL và repository/store đầu tiên. Không phải viết toàn DB một lần.

Tra `CREATE TABLE`, kiểu cột, `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK`, index và `SELECT/INSERT/UPDATE`. Viết bằng lời câu SQL đang chạy. `WHERE id = ?` nghĩa gì, ai truyền `?`, vì sao không ghép thẳng input vào SQL?

Với Flyway, hiểu `V1__money_foundation.sql`: V là versioned migration, số thứ tự, hai dấu gạch dưới, mô tả, đuôi SQL. Mở bảng `flyway_schema_history` sau khi tối trước chạy. Đọc [Flyway migrations](https://documentation.red-gate.com/flyway/flyway-concepts/migrations).

Nếu code dùng `@Entity/@Id/@Table` thì tra JPA mapping; nếu dùng JdbcTemplate/PreparedStatement thì tra JDBC/query binding. Theo cách đã chọn trong code, không học đồng thời mọi phong cách repository. `ddl-auto=validate` kiểm schema, không thay migration.

**Đầu ra:** nối tên field Java với cột DB, hiểu vì sao account phải thuộc Subscriber và debit phải có cycle; tối tiếp theo thử một FK/UQ/CHECK sai bằng test.

## 10. C07 — Đọc transaction của chính lệnh cộng tiền

**Sau N04/N05, mở:** OperationCoordinator, MoneyService, nơi đặt `@Transactional` hoặc TransactionTemplate.

Tra annotation và tham số đang có: propagation/isolation/rollbackFor; bean proxy và self-invocation. Gạch chân ranh giới claim transaction khác finalizer transaction; đánh dấu balance/ledger/outcome phải cùng commit. Đọc [Spring @Transactional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

**Đọc bằng câu chuyện:** “Lệnh này lấy số dư cũ ở đây; cộng ở đây; ghi ledger ở đây; nếu ném lỗi ở đây thì cả ba còn hay mất?”. Flush chưa phải commit. Java gọi method cùng object không mặc nhiên đi qua transaction proxy.

**Tối thử:** test cố lỗi sau update balance, trước insert ledger; xem DB có rollback. Lỗi nghiệp vụ cần lưu FAILED bền vững, không lưu trong transaction đã bị rollback rồi tưởng kết quả còn.

## 11. C08 — Đọc khóa DB và test hai request cùng lúc

**Sau N06/N07:** lấy hai transaction trong log/test của mình ra vẽ timeline. Tra `SELECT ... FOR UPDATE`, row lock, READ COMMITTED, index, deadlock1213, lock timeout1205 trong [MySQL locking reads](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html).

Với dư100.000, hai lệnh mới mỗi80.000, chỉ một lệnh được thành công nếu cả hai được xử lý đầy đủ trên DB khỏe. Chỉ ra dòng nào giữ khóa account, T2 đợi ở đâu, sau đợi đọc số dư nào. `synchronized` của Java không thay khóa/ràng buộc DB giữa nhiều instance.

**Tối thử:** hai connection/transaction riêng, barrier trước chỗ tranh khóa, đối soát cuối. Không đợi tất cả thread cùng qua barrier đặt sau khi một thread đã độc quyền giữ lock.

## 12. C09 — Đọc requestId/hash/replay từ lệnh gửi lại

**Sau N04/N08:** mở request record, canonical builder và unique key. Tra Java record/enum/UUID/MessageDigest/JSON serialization theo code thực tế, rồi đọc lại thiết kế idempotency của dự án.

Viết bảng: caller/operation/ID giống hay khác; payload giống hay khác; trạng thái cũ là PROCESSING/SUCCEEDED/FAILED; code sẽ return hay tác động tiền. Token và correlationId không phải nội dung tiền mới. Một lần gửi HTTP là attempt, một ý định nghiệp vụ là logical request.

**Tối thử:** cùng ID gửi20lần chỉ một effect; đổi amount bị conflict; FAILED thiếu tiền rồi nạp vẫn FAILED nếu replay ID cũ. HTTP timeout không chứng minh rollback; cần lookup/cùng ID.

## 13. C10 — Đọc từng phần SecurityConfig/token

**Sau N03:** mở SecurityConfig và cấu hình JWT. Tra `SecurityFilterChain`, resource server, issuer-uri/JWKS/audience, authority/scope, authentication principal; đọc [Spring Security JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html).

Viết rõ IdP cấp token, Telecom xác minh token. Giải mã payload không đủ xác thực. `callerId` suy ra từ claim đã kiểm; lookup request của caller khác trả404 theo contract. Tra chữ ký method/API đúng phiên bản dependency của dự án, không chép tutorial dùng Spring Security cũ.

**Tối thử:** thiếu token401, thiếu scope403, callerB tra requestA404. Khi bật security, test ping/health có thể đổi response theo cấu hình; đọc rule thay vì bỏ toàn bộ security cho tiện.

## 14. C11 — Đọc test để hiểu code đang được bảo vệ điều gì

**Sau mỗi buổi:** chọn một test xanh và một test từng đỏ; chỉ phần arrange/act/assert, exception mong đợi và dữ liệu DB sau chạy. Tra `@Test`, assertions, parameterized test, Mockito và test lifecycle khi code thật có dùng.

Test unit không DB khác test MySQL thật. Testcontainers cần Docker ở nhà; tên `*IT` không tự khiến Maven chạy nếu chưa cấu hình Failsafe. Đọc [JUnit](https://docs.junit.org/current/user-guide/) và [Testcontainers MySQL](https://java.testcontainers.org/modules/databases/mysql/).

**Tối thử:** chủ động thay sai một điều kiện để thấy test bắt được lỗi, rồi trả code về đúng. Không đánh dấu “đã test concurrency” chỉ vì fake repository trả kết quả mong đợi.

## 15. C12 — Đọc lỗi tối qua và tìm root cause

| Điều thấy | Đọc/tra trước | Tối kiểm lại |
|---|---|---|
| `docker` không được nhận diện | CLI/PATH, mở terminal mới sau cài | `docker --version` |
| Không nối được Docker daemon | Desktop/Engine/WSL context | `docker version` có Server |
| `manifest unknown` khi pull image | Tag/registry thực có không | Giữ version quyết định, ghi blocker nếu tag thiếu |
| `port is already allocated` | Host port nào đang bị dùng | Đổi mapping local có ghi nhận, không đổi port trong app mù quáng |
| `Connection refused` | Hostname/port/service ready | Log/ps và đúng URL host hoặc container |
| `Access denied for user` | User/password và DB đã init trước đó | Biến env mới không tự sửa user trong volume cũ |
| `COPY ... not found` | JAR tồn tại, context và dockerignore | Build JAR trước image |
| `no main manifest attribute` | Boot repackage/plugin và đúng JAR | Kiểm executable JAR |
| `Table ... doesn't exist` | Flyway có chạy đúng DB/profile không | History/schema và log migration |
| Tests xanh nhưng số test bằng0 | Tên test/plugin/include/exclude | Kiểm Surefire/Failsafe report |
| HTTP timeout | Chưa biết commit outcome | Lookup cùng requestId, xem request/ledger |

Đọc lỗi đầu tiên có ý nghĩa và chuỗi `Caused by`, không sửa theo dòng cuối stack trace. Ghi nguyên lỗi nhưng che secrets/token trước khi đưa vào tài liệu/trao đổi. Không xóa volume hoặc DB như một bước chữa lỗi mặc định.

## 16. C13–C15 — Khi tiền đã chạy thì học gì tiếp?

| Mã | Mở file code mới ở nhà | Tra syntax/kỹ thuật theo file | Quy tắc dự án cần hiểu |
|---|---|---|---|
| C13 | ActivationService, SubscriptionService, CancelService, RenewalWorker | Java time, enum/state, transaction, scheduler và version check | HybridWAITING, fee+kỳ nguyên tử, ngày lịch, hủy cuối toàn thời gian đã trả và receipt sau confirmed commit |
| C14 | Rabbit consumer, Redis cache config, compose bổ sung | Listener/ack/redelivery, cache/TTL, service env/network | MQ chỉ tín hiệu, Redis chỉ cache, DB giữ tính đúng |
| C15 | ReportQueryService, DailySummaryJob, k6/runbook | SQLgroup/pagination, cursor, JS k6 syntax, metrics/backup | recordedAt, fixed source, late adjustment, TPS mới/đối soát/≥500K |

Nguồn: [RabbitMQ confirms/ack](https://www.rabbitmq.com/docs/confirms), [k6 arrival-rate](https://grafana.com/docs/k6/latest/using-k6/scenarios/concepts/arrival-rate-vu-allocation/), [protocol tải dự án](05-testing/performance-test.md). Học chúng vào lúc code thực sự cần, không phải điều kiện để bắt đầu Docker/Spring.

## 17. Bảng ghép buổi ở nhà → buổi học lại ở công ty

| Tối vừa làm | Sáng hôm sau mở | Chỉ cần hiểu trước buổi tối tiếp |
|---|---|---|
| N01A cài/kiểm tools | Lệnh/log + C01/C02 | Desktop/Engine/CLI/Java/Maven và lỗi cài |
| N01B chạy MySQL Compose | compose/.env + C05 | Từng khóa YAML, port/volume/env và vòng đời DB |
| N01C tạo Spring/Ping | pom/main/YAML/controller + C02–C04 | Mỗi annotation/dependency/property đã dùng |
| N01D build backend container | Dockerfile/.dockerignore/Compose + C05 | Build JAR → image → container, hostname và rebuild |
| N02 schema/fixtures | SQL/history/store + C06 | Câu SQL/ràng buộc và mapping đầu tiên |
| N03 security | SecurityConfig + C10 | Token/scope/caller và lỗi401/403/404 |
| N04 registry | Canonical/request store + C07/C09 | Claim/unique/replay/finalizer |
| N05 credit/N06 debit | Controller/service/SQL + C04/C07/C08 | Quy tắc tiền và transaction thực tế |
| N07–N09 tests/recovery/demo | Test + log + C09/C11/C12 | Test đã chứng minh gì, lỗi nào còn chưa hiểu |
| N10–N16 phần còn lại | File mới + C13–C15 | Một thành phần mới mỗi lần |

N không phải số ngày bắt buộc; một N có thể kéo dài nhiều tối. Nếu tối trước chưa tiến vì lỗi Docker, sáng tiếp tục học lỗi đó, không nhảy sang idempotency vì bảng lịch.

## 18. Mẫu ghi chú ngay trong hai file này

| File/dòng/lệnh tối qua | Từ/cú pháp chưa hiểu | Đã tra ở đâu | Giải thích bằng lời mình | Thử gì tối nay? |
|---|---|---|---|---|
| Ví dụ compose: `3306:3306` | Hai số thuộc bên nào? | C05 + docs Compose | Bên host khác bên container | Gọi từ IDE và từ backend container |

Mỗi thứ tra xong phải trả lời được: **nó là gì → dòng này làm gì → bỏ/đổi nó ảnh hưởng gì → cách kiểm lại ở nhà**. Mục tiêu không phải nhớ hết syntax; là biết đọc code mình đang dùng và tra đúng nơi khi chưa nhớ.
