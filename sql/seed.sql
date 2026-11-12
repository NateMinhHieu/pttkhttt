INSERT INTO tblCustomer9(id,fullName) VALUES(1,'Khách demo');
INSERT INTO tblStaff9(id,fullName,position) VALUES(2,'Nhân viên kho','WAREHOUSE');
INSERT INTO tblMemberAccount9(id,username,salt,credential,role,accountStatus,customerId) VALUES(1,'khach','27b72460b3dc6fb846d67fdc50e3e226','5b433ed5cac0b71f86de4cb9ea7d283e7e1bf6a5d7d5a6cd90f79d1f8a0f2210','CUSTOMER','ACTIVE',1);
INSERT INTO tblMemberAccount9(id,username,salt,credential,role,accountStatus,staffId) VALUES(2,'kho','54b640b05b4064530a2d1ceec08742ad','23b144151735adbe6029e83eb2d6115b3f41f9559f7c2ae8f784fbd93ff14415','WAREHOUSE','ACTIVE',2);
INSERT INTO tblSupplier9 VALUES(1,'An Phát','Số 12 Nguyễn Trãi, Hà Nội','0901234567','','ACTIVE');
INSERT INTO tblSparePartCategory9 VALUES(1,'Lọc nhớt',''),(2,'Lọc khí','');
INSERT INTO tblServiceCategory9 VALUES(1,'Bảo dưỡng','');
INSERT INTO tblSparePart9 VALUES(1,'Lọc dầu','Lọc dầu động cơ','cái',150000,'ACTIVE',1),(2,'Lọc gió','Lọc gió động cơ','cái',250000,'ACTIVE',2);
INSERT INTO tblService9 VALUES(1,'Thay lọc dầu','Thay lọc dầu động cơ và kiểm tra rò rỉ.',100000,30,'ACTIVE',1);
