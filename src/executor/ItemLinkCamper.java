package executor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import backend.Item;

public class ItemLinkCamper implements Runnable {

	private Item item;
	private LinkFinder linkFinder;
	private TaskProcessor processor;
	private HTTPConnector connector;

	
	
	
	
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * EARLY LINKS NOT WORKING AT ALL
	 * BOT MUST SCAN COOKIES TO MAKE SURE ITEMS ARE ACUTALLY ADDED TO CART, BECAUSE AN ERROR IS NO LONGER THROWN WHEN SOMETHING GOES WRONG
	 */
	
	
	
	private int orderNumber;

	private ArrayList<String> previousConfirmation; //the last JOptionPane shown, if its the same as the one before we dont want to ask again
	private int previousConfirmationNum = 0; //number of times the same confirmation has been shown

	public ItemLinkCamper(Item item, LinkFinder linkFinder, HTTPConnector connector, TaskProcessor processor, int orderNumber) {
		this.item = item;
		this.linkFinder = linkFinder;
		this.processor = processor;
		this.connector = connector;
		this.orderNumber = orderNumber;
		processor.setStatus(item.getItemNumber(), "Finding Link");
		if (!item.getEarlyLink().isEmpty()) formatEarlyLink();
	}


	public void run() {

		int count = 0;
		while (!Thread.currentThread().isInterrupted()) {
			count++;

			processor.setStatus(item.getItemNumber(), "Link Finding (" + count + " checks)");

			waitForUpdate();

			if (!item.getEarlyLink().isEmpty()) if (checkEarlyLink()) terminate();

			boolean shouldCheck = false;

			for (String keyword : item.getKeywords()) { //iterate through keywords, if any are in the html, check it
				if (linkFinder.getMostRecentHTML(item.getCategory()).toLowerCase().contains(keyword) && !keyword.isEmpty()) {
					shouldCheck = true;
					break;
				}
			}

			if (shouldCheck) { //if a keyword was found, try and check the HTML again
				checkHTML();
				processor.printSys("Item "+ item.getItemNumber() + " checked HTML " + count + " times");
			}


		}
	}

	private void checkHTML() {

		if (pullLink(linkFinder.getMostRecentHTML(item.getCategory()).toLowerCase())) terminate(); //if the link is successfully found, terminate this thread
	}


	private boolean pullLink(String html) { //once its confirmed the item is up, pull the link and set it in item settings

		PotentialItems possibilities = getValidLinks(html);

		System.out.println("Possibilities: " + possibilities.toString());

		PotentialItems definites = new PotentialItems();

		for (int i = 0; i < possibilities.size(); i++) { //add links with keywords to definites
			for (String keyword : item.getKeywords()) if (possibilities.getLinkText(i).contains(keyword)) {
				definites.add(possibilities.get(i));
				break; //goto next iteration, no need to check this link again it was already added
			}
		} 

		int max = 0;

		for (int i = 0; i < definites.size(); i++) {
			int keywordsNum = 0;
			for (String keyword : item.getKeywords()) if (definites.getLinkText(i).contains(keyword)) keywordsNum++;
			if (keywordsNum > max) max = keywordsNum;
		}


		for (int i = 0; i < definites.size(); i++) { //iterate through definites, removing links which have less than max keywords
			int keywordNums = 0;
			for (String keyword : item.getKeywords()) if (definites.getLinkText(i).contains(keyword)) keywordNums++; //count keywords in link
			if (keywordNums < max) definites.remove(i); //if less than max keywords found, remove item
		}

		return processItems(definites, getColorCorrect(definites));
	}

	private int confirm(PotentialItems items) { //confirms that the 

		ArrayList<String> prompt = new ArrayList<String>();

		for (int i = 0; i < items.size(); i++) prompt.add(items.getLinkText(i));
		
		if (prompt.equals(previousConfirmation)) {previousConfirmationNum++;} else {previousConfirmationNum = 0;}
		
		if (previousConfirmationNum >= 2) return -1; //if we already asked about these links twice, dont ask again
		
		previousConfirmation = prompt; //this was the previous confirmation

		JComboBox<Object> optionList = new JComboBox<Object>(prompt.toArray());

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(optionList, BorderLayout.SOUTH);
		panel.add(new JLabel("Which of these is the correct link for them item with keywords '" + Arrays.asList(item.getKeywords()).toString().replace("[", "").replace("]", "")  + "'" + (!item.getEarlyLink().isEmpty() ? " and early link '" + item.getEarlyLink() + "'": "") + " in color '" + Arrays.asList(item.getColors()).toString().replace("[", "").replace("]", "") + "'?"), BorderLayout.NORTH);

		if (JOptionPane.showOptionDialog(null, panel, "Confirm Order " + orderNumber + " Item " + item.getItemNumber() + " Link", 0, 3, null, new String[]{"None of these", "Ok"}, 0) == 1) return optionList.getSelectedIndex(); //they chose ok, return which link was chosen

		//maybe make the confirmation a runnable so this item can keep checking,  and then have it notify this thread once its answered
		//and have it so if two confirmations are up at one time one is interrupted (because you dont want two prompts open)

		return -1; //they chose none of the above

	}

	private synchronized void waitForUpdate() {//wait for notification of new html

		try {
			wait();
		} catch (InterruptedException e) { //bot aborted
			Thread.currentThread().interrupt();
		}

	}

	private String formatLink(String link, boolean print) { //make sure the link is a complete URL
		if (!link.contains("supremenewyork.com")) link = "supremenewyork.com" + link;

		if (!link.contains("www.")) link = "www." + link;

		if (!link.contains("http://")) link = "http://" + link;

		if (print) processor.print("Item " + item.getItemNumber() + " Link: " + link);

		return link;

	}

	private void formatEarlyLink() {
		item.setEarlyLink(formatLink(item.getEarlyLink(), false)); //make sure link is complete URL

		String link = item.getEarlyLink();

		processor.printSys("Item " + item.getItemNumber() + " Early Link Full URL: " + link);

		int slashes = getNumberOfSlashes(link);

		if (slashes == 6) { //early link might contain colors

			String linkPart = link.substring(0, link.lastIndexOf("/")); 
			String colorPart = link.substring(link.lastIndexOf("/") + 1, link.length()); 

			processor.printSys("Item " + item.getItemNumber() + " Two parts of link (item part and color part): " + linkPart + colorPart);

			if (!colorPart.isEmpty()) setColorsAndEarlyLinkFromEarlyLink(colorPart, linkPart);  //if the color part of the link isn't empty

		} else if (slashes == 5) { //early link contained no colors
			processor.printSys("Early Link Didn't Contain Colors");
		} else {
			processor.print("Item " + item.getItemNumber() + ": Early Link Format Invalid");
			processor.throwRunnableErrorPane("Early Link: '" + link + "' is invalid and could not be processed. It will not be used to prevent erratic and unpredictable results.", "Item " + item.getItemNumber() + ": Early Link Format Invalid");
			item.setEarlyLink("");
		}

	}

	private boolean checkEarlyLink() { 

		Object[] res = connector.chechEarlyLink(item.getEarlyLink());

		if (!(boolean) res[0]) return false; //if it failed return

		PotentialItems definites = getValidLinks((String) res[1]); //res[1] is the html

		for (int i = 0; i < definites.size(); i++) {
			if (!formatLink(definites.getURL(i), false).contains(item.getEarlyLink())) {
				definites.remove(i);
			}
		}

		return processItems(definites, getColorCorrect(definites));

	}


	private PotentialItems getValidLinks(String html) { //get all links, with no duplicates or get requests

		Document page = Jsoup.parse(html);

		Elements elements = page.getElementsByTag("a");

		PotentialItems items = new PotentialItems();

		for (Element e : elements) //add all links to the possibilities
			if (
					e.hasAttr("href") && //its a link
					e.attr("href").contains("/shop/") && //it contains shop
					e.attr("href").contains(item.getCategory()) && //it contains the category
					!e.attr("href").contains("//") //it isn't a full url (leading to another site)
					) {

				String urlWithoutGetRequests = e.attr("href").split("\\?")[0]; //remove get requests

				String newLinkText = e.text().toLowerCase(); //convert to lower case
				
				if (!e.getElementsByTag("img").isEmpty()) { //this is the image, disregard it because it has no link text
				
				} else if (items.containsURL(urlWithoutGetRequests)) { //this url has already been registered

					int indexOfThisURL = items.indexOfURL(urlWithoutGetRequests);

					String existingLinkTextOfThisURL = items.getLinkText(indexOfThisURL);

					if (!existingLinkTextOfThisURL.contains(e.text().toLowerCase())) items.setLinkText(indexOfThisURL, existingLinkTextOfThisURL + " " + newLinkText); //if the link text has changed for this new instance of the url, add it to the existing link text	

				} else {
					items.add(urlWithoutGetRequests, newLinkText); //this link is new, add it
				}
			}

		return items;
	}

	private PotentialItems getColorCorrect(PotentialItems definites) { //returns arraylist with correct colors

		PotentialItems colorCorrect =  new PotentialItems();

		for (int i = 0; i < definites.size(); i++) {
			for (String color : item.getColors()) {
				if (definites.getURL(i).contains(color)) {
					colorCorrect.add(definites.get(i));
					break;
				}
			}
		}

		return colorCorrect;

	}

	private boolean processItems(PotentialItems definites, PotentialItems colorCorrect) { //prompts user if necessary, figures out proper item

		if (colorCorrect.size() == 1) { //only one link

			item.setLink(formatLink(colorCorrect.getURL(0), true));

		} else if (colorCorrect.size() == 0 && definites.size() > 1) { //no links in color correct but others 

			int result = confirm(definites);
			if (result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(definites.getURL(0), true));

		} else if (colorCorrect.size() == 0 && definites.size() == 1) { //no links in color correct but one in definites

			item.setLink(formatLink(definites.getURL(0), true));

		} else if (colorCorrect.size() > 1) {

			int result = confirm(colorCorrect);
			if (result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(colorCorrect.getURL(result), true));

		} else {
			//extract link like from the jordans
			return false;
		}

		return true;

	}

	private void terminate() { //warn them the link was found, then close this thread
		processor.printSys("Item " + item.getItemNumber() + " link found");
		processor.setStatus(item.getItemNumber(), "Link Found");						
		linkFinder.remove(item);

		synchronized (linkFinder) { //notify link finder to check items remaining again (it might be sleeping and all links may have been found)
			linkFinder.notify();
		}

		Thread.currentThread().interrupt(); //kill this thread because its link was found, meaning the job is done
	}

	private String[] removeBlanks(String[] firstArray) { //removes blank elements from array

		ArrayList<String> list = new ArrayList<String>();

		for (String s : firstArray) if (!s.isEmpty()) list.add(s);

		return list.toArray(new String[list.size()]);

	}

	private int getNumberOfSlashes(String link) {

		int i = 0;
		while (link.contains("/")) {link = link.replaceFirst("/", ""); i++;} //count slashes

		processor.printSys("Item " + item.getItemNumber() + " Number of '/': " + i);

		return i;

	}

	private void setColorsAndEarlyLinkFromEarlyLink(String colorPart, String linkPart) {
		String[] allColors = removeBlanks((String []) ArrayUtils.addAll(item.getColors(), colorPart.split("-"))); //get colors at end of url (split by dashes)
		processor.printSys("New Colors Based On Early Link: " + Arrays.asList(allColors).toString());
		item.setColors(allColors);
		processor.printSys("New Early Link: " + linkPart);
		item.setEarlyLink(linkPart);
	}

}