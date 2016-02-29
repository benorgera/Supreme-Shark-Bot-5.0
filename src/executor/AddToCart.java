package executor;

import java.awt.BorderLayout;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import backend.Item;
import backend.Order;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AddToCart {

	private Order order;
	private TaskProcessor processor;
	private HTTPConnector connector;
	private int itemsAdded = 0;
	private int attempts = 0; //number of times same item has been tried to atc, if not working after set threshold skip item or prompt

	public AddToCart(Order order, TaskProcessor processor, HTTPConnector connector) {
		this.order = order;
		this.processor = processor;
		this.connector = connector;
	}

	public void addThem() {
		Item currentItem = order.getItems().get(itemsAdded);

		try {
			prepareItem(currentItem); //prepare item, maybe previous prepare failed. you could theoretically just prepare once

			if (connector.atcPost(currentItem)) { //if successful, goto next item, reset attempts
				processor.setStatus(currentItem.getItemNumber(), "Added to Cart");
				itemsAdded++;
				attempts = 0;
			} else {
				attempts++;
			}

		} catch (Exception e) {
			processor.printSys("Add to cart error, retrying");
			attempts++;
		}


		if (attempts >= 50 && order.getItems().size() > 1) { //too many attempts and other items to worry about
			processor.setStatus(currentItem.getItemNumber(), "Add to Cart Failed");
			itemsAdded++;
			attempts = 0;
		}

		if (itemsAdded == order.getItems().size()) processor.setStage(Stage.CHECKOUT);
	}

	private void prepareItem(Item item) { //scrape item page for post parameters

		processor.setStatus(item.getItemNumber(), "Adding to Cart (" + attempts + " attempts)");

		Document doc = Jsoup.parse(connector.getHTMLString(item.getLink(), false));

		Elements form = doc.select("form input");

		Elements sizes = doc.select("form select");

		form.addAll(sizes); //form contains all inputs and all dropdowns

		String askingAbout = "size";

		String data = "";
		for (int i = 0; i < form.size(); i ++) { //dynamically scrape atc params

			Element current = form.get(i);

			Elements sizeOptions = current.select("option");

			if (i != 0) data += "&"; //if not first parameter, add & to separate it from previous parameter

			if (!sizeOptions.isEmpty()) { //if it's not empty, there is a size dropdown
				data += pullSize(sizeOptions, item, askingAbout);
				askingAbout = "option"; //we already propted them about size, lets say we're asking about "option" next time (which could mean quantity or some other option)
			} else { //there is either no size dropdown or this is auth_token, utf8, or commit
				String name = form.get(i).attr("name");
				String value = form.get(i).attr("value");
				data += format(name, value);
				processor.printSys("Item " + item.getItemNumber() + ": Added attribute " + name + " with value " + value + " to add to cart params");
			}
		}

		processor.printSys("Item " + item.getItemNumber() + ": ATC Data: " + data);
		item.setAtcParameters(data);


		String atcLink = "http://www.supremenewyork.com" + doc.select("form").attr("action");
		processor.printSys("Item " + item.getItemNumber() + ": ATC Post Link: " + atcLink);
		item.setAtcLink(atcLink); //dynamically scrape post link

		String authToken = doc.select("head meta[name=csrf-token]").first().attr("content");
		processor.printSys("Item " + item.getItemNumber() + ": Authenticity Token: " + authToken);
		item.setAuthenticityToken(authToken);

	}

	private String pullSize(Elements sizes, Item item, String askingAbout) { //get proper size or option (maybe quantity, maybe other option) (stored in "askAbout"), and return its atc params


		if (sizes.size() == 1) { //only one size, we don't need to think
			processor.printSys("Item " + item.getItemNumber() + ": Size Number: " + sizes.get(0).attr("value") + " Because Only One Size Available");
			return format(sizes.get(0).parent().attr("name"), sizes.get(0).attr("value"));
		}

		String[] sizeTexts = new String[sizes.size()];

		for (int i = 0 ; i < sizes.size(); i ++) {
			Element element = sizes.get(i);

			sizeTexts[i] = element.text(); //if we need to prompt them later we already built the array

			if (element.text().toLowerCase().contains(item.getSize().toLowerCase()) && !item.getSize().toLowerCase().isEmpty()) { //compare items text to entered text in GUI
				processor.printSys("Item " + item.getItemNumber() + ": Size Number: " + element.attr("value") + " Because It Matched the Entered Size");
				return format(element.parent().attr("name"), element.attr("value"));
			}
		}


		JComboBox<Object> optionList = new JComboBox<Object>(sizeTexts);

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(optionList, BorderLayout.SOUTH);
		panel.add(new JLabel("Which of these is the correct " + askingAbout + " for them item with keywords '" + Arrays.asList(item.getKeywords()).toString().replace("[", "").replace("]", "")  + "'" + (!item.getEarlyLink().isEmpty() ? " and early link '" + item.getEarlyLink() + "'": "") + " in color '" + Arrays.asList(item.getColors()).toString().replace("[", "").replace("]", "") + "'?"), BorderLayout.NORTH);

		JOptionPane.showOptionDialog(null, panel, "Confirm Order " + order.getOrderNum() + " Item " + item.getItemNumber() + " Size", 0, 3, null, new String[]{"Ok"}, 0);

		String name = sizes.get(optionList.getSelectedIndex()).parent().attr("name");

		String value = sizes.get(optionList.getSelectedIndex()).attr("value");

		String text = sizes.get(optionList.getSelectedIndex()).text();

		processor.printSys("Item " + item.getItemNumber() + ": User chose " + askingAbout + " " + text + " which had number " + value);

		return format(name, value);
	}

	private String format(String name, String value) { //formats atc params (name and value) into proper url encoded for for the post request
		String returnValue = null;

		try {
			returnValue = (name + "=" + URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); //this should never happen
		}

		return returnValue;
	}

}
