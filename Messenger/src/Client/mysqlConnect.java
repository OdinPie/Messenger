package Client;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysqlConnect {
	
	static Connection con;
	/*
	public mysqlConnect() {
	//public static void main(String [] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clientsinfo","root","mysql2000");
				//System.out.println("Connection successfull.");
			} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		//return null;
	}
	*/
	public static Connection getConnector() {
		try {
			
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clientsinfo","root","mysql2000");
			//System.out.println("Connection successfull.");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		return con;
	}
}
