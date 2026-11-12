# GaraMan · PTIT
Quách Vũ Minh Hiếu — B23DCAT109. Bản lập trình cho hai module **tra cứu dịch vụ/phụ tùng** và **nhập phụ tùng từ nhà cung cấp**.

## Chạy nhanh trên Windows, Linux hoặc macOS
Cài JDK 17 trở lên và Python 3. Mở terminal ở thư mục chứa pom.xml, chạy:
```sh
python run-demo.py
```
Nếu máy dùng lệnh python3, chạy `python3 run-demo.py`. Mở http://127.0.0.1:8080/. Dùng Ctrl+C để dừng. Bản demo chạy trên máy cá nhân, lưu SQLite tại demo/garaman_design.db. Xóa file này khi đã dừng ứng dụng để tạo lại dữ liệu mẫu.

| Vai trò | Tài khoản | Mật khẩu demo |
|---|---|---|
| Khách hàng | khach | GaraMan9! |
| Nhân viên kho | kho | GaraMan9! |

## Chạy JSP và JDBC theo thiết kế
Dùng JDK 17+, Maven, Tomcat 9 và MySQL 8. Tạo database garaman với utf8mb4. Trong MySQL client chọn database rồi chạy `SOURCE sql/schema.sql;` và `SOURCE sql/seed.sql;` từ thư mục dự án. Khởi tạo trên database trống. Cấp quyền SELECT, INSERT, UPDATE trên database cho tài khoản ứng dụng. Khai báo ba biến môi trường cho tiến trình Tomcat: GARAMAN_DB_URL (ví dụ jdbc:mysql://localhost:3306/garaman), GARAMAN_DB_USER và GARAMAN_DB_PASSWORD. Chạy `mvn clean package`, chép target/GaraMan.war vào webapps rồi khởi động Tomcat. Mở http://localhost:8080/GaraMan/.
 
## Đối chiếu trực tiếp với pha thiết kế

Trên Windows, mở thư mục chứa pom.xml trong VS Code và chạy `py -3 run-demo.py`. Demo dùng JDK 17 và SQLite. Bản WAR dùng JSP, JDBC và MySQL. Dùng một CSDL mới cho phiên bản này vì cấu trúc bảng khác bản demo cũ.

Các trang ở `src/main/webapp/WEB-INF/view/`, DAO và model ở `src/main/java/garaman/`. Hình 9 tương ứng mục 4.2–4.3 của pha thiết kế. Hình 11 tương ứng mục 5.2–5.3.

| Scenario | Trang JSP | Lời gọi cần mở trong VS Code |
|---|---|---|
| Tìm kiếm 1–3 | khachhang/gdChinhKH9.jsp → gdTimThongTin9.jsp | Mở trang, lấy từ khóa và hiển thị ô tìm kiếm |
| 4–15 | gdTimThongTin9.jsp, submitSearch | ServiceDAO9.findByName và SparePartDAO9.findByName đọc bản ghi ACTIVE; mapRow trả đối tượng để hiển thị hai nhóm |
| 16–23 | gdChiTiet9.jsp | findById của DAO tương ứng với loại kết quả; đọc lại tên, loại, giá và chi tiết |
| 24–27 | gdChiTiet9.jsp → gdTimThongTin9.jsp → gdChinhKH9.jsp | Liên kết quay lại mang theo q; liên kết trang chủ đóng tra cứu |
| Nhập hàng 1–10 | kho/gdChinhNVKho9.jsp → gdChonNCC9.jsp | submitSearch → SupplierDAO9.findByName |
| 11–17 | gdNhapPhuTung9.jsp | SupplierDAO9.findById → PurchaseInvoiceDAO9.createDraft; bản nháp gắn với phiên và nhân viên kho |
| 18–28 | gdChonPhuTung9.jsp → gdChiTietNhap9.jsp | SparePartDAO9.findByName và findById; mở màn hình nhập số lượng, đơn giá |
| 29–37 | gdChiTietNhap9.jsp → gdNhapPhuTung9.jsp | submitAdd → PurchaseInvoiceDetail9.calculateAmount → PurchaseInvoice9.addLine/updateLine → renderDraft → calculateTotal |
| 38 | Ngoài hệ thống | Nhân viên kiểm đếm hàng thực giao và có thể sửa dòng nháp |
| 39–46 | gdNhapPhuTung9.jsp → doXacNhanNhap9.jsp → gdHoaDonNhap9.jsp | execute → confirm → lockParts theo id tăng dần → saveLines. Database9 thực thi khóa và lệnh lưu trên cùng kết nối; commit hoặc rollback |
| 47–50 | gdHoaDonNhap9.jsp | getPrintableInvoice → xuất bản in → trình duyệt gọi window.print() |
| 51–52 | Ngoài hệ thống | Nhân viên trả tiền và nhà cung cấp nhận tiền trực tiếp |
| 53–57 | doGhiThanhToan9.jsp → gdHoaDonNhap9.jsp | execute → recordFullPayment → findById; hiển thị trạng thái PAID |
| Nhánh thêm NCC, mục 5.4 | gdThemNCC9.jsp | submitSave → SupplierDAO9.addSupplier → trở về danh sách để chọn |
| Nhánh thêm phụ tùng, mục 5.5 | gdThemPhuTung9.jsp | getCategories → submitSave → SparePartDAO9.addPart → trở về danh sách; tồn ban đầu bằng 0 |

### Cách thực thi

index.jsp nhận request và điều phối theo phiên, vai trò, phương thức HTTP và thao tác. Mỗi trang JSP có phần xử lý riêng. Khi chạy không có Tomcat, demo/compile_pages.py biên dịch phần Java khai báo trong chính các trang JSP đó. SQL xác nhận và thanh toán chạy thành giao dịch. MySQL khóa hàng bằng FOR UPDATE; SQLite khóa giao dịch ghi bằng BEGIN IMMEDIATE.

Một số tên trên sơ đồ là thao tác giao diện (Quay lại, Đóng, Chọn, Hiện kết quả). Trong source chúng có thể là liên kết, biểu mẫu hoặc một đoạn trong show(), không nhất thiết là một phương thức Java riêng. Kết nối và vòng đời giao dịch JDBC được quản lý trong Database9. DAO9 chứa chức năng truy vấn chung. Chức năng chạy được tập trung vào hai module; các lớp và bảng toàn hệ thống phục vụ mô hình dữ liệu. Đã kiểm tra luồng chính bằng HTTP trên bản demo. Chưa kiểm thử triển khai WAR với Tomcat/MySQL.

## Thử nghiệp vụ
1. Đăng nhập khach, tìm “lọc”, chọn Lọc dầu. Giá bán 150.000 đồng, đơn vị cái. Quay lại để giữ kết quả.
2. Đăng nhập kho, chọn An Phát. Thêm 10 Lọc dầu với đơn giá nhập 100.000 đồng và 5 Lọc gió với đơn giá 180.000 đồng. Có thể sửa hoặc bỏ dòng. Tổng 1.900.000 đồng.
3. Sau khi kiểm đếm, xác nhận một lần. Khi mở lại hóa đơn hoặc in lại, số lượng nhập giữ nguyên. Xuất bản in, dùng nút In hóa đơn để mở hộp thoại in của trình duyệt. Sau khi thực trả, quay về hóa đơn và nhập phương thức, thời điểm trả.
4. Nếu chưa có nhà cung cấp/phụ tùng, chọn Thêm trên màn hình tìm, lưu rồi chọn lại. Phụ tùng mới có tồn ban đầu bằng 0.

