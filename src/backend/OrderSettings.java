package backend;

import javax.swing.JComboBox;
import javax.swing.JTextField;

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
	private String checkoutType;

	private String[] fieldValuesAsArray;

	public OrderSettings() {
		initializeArrayOfValues(); //makes array of values in same order as checkout info
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
		this.ccNumber = ccNumber;
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
		this.refreshRate = refreshRate;
	}
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
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
	public void setProxytPass(String proxytPass) {
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
		this.phone = phone;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getCheckoutType() {
		return checkoutType;
	}

	public void setCheckoutType(String checkoutType) {
		this.checkoutType = checkoutType;
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

	private void initializeArrayOfValues() { //makes array of values in same order as checkout info
		fieldValuesAsArray = new String [23];
		fieldValuesAsArray[0] = name;
		fieldValuesAsArray[1] = email;
		fieldValuesAsArray[2] = phone;
		fieldValuesAsArray[3] = address1;
		fieldValuesAsArray[4] = address2;
		fieldValuesAsArray[5] = postalCode;
		fieldValuesAsArray[6] = city;
		fieldValuesAsArray[7] = stateAbbr;
		fieldValuesAsArray[8] = country;
		fieldValuesAsArray[9] = ccProvider;
		fieldValuesAsArray[10] = ccNumber;
		fieldValuesAsArray[11] = expMonth;
		fieldValuesAsArray[12] = expYear;
		fieldValuesAsArray[13] = cvv;
		fieldValuesAsArray[14] = refreshRate;
		fieldValuesAsArray[15] = null; //these are from the old checkout profiles (autoproccess/ disable images are deprecated)
		fieldValuesAsArray[16] = null; //these are from the old checkout profiles (autoproccess/ disable images are deprecated)
		fieldValuesAsArray[17] = address3;
		fieldValuesAsArray[18] = proxyAddress;
		fieldValuesAsArray[19] = proxyPort;
		fieldValuesAsArray[20] = proxyUser;
		fieldValuesAsArray[21] = proxyPass;
		fieldValuesAsArray[22] = checkoutType;
	}

	private void initializeValues() {//initializes values in array
		for (int i = 0; i < fieldValuesAsArray.length; i ++) {
			fieldValuesAsArray[i] = "";
		}
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	private void storeValuesFromArray(String[] array) {
		name = array[0];
		email = array[1];
		phone = array[2];
		address1 = array[3];
		address2 = array[4];
		postalCode = array[5];
		city = array[6];
		stateAbbr = array[7];
		country = array[8];
		ccProvider = array[9];
		ccNumber = array[10];
		expMonth = array[11];
		expYear = array[12];
		cvv = array[13];
		refreshRate = array[14];
	    //15 is from the old checkout profiles (autoproccess/ disable images are deprecated)
		//16 is from the old checkout profiles (autoproccess/ disable images are deprecated)
		address3 = array[17];
		proxyAddress = array[18];
		proxyPort = array[19];
		proxyUser = array[20];
		proxyPass = array[21];
		checkoutType = array[22];	
		
	}

}
