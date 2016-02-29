package executor;

import backend.Order;

public class Checkout {
	
	private Order order;
	private TaskProcessor processor;
	private HTTPConnector connector;
	
	int attempts = 0;
	
	public Checkout(Order order, TaskProcessor processor, HTTPConnector connector) {
		this.order = order;
		this.processor = processor;
		this.connector = connector;
	}
	
	public void attemptCheckout() {
		attempts++;
		
		processor.setAllStatuses("Checkout (" + attempts + " attempts)");
	
		prepCheckout();
		
		if (connector.checkoutPost(order.getOrderSettings())) processResponse(); //if we successfully posted the checkout form, process the server response to see
		
		
	}
	
	private void prepCheckout() { //process form data by loading checkout page and seeing what it wants, set ordersettings post parameters accordingly
		
		String checkoutHTML = connector.getHTMLString("http://www.supremenewyork.com/checkout/");
			
	}
	
	private void processResponse() { //makes sure checkout was successful, if not prompt user or try again, depending on if supreme sent any errors
		
	}
	
	
	
	
}