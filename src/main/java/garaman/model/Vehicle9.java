package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Vehicle9 {
    public long id;
    public String licensePlate;
    public String chassisNumber;
    public String brand;
    public String model;
    public int year;
    public Customer9 customer;

    public boolean belongsTo(Customer9 owner) {
        return owner != null && customer != null && customer.id == owner.id;
    }
}
