package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class RepairPartDetail9 {
    public long id;
    public int requestedQuantity;
    public int issuedQuantity;
    public BigDecimal unitPrice;
    public SparePart9 part;
    public List<StockIssue9> issues = new ArrayList<>();
    public int getIssuedQuantity(){ return issues.stream().mapToInt(i -> i.quantity).sum(); }

    public void recordIssue(int quantity, Staff9 staff, LocalDateTime at) {
        if (quantity > requestedQuantity - getIssuedQuantity())
            throw new IllegalArgumentException("Số lượng xuất vượt yêu cầu.");
        issues.add(StockIssue9.record(quantity, staff, at));
        issuedQuantity = getIssuedQuantity();
    }

    public BigDecimal calculateAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(getIssuedQuantity()));
    }
}
