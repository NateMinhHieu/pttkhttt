# GaraMan · PTIT
Quách Vũ Minh Hiếu — B23DCAT109. Bản lập trình cho hai module **tra cứu dịch vụ/phụ tùng** và **nhập phụ tùng từ nhà cung cấp**.

## Chạy nhanh trên Windows, Linux hoặc macOS
Cài JDK 17 trở lên và Python 3. Mở terminal ở thư mục chứa pom.xml, chạy:
```sh
python run-demo.py
```
Nếu máy dùng lệnh python3, chạy `python3 run-demo.py`. Mở http://127.0.0.1:8080/. Dùng Ctrl+C để dừng. Bản demo chạy trên máy cá nhân, lưu SQLite tại demo/garaman.db. Xóa file này khi đã dừng ứng dụng để tạo lại dữ liệu mẫu.

| Vai trò | Tài khoản | Mật khẩu demo |
|---|---|---|
| Khách hàng | khach | GaraMan9! |
| Nhân viên kho | kho | GaraMan9! |

## Chạy JSP và JDBC theo thiết kế
Dùng JDK 17+, Maven, Tomcat 9 và MySQL 8. Tạo database garaman với utf8mb4. Trong MySQL client chọn database rồi chạy `SOURCE sql/schema.sql;` và `SOURCE sql/seed.sql;` từ thư mục dự án. Khởi tạo trên database trống. Cấp quyền SELECT, INSERT, UPDATE trên database cho tài khoản ứng dụng. Khai báo ba biến môi trường cho tiến trình Tomcat: GARAMAN_DB_URL (ví dụ jdbc:mysql://localhost:3306/garaman), GARAMAN_DB_USER và GARAMAN_DB_PASSWORD. Chạy `mvn clean package`, chép target/GaraMan.war vào webapps rồi khởi động Tomcat. Mở http://localhost:8080/GaraMan/.
 
## Tổ chức chương trình
- src/main/webapp/index.jsp nhận HTTP, đọc phiên và chuyển yêu cầu cho GaraMan9. Mỗi màn hình có một tham số a. Page9 sinh HTML dùng chung cho JSP và demo.
- dao/Database9 mở kết nối JDBC và chạy câu lệnh với tham số. PurchaseInvoiceDAO9 xác nhận toàn bộ hóa đơn trong một transaction và ghi nhận thanh toán.
- model/Draft9 giữ nhà cung cấp và các dòng của bản nháp. Một dòng cho một phụ tùng. Chỉ lưu chứng từ khi xác nhận. Vòng đời bản nháp gắn với phiên.
- demo/Demo9 và sqlite_bridge.py thay bộ tiếp nhận HTTP và bộ kết nối dữ liệu khi chạy bản demo. Cả hai chế độ dùng cùng mã xử lý nghiệp vụ, giao diện và dữ liệu mẫu.
- sql/ chỉ triển khai các bảng cần cho hai module. Nhân viên lập hóa đơn được xác định qua tài khoản kho. Mã UUID dùng cho chứng từ và bản ghi mới. Giá lưu theo đồng nguyên. View v_stock_balance trả tổng nhập đã xác nhận trừ tổng xuất.

## Thử nghiệp vụ
1. Đăng nhập khach, tìm “lọc”, chọn Lọc dầu. Giá bán 150.000 đồng, đơn vị cái. Quay lại để giữ kết quả.
2. Đăng nhập kho, chọn An Phát. Thêm 10 Lọc dầu với đơn giá nhập 100.000 đồng và 5 Lọc gió với đơn giá 180.000 đồng. Có thể sửa hoặc bỏ dòng. Tổng 1.900.000 đồng.
3. Sau khi kiểm đếm, xác nhận một lần. Khi mở lại hóa đơn hoặc in lại, số lượng nhập giữ nguyên. Xuất bản in, dùng nút In hóa đơn để mở hộp thoại in của trình duyệt. Sau khi thực trả, quay về hóa đơn và nhập phương thức, thời điểm trả.
4. Nếu chưa có nhà cung cấp/phụ tùng, chọn Thêm trên màn hình tìm, lưu rồi chọn lại. Phụ tùng mới có tồn ban đầu bằng 0.

## Đưa mã nguồn lên GitHub
Trong thư mục chứa pom.xml, tạo repository Git cục bộ và commit mã nguồn. Khi đã đăng nhập GitHub CLI bằng tài khoản của mình, có thể chạy:
```sh
git init -b main
git add .
git commit -m "GaraMan: tra cuu va nhap phu tung"
gh repo create GaraMan-B23DCAT109 --private --source=. --push
gh repo view --json url --jq .url
```
Dùng URL lệnh cuối trả về trong báo cáo. Với repo riêng tư, cấp quyền đọc cho giảng viên hoặc chọn chế độ công khai khi tạo nếu muốn chia sẻ rộng rãi.
