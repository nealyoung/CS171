// CS171 Fall 2012 Final Project
//
// Nealon Young
// ID #81396982

public class AIGameMove
{
	private int row;
	private int column;
	
	//
	// Constructor for AIGameMove taking the move's row and column values
	//
	public AIGameMove(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	//
	// getRow() returns the move's row
	//
	public int getRow() {
		return row;
	}
	
	//
	// getColumn() returns the move's column
	//
	public int getColumn() {
		return column;
	}
	
	//
	// equals() compares two moves, and returns true if they are equivalent, false otherwise
	//
	public boolean equals(AIGameMove other) {
		if ((other.getRow() == row) && (other.getColumn() == column)) {
			return true;
		} else {
			return false;
		}
	}
}
