package garaman.dao;
import java.util.*;
public abstract class DAO9 {
    protected final Database9 db;
    protected DAO9(Database9 db){ this.db=db; }
    protected static long newId(){ return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE; }
    protected static String pattern(String text){
        return "%" + text.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }
    protected List<String> one(String sql, String... args) throws Exception {
        var rows=db.read(sql,args);
        if(rows.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hồ sơ.");
        return rows.get(0);
    }
}
