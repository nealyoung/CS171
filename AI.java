import java.util.ArrayList;
import javax.swing.*;


public class AI implements Runnable {	
	//any data needed between moves should be placed here as static:
	//NONE
	
	private final int SEARCH_DEPTH = 4;
	
	public AI () {

	}
	
	//AI Algorithm here. ------------------------------------------!!!!
	//called when it's AI's turn to move.
	//board is AIGame.buttons[width][height]
	public void run () {
		validMoves();
		int topScore = 0;
		ArrayList<int[]> moves = validMoves();
		boolean breakTime = false;
		
		
	}
	
	private int search(int[] move, int depth) {
		if (depth == 0) {
			
		}
	}
	
	private int eval(int[] pos) {
		int x = pos[0];
		int y = pos[1];
		return 1;
	}
	
	private ArrayList<int[]> validMoves() {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				
				int[] tempPos = new int[2];
				tempPos[0] = i;
				tempPos[1] = j;
				
				// Change the positions to account for the effects of gravity in the game
				int[] pos = AIGame.TransposeForGravity(tempPos);
				
				if (AIGame.buttons[pos[0]][pos[1]].getText().equals(AIGame.blankButtonText)) {
					// Check if the move has already been counted
					if (!moves.contains(pos)) {
						moves.add(pos);
						System.out.println(pos[0]+ " , " + pos[1]);
					}
				}
			}
		}
		
		return moves;
	}
	
	public void Reset () {
		//clear sequential-AI based data here:
		
	}
}
