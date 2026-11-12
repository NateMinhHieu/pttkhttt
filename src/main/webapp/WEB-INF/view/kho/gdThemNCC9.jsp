<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    if(w.action().equals("saveSupplier"))return submitSave(w);
    return w.screen("Thêm nhà cung cấp",w.form("saveSupplier")+Page9.field("Tên nhà cung cấp","name","","text")+Page9.field("Địa chỉ","address","","text")
        +Page9.field("Điện thoại","phone","","tel")+"<label>Email<input type='email' name='email'></label>"+Page9.btn("Lưu nhà cung cấp")+"</form>"+Page9.link("a=suppliers","Hủy"));
}
private static Reply submitSave(GaraMan9 w) throws Exception {
        Supplier9 s=new Supplier9();s.name=w.need("name",150);s.phone=w.need("phone",20);s.address=w.need("address",255);s.email=w.val("email");
        new SupplierDAO9(w.db).addSupplier(s);return w.go("a=suppliers&q="+Page9.url(s.name));
}

%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
