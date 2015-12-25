package executor;

import java.util.ArrayList;

import backend.Item;
import backend.Main;

public class LinkFinder  {

	private ArrayList<Item> items;
	private int refreshRate;
	private TaskProcessor processor;
	private HTTPConnector connector;

	private String mostRecentHTML;

	private ArrayList<ItemLinkCamper> campers; //stores campers to be used as monitors for synchronization

	public LinkFinder(ArrayList<Item> items, int refreshRate, TaskProcessor processor, HTTPConnector connector) {
		this.items = new ArrayList<Item>(); //make a copy of the items ArrayList, because this one will be cleared out as links are found (and you don't want to clear the real objects, they're needed later in the checkout process
		this.items = items;
		this.refreshRate = refreshRate;
		this.mostRecentHTML = "";
		this.processor = processor;
		this.connector = connector;

		campers = new ArrayList<ItemLinkCamper>();

		for (Item i : items) { //start all of the workers for each item

			ItemLinkCamper camper = new ItemLinkCamper(i, this, processor); //make object

			Thread thread = new Thread(camper); //make runnable
			Main.pushToWorkerArray(thread); //add runnable to 
			campers.add(camper); //stores monitors for synchronization
			thread.start(); //starts runnable
		}
	}


	public String getMostRecentHTML() { //gives ItemLinkCampers most recent HTML from /shop/all
		return mostRecentHTML;
	}


	public void findThem() { //gets new html from site
		try {
			mostRecentHTML = connector.getHTMLString("http://www.supremenewyork.com/shop/all");

			processor.printSys("HTTP connection made");

			notifyCampers(); //let camping threads know new html has been found

			processor.printSys("Items with links to be found: " + items.size());

			checkIfReady(); //check if all items found, if so move to next stage

	
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); //if thread interrupted (bot aborted), interrupt yourself
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
			TaskProcessor.stage = Stage.ADD_TO_CART; //next stage
			return;
		} else {
			synchronized (this) { //synchronized wait so it can be woken if more items found (to avoid a sleep during a period in which all links are found)
				wait(refreshRate);
			}
		}
	}




}
