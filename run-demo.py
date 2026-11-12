"""Run the two modules with a local JDK and Python, without Maven or a DB server."""
import os
import subprocess
import sys
from pathlib import Path
os.chdir(Path(__file__).resolve().parent)
os.environ["PYTHON"] = sys.executable
subprocess.run([sys.executable, "demo/sqlite_bridge.py", "init"], check=True)
out = Path("target/demo")
out.mkdir(parents=True, exist_ok=True)
sources = [str(p) for p in Path("src/main/java").rglob("*.java")]
from demo.compile_pages import prepare
sources.extend(prepare(Path("target/generated-pages")))
subprocess.run(["java", "-m", "jdk.compiler/com.sun.tools.javac.Main", "-encoding", "UTF-8", "-d", str(out), *sources, "demo/Demo9.java"], check=True)
subprocess.run(["java", "-cp", str(out), "Demo9"], check=True)
