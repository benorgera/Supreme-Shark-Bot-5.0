package executor;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Map;

import backend.Main;
import backend.Order;


public class ProxyAuthenticator extends Authenticator {
	
	private Map<String, String[]> authMap;
	
	 @Override
     protected PasswordAuthentication getPasswordAuthentication() {
		 
		String[] entry = authMap.get(getRequestingHost());
		
		if (entry == null) {
			//proxy not foreseen to require authentication, some error must be handled
		}
		
		String user = entry[0];
		char[] pass = entry[1].toCharArray();
		 
        return new PasswordAuthentication(user, pass);
     }
	 
	 public void addProxy(String hostname, String username, String password) { //adds proxy to authMap
		 if (!authMap.containsKey(hostname)) authMap.put(hostname, new String[] {username, password});
	 }
	 
	 public void initialize() { //adds proxies to auth table
		 for (Order o : Main.getOrders()) {
			 
			 String username = o.getOrderSettings().getProxyUser();
			 String password = o.getOrderSettings().getProxytPass();
			 
			 if (!username.isEmpty() && !password.isEmpty()) { //if the proxy requires authentication
				 //addProxy(blah, username, password) - you don't know what hostname is yet
			 }
		 }
	 }

}
