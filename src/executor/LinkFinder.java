package executor;

import java.util.ArrayList;
import java.util.HashMap;
import backend.Item;
import backend.Main;

public class LinkFinder  {

	private ArrayList<Item> items;
	private int refreshRate;
	private TaskProcessor processor;
	private HTTPConnector connector;

	private HashMap<String, String> categoriesHTML;

	private ArrayList<ItemLinkCamper> campers; //stores campers to be used as monitors for synchronization

	public LinkFinder(ArrayList<Item> items, int refreshRate, TaskProcessor processor, HTTPConnector connector, int orderNumber) {
		this.items = new ArrayList<Item>(items); //make a copy of the items ArrayList, because this one will be cleared out as links are found (and you don't want to clear the real objects, they're needed later in the checkout process
		this.refreshRate = refreshRate;
		this.processor = processor;
		this.connector = connector;
		campers = new ArrayList<ItemLinkCamper>();
		
		categoriesHTML = new HashMap<String, String>();
		
		for (Item i : items) { //start all of the workers for each item

			if (null == categoriesHTML.get(i.getCategory())) categoriesHTML.put(i.getCategory(), "placeholder"); //figure out what category pages need to be monitored for their html

			ItemLinkCamper camper = new ItemLinkCamper(i, this, connector, processor, orderNumber); //make object for each item

			Thread thread = new Thread(camper); //make runnable
			Main.pushToWorkerArray(thread); //add runnable to 
			campers.add(camper); //stores monitors for synchronization
			thread.start(); //starts runnable
		}
	}


	public String getMostRecentHTML(String category) { //gives ItemLinkCampers most recent HTML from /shop/CATEGORY
		return categoriesHTML.get(category);
	}


	public void findThem() { //gets new html from site
		try {
			pullCategoriesHTML();

			processor.printSys("HTTP connection made");

			notifyCampers(); //let camping threads know new html has been found

			processor.printSys("Items with links to be found: " + items.size());

			checkIfReady(); //check if all items found, if so move to next stage


		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); //if thread interrupted (bot aborted), interrupt yourself
		} catch (Exception e) {
			processor.printSys("Link finding error, retrying");
		}


	}

	public void remove(Item i) { //removes item from item ArrayList (called by campers when they find their links)
		items.remove(i);
	}


	private void notifyCampers() { //notify all camping threads that new HTML has been loaded

		for (ItemLinkCamper i : campers) {
			synchronized (i) { //synchronize on monitor of each camper
				i.notify(); //notify camper
			}	
		}
	}

	public int getItemNumber() {
		return items.size();
	}

	private void checkIfReady() throws InterruptedException { //check if all items found, if so move to next stage, errors thrown caught by try catch in findThem()

		if (items.isEmpty()) { //all items removed (meaning they were found)
			processor.print("All item links found");
			processor.setStage(Stage.ADD_TO_CART); //next stage
		} else {
			synchronized (this) { //synchronized wait so it can be woken if more items found (to avoid a sleep during a period in which all links are found)
				wait(refreshRate);
			}
		}
	}


	private void pullCategoriesHTML() { //pulls most recent HTML from each necessary category
		 for (String s : categoriesHTML.keySet()) categoriesHTML.replace(s, connector.getHTMLString(getCategoryURL(s)));
	}
	
	private String getCategoryURL(String category) { //returns the URL of the given category's /shop page
		return "http://www.supremenewyork.com/shop/all/" + (category.equals("tops-sweaters") ? "tops_sweaters" : category);
	}

}
