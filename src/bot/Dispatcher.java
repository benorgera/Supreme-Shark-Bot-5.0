package bot;

import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;

public class Dispatcher {
	
	private ArrayList<Order> orders;
	private JTextArea txtConsole;
	private JEditorPane htmlConsole;
	
	public Dispatcher(ArrayList<Order> orders, JTextArea txtConsole, JEditorPane htmlConsole) {
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
