"""Local demo only. Run a parameterized batch in one SQLite transaction."""
import base64
import sqlite3
import sys
from pathlib import Path

root = Path(__file__).resolve().parent.parent
database = root / "demo" / "garaman.db"
decode = lambda field: base64.b64decode(field).decode()
encode = lambda field: base64.b64encode(str(field).encode()).decode()
with sqlite3.connect(database, timeout=10) as conn:
    conn.execute("PRAGMA foreign_keys=ON")
    if sys.argv[1:] == ["init"]:
        if not conn.execute("SELECT name FROM sqlite_master WHERE name='tblMemberAccount9'").fetchone():
            conn.executescript("BEGIN;\n" + (root/"sql/schema.sql").read_text() + (root/"sql/seed.sql").read_text() + "\nCOMMIT;")
    else:
        for line in sys.stdin:
            sql, *args = map(decode, line.rstrip("\n").split("\t"))
            for row in conn.execute(sql, args):
                print("\t".join(encode("" if v is None else v) for v in row))
