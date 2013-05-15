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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Customer {
	
	static JButton addCustomer, removeCustomer;
	static JLabel errorMessage;
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
		
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("DroidSerif-Regular.ttf"));
			font = font.deriveFont(Font.PLAIN, 18);
		} catch(IOException e) {
			font = new Font("Serif", Font.PLAIN, 18);
		} catch (FontFormatException e) {
			font = new Font("Serif", Font.PLAIN, 18);
		}
		
		table.setFont(font);
		table.setRowHeight(table.getRowHeight() + 8);
		table.setAutoCreateRowSorter(true);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent mouseEvent) {
				if (SwingUtilities.isRightMouseButton(mouseEvent)) {
					String value = JOptionPane.showInputDialog(null, "Enter Cell Value:");
					if(value != null) {
						table.setValueAt(value, table.getSelectedRow(), table.getSelectedColumn());
						String updateColumn;
						
						try {
							rows.absolute(table.getSelectedRow()+1);
							updateColumn = defaultTableModel.getColumnName(table.getSelectedColumn());
						
							switch(updateColumn) {
							case "Date_Registered":
								sqlDateRegistered = getADate(value);
								rows.updateDate(updateColumn, (Date) sqlDateRegistered);
								rows.updateRow();
								break;
							default:
								rows.updateString(updateColumn, value);
								rows.updateRow();
								break;
							} 
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		addCustomer = new JButton("Add Customer");
		removeCustomer = new JButton("Remove Customer");
		
		ListenForAction actionListener = new ListenForAction();
		addCustomer.addActionListener(actionListener);
		removeCustomer.addActionListener(actionListener);
		
		tfFirstName = new JTextField("First Name", 6);
		tfLastName = new JTextField("Last Name", 8);
		tfPhoneNumber = new JTextField("Phone Number", 8);
		tfEmailAddress = new JTextField("Email Address", 14);
		tfCity = new JTextField("City", 8);
		tfState = new JTextField("State", 3);
		tfDateRegistered = new JTextField("Date Registered", 9);
		
		ListenForFocus focusListener = new ListenForFocus();
		tfFirstName.addFocusListener(focusListener);
		tfLastName.addFocusListener(focusListener);
		tfPhoneNumber.addFocusListener(focusListener);
		tfEmailAddress.addFocusListener(focusListener);
		tfCity.addFocusListener(focusListener);
		tfState.addFocusListener(focusListener);
		tfDateRegistered.addFocusListener(focusListener);
		
		JPanel inputPanel = new JPanel();
		inputPanel.add(tfFirstName);
		inputPanel.add(tfLastName);
		inputPanel.add(tfPhoneNumber);
		inputPanel.add(tfEmailAddress);
		inputPanel.add(tfCity);
		inputPanel.add(tfState);
		inputPanel.add(tfDateRegistered);
		
		inputPanel.add(addCustomer);
		inputPanel.add(removeCustomer);
		
		errorMessage = new JLabel("");
		errorMessage.setForeground(Color.red);
		JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		errorPanel.add(errorMessage);
		
		
		frame.add(inputPanel, BorderLayout.SOUTH);
		frame.add(errorPanel, BorderLayout.NORTH);
		
		setColumnWidths(columns, 10, 60, 80, 100, 250, 80, 50, 100);
		DefaultTableCellRenderer centerColumns = new DefaultTableCellRenderer();
		centerColumns.setHorizontalAlignment(JLabel.CENTER);
		TableColumn tc = table.getColumn("ID");
		tc.setCellRenderer(centerColumns);
		
		
		
		frame.setSize(1110,500);
		frame.setVisible(true);
	}
	
	private static class ListenForAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == addCustomer) {
				String firstName, lastName, phoneNumber, emailAddress, city, state, dateRegistered;
				firstName = tfFirstName.getText();
				lastName = tfLastName.getText();
				phoneNumber = tfPhoneNumber.getText();
				emailAddress = tfEmailAddress.getText();
				city = tfCity.getText();
				state = tfState.getText();
				dateRegistered = tfDateRegistered.getText();
				
				if(!state.matches("[A-Za-z]{2}")) {
					errorMessage.setText("A state should be a two-letter abbreviation.");
					return;
				}
				
				if(!dateRegistered.matches("[0-2][0-9]{3}-[0-1][0-2]-[0-3][0-9]")) {
					errorMessage.setText("The date should be in the following format: YYYY-MM-DD");
					return;
				}
				
				sqlDateRegistered = getADate(dateRegistered);
				
				int customerID = 0;
				
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
					
					rows.last();
					customerID = rows.getInt(1);
					Object[] customer = {customerID, firstName, lastName, phoneNumber, emailAddress, city, state, sqlDateRegistered};
					defaultTableModel.addRow(customer);
					errorMessage.setText("");
				} catch (SQLException e2) {
					System.out.println(e2.getMessage());
					if (e2.getMessage().toString().startsWith("Data")) {
						errorMessage.setText("A state should be a two-letter abbreviation."); 
					}
				}
			} else if (e.getSource() == removeCustomer) {
				try {
					defaultTableModel.removeRow(table.getSelectedRow());
					rows.absolute(table.getSelectedRow());
					rows.deleteRow();
				} catch(SQLException e1) {
					System.out.println(e1.getMessage());
					errorMessage.setText(e1.getMessage());
				} catch(ArrayIndexOutOfBoundsException e1) {
					System.out.println(e1.getMessage());
					errorMessage.setText("To delete a customer, you must first select a row.");
				}
			}
		}
	}
	
	private static java.util.Date getADate(String dateRegistered) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			dateDateRegistered = dateFormatter.parse(dateRegistered);
			sqlDateRegistered = new java.sql.Date(dateDateRegistered.getTime());
		} catch (ParseException e1) {
			System.out.println(e1.getMessage());
			if(e1.getMessage().toString().startsWith("Unparseable date:")) {
				errorMessage.setText("The date should be in the following format: YYYY-MM-DD"); 
			}
		}
		return sqlDateRegistered;
	}
	
	// My terrible and possibly hack-ish way of implementing 'placeholders' in the JTextFields.
	private static class ListenForFocus implements FocusListener {
		
		public void focusGained(FocusEvent e) {
			if(tfFirstName.getText().equals("First Name") && e.getSource() == tfFirstName) {
				tfFirstName.setText("");
			} else if(tfLastName.getText().equals("Last Name") && e.getSource() == tfLastName) {
				tfLastName.setText("");
			} else if(tfPhoneNumber.getText().equals("Phone Number") && e.getSource() == tfPhoneNumber) {
				tfPhoneNumber.setText("");
			} else if(tfEmailAddress.getText().equals("Email Address") && e.getSource() == tfEmailAddress) {
				tfEmailAddress.setText("");
			} else if(tfCity.getText().equals("City") && e.getSource() == tfCity) {
				tfCity.setText("");
			} else if(tfState.getText().equals("State") && e.getSource() == tfState) {
				tfState.setText("");
			} else if(tfDateRegistered.getText().equals("Date Registered") && e.getSource() == tfDateRegistered) {
				tfDateRegistered.setText("");
			}
		}

		public void focusLost(FocusEvent e) {
			if(tfFirstName.getText().equals("") && e.getSource() == tfFirstName) {
				tfFirstName.setText("First Name");
			} else if(tfLastName.getText().equals("") && e.getSource() == tfLastName) {
				tfLastName.setText("Last Name");
			} else if(tfPhoneNumber.getText().equals("") && e.getSource() == tfPhoneNumber) {
				tfPhoneNumber.setText("Phone Number");
			} else if(tfEmailAddress.getText().equals("") && e.getSource() == tfEmailAddress) {
				tfEmailAddress.setText("Email Address");
			} else if(tfCity.getText().equals("") && e.getSource() == tfCity) {
				tfCity.setText("City");
			} else if(tfState.getText().equals("") && e.getSource() == tfState) {
				tfState.setText("State");
			} else if(tfDateRegistered.getText().equals("") && e.getSource() == tfDateRegistered) {
				tfDateRegistered.setText("Date Registered");
			}
		}
		
	}
	
	public static void setColumnWidths(Object[] columns, int...widths) {
		TableColumn column;
		for(int i = 0; i < columns.length; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(widths[i]);
		}
	}
}
