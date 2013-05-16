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


/**
 * GUI class used to display and interact with information from
 * a MySQL database graphically.
 * @author Casey Scarborough
 * @since 2013-05-15
 * @see Database
 */
public class GUI extends JFrame {
	
	// Create buttons, labels, text fields, a new JTable, a Font, and the dates
	private JButton addCustomer, removeCustomer;
	private JLabel errorMessage;
	private JTextField tfFirstName, tfLastName, tfPhoneNumber, tfEmailAddress, tfCity, tfState, tfDateRegistered;
	private JTable table;
	private java.util.Date dateDateRegistered, sqlDateRegistered;
	private Font font;
	
	/**
	 * Constructor for the GUI class.
	 * @param db the database object used to manipulate the database.
	 */
	public GUI(Database db) {
		super();
		table = new JTable(db.defaultTableModel);
		
		// If the font is available, set it, if not, use the Serif font
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("DroidSerif-Regular2.ttf"));
			font = font.deriveFont(Font.PLAIN, 18);
		} catch(IOException e) {
			font = new Font("Serif", Font.PLAIN, 18);
		} catch (FontFormatException e) {
			font = new Font("Serif", Font.PLAIN, 18);
		}
		
		table.setFont(font);
		table.setRowHeight(table.getRowHeight() + 8);
		table.setAutoCreateRowSorter(true);
		
		// Create a new mouse listener and assign it to the table
		ListenForMouse mouseListener = new ListenForMouse();
		table.addMouseListener(mouseListener);
		
		// Create a JScrollPane and add it to the center of the window
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);
		
		// Set button values
		addCustomer = new JButton("Add Customer");
		removeCustomer = new JButton("Remove Customer");
		
		// Add action listeners to the buttons to listen for clicks
		ListenForAction actionListener = new ListenForAction();
		addCustomer.addActionListener(actionListener);
		removeCustomer.addActionListener(actionListener);
		
		// Set the text field widths and values
		tfFirstName = new JTextField("First Name", 6);
		tfLastName = new JTextField("Last Name", 8);
		tfPhoneNumber = new JTextField("Phone Number", 8);
		tfEmailAddress = new JTextField("Email Address", 14);
		tfCity = new JTextField("City", 8);
		tfState = new JTextField("State", 3);
		tfDateRegistered = new JTextField("Date Registered", 9);
		
		// Create a focus listener and add it to each text field to remove text when clicked on
		ListenForFocus focusListener = new ListenForFocus();
		tfFirstName.addFocusListener(focusListener);
		tfLastName.addFocusListener(focusListener);
		tfPhoneNumber.addFocusListener(focusListener);
		tfEmailAddress.addFocusListener(focusListener);
		tfCity.addFocusListener(focusListener);
		tfState.addFocusListener(focusListener);
		tfDateRegistered.addFocusListener(focusListener);
		
		// Create a new panel and add the text fields and add/remove buttons to it
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
		
		// Change settings and add the error message to the error panel
		errorMessage = new JLabel("");
		errorMessage.setForeground(Color.red);
		JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		errorPanel.add(errorMessage);
		
		// set the input panel to the bottom and the error panel to the top
		this.add(inputPanel, BorderLayout.SOUTH);
		this.add(errorPanel, BorderLayout.NORTH);
		
		// Center the ID column in the table
		DefaultTableCellRenderer centerColumns = new DefaultTableCellRenderer();
		centerColumns.setHorizontalAlignment(JLabel.CENTER);
		TableColumn tc = table.getColumn("ID");
		tc.setCellRenderer(centerColumns);
	}
	
	/**
	 * ActionListener implementation to listen for actions such
	 * as button clicks.
	 * @author Casey Scarborough
	 */
	private class ListenForAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == addCustomer) { // If the user clicks Add Customer, add the information into the database
				// Create variables to hold information to be inserted, and get the info from the text fields
				String firstName, lastName, phoneNumber, emailAddress, city, state, dateRegistered;
				firstName = tfFirstName.getText();
				lastName = tfLastName.getText();
				phoneNumber = tfPhoneNumber.getText();
				emailAddress = tfEmailAddress.getText();
				city = tfCity.getText();
				state = tfState.getText();
				dateRegistered = tfDateRegistered.getText();
				
				// Check that the state matches the required format, if not display an error and return
				if(!state.matches("[A-Za-z]{2}")) {
					errorMessage.setText("A state should be a two-letter abbreviation.");
					return;
				}
				
				// Check that the date matches the required format, if not display an error and return
				if(!dateRegistered.matches("[0-2][0-9]{3}-[0-1][0-2]-[0-3][0-9]")) {
					errorMessage.setText("The date should be in the following format: YYYY-MM-DD");
					return;
				}
				
				// Convert the date
				sqlDateRegistered = getADate(dateRegistered);
				
				int customerID = 0;
				
				try { // Attempt to insert the information into the database
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
					Customer.db.defaultTableModel.addRow(customer); // Add the row to the screen
					errorMessage.setText(""); // Remove the error message if one was displayed
				} catch (SQLException e2) { // Catch any exceptions and display appropriate errors
					System.out.println(e2.getMessage());
					if (e2.getMessage().toString().startsWith("Data")) {
						errorMessage.setText("A state should be a two-letter abbreviation."); 
					}
				}
			} else if (e.getSource() == removeCustomer) {
				try { // If the user clicked remove customer, delete from database and remove from table
					Customer.db.defaultTableModel.removeRow(table.getSelectedRow());
					Customer.db.rows.absolute(table.getSelectedRow());
					Customer.db.rows.deleteRow();
				} catch(SQLException e1) { // Catch any exceptions
					System.out.println(e1.getMessage());
					errorMessage.setText(e1.getMessage());
				} catch(ArrayIndexOutOfBoundsException e1) {
					System.out.println(e1.getMessage());
					errorMessage.setText("To delete a customer, you must first select a row.");
				}
			}
		}
	}
	
	
	/**
	 * FocusListener implementation used to listen for JTextFields
	 * being focused on.
	 * @author Casey Scarborough
	 */
	private class ListenForFocus implements FocusListener {
		// My terrible and possibly hack-ish way of implementing 'placeholders' in the JTextFields.
		public void focusGained(FocusEvent e) { // If a text field gains focus and has the default text, remove the text
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

		public void focusLost(FocusEvent e) { // If the text field loses focus and is blank, set the default text back
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
	
	/**
	 * ListenForMouse class that listens for mouse events on cells so that
	 * they can be updated.
	 * @author Casey Scarborough
	 *
	 */
	private class ListenForMouse extends MouseAdapter {
		public void mouseReleased(MouseEvent mouseEvent) {
			// If the mouse is released and the click was a right click
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				// Create a dialog for the user to enter new data
				String value = JOptionPane.showInputDialog(null, "Enter Cell Value:");
				if(value != null) { // If they entered info, update the database
					table.setValueAt(value, table.getSelectedRow(), table.getSelectedColumn());
					String updateColumn;
					
					try { // Go to the row in the db
						Customer.db.rows.absolute(table.getSelectedRow()+1);
						updateColumn = Customer.db.defaultTableModel.getColumnName(table.getSelectedColumn());
						
						switch(updateColumn) {
						// if the column was date_registered, convert date update using a Date
						case "Date_Registered":
							sqlDateRegistered = getADate(value);
							Customer.db.rows.updateDate(updateColumn, (Date) sqlDateRegistered);
							Customer.db.rows.updateRow();
							break;
						default: // otherwise update using a String
							Customer.db.rows.updateString(updateColumn, value);
							Customer.db.rows.updateRow();
							break;
						} 
					} catch (SQLException e1) { // Catch any exceptions and display an error
						errorMessage.setText("An error has occurred.");
						System.out.println(e1.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Method used to set the column widths of the JTable being displayed.
	 * @param columns the Object array of column names.
	 * @param widths the specified widths to set the columns to.
	 */
	public void setColumnWidths(Object[] columns, int...widths) {
		TableColumn column;
		for(int i = 0; i < columns.length; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(widths[i]);
		}
	}
	
	/**
	 * Used to set the message on the errorPanel.
	 * @param message the message to display.
	 */
	public void setErrorMessage(String message) {
		errorMessage.setText(message);
	}
	
	/**
	 * Converts a date into one that can be recorded into the database.
	 * @param dateRegistered the date that the user inputs in the Date Registered field.
	 * @return the newly converted date.
	 */
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
