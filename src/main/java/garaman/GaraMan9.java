package garaman;
import garaman.dao.*;
import garaman.model.*;
import garaman.view.Page9;
import static garaman.view.Page9.*;
import java.util.*;
import java.security.MessageDigest;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.time.LocalDateTime;

public class GaraMan9 {
    public static class Session {
        public String csrf=UUID.randomUUID().toString(), user="",role="";
        public PurchaseInvoice9 draft;
        public Staff9 staff;
        public Customer9 customer;
        public final Set<String> printed=new HashSet<>();
    }
    public record Reply(int status,String content,String redirect){}
    public final Database9 db;
    public final Session s;
    public final Map<String,String> p;
    public final String method;
    public GaraMan9(Database9 db,Session session,Map<String,String> params,String method){
        this.db=db;s=session;p=params;this.method=method;
    }
    public String val(String k){return p.getOrDefault(k,"").strip();}
    public String need(String k,int max){String v=val(k);if(v.isEmpty()||v.length()>max)throw new IllegalArgumentException("Kiểm tra lại trường "+k+".");return v;}
    public void check(boolean ok,String msg){if(!ok)throw new IllegalArgumentException(msg);}
    public String form(String action){return "<form method='post'><input type='hidden' name='a' value='"+action+"'><input type='hidden' name='csrf' value='"+s.csrf+"'>";}
    public String hidden(String k,String v){return "<input type='hidden' name='"+k+"' value='"+h(v)+"'>";}
    public String nav(){return s.user.isEmpty()?"":link("a=home","Trang chủ")+form("logout")+btn("Đăng xuất")+"</form>";}
    public Reply screen(String t,String b){return new Reply(200,page(t,b,nav()),"");}
    public Reply go(String route){return new Reply(303,"","?"+route);}
    public List<String> one(String sql,String... args)throws Exception{
        var rows=db.read(sql,args);check(!rows.isEmpty(),"Không tìm thấy bản ghi.");return rows.get(0);
    }
    public Reply login()throws Exception{
        var rows=db.read("SELECT id,salt,credential,role,staffId,customerId FROM tblMemberAccount9 WHERE username=? AND accountStatus='ACTIVE'",val("user"));
        if(!rows.isEmpty()){
            var r=rows.get(0);if(MemberAccount9.verifyPassword(val("pass"), r.get(1), r.get(2))){if(r.get(3).equals("WAREHOUSE")){s.staff=Staff9.withId(Long.parseLong(r.get(4)));} else {s.customer=Customer9.withId(Long.parseLong(r.get(5)));} s.user=r.get(0);s.role=r.get(3);s.csrf=UUID.randomUUID().toString();return go("a=home");}
        }
        throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu chưa đúng.");
    }
    public String searchBox(String a,String q){return "<form class='search'><input type='hidden' name='a' value='"+a+"'><label>Tên cần tìm<input name='q' value='"+h(q)+"'></label>"+btn("Tìm")+"</form>";}
    public String table(String heads,String rows){StringBuilder b=new StringBuilder("<table><thead><tr>");for(String head:heads.split("[|]"))b.append("<th>").append(head).append("</th>");return b+"</tr></thead><tbody>"+rows+"</tbody></table>";}
    public String row(String... html){return "<tr><td>"+String.join("</td><td>",html)+"</td></tr>";}
    public long id(String key){return Long.parseLong(need(key,32));}
    public java.math.BigDecimal price(String key){
        var n=new java.math.BigDecimal(need(key,20));
        check(n.signum()>0 && n.scale()<=2 && n.compareTo(new java.math.BigDecimal("100000000"))<=0,"Đơn giá không hợp lệ.");
        return n;
    }
    public PurchaseInvoice9 draft(){check(s.draft!=null,"Hãy chọn nhà cung cấp trước.");return s.draft;}
    public String action(){return p.getOrDefault("a","home");}
    public String route(){
        boolean post=method.equals("POST");
        if(post)check(s.csrf.equals(val("csrf")),"Phiên biểu mẫu đã hết hạn.");
        if(s.user.isEmpty() || action().equals("logout"))return "login";
        if(action().equals("home"))return s.role.equals("WAREHOUSE")?"gdChinhNVKho9":"gdChinhKH9";
        if(s.role.equals("CUSTOMER") && !post)return switch(action()){
            case "search" -> "gdTimThongTin9";
            case "detail" -> "gdChiTiet9";
            default -> throw new IllegalArgumentException("Không có quyền.");
        };
        check(s.role.equals("WAREHOUSE"),"Không có quyền.");
        if(post)return switch(action()){
            case "saveSupplier" -> "gdThemNCC9";
            case "start","remove","cancel" -> "gdNhapPhuTung9";
            case "savePart" -> "gdThemPhuTung9";
            case "saveLine" -> "gdChiTietNhap9";
            case "issuePrint" -> "gdHoaDonNhap9";
            case "pay" -> "doGhiThanhToan9";
            case "confirm" -> "doXacNhanNhap9";
            default -> throw new IllegalArgumentException("Thao tác không hợp lệ.");
        };
        return switch(action()){
            case "suppliers" -> "gdChonNCC9";
            case "newSupplier" -> "gdThemNCC9";
            case "parts" -> "gdChonPhuTung9";
            case "newPart" -> "gdThemPhuTung9";
            case "draft" -> "gdNhapPhuTung9";
            case "line" -> "gdChiTietNhap9";
            case "invoices","invoice","print" -> "gdHoaDonNhap9";
            default -> throw new IllegalArgumentException("Trang không tồn tại.");
        };
    }
    public Reply error(String message){return new Reply(400,page("Chưa thể thực hiện",h(message)+"<p>"+link("a=home","Trang chủ")+"</p>",nav()),"");}
}
