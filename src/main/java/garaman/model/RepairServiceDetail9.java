package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class RepairServiceDetail9 {
    public long id;
    public int quantity;
    public BigDecimal unitPrice;
    public String progress;
    public String note;
    public Service9 service;
    public Technician9 technician;

    public BigDecimal calculateAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
