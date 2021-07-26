package graphics;

import com.jogamp.opengl.GL2;


import lib342.Colors;
import lib342.opengl.Constants.PrimitiveType;
import model.Card;
import model.PileLocation;
import mygraphicslib.GLUtilities;
import solitiare.SolitaireWindow;

public class GameBoardDrawer implements IGameBoardDrawer{
	
	private static GameBoardDrawer instance;
	
	/**
	 * Constructs a new GameBoardDrawer object.
	 */
	private GameBoardDrawer() {
		
	}
	
	/**
	 * Returns an instance of this class.
	 * @return instance an instance of this clas.
	 */
	public static GameBoardDrawer getInstance() {
		if(instance == null) {
			instance = new GameBoardDrawer();
		}
		return instance;
	}
	
	/**
	 * Draws the entire solitaire game board including game board border, pile outlines, and 
	 * ace pile suits.
	 */
	public void drawGameBoard(GL2 gl) {
		drawBorder(gl);
		drawCardOutlines(gl);
		drawSuits(gl);
	}
	
	/**
	 * Draws the game board border.
	 * @param gl a GL2 object to allow access to openGL functions.
	 */
	public void drawBorder(GL2 gl) {
		float[] darkGreen = new float[3];
		darkGreen[0] = 0f; darkGreen[1] = 100/255f; darkGreen[2] = 0f;
		gl.glColor3fv(darkGreen,0);
		gl.glLineWidth(3.0f);
		double boardCenterX = (SolitaireWindow.WORLD_RIGHT-SolitaireWindow.WORLD_LEFT)/2;
		double boardCenterY = (SolitaireWindow.WORLD_TOP-SolitaireWindow.WORLD_BOTTOM)/2;
		double boardWidth = (SolitaireWindow.WORLD_RIGHT-SolitaireWindow.WORLD_LEFT);
		double boardHeight = (SolitaireWindow.WORLD_TOP-SolitaireWindow.WORLD_BOTTOM);
		double cornerRadius = .2;
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawRoundedRectangle(gl, boardCenterX, boardCenterY, boardWidth, boardHeight, cornerRadius);
		}
		gl.glEnd();
	
	}
	
	/**
	 * Draws outlines around where the card piles are.
	 * @param gl a GL2 object to allow access to openGL functions.
	 */
	public void drawCardOutlines(GL2 gl) {
		gl.glColor3fv(Colors.LIGHT_GRAY,0);
		for(PileLocation location: PileLocation.values()) {
			double x = location.getX();
			double y = location.getY();
			gl.glBegin(PrimitiveType.LINE_STRIP.getValue());{
				GLUtilities.drawRoundedRectangle(gl, x, y, Card.CARD_WIDTH, Card.CARD_HEIGHT, .1);
			}
			gl.glEnd();
		}
	}
	
	/**
	 * Draw suits on the ace pile locations.
	 * @param gl a GL2 object to allow access to openGL functions.
	 */
	public void drawSuits(GL2 gl) {
		double x = PileLocation.Ace1.getX();
		double y = PileLocation.Ace1.getY();
		gl.glColor3fv(Colors.RED,0);
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x, y-.2);
			gl.glVertex2d(x-.1, y+.15);
			gl.glVertex2d(x+.1, y+.15);	
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.05, y+.15, .05, 0, 180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.05, y+.15, .05, 0, 180);
		}
		gl.glEnd();
		
		x = PileLocation.Ace3.getX();
		y = PileLocation.Ace3.getY();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x+.1, y);
			gl.glVertex2d(x, y+.2);
			gl.glVertex2d(x-.1, y);
			gl.glVertex2d(x, y-.2);
		}
		gl.glEnd();
		
		gl.glColor3fv(Colors.BLACK,0);
		x = PileLocation.Ace2.getX();
		y = PileLocation.Ace2.getY();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.05, y-.2);
			gl.glVertex2d(x+.05, y-.2);
			gl.glVertex2d(x, y-.05);
		}
		gl.glEnd();
		
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.05, y-.05, .05, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.05, y-.05, .05, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.1, y-.05);
			gl.glVertex2d(x+.1, y-.05);
			gl.glVertex2d(x, y+.2);
		}
		gl.glEnd();
		
		x = PileLocation.Ace4.getX();
		y = PileLocation.Ace4.getY();
		
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.05, y-.15);
			gl.glVertex2d(x+.05, y-.15);
			gl.glVertex2d(x, y);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(x, y-.05);
			gl.glVertex2d(x, y+.05);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.075, y, .075, 0, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.075, y, .075, 180, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x, y+.125, .075, -90, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x, y, .075, 0, 360);
		}
		gl.glEnd();
	}
}
