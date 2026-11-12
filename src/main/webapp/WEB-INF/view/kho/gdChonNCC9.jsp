<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    return submitSearch(w,w.val("q"));
}
private static Reply submitSearch(GaraMan9 w,String q)throws Exception {
    StringBuilder rows=new StringBuilder();
    for(var s:Supplier9.searchByName(new SupplierDAO9(w.db), q))
        rows.append(w.row(Page9.h(s.name),Page9.h(s.address),Page9.h(s.phone),w.form("start")+w.hidden("id",""+s.id)+Page9.btn("Chọn")+"</form>"));
    return w.screen("Chọn nhà cung cấp",w.searchBox("suppliers",q)+Page9.link("a=newSupplier","Thêm nhà cung cấp")+w.table("Tên|Địa chỉ|Điện thoại|",rows.toString()));
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
