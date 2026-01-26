## TaskStatus

| Enum Value   | Meaning                    | Terminal |
|-------------|----------------------------|----------|
| TODO        | Chưa bắt đầu               | No       |
| IN_PROGRESS | Đang thực hiện             | No       |
| DONE        | Hoàn thành                 | Yes      |
| CANCELLED   | Bị hủy                     | Yes      |

## TaskStatus Flow

TODO → IN_PROGRESS  
TODO → CANCELLED  
IN_PROGRESS → DONE  
IN_PROGRESS → CANCELLED

## ProjectStatus

| Enum Value | Meaning            |
|-----------|--------------------|
| PLANNING  | Đang lên kế hoạch  |
| ACTIVE    | Đang thực hiện     |
| FINISHED  | Đã hoàn thành      |
| ARCHIVED  | Lưu trữ            |

## Enum Usage

- Task.status → TaskStatus
- Project.status → ProjectStatus

