package generate;

import java.sql.*;

public class ReadSql {
	public static ResultSet executeSQL(String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:NMTdata-ultimate.sqlite3");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);

			stmt.close();
			c.commit();
			c.close();
			
			System.out.println("________Repositories loaded successfully_________");
			return rs;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
			
			return null;
		}
	}
	
	public static void main(String[] args) {
		executeSQL("SELECT * FROM repositories");
	}
}
