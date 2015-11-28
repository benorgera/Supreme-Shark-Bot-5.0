package executor;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.web.WebView;
import javax.swing.JTextArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import backend.Item;

public class LinkFinder extends TaskProcessor {
	
	private Stage stage;
	private ArrayList<Item> items;
	private int orderNumber;
	private String[][] keywords;
	private int refreshRate;
	
	public LinkFinder(Stage stage, ArrayList<Item> items, JTextArea txtConsole, WebView htmlConsole, int orderNumber, int refreshRate) {
		super(txtConsole, htmlConsole);
		this.stage = stage;
		items = new ArrayList<Item>(); //make a copy of the items ArrayList, because this one will be cleared out as links are found (and you don't want to clear the real objects, they're needed later in the checkout process
		this.items = items;
		this.orderNumber = orderNumber;
		this.refreshRate = refreshRate;
	}
	
	public void findLinks() {
		
		try {
			Document page = Jsoup.connect("http://www.supremenewyork.com/shop/all").get();
			
			for (Item i : items) { //for each item
				for (String keyword : i.getKeywords()) { //for each keyword
					if (page.html().toLowerCase().contains(keyword)) { //if the item might be there (a keyword was found, look closer and extract link)
						if (lookForLink(i)) items.remove(i); //removes item from list
					}
				}
					
			}
			
			returnIfReady(); //maybe we just found the last items link, check if sleep can be avoided (this method will return and the thread sleeo won't happen)
			
			Thread.sleep(refreshRate);
			
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private boolean lookForLink(Item i) { //finds item link and it found sets it in item settings then returns true, otherwise returns false
		return true;
	}
	
	private void returnIfReady() { //if all of the items have been found 
		if (items.isEmpty()) {
			stage = Stage.ADD_TO_CART;
			return;
		}
	}
	
	
	
}
