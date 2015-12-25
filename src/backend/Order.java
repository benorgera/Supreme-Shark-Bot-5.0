package backend;

import gui.MyDefaultTableModel;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;

public class Order {
	private int orderNum;
	private ArrayList<Item> itemList;
	private JTable orderTable;
	private MyDefaultTableModel tableModel;
	private JButton deleteButton;
	private JButton settingsButton;
	private OrderSettings orderSettings;
	
	public Order() {
		this.orderSettings = new OrderSettings(); //initialize the orderSettings
		//initializes order
		this.itemList = new ArrayList<Item>();
	    Object[] arr = Main.getGUI().newOrder(); //get order number and data
		this.setOrderNum((Integer) arr[0]); //assign order number
		this.setData(arr[1], arr[2]); //assign data
		this.deleteButton = (JButton) arr[3];
		this.settingsButton = (JButton) arr[4];
		Main.pushToOrderList(this);
		System.out.println(Main.getOrdersListLength()+" is the size of the orders list");	
	}
	
	public void addItem(Item i) {
		itemList.add(i);
	}
	
	public void removeItem(Item i) {
		itemList.remove(i);
	}
	
	public ArrayList<Item> getItems() {
		return itemList;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	
	public void setData(Object table, Object model) {
		this.orderTable = (JTable) table;
		this.tableModel = (MyDefaultTableModel) model;
	}
	
	public MyDefaultTableModel getModel() {
		return this.tableModel;
	}
	
	public JTable getTable() {
		return this.orderTable;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}
	
	public void setDeleteButtonText(String s) {
		this.deleteButton.setText(s);
	}

	public OrderSettings getOrderSettings() {
		return orderSettings;
	}

	public void setOrderSettings(OrderSettings orderSettings) {
		this.orderSettings = orderSettings;
	}

	public void setSettingsButtonText(String newText) {
		this.settingsButton.setText(newText);;
	}
	
	public void clearItems() {
		itemList.clear();
	}
	
}
