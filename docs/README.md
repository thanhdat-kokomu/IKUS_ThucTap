REVIEW QUAN HỆ + GIẢI THÍCH:
+ Quan hệ giữa Project và Task là quan hệ một – nhiều (1–N).

+ Mỗi Project có thể chứa nhiều Task khác nhau. Ngược lại, mỗi Task
bắt buộc phải thuộc về đúng một Project duy nhất. Điều này được
thể hiện thông qua khóa ngoại project_id trong bảng Task.

+ Thiết kế này phù hợp với nghiệp vụ hệ thống, trong đó Task không
thể tồn tại độc lập mà luôn gắn với một Project cụ thể.

+ Quan hệ giữa User và Task là quan hệ một – nhiều (1–N).

+ Một User có thể được gán nhiều Task khác nhau. Tuy nhiên, một Task
chỉ được gán cho tối đa một User tại một thời điểm và có thể
chưa được gán cho User nào. Do đó, khóa ngoại assigned_user_id
trong bảng Task được cho phép giá trị NULL.

+ Thiết kế này phản ánh đúng nghiệp vụ phân công công việc của hệ thống.

+ Hệ thống không sử dụng bảng trung gian cho quan hệ User – Task
hoặc Project – Task vì các quan hệ đều là một – nhiều (1–N).

+ Việc sử dụng bảng trung gian chỉ cần thiết trong trường hợp
quan hệ nhiều – nhiều (N–N). Do đó, thiết kế hiện tại giúp
đơn giản hóa cấu trúc CSDL và tránh dư thừa dữ liệu.

+ Các trạng thái của Project và Task được lưu dưới dạng giá trị
chuỗi (VARCHAR) trong CSDL và được kiểm soát bằng CHECK constraint
để đảm bảo chỉ các trạng thái hợp lệ mới được phép lưu trữ.
+ Ràng buộc khóa ngoại giữa Task và Project đã được kiểm tra
  thông qua việc insert dữ liệu không hợp lệ.
  Hệ thống từ chối Task có project_id không tồn tại,
  chứng minh rằng thiết kế CSDL đảm bảo toàn vẹn tham chiếu.


Fix ERD:
+ Sau khi thực hiện review các quan hệ giữa User, Project và Task,
+ ERD ban đầu đáp ứng đầy đủ yêu cầu nghiệp vụ của hệ thống.

+ Các thực thể, thuộc tính và quan hệ đã được thiết kế phù hợp,
không phát sinh dư thừa dữ liệu và đảm bảo chuẩn hóa ở mức 3NF.

=> Do đó, ERD final được giữ nguyên so với ERD đã trình bày ở mục trước.
