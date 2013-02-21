// CS171 Fall 2012 Final Project
//
// Nealon Young
// ID #81396982

import java.lang.*;
import java.util.ArrayList;

public class AIGameState implements Cloneable {
	private static final int emptyPosition = 0;
	private static final int aiPosition = 1;
	private static final int humanPosition = 2;
	
	int[][] boardPositions;
	boolean aiMove;
	
	//
	// Constructor for AIGameState, board positions taken from currently running game
	//
	public AIGameState() {
		aiMove = true;
		
		boardPositions = new int[AIGame.width][AIGame.height];
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (AIGame.buttons[i][j].getText().equals(AIGame.aiButtonText)) {
					boardPositions[i][j] = aiPosition;
				} else if (AIGame.buttons[i][j].getText().equals(AIGame.humanButtonText)) {
					boardPositions[i][j] = humanPosition;
				} else {
					boardPositions[i][j] = emptyPosition;
				}
			}
		}
	}
	
	//
	// Constructor for AIGameState which takes parameters for board postions and the current move
	//
	public AIGameState(int[][] boardPositions, boolean aiMove) {
		this.boardPositions = boardPositions;
		this.aiMove = aiMove;
	}
	
	//
	// aiMove() returns true if it is the AI's turn to move, false otherwise
	//
	public boolean aiMove() {
		return aiMove;
	}
	
	public void makeMove(AIGameMove move) {
		//
		// Check that the position is empty
		//
		if (boardPositions[move.getRow()][move.getColumn()] == emptyPosition) {
			if (aiMove) {
				//System.out.println("AI Moves (" + move.getRow() + ", " + move.getColumn() + ")");
				boardPositions[move.getRow()][move.getColumn()] = aiPosition;
			} else {
				//System.out.println("Human Moves (" + move.getRow() + ", " + move.getColumn() + ")");
				boardPositions[move.getRow()][move.getColumn()] = humanPosition;
			}
			
			// Switch the player
			aiMove = (aiMove ? false : true);
		}
	}
	
	public void makeAIMove(AIGameMove move) {
		boardPositions[move.getRow()][move.getColumn()] = aiPosition;
	}
	
	public void makeHumanMove(AIGameMove move) {
		boardPositions[move.getRow()][move.getColumn()] = humanPosition;
	}
	
	//
	// validMoves() returns an ArrayList of all moves that can be made from a game state
	//
	public ArrayList<AIGameMove> validMoves() {
		ArrayList<AIGameMove> moves = new ArrayList<AIGameMove>();
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (boardPositions[i][j] == emptyPosition) {
					AIGameMove move = new AIGameMove(i, j);
					moves.add(move);
					
					if (AIGame.gravity) {
						break;
					}
				}
			}
		}
		
		return moves;
	}
	
	//
	// printBoard() prints a representation of the game state to the console
	//
	public void printBoard() {
		for (int i = 0; i < AIGame.height; i++) {
			for (int j = 0; j < AIGame.width; j++) {
				if (boardPositions[j][i] == aiPosition) {
					System.out.print(" A");
				} else if (boardPositions[j][i] == humanPosition) {
					System.out.print(" H");
				} else {
					System.out.print(" .");
				}
			}
			
			System.out.println("");
		}
	}
	
	//
	// clone() duplicates a game state
	//
	public AIGameState clone() {
		int[][] newPositions = new int[AIGame.width][AIGame.height];
		
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				newPositions[i][j] = boardPositions[i][j];
			}
		}
		
		AIGameState newState = new AIGameState(newPositions, aiMove);
		
		return newState;
	}
}