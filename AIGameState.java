public class AIGameState {
	private AIGame game;
	private static final int emptyPosition = 0;
	private static final int AIPosition = 1;
	private static final int PlayerPosition = 2;
	
	public AIGameState(AIGame game) {
		this.game = game;
	}
	
	private ArrayList<int[]> validMoves() {
		if (game.remainingMovesExist()) {
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
	}
	
}