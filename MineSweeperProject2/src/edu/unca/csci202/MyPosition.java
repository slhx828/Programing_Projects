package edu.unca.csci202;
/**
 * position class
 * gets cell coordinates 
 * 
 * @author Sarah Hendricks
 *
 */
public class MyPosition {
	
    public int row;
    public int col;

    public MyPosition() {
    }

    public MyPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return "row: " + row + ", col: " + col;
    }	

}
