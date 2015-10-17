package bot;

import java.util.Date;

public class SchedulerSettings {
	private Date enableDate;
	private boolean isSelected;
	
	public SchedulerSettings() {
		isSelected = false;
		enableDate = new Date();
	}
	
	public Date getEnableDate() {
		return enableDate;
	}
	public void setEnableDate(Date enableDate) {
		this.enableDate = enableDate;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	

}
