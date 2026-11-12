<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    long id=w.id("id");String name,category,description,extra;BigDecimal price;
    if(w.val("group").equals("part")){
        SparePart9 part=SparePart9.getDetail(new SparePartDAO9(w.db), id);
        name=part.name;category=part.category.name;description=part.description;price=part.sellingPrice;extra="Đơn vị: "+part.unit;
    }else{
        w.check(w.val("group").equals("service"),"Nhóm không hợp lệ.");
        Service9 service=Service9.getDetail(new ServiceDAO9(w.db), id);
        name=service.name;category=service.category.name;description=service.description;price=service.listedPrice;extra="Thời gian dự kiến: "+service.estimatedDuration+" phút";
    }
    return w.screen(name,"<h2>"+Page9.h(category)+"</h2><p>"+Page9.h(description)+"</p>"+Page9.money(price)
        +"<p>"+Page9.h(extra)+"</p>"+Page9.link("a=search&q="+Page9.url(w.val("q")),"Quay lại kết quả"));
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
