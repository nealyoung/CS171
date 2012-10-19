import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;


public class AIGame implements ActionListener {
	public final String authorName = "Nealon Young + Nicholas Chihaia";
	public static ImageIcon icon = null;
	public AI aiPlayer = null;
	public JFrame frame = new JFrame(authorName + "'s AI Game");
	public JPanel board = new JPanel();
	public JLabel headerLabel = new JLabel("loading...");
	public JPanel footer = new JPanel();
	
	public static JButton[][] buttons = null;//the game board
	
	//game conditions:
	public static int width = 0;
	public static int height = 0;
	public static int winLength = 1;
	public static boolean gravity = false;
	
	private boolean processingHumanMove = false;
	private boolean gameOver = false;
	
	public static final String restartButtonText = "reset";
	public static final String blankButtonText = "-";
	public static final String humanButtonText = "ME";
	public static final String aiButtonText = "AI";
	
	public void actionPerformed(ActionEvent e) {
		if (!processingHumanMove) {
			processingHumanMove = true;

			JButton source = (JButton)e.getSource();
			if (source.getText().equals(AIGame.restartButtonText)) {
				Reset();
				processingHumanMove = false;
				return;
			}
			
			if (source.getText().equals(AIGame.blankButtonText) && !gameOver) { //it's a valid move
				//lock out all buttons
				AIGame.SetButtonsEnabled(false);
				
				//Update board based on human's move
				if (AIGame.gravity) {
					//we have a node, we need its position on the board:
					int[] tempPos = AIGame.FindNode (source);
					if (tempPos != null) {
						int[] pos = AIGame.TransposeForGravity(tempPos);
						AIGame.buttons[pos[0]][pos[1]].setText(AIGame.humanButtonText);
					}
				} else {
					source.setText(AIGame.humanButtonText);
				}
				
				if (!AIGame.RemainingMovesExist()) { //did the human fill the last box?
					headerLabel.setText("Draw.");
					JOptionPane.showMessageDialog(frame, "Game is a draw.", "Draw", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					AIGame.SetButtonsEnabled(true);
					processingHumanMove = false;
					return;
				}
				
				int winnerCheck = AIGame.checkForWinner();
				if (winnerCheck != 0) {//we have a winner
					gameOver = true;
					if (winnerCheck == 1) {
						headerLabel.setText("You won.");
						JOptionPane.showMessageDialog(frame, "Congratulations!\nYOU won!", "Victory!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					} else {
						headerLabel.setText("You lost.");
						JOptionPane.showMessageDialog(frame, "Sorry.\nAI won.", "Defeat!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					}
					AIGame.SetButtonsEnabled(true);
					processingHumanMove = false;
					return;
				}
			
				//Start AI thread
				Date startDate = new Date();
				Date currentDate = null;
				Date elapsed = null;
				headerLabel.setText("AI is thinking...");
				headerLabel.paintImmediately(headerLabel.getVisibleRect());
				try {
					Thread aiThread = new Thread(aiPlayer);
					aiThread.start();
					while(true) {
						if (!aiThread.isAlive()) {
							break;
						} else {
							Thread.sleep(100);
						}
					}
				} catch (Exception ex) {
					System.out.println(ex);
				}
				currentDate = new Date();
				elapsed = new Date(currentDate.getTime() - startDate.getTime());
				
				/*===================*
				ADDED TO AVOID COMPILER WARNINGS
				*====================*/
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(elapsed);
				
				headerLabel.setText("Your turn. AI took " + calendar.get(Calendar.SECOND) + "s");
				headerLabel.paintImmediately(headerLabel.getVisibleRect());
				
				if (!AIGame.RemainingMovesExist()) { //did the AI fill the last box?
					headerLabel.setText("Draw.");
					JOptionPane.showMessageDialog(frame, "Game is a draw.", "Draw", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					AIGame.SetButtonsEnabled(true);
					return;
				}
				
				winnerCheck = AIGame.checkForWinner();
				if (winnerCheck != 0) {//we have a winner
					gameOver = true;
					if (winnerCheck == 1) {
						headerLabel.setText("You won.");
						JOptionPane.showMessageDialog(frame, "Congratulations!\n\nYOU won!", "Victory!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					} else {
						headerLabel.setText("You lost.");
						JOptionPane.showMessageDialog(frame, "Sorry.\n\nAI won.", "Defeat!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
					}
					AIGame.SetButtonsEnabled(true);
					processingHumanMove = false;
					return;
				} else {
					AIGame.SetButtonsEnabled(true);
				}
			}
			processingHumanMove = false;
		}
	}
	
	//pass in location of move requested. If(gravity), will return location of lowest available node. Else, return same location.
	public static int[] TransposeForGravity (int[] index) {
		if (AIGame.gravity) {
			int[] retVal = new int[2];
			for (int heightAboveBottom = AIGame.height - 1; heightAboveBottom >= 0; heightAboveBottom--) {
				if (AIGame.buttons[index[0]] [heightAboveBottom].getText().equals(AIGame.blankButtonText)) {
					retVal[0] = index[0];
					retVal[1] = heightAboveBottom;
					return retVal;
				}
			}
			return index; //in case the row is full
		} else {
			return index;
		}
	}
	
	public static boolean RemainingMovesExist () {
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (AIGame.buttons[i][j].getText().equals(AIGame.blankButtonText)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static int[] FindNode (JButton node) {
		int[] tempPos = new int[2];
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (AIGame.buttons[i][j].equals(node)) {
					tempPos[0] = i;
					tempPos[1] = j;
					return tempPos;
				}
			}
		}
		return null;
	}
	
	public static void SetButtonsEnabled (boolean enabled) {
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				AIGame.buttons[i][j].setEnabled(enabled);
			}
		}
	}
	
	private void Reset () {
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				aiPlayer.Reset();
				//reset board:
				AIGame.buttons[i][j].setText(AIGame.blankButtonText);
				headerLabel.setText("Your turn.");
			}
		}
		gameOver = false;
	}
	
	//check for win condition
	//0 = none, 1 = human, 2 = AI
	public static int checkForWinner () {
		//this method works great, but might use some performance boosts.
		int numInARowToWin = AIGame.winLength;
		int count_Human = 0;
		int count_AI = 0;
		
		//check vertical
		for (int i = 0; i < AIGame.width; i++) {
			for (int j = 0; j < AIGame.height; j++) {
				if (AIGame.buttons[i][j].getText().equals(AIGame.humanButtonText)) {
					count_Human++;
					count_AI = 0;
					if (count_Human == numInARowToWin) {
						return 1;
					}
				} else if (AIGame.buttons[i][j].getText().equals(AIGame.aiButtonText)) {
					count_Human = 0;
					count_AI++;
					if (count_AI == numInARowToWin) {
						return 2;
					}
				} else {
					count_Human = 0;
					count_AI = 0;
				}
			}
			count_Human = 0;
			count_AI = 0;
		}
		
		//check horizontal
		for (int j = 0; j < AIGame.height; j++) {
			for (int i = 0; i < AIGame.width; i++) {
				if (AIGame.buttons[i][j].getText().equals(AIGame.humanButtonText)) {
					count_Human++;
					count_AI = 0;
					if (count_Human == numInARowToWin) {
						return 1;
					}
				} else if (AIGame.buttons[i][j].getText().equals(AIGame.aiButtonText)) {
					count_Human = 0;
					count_AI++;
					if (count_AI == numInARowToWin) {
						return 2;
					}
				} else {
					count_Human = 0;
					count_AI = 0;
				}
			}
			count_Human = 0;
			count_AI = 0;
		}
		
		//check sloping
		int widthIteration = AIGame.width - numInARowToWin + 1;
		int heightIteration = AIGame.height - numInARowToWin + 1;
		for (int i = 0; i < widthIteration; i++) {
			for (int j = 0; j < heightIteration; j++) {
				boolean humanWins = true;
				for (int k = 0; k < numInARowToWin; k++) {
					if (!AIGame.buttons[i+k][j+k].getText().equals(AIGame.humanButtonText)) {
						humanWins = false;
						break;
					}
				}
				if (humanWins) {
					return 1;
				} 
				humanWins = true;
				for (int k = 0; k < numInARowToWin; k++) {
					if (!AIGame.buttons[i+(numInARowToWin - 1 - k)][j+k].getText().equals(AIGame.humanButtonText)) {
						humanWins = false;
						break;
					}
				}
				if (humanWins) {
					return 1;
				} 
				
				boolean aiWins = true;
				for (int k = 0; k < numInARowToWin; k++) {
					if (!AIGame.buttons[i+k][j+k].getText().equals(AIGame.aiButtonText)) {
						aiWins = false;
						break;
					}
				}
				if (aiWins) {
					return 2;
				} 
				aiWins = true;
				for (int k = 0; k < numInARowToWin; k++) {
					if (!AIGame.buttons[i+(numInARowToWin - 1 - k)][j+k].getText().equals(AIGame.aiButtonText)) {
						aiWins = false;
						break;
					}
				}
				if (aiWins) {
					return 2;
				} 
			}
		}
		

		return 0;
	}
	
	
	public static void main (String[] args) {
		Scanner in = new Scanner(System.in);
		AIGame game = new AIGame();
		
		System.out.print("Enter board width: ");
	    AIGame.width = in.nextInt();
	    if (AIGame.width < 1 || AIGame.width > 20) {
	    	System.out.println("ERROR: width must be greater than 0 and less than 20.");
	    	return;
	    }
		System.out.print("Enter board height: ");
	    AIGame.height = in.nextInt();
	    if (AIGame.height < 1 || AIGame.height > 20) {
	    	System.out.println("ERROR: height must be greater than 0 and less than 20.");
	    	return;
	    }
		System.out.print("Number in a row to win: ");
	    AIGame.winLength = in.nextInt();
	    if (!(AIGame.winLength > 0 && AIGame.winLength <= Math.max(AIGame.width, AIGame.height))) {
	    	System.out.println("ERROR: the number of rows to win must be greater than 0 and less than max(width, height).");
	    	return;
	    }
		System.out.print("Gravity? (Y or N): ");
		String gravityInput = in.next();
	    if (gravityInput.toUpperCase().contains("Y") || gravityInput.toUpperCase().contains("YES") || gravityInput.contains("1")) {
	    	AIGame.gravity = true;
	    }
	    
	    game.aiPlayer = new AI();
	    
		game.frame.setTitle(game.authorName + "'s AI Game " + AIGame.width + "x" + AIGame.height + ", g = " + AIGame.gravity);
		
		URL imgURL = game.getClass().getResource("gameicon.png");
		AIGame.icon = new ImageIcon(imgURL, "main icon");
		game.frame.setIconImage(AIGame.icon.getImage());
	    AIGame.buttons = new JButton[AIGame.width][AIGame.height];
	    game.frame.getContentPane().setLayout(new BoxLayout(game.frame.getContentPane(), BoxLayout.Y_AXIS));
	    game.headerLabel = new JLabel ("Your turn.");
	    JPanel header = new JPanel();
	    header.add(game.headerLabel);
	    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
	    game.frame.getContentPane().add(header);
	    game.board.setLayout(new BoxLayout(game.board, BoxLayout.X_AXIS));
	    game.frame.getContentPane().add(game.board);
	    
	    JButton restartButton = new JButton(AIGame.restartButtonText);
	    restartButton.addActionListener(game);
	    game.footer.add(restartButton);
	    game.footer.setLayout(new BoxLayout(game.footer, BoxLayout.X_AXIS));
	    game.frame.getContentPane().add(game.footer);
	    
		for (int i = 0; i < AIGame.width; i++) {
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			game.board.add(p);
			for (int j = 0; j < AIGame.height; j++) {
				AIGame.buttons[i][j] = new JButton(AIGame.blankButtonText);
				AIGame.buttons[i][j].addActionListener(game);
				p.add(AIGame.buttons[i][j]);
			}
		}

		game.frame.pack();
		game.frame.setVisible(true);
		
		in.close();
	}
}
