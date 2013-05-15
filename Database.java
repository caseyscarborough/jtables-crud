import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;


public class Database {
	private Object[][] databaseResults;
	public ResultSet rows;
	public Object[] columns;
	public DefaultTableModel defaultTableModel;
	private Connection conn = null;
	
	public Database() {
		columns = new Object[]{"ID", "First_Name", "Last_Name", "Phone_Number", "Email_Address", "City", "State", "Date_Registered"};
		defaultTableModel = new DefaultTableModel(databaseResults, columns) {
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int column) {
				Class classToReturn;
				
				if((column >= 0) && column < getColumnCount()) {
					classToReturn = getValueAt(0, column).getClass();
				} else {
					classToReturn = Object.class;
				}
				return classToReturn;
			}
		};
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/customer", "root", "root");
			Statement sqlStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String select = "SELECT id, first_name, last_name, phone_number, email_address, city, state, date_registered FROM customer";
			rows = sqlStatement.executeQuery(select);
			Object[] tempRow;
			
			while(rows.next()) {
				tempRow = new Object[]{rows.getInt(1), rows.getString(2), rows.getString(3), rows.getString(4), 
						rows.getString(5), rows.getString(6), rows.getString(7), rows.getDate(8)};
				defaultTableModel.addRow(tempRow);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage()); 
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage()); 
		}
	}
}
