package executor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import backend.Item;
import backend.OrderSettings;
import backend.RandomUserAgent;

public class HTTPConnector {

	private ProxyBuilder proxyBuilder;
	private OrderSettings settings;
	private TaskProcessor processor;
	private String cookies = "";

	public HTTPConnector(OrderSettings settings, TaskProcessor processor) {
		this.proxyBuilder = settings.isUsingProxy() ? new ProxyBuilder(settings.getProxyAddress(), settings.getProxyPort(), settings.getProxyUser(), settings.getProxytPass()) : null;
		this.settings = settings;
		this.processor = processor;
	}

	public String getHTMLString(String url) {
		String mostRecentHTML = "";
		try {
			URLConnection con = settings.isUsingProxy() ? new URL(url).openConnection(proxyBuilder.getProxy()) : new URL(url).openConnection();
			con.setUseCaches(false);
			con.setConnectTimeout(8000); //timeout after 8 seconds
			con.setReadTimeout(8000);
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

	public Object[] chechEarlyLink(String url) { //checks if early link redirects, if it doesn't send back the html of the item
		String mostRecentHTML = "";
		boolean success;
		processor.printSys("Original URL of Early Link: " + url);
		try {
			HttpURLConnection con = settings.isUsingProxy() ? (HttpURLConnection)(new URL(url).openConnection(proxyBuilder.getProxy())) : (HttpURLConnection)(new URL(url).openConnection());
			con.setUseCaches(false);
			con.setConnectTimeout(8000);
			con.setInstanceFollowRedirects(false);
			con.connect();
			String location = con.getHeaderField("Location");
			processor.printSys("Response Code: " + con.getResponseCode() + ", Early Link Redirected to: " + location);
			mostRecentHTML = connectionToString(con);
			success = location == null; //if the redirect location was null, this worked
		} catch (MalformedURLException e) {
			success = false;
		} catch (IOException e) {
			success = false;
		}

		return new Object[]{success, mostRecentHTML};
	}

	public boolean atcPost(Item item) { //returns 

		try {
			String urlParameters = item.getAtcParameters();
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			//utf8, size, auth token, add-to-cart


			String itemLink = item.getLink();
			String xCSRFToken = item.getAuthenticityToken();

			URL url = new URL(item.getAtcLink());

			HttpURLConnection con = (HttpURLConnection) url.openConnection();           
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(8000); //timeout after 8 seconds
			con.setReadTimeout(8000);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Host", "www.supremenewyork.com");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			con.setRequestProperty("Accept", "*/*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript");
			con.setRequestProperty("Origin", "http://www.supremenewyork.com");
			con.setRequestProperty("X-CSRF-Token", xCSRFToken);
			con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			con.setRequestProperty("User-Agent", RandomUserAgent.getRandomUserAgent());
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			con.setRequestProperty("Referer", itemLink);
			con.setRequestProperty("Accept-Encoding", "gzip, deflate");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			setCookies(con);

			//			POST /shop/169443/add HTTP/1.1
			//			Host: www.supremenewyork.com
			//			Connection: keep-alive
			//			Content-Length: 114
			//			Accept: */*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript
			//			Origin: http://www.supremenewyork.com
			//			X-CSRF-Token: j/3WH4FuSfPEDw9GkzSmeYrH14VZrcI1S1KXJO+24/8=
			//			X-Requested-With: XMLHttpRequest
			//			User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36
			//			Content-Type: application/x-www-form-urlencoded; charset=UTF-8
			//			Referer: http://www.supremenewyork.com/shop/pants/eat-me-sweatshort
			//			Accept-Encoding: gzip, deflate
			//			Accept-Language: en-US,en;q=0.8
			//
			//			utf8=%E2%9C%93&authenticity_token=RWvrnsyge9GlIfS5rX63S5p%2B5J%2Bcd6hecMFnhZ9XjQk%3D&size=28959&commit=add+to+cart


			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(postData);

			storeCookies(con);

			con.getInputStream(); //throws error if atc failed, ensuring false will be returned

			con.disconnect();

			return true; //if error hasn't been thrown, everything went smoothly

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false; //error thrown, something failed, lets try again

	}

	private String connectionToString(URLConnection con) throws IOException { //gets body of connection as string
		String mostRecentHTML = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine = "";
		while ((inputLine = in.readLine()) != null) mostRecentHTML += inputLine; //read html stream
		in.close();
		return mostRecentHTML;
	}

	private void addCookie(String cookie) { //adds received cookie to cookie string
		if (cookies.contains(cookie)) return; //we have this exact cookie

		String newCookieName = cookie.split("=")[0];
		if (cookies.contains(newCookieName)) replaceCookies(newCookieName);  //we have a cookie with the same name as the new cookie, swap them
		
		processor.printSys("New Cookie: " + cookie);
		cookies += (cookie + "; "); //add the new cookie followed by a semicolon
	}

	private void storeCookies(URLConnection con) { //iterates through headers, adding new cookie
		String headerName = null;
		
		for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++) if (headerName.equals("Set-Cookie")) addCookie(con.getHeaderField(i));               
	}

	private void setCookies(URLConnection conn) { //add stored cookies to new URLConnection
		if (cookies.isEmpty()) {conn.setRequestProperty("Cookie", ""); return;}

		conn.setRequestProperty("Cookie", cookies);
		processor.printSys("Cookies Set: " + cookies);
	}

	private void replaceCookies(String newCookieName) {

		String[] cookiesAsArray = cookies.split("; "); //create array of cookies
		for (int i = 0; i < cookiesAsArray.length; i++) {
			if (cookiesAsArray[i].split("=")[0].equals(newCookieName)) { //if they have the same name remove the existing cookie
				cookiesAsArray[i] = "";
				processor.printSys("Cookie '" + newCookieName + "' Updated");
			}
		}


		cookies = "";

		for (String s : cookiesAsArray) if (!s.isEmpty()) cookies += (s + "; "); //rebuild cookie string with non blank cookies
		

	}
}



