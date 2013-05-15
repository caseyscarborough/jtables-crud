import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Customer {
	
	
	static java.util.Date dateDateRegistered, sqlDateRegistered;
	static GUI gui;
	static Object[][] databaseResults;
	static ResultSet rows;
	static Object[] columns = {"ID", "First_Name", "Last_Name", "Phone_Number", "Email_Address", "City", "State", "Date_Registered"};
	static DefaultTableModel defaultTableModel = new DefaultTableModel(databaseResults, columns) {
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
	
	public static void main (String[] args) {
		gui = new GUI(defaultTableModel);
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
		
		
		
		gui.setColumnWidths(columns, 10, 60, 80, 100, 250, 80, 50, 100);
		gui.setSize(1110,500);
		gui.setVisible(true);
	}
	
	
	
	public static java.util.Date getADate(String dateRegistered) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			dateDateRegistered = dateFormatter.parse(dateRegistered);
			sqlDateRegistered = new java.sql.Date(dateDateRegistered.getTime());
		} catch (ParseException e1) {
			System.out.println(e1.getMessage());
			if(e1.getMessage().toString().startsWith("Unparseable date:")) {
				gui.setErrorMessage("The date should be in the following format: YYYY-MM-DD"); 
			}
		}
		return sqlDateRegistered;
	}
	
		
	
}