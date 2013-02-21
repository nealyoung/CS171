import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class AIGame implements ActionListener {
	public final static boolean USE_DEFAULTS = false;
	
	public final static boolean DEFAULT_ALLOW_GRAVITY = true;
	public final static boolean DEFAULT_HUMAN_GOES_FIRST = false;
	public final static int DEFAULT_WIDTH = 7;
	public final static int DEFAULT_HEIGHT = 6;
	public final static int DEFAULT_WIN_LENGTH = 4;
	
	public final static int BUTTON_WIDTH = 45;
	public final static int BUTTON_HEIGHT = BUTTON_WIDTH;
	
	public final String authorName = "Nealon Young and Nicholas Chihaia";
	public static ImageIcon icon = null;
	public static AI aiPlayer = null;
	public JFrame frame = new JFrame( authorName + "'s AI Game" );
	public JPanel board = new JPanel();
	public static JLabel headerLabel = new JLabel("loading...");
	public JPanel footer = new JPanel();
	
	public static JButton[][] buttons = null;//the game board
	
	//game conditions:
	public static int width = 0;
	public static int height = 0;
	public static int winLength = 1;
	public static boolean gravity = false;
	public static boolean humanGoesFirst = false;
	
	private boolean processingHumanMove = false;
	private boolean gameOver = false;
	
	public static final String restartButtonText = "RESET";
	public static final String blankButtonText = " ";
	public static final String humanButtonText = "H";
	public static final String aiButtonText = "A";
	
	public void actionPerformed(ActionEvent e) 
	{
		JButton source = (JButton)e.getSource();
		if( source.getText().equals(AIGame.restartButtonText) ) 
		{
			Reset();
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
			
			int winnerCheck = AIGame.checkForWinner();
			
			if (winnerCheck != 0) {//we have a winner
				gameOver = true;
				if (winnerCheck == 1) {
					headerLabel.setText("You won");
					JOptionPane.showMessageDialog(frame, "Congratulations!\nYOU won!", "Victory!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				} else {
					headerLabel.setText("You lost");
					JOptionPane.showMessageDialog(frame, "Sorry\nAI won", "Defeat!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				}
				AIGame.SetButtonsEnabled(true);
				processingHumanMove = false;
				return;
			}
			
			if (!AIGame.RemainingMovesExist()) { //did the human fill the last box?
				headerLabel.setText("Draw.");
				JOptionPane.showMessageDialog(frame, "Game is a draw", "Draw", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				AIGame.SetButtonsEnabled(true);
				return;
			}
		
			//Start AI thread
			performAITurn();
			
			winnerCheck = AIGame.checkForWinner();
			if (winnerCheck != 0) {//we have a winner
				gameOver = true;
				if (winnerCheck == 1) {
					headerLabel.setText("You won");
					JOptionPane.showMessageDialog(frame, "Congratulations!\n\nYOU won!", "Victory!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				} else {
					headerLabel.setText("You lost");
					JOptionPane.showMessageDialog(frame, "Sorry\n\nAI won", "Defeat!", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				}
				AIGame.SetButtonsEnabled(true);
				return;
			} else {
				AIGame.SetButtonsEnabled(true);
			}
			if (!AIGame.RemainingMovesExist()) { //did the AI fill the last box?
				headerLabel.setText("Draw.");
				JOptionPane.showMessageDialog(frame, "Game is a draw", "Draw", JOptionPane.PLAIN_MESSAGE, AIGame.icon);
				AIGame.SetButtonsEnabled(true);
				return;
			}
		}
	}
	
	public static void performAITurn()
	{
		//Start AI thread
		long startMillis = System.currentTimeMillis();
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
		
		long currentMillis = System.currentTimeMillis();
		double elapsed = ((double)currentMillis - (double)startMillis) / 1000.0;
		
		headerLabel.setText("Your turn, AI took " + elapsed + " seconds");
		headerLabel.paintImmediately(headerLabel.getVisibleRect());
	}
	
	//pass in location of move requested. If(gravity), will return location of lowest available node. Else, return same location.
	public static int[] TransposeForGravity (int[] index) {
		if (AIGame.gravity) {
			int[] retVal = new int[2];
			for (int i = 0; i < AIGame.height; i++) {
				if (AIGame.buttons[index[0]][i].getText().equals(AIGame.blankButtonText)) {
					retVal[0] = index[0];
					retVal[1] = i;
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
				headerLabel.setText("Your turn");
			}
		}
				
		gameOver = false;
		if( !humanGoesFirst )
		{
			SetButtonsEnabled( false );
			performAITurn();
		}
		SetButtonsEnabled( true );
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
		if( !USE_DEFAULTS )
		{
			System.out.print("Enter board width: ");
		    AIGame.width = in.nextInt();
		    if (AIGame.width < 3) {
		    	System.out.println("ERROR: width must be at least 3.");
		    	return;
		    }
			System.out.print("Enter board height: ");
		    AIGame.height = in.nextInt();
		    if (AIGame.height < 3) {
		    	System.out.println("ERROR: height must be at least 3.");
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
			System.out.print("Human start? (Y or N): ");
			String startInput = in.next();
		    if (startInput.toUpperCase().contains("Y") || startInput.toUpperCase().contains("YES") || startInput.contains("1")) {
		    	humanGoesFirst = true;
		    }
		}
		else
		{
			AIGame.width = DEFAULT_WIDTH;
			AIGame.height = DEFAULT_HEIGHT;
			AIGame.winLength = DEFAULT_WIN_LENGTH;
			AIGame.gravity = DEFAULT_ALLOW_GRAVITY;
			humanGoesFirst = DEFAULT_HUMAN_GOES_FIRST;
		}
		
		Evaluator.initialize();
		
	    game.aiPlayer = new AI();
	    
	    game.frame.setTitle( "Connect " + AIGame.winLength + 
	    					 " [" + AIGame.width + "x" + AIGame.height + ";G=" +
	    					 (AIGame.gravity ? "yes" : "no") + ";F=" + 
	    					 (humanGoesFirst ? humanButtonText : aiButtonText) +"]");
		
		URL imgURL = game.getClass().getResource("gameicon.png");
		AIGame.icon = new ImageIcon(imgURL, "main icon");
		game.frame.setIconImage(AIGame.icon.getImage());
	    AIGame.buttons = new JButton[AIGame.width][AIGame.height];
	    game.frame.getContentPane().setLayout(new BoxLayout(game.frame.getContentPane(), BoxLayout.Y_AXIS));
	    
	    Dimension frameSize = new Dimension( ( BUTTON_WIDTH + 5 )  * AIGame.width + 150, 
	    									 ( BUTTON_HEIGHT + 15 ) * AIGame.height + 50 );

	    game.frame.setPreferredSize(frameSize);
	    game.frame.setMaximumSize(frameSize);
	    game.frame.setMinimumSize(frameSize);
	    game.frame.setResizable( false );
	    
		JLabel headerLabel = new JLabel ();
		
		Dimension headerLabelSize = new Dimension( 150, 20 );
		headerLabel.setMinimumSize( headerLabelSize );
		headerLabel.setMaximumSize( headerLabelSize );
		//headerLabel.setPreferredSize( headerLabelSize );
		headerLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		headerLabel.setText("           Your turn           ");
		//headerLabel.setText("Your turn");
		game.headerLabel = headerLabel;
		JPanel header = new JPanel();
		//header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setAlignmentY(Component.CENTER_ALIGNMENT);
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		header.add(game.headerLabel);
		game.frame.getContentPane().add(header);
	    
	    game.board.setLayout(new BoxLayout(game.board, BoxLayout.X_AXIS));
	    game.frame.getContentPane().add(game.board);
	    
		for (int i = 0; i < AIGame.width; i++) {
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			game.board.add(p);
			for (int j = (AIGame.height - 1); j >= 0; j--) {
			    JButton button = new JButton(AIGame.blankButtonText);
			    button.setForeground(Color.BLACK);
			    button.setBackground(Color.WHITE);
			    Border line = new LineBorder(Color.BLACK);
			    Border margin = new EmptyBorder(5, 15, 5, 15);
		  	    Border compound = new CompoundBorder(line, margin);
			    button.setBorder( compound );
			    Dimension d = new Dimension( BUTTON_WIDTH, BUTTON_HEIGHT );
			    button.setPreferredSize( d );
			    button.setMinimumSize( d );
			    button.setMaximumSize( d );
			    button.setFocusable(false);
			    AIGame.buttons[i][j] = button;

				AIGame.buttons[i][j].addActionListener(game);
				p.add(AIGame.buttons[i][j]);
			}
		}
	    
	    JButton restartButton = new JButton(AIGame.restartButtonText);
	    restartButton.setFocusable(false);
	    restartButton.addActionListener(game);
	    game.footer.add(restartButton);
	    game.frame.getContentPane().add(game.footer);
	    
	    Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
	    int wdwLeft = screenSize.width  / 2 - game.frame.getWidth() / 2;
        int wdwTop  = screenSize.height / 2 - game.frame.getHeight() / 2;

		game.frame.pack();
		game.frame.setLocation(wdwLeft, wdwTop);
		game.frame.setVisible(true);
		
		in.close();
		if( !humanGoesFirst )
		{
			SetButtonsEnabled( false );
			performAITurn();
		}
		SetButtonsEnabled( true );
	}
}
