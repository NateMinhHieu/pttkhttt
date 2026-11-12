<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" import="garaman.view.Page9,garaman.*,garaman.GaraMan9.Reply,garaman.dao.*,garaman.model.*,java.util.*,java.math.*,java.time.*" %>
<%!
public static Reply show(GaraMan9 w) throws Exception {
    w.draft();var categories=new SparePartCategoryDAO9(w.db).getCategories();
    if(w.action().equals("savePart"))return submitSave(w,categories);
    StringBuilder options=new StringBuilder();for(var c:categories)options.append("<option value='"+c.id+"'>"+Page9.h(c.name)+"</option>");
    return w.screen("Thêm phụ tùng",w.form("savePart")+Page9.field("Tên phụ tùng","name","","text")+"<label>Loại<select name='category'>"+options+"</select></label>"
        +Page9.field("Mô tả","description","","text")+Page9.field("Đơn vị","unit","","text")+Page9.field("Giá bán","price","","number")+Page9.btn("Lưu phụ tùng")+"</form>"+Page9.link("a=parts","Hủy"));
}
private static Reply submitSave(GaraMan9 w, List<SparePartCategory9> categories) throws Exception {
        SparePart9 p=new SparePart9();p.name=w.need("name",150);p.description=w.need("description",500);p.unit=w.need("unit",30);
        long category=w.id("category");p.category=categories.stream().filter(c->c.id==category).findFirst().orElseThrow(()->new IllegalArgumentException("Loại không tồn tại."));
        p.sellingPrice=w.price("price");new SparePartDAO9(w.db).addPart(p);return w.go("a=parts&q="+Page9.url(p.name));
}

%><%
GaraMan9 w=(GaraMan9)request.getAttribute("web");
try { request.setAttribute("reply",show(w)); }
catch(IllegalArgumentException ex){ request.setAttribute("reply",w.error(ex.getMessage())); }
catch(Exception ex){throw new javax.servlet.ServletException(ex);}
%>
