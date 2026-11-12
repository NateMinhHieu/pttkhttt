package garaman;
import garaman.dao.*;
import garaman.model.Draft9;
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
        public Draft9 draft;
        public final Set<String> printed=new HashSet<>();
    }
    public record Reply(int status,String content,String redirect){}
    private final Database9 db;
    private final Session s;
    private final Map<String,String> p;
    private final String method;
    public GaraMan9(Database9 db,Session session,Map<String,String> params,String method){
        this.db=db;s=session;p=params;this.method=method;
    }
    String val(String k){return p.getOrDefault(k,"").strip();}
    String need(String k,int max){String v=val(k);if(v.isEmpty()||v.length()>max)throw new IllegalArgumentException("Kiểm tra lại trường "+k+".");return v;}
    void check(boolean ok,String msg){if(!ok)throw new IllegalArgumentException(msg);}
    String form(String action){return "<form method='post'><input type='hidden' name='a' value='"+action+"'><input type='hidden' name='csrf' value='"+s.csrf+"'>";}
    String hidden(String k,String v){return "<input type='hidden' name='"+k+"' value='"+h(v)+"'>";}
    String nav(){return s.user.isEmpty()?"":link("a=home","Trang chủ")+form("logout")+btn("Đăng xuất")+"</form>";}
    Reply screen(String t,String b){return new Reply(200,page(t,b,nav()),"");}
    Reply go(String route){return new Reply(303,"","?"+route);}
    List<String> one(String sql,String... args)throws Exception{
        var rows=db.read(sql,args);check(!rows.isEmpty(),"Không tìm thấy bản ghi.");return rows.get(0);
    }
    public Reply handle()throws Exception{
        synchronized(s){
            try {
                boolean post=method.equals("POST");
                if(post)check(s.csrf.equals(val("csrf")),"Phiên biểu mẫu đã hết hạn.");
                String a=p.getOrDefault("a","home");
                if(s.user.isEmpty()){
                    if(post&&a.equals("login"))return login();
                    return screen("Đăng nhập",form("login")+field("Tên đăng nhập","user","","text")+field("Mật khẩu","pass","","password")+btn("Đăng nhập")+"</form>");
                }
                if(post&&a.equals("logout")){s.user="";s.role="";s.draft=null;s.printed.clear();s.csrf=UUID.randomUUID().toString();return go("a=home");}
                if(a.equals("home")){
                    boolean wh=s.role.equals("WAREHOUSE");
                    return screen(wh?"Bàn làm việc nhân viên kho":"Trang khách hàng","<section><h2>"+(wh?"Nhập kho":"Danh mục gara")+"</h2>"+(wh?link("a=suppliers","Nhập phụ tùng")+link("a=invoices","Hóa đơn đã xác nhận")+(s.draft==null?"":link("a=draft","Tiếp tục hóa đơn nháp")):link("a=search","Tìm dịch vụ và phụ tùng"))+"</section>");
                }
                if(s.role.equals("CUSTOMER")&&!post&&Set.of("search","detail").contains(a))return catalog(a);
                if(!s.role.equals("WAREHOUSE"))return new Reply(403,page("Không có quyền",link("a=home","Trang chủ"),nav()),"");
                if(post)return update(a);
                return warehouse(a);
            } catch(IllegalArgumentException e){
                return new Reply(400,page("Chưa thể thực hiện", "<section>"+h(e.getMessage())+"<p>"+link("a=home","Về trang chủ")+"</p></section>",nav()),"");
            }
        }
    }
    Reply login()throws Exception{
        var rows=db.read("SELECT id,salt,credential,role FROM tblMemberAccount9 WHERE username=?",val("user"));
        if(!rows.isEmpty()){
            var r=rows.get(0);var spec=new PBEKeySpec(val("pass").toCharArray(),HexFormat.of().parseHex(r.get(1)),100000,256);
            byte[] key=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            if(MessageDigest.isEqual(key,HexFormat.of().parseHex(r.get(2)))){s.user=r.get(0);s.role=r.get(3);s.csrf=UUID.randomUUID().toString();return go("a=home");}
        }
        throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu chưa đúng.");
    }
    String searchBox(String a,String q){return "<form class='search'><input type='hidden' name='a' value='"+a+"'><label>Tên cần tìm<input name='q' value='"+h(q)+"'></label>"+btn("Tìm")+"</form>";}
    String table(String heads,String rows){StringBuilder b=new StringBuilder("<table><thead><tr>");for(String head:heads.split("[|]"))b.append("<th>").append(head).append("</th>");return b+"</tr></thead><tbody>"+rows+"</tbody></table>";}
    String row(String... html){return "<tr><td>"+String.join("</td><td>",html)+"</td></tr>";}
    boolean matches(String name,String q){return name.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT));}
    Reply catalog(String a)throws Exception{
        String q=val("q");
        if(a.equals("detail")){
            boolean part=val("group").equals("part");
            var r=one(part?"SELECT p.name,c.name,p.description,p.sellingPrice,p.unit FROM tblSparePart9 p JOIN tblSparePartCategory9 c ON c.id=p.categoryId WHERE p.id=?":"SELECT p.name,c.name,p.description,p.listedPrice,p.estimatedDuration FROM tblService9 p JOIN tblServiceCategory9 c ON c.id=p.categoryId WHERE p.id=?",val("id"));
            return screen(r.get(0),"<section><small>"+(part?"Phụ tùng":"Dịch vụ")+"</small><h2>"+h(r.get(1))+"</h2><p>"+h(r.get(2))+"</p><p class='price'>"+money(Long.parseLong(r.get(3)))+"</p><p>"+(part?"Đơn vị":"Thời gian dự kiến")+": "+h(r.get(4))+(part?"":" phút")+"</p>"+link("a=search&q="+url(q),"Quay lại kết quả")+"</section>");
        }
        String rows="";
        if(!q.isBlank()) for(boolean part:new boolean[]{false,true}){
            for(var r:db.read(part?"SELECT id,name,sellingPrice FROM tblSparePart9 ORDER BY name":"SELECT id,name,listedPrice FROM tblService9 ORDER BY name"))
                if(matches(r.get(1),q))rows+=row(h(r.get(1)),part?"Phụ tùng":"Dịch vụ",money(Long.parseLong(r.get(2))),link("a=detail&group="+(part?"part":"service")+"&id="+url(r.get(0))+"&q="+url(q),"Xem chi tiết"));
        }
        return screen("Tìm dịch vụ và phụ tùng",searchBox("search",q)+(q.isBlank()?(p.containsKey("q")?"<p>Hãy nhập tên cần tìm.</p>":""):rows.isEmpty()?"<p>Không tìm thấy kết quả.</p>":table("Tên|Nhóm|Giá|",rows)));
    }
    Reply warehouse(String a)throws Exception{
        String q=val("q");String rows="";
        switch(a){
        case "suppliers":
            for(var r:db.read("SELECT id,name,address,phone FROM tblSupplier9 ORDER BY name"))if(matches(r.get(1),q))
                rows+=row(h(r.get(1)),h(r.get(2)),h(r.get(3)),form("start")+hidden("id",r.get(0))+btn("Chọn")+"</form>");
            return screen("Chọn nhà cung cấp",searchBox(a,q)+link("a=newSupplier","Thêm nhà cung cấp")+table("Tên|Địa chỉ|Điện thoại|",rows));
        case "newSupplier":
            return screen("Thêm nhà cung cấp",form("saveSupplier")+field("Tên nhà cung cấp","name","","text")+field("Địa chỉ","address","","text")+field("Điện thoại","phone","","tel")+"<label>Email<input type='email' name='email'></label>"+btn("Lưu nhà cung cấp")+"</form>"+link("a=suppliers","Hủy"));
        case "invoices":
            for(var r:db.read("SELECT i.id,s.name,i.confirmedAt,i.paymentStatus FROM tblPurchaseInvoice9 i JOIN tblSupplier9 s ON s.id=i.supplierId WHERE i.createdById=? ORDER BY i.confirmedAt DESC",s.user))
                rows+=row(h(r.get(1)),h(r.get(2)),r.get(3).equals("PAID")?"Đã thanh toán":"Chưa thanh toán",link("a=invoice&id="+url(r.get(0)),"Xem hóa đơn"));
            return screen("Hóa đơn đã xác nhận",table("Nhà cung cấp|Thời điểm|Thanh toán|",rows));
        case "invoice": case "print":return invoice(a);
        }
        check(s.draft!=null,"Hãy chọn nhà cung cấp trước.");
        if(a.equals("parts")){
            for(var r:db.read("SELECT p.id,p.name,p.sellingPrice,v.stockQuantity FROM tblSparePart9 p JOIN v_stock_balance v ON v.id=p.id ORDER BY p.name"))if(matches(r.get(1),q))
                rows+=row(h(r.get(1)),money(Long.parseLong(r.get(2))),h(r.get(3)),link("a=line&id="+url(r.get(0)),"Chọn"));
            return screen("Chọn phụ tùng",searchBox(a,q)+link("a=newPart","Thêm phụ tùng mới")+table("Tên|Giá bán|Tồn kho|",rows)+link("a=draft","Quay lại hóa đơn"));
        }
        if(a.equals("newPart")){
            String options="";for(var r:db.read("SELECT id,name FROM tblSparePartCategory9 ORDER BY name"))options+="<option value='"+h(r.get(0))+"'>"+h(r.get(1))+"</option>";
            return screen("Thêm phụ tùng",form("savePart")+field("Tên phụ tùng","name","","text")+"<label>Loại<select name='category'>"+options+"</select></label>"+field("Mô tả","description","","text")+field("Đơn vị","unit","","text")+field("Giá bán (đồng)","price","","number")+btn("Lưu phụ tùng")+"</form>"+link("a=parts","Hủy"));
        }
        if(a.equals("line")){
            var r=one("SELECT name,unit FROM tblSparePart9 WHERE id=?",val("id"));var old=s.draft.lines.get(val("id"));
            return screen("Nhập chi tiết", "<h2>"+h(r.get(0))+" · "+h(r.get(1))+"</h2>"+form("saveLine")+hidden("id",val("id"))+field("Số lượng thực nhận","quantity",old==null?"":String.valueOf(old.quantity()),"number")+field("Đơn giá nhập (đồng)","price",old==null?"":String.valueOf(old.price()),"number")+btn("Lưu dòng hàng")+"</form>"+link("a=draft","Quay lại hóa đơn"));
        }
        check(a.equals("draft"),"Trang không tồn tại.");
        long total=0;
        for(var l:s.draft.lines.values()){
            total=Math.addExact(total,l.amount());
            rows+=row(h(l.name()),String.valueOf(l.quantity()),money(l.price()),money(l.amount()),link("a=line&id="+url(l.id()),"Sửa")+form("remove")+hidden("id",l.id())+btn("Bỏ dòng")+"</form>");
        }
        return screen("Hóa đơn nhập nháp","<p>Nhà cung cấp · <b>"+h(one("SELECT name FROM tblSupplier9 WHERE id=?",s.draft.supplier).get(0))+"</b></p>"+link("a=parts","Thêm dòng phụ tùng")+table("Phụ tùng|SL|Đơn giá nhập|Thành tiền|",rows)+"<h2>Tổng cộng · "+money(total)+"</h2>"+form("confirm")+hidden("key",s.draft.key)+btn("Xác nhận nhập kho")+"</form>"+form("cancel")+btn("Hủy bản nháp")+"</form>");
    }
    Reply invoice(String a)throws Exception{
        String id=val("id");var r=one("SELECT s.name,i.confirmedAt,i.paymentStatus,i.paymentMethod,i.paidAt FROM tblPurchaseInvoice9 i JOIN tblSupplier9 s ON s.id=i.supplierId WHERE i.id=? AND i.createdById=?",id,s.user);
        String rows="";long total=0;
        for(var l:db.read("SELECT p.name,d.quantity,d.unitPrice FROM tblPurchaseInvoiceDetail9 d JOIN tblSparePart9 p ON p.id=d.partId WHERE d.purchaseInvoiceId=? ORDER BY p.name",id)){
            long price=Long.parseLong(l.get(2)),amount=price*Long.parseLong(l.get(1));total+=amount;rows+=row(h(l.get(0)),l.get(1),money(price),money(amount));
        }
        String body="<p>Nhà cung cấp · <b>"+h(r.get(0))+"</b></p><p>Số phiếu "+h(id.substring(0,8))+" · "+h(r.get(1))+"</p>"+table("Phụ tùng|SL|Đơn giá nhập|Thành tiền",rows)+"<h2>Tổng thanh toán · "+money(total)+"</h2>";
        if(a.equals("print")){return screen("Bản in hóa đơn nhập",body+"<div class='tools'><button onclick='window.print()'>In hóa đơn</button>"+link("a=invoice&id="+url(id),"Trở về hóa đơn")+"</div>");}
        body+=form("issuePrint")+hidden("id",id)+btn("Xuất bản in")+"</form>";
        if(r.get(2).equals("PAID"))body+="<section><h2>Đã thanh toán đủ</h2><p>"+h(r.get(3))+" · "+h(r.get(4))+"</p></section>";
        else if(s.printed.contains(id))body+=form("pay")+hidden("id",id)+"<label>Phương thức<select name='payment'><option>Tiền mặt</option><option>Chuyển khoản</option></select></label>"+field("Thời điểm thanh toán","time","","datetime-local")+btn("Ghi nhận đã trả đủ")+"</form>";
        else body+="<p>Chưa thanh toán. Xuất bản in trước khi ghi nhận số tiền đã trả.</p>";
        return screen("Hóa đơn nhập đã xác nhận",body);
    }
    int number(String k,int max){int n=Integer.parseInt(val(k));check(n>0&&n<=max,"Số lượng hoặc giá ngoài phạm vi.");return n;}
    Reply update(String a)throws Exception{
        String id=val("id");
        switch(a){
        case "saveSupplier":
            String name=need("name",150),phone=need("phone",20);
            check(db.read("SELECT id FROM tblSupplier9 WHERE phone=?",phone).isEmpty(),"Điện thoại đã tồn tại.");
            db.read("INSERT INTO tblSupplier9 VALUES (?,?,?,?,?)",UUID.randomUUID().toString(),name,need("address",255),phone,val("email"));return go("a=suppliers&q="+url(name));
        case "start":
            one("SELECT id FROM tblSupplier9 WHERE id=?",id);
            check(s.draft==null||s.draft.supplier.equals(id),"Đang có hóa đơn nháp. Hãy hoàn tất hoặc hủy nháp trước.");
            if(s.draft==null)s.draft=new Draft9(id);return go("a=draft");
        case "issuePrint":
            one("SELECT id FROM tblPurchaseInvoice9 WHERE id=? AND createdById=?",id,s.user);
            s.printed.add(id);return go("a=print&id="+url(id));
        case "pay":
            check(s.printed.contains(id),"Hãy xuất bản in trước.");check(Set.of("Tiền mặt","Chuyển khoản").contains(val("payment")),"Chọn phương thức thanh toán.");
            String time=val("time");try{LocalDateTime.parse(time);}catch(Exception e){throw new IllegalArgumentException("Thời điểm không hợp lệ.");}
            new PurchaseInvoiceDAO9(db).recordFullPayment(id,s.user,val("payment"),time.replace('T',' '));return go("a=invoice&id="+url(id));
        case "confirm":
            String key=val("key");
            if(!db.read("SELECT id FROM tblPurchaseInvoice9 WHERE id=? AND createdById=?",key,s.user).isEmpty())return go("a=invoice&id="+url(key));
            check(s.draft!=null&&s.draft.key.equals(key),"Bản nháp đã thay đổi.");
            new PurchaseInvoiceDAO9(db).confirm(s.draft,s.user);s.draft=null;return go("a=invoice&id="+url(key));
        }
        check(s.draft!=null,"Chưa có hóa đơn nháp.");
        switch(a){
        case "savePart":
            String name=need("name",150),category=val("category");one("SELECT id FROM tblSparePartCategory9 WHERE id=?",category);
            check(db.read("SELECT id FROM tblSparePart9 WHERE name=?",name).isEmpty(),"Tên phụ tùng đã tồn tại.");
            db.read("INSERT INTO tblSparePart9 VALUES(?,?,?,?,?,?)",UUID.randomUUID().toString(),name,need("description",500),category,String.valueOf(number("price",100000000)),need("unit",30));return go("a=parts&q="+url(name));
        case "saveLine":
            var nameRow=one("SELECT name FROM tblSparePart9 WHERE id=?",id);
            check(s.draft.lines.size()<100||s.draft.lines.containsKey(id),"Hóa đơn đã đủ 100 dòng.");
            s.draft.lines.put(id,new Draft9.Line(id,nameRow.get(0),number("quantity",100000),number("price",100000000)));return go("a=draft");
        case "remove":s.draft.lines.remove(id);return go("a=draft");
        case "cancel":s.draft=null;return go("a=home");
        default:throw new IllegalArgumentException("Thao tác không hợp lệ.");
        }
    }
}
