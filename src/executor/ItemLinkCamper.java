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

import backend.Item;

public class ItemLinkCamper implements Runnable {

	private Item item;
	private LinkFinder linkFinder;
	private TaskProcessor processor;

	private ArrayList<String> previousConfirmation; //the last JOptionPane shown, if its the same as the one before we dont want to ask again

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

		for (String link : definites) for (String color: item.getColor()) if (link.contains(color)) {colorCorrect.add(link); break;} //if it has the right color add it to the colorCorrect arraylist

		System.out.println(definites.toString());
		
		if (colorCorrect.size() == 1) { //only one link

			item.setLink(formatLink(colorCorrect.get(0)));

		} else if (colorCorrect.size() == 0 && definites.size() > 1) { //no links in color correct but others 

			
			int result = confirm(definites);
			if (result == definites.size() || result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(definites.get(result)));

		} else if (colorCorrect.size() == 0 && definites.size() == 1) { //no links in color correct but one in definites

			item.setLink(formatLink(definites.get(0)));

		} else if (colorCorrect.size() > 1) {

			int result = confirm(colorCorrect);
			if (result == colorCorrect.size() || result <= -1) return false; //they chose none or closed the dialog
			item.setLink(formatLink(colorCorrect.get(result)));

		} else {
			//extract link like from the jordans
			return false;
		}

		return true;

	}

	private int confirm(ArrayList<String> links) { //confirms that the 


		if (links.equals(previousConfirmation)) return -1; //if we already asked about these links, dont ask again

		previousConfirmation = links; //this was the previous confirmation

		ArrayList<String> copy = new ArrayList<String>(links);

		copy.add("None of the above");

		for (int i = 0; i < copy.size();  i++)  copy.set(i, copy.get(i).replace(item.getCategory(), "").replaceFirst("/", "").replaceFirst("/", "").replaceFirst("/", "").replace("shop", ""));

		JComboBox<Object> optionList = new JComboBox<Object>(copy.toArray());
		
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(optionList, BorderLayout.SOUTH);
		panel.add(new JLabel("Which of these is the correct link for them item with keywords '" + Arrays.asList(item.getKeywords()).toString().replace("[", "").replace("]", "")  + "' in color '" + item.getColor() + "'?"), BorderLayout.NORTH);
		
		JOptionPane.showMessageDialog(null, panel, "Confirm Item " + item.getItemNumber() + " Link", JOptionPane.QUESTION_MESSAGE);

		return optionList.getSelectedIndex();
	}

	private synchronized void waitForUpdate() {//wait for notification of new html

		try {
			wait();
		} catch (InterruptedException e) { //bot aborted
			Thread.currentThread().interrupt();
		}

	}

	private String formatLink(String link) { //make sure the link is a complete URL
		if (!link.contains("supremenewyork.com")) link = "supremenewyork.com" + link;

		if (!link.contains("http://www.")) link = "http://www." + link;

		processor.print("Item " + item.getItemNumber() + " Link: " + link);

		return link;

	}
	
	
	//early link support, figure out "supreme schott shearling hooded coat" glitch



}
