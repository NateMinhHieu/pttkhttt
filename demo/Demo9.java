import com.sun.net.httpserver.*;
import garaman.GaraMan9;
import garaman.dao.Database9;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Demo9 {
    static final Map<String,GaraMan9.Session> sessions=new ConcurrentHashMap<>();
    static String encode(String s){return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));}
    static List<List<String>> sqlite(List<Database9.Query> batch)throws Exception{
        Process p=new ProcessBuilder(System.getenv().getOrDefault("PYTHON","python3"),"demo/sqlite_bridge.py").redirectError(ProcessBuilder.Redirect.INHERIT).start();
        try(var w=p.outputWriter(StandardCharsets.UTF_8)){
            for(var q:batch){w.write(encode(q.sql()));for(var a:q.args())w.write("\t"+encode(a));w.newLine();}
        }
        List<List<String>> rows=new ArrayList<>();
        try(var r=p.inputReader(StandardCharsets.UTF_8)){
            for(String line;(line=r.readLine())!=null;)rows.add(Arrays.stream(line.split("\t",-1)).map(x->new String(Base64.getDecoder().decode(x),StandardCharsets.UTF_8)).toList());
        }
        if(p.waitFor()!=0)throw new IOException("SQLite transaction failed");return rows;
    }
    static void params(Map<String,String> m,String query){
        if(query==null)return;
        for(String pair:query.split("&")){String[] kv=pair.split("=",2);if(kv.length==2)m.put(URLDecoder.decode(kv[0],StandardCharsets.UTF_8),URLDecoder.decode(kv[1],StandardCharsets.UTF_8));}
    }
    static void serve(HttpExchange e)throws IOException{
        try{
            if(e.getRequestURI().getPath().equals("/app.css")){
                byte[] css=Files.readAllBytes(Path.of("src/main/webapp/app.css"));e.getResponseHeaders().set("Content-Type","text/css");e.sendResponseHeaders(200,css.length);e.getResponseBody().write(css);return;
            }
            if(!e.getRequestURI().getPath().equals("/")){e.sendResponseHeaders(404,-1);return;}
            String sid="";
            for(String cookie:e.getRequestHeaders().getOrDefault("Cookie",List.of()))for(String token:cookie.split(";"))if(token.trim().startsWith("GM="))sid=token.trim().substring(3);
            if(!sessions.containsKey(sid)){sid=UUID.randomUUID().toString();sessions.put(sid,new GaraMan9.Session());e.getResponseHeaders().add("Set-Cookie","GM="+sid+"; HttpOnly; SameSite=Lax; Path=/");}
            var m=new HashMap<String,String>();params(m,e.getRequestURI().getRawQuery());
            if(e.getRequestMethod().equals("POST"))params(m,new String(e.getRequestBody().readNBytes(65536),StandardCharsets.UTF_8));
            var result=DemoPages.handle(new GaraMan9(Demo9::sqlite,sessions.get(sid),m,e.getRequestMethod()));
            e.getResponseHeaders().set("Cache-Control","no-store");
            if(!result.redirect().isEmpty()){e.getResponseHeaders().set("Location","/"+result.redirect());e.sendResponseHeaders(303,-1);return;}
            byte[] body=result.content().getBytes(StandardCharsets.UTF_8);e.getResponseHeaders().set("Content-Type","text/html; charset=utf-8");e.sendResponseHeaders(result.status(),body.length);e.getResponseBody().write(body);
        } catch(Exception ex){ex.printStackTrace();e.sendResponseHeaders(500,-1);}finally{e.close();}
    }
    public static void main(String[] args)throws Exception{
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",8080),0);
        server.createContext("/",Demo9::serve);server.setExecutor(Executors.newCachedThreadPool());server.start();
        System.out.println("GaraMan: http://127.0.0.1:8080/");
    }
}
