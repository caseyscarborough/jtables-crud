import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Customer {
	
	static JLabel lID, lFirstName, lLastName, lPhoneNumber, lEmailAddress, lCity, lState, lDateRegistered;
	static JTextField tfFirstName, tfLastName, tfPhoneNumber, tfEmailAddress, tfCity, tfState, tfDateRegistered;
	static Date dateRegistered, sqlDateRegistered;
	
	static Object[][] databaseResults;
	static ResultSet rows;
	static Object[] columns = {"ID", "First_Name", "Last_Name", "Phone_Number", "Email_Address", "City", "State", "Date_Registered"};
	static DefaultTableModel defaultTableModel = new DefaultTableModel(databaseResults, columns) {
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
	
	static JTable table = new JTable(defaultTableModel);
	
	public static void main (String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Connection conn = null;
		
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
		
		table.setFont(new Font("Sans-serif", Font.PLAIN, 20)); // Change font
		table.setRowHeight(table.getRowHeight() + 16);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		setColumnWidths(columns, 10, 80, 80, 100, 250, 80, 50, 100);
		
		frame.setSize(1200,500);
		frame.setVisible(true);
	}
	
	public static void setColumnWidths(Object[] columns, int...widths) {
		TableColumn column;
		for(int i = 0; i < columns.length; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(widths[i]);
		}
	}
}
