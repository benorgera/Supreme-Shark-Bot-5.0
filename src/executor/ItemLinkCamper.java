package executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import backend.Item;

public class ItemLinkCamper implements Runnable {

	private Item item;
	private LinkFinder linkFinder;
	private TaskProcessor processor;

	public ItemLinkCamper(Item item, LinkFinder linkFinder, TaskProcessor processor) {
		this.item = item;
		this.linkFinder = linkFinder;
		this.processor = processor;
		processor.setStatus(item.getItemNumber(), "Finding Link");
	}


	public void run() {

		int count = 1;
		while (!Thread.currentThread().isInterrupted()) {

			processor.println("Item "+ item.getItemNumber() + " checked HTML " + count + " times");

			count++;

			waitForUpdate();

			for (String keyword : item.getKeywords()) {
				if (linkFinder.getMostRecentHTML().toLowerCase().contains(keyword) && !keyword.isEmpty()) {
					if (pullLink(linkFinder.getMostRecentHTML().toLowerCase())) {
						processor.println("Item " + item.getItemNumber() + " link found");
						processor.setStatus(item.getItemNumber(), "Link Found");						
						linkFinder.remove(item);
						
						synchronized (linkFinder) { //notify link finder to check items remaining again (it might be sleeping and all links may have been found)
							linkFinder.notify();
						}
						
						Thread.currentThread().interrupt();
					}
				}
			}

		}
	}

	private boolean pullLink(String html) { //once its confirmed the item is up, pull the link and set it in item settings

		ArrayList<String> possibilities = new ArrayList<String>(Arrays.asList(html.split("\"")));

		Iterator<String> iterator = possibilities.iterator();

		while (iterator.hasNext()) {String next = iterator.next(); if (!next.contains("/shop/") || !next.contains(item.getCategory())) iterator.remove();} //remove all non-links

		
		//tops and sweaters has something strange going on
		
		
		iterator = possibilities.iterator();
		
		ArrayList<String> definites = new ArrayList<String>();
		
		while (iterator.hasNext()) {String next = iterator.next(); for (String keyword : item.getKeywords()) if (next.contains(keyword)) {definites.add(next); continue;} } //add links with keywords to definites

		System.out.println(possibilities);

		System.out.println(definites);
		
		return true;
	}

	private synchronized void waitForUpdate() {

		try {
			wait();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
//			e.printStackTrace();
		}

	}



}
