package executor;

import java.net.Authenticator;
import java.util.ArrayList;
import javafx.scene.web.WebView;
import javax.swing.JTextArea;
import backend.Order;
import backend.Main;

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
		
		ProxyAuthenticator auth = new ProxyAuthenticator(); 
		auth.initialize();
		Authenticator.setDefault(auth);
		
		for (Order o : orders) {
			Thread thread = new Thread(new TaskProcessor(o, txtConsole, htmlConsole));
			Main.pushToWorkerArray(thread);
			thread.start();		
		}
	}

}
