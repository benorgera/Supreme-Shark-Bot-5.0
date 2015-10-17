package GUI;

import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
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
import javax.swing.table.TableColumn;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;

import bot.ButtonColumn;
import bot.Encrypter;
import bot.Order;
import bot.SetCentered;
import bot.main;

import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GUI extends JFrame {
	private static boolean isPro; //true if pro
	private static String thisVersionNumberAsString; //this copies version number
	private JPanel contentPane;
	private JTabbedPane orderTabHolder;
	private int orderCount = 0;
	private final String[] headers = {"Keywords", "Category", "Color", "Size", "Early Link", "Status", "Actions"};
	private final String[] newItemRow =  {"", "", "", "", "", "", "Delete Item"};
	private final String[] newItemButtonRow =  {"", "", "", "", "", "", "+"};
	private TabChangeListener tabChange;

	private JLabel scheduledDateLabel = new JLabel(); //blank unless scheduler enabled

	public GUI(boolean isPro, double thisVersionNumber) {
		GUI.isPro = isPro;
		thisVersionNumberAsString = Double.toString(thisVersionNumber);
		if (isPro) {
			setTitle("Supreme Shark Bot "+thisVersionNumberAsString+" Pro");
		} else {
			setTitle("Supreme Shark Bot "+thisVersionNumberAsString);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 926, 518);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));


		Action launchHelpAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchHelp();

			}
		};

		Action launchSchedulerAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchScheduler();
			}
		};

		JPanel orderTabHolderHolder = new JPanel();
		orderTabHolderHolder.setLayout(new BorderLayout(0,0));
		contentPane.add(orderTabHolderHolder, BorderLayout.CENTER);

		orderTabHolder = new JTabbedPane(JTabbedPane.TOP);
		orderTabHolderHolder.add(orderTabHolder, BorderLayout.CENTER);

		JPanel deactivateAndEnableButtonsPanel = new JPanel();
		deactivateAndEnableButtonsPanel.setLayout(new BorderLayout(0,0));

		JButton enableBotButton = new JButton("Enable Bot");
		JButton enableRestockMonitorButton = new JButton("Enable Restock Monitor");
		JButton deactivateLicense = new JButton("Deactivate License");
		
		Action deactivateLicenseAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (prompt("Deactivating license will disable the bot on this computer. You will be able to reactivate and download using the key\nin the original email we sent you upon purchase. Are you sure you want to deactivate this license? ", "Are you sure you want to deactivate this license?") == 0) {	
					main.getBotSecurity().deactivateLicense(); //runs deactivate license from software security
				}	
			}
			
		};
		deactivateLicense.addActionListener(deactivateLicenseAction);

		JPanel enableButtonsPanel = new JPanel();
		enableButtonsPanel.setLayout(new BorderLayout(0,0));
		enableButtonsPanel.add(enableBotButton, BorderLayout.WEST);
		enableButtonsPanel.add(enableRestockMonitorButton, BorderLayout.EAST);

		deactivateAndEnableButtonsPanel.add(deactivateLicense, BorderLayout.WEST);
		deactivateAndEnableButtonsPanel.add(enableButtonsPanel, BorderLayout.EAST);

		orderTabHolder.addChangeListener(tabChange = new TabChangeListener());

		JPanel splitPaneHolder = new JPanel();
		splitPaneHolder.setLayout(new BorderLayout(0,0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPaneHolder.add(splitPane, BorderLayout.NORTH);
		splitPaneHolder.add(deactivateAndEnableButtonsPanel, BorderLayout.SOUTH);
		
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPaneHolder, BorderLayout.SOUTH);

		JPanel textConsolePanel = new JPanel();

		JPanel HTMLConsolePanel = new JPanel();


		splitPane.setLeftComponent(textConsolePanel);
		textConsolePanel.setLayout(new BorderLayout(0,0));

		splitPane.setRightComponent(HTMLConsolePanel);
		HTMLConsolePanel.setLayout(new BorderLayout(0,0));

		JScrollPane textConsoleScroller = new JScrollPane();
		textConsolePanel.add(textConsoleScroller, BorderLayout.CENTER);

		JScrollPane HTMLConsoleScroller = new JScrollPane();
		HTMLConsolePanel.add(HTMLConsoleScroller, BorderLayout.CENTER);

		JLabel textConsole = new JLabel("Text Console:");
		textConsolePanel.add(textConsole, BorderLayout.NORTH);
		textConsole.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel HTMLConsole = new JLabel("HTML Console:");
		HTMLConsolePanel.add(HTMLConsole, BorderLayout.NORTH);
		HTMLConsole.setHorizontalAlignment(SwingConstants.CENTER);

		JTextArea textConsoleArea = new JTextArea();
		textConsoleArea.setRows(8);
		textConsoleArea.setEditable(false);

		textConsoleScroller.setViewportView(textConsoleArea);

		JPanel logoPanel = new JPanel();
		contentPane.add(logoPanel, BorderLayout.NORTH);


		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new BorderLayout(0, 0));


		JPanel schedulerHolderPanel = new JPanel();
		schedulerHolderPanel.setLayout(new BorderLayout(0,0));
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
		SetCentered centerer = new SetCentered(this);

	}

	public Object[] newOrder() {
		//		//called by order to add new order to GUI
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
		orderPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane tableHolderScrollPane = new JScrollPane();
		JPanel tableHolder = new JPanel();

		Action deleteOrderAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteOrder();

			}
		};

		tableHolder.setLayout(new BorderLayout(0,0));
		JButton deleteOrderButton = new JButton("Delete Order "+orderCount);
		deleteOrderButton.addActionListener(deleteOrderAction);


		JPanel buttonPanelHolder = new JPanel();//this block makes the delete order goto the southeast
		buttonPanelHolder.setLayout(new BorderLayout(0,0));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(deleteOrderButton);
		buttonPanelHolder.add(buttonPanel, BorderLayout.EAST);

		JPanel orderSettingsButtonHolder = new JPanel();


		Action launchOrderSettings = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchOrderSettings();
			}
		};


		JButton orderSettings = new JButton("Order "+orderCount+" Settings");
		orderSettings.addActionListener(launchOrderSettings);
		orderSettingsButtonHolder.add(orderSettings);
		buttonPanelHolder.add(orderSettingsButtonHolder, BorderLayout.WEST);
		tableHolder.add(buttonPanelHolder, BorderLayout.SOUTH);


		tableHolder.add(table.getTableHeader(), BorderLayout.NORTH);
		tableHolder.add(table, BorderLayout.CENTER);

		tableHolderScrollPane.setViewportView(tableHolder);

		orderPanel.add(tableHolderScrollPane, BorderLayout.CENTER);
		orderTabHolder.addTab("Order "+orderCount, orderPanel);

		for (int i = 0; i < orderTabHolder.getTabCount(); i ++) {
			//removes tab to add tab if new order being added
			if (getTabAsString(i, null).equals("+")) {
				orderTabHolder.removeTabAt(i);
			}
		}
		orderTabHolder.addTab("+", null);


		Action deleteOrAdd = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable source = (JTable)e.getSource();
				String buttonText = (String) source.getValueAt(Integer.valueOf(e.getActionCommand()), 6); //check if the row that was clicked was a delete or an addition
				System.out.println("Button text was "+buttonText);
				if (buttonText.contains("Delete") && ((DefaultTableModel) table.getModel()).getRowCount() != 2) {
					//deletes row whose delete button was pressed
					if (confirmAction("Item", null) == 0) { //shows JOptionPane
						int deleteRow = Integer.valueOf(e.getActionCommand());
						((DefaultTableModel) table.getModel()).removeRow(deleteRow);
						setAllButOneUneditable(table.getModel().getRowCount()-1, model); //makes add new item row uneditable after delete item
					}
				} else if (((DefaultTableModel) table.getModel()).getRowCount() == 2 && buttonText.contains("Delete")) { //theyre trying to delete the only item
					JOptionPane.showMessageDialog(null, "Item 1 cannot be deleted, only one item exists!");
				} else {
					addItem();
				}
			}
		};


		ButtonColumn buttonColumn = new ButtonColumn(table, deleteOrAdd, 6); //makes actions column a button column

		TableColumn JComboBoxColumn = table.getColumnModel().getColumn(1);
		JComboBox comboBox = new JComboBox(new String[] {"jackets", "shirts", "tops and sweaters", "sweatshirts", "pants", "t-shirts", "hats", "bags", "accessories", "skate", "shoes", "shorts"});
		JComboBoxColumn.setCellEditor(new DefaultCellEditor(comboBox));


		int rows = model.getRowCount();
		model.addRow(newItemRow);
		System.out.println("table has "+rows+" row(s)");
		setAllEditable(rows, model);  //allows button to be pressed and new item to be edited

		addNewItemButtonRow(model); //adds new '+' row

		System.out.println("Order "+orderCount+" added");
		Object [] arr = new Object[5];
		arr[0] = orderCount;
		arr[1] = table;
		arr[2] = model;
		arr[3] = deleteOrderButton;
		arr[4] = orderSettings;
		repaint();
		return arr; //informs order what number it is, and tells it the data
	}

	private void addItem() {
		MyDefaultTableModel model = main.getOrders().get(getTabAsInt(null, null) - 1).getModel(); //get selected tab of pane and get its order and that orders tablemodel
		int rows = model.getRowCount();
		if (rows>=5 && !isPro) { //num is 5 because there's the row with the '+' button
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
		for (int i = 0; i <= 4; i++) {
			model.setCellEditable(row, i, false);
		}
	}

	private void setAllEditable(int row, MyDefaultTableModel model) {

		//sets every column in a row editable, except status column, called following new order or new item
		System.out.println("setting all columns editable for row "+row);
		for (int i = 0; i <= 6; i++) {
			if (i == 5) { //its the status column
				model.setCellEditable(row, i, false); //sets status column uneditable
			} else {
				model.setCellEditable(row, i, true); //allows button to be pressed and new item to be edited
			}
		}
	}

	private void addNewItemButtonRow(MyDefaultTableModel model) {
		//called after newOrder or new item, adds new '+' row
		int rows = model.getRowCount(); //get row count so you know what row to make editable
		model.addRow(newItemButtonRow); //adds button that can add an item
		model.setCellEditable(rows, 6, true); //you can click the plus to add an item
	}

	private void launchHelp() {//launches tutorial in default browser
		String url = "http://www.supremesharkbot.com:8080/tutorial.pdf";
		if (Desktop.isDesktopSupported()){
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
		Encrypter encrypter = new Encrypter(main.getActivationKey());

		try {
			encrypter.SetupEncrypter();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Encrypter initialize failed, user won't be warned unless they try and encrypt or decrypt");
		}

		gui = new SettingsGUI(main.getOrders().get(getTabAsInt(null, null) - 1).getOrderSettings(), getTabAsInt(null, null), encrypter); //settings gui passed the encrypter which it will pass to the load or save gui
		System.out.println("GUI passed to Encrypter");
		encrypter.setGUI(gui); //encrypter passed the gui which it will pass to the load or save GUI
		System.out.println("Encrypter passed to GUI");
	}

	private void launchScheduler() {
		//instantiate new scheduler settings panel
		System.out.println("Launched Scheduler");
		SchedulerGUI scheduler = new SchedulerGUI();
	}

	private void deleteOrder() {
		int order = getTabAsInt(null, null);
		if (order == 1 && orderTabHolder.getTabCount() == 2) { //only one order tab in tabbed pane
			JOptionPane.showMessageDialog(null, "Order 1 cannot be deleted, only 1 order exists!");
			return;
		}
		System.out.println("Delete order "+order+" pending");
		if (confirmAction("Order", order) == 0) { // show the joptionpane
			//delete the order and set back the order count
			orderTabHolder.removeChangeListener(tabChange); //remove tab listener so new order isnt addde if the + tab is selected once the previously selected tab dissppears
			orderTabHolder.remove(order-1); //remove order from gui
			main.removeFromOrderList(order-1); //remove order from orders arraylist
			editOrderObjects(); //resets order numbers and buttons in order objects array following delete

			if (getTabAsString(null, null).equals("+")) {
				orderTabHolder.setSelectedIndex(orderTabHolder.getTabCount()-2); //deselect + tab if its selected following deletion
			}
			orderTabHolder.addChangeListener(tabChange); //re-add the change listener now that + deselected
			orderCount--; //drop the order count

		}
	}

	private void editOrderObjects() { //resets order numbers and buttons in order objects array following delete
		int prev = 1;
		for (int i = 0; i < orderTabHolder.getTabCount() - 1; i ++) {
			if (getTabAsInt(i, null) != prev) {
				orderTabHolder.setTitleAt(i, "Order "+prev); //reset tab names
				main.getOrders().get(i).setDeleteButtonText("Delete Order "+prev); //reset delete button
				main.getOrders().get(i).setSettingsButtonText("Order "+prev+" Settings"); //reset settings button
				main.getOrders().get(i).setOrderNum(prev); //reset order numbers
			}
			prev ++;
		}
	}

	private int confirmAction(String type, Integer order) { //calls prompt which prompts, called when order or item deleted
		String message;
		if (order == null) {
			message = "delete the selected item?";
		} else {
			message = "delete Order "+order+"?";
		}
		return prompt("Are you sure you want to "+message, "Confirm "+type+" Deletion");
	}
	
	private int prompt(String message, String title) { //called by confirm action, also called upon license deactivation
		return JOptionPane.showOptionDialog(null,message, title, 0, 0, null, null, 0);
	}

	private String getTabAsString(Integer at, JTabbedPane source) { //get title of tab as String with only numbers or '+'

		JTabbedPane tempTabbedPane;
		if (source == null) {//they want the default pane
			tempTabbedPane = orderTabHolder;
		} else { // they wanted a specified pane
			tempTabbedPane = source;
		}

		if (at == null) { //the want the current tab
			return tempTabbedPane.getTitleAt(orderTabHolder.getSelectedIndex()).replace("Order ", "").replace(" Settings","");
		} else { //they specified a tab
			return tempTabbedPane.getTitleAt(at).replace("Order ", "").replace(" Settings","");
		}
	}

	private int getTabAsInt(Integer at, JTabbedPane source) {
		return Integer.parseInt(getTabAsString(at, source));
	}

	class TabChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JTabbedPane source = (JTabbedPane)e.getSource();

			if (getTabAsString(null, source).equals("+")) { //if tab changes and the tab clicked on is a '+', add a new order

				if (main.getOrdersListLength() >= 1 && !isPro) {//they have regular version and have too many orders
					orderTabHolder.setSelectedIndex(orderTabHolder.getSelectedIndex()-1); //sets selected tab back one to avoid it being the '+'
					JOptionPane.showMessageDialog(null, "Limit of one order has been reached, you must upgrade to pro for infinite orders");

					//add a link to upgrade in that joptionpane, or buttons to do so (an upgrade and a cancel button)
				} else {
					Order newOrder = new Order();
				}
			} 
		}
	}


	public JLabel getScheduledDateLabel() {//gets scheduled date label so scheduler can manipulate it
		return scheduledDateLabel;
	}

	public void setStarted() {
		//sets enable button to abort
		
	}
	

}
