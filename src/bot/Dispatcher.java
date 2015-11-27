package bot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Dispatcher {
	
	private ArrayList<Order> orders;
	private JTextArea txtConsole;
	private JPanel htmlConsole;
	
	
	public Dispatcher(ArrayList<Order> orders, JTextArea txtConsole, JPanel htmlConsole) {
		this.orders = orders;
		this.txtConsole = txtConsole;
		this.htmlConsole = htmlConsole;
	}

	public void deploy() { //deploys new TaskProccessor thread for each order
		for (Order o : orders) {
			TaskProcessor p = new TaskProcessor(o, txtConsole, htmlConsole);
			p.execute();
		}
	}

}
