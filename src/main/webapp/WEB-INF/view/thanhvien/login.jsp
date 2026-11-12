<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    if(w.method.equals("POST") && w.action().equals("login")) return w.login();
    if(w.method.equals("POST") && w.action().equals("logout")){
        w.s.user="";w.s.role="";w.s.staff=null;w.s.customer=null;w.s.draft=null;
        w.s.printed.clear();w.s.csrf=UUID.randomUUID().toString();return w.go("a=home");
    }
    return w.screen("Đăng nhập",w.form("login")+Page9.field("Tên đăng nhập","user","","text")
        +Page9.field("Mật khẩu","pass","","password")+Page9.btn("Đăng nhập")+"</form>");
}
%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
