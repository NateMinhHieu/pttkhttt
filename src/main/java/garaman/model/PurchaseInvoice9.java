package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class PurchaseInvoice9 {
    public long id;
    public String confirmationKey;
    public LocalDateTime createdAt;
    public String supplierDocumentNumber;
    public LocalDateTime confirmedAt;
    public BigDecimal total;
    public String confirmationStatus;
    public String paymentStatus;
    public String paymentMethod;
    public LocalDateTime paidAt;
    public Supplier9 supplier;
    public Staff9 createdBy;
    public List<PurchaseInvoiceDetail9> details = new ArrayList<>();
    public void addLine(PurchaseInvoiceDetail9 line){
        requireDraft();
        for(int i=0; i<details.size(); i++){
            if(details.get(i).part.id == line.part.id){ details.set(i,line); return; }
        }
        details.add(line);
    }
    public void updateLine(PurchaseInvoiceDetail9 line){ addLine(line); }
    public void removeLine(long partId){ requireDraft(); details.removeIf(line -> line.part.id == partId); }
    public BigDecimal calculateTotal(){
        total = BigDecimal.ZERO;
        for(var line : details) total = total.add(line.calculateAmount());
        return total;
    }

    public static PurchaseInvoice9 createDraft(long id, Supplier9 supplier, Staff9 staff) {
        var draft = new PurchaseInvoice9();
        draft.id = id;
        draft.supplier = Objects.requireNonNull(supplier);
        draft.createdBy = Objects.requireNonNull(staff);
        draft.confirmationKey = UUID.randomUUID().toString();
        draft.createdAt = LocalDateTime.now();
        draft.confirmationStatus = "DRAFT";
        draft.paymentStatus = "UNPAID";
        draft.total = BigDecimal.ZERO;
        return draft;
    }

    public void validateForConfirmation(long staffId) {
        if (!"DRAFT".equals(confirmationStatus) || createdBy.id != staffId || details.isEmpty())
            throw new IllegalArgumentException("Hóa đơn nháp chưa hợp lệ.");
        for (var line : details) {
            if (line.quantity <= 0 || line.unitPrice.signum() <= 0)
                throw new IllegalArgumentException("Kiểm tra số lượng và đơn giá.");
        }
        calculateTotal();
    }

    private void requireDraft() {
        if (!"DRAFT".equals(confirmationStatus))
            throw new IllegalArgumentException("Chỉ được chỉnh sửa hóa đơn nháp.");
    }
}
