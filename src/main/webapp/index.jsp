<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,garaman.GaraMan9,garaman.dao.Database9" trimDirectiveWhitespaces="true" %><%
request.setCharacterEncoding("UTF-8");
GaraMan9.Session state;
synchronized(session) {
    state=(GaraMan9.Session)session.getAttribute("gara");
    if(state==null){state=new GaraMan9.Session();session.setAttribute("gara",state);}
}
Map<String,String> params=new HashMap<>();
for(String name: request.getParameterMap().keySet()) params.put(name,request.getParameter(name));
try {
    GaraMan9.Reply result=new GaraMan9(Database9.mysql(),state,params,request.getMethod()).handle();
    response.setHeader("Cache-Control","no-store");
    response.setStatus(result.status());
    if(!result.redirect().isEmpty()){response.setHeader("Location",request.getContextPath()+"/"+result.redirect());return;}
    out.print(result.content());
} catch(Exception error){application.log("GaraMan request failed",error);response.sendError(500,"Không thể hoàn tất giao dịch.");}
%>
