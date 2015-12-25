package executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import backend.OrderSettings;

public class HTTPConnector {
	
	private ProxyBuilder proxyBuilder;
	private OrderSettings settings;
	private URLConnection con;
	private TaskProcessor processor;
	
	public HTTPConnector(OrderSettings settings, TaskProcessor processor) {
		this.proxyBuilder = settings.isUsingProxy() ? new ProxyBuilder(settings.getProxyAddress(), settings.getProxyPort(), settings.getProxyUser(), settings.getProxytPass()) : null;
		this.settings = settings;
		this.processor = processor;
	}

	public String getHTMLString(String url) {
		
		String mostRecentHTML = "";
		try {
			con = settings.isUsingProxy() ? new URL(url).openConnection(proxyBuilder.getProxy()) : new URL(url).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) mostRecentHTML += inputLine; //read html stream
			in.close();
			processor.printSys("Connection to Supreme" + (!settings.isUsingProxy() ? " Not " : " ") + "Using Proxy Successful");
		} catch (IOException e) {
			processor.print("Connection to Supreme failed");
		}
		return mostRecentHTML;
	}
}
