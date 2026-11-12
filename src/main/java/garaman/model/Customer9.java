package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Customer9 {
    public long id;
    public String fullName;
    public String phone;
    public String email;
    public String address;
    public MemberAccount9 memberAccount;

    public static Customer9 withId(long id) {
        var result = new Customer9();
        result.id = id;
        return result;
    }
}
