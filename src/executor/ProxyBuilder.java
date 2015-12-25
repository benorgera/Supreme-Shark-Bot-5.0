package executor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLConnection;

public class ProxyBuilder {
	
	private Proxy proxy;
	private String proxyUser;
	private String proxyPass;
	
	public ProxyBuilder(String proxyAddress, String proxyPort, String proxyUser, String proxyPass) { //returns proxy object properly configured
		
		if (proxyPort != null && !proxyPort.isEmpty()) { //no port specified, 80 assumed
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, Integer.parseInt(proxyPort)));
		} else { //port specified
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, 80));
		}
		
		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;
	}
	
	public Proxy getProxy() {return proxy;}

	public void addAuthorization(URLConnection con) { //adds authorization (some proxies have login)
		String userpass = proxyUser + ":" + proxyPass;
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
		con.addRequestProperty("Authorization", basicAuth);
	}
}
