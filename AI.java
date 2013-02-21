// CS171 Fall 2012 Final Project
//
// Nealon Young
// ID #81396982

import java.util.ArrayList;
import javax.swing.*;

public class AI implements Runnable {
	private static final int TIME_LIMIT_MILLIS = 5000;
	private static final int EVALS_PER_SECOND = 100;
	private static final int emptyPosition = 0;
	private static final int aiPosition = 1;
	private static final int humanPosition = 2;
	private static final int winCutoff = 500000;
	
	private static boolean searchCutoff = false;
	
	public AI() {

	}
	
	//
	// run() executes a search of the game state, and makes a move for the AI player
	//
	public void run() {
		AIGameState state = new AIGameState();
		
		AIGameMove move = chooseMove(state);
		
		AIGame.buttons[move.getRow()][move.getColumn()].setText(AIGame.aiButtonText);
	}
	
	//
	// chooseMove() scores each of the possible moves that can be made from the given AIGameState, and returns the move with the highest eval score
	//
	private AIGameMove chooseMove(AIGameState state) {
		long startTime = System.currentTimeMillis();
		boolean aiMove = state.aiMove();
		int maxScore = Integer.MIN_VALUE;
		AIGameMove bestMove = null;
		
		ArrayList<AIGameMove> moves = state.validMoves();
		
		for (AIGameMove move : moves) {
			//
			// Copy the current game state
			//
			AIGameState newState = state.clone();
			
			newState.makeMove(move);
			
			//
			// Compute how long to spend looking at each move
			//
			long searchTimeLimit = ((TIME_LIMIT_MILLIS - 1000) / (moves.size()));
			
			int score = iterativeDeepeningSearch(newState, searchTimeLimit);
			
			//
			// If the search finds a winning move
			//
			if (score >= winCutoff) {
				return move;
			}
			
			if (score > maxScore) {
				maxScore = score;
				bestMove = move;
			}
		}
				
		return bestMove;
	}
	
	//
	// Run an iterative deepening search on a game state, taking no longrer than the given time limit
	//
	private int iterativeDeepeningSearch(AIGameState state, long timeLimit) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeLimit;
		int depth = 1;
		int score = 0;
		searchCutoff = false;
		
		while (true) {
			long currentTime = System.currentTimeMillis();
			
			if (currentTime >= endTime) {
				break;
			}
			
			int searchResult = search(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentTime, endTime - currentTime);
			
			//
			// If the search finds a winning move, stop searching
			//
			if (searchResult >= winCutoff) {
				return searchResult;
			}
			
			if (!searchCutoff) {
				score = searchResult;
			}
			
			depth++;
		}
		
		return score;
	}
	
	//
	// search() will perform minimax search with alpha-beta pruning on a game state, and will cut off if the given time
	// limit has elapsed since the beginning of the search
	//
	private int search(AIGameState state, int depth, int alpha, int beta, long startTime, long timeLimit) {
		ArrayList<AIGameMove> moves = state.validMoves();
		boolean myMove = state.aiMove();
		int savedScore = (myMove) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int score = Evaluator.eval(state);
		long currentTime = System.currentTimeMillis();
		long elapsedTime = (currentTime - startTime);
		
		if (elapsedTime >= timeLimit) {
			searchCutoff = true;
		}
		
		//
		// If this is a terminal node or a win for either player, abort the search
		//
		if (searchCutoff || (depth == 0) || (moves.size() == 0) || (score >= winCutoff) || (score <= -winCutoff)) {
			return score;
		}
		
		if (state.aiMove) {
			for (AIGameMove move : moves) {
				AIGameState childState = state.clone();
				childState.makeMove(move);

				alpha = Math.max(alpha, search(childState, depth - 1, alpha, beta, startTime, timeLimit));
				
				if (beta <= alpha) {
					break;
				}
			}
			
			return alpha;
		} else {
			for (AIGameMove move : moves) {
				AIGameState childState = state.clone();
				childState.makeMove(move);
				
				beta = Math.min(beta, search(childState, depth - 1, alpha, beta, startTime, timeLimit));
					
				if (beta <= alpha) {
					break;
				}
			}
			
			return beta;
		}
	}
	
	public void Reset() {
		//clear sequential-AI based data here:	
	}
}
