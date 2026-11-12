<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    return w.screen("Trang khách hàng",Page9.link("a=search","Tìm dịch vụ và phụ tùng"));
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
