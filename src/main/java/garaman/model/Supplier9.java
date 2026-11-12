package garaman.model;
import garaman.dao.SupplierDAO9;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Supplier9 {
    public long id;
    public String name;
    public String address;
    public String phone;
    public String email;
    public String cooperationStatus;

    public static List<Supplier9> searchByName(SupplierDAO9 catalog, String keyword) throws Exception {
        return catalog.findByName(keyword);
    }
}
