package gui;

import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;
import backend.SchedulerEnabler;
import backend.SchedulerSettings;
import backend.SetCentered;
import backend.Main;
import eu.hansolo.custom.SteelCheckBox;
import eu.hansolo.tools.ColorDef;
import net.miginfocom.swing.MigLayout;

public class SchedulerGUI extends JFrame {

	private static final long serialVersionUID = 6854214767088924648L;
	private SteelCheckBox switcher;
	private JPanel contentPane;
	private SchedulerSettings settings;
	private Timer timer; //static otherwise multiple enables will occur
	private JSpinner timeSpinner; 
	private final String dateFormat = "yyyy-MM-dd h:mm:ss a";

	public SchedulerGUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Scheduler Settings");
		setResizable(false);
		setBounds(100, 100, 100, 100);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("wrap 2"));

		JLabel dateTimeLabel = new JLabel("Bot Enable Date/Time:");
		switcher = new SteelCheckBox();
		switcher.setSelectedColor(ColorDef.GREEN);
		switcher.setColored(true);
		switcher.setText("Enable Scheduler?");
		settings = Main.getSchedulerSettings();
		switcher.setSelected(settings.isSelected()); //will show false if it hasnt been enabled
		JButton applyAndExitButton = new JButton("Apply and Exit");

		timeSpinner = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, dateFormat);

		timeSpinner.setEditor(timeEditor);

		timeSpinner.setValue(switcher.isSelected() ? settings.getEnableDate() : new Date());

		contentPane.add(dateTimeLabel);
		contentPane.add(timeSpinner);
		contentPane.add(switcher);
		contentPane.add(applyAndExitButton);

		AbstractAction exit = new AbstractAction() {

			private static final long serialVersionUID = -2709780000004833601L;

			@Override
			public void actionPerformed(ActionEvent e) {

				settings.setSelected(switcher.isSelected());
				if (switcher.isSelected() && new Date().compareTo((Date) timeSpinner.getValue()) <= 0) { //now that settings has the proper values
					setupScheduler();
					System.out.println("Scheduled for an acceptable date, not less than or equal to the current date");
				} else if (!switcher.isSelected() ){
					unsetScheduler();
				} else {
					System.out.println("Scheduled for an unacceptable date, less than or equal to the current date");
					JOptionPane.showMessageDialog(null, "Error, bot cannot be scheduled to a time before or equal to the current time!");
					return;
				}
				dispose();

			}
		};
		applyAndExitButton.addActionListener(exit);

		pack();
		new SetCentered(this);
		setVisible(true);

	}

	private void setupTimer(Date enableDate) throws ParseException { //sets timer to enable bot at scheduled time
		
		cancelTimers();

		timer = new Timer();

		Main.getTimerStack().push(timer);

		timer.schedule(new SchedulerEnabler(), enableDate);
	}

	private void setupScheduler() {

		settings.setEnableDate((Date) timeSpinner.getValue()); //give the settings the right time
		try {
			setupTimer((Date) timeSpinner.getValue()); //start the countdown
		} catch (ParseException e1) {
			e1.printStackTrace(); //this error shouldnt be thrown
		}

		Main.getGUI().getScheduledDateLabel().setText("Scheduled Date/Time: "+new SimpleDateFormat(dateFormat).format(settings.getEnableDate()));

		setVisibleMainGUI(true);
	}

	private void unsetScheduler() {
		cancelTimers();
		setVisibleMainGUI(false);
		settings.setEnableDate(new Date());
	}

	private void setVisibleMainGUI(boolean val) {
		Main.getGUI().getScheduledDateLabel().setVisible(val);

	}
	
	private void cancelTimers() {
		for (Timer t: Main.getTimerStack()) t.cancel();
	}

}
