package backend;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class SetCentered {
	
	public SetCentered(JFrame frame) { //centers a JFrame
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
	}
}
