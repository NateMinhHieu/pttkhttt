package garaman.model;
import java.util.*;
public class Draft9 {
    public record Line(String id,String name,int quantity,long price) {
        public long amount(){return Math.multiplyExact((long)quantity,price);}
    }
    public final String key=UUID.randomUUID().toString();
    public final String supplier;
    public final Map<String,Line> lines=new LinkedHashMap<>();
    public Draft9(String supplier){this.supplier=supplier;}
}
