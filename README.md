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
Project hien chua them framework hay thu vien kiem thu.
