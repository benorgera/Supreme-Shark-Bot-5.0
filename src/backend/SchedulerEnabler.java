package backend;

import java.util.TimerTask;

public class SchedulerEnabler extends TimerTask {

	@Override
	public void run() {
		System.out.println("Scheduler Enabled");
		Main.getSchedulerSettings().setSelected(false); //turn off scheduler
		Main.getGUI().getScheduledDateLabel().setVisible(false);
		Main.getGUI().enableRegardlessOfProxyReadinessOrALackThereof();
		
	}
}
