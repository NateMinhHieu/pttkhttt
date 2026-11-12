<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    return execute(w);
}
private static Reply execute(GaraMan9 w)throws Exception{
    long id=w.id("id");w.check(w.s.printed.contains(""+id),"Hãy xuất bản in trước.");
    String method=w.val("payment");w.check(Set.of("Tiền mặt","Chuyển khoản").contains(method),"Chọn phương thức thanh toán.");
    LocalDateTime time;
    try{time=LocalDateTime.parse(w.val("time"));}catch(java.time.format.DateTimeParseException e){throw new IllegalArgumentException("Thời điểm không hợp lệ.");}
    new PurchaseInvoiceDAO9(w.db).recordFullPayment(id,w.s.staff.id,method,time);
    return w.go("a=invoice&id="+id);
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
