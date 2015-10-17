package bot;

import java.util.TimerTask;

public class SchedulerEnabler extends TimerTask {

	@Override
	public void run() {
		System.out.println("Enabled");
		main.getSchedulerSettings().setSelected(false); //turn off scheduler
		main.getGUI().getScheduledDateLabel().setVisible(false);
		main.getGUI().setStarted();
		
	}
}
