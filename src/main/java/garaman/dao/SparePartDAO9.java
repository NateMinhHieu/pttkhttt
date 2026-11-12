package garaman.dao;
import garaman.model.*;
import java.util.*;
import java.math.BigDecimal;
public class SparePartDAO9 extends DAO9 {
    public SparePartDAO9(Database9 db){ super(db); }
    private static final String SELECT = "SELECT p.id,p.name,p.description,p.sellingPrice,p.unit,c.id,c.name,v.stockQuantity FROM tblSparePart9 p JOIN tblSparePartCategory9 c ON c.id=p.categoryId JOIN v_stock_balance v ON v.id=p.id WHERE p.status='ACTIVE'";
    public List<SparePart9> findByName(String keyword) throws Exception {
        return db.read(SELECT+" AND LOWER(p.name) LIKE LOWER(?) ESCAPE '!' ORDER BY p.name",pattern(keyword))
            .stream().map(this::mapRow).toList();
    }
    public SparePart9 findById(long id) throws Exception { return mapRow(one(SELECT+" AND p.id=?",Long.toString(id))); }
    private SparePart9 mapRow(List<String> r){
        var item=new SparePart9();
        item.id=Long.parseLong(r.get(0)); item.name=r.get(1); item.description=r.get(2);
        item.sellingPrice=new BigDecimal(r.get(3)); item.unit=r.get(4);
        item.category=SparePartCategory9.of(Long.parseLong(r.get(5)), r.get(6));
        item.status="ACTIVE"; item.stockQuantity=Integer.parseInt(r.get(7));
        return item;
    }

    public long addPart(SparePart9 p) throws Exception {
        if(!db.read("SELECT id FROM tblSparePart9 WHERE name=?",p.name).isEmpty())
            throw new IllegalArgumentException("Tên phụ tùng đã tồn tại.");
        p.id=newId();
        db.read("INSERT INTO tblSparePart9(id,name,description,unit,sellingPrice,status,categoryId) VALUES(?,?,?,?,?,'ACTIVE',?)",
            ""+p.id,p.name,p.description,p.unit,p.sellingPrice.toPlainString(),""+p.category.id);
        return p.id;
    }
    public int getStockQuantity(long id) throws Exception { return findById(id).stockQuantity; }
    public List<Database9.Query> lockParts(List<Long> ids){
        return ids.stream().distinct().sorted().map(id -> new Database9.Query(
            "SELECT id FROM tblSparePart9 WHERE id=? FOR UPDATE",id.toString())).toList();
    }
}
