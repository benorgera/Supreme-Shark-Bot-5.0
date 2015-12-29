package executor;

import javax.swing.JOptionPane;

public class RunnableErrorPane implements Runnable {

	private String message;
	private String title;
	
	public RunnableErrorPane(String message, String title) {
		this.message = message;
		this.title = title;
	}
	
	@Override
	public void run() {
		JOptionPane.showMessageDialog(null, message, title, 0);
	}

}
