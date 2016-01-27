package executor;

import backend.Order;

public class Checkout {
	
	private Order order;
	private TaskProcessor processor;
	private HTTPConnector connector;
	
	public Checkout(Order order, TaskProcessor processor, HTTPConnector connector) {
		this.order = order;
		this.processor = processor;
		this.connector = connector;
	}
	
	
}