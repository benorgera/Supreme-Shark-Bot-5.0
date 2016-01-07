package executor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

		
		if (attempts >= 100); //maybe skip item after set amount of tries?
		
		Item currentItem = order.getItems().get(itemsAdded);
		if (!currentItem.isPrepared()) prepareItem(currentItem); //prepare item if not prepared

		String html = connector.atcPost(currentItem);

		if (connectionSuccessful(html)) { //goto next item, reset attempts
			itemsAdded++;
			attempts = 0;
		} else {
			attempts++;
		}
		
		if (itemsAdded == order.getItems().size()) processor.stage = Stage.CHECKOUT;

	}

	private void prepareItem(Item item) {

		item.setDocumentHTML(connector.getHTMLString(item.getLink()));
		
		String html = item.getDocumentHTML();

		Document doc = Jsoup.parse(html);

		Element form = doc.select("form[class=add]").first();

		String atcLink = "http://www.supremenewyork.com" + form.attr("action");

		processor.printSys("Item " + item.getItemNumber() + " ATC Post Link: " + atcLink);

		item.setAtcLink(atcLink);
		
		Element link = doc.select("input[name=authenticity_token]").first();
		String authToken = link.attr("value");

		processor.printSys("Item " + item.getItemNumber() + " Authenticity Token: " + authToken);

		item.setAuthenticityToken(authToken);
		
		String data = null;
		
		try {
			data = "utf8=%E2%9C%93&authenticity_token=" + URLEncoder.encode(authToken, "UTF-8") + "&size=" + pullSize(doc, item) + "&commit=add+to+cart";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		processor.printSys("Item " + item.getItemNumber() + " ATC Data: " + data);
		
		item.setAtcParameters(data);
		
		item.setPrepared(true);
	}



	private String pullSize(Document doc, Item item) {

		Elements options = doc.select("select > option");
		
		if (options.size() == 1) {
			processor.printSys("Item " + item.getItemNumber() + " Size Number: " + options.get(0).attr("value"));
			return options.get(0).attr("value"); //only one size, set it and return
		}

		for (Element element : options) if (element.text().toLowerCase().contains(item.getSize().toLowerCase())) {
			processor.printSys("Item " + item.getItemNumber() + " Size Number: " + element.attr("value"));
			return element.attr("value");
		}
		
		return "Shit"; //prompt them, no size was selected 
		
	}
	


	private boolean connectionSuccessful(String html) {

		return true;

	}

}
