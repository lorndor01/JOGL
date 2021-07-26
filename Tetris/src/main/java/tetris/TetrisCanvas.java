package tetris;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.joml.Rectangled;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import lib342.Colors;
import lib342.GraphicsWindowFP;
import lib342.ViewportLocation;
import lib342.opengl.Constants.PrimitiveType;
import lib342.opengl.Utilities;
import mygraphicslib.McFallGLUtilities;
import mygraphicslib.Polyline;
import mygraphicslib.PolylineCollection;
import mygraphicslib.SoundPlayer;


/**
 * The canvas for my Tetris game. It will be divided into 3 parts: 1.)The left side
 * where the game will be played. 2.) The right side that will show the current and next piece.
 * 3.) A footer that will hold some swing controls to show information about the game.
 * @author Liam Orndorff
 *
 */
public class TetrisCanvas extends GraphicsWindowFP {
	private final double GAME_BOARD_HEIGHT = 18;
	private final double GAME_BOARD_WIDTH = 12;
	private int piecesPlaced;
	private int linesFinished;
	private JPanel footer;
	private JLabel pieceCounter;
	private JLabel lineCounter;
	private Tetronimoes currentPiece;
	private Tetronimoes nextPiece;
	private Tetronimoes currentPieceCopy;
	private Tetronimoes nextPieceCopy;
	private boolean firstPiece;
	private boolean[][] gameBoard;
	private Timer timer;
	private HashMap<Integer, float[]> colorMap;
	private boolean testMode;
	private boolean isPaused;
	private JWindow startWindow;
	private boolean start;
	private ArrayList<Tetronimoes> gamePieces;
	private KeyHandler handler;
	private JFrame pausedFrame;
	private int timeInCycle;
	private SoundPlayer soundPlayer;
	public TetrisCanvas(boolean testMode) {
		super(testMode ? "Tetris -- Test Mode" : "Tetris");
		this.testMode = testMode;
		timeInCycle =1;
		start = false;
		firstPiece = true;
		soundPlayer = new SoundPlayer("sounds/tetris_theme.wav");
		try {
			soundPlayer.play();
			soundPlayer.loopForever();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		piecesPlaced =0;
		linesFinished = 0;
		handler = new KeyHandler();
		gameBoard = new boolean[(int) GAME_BOARD_WIDTH][(int) GAME_BOARD_HEIGHT];
		gamePieces = new ArrayList<Tetronimoes>();
		generateColorMap();
		this.addKeyListener(handler);
		canvas.addKeyListener(handler);
		add(generateFooter(), BorderLayout.SOUTH);
		currentPiece = generateRandomPiece();
		nextPiece = generateRandomPiece();
		if(!testMode) {
			timer = new Timer(750, new ForceDown());
		    timer.setRepeats(true);
		    timer.start(); 
		}
		isPaused = false;

	}
	@Override
	public void display(GLAutoDrawable arg0) {
		  GL2 gl = (GL2) canvas.getGL();
		  McFallGLUtilities.clearColorBuffer(gl, Colors.WHITE);
		  McFallGLUtilities.setWorldWindow(gl, 0, 5,0,10);
		  Rectangle viewport = McFallGLUtilities.getMaximumViewport(Utilities.getCanvasWidth(canvas)-20, Utilities.getCanvasHeight(canvas)-20, 12.0/18.0, ViewportLocation.BottomRight);
		  gl.glViewport(viewport.width+5,10, viewport.width, viewport.height-20);
		  drawPreviewPane(gl);
		  
		  McFallGLUtilities.setWorldWindow(gl, 0, 12, 0, 18);
		  viewport = McFallGLUtilities.getMaximumViewport(Utilities.getCanvasWidth(canvas)-20, Utilities.getCanvasHeight(canvas)-20, 12.0/18.0,ViewportLocation.BottomLeft);
		  gl.glViewport(10,10, viewport.width-20, viewport.height-20);
		  drawGridOutline(gl);
		  drawGrid(gl);
		  drawGamePieces(gl);
		  currentPiece.draw(gl);
		  if(!isPaused) {
			  canMoveDown();
		  }
	}
	
	/**
	 * Returns a footer of all the Swing controls to be added to the canvas.
	 * @return JPanel Footer
	 */
	public JPanel generateFooter() {
		footer = new JPanel(new FlowLayout());
		String piecesPlacedString = String.valueOf(piecesPlaced);
		pieceCounter = new JLabel("Pieces Placed: " + piecesPlacedString);
		lineCounter = new JLabel("Lines Completed: " + linesFinished);
		footer.add(pieceCounter);
		footer.add(lineCounter);
		return footer;
	}
	
	public void drawGrid(GL2 gl) {
		PolylineCollection horizontalLines = new PolylineCollection();
		PolylineCollection verticalLines = new PolylineCollection();
		Polyline currentLine;
		//loop to add horizontal lines.
		for(int i =1; i<12; i++) {
			currentLine = new Polyline(i,0,i,18);
			horizontalLines.add(currentLine);
		}
		//loop to add vertical lines.
		for(int i =1; i<18; i++) {
			currentLine = new Polyline(0,i,12,i);
			verticalLines.add(currentLine);
		}
		gl.glLineWidth(1f);
		horizontalLines.draw(gl, PrimitiveType.LINES, Colors.BLACK);
		verticalLines.draw(gl, PrimitiveType.LINES, Colors.BLACK);
	}
	
	public void drawGridOutline(GL2 gl) {
		gl.glLineWidth(1f);
		gl.glColor3fv(Colors.BLACK, 0);
		  gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		  {
		    gl.glVertex2d(0, 0);
		    gl.glVertex2d(12, 0);
		    gl.glVertex2d(12, 18);
		    gl.glVertex2d(0, 18);
		  }
		  gl.glEnd();
	}
	
	private final class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			GL2 gl = (GL2)canvas.getGL();
			if(e.getKeyChar()=='q' || e.getKeyChar()=='Q') {
				System.exit(0);
			}
			if(e.getKeyCode()==KeyEvent.VK_RIGHT||e.getKeyChar()=='l') {
				if(currentPiece.canMoveRight(gameBoard)) {
					currentPiece.shiftRight();
					currentPiece.draw(gl);
					canvas.repaint();
				}
			}
			
			if(e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyChar()=='h') {
				if(currentPiece.canMoveLeft(gameBoard)) {
					currentPiece.shiftLeft();
					currentPiece.draw(gl);
					canvas.repaint();
				}
			}
			
			if(e.getKeyChar()=='d'||e.getKeyChar()=='j') {
				currentPiece.moveDown();
				currentPiece.draw(gl);
				canvas.repaint();
			}
			if(e.getKeyChar()=='D') {
				while(canMoveDown()) {
					currentPiece.moveDown();
					currentPiece.draw(gl);
					canvas.repaint();
				}
				
			}
			if(e.getKeyChar()=='p') {
				pause();
				if(isPaused) {
					timer.stop();
					generatePausedWindow();
				}
				else {
					pausedFrame.setVisible(false);
					timer.start();
				}
				}
			if(e.getKeyCode()==KeyEvent.VK_DOWN||e.getKeyChar()=='r') {
				if(currentPiece.canRotateClockwise(gameBoard)!=false) {
					currentPiece.rotateClockWise(gameBoard);
					currentPiece.draw(gl);
					canvas.repaint();
				}
				else {
					canvas.repaint();
				}
			}
			if(e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_R) {
					currentPiece.rotateCounterClockwise(gameBoard);
					currentPiece.draw(gl);
					canvas.repaint();
			}
			if((e.getKeyCode()==KeyEvent.VK_SPACE)||e.getKeyChar()=='s') {
				if(testMode) {
					currentPiece.reflect(gameBoard);
					currentPiece.draw(gl);
					canvas.repaint();
				}
			}
			if((e.getKeyChar()=='*')) {
				if(testMode) {
					printBoard();
				}
			}
		}
	}

	private Tetronimoes generateRandomPiece() {
		Random random = new Random();
		int pieceNumber;
		if(!testMode) {
			pieceNumber = random.nextInt(7)+1;
		}
		else {
			pieceNumber = timeInCycle;
			timeInCycle +=1;
			if(timeInCycle ==5) {
				timeInCycle = 1;
			}
		}
		float[] color = generateRandomColor();
		if(firstPiece) {
			currentPieceCopy = new Tetronimoes(pieceNumber, 0, 5, 10, 0, color);
			currentPieceCopy.generateRectangles(1, 6);
			firstPiece = false;
		}
		else{
			nextPieceCopy = new Tetronimoes(pieceNumber, 0, 5, 10, 0, color);
			nextPieceCopy.generateRectangles(1, 1);
		}
		Tetronimoes piece = new Tetronimoes(pieceNumber, 0, GAME_BOARD_WIDTH, GAME_BOARD_HEIGHT, 0, color);
		piece.generateRectangles(0, 15);
		boolean canGenerate = true;
		ArrayList<Rectangled> rectangles = piece.getRectangles();
		for(Rectangled rectangle : rectangles) {
			if(gameBoard[(int)rectangle.minX][(int)rectangle.minY]) {
				canGenerate = false;
			}
		}
		if(!canGenerate) {
			soundPlayer.stop();
			soundPlayer.switchFile("sounds/title_music.wav");
			try {
				soundPlayer.play();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			generateGameOverWindow();
			isPaused = true;
			canvas.removeKeyListener(handler);
			if(!testMode) {
				timer.stop();
			}
		}
		return piece;
	}
	
	public class ForceDown implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			currentPiece.moveDown();
            canvas.repaint();
		}
		
	}
	
	public void drawGamePieces(GL2 gl) {
		for(Tetronimoes gamePiece : gamePieces) {
			gamePiece.draw(gl);
		}
	}
	
	public void drawPreviewPane(GL2 gl) {
		gl.glColor3fv(Colors.BLACK,0);
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		  {
			  gl.glVertex2d(0,0);
			  gl.glVertex2d(0,10);
			  gl.glVertex2d(5,10);
			  gl.glVertex2d(5,0);
		  }
		  gl.glEnd();
		  gl.glBegin(PrimitiveType.LINES.getValue());
		  {
			  for(int i =0; i<5; i++) {
				  gl.glVertex2d(i, 0);
				  gl.glVertex2d(i, 10);
			  }
			  for(int i =0; i<10; i++) {
				  gl.glVertex2d(0, i);
				  gl.glVertex2d(5, i);
			  }
		  }
		  gl.glEnd();
		  currentPieceCopy.draw(gl);
		  nextPieceCopy.draw(gl);
		

		  
	}
	
	/**
	 * Checks to see if there is open space under the current game pieces. If not, populateBoard() is called which changes the currentpiece to next piece.
	 */
	public boolean canMoveDown() {
		boolean canMoveDown = true;
		ArrayList<Rectangled> rectangles = currentPiece.getRectangles();
		for(Rectangled rectangle : rectangles) {
			int x = (int) rectangle.minX;
			int y = (int) rectangle.minY;
			if(currentPiece.canMoveDown()&&(y-1)>=0) {
				if(gameBoard[x][y-1]==true) {
					canMoveDown = false;
				}
			}
		}
		if(!currentPiece.canMoveDown()) {
			populateBoard(rectangles);
			piecesPlaced++;
			updatePiecesPlacedLabel();
		}
		if((canMoveDown == false)&&currentPiece.canMoveDown()) {
			populateBoard(rectangles);
			piecesPlaced++;
			updatePiecesPlacedLabel();
		}
		return canMoveDown;
	}
	
	public void populateBoard(ArrayList<Rectangled> rectangles){
		for(Rectangled rectangle: rectangles) {
			int x = (int) rectangle.minX;
			int y = (int) rectangle.minY;
			gameBoard[x][y]=true;
		}
		gamePieces.add(currentPiece);
		currentPiece = nextPiece;
		currentPieceCopy = nextPieceCopy;
		currentPieceCopy.getRectangles().clear();
		currentPieceCopy.generateRectangles(1, 6);
		nextPiece = generateRandomPiece();
		for(int j = 0; j<GAME_BOARD_HEIGHT; j++ ) {
			boolean lineFilled = true;
			for(int i =0; i<GAME_BOARD_WIDTH; i++) {
				if(gameBoard[i][j]==false) {
					lineFilled= false;
				}
			}
			if(lineFilled==true) {
				linesFinished++;
				Iterator<Tetronimoes> gamePiecesItr = gamePieces.iterator();
				while(gamePiecesItr.hasNext()) {
					Tetronimoes gamePiece = gamePiecesItr.next();
					Iterator<Rectangled> rectanglesItr = gamePiece.getRectangles().iterator();
					while(rectanglesItr.hasNext()) {
						Rectangled rectangle = rectanglesItr.next();
						if(rectangle.minY==j) {
							rectanglesItr.remove();
							gameBoard[(int) rectangle.minX][(int) rectangle.minY]=false;
						}
					}
				}
				gamePiecesItr = gamePieces.iterator();
				while(gamePiecesItr.hasNext()) {
					Tetronimoes gamePiece = gamePiecesItr.next();
					Iterator<Rectangled> itr = gamePiece.getRectangles().iterator();
					while(itr.hasNext()) {
						Rectangled rectangle = itr.next();
						if(rectangle.minY!=0) {
							gameBoard[(int) rectangle.minX][(int) rectangle.minY]=false;
							rectangle.translate(0,-1);
							gameBoard[(int)rectangle.minX][(int) rectangle.minY]=true;
						}
					}
				}
				j--;
			}
		}
		canvas.repaint();
	}

	public float[] generateRandomColor() {
		Random random = new Random();
		int value = random.nextInt(4);
		
		if(colorMap.containsKey(value)) {
			return colorMap.get(value);
		}
		else {
			return Colors.BLACK;
		}
	}
	
	public void updatePiecesPlacedLabel() {
		pieceCounter.setText("Pieces Placed: " + String.valueOf(piecesPlaced));
		lineCounter.setText("Lines Completed: " + String.valueOf(linesFinished));
	}
	
	public void generateColorMap() {
		colorMap = new HashMap<Integer, float[]>();
		colorMap.put(0,Colors.RED);
		colorMap.put(1,Colors.CYAN);
		float[] pink = new float[3]; pink[0]=128; pink[1] = 0; pink[2] =128;
		colorMap.put(2, pink);
		colorMap.put(3,Colors.GREEN);
	}

	public void pause() {
		isPaused = !isPaused;
	}

	public void init(GLAutoDrawable drawable) {
		try {
		SwingUtilities.invokeLater(() -> canvas.requestFocus());
		}
		catch (Exception e) {}
		}

	public void generateGameOverWindow() {
		JFrame frame = new JFrame();
		frame.setSize(400,400);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridLayout(4, 1));
	      frame.addWindowListener(new WindowAdapter() {
	         public void frameClosing(WindowEvent frameEvent){
	            System.exit(0);
	         }        
	      });
	      JLabel gameOver = new JLabel("GAME OVER!", JLabel.CENTER);
	      JLabel stats = new JLabel("Stats:", JLabel.CENTER);
	      JLabel lines = new JLabel("Lines Completed: " + linesFinished, JLabel.CENTER);
	      JLabel pieces = new JLabel("Pieces Placed: " + piecesPlaced, JLabel.CENTER);
	      frame.add(gameOver);
	      frame.add(stats);
	      frame.add(lines);
	      frame.add(pieces);
		frame.setVisible(true);
	}

	public void generatePausedWindow() {
		pausedFrame = new JFrame();
		pausedFrame.setSize(400,400);
		pausedFrame.setLocationRelativeTo(null);
		JLabel paused = new JLabel("PAUSED", JLabel.CENTER);
		pausedFrame.add(paused);
		pausedFrame.setVisible(true);
	}

	public void printBoard() {
		System.out.flush();
		for(int i = (int) (GAME_BOARD_HEIGHT-1); i>=0; i--) {
			System.out.println();
			for(int j = 0; j<GAME_BOARD_WIDTH; j++) {
				int output = 0;
				if(gameBoard[j][i]) {
					output =1;
				}
				System.out.print(output + " ");
			}
		}
	}
	
	public void resetCurrentPieceLocation() {
		for(Rectangled rectangle : currentPiece.getRectangles()) {
			gameBoard[(int) rectangle.minX][(int) rectangle.minY]=false;
		}
	}
	public void setCurrentPieceLocation() {
		for(Rectangled rectangle : currentPiece.getRectangles()) {
			gameBoard[(int) rectangle.minX][(int) rectangle.minY]=false;
		}
	}

	public void parseFile(String filePath) {
		System.out.println("Parsing file...");
		File file = new File(filePath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String commandLine;
			while((commandLine=reader.readLine())!=null) {
				String[] commands = commandLine.split(" ");
				for(String command : commands) {
					if(command.length()==2) {
						int repetition = Integer.valueOf(command.charAt(0));
						int i = 0;
						while(i<repetition) {
							runCommand(command);
							i++;
						}
					}
					else {
						runCommand(command);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void runCommand(String command) {
		
		switch(command) {
		case "l": if(currentPiece.canMoveRight(gameBoard)) {currentPiece.shiftRight();}; break;
		case "d": while(canMoveDown()) {currentPiece.moveDown();}; break;
		case "r": currentPiece.rotateClockWise(gameBoard);break;
		case "D": while(currentPiece.canMoveDown()){currentPiece.moveDown();}; populateBoard(currentPiece.getRectangles()); break;
		case "R": currentPiece.rotateCounterClockwise(gameBoard); break;
		case "s": currentPiece.reflect(gameBoard); break;
		case "h": if(currentPiece.canMoveLeft(gameBoard)) {currentPiece.shiftLeft();}; break;
		default: break;
		}
		canvas.repaint();
	}
}


