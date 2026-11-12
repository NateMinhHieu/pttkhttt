<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    String q=w.val("q"); String body=w.searchBox("search",q);
    if(q.isEmpty())return w.screen("Tìm dịch vụ và phụ tùng",body+(w.p.containsKey("q")?"Hãy nhập tên cần tìm.":""));
    return submitSearch(w,q,body);
}

private static Reply submitSearch(GaraMan9 w,String q,String body)throws Exception {
    StringBuilder rows=new StringBuilder();
    for(Service9 service:Service9.searchByName(new ServiceDAO9(w.db), q))
        rows.append(result(w,"service","Dịch vụ",service.id,service.name,service.listedPrice,q));
    for(SparePart9 part:SparePart9.searchByName(new SparePartDAO9(w.db), q))
        rows.append(result(w,"part","Phụ tùng",part.id,part.name,part.sellingPrice,q));
    return w.screen("Tìm dịch vụ và phụ tùng",body+(rows.isEmpty()?"Không tìm thấy kết quả.":w.table("Tên|Nhóm|Giá|",rows.toString())));
}
private static String result(GaraMan9 w,String type,String group,long id,String name,BigDecimal price,String q){
    return w.row(Page9.h(name),group,Page9.money(price),Page9.link("a=detail&group="+type+"&id="+id+"&q="+Page9.url(q),"Xem chi tiết"));
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
