# Jenkins CI cho repository này

Repository: https://github.com/hminqn/baitapdientoandammay

Jenkinsfile dùng Maven 3.8.6 và JDK21. Ứng dụng biên dịch với release 11, chạy 2 unit test, lưu JAR và in Hello Jenkins!.

Trong Manage Jenkins > Tools, tạo Maven tên `Maven 3.8.6` và JDK tên `JDK21`. Agent cần Git. Tạo job Pipeline script from SCM, Git URL repository trên, nhánh `*/main`, Script Path `Jenkinsfile`.

Chạy Build Now lần đầu để đăng ký lịch pollSCM khoảng 5 phút. Sau mỗi push có thay đổi, Jenkins sẽ tự build ở chu kỳ kiểm tra tiếp theo. Không cần mở Jenkins localhost ra Internet.

Pipeline HelloWorld độc lập nằm trong Jenkinsfile.hello.

## Kiểm tra SCM polling

Commit tài liệu này dùng để kiểm tra Jenkins phát hiện thay đổi trên nhánh main sau push. Kết quả thực tế được đối chiếu trong job MavenTestProject, mục Changes và nguyên nhân Started by an SCM change.
