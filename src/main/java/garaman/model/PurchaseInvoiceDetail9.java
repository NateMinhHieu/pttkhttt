package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class PurchaseInvoiceDetail9 {
    public long id;
    public int quantity;
    public BigDecimal unitPrice;
    public BigDecimal amount;
    public SparePart9 part;
    public BigDecimal calculateAmount(){ return amount = unitPrice.multiply(BigDecimal.valueOf(quantity)); }

    public static PurchaseInvoiceDetail9 createLine(SparePart9 part, int quantity, BigDecimal unitPrice) {
        Objects.requireNonNull(part, "Thiếu phụ tùng.");
        if (quantity <= 0 || quantity > 100000 || unitPrice == null || unitPrice.signum() <= 0)
            throw new IllegalArgumentException("Số lượng hoặc đơn giá không hợp lệ.");
        var line = new PurchaseInvoiceDetail9();
        line.part = part;
        line.quantity = quantity;
        line.unitPrice = unitPrice;
        line.calculateAmount();
        return line;
    }
}
