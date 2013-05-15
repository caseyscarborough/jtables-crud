import javax.swing.JFrame;

public class Customer {
	static GUI gui;
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