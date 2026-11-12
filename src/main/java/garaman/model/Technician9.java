package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class Technician9 extends Staff9 {

    public void updateProgress(RepairServiceDetail9 work, String progress) {
        if (work.technician == null || work.technician.id != id)
            throw new IllegalArgumentException("Công việc chưa được giao cho nhân viên này.");
        if (progress == null || progress.isBlank())
            throw new IllegalArgumentException("Thiếu tiến độ công việc.");
        work.progress = progress;
    }
}
