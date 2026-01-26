## User

| Field        | Type      | Required | Description                  |
|-------------|----------|----------|------------------------------|
| id          | Long     | Yes      | Khóa chính                   |
| username    | String   | Yes      | Tên đăng nhập, không trùng   |
| email       | String   | Yes      | Email người dùng, unique     |
| password    | String   | Yes      | Mật khẩu (đã mã hóa)         |
| createdAt  | LocalDateTime | Yes | Ngày tạo                     |
| active      | Boolean  | Yes      | Trạng thái hoạt động         |

## Project

| Field        | Type      | Required | Description                 |
|-------------|----------|----------|-----------------------------|
| id          | Long     | Yes      | Khóa chính                  |
| name        | String   | Yes      | Tên dự án                   |
| description | String   | No       | Mô tả dự án                 |
| startDate   | LocalDate| Yes      | Ngày bắt đầu                |
| endDate     | LocalDate| No       | Ngày kết thúc               |
| status      | ProjectStatus | Yes | Trạng thái dự án            |


## Task

| Field        | Type        | Required | Description                          |
|-------------|------------|----------|--------------------------------------|
| id          | Long       | Yes      | Khóa chính                           |
| title       | String     | Yes      | Tiêu đề công việc                    |
| description | String     | No       | Nội dung chi tiết                    |
| status      | TaskStatus | Yes      | Trạng thái Task                      |
| priority    | Integer    | Yes      | Độ ưu tiên (1–5)                     |
| dueDate     | LocalDate  | No       | Hạn hoàn thành                       |
| projectId   | Long       | Yes      | FK tới Project                       |
| userId      | Long       | No       | FK tới User (được gán)               |

## Validation Rules

### User
- username không rỗng
- email đúng định dạng
- email không trùng

### Project
- startDate <= endDate (nếu có)
- name không rỗng

### Task
- title không rỗng
- priority từ 1 đến 5
- projectId không null
- userId phải tồn tại nếu được gán
