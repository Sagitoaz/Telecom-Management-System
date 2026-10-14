# Mô tả bài toán quản lý thuê bao trong viễn thông

+ Trong hệ thống viễn thông, mỗi khách hàng thực tế được đại diện với 1 số thuê bao trên hệ thống viễn thông
+ Khi khách hàng muốn sử dụng dịch vụ cần ra cửa hàng để đăng ký thông tin cá nhân như cmt, ngày tháng năm sinh, nghề nghiệp,...
+ 1 thuê bao có nhiều gói cước và tài khoản cho nhiều dịch vụ khác nhau: ví dụ gói cước dùng fb, gói cước dùng chơi game,..=> có thời gian sử dụng
+ Khi có tiêu dùng và sử dụng dịch vụ t=> sẽ phát sinh các giao dịch cộng  trừ tiền trên hệ thống

**Yêu cầu thiết kế 1 hệ thống đáp ứng được các nghiệp vụ bên dưới**

+ xây dựng thiết kết mô hình các đối tượng và csdl quan hệ giữa các thực thể với nhau
+ xây dựng được các api cung cấp cho hệ thống chăm sóc khách hàng cần dùng như
    + cho phép đấu nối mới thuê bao vào mạng
    + cho phép gửi lệnh đăng ký gói cước và hủy gói cước
    + cho phép gửi lệnh cộng tiền và trừ tiền khi sử dụng dịch vụ
+ xây dựng được các luồng báo cáo định kỳ hàng ngày
    + có api  truy vấn được danh sách các thuê bao có phát sinh tiêu dùng dịch vụ từ ngày x đến ngày y trên gói cước A, B
    + xây dựng job định kỳ hàng ngày kiểm tra và xóa các dữ liệu có gói cước và tài khoản hết hạn

**Yêu cầu về hiệu năng cần đạt được tối thiếu**

+ xây dựng cơ sở dữ liệu tối thiêu có 500k bản ghi
+ Xây dựng tool test hiệu năng gửi các lệnh bên dưới tối thiểu đạt được 30 tps đồng thời
+ đăng ký gói cước
+ trừ tiền khách hàng
    + có giả lập được tỷ lệ trừ tiền đồng thời trên cùng 1 thuê bao và 1 tài khoản=> tỷ lệ này đạt 50% request cùng thuê bao cùng tài khoản / tổng request gửi lên
    + xây dựng được công cụ và phương pháp kiểm chứng việc trừ tiền là đúng đủ khi có tác động đồng thời trên cùng thuê bao và cùng tài khoản
