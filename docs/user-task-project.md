1. Mục tiêu hệ thống

Hệ thống được xây dựng nhằm quản lý công việc (Task) trong các dự án (Project).
Người dùng (User) có thể tham gia thực hiện các Task được giao.
+ Hệ thống hỗ trợ:
+ Tạo và quản lý Project
+ Tạo, cập nhật, xóa Task
+ Gán Task cho User
+ Theo dõi trạng thái Task và Project

2. Các đối tượng chính

Hệ thống gồm 3 đối tượng cốt lõi:

+ User: người sử dụng hệ thống
+ Project: dự án, dùng để nhóm các Task
+ Task: công việc cụ thể trong Project

3. Mô tả chi tiết từng đối tượng
   3.1 User

User đại diện cho người sử dụng hệ thống.

Đặc điểm nghiệp vụ:

+ Mỗi User có định danh duy nhất (id)
+ Username và email không được trùng
+ Một User có thể được gán nhiều Task
+ Một User có thể không được gán Task nào

Vai trò trong hệ thống:

+ Nhận Task
+ Thực hiện Task được giao

3.2 Project

Project dùng để quản lý và nhóm các Task liên quan với nhau.

Đặc điểm nghiệp vụ:

+ Mỗi Project có tên và trạng thái
+ Một Project có thể chứa nhiều Task
+ Task bắt buộc phải thuộc về một Project
+ Project có vòng đời rõ ràng (lên kế hoạch → thực hiện → kết thúc → lưu trữ)

Trạng thái Project:

+ PLANNING: đang lên kế hoạch
+ ACTIVE: đang thực hiện
+ FINISHED: đã hoàn thành
+ ARCHIVED: lưu trữ

Luật nghiệp vụ:

+ Không được thêm Task mới nếu Project đang ở trạng thái ARCHIVED

3.3 Task

Task là một công việc cụ thể trong Project.

Đặc điểm nghiệp vụ:

+ Task phải thuộc về một Project
+ Task có thể được gán cho một User hoặc chưa gán cho ai
+ Task có trạng thái và luồng xử lý rõ ràng
+ Trong cùng một Project, không cho phép Task trùng tiêu đề

Trạng thái Task:

+ TODO: chưa bắt đầu
+ IN_PROGRESS: đang thực hiện
+ DONE: hoàn thành
+ CANCELLED: bị hủy

Trạng thái mặc định:

+ Task mới tạo có trạng thái TODO

4. Quan hệ giữa User – Task – Project
   
4.1 Quan hệ User – Task

+ Một User có thể được gán nhiều Task
+ Một Task chỉ được gán cho tối đa một User
+ Task có thể không được gán User
+ Quan hệ:
User (1) —— (n) Task

4.2 Quan hệ Project – Task

+ Một Project có thể có nhiều Task
+ Một Task bắt buộc thuộc về một Project duy nhất
+ Quan hệ:
Project (1) —— (n) Task

5. Luồng xử lý trạng thái Task
   
5.1 Luồng hợp lệ

+ TODO → IN_PROGRESS
+ TODO → CANCELLED
+ IN_PROGRESS → DONE
+ IN_PROGRESS → CANCELLED

5.2 Luồng không hợp lệ

+ DONE → IN_PROGRESS
+ DONE → TODO
+ CANCELLED → bất kỳ trạng thái nào khác

Quy tắc:

+ DONE và CANCELLED là trạng thái kết thúc (terminal)
+ Task ở trạng thái kết thúc không được thay đổi tiếp

6. Luật nghiệp vụ tổng hợp
   
User

+ Username không được rỗng
+ Email đúng định dạng và không trùng

Project

+ Tên Project không được rỗng
+ Không được thêm Task khi Project = ARCHIVED

Task

+ Tiêu đề Task không được rỗng
+ Task phải thuộc một Project
+ Priority (nếu có) phải trong khoảng hợp lệ
+ Không cho phép 2 Task trùng tiêu đề trong cùng Project
+ User được gán cho Task phải tồn tại

7. Mục đích của tài liệu này

Tài liệu này được sử dụng để:
+ Làm cơ sở thiết kế class OOP
+ Viết constructor và validate logic
+ Kiểm tra nghiệp vụ trước khi code
+ Giải thích logic hệ thống khi review hoặc chấm bài

