"""Compile the declaration from each application JSP for the standalone JDK demo."""
from pathlib import Path
import re

def prepare(directory):
    directory.mkdir(parents=True, exist_ok=True)
    sources = []
    routes = []
    for page in sorted(Path("src/main/webapp/WEB-INF/view").rglob("*.jsp")):
        text = page.read_text(encoding="utf-8")
        imports = re.search(r'import="([^"]+)"', text)[1].split(",")
        methods = re.search(r"<%!(.*?)%>", text, re.S)[1]
        name = page.stem
        source = directory / (name + ".java")
        source.write_text("\n".join("import " + item + ";" for item in imports)
                          + "\nclass " + name + " {\n" + methods + "\n}", encoding="utf-8")
        sources.append(str(source))
        routes.append(f'case "{name}" -> {name}.show(web);')
    dispatch = directory / "DemoPages.java"
    dispatch.write_text("import garaman.GaraMan9;\nclass DemoPages {\n"
        "static GaraMan9.Reply handle(GaraMan9 web) throws Exception {\n"
        "synchronized(web.s){try{return switch(web.route()){\n"
        + "\n".join(routes) + '\ndefault -> throw new IllegalArgumentException("Trang không tồn tại.");'
        + "\n};}catch(IllegalArgumentException e){return web.error(e.getMessage());}}}}", encoding="utf-8")
    return [*sources, str(dispatch)]
