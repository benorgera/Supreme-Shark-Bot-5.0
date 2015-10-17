package GUI;


import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;

import bot.SchedulerEnabler;
import bot.SchedulerSettings;
import bot.SetCentered;
import bot.main;
import eu.hansolo.custom.SteelCheckBox;
import eu.hansolo.tools.ColorDef;
import net.miginfocom.swing.MigLayout;

public class SchedulerGUI extends JFrame {

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
		settings = main.getSchedulerSettings();
		switcher.setSelected(settings.isSelected()); //will show false if it hasnt been enabled
		JButton applyAndExitButton = new JButton("Apply and Exit");

		timeSpinner = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, dateFormat);

		timeSpinner.setEditor(timeEditor);
		
		if (switcher.isSelected()) { //if the thing was enabled, what was the date
			timeSpinner.setValue(settings.getEnableDate()); // will previously set date
		} else { //it wasn't enabled, set it to the current date
			timeSpinner.setValue(new Date());
		}

		contentPane.add(dateTimeLabel);
		contentPane.add(timeSpinner);
		contentPane.add(switcher);
		contentPane.add(applyAndExitButton);

		AbstractAction exit = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {

				settings.setSelected(switcher.isSelected());
				if (switcher.isSelected()) { //now that settings has the proper values
					setupScheduler();
				} else {
					unsetScheduler();
				}
				dispose();


			}
		};
		applyAndExitButton.addActionListener(exit);

		pack();
		SetCentered centerer = new SetCentered(this);
		setVisible(true);

	}

	private void setupTimer(Date enableDate) throws ParseException { //sets timer to enable bot at scheduled time

		for (Timer t: main.getTimerStack()) {
			t.cancel();
		}
		
		timer = new Timer();
		
		main.getTimerStack().push(timer);
		
		timer.schedule(new SchedulerEnabler(), enableDate);
	}

	private void setupScheduler() {

		settings.setEnableDate((Date) timeSpinner.getValue()); //give the settings the right time
		try {
			setupTimer((Date) timeSpinner.getValue()); //start the countdown
		} catch (ParseException e1) {
			e1.printStackTrace(); //this error shouldnt be thrown
		}

		main.getGUI().getScheduledDateLabel().setText("Scheduled Date/Time: "+new SimpleDateFormat(dateFormat).format(settings.getEnableDate()));

		setVisibleMainGUI(true);
	}

	private void unsetScheduler() {
		setVisibleMainGUI(false);
		settings.setEnableDate(new Date());
	}

	private void setVisibleMainGUI(boolean val) {
		main.getGUI().getScheduledDateLabel().setVisible(val);

	}

}
