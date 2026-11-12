package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class AppointmentServiceDetail9 {
    public long id;
    public String note;
    public Service9 service;

    public static AppointmentServiceDetail9 forService(Service9 service, String note) {
        var detail = new AppointmentServiceDetail9();
        detail.service = Objects.requireNonNull(service);
        detail.note = note;
        return detail;
    }
}
