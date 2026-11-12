package garaman.dao;
import garaman.model.*;
import java.util.*;
public class SparePartCategoryDAO9 extends DAO9 {
    public SparePartCategoryDAO9(Database9 db){super(db);}
    public List<SparePartCategory9> getCategories() throws Exception{
        return db.read("SELECT id,name,description FROM tblSparePartCategory9 ORDER BY name").stream().map(this::mapRow).toList();
    }
    private SparePartCategory9 mapRow(List<String> r){
        var c=new SparePartCategory9(); c.id=Long.parseLong(r.get(0)); c.name=r.get(1); c.description=r.get(2); return c;
    }
}
