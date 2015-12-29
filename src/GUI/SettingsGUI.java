package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import backend.Encrypter;
import backend.OrderSettings;
import backend.SetCentered;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.Arrays;
import net.miginfocom.swing.MigLayout;

public class SettingsGUI extends JFrame {

	private static final long serialVersionUID = 6264764193908764903L;

	private Object[] fieldsAsArray;

	private JComboBox<String> storeOption;

	private JPanel contentPane;
	private final String r = "*";
	private final Color rC = Color.RED;

	//layouts
	private MigLayout addressPanelLayout;
	private MigLayout techPanelLayout;
	
	//address settings
	private JTextField nameField;
	private JTextField emailField;
	private JTextField phoneField;
	private JTextField address1Field;
	private JTextField postalCodeField;
	private JTextField cityField;
	private JTextField address2Field;
	private JTextField address3Field;
	private JTextField stateAbbrField;
	private JComboBox<String> countryField;
	private JComboBox<String> ccProviderField;
	private JTextField ccNumberField;
	private JComboBox<String> expMonthField;
	private JTextField expYearField;
	private JTextField cvvField;
	//address req's

	private JLabel address2Req;
	private JLabel address3Req;
	private JLabel stateAbbrReq;
	private JLabel ccNumberReq;
	private JLabel expMonthReq;
	private JLabel expYearReq;
	private JLabel cvvReq;
	// address labels

	private JLabel stateAbbrLabel;
	private JLabel cityLabel;
	private JLabel countryLabel;

	//technical settings
	//tech fields
	private JTextField proxyAddressField;
	private JTextField proxyPortField;
	private JTextField proxyUserField;
	private JTextField proxyPassField;
	private JFormattedTextField refreshRateField;
	private JComboBox<String> checkoutTypeField;

	private OrderSettings orderSettings;

	private final String[] checkoutTypeFieldOptions = {"Browserless (HTTP Mode)", "Browser (Firefox)"};
	private final String[] countriesUS = {"USA", "CANADA"};
	private final String[] countriesUK = {
			"UK",
			"UK (N. IRELAND)",
			"AUSTRIA",
			"BELARUS",
			"BELGIUM",
			"BULGARIA",
			"CROATIA",
			"CZECH REPUBLIC",
			"DENMARK",
			"ESTONIA",
			"FINLAND",
			"FRANCE",
			"GERMANY",
			"GREECE",
			"HUNGARY",
			"ICELAND",
			"IRELAND",
			"ITALY",
			"LATVIA",
			"LITHUANIA",
			"LUXEMBOURG",
			"MONACO",
			"NETHERLANDS",
			"NORWAY",
			"POLAND",
			"PORTUGAL",
			"ROMANIA",
			"RUSSIA",
			"SLOVAKIA",
			"SLOVENIA",
			"SPAIN",
			"SWEDEN",
			"SWITZERLAND",
	"TURKEY"};

	private final String[] countriesJP = {
			"北海道",
			"青森県",
			"岩手県",
			"宮城県",
			"秋田県",
			"山形県",
			"福島県",
			"茨城県",
			"栃木県",
			"群馬県",
			"埼玉県",
			"千葉県",
			"東京都",
			"神奈川県",
			"新潟県",
			"富山県",
			"石川県",
			"福井県",
			"山梨県",
			"長野県",
			"岐阜県",
			"静岡県",
			"愛知県",
			"三重県",
			"滋賀県",
			"京都府",
			"大阪府",
			"兵庫県",
			"奈良県",
			"和歌山県",
			"鳥取県",
			"島根県",
			"岡山県",
			"広島県",
			"山口県",
			"徳島県",
			"香川県",
			"愛媛県",
			"高知県",
			"福岡県",
			"佐賀県",
			"長崎県",
			"熊本県",
			"大分県",
			"宮崎県",
			"鹿児島県",
	"沖縄県"};

	private final String[] ccProvidersUS = {"Visa", "American Express", "Mastercard"};
	private final String[] ccProvidersUK = {"Visa", "American Express", "Mastercard", "Solo"};
	private final String[] ccProvidersJP = {"Visa", "American Express", "Mastercard", "代金引換"};

	private final String[] stores = {"US/CANADA", "UK", "JAPAN"};

	private final String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

	private String currentStore = "US"; //US by default

	public SettingsGUI(OrderSettings settings, int orderNumber, final Encrypter encrypter) {
		this.orderSettings = settings;
		setTitle("Order "+orderNumber+" Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 690, 414);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));


		initializeFields(); //initializes texfields and combo boxes whih are private
		initializeLabels(); //initializes the labels which are private
		initializeReqs(); //initializes and sets color or reqs which are private
		initializeTechFields();
		setReqsUS(); //sets reqs to US on launch, combo box is also on US
		setNamesUS();// set the labels names to their US fields


		JPanel addressPanel = new JPanel();

		JPanel loadCheckoutInfoButtonPanel = new JPanel();
		JPanel saveCheckoutInfoButtonPanel = new JPanel();

		JButton loadCheckoutInfoButton = new JButton("Load Checkout Info From File");
		loadCheckoutInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Load Checkout Info From File");
				new LoadOrSaveGUI("load", encrypter);
			}
		});

		JButton saveCheckoutInfoButton = new JButton("Save Checkout Info To File");
		saveCheckoutInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Checkout Info To File");
				new LoadOrSaveGUI("save", encrypter);
			}
		});

		loadCheckoutInfoButtonPanel.add(loadCheckoutInfoButton);
		saveCheckoutInfoButtonPanel.add(saveCheckoutInfoButton);


		JPanel loadOrSaveCheckoutInfoButtonsPanel = new JPanel();
		loadOrSaveCheckoutInfoButtonsPanel.setLayout(new BorderLayout(0,0));

		loadOrSaveCheckoutInfoButtonsPanel.add(loadCheckoutInfoButtonPanel, BorderLayout.NORTH);
		loadOrSaveCheckoutInfoButtonsPanel.add(saveCheckoutInfoButtonPanel, BorderLayout.SOUTH);

		JPanel addressPanelHolder = new JPanel();
		addressPanelHolder.setLayout(new BorderLayout(0,0));
		addressPanelHolder.add(addressPanel, BorderLayout.NORTH);
		addressPanelHolder.add(loadOrSaveCheckoutInfoButtonsPanel, BorderLayout.SOUTH);


		JButton applyButton = new JButton("Apply and Exit");
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndExit();

			}

		});

		JButton exitButton = new JButton("Cancel");
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel applyOrExitButtonPanel = new JPanel();
		applyOrExitButtonPanel.setLayout(new BorderLayout(0,0));
		applyOrExitButtonPanel.add(applyButton, BorderLayout.NORTH);
		applyOrExitButtonPanel.add(exitButton, BorderLayout.SOUTH);

		JPanel techPanel = new JPanel();

		JPanel mainPanelHolder = new JPanel();
		mainPanelHolder.setLayout(new BorderLayout(0,0));
		mainPanelHolder.add(techPanel, BorderLayout.EAST);
		mainPanelHolder.add(addressPanelHolder, BorderLayout.WEST);
		mainPanelHolder.add(applyOrExitButtonPanel, BorderLayout.SOUTH);

		contentPane.add(mainPanelHolder);

		techPanelLayout = new MigLayout("wrap 2");
		addressPanelLayout = new MigLayout("wrap 3", "[][][17.00]");
		
		techPanel.setLayout(techPanelLayout);
		addressPanel.setLayout(addressPanelLayout);
		JLabel addressSettingsHeader = new JLabel("Address Settings:");
		//		addressSettingsHeader.setHorizontalAlignment(1); //why wont this align the header label!!!!!!
		addressPanel.add(addressSettingsHeader, "span");
		addressPanel.add(Box.createRigidArea(new Dimension(0,10)), "span"); //spacer under header

		countryField.setModel(new DefaultComboBoxModel<String>(countriesUS));
		JLabel countryReq = new JLabel(r);
		countryReq.setForeground(rC);
		addressPanel.add(countryLabel);
		addressPanel.add(countryField);
		addressPanel.add(countryReq);

		JLabel nameLabel = new JLabel("Full Name:");
		JLabel nameReq = new JLabel(r);
		nameReq.setForeground(rC);
		addressPanel.add(nameLabel);
		addressPanel.add(nameField);
		addressPanel.add(nameReq);

		JLabel emailLabel = new JLabel("Email:");
		JLabel emailReq = new JLabel(r);
		emailReq.setForeground(rC);
		addressPanel.add(emailLabel);
		addressPanel.add(emailField);
		addressPanel.add(emailReq);


		JLabel phoneLabel = new JLabel("Phone:");
		JLabel phoneReq = new JLabel(r);
		phoneReq.setForeground(rC);
		addressPanel.add(phoneLabel);
		addressPanel.add(phoneField);
		addressPanel.add(phoneReq);

		JLabel address1Label = new JLabel("Address:");
		JLabel address1Req = new JLabel(r);
		address1Req.setForeground(rC);
		addressPanel.add(address1Label);
		addressPanel.add(address1Field);
		addressPanel.add(address1Req);

		JLabel address2Label = new JLabel("Address 2:");
		addressPanel.add(address2Label);
		addressPanel.add(address2Field);
		addressPanel.add(address2Req);

		JLabel address3Label = new JLabel("Address 3:");
		addressPanel.add(address3Label);
		addressPanel.add(address3Field);
		addressPanel.add(address3Req);


		JLabel postalCodeReq = new JLabel(r);
		postalCodeReq.setForeground(rC);
		JLabel postalCodeLabel = new JLabel("Postal Code:");
		addressPanel.add(postalCodeLabel);
		addressPanel.add(postalCodeField);
		addressPanel.add(postalCodeReq);

		JLabel cityReq = new JLabel(r);
		cityReq.setForeground(rC);
		addressPanel.add(cityLabel);
		addressPanel.add(cityField);
		addressPanel.add(cityReq);

		addressPanel.add(stateAbbrLabel);	
		addressPanel.add(stateAbbrField);	
		addressPanel.add(stateAbbrReq);

		JLabel ccProviderLabel = new JLabel("Credit Card Provider:");
		ccProviderField.setModel(new DefaultComboBoxModel<String>(ccProvidersUS));
		JLabel ccProviderReq = new JLabel(r);
		ccProviderReq.setForeground(rC);
		addressPanel.add(ccProviderLabel);	
		addressPanel.add(ccProviderField);	
		addressPanel.add(ccProviderReq);


		JLabel ccNumberLabel = new JLabel("Credit Card Number:");
		addressPanel.add(ccNumberLabel);	
		addressPanel.add(ccNumberField);	
		addressPanel.add(ccNumberReq);

		JLabel expMonthLabel = new JLabel("Expiration Month:");
		addressPanel.add(expMonthLabel);	
		addressPanel.add(expMonthField);	
		addressPanel.add(expMonthReq);

		JLabel expYearLabel = new JLabel("Expiration Year:");
		addressPanel.add(expYearLabel);	
		addressPanel.add(expYearField);	
		addressPanel.add(expYearReq);

		JLabel cvvLabel = new JLabel("CVV:");
		addressPanel.add(cvvLabel);	
		addressPanel.add(cvvField);	
		addressPanel.add(cvvReq);

		JPanel storePanel = new JPanel();
		contentPane.add(storePanel, BorderLayout.NORTH);

		storeOption = new JComboBox<String>(stores);
		storePanel.add(new JLabel("Store:"), BorderLayout.WEST);
		storePanel.add(storeOption, BorderLayout.EAST);
		storeOption.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!storeOption.getSelectedItem().equals(currentStore)) {
					if (storeOption.getSelectedItem().toString().contains("US")) {
						setNamesUS();
						setReqsUS();
					} else if (storeOption.getSelectedItem().equals("UK")) {
						setNamesUK();
						setReqsUK();
					} else {
						setNamesJP();
						setReqsJP();
					}
					setCheckoutPayOnDeliveryStatus(true, r); //just in case pay on delivery was selected, fix it
					currentStore = storeOption.getSelectedItem().toString();
					System.out.println("current store: "+currentStore);
				}

			}
		});
		JLabel proxyHostAddressLabel = new JLabel("Proxy Address:");
		JLabel proxyUserLabel = new JLabel("Username:");
		JLabel proxyPassLabel = new JLabel("Password:");

		JLabel proxyPortLabel = new JLabel("Proxy Port:");



		techPanel.add(new JLabel("Proxy Settings:"), "span");
		techPanel.add(Box.createRigidArea(new Dimension(0,10)), "span"); //spacer under header
		techPanel.add(proxyHostAddressLabel);
		techPanel.add(proxyAddressField);

		techPanel.add(proxyPortLabel);
		techPanel.add(proxyPortField);

		techPanel.add(proxyUserLabel);
		techPanel.add(proxyUserField);

		techPanel.add(proxyPassLabel);
		techPanel.add(proxyPassField);

		JLabel refreshRateLabel = new JLabel("Refresh Rate (milliseconds):");

		JLabel checkoutTypeLabel = new JLabel("Checkout Type:");

		techPanel.add(Box.createRigidArea(new Dimension(0,10)), "span"); //spacer under proxy area
		techPanel.add(new JLabel("Checkout Settings:"), "span");
		techPanel.add(Box.createRigidArea(new Dimension(0,10)), "span"); //spacer under header
		techPanel.add(refreshRateLabel);
		techPanel.add(refreshRateField);
		techPanel.add(checkoutTypeLabel);
		techPanel.add(checkoutTypeField);

		buildFieldsAsArray(); //initialize the array used in the checkout profiles and order settings
		setFieldsAccordingToOrderSettings(); //gets order settings object values and fills out form with them

		pack();
		new SetCentered(this);
		setVisible(true);
	}

	private void buildFieldsAsArray() {
		fieldsAsArray = new Object[23];
		fieldsAsArray[0] = nameField;
		fieldsAsArray[1] = emailField;
		fieldsAsArray[2] = phoneField;
		fieldsAsArray[3] = address1Field;
		fieldsAsArray[4] = address2Field;
		fieldsAsArray[5] = postalCodeField;
		fieldsAsArray[6] = cityField;
		fieldsAsArray[7] = stateAbbrField;
		fieldsAsArray[8] = countryField;
		fieldsAsArray[9] = ccProviderField;
		fieldsAsArray[10] = ccNumberField;
		fieldsAsArray[11] = expMonthField;
		fieldsAsArray[12] = expYearField;
		fieldsAsArray[13] = cvvField;
		fieldsAsArray[14]= refreshRateField;
		fieldsAsArray[15] = null; //these are from the old checkout profiles (autoproccess/ disable images are deprecated)
		fieldsAsArray[16] = null; //these are from the old checkout profiles (autoproccess/ disable images are deprecated)
		fieldsAsArray[17] = address3Field;
		fieldsAsArray[18] = proxyAddressField;
		fieldsAsArray[19] = proxyPortField;
		fieldsAsArray[20] = proxyUserField;
		fieldsAsArray[21] = proxyPassField;
		fieldsAsArray[22] = checkoutTypeField;

	}

	private void setReqsJP() {
		//iterate through reqs setting them to "" or r ("*")

		address2Req.setText(null);
		address3Req.setText(null);
		stateAbbrReq.setText(null);
		ccNumberReq.setText(r);
		expMonthReq.setText(r);
		expYearReq.setText(r);
		cvvReq.setText(r);

		address2Field.setEnabled(false);
		address3Field.setEnabled(false);
		stateAbbrField.setEnabled(false);
		ccNumberReq.setEnabled(true);
		expMonthReq.setEnabled(true);
		expYearReq.setEnabled(true);
		cvvReq.setEnabled(true);
		System.out.println("Reqs set to JP");
	}

	public void setReqsUK() {
		//iterate through reqs setting them to "" or r ("*")

		address2Req.setText(null);
		address3Req.setText(null);
		stateAbbrReq.setText(null);
		ccNumberReq.setText(r);
		expMonthReq.setText(r);
		expYearReq.setText(r);
		cvvReq.setText(r);

		address2Field.setEnabled(true);
		address3Field.setEnabled(true);
		stateAbbrField.setEnabled(false);
		ccNumberReq.setEnabled(true);
		expMonthReq.setEnabled(true);
		expYearReq.setEnabled(true);
		cvvReq.setEnabled(true);

		System.out.println("Reqs set to UK");
	}

	private void setReqsUS() {
		//iterate through reqs setting them to "" or r ("*")

		address2Req.setText(null);
		address3Req.setText(null);
		stateAbbrReq.setText(r);
		ccNumberReq.setText(r);
		expMonthReq.setText(r);
		expYearReq.setText(r);
		cvvReq.setText(r);

		address2Field.setEnabled(true);
		address3Field.setEnabled(false);
		stateAbbrField.setEnabled(true);
		ccNumberReq.setEnabled(true);
		expMonthReq.setEnabled(true);
		expYearReq.setEnabled(true);
		cvvReq.setEnabled(true);

		System.out.println("Reqs set to US");
	}

	private void setNamesUS() {
		//sets labels and comboboxes to US
		stateAbbrLabel.setText("State Abbreviation (2 letters):");
		cityLabel.setText("City:");
		countryLabel.setText("Country:");
		countryField.setModel(new DefaultComboBoxModel<String>(countriesUS));
		ccProviderField.setModel(new DefaultComboBoxModel<String>(ccProvidersUS));
		System.out.println("Names set to US");
	}

	private void setNamesUK() {
		//sets labels and comboboxes to UK
		cityLabel.setText("City:");
		countryLabel.setText("Country:");
		countryField.setModel(new DefaultComboBoxModel<String>(countriesUK));
		ccProviderField.setModel(new DefaultComboBoxModel<String>(ccProvidersUK));
		System.out.println("Names set to UK");
	}

	private void setNamesJP() {
		//sets labels and comboboxes to JP

		stateAbbrLabel.setText("State Abbreviation (2 letters):"); //set just to prevent municipality from showing twice
		countryLabel.setText("Prefecture:");
		cityLabel.setText("Municipality:");
		countryField.setModel(new DefaultComboBoxModel<String>(countriesJP));
		ccProviderField.setModel(new DefaultComboBoxModel<String>(ccProvidersJP));
		System.out.println("Names set to JP");
	}

	private void initializeLabels() {

		cityLabel = new JLabel();
		countryLabel = new JLabel();
		stateAbbrLabel = new JLabel();
	}

	private void initializeTechFields() {
		proxyAddressField = new JTextField();
		proxyAddressField.setColumns(10);

		proxyPortField = new JTextField();
		proxyPortField.setColumns(10);

		proxyUserField = new JTextField();
		proxyUserField.setColumns(10);

		proxyPassField = new JTextField();
		proxyPassField.setColumns(10);

		NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
		refreshRateField = new JFormattedTextField(integerFieldFormatter);
		integerFieldFormatter.setMaximumFractionDigits(0);
		refreshRateField.setColumns(10);

		checkoutTypeField = new JComboBox<String>();
		checkoutTypeField.setModel(new DefaultComboBoxModel<String>(checkoutTypeFieldOptions));

	}

	private void initializeReqs() {
		//initializes public reqs
		address2Req = new JLabel();
		address3Req = new JLabel();
		stateAbbrReq = new JLabel();
		ccNumberReq = new JLabel();
		expMonthReq = new JLabel();
		expYearReq = new JLabel();
		cvvReq = new JLabel();

		address2Req.setForeground(rC);
		address3Req.setForeground(rC);
		stateAbbrReq.setForeground(rC);
		ccNumberReq.setForeground(rC);
		expMonthReq.setForeground(rC);
		expYearReq.setForeground(rC);
		cvvReq.setForeground(rC);
	}

	private void initializeFields() {
		//initializes public fields
		nameField = new JTextField();
		nameField.setColumns(10);

		emailField = new JTextField();
		emailField.setColumns(10);

		phoneField = new JTextField();
		phoneField.setColumns(10);

		address1Field = new JTextField();
		address1Field.setColumns(10);

		postalCodeField = new JTextField();
		postalCodeField.setColumns(10);

		cityField = new JTextField();
		cityField.setColumns(10);

		address2Field = new JTextField();
		address2Field.setColumns(10);

		address3Field = new JTextField();
		address3Field.setColumns(10);

		stateAbbrField = new JTextField();
		stateAbbrField.setColumns(10);

		countryField = new JComboBox<String>();
		countryField.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (countryField.getSelectedItem().equals("CANADA")) {
					stateAbbrLabel.setText("Municipality:");
					System.out.println("Canada so municipality");
				} else if (countryField.getSelectedItem().equals("USA")) {
					stateAbbrLabel.setText("State Abbreviation (2 letters):");
					System.out.println("USA so state");
				}

			}
		});

		ccProviderField = new JComboBox<String>();
		ccProviderField.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ccProviderField.getSelectedItem().equals("代金引換")) {
					setCheckoutPayOnDeliveryStatus(false, null);
				} else {
					setCheckoutPayOnDeliveryStatus(true, r);
				}
			}
		});

		ccNumberField = new JTextField();
		ccNumberField.setColumns(10);

		ccNumberField = new JTextField();
		ccNumberField.setColumns(10);

		ccNumberField = new JTextField();
		ccNumberField.setColumns(10);

		ccNumberField = new JTextField();
		ccNumberField.setColumns(10);

		expMonthField = new JComboBox<String>(months);

		expYearField = new JTextField();
		expYearField.setColumns(10);

		cvvField = new JTextField();
		cvvField.setColumns(10);

	}

	private void setCheckoutPayOnDeliveryStatus(boolean status, String reqStatus) {
		//does or undoes japan pay on delivery
		ccNumberField.setEnabled(status);
		ccNumberReq.setText(reqStatus);
		expMonthReq.setText(reqStatus);
		expMonthField.setEnabled(status);
		expYearReq.setText(reqStatus);
		expYearField.setEnabled(status);
		cvvField.setEnabled(status);
		cvvReq.setText(reqStatus);

	}

	public String pullFields() { //pulls fields from settings gui for encrytion then write to file
		String returnString = "";
		for (int counter = 0; counter < fieldsAsArray.length; counter ++) {

			if (fieldsAsArray[counter] == null) {
				//deprecated field (auto process/ disable images)
			} else if (fieldsAsArray[counter] instanceof JComboBox) { //get Jcombobox item

				returnString = returnString.concat((String) ((JComboBox<?>) fieldsAsArray[counter]).getSelectedItem());

			} else { //get jtext field text

				returnString = returnString.concat(((JTextField) fieldsAsArray[counter]).getText());

			}

			if (counter != 22) { //this must be the array length - 1
				returnString = returnString.concat("\n");
			}
		}

		System.out.println("Checkout File Before Encryption:\n" + returnString);
		return returnString;
	}

	public void updateGUI(String newContent) throws HeadlessException, IOException {
		//errors handled by try catch in processLoad method of LoadOrSaveGUI
		System.out.println(newContent);
		BufferedReader bufReader = new BufferedReader(new StringReader(newContent));
		iterateThroughCheckoutProfile(bufReader);

	}

	private void iterateThroughCheckoutProfile(BufferedReader reader) throws HeadlessException, IOException {//sets the appropriate field for each line of the profile
		//errors handled by try catch in processLoad method of LoadOrSaveGUI
		String line;
		int counter = 0;

		while((line = reader.readLine()) != null) {	//iterate through file setting fields to their respective lines

			if (fieldsAsArray[counter] == null) {
				//deprecated field (auto process/ disable images)

			} else if (line.isEmpty()) {
				//blank line was skipped
				System.out.println("Line was blank, field not manipulated");
			} else if (fieldsAsArray[counter] instanceof JComboBox) { //set Jcombobox item

				if (counter == 8) triggerStoreOptionActionListener(line); //sets ui to correct store based on the country, allowing the country to be selected properly (if this didnt happen the country field would have the wrong options and the country would always be US)
		
				((JComboBox<?>) fieldsAsArray[counter]).setSelectedItem(line);

			} else { //set jtext field text

				((JTextField) fieldsAsArray[counter]).setText(line);
				
			}
			counter++;
		}

		triggerOddballActionListeners(); //triggers cc provider and country action listeners, in case the checkout form requires changes which those listeners account for
	}

	private void saveAndExit() {
			this.orderSettings.assignValuesFromFieldArray(fieldsAsArray);
			dispose();
	}

	private void setFieldsAccordingToOrderSettings() {//gets order settings fields array and converts it to a checkout profile
		Object[] formValuesFromOrderSettings = this.orderSettings.getFieldValuesAsArray();
		String valuesAsCheckoutFile = "";
		for (Object values : formValuesFromOrderSettings) {
			valuesAsCheckoutFile = valuesAsCheckoutFile.concat(values+"\n");
		}


		System.out.println("\nFields from Order Settings:\n"+valuesAsCheckoutFile);
		BufferedReader reader = new BufferedReader(new StringReader(valuesAsCheckoutFile));

		try { //this should never ever throw an error, java just requires the try catch
			iterateThroughCheckoutProfile(reader);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void triggerStoreOptionActionListener(String line) { //sets store to right value (this block is needed because old checkout profiles didnt have store in them, and it cant be added to the end because the store needs to be set early so that the ccprovider and country fields have the right models
		if (Arrays.asList(countriesUS).contains(line)) {
			storeOption.setSelectedItem("US/CANADA");
		} else if (Arrays.asList(countriesJP).contains(line)) {
			storeOption.setSelectedItem("JAPAN");
		} else if (Arrays.asList(countriesUK).contains(line)) {
			storeOption.setSelectedItem("UK");
		}
		ActionListener[] action = storeOption.getActionListeners();
		action[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)); //trigger store change
	}

	private void triggerOddballActionListeners() {//triggers cc provider and country action listeners, in case the checkout form requires changes which those listeners account for
		ActionListener[] action = ccProviderField.getActionListeners();
		ActionListener[] action1 = countryField.getActionListeners();
		action[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		action1[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

	}

}

