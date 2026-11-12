package garaman.model;
import garaman.dao.ServiceDAO9;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Service9 {
    public long id;
    public String name;
    public String description;
    public BigDecimal listedPrice;
    public int estimatedDuration;
    public String status;
    public ServiceCategory9 category;

    public static List<Service9> searchByName(ServiceDAO9 catalog, String keyword) throws Exception {
        return catalog.findByName(keyword);
    }

    public static Service9 getDetail(ServiceDAO9 catalog, long id) throws Exception {
        return catalog.findById(id);
    }
}
