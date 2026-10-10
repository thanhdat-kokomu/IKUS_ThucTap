IKUS_Task Manager

Đây là dự án backend quản lý công việc được xây dựng bằng Spring Boot, cung cấp các RESTful API phục vụ cho việc quản lý dự án, phân công công việc và theo dõi tiến độ thực hiện.

Chức năng:
Quản lý tài khoản: Đăng ký, đăng nhập và bảo mật bằng JWT token.
Phân quyền (Role-based): Sử dụng Spring Security để chia quyền:
User: chỉ có thể xem và chỉnh sửa task của họ.
Manager: có thể xem ,chỉnh sửa,tạo mới,xóa tất cả các task.
Quản lý dự án (Project): CRUD project.
Quản lý công việc (Task): CRUD task và cập nhật trạng thái (TODO, IN_PROGRESS, DONE).

Ứng dụng sử dụng:
Code: intellij IDEA
Database: SQL Server
TestAPI: Postman

Stack công nghệ:
Ngôn ngữ: Java 17
Framework: Spring Boot 3.x
Database: SQL Server
Authentication: Spring Security & JWT
Document API: Swagger UI

Hướng dẫn chạy dự án ở máy Local:
B1 Clone source code về máy.

B2 Mở SQL Server, đưa file thuctap.sql vào và chạy toàn bộ( creatdatabase -> use -> ctrl+A+chạy)

B3 Mở file cấu hình src/main/resources/application-dev.properties, sửa lại config kết nối database (username, password) cho khớp với máy của bạn.

B4 Mở maven chạy clean và package để tạo task-management-0.0.1-SNAPSHOT.jar và sử dụng terminal của app gõ: java -jar target/task-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

B5 Sau khi server start ở port 8080, mở trình duyệt truy cập vào http://localhost:8080/swagger-ui/index.html để xem danh sách API và test trực tiếp.
