package garaman.dao;
import garaman.model.*;
import java.util.*;
import java.math.BigDecimal;
public class ServiceDAO9 extends DAO9 {
    public ServiceDAO9(Database9 db){ super(db); }
    private static final String SELECT = "SELECT p.id,p.name,p.description,p.listedPrice,p.estimatedDuration,c.id,c.name FROM tblService9 p JOIN tblServiceCategory9 c ON c.id=p.categoryId WHERE p.status='ACTIVE'";
    public List<Service9> findByName(String keyword) throws Exception {
        return db.read(SELECT+" AND LOWER(p.name) LIKE LOWER(?) ESCAPE '!' ORDER BY p.name",pattern(keyword))
            .stream().map(this::mapRow).toList();
    }
    public Service9 findById(long id) throws Exception { return mapRow(one(SELECT+" AND p.id=?",Long.toString(id))); }
    private Service9 mapRow(List<String> r){
        var item=new Service9();
        item.id=Long.parseLong(r.get(0)); item.name=r.get(1); item.description=r.get(2);
        item.listedPrice=new BigDecimal(r.get(3)); item.estimatedDuration=Integer.parseInt(r.get(4));
        item.category=ServiceCategory9.of(Long.parseLong(r.get(5)), r.get(6));
        item.status="ACTIVE"; 
        return item;
    }

}
