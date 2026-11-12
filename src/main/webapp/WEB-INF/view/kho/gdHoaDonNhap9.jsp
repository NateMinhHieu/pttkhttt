<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    var dao=new PurchaseInvoiceDAO9(w.db);
    if(w.action().equals("invoices")){
        StringBuilder rows=new StringBuilder();for(var r:w.db.read("SELECT i.id,s.name,i.confirmedAt,i.paymentStatus FROM tblPurchaseInvoice9 i JOIN tblSupplier9 s ON s.id=i.supplierId WHERE i.createdById=? ORDER BY i.confirmedAt DESC",""+w.s.staff.id))
            rows.append(w.row(Page9.h(r.get(1)),Page9.h(r.get(2)),Page9.h(r.get(3)),Page9.link("a=invoice&id="+r.get(0),"Xem hóa đơn")));
        return w.screen("Hóa đơn đã xác nhận",w.table("Nhà cung cấp|Ngày|Trạng thái|",rows.toString()));
    }
    long id=w.id("id");boolean print=w.action().equals("print")||w.action().equals("issuePrint");
    var inv=print?dao.getPrintableInvoice(id,w.s.staff.id):dao.findById(id,w.s.staff.id);
    if(w.action().equals("issuePrint")){w.s.printed.add(""+id);return w.go("a=print&id="+id);}
    StringBuilder rows=new StringBuilder();for(var l:inv.details)rows.append(w.row(Page9.h(l.part.name),""+l.quantity,Page9.money(l.unitPrice),Page9.money(l.amount)));
    String body="<p>"+Page9.h(inv.supplier.name)+"</p>"+w.table("Phụ tùng|SL|Đơn giá|Thành tiền",rows.toString())+"<h2>"+Page9.money(inv.total)+"</h2>";
    if(print)return w.screen("Bản in hóa đơn nhập",body+"<button onclick='window.print()'>In hóa đơn</button>"+Page9.link("a=invoice&id="+id,"Trở về hóa đơn"));
    body+=w.form("issuePrint")+w.hidden("id",""+id)+Page9.btn("Xuất bản in")+"</form>";
    if(inv.paymentStatus.equals("PAID"))body+="<p>Đã thanh toán đủ · "+Page9.h(inv.paymentMethod)+" · "+inv.paidAt+"</p>";
    else if(w.s.printed.contains(""+id))body+=w.form("pay")+w.hidden("id",""+id)+"<label>Phương thức<select name='payment'><option>Tiền mặt</option><option>Chuyển khoản</option></select></label>"
        +Page9.field("Thời điểm","time","","datetime-local")+Page9.btn("Ghi nhận đã trả đủ")+"</form>";
    return w.screen("Hóa đơn nhập đã xác nhận",body);
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
