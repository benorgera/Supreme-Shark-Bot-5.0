package executor;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URLConnection;

public class ProxyBuilder {

	private Proxy proxy;
	private String proxyUser;
	private String proxyPass;

	public ProxyBuilder(String proxyAddress, String proxyPort, String proxyUser, String proxyPass) { //returns proxy object properly configured

		proxy = proxyPort.isEmpty() ? new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, 80)) : new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, Integer.parseInt(proxyPort))); //no port specified 80 assumed

		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;
	}

	public Proxy getProxy() {return proxy;}

	public void addAuthorization(URLConnection con) { //adds authorization (some proxies have login)
//		con.addRequestProperty("Authorization", ("Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((proxyUser + ":" + proxyPass).getBytes())));
	}
}
