package executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

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

		int count = 0;
		while (!Thread.currentThread().isInterrupted()) {

			waitForUpdate();

			boolean shouldCheck = false;

			for (String keyword : item.getKeywords()) { //iterate through keywords, if any are in the html, check it
				if (linkFinder.getMostRecentHTML().toLowerCase().contains(keyword) && !keyword.isEmpty()) {
					shouldCheck = true;
					break;
				}
			}

			if (shouldCheck) { //if a keyword was found, try and check the HTML again
				checkHTML();
				count++;
				processor.printSys("Item "+ item.getItemNumber() + " checked HTML " + count + " times");
			}


		}
	}

	private void checkHTML() {

		if (pullLink(linkFinder.getMostRecentHTML().toLowerCase())) { //if the link is successfully found

			processor.printSys("Item " + item.getItemNumber() + " link found");
			processor.setStatus(item.getItemNumber(), "Link Found");						
			linkFinder.remove(item);

			synchronized (linkFinder) { //notify link finder to check items remaining again (it might be sleeping and all links may have been found)
				linkFinder.notify();
			}

			Thread.currentThread().interrupt(); //kill this thread because its link was found, meaning the job is done
		}
	}


	private boolean pullLink(String html) { //once its confirmed the item is up, pull the link and set it in item settings

		ArrayList<String> possibilities = new ArrayList<String>(new HashSet<String>(Arrays.asList(html.split("\"")))); //create array list of href tags (everything in quotes), and remove duplicates

		Iterator<String> iterator = possibilities.iterator(); //must use iterator when iterating and then removing 

		while (iterator.hasNext()) {String next = iterator.next(); if (!next.contains("/shop/") || !next.contains(item.getCategory())) iterator.remove();} //remove all non-links

		//tops and sweaters has something strange going on

		ArrayList<String> definites = new ArrayList<String>();

		for (String next : possibilities) { //add links with keywords to definites
			for (String keyword : item.getKeywords()) if (next.contains(keyword) && next.contains(item.getCategory())) {
				definites.add(next); 
				break; //goto next iteration, no need to check this link again it was already added
			}
		} 


		int max = 0;

		for (String link : definites) {
			int keywordNums = 0;
			for (String keyword : item.getKeywords()) if (link.contains(keyword)) keywordNums++; //count keywords in link
			if (keywordNums > max) max = keywordNums;
		}

		iterator = definites.iterator();

		while (iterator.hasNext()) { //iterate through definites, removing links which have less than max keywords
			String next = iterator.next();
			int keywordNums = 0;
			for (String keyword : item.getKeywords()) if (next.contains(keyword)) keywordNums++; //count keywords in link
			if (keywordNums < max) iterator.remove(); //if less than max keywords found, remove link
		}

		ArrayList<String> colorCorrect = new ArrayList<String>();

		for (String link : definites) if (link.contains(item.getColor())) colorCorrect.add(link);

		if (colorCorrect.size() == 1) {
			item.setLink(formatLink(colorCorrect.get(0)));
			processor.print("Item " + item.getItemNumber() + " Link: " + formatLink(colorCorrect.get(0)));
			return true;
		} else if (colorCorrect.size() == 0) {
			int result = confirm(definites);
			if (result == 0) return false;
			String link;
			try {
				link = definites.get(result);
			} catch (Exception e) {
				link = definites.get(0);
			}
			item.setLink(formatLink(link));
		} else if (colorCorrect.size() > 1) {
			int result = confirm(colorCorrect);
			if (result == 0) return false;
			String link;
			try {
				link = colorCorrect.get(result);
			} catch (Exception e) {
				link = colorCorrect.get(0);
			}
			item.setLink(formatLink(link));
		}
		
		return false; //the above if else must be checked

	}

	private int confirm(ArrayList<String> links) {
		String first = links.get(0);
		links.add(first);
		links.set(0, "None");
		return JOptionPane.showOptionDialog(null, "Which of these is the correct link for " + Arrays.asList(item.getKeywords()).toString() + " in color " + item.getColor() + "?", "Confirm Item " + item.getItemNumber(), 0, 3, null, links.toArray(), 0);
	}

	private synchronized void waitForUpdate() {//wait for notification of new html

		try {
			wait();
		} catch (InterruptedException e) { //bot aborted
			Thread.currentThread().interrupt();
		}

	}

	private String formatLink(String link) {
		if (!link.contains("supremenewyork.com")) link = "supremenewyork.com" + link;

		if (!link.contains("http://www.")) link = "http://www." + link;

		return link;

	}



}
