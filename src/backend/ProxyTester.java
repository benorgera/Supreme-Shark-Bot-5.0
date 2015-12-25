package backend;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import executor.ProxyBuilder;


public class ProxyTester implements Runnable {

	private ArrayList<String> results;
	private Order o;
	private JTextArea area;
	private ArrayList<Long> resNums;

	public ProxyTester(Order o, JTextArea area) {
		results = new ArrayList<String>();
		this.area = area;
		this. o = o;
	}


	public void run() {
		System.out.println("Order "+o.getOrderNum()+" proxy attempt\n\tAddress: "+o.getOrderSettings().getProxyAddress()+"\n\tPort: "+o.getOrderSettings().getProxyPort() == null || o.getOrderSettings().getProxyPort().isEmpty() ? 80 : o.getOrderSettings().getProxyPort());
		
		resNums = new ArrayList<Long>();
		results = new ArrayList<String>();
		try {
	
			ProxyBuilder proxyBuilder = new ProxyBuilder(o.getOrderSettings().getProxyAddress(), o.getOrderSettings().getProxyPort(), o.getOrderSettings().getProxyUser(), o.getOrderSettings().getProxytPass());

			System.out.println("Order "+o.getOrderNum()+" proxy address: "+o.getOrderSettings().getProxyAddress()+" on port " + o.getOrderSettings().getProxyPort() == null || o.getOrderSettings().getProxyPort().isEmpty() ? 80 : o.getOrderSettings().getProxyPort());

			for (int i = 0; i < 10; i++) { //take average connection time
				long startTime = System.currentTimeMillis();
				
				URLConnection con = new URL("http://www.supremenewyork.com/shop/all").openConnection(proxyBuilder.getProxy());
				proxyBuilder.addAuthorization(con);
				con.connect();
				con.getInputStream();
				long endTime = System.currentTimeMillis();
				System.out.println("Connection Time: "+(endTime-startTime));
				if (i != 0) resNums.add(endTime - startTime); //skip the first result, its an outlier
			}
			
			long total = 0;
			for (Long l : resNums) total += l;
			long avg = total / resNums.size();
			
			results.add("Order "+o.getOrderNum()+" proxy initialized successfully\n\tAverage Connection Time to Supreme Server: " + avg + " milliseconds");
		} catch (NullPointerException | IllegalArgumentException e) {
			results.add("Order "+o.getOrderNum()+" proxy is not set in order settings");
		} catch (Exception w) {
			if (w.getMessage().contains("503")) {
				results.add("Order "+o.getOrderNum()+" proxy banned from Supreme Server, they returned a 503 error");
			} else {
				results.add("Order "+o.getOrderNum()+" proxy failed");
			}


		}

		for (String s : results) area.setText(area.getText() +"\n"+s); //notify user of results
	}


}
