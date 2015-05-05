/**
 * DBにアクセスするためのクラスです。
 */
package jp.co.hiroshimabank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author IBM梅沢
 *
 */
public class DBAccessor {
	//DB driver
	private static final String DB_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	//Datasource Name
	private static final String LOOKUPNAME = "jdbc/SQL Database-19";

	/**
	 * コネクションを取得します。
	 * 
	 * @return コネクション
	 */
	public Connection getConnection() {

		Connection conn = null;
		
		String VCAP_SERVICES = System.getenv ("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
			DataSource dataSource = null;
			try {
				Context context = new InitialContext();
				dataSource = (DataSource) context.lookup(LOOKUPNAME);
				conn = dataSource.getConnection();
				conn.setAutoCommit(false);
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        } else {
        	String user = "zrkldncd";
        	String password = "boqizox9z5mq";
        	String databaseUrl = "jdbc:db2://23.246.233.47:50000/I_966690";
        	System.out.println("VCAP_SERVICES is null. init the database connection paramater by hard code.");
        	
        	try{  
    	    	Class.forName(DB_DRIVER);
    	    	conn = DriverManager.getConnection(databaseUrl , user , password) ;   
    	    }catch(SQLException se){   
    	    	 System.out.println("DB connection failed!");   
    	    	 se.printStackTrace() ;
    	    }catch(Exception ex){
    	    	System.out.println("DB connection failed-driver exception!");
    	    	ex.printStackTrace();
    	    }
        }
		return conn;
	}

	/**
	 * DBとの接続を切断します。
	 * 
	 * @param conn
	 * @param stmt
	 * @param result
	 */
	protected void close(Connection conn, Statement stmt, ResultSet result) {
		// close connections.
		if (result != null) {
			try {
				result.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Connection conn = new DBAccessor().getConnection();
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
