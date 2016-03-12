package backend;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Prompter {

	public static void throwError(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, 0);
	}


	public static int prompt(String message, String title) {
		return JOptionPane.showOptionDialog(null, message, title, 0, 0, null, null, 0);
	}

	public static void throwSuccess(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, 1);
	}
	
	public static int buttonOptionPrompt(String message, String title, String[] options) {
		return JOptionPane.showOptionDialog(null, message, title, 0, 0, null, options, 0);
	}

	public static int[] comboPrompt(String message, String title, String[] options, String[] buttons) { //prompt with a combobox dropdown, return the selected button and the selected combo option
		System.out.println("prompting");

		JComboBox<Object> optionList = new JComboBox<Object>(options);

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(optionList, BorderLayout.SOUTH);

		panel.add(new JLabel(message), BorderLayout.NORTH);
		
		int res = JOptionPane.showOptionDialog(null, panel, "Confirm Order " + title, 0, 3, null, buttons, 0);
		
		return new int[] {res, optionList.getSelectedIndex()};

	}

}
