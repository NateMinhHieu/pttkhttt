package garaman.view;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
public class Page9 {
    public static String h(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
    public static String url(String s){return URLEncoder.encode(s,StandardCharsets.UTF_8);}
    public static String money(long n){return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(n)+" ₫";}
    public static String link(String route,String label){return "<a href='?"+h(route)+"'>"+h(label)+"</a>";}
    public static String field(String label,String name,String value,String type){return "<label>"+h(label)+"<input required name='"+name+"' type='"+type+"' value='"+h(value)+"'></label>";}
    public static String btn(String label){return "<button>"+h(label)+"</button>";}
    public static String page(String title,String body,String nav){
        return "<!doctype html><html lang='vi'><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>"+h(title)+" · GaraMan</title><link rel='stylesheet' href='app.css'><header><strong>Gara<span>Man</span></strong><nav>"+nav+"</nav></header><main><h1>"+h(title)+"</h1>"+body+"</main></html>";
    }
}
