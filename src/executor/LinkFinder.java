package executor;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import backend.Item;

public class LinkFinder  {

	private ArrayList<Item> items;
	private int orderNumber;
	private int refreshRate;
	
	private TaskProcessor processor;
	
	private String mostRecentHTML;

	public LinkFinder(ArrayList<Item> items, int orderNumber, int refreshRate, TaskProcessor processor) {
		this.items = new ArrayList<Item>(); //make a copy of the items ArrayList, because this one will be cleared out as links are found (and you don't want to clear the real objects, they're needed later in the checkout process
		this.items = items;
		this.orderNumber = orderNumber;
		this.refreshRate = refreshRate;
		this.mostRecentHTML = "";
		this.processor = processor;
		
		for (Item i : items) { //start all of the workers for each item
			ItemLinkCamper camper = new ItemLinkCamper(i, this, orderNumber, processor);
			camper.execute();
			
		}
	}


	public String getMostRecentHTML() { //gives ItemLinkCampers most recent HTML from /shop/all
		return mostRecentHTML;
	}


	public void findThem() {

		try {
			mostRecentHTML = Jsoup.connect("http://www.supremenewyork.com/shop/all").get().html();
			
			System.out.println("Size of items: " + items.size());
			
			if (items.isEmpty()) { //all items removed (meaning they were found)
				processor.print("All Item Links Found in Order " + orderNumber);
				TaskProcessor.stage = Stage.ADD_TO_CART;
				return;
			}
			
			Thread.sleep(refreshRate);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public void remove(Item i) { //removes item from item arraylist (called by campers when they find their links
		items.remove(i);
	}



}
