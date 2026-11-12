package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class SalesInvoice9 {
    public long id;
    public LocalDateTime createdAt;
    public BigDecimal total;
    public String paymentStatus;
    public String paymentMethod;
    public LocalDateTime paidAt;
    public RepairOrder9 repairOrder;
    public BigDecimal getTotal(){
        return total = repairOrder.calculateActualTotal();
    }
}
