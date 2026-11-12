package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class StockIssue9 {
    public long id;
    public int quantity;
    public LocalDateTime issuedAt;
    public Staff9 issuedBy;

    public static StockIssue9 record(int quantity, Staff9 staff, LocalDateTime at) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng xuất phải dương.");
        var issue = new StockIssue9();
        issue.quantity = quantity;
        issue.issuedBy = Objects.requireNonNull(staff);
        issue.issuedAt = Objects.requireNonNull(at);
        return issue;
    }
}
