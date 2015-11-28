package bot;

import java.util.ArrayList;
import javafx.scene.web.WebView;
import javax.swing.JTextArea;

@SuppressWarnings("restriction")
public class Dispatcher {
	
	private ArrayList<Order> orders;
	private JTextArea txtConsole;
	private WebView htmlConsole;
	
	public Dispatcher(ArrayList<Order> orders, JTextArea txtConsole, WebView htmlConsole) {
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
