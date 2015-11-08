package bot;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;


public class ProxyTester extends SwingWorker {

	private ArrayList<String> results;
	private Order o;
	private SwingWorker worker;
	private JTextArea area;

	public ProxyTester(Order o, JTextArea area) {
		results = new ArrayList<String>();
		this.area = area;
		this. o = o;
	}
	

	@Override
	protected Object doInBackground() throws Exception {
		System.out.println("Order "+o.getOrderNum()+" proxy attempt\n\tAddress: "+o.getOrderSettings().getProxyAddress()+"\n\tPort: "+o.getOrderSettings().getProxyPort());
		System.out.println(Arrays.asList(o.getOrderSettings().getFieldValuesAsArray()));
		System.out.println(o.getOrderSettings().getName());
		try {
			Proxy proxy;

			long startTime = System.currentTimeMillis();
			String proxyAddress = o.getOrderSettings().getProxyAddress();
			String proxyPort = o.getOrderSettings().getProxyPort();
			String proxyUser = o.getOrderSettings().getProxyUser();
			String proxyPass = o.getOrderSettings().getProxytPass();

			if (proxyPort != null && !proxyPort.isEmpty()) { //no port specified, 80 assumed
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, Integer.parseInt(proxyPort)));
				System.out.println("Order "+o.getOrderNum()+" proxy address: "+proxyAddress+" on port "+Integer.parseInt(proxyPort));
			} else { //port specified
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, 80));
				System.out.println("Order "+o.getOrderNum()+" proxy address: "+proxyAddress+" on port 80 assumed because no port provided");
			}

			String userpass = proxyUser + ":" + proxyPass;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());


			URLConnection con = new URL("http://www.supremenewyork.com/shop/all").openConnection(proxy);
			con.setRequestProperty("Authorization", basicAuth);
			con.connect();
			con.getInputStream();
			long endTime = System.currentTimeMillis();
			System.out.println("Connection Time: "+(endTime-startTime));
			results.add("Order "+o.getOrderNum()+" proxy initialized successfully\n\tTime to Supreme Server: "+(endTime-startTime)+" ms");
		} catch (NullPointerException | IllegalArgumentException e) {
			results.add("Order "+o.getOrderNum()+" proxy is not set in order settings");
			e.printStackTrace();
		} catch (Exception w) {
			if (w.getMessage().contains("503")) {
				results.add("Order "+o.getOrderNum()+" proxy banned from Supreme Server, they returned a 503 error");
			} else {
				results.add("Order "+o.getOrderNum()+" proxy failed");
			}

		}
		
		return null;
		

	}
	
	@Override
	protected void done() {
		for (String s : results) area.setText(area.getText()+"\n"+s);
	}




}
