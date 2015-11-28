package executor;

import javax.swing.SwingWorker;

import backend.Item;
import backend.main;

public class ItemLinkCamper extends SwingWorker<Object, Object> {

	private Item item;
	private LinkFinder linkFinder;
	private int orderNumber;
	private TaskProcessor processor;
	
	public ItemLinkCamper(Item item, LinkFinder linkFinder, int orderNumber, TaskProcessor processor) {
		this.item = item;
		this.linkFinder = linkFinder;
		this.orderNumber = orderNumber;
		this.processor = processor;
		main.pushToWorkerArray(this);
		processor.setStatus(item.getItemNumber(), "Finding Link");
		
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		while (!isCancelled()) {
			
			for (String keyword : item.getKeywords()) {
				if (linkFinder.getMostRecentHTML().toLowerCase().contains(keyword) && !keyword.equals("")) {
					if (pullLink()) {
						System.out.println("Item " + item.getItemNumber() + " of Order " + orderNumber + " Link Found");
						processor.setStatus(item.getItemNumber(), "Link Found");						
						linkFinder.remove(item);
						cancel(true);
					}
				}
			}
			
		}
		return new Object();
	}

	private boolean pullLink() {
		
		return true;
	}
	
	

}
