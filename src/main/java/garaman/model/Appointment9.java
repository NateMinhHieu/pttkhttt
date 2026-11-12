package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Appointment9 {
    public long id;
    public LocalDateTime requestedTime;
    public String note;
    public String status;
    public String cancellationReason;
    public Customer9 customer;
    public Vehicle9 vehicle;
    public Staff9 confirmedBy;
    public List<AppointmentServiceDetail9> services = new ArrayList<>();

    public void requestService(Service9 service, String note) {
        services.add(AppointmentServiceDetail9.forService(service, note));
    }
}
