package executor;

import java.text.DateFormat;
import javafx.scene.web.WebView;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import backend.Order;
import backend.main;

@SuppressWarnings("restriction")
public class TaskProcessor extends SwingWorker<Object, Object> {

	private Order order;
	private JTextArea txtConsole;
	public static Stage stage;
	private WebView htmlConsole;
	
	public final DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");

	public TaskProcessor(Order order, JTextArea txtConsole, WebView htmlConsole) {
		this.order = order;
		this.txtConsole = txtConsole;
		this.htmlConsole = htmlConsole;

		print("Order " + order.getOrderNum() + " Thread Initialized");
		main.pushToWorkerArray(this); //gives worker so it can be cancelled later
	}

	public void print(String s) { //print to text console and to software console
		txtConsole.setText(txtConsole.getText() + (txtConsole.getText().isEmpty() ? "" : "\n") + s + " (" + dateFormat.format(new Date()).toString() + ")");
		System.out.println(s + " (" + dateFormat.format(new Date()).toString() + ")");
	}

	public void display(String html) { //print to text console and to software console
		htmlConsole.getEngine().loadContent(html); //needs fixing, try java fx (fx also has browser built in, could replace firefox)

	}

	private void loop() { //main loop of software

		stage = Stage.LINK_FINDING; //start at link finding

		int refreshRate;
		try { //if no refresh rate, make it 400
			refreshRate = Integer.parseInt(order.getOrderSettings().getRefreshRate());
		} catch (Exception e) {
			refreshRate = 400;
		}	

		System.out.println("Refresh Rate Order " + order.getOrderNum() + ": " + refreshRate);

		LinkFinder linkFinder = new LinkFinder(order.getItems(), order.getOrderNum(), refreshRate, this); //new link finder

		while (!isCancelled()) { //you must check if cancelled in every loop!!!
			System.out.println("Stage: " + stage.name());


			switch (stage) {

			case LINK_FINDING:
				System.out.println("Find Links");
				linkFinder.findThem();	
				break;
			case ADD_TO_CART:
				System.out.println("Add to cart");
				break;
			case CHECKOUT:
				System.out.println("Checkout");
				break;
			default:
				System.out.println("Huh?");
				break;


			}

		}
		if (isCancelled()) print("Order " + order.getOrderNum() + " Thread Aborted");

	}


	@Override
	protected Object[] doInBackground() throws Exception { //background task of each order
		loop();
		Object[] o = new Object[2];
		return o;
	}
	

	public void setStatus(int itemNumber, String text) { //sets status of item in table
		order.getModel().setValueAt(text, itemNumber - 1, 5);
	}
	


	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!



}
