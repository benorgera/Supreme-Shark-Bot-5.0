package bot;

import java.util.Arrays;

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
	private String proxyUser;
	private String proxyPass;
	private String checkoutType;

	private String[] fieldValuesAsArray;

	public OrderSettings() {
		initializeArrayOfValues(); //makes array of values in same order as checkout info
		initializeValues(); //initializes each string int the array
	}

	public void assignValuesFromFieldArray(Object[] fieldsAsArray) {//receives array from gui and stores values
		for (int counter = 0; counter <= 21; counter ++) {

			if (fieldsAsArray[counter] == null) {
				//deprecated field (auto process/ disable images)
			} else if (fieldsAsArray[counter] instanceof JComboBox) { //get Jcombobox item

				fieldValuesAsArray[counter] = (String) ((JComboBox) fieldsAsArray[counter]).getSelectedItem();

			} else { //get jtext field text

				fieldValuesAsArray[counter] = (String) ((JTextField) fieldsAsArray[counter]).getText();

			}
		}

		System.out.println("Values assigned in order settings");
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
		fieldValuesAsArray = new String[22];
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
		fieldValuesAsArray[19] = proxyUser;
		fieldValuesAsArray[20] = proxyPass;
		fieldValuesAsArray[21] = checkoutType;
	}

	private void initializeValues() {//initializes values in array
		for (int i = 0; i< fieldValuesAsArray.length; i ++) {
			fieldValuesAsArray[i] = "";
		}
	}

}
