# Bài thực hành Pipeline với Jenkins

Bộ bài làm gồm hai pipeline theo PDF: HelloWorld và Java Maven có unit test. Vì chưa có project Java sẵn, bài này cung cấp ứng dụng mẫu in `Hello Jenkins!`.

## 1. Các tệp

| Tệp | Vai trò |
| --- | --- |
| Jenkinsfile.hello | Pipeline hai stage theo bài mẫu |
| Jenkinsfile | Checkout, Build, Test, Package, Run; công bố kết quả test và lưu JAR |
| pom.xml | Cấu hình Maven, Java 11, JUnit 5 |
| src/main/java/vn/cmc/App.java | Chương trình Hello Jenkins |
| src/test/java/vn/cmc/AppTest.java | Hai unit test kiểm tra lời chào và đầu ra console |

## 2. Chuẩn bị Jenkins

1. Cài Jenkins và chạy dịch vụ tại `http://localhost:8080`.
2. Jenkins hiện hành cần Java runtime được hỗ trợ (Java 21 hoặc 25); Java 11 trong đề chỉ dùng để build ứng dụng. Controller và tiến trình agent cần runtime phù hợp, độc lập với JDK build.
3. Trong Manage Jenkins > Plugins, cài Pipeline, Git, JUnit; cài Pipeline: Stage View nếu cần giao diện stage như đề. PDF còn liệt kê Maven Integration và Blue Ocean, nhưng pipeline gọi Maven bằng lệnh trực tiếp không cần hai plugin này để chạy.
4. Cài Git trên máy agent và kiểm tra `git --version`.
5. Vào Manage Jenkins > Tools (bản cũ: Global Tool Configuration):
   - Maven: đặt tên chính xác `Maven 3.8.6`, trỏ tới Maven 3.8.6 hoặc chọn tự cài phiên bản đó.
   - JDK: đặt tên chính xác `JDK21`, trỏ tới thư mục cài JDK 21 trên agent. Không nhập đường dẫn thư mục `bin`.
6. Agent phải truy cập được GitHub và Maven Central; tài khoản dịch vụ Jenkins cần quyền chạy Git, Java, Maven và ghi workspace.

## 3. Phần 1: HelloWorldPipeline

1. New Item > nhập `HelloWorldPipeline` > Pipeline > OK.
2. Trong Pipeline > Definition chọn Pipeline script.
3. Dán toàn bộ nội dung `Jenkinsfile.hello`, Save > Build Now.
4. Console Output dự kiến có:

```text
Hello World
Hello Again
A third time Hello
Hello Jenkins!
Finished: SUCCESS
```

Đây là kết quả mong đợi, chưa phải log của lần chạy thực tế.

## 4. Phần 2: Đưa project lên GitHub

Tạo repository rỗng tên `my-maven-app` trên GitHub. Mở terminal ngay trong thư mục chứa README này và thay `YOUR_USERNAME` bằng tên tài khoản thật:

```powershell
git init
git branch -M main
git add .
git commit -m "Add Java Maven app and Jenkins pipelines"
git remote add origin https://github.com/hminqn/baitapdientoandammay.git
git push -u origin main
```

Nếu Git yêu cầu danh tính, cấu hình tên và email của bạn trước khi commit. Đăng nhập GitHub bằng trình quản lý thông tin xác thực; không ghi token vào mã nguồn. Các lệnh trên là hướng dẫn, chưa được thực thi tự động.

## 5. Tạo MavenTestProject

1. Jenkins > New Item > `MavenTestProject` > Pipeline > OK.
2. Definition: Pipeline script from SCM.
3. SCM: Git; Repository URL là URL repository vừa tạo. Nếu repository private, chọn Credentials có quyền đọc.
4. Branch Specifier: `*/main`; Script Path: `Jenkinsfile`.
5. Save > Build Now để chạy lần đầu và đăng ký lịch kiểm tra thay đổi.
6. Kiểm tra Console Output, Stage View, Test Result và mục Artifacts.

Pipeline tự kiểm tra Git khoảng mỗi 5 phút bằng `pollSCM('H/5 * * * *')`; chỉ có commit mới mới chạy build. Không cần GitHub truy cập được localhost. Để kiểm tra, sửa lời chào và cập nhật kỳ vọng tương ứng trong test, commit rồi push; đợi lần poll tiếp theo.

Nếu cần kích hoạt ngay bằng webhook, cài GitHub plugin, thay mục triggers bằng `triggers { githubPush() }` và cấu hình webhook GitHub đến `https://TEN-MIEN-JENKINS/github-webhook/`, Content type `application/json`, sự kiện push. Địa chỉ Jenkins phải truy cập được từ GitHub; `localhost:8080` không đáp ứng điều này.

## 6. Chạy thủ công khi có JDK và Maven

```powershell
mvn -B -ntp clean verify
java -jar target/my-maven-app-1.0.0.jar
```

Kết quả mong đợi: 2 tests, 0 failures, 0 errors; chương trình in `Hello Jenkins!`. Báo cáo XML ở `target/surefire-reports/`. JAR ở `target/my-maven-app-1.0.0.jar`.

## 7. Giải thích bài làm

- `agent any`: sử dụng agent khả dụng; agent được chọn phải có các công cụ đã cấu hình.
- `tools`: chọn Maven và JDK theo tên trong Jenkins.
- Checkout: xóa workspace cũ và lấy đúng source của job SCM.
- Build: `mvn clean compile` biên dịch Java.
- Test: `mvn test` chạy JUnit và công bố XML kể cả khi test thất bại.
- Package: đóng gói JAR, lưu artifact trên Jenkins; bỏ chạy lại test vì stage trước đã kiểm tra.
- Run: chạy JAR để hiện lời chào trong console.
- `sh` trên Unix và `bat` trên Windows giúp cùng Jenkinsfile chạy được trên hai loại agent.
- `post cleanup`: dọn workspace sau khi test report và artifact đã được lưu trên Jenkins.

Để chứng minh pipeline phát hiện lỗi, có thể tạm đổi lời chào trong App.java mà giữ nguyên test, commit và push. Build sẽ thất bại ở Test, các stage sau bị bỏ qua và Test Result thể hiện test lỗi. Sau đó khôi phục lời chào, commit và push để có build thành công.

## 8. Minh chứng cần chụp sau khi chạy

1. Job HelloWorldPipeline với hai stage thành công.
2. Console Output có các lời chào và Finished: SUCCESS.
3. Cấu hình SCM của MavenTestProject (che thông tin xác thực).
4. Các stage Maven thành công.
5. Test Result có 2 tests và không có lỗi.
6. Artifact JAR và console có Hello Jenkins!.
7. Build mới tự chạy sau commit push, có nguyên nhân Started by an SCM change.

Đã chụp ảnh minh chứng trực tiếp trên Jenkins, gồm HelloWorld, Maven, kết quả test, artifact và repository GitHub. Bộ ảnh cùng báo cáo được lưu trong thư mục output trên máy thực hành.

## 9. Lỗi thường gặp

| Lỗi | Cách xử lý |
| --- | --- |
| Tool type/name chưa được cấu hình | Tạo đúng tên Maven 3.8.6 và JDK21 trong Tools |
| mvn/git không được nhận diện | Kiểm tra công cụ trên agent và quyền tài khoản dịch vụ Jenkins |
| sh không chạy trên Windows | Dùng Jenkinsfile có nhánh bat đã cung cấp |
| Không tìm thấy Jenkinsfile | Đặt file ở gốc repository; Script Path đúng chữ hoa/thường |
| No test report files found | Xem lỗi Maven trước đó, kiểm tra Test đã được chạy và tải dependency thành công |
| Push nhưng không có build | Chạy Build Now lần đầu, kiểm tra branch main và chờ chu kỳ poll |
| UnsupportedClassVersionError khi khởi động Jenkins | Dùng runtime hỗ trợ phiên bản Jenkins, tách riêng JDK21 để build |

## 10. Trạng thái xác minh

Đã chạy thực tế ngày 06/11/2026: Maven clean verify thành công, 2 test đạt, JAR in Hello Jenkins!. HelloWorldPipeline #1 và MavenLocalDemo #1 đều SUCCESS. Đã kết nối repository này với job MavenTestProject trên Jenkins cục bộ; build #2 checkout từ GitHub và hoàn tất SUCCESS. Jenkinsfile trên nhánh main dùng Maven 3.8.6, JDK21 và biên dịch release 11; lịch pollSCM kiểm tra thay đổi khoảng mỗi 5 phút. Log và công cụ nằm trong jenkins-runtime trên máy thực hành. Xem CI-SETUP.md để tái lập cấu hình SCM.

Tài liệu đối chiếu:
- https://www.jenkins.io/doc/book/pipeline/syntax/
- https://www.jenkins.io/doc/book/platform-information/support-policy-java/
- https://www.jenkins.io/doc/pipeline/steps/junit/
