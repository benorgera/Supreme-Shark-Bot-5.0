package backend;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.google.common.base.CharMatcher;

public class OrderSettings {
	private String store;
	private String name;
	private String email;
	private String phone;
	private String address1;
	private String address2;
	private String address3;
	private String postalCode;
	private String city;
	private String stateAbbr;
	private String country;
	private String ccProvider;
	private String ccNumber;
	private String expMonth;
	private String expYear;
	private String cvv;
	private String refreshRate;
	private String proxyAddress;
	private String proxyPort;
	private String proxyUser;
	private String proxyPass;

	private String[] fieldValuesAsArray;

	private boolean usingProxy = false;

	private boolean settingsSet = false; //tells bot whether settings have been inputted, if false bot cant be enabled

	//technical data

	private String checkoutParameters;
	private String checkoutServerResponse;
	private String checkoutLink;

	public OrderSettings() {
		fieldValuesAsArray = new String[22];//makes array of values in same order as checkout info
		initializeValues(); //initializes each string int the array
	}

	public void assignValuesFromFieldArray(Object[] fieldsAsArray) {//receives array from gui and stores values
		for (int counter = 0; counter < fieldValuesAsArray.length; counter ++) {

			if (fieldsAsArray[counter] == null) {
				//deprecated field (auto process/ disable images)
			} else if (fieldsAsArray[counter] instanceof JComboBox) { //get Jcombobox ite

				this.fieldValuesAsArray[counter] = (String) ((JComboBox<?>) fieldsAsArray[counter]).getSelectedItem();

			} else { //get jtext field text

				this.fieldValuesAsArray[counter] = (String) ((JTextField) fieldsAsArray[counter]).getText();

			}
		}

		System.out.println("Values assigned in order settings");
		storeValuesFromArray(fieldValuesAsArray);
		settingsSet = true; //the user has no inputted order settings
	}


	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;

		//set store based on country
		if (country.equals("USA") || country.equals("CANADA")) {
			setStore("US/CANADA");
		} else if (CharMatcher.ASCII.matchesAllOf(country)) { //its not jp because those characters are unicode
			setStore("UK");
		} else {
			setStore("JAPAN");
		}
		System.out.println("Store based on country: " + getStore());
	}
	public String getStateAbbr() {
		return stateAbbr;
	}
	public void setStateAbbr(String stateAbbr) {
		this.stateAbbr = stateAbbr;
	}
	public String getCcProvider() {
		return ccProvider;
	}
	public void setCcProvider(String ccProvider) {
		this.ccProvider = ccProvider;
	}
	public String getCcNumber() {
		return ccNumber;
	}
	public void setCcNumber(String ccNumber) {
		String justNumbers = ccNumber.replace(" ", "");
		if (ccNumber.isEmpty() || getCcProvider().isEmpty() || justNumbers.length() < 10) {
			this.ccNumber = ccNumber;
		} else if (getCcProvider().equals("American Express")) {
			this.ccNumber = justNumbers.substring(0, 4) + " " + justNumbers.substring(4, 10) + " " + justNumbers.substring(10);
		} else {
			this.ccNumber = justNumbers.substring(0, 4) + " " + justNumbers.substring(4, 8) + " " + justNumbers.substring(8, 12) + " " + justNumbers.substring(12);
		}
	}
	public String getExpMonth() {
		return expMonth;
	}
	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}
	public String getExpYear() {
		return expYear;
	}
	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}
	public String getCvv() {
		return cvv;
	}
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	public String getRefreshRate() {
		return refreshRate;
	}
	public void setRefreshRate(String refreshRate) {
		this.refreshRate = refreshRate.replace(",", ""); //remove commas from number
	}
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
		setUsingProxy(!proxyAddress.isEmpty()); //if its empty, they're not using a proxy
	}
	public String getProxyUser() {
		return proxyUser;
	}
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public String getProxytPass() {
		return proxyPass;
	}
	public void setProxyPass(String proxytPass) {
		this.proxyPass = proxytPass;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {

		phone = phone.replaceAll("[^\\d.]", ""); //get only numbers


		//format as (123) 456-7890

		if (phone.length() >= 10) {		
			
			String firstThree = phone.substring(0, 3);

			String secondThree = phone.substring(3, 6);

			String lastFour = phone.substring(6, 10);

			this.phone = "(" + firstThree + ") " + secondThree + "-" + lastFour; 

		} else {
			
			this.phone = phone;
			
		}
	}

	public String getPhoneUnformatted() {
		return getPhone().replace(" ", "").replace("(", "").replace(")", "").replace("-", " ");
	}
	
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getFieldValuesAsArray() {
		return fieldValuesAsArray;
	}

	private void initializeValues() {//initializes values in array	
		for (int i = 0; i < fieldValuesAsArray.length; i ++) fieldValuesAsArray[i] = (i != 14 ? "" : "400"); //set field blank, unless its refresh rate
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isUsingProxy() {
		return this.usingProxy;
	}

	public void setUsingProxy(boolean isUsingProxy) {
		this.usingProxy = isUsingProxy;
	}

	private void storeValuesFromArray(String[] array) { //takes values from array and 
		setName(array[0]);
		setEmail(array[1]);
		setPhone(array[2]);
		setAddress1(array[3]);
		setAddress2(array[4]);
		setPostalCode(array[5]);
		setCity(array[6]);
		setStateAbbr(array[7]);
		setCountry(array[8]);
		setCcProvider(array[9]);
		setCcNumber(array[10]);
		setExpMonth(array[11]);
		setExpYear(array[12]);
		setCvv(array[13]);
		setRefreshRate(array[14]);
		//15 is from the old checkout profiles (autoproccess/ disable images are deprecated)
		//16 is from the old checkout profiles (autoproccess/ disable images are deprecated)
		setAddress3(array[17]);
		setProxyAddress(array[18]);
		setProxyPort(array[19]);
		setProxyUser(array[20]);
		setProxyPass(array[21]);

	}

	public String getCheckoutParameters() {
		return checkoutParameters;
	}

	public void setCheckoutParameters(String checkoutParameters) {
		this.checkoutParameters = checkoutParameters;
	}

	public String getCheckoutLink() {
		return checkoutLink;
	}

	public void setCheckoutLink(String checkoutLink) {
		this.checkoutLink = checkoutLink;
	}

	public String getCheckoutServerResponse() {
		return checkoutServerResponse;
	}

	public void setCheckoutServerResponse(String checkoutServerResponse) {
		this.checkoutServerResponse = checkoutServerResponse;
	}

	public boolean areSettingsSet() {
		return settingsSet;
	}

}
