Dựa trên các đối tượng nghiệp vụ đã phân tích, hệ thống được chuẩn hóa
và ánh xạ sang các bảng trong cơ sở dữ liệu quan hệ như sau:

- Entity User được ánh xạ thành bảng users.
- Entity Project được ánh xạ thành bảng projects.
- Entity Task được ánh xạ thành bảng tasks.

Quan hệ giữa các bảng:
- Một User có thể được gán nhiều Task (1–N), thể hiện qua khóa ngoại
  tasks.user_id (có thể NULL).
- Một Project có thể chứa nhiều Task (1–N), thể hiện qua khóa ngoại
  tasks.project_id (NOT NULL).

Các trạng thái ProjectStatus và TaskStatus được lưu dưới dạng VARCHAR
kết hợp với CHECK constraint để đảm bảo tính hợp lệ dữ liệu.
