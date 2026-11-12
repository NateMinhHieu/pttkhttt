<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    w.draft();return submitSearch(w,w.val("q"));
}
private static Reply submitSearch(GaraMan9 w,String q)throws Exception {
    StringBuilder rows=new StringBuilder();
    for(SparePart9 part:SparePart9.searchByName(new SparePartDAO9(w.db), q))
        rows.append(w.row(Page9.h(part.name),Page9.money(part.sellingPrice),""+part.getStockQuantity(),Page9.link("a=line&id="+part.id,"Chọn")));
    return w.screen("Chọn phụ tùng",w.searchBox("parts",q)+Page9.link("a=newPart","Thêm phụ tùng mới")
        +w.table("Phụ tùng|Giá bán|Tồn kho|",rows.toString())+Page9.link("a=draft","Quay lại hóa đơn"));
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
