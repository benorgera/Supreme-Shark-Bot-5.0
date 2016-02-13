package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import executor.Dispatcher;
import backend.ButtonColumn;
import backend.Encrypter;
import backend.Item;
import backend.Order;
import backend.ProxyTester;
import backend.SetCentered;
import backend.Main;

import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

@SuppressWarnings("restriction")
public class GUI extends JFrame {

	private Date enableDate;
	
	private static final long serialVersionUID = -2271100967580465591L;
	private static boolean isPro; //true if pro
	private JPanel contentPane;
	private JTabbedPane orderTabHolder;
	private int orderCount = 0;
	private JTextArea textConsoleArea; //text console, reached by textConsoleNewLine
	private JButton enableBotButton;

	private final String[] comboBoxOptions = {"jackets", "shirts", "tops-sweaters", "sweatshirts", "pants", "t-shirts", "hats", "bags", "accessories", "skate", "shoes", "shorts"};
	private final String[] headers = {"Keywords", "Category", "Colors", "Size", "Early Link", "Status", "Actions"};
	private final String[] newItemRow =  {"", "", "", "", "", "", "Delete Item"};
	private final String[] newItemButtonRow =  {"", "", "", "", "", "", "+"};
	private TabChangeListener tabChange;
	
	private boolean techMessagesEnabled = false;

//	private WebView webView;

	private JLabel scheduledDateLabel = new JLabel(); //blank unless scheduler enabled

	public GUI(boolean isPro, double thisVersionNumber) {
		GUI.isPro = isPro;
		setTitle("Supreme Shark Bot " + Double.toString(thisVersionNumber) + (isPro ? " Pro" : ""));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		Action launchHelpAction = new AbstractAction() {

			private static final long serialVersionUID = -8782259835901421375L;

			@Override
			public void actionPerformed(ActionEvent e) {
				launchHelp();

			}
		};

		Action launchSchedulerAction = new AbstractAction() {

			private static final long serialVersionUID = 4131971390457695810L;

			@Override
			public void actionPerformed(ActionEvent e) {
				launchScheduler();
			}
		};

		JPanel orderTabHolderHolder = new JPanel();
		orderTabHolderHolder.setLayout(new BorderLayout());
		contentPane.add(orderTabHolderHolder, BorderLayout.CENTER);

		orderTabHolder = new JTabbedPane(JTabbedPane.TOP);
		orderTabHolderHolder.add(orderTabHolder, BorderLayout.CENTER);

		JPanel deactivateAndEnableButtonsPanel = new JPanel();
		deactivateAndEnableButtonsPanel.setLayout(new BorderLayout());

		enableBotButton = new JButton("Enable Bot");

		Action enableAction = new AbstractAction() {

			private static final long serialVersionUID = 4097381048444547842L;

			@Override
			public void actionPerformed(ActionEvent e) {
				processEnableOrAbort();
			}
		};
		enableBotButton.addActionListener(enableAction);

		JPanel testAndDeactivatePanel = new JPanel(new BorderLayout());
		JButton testProxies = new JButton("Test Proxies");

		Action testProxiesAction = new AbstractAction() {

			private static final long serialVersionUID = 4097381048444547842L;

			@Override
			public void actionPerformed(ActionEvent e) {
				textConsoleNewLine("\nProxy test started");

				for (Order o : Main.getOrders()) new Thread(new ProxyTester(o, textConsoleArea)).start();

			}
		};
		testProxies.addActionListener(testProxiesAction);

		JButton deactivateLicense = new JButton("Deactivate License");
		testAndDeactivatePanel.add(deactivateLicense, BorderLayout.WEST);
		testAndDeactivatePanel.add(testProxies, BorderLayout.EAST);

		
		Action deactivateLicenseAction = new AbstractAction() {

			private static final long serialVersionUID = 1774086347779837678L;

			@Override
			public void actionPerformed(ActionEvent e) {
				processDeactivate();
			}

		};
		deactivateLicense.addActionListener(deactivateLicenseAction);

		deactivateAndEnableButtonsPanel.add(testAndDeactivatePanel, BorderLayout.WEST);
		deactivateAndEnableButtonsPanel.add(enableBotButton, BorderLayout.EAST);

		orderTabHolder.addChangeListener(tabChange = new TabChangeListener());

		JPanel splitPaneHolder = new JPanel();
		splitPaneHolder.setLayout(new BorderLayout());

		JSplitPane splitPane = new JSplitPane();
		splitPaneHolder.add(splitPane, BorderLayout.NORTH);
		splitPaneHolder.add(deactivateAndEnableButtonsPanel, BorderLayout.SOUTH);

		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPaneHolder, BorderLayout.SOUTH);

		JPanel textConsolePanel = new JPanel();

		JPanel htmlConsolePanel = new JPanel();

//		JFXPanel jfxPanel = new JFXPanel();

		htmlConsolePanel.setPreferredSize(new Dimension(100, 150));

//		Platform.runLater(() -> {
//			webView = new WebView();
//			webView.setZoom(.5);
//			jfxPanel.setScene(new Scene(webView));
//			webView.getEngine().load("http://www.supremenewyork.com/shop/all");
//			//		    webView.setDisable(true); //make it read only
//		});


		splitPane.setLeftComponent(textConsolePanel);
		textConsolePanel.setLayout(new BorderLayout());

		splitPane.setRightComponent(htmlConsolePanel);
		htmlConsolePanel.setLayout(new BorderLayout());

		JScrollPane textConsoleScroller = new JScrollPane();
		textConsolePanel.add(textConsoleScroller, BorderLayout.CENTER);

//		htmlConsolePanel.add(jfxPanel, BorderLayout.CENTER);


		JLabel textConsole = new JLabel("Text Console:");
		textConsolePanel.add(textConsole, BorderLayout.NORTH);
		textConsole.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel clearConsoleButtonPanel = new JPanel(new BorderLayout());

		JButton clearConsoleButton = new JButton("Clear Text Console");
		
		JCheckBox enableTechMessagesCheckbox = new JCheckBox("Enable Technical Messages");
		
		clearConsoleButtonPanel.add(enableTechMessagesCheckbox, BorderLayout.WEST);

		clearConsoleButtonPanel.add(clearConsoleButton, BorderLayout.EAST);

		Action clearConsole = new AbstractAction() {

			private static final long serialVersionUID = -573938828841820363L;

			@Override
			public void actionPerformed(ActionEvent e) {
				textConsoleArea.setText(null);

			}
		};

		clearConsoleButton.addActionListener(clearConsole);
		
		Action enableTechMessages = new AbstractAction() {

			private static final long serialVersionUID = 3515543695745849483L;

			@Override
			public void actionPerformed(ActionEvent e) {
				techMessagesEnabled = ((JCheckBox) e.getSource()).isSelected();
			}
			
		};
		
		enableTechMessagesCheckbox.addActionListener(enableTechMessages);

		textConsolePanel.add(clearConsoleButtonPanel, BorderLayout.SOUTH);


		JLabel htmlConsole = new JLabel("HTML Console:");
		htmlConsolePanel.add(htmlConsole, BorderLayout.NORTH);
		htmlConsole.setHorizontalAlignment(SwingConstants.CENTER);

		textConsoleArea = new JTextArea();
		textConsoleArea.setRows(8);
		textConsoleArea.setEditable(false);


		//makes textConsoleArea  always scroll to bottom
		DefaultCaret caret = (DefaultCaret)textConsoleArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		textConsoleScroller.setViewportView(textConsoleArea);

		JPanel logoPanel = new JPanel();
		contentPane.add(logoPanel, BorderLayout.NORTH);


		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new BorderLayout());


		JPanel schedulerHolderPanel = new JPanel();
		schedulerHolderPanel.setLayout(new BorderLayout());
		scheduledDateLabel.setVisible(false);

		JButton schedulerButton = new JButton("Scheduler Settings");
		schedulerButton.addActionListener(launchSchedulerAction);

		schedulerHolderPanel.add(schedulerButton, BorderLayout.EAST);
		schedulerHolderPanel.add(scheduledDateLabel, BorderLayout.WEST);

		topButtonPanel.add(schedulerHolderPanel, BorderLayout.EAST);
		contentPane.add(topButtonPanel, BorderLayout.NORTH);

		JButton helpButton = new JButton("Launch Help");

		topButtonPanel.add(helpButton, BorderLayout.WEST);
		helpButton.addActionListener(launchHelpAction);
		setSize(new Dimension(1050, 518));
		new SetCentered(this);

	}

	public Object[] newOrder() {
		//	called by order to add new order to GUI
		System.out.println("Start New Order");
		orderCount++;
		final MyDefaultTableModel model = new MyDefaultTableModel(headers,0); //custom table model which enable only certain cells be editable

		final JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setSelectionBackground(new Color(238,245,252));
		table.setSelectionForeground(Color.BLACK);
		table.setDefaultRenderer(Object.class, new MyDefaultTableCellRenderer());
		table.getTableHeader().setReorderingAllowed(false);

		JPanel orderPanel = new JPanel();
		orderPanel.setLayout(new BorderLayout());

		JScrollPane tableHolderScrollPane = new JScrollPane();
		JPanel tableHolder = new JPanel();

		Action deleteOrderAction = new AbstractAction() {

			private static final long serialVersionUID = -7603248098132965886L;

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteOrder();

			}
		};

		tableHolder.setLayout(new BorderLayout());
		JButton deleteOrderButton = new JButton("Delete Order "+orderCount);
		deleteOrderButton.addActionListener(deleteOrderAction);


		JPanel buttonPanelHolder = new JPanel();//this block makes the delete order goto the southeast
		buttonPanelHolder.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(deleteOrderButton);
		buttonPanelHolder.add(buttonPanel, BorderLayout.EAST);

		JPanel orderSettingsButtonHolder = new JPanel();


		Action launchOrderSettings = new AbstractAction() {

			private static final long serialVersionUID = -573938828841820363L;

			@Override
			public void actionPerformed(ActionEvent e) {
				launchOrderSettings();
			}
		};


		JButton orderSettings = new JButton("Order " + orderCount + " Settings");
		orderSettings.addActionListener(launchOrderSettings);
		orderSettingsButtonHolder.add(orderSettings);
		buttonPanelHolder.add(orderSettingsButtonHolder, BorderLayout.WEST);
		orderPanel.add(buttonPanelHolder, BorderLayout.SOUTH);


		tableHolder.add(table.getTableHeader(), BorderLayout.NORTH);
		tableHolder.add(table, BorderLayout.CENTER);

		tableHolderScrollPane.setViewportView(tableHolder);

		orderPanel.add(tableHolderScrollPane, BorderLayout.CENTER);
		orderTabHolder.addTab("Order "+orderCount, orderPanel);

		for (int i = 0; i < orderTabHolder.getTabCount(); i ++) if (getTabAsString(i, null).equals("+")) orderTabHolder.removeTabAt(i); //removes tab to add tab if new order being added
			
		orderTabHolder.addTab("+", null);

		Action deleteOrAdd = new AbstractAction() {

			private static final long serialVersionUID = -7584511112190398057L;

			public void actionPerformed(ActionEvent e) {
				String buttonText = (String) ((JTable)e.getSource()).getValueAt(Integer.valueOf(e.getActionCommand()), 6); //check if the row that was clicked was a delete or an addition
				if (buttonText.contains("Delete") && ((DefaultTableModel) table.getModel()).getRowCount() != 2) {
					//deletes row whose delete button was pressed
					if (confirmAction("Item", null) == 0) { //shows JOptionPane
						((DefaultTableModel) table.getModel()).removeRow(Integer.valueOf(e.getActionCommand()));
						setAllButOneUneditable(table.getModel().getRowCount() - 1, model); //makes add new item row uneditable after delete item
					}
				} else if (((DefaultTableModel) table.getModel()).getRowCount() == 2 && buttonText.contains("Delete")) { //theyre trying to delete the only item
					JOptionPane.showMessageDialog(null, "Item 1 cannot be deleted, only 1 item exists!");
				} else { //they're adding an item
					addItem();
				}
			}
		};


		new ButtonColumn(table, deleteOrAdd, 6); //makes actions column a button column

		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<String>(comboBoxOptions)));

		model.addRow(newItemRow);
		setAllEditable(model.getRowCount() - 1, model);  //allows button to be pressed and new item to be edited

		addNewItemButtonRow(model); //adds new '+' row

		System.out.println("Order " + orderCount + " added");
		repaint();
		return new Object[]{orderCount, table, model, deleteOrderButton, orderSettings}; //informs order what number it is, and tells it the data
	}

	private void addItem() {
		MyDefaultTableModel model = Main.getOrders().get(getTabAsInt(null, null) - 1).getModel(); //get selected tab of pane and get its order and that orders tablemodel
		int rows = model.getRowCount();
		
		if (rows >= 5 && !isPro) { //num is 5 because there's the row with the '+' button
			JOptionPane.showMessageDialog(null, "Limit of 4 items reached, you must upgrade to pro for infinite items");
		} else {
			model.addRow(newItemRow); 
			setAllEditable(rows - 1, model);
			model.removeRow(rows - 1); //remove old '+' row
			addNewItemButtonRow(model); //adds new '+' row
		}
	}

	private void setAllButOneUneditable(int row, MyDefaultTableModel model) {
		//makes add new item row uneditable after delete item
		for (int i = 0; i <= 4; i++) model.setCellEditable(row, i, false);
		
	}

	private void setAllEditable(int row, MyDefaultTableModel model) {

		//sets every column in a row editable, except status column, called following new order or new item
		System.out.println("Setting all columns editable for row " + row);
		for (int i = 0; i <= 6; i++) model.setCellEditable(row, i, !(i == 5));
		
	}

	private void addNewItemButtonRow(MyDefaultTableModel model) {
		//called after newOrder or new item, adds new '+' row
		int rows = model.getRowCount(); //get row count so you know what row to make editable
		model.addRow(newItemButtonRow); //adds button that can add an item
		model.setCellEditable(rows, 6, true); //you can click the plus to add an item
	}

	private void launchHelp() {//launches tutorial in default browser
		String url = "http://www.supremesharkbot.com:8080/tutorial.pdf";
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Launched Help");
	}

	private void launchOrderSettings() {
		//instantiate new order settings panel
		System.out.println("Launch Order Settings");
		SettingsGUI gui; 
		Encrypter encrypter = new Encrypter(Main.getActivationKey());

		try {
			encrypter.SetupEncrypter();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Encrypter initialize failed, user won't be warned unless they try and encrypt or decrypt");
		}

		gui = new SettingsGUI(Main.getOrders().get(getTabAsInt(null, null) - 1).getOrderSettings(), getTabAsInt(null, null), encrypter); //settings gui passed the encrypter which it will pass to the load or save gui
		System.out.println("GUI passed to Encrypter");
		encrypter.setGUI(gui); //encrypter passed the gui which it will pass to the load or save GUI
		System.out.println("Encrypter passed to GUI");
	}

	private void launchScheduler() {
		//instantiate new scheduler settings panel
		System.out.println("Launched Scheduler");
		new SchedulerGUI();
	}

	private void deleteOrder() {
		int order = getTabAsInt(null, null);
		
		if (order == 1 && orderTabHolder.getTabCount() == 2) { //only one order tab in tabbed pane
			JOptionPane.showMessageDialog(null, "Order 1 cannot be deleted, only 1 order exists!");
			return;
		}
		
		System.out.println("Delete order " + order + " pending");
		
		if (confirmAction("Order", order) == 0) { // show the joptionpane
			//delete the order and set back the order count
			orderTabHolder.removeChangeListener(tabChange); //remove tab listener so new order isnt addde if the + tab is selected once the previously selected tab dissppears
			orderTabHolder.remove(order - 1); //remove order from gui
			Main.removeFromOrderList(order - 1); //remove order from orders arraylist
			editOrderObjects(); //resets order numbers and buttons in order objects array following delete

			if (getTabAsString(null, null).equals("+")) orderTabHolder.setSelectedIndex(orderTabHolder.getTabCount() - 2); //deselect + tab if its selected following deletion
			
			orderTabHolder.addChangeListener(tabChange); //re-add the change listener now that + deselected
			orderCount--; //drop the order count

		}
	}

	private void editOrderObjects() { //resets order numbers and buttons in order objects array following delete
		int prev = 1;
		for (int i = 0; i < orderTabHolder.getTabCount() - 1; i ++) {
			if (getTabAsInt(i, null) != prev) {
				orderTabHolder.setTitleAt(i, "Order " + prev); //reset tab names
				Main.getOrders().get(i).setDeleteButtonText("Delete Order " + prev); //reset delete button
				Main.getOrders().get(i).setSettingsButtonText("Order " + prev + "Settings"); //reset settings button
				Main.getOrders().get(i).setOrderNum(prev); //reset order numbers
			}
			prev++;
		}
	}

	private int confirmAction(String type, Integer order) { //calls prompt which prompts, called when order or item deleted
		String message = order == null ? "delete the selected item?" : "delete Order " + order + "?";

		return prompt("Are you sure you want to " + message, "Confirm " + type + " Deletion");
	}

	private int prompt(String message, String title) { //called by confirm action, also called upon license deactivation
		return JOptionPane.showOptionDialog(null, message, title, 0, 0, null, null, 0);
	}

	private String getTabAsString(Integer at, JTabbedPane source) { //get title of tab as String with only numbers or '+'

		//null they want the order tab, otherwie they specified
		JTabbedPane tempTabbedPane = source == null ? orderTabHolder : source;

		//null they want the current tab, otherwise the want the specified tab
		return at == null ? tempTabbedPane.getTitleAt(orderTabHolder.getSelectedIndex()).replace("Order ", "").replace(" Settings","") : tempTabbedPane.getTitleAt(at).replace("Order ", "").replace(" Settings","");
	
	}

	private int getTabAsInt(Integer at, JTabbedPane source) {
		return Integer.parseInt(getTabAsString(at, source));
	}

	class TabChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JTabbedPane source = (JTabbedPane)e.getSource();

			if (getTabAsString(null, source).equals("+")) { //if tab changes and the tab clicked on is a '+', add a new order

				if (Main.getOrdersListLength() >= 1 && !isPro) {//they have regular version and have too many orders
					orderTabHolder.setSelectedIndex(orderTabHolder.getSelectedIndex()-1); //sets selected tab back one to avoid it being the '+'
					JOptionPane.showMessageDialog(null, "Limit of one order has been reached, you must upgrade to pro for infinite orders");

					//add a link to upgrade in that joptionpane, or buttons to do so (an upgrade and a cancel button)
				} else {
					new Order();
				}
			} 
		}
	}


	public JLabel getScheduledDateLabel() {//gets scheduled date label so scheduler can manipulate it
		return scheduledDateLabel;
	}

	public void toggleButton() { //enable to abort and vice versa
		enableBotButton.setText(enableBotButton.getText().equals("Enable Bot") ? "Abort Bot" : "Enable Bot");
	}

	private void processDeactivate() { //maybe have a prompt asking if they want to reactivate on windows or mac, because this current setup will give them their existing OS bot regardless
		if (prompt("Deactivating license will disable the bot on this computer. You will be able to reactivate and redownload on any computer\nusing the key in the original email we sent you upon purchase. Are you sure you want to deactivate this license? ", "Are you sure you want to deactivate this license?") == 0) {	
			if (Main.getBotSecurity().deactivateLicense()) { //runs deactivate license from software security, which returns true if deactivated
				JOptionPane.showMessageDialog(null,"License deactivated successfully! Your download link and activation key from your purchase confirmation email have\nbeen reactivated. You may now reactivate the bot on any computer using those credentials. The bot will exit now.", "License Deactivated", 2);
				System.exit(0);
			} else { //deactivation failed, could've been sparked by their already having 0 downloads in the db
				JOptionPane.showMessageDialog(null, "Licence deactivation failed. Check your internet connection because deactivation requires internet connectivity. \nIf this problem persists, email us at team@supremesharkbot.com for a manual deactivation.", "Deactivation Failed", 0);
			}
		}	
	}

	public void textConsoleNewLine(String message) { //pushes new line to text console

		message = textConsoleArea.getText().isEmpty() ? message.replace("\n","") : (textConsoleArea.getText() + "\n" + message); //if the field is empty, don't add a line break and remove all line breaks from message

		textConsoleArea.setText(message);
	}

	private boolean configurationIsAcceptable() { //if too many proxy-less connections are made user is warned
		int counter = 0;
		for (Order o : Main.getOrders()) if (!o.getOrderSettings().isUsingProxy()) counter++;

		return counter > 2 ? (prompt("More than two orders have no proxies set, and too many connections on one IP can result\nin a temporary ban. Are you sure you want to proceed with current configuration?", "IP Ban Risk") == 0) : true;
	}

	private void processEnableOrAbort() { //processes enable/ abort action (called by scheduler and by button click)
		if (enableBotButton.getText().equals("Enable Bot") && configurationIsAcceptable()) {
			enableRegardlessOfProxyReadinessOrALackThereof();
		} else if (enableBotButton.getText().equals("Abort Bot")) { //if the bot was actually enabled, abort it
			Main.interruptThreads(); //abort bot
			System.out.println("Process Time: " + (new Date().getTime() - enableDate.getTime()) + " milliseconds"); //only 
			toggleButton();
			abortStatuses();
		} else {
			//dont do anything if the configuration is acceptable prompt failed (due to too many proxy-less connections)
		}
	}

	public void enableRegardlessOfProxyReadinessOrALackThereof() { //called to enable bot, scheduler calls this to bypass any warnings
		enableDate = new Date();
		setItemInfoFromTable();
//		new Dispatcher(Main.getOrders(), textConsoleArea, webView).deploy(); //launch bot
		toggleButton();
		new Dispatcher(Main.getOrders(), textConsoleArea, null).deploy(); //launch bot
	}

	private void setItemInfoFromTable() { //converts table data into item objects

		for (Order o : Main.getOrders()) { //for each order
			o.clearItems(); //if bot was already enabled this clears the old items

			if (o.getTable().getCellEditor() != null) o.getTable().getCellEditor().stopCellEditing(); //saves values of cells being edited
			for (int i = 0; i < o.getTable().getModel().getRowCount() - 1; i++) { //for each row (item)
				TableModel model = o.getModel();
				Item item = new Item();
				item.setItemNumber(i + 1);
				item.setKeywords(((String) model.getValueAt(i, 0)).toLowerCase().split("\\s+"));
				item.setCategory((String) model.getValueAt(i, 1));
				item.setColors(((String) model.getValueAt(i, 2)).toLowerCase().split("\\s+"));
				item.setSize((String) model.getValueAt(i, 3));
				item.setEarlyLink((String) model.getValueAt(i, 4));
				o.addItem(item);

				System.out.println("\n\nOrder " + o.getOrderNum() + " " + item.toString());
			}
		}
	}

	private void abortStatuses() { //sets status of each item to abort following bot abortion

		for (Order o : Main.getOrders()) for (int i = 0; i < o.getModel().getRowCount(); i ++) o.getModel().setValueAt("Aborted", i, 5);
	
	}

	public boolean areTechMessagesEnabled() { //tells processor whether it should print tech messages
		return techMessagesEnabled;
	}


}
