package backend;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLConnection;

public class ProxyBuilder {

	public Proxy configure(String proxyAddress, String proxyPort) { //returns proxy object properly configured
		Proxy proxy;
		
		if (proxyPort != null && !proxyPort.isEmpty()) { //no port specified, 80 assumed
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, Integer.parseInt(proxyPort)));
		} else { //port specified
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, 80));
		}
		return proxy;

	}

	public URLConnection addAuthorization(URLConnection con, String proxyUser, String proxyPass) { //adds authorization (some proxies have login)
		String userpass = proxyUser + ":" + proxyPass;
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
		con.addRequestProperty("Authorization", basicAuth);
		return con;
	}
}
