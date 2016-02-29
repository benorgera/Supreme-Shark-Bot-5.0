package executor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import backend.Order;

public class Checkout {
	
	private Order order;
	private TaskProcessor processor;
	private HTTPConnector connector;
	
	int attempts = 0;
	
	public Checkout(Order order, TaskProcessor processor, HTTPConnector connector) {
		this.order = order;
		this.processor = processor;
		this.connector = connector;
	}
	
	public void attemptCheckout() {
		attempts++;
		
		processor.setAllStatuses("Checkout (" + attempts + " attempts)");
	
		prepCheckout();
		
//		if (connector.checkoutPost(order.getOrderSettings())) processResponse(); //if we successfully posted the checkout form, process the server response to see
		
		
	}
	
	private void prepCheckout() { //process form data by loading checkout page and seeing what it wants, set ordersettings post parameters accordingly
		
		String checkoutPage = connector.getHTMLString("https://www.supremenewyork.com/checkout", true);
		
		Document doc = Jsoup.parse(checkoutPage);
		
		System.out.println("Checkout page: " + checkoutPage);

		Elements form = doc.select("form input");

		Elements sizes = doc.select("form select");

		form.addAll(sizes); //form contains all inputs and all dropdown
		
		String data = "";
		for (int i = 0; i < form.size(); i ++) { //dynamically scrape atc params

			Element current = form.get(i);

			Elements options = current.select("option");

			if (i != 0) data += "&"; //if not first parameter, add & to separate it from previous parameter

			if (!options.isEmpty()) { //if it's not empty, there is a size dropdown
				data += pullOptionValue(current);
			} else { //there is either no size dropdown or this is auth_token, utf8, or commit
				String name = form.get(i).attr("name");
				String value = form.get(i).attr("value");
				data += format(name, value);
				processor.printSys("Added attribute " + name + " with value " + value + " to checkout params");
			}

		}

		processor.printSys("Checkout Data: " + data);
		order.getOrderSettings().setCheckoutParameters(data);


		String checkoutLink = "http://www.supremenewyork.com" + doc.select("form").attr("action");
		processor.printSys("Checkout Post Link: " + checkoutLink);
		order.getOrderSettings().setCheckoutLink(checkoutLink); //dynamically scrape post link
	}
	
	private String format(String name, String value) { //formats checkout params (name and value) into proper url encoded for for the post request
		String returnValue = null;

		try {
			returnValue = (name + "=" + URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); //this should never happen
		}

		return returnValue;
	}
	
	private String pullOptionValue(Element optionHolder) {
		
		Elements options = optionHolder.select("option");
		
		System.out.println("New option " + optionHolder.attr("name") + " with options:");
		
		for (Element e : options) System.out.println("\t" + e.text());
		
		return "";
	}
	
	private void processResponse() { //makes sure checkout was successful, if not prompt user or try again, depending on if supreme sent any errors
		
	}
	
	
	
	
}