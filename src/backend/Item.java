package backend;

import java.util.Arrays;

public class Item {
	private String[] keywords;
	private String color;
	private String category;
	private String earlyLink;
	private String size;
	private int itemNumber;
//	private String[] potential;
//	private String[] incorrect;
//	private boolean unsure;
//	
	private String link; //set by link finder once link is found
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
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
	
//	public String[] getPotential() {
//		return potential;
//	}
//	
//	public void setPotential(String[] potential) {
//		this.potential = potential;
//	}
//	
//	public String[] getIncorrect() {
//		return incorrect;
//	}
//	
//	public void setIncorrect(String[] incorrect) {
//		this.incorrect = incorrect;
//	}
//
//	public boolean isUnsure() {
//		return unsure;
//	}
//
//	public void setUnsure(boolean unsure) {
//		this.unsure = unsure;
//	}
	
//	public Item() {	
//		setUnsure(false);
//	}

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
		return "Item Number " + itemNumber + ":\n\nKeywords:\n\t" + Arrays.asList(keywords).toString() + "\nCategorylor:\n\t" + category + "\nColor:\n\t" + color + "\nSize:\n\t" + size + "\nEarly Link:\n\t" + earlyLink;
	}
	
	
}
