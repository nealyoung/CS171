// CS171 Fall 2012 Final Project
//
// Nealon Young
// ID #81396982

import java.util.ArrayList;

public class Evaluator {
	private static final int emptyPosition = 0;
	private static final int aiPosition = 1;
	private static final int humanPosition = 2;
	
	private static int[][] positionWeights;
	private static int[][] winSequence;
	private static int[][] openEndSequence;
	private static int[][] threatSequence;
	private static int[][] sevenTrapSequence;
	
	//
	// intialize() generates piece sequences for the current game board
	//
	public static void initialize() {
		createPositionWeights();
		createWinSequence();
		createOpenEndSequence();
		createThreatSequence();
		createSevenTrapSequence();
	}
	
	//
	// inBounds() returns true if the given x and y coordinates are within the bounds of the board
	//
	private static boolean inBounds(int x, int y) {
		return ((0 <= x) && (x < AIGame.width) && (0 <= y) && (y < AIGame.height)) ? true : false;
	}
	
	//
	// matchPosition() scans the game board for a sequence eminating from the (x, y) position, looking
	// in a direction determined by the deltaX and deltaY parameters, deltaS determines whether to
	// reverse the sequence
	//
	private static boolean match(AIGameState state, int x , int y, int[] sequence, int deltaX, int deltaY, int deltaS) {
		int s = (deltaS > 0) ? 0 : sequence.length - 1;
		
		if (inBounds(x + deltaX * (sequence.length - 1), y + deltaY * (sequence.length - 1))) {
			for (int i = 0; i < sequence.length; i++) {
				if (state.boardPositions[x][y] != sequence[s]) {
					return false;
				}
			
				x += deltaX;
				y += deltaY;
				s += deltaS;
			}
			
			return true;
		}
		
		return false;
	}
	
	//
	// matchPosition() scans the game board for a sequence eminating from the (x, y) position, direction
	// specifies whether the sequence should be reversed
	//
	private static boolean matchPosition(AIGameState state, int x, int y, int[] sequence, int direction) {		
		//
		// Look horizontally right
		//
		if (match(state, x, y, sequence, 1, 0, direction)) {
			return true;
		}
		
		//
		// Look vertically up
		//
		if (match(state, x, y, sequence, 0, 1, direction)) {
			return true;
		}

		//
		// Look diagonally right upwards
		//
		if (match(state, x, y, sequence, 1, 1, direction)) {
			return true;
		}
		
		//
		// Look diagonally right downwards
		//
		if (match(state, x, y, sequence, 1, -1, direction)) {
			return true;
		}
		
		return false;
	}
	
	//
	// countPositionForward() scans the game board for the given sequence, and returns the number of occurences
	//
	private static int countPositionForward(AIGameState state, int[] sequence) {
		int sequenceCount = 0;
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (matchPosition(state, i, j, sequence, 1)) {
					sequenceCount++;
				}
			}
		}
		
		return sequenceCount;
	}
	
	//
	// countPositionBackward() scans the game board for the given sequence, and returns the number of occurences
	//
	private static int countPositionBackward(AIGameState state, int[] sequence) {
		int sequenceCount = 0;
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (matchPosition(state, i, j, sequence, -1)) {
					sequenceCount++;
				}
			}
		}
		
		return sequenceCount;
	}
	
	//
	// evalWins() counts the number of win sequences (k in a row) on the game board for the given player
	//
	private static int evalWins(AIGameState state, boolean isAi) {
		int player = (isAi) ? aiPosition : humanPosition;
		
		return countPositionForward(state, winSequence[player]);
	}
	
	private static void createWinSequence() {
		winSequence = new int[humanPosition + 1][AIGame.winLength];
		
		for (int i = aiPosition; i <= humanPosition; i++) {
			for (int j = 0; j < AIGame.winLength; j++) {
				winSequence[i][j] = i;
			}
		}
	}
	
	//
	// evalThreats() counts the number of threat sequences (k-1 in a row) on the game board for the given player
	//
	private static int evalThreats(AIGameState state, boolean isAi) {
		int player = (isAi) ? aiPosition : humanPosition;
		
		return (countPositionForward(state, threatSequence[player]) + countPositionBackward(state, threatSequence[player]));
	}
	
	//
	// createThreatSequences() generates threat sequences based on the game parameters (width, height, K)
	//
	private static void createThreatSequence() {
		threatSequence = new int[humanPosition + 1][AIGame.winLength];
		
		for (int i = aiPosition; i <= humanPosition; i++) {
			threatSequence[i][0] = emptyPosition;
			
			for (int j = 1; j < AIGame.winLength; j++) {
				threatSequence[i][j] = i;
			}
		}
	}
	
	//
	// evalOpenEnds() counts the number of open end sequences (empty position, k-2 player pieces, 2 empty positions) on the game board
	// for the given player
	//
	private static int evalOpenEnds(AIGameState state, boolean isAi) {
		int player = (isAi) ? aiPosition : humanPosition;
		
		return (countPositionForward(state, openEndSequence[player]) + countPositionBackward(state, openEndSequence[player]));
	}
	
	//
	// createOpenEndSequence() generates open end sequences based on the game parameters (width, height, K)
	//
	private static void createOpenEndSequence() {
		openEndSequence = new int[humanPosition + 1][AIGame.winLength + 1];
		
		for (int i = aiPosition; i <= humanPosition; i++) {
			openEndSequence[i][0] = emptyPosition;
			
			for (int j = 1; j < (AIGame.winLength - 1); j++) {
				openEndSequence[i][j] = i;
			}
			
			openEndSequence[i][AIGame.winLength - 1] = emptyPosition;
			openEndSequence[i][AIGame.winLength] = emptyPosition;
		}
	}
	
	//
	// evalThreats() counts the number of 'seven trap' sequences (k-1 in a row) on the game board for the given player
	//
	private static int evalSevenTraps(AIGameState state, boolean isAi) {
		int player = (isAi) ? aiPosition : humanPosition;
		
		int sevenTraps = 0;
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height - 1; j++) {
				if (match(state, i, j, sevenTrapSequence[player], -1, 0, 1)) {
					if (match(state, i, j + 1, sevenTrapSequence[player], -1, -1, 1)) {
						sevenTraps++;
					}
				}
				
				if (match(state, i, j, sevenTrapSequence[player], 1, 0, 1)) {
					if (match(state, i, j + 1, sevenTrapSequence[player], 1, -1, 1)) {
						sevenTraps++;
					}
				}
			}
		}
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 1; j < AIGame.height; j++) {
				if (match(state, i, j, sevenTrapSequence[player], -1, 0, 1)) {
					if (match(state, i, j - 1, sevenTrapSequence[player], -1, 1, 1)) {
						sevenTraps++;
					}
				}
				
				if (match(state, i, j, sevenTrapSequence[player], 1, 0, 1)) {
					if (match(state, i, j - 1, sevenTrapSequence[player], 1, 1, 1)) {
						sevenTraps++;
					}
				}
			}
		}
		
		return sevenTraps;
	}
	
	//
	// createSevenTrapSequence() generates 'seven trap' sequences based on the game parameters (width, height, K)
	//
	private static void createSevenTrapSequence() {
		sevenTrapSequence = new int[humanPosition + 1][AIGame.winLength];
		
		for (int i = aiPosition; i <= humanPosition; i++) {
			sevenTrapSequence[i][0] = emptyPosition;
			
			for (int j = 1; j < AIGame.winLength; j++) {
				sevenTrapSequence[i][j] = i;
			}
		}

	}
	
	//
	// evalPositionWeights() finds the sum of the weights of positions held by the given player
	//
	private static int evalPositionWeights(AIGameState state, boolean isAi) {
		int player = (isAi) ? aiPosition : humanPosition;
		int score = 0;
				
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (state.boardPositions[i][j] == player) {
					score += positionWeights[i][j];
				}
			}
		}
		
		return score;
	}
	
	//
	// createPositionWeights() generates weights for each position on the board, based on the game parameters (width, height, K)
	//
	private static void createPositionWeights() {
		positionWeights = new int[AIGame.width][AIGame.height];
		
		int width = AIGame.width;
		int height = AIGame.height;
		int k = AIGame.winLength;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (inBounds(i + (k - 1), j)) {
					for (int l = 0; l < k; l++) {
						positionWeights[i + l][j]++;
					}
				}
				
				if (inBounds(i, j + (k - 1))) {
					for (int l = 0; l < k; l++) {
						positionWeights[i][j + l]++;
					}
				}
				
				if (inBounds(i + (k - 1), j + (k - 1))) {
					for (int l = 0; l < k; l++) {
						positionWeights[i + l][j + l]++;
					}
				}
				
				if (inBounds(i + (k - 1), j - (k - 1))) {
					for (int l = 0; l < k; l++) {
						positionWeights[i + l][j - l]++;
					}
				}
			}
		}
	}
	
	//
	// Weights to use for the different features in eval()
	//
	private static final int[] evalWeights = {1000000, 100, 10, 15, 20};
	
	//
	// eval() is a MFEF that returns the score for a given game state by combining 5 weighted factors
	//
	public static int eval(AIGameState state) {
		int[] aiScores    = {0, 0, 0, 0, 0};
		int[] humanScores = {0, 0, 0, 0, 0};
		
		aiScores[0]    = evalWins(state, true);
		humanScores[0] = evalWins(state, false);
		
		aiScores[1]    = evalThreats(state, true);
		humanScores[1] = evalThreats(state, false);
		
		aiScores[2]    = evalOpenEnds(state, true);
		humanScores[2] = evalOpenEnds(state, false);
		
		aiScores[3]    = evalPositionWeights(state, true);
		humanScores[3] = evalPositionWeights(state, false);
		
		aiScores[4]    = evalSevenTraps(state, true);
		humanScores[4] = evalSevenTraps(state, false);
		
		int finalScore = 0;
		
		for (int i = 0; i < aiScores.length; i++) {
			finalScore += (evalWeights[i] * (aiScores[i] - humanScores[i]));
		}
		
		return finalScore;
	}
}
