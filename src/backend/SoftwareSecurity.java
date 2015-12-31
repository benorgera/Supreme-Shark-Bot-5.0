package backend;

import gui.LoadingGIF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class SoftwareSecurity {
	//tokens searched for to find values
	private final String activatedKeyToken = "supremeSharkBotActivated";
	private final String enabledKeyToken = "supremeSharkBotEnabled";
	private final String bannedKeyToken = "supremeSharkBotBanned";
	private final String licenseStatusToken = "supremeSharkBotVersionStatusToken"; //build storage of pro vs regular
	private final int timeoutMS = 4000;
	private LoadingGIF loadingGIF;
	private final double thisVersionNumber; //version number, value is blank, upon initialize pulled from main class 

	//vars storing values
	private String activationKeyValue;
	private boolean bannedKeyValue;
	private boolean enabledKeyValue;
	private licenseStatusEnum licenseStatusValue;

	//enums
	private enum botStatusEnum {BANNED, ENABLED, UNENABLED};
	private enum licenseStatusEnum {PRO, REGULAR, ERROR};


	private Preferences prefs;

	public SoftwareSecurity(LoadingGIF loadingGIF) {
		this.loadingGIF = loadingGIF; //passes loading gif jframe so that this class can update its text
		this.thisVersionNumber = Main.getThisVersionNumber(); //assign version number, stored in main class

	}

	public void initialize() {
		loadingGIF.passUI("Checking activation key");
		prefs = Preferences.userRoot().node(this.getClass().getName());
		activationKeyValue = prefs.get(activatedKeyToken, "failed"); //sets activationKeyValue, itll be failed if failed
		System.out.println("activationKeyValue: " + activationKeyValue);

		loadingGIF.passUI("Checking license activity");
		bannedKeyValue = prefs.getBoolean(bannedKeyToken, false); //sets banned value, banned is false upon failure
		System.out.println("bannedKeyValue from storage: " + bannedKeyValue);
		if (checkBanned()) { //it was banned
			prefs.putBoolean(bannedKeyToken, true);
			System.out.println("License was banned");
			bannedKeyValue = true;
		}
		System.out.println("bannedKeyValue following server check: " + bannedKeyValue);

		loadingGIF.passUI("Checking if activated");
		enabledKeyValue = prefs.getBoolean(enabledKeyToken, false); //if enabled couldnt be recovered enabled was false
		System.out.println("enabledKeyValue: " + enabledKeyValue);

		loadingGIF.passUI("Checking license status");
		licenseStatusValue = licenseStatusEnum.valueOf(prefs.get(licenseStatusToken, "ERROR")); //version set to error if version couldn't be recovered
		System.out.println("licenseStatusValue from storage: " + licenseStatusValue);

		checkLicenseStatus(); //checks license status from server
		System.out.println("licenseStatusValue following server check: " + licenseStatusValue);

		Object[] res = checkOutdated(); //array of boolean of if its outdated and JSON of version info
		if ((Boolean) res[0]) updateSoftware((JSONObject) res[1]);

	}

	public void clearPrefsRoot() {
		prefs.remove(enabledKeyToken);
		prefs.remove(bannedKeyToken);
		prefs.remove(licenseStatusToken);
		prefs.remove(activatedKeyToken);
	}

	private void updateSoftware(JSONObject JSON) {
		System.out.println("Updating software");
		//future things pulled from JSON must be added to the blank list of json keys`
		//this json object will contain info about the new version

	}

	private Object[] checkOutdated() { //returns [true, JSON from version script on server] or [false, JSON from version script on server] 

		JSONObject newestVersionInfo = connectToServer("http://www.supremesharkbot.com:8080/version/");
		double newestVersionNumber = newestVersionInfo.getDouble("version");

		System.out.println(newestVersionNumber > thisVersionNumber ? "newestVersionNumber: " + newestVersionNumber + " > " + "thisVersionNumber: " + thisVersionNumber : "newestVersionNumber: " + newestVersionNumber + " <= " + "thisVersionNumber: " + thisVersionNumber);
		
		return new Object[]{newestVersionNumber > thisVersionNumber, newestVersionInfo};
	}

	private botStatusEnum checkStatus() { //returns status of bot
		return bannedKeyValue ? botStatusEnum.BANNED : (enabledKeyValue ? botStatusEnum.ENABLED : botStatusEnum.UNENABLED);
	}

	public void processStatus() {
		if (botStatusEnum.BANNED.equals(checkStatus())) { //banned
			message("This license has been banned, exiting now", "Banned");
			System.exit(0);
		} else if (botStatusEnum.ENABLED.equals(checkStatus())) { //everything worked out, send them back to main
			if (licenseStatusEnum.ERROR.equals(licenseStatusValue)) checkLicenseStatus(); //license status not determined
		} else if (attemptActivation(getKeyFromUser())) { //needs to be activated, gets key then tries to activate
			setActivated();
		}
	}

	private void setActivated() {
		prefs.put(activatedKeyToken, activationKeyValue); //set activation key in storage
		prefs.putBoolean(enabledKeyToken, true);
		prefs.putBoolean(bannedKeyToken, false);
		System.out.println("Bot Activated in Storage");

		initialize(); //simulates bot being launched, causing a banned check and double security
	}

	private String getKeyFromUser() { //prompts user for key
		boolean ready = false;
		String keyRes = null;
		while (!ready) {
			keyRes = JOptionPane.showInputDialog(null, "Enter Activation Key (Requires Internet Connection):", "Activate Supreme Shark Bot", 3);

			if (null == keyRes) { //they clicked x or cancel to activation prompt
				System.out.println("Activation Prompted Exited, Software Quitting");
				System.exit(0);
			} else if (!keyRes.isEmpty()) { //the keyRes wasn't blank
				ready = true;
				System.out.println("Activation Key Entered: " + keyRes);
			}
		}
		return keyRes;
	}

	private boolean attemptActivation(String userEnteredKey) {

		boolean res = connectToServer(makeActivationLink(userEnteredKey)).getBoolean("success");
		
		System.out.println("Server Response to Activation: " + res);
		if (res) {
			message("Activated Successfully!", "Success");
			activationKeyValue = userEnteredKey; //set the key so it can be accessed by setActivates
			enabledKeyValue = true;
			return true;
		} else if (!res) {
			message("Activation Failed! Activation key invalid or already used", "Activation Failed");
			System.exit(0);
			return false;
		} else {
			message("Unable to connect to activation server", "Connection Failed");
			System.exit(0);
			return false;
		}
	}

	private String makeActivationLink(String enteredKeyRes) {
		String enteredKey = enteredKeyRes;
		String macAddress = "unreachable";
		String ipAddress = "unreachable";

		try {
			String[] res = getMacAndIPAddresses();
			ipAddress = res[0];
			macAddress = res[1];
		} catch (IOException e) {
			//returns unreachable for both
		}


		return "http://supremesharkbot.com:8080/activation/?key=" + enteredKey + "&mac_address=" + macAddress + "&ip_address=" + ipAddress;
	}

	private JSONObject connectToServer(String site) { //every call to this method must add to the blankjsonobjectmadetoavoiderrors if it calls a new json key
		try {
			URLConnection con = new URL(site).openConnection();
			con.setConnectTimeout(timeoutMS);
			con.setReadTimeout(timeoutMS);
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			System.out.println("Server connection successful, returning JSON from server");
			return new JSONObject(IOUtils.toString(in, encoding == null ? "UTF-8" : encoding));
		} catch (Exception e) {
			System.out.println("Error during server connection: " + e.getMessage());
		}
		
		
		JSONObject blankJSONObjectMadeToAvoidErrors = new JSONObject(); //returns json object with values expected to avoid org.jsonexception
		blankJSONObjectMadeToAvoidErrors.put("banned", "no");
		blankJSONObjectMadeToAvoidErrors.put("version", 0.1);
		blankJSONObjectMadeToAvoidErrors.put("success", false);
		blankJSONObjectMadeToAvoidErrors.put("licenseType", "");

		return blankJSONObjectMadeToAvoidErrors; //returns null if error connecting
	}

	private String[] getMacAndIPAddresses() throws IOException {
		String macAddress;
		String ipAddress;

		//get ip
		ipAddress = ("External:'"  + new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine() + "',Host:" + InetAddress.getLocalHost().getHostAddress() + "'");

		//get mac
		byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
		
		macAddress = sb.toString();

		//return the values as a string array
		return new String[]{ipAddress, macAddress};
	}

	private boolean checkBanned() { //return true if it was banned
		return "yes".equals(connectToServer("http://supremesharkbot.com:8080/banned/?key=" + activationKeyValue).get("banned"));
	}

	public boolean getVersionIsPro() {
		return licenseStatusEnum.PRO.equals(licenseStatusValue);
	}

	public double getThisVersionNumber() {
		return thisVersionNumber;
	}

	private void checkLicenseStatus() {//makes sure their version status wasn't an error

		licenseStatusEnum res = null;
		try {
			res = licenseStatusEnum.valueOf((String) connectToServer("http://www.supremesharkbot.com:8080/licenseStatus/?key=" + activationKeyValue).get("licenseType"));
		} catch (Exception e) {
			System.out.println("Couldn't get license status");
		}

		if (null != res && !licenseStatusEnum.ERROR.equals(res)) { //server had a licenseStatus, giving them that one
			System.out.println("License From Server Was: " + res + ", Setting licenseStatusValue As Such");
			licenseStatusValue = res;
			prefs.put(licenseStatusToken, licenseStatusValue.toString());
		} else if (licenseStatusEnum.ERROR.equals(licenseStatusValue)) {//server and storage doesn't know, they're getting regular and hopefully that's what they paid for
			System.out.println("License From Server And Storage Was Null, Giving Them Regular");
			licenseStatusValue = licenseStatusEnum.REGULAR;
		}
	}

	private void message(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title == null ? "Supreme Shark Bot" : title , 3);
	}

	public String getActivationKeyValue() {
		return activationKeyValue;
	}

	public boolean deactivateLicense() { //called by GUI to deactivate license
		
		if (connectToServer("http://www.supremesharkbot.com:8080/deactivateLicense/?key=" + Main.getActivationKey()).getBoolean("success")) { //deactivated successfully, remove their license from computer
			clearPrefsRoot();
			return true;
		} else {
			return false;
		}
	}
}
