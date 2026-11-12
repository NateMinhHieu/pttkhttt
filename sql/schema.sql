CREATE TABLE tblCustomer9 (
    id BIGINT PRIMARY KEY,
    fullName VARCHAR(500),
    phone VARCHAR(500),
    email VARCHAR(500),
    address VARCHAR(500)
);
CREATE TABLE tblStaff9 (
    id BIGINT PRIMARY KEY,
    fullName VARCHAR(500),
    phone VARCHAR(500),
    position VARCHAR(500)
);
CREATE TABLE tblMemberAccount9 (
    id BIGINT PRIMARY KEY,
    username VARCHAR(500) UNIQUE,
    credential VARCHAR(500),
    role VARCHAR(500),
    accountStatus VARCHAR(500),
    customerId BIGINT,
    staffId BIGINT,
    salt VARCHAR(64) NOT NULL,
    FOREIGN KEY (customerId) REFERENCES tblCustomer9(id),
    FOREIGN KEY (staffId) REFERENCES tblStaff9(id)
);
CREATE TABLE tblVehicle9 (
    id BIGINT PRIMARY KEY,
    licensePlate VARCHAR(500),
    chassisNumber VARCHAR(500),
    brand VARCHAR(500),
    model VARCHAR(500),
    year INT,
    customerId BIGINT,
    FOREIGN KEY (customerId) REFERENCES tblCustomer9(id)
);
CREATE TABLE tblServiceCategory9 (
    id BIGINT PRIMARY KEY,
    name VARCHAR(500),
    description VARCHAR(500)
);
CREATE TABLE tblSparePartCategory9 (
    id BIGINT PRIMARY KEY,
    name VARCHAR(500),
    description VARCHAR(500)
);
CREATE TABLE tblService9 (
    id BIGINT PRIMARY KEY,
    name VARCHAR(500),
    description VARCHAR(500),
    listedPrice DECIMAL(18,2),
    estimatedDuration INT,
    status VARCHAR(500),
    categoryId BIGINT,
    FOREIGN KEY (categoryId) REFERENCES tblServiceCategory9(id)
);
CREATE TABLE tblSparePart9 (
    id BIGINT PRIMARY KEY,
    name VARCHAR(500) UNIQUE,
    description VARCHAR(500),
    unit VARCHAR(500),
    sellingPrice DECIMAL(18,2),
    status VARCHAR(500),
    categoryId BIGINT,
    FOREIGN KEY (categoryId) REFERENCES tblSparePartCategory9(id)
);
CREATE TABLE tblSupplier9 (
    id BIGINT PRIMARY KEY,
    name VARCHAR(500),
    address VARCHAR(500),
    phone VARCHAR(500) UNIQUE,
    email VARCHAR(500),
    cooperationStatus VARCHAR(500)
);
CREATE TABLE tblAppointment9 (
    id BIGINT PRIMARY KEY,
    requestedTime DATETIME,
    note VARCHAR(500),
    status VARCHAR(500),
    cancellationReason VARCHAR(500),
    customerId BIGINT,
    vehicleId BIGINT,
    confirmedById BIGINT,
    FOREIGN KEY (customerId) REFERENCES tblCustomer9(id),
    FOREIGN KEY (vehicleId) REFERENCES tblVehicle9(id),
    FOREIGN KEY (confirmedById) REFERENCES tblStaff9(id)
);
CREATE TABLE tblAppointmentServiceDetail9 (
    id BIGINT PRIMARY KEY,
    note VARCHAR(500),
    appointmentId BIGINT,
    serviceId BIGINT,
    FOREIGN KEY (appointmentId) REFERENCES tblAppointment9(id),
    FOREIGN KEY (serviceId) REFERENCES tblService9(id)
);
CREATE TABLE tblRepairOrder9 (
    id BIGINT PRIMARY KEY,
    receivedAt DATETIME,
    vehicleCondition VARCHAR(500),
    mileage INT,
    expectedReturnDate DATE,
    status VARCHAR(500),
    completedAt DATETIME,
    customerId BIGINT,
    vehicleId BIGINT,
    appointmentId BIGINT,
    receivedById BIGINT,
    FOREIGN KEY (customerId) REFERENCES tblCustomer9(id),
    FOREIGN KEY (vehicleId) REFERENCES tblVehicle9(id),
    FOREIGN KEY (appointmentId) REFERENCES tblAppointment9(id),
    FOREIGN KEY (receivedById) REFERENCES tblStaff9(id)
);
CREATE TABLE tblRepairServiceDetail9 (
    id BIGINT PRIMARY KEY,
    quantity INT,
    unitPrice DECIMAL(18,2),
    progress VARCHAR(500),
    note VARCHAR(500),
    repairOrderId BIGINT,
    serviceId BIGINT,
    technicianId BIGINT,
    FOREIGN KEY (repairOrderId) REFERENCES tblRepairOrder9(id),
    FOREIGN KEY (serviceId) REFERENCES tblService9(id),
    FOREIGN KEY (technicianId) REFERENCES tblStaff9(id)
);
CREATE TABLE tblRepairPartDetail9 (
    id BIGINT PRIMARY KEY,
    requestedQuantity INT,
    unitPrice DECIMAL(18,2),
    repairOrderId BIGINT,
    partId BIGINT,
    FOREIGN KEY (repairOrderId) REFERENCES tblRepairOrder9(id),
    FOREIGN KEY (partId) REFERENCES tblSparePart9(id)
);
CREATE TABLE tblStockIssue9 (
    id BIGINT PRIMARY KEY,
    quantity INT,
    issuedAt DATETIME,
    repairPartDetailId BIGINT,
    issuedById BIGINT,
    FOREIGN KEY (repairPartDetailId) REFERENCES tblRepairPartDetail9(id),
    FOREIGN KEY (issuedById) REFERENCES tblStaff9(id)
);
CREATE TABLE tblSalesInvoice9 (
    id BIGINT PRIMARY KEY,
    createdAt DATETIME,
    paymentStatus VARCHAR(500),
    paymentMethod VARCHAR(500),
    paidAt DATETIME,
    repairOrderId BIGINT,
    FOREIGN KEY (repairOrderId) REFERENCES tblRepairOrder9(id)
);
CREATE TABLE tblPurchaseInvoice9 (
    id BIGINT PRIMARY KEY,
    confirmationKey VARCHAR(500) UNIQUE,
    createdAt DATETIME,
    supplierDocumentNumber VARCHAR(500),
    confirmedAt DATETIME,
    confirmationStatus VARCHAR(500),
    paymentStatus VARCHAR(500),
    paymentMethod VARCHAR(500),
    paidAt DATETIME,
    supplierId BIGINT,
    createdById BIGINT,
    FOREIGN KEY (supplierId) REFERENCES tblSupplier9(id),
    FOREIGN KEY (createdById) REFERENCES tblStaff9(id)
);
CREATE TABLE tblPurchaseInvoiceDetail9 (
    id BIGINT PRIMARY KEY,
    quantity INT,
    unitPrice DECIMAL(18,2),
    purchaseInvoiceId BIGINT,
    partId BIGINT,
    UNIQUE (purchaseInvoiceId,partId),
    CHECK (quantity > 0 AND unitPrice > 0),
    FOREIGN KEY (purchaseInvoiceId) REFERENCES tblPurchaseInvoice9(id),
    FOREIGN KEY (partId) REFERENCES tblSparePart9(id)
);
CREATE VIEW v_stock_balance AS
SELECT p.id,
 COALESCE((SELECT SUM(d.quantity) FROM tblPurchaseInvoiceDetail9 d JOIN tblPurchaseInvoice9 i ON i.id=d.purchaseInvoiceId WHERE d.partId=p.id AND i.confirmationStatus='CONFIRMED'),0)
 - COALESCE((SELECT SUM(s.quantity) FROM tblStockIssue9 s JOIN tblRepairPartDetail9 r ON r.id=s.repairPartDetailId WHERE r.partId=p.id),0) AS stockQuantity
FROM tblSparePart9 p;
