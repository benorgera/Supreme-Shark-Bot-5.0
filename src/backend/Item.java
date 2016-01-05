package backend;

import java.util.Arrays;

public class Item {
	private String[] keywords;
	private String[] color;
	private String category;
	private String earlyLink;
	private String size;
	private int itemNumber;
	
	private String atcLink;
	private String authenticityToken;
	private String atcParameters;
	private String link; //set by link finder once link is found
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	
	public String[] getColors() {
		return color;
	}
	
	public void setColors(String[] color) {
		this.color = color;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getEarlyLink() {
		return earlyLink;
	}
	
	public void setEarlyLink(String earlyLink) {
		this.earlyLink = earlyLink;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}
	
	public String toString() {
		return "Item Number " + itemNumber + ":\n\nKeywords:\n\t" + Arrays.asList(keywords).toString() + "\nCategorylor:\n\t" + category + "\nColor:\n\t" + Arrays.asList(color).toString() + "\nSize:\n\t" + size + "\nEarly Link:\n\t" + earlyLink;
	}

	public String getAtcLink() {
		return atcLink;
	}

	public void setAtcLink(String atcLink) {
		this.atcLink = atcLink;
	}

	public String getAtcParameters() {
		return atcParameters;
	}

	public void setAtcParameters(String atcParameters) {
		this.atcParameters = atcParameters;
	}

	public String getAuthenticityToken() {
		return authenticityToken;
	}

	public void setAuthenticityToken(String authenticityToken) {
		this.authenticityToken = authenticityToken;
	}
	
	
	
}
