package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class SparePartCategory9 {
    public long id;
    public String name;
    public String description;

    public static SparePartCategory9 of(long id, String name) {
        var result = new SparePartCategory9();
        result.id = id;
        result.name = name;
        return result;
    }
}
