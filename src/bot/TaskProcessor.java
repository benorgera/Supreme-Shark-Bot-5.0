package bot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class TaskProcessor extends SwingWorker {
	
	private Order order;
	private JTextArea txtConsole;
	private JPanel htmlConsole;
	
	public final DateFormat dateFormat = new SimpleDateFormat("h:mm:ss a");
	
	public TaskProcessor(Order order, JTextArea txtConsole, JPanel htmlConsole) {
		this.order = order;
		this.txtConsole = txtConsole;
		this.htmlConsole = htmlConsole;
		
		print("Order " + order.getOrderNum() + " thread initialized");
		main.pushToWorkerArray(this); //gives worker so it can be cancelled later
	}
	
	private void print(String s) { //print to text console and to software console
		txtConsole.setText(txtConsole.getText() + (txtConsole.getText().isEmpty() ? "" : "\n") + s + " (" + dateFormat.format(new Date()).toString() + ")");
		System.out.println(s + " (" + dateFormat.format(new Date()).toString() + ")");
	}

	private void findLinks() { //dummy class for now, actual bot process should start here
		while (!isCancelled()) { //you must check if cancelled in every loop!!!
			
			print("Order " + order.getOrderNum() + " thread sleeping");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	@Override
	protected Object doInBackground() throws Exception { //background task of each order
		findLinks();
		return true;
	}
	
	
	 //you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	 //you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	 //you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	 //you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	 //you must check if cancelled in every loop, otherwise abort wont work (see line 33)!!!
	
	

}
