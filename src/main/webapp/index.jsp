<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,garaman.GaraMan9,garaman.dao.Database9" trimDirectiveWhitespaces="true" %><%
request.setCharacterEncoding("UTF-8");
GaraMan9.Session state;
synchronized(session){
    state=(GaraMan9.Session)session.getAttribute("gara");
    if(state==null){state=new GaraMan9.Session();session.setAttribute("gara",state);}
}
Map<String,String> params=new HashMap<>();
for(String key:request.getParameterMap().keySet())params.put(key,request.getParameter(key));
GaraMan9 web=new GaraMan9(Database9.mysql(),state,params,request.getMethod());
synchronized(state){
    GaraMan9.Reply result;
    try{
        String screen=web.route();
        request.setAttribute("web",web);
        String group=screen.equals("login")?"thanhvien":state.role.equals("CUSTOMER")?"khachhang":"kho";
        request.getRequestDispatcher("/WEB-INF/view/"+group+"/"+screen+".jsp").include(request,response);
        result=(GaraMan9.Reply)request.getAttribute("reply");
    }catch(IllegalArgumentException e){result=web.error(e.getMessage());}
    response.setHeader("Cache-Control","no-store");
    response.setStatus(result.status());
    if(!result.redirect().isEmpty()){response.setHeader("Location",request.getContextPath()+"/"+result.redirect());return;}
    out.print(result.content());
}
%>
