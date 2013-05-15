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
import java.sql.Date;
import java.sql.SQLException;
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
import javax.swing.table.TableColumn;


public class GUI extends JFrame {
	private JButton addCustomer, removeCustomer;
	private JLabel errorMessage;
	private JTextField tfFirstName, tfLastName, tfPhoneNumber, tfEmailAddress, tfCity, tfState, tfDateRegistered;
	private JTable table;
	private java.util.Date dateDateRegistered, sqlDateRegistered;
	private Font font;
	
	public GUI(Database db) {
		super();
		table = new JTable(db.defaultTableModel);
		
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
		
		ListenForMouse mouseListener = new ListenForMouse();
		table.addMouseListener(mouseListener);
		
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);
		
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
		
		
		this.add(inputPanel, BorderLayout.SOUTH);
		this.add(errorPanel, BorderLayout.NORTH);
		
		DefaultTableCellRenderer centerColumns = new DefaultTableCellRenderer();
		centerColumns.setHorizontalAlignment(JLabel.CENTER);
		TableColumn tc = table.getColumn("ID");
		tc.setCellRenderer(centerColumns);
	}
	
	private class ListenForAction implements ActionListener {
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
					Customer.db.rows.moveToInsertRow();
					Customer.db.rows.updateString("first_name", firstName);
					Customer.db.rows.updateString("last_name", lastName);
					Customer.db.rows.updateString("phone_number", phoneNumber);
					Customer.db.rows.updateString("email_address", emailAddress);
					Customer.db.rows.updateString("city", city);
					Customer.db.rows.updateString("state", state);
					Customer.db.rows.updateDate("date_registered", (Date) sqlDateRegistered);
					
					Customer.db.rows.insertRow();
					Customer.db.rows.updateRow();
					
					Customer.db.rows.last();
					customerID = Customer.db.rows.getInt(1);
					Object[] customer = {customerID, firstName, lastName, phoneNumber, emailAddress, city, state, sqlDateRegistered};
					Customer.db.defaultTableModel.addRow(customer);
					errorMessage.setText("");
				} catch (SQLException e2) {
					System.out.println(e2.getMessage());
					if (e2.getMessage().toString().startsWith("Data")) {
						errorMessage.setText("A state should be a two-letter abbreviation."); 
					}
				}
			} else if (e.getSource() == removeCustomer) {
				try {
					Customer.db.defaultTableModel.removeRow(table.getSelectedRow());
					Customer.db.rows.absolute(table.getSelectedRow());
					Customer.db.rows.deleteRow();
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
	
	// My terrible and possibly hack-ish way of implementing 'placeholders' in the JTextFields.
	private class ListenForFocus implements FocusListener {
		
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
	
	private class ListenForMouse extends MouseAdapter {
		public void mouseReleased(MouseEvent mouseEvent) {
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				String value = JOptionPane.showInputDialog(null, "Enter Cell Value:");
				if(value != null) {
					table.setValueAt(value, table.getSelectedRow(), table.getSelectedColumn());
					String updateColumn;
					
					try {
						Customer.db.rows.absolute(table.getSelectedRow()+1);
						updateColumn = Customer.db.defaultTableModel.getColumnName(table.getSelectedColumn());
					
						switch(updateColumn) {
						case "Date_Registered":
							sqlDateRegistered = getADate(value);
							Customer.db.rows.updateDate(updateColumn, (Date) sqlDateRegistered);
							Customer.db.rows.updateRow();
							break;
						default:
							Customer.db.rows.updateString(updateColumn, value);
							Customer.db.rows.updateRow();
							break;
						} 
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void setColumnWidths(Object[] columns, int...widths) {
		TableColumn column;
		for(int i = 0; i < columns.length; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(widths[i]);
		}
	}
	
	public void setErrorMessage(String message) {
		errorMessage.setText(message);
	}
	
	public java.util.Date getADate(String dateRegistered) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			dateDateRegistered = dateFormatter.parse(dateRegistered);
			sqlDateRegistered = new java.sql.Date(dateDateRegistered.getTime());
		} catch (ParseException e1) {
			System.out.println(e1.getMessage());
			if(e1.getMessage().toString().startsWith("Unparseable date:")) {
				this.setErrorMessage("The date should be in the following format: YYYY-MM-DD"); 
			}
		}
		return sqlDateRegistered;
	}
}
