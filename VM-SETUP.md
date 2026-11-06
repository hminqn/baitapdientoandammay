# Chạy Jenkins trong máy ảo Ubuntu

Bài thực hành có thể chạy trong Ubuntu Server trên VMware Workstation với cấu hình 4 vCPU, RAM 4 GB, đĩa 40 GB và mạng NAT.

## Công cụ

- Java 21, Git và unzip trong Ubuntu.
- Apache Maven 3.8.6, đặt tên công cụ Jenkins là `Maven 3.8.6`.
- JDK `/usr/lib/jvm/java-21-openjdk-amd64`, đặt tên công cụ Jenkins là `JDK21`.
- Jenkins cùng plugin Pipeline, Git, Maven và JUnit.

Jenkins chạy dưới dạng dịch vụ systemd `jenkins-practice`, cổng 8080. Truy cập bằng địa chỉ IP của máy ảo từ máy host; không cần chuyển tiếp cổng ra Internet để dùng SCM polling.

## Job

Tạo Pipeline script from SCM, repository `https://github.com/hminqn/baitapdientoandammay.git`, nhánh `*/main`, Script Path `Jenkinsfile`. Jenkinsfile tự chọn `sh` trên Ubuntu, build Java, chạy 2 unit test, lưu JAR và chạy chương trình in `Hello Jenkins!`.

Sau lần build đầu, Jenkins kiểm tra thay đổi trên GitHub khoảng mỗi 5 phút. Commit tài liệu này dùng để xác nhận build tự động trong máy ảo sau push.

Job HelloWorld độc lập sử dụng `Jenkinsfile.hello`.
