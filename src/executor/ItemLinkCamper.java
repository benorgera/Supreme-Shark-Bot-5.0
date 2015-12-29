package executor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;

import backend.Item;

public class ItemLinkCamper implements Runnable {

	private Item item;
	private LinkFinder linkFinder;
	private TaskProcessor processor;
	private HTTPConnector connector;

	private ArrayList<String> previousConfirmation; //the last JOptionPane shown, if its the same as the one before we dont want to ask again
	private int previousConfirmationNum = 0; //number of times the same confirmation has been shown

	public ItemLinkCamper(Item item, LinkFinder linkFinder, HTTPConnector connector, TaskProcessor processor) {
		this.item = item;
		this.linkFinder = linkFinder;
		this.processor = processor;
		this.connector = connector;
		processor.setStatus(item.getItemNumber(), "Finding Link");
		if (!item.getEarlyLink().isEmpty()) formatEarlyLink();
	}


	public void run() {

		int count = 0;
		while (!Thread.currentThread().isInterrupted()) {

			waitForUpdate();

			if (!item.getEarlyLink().isEmpty()) if(checkEarlyLink()) terminate();

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

		if (pullLink(linkFinder.getMostRecentHTML().toLowerCase())) terminate(); //if the link is successfully found, terminate this thread
	}


	private boolean pullLink(String html) { //once its confirmed the item is up, pull the link and set it in item settings

		ArrayList<String> possibilities = getValidLinks(html);

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

		Iterator<String> iterator = definites.iterator();

		while (iterator.hasNext()) { //iterate through definites, removing links which have less than max keywords
			String next = iterator.next();
			int keywordNums = 0;
			for (String keyword : item.getKeywords()) if (next.contains(keyword)) keywordNums++; //count keywords in link
			if (keywordNums < max) iterator.remove(); //if less than max keywords found, remove link
		}

		ArrayList<String> colorCorrect = getColorCorrect(definites);

		return processArrayLists(definites, colorCorrect);


	}

	private int confirm(ArrayList<String> links) { //confirms that the 


		if (links.equals(previousConfirmation)) { //count how many times we asked about these links
			previousConfirmationNum++;
		} else { //if its not the previous link, we've asked about it 0 times
			previousConfirmationNum = 0; 
		}

		if (previousConfirmationNum >= 2) return -1; //if we already asked about these links twice, dont ask again


		previousConfirmation = links; //this was the previous confirmation

		ArrayList<String> copy = new ArrayList<String>(links);

		if (!item.getCategory().isEmpty()) {
			for (int i = 0; i < copy.size();  i++)  copy.set(i, copy.get(i).replace(item.getCategory(), "").replaceFirst("/", "").replaceFirst("/", "").replaceFirst("/", "").replace("shop", ""));
		} else {
			for (int i = 0; i < copy.size();  i++)  copy.set(i, copy.get(i).replaceFirst("/", "").replaceFirst("/", "").replace("shop", ""));
		}


		JComboBox<Object> optionList = new JComboBox<Object>(copy.toArray());

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(optionList, BorderLayout.SOUTH);
		panel.add(new JLabel("Which of these is the correct link for them item with keywords '" + Arrays.asList(item.getKeywords()).toString().replace("[", "").replace("]", "")  + "'" + (!item.getEarlyLink().isEmpty() ? " and early link '" + item.getEarlyLink() + "'": "") + " in color '" + Arrays.asList(item.getColor()).toString().replace("[", "").replace("]", "") + "'?"), BorderLayout.NORTH);

		if (JOptionPane.showOptionDialog(null, panel, "Confirm Item " + item.getItemNumber() + " Link", 0, 3, null, new String[]{"None of these", "Ok"}, 0) == 1) return optionList.getSelectedIndex(); //they chose ok, return which link was chosen

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

		processor.printSys("Early Link Full URL: " + link);

		String temp = link;
		int i = 0;
		while (temp.contains("/")) {temp = temp.replaceFirst("/", ""); i++;} //count slashed

		processor.printSys("Number of '/': " + i);

		String[] parts = new String[2];

		if (i == 6) { //early link might contain colors

			parts[0] = link.substring(0, link.lastIndexOf("/"));
			parts[1] = link.substring(link.lastIndexOf("/") + 1, link.length());

			processor.printSys("Two parts of link (item part and color part): " + Arrays.asList(parts).toString());

			if (!parts[1].isEmpty()) { //if the color part of the link isn't empty
				String[] existingColors = item.getColor();
				String[] newColors = parts[1].split("-");
				System.out.println(Arrays.asList(newColors).toString());
				String[] allColors = removeBlanks((String []) ArrayUtils.addAll(existingColors, newColors));
				processor.printSys("New Colors Based On Early Link: " + Arrays.asList(allColors).toString());
				item.setColor(allColors);
				processor.printSys("New Early Link: " + parts[0]);
				item.setEarlyLink(parts[0]);
			} else {
				processor.printSys("Early Link Didn't Contain Colors");
			}

		} else if (i == 5){ //early link contained no colors
			processor.printSys("Early Link Didn't Contain Colors");
		} else {
			processor.print("Item " + item.getItemNumber() + ": Early Link Format Invalid");
			processor.throwRunnableErrorPane("Early Link: '" + link + "' is invalid and could not be processed. It has been removed to prevent erratic and unpredictable results.", "Item " + item.getItemNumber() + ": Early Link Format Invalid");
			item.setEarlyLink("");
		}

	}

	private boolean checkEarlyLink() { 

		Object[] res = connector.chechEarlyLink(item.getEarlyLink());
		if (!(boolean) res[0]) return false; //if it failed return

		ArrayList<String> definites = new ArrayList<String>(getValidLinks((String) res[1]));

		Iterator<String> iterator = definites.iterator();

		while (iterator.hasNext()) if (!formatLink(iterator.next(), false).contains(item.getEarlyLink())) iterator.remove(); //remove all the non early link links

		ArrayList<String> colorCorrect = new ArrayList<>(getColorCorrect(definites));

		return processArrayLists(definites, colorCorrect);

	}


	private ArrayList<String> getValidLinks(String html) { //get all links, with no duplicates or get requests

		ArrayList<String> possibilities = new ArrayList<String>(Arrays.asList(html.split("\""))); //create array list of href tags (everything in quotes), 

		for (int i = 0; i < possibilities.size(); i ++ ) if (possibilities.get(i).contains("?")) possibilities.set(i, possibilities.get(i).split("\\?")[0]); //remove get requests

		possibilities = new ArrayList<String>(new HashSet<String>(possibilities)); // remove duplicates

		Iterator<String> iterator = possibilities.iterator(); //must use iterator when iterating and then removing 

		while (iterator.hasNext()) {String next = iterator.next(); if (!next.contains("/shop/") || !next.contains(item.getCategory()) || next.contains("//")) iterator.remove();} //remove all non-links

		return possibilities;

	}

	private ArrayList<String> getColorCorrect(ArrayList<String> definites) { //returns arraylist with correct colors

		ArrayList<String> colorCorrect = new ArrayList<String>();

		for (String link : definites) for (String color: item.getColor()) if (link.contains(color)) {colorCorrect.add(link); break;} //if it has the right color add it to the colorCorrect arraylist

		return colorCorrect;

	}

	private boolean processArrayLists(ArrayList<String> definites, ArrayList<String> colorCorrect) { //prompts user if necessary, figures out proper item

		if (colorCorrect.size() == 1) { //only one link

			item.setLink(formatLink(colorCorrect.get(0), true));

		} else if (colorCorrect.size() == 0 && definites.size() > 1) { //no links in color correct but others 


			int result = confirm(definites);
			if (result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(definites.get(result), true));

		} else if (colorCorrect.size() == 0 && definites.size() == 1) { //no links in color correct but one in definites

			item.setLink(formatLink(definites.get(0), true));

		} else if (colorCorrect.size() > 1) {

			int result = confirm(colorCorrect);
			if (result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(colorCorrect.get(result), true));

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
		
		for(String s : firstArray) if(s != null && s.length() > 0) list.add(s);
		
		return list.toArray(new String[list.size()]);

	}

}
