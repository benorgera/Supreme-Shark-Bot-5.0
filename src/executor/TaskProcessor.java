package executor;

import java.text.DateFormat;
import javafx.scene.web.WebView;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
import backend.Order;
import backend.Main;

@SuppressWarnings("restriction")
public class TaskProcessor implements Runnable {

	private Order order;
	private JTextArea txtConsole;
	public static Stage stage;
	private WebView htmlConsole;
	private HTTPConnector connector;

	public final DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");

	public TaskProcessor(Order order, JTextArea txtConsole, WebView htmlConsole) {
		this.order = order;
		this.txtConsole = txtConsole;
		this.htmlConsole = htmlConsole;

		connector = new HTTPConnector(order.getOrderSettings(), this);
		print("Thread Initialized");



	}

	public void print(String s) { //print to system console and to software console
		try {
			txtConsole.setText(txtConsole.getText() + (txtConsole.getText().isEmpty() ? "" : "\n") + "Order " + order.getOrderNum() + ": " + s + " (" + dateFormat.format(new Date()).toString() + ")");
		} catch (java.lang.Error e) { //if the writelock couldn't be acquired (simultaneous writing occurred), retry
			print(s);
		}
		
		printSys(s);
	}

	public void printSys(String s) { //print just to system console
		System.out.println("Order " + order.getOrderNum() + ": " + s + " (" + dateFormat.format(new Date()).toString() + ")");
	}

	public void display(String html) { //display html
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

		printSys("Refresh Rate: " + refreshRate);

		LinkFinder linkFinder = new LinkFinder(order.getItems(), refreshRate, this, connector); //new link finder

		while (!Thread.currentThread().isInterrupted()) { //you must check if cancelled in every loop!!!

			printSys("Stage: " + stage.name());

			switch (stage) {

			case LINK_FINDING:
				linkFinder.findThem();	
				break;
			case ADD_TO_CART:
				Main.getGUI().toggleButton();
				Main.killThreads();
			case CHECKOUT:
				break;
			}

		}
		if (Thread.currentThread().isInterrupted()) print("Thread Aborted");

	}


	public void run() { //background task of each order
		loop();
	}


	public void setStatus(int itemNumber, String text) { //sets status of item in table
		order.getModel().setValueAt(text, itemNumber - 1, 5);
	}

	public void throwRunnableErrorPane(String message, String title) {
		RunnableErrorPane pane = new RunnableErrorPane(message, "Order " + order.getOrderNum() + ": " + title);
		Thread thread = new Thread(pane);
		Main.pushToWorkerArray(thread);
		thread.start();
	}



	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	//you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!

}
