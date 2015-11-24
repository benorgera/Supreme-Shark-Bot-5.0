package GUI;

import javax.swing.table.DefaultTableModel;

public class MyDefaultTableModel extends DefaultTableModel {
    
	private static final long serialVersionUID = -9147556184564598451L;
	private boolean[][] editable_cells; // 2d array to represent rows and columns

    public MyDefaultTableModel(Object[] rows, int cols) { // constructor
        super(rows, cols);
        this.editable_cells = new boolean[100][7]; //2d array not dynamic to avoid outOfBounds because of more rows being added
    }

    @Override
    public boolean isCellEditable(int row, int column) { // custom isCellEditable function
    	System.out.println("Cell at row "+row+" and column "+column+" is editable "+this.editable_cells[row][column]);
        return this.editable_cells[row][column];
    }

    public void setCellEditable(int row, int col, boolean value) {
        this.editable_cells[row][col] = value; // set cell true/false
        System.out.println("Cell at row "+row+" and column "+col+" is now editable "+value);
        this.fireTableCellUpdated(row, col);
    }
}