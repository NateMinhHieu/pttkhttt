package garaman.dao;
import garaman.model.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class PurchaseInvoiceDAO9 extends DAO9 {
    public PurchaseInvoiceDAO9(Database9 db){super(db);}
    public PurchaseInvoice9 createDraft(Supplier9 supplier, Staff9 staff){
        return PurchaseInvoice9.createDraft(newId(), supplier, staff);
    }
    private void saveLines(List<Database9.Query> work,PurchaseInvoice9 draft){
        for(var line:draft.details) work.add(new Database9.Query(
            "INSERT INTO tblPurchaseInvoiceDetail9(id,purchaseInvoiceId,partId,quantity,unitPrice) VALUES(?,?,?,?,?)",
            ""+newId(),""+draft.id,""+line.part.id,""+line.quantity,line.unitPrice.toPlainString()));
    }
    public PurchaseInvoice9 confirm(PurchaseInvoice9 draft,long staff) throws Exception{
        draft.validateForConfirmation(staff);
        var work=new ArrayList<>(new SparePartDAO9(db).lockParts(draft.details.stream().map(l->l.part.id).toList()));
        work.add(new Database9.Query("INSERT INTO tblPurchaseInvoice9(id,confirmationKey,createdAt,supplierDocumentNumber,confirmedAt,confirmationStatus,paymentStatus,supplierId,createdById) VALUES(?,?,?,?,CURRENT_TIMESTAMP,'CONFIRMED','UNPAID',?,?)",
            ""+draft.id,draft.confirmationKey,draft.createdAt.toString().replace('T',' '),draft.supplierDocumentNumber==null?"":draft.supplierDocumentNumber,""+draft.supplier.id,""+staff));
        saveLines(work,draft);
        try { db.batch(work); }
        catch(Exception e){
            if(findConfirmedId(draft.confirmationKey,staff).isEmpty())throw e;
        }
        return findById(draft.id,staff);
    }
    public Optional<Long> findConfirmedId(String key,long staff)throws Exception{
        var rows=db.read("SELECT id FROM tblPurchaseInvoice9 WHERE confirmationKey=? AND createdById=?",key,""+staff);
        return rows.stream().findFirst().map(r->Long.parseLong(r.get(0)));
    }
    public PurchaseInvoice9 findById(long id,long staff)throws Exception{
        var r=one("SELECT i.id,i.confirmationKey,i.createdAt,i.confirmedAt,i.confirmationStatus,i.paymentStatus,i.paymentMethod,i.paidAt,s.id,s.name FROM tblPurchaseInvoice9 i JOIN tblSupplier9 s ON s.id=i.supplierId WHERE i.id=? AND i.createdById=?",""+id,""+staff);
        var inv=new PurchaseInvoice9(); inv.id=id; inv.confirmationKey=r.get(1);inv.createdAt=time(r.get(2));
        inv.confirmedAt=time(r.get(3));inv.confirmationStatus=r.get(4);inv.paymentStatus=r.get(5); inv.paymentMethod=r.get(6);
        if(!r.get(7).isBlank())inv.paidAt=time(r.get(7));
        inv.supplier=new Supplier9();inv.supplier.id=Long.parseLong(r.get(8));inv.supplier.name=r.get(9);
        for(var row:db.read("SELECT d.id,p.id,p.name,d.quantity,d.unitPrice FROM tblPurchaseInvoiceDetail9 d JOIN tblSparePart9 p ON p.id=d.partId WHERE d.purchaseInvoiceId=? ORDER BY p.name",""+id)){
            var line=new PurchaseInvoiceDetail9();line.id=Long.parseLong(row.get(0));line.part=new SparePart9();line.part.id=Long.parseLong(row.get(1));
            line.part.name=row.get(2);line.quantity=Integer.parseInt(row.get(3));line.unitPrice=new BigDecimal(row.get(4));inv.details.add(line);
        }
        inv.calculateTotal();return inv;
    }
    private static LocalDateTime time(String s){return LocalDateTime.parse(s.replace(' ','T'));}
    public PurchaseInvoice9 getPrintableInvoice(long id,long staff)throws Exception{return findById(id,staff);}
    public PurchaseInvoice9 recordFullPayment(long id,long staff,String method,LocalDateTime paidAt)throws Exception{
        db.batch(List.of(new Database9.Query("SELECT id FROM tblPurchaseInvoice9 WHERE id=? AND createdById=? FOR UPDATE",""+id,""+staff),
            new Database9.Query("UPDATE tblPurchaseInvoice9 SET paymentStatus='PAID',paymentMethod=?,paidAt=? WHERE id=? AND createdById=? AND confirmationStatus='CONFIRMED' AND paymentStatus='UNPAID'",method,paidAt.toString().replace('T',' '),""+id,""+staff)));
        return findById(id,staff);
    }
}
