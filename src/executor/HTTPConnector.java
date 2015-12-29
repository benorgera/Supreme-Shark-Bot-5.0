package executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
			con.setConnectTimeout(8000); //timeout after 8 seconds
			con.connect();
			mostRecentHTML = connectionToString(con);
			processor.printSys("Connection to Supreme" + (settings.isUsingProxy() ? " Using Proxy " : " ") + "Successful");
		} catch (java.net.SocketTimeoutException e) {
			processor.print("Connection to Supreme" + (settings.isUsingProxy() ? " using proxy " : " ") + "timed out");
		} catch (IOException e) {
			processor.print("Connection to Supreme" + (settings.isUsingProxy() ? " using proxy " : " ") + "failed");
		} 

		return mostRecentHTML;
	}

	public Object[] chechEarlyLink(String url) {
		String location = "";
		String mostRecentHTML = "";
		boolean success;
		processor.printSys("Original URL of Early Link: " + url);
		try {
			HttpURLConnection con = (HttpURLConnection)(new URL(url).openConnection());
			con.setConnectTimeout(8000);
			con.setInstanceFollowRedirects(false);
			con.connect();
			location = con.getHeaderField("Location");
			processor.printSys("Response Code: " + con.getResponseCode() + ", Early Link Redirected to: " + location);
			mostRecentHTML =  connectionToString(con);
			success = location == null;
		} catch (MalformedURLException e) {
			success = false;
		} catch (IOException e) {
			success = false;
		}
		
		Object[] returnArray = new Object[2];
		returnArray[0] = success;
		returnArray[1] = mostRecentHTML;
		
		return returnArray;
		
		//process this in item link camper
	}
	
	private String connectionToString(URLConnection con) throws IOException {
		String mostRecentHTML = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine = "";
		while ((inputLine = in.readLine()) != null) mostRecentHTML += inputLine; //read html stream
		in.close();
		return mostRecentHTML;
	}
}
