package garaman.model;
import garaman.dao.SparePartDAO9;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class SparePart9 {
    public long id;
    public String name;
    public String description;
    public String unit;
    public BigDecimal sellingPrice;
    public int stockQuantity;
    public String status;
    public SparePartCategory9 category;
    public int getStockQuantity(){ return stockQuantity; }

    public static List<SparePart9> searchByName(SparePartDAO9 catalog, String keyword) throws Exception {
        return catalog.findByName(keyword);
    }

    public static SparePart9 getDetail(SparePartDAO9 catalog, long id) throws Exception {
        return catalog.findById(id);
    }
}
