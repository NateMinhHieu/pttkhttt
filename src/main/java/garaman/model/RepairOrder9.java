package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class RepairOrder9 {
    public long id;
    public LocalDateTime receivedAt;
    public String vehicleCondition;
    public int mileage;
    public LocalDate expectedReturnDate;
    public String status;
    public LocalDateTime completedAt;
    public Customer9 customer;
    public Vehicle9 vehicle;
    public Appointment9 appointment;
    public Staff9 receivedBy;
    public List<RepairServiceDetail9> services = new ArrayList<>();
    public List<RepairPartDetail9> parts = new ArrayList<>();

    public BigDecimal calculateActualTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (var service : services) sum = sum.add(service.calculateAmount());
        for (var part : parts) sum = sum.add(part.calculateAmount());
        return sum;
    }
}
