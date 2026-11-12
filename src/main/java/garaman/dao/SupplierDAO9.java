package garaman.dao;
import garaman.model.*;
import java.util.*;
public class SupplierDAO9 extends DAO9 {
    public SupplierDAO9(Database9 db){super(db);}
    private static final String SELECT="SELECT id,name,address,phone,email,cooperationStatus FROM tblSupplier9 WHERE cooperationStatus='ACTIVE'";
    public List<Supplier9> findByName(String name) throws Exception {
        return db.read(SELECT+" AND LOWER(name) LIKE LOWER(?) ESCAPE '!' ORDER BY name",pattern(name)).stream().map(this::mapRow).toList();
    }
    public Supplier9 findById(long id) throws Exception {return mapRow(one(SELECT+" AND id=?",""+id));}
    public Supplier9 addSupplier(Supplier9 s) throws Exception {
        if(!db.read("SELECT id FROM tblSupplier9 WHERE phone=?",s.phone).isEmpty())
            throw new IllegalArgumentException("Điện thoại đã tồn tại.");
        s.id=newId();
        db.read("INSERT INTO tblSupplier9(id,name,address,phone,email,cooperationStatus) VALUES(?,?,?,?,?,'ACTIVE')",""+s.id,s.name,s.address,s.phone,s.email);
        return s;
    }
    private Supplier9 mapRow(List<String> r){
        var s=new Supplier9(); s.id=Long.parseLong(r.get(0)); s.name=r.get(1); s.address=r.get(2);
        s.phone=r.get(3); s.email=r.get(4); s.cooperationStatus=r.get(5); return s;
    }
}
