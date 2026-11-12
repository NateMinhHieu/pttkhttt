package garaman.dao;
import java.util.*;
import java.sql.*;

public interface Database9 {
    record Query(String sql, List<String> args) {
        public Query(String sql, String... args) { this(sql, List.of(args)); }
    }
    // All statements in a batch commit together. The final query may return rows.
    List<List<String>> batch(List<Query> queries) throws Exception;
    default List<List<String>> read(String sql, String... args) throws Exception {
        return batch(List.of(new Query(sql,args)));
    }
    static Database9 mysql() {
        return queries -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c=DriverManager.getConnection(System.getenv("GARAMAN_DB_URL"),
                    System.getenv("GARAMAN_DB_USER"),System.getenv("GARAMAN_DB_PASSWORD"))) {
                c.setAutoCommit(false);
                List<List<String>> rows=new ArrayList<>();
                try {
                    for (Query q:queries) try (PreparedStatement ps=c.prepareStatement(q.sql())) {
                        for(int i=0;i<q.args().size();i++) ps.setString(i+1,q.args().get(i));
                        if(ps.execute()) try(ResultSet rs=ps.getResultSet()) {
                            while(rs.next()) {
                                List<String> row=new ArrayList<>();
                                for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
                                    row.add(Objects.toString(rs.getString(i),""));
                                rows.add(row);
                            }
                        }
                    }
                    c.commit(); return rows;
                } catch(Exception e) { c.rollback(); throw e; }
            }
        };
    }
}
