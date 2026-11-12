package garaman.dao;
import java.util.*;
import garaman.model.Draft9;
import garaman.dao.Database9.Query;

public record PurchaseInvoiceDAO9(Database9 db) {
    public void confirm(Draft9 draft,String staff) throws Exception {
        if(draft.lines.isEmpty()) throw new IllegalArgumentException("Hóa đơn cần ít nhất một dòng.");
        List<Query> work=new ArrayList<>();
        work.add(new Query("INSERT INTO tblPurchaseInvoice9(id,supplierId,createdById) VALUES(?,?,?)",draft.key,draft.supplier,staff));
        for(var l:draft.lines.values())
            work.add(new Query("INSERT INTO tblPurchaseInvoiceDetail9(purchaseInvoiceId,partId,quantity,unitPrice) VALUES(?,?,?,?)",
                    draft.key,l.id(),String.valueOf(l.quantity()),String.valueOf(l.price())));
        try { db.batch(work); }
        catch(Exception failure) {
            if(db.read("SELECT id FROM tblPurchaseInvoice9 WHERE id=? AND createdById=?",draft.key,staff).isEmpty()) throw failure;
        }
    }
    public void recordFullPayment(String id,String staff,String method,String time) throws Exception {
        db.read("UPDATE tblPurchaseInvoice9 SET paymentStatus='PAID',paymentMethod=?,paidAt=? WHERE id=? AND createdById=? AND paymentStatus='UNPAID'",method,time,id,staff);
    }
}
