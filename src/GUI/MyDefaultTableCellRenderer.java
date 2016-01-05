package gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 8895676313360873624L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


		if (table.getModel().getRowCount() == row + 1) {
			setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
			return this;
		} else {
			DefaultTableCellRenderer def = new DefaultTableCellRenderer();
			def.getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, column);
			
			
//			//blue color scheme		
//			if ((row & 1) == 1 && !hasFocus && !isSelected) def.setBackground(new Color(238,245,252)); //if even row and not focused or selected make it striped light blue
//			
//			if (isSelected) def.setBackground(new Color(119,214,249)); // if selected make row medium blue
//			
//			if (hasFocus) {
//				def.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE)); // if has focus make border dark blue
//				def.setBackground(Color.WHITE); //set the focused cell's background to white
//			} else {
//				def.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY)); //if no focus make border gray
//			}
			
			
			
			//red color scheme
			if ((row & 1) == 1 && !hasFocus && !isSelected) def.setBackground(new Color(245,245,245));
			
			if (isSelected) def.setBackground(new Color(255,220,220)); 
			
			if (hasFocus) {
				def.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(255,112,112))); 
				def.setBackground(Color.WHITE); //set the focused cell's background to white
			} else {
				def.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY)); //if no focus make border gray
			}

			return def;
		}
	}

}

