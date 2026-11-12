<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    return execute(w);
}
private static Reply execute(GaraMan9 w)throws Exception{
    var dao=new PurchaseInvoiceDAO9(w.db);String key=w.need("key",64);
    var previous=dao.findConfirmedId(key,w.s.staff.id);
    if(previous.isPresent())return w.go("a=invoice&id="+previous.get());
    var draft=w.draft();w.check(key.equals(draft.confirmationKey),"Bản nháp đã thay đổi.");
    var inv=dao.confirm(draft,w.s.staff.id);w.s.draft=null;return w.go("a=invoice&id="+inv.id);
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
