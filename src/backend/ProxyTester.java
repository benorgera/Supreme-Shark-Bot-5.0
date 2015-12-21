package backend;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;


public class ProxyTester implements Runnable {

	private ArrayList<String> results;
	private Order o;
	private JTextArea area;

	public ProxyTester(Order o, JTextArea area) {
		results = new ArrayList<String>();
		this.area = area;
		this. o = o;
	}


	public void run() {
		System.out.println("Order "+o.getOrderNum()+" proxy attempt\n\tAddress: "+o.getOrderSettings().getProxyAddress()+"\n\tPort: "+o.getOrderSettings().getProxyPort());
		System.out.println(Arrays.asList(o.getOrderSettings().getFieldValuesAsArray()));
		System.out.println(o.getOrderSettings().getName());
		try {

			long startTime = System.currentTimeMillis();
			String proxyAddress = o.getOrderSettings().getProxyAddress();
			String proxyPort = o.getOrderSettings().getProxyPort();
			String proxyUser = o.getOrderSettings().getProxyUser();
			String proxyPass = o.getOrderSettings().getProxytPass();

			ProxyBuilder proxyBuilder = new ProxyBuilder();
			
			System.out.println("Order "+o.getOrderNum()+" proxy address: "+proxyAddress+" on port " + proxyPort == null ? 80 : proxyPort);

			URLConnection con = new URL("http://www.supremenewyork.com/shop/all").openConnection(proxyBuilder.configure(proxyAddress, proxyPort));
			proxyBuilder.addAuthorization(con, proxyUser, proxyPass);
			con.connect();
			con.getInputStream();
			long endTime = System.currentTimeMillis();
			System.out.println("Connection Time: "+(endTime-startTime));
			results.add("Order "+o.getOrderNum()+" proxy initialized successfully\n\tTime to Supreme Server: "+(endTime-startTime)+" milliseconds");
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
