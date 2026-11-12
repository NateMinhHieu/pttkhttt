<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    var draft=w.draft();var part=SparePart9.getDetail(new SparePartDAO9(w.db), w.id("id"));
    var old=draft.details.stream().filter(l->l.part.id==part.id).findFirst();
    if(w.action().equals("saveLine"))return submitAdd(w,draft,part,old);
    return w.screen("Nhập chi tiết","<h2>"+Page9.h(part.name)+"</h2>"+w.form("saveLine")+w.hidden("id",""+part.id)
        +Page9.field("Số lượng thực nhận","quantity",old.map(l->""+l.quantity).orElse(""),"number")
        +Page9.field("Đơn giá nhập","price",old.map(l->l.unitPrice.toPlainString()).orElse(""),"number")+Page9.btn("Lưu dòng hàng")+"</form>"+Page9.link("a=draft","Hủy"));
}
private static Reply submitAdd(GaraMan9 w, PurchaseInvoice9 draft, SparePart9 part, Optional<PurchaseInvoiceDetail9> old) throws Exception {
    var line = PurchaseInvoiceDetail9.createLine(part, Integer.parseInt(w.need("quantity", 8)), w.price("price"));
        if(old.isPresent())draft.updateLine(line);else draft.addLine(line);
        return w.go("a=draft");
}

%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
