package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.swing.JTextArea;
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

		results = new ArrayList<String>();
		resNums = new ArrayList<Long>();
		try {
			System.out.println("Order "+o.getOrderNum()+" proxy attempt\n\tAddress: "+o.getOrderSettings().getProxyAddress()+"\n\tPort: "+ (o.getOrderSettings().getProxyPort() == null || o.getOrderSettings().getProxyPort().isEmpty() ? "80" : o.getOrderSettings().getProxyPort()));

			ProxyBuilder proxyBuilder = new ProxyBuilder(o.getOrderSettings().getProxyAddress(), o.getOrderSettings().getProxyPort(), o.getOrderSettings().getProxyUser(), o.getOrderSettings().getProxytPass());

			String html = "";
			for (int i = 0; i < 5; i++) { //take average connection time
				long startTime = System.currentTimeMillis();

				URLConnection con = new URL("http://www.supremenewyork.com/shop/all").openConnection(proxyBuilder.getProxy());
				proxyBuilder.addAuthorization(con);
				con.setConnectTimeout(8000); //set timeout to 8 seconds
				con.setReadTimeout(8000);
				con.connect();
				con.getInputStream();
				if (i == 0) { //only need to read the html once
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine = "";
					while ((inputLine = in.readLine()) != null) html += inputLine; //read html stream
					in.close();
				}
				long endTime = System.currentTimeMillis();
				System.out.println("Connection Time: "+(endTime-startTime));
				if (i != 0) resNums.add(endTime - startTime); //skip the first result, its an outlier
			}

			long total = 0;
			for (Long l : resNums) total += l;
			long avg = total / resNums.size();

			results.add("Order " + o.getOrderNum() + " proxy initialized successfully\n\tAverage Connection Time to Supreme Server: " + avg + " ms\n\tProxy Connects to " + (!html.contains("LDN") ? (html.contains("TYO") ? "Tokyo" : "NYC") : "London") + " Store");
		} catch (NullPointerException e) {
			results.add("Order " + o.getOrderNum() + " proxy is not set in order settings");
		} catch (IllegalArgumentException e) {
			results.add("Order " + o.getOrderNum() + " proxy is not set in order settings");
		} catch (java.net.SocketTimeoutException e) {
			results.add("Order " + o.getOrderNum() + " proxy timed out");
		} catch (Exception w) {
			results.add(w.getMessage().contains("503") ? "Order " + o.getOrderNum() + " proxy banned from Supreme Server, they returned a 503 error" : "Order " + o.getOrderNum() + " proxy failed");
		}

		for (String s : results) area.setText(area.getText() +"\n"+s); //notify user of results
	}


}
