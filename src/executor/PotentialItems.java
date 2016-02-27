package executor;

import java.util.ArrayList;
import java.util.Arrays;

public class PotentialItems {
	
	private ArrayList<String> urls;
	private ArrayList<String> linkTexts;

	public PotentialItems() {
		urls = new ArrayList<String>();
		linkTexts = new ArrayList<String>();
	}
	
	public void add(String url, String text) {
		urls.add(url);
		linkTexts.add(text);
	}
	
	public void add(String[] both) {
		urls.add(both[0]);
		linkTexts.add(both[1]);
	}
	
	public String[] get(int index) {
		return new String[] {urls.get(index), linkTexts.get(index)};
	}
	
	public String getLinkText(int index) {
		return linkTexts.get(index);
	}

	public String getURL(int index) {
		return urls.get(index);
	}
	
	public void remove(int index) {
		System.out.println("removed:" + Arrays.asList(get(index)));
		urls.remove(index);
		linkTexts.remove(index);
	}
	
	public int size() {
		return urls.size();
	}
	
	public boolean containsURL(String text) {
		return urls.contains(text);
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < size(); i++) s += (Arrays.asList(get(i)) + "\n");
		return s;
	}
	
	public int indexOfURL(String url) {
		return urls.indexOf(url);
	}
	
	public void setLinkText(int index, String value) {
		linkTexts.set(index, value);
	}
	
	
}
