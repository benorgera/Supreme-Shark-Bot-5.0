package backend;

import gui.GUI;
import gui.LoadingGIF;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;

public class Main {
	private static GUI frame;
	private static SoftwareSecurity botSecurity;
	private static ArrayList<Order> orders;
	private static SchedulerSettings schedulerSettings;
	private static Stack<Timer> timerStack;
	private final static double thisVersionNumber = 5.0;
	private static ArrayList<Thread> threads;

	public static void main(String[] args) {
		
		LoadingGIF loader = new LoadingGIF("Initializing Supreme Shark Bot", "Supreme Shark Bot"); //starts loading gif

		botSecurity = new SoftwareSecurity(loader);
		try {
			botSecurity.initialize();
			botSecurity.processStatus();
		} catch (Exception e) {
			System.out.println("Unforeseen security error"); //this should never be thrown 
			e.printStackTrace();
		}

		loader.passUI("Initializing bot interface");
		orders = new ArrayList<Order>();
		threads = new ArrayList<Thread>();
		timerStack = new Stack<Timer>();
		frame = new GUI(botSecurity.getVersionIsPro(), botSecurity.getThisVersionNumber());
		new Order();
		schedulerSettings = new SchedulerSettings();
		frame.setVisible(true);
		loader.dispose(); //gets rid of gif
	}

	public static GUI getGUI() {
		return frame;
	}

	public static void pushToOrderList(Order order) {
		orders.add(order);
	}

	public static void removeFromOrderList(int index) {
		System.out.println("Order " + orders.get(index).getOrderNum() + " deleted");
		orders.remove(index);
	}

	public static int getOrdersListLength() {
		return orders.size();
	}

	public static ArrayList<Order> getOrders() {
		return orders;
	}

	public static String getActivationKey() {
		return botSecurity.getActivationKeyValue();
	}

	public static SchedulerSettings getSchedulerSettings() {
		return schedulerSettings;
	}

	public static void setSchedulerSettings(SchedulerSettings schedulerSettings) {
		Main.schedulerSettings = schedulerSettings;
	}

	public static Stack<Timer> getTimerStack() {
		return timerStack;
	}

	public static double getThisVersionNumber() {
		return thisVersionNumber;
	}

	public static SoftwareSecurity getBotSecurity() {
		return botSecurity;
	}

	public static void pushToWorkerArray(Thread thread) {
		threads.add(thread);
	}

	public static void interruptThreads() {
		for (Thread t : threads) t.interrupt();
		threads.clear();
	}

}
