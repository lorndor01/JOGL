package solitiare;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import graphics.IGameBoardDrawer;
import graphics.IPileDrawer;
import lib342.Colors;
import lib342.GraphicsWindowFP;
import lib342.ViewportLocation;
import lib342.opengl.Constants.PrimitiveType;
import lib342.opengl.Utilities;
import model.AcePile;
import model.Card;
import model.DeckPile;
import model.Denomination;
import model.Pile;
import model.PileLocation;
import model.Suit;
import model.TemporaryPile;
import mygraphicslib.GLUtilities;
import mygraphicslib.McFallGLUtilities;
import mygraphicslib.SoundPlayer;

public class SolitaireWindow extends GraphicsWindowFP implements IGameController{
	
	private final double X_SCALING_FACTOR = Utilities.getDPIScalingFactors(canvas).x;
	private final double Y_SCALING_FACTOR = Utilities.getDPIScalingFactors(canvas).y;
	public static final double WORLD_LEFT = 0;
	public static final double WORLD_RIGHT = 10.5;
	public static final double WORLD_BOTTOM = 0;
	public static final double WORLD_TOP = 10.5;
	private final double MARGIN_OF_ERROR = .5;
	private final int BORDER_WIDTH = 10;
	
	private Configuration config;
	private IGameBoardDrawer gameBoardDrawer;
	private IPileDrawer pileDrawer;
	private KeyStrokeMapper keyStrokeMapper;
	private SoundPlayer soundPlayer;
	
	private MainKeyAdapter firstKeyAdapter;
	private SecondKeyAdapter secondKeyAdapter;
	private ThirdKeyAdapter thirdKeyAdapter;
	private FourthKeyAdapter fourthKeyAdapter;
	
	private MouseHandler firstMouseHandler;
	private SecondMouseHandler secondMouseHandler;
	
	private ArrayList<TemporaryPile> tempPiles;
	private ArrayList<AcePile> acePiles;
	private DeckPile deck;
	private DeckPile viewPile;
	private Pile firstClickedPile;
	
	private Rectangle  viewportRec;
	
	private double mouseX;
	private double mouseY;
	private boolean isBeingDragged;
	private boolean isInRange;
	
	public SolitaireWindow(boolean testMode) {
		super(testMode ? "Solitaire - TestMode" : "Solitaire", 900, 900);
		
		//canvas.addKeyListener();
		config = Configuration.getInstance();
		gameBoardDrawer = config.getGameBoardDrawer();
		pileDrawer = config.getPileDrawer();
		keyStrokeMapper = new KeyStrokeMapper();
		soundPlayer = new SoundPlayer("cardSlide8.wav");

		tempPiles = new ArrayList<TemporaryPile>();
		acePiles = new ArrayList<AcePile>();
		deck = new DeckPile(PileLocation.Draw);
		if(testMode) {
			deck.setPileToNonRandomDeck();
		}
		else {
			deck.setPileToRandomDeck();
		}
		viewPile = new DeckPile(PileLocation.View);
		addTemporaryPiles();
		addAcePiles();
		dealCards();
		
		firstKeyAdapter = new MainKeyAdapter();
		secondKeyAdapter = new SecondKeyAdapter();
		thirdKeyAdapter = new ThirdKeyAdapter();
		fourthKeyAdapter = new FourthKeyAdapter();
		firstMouseHandler = new MouseHandler();
		secondMouseHandler = new SecondMouseHandler();
		
		canvas.addKeyListener(firstKeyAdapter);
		canvas.addMouseListener(firstMouseHandler);
		
		add(generateHelpButton(), BorderLayout.SOUTH);
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		GL2 gl = (GL2) canvas.getGL();
		float[] lightBlue = new float[3];
		lightBlue[0] = 135/255f; lightBlue[1] = 206/255f; lightBlue[2] = 235/255f;
		GLUtilities.clearColorBuffer(gl, lightBlue);
		McFallGLUtilities.setWorldWindow(gl, WORLD_LEFT, WORLD_RIGHT, WORLD_BOTTOM, WORLD_TOP);
		
		Rectangle viewport = McFallGLUtilities.getMaximumViewport(canvas.getWidth()-BORDER_WIDTH*2, (int)(canvas.getHeight())-BORDER_WIDTH*2, 1.0, ViewportLocation.Center);
		int left = (int)(((canvas.getWidth()/2.0)-(viewport.getWidth()/2.0))*X_SCALING_FACTOR);
		int right = (int)(((canvas.getWidth()/2.0)+(viewport.getWidth()/2.0))*X_SCALING_FACTOR)- left;
		int bottom = (int)(((canvas.getHeight()/2)-(viewport.getHeight()/2.0))*Y_SCALING_FACTOR);
		int top = (int)(((canvas.getHeight()/2.0)+(viewport.getHeight()/2.0))*Y_SCALING_FACTOR)-bottom;
		
		
		gl.glViewport(left, bottom, right, top);
		viewportRec = new Rectangle((int)(left/X_SCALING_FACTOR), BORDER_WIDTH+(int)((viewport.getHeight())),(int) (right/X_SCALING_FACTOR), (int)(viewport.getHeight()));
		gameBoardDrawer.drawGameBoard(gl);
		
		drawPiles(gl);
		for(AcePile pile : acePiles) {
			if(pile.size()==13) {
				pileDrawer.drawGreenCheck(gl, pile);
			}
		}
		
		if(isBeingDragged&&isInRange) {
			gl.glColor3fv(new float[]{1,1,0}, 0);
			gl.glLineWidth(10f);
			gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
			{
				GLUtilities.drawArc(gl, mouseX, mouseY, .25, 0, 360);
				isInRange = false;
			}
			gl.glEnd();
		}
		
		TextRenderer renderer = new TextRenderer(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		renderer.beginRendering(canvas.getWidth(), canvas.getHeight());
		renderer.draw("Implemented by: Liam Orndorff", 10, 10);
		renderer.endRendering();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		SwingUtilities.invokeLater(() -> canvas.requestFocus());
	}
	
	public void startWinSequence() {
		soundPlayer.switchFile("Winning-sound.wav");
		try {
			soundPlayer.play();
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		canvas.removeMouseListener(firstMouseHandler);
		canvas.removeKeyListener(firstKeyAdapter);
		
		JFrame winnerFrame = new JFrame("Congratulations!");
		JLabel label = new JLabel();
		label.setSize(50, 10);
		label.setText("You Won!!! Thanks for playing!");
		winnerFrame.setSize(label.getWidth(), label.getHeight());
		winnerFrame.add(label);
		
		
		winnerFrame.setVisible(true);
		
		Timer timer = new Timer(5000, e->{
			System.exit(0);
		});
		timer.start();
	}
	
	/**
	 * Moves the top card from the view pile to a new location.
	 * @param nextLocation Next location for card.
	 */
	public void moveCardFromViewPile(PileLocation nextLocation) {
		for(TemporaryPile pile : tempPiles) {
			if(pile.getLocation()==nextLocation) {
				if(viewPile.getCards().size()>0) {
					Card card = viewPile.getTopCard();
					if(pile.addCard(card)) {
						try {
							soundPlayer.play();
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						viewPile.removeTopCard();
					}
					
				}
			}
		}
		canvas.repaint();
	}
	
	/**
	 * Attempts to add the top card from the view pile to any of the ace piles. If the add is
	 * successful then the card is removed from the view pile.
	 */
	public void moveCardFromViewPileToAcePile() {
		if(viewPile.getCards().size()>0) {
			Card card = viewPile.getTopCard();
			for(AcePile pile : acePiles) {
				if(pile.addCard(card)) {
					try {
						soundPlayer.play();
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					viewPile.removeTopCard();
				}
			}
		}
		canvas.repaint();
		if(areAcePilesFull()) {
			startWinSequence();
		}
	}
	
	/**
	 * Attempts to move the topCard from a temporary and add it to any of the ace piles.
	 * @param location location of the card.
	 */
	public void moveCardFromTemporaryPileToAcePile(PileLocation location) {
		for(TemporaryPile tempPile : tempPiles) {
			if(tempPile.getLocation()== location && tempPile.size()>0) {
				if(!tempPile.getTopCard().isFaceUp()) {
					tempPile.getTopCard().turnOver();
				}
				else{
					Card card = tempPile.getTopCard();
					for(AcePile acePile : acePiles) {
						if(acePile.addCard(card)) {
							try {
								soundPlayer.play();
							} catch (LineUnavailableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tempPile.removeTopCard();
						}
					}
				}
			}
		}
		canvas.repaint();
		if(areAcePilesFull()) {
			startWinSequence();
		}
	}
	
	/**
	 * Attempts to move the top card from the deck to the view pile. If the deck is empty then
	 * the deck is reloaded instead.
	 */
	public void moveCardFromDrawToViewPile() {
		if(deck.size()==0 && viewPile.size()>0){
			try {
				soundPlayer.switchFile("shuffle.wav");
				soundPlayer.play();
				soundPlayer.switchFile("cardSlide8.wav");
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(viewPile.getCards().size()>0) {
				int viewPileSize = viewPile.getCards().size();
				Card card = viewPile.getCard(viewPileSize-1);
				card.turnOver();
				deck.addCard(card);
				viewPile.getCards().remove(viewPileSize-1);
				
			}
		}
		if(deck.size()==0 && viewPile.size()==0) {
			return;
		}
		else {
			try {
				soundPlayer.play();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deck.moveTopCard(viewPile);
		}
		canvas.repaint();
	}
	
	/**
	 *Attempts to move a given number of cards from a temporary pile to another temporary pile.
	 *@param location Location of the cards to be moved.
	 *@param nextLocation Next location of the cards.
	 *@param numOfCards Number of cards to try to move.
	 */
	public void moveCardToAndFromTemporaryPile(PileLocation location, PileLocation nextLocation, Denomination startingDeno) {
		int sourceIndex = 0;
		for(int i =0; i<tempPiles.size(); i++) {
			if(tempPiles.get(i).getLocation()==location) {
				sourceIndex = i;
				break;
			}
		}
		
		int destinationIndex = 0;
		for(int i =0; i<tempPiles.size(); i++) {
			if(tempPiles.get(i).getLocation()==nextLocation) {
				destinationIndex = i;
				break;
			}
		}
		int cardStartIndex = -1;
		for(int i = 0; i<tempPiles.get(sourceIndex).size(); i++) {
			if(tempPiles.get(sourceIndex).getCard(i).getDenomination().getValue()==startingDeno.getValue()) {
				
				cardStartIndex = i;
			}
		}
		//Card with the parameter denomination is doesn't exist in the source pile.
		if(cardStartIndex == -1) {
			return;
		}
		else {
			int i = 0;
			int repetitions = tempPiles.get(sourceIndex).size()-cardStartIndex;
			while(i<repetitions) {
				if(tempPiles.get(destinationIndex).addCard(tempPiles.get(sourceIndex).getCard(cardStartIndex))){
					tempPiles.get(sourceIndex).removeCard(cardStartIndex);
				}
				i++;
			}
		}
		canvas.repaint();
	}
	
	/**
	 * Deals cards from the deck to the required piles.
	 */
	public void dealCards() {
		for(int i = 0; i<tempPiles.size(); i++) {
			int j =i;
			while(j<tempPiles.size()) {
				deck.moveTopCard(tempPiles.get(j));
				int newSize = tempPiles.get(j).getCards().size();
				tempPiles.get(j).getCard(newSize-1).move(tempPiles.get(j).getLocation());
				if(j==i) {
					tempPiles.get(j).getCard(j).turnOver();
				}
				j++;
			}
		}
		deck.moveTopCard(viewPile);
		viewPile.getCard(0).move(PileLocation.View);
	}
	
	/**
	 * Draws all the piles in their locations.
	 * @param gl GL2 object.
	 */
	public void drawPiles(GL2 gl) {
		for(Pile pile : acePiles) {
			pileDrawer.drawPile(gl, pile);
		}
		pileDrawer.drawPile(gl, viewPile);
		pileDrawer.drawPile(gl, deck);
		for(Pile tempPile : tempPiles) {
			pileDrawer.drawPile(gl, tempPile);
		}
	}
	
	/**
	 * Adds the seven temporary piles to the tempPiles field.
	 */
	public void addTemporaryPiles() {
		TemporaryPile temp1 = new TemporaryPile(PileLocation.One);
		TemporaryPile temp2 = new TemporaryPile(PileLocation.Two);
		TemporaryPile temp3 = new TemporaryPile(PileLocation.Three);
		TemporaryPile temp4 = new TemporaryPile(PileLocation.Four);
		TemporaryPile temp5 = new TemporaryPile(PileLocation.Five);
		TemporaryPile temp6 = new TemporaryPile(PileLocation.Six);
		TemporaryPile temp7 = new TemporaryPile(PileLocation.Seven);
		tempPiles.add(temp1);
		tempPiles.add(temp2);
		tempPiles.add(temp3);
		tempPiles.add(temp4);
		tempPiles.add(temp5);
		tempPiles.add(temp6);
		tempPiles.add(temp7);
	}

	/**
	 * Adds all four ace piles to the acePiles field.
	 */
	public void addAcePiles() {
		AcePile ace1 = new AcePile(PileLocation.Ace4, Suit.Club);
		AcePile ace2 = new AcePile(PileLocation.Ace3, Suit.Diamond);
		AcePile ace3 = new AcePile(PileLocation.Ace1, Suit.Heart);
		AcePile ace4 = new AcePile(PileLocation.Ace2, Suit.Spade);
		
		acePiles.add(ace1);
		acePiles.add(ace2);
		acePiles.add(ace3);
		acePiles.add(ace4);
	}

	/**
	 * Prints a textual representation of the game's current state.
	 */
	public void printTextualRepresentationOfGame() {
		
		System.out.println("DRAW");
		deck.printPile();
		System.out.println("\n");
		
		System.out.println("VIEW");
		viewPile.printPile();
		System.out.println("\n");
		
		System.out.println("ACE CLUBS");
		acePiles.get(0).printPile();;
		System.out.println("\n");
		
		System.out.println("ACE DIAMONDS");
		acePiles.get(1).printPile();;
		System.out.println("\n");
		
		System.out.println("ACE HEARTS");
		acePiles.get(2).printPile();;
		System.out.println("\n");
		
		System.out.println("ACE SPADES");
		acePiles.get(3).printPile();
		System.out.println("\n");
		
		for(int i = 0; i <tempPiles.size(); i++) {
			System.out.println("TEMP " + (i+1));
			tempPiles.get(i).printPile();
			System.out.println("\n");
		}
	}

	/**
	 * Checks to see if any top card from any temporary pile can be moved to an ace pile.
	 * Used by the MainKeyAdapter to decide whether or not to keep listening for the location of a ace pile.
	 * 
	 * @return isThereAvailableMove
	 */
	public boolean isAvailableMoveFromTemporaryToAce() {
		boolean isThereAvailabelMove = false;
		for(TemporaryPile tempPile : tempPiles) {
			if(tempPile.size()>0) {
				Card topCard = tempPile.getTopCard();
				Denomination topCardDeno = topCard.getDenomination();
				for(AcePile acePile : acePiles) {
					if(acePile.size()==0&&topCardDeno == Denomination.Ace) {
						isThereAvailabelMove =true;
					}
					if(acePile.size()>0) {
						Card aceTop = acePile.getTopCard();
						Denomination aceDeno = aceTop.getDenomination();
						if(aceDeno !=Denomination.King) {
							int i = 0;
							while(Denomination.values()[i] != aceDeno) {
								i++;
							}
							if(topCardDeno == Denomination.values()[i-1]) {
								isThereAvailabelMove= true;
							}
						}
					}
					}
			}
			}
	
		return isThereAvailabelMove;
	}
	
	/**
	 * Checks to see the top card on the view pile can move to an ace pile.
	 * @return isAvailabeMove Whether or not their is an available move from a temporary pile to the view pile.
	 */
	public boolean isAvailableMoveFromViewToAce() {
		boolean isAvailableMove = false;
		if(viewPile.size()>0) {
			Card viewTopCard = viewPile.getTopCard();
			for(AcePile acePile : acePiles) {
				if(acePile.size()==0) {
					if(viewTopCard.getDenomination()==Denomination.Ace && acePile.getSuit()== viewTopCard.getSuit()) {
						isAvailableMove = true;
					}
				}
				else {
					Card aceTopCard = acePile.getTopCard();
					if(aceTopCard.getDenomination() != Denomination.King && acePile.getSuit()==viewTopCard.getSuit()) {
						if(viewTopCard.getDenomination()==acePile.nextDenominationNeeded()) {
							isAvailableMove = true;
						}
					}
				}
			}
		}
		
		return isAvailableMove;
	}
	
	/**
	 * Checks to see if all the ace piles are full.
	 * @return areAcePilesFull true if all ace piles are full and false otherwise.
	 */
	public boolean areAcePilesFull() {
		for(AcePile pile : acePiles) {
			if(pile.size()<13) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Handles the main key presses for the game. Switches focus to a
	 * SecondKeyAdapter if the user wants to to perform a multi-key command.
	 * @author Liam Orndorff
	 *
	 */
	public class MainKeyAdapter extends KeyAdapter{
		@Override
		public void keyTyped(KeyEvent e) {
			if(e.getKeyChar()=='q'|| e.getKeyChar() == 'Q') {
				System.exit(0);
			}
			
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
				if(canvas.getMouseListeners()[0] == secondMouseHandler) {
					canvas.removeMouseListener(secondMouseHandler);
					canvas.addMouseListener(firstMouseHandler);
					return;
				}
			}
			
			if(e.getKeyChar()== 'a') {
				boolean isHiddenCard = false;
				for(TemporaryPile pile : tempPiles) {
					if(pile.size()>0) {
						if(!pile.getTopCard().isFaceUp()) {
							isHiddenCard = true;
						}
					}
				}
				if(isAvailableMoveFromTemporaryToAce()||isHiddenCard) {
					keyStrokeMapper.mapFirstKeyStroke(e.getKeyChar(), SolitaireWindow.this);
					canvas.removeKeyListener(this);
					canvas.addKeyListener(secondKeyAdapter);
					return;
				}
			}
			if(e.getKeyChar()=='m') {
				keyStrokeMapper.mapFirstKeyStroke(e.getKeyChar(), SolitaireWindow.this);
				canvas.removeKeyListener(this);
				canvas.addKeyListener(secondKeyAdapter);
				return;
			}
			else {
				keyStrokeMapper.mapFirstKeyStroke(e.getKeyChar(), SolitaireWindow.this);
			}
			
			canvas.repaint();
		}
	}
	
	/**
	 * Handles the second key press for a users multi-key command. 
	 * @author Liam Orndorff
	 *
	 */
	public class SecondKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyChar()== KeyEvent.VK_ESCAPE) {
				canvas.removeKeyListener(this);
				canvas.addKeyListener(firstKeyAdapter);
			}
			else {
				if(keyStrokeMapper.firstKeyStroke=='a') {
					keyStrokeMapper.mapSecondKeyStroke(e.getKeyChar(), SolitaireWindow.this);
					canvas.removeKeyListener(this);
					canvas.addKeyListener(firstKeyAdapter);
					return;
				}
				else if(keyStrokeMapper.firstKeyStroke=='m') {
					keyStrokeMapper.mapSecondKeyStroke(e.getKeyChar(), SolitaireWindow.this);
					canvas.removeKeyListener(this);
					canvas.addKeyListener(thirdKeyAdapter);
				}
				else {
					canvas.removeKeyListener(this);
					canvas.addKeyListener(firstKeyAdapter);
				}
			}
		}
	}
	
	public class ThirdKeyAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyChar()== KeyEvent.VK_ESCAPE) {
				canvas.removeKeyListener(this);
				canvas.addKeyListener(firstKeyAdapter);
			}
			else{
				keyStrokeMapper.mapThirdKeyStroke(e.getKeyChar(), SolitaireWindow.this);
				canvas.removeKeyListener(this);
				canvas.addKeyListener(fourthKeyAdapter);
			}
		}
	}
	
	public class FourthKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyChar()== KeyEvent.VK_ESCAPE) {
				canvas.removeKeyListener(this);
				canvas.addKeyListener(firstKeyAdapter);
			}
			else{
				keyStrokeMapper.mapFourthKeyStroke(e.getKeyChar(), SolitaireWindow.this);
				canvas.removeKeyListener(this);
				canvas.addKeyListener(firstKeyAdapter);
			}
		}
	}
	/**
	 * Handles single click moves and switches to a SecondMouseHandler for multi-click moves.
	 * @author Liam Orndorff
	 *
	 */
	public class MouseHandler extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent e) {
			double xCor = e.getX();
			double yCor = canvas.getHeight()-e.getY();
			double eventWorldX = getXInWorld(xCor);
			double eventWorldY = getYInWorld(yCor);
			//if deck is clicked.
			if(isEventInRange(eventWorldX, eventWorldY, deck.getLocation().getX(), deck.getLocation().getY(), MARGIN_OF_ERROR)) {
				moveCardFromDrawToViewPile();
				return;
			}
			
			//if view pile is clicked.
			if(isEventInRange(eventWorldX, eventWorldY, viewPile.getLocation().getX(), viewPile.getLocation().getY(), MARGIN_OF_ERROR)) {
				if(viewPile.size()>0) {
					firstClickedPile = viewPile;
					canvas.removeMouseListener(this);
					canvas.addMouseListener(secondMouseHandler);
					canvas.addMouseMotionListener(secondMouseHandler);
				}
			}
			
			//if a temporary pile is clicked.
			boolean tempPileClicked = false;
			for(TemporaryPile pile : tempPiles) {
				for(Card card : pile.getCards()) {
					if(isEventInRange(eventWorldX, eventWorldY, card.getDrawnLocation().x, card.getDrawnLocation().y, MARGIN_OF_ERROR)) {
						if(!pile.getTopCard().isFaceUp()) {
							pile.getTopCard().turnOver();
							canvas.repaint();
							return;
						}
						else{
							firstClickedPile =pile;
							tempPileClicked  = true;
							break;
						}
					}
				}
				if(tempPileClicked) {
					break;
				}
			}
			if(tempPileClicked) {
				tempPileClicked = false;
				canvas.removeMouseListener(this);
				canvas.addMouseListener(secondMouseHandler);
				return;
			}
		}
		
	}
	

	/**
	 * Handles the second click for multi-click moves.
	 * @author Liam Orndorff
	 *
	 */
	public class SecondMouseHandler extends MouseAdapter implements MouseMotionListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			double xCor = e.getX();
			double yCor = canvas.getHeight()-e.getY();
			double eventWorldX = getXInWorld(xCor);
			double eventWorldY = getYInWorld(yCor);
			
			//if the viewpile is clicked twice.
			if(firstClickedPile == viewPile && isEventInRange(eventWorldX, eventWorldY, viewPile.getLocation().getX(), viewPile.getLocation().getY(), MARGIN_OF_ERROR)) {
				moveCardFromViewPileToAcePile();
				firstClickedPile = null;
				canvas.removeMouseListener(this);
				canvas.addMouseListener(firstMouseHandler);
				return;
			}
			
			//if a temporary pile is clicked twice.
			if(firstClickedPile instanceof TemporaryPile) {
				for(TemporaryPile pile : tempPiles) {
					if(pile == firstClickedPile && isEventInRange(eventWorldX, eventWorldY, pile.getTopCard().getDrawnLocation().x, pile.getTopCard().getDrawnLocation().y, MARGIN_OF_ERROR)) {
						moveCardFromTemporaryPileToAcePile(pile.getLocation());
						
						canvas.removeMouseListener(this);
						canvas.addMouseListener(firstMouseHandler);
						return;
					}
				}
			}
			
			firstClickedPile = null;
			canvas.removeMouseListener(this);
			canvas.addMouseListener(firstMouseHandler);
			
			}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(isBeingDragged) {
				double xCor = e.getX();
				double yCor = canvas.getHeight()-e.getY();
				double eventWorldX = getXInWorld(xCor);
				double eventWorldY = getYInWorld(yCor);
				if(firstClickedPile==viewPile) {
					for(TemporaryPile pile : tempPiles) {
						if(pile.size()>0) {
							if(isEventInRange(eventWorldX, eventWorldY, pile.getTopCard().getDrawnLocation().x, pile.getTopCard().getDrawnLocation().y, MARGIN_OF_ERROR)) {
								moveCardFromViewPile(pile.getLocation());
							}
						}
					}
					firstClickedPile = null;
				}
				canvas.repaint();
				canvas.removeMouseListener(this);
				canvas.addMouseListener(firstMouseHandler);
				isBeingDragged= false;
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			isBeingDragged = true;
			double xCor = e.getX();
			double yCor = canvas.getHeight()-e.getY();
			double eventWorldX = getXInWorld(xCor);
			double eventWorldY = getYInWorld(yCor);
			if(firstClickedPile == viewPile) {
				for(TemporaryPile pile : tempPiles) {
					if(isEventInRange(eventWorldX, eventWorldY, pile.getTopCard().getDrawnLocation().x, pile.getTopCard().getDrawnLocation().y, MARGIN_OF_ERROR)) {
						Card topCard = viewPile.getTopCard();
						if(pile.addCard(topCard)) {
							pile.removeTopCard();
							isInRange = true;
						}
					}
				}
			}	
			mouseX = eventWorldX;
			mouseY = eventWorldY;
			canvas.repaint();
		}
	}
	
	/**
	 * Converts an x-value from the world coordinate system to screen coordinates.
	 * @param worldX X-value in world coordinate system.
	 * @return xOnScreen X-value in screen coordinate system.
	 */
	public double getXInWorld(double screenX) {
		double xInWorld = ((screenX-viewportRec.getMinX())/(viewportRec.getMaxX()-viewportRec.getMinX()))*(WORLD_RIGHT-WORLD_LEFT) + WORLD_LEFT;
		return xInWorld;
	}
	
	/**
	 * Converts a y-value from the world coordinate system to screen coordinates.
	 * @param worldY Y-coordinate in world coordinate system.
	 * @return yOnScreen Y-coordinate in screen coordinate system.
	 */
	public double getYInWorld(double screenY) {
		double yInWorld = ((screenY-viewportRec.getMinY())/(viewportRec.getMaxY()-viewportRec.getMinY()))*(WORLD_TOP-WORLD_BOTTOM) + WORLD_BOTTOM + WORLD_TOP;
		return yInWorld;
	}

	/**
	 * The methods takes in the coordinates of where an event took place and checks to see
	 * if it was close enough to a give location with a given margin of error.
	 * @param eventX X-coordinate of event.
	 * @param eventY Y-coordinate of event.
	 * @param locationX X-coordinate of pile/card location.
	 * @param locationY Y-coordinate of pile/card location.
	 * @param marginOfError Margin of error for how far away from the location an event can be.
	 * @return isEventInRange Whether or not the event is in range.
	 */
	public boolean isEventInRange(double eventX, double eventY, double locationX, double locationY, double marginOfError) {
		boolean isEventInRange = false;
		double error = marginOfError;
		//Check if event's x coordinate is within the bounds of where the event can be registered.
		if((eventX< locationX+error) && (eventX>locationX-error)) {
			//Now checking event's y coordinate.
			if((eventY < locationY+error)&&(eventY> locationY -error)) {
				isEventInRange = true;
			}
		}
		return isEventInRange;
	}

	public JButton generateHelpButton() {
		JButton button = new JButton("?");
		button.addActionListener(e->{
			JFrame helpFrame = generateHelpFrame();
			helpFrame.setVisible(true);
		});
		
		return button;
	}
	
	public JFrame generateHelpFrame() {
		JFrame helpFrame = new JFrame("Help");
		helpFrame.setSize(canvas.getWidth(), canvas.getWidth()/2);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("KEYBOARD COMMANDS"));
		panel.add(new JLabel("D or d: Draw Card"));
		panel.add(new JLabel("1-7: Move card from view pile to one of seven temporary piles"));
		panel.add(new JLabel("A: Move card from view pile to ace pile"));
		panel.add(new JLabel("a + (1-7): Move card from one of seven temporary piles to an ace pile"));
		panel.add(new JLabel("m + (1-7) + Denomination + (1-7): Move cards between temporary piles"));
		panel.add(new JLabel("Example: m+1+J+2 moves all cards from Jack to the top card of pile 1 and moves them to pile 2"));
		panel.add(new JLabel("Denominations: A,2,3,4,5,6,7,8,9,1,J,Q,K (Note that 10 must be typed as 1"));
		panel.add(new JLabel("ESC: canvas a multi-step move"));
		
		panel.add(new JLabel(". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . ."));
		panel.add(new JLabel("MOUSE COMMANDS"));
		panel.add(new JLabel("Double click draw pile to draw card to view pile"));
		panel.add(new JLabel("Click view pile followed by a temporary pile to move a card from the view pile to the draw pile"));
		panel.add(new JLabel("Double click view pile to move card from view pile to ace pile"));
		panel.add(new JLabel("Double click temporary pile to move card from temporary pile to an ace pile"));
		panel.add(new JLabel("Click temporary pile to flip hidden card over"));
		
		helpFrame.add(panel);
		helpFrame.pack();
		return helpFrame;
	}
}