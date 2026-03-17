# Task Business Rules - Week 5

## 1. Task Status Flow

Task có 3 trạng thái:

- TODO
- IN_PROGRESS
- DONE

### Luồng hợp lệ:

TODO -> IN_PROGRESS -> DONE

### Không hợp lệ:

- TODO -> DONE (phải qua IN_PROGRESS trước)
- DONE -> TODO
- DONE -> IN_PROGRESS

Khi task đã DONE:
- Không được cập nhật status
- Không được assign lại user


---

## 2. Create Task Rule

- projectId phải tồn tại
- Task mặc định status = TODO
- Task phải thuộc 1 Project


---

## 3. Assign Task Rule

- Task phải tồn tại
- User phải tồn tại
- User phải thuộc Project của Task
- Không được assign nếu task đã DONE


---

## 4. Update Status Rule

- Task phải tồn tại
- Không được update nếu task đã DONE
- Phải tuân theo status flow


---

## 5. List API

- Có thể list task theo Project
- Có thể list task theo User