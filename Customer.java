import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Customer {
	
	static JLabel lID, lFirstName, lLastName, lPhoneNumber, lEmailAddress, lCity, lState, lDateRegistered;
	static JTextField tfFirstName, tfLastName, tfPhoneNumber, tfEmailAddress, tfCity, tfState, tfDateRegistered;
	static java.util.Date dateDateRegistered, sqlDateRegistered;
	
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
		
		table.setFont(new Font("Sans-serif", Font.PLAIN, 18)); // Change font
		table.setRowHeight(table.getRowHeight() + 8);
		table.setAutoCreateRowSorter(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		JButton addCustomer = new JButton("Add Customer");
		addCustomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String firstName, lastName, phoneNumber, emailAddress, city, state, dateRegistered;
				firstName = tfFirstName.getText();
				lastName = tfLastName.getText();
				phoneNumber = tfPhoneNumber.getText();
				emailAddress = tfEmailAddress.getText();
				city = tfCity.getText();
				state = tfState.getText();
				dateRegistered = tfDateRegistered.getText();
				
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				
				try {
					dateDateRegistered = dateFormatter.parse(dateRegistered);
					sqlDateRegistered = new java.sql.Date(dateDateRegistered.getTime());
				} catch (ParseException e1) {
					System.out.println(e1.getMessage()); 
				}
				
				try {
					rows.moveToInsertRow();
					rows.updateString("first_name", firstName);
					rows.updateString("last_name", lastName);
					rows.updateString("phone_number", phoneNumber);
					rows.updateString("email_address", emailAddress);
					rows.updateString("city", city);
					rows.updateString("state", state);
					rows.updateDate("date_registered", (Date) sqlDateRegistered);
					
					rows.insertRow();
					rows.updateRow();
				} catch (SQLException e2) {
					System.out.println(e2.getMessage()); 
				}
				
				int customerID = 0;
				try {
					rows.last();
					customerID = rows.getInt(1);
				} catch(SQLException e3) {
					System.out.println(e3.getMessage()); 
				}
				
				Object[] customer = {customerID, firstName, lastName, phoneNumber, emailAddress, city, state, sqlDateRegistered};
				defaultTableModel.addRow(customer);
			}
		});
		
		tfFirstName = new JTextField("First Name", 8);
		tfLastName = new JTextField("Last Name", 12);
		tfPhoneNumber = new JTextField("Phone Number", 8);
		tfEmailAddress = new JTextField("Email Address", 20);
		tfCity = new JTextField("City", 8);
		tfState = new JTextField("State", 3);
		tfDateRegistered = new JTextField("Date Registered", 10);
		
		JPanel inputPanel = new JPanel();
		inputPanel.add(tfFirstName);
		inputPanel.add(tfLastName);
		inputPanel.add(tfPhoneNumber);
		inputPanel.add(tfEmailAddress);
		inputPanel.add(tfCity);
		inputPanel.add(tfState);
		inputPanel.add(tfDateRegistered);
		
		inputPanel.add(addCustomer);
		
		frame.add(inputPanel, BorderLayout.SOUTH);
		
		setColumnWidths(columns, 10, 60, 80, 100, 250, 80, 50, 100);
		DefaultTableCellRenderer centerColumns = new DefaultTableCellRenderer();
		centerColumns.setHorizontalAlignment(JLabel.CENTER);
		TableColumn tc = table.getColumn("ID");
		tc.setCellRenderer(centerColumns);
		
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
