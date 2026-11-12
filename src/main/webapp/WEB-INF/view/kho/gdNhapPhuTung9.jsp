<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    if(w.action().equals("start")){
        var supplier=new SupplierDAO9(w.db).findById(w.id("id"));
        w.check(w.s.draft==null || w.s.draft.supplier.id==supplier.id,"Hãy hoàn tất hoặc hủy nháp trước.");
        if(w.s.draft==null)w.s.draft=new PurchaseInvoiceDAO9(w.db).createDraft(supplier,w.s.staff);
        return w.go("a=draft");
    }
    var draft=w.draft();
    if(w.action().equals("remove")){draft.removeLine(w.id("id"));return w.go("a=draft");}
    if(w.action().equals("cancel")){w.s.draft=null;return w.go("a=home");}
    return renderDraft(w,draft);
}
private static Reply renderDraft(GaraMan9 w,PurchaseInvoice9 draft) throws Exception {
    StringBuilder rows=new StringBuilder();
    for(var line:draft.details){
        String id=""+line.part.id;
        rows.append(w.row(Page9.h(line.part.name),""+line.quantity,Page9.money(line.unitPrice),Page9.money(line.calculateAmount()),
            Page9.link("a=line&id="+id,"Sửa")+w.form("remove")+w.hidden("id",id)+Page9.btn("Bỏ dòng")+"</form>"));
    }
    return w.screen("Hóa đơn nhập nháp","<p>"+Page9.h(draft.supplier.name)+"</p>"+Page9.link("a=parts","Thêm dòng phụ tùng")
        +w.table("Phụ tùng|SL|Đơn giá nhập|Thành tiền|",rows.toString())+"<h2>Tổng cộng · "+Page9.money(draft.calculateTotal())+"</h2>"
        +w.form("confirm")+w.hidden("key",draft.confirmationKey)+Page9.btn("Xác nhận nhập kho")+"</form>"+w.form("cancel")+Page9.btn("Hủy bản nháp")+"</form>");
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
