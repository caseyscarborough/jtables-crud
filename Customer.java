import javax.swing.JFrame;

/**
 * The main driver class for the application working with JTables and
 * MySQL to read, edit, create, and delete customer records.
 * @author Casey Scarborough
 * @version 1.2
 * @since 2013-05-13
 * @see Database
 * @see GUI
 */
public class Customer {
	/**
	 * The GUI object to display data.
	 */
	static GUI gui;
	
	/**
	 * The database object used to work with the MySQL database.
	 */
	static Database db;
	
	public static void main (String[] args) {
		db = new Database();
		gui = new GUI(db);
		
		gui.setColumnWidths(db.columns, 10, 60, 80, 100, 250, 80, 50, 100);
		gui.setSize(1110,500);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
	}
}