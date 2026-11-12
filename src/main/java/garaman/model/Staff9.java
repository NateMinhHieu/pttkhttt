package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Staff9 {
    public long id;
    public String fullName;
    public String phone;
    public String position;
    public MemberAccount9 memberAccount;

    public static Staff9 withId(long id) {
        var result = new Staff9();
        result.id = id;
        return result;
    }
}
